<template>
	<view :data-theme="theme" class="seckill theme-gradient-color">
		<view class="seckill_activity pad-30 acea-row row-between acea-no-warp" :style="[...bgImage]">
			<view class="mt-26">
				<view class="acea-row price-box items-baseline">
          <text class="title f-s-28 mr-16">拼团价</text>
          <baseMoney :money="productInfo.groupPrice" symbolSize="32" integerSize="48" decimalSize="32"
                     incolor="FFFFFF" weight></baseMoney>
					<text class="ot-price text-dec regular f-s-32 ml-16">￥{{productInfo.price}}</text>
				</view>
			</view>
			<view class="acea-row row-column groupFoter text-right mt-26">
				<view class="groupNum theme-font-color">{{groupBuyActivityResponse.buyCount}}人团</view>
			</view>
		</view>
	</view>
</template>

<script>
	import svipPrice from '@/components/svipPrice.vue';
	const app = getApp();
	export default {
		components: {
			svipPrice
		},
		props:['productInfo','groupBuyActivityResponse'],
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				theme: app.globalData.theme,
				bgColor: {
					'bgColor': '#fff',
					'Color': '',
					'width': '36rpx',
					'timeTxtwidth': '16rpx',
					'isDay': true
				},
			}
		},
		computed: {
			bgImage() {
				return [
					{
						// 'background':`url(${this.urlDomain}crmebimage/presets/groupMb.png)`
						'background-image': `url(${this.urlDomain}crmebimage/presets/groupNewMb.png)`
					},
					{
						'background-size':`cover`
					}
				]
			}
		},
	}
</script>

<style scoped lang="scss">
	.pad-30 {
		padding: 0 30rpx;
	}
	.seckill_activity {
		width: 750rpx;
    height: 152rpx;
		color: #fff;
		padding: 0 30rpx;
		background-size: cover;

		.groupFoter {
			.groupNum{
				background: #FFFFFF;
				opacity: 0.9;
				font-size: 26rpx;
				color: #E93323;
				text-align: center;
				padding: 6rpx 16rpx;
				border-radius: 50rpx 40rpx 40rpx 0;
			}
		}
	}
	.theme-font-color {
		@include main_color(theme);
	}
	.theme-bg-color {
		@include main_bg_color(theme)
	}
	.theme-gradient-color {
		@include index-gradient(theme)
	}
</style>