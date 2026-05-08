package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.order.WechatProfitSharingRecord;
import com.zbkj.service.dao.WechatProfitSharingRecordDao;
import com.zbkj.service.service.WechatProfitSharingRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * [LQQ-迁移] 微信多方分账记录 Service 实现
 */
@Service
public class WechatProfitSharingRecordServiceImpl extends ServiceImpl<WechatProfitSharingRecordDao, WechatProfitSharingRecord>
        implements WechatProfitSharingRecordService {

    @Resource
    private WechatProfitSharingRecordDao dao;

    @Override
    public List<WechatProfitSharingRecord> getByOrderNo(String orderNo) {
        LambdaQueryWrapper<WechatProfitSharingRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(WechatProfitSharingRecord::getOrderNo, orderNo);
        lqw.orderByDesc(WechatProfitSharingRecord::getCreateTime);
        return dao.selectList(lqw);
    }

    @Override
    public List<WechatProfitSharingRecord> getPendingRecords(int limit) {
        LambdaQueryWrapper<WechatProfitSharingRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(WechatProfitSharingRecord::getStatus, 0);
        lqw.orderByAsc(WechatProfitSharingRecord::getCreateTime);
        lqw.last(" limit " + limit);
        return dao.selectList(lqw);
    }

    @Override
    public List<WechatProfitSharingRecord> getByMerId(Integer merId, String date) {
        LambdaQueryWrapper<WechatProfitSharingRecord> lqw = Wrappers.lambdaQuery();
        if (merId > 0) {
            lqw.eq(WechatProfitSharingRecord::getOrderMerId, merId);
        }
        if (date != null) {
            lqw.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        }
        lqw.orderByDesc(WechatProfitSharingRecord::getCreateTime);
        return dao.selectList(lqw);
    }

    @Override
    public List<WechatProfitSharingRecord> getByOrderNoAndMerId(String orderNo, Integer merId) {
        // [LQQ-迁移] 商户数据隔离: 按订单号+商户ID查询
        LambdaQueryWrapper<WechatProfitSharingRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(WechatProfitSharingRecord::getOrderNo, orderNo);
        lqw.eq(WechatProfitSharingRecord::getOrderMerId, merId);
        lqw.orderByDesc(WechatProfitSharingRecord::getCreateTime);
        return dao.selectList(lqw);
    }

    @Override
    public List<WechatProfitSharingRecord> getUnfreezeReadyRecords(int limit) {
        // [LQQ-迁移] 查询可解冻佣金的记录: 分账成功 + 冻结期已过 + 尚未解冻
        LambdaQueryWrapper<WechatProfitSharingRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(WechatProfitSharingRecord::getStatus, 1); // 分账成功
        lqw.le(WechatProfitSharingRecord::getFrozenUntil, new Date()); // 冻结期已过
        lqw.eq(WechatProfitSharingRecord::getIsUnfrozen, 0); // 尚未解冻
        lqw.ne(WechatProfitSharingRecord::getReceiverType, "PLATFORM"); // 平台不需要解冻到用户余额
        lqw.orderByAsc(WechatProfitSharingRecord::getCreateTime);
        lqw.last(" limit " + limit);
        return dao.selectList(lqw);
    }
}
