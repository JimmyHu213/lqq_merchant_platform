package com.zbkj.service.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.dto.MemberRegisterNumDto;
import com.zbkj.common.model.merchant.MerchantMember;
import com.zbkj.common.request.DateRequest;
import com.zbkj.service.dao.MerchantMemberDao;
import com.zbkj.service.service.MerchantMemberService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author HZW
 * @description MerchantMemberServiceImpl 接口实现
 * @date 2025-12-01
 */
@Service
public class MerchantMemberServiceImpl extends ServiceImpl<MerchantMemberDao, MerchantMember> implements MerchantMemberService {

    @Resource
    private MerchantMemberDao dao;


    @Override
    public MerchantMember getByUidAndMerId(Integer userId, Integer merId) {
        LambdaQueryWrapper<MerchantMember> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantMember::getUid, userId);
        lqw.eq(MerchantMember::getMerId, merId);
        lqw.orderByDesc(MerchantMember::getId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 更新商户会员金额
     *
     * @param userId         用户ID
     * @param rechargeAmount 充值金额
     * @param giftAmount     赠送金额
     * @param type           增加add、扣减sub
     * @return Boolean
     */
    @Override
    public Boolean updateAmount(Integer userId, Integer merId, BigDecimal rechargeAmount, BigDecimal giftAmount, String type) {
        UpdateWrapper<MerchantMember> wrapper = Wrappers.update();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            wrapper.setSql(StrUtil.format("recharge_amount = recharge_amount + {}", rechargeAmount));
            wrapper.setSql(StrUtil.format("gift_amount = gift_amount + {}", giftAmount));
        } else {
            wrapper.setSql(StrUtil.format("recharge_amount = recharge_amount - {}", rechargeAmount));
            wrapper.setSql(StrUtil.format("gift_amount = gift_amount - {}", giftAmount));
        }
        wrapper.eq("uid", userId);
        wrapper.eq("mer_id", merId);
        if (type.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            wrapper.apply(StrUtil.format(" recharge_amount - {} >= 0", rechargeAmount));
        }
        return update(wrapper);
    }

