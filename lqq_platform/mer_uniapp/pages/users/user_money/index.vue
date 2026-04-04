<template>
	<view :data-theme="theme">
		<view class='my-account mb-40rpx pt-20 relative'>
      <!-- #ifdef MP -->
      <view class="relative">
        <nav-bar :isBackgroundColor="false" ref="navBarRef"  navTitle='我的账户' :isShowMenu="false" :backgroundColor="backgroundColor">
        </nav-bar>
      </view>
      <!-- #endif -->
			<view class='wrapper borderPad'>
				<view class='header borRadius14'>
					<view class='headerCon'>
						<view class='account acea-row row-top row-between'>
							<view class='assets'>
								<view>账户余额(元)</view>
								<view class='money semiBold'>{{statistics.nowMoney || 0}}</view>
							</view>
							<view v-if="statistics.rechargeSwitch"
								@click="openSubscribe('/pages/users/user_payment/index')"
								class='recharge font_color relative z-10'>充值</view>
						</view>
						<view class='cumulative acea-row row-middle'>
							<view class='item mr-80' v-if="statistics.rechargeSwitch">
								<view>余额充值(元)</view>
								<view class='money semiBold'>{{statistics.recharge || 0}}</view>
							</view>
							<view class='item'>
								<view>余额消费(元)</view>
								<view class='money semiBold'>{{statistics.monetary || 0}}</view>
							</view>
						</view>
            <view class="pictrue">
              <image :src="urlDomain+'crmebimage/presets/pig.png'"></image>
            </view>
					</view>
				</view>
				<view class='nav acea-row row-between-wrapper mt20'>
          <navigator class='item acea-row row-between-wrapper' hover-class='none' url='/pages/users/user_bill/index?type=expenditure'>
            <view class="left">
              <view class="name">消费记录</view>
              <view>赚积分抵现金</view>
            </view>
            <view class="pictrue">
              <image src="../static/images/xiaofeijilu.png"></image>
            </view>
          </navigator>
          <navigator v-if="statistics.rechargeSwitch"  class='item acea-row row-between-wrapper' hover-class='none' url='/pages/users/user_bill/index?type=recharge'>
            <view class="left">
              <view class="name">充值记录</view>
              <view>满减享优惠</view>
            </view>
            <view class="pictrue">
              <image src="../static/images/chongzhijilu.png"></image>
            </view>
          </navigator>
				</view>
			</view>
		</view>
    <recommend ref="recommendIndex" @getRecommendLength="getRecommendLength"></recommend>
    <view class='noCommodity' v-if="isNoCommodity">
      <view class='pictrue'>
        <image :src="urlDomain+'crmebimage/presets/noShopper.png'"></image>
      </view>
      <text class="text-ccc">暂无商品</text>
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
		getMyAccountApi
	} from '@/api/user.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import {
		alipayQueryPayResult
	} from '@/api/order.js';
	import recommend from "@/components/base/recommend.vue";
  import navBar from '@/components/navBar';
	let app = getApp();
	export default {
		components: {
			recommend,
      navBar
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				isClose: false,
				activity: {},
				hotScroll: false,
				statistics: {},
				hotPage: 1,
				hotLimit: 10,
				theme: app.globalData.theme,
				isNoCommodity: false,
        backgroundColor: '',
			};
		},
		computed: mapGetters(['isLogin', 'userInfo']),
		watch: {
			isLogin: {
				handler: function(newV, oldV) {
					if (newV) {
						this.userDalance();
					}
				},
				deep: true
			}
		},
		onLoad() {
			if (this.isLogin) {
				// #ifdef H5
				var url = window.location.search;
				if (url) {
					var theRequest = new Object();
					if (url.indexOf("?") != -1) {
						var str = url.substr(1);
						var strs = str.split("&");
						for (var i = 0; i < strs.length; i++) {
							theRequest[strs[i].split('=')[0]] = decodeURI(strs[i].split('=')[1]);
						}
					}
					this.orderId = theRequest.out_trade_no; //返回的订单号
					this.alipayQueryPay();
				}
				// #endif
				this.userDalance();
			} else {
				toLogin();
			}
		},
		onReachBottom() {
			this.$refs.recommendIndex.get_host_product();
		},
		// 滚动监听
		onPageScroll(e) {
			// 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
			uni.$emit('scroll');
      // #ifdef MP
      if (e.scrollTop > 50) {
        this.backgroundColor = '#fff';
        this.iconColor = '#333333'
      } else if (e.scrollTop < 50) {
        this.backgroundColor = '';
        this.iconColor = '#fff'
      }
      // #endif
		},
		methods: {
			getRecommendLength(e) {
				this.isNoCommodity = e == 0 ? true : false;
			},
			/**
			 * 支付宝充值结果查询
			 */
			alipayQueryPay() {
				uni.showLoading({
					title: '查询中...'
				});
				alipayQueryPayResult(this.orderId).then(res => {
					this.userDalance();
					return this.$util.Tips({
						title: '充值成功'
					});
					uni.hideLoading();
				}).catch(err => {
					uni.hideLoading();
					return this.$util.Tips({
						title: err
					});
				})
			},
			userDalance() {
				getMyAccountApi().then(res => {
					this.statistics = res.data;
				})
			},
			openSubscribe(page) {
				uni.navigateTo({
					url: page,
				});
			}
		}
	}
