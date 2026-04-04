<template>
	<view :data-theme="theme" class="area-select-page">
		<!-- 顶部搜索栏 -->
		<view class="mp-header">
			<!-- 状态栏占位 -->
			<view class="sys-head" :style="{ height: statusBarHeight + 'px' }"></view>
			<view class="serch-box">
				<view class="serch-wrapper" :style="mpSearchWrapperStyle">
					<!-- 返回按钮 -->
					<view class="leftNav-box">
						<view class="select_nav" @click="goBack">
							<text class="iconfont icon-ic_left"></text>
						</view>
					</view>
					<!-- 搜索框 -->
					<view class="search-input-container" :style="mpSearchInputStyle">
						<view class="search-input-box">
							<text class="iconfont icon-ic_search"></text>
							<input class="search-input" placeholder="搜索商圈" v-model="searchKeyword" @input="onSearchInput" />
						</view>
					</view>
				</view>
			</view>
		</view>
		<!-- 占位高度 -->
		<view :style="'height:' + headerHeight + 'px;'"></view>

		<!-- 当前定位 -->
		<view class="current-location">
			<view class="location-item">
				<view class="iconfont icon-ic_location1" style="color: #e93323; margin-right: 4rpx"></view>
				<view class="location-info">
					<text class="location-label">当前选择：</text>
					<text class="location-name">{{ currentSelectedLocation }}</text>
				</view>
				<view class="relocate-btn" @click="relocate">
					<image :src="`${urlDomain}crmebimage/presets/location.png`" class="reload-icon" mode="aspectFit"></image>
					<text class="relocate-text">重新定位</text>
				</view>
			</view>
		</view>

		<!-- 历史位置 -->
		<view class="user-address-section" v-if="locationHistory.length > 0">
			<view class="address-item">
				<view class="section-title">历史位置</view>
				<view class="address-content">
					<view class="address-name" v-for="(location, index) in locationHistory" :key="index" @click="selectLocationHistory(location)">
						{{ location.name || location.address }}
					</view>
				</view>
			</view>
		</view>

		<!-- 商圈 -->
		<view class="business-section">
			<view class="business-card">
				<!-- 标题和距离筛选 -->
				<view class="title-filter-row">
					<view class="section-title">商圈</view>
					<view class="distance-filter">
						<text class="filter-label">离我：</text>
						<view
							class="distance-tag"
							v-for="(distance, index) in distanceOptions"
							:key="index"
							:class="{ active: selectedDistance === distance }"
							@click="selectDistance(distance)"
						>
							{{ distance }}
						</view>
					</view>
				</view>

				<!-- 商圈网格 -->
				<view class="business-grid" v-if="filteredBusinessList.length">
					<view class="business-item" v-for="(item, index) in filteredBusinessList" :key="index" @click="selectLocation(item)">
						<view class="business-info">
							<text class="business-name">{{ item.name }}</text>
						</view>
					</view>
				</view>

				<!-- 无数据状态 -->
				<view class="empty-state" v-if="!filteredBusinessList.length">
					<image class="empty-icon" :src="`${urlDomain}crmebimage/presets/noshop.png`" mode="aspectFit"></image>
					<text class="empty-text">附近暂无商圈~</text>
				</view>
			</view>
		</view>

		<!-- 商圈缺省（已移至商圈区域内） -->
	</view>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import { mapGetters } from 'vuex';
import locationManager from '@/utils/locationManager.js';
import { getAreasList, getAreasLocationList } from '@/api/api.js';

let app = getApp();

