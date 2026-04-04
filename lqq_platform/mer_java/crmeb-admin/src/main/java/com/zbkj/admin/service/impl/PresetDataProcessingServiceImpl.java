package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.zbkj.admin.service.PresetDataProcessingService;
import com.zbkj.common.constants.GroupConfigConstants;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.merchant.MerchantMemberBenefits;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 预置数据处理服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2026/1/29
 */
@Service
public class PresetDataProcessingServiceImpl implements PresetDataProcessingService {

    @Autowired
    private MerchantMemberBenefitsService merchantMemberBenefitsService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private GroupConfigService groupConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 商户会员权益预置数据处理
     */
    @Override
    public Boolean MemberBenefitsPresetDataProcessing() {
        List<MerchantMemberBenefits> memberBenefitsList = merchantMemberBenefitsService.getPresetData(0);
        List<Merchant> merchantList = merchantService.all();
        List<MerchantMemberBenefits> benefitsList = new ArrayList<>();
        for (Merchant merchant : merchantList) {
            memberBenefitsList.forEach(benefits -> {
                MerchantMemberBenefits memberBenefits = new MerchantMemberBenefits();
                memberBenefits.setMerId(merchant.getId());
                memberBenefits.setName(benefits.getName());
                memberBenefits.setSelectedIcon(benefits.getSelectedIcon());
                memberBenefits.setUnselectedIcon(benefits.getUnselectedIcon());
                memberBenefits.setSort(benefits.getSort());
                memberBenefits.setCanDel(false);
                benefitsList.add(memberBenefits);
            });
        }
        return merchantMemberBenefitsService.saveBatch(benefitsList);
    }

    /**
     * 商户底部导航预置数据处理
     */
    @Override
    public Boolean MemberBottomNavigationProcessing() {
        List<Merchant> merchantList = merchantService.all();
        List<GroupConfig> groupConfigAllList = new ArrayList<>();
        for (Merchant merchant : merchantList) {
            List<GroupConfig> groupConfigList = initMemberBottomNavigationGroupConfig(merchant.getId());
            groupConfigAllList.addAll(groupConfigList);
        }
        return transactionTemplate.execute(e -> {
            for (Merchant merchant : merchantList) {
                groupConfigService.deleteByTag(GroupConfigConstants.TAG_BOTTOM_NAVIGATION, merchant.getId());
            }
            if (CollUtil.isNotEmpty(groupConfigAllList)) {
                groupConfigService.saveList(groupConfigAllList);
            }
            return Boolean.TRUE;
        });
    }

    private List<GroupConfig> initMemberBottomNavigationGroupConfig(Integer merId) {
        GroupConfig groupConfig1 = new GroupConfig();
        groupConfig1.setTag(GroupConfigConstants.TAG_BOTTOM_NAVIGATION);
        groupConfig1.setMerId(merId);
        groupConfig1.setName("首页");
        groupConfig1.setLinkUrl("crmebimage/presets/icon/首页-选中@3x.png");
        groupConfig1.setImageUrl("crmebimage/presets/icon/首页-未选中@3x.png");
        groupConfig1.setValue("/pages/merchant/home/index");
        groupConfig1.setStatus(true);
        groupConfig1.setSort(1);
        GroupConfig groupConfig2 = new GroupConfig();
        groupConfig2.setTag(GroupConfigConstants.TAG_BOTTOM_NAVIGATION);
        groupConfig2.setMerId(merId);
        groupConfig2.setName("分类");
        groupConfig2.setLinkUrl("crmebimage/presets/icon/商品分类-选中@3x.png");
        groupConfig2.setImageUrl("crmebimage/presets/icon/商品分类-未选中@3x.png");
        groupConfig2.setValue("/pages/merchant/classify/index");
        groupConfig2.setStatus(true);
        groupConfig2.setSort(2);
        GroupConfig groupConfig3 = new GroupConfig();
        groupConfig3.setTag(GroupConfigConstants.TAG_BOTTOM_NAVIGATION);
        groupConfig3.setMerId(merId);
        groupConfig3.setName("优惠券");
        groupConfig3.setLinkUrl("crmebimage/presets/icon/优惠券-选中@3x.png");
        groupConfig3.setImageUrl("crmebimage/presets/icon/优惠券-未选中@3x.png");
        groupConfig3.setValue("/pages/merchant/coupon/index");
        groupConfig3.setStatus(true);
        groupConfig3.setSort(3);
        GroupConfig groupConfig4 = new GroupConfig();
        groupConfig4.setTag(GroupConfigConstants.TAG_BOTTOM_NAVIGATION);
        groupConfig4.setMerId(merId);
        groupConfig4.setName("会员");
        groupConfig4.setLinkUrl("crmebimage/presets/icon/店铺会员-选中@3x.png");
        groupConfig4.setImageUrl("crmebimage/presets/icon/店铺会员-未选中@3x.png");
        groupConfig4.setValue("");
        groupConfig4.setStatus(false);
        groupConfig4.setSort(4);
        List<GroupConfig> groupConfigList = new ArrayList<>();
        groupConfigList.add(groupConfig1);
        groupConfigList.add(groupConfig2);
        groupConfigList.add(groupConfig3);
        groupConfigList.add(groupConfig4);
        return groupConfigList;
    }
}
