package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.BrokerageRecordConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.promoter.PromoterMerchant;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBrokerageRecord;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.PromoterCommissionStatsResponse;
import com.zbkj.service.dao.PromoterMerchantDao;
import com.zbkj.service.service.PromoterMerchantService;
import com.zbkj.service.service.UserBrokerageRecordService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 推广员-商户绑定 Service 实现
 * [LQQ-迁移] 推广员代理模式
 * 流程: 商户邀请 → 平台审核 → 商户管理
 */
@Service
public class PromoterMerchantServiceImpl extends ServiceImpl<PromoterMerchantDao, PromoterMerchant>
        implements PromoterMerchantService {

    @Resource
    private PromoterMerchantDao dao;

    @Autowired
    private UserService userService;
    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    // ===== 商户端 =====

    @Override
    public Boolean invitePromoter(Integer uid, Integer merId, BigDecimal commissionRate) {
        // 验证推广员存在
        User user = userService.getById(uid);
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("推广员用户不存在");
        }

        // 单代理检查：该商户是否已有代理
        PromoterMerchant existing = getByMerId(merId);
        if (ObjectUtil.isNotNull(existing)) {
            throw new CrmebException("该商户已有代理推广员，请先解除当前代理");
        }

        // 检查是否有待审核的申请
        Integer pendingCount = dao.selectCount(Wrappers.<PromoterMerchant>lambdaQuery()
                .eq(PromoterMerchant::getMerId, merId)
                .eq(PromoterMerchant::getAuditStatus, 0)
                .eq(PromoterMerchant::getIsDel, false));
        if (pendingCount > 0) {
            throw new CrmebException("已有待审核的邀请申请");
        }

        // [LQQ-迁移] 检查是否有已软删除的记录（UNIQUE INDEX uk_mer_id 冲突）
        // 如果存在已删除的记录，复用该记录而非插入新记录
        PromoterMerchant deletedRecord = dao.selectOne(Wrappers.<PromoterMerchant>lambdaQuery()
                .eq(PromoterMerchant::getMerId, merId)
                .eq(PromoterMerchant::getIsDel, true));
        if (ObjectUtil.isNotNull(deletedRecord)) {
            deletedRecord.setUid(uid);
            deletedRecord.setCommissionRate(commissionRate);
            deletedRecord.setStatus(0);
            deletedRecord.setAuditStatus(0);
            deletedRecord.setAuditReason(null);
            deletedRecord.setIsDel(false);
            deletedRecord.setUpdateTime(DateUtil.date());
            return updateById(deletedRecord);
        }

        PromoterMerchant record = new PromoterMerchant();
        record.setUid(uid);
        record.setMerId(merId);
        record.setCommissionRate(commissionRate);
        record.setStatus(0); // 默认停用，审核通过后启用
        record.setAuditStatus(0); // 待审核
        record.setIsDel(false);
        record.setCreateTime(DateUtil.date());
        record.setUpdateTime(DateUtil.date());
        return save(record);
    }

    @Override
    public Boolean dismissPromoter(Integer merId) {
        PromoterMerchant record = getByMerId(merId);
        if (ObjectUtil.isNull(record)) {
            throw new CrmebException("该商户无代理推广员");
        }
        record.setIsDel(true);
        record.setStatus(0);
        record.setUpdateTime(DateUtil.date());
        return updateById(record);
    }

    @Override
    public Boolean updateCommissionRate(Integer merId, BigDecimal commissionRate) {
        PromoterMerchant record = getByMerId(merId);
        if (ObjectUtil.isNull(record)) {
            throw new CrmebException("该商户无代理推广员");
        }
        record.setCommissionRate(commissionRate);
        record.setUpdateTime(DateUtil.date());
        return updateById(record);
    }

    @Override
    public PromoterMerchant getByMerId(Integer merId) {
        LambdaQueryWrapper<PromoterMerchant> lqw = Wrappers.lambdaQuery();
        lqw.eq(PromoterMerchant::getMerId, merId);
        lqw.eq(PromoterMerchant::getAuditStatus, 1); // 审核通过的
        lqw.eq(PromoterMerchant::getStatus, 1);
        lqw.eq(PromoterMerchant::getIsDel, false);
        return dao.selectOne(lqw);
    }

    // ===== 平台端 =====

    @Override
    public Boolean auditBinding(Integer id, Integer auditStatus, String reason) {
        PromoterMerchant record = dao.selectById(id);
        if (ObjectUtil.isNull(record) || record.getIsDel()) {
            throw new CrmebException("绑定记录不存在");
        }
        if (!record.getAuditStatus().equals(0)) {
            throw new CrmebException("该申请不在待审核状态");
        }
        record.setAuditStatus(auditStatus);
        if (auditStatus.equals(1)) {
            // 审核通过，自动启用
            record.setStatus(1);
        }
        if (auditStatus.equals(2)) {
            record.setAuditReason(reason);
        }
        record.setUpdateTime(DateUtil.date());
        return updateById(record);
    }

    @Override
    public Boolean forceUnbind(Integer id) {
        PromoterMerchant record = dao.selectById(id);
        if (ObjectUtil.isNull(record) || record.getIsDel()) {
            throw new CrmebException("绑定记录不存在");
        }
        record.setIsDel(true);
        record.setStatus(0);
        record.setUpdateTime(DateUtil.date());
        return updateById(record);
    }

    @Override
    public PageInfo<PromoterMerchant> getPlatformList(PageParamRequest pageParamRequest) {
        Page<PromoterMerchant> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<PromoterMerchant> lqw = Wrappers.lambdaQuery();
        lqw.eq(PromoterMerchant::getIsDel, false);
        lqw.orderByDesc(PromoterMerchant::getCreateTime);
        List<PromoterMerchant> list = dao.selectList(lqw);
        return new PageInfo<>(list);
    }

    // ===== 用户端 =====

    @Override
    public List<PromoterMerchant> getByUid(Integer uid) {
        LambdaQueryWrapper<PromoterMerchant> lqw = Wrappers.lambdaQuery();
        lqw.eq(PromoterMerchant::getUid, uid);
        lqw.eq(PromoterMerchant::getAuditStatus, 1);
        lqw.eq(PromoterMerchant::getIsDel, false);
        lqw.orderByDesc(PromoterMerchant::getCreateTime);
        return dao.selectList(lqw);
    }

    @Override
    public PromoterCommissionStatsResponse getCommissionStats(Integer uid) {
        PromoterCommissionStatsResponse response = new PromoterCommissionStatsResponse();

        BigDecimal agentTotal = sumBrokerageByLevel(uid, 3);
        BigDecimal referral1 = sumBrokerageByLevel(uid, 1);
        BigDecimal referral2 = sumBrokerageByLevel(uid, 2);
        BigDecimal referralTotal = referral1.add(referral2);

        response.setAgentTotal(agentTotal);
        response.setReferralTotal(referralTotal);
        response.setTotalCommission(agentTotal.add(referralTotal));

        Integer merchantCount = dao.selectCount(Wrappers.<PromoterMerchant>lambdaQuery()
                .eq(PromoterMerchant::getUid, uid)
                .eq(PromoterMerchant::getAuditStatus, 1)
                .eq(PromoterMerchant::getStatus, 1)
                .eq(PromoterMerchant::getIsDel, false));
        response.setMerchantCount(merchantCount);

        return response;
    }

    private BigDecimal sumBrokerageByLevel(Integer uid, Integer level) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getBrokerageLevel, level);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        lqw.select(UserBrokerageRecord::getPrice);
        List<UserBrokerageRecord> list = userBrokerageRecordService.list(lqw);
        return list.stream()
                .map(UserBrokerageRecord::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
