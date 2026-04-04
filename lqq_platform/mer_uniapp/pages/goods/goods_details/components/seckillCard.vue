<template>
	<view :data-theme="theme" class="theme-gradient-color">
		<view class="seckill_activity pad-30 acea-row row-between acea-no-warp" :style="[...bgImage]">
			<view class="acea-row items-baseline">
				<text class="title f-s-28 mr-16">秒杀价</text>
        <view class="mt-26">
          <baseMoney :money="productPrice.price" symbolSize="32" integerSize="48" decimalSize="32"
                     incolor="FFFFFF" weight></baseMoney>
        </view>
			</view>
			<view v-if="seckillTime" class="acea-row row-column seckillTime mt-16 theme-font-color">
				<text class="f-s-20 text-right mb-4"
					:class="seckillStatus == 2 ? 'end_title' : 'text-white'">{{seckillStatus === 0? '活动已结束' : seckillStatus === 2 || seckillStatus === 3? '距活动开始' : '距活动结束'}}</text>
				<countDown v-if="seckillStatus > 0" :is-day="true" :tip-text="' '" :day-text="' '" :hour-text="' : '"
					:minute-text="' : '" :second-text="' '" :datatime="seckillTime" :isCol="false" :bgColor="bgColor">
				</countDown>
			</view>
		</view>
	</view>

</template>

<script>
	import svipPrice from '@/components/svipPrice.vue';
	import countDown from '@/components/countDown';
	import {
		setThemeColor
	} from '@/utils/setTheme.js'
	import {
		mapGetters
	} from "vuex";
	const app = getApp();
	export default {
		components: {
			countDown,
			svipPrice
		},
		props: {
			// 0=已结束 1=抢购中 2=即将开始 3=明日预告
			seckillStatus: {
				type: Number,
				default: 0
			},
			productInfo: {
				type: Object,
				default: () => {}
			},
			seckillTime: {
				type: Number,
				default: 0
			},
			//选中的价格
			productPrice: {
				type: Object,
				default: () => {},
			}
		},
		data() {
			return {
        urlDomain: this.$Cache.get("imgHost"),
				theme: this.$Cache.get("theme"),
				bgColor: {
					'bgColor': '#fff',
					'Color': '#E93323',
					'width': '36rpx',
					'timeTxtwidth': '16rpx',
					'isDay': true
				},
			}
		},
		computed: {
      ... mapGetters(["globalData"]),
      bgImage() {
        return [
          {
            // 'background':`url(${this.urlDomain}crmebimage/presets/seckillCard.png)`
						'background':`url(${this.urlDomain}crmebimage/presets/seckillNewMb.png)`
          },
          {
            'background-size':`cover`
          },
          {
            'background-repeat': `no-repeat`
          }
        ]
      }
		},
		mounted() {
			this.bgColor.Color = setThemeColor();
		},
	}
</script>

<style scoped lang="scss">
	.pad-30 {
		padding: 0 30rpx;
	}

	.mb12 {
		margin-bottom: 12rpx;
	}

	.seckill_activity {
		width: 750rpx;
		height: 152rpx;
		color: #fff;

		.price {
			font-size: 50rpx;
			margin-right: 6rpx;
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