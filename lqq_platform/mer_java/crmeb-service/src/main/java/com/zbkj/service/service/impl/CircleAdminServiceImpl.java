package com.zbkj.service.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.enums.RoleEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.circle.CircleAdmin;
import com.zbkj.common.model.circle.CircleRole;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.SystemAdminResponse;
import com.zbkj.common.result.AdminResultCode;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.circle.CircleAdminDao;
import com.zbkj.service.service.CircleAdminService;
import com.zbkj.service.service.CircleRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author zzp
* @description CircleAdminServiceImpl 接口实现
* @date 2025-09-16
*/
@Service
public class CircleAdminServiceImpl extends ServiceImpl<CircleAdminDao, CircleAdmin> implements CircleAdminService {

    @Resource
    private CircleAdminDao dao;
    @Resource
    private CrmebConfig crmebConfig;
    @Autowired
    private CircleRoleService circleRoleService;


    @Override
    public Boolean deleteByCircleId(Integer circleId) {
        LambdaUpdateWrapper<CircleAdmin> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CircleAdmin::getCircleId, circleId);
        updateWrapper.set(CircleAdmin::getIsDel, Boolean.TRUE);
        return update(updateWrapper);
    }

    @Override
    public Integer checkAdminAccount(String account) {
        LambdaQueryWrapper<CircleAdmin> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CircleAdmin::getAccount, account);
        lqw.eq(CircleAdmin::getIsDel, Boolean.TRUE);
        return dao.selectCount(lqw);
    }

    /**
     * 更新圈层管理员信息：姓名和电话
     *
     * @param realName 姓名
     * @param phone 电话号码
     * @param circleId 圈层ID
     * @return 更新结果
     */
    @Override
    public Boolean updateCircleAdminData(String realName, String phone, Integer circleId, String role) {
        LambdaUpdateWrapper<CircleAdmin> luw = new LambdaUpdateWrapper<>();
        luw.eq(CircleAdmin::getCircleId, circleId);
        luw.eq(CircleAdmin::getType, RoleEnum.CIRCLE_SUPER.getValue());
        luw.eq(CircleAdmin::getIsDel, Boolean.FALSE);
        luw.set(CircleAdmin::getRealName, realName);
        luw.set(CircleAdmin::getPhone, phone);
        luw.set(CircleAdmin::getRoles, role);
        return update(luw);
    }

    /**
     * 根据圈层ID获取超管信息
     *
     * @param circleId 圈层ID
     * @return 超管信息
     */
    @Override
    public CircleAdmin getByCircleId(Integer circleId) {
        LambdaQueryWrapper<CircleAdmin> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CircleAdmin::getCircleId, circleId);
        lqw.eq(CircleAdmin::getIsDel, Boolean.FALSE);
        lqw.eq(CircleAdmin::getType, RoleEnum.CIRCLE_SUPER.getValue());
        return dao.selectOne(lqw);
    }

    @Override
    public Boolean isExistRole(Integer roleId) {
        LambdaQueryWrapper<CircleAdmin> lqw = Wrappers.lambdaQuery();
        lqw.select(CircleAdmin::getId);
        lqw.apply(StrUtil.format(" find_in_set('{}', roles)", roleId));
        lqw.last(" limit 1");
        CircleAdmin circleAdmin = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(circleAdmin);
    }

    @Override
    public CircleAdmin selectUserByAccountAndType(String account, Integer type) {
        List<Integer> types = new ArrayList<>();
        if (type.equals(RoleEnum.CIRCLE_ADMIN.getValue()) || type.equals(RoleEnum.CIRCLE_SUPER.getValue())) {
            types.add(RoleEnum.CIRCLE_SUPER.getValue());
            types.add(RoleEnum.CIRCLE_ADMIN.getValue());
        }
        LambdaQueryWrapper<CircleAdmin> lqw = Wrappers.lambdaQuery();
        lqw.eq(CircleAdmin::getAccount, account);
        lqw.in(CircleAdmin::getType, types);
        lqw.eq(CircleAdmin::getIsDel, false);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 圈层管理员列表
     *
     * @param request request
     * @param pageParamRequest 分页参数
     * @return 圈层管理员列表
     */
    @Override
    public PageInfo<SystemAdminResponse> getCircleAdminList(SystemAdminRequest request, PageParamRequest pageParamRequest) {
        Page<CircleAdmin> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        //带SystemAdminRequest类的多条件查询
        LambdaQueryWrapper<CircleAdmin> lqw = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lqw.eq(CircleAdmin::getStatus, request.getStatus());
        }
        if (StrUtil.isNotBlank(request.getRealName())) {
            String decode = URLUtil.decode(request.getRealName());
            lqw.and(i -> i.like(CircleAdmin::getRealName, decode)
                    .or().like(CircleAdmin::getAccount, decode));
        }
        SystemAdmin currentAdmin = SecurityUtil.getLoginUserVo().getUser();
        lqw.eq(CircleAdmin::getCircleId, currentAdmin.getCircleId());
        lqw.eq(CircleAdmin::getIsDel, Boolean.FALSE);

        List<Integer> types = new ArrayList<>();
        types.add(RoleEnum.CIRCLE_ADMIN.getValue());
        types.add(RoleEnum.CIRCLE_SUPER.getValue());
        lqw.in(CircleAdmin::getType, types);

        if (ObjectUtil.isNotNull(request.getRoles()) && request.getRoles() > 0) {
            lqw.apply(" find_in_set({0}, roles)", request.getRoles());
        }

        List<CircleAdmin> circleAdmins = dao.selectList(lqw);

        if (CollUtil.isEmpty(circleAdmins)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        List<SystemAdminResponse> adminResponseList = new ArrayList<>();

        List<CircleRole> roleList = circleRoleService.getListByCircleId(currentAdmin.getCircleId());
        for (CircleAdmin admin : circleAdmins) {
            SystemAdminResponse sar = new SystemAdminResponse();
            BeanUtils.copyProperties(admin, sar);
            sar.setLastTime(admin.getUpdateTime());
            List<Integer> roleIds = CrmebUtil.stringToArrayInt(admin.getRoles());
            List<String> roleNames = new ArrayList<>();
            for (Integer roleId : roleIds) {
                if (5 == roleId) {
                    roleNames.add("圈层超管");
                    continue;
                }
                for (CircleRole role : roleList) {
                    if (role.getId().equals(roleId)) {
                        roleNames.add(role.getRoleName());
                    }
                }
            }
            sar.setRoleNames(StringUtils.join(roleNames, ","));
            if (crmebConfig.getPhoneMaskSwitch() && StrUtil.isNotBlank(sar.getPhone())) {
                sar.setPhone(CrmebUtil.maskMobile(sar.getPhone()));
            }
            adminResponseList.add(sar);
        }
        return CommonPage.copyPageInfo(page, adminResponseList);
    }

    /**
     * 新增圈层管理员
     *
     * @param request 新增管理员信息
     * @return Boolean
     */
    @Override
    public Boolean saveCircleAdmin(SystemAdminAddRequest request) {
        SystemAdmin currentUser = SecurityUtil.getLoginUserVo().getUser();

        // 管理员名称唯一校验
        if (checkAdminAccount(request.getAccount()) > 0) {
            throw new CrmebException(AdminResultCode.ADMIN_EXIST);
        }
        CircleAdmin circleAdmin = new CircleAdmin();
        BeanUtils.copyProperties(request, circleAdmin);

        String pwd = CrmebUtil.encryptPassword(circleAdmin.getPwd(), circleAdmin.getAccount());
        circleAdmin.setPwd(pwd);
        circleAdmin.setCircleId(currentUser.getCircleId());
        circleAdmin.setType(RoleEnum.CIRCLE_ADMIN.getValue());
        return save(circleAdmin);
    }


    /**
     * 删除圈层管理员
     *
     * @param id 管理员id
     * @return Boolean
     */
    @Override
    public Boolean removeCircleAdmin(Integer id) {
        CircleAdmin perDelAdmin = getDetail(id);
        if (perDelAdmin.getType().equals(RoleEnum.CIRCLE_SUPER.getValue())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "超级管理员此处不允许删除");
        }
        LambdaUpdateWrapper<CircleAdmin> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CircleAdmin::getId, id);
        wrapper.set(CircleAdmin::getIsDel, true);
        return update(wrapper);
    }

    /**
     * 更新圈层管理员信息
     *
     * @param request 更新信息
     * @return Boolean
     */
    @Override
    public Boolean updateCircleAdmin(SystemAdminUpdateRequest request) {

        SystemAdmin currentUser = SecurityUtil.getLoginUserVo().getUser();

        CircleAdmin adminDetail = getDetail(request.getId());
        // 超级管理员不允许编辑
        if (adminDetail.getType().equals(RoleEnum.CIRCLE_SUPER.getValue())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "超级管理员此处不允许编辑");
        }

        verifyAccount(request.getId(), request.getAccount());
        CircleAdmin circleAdmin = new CircleAdmin();
        circleAdmin.setId(request.getId());
        circleAdmin.setAccount(request.getAccount());
        circleAdmin.setRealName(request.getRealName());
        circleAdmin.setRoles(request.getRoles());
        circleAdmin.setStatus(request.getStatus());
        if (StrUtil.isNotBlank(request.getPhone())) {
            circleAdmin.setPhone(request.getPhone());
        }
        circleAdmin.setCircleId(currentUser.getCircleId());
        circleAdmin.setUpdateTime(DateUtil.date());
        return updateById(circleAdmin);
    }

    /**
     * 校验账号唯一性（管理员更新时）
     *
     * @param id 管理员id
     * @param account 管理员账号
     */
    private void verifyAccount(Integer id, String account) {
        LambdaQueryWrapper<CircleAdmin> lqw = Wrappers.lambdaQuery();
        lqw.ne(CircleAdmin::getId, id);
        lqw.eq(CircleAdmin::getAccount, account);
        CircleAdmin circleAdmin = dao.selectOne(lqw);
        if (ObjectUtil.isNotNull(circleAdmin)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "账号已存在");
        }
    }

    /**
     * 根据管理员id获取详情
     *
     * @param id 管理员id
     * @return CircleAdmin
     */
    @Override
    public CircleAdmin getDetail(Integer id) {
        CircleAdmin circleAdmin = getById(id);
        if (ObjectUtil.isNull(circleAdmin) || circleAdmin.getIsDel()) {
            throw new CrmebException(AdminResultCode.ADMIN_NOT_EXIST);
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();

        if (!admin.getCircleId().equals(circleAdmin.getCircleId())) {
            throw new CrmebException(AdminResultCode.ADMIN_NOT_EXIST);
        }
        return circleAdmin;
    }

    /**
     * 修改圈层管理员状态
     *
     * @param id 管理员id
     * @param status 状态
     * @return Boolean
     */
    @Override
    public Boolean updateCircleAdminStatus(Integer id, Boolean status) {
        CircleAdmin adminDetail = getDetail(id);
        if (adminDetail.getType().equals(RoleEnum.CIRCLE_SUPER.getValue())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "超级管理员此处不允许编辑");
        }
        if (adminDetail.getStatus().equals(status)) {
            return true;
        }
        adminDetail.setStatus(status);
        adminDetail.setUpdateTime(DateUtil.date());
        return updateById(adminDetail);
    }

    /**
     * 修改圈层管理员密码
     *
     * @param request request
     * @return Boolean
     */
    @Override
    public Boolean updateCircleAdminPassword(SystemAdminUpdatePwdRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "两次密码输入不一致");
        }

        CircleAdmin adminDetail = getDetail(request.getId());
        if (adminDetail.getType().equals(RoleEnum.CIRCLE_SUPER.getValue())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "超级管理员此处不允许编辑");
        }
        LambdaUpdateWrapper<CircleAdmin> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CircleAdmin::getId, request.getId());
        wrapper.set(CircleAdmin::getPwd, CrmebUtil.encryptPassword(request.getPassword(), adminDetail.getAccount()));
        return update(wrapper);
    }

}