export default {
	computed: {
		...mapGetters(['isLogin']),
		// 过滤后的商圈列表
		filteredBusinessList() {
			if (!this.businessList.length) {
				return [];
			}

			// 距离筛选逻辑应该基于API返回的数据或者其他业务逻辑
			// 这里暂时直接返回所有商圈，因为距离筛选应该在后端处理
			return this.businessList;
		},

		// 小程序搜索框动态样式
		mpSearchWrapperStyle() {
			/* #ifdef MP */
			return {
				'margin-top': this.searchTop + 'px',
				height: this.searchHeight + 'px'
			};
			/* #endif */
			/* #ifndef MP */
			return {};
			/* #endif */
		},

		// 小程序搜索输入框容器动态样式
		mpSearchInputStyle() {
			/* #ifdef MP */
			return {
				'margin-right': this.searchRight + 45 + 'px' // 额外留45px间距（约90rpx），明显避开胶囊
			};
			/* #endif */
			/* #ifndef MP */
			return {};
			/* #endif */
		}
	},
	data() {
		return {
			theme: app.globalData.theme,
			urlDomain: '',
			statusBarHeight: app.globalData.statusBarHeight || uni.getSystemInfoSync().statusBarHeight,
			headerHeight: 0, // 头部总高度
			searchKeyword: '', // 搜索关键词
			// 小程序胶囊适配数据
			searchTop: 0, // 搜索框距离状态栏的距离
			searchRight: 0, // 搜索框右侧边距
			searchHeight: 0, // 搜索框高度
			statusWidth: 0, // 胶囊宽度
			currentSelectedLocation: '国润·季购物中心', // 当前选中的位置
			locationHistory: [],
			distanceOptions: ['5km', '10km', '20km', '50km'],
			selectedDistance: '5km',
			businessList: [],
			searchTimer: null,
			currentLocation: null, // 当前地理位置
			hasSelectedSpecificArea: false // 标记用户是否选择了具体区域
		};
	},
	onLoad(options) {
		// 记录来源页面信息（用于返回）

		// 初始化图片域名
		this.urlDomain = this.$Cache.get('imgHost') || '';

		// 从定位管理器获取位置信息
		const locationData = locationManager.getLocationData();

		// 如果没有位置数据，跳转到选择位置页面
		if (!locationData) {
			uni.redirectTo({
				url: '/pages/circle/index'
			});
			return;
		}

		// 获取传入的参数
		if (options.current) {
			this.currentSelectedLocation = decodeURIComponent(options.current);
		} else {
			// 使用位置数据更新当前选择的位置
			this.updateCurrentSelectedLocation(locationData);
		}

		this.currentLocation = locationData;

		// 获取附近商圈
		this.getAreasLocationList();

		// 获取位置历史记录
		this.getLocationHistory();
	},
	mounted() {
		// 小程序胶囊适配
		// #ifdef MP
		const res = uni.getMenuButtonBoundingClientRect();
		const statusHeight = res.top; // 胶囊距离顶部
		const statusRight = res.right; // 胶囊右边界坐标
		const jnHeight = res.height; // 胶囊高度
		this.statusWidth = res.width;
		this.searchTop = statusHeight - this.statusBarHeight;
		this.searchHeight = jnHeight;
		// 搜索框宽度计算
		uni.getSystemInfo({
			success: (res) => {
				this.searchRight = res.windowWidth - statusRight;
			}
		});
		// #endif

		// 计算头部高度
		this.$nextTick(() => {
			setTimeout(() => {
				const query = uni.createSelectorQuery().in(this);
				query
					.select('.serch-box')
					.boundingClientRect((data) => {
						if (data) {
							this.headerHeight = data.height + this.statusBarHeight;
						}
					})
					.exec();
			}, 100);
		});
	},
	methods: {
		// 更新当前选择的位置显示
		updateCurrentSelectedLocation(locationData) {
			if (!locationData) {
				this.currentSelectedLocation = '去选择位置';
				return;
			}

			// 简化逻辑：优先显示name，备选显示address
			if (locationData.name) {
				this.currentSelectedLocation = locationData.name;
			} else if (locationData.address) {
				this.currentSelectedLocation = locationData.address;
			} else {
				this.currentSelectedLocation = '去选择位置';
			}
		},

		// 返回上一页
		goBack() {
			// 如果用户没有选择具体区域，但有搜索/浏览结果，则缓存当前所有区域结果
			if (!this.hasSelectedSpecificArea) {
				this.cacheCurrentSearchResults();
			}
			// 如果用户选择了具体区域，cacheAreaInfo 已经在 selectLocation 中被调用了

			// 获取页面栈，判断如何返回
			const pages = getCurrentPages();

			if (pages.length > 1) {
				// 有上一页，获取上一页信息
				const prevPage = pages[pages.length - 2];
				const prevPageRoute = prevPage.route;

				// 检查上一页是否是 Tab 页面
				const tabPages = ['pages/index/index', 'pages/goods_cate/index', 'pages/order_addcart/index', 'pages/user/index'];
				const isTabPage = tabPages.some((tabRoute) => prevPageRoute.includes(tabRoute));

				if (isTabPage) {
					// 如果上一页是 Tab 页面，使用 switchTab
					const tabUrl = '/' + prevPageRoute;
					uni.switchTab({ url: tabUrl });
				} else {
					// 普通页面，直接返回
					uni.navigateBack();
				}
			} else {
				// 没有上一页（可能是直接访问），跳转到首页
				uni.switchTab({
					url: '/pages/index/index'
				});
			}
		},

		// 搜索输入处理
		onSearchInput(e) {
			this.searchKeyword = e.detail.value;
			// 可以在这里添加防抖搜索逻辑
			this.debounceSearch();
		},

		// 防抖搜索
		debounceSearch() {
			clearTimeout(this.searchTimer);
			this.searchTimer = setTimeout(() => {
				this.searchAreas();
			}, 500);
		},

		// 搜索地区
		async searchAreas() {
			try {
				// 构建搜索API参数
				let params = {};

				// 搜索关键词（必传）
				const searchKeyword = this.searchKeyword.trim();
				if (searchKeyword) {
					params.address = searchKeyword;
				} else {
					// 无搜索关键词时，调用位置API
					await this.getAreasLocationList();
					return;
				}

				// 距离参数（选中的距离）
				params.distance = parseInt(this.selectedDistance);

				const res = await getAreasList(params);

				this.businessList = this.processAreasData(res.data);
			} catch (error) {
				this.businessList = [];
				uni.showToast({
					title: '搜索失败',
					icon: 'none'
				});
			}
		},

		// 重新定位 - 参照用户地址界面处理方式
		async relocate() {
			// 使用统一的位置工具方法，避免权限死循环
			this.$util.$L
				.getLocation()
				.then((res) => {
					// 获取位置成功，打开地图选择
					uni.chooseLocation({
						// latitude: res.latitude || uni.getStorageSync('user_latitude'),
						// longitude: res.longitude || uni.getStorageSync('user_longitude'),
						success: async (locationRes) => {
							// 保存用户选择的位置
							await locationManager.saveLocationData(
								{
									latitude: locationRes.latitude,
									longitude: locationRes.longitude,
									updateTime: Date.now()
								},
								locationRes.name,
								locationRes.address,
								true
							);

							// 获取并更新位置信息
							const updatedLocationData = locationManager.getLocationData();
							this.currentLocation = updatedLocationData;

							// 使用统一的方法更新显示
							this.updateCurrentSelectedLocation(updatedLocationData);

							// 添加到历史记录
							this.addLocationHistory(updatedLocationData);

							uni.showToast({
								title: '位置已更新',
								icon: 'success'
							});

							this.getAreasLocationList();
						},
						fail: (error) => {
							if (error.errMsg && error.errMsg.includes('cancel')) {
								// 用户取消选择位置
								return;
							}
							uni.showToast({
								title: '位置选择失败',
								icon: 'none',
								duration: 2000
							});
						}
					});
				})
				.catch((err) => {
					// 位置获取失败，工具方法已经处理了权限问题，不再显示额外提示
				});
		},

		// 获取位置历史记录
		getLocationHistory() {
			try {
				const history = uni.getStorageSync('location_history') || [];
				this.locationHistory = history.slice(0, 4);
			} catch (error) {
				this.locationHistory = [];
			}
		},

		// 添加位置到历史记录
		addLocationHistory(locationData) {
			try {
				let history = uni.getStorageSync('location_history') || [];

				// 去重检查：相同名称或相近坐标
				const isDuplicate = history.some(
					(item) => item.name === locationData.name || (Math.abs(item.latitude - locationData.latitude) < 0.001 && Math.abs(item.longitude - locationData.longitude) < 0.001)
				);

				if (!isDuplicate) {
					history.unshift(locationData); // 插入队首
					history = history.slice(0, 4); // 保持最多4条
					uni.setStorageSync('location_history', history);
					this.locationHistory = history; // 更新显示
				}
			} catch (error) {
				console.error('添加历史记录失败:', error);
			}
		},

		// 选择历史位置
		selectLocationHistory(historyItem) {
			// 直接更新当前位置缓存
			uni.setStorageSync('location_info', historyItem);

			// 更新当前显示的位置
			this.currentSelectedLocation = historyItem.name || historyItem.address;

			// 提示用户
			uni.showToast({
				title: '已选择：' + (historyItem.name || historyItem.address),
				icon: 'success'
			});

			// 延迟返回上一页
			setTimeout(() => {
				this.goBack();
			}, 1500);
		},

		// 选择位置
		selectLocation(location) {
			this.currentSelectedLocation = location.name;

			// 标记用户选择了具体区域
			this.hasSelectedSpecificArea = true;

			// 缓存区域信息到 areas_info
			this.cacheAreaInfo(location);

			// 将选择的商圈信息作为定位地址缓存到 location_info
			this.cacheLocationInfo(location);

			// 添加到历史记录
			const locationData = {
				latitude: location.latitude,
				longitude: location.longitude,
				name: location.name,
				address: location.address || location.name,
				updateTime: Date.now(),
				hasPermission: true,
				permissionStatus: 1,
				version: '2.0',
				source: 'manual_select',
				type: location.type || 'business'
			};
			// this.addLocationHistory(locationData);

			// 返回选择结果给上一页
			const pages = getCurrentPages();
			if (pages.length > 1) {
				const prevPage = pages[pages.length - 2];
				if (prevPage.handleLocationSelect) {
					prevPage.handleLocationSelect(location);
				}
			}

			uni.showToast({
				title: '已选择：' + location.name,
				icon: 'success'
			});

			// 延迟返回上一页
			setTimeout(() => {
				this.goBack();
			}, 1500);
		},

		// 获取附近商圈
		async getAreasLocationList() {
			try {
				// 构建API参数
				let params = {};

				// 检查当前位置信息，优先准备经纬度参数
				if (this.currentLocation && this.currentLocation.latitude && this.currentLocation.longitude) {
					// 经纬度搜索参数（距离搜索必传）
					params.latitude = String(this.currentLocation.latitude);
					params.longitude = String(this.currentLocation.longitude);
					params.distance = parseInt(this.selectedDistance);
				}

				// 检查地址搜索参数
				const searchKeyword = this.searchKeyword.trim();
				if (searchKeyword) {
					// 用户输入的搜索关键词（地址搜索必传）
					params.address = searchKeyword;
				} else {
					// 无搜索关键词时，尝试使用缓存的地址名称
					const locationData = locationManager.getLocationData();

					if (locationData && locationData.name) {
						// 使用缓存地址名称（地址搜索必传）
						params.address = locationData.name;
					}
				}

				// 确保至少有一种搜索方式的参数
				if (!params.address && (!params.latitude || !params.longitude || !params.distance)) {
					this.businessList = [];
					return;
				}

				const res = await getAreasLocationList(params);

				this.businessList = this.processAreasData(res.data);
			} catch (error) {
				this.businessList = [];
				if (this.searchKeyword.trim()) {
					uni.showToast({
						title: '搜索失败',
						icon: 'none'
					});
				}
			}
		},

		// 选择距离
		selectDistance(distance) {
			this.selectedDistance = distance;

			// 根据是否有搜索关键词，调用不同的API
			if (this.searchKeyword.trim()) {
				// 有搜索关键词，调用搜索API
				this.searchAreas();
			} else {
				// 无搜索关键词，调用位置API
				this.getAreasLocationList();
			}
		},

		// 处理区域数据
		processAreasData(areasData) {
			if (!areasData || !Array.isArray(areasData)) {
				return [];
			}

			// 直接返回后端数据，只过滤状态为开启的商圈
			return areasData.filter((area) => area.status === true);
		},

		// 缓存当前区域结果（用户查看区域但没有选择具体区域）
		cacheCurrentSearchResults() {
			try {
				// 只有当有区域结果时才缓存
				if (this.businessList && this.businessList.length > 0) {
					// 将当前显示的所有区域都缓存为areas_info
					const searchResultAreas = this.businessList.map((business) => ({
						id: business.id,
						name: business.name,
						type: business.type || 'business',
						latitude: business.latitude,
						longitude: business.longitude,
						address: business.address,
						distance: business.distance,
						updateTime: Date.now(),
						version: '2.0',
						isSelected: false, // 标记这些是搜索结果，不是用户选择的
						isSearchResult: true // 标记这是搜索结果批量缓存
					}));

					// 限制最大缓存数量，避免数据过多
					const maxCacheSize = 50;
					const finalAreas = searchResultAreas.slice(0, maxCacheSize);
					// 保存到缓存，覆盖之前的缓存
					uni.setStorageSync('areas_info', finalAreas);
				} else {
					// 如果没有区域结果，清空缓存，避免使用旧数据
					uni.setStorageSync('areas_info', []);
				}
			} catch (error) {
				console.error('缓存区域结果失败:', error);
			}
		},

		// 缓存区域信息（用户选择了具体区域）
		cacheAreaInfo(location) {
			try {
				// 构建区域信息对象
				const areaInfo = {
					id: location.id || Date.now(), // 如果没有id，使用时间戳
					name: location.name,
					type: location.type || 'business', // business, address
					latitude: location.latitude,
					longitude: location.longitude,
					address: location.address,
					distance: location.distance,
					updateTime: Date.now(),
					version: '2.0',
					isSelected: true // 标记这是用户主动选择的区域
				};

				// 用户选择具体区域时，只缓存这一个区域（覆盖之前的搜索结果）
				const selectedArea = [areaInfo];

				// 保存到缓存
				uni.setStorageSync('areas_info', selectedArea);
			} catch (error) {
				console.error('缓存选择区域失败:', error);
			}
		},

		// 缓存选择的商圈信息到 location_info（作为定位地址）
		cacheLocationInfo(location) {
			try {
				// 构造标准的location_info格式，与定位数据保持一致
				const locationInfo = {
					latitude: location.latitude || 0,
					longitude: location.longitude || 0,
					name: location.name,
					address: location.address || location.name,
					updateTime: Date.now(),
					hasPermission: true,
					permissionStatus: 1, // 1表示已允许
					version: '2.0',
					source: 'manual_select', // 标记为手动选择
					type: location.type || 'business' // 商圈类型
				};

				// 缓存到 location_info，替换原有定位信息
				uni.setStorageSync('location_info', locationInfo);
			} catch (error) {}
		}
	},

	beforeDestroy() {
		// 清理定时器
		if (this.searchTimer) {
			clearTimeout(this.searchTimer);
		}
	}
};
</script>

