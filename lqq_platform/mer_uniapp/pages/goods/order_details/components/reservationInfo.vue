<template>
	<view :data-theme="theme">
		<!--到店-->
		<view v-if="orderInfo.shippingType === 4" class="store-info bg--w111-fff borRadius14">
			<view class="acea-row row-between row-middle">
				<view class="info-t">
					<view class="acea-row row-middle">
						<text class="iconfont icon-zhuanshudaogou rgba-bg-color font-color"></text>
						<view class="info"><text class="name line1">{{orderInfo.realName}}</text><text
								class="phone">{{orderInfo.userPhone}}</text></view>
					</view>
					<view class="acea-row row-middle" style="margin-top: 28rpx;">
						<text class="iconfont icon-ic_mall rgba-bg-color font-color"></text>
						<view class="info price-info-100">{{orderInfo.merName}}</view>
					</view>
				</view>
				<image class="picture" :src="orderInfo.coverImage"></image>
			</view>
			<view class="store-address" style="border-bottom: 1rpx solid #eee;">
				<view class="address acea-row row-between">
					<view class="info">地址：{{orderInfo.merAddressDetail}}</view>
					<!-- #ifndef H5 -->
					<text class='copy' @tap='copy(orderInfo.merAddressDetail)'>复制</text>
					<!-- #endif -->
					<!-- #ifdef H5 -->
					<text class='copy copy-data' :data-clipboard-text="orderInfo.merAddressDetail">复制</text>
					<!-- #endif -->
				</view>
			</view>
			<view class="store-count acea-row row-between">
				<view class="acea-row row-middle row-center" @click="serviceClick"><text
						class="iconfont icon-ic_phone"></text>联系商家</view>
				<view class="acea-row row-middle row-center" @tap="showMaoLocation"><text
						class="iconfont icon-ic_location"></text>导航至商家</view>
			</view>
		</view>
		<!--上门-->
		<view v-else class="store-info bg--w111-fff borRadius14">
			<view class="acea-row row-between">
				<view class="info-t">
					<view class="acea-row row-middle">
						<text class="iconfont icon-ic_location4"></text>
						<view class="info">{{orderInfo.realName}}<text class="phone">{{orderInfo.userPhone}}</text></view>
					</view>
				</view>
			</view>
			<view class="store-address">
				<view class="address">
					<view class="info info-100">地址：{{orderInfo.userAddress}}</view>
				</view>
			</view>
			<block v-if="orderInfo.orderInfoList && orderInfo.orderInfoList[0].staffList">
				<view class="service-box">
					<view 
						class="service acea-row row-between row-middle">
						<view>
							<view class="service-item">服务人员：{{orderInfo.orderInfoList[0].staffList[0].name}}</view>
							<view class="service-item">联系电话：{{orderInfo.orderInfoList[0].staffList[0].phone}}</view>
						</view>
						<view class="call acea-row row-center row-middle" @click="makePhone(orderInfo.orderInfoList[0].staffList[0].phone)">拨打电话</view>
					</view>
				</view>
			</block>
			<view class='line'>
				<image src='../../static/images/line.png'></image>
			</view>
		</view>
		<!--核销码,到店展示-->
		<view v-show="orderInfo.shippingType === 4" class="verificate bg--w111-fff borRadius14">
			<view class="title">核销码</view>
			<view class="pictrue">
				<image :src="codeUrl"></image>
			</view>
			<view class="acea-row row-middle row-center mt-20">
				<view class="code">{{orderInfo.verifyCode}}</view>
				<view class="conter">
					<!-- #ifndef H5 -->
					<text class='copyCode copy-data' @tap='copy(orderInfo.verifyCode)'>复制</text>
					<!-- #endif -->
					<!-- #ifdef H5 -->
					<text class='copyCode copy-data' :data-clipboard-text="orderInfo.verifyCode">复制</text>
					<!-- #endif -->
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
		chatConfig
	} from "@/utils/consumerType.js";
	import {
		merCustomerApi
	} from "@/api/merchant.js";
	import {
		ProductTypeEnum,
		ProductMarketingTypeEnum
	} from "@/enums/productEnums";
	let app = getApp();
	export default {
		name: 'reservationInfo',
		props: {
			// 二维码
			codeUrl: {
				type: String,
				default: ''
			},
			domain: {
				type: String,
				default: ""
			},
			orderInfo: {
				type: Object,
				default: () => {
					{}
				}
			}
		},
		data() {
			return {
				theme: app.globalData.theme,
				serviceConfig: {}, // 联系客服服务配置
				ProductTypeEnum: ProductTypeEnum,
			};
		},
		created() {
			// 获取客服信息
			this.getMerCustomer(this.orderInfo.merId)
		},
		methods: {
			copy(data) {
				this.$emit('copy', data);
			},
			makePhone(phone) {
				this.$emit('makePhone', phone);
			},
			showMaoLocation() {
				this.$emit('showMaoLocation', this.orderInfo);
			},
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
		}
	}
