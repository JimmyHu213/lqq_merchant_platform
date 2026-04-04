<template>
	<view class="">
		<view class="mer-info-box borRadius14 flex-col">
			<view v-if="isPunchCard" class="mer-title-box flex">
				<view class="iconfont icon-ic_truck fs-36 font-color"></view>
				<view class="title fs-28 fw-500 font-color">到店服务</view>
				<view class="text fs-24 color-999">线上下单，到店核销</view>
			</view>
			<view class="mer-text-info-box flex-between-center" :class="{'mt-24': isPunchCard}">
				<view class="left-info-box">
					<view v-if="isPunchCard" class="mer-name fs-30 fw-500">
						{{merAddressInfo.merName}}
					</view>
					<view v-else class="mer-name fs-30 fw-500">
						{{merAddressInfo.phone}}
					</view>
					<view class="mer-address color-666 mt-12" :class="isPunchCard ? 'fs-22' : 'fs-24'">
						{{merAddressInfo.addressDetail}}
					</view>
				</view>
				<view v-if="isPunchCard" class="mer-img-box flex flex-column"
					:style="{ backgroundImage: `url(${urlDomain}crmebimage/presets/type_loaction.png)`}" @click="showMaoLocation">
					<view class="distance flex flex-column fs-20">
						距您{{distance}}km
					</view>
					<view class="mer-logo-box flex flex-column">
						<img class="logo" :src="merAddressInfo.coverImage" alt="" />
					</view>
					<view class="dot"></view>
				</view>
				<!-- 自提点地址 -->
				<view v-else class="mer-img-box ml-24 pl-24 img-left-border flex flex-column" @click="showMaoLocation">
					<view class="distance flex flex-column fs-20">
						距您{{distance}}km
					</view>
					<view class="mer-logo-box flex flex-column">
						<img class="logo" :src="merAddressInfo.coverImage" alt="" />
					</view>
					<view class="dot"></view>
				</view>
			</view>
			<image v-if="isPunchCard" src='../../static/images/line.png' class="line"></image>
		</view>
	</view>
</template>

<script>
	import {
		ProductTypeEnum,
		ProductMarketingTypeEnum
	} from "@/enums/productEnums";
	import {
		getDistanceFromLatLonInMeter
	} from "@/libs/order";
	import {
		chatConfig
	} from "@/utils/consumerType.js";
	import {
		merCustomerApi
	} from "@/api/merchant.js";
	export default {
		name: 'pickUpInfo',
		props: {
			// 商户地址信息
			merAddressInfo: {
				type: Object,
				default: () => {
					return {}
				}
			},
			// 订单类型
			secondType: {
				type: Number,
				default: 0
			},
			// 是否次卡
			isPunchCard: {
				type: Boolean,
				default: false
			}
		},
		computed: {},
		watch: {},
		mounted() {
			if (this.merAddressInfo && this.merAddressInfo.latitude && this.merAddressInfo.longitude) {
				this.distance = getDistanceFromLatLonInMeter(Number(this.merAddressInfo.latitude),
					Number(this.merAddressInfo.longitude))
			}
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				ProductTypeEnum: ProductTypeEnum,
				serviceConfig: {}, // 联系客服服务配置
				distance: 0, //距离
			}
		},
		methods: {
			// 导航
			showMaoLocation() {
				let that = this;
				//#ifdef H5
				if (that.$wechat.isWeixin() === true) {
					that.$wechat.seeLocation({
						latitude: parseFloat(this.merAddressInfo.latitude),
						longitude: parseFloat(this.merAddressInfo.longitude),
						name: this.merAddressInfo.merName,
						address: this.merAddressInfo.addressDetail ? this.merAddressInfo.addressDetail : '',
					}).then(res => {
						console.log('success');
					})
				} else {
					//#endif
					uni.openLocation({
						latitude: parseFloat(this.merAddressInfo.latitude),
						longitude: parseFloat(this.merAddressInfo.longitude),
						scale: 8,
						name: this.merAddressInfo.merName,
						address: this.merAddressInfo.addressDetail ? this.merAddressInfo.addressDetail : '',
						success: function(res) {
							// that.go_map = true
						},
					});
					// #ifdef H5
				}
				//#endif
			},
			// 复制内容
			handleCopy(verifyCode) {
				this.$emit('handleCopy', verifyCode)
			}
		}
	}
</script>

<style scoped lang="scss">
	.mer-info-box {
		padding: 24rpx 0 !important;
		position: relative;

		.mer-title-box {
			align-items: center;
			padding: 0 24rpx 20rpx;
			border-bottom: 2rpx solid #EEEEEE;

			.iconfont {
				height: 36rpx;
				line-height: 36rpx;
			}

			.title {
				height: 36rpx;
				line-height: 36rpx;
				margin: 0 12rpx 0 12rpx;
			}
		}

		.mer-text-info-box {
			padding: 0 24rpx;
			// border-bottom: 2rpx solid #EEEEEE;

			.mer-img-box {
				flex-shrink: 0;
				width: 188rpx;
				height: 104rpx;
				margin-left: 48rpx;
				background-size: 100% 100%;
				align-items: center;

				.distance {
					position: relative;
					align-items: center;
					background: #FFFFFF;
					box-shadow: 0rpx 0rpx 16rpx 0rpx rgba(0, 0, 0, 0.0784);
					padding: 4rpx 16rpx;

					&::after {
						content: "";
						display: block;
						width: 0;
						height: 0;
						border-top: 4rpx solid #fff;
						border-left: 9rpx solid transparent;
						border-right: 9rpx solid transparent;
						position: absolute;
						bottom: -4rpx;
					}
				}

				.mer-logo-box {
					position: relative;
					align-items: center;
					padding: 6rpx;
					margin-top: 4rpx;
					width: 52rpx;
					height: 52rpx;
					background: #FFFFFF;
					box-shadow: 0rpx 0rpx 12rpx 0rpx rgba(0, 0, 0, 0.0784);
					border-radius: 8rpx;

					&::after {
						content: "";
						display: block;
						width: 0;
						height: 0;
						border-top: 4rpx solid #fff;
						border-left: 9rpx solid transparent;
						border-right: 9rpx solid transparent;
						position: absolute;
						bottom: -4rpx;
					}

					.logo {
						width: 40rpx;
						height: 40rpx;
						border-radius: 8rpx;
					}
				}

				.dot {
					margin-top: 4rpx;
					width: 6rpx;
					height: 6rpx;
					// background: #E93323;
					@include main_bg_color(theme);
					border-radius: 50%;
				}
			}
			.img-left-border {
				border-left: 1rpx solid #ddd; 
			}
		}

		.line {
			position: absolute;
			bottom: 0;
			width: 100%;
			height: 4rpx;
		}
	}

	.user-info-box {
		position: relative;
		padding: 32rpx 0 !important;
		overflow: hidden;

		.user-info {
			padding: 0 32rpx;
		}

		.mer-address {
			padding: 0 32rpx;
		}

		.line {
			position: absolute;
			bottom: 0;
			width: 100%;
			height: 4rpx;
		}
	}

	.card-bg {
		background-color: #fff;
		margin-top: 20rpx;
		padding: 24rpx;
	}

	.rgba-bg-color {
		@include main_rgba_color(theme);
	}
	
	.ml-24 {
		margin-left: 24rpx !important;
	}

	.color-999 {
		color: #999;
	}

	.color-666 {
		color: #666;
	}
</style>