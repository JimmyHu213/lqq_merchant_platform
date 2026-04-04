<template>
	<view class="">
		<!-- 次卡商品核销次数弹窗 -->
		<uni-popup ref="punchCardPopup" type="bottom" borderRadius="20px 20px 0 0">
			<view class="punch-card-pop" v-if="punchCardOrderInfo">
				<view class="title">
					<view class="">立即核销</view>
          <CloseIcon @handle-close="punchCardPopupHandle(false)"></CloseIcon>
				</view>
				<view class="content-box">
					<text>订单号：{{verificationOrderData.orderNo}}</text>
					<view class="goods-box flex">
						<view class="goods-img-box"><img :src="punchCardOrderInfo.image" alt="" class="goods-img" /></view>
						<view class="goods-info-box ml-20 flex-col">
							<view class="">{{punchCardOrderInfo.productName}}</view>
							<view class="sku-text fs-24">{{punchCardOrderInfo.sku}}</view>
							<view class="verified-box fs-24">
								已核销：
								<text>{{punchCardOrderInfo.verifyTotalTimes-punchCardOrderInfo.verifyRemainingTimes}}</text>
								<text>/{{punchCardOrderInfo.verifyTotalTimes}}</text>
							</view>
						</view>
					</view>
					<view class="item acea-row row-between">
						<view class="">核销次数</view>
						<view class='carnum acea-row row-center-wrapper'>
							<view class="reduce iconfont icon-ic_Reduce mr-8" :class="verifyCount == 1 ? 'on' : ''"
								@click.stop='subCart()'></view>
							<view class='flex-center num rd-12rpx'>
								<view class="verify-number-box">
									<text class="verify-number">{{verifyCount}}</text>
								</view>
							</view>
							<view class="plus iconfont icon-ic_increase ml-8" :class="verifyCount == verifyRemainingTimes ? 'on' : ''"
								@click.stop='addCart()'></view>
						</view>
					</view>
				</view>
				<view class="verify-btn" @click="toCancel">
					确认核销
				</view>
			</view>
		</uni-popup>
		<!-- 普通商品核销确认弹窗 -->
		<uni-popup ref="normalPopup" background-color="#fff" borderRadius="10px">
			<view class="normal-pop">
				<view class="title">
					核销订单
				</view>
				<view class="text">
					你确定要核销此订单吗？
				</view>
				<view class="btn-box">
					<view class="no-btn btn" @click="normalPopupHandle(false)">取消</view>
					<view class="yes-btn btn" @click="toCancel">确认</view>
				</view>
			</view>
		</uni-popup>


	</view>
</template>

<script>
	import {
		ProductTypeEnum
	} from "@/enums/productEnums.js";
	import {
		Debounce
	} from '@/utils/validate.js'
	import {
		employeeOrderCancel
	} from '@/api/work.js'
	export default {
		name: 'verificationPopup',
		props: {
			// 核销订单数据
			verificationOrderData: {
				type: Object,
				default: () => {
					return {}
				}
			}
		},
		data() {
			return {
				popup_center: 'center',
				info: {},
				verifyCode: '',
				ProductTypeEnum,
				punchCardOrderInfo: {}, // 次卡订单数据
				verifyCount: 1, // 核销次数
				verifyRemainingTimes: 1, // 剩余核销次数
			}
		},
		computed: {

		},
		watch: {
			verificationOrderData(nVal) {
				this.punchCardOrderInfo = nVal.infoResponseList ? nVal.infoResponseList[0] : nVal.orderDetailList[0]
				this.verifyCount = 1
				this.verifyRemainingTimes = nVal.infoResponseList ? nVal.infoResponseList[0].verifyRemainingTimes :
					nVal.orderDetailList[0].verifyRemainingTimes
			}
		},
		mounted() {
			this.verifyCount = 1
		},
		methods: {
			// 次卡商品核销次数弹窗
			punchCardPopupHandle(type) {
				if (type) {
					this.$refs.punchCardPopup.open()
				} else {
					this.$refs.punchCardPopup.close()
					this.verifyCount = 1
				}
			},
			// 核销确认弹窗
			normalPopupHandle(type) {
				if (type) {
					this.$refs.normalPopup.open()
				} else {
					this.$refs.normalPopup.close()
					this.verifyCount = 1
				}
			},
			// 核销次数减
			subCart() {
				this.verifyCount--;
				if (this.verifyCount <= 1) {
					this.verifyCount = 1
				}
			},
			// 核销次数加
			addCart() {
				this.verifyCount++;
				if (this.verifyCount >= this.punchCardOrderInfo.verifyRemainingTimes) {
					this.verifyCount = this.punchCardOrderInfo.verifyRemainingTimes
				}
			},
			// 核销
			toCancel: Debounce(function() {
				{
					employeeOrderCancel({
						orderNo: this.verificationOrderData.orderNo,
						verifyCount: this.verifyCount
					}).then(res => {
						if (res.code == 200) {
							uni.navigateTo({
								url: `/pages/admin/cancel/result?type=1&&orderNo=${this.verificationOrderData.orderNo}`
							})
						} else {
							uni.navigateTo({
								url: '/pages/admin/cancel/result?type=0'
							})
						}
					}).catch(err => {
						return this.$util.Tips({
							title: err
						});
					})
				}
			}),
		}
	}
</script>

<style lang="scss" scoped>
	.punch-card-pop {
		width: 100%;
		background-color: #fff;
		border-top-left-radius: 40rpx;
		border-top-right-radius: 40rpx;
		padding-bottom: 22rpx;
		padding-top: 30rpx;
		padding-bottom: calc(22rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		padding-bottom: calc(22rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/

		.title {
			font-size: 32rpx;
			color: #282828;
			text-align: center;
			position: relative;
		}

		.content-box {
			margin: 66rpx 30rpx 30rpx;

			.goods-box {
				background: #FFFFFF;
				border-radius: 24rpx 24rpx 24rpx 24rpx;
				padding: 26rpx 0rpx;

				.goods-img-box {
					.goods-img {
						width: 136rpx;
						height: 136rpx;
						border-radius: 16rpx 16rpx 16rpx 16rpx;
					}
				}

				.goods-info-box {
					justify-content: space-around;

					.sku-text {
						color: #999;
					}

					.verified-box {
						color: #666;

						.verified-text {
							color: #2A7EFB;
						}
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
		}

		.verify-btn {
			margin: 0 30rpx;
			height: 86rpx;
			text-align: center;
			line-height: 86rpx;
			border-radius: 43rpx;
			font-size: 30rpx;
			color: #FFFFFF !important;
			background-color: #2A7EFB !important;
			border-color: #2A7EFB !important;
		}

		.btn {
			@include main_bg_color(theme);
		}
	}
	.normal-pop {
		width: 600rpx;
		height: 270rpx;
		// border-radius: 14rpx;
		// padding-bottom: 20rpx;
		.title {
			font-weight: 500;
			font-size: 32rpx;
			color: #333333;
			text-align: center;
			margin-top: 40rpx;
		}

		.text {
			font-weight: 400;
			font-size: 30rpx;
			color: #666666;
			text-align: center;
			margin-top: 30rpx;
		}

		.btn-box {
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

			.no-btn {
				border: 1px solid #2A7EFB;
				color: #2A7EFB;
			}

			.yes-btn {
				background: #2A7EFB;
				color: #FFFFFF;
			}
		}
	}
</style>