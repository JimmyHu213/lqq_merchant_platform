package com.zbkj.front.service.impl.employee;

import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.request.merchant.MerchantServiceStaffSearchRequest;
import com.zbkj.common.response.MerchantServiceStaffResponse;
import com.zbkj.front.service.employee.EmployeeServiceStaffService;
import com.zbkj.service.service.MerchantEmployeeService;
import com.zbkj.service.service.MerchantServiceStaffService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceStaffServiceImpl implements EmployeeServiceStaffService {

    @Autowired
    private MerchantServiceStaffService merchantServiceStaffService;
    @Autowired
    private UserService userService;
    @Autowired
    private MerchantEmployeeService merchantEmployeeService;

    @Override
    public List<MerchantServiceStaffResponse> getList(MerchantServiceStaffSearchRequest request) {
        //merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_WORK_ORDER.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return merchantServiceStaffService.getStaffList(request, systemAdmin);
    }
}
