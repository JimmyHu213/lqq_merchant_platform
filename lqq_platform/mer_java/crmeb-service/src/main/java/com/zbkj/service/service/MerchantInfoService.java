package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.merchant.MerchantInfo;
import com.zbkj.common.request.reservation.MerchantReservationToHomeRequest;
import com.zbkj.common.request.reservation.MerchantReservationToStoreRequest;
import com.zbkj.common.response.reservation.MerchantReservationToStoreResponse;
import com.zbkj.common.response.reservation.MerchantReservationToHomeResponse;

/**
*  MerchantInfoService 接口
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
public interface MerchantInfoService extends IService<MerchantInfo> {

    /**
     * 通过商户id获取
     * @param merId 商户id
     * @return MerchantInfo
     */
    MerchantInfo getByMerId(Integer merId);

    /**
     * 获取商户预约设置信息
     * @return  商户预约设置信息响应对象
     */
    MerchantReservationToHomeResponse getReservationConfigInfo();

    /**
     * 编辑商家预约设置信息
     * @param merchantReservationToHomeRequest 商家预约设置信息对象
     * @return 编辑结果
     */
    Boolean editReservationConfig(MerchantReservationToHomeRequest merchantReservationToHomeRequest);

    /**
     * 获取商户到店服务预约设置信息
     *
     * @return  商户到店服务预约设置信息响应对象
     */
    MerchantReservationToStoreResponse getReservationConfigToStoreInfo();

    /**
     * 编辑商家到店服务预约设置信息
     *
     * @param merchantReservationRequest  商户到店服务预约设置请求对象
     * @return 编辑结果
     */
    Boolean editToStoreReservationConfig(MerchantReservationToStoreRequest merchantReservationRequest);


}