</script>

<style scoped lang="scss">
.my-account{
  @include shallow-gradient(theme);
}
	.my-account .wrapper .header {
		width: 100%;
		height: 362rpx;
    @include linear-gradient(theme);
		margin: 0 auto;
		box-sizing: border-box;
		color: rgba(255, 255, 255, 0.6);
		font-size: 24rpx;
    position: relative;
	}

	.my-account .wrapper .header .headerCon {
    padding-top: 36rpx;
    .pictrue {
      width: 290rpx;
      height: 362rpx;
      position: absolute;
      right: 0;
      bottom: 0;
      image{
        width: 100%;
        height: 100%;
      }
    }
	}

	.my-account .wrapper .header .headerCon .account {
    padding: 0 28rpx;
	}

	.my-account .wrapper .header .headerCon .account .assets .money {
    font-size: 64rpx;
    color: #fff;
    margin-top: 12rpx;
	}

	.my-account .wrapper .header .headerCon .account .recharge {
    font-size: 24rpx;
    width: 112rpx;
    height: 56rpx;
    border-radius: 50rpx;
    background-color: rgba(255,255,255,0.8);
    text-align: center;
    line-height: 56rpx;
    font-weight: 500;
	}

	.font_color {
		@include main_color(theme);
	}

	.icon_txt {
		font-size: 43rpx;
		@include main_color(theme);
	}

	.my-account .wrapper .header .headerCon .cumulative {
    width: 100%;
    height: 142rpx;
    background: rgba(255,255,255,0.1);
    margin-top: 62rpx;
    padding-left: 28rpx;
    position: absolute;
    left: 0;
    bottom: 0;
	}

	.my-account .wrapper .header .headerCon .cumulative .item {
    position: static;
    z-index: 9;
	}

	.my-account .wrapper .header .headerCon .cumulative .item .money {
    font-size: 40rpx;
    color: #fff;
    margin-top: 12rpx;
	}
	.my-account .wrapper .nav .item {
    font-size: 24rpx;
    color: #999;
    width: 342rpx;
    height: 152rpx;
    background: linear-gradient(180deg, #FFF7F0 0%, #FFFFFF 100%);
    border-radius: 24rpx;
    border: 4rpx solid #fff;
    padding: 0 31rpx;
    box-sizing: border-box;
    .name{
      font-size: 28rpx;
      color: #333;
      font-weight: 500;
      margin-bottom: 8rpx;
    }
	}

	.my-account .wrapper .nav .item .pictrue {
    width: 96rpx;
    height: 96rpx;
    image{
      width: 100%;
      height: 100%;
    }
	}
</style>
