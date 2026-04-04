<template>
	<view>
		<view v-if="verificationRecordList.length" class='record-list'>
			<view class="color-333 fs-28 fw-500">核销记录</view>
			<view class="list-box">
				<view class='record-item acea-row row-between-wrapper line' v-for="(item,index) in verificationRecordList"
					:key="index">
					<view class="fs-28 color-333">{{item.verifyTime}}</view>
					<view class='record-number '>
						<text v-if="isPunchCard" class="fs-28 color-666">核销{{item.verifyTimes}}次</text>
						<text v-else class="fs-28 color-666">{{item.verifyName}}</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: 'verRecord',
		props: {
			// 核销记录列表
			verificationRecordList: {
				type: Array,
				default: () => {
					return []
				}
			},
			// 是否次卡商品
			isPunchCard: {
				type: Boolean,
				default: true
			}
		},
		data() {
			return {}
		},
		methods: {}
	}
</script>

<style lang="scss">
	.record-list {
		margin-bottom: -30rpx;
		.list-box {
			.record-item {
				padding: 30rpx 0;
			}
			.line {
				& + .line {
					border-top: 1rpx solid #EEEEEE;
				}
			}
		}
	}

	.color-333 {
		color: #333;
	}

	.color-666 {
		color: #666;
	}

	.mb-30 {
		margin-bottom: 30rpx;
	}
</style>