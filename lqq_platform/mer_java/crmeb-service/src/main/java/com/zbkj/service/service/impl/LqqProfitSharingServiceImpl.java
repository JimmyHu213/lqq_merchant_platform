package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.profitsharing.request.ProfitSharingReceiverRequest;
import com.github.binarywang.wxpay.bean.profitsharing.request.ProfitSharingRequest;
import com.github.binarywang.wxpay.bean.profitsharing.request.ProfitSharingUnfreezeRequest;
import com.github.binarywang.wxpay.bean.profitsharing.result.ProfitSharingResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.ProfitSharingService;
import com.github.binarywang.wxpay.service.WxPayService;
import com.zbkj.common.constants.UserConstants;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.WechatProfitSharingRecord;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserToken;
import com.zbkj.common.wxjava.WxPayServiceFactory;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * [LQQ-迁移] 溜圈圈多方分账业务 Service 实现
 *
 * 业务逻辑参考 mlqApi: BuyerYhqServiceImpl.dxcOrder()
 * 分润公式 [需审核]:
 *   基础金额 = goodsPrice * (1 - fszk)
 *   锁客商铺分润 = 基础金额 * skzk%
 *   推荐人分润 = 基础金额 * 佣金%
 *   平台 = 剩余
 */
@Service
public class LqqProfitSharingServiceImpl implements LqqProfitSharingService {

    private static final Logger logger = LoggerFactory.getLogger(LqqProfitSharingServiceImpl.class);

    /** 分账记录状态 */
    private static final int STATUS_PENDING = 0;
    private static final int STATUS_SUCCESS = 1;
    private static final int STATUS_FAILED = 2;
    private static final int STATUS_UNFROZEN = 3;

    /** 分账接收方类型 */
    private static final String RECEIVER_LOCKED_MERCHANT = "LOCKED_MERCHANT";
    private static final String RECEIVER_REFERRER = "REFERRER";
    private static final String RECEIVER_REFERRER_PARENT = "REFERRER_PARENT";
    private static final String RECEIVER_PROMOTER = "PROMOTER";
    private static final String RECEIVER_PLATFORM = "PLATFORM";

    /** 最小分账金额(元) */
    private static final BigDecimal MIN_SHARING_AMOUNT = new BigDecimal("0.01");

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private WechatProfitSharingRecordService recordService;
    @Autowired
    private WxPayServiceFactory wxPayServiceFactory;

    /**
     * 为订单生成分账记录
     * [需审核] 金额计算逻辑
     *
     * 参考 mlqApi BuyerYhqServiceImpl.dxcOrder():
     * 1. 获取商户的分账参数(fszk, skzk, isProfitSharing)
     * 2. 计算基础分账金额 = 订单金额 * (1 - fszk)
     * 3. 扣除优惠券费用
     * 4. 锁客商户分润 = 基础金额 * skzk%
     * 5. 推荐人分润 = 基础金额 * 推荐人佣金%
     * 6. 平台 = 剩余金额
     */
    @Override
    public void createProfitSharingRecords(MerchantOrder merchantOrder) {
        // 获取商户信息
        Merchant merchant = merchantService.getByIdException(merchantOrder.getMerId());
        if (ObjectUtil.isNull(merchant.getIsProfitSharing()) || !merchant.getIsProfitSharing()) {
            logger.info("商户 {} 未启用分账，跳过", merchant.getId());
            return;
        }

        // 获取平台订单（用于取微信支付交易号）
        Order platOrder = orderService.getByOrderNo(merchantOrder.getOrderNo());
        if (ObjectUtil.isNull(platOrder)) {
            logger.error("分账生成失败: 找不到订单 {}", merchantOrder.getOrderNo());
            return;
        }

        // [需审核] 分账基础金额计算
        BigDecimal fszk = Optional.ofNullable(merchant.getFszk()).orElse(BigDecimal.ZERO);
        BigDecimal orderPayPrice = merchantOrder.getPayPrice();
        // 基础金额 = 订单金额 * (1 - fszk/100)
        BigDecimal baseAmount = orderPayPrice.multiply(
                BigDecimal.ONE.subtract(fszk.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP))
        ).setScale(2, RoundingMode.DOWN);

