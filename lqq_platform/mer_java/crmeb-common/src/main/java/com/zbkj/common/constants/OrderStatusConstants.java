package com.zbkj.common.constants;

/**
 * 订单状态日志常量类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public class OrderStatusConstants {

    /** 操作类型-创建 */
    public static final String ORDER_STATUS_CREATE = "create";
    /** 操作类型-支付成功 */
    public static final String ORDER_STATUS_PAY_SUCCESS = "paid";
    /** 操作类型-发货 */
    public static final String ORDER_STATUS_DELIVERY = "delivery";
    /** 操作类型-订单收货 */
    public static final String ORDER_STATUS_RECEIPT = "receipt";
    /** 操作类型-订单完成 */
    public static final String ORDER_STATUS_COMPLETE = "complete";
    /** 操作类型-订单取消 */
    public static final String ORDER_STATUS_CANCEL = "cancel";
    /** 操作类型-订单退款 */
    public static final String ORDER_STATUS_REFUND = "refund";



    /** 订单操作日志类型说明-生成订单 */
    public static final String ORDER_LOG_MESSAGE_CREATE = "订单生成";
    /** 订单操作日志类型说明-用户付款成功 */
    public static final String ORDER_LOG_MESSAGE_PAY_SUCCESS = "用户付款成功";
    /** 订单操作日志类型说明-虚拟发货 */
    public static final String ORDER_LOG_MESSAGE_DELIVERY_VI = "虚拟发货";
    /** 订单操作日志类型说明-快递发货 */
    public static final String ORDER_LOG_MESSAGE_EXPRESS = "快递公司：{deliveryName}, 快递单号：{deliveryCode}";
    /** 订单操作日志类型说明-商家配置 */
    public static final String ORDER_LOG_MESSAGE_SEND_MERCHANT = "配送人员：{}, 手机号：{}";
    /** 订单操作日志类型说明-无需发货 */
    public static final String ORDER_LOG_MESSAGE_SEND_NO_NEED = "发货备注：{}";
    /** 订单操作日志类型说明-用户收货 */
    public static final String ORDER_LOG_USER_RECEIPT = "用户已收货";
    /** 订单操作日志类型说明-系统自动收货 */
    public static final String ORDER_LOG_SYSTEM_AUTO_RECEIPT = "系统自动收货";
    /** 订单操作日志类型说明-用户取消订单 */
    public static final String ORDER_LOG_USER_CANCEL = "用户取消订单";
    /** 订单操作日志类型说明-系统自动取消订单 */
    public static final String ORDER_LOG_SYSTEM_AUTO_CANCEL = "订单超时,系统自动取消订单";
    /** 订单操作日志类型说明-订单完成 */
    public static final String ORDER_LOG_COMPLETE = "订单已完成";
    /** 订单操作日志类型说明-订单已退款 */
    public static final String ORDER_LOG_REFUND = "订单已退款";
    /** 订单操作日志类型说明-用户收货 */
    public static final String ORDER_LOG_MERCHANT_VERIFY = "商家已核销";
}
