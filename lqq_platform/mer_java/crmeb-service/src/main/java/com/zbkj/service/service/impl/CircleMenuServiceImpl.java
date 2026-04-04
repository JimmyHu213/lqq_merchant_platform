package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.MenuConstants;
import com.zbkj.common.constants.RedisConstants;
import com.zbkj.common.enums.RoleEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.circle.CircleAdmin;
import com.zbkj.common.model.circle.CircleMenu;
import com.zbkj.common.request.SystemMenuSearchRequest;
import com.zbkj.common.request.circle.CircleMenuRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.SystemConfigResultCode;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.LoginUserVo;
import com.zbkj.common.vo.MenuCheckTree;
import com.zbkj.common.vo.MenuCheckVo;
import com.zbkj.service.dao.circle.CircleMenuDao;
import com.zbkj.service.service.CircleAdminService;
import com.zbkj.service.service.CircleMenuService;
import com.zbkj.service.service.CircleRoleMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author zzp
* @description CircleMenuServiceImpl 接口实现
* @date 2025-09-16
*/
@Service
public class CircleMenuServiceImpl extends ServiceImpl<CircleMenuDao, CircleMenu> implements CircleMenuService {

    @Resource
    private CircleMenuDao dao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CircleRoleMenuService roleMenuService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private CircleAdminService circleAdminService;

