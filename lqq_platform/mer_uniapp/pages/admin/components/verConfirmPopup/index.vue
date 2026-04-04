<template>
	<view class="">
		<uni-popup ref="popup" background-color="#fff" borderRadius="16px" @maskClick="refreshPage">
			<view class="pop-box">
				<view class="title color-333 fs-32 fw-500">
					核销成功
				</view>
				<view class="text color-666 fs-30">
					当前订单已完成核销
				</view>
				<view class="btn-box">
					<view class="btn" @click="popupHandle(false)">知道了</view>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
	export default {
		name: 'ver-confirm-popup',
		props: {
			// 核销码
			verifyCode: {
				type: String,
				default: ''
			},
			// 返回页面
			backPage: {
				type: String,
				default: ''
			}
		},
		data() {
			return {}
		},
		methods: {
			// 核销确认弹窗
			popupHandle(type) {
				if (type) {
					this.$refs.popup.open()
				} else {
					this.$refs.popup.close()
					this.refreshPage()
				}
			},
			// 刷新页面
			refreshPage() {
				uni.redirectTo({
					url: `/pages/admin/cancel/list?verifyCode=${this.verifyCode}&backPage=${this.backPage}`
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.pop-box {
		width: 600rpx;
		height: 310rpx;
		padding: 40rpx;

		// border-radius: 14rpx;
		// padding-bottom: 20rpx;
		.title {

			text-align: center;
		}

		.text {

			text-align: center;
			margin: 24rpx 0 40rpx 0;
		}

		.btn-box {


			.btn {
				width: 100%;
				height: 72rpx;
				border-radius: 50rpx;
				text-align: center;
				line-height: 72rpx;
				background: #2A7EFB;
				color: #FFFFFF;
			}
		}
	}
</style>