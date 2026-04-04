package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.merchant.ShoppingCreditsOrder;
import com.zbkj.common.model.merchant.ShoppingCreditsPackage;
import com.zbkj.common.model.merchant.ShoppingCreditsRefundOrder;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserShoppingCredits;
import com.zbkj.common.model.user.UserShoppingCreditsRecord;
import com.zbkj.common.model.user.UserToken;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.OrderPayResultResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RequestUtil;
import com.zbkj.common.vo.*;
import com.zbkj.front.request.PayShoppingCreditsPackageRecordRequest;
import com.zbkj.front.request.PayShoppingCreditsPackageRequest;
import com.zbkj.front.request.ShoppingCreditsOrderRefundApplyRequest;
import com.zbkj.front.request.UserShoppingCreditsRecordPageRequest;
import com.zbkj.front.response.MerchantMemberShoppingCreditsAssetResponse;
import com.zbkj.front.response.UserShoppingCreditsMonthRecordResponse;
import com.zbkj.front.service.FrontShoppingCreditsService;
import com.zbkj.service.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物金服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/15
 */
@Slf4j
@Service
public class FrontShoppingCreditsServiceImpl implements FrontShoppingCreditsService {

    @Autowired
    private ShoppingCreditsPackageService shoppingCreditsPackageService;
    @Autowired
    private UserService userService;
    @Autowired
    private MerchantMemberService merchantMemberService;
    @Autowired
    private ShoppingCreditsOrderService shoppingCreditsOrderService;
    @Autowired
    private UserShoppingCreditsRecordService userShoppingCreditsRecordService;
    @Autowired
    private ShoppingCreditsRefundOrderService shoppingCreditsRefundOrderService;
    @Autowired
    private UserShoppingCreditsService userShoppingCreditsService;

    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private WechatPayService wechatPayService;
    @Autowired
    private AliPayService aliPayService;
    @Autowired
    private CrmebConfig crmebConfig;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 获取购物金套餐列表
     */
    @Override
    public List<ShoppingCreditsPackage> findPackageList(Integer merId) {
        List<ShoppingCreditsPackage> packageList = shoppingCreditsPackageService.findList(merId, 1);
        return packageList;
    }

    /**
     * 获取商户会员购物金资产
     */
    @Override
    public MerchantMemberShoppingCreditsAssetResponse getMerchantMemberAsset(Integer merId) {
        Integer userId = userService.getUserId();
        MerchantMemberShoppingCreditsAssetResponse response = new MerchantMemberShoppingCreditsAssetResponse();
        if (userId <= 0) {
            return response;
        }
        UserShoppingCredits userShoppingCredits = userShoppingCreditsService.getByUserIdAndMerId(userId, merId);
        response.setRechargeAmount(userShoppingCredits.getRechargeAmount());
        response.setGiftAmount(userShoppingCredits.getGiftAmount());
        response.setShoppingCreditsBalance(userShoppingCredits.getRechargeAmount().add(userShoppingCredits.getGiftAmount()));
        response.setRechargeTotalAmount(userShoppingCreditsService.getTotalRechargeAmount(userId, merId));
        response.setGiftTotalAmount(userShoppingCreditsService.getTotalGiftAmount(userId, merId));
        response.setConsumeTotalAmount(userShoppingCreditsService.getTotalConsumeAmount(userId, merId));
        return response;
    }

