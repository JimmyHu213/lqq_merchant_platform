<template>
	<view :data-theme="theme" class="area-select-page">
		<view v-if="circleData.length" class="circle-layer-container">
			<view class="circle-layer-item" v-for="(province, index) in circleData" :key="index">
				<view class="province-header">
					<text class="province-name" :class="{ active: province.id === selectedCircle.provinceId }" @click="selectProvince(province)">
						{{ province.name }}
						<text class="iconfont icon-ic_rightarrow"></text>
					</text>
					<text class="expand-icon" @click="toggleProvince(province)">
						{{ province.isShow ? '收起' : '展开' }}
						<text class="iconfont" :class="province.isShow ? 'icon-ic_uparrow' : 'icon-ic_downarrow'"></text>
					</text>
				</view>

				<transition name="slide-down">
					<view class="city-list" v-if="province.isShow" key="city-list">
						<view class="city-container" v-for="(city, cityIndex) in province.childList" :key="cityIndex">
							<view class="city-header" @click="selectCity(province.id, city)">
								<text class="city-name" :class="{ active: city.id === selectedCircle.cityId }">{{ city.name }}
								<text class="iconfont icon-ic_rightarrow"></text>
								</text>
							</view>

							<transition name="fade-in">
								<view class="district-list" v-if="city.childList" key="district-list">
									<view
										class="district-item"
										v-for="(district, districtIndex) in city.childList"
										:key="districtIndex"
										@click="selectDistrict(province.id, city.id, district)"
										:class="{
											active: district.id === selectedCircle.districtId
										}"
									>
										{{ district.name }}
									</view>
								</view>
							</transition>
						</view>
					</view>
				</transition>
			</view>
		</view>
		<!-- 无数据状态 -->
		<view v-if="!circleData.length" class="empty-state">
			<image class="empty-icon" :src="`${urlDomain}crmebimage/presets/noshop.png`" mode="aspectFit"></image>
			<text class="empty-text">附近暂无商圈~</text>
		</view>
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
import { circleAreasTreeApi } from '@/api/public';

let app = getApp();

