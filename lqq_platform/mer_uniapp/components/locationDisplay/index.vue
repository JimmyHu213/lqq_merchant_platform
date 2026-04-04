<template>
	<view class="location-display" :class="{ 'scroll ': isScrolled }" @click="handleLocationClick">
		<text v-show="!positioningType" class="iconfont icon-ic_location1"></text>
		<view class="location-text">
			<!-- 小程序使用CSS动画类 -->
			<!-- #ifdef MP -->
			<text class="text-content mp-text" :class="animationClass" :style="{transform: `translateX(${textOffset}px)`}">{{ displayText }}</text>
			<!-- #endif -->
			<!-- 其他平台使用JS动画 -->
			<!-- #ifndef MP -->
			<text class="text-content" :style="{transform: `translateX(${textOffset}px)`}">{{ displayText }}</text>
			<!-- #endif -->
		</view>
    <text v-show="positioningType" class="iconfont icon-ic_downarrow ml-4"></text>
	</view>
</template>

<script>
export default {
	name: 'LocationDisplay',
	props: {
		// 显示的位置文字
		text: {
			type: String,
			default: '定位中...'
		},
		// 最大显示宽度
		maxWidth: {
			type: String,
			default: '118rpx'
		},
		// 最大高度
		maxHeight: {
			type: String,
			default: '42rpx'
		},
		// 是否处于滚动状态 外界用于时候更换颜色
		isScrolled: {
			type: Boolean,
			default: false
		},
		// 是否启用文字滚动功能
		enableScroll: {
			type: Boolean,
			default: true
		},
		positioningType:{
			type: Boolean,
			default: false
		}
		
	},
	data() {
		return {
			textOffset: 0, // 文字偏移量
			scrollTimer: null, // 滚动定时器
			isScrolling: false, // 是否正在滚动
			// 小程序CSS动画状态
			animationClass: '', // 动画类名
			maxDistance: 0, // 最大滚动距离
			currentLocationName: '', // 当前位置名称
			storageTimer: null, // 位置监听定时器
			isDestroyed: false // 组件销毁标记
		}
	},
	computed: {
		displayText() {
			// 如果外部传入了text，优先使用外部text
			if (this.text && this.text !== '定位中...') {
				return this.text;
			}
			if(this.currentLocationName){
				return this.currentLocationName
			} else {
				// 否则使用内部管理的位置数据
				 return this.positioningType ? '选择圈层' : '选择位置';
			}
			
		}
	},
	mounted() {
		// 只有在没有外部传入text时，才启用内部位置管理
		if (!this.text || this.text === '定位中...') {
			this.initLocationData();
			this.setupStorageListener();
		}
		
		// 启动滚动
		this.resetAndStartScroll();
	},
	created() {
		// LocationDisplay 组件初始化
	},
	watch: {
		text() {
			this.resetAndStartScroll();
		},
		displayText() {
			this.resetAndStartScroll();
		},
		enableScroll() {
			if (!this.enableScroll) {
				this.stopScroll();
				this.textOffset = 0;
			} else {
				this.resetAndStartScroll();
			}
		}
	},
	beforeDestroy() {
		this.isDestroyed = true;
		this.stopScroll();
		this.stopLocationListener();
	},
	methods: {
		// 重置并开始滚动 - 临时停用
		resetAndStartScroll() {
			this.stopScroll();
			this.textOffset = 0;
			this.isScrolling = false;
			
			// 临时停用滚动功能
			return;
			
			if (!this.enableScroll) return;
			
			this.$nextTick(() => {
				this.startSimpleScroll();
			});
		},
		
		// 完整的双向滚动逻辑
		startSimpleScroll() {
			if (this.isScrolling) return;
			
			// 先计算需要滚动的距离
			this.$nextTick(() => {
				this.calculateScrollDistance();
			});
		},
		
		// 计算滚动距离
		calculateScrollDistance() {
			const query = uni.createSelectorQuery().in(this);
			query.select('.text-content').boundingClientRect();
			query.select('.location-text').boundingClientRect();
			query.exec((res) => {
				if (res[0] && res[1]) {
					const textWidth = res[0].width;      // 文字总宽度
					const containerWidth = res[1].width;  // 容器宽度
					let overflowWidth = textWidth - containerWidth; // 溢出宽度
					
					// 重新计算正确的滚动距离
					// 目标：让文字末尾刚好显示在容器右边缘，而不是让文字滚动消失
					// 正确公式：滚动距离 = 文字宽度 - 容器宽度 (这样文字末尾刚好到达容器右边)
					// 但实际上我们不需要滚动这么远，只需要让末尾文字可见即可
					
					// 小程序保守策略：确保文字末尾能显示，但保留开头部分
					// #ifdef MP
					// 策略：滚动距离 = 溢出宽度 - 10px，这样文字末尾显示，开头也保留10px可见
					overflowWidth = Math.max(0, overflowWidth - 10);
					// #endif

					if (overflowWidth > 0) {
						// 需要滚动，开始双向滚动
						this.startTwoWayScroll(overflowWidth);
					} else {
						// console.log('LocationDisplay无需滚动');
					}
				}
			});
		},
		
		// 双向滚动：先向左显示末尾，再向右回到开头
		startTwoWayScroll(maxDistance) {
			// 延迟1秒开始第一阶段滚动
			setTimeout(() => {
				if (this.isDestroyed || !this.enableScroll) return;
				
				this.isScrolling = true;
				this.textOffset = 0;
				
				// 第一阶段：向左滚动显示文字末尾
				this.scrollToEnd(maxDistance);
			}, 1000);
		},
		
		// 第一阶段：向左滚动到末尾
		scrollToEnd(maxDistance) {
			// 小程序优化：降低频率，增加步进
			// #ifdef MP
			const scrollStep = 1;         // 小程序用更大步进
			const scrollInterval = 30;   // 小程序用更长间隔
			// #endif
			
			// H5保持丝滑
			// #ifdef H5
			const scrollStep = 0.5;       
			const scrollInterval = 20;   
			// #endif
			
			// APP采用中等参数
			// #ifdef APP-PLUS
			const scrollStep = 0.8;       
			const scrollInterval = 25;   
			// #endif
			this.scrollTimer = setInterval(() => {
				// 小程序模式：每次更新前检查是否会超出边界
				// #ifdef MP
				const nextOffset = this.textOffset - scrollStep;
				if (nextOffset < -maxDistance) {
					// 直接定位到目标位置，不再继续滚动
					this.textOffset = -maxDistance;
					clearInterval(this.scrollTimer);
					
					setTimeout(() => {
						this.scrollToStart();
					}, 2000);
					return;
				}
				this.textOffset = nextOffset;
				// #endif
				
				// H5和APP正常滚动
				// #ifndef MP
				this.textOffset -= scrollStep;
				
				// 滚动到末尾位置（文字末尾刚好显示完整）
				if (this.textOffset <= -maxDistance) {
					this.textOffset = -maxDistance; // 精确定位
					clearInterval(this.scrollTimer);

					// 停留2秒后开始第二阶段
					setTimeout(() => {
						this.scrollToStart();
					}, 2000);
				}
				// #endif
			}, scrollInterval);
		},
		
		// 第二阶段：向右滚动回到开头
		scrollToStart() {
			// 小程序优化：使用相同的平台参数
			// #ifdef MP
			const scrollStep = 1;         
			const scrollInterval = 30;   
			// #endif
			
			// H5保持丝滑
			// #ifdef H5
			const scrollStep = 0.5;       
			const scrollInterval = 20;   
			// #endif
			
			// APP采用中等参数
			// #ifdef APP-PLUS
			const scrollStep = 0.8;       
			const scrollInterval = 25;   
			// #endif
			this.scrollTimer = setInterval(() => {
				// 小程序模式：每次更新前检查是否会超出边界
				// #ifdef MP
				const nextOffset = this.textOffset + scrollStep;
				if (nextOffset > 0) {
					// 直接定位到起始位置，不再继续滚动
					this.textOffset = 0;
					this.stopScroll();
					return;
				}
				this.textOffset = nextOffset;
				// #endif
				
				// H5和APP正常滚动
				// #ifndef MP
				this.textOffset += scrollStep;
				
				// 回到初始位置
				if (this.textOffset >= 0) {
					this.textOffset = 0; // 精确定位到起始位置
					this.stopScroll();
				}
				// #endif
			}, scrollInterval);
		},
		
		
		// 停止滚动
		stopScroll() {
			if (this.scrollTimer) {
				clearInterval(this.scrollTimer);
				this.scrollTimer = null;
			}
			this.isScrolling = false;
		},
		
		// 点击组件圈层
		handleLocationClick() {
			this.$emit('click');
		},
		
		// ============ 位置管理相关方法 ============
		
		// 从缓存获取位置数据 - 以location_info为准
		initLocationData() {
			if (this.isDestroyed) return;
			
			try {
				// 优先使用location_info（用户最新的主动定位选择）
				const locationInfo = uni.getStorageSync('location_info');
				if (locationInfo && (locationInfo.name || locationInfo.address) && !this.positioningType) {
					this.currentLocationName = locationInfo.name || locationInfo.address;
					return;
				}
				
				// 备选：使用areas_info（圈层选择的位置）
				const areasInfo = uni.getStorageSync('areas_info');
				if (areasInfo && Array.isArray(areasInfo) && areasInfo.length > 0 && this.positioningType) {
					// 显示最新选择的圈层
					const latestArea = areasInfo[0];
					this.currentLocationName = latestArea.name || latestArea.address || '选择圈层';
					return;
				}
				
				// 默认显示
				this.currentLocationName = this.positioningType ? '选择圈层' : '选择位置';
			} catch (error) {
				this.currentLocationName =  this.positioningType ? '选择圈层' : '选择位置';
			}
		},
		
		// 设置存储监听
		setupStorageListener() {
			if (this.isDestroyed) return;
			
			// 使用定时器定期检查缓存变化
			this.storageTimer = setInterval(() => {
				if (!this.isDestroyed) {
					this.checkLocationUpdate();
				}
			}, 1000); // 每秒检查一次
		},
		
		// 检查位置更新 - 以location_info为准
		checkLocationUpdate() {
			if (this.isDestroyed) return;
			
			try {
				let newLocationName = this.positioningType ? '选择圈层' : '选择位置';
				
				// 优先使用location_info（用户最新的主动定位选择）
				const locationInfo = uni.getStorageSync('location_info');
				if (locationInfo && (locationInfo.name || locationInfo.address)&& !this.positioningType) {
					newLocationName = locationInfo.name || locationInfo.address;
				} else {
					// 备选：使用areas_info（圈层选择的位置）
					const areasInfo = uni.getStorageSync('areas_info');
					if (areasInfo && Array.isArray(areasInfo) && areasInfo.length > 0 && this.positioningType) {
						const latestArea = areasInfo[0];
						newLocationName = latestArea.name || latestArea.address || '选择圈层';
					}
				}
				
				// 如果地址发生变化，更新显示
				if (newLocationName !== this.currentLocationName) {
					this.currentLocationName = newLocationName;
					// displayText计算属性会自动更新，触发watch重新计算滚动
				}
			} catch (error) {
				// 位置更新失败，忽略
			}
		},
		
		// 停止位置监听
		stopLocationListener() {
			if (this.storageTimer) {
				clearInterval(this.storageTimer);
				this.storageTimer = null;
			}
		}
	}
}
</script>

