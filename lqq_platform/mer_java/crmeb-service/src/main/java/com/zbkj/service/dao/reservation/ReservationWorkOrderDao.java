package com.zbkj.service.dao.reservation;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.reservation.ReservationWorkOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 预约工单表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2025-07-08
 */
public interface ReservationWorkOrderDao extends BaseMapper<ReservationWorkOrder> {


    List<ReservationWorkOrder> selectWorkerOrderList(Map<String, Object> map);

    List<ReservationWorkOrder> getWorkOrderListPage(Map<String, Object> map);

    Integer getWorkOrderCount(Map<String, Object> map);

    List<ReservationWorkOrder> findAwaitReceiveList(@Param(value = "merId") Integer merId, @Param(value = "keywords") String keywords);

    List<ReservationWorkOrder> findFrontList(Map<String, Object> map);

    List<ReservationWorkOrder> getOverViewData(Map<String, Object> map);
}
