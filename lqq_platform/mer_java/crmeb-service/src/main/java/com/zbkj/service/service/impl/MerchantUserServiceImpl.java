package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.UserConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.MerchantMember;
import com.zbkj.common.model.merchant.MerchantMemberLevel;
import com.zbkj.common.model.merchant.MerchantUser;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserShoppingCredits;
import com.zbkj.common.model.user.UserShoppingCreditsRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.DateRequest;
import com.zbkj.common.request.merchant.MerchantUserSearchRequest;
import com.zbkj.common.response.MerchantUserDetailResponse;
import com.zbkj.common.response.MerchantUserPageResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.MerchantUserDao;
import com.zbkj.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HZW
 * @description MerchantUserServiceImpl 接口实现
 * @date 2025-12-24
 */
@Service
public class MerchantUserServiceImpl extends ServiceImpl<MerchantUserDao, MerchantUser> implements MerchantUserService {

    @Resource
    private MerchantUserDao dao;

    @Autowired
    private UserService userService;
    @Autowired
    private MerchantMemberService merchantMemberService;
    @Autowired
    private UserShoppingCreditsService userShoppingCreditsService;
    @Autowired
    private MerchantMemberLevelService merchantMemberLevelService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserShoppingCreditsRecordService userShoppingCreditsRecordService;

