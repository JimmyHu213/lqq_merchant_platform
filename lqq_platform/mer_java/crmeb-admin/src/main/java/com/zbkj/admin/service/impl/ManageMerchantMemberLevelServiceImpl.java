package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.zbkj.admin.request.MerchantMemberLevelSaveRequest;
import com.zbkj.admin.response.MerMemCouponResponse;
import com.zbkj.admin.response.MerchantMemberLevelSaveResponse;
import com.zbkj.admin.service.ManageMerchantMemberLevelService;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.merchant.MerchantMemberBenefits;
import com.zbkj.common.model.merchant.MerchantMemberLevel;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.CouponService;
import com.zbkj.service.service.MerchantMemberBenefitsService;
import com.zbkj.service.service.MerchantMemberLevelService;
import com.zbkj.service.service.MerchantMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类的详细说明
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/3
 */
@Service
public class ManageMerchantMemberLevelServiceImpl implements ManageMerchantMemberLevelService {

    @Autowired
    private MerchantMemberLevelService memberLevelService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private MerchantMemberBenefitsService merchantMemberBenefitsService;
    @Autowired
    private MerchantMemberService merchantMemberService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 会员等级列表
     */
    @Override
    public List<MerchantMemberLevelSaveResponse> getList() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        List<MerchantMemberLevel> merchantMemberLevelList = memberLevelService.getList(systemAdmin.getMerId());
        if (CollUtil.isEmpty(merchantMemberLevelList)) return new ArrayList<>();
        List<Integer> levelList = merchantMemberLevelList.stream().map(MerchantMemberLevel::getLevel).collect(Collectors.toList());

