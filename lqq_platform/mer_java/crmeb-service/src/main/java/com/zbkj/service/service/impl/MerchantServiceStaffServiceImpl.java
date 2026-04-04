package com.zbkj.service.service.impl;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.reservation.MerchantServiceStaff;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.merchant.MerchantServiceStaffRequest;
import com.zbkj.common.request.merchant.MerchantServiceStaffSearchRequest;
import com.zbkj.common.response.MerchantServiceStaffResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.token.FrontTokenComponent;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.reservation.MerchantServiceStaffDao;
import com.zbkj.service.service.MerchantServiceStaffService;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author HZW
 * @description MerchantServiceStaffServiceImpl 接口实现
 * @date 2025-07-08
 */
@Service
public class MerchantServiceStaffServiceImpl extends ServiceImpl<MerchantServiceStaffDao, MerchantServiceStaff> implements MerchantServiceStaffService {

    @Resource
    private MerchantServiceStaffDao dao;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private FrontTokenComponent tokenComponent;
    @Autowired
    private UserService userService;
    @Autowired
    private CrmebConfig crmebConfig;

    /**
     * 列表
     * @param request 请求参数
     * @author zzp
     * @return List<MerchantServiceStaff>
     */
    @Override
    public PageInfo<MerchantServiceStaffResponse> getList(MerchantServiceStaffSearchRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        LambdaQueryWrapper<MerchantServiceStaff> wrapper = Wrappers.lambdaQuery();

        if (ObjectUtil.isNotEmpty(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            wrapper.nested(i -> i.like(MerchantServiceStaff::getPhone, keywords)
                    .or()
                    .like(MerchantServiceStaff::getName, keywords)
            );
        }
        if (ObjectUtil.isNotEmpty(request.getStatus())) {
            wrapper.eq(MerchantServiceStaff::getStatus, request.getStatus());
        }
        wrapper.eq(MerchantServiceStaff::getIsDelete, false);
        wrapper.eq(MerchantServiceStaff::getMerId, systemAdmin.getMerId())
                .orderByDesc(MerchantServiceStaff::getSort)
                .orderByDesc(MerchantServiceStaff::getCreateTime);

        Page<MerchantServiceStaff> serviceStaffPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<MerchantServiceStaff> staffList = dao.selectList(wrapper);

        //实体类转换
        List<MerchantServiceStaffResponse> responseList = staffList.stream()
                .map(item -> {
                    MerchantServiceStaffResponse response = new MerchantServiceStaffResponse();
                    BeanUtils.copyProperties(item, response);
                    User user = userService.getById(item.getUserId());
                    response.setNickName(user.getNickname());
                    response.setAvatar(user.getAvatar());
                    if (crmebConfig.getPhoneMaskSwitch()) {
                        response.setPhone(CrmebUtil.maskMobile(response.getPhone()));
                    }
                    return response;
                }).collect(Collectors.toList());

        return CommonPage.copyPageInfo(serviceStaffPage, responseList);
    }

    /**
     * 新增商家服务员工
     * @param request 当前新增的商家服务员工
     * @return 新增结果
     */
    @Override
    public Boolean addMerchantServiceStaff(MerchantServiceStaffRequest request) {
        // 关联当前商户id
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        // 检查当前新增服务员工是否已经关联某个店铺
        checkEmployeeExistCurrentMerchant(request);

        MerchantServiceStaff merchantServiceStaff = new MerchantServiceStaff();
        BeanUtils.copyProperties(request, merchantServiceStaff);
        // 清理素材前缀
        merchantServiceStaff.setIdPhoto(systemAttachmentService.clearPrefix(merchantServiceStaff.getIdPhoto()));
        merchantServiceStaff.setMerId(systemAdmin.getMerId());
        return save(merchantServiceStaff);
    }

    /**
     * 删除商家服务员工
     * @param id 删除员工ID
     * @return 删除结果
     */
    @Override
    public Boolean deleteById(Integer id) {
        // 校验员工数据是否存在，并且是否属于当前商户下员工
        MerchantServiceStaff serviceStaff = checkMerchantServiceStaff(id);

        MerchantServiceStaff updateEntity = new MerchantServiceStaff();
        updateEntity.setId(id);
        updateEntity.setIsDelete(true); // 标记删除状态
        updateEntity.setUpdateTime(DateUtil.date());
        boolean success = updateById(updateEntity);
        if (success) {
            // 清除用户商家管理token
            tokenComponent.cleanMerchantUserToken(serviceStaff.getUserId());
        }
        return success;
    }

    /**
     * 修改商家服务员工
     * @param request 当前修改的商家服务员工
     * @return 修改结果
     */
    @Override
    public Boolean editMerchantServiceStaff(MerchantServiceStaffRequest request) {
        // 校验员工数据是否存在，并且是否属于当前商户下员工
        checkMerchantServiceStaff(request.getId());

        MerchantServiceStaff merchantServiceStaff = new MerchantServiceStaff();
        BeanUtils.copyProperties(request, merchantServiceStaff);
        merchantServiceStaff.setIdPhoto(systemAttachmentService.clearPrefix(merchantServiceStaff.getIdPhoto()));
        merchantServiceStaff.setUpdateTime(DateUtil.date());
        boolean update = updateById(merchantServiceStaff);
        if (update && merchantServiceStaff.getStatus().equals(1) && !request.getStatus().equals(0)) {
            // 清除用户商家管理token
            tokenComponent.cleanMerchantUserToken(merchantServiceStaff.getUserId());
        }
        return update;
    }

    /**
     * 查询员工信息
     * @param id 主键ID
     * @return 员工封装对象
     */
    @Override
    public MerchantServiceStaffResponse getStaffInfo(Integer id) {
        MerchantServiceStaff serviceStaff = getById(id);
        if (ObjectUtil.isNull(serviceStaff) || serviceStaff.getIsDelete()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "数据不存在");
        }
        MerchantServiceStaffResponse serviceStaffResponse = new MerchantServiceStaffResponse();
        BeanUtils.copyProperties(serviceStaff, serviceStaffResponse);
        User user = userService.getById(serviceStaff.getUserId());
        if (ObjectUtil.isNotNull(user)) {
            serviceStaffResponse.setNickName(user.getNickname());
            serviceStaffResponse.setAvatar(user.getAvatar());
        }
        return serviceStaffResponse;
    }