</script>

<style lang="scss">
	.verificate {
		text-align: center;
		padding: 48rpx 0;

		.pictrue {
			margin: 40rpx auto 0;
			width: 320rpx;
			height: 320rpx;

			image {
				width: 320rpx;
				height: 320rpx;
			}
		}

		.code {
			// color: var(--view-theme);
			font-weight: 500;
		}

		.copyCode {
			font-size: 24rpx;
			color: #666;
			margin-left: 20rpx;
		}
	}

	.store-info {
		padding: 24rpx 24rpx 0;
		margin: 20rpx 0;
		position: relative;

		.line {
			position: absolute;
			left: 2%;
			bottom: 0;
			width: 96%;
			height: 4rpx;

			image {
				width: 100%;
				height: 100%;
				display: block;
			}
		}

		.info-t {
			max-width: 500rpx;

			.iconfont {
				width: 48rpx;
				height: 48rpx;
				// background: var(--view-bgColor);
				border-radius: 100%;
				// color: var(--view-theme);
				text-align: center;
				line-height: 48rpx;
				margin-right: 16rpx;
				font-size: 28rpx;

				&.icon-ic_location4 {
					color: #000000;
					width: auto;
					height: auto;
					font-size: 30rpx;
					margin-right: 10rpx;
					background: transparent;
				}
			}

			.info {
				font-weight: 500;

				.name {
					max-width: 200rpx;
				}

				.phone {
					margin-left: 18rpx;
				}
			}
		}

		.picture {
			width: 128rpx;
			height: 128rpx;
			border-radius: 16rpx;
		}

		.store-address {
			padding: 20rpx 0 24rpx;

			.address {
				margin-bottom: 14rpx;
			}

			.info {
				width: 560rpx;
				color: #999;
				font-size: 24rpx;

				&.info-100 {
					width: 100%;
				}
			}

			.copy {
				display: flex;
				align-items: center;
				justify-content: center;
				width: 68rpx;
				height: 36rpx;
				background: #F5F5F5;
				border-radius: 26rpx;
				font-size: 10px;
			}
		}

		.store-count {
			margin-top: 24rpx;
			justify-content: center;
			padding-bottom: 24rpx;

			view {
				flex: 0 0 50%;
				text-align: center;
				font-size: 24rpx;

				.iconfont {
					margin-right: 6rpx;
					font-size: 32rpx;
				}
			}
		}
		.service-box {
			border-top: 2rpx solid #eee;
			margin-bottom: 20rpx;
		}
		.service {
			// margin-bottom: 20rpx;
			padding: 30rpx 0;
			// border-top: 2rpx solid #eee;

			.service-item {
				color: #666;
				font-size: 24rpx;
				margin-bottom: 14rpx;
			}

			.call {
				width: 144rpx;
				height: 56rpx;
				border-radius: 80rpx;
				border: 1rpx solid #CCCCCC;
				font-size: 24rpx;
			}
		}
	}

	.rgba-bg-color {
		@include main_rgba_color(theme);
	}
</style>