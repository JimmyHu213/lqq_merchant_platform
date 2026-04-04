package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.circle.CircleMenu;
import com.zbkj.common.request.SystemMenuSearchRequest;
import com.zbkj.common.request.circle.CircleMenuRequest;
import com.zbkj.common.vo.MenuCheckVo;

import java.util.List;

/**
* @author zzp
* @description CircleMenuService 接口
* @date 2025-09-16
*/
public interface CircleMenuService extends IService<CircleMenu> {

    List<CircleMenu> getCircleMenuList(SystemMenuSearchRequest request);
    Boolean addCircleMenu(CircleMenuRequest request);

    Boolean deleteCircleMenu(Integer id);

    Boolean editCircleMenu(CircleMenuRequest request);
    CircleMenu getInfo(Integer id);
    Boolean updateCircleShowStatus(Integer id);

    List<CircleMenu> getMenuCacheList(Integer value);

    List<MenuCheckVo> getCircleMenuCacheTree();

    List<CircleMenu> findPermissionByUserId(Integer uid);

    List<CircleMenu> getMenusByUserId(Integer id);

    List<MenuCheckVo> getCircleMenuCacheList();

    List<CircleMenu> getList();
}