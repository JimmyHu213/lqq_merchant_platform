<template>
	<text class="price-box semiBold" :class="isActivity ? 'activity-font-color' : 'theme-font-color'">
		<text class="icon">￥</text>
		<text class="big-num">{{priceArr[0]}}</text>
		<text>.</text>
		<text class="small-num">{{priceArr[1]}}</text>
	</text>
</template>

<script>
	export default{
		props: {
			price: {
				type: String,
				default: '0'
			},
			isActivity: {
				type: Boolean,
				default: false
			}
		},
		computed:{
			priceArr(){
				return this.price && this.price.toString().split('.')
			}
		}
	}
</script>

<style lang="scss" scoped>
	.price-box{
		// color: #E93323;
		// @include main_color(theme);
		font-weight: 600;
		.icon{
			font-size: 24rpx;
		}
		.big-num{
			font-size: 40rpx;
		}
		.small-num{
			font-size: 24rpx;
		}
	}
	.theme-font-color {
		@include main_color(theme);
	}
	.activity-font-color {
		color: #E93323 !important;
	}
</style>