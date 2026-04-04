package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.MerchantInfo;
import com.zbkj.common.request.reservation.MerchantReservationToHomeRequest;
import com.zbkj.common.request.reservation.MerchantReservationToStoreRequest;
import com.zbkj.common.response.reservation.MerchantReservationToStoreResponse;
import com.zbkj.common.response.reservation.MerchantReservationToHomeResponse;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.MerchantInfoDao;
import com.zbkj.service.service.MerchantInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
*  MerchantInfoServiceImpl 接口实现
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
@Service
public class MerchantInfoServiceImpl extends ServiceImpl<MerchantInfoDao, MerchantInfo> implements MerchantInfoService {

    @Resource
    private MerchantInfoDao dao;

    /**
     * 通过商户id获取
     * @param merId 商户id
     * @return MerchantInfo
     */
    @Override
    public MerchantInfo getByMerId(Integer merId) {
        LambdaQueryWrapper<MerchantInfo> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantInfo::getMerId, merId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取商户预约设置信息
     * @return  商户预约设置信息响应对象
     */
    @Override
    public MerchantReservationToHomeResponse getReservationConfigInfo() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        MerchantInfo merchantInfo = getByMerId(systemAdmin.getMerId());
        MerchantReservationToHomeResponse reservationResponse = new MerchantReservationToHomeResponse();
        BeanUtils.copyProperties(merchantInfo, reservationResponse);
        return reservationResponse;
    }

    /**
     * 编辑商家预约设置信息
     * @param request 商家预约设置信息对象
     * @return 编辑结果
     */
    @Override
    public Boolean editReservationConfig(MerchantReservationToHomeRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        MerchantInfo merchantInfo = getByMerId(systemAdmin.getMerId());
        BeanUtils.copyProperties(request, merchantInfo);
        if (ObjectUtil.isNull(merchantInfo.getServiceEvidenceFormId()) || ObjectUtil.isEmpty(merchantInfo.getServiceEvidenceFormId())) {
            merchantInfo.setServiceEvidenceFormId(0);
        }
        merchantInfo.setUpdateTime(new Date());
        return updateById(merchantInfo);
    }

    /**
     * 获取商户到店服务预约设置信息
     *
     * @return  商户到店服务预约设置信息响应对象
     */
    @Override
    public MerchantReservationToStoreResponse getReservationConfigToStoreInfo() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        MerchantInfo merchantInfo = getByMerId(systemAdmin.getMerId());
        MerchantReservationToStoreResponse response = new MerchantReservationToStoreResponse();
        response.setServiceWorkOrderSwitch(merchantInfo.getServiceWorkOrderSwitch());
        return response;
    }

    /**
     * 编辑商家到店服务预约设置信息
     *
     * @param request  商户到店服务预约设置请求对象
     * @return 编辑结果
     */
    @Override
    public Boolean editToStoreReservationConfig(MerchantReservationToStoreRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Integer merId = systemAdmin.getMerId();
        LambdaUpdateWrapper<MerchantInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MerchantInfo::getMerId, merId);
        wrapper.set(MerchantInfo::getServiceWorkOrderSwitch, request.getServiceWorkOrderSwitch());
        return update(wrapper);
    }
}

