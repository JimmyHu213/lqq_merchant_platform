package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.circle.CircleRole;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemRoleRequest;
import com.zbkj.common.request.SystemRoleSearchRequest;
import com.zbkj.common.request.SystemRoleStatusRequest;
import com.zbkj.common.response.RoleInfoResponse;

import java.util.List;

/**
* @author zzp
* @description CircleRoleService 接口
* @date 2025-09-16
*/
public interface CircleRoleService extends IService<CircleRole> {
    List<CircleRole> getPlaCircleRoleList(SystemRoleSearchRequest request, PageParamRequest pageParamRequest);

    Boolean addPlaCircleRole(SystemRoleRequest request);

    Boolean deletePlaCircleRole(Integer id);

    Boolean editPlaCircleRole(SystemRoleRequest request);

    RoleInfoResponse getInfo(Integer id);

    Boolean updateStatus(SystemRoleStatusRequest request);


    /**
     * 根据圈层ID获取圈层角色列表
     *
     * @param circleId  圈层ID
     * @return 圈层角色列表
     */
    List<CircleRole> getListByCircleId(Integer circleId);

    List<CircleRole> getCircleRoleList(SystemRoleSearchRequest request, PageParamRequest pageParamRequest);

    Boolean addCircleRole(SystemRoleRequest request);

    Boolean deleteCircleRole(Integer id);

    Boolean editCircleRole(SystemRoleRequest request);
}