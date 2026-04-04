package com.zbkj.front.service.impl.staff;

import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.dto.AntiResolutionAddress;
import com.zbkj.common.dto.Location;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.AddressUtil;
import com.zbkj.front.service.staff.StaffAddressService;
import com.zbkj.service.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 工单地址服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/7
 */
@Service
public class StaffAddressServiceImpl implements StaffAddressService {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 通过坐标获取地址
     *
     * @param location 坐标对象
     */
    @Override
    public AntiResolutionAddress getAddressByCoordinate(Location location) {
        if (StrUtil.isBlank(location.getLongitude()) || StrUtil.isBlank(location.getLatitude())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "经纬度不能为空");
        }
        String txKey = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_SITE_TENG_XUN_MAP_KEY);
        return AddressUtil.getAntiAddressByTx(txKey, location.getLongitude(), location.getLatitude());
    }
}
