package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.circle.CircleAdmin;
import com.zbkj.common.request.*;
import com.zbkj.common.response.SystemAdminResponse;

/**
* @author zzp
* @description CircleAdminService 接口
* @date 2025-09-16
*/
public interface CircleAdminService extends IService<CircleAdmin> {

    Boolean deleteByCircleId(Integer circleId);

    Integer checkAdminAccount(String account);

    Boolean updateCircleAdminData(String realName, String phone, Integer circleId, String role);

    CircleAdmin getByCircleId(Integer circleId);

    Boolean isExistRole(Integer id);

    CircleAdmin selectUserByAccountAndType(String account, Integer type);

    PageInfo<SystemAdminResponse> getCircleAdminList(SystemAdminRequest request, PageParamRequest pageParamRequest);

    Boolean saveCircleAdmin(SystemAdminAddRequest request);

    Boolean removeCircleAdmin(Integer id);

    Boolean updateCircleAdmin(SystemAdminUpdateRequest request);

    CircleAdmin getDetail(Integer id);

    Boolean updateCircleAdminStatus(Integer id, Boolean status);

    Boolean updateCircleAdminPassword(SystemAdminUpdatePwdRequest request);


    //List<CircleAdmin> getList(CircleAdminSearchRequest request, PageParamRequest pageParamRequest);
}