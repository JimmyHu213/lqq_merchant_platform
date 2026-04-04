package com.zbkj.service.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.circle.CircleRoleMenu;
import com.zbkj.service.dao.circle.CircleRoleMenuDao;
import com.zbkj.service.service.CircleRoleMenuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author zzp
* @description CircleRoleMenuServiceImpl 接口实现
* @date 2025-09-16
*/
@Service
public class CircleRoleMenuServiceImpl extends ServiceImpl<CircleRoleMenuDao, CircleRoleMenu> implements CircleRoleMenuService {

    @Resource
    private CircleRoleMenuDao dao;

    @Override
    public Boolean deleteByRidAndMenuId(int rid, Integer menuId) {
        LambdaUpdateWrapper<CircleRoleMenu> luw = Wrappers.lambdaUpdate();
        luw.eq(CircleRoleMenu::getRid, rid);
        luw.eq(CircleRoleMenu::getMenuId, menuId);
        return dao.delete(luw) > 0;
    }

    @Override
    public Boolean deleteByRidAndMenuIdList(int rid, List<Integer> menuIdList) {
        LambdaUpdateWrapper<CircleRoleMenu> luw = Wrappers.lambdaUpdate();
        luw.eq(CircleRoleMenu::getRid, rid);
        luw.in(CircleRoleMenu::getMenuId, menuIdList);
        return dao.delete(luw) > 0;
    }

    @Override
    public Boolean deleteByRid(Integer rid) {
        LambdaUpdateWrapper<CircleRoleMenu> luw = Wrappers.lambdaUpdate();
        luw.eq(CircleRoleMenu::getRid, rid);
        return dao.delete(luw) > 0;
    }

    @Override
    public List<Integer> getMenuListByRid(Integer rid) {
        LambdaQueryWrapper<CircleRoleMenu> lqw = Wrappers.lambdaQuery();
        lqw.select(CircleRoleMenu::getMenuId);
        lqw.eq(CircleRoleMenu::getRid, rid);
        List<CircleRoleMenu> roleMenuList = dao.selectList(lqw);
        return roleMenuList.stream().map(CircleRoleMenu::getMenuId).collect(Collectors.toList());
    }


}

