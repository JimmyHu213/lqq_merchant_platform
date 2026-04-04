<template>
	<view class="">
		<view class="bg-card borderPad">
			<view class="nav-text-box flex-between-center">
				<view class="nav-text fs-26" v-for="(item, index) in navTextList" :key="index">
					{{item.text}}
				</view>
			</view>
			<view class="nav-line-box flex-between-center mt-18">
				<view class="iconfont icon-ic_jindu2 fs-26 font-color" :class="{'icon-ic_jindu1': status == 0}"></view>
				<block v-for="(item, index) in navTextList" :key="index">
					<block v-if="index != 0">
						<view class="line" :class="{'bg-color': status >= item.status}"></view>
						<view class="iconfont color-999 fs-26" :class="[iconStyle(item.status)]"></view>
					</block>
				</block>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		ProductTypeEnum,
		ProductMarketingTypeEnum
	} from "@/enums/productEnums";
	export default {
		name: 'progressBar',
		props: {
			// 订单数据
			orderInfo: {
				type: Object,
				default: () => {
					return {}
				}
			},
			// 订单状态
			status: {
				type: Number,
				default: 9
			},
			// 配送方式
			shippingType: {
				type: Number,
				default: 0
			},
			// 订单类型
			secondType: {
				type: Number,
				default: 0
			},
		},
		computed: {
			// 进度列表
			navTextList() {
				if (this.shippingType > 3) {
					return [
						{text: '待付款', status: 0},
						{text: '待使用', status: 3},
						{text: '已收货', status: 5},
						{text: '已完成', status: 6}
					]
				} else if (this.shippingType == 2) {
					return [
						{text: '待付款', status: 0},
						{text: '待核销', status: 3},
						{text: '已收货', status: 5},
						{text: '已完成', status: 6}
					]
				} else if (this.shippingType == 1) {
					return [
						{text: '待付款', status: 0},
						{text: '待发货', status: 1},
						{text: '待收货', status: 4},
						{text: '已收货', status: 5},
						{text: '已完成', status: 6}
					]
				}
			},
			// icon样式
			iconStyle() {
				return (itemStatus) => {
					let res = 'icon-ic_jindu2'
					if (itemStatus == 1 && this.status == 2) {
						res = 'icon-ic_jindu1 font-color'
					} else if (itemStatus == this.status) {
						res = 'icon-ic_jindu1 font-color'
					} else if (itemStatus < this.status) {
						res = 'icon-ic_jindu2 font-color'
					}
					return res
				}
			}
		},
		data() {
			return {
				ProductTypeEnum: ProductTypeEnum,
			}
		},
		methods: {
		}
	}
</script>

<style scoped lang="scss">
	.bg-card {
		background-color: #fff;
		border-radius: 24rpx 24rpx 24rpx 24rpx;
		padding: 32rpx 24rpx;
	}

	.nav-line-box {
		padding: 0 26rpx;

		.line {
			width: 100rpx;
			height: 2rpx;
			background-color: #999;
		}
	}

	.color-999 {
		color: #999;
	}

	.fs-24 {
		font-size: 24rpx;
	}
</style>