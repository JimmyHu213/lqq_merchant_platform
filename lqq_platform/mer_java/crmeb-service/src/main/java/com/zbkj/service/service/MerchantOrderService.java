package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.order.MerchantOrder;

import java.util.List;
import java.util.Map;

/**
*  MerchantOrderService 接口
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
public interface MerchantOrderService extends IService<MerchantOrder> {

    /**
     * 根据主订单号获取商户订单
     * @param orderNo 主订单号
     * @return List
     */
    List<MerchantOrder> getByOrderNo(String orderNo);

    /**
     * 根据主订单号获取商户订单（支付完成进行商户拆单后可用）
     * @param orderNo 主订单号
     * @return MerchantOrder
     */
    MerchantOrder getOneByOrderNo(String orderNo);

    /**
     * 通过核销码获取订单
     * @param verifyCode 核销码
     */
    MerchantOrder getOneByVerifyCode(String verifyCode);


    /**
     * 商户查询待核销订单
     * @param verifyCode 核销订单
     * @param merId 商户id
     * @return 待核销订单
     */
    MerchantOrder getByVerifyCodeForMerchant(String verifyCode, Integer merId);

    Map<String, List<MerchantOrder>> getMapByOrderNoList(List<String> orderNoList);

    /**
     * 商户订单统计数据--发货类型
     * @param merId 商户id
     */
    List<MerchantOrder> statistics(Integer merId);

    /**
     * 根据时间、商户id获取下单件数
     * @param date 时间，格式'yyyy-MM-dd'
     * @param merId 商户id
     * @return Integer
     */
    Integer getOrderProductNumByDateAndMerId(String date, Integer merId);
}
