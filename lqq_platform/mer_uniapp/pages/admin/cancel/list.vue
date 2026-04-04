<template>
	<view class="pagebox">
		<view class="after_sale" v-if="info.userRefundSign" @click="skipList(info.orderNo)">
			该订单存在处理中售后，请点击查看
			<text class="iconfont icon-ic_rightarrow"></text>
		</view>
		<view class="order-info mb-20">
			<view class="order-info-number">
				订单号：{{info.orderNo}}
			</view>
			<!-- 次卡商品 -->
			<template v-if="info.infoResponseList && info.infoResponseList[0].productType == ProductTypeEnum.PunchCard">
				<view class="order-info-box ">
					<ver-goods-info v-if="info.infoResponseList[0]" :orderInfoList="info.infoResponseList" :type="'admin'"
						:isVer="true" @getVerifyCount="getVerifyCount">
					</ver-goods-info>
				</view>
			</template>
			<!-- 非次卡商品 -->
			<template v-else>
				<view class="order-info-box mt-26">
					<view class="order-info-item" v-for="(item,index) in info.infoResponseList" :key="index">
						<image class="product-img" :src="item.image" mode="scaleToFill"></image>
						<view class="info-text">
							<view class="info-text-title">
								{{item.productName}}
							</view>
							<view class="info-text-sku">
								{{item.sku}}
							</view>
							<view class="operate">
								<text class="infor-num">核销数：{{item.payNum}} <text class="infor-text">
										{{item.applyRefundNum||item.refundNum?`(${item.applyRefundNum}件售后中 ${item.refundNum}件已退款）`:''}}</text></text>
								<!-- <text>核销：<text class="cancel-num">{{item.payNum-item.refundNum}}</text></text> -->
							</view>
						</view>
					</view>
				</view>
			</template>
		</view>
		<!-- 次卡商品核销记录 -->
		<template v-if="info.infoResponseList && info.infoResponseList[0].productType == ProductTypeEnum.PunchCard">
			<view class="record-box" v-if="info.verificationRecordList.length">
				<ver-record :verificationRecordList="info.verificationRecordList">
				</ver-record>
			</view>
		</template>
		<template v-else>
			<view class="record-box" v-if="info.verificationRecordList && info.verificationRecordList.length">
				<ver-record :verificationRecordList="info.verificationRecordList" :isPunchCard="false">
				</ver-record>
			</view>
		</template>
		<view class="page-bottom flex-between-center">
			<!-- <view class="page_bottom_num">
				共{{info.totalNum}}件
			</view> -->
			<view class="page-bottom-btn fs-26 fw-500" :class="info.verificationComplete ? 'primary-button' : ''"
				@click="toBackPage">
				{{backPageText}}
			</view>
			<view v-if="!info.verificationComplete" class="page-bottom-btn primary-button fs-26 fw-500" @click="toCancel">
				确认核销
			</view>
		</view>
		<ver-confirm-popup ref="verConfirmPopup" :verifyCode="verifyCode" :backPage="backPage"></ver-confirm-popup>
	</view>
</template>

<script>
	import {
		employeeOrderCancel,
		orderGetVerfication
	} from '@/api/work.js'
	import {
		Debounce
	} from '@/utils/validate.js'
	import {
		ProductTypeEnum
	} from "@/enums/productEnums.js";
	import verGoodsInfo from '@/subPackage/pages/verGoodsInfo/index.vue'
	import verRecord from '@/subPackage/pages/verRecord/index.vue'
	import verConfirmPopup from '../components/verConfirmPopup/index.vue'
	export default {
		components: {
			verGoodsInfo,
			verRecord,
			verConfirmPopup
		},
		data() {
			return {
				popup_center: 'center',
				info: {},
				verifyCode: '', // 核销码
				backPage: '', // 返回页面类型
				ProductTypeEnum,
				verifyCount: 1, // 核销次数
				verifyRemainingTimes: 1, // 剩余核销次数
				verificationOrderData: {}, // 核销订单数据
			}
		},
		computed: {
			backPageText() {
				let text = '返回'
				switch (this.backPage) {
					case 'work':
						text = '返回工作台'
						break
					case 'list':
						text = '返回订单列表'
						break
					case 'detail':
						text = '返回订单详情'
						break
				}
				return text
			}
		},
		onLoad(options) {
			this.verifyCode = options.verifyCode
			this.backPage = options.backPage
			// 获取订单数据
			this.getVerOrderData(options.verifyCode)
			// this.info = JSON.parse(options.info)
			// 监听事件
			uni.$on('getVerifyCount', this.getVerifyCount);
		},
		onUnload() {
			// 销毁监听事件
			uni.$off('getVerifyCount', this.getVerifyCount);
		},
		methods: {
			// 获取核销订单数据
			getVerOrderData(verifyCode) {
				orderGetVerfication({
					verifyCode: verifyCode
				}).then(res => {
					this.info = res.data
					this.verifyRemainingTimes = this.info.infoResponseList[0].verifyRemainingTimes
				}).catch(res => {
					return this.$util.Tips({
						title: err
					});
				})
			},
			skipList(orderNo) {
				uni.navigateTo({
					url: `/pages/admin/skipRefund/index?orderNo=${orderNo}`
				})
			},
			//立即开通
			toCancel: Debounce(function(e) {
				if (this.verifyCount == '') {
					return this.$util.Tips({
						title: '请输入核销次数!'
					});
				}
				employeeOrderCancel({
					verifyCode: this.verifyCode,
					verifyCount: this.verifyCount
				}).then(res => {
					if (this.info.infoResponseList[0].productType == this.ProductTypeEnum.PunchCard &&
						this.verifyCount != this.info.infoResponseList[0].verifyRemainingTimes) {
						this.$refs.verConfirmPopup.popupHandle(true)
					} else {
						uni.redirectTo({
							url: `/pages/admin/cancel/result?type=1&&orderNo=${this.info.orderNo}`
						})
					}
					// if (res.code == 200) {
					// 	uni.navigateTo({
					// 		url: `/pages/admin/cancel/result?type=1&&orderNo=${this.info.orderNo}`
					// 	})
					// } else {
					// 	uni.navigateTo({
					// 		url: '/pages/admin/cancel/result?type=0'
					// 	})
					// }
				}).catch(err => {
					return this.$util.Tips({
						title: err
					});
				})
			}),
			// 获取核销次数
			getVerifyCount(verifyCount) {
				this.verifyCount = verifyCount
			},
			// 返回页面
			toBackPage() {
				const backNumber = this.backPage == 'work' ? 2 : 1
				uni.navigateBack({
					delta: backNumber
				});
			}
		}
	}
