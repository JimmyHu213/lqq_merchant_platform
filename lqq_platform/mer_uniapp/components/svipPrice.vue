<template>
	<view class="acea-row items-baseline" v-if="productPrice">
		<view :class="priceColor.color?'':'x-money'" class="mr-12 acea-row items-baseline relative x-money" :style="svipPriceStyle.topStyle?[svipPriceStyle.topStyle,priceColor]:[priceColor]">
			<view class="f-s-26">￥</view>
			<view v-if="productPrice.groupPrice" class='tui-skeleton-rect semiBold' :style="[svipIconStyle.price]">{{productPrice.groupPrice}}</view>
			<view v-else class='tui-skeleton-rect semiBold' :style="[svipIconStyle.price]">{{userIsPaidMember && productPrice.vipPriceType == 1?productPrice.vipPrice:productPrice.price}}</view>
		</view>
		<view v-if="userIsPaidMember && productPrice.vipPriceType == 1" class="svip-icon acea-row row-middle" :style="[svipIconStyle.svipBox]">
			<view :style="[svipIconStyle.svipPrice]">SVIP会员价</view>
		</view>
		
		<view v-if="!userIsPaidMember && paidMemberPriceDisplay==='all' && productPrice.vipPriceType == 1 && !productPrice.groupPrice" class="svip-price acea-row row-middle" :style="[svipPriceStyle.svipBox]">
			<view class="icon pl-10 acea-row row-middle" :style="[svipPriceStyle.icon]">SVIP</view>
			<view class="price ml-6 semiBold" :style="[svipPriceStyle.svipPrice]">¥{{productPrice.vipPrice}}</view>
		</view>
		<view v-if="productPrice.vipPriceType == 2" class="svip-price acea-row row-middle" :style="[svipPriceStyle.svipBox]">
			<view class="icon pl-10 acea-row row-middle" :style="[svipPriceStyle.icon]">会员价</view>
			<view class="price ml-6 semiBold" :style="[svipPriceStyle.svipPrice]">¥{{productPrice.vipPrice}}</view>
		</view>
	</view>
</template>

<script>
	import {configMap} from "../utils";

  export default {
    computed: configMap(['userIsPaidMember','paidMemberPriceDisplay']),
		props: {
			//是否展示返回按钮
			productPrice: {
				type: Object,
				default: null
			},
			//SVIP样式图标
			svipIconStyle:{
				type: Object,
				default: null
			},
			//普通商品样式
			svipPriceStyle:{
				type: Object,
				default: null
			},
			//价格颜色
			priceColor: {
				type: Object,
				default: ()=>{
					return {}
				}
			}
		},
		mounted() {}
	}
</script>

<style scoped lang="scss">
	.x-money {
		top: 5rpx;
		font-weight: 700;
		@include main_color(theme);
	
	}
	.svip-price {
		padding: 0 8rpx 0 0;
		background: #FFF0D1;
		.icon{
			font-weight: 600;
			color: #FDDAA4;
			padding-right: 6rpx;
			background: linear-gradient( 180deg, #484643 100%,#1F1B17 100%);
		}
		.price{
			color: #333333;
		}
	}
	.svip-icon {
		padding: 0 16rpx;
		background: linear-gradient( 180deg, #FDF6EC 0%, #FFE3B7 100%);
		color: #70490C;
	}
</style>