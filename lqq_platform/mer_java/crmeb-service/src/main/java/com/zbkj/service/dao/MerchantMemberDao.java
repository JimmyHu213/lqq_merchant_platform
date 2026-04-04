package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.dto.MemberRegisterNumDto;
import com.zbkj.common.model.merchant.MerchantMember;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * <p>
 * 商户会员表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2025-12-01
 */
public interface MerchantMemberDao extends BaseMapper<MerchantMember> {

    Integer getMerchantMemberNum(Integer merId);

    List<MemberRegisterNumDto> findRegisterTypeNumListByMerId(@Param(value = "merId") Integer merId);
}
