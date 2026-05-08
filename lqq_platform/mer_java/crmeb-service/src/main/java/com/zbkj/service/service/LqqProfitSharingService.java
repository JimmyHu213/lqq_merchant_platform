package com.zbkj.service.service;

import com.zbkj.common.model.order.MerchantOrder;

/**
 * [LQQ-迁移] 溜圈圈多方分账业务 Service 接口
 * 负责：计算分润金额、生成分账记录、调用微信分账API、分账解冻
 */
public interface LqqProfitSharingService {

    /**
     * 为订单生成分账记录（支付成功后调用）
     * 计算各方分润金额并写入 eb_wechat_profit_sharing_record 表
     *
     * @param merchantOrder 商户订单
     */
    void createProfitSharingRecords(MerchantOrder merchantOrder);

    /**
     * 执行待分账记录的微信分账请求
     * 由定时任务调用，批量处理 status=0 的记录
     */
    void executePendingProfitSharing();

    /**
     * 对指定订单执行分账解冻
     *
     * @param orderNo 订单号
     */
    void unfreezeOrder(String orderNo);
}