    /**
     * 购物金套餐列表-根据id集合加载
     */
    @Override
    public List<ShoppingCreditsPackage> findPackageListByIds(String ids) {
        List<Integer> idList;
        try {
            idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        } catch (Exception e) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "IDS参数错误");
        }
        List<ShoppingCreditsPackage> packageList = shoppingCreditsPackageService.findByIdList(idList);
        if (CollUtil.isEmpty(packageList)) return new ArrayList<>();
        return packageList;
    }

    /**
     * 购买购物金套餐
     */
    @Override
    public OrderPayResultResponse payShoppingCreditsPackage(PayShoppingCreditsPackageRequest request) {
        User user = userService.getInfo();
        ShoppingCreditsPackage shoppingCreditsPackage = shoppingCreditsPackageService.getByIdException(request.getPackageId());
        if (shoppingCreditsPackage.getShowStatus().equals(0))
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购物金套餐不存在");

        String rechargeNo = CrmebUtil.getOrderNo(OrderConstants.SHOPPING_CREDITS_ORDER_PREFIX);
        OrderPayResultResponse response = new OrderPayResultResponse();
        ShoppingCreditsOrder shoppingCreditsOrder = new ShoppingCreditsOrder();
        if (request.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            MyRecord record = wechatPayment(shoppingCreditsPackage.getRechargeAmount(), request.getPayChannel(), user.getId());
            WxPayJsResultVo vo = record.get("vo");
            String outTradeNo = record.getStr("outTradeNo");
            response.setJsConfig(vo);
            shoppingCreditsOrder.setOutTradeNo(outTradeNo);
        }
        if (request.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY)) {
            String result = aliPayService.pay(rechargeNo, shoppingCreditsPackage.getRechargeAmount(), PayConstants.PAY_SERVICE_TYPE_SHOPPING_CREDITS, request.getPayChannel(), "");
            response.setAlipayRequest(result);
            shoppingCreditsOrder.setOutTradeNo(rechargeNo);
        }
        shoppingCreditsOrder.setMerId(shoppingCreditsPackage.getMerId());
        shoppingCreditsOrder.setUid(user.getId());
        shoppingCreditsOrder.setOrderNo(rechargeNo);
        shoppingCreditsOrder.setRechargeAmount(shoppingCreditsPackage.getRechargeAmount());
        shoppingCreditsOrder.setGiftAmount(shoppingCreditsPackage.getGiftAmount());
        shoppingCreditsOrder.setPayType(request.getPayType());
        shoppingCreditsOrder.setPayChannel(request.getPayChannel());
        boolean save = shoppingCreditsOrderService.save(shoppingCreditsOrder);
        if (!save) {
            throw new CrmebException("生成充值订单失败!");
        }
        response.setOrderNo(rechargeNo);
        return response;
    }

    private MyRecord wechatPayment(BigDecimal rechargeAmount, String payChannel, Integer userId) {
        WxPayJsResultVo vo = wechatUnifiedorder(rechargeAmount, payChannel, userId);
        MyRecord record = new MyRecord();
        record.set("outTradeNo", vo.getOutTradeNo());
        vo.setOutTradeNo(null);
        record.set("vo", vo);
        return record;
    }

    private WxPayJsResultVo wechatUnifiedorder(BigDecimal rechargePrice, String payChannel, Integer userId) {
        // 获取用户openId
        // 根据订单支付类型来判断获取公众号openId还是小程序openId
        UserToken userToken = new UserToken();
        userToken.setToken("");
        if (payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_PUBLIC)) {// 公众号
            userToken = userTokenService.getTokenByUserId(userId, UserConstants.USER_TOKEN_TYPE_WECHAT);
        }
        if (payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_MINI)) {// 小程序
            userToken = userTokenService.getTokenByUserId(userId, UserConstants.USER_TOKEN_TYPE_ROUTINE);
        }

        // 获取appid、mch_id、微信签名key
        String paySource = payChannel;
        if (paySource.equals(PayConstants.PAY_CHANNEL_H5) || paySource.equals(PayConstants.PAY_CHANNEL_WECHAT_NATIVE)) {
            String source = systemConfigService.getValueByKey(SysConfigConstants.WECHAT_PAY_SOURCE_H5_PC);
            if (StrUtil.isNotBlank(source) && source.equals(PayConstants.WECHAT_PAY_SOURCE_MINI)) {
                paySource = PayConstants.PAY_CHANNEL_WECHAT_MINI;
            } else {
                paySource = PayConstants.PAY_CHANNEL_WECHAT_PUBLIC;
            }
        }
        // 根据不同的版本调用对应的微信下单接口
        MyRecord myRecord;
        String outTradeNo;
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);
        if ("V2".equals(crmebConfig.getWxPayVersion())) {
            WxPayUnifiedOrderRequest unifiedOrderRequest = buildWxPayUnifiedOrderRequest(rechargePrice, userId);
            outTradeNo = unifiedOrderRequest.getOutTradeNo();
            myRecord = wechatPayService.createOrder(unifiedOrderRequest, userToken.getToken(), payChannel, paySource, apiDomain);
        } else {
            WxPayUnifiedOrderV3Request v3Request = buildWxPayUnifiedOrderV3Request(rechargePrice, userId);
            outTradeNo = v3Request.getOutTradeNo();
            myRecord = wechatPayService.createV3Order(v3Request, userToken.getToken(), payChannel, paySource, apiDomain);
        }
        WxPayJsResultVo vo = new WxPayJsResultVo();
        vo.setOutTradeNo(outTradeNo);
        switch (payChannel) {
            case PayConstants.PAY_CHANNEL_WECHAT_MINI:
            case PayConstants.PAY_CHANNEL_WECHAT_MINI_VIDEO:
            case PayConstants.PAY_CHANNEL_WECHAT_PUBLIC:
                vo.setAppId(myRecord.getStr("appId"));
                vo.setNonceStr(myRecord.getStr("nonceStr"));
                vo.setPackages(myRecord.getStr("package"));
                vo.setSignType(myRecord.getStr("signType"));
                vo.setPaySign(myRecord.getStr("paySign"));
                vo.setTimeStamp(myRecord.getStr("timeStamp"));
                break;
            case PayConstants.PAY_CHANNEL_H5:
                vo.setMwebUrl(myRecord.getStr("mwebUrl"));
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_NATIVE:
                vo.setMwebUrl(myRecord.getStr("codeUrl"));
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_APP_IOS:
            case PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID:
                vo.setAppId(myRecord.getStr("appId"));
                vo.setPartnerid(myRecord.getStr("partnerId"));
                vo.setPrepayid(myRecord.getStr("prepayId"));
                vo.setPackages(myRecord.getStr("package"));
                vo.setNonceStr(myRecord.getStr("nonceStr"));
                vo.setTimeStamp(myRecord.getStr("timeStamp"));
                vo.setPaySign(myRecord.getStr("sign"));
                break;
        }
        return vo;
    }

    private WxPayUnifiedOrderRequest buildWxPayUnifiedOrderRequest(BigDecimal rechargePrice, Integer uid) {
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        // 因商品名称在微信侧超长更换为网站名称
        String siteName = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME);
        orderRequest.setBody(siteName);

        AttachVo attachVo = new AttachVo(PayConstants.PAY_SERVICE_TYPE_SHOPPING_CREDITS, uid);
        orderRequest.setAttach(JSONObject.toJSONString(attachVo));
        orderRequest.setOutTradeNo(CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_WECHAT));
        orderRequest.setTotalFee(rechargePrice.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        orderRequest.setSpbillCreateIp(RequestUtil.getClientIp());
        String domain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_URL);
        CreateOrderH5SceneInfoVo createOrderH5SceneInfoVo = new CreateOrderH5SceneInfoVo(
                new CreateOrderH5SceneInfoDetailVo(
                        domain,
                        systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME)
                )
        );
        orderRequest.setSceneInfo(JSONObject.toJSONString(createOrderH5SceneInfoVo));
        return orderRequest;
    }

    private WxPayUnifiedOrderV3Request buildWxPayUnifiedOrderV3Request(BigDecimal rechargePrice, Integer uid) {
        WxPayUnifiedOrderV3Request orderRequest = new WxPayUnifiedOrderV3Request();
        // description【商品描述】不能超过127个字符。
        String siteName = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME);
        orderRequest.setDescription(siteName);
        orderRequest.setOutTradeNo(CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_WECHAT));
        AttachVo attachVo = new AttachVo(PayConstants.PAY_SERVICE_TYPE_SHOPPING_CREDITS, uid);
        orderRequest.setAttach(JSONObject.toJSONString(attachVo));
        WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
        amount.setTotal(rechargePrice.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        orderRequest.setAmount(amount);
        return orderRequest;
    }

    /**
     * 购物金购买记录
     */
    @Override
    public PageInfo<ShoppingCreditsOrder> payShoppingCreditsPackageRecord(PayShoppingCreditsPackageRecordRequest request) {
        Integer userId = userService.getUserIdException();
        Page<ShoppingCreditsOrder> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<ShoppingCreditsOrder> list = shoppingCreditsOrderService.findPaidPageByMerIdAndUserId(request.getMerId(), userId);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 用户购物金记录分页列表
     */
    @Override
    public PageInfo<UserShoppingCreditsMonthRecordResponse> userShoppingCreditsPageRecord(UserShoppingCreditsRecordPageRequest request) {
        Integer userId = userService.getUserIdException();
        Page<Object> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<UserShoppingCreditsRecord> recordList = userShoppingCreditsRecordService.findFrontList(userId, request.getMerId(), request.getSearchType());
        if (CollUtil.isEmpty(recordList)) return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        List<UserShoppingCreditsMonthRecordResponse> monthResponseArrayList = new ArrayList<>();
        List<String> monthList = CollUtil.newArrayList();
        recordList.forEach(record -> {
            String month = CrmebDateUtil.dateToStr(record.getCreateTime(), DateConstants.DATE_FORMAT_MONTH);
            if (monthList.contains(month)) {
                //如果在已有的数据中找到当前月份数据则追加
                for (UserShoppingCreditsMonthRecordResponse monthRecordResponse : monthResponseArrayList) {
                    if (monthRecordResponse.getYearMonth().equals(month)) {
                        monthRecordResponse.getRecordList().add(record);
                        break;
                    }
                }
            } else {// 不包含此月份
                //创建一个
                UserShoppingCreditsMonthRecordResponse monthResponse = new UserShoppingCreditsMonthRecordResponse();
                monthResponse.setYearMonth(month);
                monthResponse.setRecordList(CollUtil.newArrayList(record));
                monthResponseArrayList.add(monthResponse);
                monthList.add(month);
            }
        });
        return CommonPage.copyPageInfo(page, monthResponseArrayList);
    }

    /**
     * 购物金订单退款申请
     */
    @Override
    public Boolean orderRefundApply(ShoppingCreditsOrderRefundApplyRequest request) {
        Integer userId = userService.getUserIdException();
        ShoppingCreditsOrder shoppingCreditsOrder = shoppingCreditsOrderService.getByOrderNo(request.getOrderNo());
        if (ObjectUtil.isNull(shoppingCreditsOrder) || !shoppingCreditsOrder.getUid().equals(userId)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "订单不存在");
        }
        if (!shoppingCreditsOrder.getPaid()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "订单未支付");
        }
        if (!shoppingCreditsOrder.getRefundStatus().equals(0)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "订单正在退款中");
        }
        UserShoppingCredits userShoppingCredits = userShoppingCreditsService.getByUserIdAndMerId(userId, shoppingCreditsOrder.getMerId());
        if (userShoppingCredits.getRechargeAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购物金充值金额为0，无法退款");
        }

        ShoppingCreditsRefundOrder refundOrder = new ShoppingCreditsRefundOrder();
        refundOrder.setOrderNo(shoppingCreditsOrder.getOrderNo());
        refundOrder.setRefundOrderNo(CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_SHOPPING_CREDITS_REFUND));
        refundOrder.setMerId(shoppingCreditsOrder.getMerId());
        refundOrder.setUid(shoppingCreditsOrder.getUid());
        refundOrder.setRefundReason(StrUtil.isNotBlank(request.getRefundReason()) ? URLUtil.decode(request.getRefundReason()) : "");
        refundOrder.setRefundStatus(0);
        refundOrder.setRefundPayType(shoppingCreditsOrder.getPayType());

        if (userShoppingCredits.getRechargeAmount().compareTo(shoppingCreditsOrder.getRechargeAmount()) >= 0) {
            refundOrder.setRefundAmount(shoppingCreditsOrder.getRechargeAmount());
        } else {
            refundOrder.setRefundAmount(userShoppingCredits.getRechargeAmount());
        }
        if (userShoppingCredits.getGiftAmount().compareTo(shoppingCreditsOrder.getGiftAmount()) >= 0) {
            refundOrder.setRefundGiftAmount(shoppingCreditsOrder.getGiftAmount());
        } else {
            refundOrder.setRefundGiftAmount(userShoppingCredits.getGiftAmount());
        }

        return transactionTemplate.execute(e -> {
            Boolean result = shoppingCreditsOrderService.updateRefundStatus(shoppingCreditsOrder.getOrderNo(), 1);
            if (!result) {
                log.error("购物金订单退款申请，变更退款状态失败，订单号：{}", shoppingCreditsOrder.getOrderNo());
                e.setRollbackOnly();
                return false;
            }
            shoppingCreditsRefundOrderService.save(refundOrder);
            result = userShoppingCreditsService.updateAmount(userId, shoppingCreditsOrder.getMerId(), refundOrder.getRefundAmount(), refundOrder.getRefundGiftAmount(), Constants.OPERATION_TYPE_SUBTRACT);
            if (!result) {
                log.error("购物金订单退款申请，变更用户购物金失败，订单号：{}", shoppingCreditsOrder.getOrderNo());
                e.setRollbackOnly();
                return false;
            }
            // 用户购物金记录
            UserShoppingCreditsRecord record = new UserShoppingCreditsRecord();
            record.setUid(userId);
            record.setMerId(shoppingCreditsOrder.getMerId());
            record.setLinkNo(refundOrder.getRefundOrderNo());
            record.setLinkType("refund");
            record.setType(BalanceRecordConstants.BALANCE_RECORD_TYPE_SUB);
            record.setTitle("购物金退款");
            record.setRechargeAmount(refundOrder.getRefundAmount());
            record.setGiftAmount(refundOrder.getRefundGiftAmount());
            userShoppingCreditsRecordService.save(record);
            return Boolean.TRUE;
        });
    }

    /**
     * 购物金退款订单分页列表
     */
    @Override
    public PageInfo<ShoppingCreditsRefundOrder> shoppingCreditsRefundOrderPage(PayShoppingCreditsPackageRecordRequest request) {
        Integer userId = userService.getUserIdException();
        Page<Object> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<ShoppingCreditsRefundOrder> orderList = shoppingCreditsRefundOrderService.findByUserIdAndMerId(userId, request.getMerId());
        if (CollUtil.isEmpty(orderList)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        return CommonPage.copyPageInfo(page, orderList);
    }
}
