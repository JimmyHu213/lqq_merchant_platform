package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.promoter.PromoterMerchant;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.PromoterCommissionStatsResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * 推广员-商户绑定 Service
 * [LQQ-迁移] 推广员代理模式
 */
public interface PromoterMerchantService extends IService<PromoterMerchant> {

    // ===== 商户端 =====

    /**
     * 商户邀请推广员代理（创建申请，待平台审核）
     */
    Boolean invitePromoter(Integer uid, Integer merId, BigDecimal commissionRate);

    /**
     * 商户解除代理
     */
    Boolean dismissPromoter(Integer merId);

    /**
     * 商户修改佣金比例
     */
    Boolean updateCommissionRate(Integer merId, BigDecimal commissionRate);

    /**
     * 查商户的代理推广员
     */
    PromoterMerchant getByMerId(Integer merId);

    // ===== 平台端 =====

    /**
     * 平台审核
     */
    Boolean auditBinding(Integer id, Integer auditStatus, String reason);

    /**
     * 平台强制解绑
     */
    Boolean forceUnbind(Integer id);

    /**
     * 所有绑定关系列表
     */
    PageInfo<PromoterMerchant> getPlatformList(PageParamRequest pageParamRequest);

    // ===== 用户端 =====

    /**
     * 查推广员绑定的商户列表
     */
    List<PromoterMerchant> getByUid(Integer uid);

    /**
     * 推广员佣金统计（代理+裂变分开）
     */
    PromoterCommissionStatsResponse getCommissionStats(Integer uid);
}