    /**
     * 商户用户分页列表
     */
    @Override
    public PageInfo<MerchantUserPageResponse> findUserPage(MerchantUserSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Page<User> pageUser = PageHelper.startPage(request.getPage(), request.getLimit());
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("merId", systemAdmin.getMerId());

        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        if (StrUtil.isNotBlank(request.getUserIdentity())) {
            if (request.getUserIdentity().equals("ordinary")) {
                map.put("isCollect", 0);
                map.put("isMember", 0);
            }
            if (request.getUserIdentity().equals("fans")) {
                map.put("isCollect", 1);
            }
            if (request.getUserIdentity().equals("member")) {
                map.put("isMember", 1);
            }
        }
        if (ObjectUtil.isNotNull(request.getMemberLevel())) {
            map.put("memberLevel", request.getMemberLevel());
        }
        if (ObjectUtil.isNotNull(request.getFinancialStatus())) {
            map.put("financialStatus", request.getFinancialStatus());
        }
        if (ObjectUtil.isNotNull(request.getSex())) {
            map.put("sex", request.getSex());
        }
        DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getMembershipTime());
        if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
            map.put("startTime", dateLimit.getStartTime());
            map.put("endTime", dateLimit.getEndTime());
        }
        List<MerchantUserPageResponse> responseList = dao.findUserList(map);
        responseList.forEach(res -> {
            if (ObjectUtil.isNotNull(res.getIsCollect()) && res.getIsCollect() > 0) {
                res.setIsCollect(1);
            }
            if (StrUtil.isBlank(res.getLevelName())) {
                res.setLevelName("--");
            }
            if (ObjectUtil.isNull(res.getFinancialStatus())) {
                res.setFinancialStatus(1);
                res.setRechargeAmount(new BigDecimal("0.00"));
                res.setGiftAmount(new BigDecimal("0.00"));
            }
        });
        return CommonPage.copyPageInfo(pageUser, responseList);
    }

    /**
     * 添加商户用户(单)
     * @param userId 用户ID
     * @param merId 商户ID
     */
    @Override
    public void addMerchantUser(Integer userId, Integer merId) {
        if (isExistMerchantUser(userId, merId)) {
            return;
        }
        MerchantUser merchantUser = new MerchantUser();
        merchantUser.setUid(userId);
        merchantUser.setMerId(merId);
        save(merchantUser);
    }

    /**
     * 添加商户用户（多）
     * @param userId 用户ID
     * @param merIdList 商户ID列表
     */
    @Override
    public void addManyMerchantUser(Integer userId, List<Integer> merIdList) {
        for (Integer merId : merIdList) {
            addMerchantUser(userId, merId);
        }
    }

    private Boolean isExistMerchantUser(Integer userId, Integer merId) {
        LambdaQueryWrapper<MerchantUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantUser::getUid, userId);
        lqw.eq(MerchantUser::getMerId, merId);
        Integer count = dao.selectCount(lqw);
        return count > 0;
    }

    /**
     * 商户用户详情
     *
     * @param userId 用户id
     */
    @Override
    public MerchantUserDetailResponse getMerchantUserDetail(Integer userId) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        MerchantUser merchantUser = getByUserIdAndMerId(userId, systemAdmin.getMerId());
        if (ObjectUtil.isNull(merchantUser)) throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户用户不存在");

        MerchantUserDetailResponse response = new MerchantUserDetailResponse();
        User user = userService.getById(userId);
        if (ObjectUtil.isNull(user) || user.getIsLogoff()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "用户不存在");
        }
        MerchantMember merchantMember = merchantMemberService.getByUidAndMerId(userId, systemAdmin.getMerId());
        UserShoppingCredits userShoppingCredits = userShoppingCreditsService.getByUserIdAndMerId(userId, systemAdmin.getMerId());

        response.setUid(user.getId());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setSex(user.getSex());

        if (ObjectUtil.isNotNull(userShoppingCredits)) {
            response.setRechargeAmount(userShoppingCredits.getRechargeAmount());
            response.setGiftAmount(userShoppingCredits.getGiftAmount());
        }
        if (ObjectUtil.isNotNull(merchantMember) && !merchantMember.getIsLogoff()) {
            response.setFinancialStatus(merchantMember.getFinancialStatus());
            response.setLevel(merchantMember.getLevel());
            MerchantMemberLevel merchantMemberLevel = merchantMemberLevelService.getByLevel(merchantMember.getLevel(), systemAdmin.getMerId());
            response.setLevelName(merchantMemberLevel.getName());

            response.setMembershipTime(merchantMember.getCreateTime());
        }
        response.setConsumeTotalAmount(orderDetailService.getConsumptionAmountByMerAndUid(systemAdmin.getMerId(), userId));
        response.setConsumeTotalOrderNum(orderService.getConsumeOrderNumByMerAndUid(systemAdmin.getMerId(), userId));

        response.setAccount(user.getAccount());
        response.setPhone(user.getPhone());
        response.setBirthday(user.getBirthday());
        response.setCountry(user.getCountry());
        String province = StrUtil.isNotBlank(user.getProvince()) ? user.getProvince() : "";
        String city = StrUtil.isNotBlank(user.getCity()) ? user.getCity() : "";
        String district = StrUtil.isNotBlank(user.getDistrict()) ? user.getDistrict() : "";
        String address = StrUtil.isNotBlank(user.getAddress()) ? user.getAddress() : "";
        response.setUserAddress(province + city + district + address);
        response.setRegisterType(user.getRegisterType());

        response.setFirstVisitTime(user.getCreateTime());
        response.setLastLoginTime(user.getLastLoginTime());
        response.setAddMerchantUserTime(merchantUser.getCreateTime());
        return response;
    }

    /**
     * 商户用户购物金记录
     */
    @Override
    public List<UserShoppingCreditsRecord> findMerchantUserShoppingCreditsRecord(Integer userId) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return userShoppingCreditsRecordService.findByMerAndUid(systemAdmin.getMerId(), userId);
    }

    /**
     * 商户用户购物金冻结/解冻
     */
    @Override
    public Boolean userShoppingCreditsFreezeUpdate(Integer userId) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        MerchantMember merchantMember = merchantMemberService.getByUidAndMerId(userId, systemAdmin.getMerId());
        if (ObjectUtil.isNull(merchantMember) || merchantMember.getIsLogoff()) return true;
        merchantMember.setFinancialStatus(merchantMember.getFinancialStatus().equals(1) ? 0 : 1);
        merchantMember.setUpdateTime(new Date());
        return merchantMemberService.updateById(merchantMember);
    }

    /**
     * 商户用户数量
     *
     * @param merId 商户ID
     */
    @Override
    public Integer merchantUserNum(Integer merId) {
        return dao.getMerchantUserNum(merId);
    }

    /**
     * 获取商户新增用户数
     *
     * @param merId 商户ID
     * @param dateRequest 日期请求对象
     */
    @Override
    public Integer getAddUserNum(Integer merId, DateRequest dateRequest) {
        QueryWrapper<MerchantUser> lqw = Wrappers.query();
        lqw.eq("mer_id", merId);
        lqw.apply("DATE_FORMAT(create_time, '%Y-%m-%d') between {0} and {1}", dateRequest.getStartTime(), dateRequest.getEndTime());
        return dao.selectCount(lqw);
    }

    @Override
    public Integer getAliveUserNum(Integer merId, DateRequest dateRequest) {
        return dao.getAliveUserNum(merId, dateRequest.getStartTime(), dateRequest.getEndTime());
    }

    @Override
    public Integer getOrderUserNum(Integer merId, DateRequest dateRequest) {
        return dao.getOrderUserNum(merId, dateRequest.getStartTime(), dateRequest.getEndTime());
    }

    @Override
    public Integer getUserAddNum(Integer merId, String date, DateRequest dateRequest) {
        QueryWrapper<MerchantUser> lqw = Wrappers.query();
        lqw.eq("mer_id", merId);
        lqw.like("create_time", date);
        if (date.matches("^\\d{4}-\\d{2}$")) {
            if (date.equals(dateRequest.getStartTime().substring(0, 7))) {
                lqw.ge("create_time", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 7))) {
                lqw.le("create_time", dateRequest.getEndTime());
            }
        } else if (date.matches("^\\d{4}$")) {
            if (date.equals(dateRequest.getStartTime().substring(0, 4))) {
                lqw.ge("create_time", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 4))) {
                lqw.le("create_time", dateRequest.getEndTime());
            }
        }
        return dao.selectCount(lqw);
    }

    @Override
    public Integer getUserAliveNum(Integer merId, String date, DateRequest dateRequest) {
        return dao.getAliveUserNumByDate(merId, date, dateRequest.getStartTime(), dateRequest.getEndTime());
    }

    @Override
    public Map<String, Integer> getOrderMerchantUserNum(Integer merId, String date, DateRequest dateRequest) {
        Map<String, Integer> map = new HashMap<>();
        List<Integer> uidList = orderService.getOrderUidList(merId, date, dateRequest);
        if (ObjectUtil.isNull(uidList) || uidList.size() == 0) {
            map.put("newUserNum", 0);
            map.put("oldUserNum", 0);
            return map;
        }
        // 新用户数
        QueryWrapper<MerchantUser> lqw = Wrappers.query();
        lqw.eq("mer_id", merId);
        lqw.in("uid", uidList);
        lqw.like("create_time", date);
        if (date.matches("^\\d{4}-\\d{2}$")) {
            if (date.equals(dateRequest.getStartTime().substring(0, 7))) {
              lqw.apply("DATE_FORMAT(create_time, '%Y-%m') >= {0}", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 7))) {
              lqw.apply("DATE_FORMAT(create_time, '%Y-%m') <= {0}", dateRequest.getEndTime());
            }
        } else if (date.matches("^\\d{4}$")) {
            if (date.equals(dateRequest.getStartTime().substring(0, 4))) {
                lqw.apply("DATE_FORMAT(create_time, '%Y') >= {0}", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 4))) {
                lqw.apply("DATE_FORMAT(create_time, '%Y') <= {0}", dateRequest.getEndTime());
            }
        }
        Integer newUserNum = dao.selectCount(lqw);
        // 老用户数
        QueryWrapper<MerchantUser> lqwOld = Wrappers.query();
        lqwOld.eq("mer_id", merId);
        lqwOld.in("uid", uidList);
        if (date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            lqwOld.apply("DATE_FORMAT(create_time, '%Y-%m-%d') < {0}", date);
        }
        if (date.matches("^\\d{4}-\\d{2}$")) {
            if (date.equals(dateRequest.getStartTime().substring(0, 7))) {
                lqwOld.apply("DATE_FORMAT(create_time, '%Y-%m') < {0}", dateRequest.getStartTime());
            } else {
                lqwOld.apply("DATE_FORMAT(create_time, '%Y-%m') < {0}", date);
            }
        } else if (date.matches("^\\d{4}$")) {
            if (date.equals(dateRequest.getStartTime().substring(0, 4))) {
                lqwOld.apply("DATE_FORMAT(create_time, '%Y') < {0}", dateRequest.getStartTime());
            } else {
                lqwOld.apply("DATE_FORMAT(create_time, '%Y') < {0}", date);
            }
        }
        Integer oldUserNum = dao.selectCount(lqwOld);
        map.put("newUserNum", newUserNum);
        map.put("oldUserNum", oldUserNum);
        return map;
    }

    private MerchantUser getByUserIdAndMerId(Integer userId, Integer merId) {
        LambdaQueryWrapper<MerchantUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantUser::getUid, userId);
        lqw.eq(MerchantUser::getMerId, merId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

}

