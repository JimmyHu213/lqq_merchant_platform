package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.enums.RoleEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.circle.CircleAdmin;
import com.zbkj.common.model.circle.CircleMenu;
import com.zbkj.common.model.circle.CircleRole;
import com.zbkj.common.model.circle.CircleRoleMenu;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemRoleRequest;
import com.zbkj.common.request.SystemRoleSearchRequest;
import com.zbkj.common.request.SystemRoleStatusRequest;
import com.zbkj.common.response.RoleInfoResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.SystemConfigResultCode;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.MenuCheckTree;
import com.zbkj.common.vo.MenuCheckVo;
import com.zbkj.service.dao.circle.CircleRoleDao;
import com.zbkj.service.service.CircleAdminService;
import com.zbkj.service.service.CircleMenuService;
import com.zbkj.service.service.CircleRoleMenuService;
import com.zbkj.service.service.CircleRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* @author zzp
* @description CircleRoleServiceImpl 接口实现
* @date 2025-09-16
*/
@Service
public class CircleRoleServiceImpl extends ServiceImpl<CircleRoleDao, CircleRole> implements CircleRoleService {

    @Resource
    private CircleRoleDao dao;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private CircleRoleMenuService circleRoleMenuService;
    @Autowired
    private CircleAdminService circleAdminService;
    @Autowired
    private CircleMenuService circleMenuService;

    /**
     * 平台端端-获取圈层超管角色列表
     *
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return 圈层角色列表
     */
    @Override
    public List<CircleRole> getPlaCircleRoleList(SystemRoleSearchRequest request, PageParamRequest pageParamRequest) {
        return getList(request, pageParamRequest, RoleEnum.CIRCLE_SUPER.getValue());
    }

