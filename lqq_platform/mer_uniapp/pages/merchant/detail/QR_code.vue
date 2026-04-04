<template>
	<uni-popup ref="qrcodePopup" type="center" borderRadius="20px 20px 0 0" :mask-click="true" @change="change">
		<view class="qr-content-wrapper">
			<view class="content-box">
				<view class="shop-info">
					<view class="shop-name">{{ shopName }}</view>
					<view class="shop-tip">保存二维码可分享店铺给好友哦~</view>
				</view>
				<view class="qr-box">
          <!-- #ifdef MP-->
          <image :src="merQRCode" @click="handleSavePosterPath" :style="{width: qrSize + 'px', height: qrSize + 'px'}"></image>
          <!-- #endif -->
          <!-- #ifndef MP-->
          <canvas class="qrcode-canvas" canvas-id="qrcode"
                  :style="{width: qrSize + 'px', height: qrSize + 'px'}"></canvas>
          <!-- #endif -->
				</view>
			</view>
			<view class="close-btn" @click="close">
				<text class="iconfont icon-ic_close1"></text>
			</view>
		</view>
	</uni-popup>
</template>

<script>
	import {
		mapGetters
	} from "vuex";
  import Routine from "../../../libs/routine";
  import Cache from "../../../utils/cache";
	export default {
		name: "QR_code",
		computed: mapGetters(['globalData', 'uid']),
		props: {
			show: {
				type: Boolean,
				default: false
			},
			merId: {
				type: [String, Number],
				default: ''
			},
			shopName: {
				type: String,
				default: ''
			},
			shopLogo: {
				type: String,
				default: ''
			}
		},
		data() {
			return {
				qrSize: 192,
				qrText: '',
				merQRCode: ''
			}
		},
		watch: {
			show(val) {
				if (val) {
					this.$nextTick(() => {
						this.$refs.qrcodePopup && this.$refs.qrcodePopup.open();
						// #ifdef MP
            this.generateQRCode();
						// #endif
						// #ifndef MP
						this.makeQRCode();
						// #endif
					});
				} else {
					this.$refs.qrcodePopup && this.$refs.qrcodePopup.close();
				}
			}
		},
		mounted() {
			if (this.show) {
				this.$nextTick(() => {
					this.$refs.qrcodePopup && this.$refs.qrcodePopup.open();
					// #ifdef MP
          this.generateQRCode();
					// #endif
					// #ifndef MP
					this.makeQRCode();
					// #endif

				});
			}
		},
		methods: {
			close() {
				this.$emit('close');
			},
			change(e) {
				if (!e.show) {
					this.$emit('close');
				}
			},
      //保存海报
      handleSavePosterPath(){
        this.$util.savePosterPath(this.merQRCode).then(userInfo => {});
      },
			generateQRCode() {
        let data = {
          scene: `merId=${this.merId}&sd=${this.uid}`,
          page: 'pages/merchant/home/index'
        }
        Routine.getWxQrCode(data)
					.then(res => {
						this.merQRCode = res;
					})
			},
			// 生成二维码；
			makeQRCode() {
				let link = `${this.globalData.frontDomain}/pages/merchant/home/index?merId=${this.merId}&sd=${this.uid}`
				this.$util.makeSpreadQRCode(link, 'qrcode', this.qrSize)
					.then(res => {
						this.merQRCode = res;
					})
			},
		}
	}
</script>

<style scoped lang="scss">
	.qr-content-wrapper {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.content-box {
		width: 544rpx;
		height: 564rpx;
		background-color: #ffffff;
		border-radius: 24rpx;
		padding: 40rpx 80rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		box-sizing: border-box;

		.shop-info {
			text-align: center;
			margin-bottom: 18rpx;

			.shop-name {
				font-size: 32rpx;
				font-weight: 500;
				color: #282828;
				margin-bottom: 20rpx;
			}

			.shop-tip {
				font-size: 24rpx;
				color: #282828;
			}
		}

		.qr-box {
			position: relative;
			width: 384rpx;
			/* Match qrSize */
			height: 384rpx;
			/* Match qrSize */
			display: flex;
			justify-content: center;
			align-items: center;

			.qrcode-canvas {
				width: 200px;
				height: 200px;
			}
		}
	}

	.close-btn {
		margin-top: 50rpx;
		width: 80rpx;
		height: 80rpx;
		display: flex;
		justify-content: center;
		align-items: center;

		.iconfont {
			font-size: 44rpx;
			color: #ffffff;
		}
	}
</style>