<style lang="scss" scoped>
.area-select-page {
	height: 100vh;
	background-color: #f8f8f8;
	display: flex;
	flex-direction: column;
}
.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;

	.empty-text {
		font-size: 26rpx;
		color: #999999;
	}

	.empty-icon {
		width: 440rpx;
		height: 360rpx;
		img {
		}
	}
}
// 状态栏占位
.sys-head {
	background: #fff;
}

// 顶部导航栏
.mp-header {
	z-index: 999;
	position: fixed;
	left: 0;
	top: 0;
	width: 100%;
	background: #fff;

	.serch-box {
		background: #fff;

		.serch-wrapper {
			height: 44px;
			display: flex;
			align-items: center;
			padding: 0 24rpx;

			/* #ifdef H5 */
			/* H5端头部优化 - 更宽敞的间距 */
			height: 50px;
			padding: 0 32rpx;
			/* #endif */

			/* #ifdef MP */
			/* 小程序端头部优化 - 胶囊适配通过动态样式处理 */
			padding: 0 20rpx;
			/* #endif */

			.leftNav-box {
				margin-right: 20rpx;
				flex-shrink: 0;

				/* #ifdef H5 */
				margin-right: 24rpx;
				/* #endif */

				/* #ifdef MP */
				margin-right: 16rpx;
				/* #endif */

				.select_nav {
					width: 60rpx;
					height: 60rpx;
					background: rgba(0, 0, 0, 0.1);
					border-radius: 30rpx;
					display: flex;
					align-items: center;
					justify-content: center;

					.iconfont {
						font-size: 32rpx;
						color: #333;
					}
				}
			}

			.search-input-container {
				flex: 1;

				.search-input-box {
					height: 60rpx;
					background: #f5f5f5;
					border-radius: 30rpx;
					display: flex;
					align-items: center;
					padding: 0 24rpx;

					/* #ifdef H5 */
					/* H5端搜索框优化 - 更宽敞的布局 */
					height: 70rpx;
					padding: 0 32rpx;
					border-radius: 35rpx;
					/* #endif */

					/* #ifdef MP */
					/* 小程序端搜索框优化 - 紧凑布局适应小屏，避开胶囊 */
					height: 56rpx;
					padding: 0 20rpx;
					border-radius: 28rpx;
					max-width: calc(100vw - 300rpx); /* 限制宽度避开胶囊 */
					/* #endif */

					.iconfont {
						font-size: 28rpx;
						color: #999;
						margin-right: 16rpx;
						flex-shrink: 0;

						/* #ifdef H5 */
						font-size: 30rpx;
						margin-right: 20rpx;
						/* #endif */

						/* #ifdef MP */
						font-size: 26rpx;
						margin-right: 12rpx;
						/* #endif */
					}

					.search-input {
						flex: 1;
						font-size: 28rpx;
						color: #333;
						min-width: 0; // 确保input能正确收缩

						/* #ifdef H5 */
						font-size: 30rpx;
						line-height: 70rpx;
						/* #endif */

						/* #ifdef MP */
						font-size: 26rpx;
						line-height: 56rpx;
						/* #endif */

						&::placeholder {
							color: #999;

							/* #ifdef H5 */
							font-size: 30rpx;
							/* #endif */

							/* #ifdef MP */
							font-size: 26rpx;
							/* #endif */
						}
					}
				}
			}
		}
	}
}

