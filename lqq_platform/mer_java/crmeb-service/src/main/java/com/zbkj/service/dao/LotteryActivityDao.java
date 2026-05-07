package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.lottery.LotteryActivity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 抽奖活动 DAO
 * [LQQ-迁移] 抽奖系统
 */
@Mapper
public interface LotteryActivityDao extends BaseMapper<LotteryActivity> {
}
