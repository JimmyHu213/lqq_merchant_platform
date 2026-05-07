package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.promoter.PromoterMerchant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 推广员-商户绑定 DAO
 * [LQQ-迁移] 推广员代理模式
 */
@Mapper
public interface PromoterMerchantDao extends BaseMapper<PromoterMerchant> {
}
