<template>
	<view class="">
		<!-- 自提商家信息卡片 -->
		<view v-if="type == 'pickUp'" class="mer-info-box card-bg borRadius14 flex-col">
			<view class="mer-title-box flex">
				<view class="iconfont icon-ic_truck fs-36 font-color"></view>
				<view class="title fs-28 fw-500 font-color">到店核销</view>
				<view class="text fs-24 color-999"> {{ merTitleText }}</view>
			</view>
			<view class="mer-text-info-box flex-between-center">
				<view class="left-info-box">
					<view class="mer-name fs-30 fw-500">
						{{orderInfo.merName}}
					</view>
					<view class="address fs-24 color-666 mt-12">
						{{orderInfo.merAddressDetail}}
					</view>
				</view>
				<view class="mer-logo-box">
					<img class="mer-logo" :src="orderInfo.coverImage" alt="" />
				</view>
			</view>
			<view class="mer-bottom-btn-box flex-around-center">
				<view class="iconfont icon-ic_phone fs-24" @tap="serviceClick">
					联系客服
				</view>
				<view class="iconfont icon-ic_location fs-24" @tap="showMaoLocation">
					{{addressText}}
				</view>
			</view>
		</view>
		<!-- 配送用户信息卡片 -->
		<view v-if="type == 'merDelivery'" class="user-info-box card-bg borRadius14">
			<view class="user-info fs-30 fw-500">
				<text>{{orderInfo.realName}}</text>
				<text class="ml-12">{{orderInfo.userPhone}}</text>
			</view>
			<view class="address fs-24 color-999 mt-12">
				地址：{{orderInfo.userAddress}}
			</view>
			<image src='../../static/images/line.png' class="line"></image>
		</view>
	</view>
</template>

<script>
	import {
		ProductTypeEnum,
		ProductMarketingTypeEnum
	} from "@/enums/productEnums";
	import {
		chatConfig
	} from "@/utils/consumerType.js";
	import {
		merCustomerApi
	} from "@/api/merchant.js";
	export default {
		name: 'topOrderInfo',
		props: {
			// 订单数据
			orderInfo: {
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
			// 类型
			type: {
				type: String,
				default: 'pickUp'
			}
		},
		computed: {
			addressText() {
				return this.secondType == ProductTypeEnum.Normal ? '导航至自提点' : '导航至商家'
			},
			merTitleText() {
				return this.secondType == ProductTypeEnum.PunchCard ? '请尽快前往店铺核销' : '请尽快前往自提点提货'
			}
			// phoneText() {
			// 	let text = '联系客服'
			// 	switch(this.secondType) {
			// 		case ProductTypeEnum.Normal:
			// 			text = '联系自提点'
			// 			break
			// 		case ProductTypeEnum.Reservation:
			// 			text = '联系商家'
			// 			break
			// 		case ProductTypeEnum.PunchCard:
			// 			text = '联系客服'
			// 			break
			// 	}
			// 	return text
			// },
		},
		watch: {
			'this.orderInfo': {
				handler(newVal) {
					if (newVal) {
						console.log(newVal, 'new')
					}
				},
				deep: true // 深度监听
			}
		},
		mounted() {
			// 获取客服信息
			this.getMerCustomer(this.orderInfo.merId)
		},
		data() {
			return {
				ProductTypeEnum: ProductTypeEnum,
				serviceConfig: {}, // 联系客服服务配置
			}
		},
		methods: {
			// 获取客服信息
			getMerCustomer(id) {
				merCustomerApi(id).then((res) => {
					this.serviceConfig = res.data;
				});
			},
			// 联系客服
			serviceClick() {
				if (this.productType === this.ProductTypeEnum.Integral) {
					chatConfig(this.$Cache.getItem("platChatConfig"));
				} else {
					chatConfig(this.serviceConfig);
				}
			},
			// 导航
			showMaoLocation() {
				this.$emit('showMaoLocation', this.orderInfo)
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

		.mer-title-box {
			align-items: center;
			padding: 0 32rpx 20rpx;
			border-bottom: 1rpx solid #eeeeee;

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
			padding: 20rpx 0;
			margin: 0 32rpx;
			border-bottom: 1rpx solid #eeeeee;

			.mer-logo-box {
				margin-left: 46rpx;

				.mer-logo {
					width: 128rpx;
					height: 128rpx;
					border-radius: 16rpx 16rpx 16rpx 16rpx;
				}
			}
		}

		.mer-bottom-btn-box {
			padding: 24rpx 32rpx 0;
		}
	}

	.user-info-box {
		position: relative;
		padding: 32rpx 0 !important;
		overflow: hidden;

		.user-info {
			padding: 0 32rpx;
		}

		.address {
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

	.color-999 {
		color: #999;
	}

	.color-666 {
		color: #666;
	}
</style>