    @Override
    public List<CircleMenu> getCircleMenuList(SystemMenuSearchRequest request) {
        LambdaQueryWrapper<CircleMenu> lqw = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(request.getName())) {
            String decode = URLUtil.decode(request.getName());
            lqw.like(CircleMenu::getName, decode);
        }
        if (StrUtil.isNotBlank(request.getMenuType())) {
            lqw.eq(CircleMenu::getMenuType, request.getMenuType());
        }
        lqw.eq(CircleMenu::getIsDelte, false);
        lqw.eq(CircleMenu::getType, RoleEnum.CIRCLE_ADMIN.getValue());
        lqw.orderByDesc(CircleMenu::getSort);
        lqw.orderByAsc(CircleMenu::getId);
        return dao.selectList(lqw);
    }

    @Override
    public Boolean addCircleMenu(CircleMenuRequest request) {
        if (request.getMenuType().equals(MenuConstants.TYPE_C) && StrUtil.isEmpty(request.getComponent())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "菜单类型的组件路径不能为空");
        }
        if (request.getMenuType().equals(MenuConstants.TYPE_A) && StrUtil.isEmpty(request.getPerms())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "按钮类型的权限表示不能为空");
        }
        CircleMenu circleMenu = new CircleMenu();
        request.setId(null);
        BeanUtils.copyProperties(request, circleMenu);
        circleMenu.setType(RoleEnum.CIRCLE_ADMIN.getValue());

        Boolean execute = transactionTemplate.execute(e -> {
            save(circleMenu);
            // 超管的菜单是单独且不同的，此处不做处理
            //CircleRoleMenu circleRoleMenu = new CircleRoleMenu();
            //circleRoleMenu.setMenuId(circleMenu.getId());
            //circleRoleMenu.setRid(RoleEnum.CIRCLE_SUPER.getValue());
            //roleMenuService.save(circleRoleMenu);
            return Boolean.TRUE;
        });
        if (Boolean.TRUE.equals(execute)) {
            redisUtil.delete(RedisConstants.CIRCLE_MENU_CACHE_LIST_KEY);
        }
        return execute;
    }

    @Override
    public Boolean deleteCircleMenu(Integer id) {
        CircleMenu circleMenu = getInfo(id);
        if (ObjectUtil.isNull(circleMenu) || circleMenu.getIsDelte() || circleMenu.getType() != 6) {
            throw new CrmebException(SystemConfigResultCode.MENU_NOT_EXIST);
        }
        circleMenu.setIsDelte(true);
        circleMenu.setUpdateTime(DateUtil.date());
        String redisKey = RedisConstants.CIRCLE_MENU_CACHE_LIST_KEY;
        if (circleMenu.getMenuType().equals(MenuConstants.TYPE_A)) {
            Boolean execute = transactionTemplate.execute(e -> {
                updateById(circleMenu);
                // 超管的菜单是单独且不同的，此处不做处理
                //roleMenuService.deleteByRidAndMenuId(RoleEnum.CIRCLE_SUPER.getValue(), circleMenu.getId());
                return Boolean.TRUE;
            });
            if (Boolean.TRUE.equals(execute)) {
                redisUtil.delete(redisKey);
            }
            return execute;
        }
        List<CircleMenu> childList = findAllChildListByPid(id, circleMenu.getMenuType());
        if (CollUtil.isEmpty(childList)) {
            Boolean execute = transactionTemplate.execute(e -> {
                updateById(circleMenu);
                // 超管的菜单是单独且不同的，此处不做处理
                //roleMenuService.deleteByRidAndMenuId(RoleEnum.CIRCLE_SUPER.getValue(), circleMenu.getId());
                return Boolean.TRUE;
            });
            if (Boolean.TRUE.equals(execute)) {
                redisUtil.delete(redisKey);
            }
            return execute;
        }
        childList.forEach(e -> e.setIsDelte(true).setUpdateTime(DateUtil.date()));
        childList.add(circleMenu);
        Boolean execute = transactionTemplate.execute(e -> {
            updateBatchById(childList);
            // 超管的菜单是单独且不同的，此处不做处理
            //List<Integer> menuIdList = childList.stream().map(CircleMenu::getId).collect(Collectors.toList());
            //roleMenuService.deleteByRidAndMenuIdList(5, menuIdList);
            return Boolean.TRUE;
        });
        if (Boolean.TRUE.equals(execute)) {
            redisUtil.delete(redisKey);
        }
        return execute;
    }

    @Override
    public Boolean editCircleMenu(CircleMenuRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "系统菜单id不能为空");
        }
        if (request.getMenuType().equals(MenuConstants.TYPE_C) && StrUtil.isEmpty(request.getComponent())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "菜单类型的组件路径不能为空");
        }
        if (request.getMenuType().equals(MenuConstants.TYPE_A) && StrUtil.isEmpty(request.getPerms())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "按钮类型的权限表示不能为空");
        }
        CircleMenu oldMenu = getInfo(request.getId());
        if (ObjectUtil.isNull(oldMenu) || oldMenu.getIsDelte() || oldMenu.getType() != 6) {
            throw new CrmebException(SystemConfigResultCode.MENU_NOT_EXIST);
        }

        CircleMenu circleMenu = new CircleMenu();
        BeanUtils.copyProperties(request, circleMenu);
        circleMenu.setType(RoleEnum.CIRCLE_ADMIN.getValue());
        circleMenu.setUpdateTime(DateUtil.date());
        boolean update = updateById(circleMenu);
        if (update) {
            redisUtil.delete(RedisConstants.CIRCLE_MENU_CACHE_LIST_KEY);
        }
        return update;
    }

    @Override
    public CircleMenu getInfo(Integer id) {
        CircleMenu circleMenu = getById(id);
        if (ObjectUtil.isNull(circleMenu) || circleMenu.getIsDelte() || circleMenu.getType() != 6) {
            throw new CrmebException(SystemConfigResultCode.MENU_NOT_EXIST);
        }
        return circleMenu;
    }

    @Override
    public Boolean updateCircleShowStatus(Integer id) {
        CircleMenu circleMenu = getInfo(id);
        if (ObjectUtil.isNull(circleMenu) || circleMenu.getIsDelte() || circleMenu.getType() != 6) {
            throw new CrmebException(SystemConfigResultCode.MENU_NOT_EXIST);
        }
        circleMenu.setIsShow(!circleMenu.getIsShow());
        circleMenu.setUpdateTime(DateUtil.date());
        boolean update = updateById(circleMenu);
        if (update) {

             redisUtil.delete(RedisConstants.CIRCLE_MENU_CACHE_LIST_KEY);
        }
        return update;
    }

    @Override
    public List<CircleMenu> getMenuCacheList(Integer type) {
        String redisKey = "";
        if (type.equals(RoleEnum.CIRCLE_ADMIN.getValue()) || type.equals(RoleEnum.CIRCLE_SUPER.getValue())) {
            redisKey = RedisConstants.CIRCLE_MENU_CACHE_LIST_KEY;
        }
        if (redisUtil.exists(redisKey)) {
            return redisUtil.get(redisKey);
        }
        LambdaQueryWrapper<CircleMenu> lqw = Wrappers.lambdaQuery();
        lqw.eq(CircleMenu::getIsDelte, false);
        lqw.eq(CircleMenu::getType, type);
        List<CircleMenu> systemMenuList = dao.selectList(lqw);
        redisUtil.set(redisKey, systemMenuList);
        return systemMenuList;
    }

    /**
     * 获取圈层的所有菜单缓存树
     *
     * @return List<MenuCheckVo>
     */
    @Override
    public List<MenuCheckVo> getCircleMenuCacheTree() {
        List<CircleMenu> menuList = getCacheList(RoleEnum.CIRCLE_ADMIN.getValue());
        List<MenuCheckVo> voList = menuList.stream().map(e -> {
            MenuCheckVo menuCheckVo = new MenuCheckVo();
            BeanUtils.copyProperties(e, menuCheckVo);
            return menuCheckVo;
        }).collect(Collectors.toList());
        MenuCheckTree menuTree = new MenuCheckTree(voList);
        return menuTree.buildTree();
    }

    @Override
    public List<CircleMenu> findPermissionByUserId(Integer uid) {
        return dao.findPermissionByUserId(uid);
    }

    /**
     * 根据用户id获取角色菜单列表
     *
     * @param uid 用户id
     * @return List<CircleMenu>
     */
    @Override
    public List<CircleMenu> getMenusByUserId(Integer uid) {
        return dao.getMenusByUserId(uid);
    }

    /**
     * 获取圈层当前登录超管的菜单缓存树
     *
     * @return List<MenuCheckVo>
     */
    @Override
    public List<MenuCheckVo> getCircleMenuCacheList() {
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        Integer circleId = loginUserVo.getUser().getCircleId();
        CircleAdmin circleAdmin = circleAdminService.getByCircleId(circleId);
        List<CircleMenu> menuList = dao.findPermissionByUserId(circleAdmin.getId());
        List<MenuCheckVo> voList = menuList.stream().map(e -> {
            MenuCheckVo menuCheckVo = new MenuCheckVo();
            BeanUtils.copyProperties(e, menuCheckVo);
            return menuCheckVo;
        }).collect(Collectors.toList());
        MenuCheckTree menuTree = new MenuCheckTree(voList);
        return menuTree.buildTree();
    }

    @Override
    public List<CircleMenu> getList() {
        LambdaQueryWrapper<CircleMenu> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CircleMenu::getType, RoleEnum.CIRCLE_ADMIN.getValue());
        lqw.eq(CircleMenu::getIsDelte, false);
        return dao.selectList(lqw);
    }

    /**
     * 获取菜单缓存列表
     *
     * @param type 系统菜单类型：6-圈层
     */
    private List<CircleMenu> getCacheList(Integer type) {
        String redisKey = "";
        if (type.equals(RoleEnum.CIRCLE_ADMIN.getValue())) {
            redisKey = RedisConstants.CIRCLE_MENU_CACHE_LIST_KEY;
        }
        if (redisUtil.exists(redisKey)) {
            return redisUtil.get(redisKey);
        }
        LambdaQueryWrapper<CircleMenu> lqw = Wrappers.lambdaQuery();
        lqw.eq(CircleMenu::getIsDelte, false);
        lqw.eq(CircleMenu::getType, type);
        List<CircleMenu> systemMenuList = dao.selectList(lqw);
        redisUtil.set(redisKey, systemMenuList);
        return systemMenuList;
    }

    /**
     * 根据菜单id获取所有下级对象
     *
     * @param pid      菜单id
     * @param menuType 类型，M-目录，C-菜单
     * @return List<CircleMenu>
     */
    private List<CircleMenu> findAllChildListByPid(Integer pid, String menuType) {
        LambdaQueryWrapper<CircleMenu> lqw = Wrappers.lambdaQuery();
        lqw.eq(CircleMenu::getPid, pid);
        lqw.eq(CircleMenu::getIsDelte, false);
        if (menuType.equals(MenuConstants.TYPE_C)) {
            return dao.selectList(lqw);
        }
        List<CircleMenu> menuList = dao.selectList(lqw);
        if (CollUtil.isEmpty(menuList)) {
            return menuList;
        }
        List<Integer> pidList = menuList.stream().map(CircleMenu::getId).collect(Collectors.toList());
        lqw.clear();
        lqw.in(CircleMenu::getPid, pidList);
        lqw.eq(CircleMenu::getIsDelte, false);
        List<CircleMenu> childMenuList = dao.selectList(lqw);
        menuList.addAll(childMenuList);
        return menuList;
    }

}