    /**
     * 修改 员工状态
     * @param id 主键ID
     * @return 修改结果
     */
    @Override
    public Boolean updateStaffStatus(Integer id) {
        // 校验员工数据是否存在，并且是否属于当前商户下员工
        MerchantServiceStaff serviceStaff = checkMerchantServiceStaff(id);

        serviceStaff.setStatus(serviceStaff.getStatus() == 1 ? 0 : 1);
        serviceStaff.setUpdateTime(DateUtil.date());
        return updateById(serviceStaff);
    }

    /**
     * 检测当前操作的员工是否存在或者属于当前商户下员工
     * @param id 服务员工主键id
     */
    private MerchantServiceStaff checkMerchantServiceStaff(Integer id) {
        MerchantServiceStaff serviceStaff = getById(id);
        if (ObjectUtil.isNull(serviceStaff) || serviceStaff.getIsDelete()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "数据不存在");
        }
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (!Objects.equals(serviceStaff.getMerId(), systemAdmin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "禁止修改其他商户的员工");
        }
        return serviceStaff;
    }

    /**
     * 检测当前添加的用户是否已经是某店铺的员工
     * @param request 当前添加的员工
     */
    private void checkEmployeeExistCurrentMerchant(MerchantServiceStaffRequest request) {
        LambdaQueryWrapper<MerchantServiceStaff> queryWrapperExit = Wrappers.lambdaQuery();
        queryWrapperExit.eq(MerchantServiceStaff::getUserId, request.getUserId());
        queryWrapperExit.eq(MerchantServiceStaff::getIsDelete, false);
        Integer count = dao.selectCount(queryWrapperExit);
        if (count > 0) {
            throw new CrmebException("当前用户已经是某个店铺的服务员工");
        }
    }

    /**
     * 获取服务员工-通过用户ID
     * @param userId 用户ID
     * @return 服务员工信息
     */
    @Override
    public MerchantServiceStaff getByUserId(Integer userId) {
        LambdaQueryWrapper<MerchantServiceStaff> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantServiceStaff::getUserId, userId);
        lqw.eq(MerchantServiceStaff::getIsDelete, false);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    @Override
    public List<MerchantServiceStaff> findByMerId(Integer merId) {
        LambdaQueryWrapper<MerchantServiceStaff> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantServiceStaff::getMerId, merId);
        lqw.eq(MerchantServiceStaff::getIsDelete, false);
        return dao.selectList(lqw);
    }

    @Override
    public List<MerchantServiceStaff> findByStaffIdList(List<Integer> staffIdList) {
        LambdaQueryWrapper<MerchantServiceStaff> lqw = Wrappers.lambdaQuery();
        lqw.in(MerchantServiceStaff::getId, staffIdList);
        return dao.selectList(lqw);
    }

    @Override
    public List<MerchantServiceStaffResponse> getStaffList(MerchantServiceStaffSearchRequest request, SystemAdmin systemAdmin) {
        LambdaQueryWrapper<MerchantServiceStaff> wrapper = Wrappers.lambdaQuery();

        if (ObjectUtil.isNotEmpty(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            wrapper.nested(i -> i.like(MerchantServiceStaff::getPhone, keywords)
                    .or()
                    .like(MerchantServiceStaff::getName, keywords)
            );
        }
        if (ObjectUtil.isNotEmpty(request.getStatus())) {
            wrapper.eq(MerchantServiceStaff::getStatus, request.getStatus());
        }
        wrapper.eq(MerchantServiceStaff::getIsDelete, false);
        wrapper.eq(MerchantServiceStaff::getMerId, systemAdmin.getMerId())
                .orderByDesc(MerchantServiceStaff::getSort)
                .orderByDesc(MerchantServiceStaff::getCreateTime);

        List<MerchantServiceStaff> staffList = dao.selectList(wrapper);

        //实体类转换
        return staffList.stream()
                .map(item -> {
                    MerchantServiceStaffResponse response = new MerchantServiceStaffResponse();
                    BeanUtils.copyProperties(item, response);
                    User user = userService.getById(item.getUserId());
                    response.setNickName(user.getNickname());
                    response.setAvatar(user.getAvatar());
                    return response;
                }).collect(Collectors.toList());

    }
}

