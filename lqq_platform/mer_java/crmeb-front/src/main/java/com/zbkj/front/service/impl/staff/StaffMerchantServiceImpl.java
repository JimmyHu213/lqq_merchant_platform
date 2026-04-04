package com.zbkj.front.service.impl.staff;

import com.zbkj.common.model.merchant.MerchantInfo;
import com.zbkj.common.model.reservation.MerchantServiceStaff;
import com.zbkj.common.model.system.SystemForm;
import com.zbkj.common.response.SystemFormResponse;
import com.zbkj.common.token.FrontTokenComponent;
import com.zbkj.front.response.MerchantReservationConfigResponse;
import com.zbkj.front.service.staff.StaffMerchantService;
import com.zbkj.service.service.MerchantInfoService;
import com.zbkj.service.service.MerchantServiceStaffService;
import com.zbkj.service.service.SystemFormService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 服务员工商户服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/17
 */
@Service
public class StaffMerchantServiceImpl implements StaffMerchantService {

    @Autowired
    private FrontTokenComponent tokenComponent;
    @Autowired
    private MerchantServiceStaffService serviceStaffService;
    @Autowired
    private MerchantInfoService merchantInfoService;
    @Autowired
    private SystemFormService systemFormService;

    /**
     * 商户预约设置信息
     */
    @Override
    public MerchantReservationConfigResponse getReservationConfig() {
        Integer staffId = tokenComponent.getServiceStaffId();
        MerchantServiceStaff staff = serviceStaffService.getById(staffId);
        MerchantInfo merchantInfo = merchantInfoService.getByMerId(staff.getMerId());
        MerchantReservationConfigResponse response = new MerchantReservationConfigResponse();
        BeanUtils.copyProperties(merchantInfo, response);
        return response;
    }

    /**
     * 商户预约服务留凭表单信息
     */
    @Override
    public SystemFormResponse getReservationServiceEvidenceFormInfo() {
        Integer staffId = tokenComponent.getServiceStaffId();
        MerchantServiceStaff staff = serviceStaffService.getById(staffId);
        MerchantInfo merchantInfo = merchantInfoService.getByMerId(staff.getMerId());
        SystemForm systemForm = systemFormService.getById(merchantInfo.getServiceEvidenceFormId());
        SystemFormResponse response = new SystemFormResponse();
        response.setId(systemForm.getId());
        response.setFormName(systemForm.getFormName());
        response.setFormValue(systemForm.getFormValue());
        return response;
    }
}