// 当前定位
.current-location {
	// background-color: #fff;
	margin-top: 8px;

	.location-item {
		padding: 16px;
		display: flex;
		align-items: center;

		.location-icon {
			margin-right: 8px;
		}

		.location-info {
			flex: 1;

			.location-label {
				color: #999;
				font-size: 14px;
			}

			.location-name {
				color: #333;
				font-size: 14px;
				margin-left: 4px;
			}
		}

		.relocate-btn {
			display: flex;
			align-items: center;

			.reload-icon {
				width: 32rpx;
				height: 32rpx;
				margin-right: 8rpx;
			}

			.relocate-text {
				color: #5577ff;
				font-size: 12px;
				margin-left: 4px;
			}
		}
	}
}

// 用户地址区域
.user-address-section {
	padding: 0 24rpx;
	margin-bottom: 20rpx;

	.address-item {
		width: 710rpx; // 355px * 2
		height: 410rpx; // 205px * 2
		background: #ffffff;
		border-radius: 16rpx; // 8px * 2
		padding: 30rpx 24rpx;

		.section-title {
			// padding: 35rpx 0 8rpx 0;
			width: 104rpx; // 52px * 2
			height: 36rpx; // 18px * 2
			font-family: PingFang SC, PingFang SC;
			font-weight: 400;
			font-size: 26rpx; // 13px * 2
			color: #999999;
			line-height: 36rpx; // 18px * 2
			text-align: left;
			font-style: normal;
			text-transform: none;
			margin-bottom: 36rpx;
		}

		.address-content {
			padding: 10rpx 0 10rpx 0;

			.address-name {
				// width: 168rpx; // 84px * 2
				height: 40rpx; // 20px * 2
				font-family: PingFang SC, PingFang SC;
				font-weight: 400;
				font-size: 28rpx; // 14px * 2
				color: #333333;
				line-height: 40rpx; // 20px * 2
				text-align: left;
				font-style: normal;
				text-transform: none;
				margin-bottom: 36rpx;

				&:last-child {
					margin-bottom: 0;
				}
			}
		}
	}
}