        // 扣除优惠券金额
        BigDecimal couponPrice = Optional.ofNullable(merchantOrder.getCouponPrice()).orElse(BigDecimal.ZERO);
        BigDecimal shareableAmount = baseAmount.subtract(couponPrice);

        if (shareableAmount.compareTo(MIN_SHARING_AMOUNT) < 0) {
            logger.info("订单 {} 可分账金额不足0.01元, 仅执行分账解冻", merchantOrder.getOrderNo());
            return;
        }

        List<WechatProfitSharingRecord> records = new ArrayList<>();
        BigDecimal totalShared = BigDecimal.ZERO;

        // 1. 锁客商户分润
        User buyer = userService.getById(merchantOrder.getUid());
        if (ObjectUtil.isNotNull(buyer) && ObjectUtil.isNotNull(buyer.getLockedMerchantId()) && buyer.getLockedMerchantId() > 0) {
            Merchant lockedMerchant = merchantService.getByIdException(buyer.getLockedMerchantId());
            BigDecimal skzk = Optional.ofNullable(lockedMerchant.getSkzk()).orElse(BigDecimal.ZERO);
            if (skzk.compareTo(BigDecimal.ZERO) > 0) {
                // [需审核] 锁客分润 = 可分账金额 * skzk%
                BigDecimal lockAmount = shareableAmount.multiply(
                        skzk.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
                ).setScale(2, RoundingMode.DOWN);

                if (lockAmount.compareTo(MIN_SHARING_AMOUNT) >= 0) {
                    // 获取锁客商户关联用户的openid
                    String openid = getMerchantAdminOpenid(lockedMerchant);
                    WechatProfitSharingRecord record = buildRecord(
                            merchantOrder, platOrder, merchant,
                            lockedMerchant.getAdminId(), lockedMerchant.getName(),
                            openid, RECEIVER_LOCKED_MERCHANT,
                            lockAmount, skzk, shareableAmount, "锁客商家分润"
                    );
                    records.add(record);
                    totalShared = totalShared.add(lockAmount);
                }
            }
        }

