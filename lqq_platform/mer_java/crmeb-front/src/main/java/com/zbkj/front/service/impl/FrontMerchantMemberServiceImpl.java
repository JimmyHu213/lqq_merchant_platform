package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.merchant.MerchantMember;
import com.zbkj.common.model.merchant.MerchantMemberBenefits;
import com.zbkj.common.model.merchant.MerchantMemberLevel;
import com.zbkj.common.model.user.UserShoppingCredits;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.MerchantCollectResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.front.response.*;
import com.zbkj.front.service.FrontMerchantMemberService;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 移动端商户会员服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/10
 */
@Service
public class FrontMerchantMemberServiceImpl implements FrontMerchantMemberService {

    @Autowired
    private UserService userService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantMemberService merchantMemberService;
    @Autowired
    private MerchantMemberLevelService merchantMemberLevelService;
    @Autowired
    private MerchantMemberBenefitsService merchantMemberBenefitsService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private UserShoppingCreditsService userShoppingCreditsService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private MerchantUserService merchantUserService;

    /**
     * 成为商户会员
     */
    @Override
    public BecomeMerchantMemberResponse becomeMember(Integer merId) {
        Integer userId = userService.getUserId();
        Merchant merchant = merchantService.getByIdException(merId);
        MerchantMember merchantMemberTemp = merchantMemberService.getByUidAndMerId(userId, merId);
        if (ObjectUtil.isNotNull(merchantMemberTemp) && !merchantMemberTemp.getIsLogoff()) {
            throw new CrmebException("已经是商户会员了");
        }

        // 获取用户在商户的累计消费额度
        BigDecimal consumptionAmount = orderDetailService.getConsumptionAmountByMerAndUid(merId, userId);
        List<MerchantMemberLevel> memberLevelList = merchantMemberLevelService.getList(merId);
        if (CollUtil.isEmpty(memberLevelList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请联系商户先设置商户会员等级");
        }
        MerchantMemberLevel memberLevel = getUserReasonableLevel(consumptionAmount, memberLevelList);

        List<Coupon> couponList = new ArrayList<>();
        if (StrUtil.isNotBlank(memberLevel.getCouponIds())) {
            String couponIds = memberLevel.getCouponIds();
            List<Integer> couponIdList = Arrays.stream(couponIds.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            List<Coupon> memberCouponList = couponService.findByIds(couponIdList);
            if (CollUtil.isNotEmpty(memberCouponList)) {
                couponList = memberCouponList;
            }
        }
        // 去除不可领优惠券
        if (CollUtil.isNotEmpty(couponList)) {
            for (int i = 0; i < couponList.size(); ) {
                Coupon coupon = couponList.get(i);
                if (!coupon.getStatus()) {
                    couponList.remove(i);
                    continue;
                }
                //看是否有剩余数量,是否够给当前用户
                if (coupon.getIsLimited() && coupon.getLastTotal() < 1) {
                    couponList.remove(i);
                    continue;
                }
                //看是否过期
                if (coupon.getIsTimeReceive()) {
                    //非永久可领取
                    String date = CrmebDateUtil.nowDateTimeStr();
                    int result = CrmebDateUtil.compareDate(date, CrmebDateUtil.dateToStr(coupon.getReceiveEndTime(), DateConstants.DATE_FORMAT), DateConstants.DATE_FORMAT);
                    if (result > 0) {
                        couponList.remove(i);
                        continue;
                    }
                }
                if (!couponUserService.userIsCanReceiveCoupon(coupon, userId)) {
                    //已经领取过了
                    couponList.remove(i);
                    continue;
                }
                //是否有固定的使用时间
                if (!coupon.getIsFixedTime()) {
                    String endTime = CrmebDateUtil.addDay(CrmebDateUtil.nowDate(DateConstants.DATE_FORMAT), coupon.getDay(), DateConstants.DATE_FORMAT);
                    coupon.setUseEndTime(CrmebDateUtil.strToDate(endTime, DateConstants.DATE_FORMAT));
                    coupon.setUseStartTime(CrmebDateUtil.nowDateTimeReturnDate(DateConstants.DATE_FORMAT));
                }
                i++;
            }
        }

        MerchantMember merchantMember = new MerchantMember();
        merchantMember.setUid(userId);
        merchantMember.setMerId(merchant.getId());
        merchantMember.setLevel(memberLevel.getLevel());
        merchantMember.setFinancialStatus(1);

        boolean isAddCredits;
        UserShoppingCredits credits = userShoppingCreditsService.getByUserIdAndMerId(userId, merId);
        isAddCredits = !ObjectUtil.isNotNull(credits);

        List<Coupon> finalCouponList = couponList;

        Boolean execute = transactionTemplate.execute(e -> {
            merchantMemberService.save(merchantMember);
            // 加入会员领取优惠券
            if (CollUtil.isNotEmpty(finalCouponList)) {
                couponUserService.batchReceiveCoupon(finalCouponList, userId);
            }
            if (isAddCredits) {
                UserShoppingCredits userShoppingCredits = new UserShoppingCredits();
                userShoppingCredits.setUid(userId);
                userShoppingCredits.setMerId(merId);
                userShoppingCredits.setRechargeAmount(BigDecimal.ZERO);
                userShoppingCredits.setGiftAmount(BigDecimal.ZERO);
                userShoppingCreditsService.save(userShoppingCredits);
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("加入商户会员失败");
        }
        BecomeMerchantMemberResponse response = new BecomeMerchantMemberResponse();
        if (CollUtil.isEmpty(couponList)) {
            response.setCouponList(new ArrayList<>());
        } else {
            List<BecomeMerchantMemberCouponResponse> couponResponseList = couponList.stream().map(coupon -> {
                BecomeMerchantMemberCouponResponse couponResponse = new BecomeMerchantMemberCouponResponse();
                BeanUtils.copyProperties(coupon, couponResponse);
                return couponResponse;
            }).collect(Collectors.toList());
            response.setCouponList(couponResponseList);
        }
        merchantUserService.addMerchantUser(userId, merId);
        return response;
    }

    /**
     * 获取商户会员用户信息
     */
    @Override
    public MerchantMemberUserResponse getMemberUserInfo(Integer merId) {
        Integer userId = userService.getUserId();
        MerchantMemberUserResponse response = new MerchantMemberUserResponse();
        if (userId <= 0) {
            response.setIsMerchantMember(false);
            return response;
        }
        MerchantMember merchantMember = merchantMemberService.getByUidAndMerId(userId, merId);
        if (ObjectUtil.isNull(merchantMember) || merchantMember.getIsLogoff()) {
            response.setIsMerchantMember(false);
            return response;
        }
        MerchantMemberLevel memberLevel = merchantMemberLevelService.getByLevel(merchantMember.getLevel(), merId);
        UserShoppingCredits userShoppingCredits = userShoppingCreditsService.getByUserIdAndMerId(userId, merId);
        response.setIsMerchantMember(true);
        response.setLevel(merchantMember.getLevel());
        response.setRechargeAmount(userShoppingCredits.getRechargeAmount());
        response.setGiftAmount(userShoppingCredits.getGiftAmount());
        response.setShoppingCreditsBalance(userShoppingCredits.getRechargeAmount().add(userShoppingCredits.getGiftAmount()));
        response.setLevelName(memberLevel.getName());
        response.setBenefits(memberLevel.getBenefits());
        return response;
    }

    /**
     * 获取商户会员等级列表
     */
    @Override
    public List<MerchantMemberLevelBenefitsResponse> findMemberLevelList(Integer merId) {
        List<MerchantMemberLevel> memberLevelList = merchantMemberLevelService.getList(merId);
        if (CollUtil.isEmpty(memberLevelList)) {
            return new ArrayList<>();
        }
        List<MerchantMemberLevelBenefitsResponse> responseList = new ArrayList<>();
        for (MerchantMemberLevel memberLevel : memberLevelList) {
            MerchantMemberLevelBenefitsResponse response = new MerchantMemberLevelBenefitsResponse();
            response.setId(memberLevel.getId());
            response.setName(memberLevel.getName());
            response.setLevel(memberLevel.getLevel());
            response.setThresholdAmount(memberLevel.getThresholdAmount());
            if (StrUtil.isNotBlank(memberLevel.getBenefits())) {
                List<Integer> benefitsIdList = Arrays.stream(memberLevel.getBenefits().split(",")).map(Integer::parseInt).collect(Collectors.toList());
                List<MerchantMemberBenefits> benefitsList = merchantMemberBenefitsService.findListByIdList(benefitsIdList, merId);
                List<MerchantMemberBenefitsResponse> benefitsResponseList = benefitsList.stream().map(e -> {
                    MerchantMemberBenefitsResponse benefitsResponse = new MerchantMemberBenefitsResponse();
                    benefitsResponse.setId(e.getId());
                    benefitsResponse.setName(e.getName());
                    benefitsResponse.setSelectedIcon(e.getSelectedIcon());
                    benefitsResponse.setUnselectedIcon(e.getUnselectedIcon());
                    benefitsResponse.setLink(e.getLink());
                    benefitsResponse.setSort(e.getSort());
                    return benefitsResponse;
                }).collect(Collectors.toList());
                response.setBenefitsList(benefitsResponseList);
            } else {
                response.setBenefitsList(new ArrayList<>());
            }
            responseList.add(response);
        }
        return responseList;
    }

    /**
     * 用户入会列表
     */
    @Override
    public PageInfo<MerchantCollectResponse> findUserAddMerchantList(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        Page<Object> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<MerchantMember> merchantMemberList = merchantMemberService.findUserAddMerchantList(userId);
        if (CollUtil.isEmpty(merchantMemberList)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        List<MerchantCollectResponse> responseList = merchantMemberList.stream().map(merchantMember -> {
            MerchantCollectResponse response = new MerchantCollectResponse();
            Merchant merchant = merchantService.getById(merchantMember.getMerId());
            response.setMerId(merchantMember.getMerId());
            response.setMerName(merchant.getName());
            response.setMerAvatar(merchant.getAvatar());
            response.setIsSelf(merchant.getIsSelf());
            response.setCollectNum(merchantMemberService.getCountByMerId(merchantMember.getMerId()));
            response.setPcGoodStoreCoverImage(merchant.getPcGoodStoreCoverImage());
            response.setPcLogo(merchant.getPcLogo());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 商户会员注销
     */
    @Override
    public Boolean logoffMember(Integer merId) {
        Integer userId = userService.getUserIdException();
        MerchantMember merchantMember = merchantMemberService.getByUidAndMerId(userId, merId);
        if (ObjectUtil.isNull(merchantMember) || merchantMember.getIsLogoff()) {
            return true;
        }
        merchantMember.setIsLogoff(true);
        merchantMember.setUpdateTime(new Date());
        return merchantMemberService.updateById(merchantMember);
    }

    /**
     * 获取用户合理等级
     *
     * @param consumptionAmount 消费金额
     * @param memberLevelList   会员等级列表
     * @return 会员等级
     */
    private MerchantMemberLevel getUserReasonableLevel(BigDecimal consumptionAmount, List<MerchantMemberLevel> memberLevelList) {
        MerchantMemberLevel memberLevel = new MerchantMemberLevel();
        for (MerchantMemberLevel merchantMemberLevel : memberLevelList) {
            if (consumptionAmount.compareTo(merchantMemberLevel.getThresholdAmount()) >= 0) {
                memberLevel = merchantMemberLevel;
            }
        }
        return memberLevel;
    }
}
