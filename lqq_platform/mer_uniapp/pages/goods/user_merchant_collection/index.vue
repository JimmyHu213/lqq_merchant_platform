<template>
	<view :data-theme="theme">
		<view class="top-tab flex borderPad fs-22">
			<view v-for="(item, index) in topTabList" :key="index" class="top-tab-item flex-center"
				:class="{'active-top-item': index == topTabIndex}" @click="onchangeTopTab(index)">
				<text>{{item}}</text>
			</view>
		</view>
		<!-- 关注列表 -->
		<view v-if="topTabIndex == 0 && storeList.length">
			<!--      :style="{ height: windowHeight + 'px' }"-->
			<scroll-view :scroll-y="isScroll">
				<block :key="index" v-for="(item, index) in storeList">
					<view @click="goStore(item.merId)" :data-index="index" class="order-item" @touchstart="drawStart"
						@touchmove="drawMove" @touchend="drawEnd" :style="{ right: item.right + 'rpx'}">
						<view class="content">
							<!-- #ifdef MP -->
							<image :src="item.merAvatar" mode=""></image>
							<!-- #endif -->
							<!-- #ifdef H5 || APP-PLUS -->
							<easy-loadimage mode="widthFix" :image-src="item.merAvatar" radius="16rpx"></easy-loadimage>
							<!-- #endif -->
							<view class="info">
								<view class="info_head flex">
									<span class="self" v-if="item.isSelf === true">自营</span>
									<text class="name line1">{{item.merName}}</text>
								</view>
								<view class="collection">
									{{item.collectNum}}人关注
								</view>
							</view>
						</view>
						<view class="remove" @click.stop="bindDetele(item.merId, index)">取消关注</view>
					</view>
				</block>
			</scroll-view>
			<view class='loadingicon acea-row row-center-wrapper'>
				<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
			</view>
		</view>
		<!-- 入会列表 -->
		<view v-if="topTabIndex == 1 " class="">
			<scroll-view :scroll-y="isScroll">
				<block :key="index" v-for="(item, index) in storeList">
					<view @click="goStore(item.merId)" class="order-item">
						<view class="content">
							<!-- #ifdef MP -->
							<image :src="item.merAvatar" mode=""></image>
							<!-- #endif -->
							<!-- #ifdef H5 || APP-PLUS -->
							<easy-loadimage mode="widthFix" :image-src="item.merAvatar" radius="16rpx"></easy-loadimage>
							<!-- #endif -->
							<view class="info">
								<view class="info_head flex">
									<span class="self" v-if="item.isSelf === true">自营</span>
									<text class="name line1">{{item.merName}}</text>
								</view>
								<view class="collection">
									{{item.collectNum}}人入会
								</view>
							</view>
						</view>
					</view>
				</block>
			</scroll-view>
			<view class='loadingicon acea-row row-center-wrapper'>
				<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
			</view>
		</view>
		<view class='noCommodity' v-if="!storeList.length && page > 1 &&!loading">
			<view class='pictrue mb30'>
				<image :src="urlDomain+'crmebimage/presets/noguanzhu.png'"></image>
				<view class="text-ccc">收藏列表为空哦~</view>
			</view>
			<recommend ref="recommendIndex"></recommend>
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
	let app = getApp();
	import {
		getMerCollectListApi,
		getMerCollectCancelApi,
		getMerAddMembershipListApi
	} from '@/api/merchant.js'
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	import recommend from "@/components/base/recommend.vue";
	export default {
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				delBtnWidth: 160,
				storeList: [],
				isScroll: true,
				theme: app.globalData.theme,
				windowHeight: 0,
				page: 1,
				limit: 20,
				loading: true,
				loadTitle: '',
				topTabIndex: 0, // 当前选中的tab索引
				topTabList: ['关注', '入会'], // 顶部tab列表
			};
		},
		components: {
			easyLoadimage,
			recommend
		},
		onLoad: function(options) {
			var that = this;
			wx.getSystemInfo({
				success: function(res) {
					that.windowHeight = res.windowHeight - 41;
				}
			});
			uni.setNavigationBarTitle({
				title: '店铺收藏'
			})
			this.getList()
		},
		methods: {
			returns: function() {
				uni.navigateBack()
			},
			drawStart: function(e) {
				var touch = e.touches[0];
				for (var index in this.storeList) {
					this.storeList[index].right = 0;
				}
				this.startX = touch.clientX;
			},
			drawMove: function(e) {
				var touch = e.touches[0];
				var item = this.storeList[e.currentTarget.dataset.index];
				var disX = this.startX - touch.clientX;
				if (disX >= 20) {
					if (disX > this.delBtnWidth) {
						disX = this.delBtnWidth;
					}
					this.isScroll = false;
					this.storeList[e.currentTarget.dataset.index].right = disX;
				} else {
					this.isScroll = false;
					this.storeList[e.currentTarget.dataset.index].right = 0;
				}
			},
			drawEnd: function(e) {
				var item = this.storeList[e.currentTarget.dataset.index];
				if (item.right >= this.delBtnWidth / 2) {
					this.isScroll = true;
					this.storeList[e.currentTarget.dataset.index].right = this.delBtnWidth;
				} else {
					this.isScroll = true;
					this.storeList[e.currentTarget.dataset.index].right = 0;
				}
			},
			goStore(id){
        this.$util.goToMerHome(id)
			},
			async getList() {
				try {
					if (!this.isScroll) return
					this.loading = true;
					this.loadTitle = "";
					const params = {
						page: this.page,
						limit: this.limit
					}
					let res = {}
					if (this.topTabIndex == 0) {
						res = await getMerCollectListApi(params)
					} else if (this.topTabIndex == 1) {
						res = await getMerAddMembershipListApi(params)
					}
					this.isScroll = res.data.list.length >= this.limit
					let loadend = res.data.list.length < this.limit;
					this.storeList = this.storeList.concat(res.data.list)
					this.loadend = loadend;
					this.loadTitle = this.storeList.length === 0 ? '' : loadend ? '到底了~~' : '显示更多';
					this.page += 1
					this.loading = false;
				} catch (err) {
					this.loading = false;
					return this.$util.Tips({
						title: res
					});
				}
			},
			// 删除收藏
			bindDetele(id, index) {
				getMerCollectCancelApi(id).then(res => {
					uni.showToast({
						title: '取消成功',
						icon: 'none'
					})
					this.storeList.splice(index, 1)
				}).catch(res => {
					this.loading = false;
					return this.$util.Tips({
						title: res
					});
				});
			},
			// 切换顶部tab
			onchangeTopTab(index) {
				this.topTabIndex = index
				this.isScroll = true
				this.page = 1
				this.storeList = []
				this.getList()
			}
		},
		onReachBottom() {
			this.getList();
			if (!this.storeList.length && this.page > 1) {
				this.$refs.recommendIndex.get_host_product();
			}

		},
		// 滚动监听
		onPageScroll(e) {
			// 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
			uni.$emit('scroll');
		}
	}
