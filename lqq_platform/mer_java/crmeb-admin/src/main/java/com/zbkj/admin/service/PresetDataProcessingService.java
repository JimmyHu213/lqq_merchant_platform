package com.zbkj.admin.service;

/**
 * 预置数据处理服务
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/29
 */
public interface PresetDataProcessingService {

    /**
     * 商户会员权益预置数据处理
     */
    Boolean MemberBenefitsPresetDataProcessing();

    /**
     * 商户底部导航预置数据处理
     */
    Boolean MemberBottomNavigationProcessing();

}
