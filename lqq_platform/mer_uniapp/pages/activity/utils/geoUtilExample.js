// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------



import geoUtil from "./geoUtil";

/**
 * 地理围栏工具使用示例
 * 此文件展示如何使用geoUtil中的方法判断点是否在多边形电子围栏内
 */

export const geoFenceExample = {
  
  /**
   * 示例1：基本的围栏判断
   */
  basicFenceCheck: function() {
    // 定义一个多边形电子围栏（例如：一个矩形区域）
    const polygonFence = [
      { lng: 116.395, lat: 39.910 },  // 点1
      { lng: 116.405, lat: 39.910 },  // 点2
      { lng: 116.405, lat: 39.900 },  // 点3
      { lng: 116.395, lat: 39.900 }   // 点4
    ];
    
    // 定义一个测试点（在围栏内）
    const pointInside = { lng: 116.400, lat: 39.905 };
    
    // 定义一个测试点（在围栏外）
    const pointOutside = { lng: 116.410, lat: 39.905 };
    
    // 判断点是否在围栏内  应返回 true false
    const isInside1 = geoUtil.isPointInPolygon(pointInside, polygonFence);
    const isInside2 = geoUtil.isPointInPolygon(pointOutside, polygonFence);
    
    return {
      isInside1,
      isInside2
    };
  },
  
  /**
   * 示例2：实际应用场景 - 多围栏管理
   * @param {Object} currentLocation 用户当前位置 {lng: 经度, lat: 纬度}
   * serviceAreaRange 为多个电子围栏数组
   */
  checkMultipleFences: function(currentLocation,serviceAreaRange) {

    // 检查用户是否在任何一个围栏内
    const results = [];
    serviceAreaRange.forEach(fence => {
      const isInside = geoUtil.isPointInPolygon(currentLocation, fence);
      results.push({
        isInside
      });
    });
    // 找出用户所在的围栏
    const userFences = results.filter(result => result.isInside);
    // 如果数组中有任何一个isInside为true，就返回true
    return userFences.length > 0;
  },
  
  /**
   * 示例3：距离计算和围栏判断结合使用
   * @param {Object} point 点
   * @param {Array} polygon 多边形
   */
  combineDistanceAndFenceCheck: function(point, polygon) {
    // 首先检查点是否在围栏内
    const isInside = geoUtil.isPointInPolygon(point, polygon);
    
    if (isInside) {
      console.log('点在围栏内');
      return { isInside };
    }
    
    // 如果点不在围栏内，计算点到围栏各边的最小距离
    let minDistance = Infinity;
    
    for (let i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
      const distance = geoUtil.pointToLineDistance(point, polygon[i], polygon[j]);
      if (distance < minDistance) {
        minDistance = distance;
      }
    }
    console.log('点不在围栏内，到围栏的最小距离:', minDistance);
    return {
      isInside,
      minDistance
    };
  }
};

export default geoFenceExample;