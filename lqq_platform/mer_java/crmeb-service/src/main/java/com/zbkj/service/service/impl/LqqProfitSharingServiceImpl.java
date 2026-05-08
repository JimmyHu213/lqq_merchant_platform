package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.binarywang.wxpay.bean.profitsharing.request.ProfitSharingReceiverRequest;
import com.github.binarywang.wxpay.bean.profitsharing.request.ProfitSharingRequest;
import com.github.binarywang.wxpay.bean.profitsharing.request.ProfitSharingUnfreezeRequest;
import com.github.binarywang.wxpay.bean.profitsharing.result.ProfitSharingResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.ProfitSharingService;
import com.github.binarywang.wxpay.service.WxPayService;
import com.zbkj.common.constants.UserConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.WechatProfitSharingRecord;
import com.zbkj.common.model.promoter.PromoterMerchant;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserToken;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.wxjava.WxPayServiceFactory;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * [LQQ-迁移] 溜圈圈多方分账业务 Service 实现
 *
 * 分账流程: 支付成功 → 计算分账池 → 首单判断 → 四方分配 → 微信分账API → 3天后解冻
 *
 * 分账池计算 [需审核]:
 *   分账池 = 实付金额 × 商家让利比例(fszk%)
 *   首单: 分账池 *= 首单系数(sdfc)
 *
 * 四方分配规则 [需审核]:
 *   1. 裂变佣金 = 分账池 × 裂变比例% (推荐人spreadUid，任何店消费都拿)
 *   2. 代理佣金 = 分账池 × 代理比例%(PromoterMerchant.commissionRate) (代理人是该商户的代理 AND 消费者是其粉丝)
 *   3. 锁客分润 = 分账池 × 锁客比例%(lockedMerchant.skzk%) (仅当消费店 ≠ 锁客店)
 *   4. 平台 = 分账池 - 以上各方总和
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
    private static final String RECEIVER_REFERRER = "REFERRER";
    private static final String RECEIVER_AGENT = "AGENT";
    private static final String RECEIVER_LOCKED_MERCHANT = "LOCKED_MERCHANT";
    private static final String RECEIVER_PLATFORM = "PLATFORM";

    /** 最小分账金额(元) */
    private static final BigDecimal MIN_SHARING_AMOUNT = new BigDecimal("0.01");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    /** 首单系数配置key */
    private static final String CONFIG_FIRST_ORDER_COEFFICIENT = "lqq_first_order_coefficient";
    /** 裂变佣金比例配置key */
    private static final String CONFIG_REFERRAL_RATE = "lqq_referral_commission_rate";
    /** 佣金冻结天数配置key */
    private static final String CONFIG_FREEZE_DAYS = "lqq_commission_freeze_days";
    private static final int DEFAULT_FREEZE_DAYS = 3;

    /** Redis 分布式锁 key */
    private static final String LOCK_KEY_EXECUTE = "lock:profit_sharing:execute";
    private static final long LOCK_EXPIRE_SECONDS = 300L; // 5分钟过期

    // [LQQ-迁移] Lua 脚本: 仅当锁值匹配时才删除，防止误删其他实例的锁
    private static final String UNLOCK_LUA_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "  return redis.call('del', KEYS[1]) " +
            "else " +
            "  return 0 " +
            "end";

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private WechatProfitSharingRecordService recordService;
    @Autowired
    private WxPayServiceFactory wxPayServiceFactory;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private PromoterMerchantService promoterMerchantService;

    /**
     * 为订单生成分账记录
     * [需审核] 金额计算逻辑 — 四方分配
     *
     * 流程:
     * 1. 检查商户是否启用分账
     * 2. 计算分账池 = 实付金额 × fszk%
     * 3. 首单翻倍判断
     * 4. 裂变佣金 → 代理佣金 → 锁客分润 → 平台(剩余)
     * 5. 校验分润总额 ≤ 分账池
     */
    @Override
    public void createProfitSharingRecords(MerchantOrder merchantOrder) {
        // [LQQ-迁移] 幂等保护: 检查该订单是否已有分账记录，有则跳过
        List<WechatProfitSharingRecord> existingRecords = recordService.getByOrderNo(merchantOrder.getOrderNo());
        if (CollUtil.isNotEmpty(existingRecords)) {
            logger.info("订单 {} 已有 {} 条分账记录，跳过重复生成", merchantOrder.getOrderNo(), existingRecords.size());
            return;
        }

        // 获取消费商户信息
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

        // [需审核] 分账池 = 实付金额 × 商家让利比例(fszk%)
        BigDecimal fszk = Optional.ofNullable(merchant.getFszk()).orElse(BigDecimal.ZERO);
        BigDecimal orderPayPrice = merchantOrder.getPayPrice();
        BigDecimal sharingPool = orderPayPrice.multiply(
                fszk.divide(HUNDRED, 4, RoundingMode.HALF_UP)
        ).setScale(2, RoundingMode.DOWN);

        if (sharingPool.compareTo(MIN_SHARING_AMOUNT) < 0) {
            logger.info("订单 {} 分账池金额不足0.01元, 跳过分账", merchantOrder.getOrderNo());
            return;
        }

        // [需审核] 首单翻倍判断: 用户在该商户的首次消费
        Integer uid = merchantOrder.getUid();
        Integer merId = merchantOrder.getMerId();
        int orderCount = merchantOrderService.count(Wrappers.<MerchantOrder>lambdaQuery()
                .eq(MerchantOrder::getUid, uid)
                .eq(MerchantOrder::getMerId, merId));
        // orderCount 包含当前这笔订单，首单时 count=1
        if (orderCount <= 1) {
            BigDecimal sdfc = getFirstOrderCoefficient();
            if (sdfc.compareTo(BigDecimal.ONE) > 0) {
                sharingPool = sharingPool.multiply(sdfc).setScale(2, RoundingMode.DOWN);
                logger.info("订单 {} 首单翻倍: 系数={}, 翻倍后分账池={}", merchantOrder.getOrderNo(), sdfc, sharingPool);
            }
        }

        // [需审核] 首单翻倍后分账池不得超过实付金额
        if (sharingPool.compareTo(orderPayPrice) > 0) {
            sharingPool = orderPayPrice;
            logger.warn("订单 {} 分账池超过实付金额，截断为: {}", merchantOrder.getOrderNo(), sharingPool);
        }

        User buyer = userService.getById(uid);
        if (ObjectUtil.isNull(buyer)) {
            logger.error("分账生成失败: 用户不存在 uid={}", uid);
            return;
        }

        List<WechatProfitSharingRecord> records = new ArrayList<>();
        BigDecimal totalShared = BigDecimal.ZERO;

        // ========== 1. 裂变佣金 = 分账池 × 裂变比例% ==========
        // 推荐人(user.spreadUid)，任何店消费都拿
        // [需审核]
        if (ObjectUtil.isNotNull(buyer.getSpreadUid()) && buyer.getSpreadUid() > 0) {
            User referrer = userService.getById(buyer.getSpreadUid());
            if (ObjectUtil.isNotNull(referrer)) {
                BigDecimal referralRate = getReferralCommissionRate();
                if (referralRate.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal referralAmount = sharingPool.multiply(
                            referralRate.divide(HUNDRED, 4, RoundingMode.HALF_UP)
                    ).setScale(2, RoundingMode.DOWN);

                    if (referralAmount.compareTo(MIN_SHARING_AMOUNT) >= 0) {
                        String openid = getUserOpenid(referrer.getId());
                        WechatProfitSharingRecord record = buildRecord(
                                merchantOrder, platOrder, merchant,
                                referrer.getId(), referrer.getNickname(),
                                openid, RECEIVER_REFERRER,
                                referralAmount, referralRate, sharingPool, "裂变佣金"
                        );
                        records.add(record);
                        totalShared = totalShared.add(referralAmount);
                    }
                }
            }
        }

        // ========== 2. 代理佣金 = 分账池 × 代理比例% ==========
        // 条件: 代理人是该消费商户的代理 AND 消费者是代理人的粉丝(spreadUid链)
        // [需审核]
        PromoterMerchant agentBind = promoterMerchantService.getByMerId(merId);
        if (ObjectUtil.isNotNull(agentBind)) {
            Integer agentUid = agentBind.getUid();
            // 验证消费者是代理人的粉丝（spreadUid链上游包含代理人）
            if (isInSpreadChain(buyer, agentUid)) {
                BigDecimal agentRate = agentBind.getCommissionRate();
                if (ObjectUtil.isNotNull(agentRate) && agentRate.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal agentAmount = sharingPool.multiply(
                            agentRate.divide(HUNDRED, 4, RoundingMode.HALF_UP)
                    ).setScale(2, RoundingMode.DOWN);

                    if (agentAmount.compareTo(MIN_SHARING_AMOUNT) >= 0) {
                        User agentUser = userService.getById(agentUid);
                        String agentName = ObjectUtil.isNotNull(agentUser) ? agentUser.getNickname() : "代理人";
                        String openid = getUserOpenid(agentUid);
                        WechatProfitSharingRecord record = buildRecord(
                                merchantOrder, platOrder, merchant,
                                agentUid, agentName,
                                openid, RECEIVER_AGENT,
                                agentAmount, agentRate, sharingPool, "代理佣金"
                        );
                        records.add(record);
                        totalShared = totalShared.add(agentAmount);
                    }
                }
            }
        }

        // ========== 3. 锁客分润 = 分账池 × 锁客比例%(lockedMerchant.skzk%) ==========
        // 仅当消费店 ≠ 锁客店时才分。消费就在锁客店则锁客分润=0
        // [需审核]
        if (ObjectUtil.isNotNull(buyer.getLockedMerchantId()) && buyer.getLockedMerchantId() > 0) {
            Integer lockedMerId = buyer.getLockedMerchantId();
            // 关键判断: 消费店 ≠ 锁客店
            if (!lockedMerId.equals(merId)) {
                Merchant lockedMerchant = merchantService.getByIdException(lockedMerId);
                BigDecimal skzk = Optional.ofNullable(lockedMerchant.getSkzk()).orElse(BigDecimal.ZERO);
                if (skzk.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal lockAmount = sharingPool.multiply(
                            skzk.divide(HUNDRED, 4, RoundingMode.HALF_UP)
                    ).setScale(2, RoundingMode.DOWN);

                    if (lockAmount.compareTo(MIN_SHARING_AMOUNT) >= 0) {
                        String openid = getMerchantAdminOpenid(lockedMerchant);
                        WechatProfitSharingRecord record = buildRecord(
                                merchantOrder, platOrder, merchant,
                                lockedMerchant.getAdminId(), lockedMerchant.getName(),
                                openid, RECEIVER_LOCKED_MERCHANT,
                                lockAmount, skzk, sharingPool, "锁客商家分润"
                        );
                        records.add(record);
                        totalShared = totalShared.add(lockAmount);
                    }
                }
            } else {
                logger.info("订单 {} 消费店=锁客店(merId={})，锁客分润=0", merchantOrder.getOrderNo(), merId);
            }
        }

        // ========== 4. 平台 = 分账池 - 以上各方总和 ==========
        // [需审核]
        BigDecimal platformAmount = sharingPool.subtract(totalShared);

        // [需审核] 校验: 分润总额不得超过分账池
        if (totalShared.compareTo(sharingPool) > 0) {
            logger.error("分账异常: 订单 {} 分润总额({})超过分账池({})，放弃分账",
                    merchantOrder.getOrderNo(), totalShared, sharingPool);
            throw new CrmebException("分账计算异常: 分润总额超过分账池");
        }

        if (platformAmount.compareTo(MIN_SHARING_AMOUNT) >= 0) {
            BigDecimal platformRate = BigDecimal.ZERO;
            if (sharingPool.compareTo(BigDecimal.ZERO) > 0) {
                platformRate = platformAmount.multiply(HUNDRED)
                        .divide(sharingPool, 2, RoundingMode.HALF_UP);
            }
            WechatProfitSharingRecord record = buildRecord(
                    merchantOrder, platOrder, merchant,
                    null, "平台",
                    null, RECEIVER_PLATFORM,
                    platformAmount, platformRate, sharingPool, "平台分账收入"
            );
            records.add(record);
        }

        // 保存分账记录
        if (CollUtil.isNotEmpty(records)) {
            recordService.saveBatch(records);
            logger.info("订单 {} 生成 {} 条分账记录, 分账池={}, 各方总分={}",
                    merchantOrder.getOrderNo(), records.size(), sharingPool, totalShared);
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
        // [LQQ-迁移] Redis 分布式锁，防止多实例重复执行分账
        // 使用 UUID 作为锁值，释放时用 Lua 脚本比较后删除，防止误删其他实例的锁
        String lockValue = UUID.randomUUID().toString();
        Boolean locked = redisUtil.getRedisTemplate().opsForValue()
                .setIfAbsent(LOCK_KEY_EXECUTE, lockValue, LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(locked)) {
            logger.info("分账任务已有其他实例在执行，本次跳过");
            return;
        }

        try {
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
        } finally {
            // [LQQ-迁移] Lua 脚本释放锁：仅当锁值匹配当前实例的 UUID 时才删除
            releaseLock(LOCK_KEY_EXECUTE, lockValue);
        }
    }

    // TODO: [LQQ-迁移] 退款分账回退 — 订单退款时需调用微信分账回退API，将已分账金额退回
    // 后续迭代实现: 监听退款事件 → 查询该订单的分账记录 → 调用profitSharingReturn API → 更新记录状态

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
     * 获取首单系数(可配置)
     * 从 SystemConfig 读取 lqq_first_order_coefficient，默认 1.5
     * [需审核]
     */
    private BigDecimal getFirstOrderCoefficient() {
        String value = systemConfigService.getValueByKey(CONFIG_FIRST_ORDER_COEFFICIENT);
        if (StrUtil.isNotBlank(value)) {
            try {
                BigDecimal coefficient = new BigDecimal(value);
                if (coefficient.compareTo(BigDecimal.ZERO) > 0) {
                    return coefficient;
                }
            } catch (NumberFormatException e) {
                logger.warn("首单系数配置格式错误: {}", value);
            }
        }
        return new BigDecimal("1.5"); // 默认1.5倍
    }

    /**
     * 获取裂变佣金比例(%)
     * 从 SystemConfig 读取 lqq_referral_commission_rate，默认 5%
     * [需审核]
     */
    private BigDecimal getReferralCommissionRate() {
        String value = systemConfigService.getValueByKey(CONFIG_REFERRAL_RATE);
        if (StrUtil.isNotBlank(value)) {
            try {
                BigDecimal rate = new BigDecimal(value);
                if (rate.compareTo(BigDecimal.ZERO) >= 0 && rate.compareTo(HUNDRED) <= 0) {
                    return rate;
                }
            } catch (NumberFormatException e) {
                logger.warn("裂变佣金比例配置格式错误: {}", value);
            }
        }
        return new BigDecimal("5"); // 默认5%
    }

    /**
     * 计算冻结截止时间 = 当前时间 + 冻结天数
     * 冻结天数从 SystemConfig 读取 lqq_commission_freeze_days，默认 3 天
     */
    private Date calcFrozenUntil() {
        int freezeDays = DEFAULT_FREEZE_DAYS;
        String value = systemConfigService.getValueByKey(CONFIG_FREEZE_DAYS);
        if (StrUtil.isNotBlank(value)) {
            try {
                int days = Integer.parseInt(value);
                if (days >= 0) {
                    freezeDays = days;
                }
            } catch (NumberFormatException e) {
                logger.warn("冻结天数配置格式错误: {}", value);
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, freezeDays);
        return cal.getTime();
    }

    /**
     * 判断用户是否在某个推广员的粉丝链上
     * 向上遍历 spreadUid 链（最多5层），判断是否包含目标用户
     */
    private boolean isInSpreadChain(User buyer, Integer targetUid) {
        if (ObjectUtil.isNull(buyer) || ObjectUtil.isNull(targetUid)) {
            return false;
        }
        Integer currentSpreadUid = buyer.getSpreadUid();
        int maxDepth = 5; // 防止循环引用
        for (int i = 0; i < maxDepth; i++) {
            if (ObjectUtil.isNull(currentSpreadUid) || currentSpreadUid <= 0) {
                return false;
            }
            if (currentSpreadUid.equals(targetUid)) {
                return true;
            }
            User parent = userService.getById(currentSpreadUid);
            if (ObjectUtil.isNull(parent)) {
                return false;
            }
            currentSpreadUid = parent.getSpreadUid();
        }
        return false;
    }

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
                    receiver.put("amount", r.getAmount().multiply(HUNDRED).intValue());
                    receiver.put("description", StrUtil.isNotBlank(r.getDescription()) ? r.getDescription() : "合作伙伴分润");
                    receiversArray.add(receiver);
                }

                ProfitSharingRequest psRequest = new ProfitSharingRequest();
                psRequest.setTransactionId(platOrder.getOutTradeNo());
                psRequest.setOutOrderNo(psOrderNo);
                psRequest.setReceivers(receiversArray.toJSONString());

                ProfitSharingResult psResult = profitSharingService.profitSharing(psRequest);
                logger.info("订单 {} 分账执行成功: {}", orderNo, JSON.toJSONString(psResult));

                // 更新分账记录状态 + 设置冻结截止时间
                Date frozenUntil = calcFrozenUntil();
                for (WechatProfitSharingRecord record : toShare) {
                    record.setStatus(STATUS_SUCCESS);
                    record.setProfitSharingOrderNo(psOrderNo);
                    record.setFrozenUntil(frozenUntil);
                    record.setUpdateTime(new Date());
                }
            }

            // 3. 平台分润记录直接标记成功（平台不需要佣金解冻）
            validRecords.stream()
                    .filter(r -> RECEIVER_PLATFORM.equals(r.getReceiverType()) && r.getStatus() == STATUS_PENDING)
                    .forEach(r -> {
                        r.setStatus(STATUS_SUCCESS);
                        r.setIsUnfrozen(1); // 平台无需解冻流程
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
        boolean allDone = records.stream()
                .allMatch(r -> r.getStatus() == STATUS_SUCCESS || r.getStatus() == STATUS_FAILED);
        if (allDone) {
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
        record.setIsUnfrozen(0);
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

    /**
     * [LQQ-迁移] 安全释放 Redis 分布式锁
     * 使用 Lua 脚本保证原子性: 仅当锁值匹配时才删除
     */
    private void releaseLock(String lockKey, String lockValue) {
        try {
            DefaultRedisScript<Long> script = new DefaultRedisScript<>(UNLOCK_LUA_SCRIPT, Long.class);
            redisUtil.getRedisTemplate().execute(script, Collections.singletonList(lockKey), lockValue);
        } catch (Exception e) {
            logger.warn("释放分布式锁异常: key={}, error={}", lockKey, e.getMessage());
        }
    }
}
