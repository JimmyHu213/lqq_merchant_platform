// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

// #ifdef APP-PLUS
import permision from "./permission.js"
// #endif

/**
 * 全局定位权限管理和数据缓存
 */
class LocationManager {
  constructor() {
    this.locationData = null;
    this.hasLocationPermission = false;
    this.permissionStatus = 0; // 0: 未检查, 1: 已允许, 2: 拒绝, 3: 系统未开启
    this.isChecking = false;
    this.listeners = [];
  }

  /**
   * 初始化定位管理器
   */
  init() {
    // 从缓存中恢复定位数据
    this.restoreLocationData();
    // 检查权限状态
    this.checkInitialPermission();
  }

  /**
   * 从缓存中恢复定位数据
   */
  restoreLocationData() {
    try {
      // 优先使用新的对象缓存格式
      const cachedLocationInfo = uni.getStorageSync('location_info');
      if (cachedLocationInfo && typeof cachedLocationInfo === 'object') {
        this.locationData = {
          latitude: parseFloat(cachedLocationInfo.latitude),
          longitude: parseFloat(cachedLocationInfo.longitude),
          name: cachedLocationInfo.name || '',
          address: cachedLocationInfo.address || '',
          updateTime: cachedLocationInfo.updateTime || 0,
          source: cachedLocationInfo.source || '', // 恢复数据来源标识
          type: cachedLocationInfo.type || '' // 恢复位置类型
        };
        this.hasLocationPermission = cachedLocationInfo.hasPermission || false;
        this.permissionStatus = cachedLocationInfo.hasPermission ? 1 : 0;
        return;
      }
      
      // 兼容旧版本的独立缓存项
      const latitude = uni.getStorageSync('user_latitude');
      const longitude = uni.getStorageSync('user_longitude');
      const locationName = uni.getStorageSync('user_location_name');
      const locationAddress = uni.getStorageSync('user_location_address');
      const updateTime = uni.getStorageSync('location_update_time');
      const hasPermission = uni.getStorageSync('has_location_permission');
      
      if (latitude && longitude) {
        this.locationData = {
          latitude: parseFloat(latitude),
          longitude: parseFloat(longitude),
          name: locationName || '',
          address: locationAddress || '',
          updateTime: updateTime || 0
        };
        this.hasLocationPermission = !!hasPermission;
        this.permissionStatus = hasPermission ? 1 : 0;
        
        // 迁移到新格式并清除旧缓存
        this.migrateToNewFormat();
      }
    } catch (error) {
    }
  }

  /**
   * 迁移旧格式缓存到新格式
   */
  migrateToNewFormat() {
    try {
      
      // 保存到新格式
      this.saveLocationDataToCache();
      
      // 清除旧格式缓存
      uni.removeStorageSync('user_latitude');
      uni.removeStorageSync('user_longitude');
      uni.removeStorageSync('user_location_name');
      uni.removeStorageSync('user_location_address');
      uni.removeStorageSync('location_update_time');
      uni.removeStorageSync('has_location_permission');
      
    } catch (error) {
    }
  }

  /**
   * 保存位置数据到缓存（使用对象格式）
   */
  saveLocationDataToCache() {
    try {
      const locationInfo = {
        latitude: this.locationData?.latitude || 0,
        longitude: this.locationData?.longitude || 0,
        name: this.locationData?.name || '',
        address: this.locationData?.address || '',
        updateTime: this.locationData?.updateTime || Date.now(),
        hasPermission: this.hasLocationPermission,
        permissionStatus: this.permissionStatus,
        source: this.locationData?.source || '', // 保存数据来源标识
        type: this.locationData?.type || '', // 保存位置类型
        version: '2.0' // 版本标识
      };
      
      uni.setStorageSync('location_info', locationInfo);
    } catch (error) {
    }
  }

  /**
   * 检查初始权限状态
   */
  async checkInitialPermission() {
    if (this.isChecking) return this.permissionStatus;
    
    this.isChecking = true;
    
    try {
      let status = 0;
      
      // #ifdef APP-PLUS
      status = permision.isIOS ? 
        await permision.requestIOS('location') : 
        await permision.requestAndroid('android.permission.ACCESS_FINE_LOCATION');
      // #endif
      
      // #ifdef MP
      status = await this.getSetting();
      // #endif
      
      // #ifdef H5
      // H5环境下通过尝试获取定位来判断权限
      try {
        await this.testLocationAccess();
        status = 1;
      } catch (error) {
        status = 2;
      }
      // #endif
      
      this.permissionStatus = status;
      this.hasLocationPermission = status === 1;
      
    } catch (error) {
      this.permissionStatus = 2;
      this.hasLocationPermission = false;
    } finally {
      this.isChecking = false;
    }
    
    return this.permissionStatus;
  }

