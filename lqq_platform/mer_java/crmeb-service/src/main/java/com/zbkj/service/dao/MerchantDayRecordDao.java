package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.record.MerchantDayRecord;

import java.util.List;

/**
 * <p>
 * 商户日记录表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-11-28
 */
public interface MerchantDayRecordDao extends BaseMapper<MerchantDayRecord> {

    Integer getVisitorsByMerIds(List<Integer> merIds, String date);
}
