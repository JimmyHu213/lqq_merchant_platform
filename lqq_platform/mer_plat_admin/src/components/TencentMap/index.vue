<template>
  <div class="tencent-map-wrapper">
    <!-- API Key 显示区域 -->
    <div v-if="showApiKey" class="api-key-display">
      <div class="api-key-content">
        <span class="api-key-label">当前腾讯地图API Key:</span>
        <code class="api-key-value">{{ getCurrentApiKey() }}</code>
        <el-button 
          type="text" 
          size="mini" 
          @click="copyApiKey"
          class="copy-btn"
        >
          复制
        </el-button>
      </div>
      <div class="api-key-tip">
        腾讯地图API Key已通过登录后自动获取，如需修改请联系系统管理员
      </div>
    </div>
    
    <!-- 地址搜索区域 -->
    <div v-if="showSearchButton" class="search-container">
      <el-input
        v-model="searchKeyword"
        placeholder="请输入地址关键词搜索"
        @keyup.enter.native="searchAddress"
        @blur="hideSearchResults"
        @focus="showSearchResultsIfExists"
        clearable
        size="medium"
      >
        <el-button slot="append" @click="searchAddress" :loading="searchLoading" icon="el-icon-search">搜索</el-button>
      </el-input>
      
      <!-- 搜索结果列表 -->
       <div v-if="searchResults.length > 0 && showSearchResults" class="search-results">
         <div class="search-results-header">
           <span class="search-results-title">搜索结果</span>
           <el-button 
             type="text" 
             size="mini" 
             @click="closeSearchResults"
             class="close-btn"
             icon="el-icon-close"
           >
           </el-button>
         </div>
         <div 
           v-for="(result, index) in searchResults" 
           :key="index"
           class="search-result-item"
           @click="selectSearchResult(result)"
         >
           <div class="result-title">{{ result.title }}</div>
           <div class="result-address">{{ result.address }}</div>
         </div>
       </div>
    </div>
    
    <!-- 地图容器 -->
    <div class="map-container">
      <div :id="mapContainerId" class="map-box" :style="{ height: mapHeight }"></div>
    </div>
    
    <!-- 地图控件 - 放在地图外面底部 -->
    <div v-if="showControls" class="map-controls">
      <el-button 
        type="primary" 
        size="medium" 
        @click="getCurrentLocation"
        :loading="locationLoading"
        icon="el-icon-location"
        class="control-btn"
      >
        定位到当前位置
      </el-button>
      <el-button 
        size="medium" 
        @click="resetMapCenter"
        icon="el-icon-refresh"
        class="control-btn"
      >
        重置地图
      </el-button>
    </div>
    
    <!-- 位置信息显示 -->
    <div v-if="locationInfo && showLocationInfo" class="location-info">
      <p><strong>详细地址：</strong>{{ locationInfo.addr }}</p>
      <p><strong>经纬度：</strong>{{ locationInfo.lat }}, {{ locationInfo.lng }}</p>
      <p><strong>定位精度：</strong>{{ locationInfo.accuracy || 0 }}米</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TencentMap',
  props: {
    // 地图容器高度
    mapHeight: {
      type: String,
      default: '300px'
    },
    // 初始中心点坐标
    center: {
      type: Object,
      default: () => ({ lat: 39.908823, lng: 116.397470 })
    },
    // 初始缩放级别
    zoom: {
      type: Number,
      default: 13
    },
    // 是否显示控件
    showControls: {
      type: Boolean,
      default: true
    },
    // 是否显示搜索按钮
    showSearchButton: {
      type: Boolean,
      default: true
    },
    // 是否显示提示信息
    showTips: {
      type: Boolean,
      default: true
    },
    // 提示文本
    tips: {
      type: String,
      default: '提示：点击地图或拖拽标记点来选择位置'
    },
    // 是否显示位置信息
    showLocationInfo: {
      type: Boolean,
      default: true
    },
    // 是否显示API Key
    showApiKey: {
      type: Boolean,
      default: false
    },
    // 初始位置值
    value: {
      type: Object,
      default: () => null
    }
  },
  data() {
    return {
      // 地图相关
      mapContainerId: `map-container-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
      map: null,
      marker: null,
      geolocation: null,
      mapType: null, // 'TMap' 或 'qq.maps'
      
      // 位置相关
      locationLoading: false,
      locationInfo: null,
      
      // 搜索相关
      searchKeyword: '',
      searchLoading: false,
      searchResults: [],
      showSearchResults: false,
      
      // 初始化相关
      initRetryCount: 0,
      maxInitRetries: 10,
      isInitializing: false,
      
      // 定位重试相关
      locationRetryCount: 0
    }
  },
  mounted() {
    // 显示调试信息
    this.showDebugInfo()
    
    // 延迟初始化，避免频繁切换时的冲突
    this.$nextTick(() => {
      this.initializeMap()
      // 如果有初始值，设置位置信息
      if (this.value) {
        this.locationInfo = this.value
      }
    })
  },
  beforeDestroy() {
    this.destroyMap()
  },
  watch: {
    // 监听value属性变化，响应式更新地图位置
    value: {
      handler(newValue) {
        if (newValue && newValue.lat && newValue.lng) {
          // 更新locationInfo以显示位置信息
          this.locationInfo = {
            lat: newValue.lat,
            lng: newValue.lng,
            addr: newValue.address || newValue.addr || '',
            accuracy: newValue.accuracy || 0
          }
          
          // 如果地图已初始化，更新地图位置
          if (this.map) {
            this.updateMapLocation(newValue.lat, newValue.lng)
          }
        }
      },
      deep: true,
      immediate: false
    }
  },
  methods: {
    // ==================== API Key 相关 ====================
    
    /**
     * 获取当前API Key
     * 从store中获取腾讯地图API Key
     */
    getCurrentApiKey() {
      return this.$store.state.settings.tencentMapKey || 'YOUR_TENCENT_MAP_API_KEY'
    },
    
    /**
     * 复制API Key到剪贴板
     */
    copyApiKey() {
      const apiKey = this.getCurrentApiKey()
      if (navigator.clipboard) {
        navigator.clipboard.writeText(apiKey).then(() => {
          this.$message.success('API Key已复制到剪贴板')
        }).catch(() => {
          this.$message.error('复制失败，请手动复制')
        })
      } else {
        // 兼容旧浏览器
        const textArea = document.createElement('textarea')
        textArea.value = apiKey
        document.body.appendChild(textArea)
        textArea.select()
        try {
          document.execCommand('copy')
          this.$message.success('API Key已复制到剪贴板')
        } catch (err) {
          this.$message.error('复制失败，请手动复制')
        }
        document.body.removeChild(textArea)
      }
    },
    
    /**
     * 重新获取API Key
     */
    async retryGetApiKey() {
      try {
        // 检查父组件或根组件是否有获取方法
        if (this.$parent && typeof this.$parent.getTencentMapKey === 'function') {
          await this.$parent.getTencentMapKey()
          return
        }
        
        if (this.$root && typeof this.$root.getTencentMapKey === 'function') {
          await this.$root.getTencentMapKey()
          return
        }
        
        // 直接调用API
        const { getTencentMapKeyApi } = await import('@/api/systemConfig')
        const res = await getTencentMapKeyApi()
        
        if (res) {
          // 处理字符串或对象格式的响应
          const apiKey = typeof res === 'string' ? res : (res.value || res.data || res)
          if (apiKey) {
            this.$store.commit('settings/SET_tencentMapKey', apiKey)
            return apiKey
          }
        } else {
          throw new Error('API返回的数据格式不正确')
        }
      } catch (error) {
        console.error('重新获取API Key失败:', error)
        throw error
      }
    },
    
    // ==================== 地图初始化 ====================
    
    /**
     * 初始化地图
     */
    async initializeMap() {
      // 防止重复初始化
      if (this.isInitializing || this.map) return
      
      this.isInitializing = true
      this.initRetryCount = 0
      
      try {
        // 检查容器是否存在
        const container = document.getElementById(this.mapContainerId)
        if (!container) {
          throw new Error('地图容器未找到，可能组件已被销毁')
        }
        
        // 确保容器有足够的尺寸
        if (container.offsetWidth === 0 || container.offsetHeight === 0) {
          console.warn('地图容器尺寸为0，等待容器准备就绪...')
          await this.waitForContainer(container)
        }
        
        await this.loadMapAPI()
        await this.createMap()
        
        // 地图初始化成功后，如果有初始值，延迟设置位置
        if (this.value && this.value.lat && this.value.lng) {
          setTimeout(() => {
            this.updateMapLocation(this.value.lat, this.value.lng)
          }, 300)
        }
        
        this.$emit('map-ready', this.map)
      } catch (error) {
        console.error('地图初始化失败:', error)
        // 只有在组件未被销毁时才发送错误事件
        if (!this._isDestroyed) {
          this.$emit('map-error', error)
        }
      } finally {
        this.isInitializing = false
      }
    },
    
    /**
     * 加载地图API
     */
    async loadMapAPI() {
      // 检查API是否已加载
      if (window.TMap || window.qq) {
        return
      }
      
      const apiKey = this.getCurrentApiKey()
      
      // 如果API Key无效，尝试重新获取
      if (!apiKey || apiKey === 'YOUR_TENCENT_MAP_API_KEY') {
        console.warn('腾讯地图API Key未配置，尝试重新获取...')
        await this.retryGetApiKey()
        
        const newApiKey = this.getCurrentApiKey()
        if (!newApiKey || newApiKey === 'YOUR_TENCENT_MAP_API_KEY') {
          throw new Error('腾讯地图API Key获取失败')
        }
      }
      
      // 动态加载脚本
      await this.loadMapScripts(this.getCurrentApiKey())
    },
    
    /**
     * 动态加载地图脚本
     */
    loadMapScripts(apiKey) {
      return new Promise((resolve, reject) => {
        const scripts = [
          `https://map.qq.com/api/gljs?v=1.exp&key=${apiKey}&libraries=service`,
          `https://apis.map.qq.com/tools/geolocation/min?key=${apiKey}&referer=myapp`,
          `https://map.qq.com/api/js?v=2.exp&key=${apiKey}`
        ]
        
        let loadedCount = 0
        const totalScripts = scripts.length
        
        scripts.forEach((src) => {
          const script = document.createElement('script')
          script.charset = 'utf-8'
          script.src = src
          
          script.onload = () => {
            loadedCount++
            if (loadedCount === totalScripts) {
              console.log('腾讯地图API脚本加载完成')
              resolve()
            }
          }
          
          script.onerror = () => {
            reject(new Error(`腾讯地图API脚本加载失败: ${src}`))
          }
          
          document.head.appendChild(script)
        })
      })
    },
    
    /**
     * 创建地图实例
     */
    async createMap() {
      // 等待API加载完成
      await this.waitForMapAPI()
      
      const mapContainer = document.getElementById(this.mapContainerId)
      if (!mapContainer) {
        throw new Error('地图容器未找到')
      }
      
      // 优先使用TMap
      if (window.TMap && window.TMap.Map) {
        this.mapType = 'TMap'
        this.createTMap(mapContainer)
      } else if (window.qq && window.qq.maps && window.qq.maps.Map) {
        this.mapType = 'qq.maps'
        this.createQQMap(mapContainer)
      } else {
        throw new Error('腾讯地图API未正确加载')
      }
      
      // 初始化位置
      if (this.value && this.value.lat && this.value.lng) {
        this.updateMapLocation(this.value.lat, this.value.lng)
      }
    },
    
    /**
     * 等待容器准备就绪
     */
    waitForContainer(container) {
      return new Promise((resolve, reject) => {
        let retryCount = 0
        const maxRetries = 20
        
        const checkContainer = () => {
          retryCount++
          
          if (container.offsetWidth > 0 && container.offsetHeight > 0) {
            resolve()
            return
          }
          
          if (retryCount < maxRetries) {
            console.warn(`等待地图容器准备就绪... (${retryCount}/${maxRetries})`)
            setTimeout(checkContainer, 100)
          } else {
            reject(new Error('地图容器准备超时'))
          }
        }
        
        setTimeout(checkContainer, 50)
      })
    },
    
    /**
     * 等待地图API加载完成
     */
    waitForMapAPI() {
      return new Promise((resolve, reject) => {
        const checkAPI = () => {
          this.initRetryCount++
          
          if (window.TMap && window.TMap.Map) {
            resolve()
            return
          }
          
          if (window.qq && window.qq.maps && window.qq.maps.Map) {
            resolve()
            return
          }
          
          if (this.initRetryCount < this.maxInitRetries) {
            console.warn(`等待腾讯地图API加载... (${this.initRetryCount}/${this.maxInitRetries})`)
            setTimeout(checkAPI, 2000)
          } else {
            reject(new Error('腾讯地图API加载超时'))
          }
        }
        
        setTimeout(checkAPI, 1000)
      })
    },
    
    /**
     * 创建TMap地图
     */
    createTMap(container) {
      // 创建地图实例
      this.map = new window.TMap.Map(container, {
        center: new window.TMap.LatLng(this.center.lat, this.center.lng),
        zoom: this.zoom
      })
      
      // 创建标记点
      this.marker = new window.TMap.MultiMarker({
        id: 'marker-layer',
        map: this.map,
        enableDragging: true,
        styles: {
          'default': new window.TMap.MarkerStyle({
            width: 25,
            height: 35,
            anchor: { x: 12, y: 35 }
          })
        },
        geometries: [{
          id: 'marker1',
          styleId: 'default',
          position: new window.TMap.LatLng(this.center.lat, this.center.lng),
          draggable: true,
          properties: { title: '当前位置' }
        }]
      })
      
      // 绑定事件
      this.map.on('click', (evt) => this.onMapClick(evt.latLng))
      this.marker.on('dragend', (evt) => this.onMarkerDragEnd(evt.latLng))
      
      // 初始化定位服务
      this.initGeolocationService()
    },
    
    /**
     * 创建qq.maps地图
     */
    createQQMap(container) {
      // 创建地图实例
      this.map = new window.qq.maps.Map(container, {
        center: new window.qq.maps.LatLng(this.center.lat, this.center.lng),
        zoom: this.zoom
      })
      
      // 创建标记点
      this.marker = new window.qq.maps.Marker({
        map: this.map,
        position: new window.qq.maps.LatLng(this.center.lat, this.center.lng),
        draggable: true
      })
      
      // 绑定事件
      window.qq.maps.event.addListener(this.map, 'click', (evt) => this.onMapClick(evt.latLng))
      window.qq.maps.event.addListener(this.marker, 'dragend', (evt) => this.onMarkerDragEnd(evt.latLng))
      
      // 初始化定位服务
      this.initGeolocationService()
    },
    
    /**
     * 初始化定位服务
     */
    initGeolocationService() {
      try {
        if (window.qq && window.qq.maps && window.qq.maps.Geolocation) {
          const apiKey = this.getCurrentApiKey()
          
          // 检查API Key是否有效
          if (!apiKey || apiKey === 'YOUR_TENCENT_MAP_API_KEY') {
            console.warn('腾讯地图API Key未配置或无效，定位服务可能无法正常工作')
            return
          }
          
          // 初始化定位服务
          this.geolocation = new window.qq.maps.Geolocation(apiKey, 'myapp')
          console.log('腾讯定位服务初始化成功')
        } else {
          console.warn('腾讯地图定位API未加载，将使用浏览器原生定位')
        }
      } catch (error) {
        console.error('初始化腾讯定位服务失败:', error)
      }
    },
    
    /**
     * 销毁地图
     */
    destroyMap() {
      try {
        // 停止正在进行的初始化
        this.isInitializing = false
        
        // 清理地图实例
        if (this.map) {
          if (this.map.destroy) {
            this.map.destroy()
          } else if (this.map.remove) {
            this.map.remove()
          }
        }
        
        // 清理所有引用
        this.map = null
        this.marker = null
        this.geolocation = null
        this.locationInfo = null
        this.searchResults = []
      } catch (error) {
        console.warn('地图销毁时出现警告:', error)
      }
    },
    
    // ==================== 地图交互 ====================
    
    /**
     * 地图点击事件
     */
    onMapClick(latLng) {
      this.updateMarkerPosition(latLng)
    },
    
    /**
     * 标记拖拽结束事件
     */
    onMarkerDragEnd(latLng) {
      this.updateMarkerPosition(latLng)
    },
    
    /**
     * 更新标记位置
     */
    updateMarkerPosition(latLng) {
      const lat = latLng.lat || latLng.getLat()
      const lng = latLng.lng || latLng.getLng()
      
      // 更新标记位置
      if (this.mapType === 'TMap' && this.marker) {
        this.marker.updateGeometries([{
          id: 'marker1',
          styleId: 'default',
          position: latLng,
          draggable: true,
          properties: { title: '当前位置' }
        }])
      } else if (this.mapType === 'qq.maps' && this.marker) {
        this.marker.setPosition(latLng)
      }
      
      // 更新地图中心
      if (this.map) {
        this.map.setCenter(latLng)
      }
      
      // 获取地址信息
      this.reverseGeocode(lat, lng)
    },
    
    /**
     * 更新地图位置
     */
    updateMapLocation(lat, lng) {
      if (!this.map || !this.marker) {
        console.warn('地图或标记未初始化，延迟更新位置')
        // 如果地图未初始化，延迟执行
        setTimeout(() => {
          if (this.map && this.marker) {
            this.updateMapLocation(lat, lng)
          }
        }, 500)
        return
      }
      
      // 验证坐标有效性
      if (!this.isValidCoordinate(lat, lng)) {
        console.error('无效的坐标值:', { lat, lng })
        return
      }
      
      try {
        const latLng = this.mapType === 'TMap' 
          ? new window.TMap.LatLng(lat, lng)
          : new window.qq.maps.LatLng(lat, lng)
        
        // 先更新标记位置，再更新地图中心
        if (this.mapType === 'TMap') {
          this.marker.updateGeometries([{
            id: 'marker1',
            styleId: 'default',
            position: latLng,
            draggable: true,
            properties: { title: '当前位置' }
          }])
        } else {
          this.marker.setPosition(latLng)
        }
        
        // 延迟更新地图中心，避免地图API内部冲突
        this.$nextTick(() => {
          if (this.map) {
            this.map.setCenter(latLng)
          }
        })
      } catch (error) {
        console.error('更新地图位置失败:', error)
        // 如果更新失败，尝试重新初始化地图
        if (error.message && error.message.includes('far')) {
          console.warn('检测到地图距离计算错误，尝试重新初始化')
          this.reinitializeMap()
        }
      }
    },
    
    /**
     * 重置地图中心
     */
    resetMapCenter() {
      this.updateMapLocation(this.center.lat, this.center.lng)
      if (this.map) {
        this.map.setZoom(this.zoom)
      }
    },
    
    /**
     * 验证坐标有效性
     */
    isValidCoordinate(lat, lng) {
      // 检查坐标是否为数字
      const numLat = parseFloat(lat)
      const numLng = parseFloat(lng)
      
      if (isNaN(numLat) || isNaN(numLng)) {
        return false
      }
      
      // 检查纬度范围 (-90 到 90)
      if (numLat < -90 || numLat > 90) {
        return false
      }
      
      // 检查经度范围 (-180 到 180)
      if (numLng < -180 || numLng > 180) {
        return false
      }
      
      return true
    },
    
    /**
     * 重新初始化地图
     */
    async reinitializeMap() {
      console.log('开始重新初始化地图...')
      
      // 先销毁现有地图
      this.destroyMap()
      
      // 等待一段时间后重新初始化
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      try {
        await this.initializeMap()
      } catch (error) {
        console.error('地图重新初始化失败:', error)
        this.$emit('map-error', error)
      }
    },
    
    // ==================== 定位功能 ====================
    
    /**
     * 获取当前位置
     */
    getCurrentLocation() {
      this.locationLoading = true
      this.locationInfo = null
      this.locationRetryCount = 0 // 重置重试计数
      
      // 优先使用腾讯位置服务
      if (this.geolocation) {
        this.getTencentLocation()
      } else {
        this.getBrowserLocation()
      }
    },

    /**
     * 重试定位
     */
    retryLocation() {
      if (this.locationRetryCount < 2) { // 最多重试2次
        this.locationRetryCount++
        
        setTimeout(() => {
          this.getCurrentLocation()
        }, 2000) // 2秒后重试
      } else {
        this.locationLoading = false
        this.$message.error('定位服务暂时不可用，请稍后重试或手动选择位置')
      }
    },

    /**
     * 检查网络连接状态
     */
    checkNetworkStatus() {
      if (navigator.onLine === false) {
        console.warn('网络连接不可用')
        return false
      }
      
      // 检查腾讯地图API的可达性
      return fetch('https://apis.map.qq.com/ws/geocoder/v1/', {
        method: 'HEAD',
        mode: 'no-cors',
        cache: 'no-cache'
      }).then(() => {
        return true
      }).catch((error) => {
        console.warn('腾讯地图API网络连接异常:', error)
        return false
      })
    },

    /**
     * 显示调试信息
     */
    showDebugInfo() {
      const debugInfo = {
        '浏览器信息': navigator.userAgent,
        '协议': location.protocol,
        '域名': location.hostname,
        '网络状态': navigator.onLine ? '在线' : '离线',
        'API Key': this.getCurrentApiKey(),
        '腾讯地图API': window.qq && window.qq.maps ? '已加载' : '未加载',
        '定位服务': this.geolocation ? '已初始化' : '未初始化',
        'HTTPS支持': location.protocol === 'https:' ? '是' : '否'
      }
      
      console.group('腾讯地图调试信息')
      Object.entries(debugInfo).forEach(([key, value]) => {
        console.log(`${key}: ${value}`)
      })
      console.groupEnd()
      
      return debugInfo
    },
    
    /**
     * 使用腾讯位置服务定位
     */
    getTencentLocation() {
      // 检查定位服务是否可用
      if (!this.geolocation) {
        console.warn('腾讯定位服务未初始化，使用浏览器定位')
        this.getBrowserLocation()
        return
      }

      // 设置超时处理
      const timeoutId = setTimeout(() => {
        console.warn('腾讯位置服务超时，切换到浏览器定位')
        this.getBrowserLocation()
      }, 8000) // 8秒超时

      this.geolocation.getLocation(
        (position) => {
          clearTimeout(timeoutId)
          const { lat, lng, accuracy, addr } = position
          
          this.locationInfo = { lat, lng, accuracy: accuracy || 0, addr: addr || '' }
          this.updateMapLocation(lat, lng)
          
          this.$emit('location-change', {
            lat: lat.toString(),
            lng: lng.toString(),
            address: addr || '',
            locationInfo: this.locationInfo
          })
          
          this.locationLoading = false
          this.$message.success('定位成功')
        },
        (error) => {
          clearTimeout(timeoutId)
          console.error('腾讯位置服务定位失败:', error)
          
          // 根据错误类型提供更详细的处理
          if (error.code === 5) {
            console.warn('腾讯位置服务API超时，可能是网络问题或API限制')
            // 检查网络状态
            this.checkNetworkStatus().then(isOnline => {
              if (!isOnline) {
                console.warn('网络连接异常，这可能是导致API超时的原因')
              }
            })
          } else if (error.code === 1) {
            console.warn('用户拒绝了定位请求')
          } else if (error.code === 2) {
            console.warn('位置信息不可用')
          } else if (error.code === 3) {
            console.warn('定位请求超时')
          }
          
          // 自动降级到浏览器定位
          console.log('腾讯定位失败，尝试浏览器定位')
          this.getBrowserLocation()
        },
        {
          timeout: 6000, // 减少超时时间到6秒
          enableHighAccuracy: true
        }
      )
    },
    
    /**
     * 使用浏览器原生定位
     */
    getBrowserLocation() {
      if (!navigator.geolocation) {
        this.locationLoading = false
        this.$message.error('浏览器不支持定位功能')
        return
      }
      
      // 检查是否为HTTPS环境
      if (location.protocol !== 'https:' && location.hostname !== 'localhost' && location.hostname !== '127.0.0.1') {
        console.warn('浏览器定位在非HTTPS环境下可能受限')
      }
      
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const { latitude: lat, longitude: lng, accuracy } = position.coords
          
          this.locationInfo = { lat, lng, accuracy: accuracy || 0, addr: '' }
          this.updateMapLocation(lat, lng)
          this.reverseGeocode(lat, lng)
          
          this.locationLoading = false
          this.$message.success('定位成功')
        },
        (error) => {
          this.locationLoading = false
          
          // 详细的错误处理
          let errorMessage = '定位失败'
          switch (error.code) {
            case error.PERMISSION_DENIED:
              errorMessage = '用户拒绝了定位请求，请在浏览器设置中允许定位权限'
              break
            case error.POSITION_UNAVAILABLE:
              errorMessage = '位置信息不可用，请检查网络连接或GPS设置'
              break
            case error.TIMEOUT:
              errorMessage = '定位请求超时，请重试'
              break
            default:
              errorMessage = `定位失败: ${error.message}`
              break
          }
          
          console.error('浏览器定位失败:', error)
          
          // 如果是首次失败，尝试重试
          if (this.locationRetryCount === 0) {
            this.$message.warning('定位失败，正在重试...')
            this.retryLocation()
          } else {
            this.$message.error(errorMessage)
          }
        },
        {
          timeout: 10000,
          enableHighAccuracy: true
        }
      )
    },
    
    /**
     * 获取定位错误信息
     */
    getLocationErrorMessage(code) {
      const messages = {
        1: '用户拒绝了定位请求',
        2: '位置信息不可用',
        3: '定位超时'
      }
      return messages[code] || '未知错误'
    },
    
    // ==================== 地址解析 ====================
    
    /**
     * 逆地址解析 - 使用WebService API
     */
    async reverseGeocode(lat, lng) {
      const apiKey = this.getCurrentApiKey()
      if (!apiKey || apiKey === 'YOUR_TENCENT_MAP_API_KEY') {
        console.warn('API Key未配置，跳过逆地址解析')
        return
      }
      
      try {
        // 使用腾讯地图WebService API进行逆地址解析
        const response = await fetch(`https://apis.map.qq.com/ws/geocoder/v1/?location=${lat},${lng}&key=${apiKey}&output=json&get_poi=1`)
        
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`)
        }
        
        const data = await response.json()

        // 容错：后端或网络问题可能导致返回体结构不完整
        const result = (data && data.status === 0 && data.result) ? data.result : null
        const address = result?.formatted_addresses?.recommend || result?.address || ''

        this.locationInfo = {
          lat,
          lng,
          accuracy: this.locationInfo?.accuracy || 0,
          addr: address
        }

        this.$emit('location-change', {
          lat: lat.toString(),
          lng: lng.toString(),
          address,
          locationInfo: this.locationInfo
        })
      } catch (error) {
        console.error('逆地址解析请求失败:', error)
      }
    },
    
    // ==================== 地址搜索 ====================
    
    /**
     * 搜索地址 - 使用WebService API
     */
    async searchAddress() {
      if (!this.searchKeyword.trim()) {
        this.$message.warning('请输入搜索关键词')
        return
      }
      
      this.searchLoading = true
      this.searchResults = []
      
      const apiKey = this.getCurrentApiKey()
      if (!apiKey || apiKey === 'YOUR_TENCENT_MAP_API_KEY') {
        this.searchLoading = false
        this.$message.error('请先配置有效的腾讯地图API Key')
        return
      }
      
      try {
        // 使用腾讯地图WebService API进行地址解析
        const response = await fetch(`https://apis.map.qq.com/ws/geocoder/v1/?address=${encodeURIComponent(this.searchKeyword)}&key=${apiKey}&output=json`)
        
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`)
        }
        
        const data = await response.json()

        // 统一容错处理，避免 result 结构异常导致的解构报错
        const result = (data && data.status === 0 && data.result) ? data.result : null
        if (result && result.location) {
          const location = result.location
          const formattedAddresses = result.formatted_addresses
          const addressComponents = result.address_components

          const composedAddress = formattedAddresses?.recommend 
            || (addressComponents ? `${addressComponents.province || ''}${addressComponents.city || ''}${addressComponents.district || ''}${addressComponents.street || ''}` : '')
            || this.searchKeyword

          this.searchResults = [{
            title: this.searchKeyword,
            address: composedAddress,
            lat: location.lat,
            lng: location.lng
          }]

          this.showSearchResults = true
        } else {
          console.warn('地址搜索失败或返回为空:', data)
          this.$message.warning((data && data.message) || '未找到相关地址')
        }
      } catch (error) {
        console.error('地址搜索请求失败:', error)
        this.$message.error('搜索服务暂时不可用，请稍后重试')
      } finally {
        this.searchLoading = false
      }
    },
    
    /**
     * 选择搜索结果
     */
    selectSearchResult(result) {
      this.updateMapLocation(result.lat, result.lng)
      this.reverseGeocode(result.lat, result.lng)
      this.closeSearchResults()
    },
    
    /**
     * 关闭搜索结果
     */
    closeSearchResults() {
      this.showSearchResults = false
    },
    
    /**
     * 隐藏搜索结果
     */
    hideSearchResults() {
      setTimeout(() => {
        this.showSearchResults = false
      }, 200)
    },
    
    /**
     * 显示搜索结果（如果存在）
     */
    showSearchResultsIfExists() {
      if (this.searchResults.length > 0) {
        this.showSearchResults = true
      }
    }
  }
}
</script>

<style scoped>
.tencent-map-wrapper {
  width: 100%;
  position: relative;
}

/* 地图容器样式 */
.map-container {
  position: relative;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.map-box {
  width: 100%;
  min-height: 300px;
}

/* 地图控件样式 - 放在地图外面底部 */
.map-controls {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 15px;
  padding: 0 15px;
}

.map-controls .control-btn {
  min-width: 140px;
  height: 40px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.map-controls .control-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.map-controls .el-button--primary {
  background: linear-gradient(135deg, #409eff, #67c2ff);
  border: none;
}

.map-controls .el-button--primary:hover {
  background: linear-gradient(135deg, #337ecc, #5aa3d9);
}

.map-tips p {
  margin: 0;
}

/* 位置信息样式 */
.location-info {
  margin-top: 15px;
  padding: 12px;
  background-color: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 4px;
}

.location-info p {
  margin: 0 0 8px 0;
  font-size: 14px;
  line-height: 1.5;
}

.location-info p:last-child {
  margin-bottom: 0;
}

.location-info strong {
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .map-controls {
    position: static;
    margin-top: 10px;
    flex-direction: row;
    justify-content: center;
  }
  
  .map-tips {
    position: static;
    margin-top: 10px;
    background-color: #f5f7fa;
    color: #606266;
    border: 1px solid #e4e7ed;
  }
}
</style>