export default {
	computed: {
		...mapGetters(['isLogin'])
	},
	data() {
		return {
			theme: app.globalData.theme,
			// 选中的圈层信息
			selectedCircle: {
				provinceId: '',
				cityId: '',
				districtId: ''
			},
			// 假数据 - 圈层数据
			circleData: [],
			urlDomain: ''
		};
	},
	onLoad() {
		// 初始化数据
		this.getCircleAreasTree();
		this.urlDomain = this.$Cache.get('imgHost') || '';
	},
	methods: {
		// 获取商圈树
		async getCircleAreasTree() {
			const res = await circleAreasTreeApi();
			// 给每个省份添加 isShow 属性
			this.circleData = res.data.map((province) => ({
				...province,
				isShow: true
			}));
		},
		// 切换省份展开/收起状态
		toggleProvince(province) {
			// 如果当前省份要展开，先收起所有其他省份
			if (!province.isShow) {
				this.selectedCircle.provinceId = province.id;
				// 重置城市和区域选择
				this.selectedCircle.cityId = '';
				this.selectedCircle.districtId = '';
			}
			province.isShow = !province.isShow;
		},
		selectProvince(province) {
			this.selectedCircle.provinceId = province.id;
			this.selectedCircle.cityId = '';
			this.selectedCircle.districtId = '';
			// 保存到缓存
			let selectedArea = {
				name: province.name,
				id: province.id
			};
			uni.setStorageSync('areas_info', [selectedArea]);
			this.$nextTick((e) => {
				uni.navigateBack();
			});
		},
		// 选择城市
		selectCity(provinceId, city) {
			this.selectedCircle.provinceId = provinceId;
			this.selectedCircle.cityId = city.id;
			// 重置区域选择
			this.selectedCircle.districtId = '';
			let selectedArea = {
				name: city.name,
				id: city.id
			};
			// 保存到缓存
			uni.setStorageSync('areas_info', [selectedArea]);
			this.$nextTick((e) => {
				uni.navigateBack();
			});
		},

		// 选择区域（只有区域可以被最终选中）
		selectDistrict(provinceId, cityId, district) {
			this.selectedCircle.provinceId = provinceId;
			this.selectedCircle.cityId = cityId;
			this.selectedCircle.districtId = district.id;
			let selectedArea = {
				name: district.name,
				id: district.id
			};
			// 保存到缓存
			uni.setStorageSync('areas_info', [selectedArea]);
			this.$nextTick((e) => {
				uni.navigateBack();
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.area-select-page {
	padding: 20rpx;
	background-color: #ffffff;
	min-height: 100vh;
	.iconfont{
		font-size: 26rpx;
	}
}

.circle-layer-container {
	background-color: #fff;
	border-radius: 12rpx;
	overflow: hidden;
	margin-bottom: 20rpx;
}

.circle-layer-item {
	&:last-child {
		border-bottom: none;
	}
}

.province-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 24rpx 30rpx;
	font-size: 30rpx;
	border-bottom: 1rpx solid #f5f5f5;
}

.province-name {
	font-weight: 500;
	color: #333;
	.iconfont{
		color: #333;
		font-weight: 500;
	}
	&.active {
		color: #1989fa;
		.iconfont{
			color: #1989fa;
		}
	}
}

.expand-icon {
	color: #999;
	font-size: 24rpx;
}

.city-list {
	padding: 0;
}

.city-container {
	border-bottom: 1rpx solid #ffffff;
}

.city-container:last-child {
	border-bottom: none;
}

.city-header {
	padding: 20rpx 30rpx 20rpx 30rpx;
	display: flex;
	justify-content: space-between;
	align-items: center;
	background-color: #ffffff;
}

.city-name {
	color: #666;
	font-size: 28rpx;
	flex: 1;
	display: flex;
	align-items: center;
}

.city-name.active {
	color: #2A7EFB;
	font-weight: 400;
	
}

.district-list {
	background-color: #ffffff;
	display: flex;
	flex-wrap: wrap;
	padding: 0rpx 30rpx;
}

.district-item {
	padding: 15rpx 30rpx;
	margin: 10rpx 20rpx 10rpx 0;
	border: 1rpx solid #f5f5f5;
	border-radius: 30rpx;
	color: #333;
	font-size: 24rpx;
	background-color: #f5f5f5;
	min-width: 120rpx;
	text-align: center;

	&.active {
		background: rgba(42, 126, 251, 0.07);
		border-color: #2a7efb;
		color: #2a7efb;
		font-weight: bold;
	}

	&:last-child {
		margin-right: 0;
	}
}

/* 展开收起动画样式 */
.slide-down-enter-active,
.slide-down-leave-active {
	transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
	overflow: hidden;
}

.slide-down-enter-from,
.slide-down-leave-to {
	max-height: 0;
	opacity: 0;
	transform: translateY(-10rpx);
}

.slide-down-enter-to,
.slide-down-leave-from {
	max-height: 2000rpx;
	opacity: 1;
	transform: translateY(0);
}

/* 区域列表动画样式 */
.fade-in-enter-active,
.fade-in-leave-active {
	transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
	overflow: hidden;
}

.fade-in-enter-from,
.fade-in-leave-to {
	max-height: 0;
	opacity: 0;
	transform: translateY(-5rpx);
}

.fade-in-enter-to,
.fade-in-leave-from {
	max-height: 800rpx;
	opacity: 1;
	transform: translateY(0);
}

/* 展开图标旋转动画 */
.expand-icon {
	transition: all 0.3s;
}

.expand-icon .iconfont {
	display: inline-block;
	transition: transform 0.3s;
}

.rotate-180 {
	transform: rotate(180deg);
}

/* 防止布局跳动的优化 */
.circle-layer-item {
	will-change: height;
	backface-visibility: hidden;
	transform: translateZ(0);
}

.city-list,
.district-list {
	will-change: max-height, opacity;
	backface-visibility: hidden;
}
.empty-state {
  margin-top: 50%;
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
	}
}
</style>
