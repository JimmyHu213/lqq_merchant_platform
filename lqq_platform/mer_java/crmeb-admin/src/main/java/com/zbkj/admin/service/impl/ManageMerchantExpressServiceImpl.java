package com.zbkj.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.zbkj.admin.service.ManageMerchantExpressService;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.express.MerchantExpress;
import com.zbkj.common.request.MerchantExpressSearchRequest;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.MerchantExpressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author HZW
 * @description ManageMerchantExpressServiceImpl 接口实现
 * @date 2024-05-08
 */
@Service
public class ManageMerchantExpressServiceImpl implements ManageMerchantExpressService {

    @Autowired
    private MerchantExpressService merchantExpressService;

    /**
     * 关联物流公司
     *
     * @param expressId 物流公司ID
     * @return 关联结果
     */
    @Override
    public Boolean relate(Integer expressId) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return merchantExpressService.relate(expressId, admin);
    }

    /**
     * 更新商户物流公司
     *
     * @param merchantExpress 商户物流公司
     */
    @Override
    public Boolean updateMerchantExpress(MerchantExpress merchantExpress) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        merchantExpress.setMerId(admin.getMerId());
        return merchantExpressService.updateById(merchantExpress);
    }

    /**
     * 商户物流公司分页列表
     */
    @Override
    public PageInfo<MerchantExpress> searchPage(MerchantExpressSearchRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return merchantExpressService.searchPage(request, admin);
    }

    /**
     * 商户物流公司开关
     *
     * @param id 商户物流公司ID
     */
    @Override
    public Boolean openSwitch(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return merchantExpressService.openSwitch(id, admin);
    }

    /**
     * 商户物流公司默认开关
     *
     * @param id 商户物流公司ID
     */
    @Override
    public Boolean defaultSwitch(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return merchantExpressService.defaultSwitch(id, admin);
    }

    /**
     * 商户物流公司删除
     *
     * @param id 商户物流公司ID
     */
    @Override
    public Boolean delete(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return merchantExpressService.delete(id, admin);
    }

    @Override
    public Boolean deleteByExpressId(Integer expressId) {
        LambdaUpdateWrapper<MerchantExpress> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MerchantExpress::getIsDelete, 1);
        wrapper.eq(MerchantExpress::getExpressId, expressId);
        wrapper.eq(MerchantExpress::getIsDelete, 0);
        return merchantExpressService.update(wrapper);
    }
}

