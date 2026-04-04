<template>

	<view :data-theme="theme">
		<!-- #ifdef APP-PLUS -->
		<view class="sys-head fixed" :style="{ height: sysHeight+'px' }"></view>
		<!--  #endif -->
		<view class="page" :style="{top: sysHeight + 'px'}">
			<view class="sign-record borderPad">
				<!-- 次卡商品信息 -->
				<view class="card-bg mb-20">
					<ver-goods-info v-if="orderInfoList[0]" :orderInfoList="orderInfoList" :isPunchCard="isPunchCard"
						:type="type">
					</ver-goods-info>
				</view>
				<!-- 核销记录 -->
				<view class="card-bg " v-if="verificationRecordList.length">
					<ver-record :verificationRecordList="verificationRecordList" :isPunchCard="isPunchCard">
					</ver-record>
				</view>
				<view v-if="verificationRecordList.length" class='loadingicon acea-row row-center-wrapper'>
					到底了~
				</view>
			</view>
			<view class='noCommodity' v-if="!verificationRecordList.length">
				<view class='pictrue text-center'>
					<image :src="urlDomain+'crmebimage/presets/noqiandao.png'"></image>
					<view class="default_txt">暂无核销记录哦~</view>
				</view>
			</view>
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
	import {
		getSignMonthList
	} from '@/api/user.js';
	import {
		getOrderDetail,
	} from '@/api/order.js';
	import {
		employeeOrderInfo,
	} from '@/api/work.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import verRecord from '@/subPackage/pages/verRecord/index.vue'
	import verGoodsInfo from '@/subPackage/pages/verGoodsInfo/index.vue'
	import {
		mapGetters
	} from "vuex";
	let app = getApp();

	export default {
		components: {
			verGoodsInfo,
			verRecord,
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				orderNo: '', // 订单号
				type: 'users', // 核销记录类型 users：用户核销记录，admin：管理核销记录
				isPunchCard: true, // 是否为次卡商品
				verificationRecordList: [], // 核销记录
				orderInfoList: [], // 订单信息列表
				punchCardOrderInfo: {}, // 次卡商品信息
				sysHeight: 0, // 系统状态栏高度
				theme: app.globalData.theme,
			};
		},
		computed: {
			...mapGetters(['isLogin']),
		},
		watch: {
			isLogin: {
				handler: function(newV, oldV) {
					if (newV) {
						this.getOrderInfo();
					}
				},
				deep: true
			}
		},
		onLoad(options) {
			// #ifdef APP-PLUS
			this.sysHeight = uni.getSystemInfoSync().statusBarHeight;
			//  #endif 
			if (this.isLogin) {
				if (!options.orderNo) return this.$util.Tips({
					title: '缺少参数'
				}, {
					tab: 3,
					url: 1
				});
				this.$set(this, 'type', options.type);
				this.$set(this, 'orderNo', options.orderNo);
				this.getOrderInfo();
			} else {
				toLogin();
			}
		},
		methods: {
			/**
			 * 获取订单详细信息
			 *
			 */
			getOrderInfo: function() {
				let that = this;
				uni.showLoading({
					title: "正在加载中"
				});
				if (this.type == 'admin') {
					employeeOrderInfo(that.orderNo).then(res => {
						uni.hideLoading();
						let data = res.data;
						that.$set(that, 'verificationRecordList', data.verificationRecordList)
						that.$set(that, 'orderInfoList', data.orderDetailList)
						that.$set(that, 'isPunchCard', data.secondType == 8)
					}).catch(err => {
						that.$util.Tips({
							title: err
						}, {
							tab: 3,
							url: '/pages/user/index'
						});
					});
				} else if (this.type == 'users') {
					getOrderDetail(that.orderNo).then(res => {
						uni.hideLoading();
						let data = res.data;
						that.$set(that, 'verificationRecordList', data.verificationRecordList)
						that.$set(that, 'orderInfoList', data.merchantOrderList[0].orderInfoList)
						that.$set(that, 'isPunchCard', data.secondType == 8)
					}).catch(err => {
						that.$util.Tips({
							title: err
						}, {
							tab: 3,
							url: '/pages/user/index'
						});
					});
				}
			},
		}
	}
</script>

<style lang="scss">
	.sys-head {
		width: 100%;
		z-index: 99;
		background: #f5f5f5;
	}

	.page {
		position: relative;
	}

	.sign-record {
		padding-top: 24rpx;

		.card-bg {
			background: #FFFFFF;
			border-radius: 24rpx 24rpx 24rpx 24rpx;
			padding: 32rpx 24rpx;
		}
	}

	.font_color {
		@include main_color(theme);
	}

	.default_txt {
		font-size: 26rpx;
		color: #999;
		text-align: center;
	}

	.record-number {
		font-size: 14px;
		color: #282828;
		margin-bottom: 5px;
	}

	.noCommodity {
		// margin-top: 30%;
	}

	.color-333 {
		color: #333;
	}

	.color-666 {
		color: #666;
	}

	.pt-32 {
		padding-top: 32rpx;
	}
</style>