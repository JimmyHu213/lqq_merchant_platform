package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.lottery.LotteryActivity;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.service.dao.LotteryActivityDao;
import com.zbkj.service.service.LotteryActivityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 抽奖活动 Service 实现
 * [LQQ-迁移] 抽奖系统
 */
@Service
public class LotteryActivityServiceImpl extends ServiceImpl<LotteryActivityDao, LotteryActivity>
        implements LotteryActivityService {

    @Resource
    private LotteryActivityDao dao;

    @Override
    public PageInfo<LotteryActivity> getActiveList(PageParamRequest pageParamRequest) {
        Page<LotteryActivity> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<LotteryActivity> lqw = Wrappers.lambdaQuery();
        lqw.eq(LotteryActivity::getStatus, 1);
        lqw.eq(LotteryActivity::getIsDel, false);
        lqw.orderByDesc(LotteryActivity::getId);
        List<LotteryActivity> list = dao.selectList(lqw);
        return new PageInfo<>(list);
    }

    @Override
    public LotteryActivity getByIdException(Integer id) {
        LotteryActivity activity = dao.selectById(id);
        if (ObjectUtil.isNull(activity) || activity.getIsDel()) {
            throw new CrmebException("抽奖活动不存在");
        }
        return activity;
    }

    @Override
    public List<LotteryActivity> getAllActiveActivities() {
        LambdaQueryWrapper<LotteryActivity> lqw = Wrappers.lambdaQuery();
        lqw.eq(LotteryActivity::getStatus, 1);
        lqw.eq(LotteryActivity::getIsDel, false);
        return dao.selectList(lqw);
    }
}