        Map<Integer, Integer> membershipMap = memberLevelService.getMembershipMap(levelList, systemAdmin.getMerId());
        return merchantMemberLevelList.stream().map(memberLevel -> {
            MerchantMemberLevelSaveResponse response = new MerchantMemberLevelSaveResponse();
            response.setId(memberLevel.getId());
            response.setLevel(memberLevel.getLevel());
            response.setName(memberLevel.getName());
            response.setThresholdAmount(memberLevel.getThresholdAmount());
            response.setBenefits(memberLevel.getBenefits());
            response.setCouponIds(memberLevel.getCouponIds());
            if (StrUtil.isNotBlank(memberLevel.getCouponIds())) {
                List<Integer> couponIdList = Arrays.stream(memberLevel.getCouponIds().split(",")).map(Integer::parseInt).collect(Collectors.toList());
                List<Coupon> couponList = couponService.findByIds(couponIdList);
                List<MerMemCouponResponse> couponResponseList = couponList.stream().map(coupon -> {
                    MerMemCouponResponse couponResponse = new MerMemCouponResponse();
                    BeanUtils.copyProperties(coupon, couponResponse);
                    return couponResponse;
                }).collect(Collectors.toList());
                response.setCouponList(couponResponseList);
            }
            response.setNum(ObjectUtil.isNotNull(membershipMap.get(memberLevel.getLevel())) ? membershipMap.get(memberLevel.getLevel()) : 0);
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 会员等级新增
     */
    @Override
    public Boolean add(MerchantMemberLevelSaveRequest request) {
        if (ObjectUtil.isNull(request.getLevel()))
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "会员等级LV不能为空");
        if (request.getLevel() > 10) throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "会员等级LV最多10级");
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (request.getLevel().equals(1) && request.getThresholdAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "等级LV1的门槛只能为0");
        }
        MerchantMemberLevel lastLevel = memberLevelService.getLastLevelByMerId(admin.getMerId());
        if (ObjectUtil.isNull(lastLevel)) {
            if (!request.getLevel().equals(1)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "等级LV错误");
            }
        } else {
            if ((request.getLevel() - lastLevel.getLevel()) != 1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "等级LV错误");
            }
            if (request.getThresholdAmount().compareTo(lastLevel.getThresholdAmount()) <= 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "等级LV的门槛只能比低一级的LV门槛高");
            }
        }
        String name = URLUtil.decode(request.getName());
        if (memberLevelService.isExistName(name, admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "会员等级名称不能重复");
        }

        String couponIds = "";
        if (StrUtil.isNotBlank(request.getBenefits())) {
            List<Integer> benefitsIdList = Arrays.stream(request.getBenefits().split(",")).map(Integer::parseInt).collect(Collectors.toList());
            List<MerchantMemberBenefits> benefitsList = merchantMemberBenefitsService.findListByIdList(benefitsIdList, admin.getMerId());
            MerchantMemberBenefits memberBenefits = benefitsList.stream().filter(e -> e.getName().equals("入会/升级有礼")).findAny().orElse(null);
            if (ObjectUtil.isNotNull(memberBenefits) && StrUtil.isNotBlank(request.getCouponIds())) {
                couponIds = request.getCouponIds();
            }
        }

        MerchantMemberLevel merchantMemberLevel = new MerchantMemberLevel();
        merchantMemberLevel.setMerId(admin.getMerId());
        merchantMemberLevel.setLevel(request.getLevel());
        merchantMemberLevel.setName(name);
        merchantMemberLevel.setLevel(request.getLevel());
        merchantMemberLevel.setThresholdAmount(request.getThresholdAmount());
        merchantMemberLevel.setBenefits(StrUtil.isNotBlank(request.getBenefits()) ? request.getBenefits() : "");
        merchantMemberLevel.setCouponIds(couponIds);
        return memberLevelService.save(merchantMemberLevel);
    }

    /**
     * 会员等级编辑
     */
    @Override
    public Boolean update(MerchantMemberLevelSaveRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择会员等级");
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        MerchantMemberLevel merchantMemberLevel = memberLevelService.getByIdException(request.getId());
        if (!merchantMemberLevel.getMerId().equals(admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户会员等级不存在");
        }
        if (request.getThresholdAmount().compareTo(merchantMemberLevel.getThresholdAmount()) != 0) {
            if (merchantMemberLevel.getLevel().equals(1)) {
                if (request.getThresholdAmount().compareTo(BigDecimal.ZERO) > 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "等级LV1的门槛只能为0");
                }
            } else {
                MerchantMemberLevel lowLevel = memberLevelService.getByLevel(merchantMemberLevel.getLevel() - 1, admin.getMerId());
                if (request.getThresholdAmount().compareTo(lowLevel.getThresholdAmount()) <= 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "等级LV的门槛只能比低一级的LV门槛高");
                }
                MerchantMemberLevel nextLevel = memberLevelService.getByLevel(merchantMemberLevel.getLevel() + 1, admin.getMerId());
                if (ObjectUtil.isNotNull(nextLevel) && nextLevel.getThresholdAmount().compareTo(request.getThresholdAmount()) <= 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "等级LV的门槛只能比高一级的LV门槛低");
                }
            }
        }
        String name = URLUtil.decode(request.getName());
        if (!name.equals(merchantMemberLevel.getName())) {
            if (memberLevelService.isExistName(name, admin.getMerId())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "会员等级名称不能重复");
            }
        }

        String couponIds = "";
        if (StrUtil.isNotBlank(request.getBenefits())) {
            List<Integer> benefitsIdList = Arrays.stream(request.getBenefits().split(",")).map(Integer::parseInt).collect(Collectors.toList());
            List<MerchantMemberBenefits> benefitsList = merchantMemberBenefitsService.findListByIdList(benefitsIdList, admin.getMerId());
            MerchantMemberBenefits memberBenefits = benefitsList.stream().filter(e -> e.getName().equals("入会/升级有礼")).findAny().orElse(null);
            if (ObjectUtil.isNotNull(memberBenefits) && StrUtil.isNotBlank(request.getCouponIds())) {
                couponIds = request.getCouponIds();
            }
        }

        MerchantMemberLevel memberLevel = new MerchantMemberLevel();
        memberLevel.setId(merchantMemberLevel.getId());
        memberLevel.setName(name);
        memberLevel.setThresholdAmount(request.getThresholdAmount());
        memberLevel.setBenefits(StrUtil.isNotBlank(request.getBenefits()) ? request.getBenefits() : "");
        memberLevel.setCouponIds(couponIds);
        memberLevel.setUpdateTime(DateUtil.date());
        return transactionTemplate.execute(e -> {
            memberLevelService.updateById(memberLevel);
            if (request.getThresholdAmount().compareTo(merchantMemberLevel.getThresholdAmount()) != 0) {
                merchantMemberService.clearLevelByLevel(merchantMemberLevel.getLevel(), admin.getMerId());
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 会员等级删除
     *
     * @param id 商户会员等级id
     */
    @Override
    public Boolean delete(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        MerchantMemberLevel merchantMemberLevel = memberLevelService.getByIdException(id);
        if (!merchantMemberLevel.getMerId().equals(admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户会员等级不存在");
        }
        merchantMemberLevel.setIsDel(true);
        return transactionTemplate.execute(e -> {
            memberLevelService.updateById(merchantMemberLevel);
            merchantMemberService.clearLevelByLevel(merchantMemberLevel.getLevel(), admin.getMerId());
            return Boolean.TRUE;
        });
    }


}
