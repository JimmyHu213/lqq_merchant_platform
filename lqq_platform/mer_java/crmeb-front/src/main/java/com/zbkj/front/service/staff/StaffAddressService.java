package com.zbkj.front.service.staff;

import com.zbkj.common.dto.AntiResolutionAddress;
import com.zbkj.common.dto.Location;

/**
 * 工单地址服务
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/7
 */
public interface StaffAddressService {

    /**
     * 通过坐标获取地址
     *
     * @param location 坐标对象
     */
    AntiResolutionAddress getAddressByCoordinate(Location location);
}