    /**
     * 获取用户入会分页列表
     */
    @Override
    public List<MerchantMember> findUserAddMerchantList(Integer userId) {
        LambdaQueryWrapper<MerchantMember> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantMember::getUid, userId);
        lqw.eq(MerchantMember::getIsLogoff, 0);
        lqw.orderByDesc(MerchantMember::getId);
        return dao.selectList(lqw);
    }

    @Override
    public Integer getCountByLevel(Integer level, Integer merId) {
        LambdaQueryWrapper<MerchantMember> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantMember::getLevel, level);
        lqw.eq(MerchantMember::getMerId, merId);
        lqw.eq(MerchantMember::getIsLogoff, 0);
        return dao.selectCount(lqw);
    }

    /**
     * 清除商户会员等级
     *
     * @param level 会员等级
     */
    @Override
    public Boolean clearLevelByLevel(Integer level, Integer merId) {
        LambdaUpdateWrapper<MerchantMember> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MerchantMember::getLevel, 1);
        wrapper.eq(MerchantMember::getLevel, level);
        wrapper.eq(MerchantMember::getMerId, merId);
        wrapper.eq(MerchantMember::getIsLogoff, 0);
        return update(wrapper);
    }

    /**
     * 商户会员数量
     *
     * @param merId 商户ID
     */
    @Override
    public Integer merchantMemberNum(Integer merId) {
        return dao.getMerchantMemberNum(merId);
    }

    /**
     * 商户会员新增数量
     *
     * @param merId       商户ID
     * @param dateRequest 时间范围
     */
    @Override
    public Integer getPaidMemberNum(Integer merId, DateRequest dateRequest) {
        QueryWrapper<MerchantMember> lqw = Wrappers.query();
        lqw.select("count(DISTINCT uid) as userCount");
        lqw.eq("mer_id", merId);
        lqw.apply("DATE_FORMAT(create_time, '%Y-%m-%d') between {0} and {1}", dateRequest.getStartTime(), dateRequest.getEndTime());
        List<Map<String, Object>> result = dao.selectMaps(lqw);
        if (result != null && !result.isEmpty()) {
            Map<String, Object> map = result.get(0);
            return ((Number) map.get("userCount")).intValue();
        }
        return 0;
    }

    /**
     * 商户会员新增数量
     *
     * @param merId       商户ID
     * @param date        时间
     * @param dateRequest 时间范围
     */
    @Override
    public Integer getAddMemberNumByDate(Integer merId, String date, DateRequest dateRequest) {
        QueryWrapper<MerchantMember> wrapper = Wrappers.query();
        wrapper.select("count(DISTINCT uid) as userCount");
        wrapper.eq("mer_id", merId);
        wrapper.like("create_time", date);
        if (date.matches("^\\d{4}-\\d{2}$")) {
            if (date.equals(dateRequest.getStartTime().substring(0, 7))) {
                wrapper.ge("create_time", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 7))) {
                wrapper.le("create_time", dateRequest.getEndTime());
            }
        } else if (date.matches("^\\d{4}$")) {
            if (date.equals(dateRequest.getStartTime().substring(0, 4))) {
                wrapper.ge("create_time", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 4))) {
                wrapper.le("create_time", dateRequest.getEndTime());
            }
        }
        List<Map<String, Object>> result = dao.selectMaps(wrapper);
        if (result != null && !result.isEmpty()) {
            Map<String, Object> map = result.get(0);
            return ((Number) map.get("userCount")).intValue();
        }
        return 0;
    }

    /**
     * 获取商户会员数量
     */
    @Override
    public Integer getCountByMerId(Integer merId) {
        LambdaQueryWrapper<MerchantMember> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantMember::getMerId, merId);
        lqw.eq(MerchantMember::getIsLogoff, 0);
        return dao.selectCount(lqw);
    }

    @Override
    public Boolean logoffByUserId(Integer userId) {
        LambdaUpdateWrapper<MerchantMember> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MerchantMember::getIsLogoff, 1);
        wrapper.set(MerchantMember::getLogoffTime, DateUtil.date());
        wrapper.eq(MerchantMember::getIsLogoff, 0);
        wrapper.eq(MerchantMember::getUid, userId);
        return update(wrapper);
    }

    /**
     * 获取商户会员来源渠道数量列表
     */
    @Override
    public List<MemberRegisterNumDto> findRegisterTypeNumListByMerId(Integer merId) {
        List<MemberRegisterNumDto> numDtoList = dao.findRegisterTypeNumListByMerId(merId);
        List<String> sourceList = new ArrayList<>();
        sourceList.add("wechat");
        sourceList.add("routine");
        sourceList.add("h5");
        sourceList.add("iosWx");
        sourceList.add("androidWx");
        sourceList.add("ios");
        if (CollUtil.isEmpty(numDtoList)) {
            for (String source : sourceList) {
                MemberRegisterNumDto memberRegisterNumDto = new MemberRegisterNumDto();
                memberRegisterNumDto.setRegisterType(source);
                memberRegisterNumDto.setNum(0);
                numDtoList.add(memberRegisterNumDto);
            }
            return numDtoList;
        }
        for (String source : sourceList) {
            MemberRegisterNumDto filterDto = numDtoList.stream().filter(e -> e.getRegisterType().equals(source)).findAny().orElse(null);
            if (ObjectUtil.isNull(filterDto)) {
                MemberRegisterNumDto memberRegisterNumDto = new MemberRegisterNumDto();
                memberRegisterNumDto.setRegisterType(source);
                memberRegisterNumDto.setNum(0);
                numDtoList.add(memberRegisterNumDto);
            }
        }
        return numDtoList;
    }

    /**
     * 获取新增商户会员数量
     *
     * @param merId         商户ID
     * @param startDateTime 开始时间
     * @param endDateTime   结束时间
     */
    @Override
    public Integer getNewCountByMerIdAndDate(Integer merId, String startDateTime, String endDateTime) {
        QueryWrapper<MerchantMember> query = Wrappers.query();
        query.select("id");
        query.eq("mer_id", merId);
//        query.eq("is_logoff", 0);
        query.apply("DATE_FORMAT(create_time, '%Y-%m-%d %h:%m:%s') between {0} and {1}", startDateTime, endDateTime);
        return dao.selectCount(query);
    }
}

