package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.circle.CircleRoleMenu;

import java.util.List;

/**
* @author zzp
* @description CircleRoleMenuService 接口
* @date 2025-09-16
*/
public interface CircleRoleMenuService extends IService<CircleRoleMenu> {
    Boolean deleteByRidAndMenuId(int rid, Integer menuId);

    Boolean deleteByRidAndMenuIdList(int rid, List<Integer> menuIdList);

    Boolean deleteByRid(Integer rid);

    List<Integer> getMenuListByRid(Integer rid);

    //List<CircleRoleMenu> getList(CircleRoleMenuSearchRequest request, PageParamRequest pageParamRequest);
}