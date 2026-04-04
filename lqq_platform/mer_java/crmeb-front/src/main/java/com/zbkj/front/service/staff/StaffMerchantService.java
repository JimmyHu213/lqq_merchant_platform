package com.zbkj.front.service.staff;

import com.zbkj.common.response.SystemFormResponse;
import com.zbkj.front.response.MerchantReservationConfigResponse;

/**
 * 服务员工商户服务类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/17
 */
public interface StaffMerchantService {

    /**
     * 商户预约设置信息
     */
    MerchantReservationConfigResponse getReservationConfig();

    /**
     * 商户预约服务留凭表单信息
     */
    SystemFormResponse getReservationServiceEvidenceFormInfo();
}