<style lang="scss" scoped>
.scroll{
  .iconfont {
    color: #000 !important;
  }

  .text-content {
    color: #000 !important;
  }
}
.location-display {
	display: flex;
	align-items: center;
	cursor: pointer;
	height: 42rpx;
	max-height: 42rpx;
	overflow: hidden;
	box-sizing: border-box;
	position: relative;
	flex-shrink: 0;
	margin-right: 8rpx;
  width: 154rpx;
	
	.iconfont {
		font-size: 26rpx;
		color: #fff;
		margin-right: 2rpx;
		flex-shrink: 0;
		height: 24rpx;
		line-height: 24rpx;
		margin-right: 4rpx;
		/* #ifdef H5 */
		font-size: 24rpx;
		/* #endif */
	}
	
	.location-text {
		line-height: 32px;
		overflow: hidden;
		position: relative;
	}
	
	.text-content {
		/* 基础样式 */
		height: 18px;
		font-family: PingFang SC, PingFang SC;
		font-weight: 400;
		font-size: 13px;
		color: #FFFFFF;
		line-height: 18px;
		text-align: left;
		font-style: normal;
		text-transform: none;
		display: inline-block;
		
		/* H5平台特殊处理 */
		/* #ifdef H5 */
		min-width: 100%;
		font-size: 12px;
		line-height: 42rpx;
		height: 42rpx;
		display: flex;
		/* 禁用CSS transition，让JS动画完全控制 */
		transition: none !important;
		transform: translateZ(0); /* 启用硬件加速 */
		will-change: transform; /* 优化渲染性能 */
		/* #endif */
		
		/* 小程序/APP平台 */
		/* #ifdef MP || APP-PLUS */
		width: auto;
		min-width: 500rpx;
		/* 禁用transition，避免小程序抖动 */
		transition: none !important;
		/* 小程序强制GPU渲染优化 */
		transform: translateZ(0) translate3d(0,0,0); 
		will-change: transform;
		backface-visibility: hidden;
		/* #endif */
		
		/* 小程序专用CSS动画 */
		/* #ifdef MP */
		&.mp-text {
			/* 基础优化 */
			transform: translateZ(0);
			will-change: transform;
			
			/* 动画效果暂时禁用，继续使用JS控制 */
			/* transition: transform 3s ease-in-out; */
		}
		
		&.mp-scroll-to-end {
			/* 滚动到末尾的动画 - 暂时不用 */
		}
		
		&.mp-scroll-to-start {
			/* 滚动回起始位置的动画 - 暂时不用 */
		}
		/* #endif */
	}
}
</style>