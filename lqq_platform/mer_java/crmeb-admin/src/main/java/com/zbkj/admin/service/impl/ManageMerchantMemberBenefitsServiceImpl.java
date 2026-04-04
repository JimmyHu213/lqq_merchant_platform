package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zbkj.admin.service.ManageMerchantMemberBenefitsService;
import com.zbkj.admin.vo.MerchantMemberBenefitsVo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.merchant.MerchantMemberBenefits;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.MerchantMemberBenefitsService;
import com.zbkj.service.service.MerchantService;
import com.zbkj.service.service.SystemAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理端商户会员权益服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/1
 */
@Service
public class ManageMerchantMemberBenefitsServiceImpl implements ManageMerchantMemberBenefitsService {

    @Autowired
    private MerchantMemberBenefitsService memberBenefitsService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private MerchantService merchantService;

    /**
     * 获取会员权益列表
     */
    @Override
    public List<MerchantMemberBenefitsVo> getList() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        List<MerchantMemberBenefits> benefitsList = memberBenefitsService.getList(systemAdmin.getMerId());
        if (CollUtil.isEmpty(benefitsList)) return new ArrayList<>();
        return benefitsList.stream().map(e -> {
            MerchantMemberBenefitsVo benefitsVo = new MerchantMemberBenefitsVo();
            benefitsVo.setId(e.getId());
            benefitsVo.setName(e.getName());
            benefitsVo.setSelectedIcon(e.getSelectedIcon());
            benefitsVo.setUnselectedIcon(e.getUnselectedIcon());
            benefitsVo.setSort(e.getSort());
            benefitsVo.setCanDel(e.getCanDel());
            benefitsVo.setLink(e.getLink());
            return benefitsVo;
        }).collect(Collectors.toList());
    }

    /**
     * 会员权益保存
     *
     * @param benefitsVoList 会员权益类表
     */
    @Override
    public Boolean save(List<MerchantMemberBenefitsVo> benefitsVoList) {
        /**
         * 预设数据名称不能修改
         * 预设数据不能删除
         */
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (benefitsVoList.size() > 20) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "会员权益最多20个");
        }
        List<String> nameList = benefitsVoList.stream().map(MerchantMemberBenefitsVo::getName).collect(Collectors.toList());
        Set<String> nameSet = new HashSet<>(nameList);
        if (nameList.size() != nameSet.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "会员权益名称不能重复");
        }

        List<MerchantMemberBenefits> presetDataList = memberBenefitsService.getPresetData(systemAdmin.getMerId());
        for (MerchantMemberBenefits presetBenefits : presetDataList) {
            boolean isCheck = false;
            for (MerchantMemberBenefitsVo benefitsVo : benefitsVoList) {
                if (ObjectUtil.isNotNull(benefitsVo.getId()) && benefitsVo.getId() > 0) {
                    if (presetBenefits.getId().equals(benefitsVo.getId()) && presetBenefits.getName().equals(benefitsVo.getName())) {
                        isCheck = true;
                        break;
                    }
                }
            }
            if (!isCheck) throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "预置会员权益不能删除");
        }

        String cdnUrl = systemAttachmentService.getCdnUrl();

        List<MerchantMemberBenefits> addBenefitsList = new ArrayList<>();
        List<MerchantMemberBenefits> updateBenefitsList = new ArrayList<>();
        for (MerchantMemberBenefitsVo benefitsVo : benefitsVoList) {
            if (ObjectUtil.isNull(benefitsVo.getId()) || benefitsVo.getId().equals(0)) {
                MerchantMemberBenefits benefits = new MerchantMemberBenefits();
                benefits.setMerId(systemAdmin.getMerId());
                benefits.setName(benefitsVo.getName());
                benefits.setSelectedIcon(systemAttachmentService.clearPrefix(benefitsVo.getSelectedIcon(), cdnUrl));
                benefits.setUnselectedIcon(systemAttachmentService.clearPrefix(benefitsVo.getUnselectedIcon(), cdnUrl));
                benefits.setSort(benefitsVo.getSort());
                benefits.setLink(benefitsVo.getLink());
                benefits.setCanDel(true);
                addBenefitsList.add(benefits);
            } else {
                MerchantMemberBenefits benefits = new MerchantMemberBenefits();
                benefits.setId(benefitsVo.getId());
                benefits.setName(benefitsVo.getName());
                benefits.setSelectedIcon(systemAttachmentService.clearPrefix(benefitsVo.getSelectedIcon(), cdnUrl));
                benefits.setUnselectedIcon(systemAttachmentService.clearPrefix(benefitsVo.getUnselectedIcon(), cdnUrl));
                benefits.setSort(benefitsVo.getSort());
                benefits.setLink(benefitsVo.getLink());
                benefits.setIsDel(false);
                updateBenefitsList.add(benefits);
            }
        }

        return transactionTemplate.execute(e -> {
            memberBenefitsService.deleteByMerId(systemAdmin.getMerId());
            if (CollUtil.isNotEmpty(addBenefitsList)) {
                memberBenefitsService.saveBatch(addBenefitsList);
            }
            if (CollUtil.isNotEmpty(updateBenefitsList)) {
                memberBenefitsService.updateBatchById(updateBenefitsList);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 商户会员权益预置数据处理
     */
    @Override
    public Boolean presetDataProcessing() {
        List<MerchantMemberBenefits> memberBenefitsList = memberBenefitsService.getPresetData(0);
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
        return memberBenefitsService.saveBatch(benefitsList);
    }
}
