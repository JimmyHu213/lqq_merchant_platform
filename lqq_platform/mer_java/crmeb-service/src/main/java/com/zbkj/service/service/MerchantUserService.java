package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.merchant.MerchantUser;
import com.zbkj.common.model.user.UserShoppingCreditsRecord;
import com.zbkj.common.request.DateRequest;
import com.zbkj.common.request.merchant.MerchantUserSearchRequest;
import com.zbkj.common.response.MerchantUserDetailResponse;
import com.zbkj.common.response.MerchantUserPageResponse;

import java.util.List;
import java.util.Map;

/**
 * @author HZW
 * @description MerchantUserService 接口
 * @date 2025-12-24
 */
public interface MerchantUserService extends IService<MerchantUser> {

    /**
     * 商户用户分页列表
     */
    PageInfo<MerchantUserPageResponse> findUserPage(MerchantUserSearchRequest request);

    /**
     * 添加商户用户(单)
     *
     * @param userId 用户ID
     * @param merId  商户ID
     */
    void addMerchantUser(Integer userId, Integer merId);

    /**
     * 添加商户用户（多）
     *
     * @param userId    用户ID
     * @param merIdList 商户ID列表
     */
    void addManyMerchantUser(Integer userId, List<Integer> merIdList);

    /**
     * 商户用户详情
     *
     * @param userId 用户id
     */
    MerchantUserDetailResponse getMerchantUserDetail(Integer userId);

    /**
     * 商户用户购物金记录
     */
    List<UserShoppingCreditsRecord> findMerchantUserShoppingCreditsRecord(Integer userId);

    /**
     * 商户用户购物金冻结/解冻
     */
    Boolean userShoppingCreditsFreezeUpdate(Integer userId);

    /**
     * 商户用户数量
     *
     * @param merId 商户ID
     */
    Integer merchantUserNum(Integer merId);

    /**
     * 获取商户新增用户数
     *
     * @param merId 商户ID
     * @param dateRequest 日期请求对象
     */
    Integer getAddUserNum(Integer merId, DateRequest dateRequest);


    Integer getAliveUserNum(Integer merId, DateRequest dateRequest);

    Integer getOrderUserNum(Integer merId, DateRequest dateRequest);

    Integer getUserAddNum(Integer merId, String date, DateRequest dateRequest);

    Integer getUserAliveNum(Integer merId, String date, DateRequest dateRequest);

    Map<String, Integer> getOrderMerchantUserNum(Integer merId, String date, DateRequest dateRequest);
}