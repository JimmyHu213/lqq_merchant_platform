<template>
	<view id="home" :data-theme="theme" :style="[pageStyle]">
		<tui-skeleton v-if="showSkeleton"></tui-skeleton>
		<!-- 有网内容-->
		<view v-if="!errorNetwork">
			<!-- 开屏广告 -->
			<advertisement v-if="isShowGuide" :advData="advData" @getShowIndexDiy="getShowIndexDiy">
			</advertisement>
			<!-- diyPage -->
			<view v-if="showIndexDiy" class="page-index tui-skeleton page_count 70ba6a" :class="{'bgf':navIndex >0}"
				:style="{visibility: showSkeleton ? 'hidden' : 'visible'}">
 <diy-page ref="diyComponentRef" :regionIds="regionIds" :isScrolled="isScrolled"
					:componentData="componentData" v-if="componentData && componentData.value"
					@getDiyData="getDiyData"></diy-page> 
			</view>
		</view>
		<!-- 断网内容 -->
		<view v-else>
			<view class="error-network af90db">
				<image class="img" src="./error-network.png"></image>
				<view class="title">网络连接断开</view>
				<view class="con">
					<view class="label">请检查情况：</view>
					<view class="item">· 在设置中是否已开启网络权限</view>
					<view class="item">· 当前是否处于弱网环境</view>
					<view class="item">· 版本是否过低，升级试试吧</view>
				</view>
				<view class="btn" @click="reconnect">重新连接</view>
			</view>
		</view>
		<!-- 备案号、授权信息 -->
		<!-- #ifdef H5 -->
		<view v-show="(globalData.authorizeFilingNum || globalData.authorizeInfo) && !isShowGuide"
			class="mt-40 mb-40rpx">
			<view @click="toInternet" class="text-center text-24rpx text--w111-666 mb-24 3cfea756">
				{{globalData.authorizeFilingNum}}
	</view>
	<view class="text-center text-24rpx text--w111-666 48bfb4a">{{globalData.authorizeInfo}}</view>
	</view>
	<!-- #endif -->

	<!-- 底部导航距离，做兼容处理的-->
	<view v-if="bottomNavigationIsCustom && !isShowGuide" class="footerBottom"></view>

	<page-footer v-show="!isShowGuide" :guidePages="guidePages"></page-footer>
	<!-- 优惠券弹窗 -->
	<coupon-dialog :urlDomain="urlDomain" v-if="couponModal" :couponModal="couponModal"
		@on-close="closeDialog"></coupon-dialog>
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
	import Cache from '../../utils/cache';
	import advertisement from '../guide/guide.vue'
	import tuiSkeleton from '@/components/base/tui-skeleton.vue';
	import pageFooter from '@/components/pageFooter/index.vue';
	import {
		getIndexData,
		getAppVersion,
		getDiy,
		getOpenAdvApi
	} from '@/api/api.js';
	// #ifdef MP-WEIXIN || APP-PLUS
	import {
		getTemlIds
	} from '@/api/api.js';
	// #endif
	import {
		mapGetters
	} from "vuex";
	import {
		silenceBindingSpread,
	} from '@/utils/index.js';
	import animationType from '@/utils/animationType.js'
	import onShare from "@/mixins/onShare";
	import diyPage from "@/subPackage/pages/diyPage/index.vue";
  import couponDialog from "../../subPackage/pages/diyPage/couponDialog";
	const arrTemp = ["beforePay", "afterPay", "createBargain", "pink"];
	let app = getApp();
	export default {
		mixins: [onShare],
		computed: {
			...mapGetters(['isLogin', 'uid', 'globalData', 'bottomNavigationIsCustom', 'isAdvertisement']),
			isShowGuide() {
				return this.guidePages && this.isAdvertisement
			}
		},
		components: {
			tuiSkeleton,
			diyPage,
			pageFooter,
			advertisement,
      couponDialog
		},
		watch: {
			regionIds: {
				handler(newValue, oldValue) {
					if (newValue && newValue !== oldValue) {
						this.getDiyData(0, false);
					}
				},
				immediate: true
			}
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				isScrolled: false, //是否开始滚动
				showSkeleton: true, //骨架屏显示隐藏
				isNodes: 0, //控制什么时候开始抓取元素节点,只要数值改变就重新抓取
				navIndex: 0, //判断首页显示内容，1显示分类页和商品，0首页
				theme: this.$Cache.get('theme'),
				imgHost: '', //图片域名地址
				appUpdate: {},
				domOffsetTop: 50,
				couponModal: false,
				styleConfig: [], //DIY数据
				pageStyle: {},
				errorNetwork: false, //是否有网络
				guidePages: false, //是否展示开屏广告
				advData: [],
				showIndexDiy: false, //首页diy内容
				regionIds: '', // 圈层ID集合，逗号分隔，传递给goodList组件
				componentData: {}
			}
		},
		//下拉刷新
		onPullDownRefresh() {
			// #ifdef APP-PLUS
			setTimeout(() => {
				this.$util.navigateTo('/pages/index/index');
				uni.stopPullDownRefresh();
			}, 1000)
			// #endif
		},
		onLoad(options) {
			if (!this.isAdvertisement) this.showIndexDiy = true
			this.loadExecution()
			// #ifdef APP-PLUS
			//app刚进入检测有无网络
			this.snycNetWork();
			//APP版本检测
			this.appVersionConfig();
			// #endif

			//初始化圈层ID
			this.initRegionIds();

			//获取浏览器id
			this.getOptionData(options)

			//首页数据加载
			this.getIndexConfig();

			// #ifdef MP || APP-PLUS
			this.getTemlIds()
			// #endif
		},
		onShow() {
			// 更新圈层ID（从圈层选择页面返回时）
			this.initRegionIds();

			// 通知组件位置可能已变化
      if(this.regionIds){
        this.$nextTick(() => {
          uni.$emit('locationChanged');
        });
      }

			let self = this;
			// #ifdef APP-PLUS
			setTimeout(() => {
				if (self.appUpdate.openUpgrade == 'true') {
					self.appVersionConfig();
				}
			}, 1000)
			// #endif

			//新人礼的展示判断
			let newGift = self.$Cache.get('newGift') ? JSON.parse(self.$Cache.get('newGift')) : [];
			self.couponModal = newGift.length ? true : false;
		},
		// 滚动监听
		onPageScroll(e) {
			// 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
			uni.$emit('scroll');
			if (e.scrollTop > this.domOffsetTop) {
				this.isScrolled = true;
			}
			if (e.scrollTop < this.domOffsetTop) {
				this.$nextTick(() => {
					this.isScrolled = false;
				});
			}
		},
		methods: {
			// 初始化圈层ID参数
			initRegionIds() {
				try {
					const areasInfo = uni.getStorageSync('areas_info') || [];
					if (Array.isArray(areasInfo) && areasInfo.length > 0) {
						// 提取所有圈层ID，用逗号分隔
						this.regionIds = areasInfo
							.filter(area => area.id) // 过滤掉没有id的项
							.map(area => area.id)
							.join(',');
					} else {
						this.regionIds = '';
					}
				} catch (error) {
					console.error('页面获取圈层ID失败:', error);
					this.regionIds = '';
				}
			},

			//开屏广告结束回调展示首页diy
			getShowIndexDiy() {
				this.showIndexDiy = true
				this.guidePages = false
				// uni.showTabBar();
			},
			// 获取开屏广告信息
			loadExecution() {
				const tagDate = uni.getStorageSync('guideDate') || 0,
					nowDate = new Date().getTime();
				getOpenAdvApi().then(res => {
					if (res.data.splashAdSwitch == 0 || res.data.adList.length == 0) {
						this.showIndexDiy = true
						// uni.switchTab({
						//   url: '/pages/index/index'
						// });
					} else if (res.data.splashAdSwitch == 1 && res.data.adList.length) {
						this.advData = res.data
						uni.hideTabBar()
						// splashAdShowInterval 开屏广告展示间隔，单位：小时, splashAdShowTime:开屏广告展示时间，单位：秒, splashAdSwitch:开屏广告开关
						let intervalTime = parseFloat(res.data.splashAdShowInterval) * 60 * 60 * 1000 || 0;
						uni.setStorageSync('intervalTime', intervalTime);
						uni.setStorageSync('guideDate', new Date().getTime());
						if ((nowDate - tagDate) <= uni.getStorageSync('intervalTime')) {
							this.showIndexDiy = true
							this.guidePages = false
							return
						}
						this.guidePages = true
					} else {
						this.showIndexDiy = true
						this.guidePages = false
						// uni.showTabBar();
					}
				}).catch(err => {
					this.showIndexDiy = true
				})
			},
			//获取扫码或者浏览器传来的值
			getOptionData(options) {
				let diyid = 0;
				if (options.hasOwnProperty('id') || options.scene) {
					if (options.scene) { // 仅仅小程序扫码进入
						let value = this.$util.getUrlParams(decodeURIComponent(options.scene));
						if (value.sd) this.$store.commit('Change_Spread', value.sd);
						//分销绑定
						silenceBindingSpread(this.isLogin, value.sd);
						diyid = value.id ? value.id : 0;
					} else {
						diyid = options.id ? options.id : 0;
						if (options.sd) this.$store.commit('Change_Spread', options.sd);
						//分销绑定
						silenceBindingSpread(this.isLogin, options.sd);
					}
				}
				//diy数据加载
				this.getDiyData(diyid, false);
			},
			//备案授权跳转
			toInternet() {
				window.open('https://beian.miit.gov.cn/#/Integrated/index')
			},
			//app刚进入检测有无网络
			snycNetWork() {
				uni.getNetworkType({
					success: res => {
						//res.networkType === 'none'无网络
						this.errorNetwork = res.networkType === 'none';
						//如果没有网络，清除骨架屏
						if (this.errorNetwork) this.reloadData();
					}
				});
			},
			// 断网后重新链接
			reconnect() {
				uni.getNetworkType({
					success: res => {
						this.errorNetwork = res.networkType === 'none';
						if (!this.errorNetwork) {
							setTimeout(() => {
								this.$util.navigateTo('/pages/index/index');
							}, 1000)
						}
					}
				});
			},
			/**
			 * 获取DIY
			 * @param {number} id
			 * @param {boolean} type 区分是否是微页面
			 */
			getDiyData(id, type) {
				this.componentData = null
				this.styleConfig = []
				uni.showLoading({
					title: '加载中...'
				});
				getDiy({
					id: id,
					circleIds: this.regionIds
				}).then(res => {
					this.errorNetwork = false;
					let data = res.data;
					uni.setNavigationBarTitle({
						title: data.title
					});
					uni.setNavigationBarColor({
						//必须要16进制小写
						frontColor: data.titleColor,
						backgroundColor: data.titleBgColor.toString().toLowerCase(),
					})

					this.componentData = data
					this.pageStyle = {
						'background-color': data.isBgColor === 1 ? res.data.colorPicker : '',
						'background-image': data.isBgPic === 1 ? `url(${res.data.bgPic})` : '',
						'background-repeat': res.data.bgTabVal === 1 ? 'repeat-y' : 'no-repeat',
						'background-size': res.data.bgTabVal === 2 ? 'cover' : 'contain'
					};
					uni.hideLoading();
					setTimeout(() => {
						this.isNodes++;
					}, 100);
				}).catch(err => {
					uni.hideLoading();
					return this.$util.Tips({
						title: err
					});
				});
			},
			//清除骨架屏
			reloadData() {
				this.showSkeleton = false;
			},
			xieyiApp() {
				uni.navigateTo({
					url: '/pages/users/web_page/index?webUel=https://admin.java.crmeb.net/useragreement/xieyi.html&title=协议内容'
				})
			},
			// #ifdef APP-PLUS
			xieyiApp() {
				uni.navigateTo({
					animationType: animationType.type,
					animationDuration: animationType.duration,
					url: '/pages/users/web_page/index?webUel=https://admin.java.crmeb.net/useragreement/xieyi.html&title=协议内容'
				})
			},
			// #endif
			// #ifdef MP || APP-PLUS
			getTemlIds() {
				for (var i in arrTemp) {
					this.getTem(arrTemp[i]);
				}
			},
			getTem(data) {
				getTemlIds({
					type: data
				}).then(res => {
					if (res.data) {
						let arr = res.data.map((item) => {
							return item.tempId
						})
						wx.setStorageSync('tempID' + data, arr);
					}
				});
			},
			// #endif
			// 首页数据
			getIndexConfig: function() {
				let that = this;
				getIndexData().then(res => {
					let imgHost = res.data.logoUrl.split('crmebimage')[0];
					that.imgHost = imgHost;
					that.$Cache.set('imgHost', imgHost + '/');
					// #ifdef H5 || APP-PLUS
					that.$store.commit("SET_CHATURL", res.data.yzfUrl);
					Cache.set('chatUrl', res.data.yzfUrl);
					// #endif
					Cache.setItem({
						name: 'platChatConfig',
						value: {
							servicePhone: res.data.consumerHotline, //客服电话
							serviceLink: res.data.consumerH5Url, //云智服
							serviceType: res.data.consumerType //客服类型四选一
						}
					});
					this.reloadData();
				}).catch(err => {
					return this.$util.Tips({
						title: err
					});
				});
			},
			appVersionConfig() {
				var that = this;
				//app升级
				// 获取本地应用资源版本号
				getAppVersion().then(res => {
					that.$set(that.appUpdate, 'androidAddress', res.data.androidAddress);
					that.$set(that.appUpdate, 'appVersion', res.data.appVersion);
					that.$set(that.appUpdate, 'iosAddress', res.data.iosAddress);
					that.$set(that.appUpdate, 'openUpgrade', res.data.openUpgrade);
					plus.runtime.getProperty(plus.runtime.appid, function(inf) {
						let nowVersion = (inf.version).split('.').join('');
						let appVersion = (res.data.appVersion).split('.').join('');
						uni.getSystemInfo({
							success: (res) => {
								if (appVersion > nowVersion) {
									uni.showModal({
										title: '更新提示',
										content: '发现新版本，是否前去下载?',
										showCancel: that.appUpdate.openUpgrade == '1' ?
											true : false,
										cancelColor: '#eeeeee',
										confirmColor: '#f55850',
										success(response) {
											if (response.confirm) {
												switch (res.platform) {
													case "android":
														plus.runtime.openURL(that
															.appUpdate
															.androidAddress);
														break;
													case "ios":
														plus.runtime.openURL(encodeURI(
															that.appUpdate
															.iosAddress));
														break;
												}

											}
										}
									});
								}
							}
						})
					});
				})
			},
			// 微信分享；
			setOpenShare: function(data) {
				let that = this;
				if (that.$wechat.isWeixin()) {
					let configAppMessage = {
						desc: this.globalData.companyName,
						title: this.globalData.companyName,
						link: location.href
					};
					that.$wechat.wechatEvevt(["updateAppMessageShareData", "updateTimelineShareData"],
						configAppMessage);
				}
			},
			stopTouchMove() {
				return true //禁止新闻swiper手动滑动
			},
			closeDialog() {
				this.couponModal = false;
				this.$Cache.clear('newGift');
			},
		},
		onHide() {
			this.guidePages = false
			// uni.showTabBar();
		},
		onReachBottom() {
			this.$refs.diyComponentRef.getRecommend();
		}
	}
