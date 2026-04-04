package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.dto.LevelMemberNumDto;
import com.zbkj.common.model.merchant.MerchantMemberLevel;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * <p>
 * 商户会员等级表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2025-12-01
 */
public interface MerchantMemberLevelDao extends BaseMapper<MerchantMemberLevel> {

    List<LevelMemberNumDto> findMerchantNumListByMerId(@Param(value = "merId") Integer merId);
}
