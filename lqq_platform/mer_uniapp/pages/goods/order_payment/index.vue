<template>
	<view :data-theme="theme">
		<view class='wrapper'>
			<view class='item borRadius14'>
				<view class="title text-center">
					<baseMoney :money="payPrice" symbolSize="48" integerSize="64" decimalSize="48" incolor="333333" weight>
					</baseMoney>
				</view>
				<view class='list'>
					<block v-for="(item,index) in cartArr" :key='index'>
						<view class='payItem acea-row row-middle' :class='active==index ?"on":""'
							@tap='payItem(index,item)'>
							<view class='name acea-row row-center-wrapper f-s-32'>
								<view class='iconfont animated'
									:class='(item.icon) + " " + (animated==true&&active==index ?"bounceIn":"")'>
								</view>
								<view class="">
									<view class="">
										{{item.name}}
									</view>
									<view v-if="item.value === 'yue'" class="fs-22">
										<text class="color-999">可用余额：</text>
										<text class="color-FE960F">¥{{item.userBalance}}</text>
									</view>
									<view v-if="item.value === 'shoppingCredits'" class="fs-22">
										<text class="color-999">可用购物金：</text>
										<text class="color-FE960F">¥{{item.shoppingCreditsBalance}}</text>
									</view>
								</view>
							</view>
							<view class="acea-row row-middle">
								<!-- <view class='tip'>
									{{item.title}}
									<block v-if="item.value === 'yue'">
										{{item.userBalance}}
									</block>
								</view> -->
								<view class="radio">
									<block v-if="active==index">
										<view class="iconfont icon-a-ic_CompleteSelect font-color"></view>
									</block>
									<block v-else>
										<view class="iconfont icon-ic_unselect text--w111-ccc"></view>
									</block>
								</view>
							</view>
						</view>
					</block>
				</view>
			</view>
		</view>
		<view v-if="isShow" class="titleNo">暂无支付方式！</view>
		<view class="btn-box">
			<button class='Bnt bg-color' @tap='getOrderPay' :disabled="isBuy">立即支付</button>
		</view>
		<view class="alipaysubmit" v-html="formContent"></view>
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
		svipOrderCreateApi
	} from '@/api/activity.js';
	import {
		Debounce
	} from '@/utils/validate.js'
	import {
		mapGetters
	} from "vuex";
	import store from '@/store'
	import orderPay from "@/mixins/OrderPay.js";
  import {shoppingCreditsPayApi} from "../../member/memberApi";
	let app = getApp();
	export default {
		mixins: [orderPay],
		data() {
			return {
				active: null, //支付方式切换
				theme: app.globalData.theme,
				//支付方式
				cartArr: [],
				payPrice: '',
				orderNo: '',
				animated: false,
				payType: '', //支付方式
				payChannel: '',
				formContent: '',
				isShow: false,
				userBalance: '', //余额
				shoppingCreditsBalance: '', //购物金余额
				fromType: '', //页面来源，svip支付，购买商品
				isBuy: false, //是否可以点击购买
        merId: 0
			}
		},
		computed: {
			...mapGetters(['productType', 'systemPlatform'])
		},
		onLoad(options) {
			this.payPrice = options.payPrice;
			this.orderNo = options.orderNo;
			this.packageId = options.packageId || 0
			this.cardId = options.cardId || 0
			this.fromType = options.fromType || ''
      this.merId = options.merId || 0
		},
		mounted() {
			this.payConfig();
		},
		methods: {
			// 支付配置
			payConfig() {
				uni.hideLoading();
				// 支付方式
				store.dispatch('getPayConfig', {orderNo: this.orderNo}).then((res) => {
          const payConfig = [...res.payConfig]
         // if(this.fromType) payConfig[2].payStatus === 0 // 会员充值不可用余额充值
          const filteredList = payConfig.filter(item => item.value !== 'yue');
          this.cartArr = this.fromType ? filteredList: payConfig
					this.userBalance = res.userBalance;
					if (this.cartArr.length) {
						this.active = 0;
						this.payType = this.cartArr[0].value;
						this.isShow = false;
					} else {
						this.isShow = true;
						return this.$util.Tips({
							title: '暂无支付方式！'
						})
					}
				});
			},
			payItem: Debounce(function(e, item) {
				let that = this;
				if (item.userBalance) that.userBalance = item.userBalance
				let active = e;
				that.active = active;
				that.animated = true;
				that.payType = that.cartArr[active].value;
				setTimeout(function() {
					that.car();
				}, 500);
			}),
			car: function() {
				let that = this;
				that.animated = false;
			},
			//选择支付方式的判断，传参
			getPayCheck() {
				if (!this.payType) return this.$util.Tips({
					title: '请选择支付方式'
				});
				if (this.payType === 'yue') {
					this.payChannel = 'yue'
				} else if (this.payType === 'shoppingCredits') {
					this.payChannel = 'shoppingCredits'
				} else if (this.payType == 'alipay') {
					// #ifdef H5
					this.payChannel = 'alipay';
					// #endif
					// #ifdef APP-PLUS
					this.payChannel = 'alipayApp';
					// #endif
				} else {
					// #ifdef H5
					this.payChannel = this.$wechat.isWeixin() ? 'public' : 'h5';
					// #endif
					// #ifdef APP-PLUS
					this.payChannel = this.systemPlatform === 'ios' ? 'wechatIos' : 'wechatAndroid';
					// #endif
					// #ifdef MP
					this.payChannel = "mini";
					if (this.productType == 'video') {
						this.payChannel = "video";
					} else {
						this.payChannel = "mini";
					}
					// #endif
				}
			},
			//立即支付
			getOrderPay: Debounce(function() {
				this.getPayCheck();
				if (Number(this.payPrice) > Number(this.userBalance) && this.payType === 'yue') return this.$util.Tips({
					title: '余额的金额不够，请切换支付方式'
				});
				uni.showLoading({
					title: '加载中...'
				});
				this.isBuy = true;
				if (this.fromType === 'svip') {
          this.svipOrderCreate() //商城vip
				}else if (this.fromType === 'shoppingCredits') {
          this.shoppingCreditCreate() //商户会员充值
        }
        else {
					// 订单支付
					this.changeOrderPay(this.orderNo, this.payChannel, this.payType, this.productType, this.fromType, this
						.payPrice)
				}
			}),
      // 商城vip付款
      svipOrderCreate(){
        svipOrderCreateApi({
          cardId: this.cardId,
          payChannel: this.payChannel,
          payType: this.payType,
        }).then(res => {
          this.handleOrderPay(res, res.data.orderNo, this.productType, this.fromType, this
              .payType, this.payPrice)
        }).catch(err => {
          this.fetchCatch(err)
        });
      },
      // 商户会员购买
      async shoppingCreditCreate(){
        try {
          let res = await shoppingCreditsPayApi({
            "packageId": this.packageId,
            "payChannel": this.payChannel,
            "payType": this.payType
          })
          await this.handleOrderPay(res, res.data.orderNo, this.productType, this.fromType, this
              .payType, this.payPrice, this.merId)
        }catch (err) {
          this.fetchCatch(err)
        }
      },
      fetchCatch(err){
        uni.hideLoading();
        this.isBuy = false;
        return this.$util.Tips({
          title: err
        });
      }
		},
	}
