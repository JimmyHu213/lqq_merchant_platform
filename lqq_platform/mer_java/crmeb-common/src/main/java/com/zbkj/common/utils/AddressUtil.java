package com.zbkj.common.utils;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.dto.AntiResolutionAddress;
import com.zbkj.common.dto.Location;
import com.zbkj.common.exception.CrmebException;

/**
 * 地址工具类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/15
 */
public class AddressUtil {

    private static final double EARTH_RADIUS = 6371000; // 地球半径（米）

    // 腾讯地址解析（地址转坐标）
    private static final String TxAddressResolutionURL = "https://apis.map.qq.com/ws/geocoder/v1/?address=";
    //逆地址解析（坐标位置描述）
    private static final String TxAddressAntiResolutionURL = "https://apis.map.qq.com/ws/geocoder/v1/?location=";

    /**
     * 腾讯地图地址解析
     *
     * @param txKey   腾讯地图key
     * @param address 要解析获取坐标及相关信息的 输入地址
     */
    public static String addressResolutionByTx(String txKey, String address) {
        // 需要对地址进行URL编码，否则若包含"#"等一些功能字符将引起错误
        String userAddress = URLUtil.encode(address);
        String url = TxAddressResolutionURL + userAddress + "&key=" + txKey;
        String result = HttpUtil.get(url);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (0 != jsonObject.getInteger("status")) {
            throw new CrmebException("腾讯地图地址解析错误，status = " + jsonObject.getInteger("status")
                    + ", message = " + jsonObject.getString("message"));
        }
        return result;
    }

    /**
     * 腾讯地图地址解析
     *
     * @param txKey   腾讯地图key
     * @param address 要解析获取坐标及相关信息的 输入地址
     */
    public static Location getLocationByTx(String txKey, String address) {
        String result = addressResolutionByTx(txKey, address);
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
        JSONObject jsonObject2 = jsonObject1.getJSONObject("location");
        // 经度
        String lng = jsonObject2.getString("lng");
        // 纬度
        String lat = jsonObject2.getString("lat");
        return new Location(lng, lat);
    }

    /**
     * 腾讯地图逆地址解析
     *
     * @param txKey     腾讯地图key
     * @param longitude 经度
     * @param latitude  纬度
     */
    public static String addressAntiResolutionByTx(String txKey, String longitude, String latitude) {
        String url = TxAddressAntiResolutionURL + latitude + "," + longitude + "&key=" + txKey;
        String result = HttpUtil.get(url);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (0 != jsonObject.getInteger("status")) {
            throw new CrmebException("腾讯地图逆地址解析错误，status = " + jsonObject.getInteger("status")
                    + ", message = " + jsonObject.getString("message"));
        }
        return result;
    }

    /**
     * 腾讯地图逆地址解析
     *
     * @param txKey     腾讯地图key
     * @param longitude 经度
     * @param latitude  纬度
     */
    public static AntiResolutionAddress getAntiAddressByTx(String txKey, String longitude, String latitude) {
        String result = addressAntiResolutionByTx(txKey, longitude, latitude);
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject resultJsonObject = jsonObject.getJSONObject("result");
        JSONObject formattedJsonObject = resultJsonObject.getJSONObject("formatted_addresses");
        String address = resultJsonObject.getString("address");
        String recommendAddress = formattedJsonObject.getString("recommend");
        String standardAddress = formattedJsonObject.getString("standard_address");
        return new AntiResolutionAddress(address, recommendAddress, standardAddress);
    }

    /**
     * 计算两个经纬度之间的直线距离（单位：米）
     * @param lat1 第一个点的纬度
     * @param lon1 第一个点的经度
     * @param lat2 第二个点的纬度
     * @param lon2 第二个点的经度
     * @return 两点间的直线距离（米）
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 将经纬度转换为弧度
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // 计算经纬度差值
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // 使用Haversine公式计算距离
        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.pow(Math.sin(deltaLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }


    public static void main(String[] args) {
//        String txKey = "";
//        String address = "";
//        String longitude = "108.77644";
//        String latitude = "34.312673";
//        String result = addressAntiResolutionByTx(txKey, longitude, latitude);
//        System.out.println(result);
        // 示例：计算北京(39.9, 116.4)到上海(31.2, 121.5)的距离
        double distance = calculateDistance(39.9, 116.4, 31.2, 121.5);
        System.out.printf("两点间距离：%.2f米", distance);
    }


}
