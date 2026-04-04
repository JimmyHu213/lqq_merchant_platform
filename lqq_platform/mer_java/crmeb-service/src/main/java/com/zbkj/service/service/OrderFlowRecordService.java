package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.order.OrderFlowRecord;

import java.util.List;

/**
 * 订单流转记录服务
 *
 * @author HZW
 * @description OrderFlowRecordService 接口
 * @date 2025-10-20
 */
public interface OrderFlowRecordService extends IService<OrderFlowRecord> {

    /**
     * 创建记录
     *
     * @param orderNo     订单号
     * @param flowType    流转类型
     * @param flowMessage 流转信息
     * @return Boolean
     */
    Boolean createRecord(String orderNo, String flowType, String flowMessage, Integer operatorId, Integer operatorType);

    /**
     * 创建订单
     *
     * @param orderNo    订单号
     * @param operatorId 操作人（用户ID）
     * @return Boolean
     */
    Boolean orderCreate(String orderNo, Integer operatorId);

    /**
     * 订单支付
     *
     * @param orderNo    订单号
     * @param operatorId 操作人（用户ID）
     * @return Boolean
     */
    Boolean orderPaid(String orderNo, Integer operatorId);

    /**
     * 订单取消
     *
     * @param orderNo      订单号
     * @param operatorId   操作人（用户ID）
     * @param cancelMethod 取消方法：user-用户取消,auto—系统自动取消
     * @return Boolean
     */
    Boolean orderCancel(String orderNo, Integer operatorId, String cancelMethod);

    /**
     * 订单拆单
     *
     * @param orderNo    订单号
     * @param newOrderNo 拆单订单号
     * @return Boolean
     */
    Boolean orderSplit(String orderNo, String newOrderNo);

    /**
     * 订单拆单
     *
     * @param orderNo        订单号
     * @param newOrderNoList 拆单订单号列表
     * @return Boolean
     */
    Boolean orderSplit(String orderNo, List<String> newOrderNoList);

    /**
     * 订单发货
     *
     * @param orderNo      订单号
     * @param flowMessage  流转信息
     * @param operatorId   操作人（管理员）
     * @param operatorType 操作类型 3-商户管理员，4-移动端商户管理员
     */
    Boolean orderDelivery(String orderNo, String flowMessage, Integer operatorId, Integer operatorType);

    /**
     * 订单收货
     *
     * @param orderNo    订单号
     * @param operatorId 操作人（用户ID）
     * @param method     收货方法：user-用户收货,auto—系统自动收货,verify-核销
     * @param operatorType 操作类型 3-商户管理员，4-移动端商户管理员
     */
    Boolean orderReceipt(String orderNo, Integer operatorId, String method, Integer operatorType);

    /**
     * 订单完成
     *
     * @param orderNo 订单号
     */
    Boolean orderComplete(String orderNo);

    /**
     * 订单已退款
     * @param orderNo 订单号
     */
    Boolean orderRefund(String orderNo);

    /**
     * 通过订单号获取订单流转记录列表
     *
     * @param orderNo 订单号
     * @return List
     */
    List<OrderFlowRecord> findRecordListByOrderNo(String orderNo);

}