</script>

<style lang="scss" scoped>
	.titleNo {
		width: 100%;
		font-size: 28rpx;
		text-align: center;
	}

	.btn-box {
		padding: 0 30rpx;
		position: fixed;
		bottom: 43rpx;
	}

	.Bnt {
		font-size: 26rpx;
		font-weight: bold;
		color: #fff;
		width: 690rpx;
		height: 72rpx;
		border-radius: 200rpx;
		text-align: center;
		line-height: 72rpx;
	}

	.wrapper {

		padding: 30rpx 24rpx;

		.list {
			margin-top: 92rpx;
		}

		.item {
			padding: 76rpx 24rpx 56rpx;
			font-size: 30rpx;
			color: #333333;
			background-color: #fff;
		}

		.payItem {
			justify-content: space-between;
			width: 100%;
			box-sizing: border-box;
			font-size: 32pxrpx;
			color: #333333;
			& + .payItem {
				margin-top: 68rpx
			}

			.on {
				// border-color: #fc5445;
				@include coupons_border_color(theme);
				color: $theme-color;
			}

			.name {
				.iconfont {
					width: 52rpx;
					height: 52rpx;
					// border-radius: 50%;
					border-radius: 12rpx;
					text-align: center;
					line-height: 52rpx;
					background-color: #fe960f;
					color: #fff;
					font-size: 32rpx;
					margin-right: 24rpx;
				}
			}

			.iconfont.icon-a-ic_wechatpay {
				background-color: #41b035;
			}

			.iconfont.icon-a-ic_alipay {
				background-color: #00AAEA;
			}
			
			.iconfont.icon-ic_gouwujin {
				background-color: #FF4619;
			}

			.tip {
				text-align: center;
				font-size: 26rpx;
				color: #aaa;
				margin-right: 20rpx;
			}

			.radio {
				.iconfont {
					font-size: 40rpx;
				}
			}
		}
	}
	.color-999 {
		color: #999;
	}
	.color-FE960F {
		color: #FE960F;
	}
</style>