<template>
	<view>
		<block v-for="(item, index) in orderInfoList" :key="index">
			<view class="goods-box flex">
				<view class="goods-img-box"><img :src="item.image" alt="" class="goods-img" /></view>
				<view class="goods-info-box ml-20 flex-col">
					<view class="goods-name">{{item.productName}}</view>
					<view class="sku-text fs-24">{{item.sku}}</view>
					<view class="verify-box flex-between-center">
						<view v-if="isPunchCard" class="verified-box fs-24 flex">
							<view>已核销：</view>
							<view :class="type == 'users' ? 'font_color' : 'verified-text'">
								{{item.verifyTotalTimes - item.verifyRemainingTimes}}
							</view>
							<view>/{{item.verifyTotalTimes}}</view>
						</view>
						<view v-else class="verified-box fs-24">
							<text>核销数：</text>
							<text>{{item.payNum}}</text>
						</view>
						<!-- 核销页面控制核销次数 -->
						<view v-if="isVer && item.verifyRemainingTimes" class='carnum acea-row row-center-wrapper'>
							<view class="reduce iconfont icon-ic_Reduce mr-8" :class="verifyCount == 1 ? 'on' : ''"
								@click='subCart()'></view>
							<view class='flex-center num rd-12rpx'>
								<view class="verify-number-box">
									<input class="verify-number-input" style="text-align: center;" type="number" v-model="verifyCount"
										@input="changeVerifyCount"></input>
								</view>
							</view>
							<view class="plus iconfont icon-ic_increase ml-8" :class="verifyCount == verifyRemainingTimes ? 'on' : ''"
								@click='addCart()'></view>
						</view>
					</view>
				</view>
			</view>
		</block>
	</view>
</template>

<script>
	export default {
		name: 'verGoodsInfo',
		props: {
			// 订单商品信息
			orderInfoList: {
				type: Array,
				default: () => {
					return []
				}
			},
			// 是否次卡商品
			isPunchCard: {
				type: Boolean,
				default: true
			},
			// 核销数
			totalNum: {
				type: Number,
				default: 0
			},
			// 页面类型：users--用户，admin--商户员工
			type: {
				type: String,
				default: ''
			},
			// 是否是核销页面
			isVer: {
				type: Boolean,
				default: false,
			}
		},
		data() {
			return {
				verifyCount: 1, // 核销次数
				verifyRemainingTimes: this.orderInfoList[0].verifyRemainingTimes, // 剩余核销次数
			}
		},
		methods: {
			// 核销次数减
			subCart() {
				this.verifyCount--;
				if (this.verifyCount <= 1) {
					this.verifyCount = 1
				}
				uni.$emit('getVerifyCount', this.verifyCount)
			},
			// 核销次数加
			addCart() {
				this.verifyCount++;
				if (this.verifyCount >= this.verifyRemainingTimes) {
					this.verifyCount = this.verifyRemainingTimes
				}
				uni.$emit('getVerifyCount', this.verifyCount)
			},
			// 更改核销次数
			changeVerifyCount() {
				if (this.verifyCount <= 0 && this.verifyCount != '') {
					this.$nextTick(() => {
						this.$set(this, 'verifyCount', 1);
						uni.$emit('getVerifyCount', this.verifyCount)
					});
				} else if (this.verifyCount >= this.verifyRemainingTimes) {
					this.$nextTick(() => {
						this.$set(this, 'verifyCount', this.verifyRemainingTimes);
						uni.$emit('getVerifyCount', this.verifyCount)
					});
				} else {
					uni.$emit('getVerifyCount', this.verifyCount)
				}
			}
		}
	}
</script>

<style lang="scss">
	.goods-box {
		& + .goods-box {
			margin-top: 26rpx;
		}
		// background: #FFFFFF;
		// border-radius: 24rpx 24rpx 24rpx 24rpx;
		// padding: 32rpx 24rpx;
		
		.goods-img-box {
			.goods-img {
				width: 136rpx;
				height: 136rpx;
				border-radius: 16rpx 16rpx 16rpx 16rpx;
			}
		}

		.goods-info-box {
			justify-content: space-around;
			overflow: hidden;
			width: 100%;
			.goods-name {
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
			}

			.sku-text {
				color: #999;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
			}

			.verify-box {
				width: 100%;

				.verified-box {
					color: #666;

					.verified-text {
						color: #2A7EFB;
					}
				}

				.verify-number-box {
					width: 72rpx;
					height: 36rpx;
					background: #F5F5F5;
					border-radius: 4rpx 4rpx 4rpx 4rpx;
					.verify-number-input {
						width: 72rpx;
						height: 36rpx;
						line-height: 36rpx;
						font-size: 24rpx;
						font-family: 'dinProRegular';
					}
				}

				.on {
					border-color: #e3e3e3;
					color: #dedede;
				}
			}
		}
	}

	.font_color {
		@include main_color(theme);
	}
</style>