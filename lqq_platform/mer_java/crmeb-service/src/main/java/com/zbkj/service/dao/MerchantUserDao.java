package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.merchant.MerchantUser;
import com.zbkj.common.response.MerchantUserPageResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商户用户表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2025-12-24
 */
public interface MerchantUserDao extends BaseMapper<MerchantUser> {

    List<MerchantUserPageResponse> findUserList(Map<String, Object> map);

    Integer getMerchantUserNum(Integer merId);

    Integer getAliveUserNum(@Param("merId") Integer merId,
                            @Param("startTime")  String startTime,
                            @Param("endTime") String endTime);

    Integer getOrderUserNum(@Param("merId") Integer merId,
                            @Param("startTime")  String startTime,
                            @Param("endTime") String endTime);

    Integer getAliveUserNumByDate(@Param("merId") Integer merId,
                            @Param("date") String date,
                            @Param("startTime")  String startTime,
                            @Param("endTime") String endTime);
}