        // 2. 推荐人(邀请人)分润
        if (ObjectUtil.isNotNull(buyer) && ObjectUtil.isNotNull(buyer.getSpreadUid()) && buyer.getSpreadUid() > 0) {
            User referrer = userService.getById(buyer.getSpreadUid());
            if (ObjectUtil.isNotNull(referrer)) {
                // 推荐人佣金比例从一级返佣获取（系统配置的分销比例）
                BigDecimal firstBrokerage = Optional.ofNullable(merchantOrder.getFirstBrokerage()).orElse(BigDecimal.ZERO);
                if (firstBrokerage.compareTo(BigDecimal.ZERO) > 0) {
                    // 推荐人获得的是一级返佣金额（已由系统计算好）
                    // 但在分账场景下，需要从分账基数重新计算
                    // [需审核] 推荐人分润比例从系统配置获取（一级返佣比例）
                    BigDecimal referrerRate = BigDecimal.ZERO;
                    if (orderPayPrice.compareTo(BigDecimal.ZERO) > 0) {
                        referrerRate = firstBrokerage.multiply(new BigDecimal("100"))
                                .divide(orderPayPrice, 2, RoundingMode.HALF_UP);
                    }
                    BigDecimal referrerAmount = shareableAmount.multiply(
                            referrerRate.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
                    ).setScale(2, RoundingMode.DOWN);

                    if (referrerAmount.compareTo(MIN_SHARING_AMOUNT) >= 0) {
                        String openid = getUserOpenid(referrer.getId());
                        WechatProfitSharingRecord record = buildRecord(
                                merchantOrder, platOrder, merchant,
                                referrer.getId(), referrer.getNickname(),
                                openid, RECEIVER_REFERRER,
                                referrerAmount, referrerRate, shareableAmount, "推广提成"
                        );
                        records.add(record);
                        totalShared = totalShared.add(referrerAmount);
                    }
                }

                // 推荐人上级(二级返佣)
                if (ObjectUtil.isNotNull(referrer.getSpreadUid()) && referrer.getSpreadUid() > 0) {
                    BigDecimal secondBrokerage = Optional.ofNullable(merchantOrder.getSecondBrokerage()).orElse(BigDecimal.ZERO);
                    if (secondBrokerage.compareTo(BigDecimal.ZERO) > 0) {
                        User referrerParent = userService.getById(referrer.getSpreadUid());
                        if (ObjectUtil.isNotNull(referrerParent)) {
                            BigDecimal parentRate = BigDecimal.ZERO;
                            if (orderPayPrice.compareTo(BigDecimal.ZERO) > 0) {
                                parentRate = secondBrokerage.multiply(new BigDecimal("100"))
                                        .divide(orderPayPrice, 2, RoundingMode.HALF_UP);
                            }
                            BigDecimal parentAmount = shareableAmount.multiply(
                                    parentRate.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
                            ).setScale(2, RoundingMode.DOWN);

                            if (parentAmount.compareTo(MIN_SHARING_AMOUNT) >= 0) {
                                String openid = getUserOpenid(referrerParent.getId());
                                WechatProfitSharingRecord record = buildRecord(
                                        merchantOrder, platOrder, merchant,
                                        referrerParent.getId(), referrerParent.getNickname(),
                                        openid, RECEIVER_REFERRER_PARENT,
                                        parentAmount, parentRate, shareableAmount, "推广上级提成"
                                );
                                records.add(record);
                                totalShared = totalShared.add(parentAmount);
                            }
                        }
                    }
                }
            }
        }

        // 3. 平台分润 = 可分账金额 - 已分金额
        // [需审核] 签约折扣与返商折扣差异处理
        BigDecimal platformAmount = shareableAmount.subtract(totalShared);
        BigDecimal qyzk = Optional.ofNullable(merchant.getQyzk()).orElse(BigDecimal.ZERO);
        if (qyzk.compareTo(BigDecimal.ZERO) > 0 && fszk.compareTo(BigDecimal.ZERO) > 0 && qyzk.compareTo(fszk) < 0) {
            // 签约折扣 < 返商折扣: 平台额外收取差额
            BigDecimal zkDiff = fszk.subtract(qyzk).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            BigDecimal extraPlatform = orderPayPrice.multiply(zkDiff).setScale(2, RoundingMode.DOWN);
            platformAmount = platformAmount.add(extraPlatform);
        }

        if (platformAmount.compareTo(MIN_SHARING_AMOUNT) >= 0) {
            BigDecimal platformRate = BigDecimal.ZERO;
            if (shareableAmount.compareTo(BigDecimal.ZERO) > 0) {
                platformRate = platformAmount.multiply(new BigDecimal("100"))
                        .divide(shareableAmount, 2, RoundingMode.HALF_UP);
            }
            // 平台分润记录，openid暂留空（平台账号在定时任务执行时从配置获取）
            WechatProfitSharingRecord record = buildRecord(
                    merchantOrder, platOrder, merchant,
                    null, "平台",
                    null, RECEIVER_PLATFORM,
                    platformAmount, platformRate, shareableAmount, "平台分账收入"
            );
            records.add(record);
        }