</script>

<style lang="scss" scoped>
	.pagebox {
		padding: 20rpx 24rpx;
		padding-bottom: 150rpx;

		.after_sale {
			font-size: 26rpx;
			background-color: #E5EFFE;
			height: 90rpx;
			line-height: 90rpx;
			padding-left: 20rpx;
			border-radius: 10rpx;
			color: #2A7EFB;
			position: relative;
			margin-bottom: 20rpx;

			.icon-ic_rightarrow {
				position: absolute;
				right: 10rpx;
			}
		}

		.order-info {
			background-color: #fff;
			border-radius: 24rpx;
			padding: 32rpx 24rpx;

			.order-info-number {
				// border-bottom: 2rpx solid #F5F5F5;
			}

			.order-info-box {
				margin-top: 26rpx;
			}

			.info-text {
				display: flex;
				flex-direction: column;
				justify-content: space-around;
				margin-left: 20rpx;
			}

			.order-info-item {
				display: flex;

				&+.order-info-item {
					padding-top: 26rpx;
				}

				.product-img {
					flex-shrink: 0;
					width: 136rpx;
					height: 136rpx;
					border-radius: 16rpx;
				}

				.info-text-title {
					width: 480rpx;
					overflow: hidden;
					text-overflow: ellipsis;
					white-space: nowrap;
				}

				.info-text-sku {
					color: #999;
					font-size: 24rpx;
					// margin-top: 8rpx;
				}

				.operate {
					display: flex;
					justify-content: space-between;
					font-size: 24rpx;

					.cancel-num {
						background-color: #F5F5F5;
						padding: 10rpx 20rpx;
					}
				}
			}
		}

		.record-box {
			background: #FFFFFF;
			border-radius: 24rpx 24rpx 24rpx 24rpx;
			padding: 32rpx 24rpx;
		}

		.page-bottom {
			position: fixed;
			bottom: 0;
			left: 0rpx;
			width: 100%;
			height: 112rpx;
			background-color: #fff;
			padding: 0 24rpx;

			.page-bottom-btn {
				width: 100%;
				height: 72rpx;
				border: 2rpx solid #2A7EFB;
				color: #2A7EFB;
				border-radius: 40rpx;
				line-height: 72rpx;
				text-align: center;

				&+.page-bottom-btn {
					margin-left: 16rpx;
				}
			}

			.primary-button {
				background: #2A7EFB;
				color: #FFFFFF;
			}
		}
	}

	.infor-text {
		color: #2A7EFB;
	}

	// /deep/ .uni-popup__wrapper {
	// 	width: 600rpx;
	// 	height: 310rpx;
	// 	background: #FFFFFF;
	// 	border-radius: 32rpx;
	// }

	.sh_popup-content {
		.sh_popup_title {
			font-weight: 500;
			font-size: 32rpx;
			color: #333333;
			text-align: center;
			margin-top: 40rpx;
		}

		.sh_popup_text {
			font-weight: 400;
			font-size: 30rpx;
			color: #666666;
			text-align: center;
			margin-top: 30rpx;
		}

		.sh_popup_btn {
			display: flex;
			justify-content: space-between;
			margin-top: 40rpx;
			padding: 0 47rpx;

			.btn {
				width: 244rpx;
				height: 72rpx;
				border-radius: 50rpx;
				text-align: center;
				line-height: 72rpx;
			}

			.no_btn {
				border: 1px solid #2A7EFB;
				color: #2A7EFB;
			}

			.yes_btn {
				background: #2A7EFB;
				color: #FFFFFF;
			}
		}
	}

	.verify-number-box {
		padding: 0 30rpx;
		background: #F5F5F5;
		border-radius: 4rpx 4rpx 4rpx 4rpx;
	}

	.on {
		border-color: #e3e3e3;
		color: #dedede;
	}

	.ver-pop {
		width: 100%;
		background-color: #fff;
		border-top-left-radius: 40rpx;
		border-top-right-radius: 40rpx;
		padding-bottom: 22rpx;
		padding-top: 38rpx;
		padding-bottom: calc(22rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		padding-bottom: calc(22rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/

		.title {
			font-size: 32rpx;
			color: #282828;
			text-align: center;
			margin: 0 0 36rpx 0;
			position: relative;
		}

		.content-box {
			margin: 0 30rpx 30rpx;

			.on {
				border-color: #e3e3e3;
				color: #dedede;
			}
		}

		.btn {
			margin: 0 30rpx;
			height: 86rpx;
			text-align: center;
			line-height: 86rpx;
			border-radius: 43rpx;
			font-size: 30rpx;
			color: #fff;
		}

		.btn {
			@include main_bg_color(theme);
		}
	}

	.num {
		color: #282828;
		background: #F5F5F5;
		width: 72rpx;
	}

	.rd-12rpx {
		border-radius: 6px;
	}

	.infor-num {
		width: 336rpx;
	}
</style>