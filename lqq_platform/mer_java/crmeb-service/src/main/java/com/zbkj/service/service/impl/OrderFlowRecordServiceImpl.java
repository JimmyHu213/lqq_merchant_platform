package com.zbkj.service.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.OrderStatusConstants;
import com.zbkj.common.model.order.OrderFlowRecord;
import com.zbkj.service.dao.OrderFlowRecordDao;
import com.zbkj.service.service.OrderFlowRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单流转记录服务实现类
 *
 * @author HZW
 * @description OrderFlowRecordServiceImpl 接口实现
 * @date 2025-10-20
 */
@Service
public class OrderFlowRecordServiceImpl extends ServiceImpl<OrderFlowRecordDao, OrderFlowRecord> implements OrderFlowRecordService {

    @Resource
    private OrderFlowRecordDao dao;

    /**
     * 创建记录
     *
     * @param orderNo     订单号
     * @param flowType    流转类型
     * @param flowMessage 流转信息
     * @return Boolean
     */
    @Override
    public Boolean createRecord(String orderNo, String flowType, String flowMessage, Integer operatorId, Integer operatorType) {
        OrderFlowRecord record = new OrderFlowRecord();
        record.setOrderNo(orderNo);
        record.setFlowType(flowType);
        record.setFlowMessage(flowMessage);
        record.setOperatorId(operatorId);
        record.setOperatorType(operatorType);
        record.setOperatorTime(new Date());
        return save(record);
    }

    /**
     * 创建订单
     *
     * @param orderNo    订单号
     * @param operatorId 操作人（用户ID）
     * @return Boolean
     */
    @Override
    public Boolean orderCreate(String orderNo, Integer operatorId) {
        return createRecord(orderNo, OrderStatusConstants.ORDER_STATUS_CREATE, OrderStatusConstants.ORDER_LOG_MESSAGE_CREATE, operatorId, 1);
    }

    /**
     * 订单支付
     *
     * @param orderNo    订单号
     * @param operatorId 操作人（用户ID）
     * @return Boolean
     */
    @Override
    public Boolean orderPaid(String orderNo, Integer operatorId) {
        return createRecord(orderNo, OrderStatusConstants.ORDER_STATUS_PAY_SUCCESS, OrderStatusConstants.ORDER_LOG_MESSAGE_PAY_SUCCESS, operatorId, 1);
    }

    /**
     * 订单取消
     *
     * @param orderNo      订单号
     * @param operatorId   操作人（用户ID）
     * @param cancelMethod 取消方法：user-用户取消,auto—系统自动取消
     * @return Boolean
     */
    @Override
    public Boolean orderCancel(String orderNo, Integer operatorId, String cancelMethod) {
        String cancelMessage;
        int operatorType;
        if ("auto".equals(cancelMethod)) {
            cancelMessage = OrderStatusConstants.ORDER_LOG_SYSTEM_AUTO_CANCEL;
            operatorType = 0;
        } else {
            cancelMessage = OrderStatusConstants.ORDER_LOG_USER_CANCEL;
            operatorType = 1;
        }
        return createRecord(orderNo, OrderStatusConstants.ORDER_STATUS_CANCEL, cancelMessage, operatorId, operatorType);
    }

    /**
     * 订单拆单
     * 将总订单的记录copy给商户订单
     *
     * @param orderNo    订单号
     * @param newOrderNo 拆单订单号
     * @return Boolean
     */
    @Override
    public Boolean orderSplit(String orderNo, String newOrderNo) {
        List<OrderFlowRecord> recordList = findRecordListByOrderNo(orderNo);
        recordList.forEach(record -> {
            record.setId(null);
            record.setOrderNo(newOrderNo);
        });
        return saveBatch(recordList);
    }

    /**
     * 订单拆单
     *
     * @param orderNo        订单号
     * @param newOrderNoList 拆单订单号列表
     * @return Boolean
     */
    @Override
    public Boolean orderSplit(String orderNo, List<String> newOrderNoList) {
        List<OrderFlowRecord> flowRecordList = new ArrayList<>();
        List<OrderFlowRecord> recordList = findRecordListByOrderNo(orderNo);
        for (String newOrderNo : newOrderNoList) {
            List<OrderFlowRecord> orderFlowRecordList = recordList.stream().map(e -> {
                OrderFlowRecord flowRecord = new OrderFlowRecord();
                BeanUtils.copyProperties(e, flowRecord);
                flowRecord.setId(null);
                flowRecord.setOrderNo(newOrderNo);
                return flowRecord;
            }).collect(Collectors.toList());
            flowRecordList.addAll(orderFlowRecordList);
        }
        return saveBatch(flowRecordList);
    }

    /**
     * 订单发货
     *
     * @param orderNo      订单号
     * @param flowMessage  流转信息
     * @param operatorId   操作人（管理员）
     * @param operatorType 操作类型 3-商户管理员，4-移动端商户管理员
     */
    @Override
    public Boolean orderDelivery(String orderNo, String flowMessage, Integer operatorId, Integer operatorType) {
        return createRecord(orderNo, OrderStatusConstants.ORDER_STATUS_DELIVERY, flowMessage, operatorId, operatorType);
    }

    /**
     * 订单收货
     *
     * @param orderNo    订单号
     * @param operatorId 操作人（用户ID）
     * @param method     收货方法：user-用户收货,auto—系统自动收货,verify-核销
     * @param operatorType 操作类型 3-商户管理员，4-移动端商户管理员
     */
    @Override
    public Boolean orderReceipt(String orderNo, Integer operatorId, String method, Integer operatorType) {
        String cancelMessage;
        int operatorTy;
        if ("auto".equals(method)) {
            cancelMessage = OrderStatusConstants.ORDER_LOG_SYSTEM_AUTO_RECEIPT;
            operatorTy = 0;
        } else if ("user".equals(method)) {
            cancelMessage = OrderStatusConstants.ORDER_LOG_USER_RECEIPT;
            operatorTy = 1;
        } else {
            cancelMessage = OrderStatusConstants.ORDER_LOG_MERCHANT_VERIFY;
            operatorTy = operatorType;
        }
        return createRecord(orderNo, OrderStatusConstants.ORDER_STATUS_RECEIPT, cancelMessage, operatorId, operatorTy);
    }

    /**
     * 订单完成
     *
     * @param orderNo 订单号
     */
    @Override
    public Boolean orderComplete(String orderNo) {
        return createRecord(orderNo, OrderStatusConstants.ORDER_STATUS_COMPLETE, OrderStatusConstants.ORDER_LOG_COMPLETE, 0, 0);
    }

    /**
     * 订单已退款
     * @param orderNo 订单号
     */
    @Override
    public Boolean orderRefund(String orderNo) {
        return createRecord(orderNo, OrderStatusConstants.ORDER_STATUS_REFUND, OrderStatusConstants.ORDER_LOG_REFUND, 0, 0);
    }

    /**
     * 通过订单号获取订单流转记录列表
     *
     * @param orderNo 订单号
     * @return List
     */
    @Override
    public List<OrderFlowRecord> findRecordListByOrderNo(String orderNo) {
        LambdaQueryWrapper<OrderFlowRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(OrderFlowRecord::getOrderNo, orderNo);
        lqw.orderByDesc(OrderFlowRecord::getOperatorTime, OrderFlowRecord::getId);
        return dao.selectList(lqw);
    }


}