        // 保存分账记录
        if (CollUtil.isNotEmpty(records)) {
            recordService.saveBatch(records);
            logger.info("订单 {} 生成 {} 条分账记录, 总分账金额: {}",
                    merchantOrder.getOrderNo(), records.size(),
                    records.stream().map(WechatProfitSharingRecord::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
        }
    }

    /**
     * 执行待分账记录
     * 定时任务调用，按订单维度聚合执行微信分账API
     *
     * 参考 mlqApi: BuyerYhqBasicServiceImpl.wechatFz()
     * 流程:
     * 1. 查询 status=0 的待分账记录
     * 2. 按订单号分组
     * 3. 每组: 添加分账接收方 → 调用分账API → 更新状态
     */
    @Override
    public void executePendingProfitSharing() {
        List<WechatProfitSharingRecord> pendingRecords = recordService.getPendingRecords(100);
        if (CollUtil.isEmpty(pendingRecords)) {
            return;
        }

        // 按订单号分组
        Map<String, List<WechatProfitSharingRecord>> orderGroups = pendingRecords.stream()
                .collect(Collectors.groupingBy(WechatProfitSharingRecord::getOrderNo));

        for (Map.Entry<String, List<WechatProfitSharingRecord>> entry : orderGroups.entrySet()) {
            String orderNo = entry.getKey();
            List<WechatProfitSharingRecord> records = entry.getValue();

            try {
                executeProfitSharingForOrder(orderNo, records);
            } catch (Exception e) {
                logger.error("订单 {} 分账执行异常", orderNo, e);
                // 标记该批次记录为失败
                for (WechatProfitSharingRecord record : records) {
                    record.setStatus(STATUS_FAILED);
                    record.setFailReason("分账执行异常: " + e.getMessage());
                    record.setUpdateTime(new Date());
                }
                recordService.updateBatchById(records);
            }
        }
    }

    /**
     * 对指定订单执行分账解冻(完结)
     * 分账完成后需调用完结接口，剩余资金解冻给商户
     *
     * 参考 mlqApi: BuyerYhqBasicServiceImpl.fzzd()
     */
    @Override
    public void unfreezeOrder(String orderNo) {
        Order platOrder = orderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(platOrder) || StrUtil.isBlank(platOrder.getOutTradeNo())) {
            logger.error("分账解冻失败: 找不到订单或微信交易号, orderNo={}", orderNo);
            return;
        }

        try {
            WxPayService wxPayService = wxPayServiceFactory.getService("ma");
            ProfitSharingService profitSharingService = wxPayService.getProfitSharingService();

            String unfreezeOrderNo = "FZJD" + generateOrderNo();

            ProfitSharingUnfreezeRequest request = new ProfitSharingUnfreezeRequest();
            request.setTransactionId(platOrder.getOutTradeNo());
            request.setOutOrderNo(unfreezeOrderNo);
            request.setDescription("分账完结解冻");

            ProfitSharingResult result = profitSharingService.profitSharingFinish(request);
            logger.info("订单 {} 分账解冻成功: {}", orderNo, JSON.toJSONString(result));

        } catch (WxPayException e) {
            logger.error("订单 {} 分账解冻失败: {}", orderNo, e.getMessage(), e);
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 对单个订单执行微信分账
     */
    private void executeProfitSharingForOrder(String orderNo, List<WechatProfitSharingRecord> records) {
        // 获取微信交易号
        Order platOrder = orderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(platOrder) || StrUtil.isBlank(platOrder.getOutTradeNo())) {
            markRecordsFailed(records, "找不到微信交易号");
            return;
        }

        // 过滤掉没有openid的记录（平台账号除外，平台不走个人分账）
        List<WechatProfitSharingRecord> validRecords = records.stream()
                .filter(r -> StrUtil.isNotBlank(r.getReceiverOpenid()) || RECEIVER_PLATFORM.equals(r.getReceiverType()))
                .collect(Collectors.toList());

        // 没有openid的记录标记失败
        records.stream()
                .filter(r -> StrUtil.isBlank(r.getReceiverOpenid()) && !RECEIVER_PLATFORM.equals(r.getReceiverType()))
                .forEach(r -> {
                    r.setStatus(STATUS_FAILED);
                    r.setFailReason("未收集到用户的openid");
                    r.setUpdateTime(new Date());
                });

        if (CollUtil.isEmpty(validRecords)) {
            recordService.updateBatchById(records);
            return;
        }

        try {
            WxPayService wxPayService = wxPayServiceFactory.getService("ma");
            ProfitSharingService profitSharingService = wxPayService.getProfitSharingService();

            // 1. 添加分账接收方 (参考 mlqApi PayCommon.addFzr)
            for (WechatProfitSharingRecord record : validRecords) {
                if (RECEIVER_PLATFORM.equals(record.getReceiverType())) {
                    continue; // 平台账号后续处理
                }
                try {
                    // 构建接收方JSON (V2 API: receiver为JSON字符串)
                    JSONObject receiverJson = new JSONObject();
                    receiverJson.put("type", "PERSONAL_SUB_OPENID");
                    receiverJson.put("account", record.getReceiverOpenid());
                    receiverJson.put("relation_type", "PARTNER");

                    ProfitSharingReceiverRequest addRequest = new ProfitSharingReceiverRequest();
                    addRequest.setReceiver(receiverJson.toJSONString());
                    profitSharingService.addReceiver(addRequest);
                    logger.info("添加分账接收方成功: userId={}, openid={}",
                            record.getReceiverUserId(), record.getReceiverOpenid());
                } catch (WxPayException e) {
                    // RECEIVER_ACCOUNT_ALREADY_EXISTS 不算错误
                    if (!"RECEIVER_ACCOUNT_ALREADY_EXISTS".equals(e.getErrCode())) {
                        logger.error("添加分账接收方失败: {}", e.getMessage());
                        record.setStatus(STATUS_FAILED);
                        record.setFailReason("添加分账方异常: " + e.getErrCode());
                        record.setUpdateTime(new Date());
                    }
                }
            }

            // 2. 构建分账请求 (V2 API: receivers为JSON字符串)
            List<WechatProfitSharingRecord> toShare = validRecords.stream()
                    .filter(r -> r.getStatus() == STATUS_PENDING && !RECEIVER_PLATFORM.equals(r.getReceiverType()))
                    .collect(Collectors.toList());

            if (CollUtil.isNotEmpty(toShare)) {
                String psOrderNo = "FZLQ" + generateOrderNo();

                // [需审核] 构建微信分账接收方JSON数组，金额单位为分
                JSONArray receiversArray = new JSONArray();
                for (WechatProfitSharingRecord r : toShare) {
                    JSONObject receiver = new JSONObject();
                    receiver.put("type", "PERSONAL_SUB_OPENID");
                    receiver.put("account", r.getReceiverOpenid());
                    receiver.put("amount", r.getAmount().multiply(new BigDecimal("100")).intValue());
                    receiver.put("description", StrUtil.isNotBlank(r.getDescription()) ? r.getDescription() : "合作伙伴分润");
                    receiversArray.add(receiver);
                }

                ProfitSharingRequest psRequest = new ProfitSharingRequest();
                psRequest.setTransactionId(platOrder.getOutTradeNo());
                psRequest.setOutOrderNo(psOrderNo);
                psRequest.setReceivers(receiversArray.toJSONString());

                ProfitSharingResult psResult = profitSharingService.profitSharing(psRequest);
                logger.info("订单 {} 分账执行成功: {}", orderNo, JSON.toJSONString(psResult));

                // 更新分账记录状态
                for (WechatProfitSharingRecord record : toShare) {
                    record.setStatus(STATUS_SUCCESS);
                    record.setProfitSharingOrderNo(psOrderNo);
                    record.setUpdateTime(new Date());
                }
            }

            // 3. 平台分润记录直接标记成功（平台金额在分账解冻后归属平台）
            validRecords.stream()
                    .filter(r -> RECEIVER_PLATFORM.equals(r.getReceiverType()) && r.getStatus() == STATUS_PENDING)
                    .forEach(r -> {
                        r.setStatus(STATUS_SUCCESS);
                        r.setUpdateTime(new Date());
                    });

        } catch (WxPayException e) {
            logger.error("订单 {} 分账请求失败: code={}, msg={}", orderNo, e.getErrCode(), e.getMessage());
            // 分账失败时重试一次（参考 mlqApi 对"订单处理中"的重试）
            if ("ORDER_IS_PROCESSING".equals(e.getErrCode())) {
                logger.info("订单 {} 处理中，稍后重试", orderNo);
                return; // 保持 status=0，下次定时任务重试
            }
            markRecordsFailed(validRecords, "分账请求失败: " + e.getErrCode() + " - " + e.getMessage());
        }

        // 保存所有更新
        recordService.updateBatchById(records);

        // 4. 分账完成后执行解冻
        boolean allSuccess = records.stream()
                .allMatch(r -> r.getStatus() == STATUS_SUCCESS || r.getStatus() == STATUS_FAILED);
        if (allSuccess) {
            unfreezeOrder(orderNo);
        }
    }

    /**
     * 获取商户管理员的小程序openid
     */
    private String getMerchantAdminOpenid(Merchant merchant) {
        if (ObjectUtil.isNull(merchant.getAdminId()) || merchant.getAdminId() <= 0) {
            return null;
        }
        // 商户管理员通过adminId关联到用户
        return getUserOpenid(merchant.getAdminId());
    }

    /**
     * 获取用户的小程序openid
     */
    private String getUserOpenid(Integer userId) {
        if (ObjectUtil.isNull(userId) || userId <= 0) {
            return null;
        }
        UserToken userToken = userTokenService.getTokenByUserId(userId, UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNotNull(userToken) && StrUtil.isNotBlank(userToken.getToken())) {
            return userToken.getToken();
        }
        return null;
    }

    /**
     * 构建分账记录对象
     */
    private WechatProfitSharingRecord buildRecord(
            MerchantOrder merchantOrder, Order platOrder, Merchant merchant,
            Integer receiverUserId, String receiverName, String receiverOpenid,
            String receiverType, BigDecimal amount, BigDecimal rate,
            BigDecimal baseAmount, String description) {

        WechatProfitSharingRecord record = new WechatProfitSharingRecord();
        record.setOrderNo(merchantOrder.getOrderNo());
        record.setMerchantOrderNo(merchantOrder.getOrderNo());
        record.setTransactionId(platOrder.getOutTradeNo());
        record.setOrderMerId(merchantOrder.getMerId());
        record.setUserId(merchantOrder.getUid());
        record.setReceiverUserId(receiverUserId);
        record.setReceiverName(receiverName);
        record.setReceiverOpenid(receiverOpenid);
        record.setReceiverType(receiverType);
        record.setAmount(amount);
        record.setRate(rate);
        record.setBaseAmount(baseAmount);
        record.setDescription(description);
        record.setStatus(STATUS_PENDING);
        record.setSubMchId(merchant.getWxSubMchId());
        record.setServiceMchId(merchant.getWxServiceMchId());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        return record;
    }

    /**
     * 生成分账单号: 时间戳 + 4位随机数
     */
    private String generateOrderNo() {
        long ts = System.currentTimeMillis();
        int rand = (int) (Math.random() * 10000);
        return String.format("%d%04d", ts, rand);
    }

    /**
     * 批量标记记录失败
     */
    private void markRecordsFailed(List<WechatProfitSharingRecord> records, String reason) {
        for (WechatProfitSharingRecord record : records) {
            if (record.getStatus() == STATUS_PENDING) {
                record.setStatus(STATUS_FAILED);
                record.setFailReason(reason);
                record.setUpdateTime(new Date());
            }
        }
    }
}