</script>
<style>
	page {
		height: auto;
		display: flex;
		flex-direction: column;
		height: 100%;
	}
</style>
<style lang="scss" scoped>
	.error-network {
		position: fixed;
		left: 0;
		top: 0;
		display: flex;
		flex-direction: column;
		align-items: center;
		width: 100%;
		height: 100%;
		padding-top: 40rpx;
		background: #fff;
		padding-top: 30%;

		.img {
			width: 414rpx;
			height: 336rpx;
		}

		.title {
			position: relative;
			top: -40rpx;
			font-size: 32rpx;
			color: #666;
		}

		.con {
			font-size: 24rpx;
			color: #999;

			.label {
				margin-bottom: 20rpx;
			}

			.item {
				margin-bottom: 20rpx;
			}
		}

		.btn {
			display: flex;
			align-items: center;
			justify-content: center;
			width: 508rpx;
			height: 86rpx;
			margin-top: 100rpx;
			border: 1px solid #d74432;
			color: #e93323;
			font-size: 30rpx;
			border-radius: 120rpx;
		}
	}

	.ysize {
		background-size: 100%;
	}

	.fullsize {
		background-size: 100% 100%;
	}

	.repeat {
		background-repeat: repeat;
	}

	.noRepeat {
		background-repeat: no-repeat;
	}

	.noCommodity {
		margin-top: 30%;
	}

	.icon-gengduo1 {
		color: #F8F8F8;
	}

	.pageIndex {
		padding: 0 24rpx;
	}



	.page-index {
		display: flex;
		flex-direction: column;
		min-height: 100%;


	}

	.fixed {
		z-index: 100;
		position: fixed;
		left: 0;
		top: 0;
		background: linear-gradient(90deg, red 50%, #ff5400 100%);

	}

	.menu-txt {
		font-size: 24rpx;
		color: #454545;
	}
</style>