package com.zbkj.admin.service;

import com.zbkj.admin.request.MerchantMemberLevelSaveRequest;
import com.zbkj.admin.response.MerchantMemberLevelSaveResponse;

import java.util.List;

/**
 * 类的详细说明
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/3
 */
public interface ManageMerchantMemberLevelService {

    /**
     * 会员等级列表
     */
    List<MerchantMemberLevelSaveResponse> getList();

    /**
     * 会员等级新增
     */
    Boolean add(MerchantMemberLevelSaveRequest request);

    /**
     * 会员等级编辑
     */
    Boolean update(MerchantMemberLevelSaveRequest request);

    /**
     * 会员等级删除
     * @param id 商户会员等级id
     */
    Boolean delete(Integer id);
}