</script>

<style lang="scss" scoped>
	.order-item {
		height: 180rpx;
		width: 702rpx;
		display: flex;
		position: relative;
		transition: all 0.2s;
		margin-bottom: 50rpx;
		margin: 20rpx 24rpx;
		border-radius: 24rpx;
		background: #fff url('../static/images/storeBg.png') no-repeat;
		background-size: 100% 100%;
	}

	.remove {
		width: 130rpx;
		height: 100%;
		@include main_bg_color(theme);
		;
		color: white;
		position: absolute;
		top: 0;
		right: -160rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		z-index: 99;
		border-radius: 16rpx;
		margin-left: 15rpx;
	}

	.content {
		position: relative;
		display: flex;
		padding: 30rpx 24rpx;
		align-items: center;

		image,
		.easy-loadimage,
		image,
		uni-image {
			width: 100rpx;
			height: 100rpx;
			overflow: hidden;
		}

		.info {
			flex: 1;
			display: flex;
			flex-direction: column;
			justify-content: space-between;
			margin-left: 20rpx;
			position: relative;

			.name {
				width: 410rpx;
				font-weight: bold;
			}
		}
	}

	.info_head {
		align-items: center;
		.self {
			padding: 2rpx 6rpx;
			// background: #E93323;
			color: #fff;
			border-radius: 3rpx;
			margin-right: 10rpx;
			font-size: 18rpx;
			@include main_bg_color(theme)
		}
	}

	.collection {
		margin-top: 14rpx;
		font-size: 22rpx;
		color: #666666;
	}

	.top-tab {
		align-items: center;
		height: 84rpx;
		background: #FFFFFF;

		.top-tab-item {
			width: 120rpx;
			height: 52rpx;
			text-align: center;
			// line-height: 52rpx;
			background: #F5F5F5;
			border: 1rpx solid #F5F5F5;
			border-radius: 200rpx 200rpx 200rpx 200rpx;

			&+.top-tab-item {
				margin-left: 20rpx;
			}
		}

		.active-top-item {
			@include cate-two-btn(theme);
			@include main_color(theme);
			@include coupons_border_color(theme);
		}
	}
</style>