    private List<CircleRole> getList(SystemRoleSearchRequest request, PageParamRequest pageParamRequest, Integer type) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<CircleRole> lqw = Wrappers.lambdaQuery();
        lqw.select(CircleRole::getId, CircleRole::getRoleName, CircleRole::getStatus,
                CircleRole::getCreateTime, CircleRole::getUpdateTime);
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lqw.eq(CircleRole::getStatus, request.getStatus());
        }
        if (StrUtil.isNotBlank(request.getRoleName())) {
            String decode = URLUtil.decode(request.getRoleName());
            lqw.like(CircleRole::getRoleName, decode);
        }
        lqw.eq(CircleRole::getType, type);
        if (type.equals(RoleEnum.CIRCLE_ADMIN.getValue())) {
            SystemAdmin currentAdmin = SecurityUtil.getLoginUserVo().getUser();
            lqw.eq(CircleRole::getCircleId, currentAdmin.getCircleId());
        }
        lqw.orderByAsc(CircleRole::getId);
        return dao.selectList(lqw);
    }

    @Override
    public Boolean addPlaCircleRole(SystemRoleRequest request) {
        // 名称校验
        if (existName(request.getRoleName(), 0, RoleEnum.CIRCLE_SUPER.getValue())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "角色名称重复");
        }
        return addRole(request, RoleEnum.CIRCLE_SUPER.getValue());
    }

    private Boolean addRole(SystemRoleRequest request, Integer type) {
        CircleRole circleRole = new CircleRole();
        BeanUtils.copyProperties(request, circleRole);
        List<Integer> ruleList = Stream.of(request.getRules().split(",")).map(Integer::valueOf).distinct().collect(Collectors.toList());
        circleRole.setId(null);
        circleRole.setRules("");
        circleRole.setType(type);
        if (type.equals(RoleEnum.CIRCLE_ADMIN.getValue())) {
            SystemAdmin currentAdmin = SecurityUtil.getLoginUserVo().getUser();
            circleRole.setCircleId(currentAdmin.getCircleId());
        }
        return transactionTemplate.execute(e -> {
            boolean save = save(circleRole);
            if (!save) {
                return Boolean.FALSE;
            }
            List<CircleRoleMenu> roleMenuList = ruleList.stream().map(rule -> {
                CircleRoleMenu roleMenu = new CircleRoleMenu();
                roleMenu.setRid(circleRole.getId());
                roleMenu.setMenuId(rule);
                return roleMenu;
            }).collect(Collectors.toList());
            circleRoleMenuService.saveBatch(roleMenuList, 100);
            return Boolean.TRUE;
        });
    }

    /**
     * 判断角色名称是否存在
     * @param roleName 角色名称
     * @param id 角色ID
     * @param type 角色类型
     * @return Boolean 是否存在
     */
    private Boolean existName(String roleName, Integer id, Integer type) {
        LambdaQueryWrapper<CircleRole> lqw = Wrappers.lambdaQuery();
        lqw.eq(CircleRole::getRoleName, roleName);
        lqw.ne(id > 0, CircleRole::getId, id);
        lqw.eq(CircleRole::getType, type);
        return dao.selectCount(lqw) > 0;
    }

    @Override
    public Boolean deletePlaCircleRole(Integer id) {
        return deleteRole(id, RoleEnum.CIRCLE_SUPER.getValue());
    }

    private Boolean deleteRole(Integer id, Integer type) {

        CircleRole circleRole = getById(id);
        if (ObjectUtil.isNull(circleRole)) {
            throw new CrmebException(SystemConfigResultCode.ROLE_DELETE);
        }
        if (type.equals(RoleEnum.CIRCLE_ADMIN.getValue()) && circleRole.getType().equals(RoleEnum.CIRCLE_SUPER.getValue())) {
            throw new CrmebException("普通角色不允许删除超管角色");
        }
        if (circleAdminService.isExistRole(id)) {
            throw new CrmebException(SystemConfigResultCode.ROLE_USED);
        }
        return transactionTemplate.execute(e -> {
            dao.deleteById(id);
            circleRoleMenuService.deleteByRid(id);
            return Boolean.TRUE;
        });
    }

    @Override
    public Boolean editPlaCircleRole(SystemRoleRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "角色ID不能为空");
        }
        if (existName(request.getRoleName(), request.getId(), RoleEnum.CIRCLE_SUPER.getValue())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "角色名称重复");
        }
        return editRole(request, RoleEnum.CIRCLE_SUPER.getValue());


    }

    private Boolean editRole(SystemRoleRequest request, Integer type) {
        CircleRole role = getById(request.getId());
        if (ObjectUtil.isNull(role)) {
            throw new CrmebException(SystemConfigResultCode.ROLE_NOT_EXIST);
        }
        List<Integer> ruleList = Stream.of(request.getRules().split(",")).map(Integer::valueOf).distinct().collect(Collectors.toList());
        List<CircleRoleMenu> roleMenuList = ruleList.stream().map(rule -> {
            CircleRoleMenu roleMenu = new CircleRoleMenu();
            roleMenu.setRid(request.getId());
            roleMenu.setMenuId(rule);
            return roleMenu;
        }).collect(Collectors.toList());

        CircleRole circleRole = new CircleRole();
        BeanUtils.copyProperties(request, circleRole);
        circleRole.setRules("");
        circleRole.setType(type);
        if (type.equals(RoleEnum.CIRCLE_ADMIN.getValue())) {
            SystemAdmin currentAdmin = SecurityUtil.getLoginUserVo().getUser();
            circleRole.setCircleId(currentAdmin.getCircleId());
        }
        circleRole.setUpdateTime(DateUtil.date());
        return transactionTemplate.execute(e -> {
            updateById(circleRole);
            circleRoleMenuService.deleteByRid(circleRole.getId());
            circleRoleMenuService.saveBatch(roleMenuList, 100);
            return Boolean.TRUE;
        });

    }

    @Override
    public RoleInfoResponse getInfo(Integer id) {
        CircleRole circleRole = getById(id);
        if (ObjectUtil.isNull(circleRole)) {
            throw new CrmebException(SystemConfigResultCode.ROLE_NOT_EXIST);
        }
        // 查询角色对应的菜单(权限)
        List<Integer> menuIdList = circleRoleMenuService.getMenuListByRid(id);
        List<CircleMenu> menuList ;

        Integer circleId = circleRole.getCircleId();
        if (circleId == 0) {
            menuList = circleMenuService.getList();

        } else {
            CircleAdmin circleAdmin = circleAdminService.getByCircleId(circleId);
            menuList = circleMenuService.findPermissionByUserId(circleAdmin.getId());
        }

        List<MenuCheckVo> menuCheckVoList = menuList.stream().map(menu -> {
            MenuCheckVo menuCheckVo = new MenuCheckVo();
            BeanUtils.copyProperties(menu, menuCheckVo);
            menuCheckVo.setChecked(menuIdList.contains(menu.getId()));
            return menuCheckVo;
        }).collect(Collectors.toList());

        RoleInfoResponse response = new RoleInfoResponse();
        BeanUtils.copyProperties(circleRole, response);
        response.setMenuList(new MenuCheckTree(menuCheckVoList).buildTree());
        return response;
    }

    @Override
    public Boolean updateStatus(SystemRoleStatusRequest request) {
        CircleRole role = getById(request.getId());
        if (ObjectUtil.isNull(role)) {
            throw new CrmebException(SystemConfigResultCode.ROLE_NOT_EXIST);
        }
        if (role.getStatus().equals(request.getStatus())) {
            return true;
        }
        role.setStatus(request.getStatus());
        role.setUpdateTime(DateUtil.date());
        return updateById(role);
    }

    /**
     * 根据圈层ID获取圈层角色列表
     *
     * @param circleId  圈层ID
     * @return 圈层角色列表
     */
    @Override
    public List<CircleRole> getListByCircleId(Integer circleId) {
        LambdaQueryWrapper<CircleRole> lqw = Wrappers.lambdaQuery();
        lqw.eq(CircleRole::getCircleId, circleId);
        lqw.ne(CircleRole::getType, RoleEnum.CIRCLE_SUPER.getValue());
        return dao.selectList(lqw);
    }

    /**
     * 圈层端-获取当前登录圈层用户的角色列表
     *
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return 圈层角色列表
     */
    @Override
    public List<CircleRole> getCircleRoleList(SystemRoleSearchRequest request, PageParamRequest pageParamRequest) {
        return getList(request, pageParamRequest, RoleEnum.CIRCLE_ADMIN.getValue());
    }

    @Override
    public Boolean addCircleRole(SystemRoleRequest request) {
        return addRole(request, RoleEnum.CIRCLE_ADMIN.getValue());
    }

    @Override
    public Boolean deleteCircleRole(Integer id) {
        return deleteRole(id, RoleEnum.CIRCLE_ADMIN.getValue());
    }

    @Override
    public Boolean editCircleRole(SystemRoleRequest request) {
        return editRole(request, RoleEnum.CIRCLE_ADMIN.getValue());
    }
}