// 商圈区域
.business-section {
	padding: 0 24rpx;
	margin-bottom: 20rpx;

	.business-card {
		background: #ffffff;
		border-radius: 16rpx;
		padding: 30rpx 24rpx;
		//	height: 606rpx; // 303px * 2

		.title-filter-row {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 36rpx;

			.section-title {
				font-family: PingFang SC, PingFang SC;
				font-weight: 400;
				font-size: 26rpx;
				color: #999999;
				line-height: 36rpx;
				text-align: left;
				font-style: normal;
				text-transform: none;
			}

			.distance-filter {
				display: flex;
				align-items: center;

				.filter-label {
					font-size: 24rpx;
					color: #999999;
					margin-right: 20rpx;
				}

				.distance-tag {
					padding: 8rpx 16rpx;
					margin-right: 16rpx;
					font-size: 24rpx;
					color: #999999;
					background-color: #f8f8f8;
					border-radius: 20rpx;

					&:last-child {
						margin-right: 0;
					}

					&.active {
						color: #2a7efb;
						background-color: rgba(85, 119, 255, 0.1);
					}
				}
			}
		}

		.business-grid {
			display: flex;
			flex-wrap: wrap;
			justify-content: flex-start;

			.business-item {
				width: 200rpx;
				min-height: 64rpx;
				background-color: #f8f8f8;
				border-radius: 32rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				margin-bottom: 24rpx;
				margin-right: 24rpx;
				padding: 8rpx 16rpx;

				// 每行第三个元素不需要右边距
				&:nth-child(3n) {
					margin-right: 0;
				}

				.business-info {
					display: flex;
					flex-direction: column;
					align-items: center;

					.business-name {
						font-family: PingFang SC, PingFang SC;
						font-weight: 400;
						font-size: 28rpx;
						color: #333333;
						line-height: 40rpx;
						text-align: center;
						margin-bottom: 4rpx;

						// 超长文本省略号
						overflow: hidden;
						white-space: nowrap;
						text-overflow: ellipsis;
						max-width: 168rpx; // 限制最大宽度
					}

					.business-distance {
						font-size: 24rpx;
						color: #999999;
						line-height: 32rpx;
					}
				}
			}
		}
	}
}
// 位置列表
.location-list {
	background-color: #fff;
	margin-top: 8px;

	.location-item {
		padding: 16px;
		display: flex;
		align-items: center;
		justify-content: space-between;
		border-bottom: 1px solid #f5f5f5;

		&:last-child {
			border-bottom: none;
		}

		.location-name {
			color: #333;
			font-size: 14px;
			flex: 1;
		}

		.location-distance {
			color: #666;
			font-size: 12px;
		}
	}
}

// 地图容器 复用在 暂无商圈
.map-container {
	padding: 0 24rpx;
	margin-bottom: 20rpx;

	.map-placeholder {
		background: #ffffff;
		border-radius: 16rpx;
		padding: 30rpx 24rpx;
		height: 606rpx; // 303px * 2
		display: flex;
		align-items: center;
		justify-content: center;

		.map-content {
			display: flex;
			flex-direction: column;
			align-items: center;

			// .location-icon {
			// 	width: 240rpx;
			// 	height: 240rpx;
			// 	opacity: 0.3;
			// }

			.map-text {
				width: 170rpx; // 85px * 2
				height: 36rpx; // 18px * 2
				font-weight: 400;
				font-size: 26rpx; // 13px * 2
				color: #999999;
				line-height: 36rpx; // 18px * 2
				text-align: left;
				font-style: normal;
				text-transform: none;
			}
		}
	}
}
</style>
