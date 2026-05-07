package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.lottery.LotteryRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 抽奖参与记录 DAO
 * [LQQ-迁移] 抽奖系统
 */
@Mapper
public interface LotteryRecordDao extends BaseMapper<LotteryRecord> {
}