  /**
   * 测试定位访问权限（H5环境）
   */
  testLocationAccess() {
    return new Promise((resolve, reject) => {
      // #ifdef H5
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position) => resolve(position),
          (error) => {
            reject(error);
          },
          { timeout: 10000, enableHighAccuracy: false, maximumAge: 60000 }
        );
      } else {
        reject(new Error('Geolocation not supported'));
      }
      // #endif
      
      // #ifndef H5
      // 非H5环境直接resolve，由其他逻辑处理
      resolve(true);
      // #endif
    });
  }

  /**
   * 获取小程序定位权限设置
   */
  getSetting() {
    return new Promise((resolve) => {
      // #ifdef MP
      uni.getSetting({
        success: (res) => {
          if (res.authSetting['scope.userLocation'] === undefined) {
            resolve(0); // 未询问
          } else if (res.authSetting['scope.userLocation']) {
            resolve(1); // 已允许
          } else {
            resolve(2); // 已拒绝
          }
        },
        fail: () => resolve(2)
      });
      // #endif
      
      // #ifndef MP
      resolve(0);
      // #endif
    });
  }

  /**
   * 实时检查系统权限状态（不依赖缓存）
   */
  async checkRealTimePermission() {
    
    try {
      let status = 0;
      
      // #ifdef APP-PLUS
      status = permision.isIOS ? 
        await permision.requestIOS('location') : 
        await permision.requestAndroid('android.permission.ACCESS_FINE_LOCATION');
      // #endif
      
      // #ifdef MP
      status = await this.getSetting();
      // #endif
      
      // #ifdef H5
      // H5环境下通过实际尝试获取定位来检查权限
      try {
        await this.testLocationAccess();
        status = 1;
      } catch (error) {
        status = 2;
      }
      // #endif
      
      return status;
      
    } catch (error) {
      return 2; // 检查失败视为无权限
    }
  }

  /**
   * 请求定位权限并获取位置信息
   */
  async requestLocationPermission(showTip = true) {
    if (this.isChecking) return false;
    
    try {
      const permissionStatus = await this.checkInitialPermission();
      
      if (permissionStatus === 1) {
        // 权限已获得，尝试获取定位
        try {
          const locationData = await this.getCurrentLocation();
          if (locationData) {
            await this.saveLocationData(locationData);
            this.notifyListeners('permission_granted', locationData);
            return true;
          }
        } catch (locationError) {
          if (showTip) {
            this.showLocationErrorDialog(locationError.userMessage);
          }
          return false;
        }
      } else if (permissionStatus === 2) {
        // 权限被拒绝
        if (showTip) {
          this.showLocationPermissionDeniedDialog();
        }
        this.notifyListeners('permission_denied');
        return false;
      } else if (permissionStatus === 0) {
        // 未询问权限，尝试获取定位
        try {
          const locationData = await this.getCurrentLocation();
          if (locationData) {
            await this.saveLocationData(locationData);
            this.permissionStatus = 1;
            this.hasLocationPermission = true;
            this.notifyListeners('permission_granted', locationData);
            return true;
          }
        } catch (locationError) {
          // 首次获取失败可能是用户拒绝，更新状态
          this.permissionStatus = 2;
          this.hasLocationPermission = false;
          if (showTip) {
            this.showLocationPermissionDeniedDialog();
          }
          return false;
        }
      }
    } catch (error) {
      if (showTip) {
        this.showLocationErrorDialog();
      }
      this.notifyListeners('permission_error', error);
    }
    
    return false;
  }

  /**
   * 获取当前位置信息
   */
  getCurrentLocation() {
    return new Promise((resolve, reject) => {
      // 小程序定位参数优化
      const locationOptions = {
        // #ifdef MP
        type: 'wgs84',        // 小程序推荐使用wgs84，兼容性更好
        isHighAccuracy: false, // 降低精度要求，提高成功率
        timeout: 15000,       // 设置超时时间15秒
        // #endif
        
        // #ifdef H5 || APP-PLUS
        type: 'gcj02',
        isHighAccuracy: true,
        timeout: 10000,
        // #endif
      };
      
      // 开始获取位置
      
      uni.getLocation({
        ...locationOptions,
        success: (res) => {
          const locationData = {
            latitude: res.latitude,
            longitude: res.longitude,
            accuracy: res.accuracy,
            altitude: res.altitude,
            speed: res.speed,
            updateTime: Date.now()
          };
          resolve(locationData);
        },
        fail: (error) => {
          // 获取定位失败
          
          // 根据具体错误提供更详细的信息
          let errorMessage = '获取位置失败';
          if (error.errMsg) {
            if (error.errMsg.includes('timeout')) {
              errorMessage = '定位超时，请检查网络连接和GPS设置';
            } else if (error.errMsg.includes('system permission denied')) {
              errorMessage = '系统定位权限被拒绝，请在系统设置中开启定位服务';
            } else if (error.errMsg.includes('location service unavailable')) {
              errorMessage = '定位服务不可用，请确保GPS已开启';
            } else if (error.errMsg.includes('network')) {
              errorMessage = '网络定位失败，请检查网络连接';
            }
          }
          
          error.userMessage = errorMessage;
          reject(error);
        }
      });
    });
  }

  /**
   * 保存定位数据到缓存
   * @param {Object} locationData 位置数据
   * @param {String} locationName 位置名称
   * @param {String} locationAddress 位置地址
   * @param {Boolean} skipPermissionCheck 是否跳过权限检查（用于地图选择场景）
   */
  async saveLocationData(locationData, locationName = '', locationAddress = '', skipPermissionCheck = false) {
    
    let canSave = false;
    
    if (skipPermissionCheck) {
      // 地图选择场景，跳过权限检查
      canSave = true;
    } else {
      // GPS定位场景，需要检查权限
      const realTimePermissionStatus = await this.checkRealTimePermission();
      canSave = realTimePermissionStatus === 1;
    }
    
    if (canSave) {
      this.locationData = {
        ...locationData,
        name: locationName,
        address: locationAddress,
        source: skipPermissionCheck ? 'manual_select' : 'gps', // 根据场景设置数据来源
        type: skipPermissionCheck ? 'business' : 'gps' // 根据场景设置位置类型
      };
      
      // 设置位置选择成功标识
      this.hasLocationPermission = true;
      this.permissionStatus = 1;
      
      // 使用新的对象格式保存到缓存
      this.saveLocationDataToCache();
      
    } else {
      // 清除可能过期的权限标识和位置数据
      this.hasLocationPermission = false;
      this.permissionStatus = 2;
      this.clearLocationData();
    }
  }

  /**
   * 清除定位数据
   */
  clearLocationData() {
    
    this.locationData = null;
    this.hasLocationPermission = false;
    this.permissionStatus = 2;
    
    // 清除新格式缓存
    uni.removeStorageSync('location_info');
    
    // 兼容性：清除可能存在的旧格式缓存
    uni.removeStorageSync('user_latitude');
    uni.removeStorageSync('user_longitude');
    uni.removeStorageSync('user_location_name');
    uni.removeStorageSync('user_location_address');
    uni.removeStorageSync('location_update_time');
    uni.removeStorageSync('has_location_permission');
    
  }

  /**
   * 清理历史圈层缓存（小程序首次启动时使用）
   */
  clearHistoricalAreaCache() {
    try {
      // 清除areas_info缓存
      uni.removeStorageSync('areas_info');
    } catch (error) {
      console.warn('清理历史圈层缓存失败:', error);
    }
  }

  /**
   * 检查是否有定位权限（实时检查）
   */
  async hasPermission() {
    
    // 检查是否已有用户选择的地址，如果有就不要覆盖
    const cachedLocationInfo = uni.getStorageSync('location_info');
    if (cachedLocationInfo && (cachedLocationInfo.source === 'manual_select' || cachedLocationInfo.type === 'business')) {
      // 直接返回权限状态，不触发GPS定位
      this.permissionStatus = 1;
      this.hasLocationPermission = true;
      return true;
    }
    
    // 进行实时权限检查而不是依赖缓存
    const realTimeStatus = await this.checkRealTimePermission();
    
    // 更新内部状态
    this.permissionStatus = realTimeStatus;
    this.hasLocationPermission = realTimeStatus === 1;
    
    // 如果权限被撤销，清除相关缓存
    if (realTimeStatus !== 1) {
      this.clearLocationData();
    } else {
      // 权限有效，更新缓存标识（但不覆盖位置数据）
      if (!cachedLocationInfo) {
        this.saveLocationDataToCache();
      }
    }
    
    return this.hasLocationPermission;
  }

  /**
   * 获取缓存的定位数据（实时从缓存读取）
   */
  getLocationData() {
    // 实时从缓存读取最新数据，而不是返回内存中的旧数据
    const cachedLocationInfo = uni.getStorageSync('location_info');
    if (cachedLocationInfo && typeof cachedLocationInfo === 'object') {
      return {
        latitude: parseFloat(cachedLocationInfo.latitude),
        longitude: parseFloat(cachedLocationInfo.longitude),
        name: cachedLocationInfo.name || '',
        address: cachedLocationInfo.address || '',
        updateTime: cachedLocationInfo.updateTime || 0,
        source: cachedLocationInfo.source || '',
        type: cachedLocationInfo.type || ''
      };
    }
    
    // 如果没有缓存，返回内存中的数据（兼容旧逻辑）
    return this.locationData;
  }

  /**
   * 获取完整的位置信息（包括缓存状态）
   */
  getFullLocationInfo() {
    const cachedLocationInfo = uni.getStorageSync('location_info');
    return {
      locationData: this.locationData,
      hasPermission: this.hasLocationPermission,
      permissionStatus: this.permissionStatus,
      isExpired: this.isLocationDataExpired(),
      cachedInfo: cachedLocationInfo,
      cacheExists: !!cachedLocationInfo
    };
  }

  /**
   * 检查缓存是否存在且有效
   */
  isCacheValid() {
    const cachedLocationInfo = uni.getStorageSync('location_info');
    if (!cachedLocationInfo || typeof cachedLocationInfo !== 'object') {
      return false;
    }
    
    // 检查必要字段
    if (!cachedLocationInfo.latitude || !cachedLocationInfo.longitude) {
      return false;
    }
    
    // 检查是否过期
    if (this.isLocationDataExpired()) {
      return false;
    }
    
    return true;
  }


  /**
   * 判断定位数据是否过期（默认30分钟）
   */
  isLocationDataExpired(maxAge = 30 * 60 * 1000) {
    if (!this.locationData || !this.locationData.updateTime) {
      return true;
    }
    
    return (Date.now() - this.locationData.updateTime) > maxAge;
  }

  /**
   * 显示定位权限被拒绝的对话框
   */
  showLocationPermissionDeniedDialog() {
    // #ifdef MP
    // 小程序：提供详细的权限开启步骤
    uni.showModal({
      title: '需要位置权限',
      content: '为了为您推荐附近商圈，需要获取位置权限。\n\n您可以：\n• 点击"去设置"在小程序设置中开启\n• 或重新进入页面时选择"允许"',
      showCancel: true,
      cancelText: '稍后再说', 
      confirmText: '去设置',
      confirmColor: '#f55850',
      success: (res) => {
        if (res.confirm) {
          this.openLocationSetting();
        }
      }
    });
    // #endif
    
    // #ifdef H5
    // H5：浏览器权限引导
    uni.showModal({
      title: '需要位置权限',
      content: '为了为您推荐附近商圈，需要获取位置权限。\n\n请在浏览器中：\n• 点击地址栏左侧的位置图标\n• 选择"始终允许"\n• 刷新页面重试',
      showCancel: true,
      cancelText: '知道了',
      confirmText: '我已设置',
      confirmColor: '#f55850',
      success: (res) => {
        if (res.confirm) {
          // H5 中重新尝试获取权限
          location.reload();
        }
      }
    });
    // #endif
    
    // #ifdef APP-PLUS
    // APP：系统权限引导
    uni.showModal({
      title: '需要位置权限',
      content: '为了为您推荐附近商圈，需要获取位置权限。\n\n请在系统设置中：\n• 找到本应用\n• 开启"位置信息"权限\n• 返回应用重试',
      showCancel: true,
      cancelText: '稍后再说',
      confirmText: '去设置',
      confirmColor: '#f55850',
      success: (res) => {
        if (res.confirm) {
          this.openLocationSetting();
        }
      }
    });
    // #endif
  }

  /**
   * 显示定位错误对话框
   */
  showLocationErrorDialog(customMessage) {
    if (customMessage) {
      // 使用自定义错误消息
      uni.showModal({
        title: '定位失败',
        content: customMessage,
        showCancel: true,
        cancelText: '稍后再试',
        confirmText: '重新定位',
        confirmColor: '#f55850',
        success: (res) => {
          if (res.confirm) {
            this.requestLocationPermission(false);
          }
        }
      });
      return;
    }
    
    // #ifdef MP
    // 小程序：针对性的问题排查
    uni.showModal({
      title: '定位失败',
      content: '获取位置信息失败，请检查：\n\n• 手机GPS定位是否开启\n• 网络连接是否正常\n• 微信位置权限是否已授权\n\n建议重新进入页面再试',
      showCancel: true,
      cancelText: '稍后再试',
      confirmText: '重新定位',
      confirmColor: '#f55850',
      success: (res) => {
        if (res.confirm) {
          this.requestLocationPermission(false);
        }
      }
    });
    // #endif
    
    // #ifdef H5
    // H5：浏览器特有问题
    uni.showModal({
      title: '定位失败',
      content: '获取位置信息失败，请检查：\n\n• 浏览器位置权限是否已开启\n• 网络连接是否正常\n• 是否允许HTTPS访问位置\n\n建议刷新页面重试',
      showCancel: true,
      cancelText: '稍后再试',
      confirmText: '刷新重试',
      confirmColor: '#f55850',
      success: (res) => {
        if (res.confirm) {
          location.reload();
        }
      }
    });
    // #endif
    
    // #ifdef APP-PLUS
    // APP：系统级问题
    uni.showModal({
      title: '定位失败',
      content: '获取位置信息失败，请检查：\n\n• 系统位置服务是否开启\n• 应用位置权限是否已授权\n• 网络连接是否正常\n\n建议检查系统设置后重试',
      showCancel: true,
      cancelText: '稍后再试',
      confirmText: '重新定位',
      confirmColor: '#f55850',
      success: (res) => {
        if (res.confirm) {
          this.requestLocationPermission(false);
        }
      }
    });
    // #endif
  }

  /**
   * 打开定位设置
   */
  openLocationSetting() {
    // #ifdef APP-PLUS
    if (permision) {
      permision.gotoAppSetting();
    }
    // #endif
    
    // #ifdef MP
    uni.openSetting({
      success: (res) => {
        if (res.authSetting && res.authSetting['scope.userLocation']) {
          this.requestLocationPermission(false);
        }
      }
    });
    // #endif
    
    // #ifdef H5
    uni.showToast({
      title: '请在浏览器设置中允许位置访问',
      icon: 'none',
      duration: 3000
    });
    // #endif
  }

  /**
   * 决定跳转路径
   */
  getLocationPagePath() {
    if (this.hasLocationPermission && this.locationData) {
      return '/pages/circle/select';
    } else {
      return '/pages/circle/index';
    }
  }

  /**
   * 智能跳转到定位页面
   */
  navigateToLocationPage(params = {}) {
    const path = this.getLocationPagePath();
    const url = Object.keys(params).length > 0 ? 
      `${path}?${Object.keys(params).map(key => `${key}=${encodeURIComponent(params[key])}`).join('&')}` : 
      path;
    
    uni.navigateTo({
      url: url,
      fail: () => {
        // 如果跳转失败，尝试重定向
        uni.redirectTo({ url: url });
      }
    });
  }

  /**
   * 添加监听器
   */
  addListener(callback) {
    this.listeners.push(callback);
  }

  /**
   * 移除监听器
   */
  removeListener(callback) {
    const index = this.listeners.indexOf(callback);
    if (index > -1) {
      this.listeners.splice(index, 1);
    }
  }

  /**
   * 通知所有监听器
   */
  notifyListeners(event, data) {
    this.listeners.forEach(callback => {
      try {
        callback(event, data);
      } catch (error) {
      }
    });
  }

  /**
   * 刷新定位数据 - 改为使用地图选择
   */
  async refreshLocationData(showLoading = true) {
    
    return new Promise((resolve, reject) => {
      // 使用当前位置作为地图中心点（如果有的话）
      const chooseLocationOptions = {};
      if (this.locationData) {
        chooseLocationOptions.latitude = this.locationData.latitude;
        chooseLocationOptions.longitude = this.locationData.longitude;
      }
      
      // 直接打开系统地图选择位置
      uni.chooseLocation({
        ...chooseLocationOptions,
        success: async (res) => {
          
          try {
            // 保存用户选择的位置（跳过权限检查）
            await this.saveLocationData({
              latitude: res.latitude,
              longitude: res.longitude,
              updateTime: Date.now()
            }, res.name, res.address, true);
            
            if (showLoading) {
              uni.showToast({
                title: '位置已更新',
                icon: 'success'
              });
            }
            
            resolve(this.locationData);
          } catch (error) {
            if (showLoading) {
              uni.showToast({
                title: '保存位置失败',
                icon: 'none'
              });
            }
            reject(error);
          }
        },
        fail: (error) => {
          
          // 如果不是用户取消，显示错误提示
          if (showLoading && error.errMsg && !error.errMsg.includes('cancel')) {
            uni.showToast({
              title: '位置选择失败',
              icon: 'none'
            });
          }
          
          // 用户取消也算是一种处理结果，返回null而不是reject
          resolve(null);
        }
      });
    });
  }
}

// 创建全局单例
const locationManager = new LocationManager();

export default locationManager;