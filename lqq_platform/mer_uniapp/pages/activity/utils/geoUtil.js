// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

/**
 * 地理围栏工具类
 * 提供根据经纬度判断是否在多边形范围内的功能
 */
const geoUtil = {
  
  /**
   * 判断点是否在多边形内部（射线法）
   * @param {Object} point 待判断的点 {lng: 经度, lat: 纬度}
   * @param {Array} polygon 多边形顶点数组 [{lng: 经度1, lat: 纬度1}, {lng: 经度2, lat: 纬度2}, ...]
   * @returns {Boolean} 是否在多边形内部
   */
  isPointInPolygon: function(point, polygon) {
    if (!point || !polygon || !Array.isArray(polygon) || polygon.length < 3) {
      return false;
    }
    
    let inside = false;
    const x = point.lng;
    const y = point.lat;
    
    // 射线法实现
    for (let i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
      const xi = polygon[i].lng;
      const yi = polygon[i].lat;
      const xj = polygon[j].lng;
      const yj = polygon[j].lat;
      
      // 判断点是否在多边形的边上
      if (this.isPointOnLine(point, {lng: xi, lat: yi}, {lng: xj, lat: yj})) {
        return true;
      }
      
      // 判断点是否与多边形边界相交
      const intersect = ((yi > y) !== (yj > y)) && 
                       (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
      
      if (intersect) {
        inside = !inside;
      }
    }
    
    return inside;
  },
  
  /**
   * 判断点是否在线段上
   * @param {Object} point 待判断的点
   * @param {Object} lineStart 线段起点
   * @param {Object} lineEnd 线段终点
   * @returns {Boolean} 是否在线段上
   */
  isPointOnLine: function(point, lineStart, lineEnd) {
    // 计算点到线段的距离
    const distance = this.pointToLineDistance(point, lineStart, lineEnd);
    
    // 如果距离非常小（接近0），且点在线段的包围盒内，则认为点在线段上
    return distance < 0.000001 && 
           point.lng >= Math.min(lineStart.lng, lineEnd.lng) && 
           point.lng <= Math.max(lineStart.lng, lineEnd.lng) && 
           point.lat >= Math.min(lineStart.lat, lineEnd.lat) && 
           point.lat <= Math.max(lineStart.lat, lineEnd.lat);
  },
  
  /**
   * 计算点到线段的距离
   * @param {Object} point 点
   * @param {Object} lineStart 线段起点
   * @param {Object} lineEnd 线段终点
   * @returns {Number} 距离
   */
  pointToLineDistance: function(point, lineStart, lineEnd) {
    const A = point.lng - lineStart.lng;
    const B = point.lat - lineStart.lat;
    const C = lineEnd.lng - lineStart.lng;
    const D = lineEnd.lat - lineStart.lat;
    
    const dot = A * C + B * D;
    const lenSq = C * C + D * D;
    let param = -1;
    
    if (lenSq !== 0) {
      param = dot / lenSq;
    }
    
    let xx, yy;
    
    if (param < 0) {
      xx = lineStart.lng;
      yy = lineStart.lat;
    } else if (param > 1) {
      xx = lineEnd.lng;
      yy = lineEnd.lat;
    } else {
      xx = lineStart.lng + param * C;
      yy = lineStart.lat + param * D;
    }
    
    const dx = point.lng - xx;
    const dy = point.lat - yy;
    
    // 这里返回的是欧几里得距离，在实际地理应用中可能需要转换为实际距离（如米）
    return Math.sqrt(dx * dx + dy * dy);
  },
  
  /**
   * 计算两个经纬度点之间的距离（Haversine公式）
   * @param {Object} point1 第一个点
   * @param {Object} point2 第二个点
   * @returns {Number} 两点之间的距离（单位：米）
   */
  getDistance: function(point1, point2) {
    const R = 6371000; // 地球半径（米）
    const φ1 = this.toRadians(point1.lat);
    const φ2 = this.toRadians(point2.lat);
    const Δφ = this.toRadians(point2.lat - point1.lat);
    const Δλ = this.toRadians(point2.lng - point1.lng);
    
    const a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
              Math.cos(φ1) * Math.cos(φ2) *
              Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    
    const distance = R * c; // 距离（米）
    return distance;
  },
  
  /**
   * 角度转弧度
   * @param {Number} degrees 角度
   * @returns {Number} 弧度
   */
  toRadians: function(degrees) {
    return degrees * (Math.PI / 180);
  }
};

export default geoUtil;