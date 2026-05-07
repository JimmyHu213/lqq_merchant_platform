package com.zbkj.common.utils;

import cn.hutool.core.util.StrUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 距离计算工具类
 * [LQQ-迁移] LBS 附近商铺搜索
 */
public class DistanceUtil {

    /** 地球平均半径(km) */
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * 使用 Haversine 公式计算两点间的球面距离
     * @param lat1 纬度1
     * @param lon1 经度1
     * @param lat2 纬度2
     * @param lon2 经度2
     * @return 距离(km)，保留2位小数；坐标无效时返回 null
     */
    public static Double calculateDistance(String lat1, String lon1, String lat2, String lon2) {
        if (StrUtil.isBlank(lat1) || StrUtil.isBlank(lon1) || StrUtil.isBlank(lat2) || StrUtil.isBlank(lon2)) {
            return null;
        }
        try {
            double la1 = Math.toRadians(Double.parseDouble(lat1));
            double lo1 = Math.toRadians(Double.parseDouble(lon1));
            double la2 = Math.toRadians(Double.parseDouble(lat2));
            double lo2 = Math.toRadians(Double.parseDouble(lon2));

            double dLat = la2 - la1;
            double dLon = lo2 - lo1;

            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                     + Math.cos(la1) * Math.cos(la2)
                     * Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = EARTH_RADIUS_KM * c;

            return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP).doubleValue();
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
