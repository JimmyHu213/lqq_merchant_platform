<template>
	<view>
		<TopHeaderfixed></TopHeaderfixed>
		<view class="borderPad relative">
			<view v-if="info.status" class="header borderPad">
				<view v-if="info.refundStatus == 3" class="state">已退款</view>
				<!-- <!-- 次卡商品部分核销 -->
				<view v-else-if="info.status == 3 && punchCardOrderInfo.productType == ProductTypeEnum.PunchCard && 
					punchCardOrderInfo.verifyRemainingTimes!=punchCardOrderInfo.verifyTotalTimes" class="state">
					部分使用
				</view>
				<view v-else class="state">{{ info.status | orderStatusFilter }}</view>
				<view v-if="[1,2].includes(info.status)&&info.refundStatus != 3" class="data">用户已下单，请及时发货哦～</view>
				<view v-if="info.refundStatus != 3&&info.status == 4" class="data">货物正在运输中，等待用户确认收货！</view>
				<view v-if="info.refundStatus != 3&&info.status == 3" class="data">用户已下单，您可以联系用户使用！</view>
				<view v-if="info.refundStatus != 3&&info.status == 5" class="data">用户已经确认收货啦，您可以联系用户索要好评呦！</view>
				<view v-if="info.refundStatus != 3&&info.status == 6" class="data">订单已经完成，您可以申请货款提现啦～</view>
				<view v-if="info.refundStatus == 3" class="data">此订单退款已原路返回！</view>
			</view>
			<view v-if="info.serviceStatus" class="header">
				<view class="state">
					{{ info.serviceType | serviceTypeFilter }} · {{ info.serviceStatus | serviceStatusFilter }}
					<text v-show="info.refundStatus > 0" class="ml-4"> - 已退款</text>
				</view>
				<view class="data">
					预约时间：{{ info.reservationDate }}
					<span class="ml20">{{ info.reservationTimeSlot }}</span>
				</view>
			</view>
			<view class="remarks acea-row row-middle" @click="$emit('changeRemark')">
				<text class="iconfont icon-ic_notes"></text>
				<view class="mark-box ml-8 w-600 color-ccc" :class="remark ? 'color-333' : 'color-ccc'" style="text-align: left;">
					{{remark ? remark : `${title}未备注，点击添加备注信息`}}
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import TopHeaderfixed from "../../../../components/TopHeaderfixed";
	import {
		ProductTypeEnum,
		ProductMarketingTypeEnum
	} from "@/enums/productEnums";
	export default {
		name: "orderHeader",
		props: {
			// 备注
			remark: {
				type: String,
				default: ''
			},
			//订单对象
			info: {
				type: Object,
				default: function() {
					return {};
				}
			},
			title: {
				type: String,
				default: '订单'
			}
		},
		components: {
			TopHeaderfixed
		},
		data() {
			return {
				getHeight: this.$util.getWXStatusHeight(),
				change: false,
				status: '',
				ProductTypeEnum,
			}
		},
		computed: {
			// 次卡商品信息
			punchCardOrderInfo() {
				if (this.info.orderDetailList)
				return this.info.orderDetailList[0]
			}
		},
		methods: {}
	}
</script>

<style lang="scss" scoped>
	.header {
		padding: 40rpx 0 30rpx 12rpx;

		.state {
			font-weight: 500;
			font-size: 32rpx;
			line-height: 50rpx;
			color: #FFFFFF;
		}

		.data {
			margin-top: 8rpx;
			font-size: 28rpx;
			line-height: 36rpx;
			color: #FFFFFF;
		}
	}

	.remarks {
		width: 100%;
		background: #FFFFFF;
		display: flex;
		padding: 32rpx 0 32rpx 32rpx;
		border-radius: 24rpx;
		background: #FFFFFF;

		.iconfont {
			font-size: 32rpx;
			color: #000000;
		}

		input {
			flex: 1;
			height: 100rpx;
			padding-left: 20rpx;
			font-size: 28rpx;
		}

		input::placeholder {
			color: #CCCCCC;
		}
	}
	.color-ccc {
		color: #ccc;
	}
	.color-333 {
		color: #333;
	}
</style>