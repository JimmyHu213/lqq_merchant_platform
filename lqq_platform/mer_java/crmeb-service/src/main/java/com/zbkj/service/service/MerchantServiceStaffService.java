package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.reservation.MerchantServiceStaff;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.merchant.MerchantServiceStaffRequest;
import com.zbkj.common.request.merchant.MerchantServiceStaffSearchRequest;
import com.zbkj.common.response.MerchantServiceStaffResponse;

import java.util.List;

/**
 * @author HZW
 * @description MerchantServiceStaffService 接口
 * @date 2025-07-08
 */
public interface MerchantServiceStaffService extends IService<MerchantServiceStaff> {

    /**
     * 商家服务员工列表
     * @param request 请求参数
     * @author zzp
     * @return List<MerchantServiceStaff>
     */
    PageInfo<MerchantServiceStaffResponse> getList(MerchantServiceStaffSearchRequest request, PageParamRequest pageParamRequest);
    /**
     * 新增商家服务员工
     * @param request 当前新增的商家服务员工
     * @return 新增结果
     */
    Boolean addMerchantServiceStaff(MerchantServiceStaffRequest request);

    /**
     * 删除商家服务员工
     * @param id 删除员工ID
     * @return 删除结果
     */
    Boolean deleteById(Integer id);

    /**
     * 修改商家服务员工
     * @param request 当前修改的商家服务员工
     * @return 修改结果
     */
    Boolean editMerchantServiceStaff(MerchantServiceStaffRequest request);

    /**
     * 查询员工信息
     * @param id 主键ID
     * @return 员工封装对象
     */
    MerchantServiceStaffResponse getStaffInfo(Integer id);

    /**
     * 修改 员工状态
     * @param id 主键ID
     * @return 修改结果
     */
    Boolean updateStaffStatus(Integer id);

    /**
     * 获取服务员工-通过用户ID
     * @param userId 用户ID
     * @return 服务员工信息
     */
    MerchantServiceStaff getByUserId(Integer userId);

    List<MerchantServiceStaffResponse> getStaffList(MerchantServiceStaffSearchRequest request, SystemAdmin systemAdmin);

    List<MerchantServiceStaff> findByMerId(Integer merId);

    List<MerchantServiceStaff> findByStaffIdList(List<Integer> staffIdList);
}