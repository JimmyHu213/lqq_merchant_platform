<template>
	<view class="">
		<view class="writeOff borRadius14">
			<view class="verification-code-box flex-col">
				<view class="verification-code-title fs-28 fw-500">核销码</view>
				<image class="verification-code-img" :src="codeImg"></image>
			</view>
			<view class="punchCard-info">
				<view class='flex-center fs-24 mt-14'>
					<view class="fs-32 fw-500">{{merOrderInfo.verifyCode}}</view>
					<view class="copy-btn color-666 ml-14">
						<!-- #ifndef H5 -->
						<text class='copy line-heightOne' @tap='handleCopy(merOrderInfo.verifyCode)'>复制</text>
						<!-- #endif -->
						<!-- #ifdef H5 -->
						<text class='copy copy-data' :data-clipboard-text="merOrderInfo.verifyCode">复制</text>
						<!-- #endif -->
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import ClipboardJS from "@/plugin/clipboard/clipboard.js";
	export default {
		name: 'VerificationQRCode',
		props: {
			// 二维码
			codeImg: {
				type: String,
				default: ''
			},
			// 商户订单信息
			merOrderInfo: {
				type: Object,
				default: () => {
					return {}
				}
			}
		},
		data() {
			return {};
		},
		methods: {
			handleCopy(verifyCode) {
				this.$emit('handleCopy', verifyCode)
			}
		}
	}
</script>

<style scoped lang="scss">
	.verification-code-box {
		justify-content: center;
		align-items: center;
		padding-top: 48rpx;

		.verification-code-img {
			width: 320rpx;
			height: 320rpx;
		}
	}
</style>