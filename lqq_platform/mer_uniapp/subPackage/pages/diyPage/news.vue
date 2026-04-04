<template>
	<!-- 新闻播报 -->
	<view>
		<view v-if="itemNew.length" class='news acea-row row-middle' :style="[boxStyle]">
			<view class='pictrue skeleton-rect'>
				<image :src='logoConfig'></image>
			</view>
			<view class='swiperTxt skeleton-rect'>
				<view class="acea-row row-between-wrapper h-100-p111-" v-if="direction && itemNew.length > 0" @click="moreTab(itemNew[0].chiild[1].val)">
					<!-- #ifdef MP -->
					<!-- 小程序使用CSS动画实现滚动 -->
					<view class="mp-scroll-container">
						<view class="mp-scroll-content">
							<text class="mp-scroll-text" :style="{color: textColor}">{{itemNew[0].chiild[0].val}}</text>
						</view>
					</view>
					<!-- #endif -->
					
					<!-- 非小程序使用uniNoticeBar -->
					<!-- #ifndef MP -->
					<uniNoticeBar 
						:scrollable="true" 
						:showGetMore="true" 
						:backgroundColor="'#fff'" 
						:color="textColor"
						:moreColor="'#888'" 
						:speed="50" 
						:single="true" 
						:text="itemNew[0].chiild[0].val">
					</uniNoticeBar>
					<!-- #endif -->
				</view>
				<swiper v-else :indicator-dots="indicatorDots" :autoplay="autoplay" interval="2500" :duration="duration"
					vertical="true" circular="true">
					<block v-for="(item,index) in itemNew" :key='index'>
						<swiper-item catchtouchmove='catchTouchMove'>
							<view @click="moreTab(item.chiild[1].val)" class='acea-row row-between-wrapper'
								hover-class='none'>
								<view class='text acea-row row-between-wrapper'>
									<view class='newsTitle line1' :style="{color:textColor}">
										{{item.chiild[0].val}}
									</view>
								</view>
<!--								<view class='iconfont icon-ic_rightarrow'></view>-->
							</view>
						</swiper-item>
					</block>
				</swiper>
			</view>
		</view>
	</view>
</template>

<script>
	// +----------------------------------------------------------------------
	// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
	// +----------------------------------------------------------------------
	// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------
	import uniNoticeBar from '@/components/uniNoticeBar/uni-notice-bar.vue';
	export default {
		components: {
			uniNoticeBar
		},
		name: 'news',
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			},
			isSortType: {
				type: String | Number,
				default: 0
			}
		},
		data() {
			return {
				indicatorDots: false,
				autoplay: true,
				duration: 500,
			};
		},
		computed: {
			//文本列表
			itemNew() {
				return this.dataConfig.listConfig && this.dataConfig.listConfig.list ? this.dataConfig.listConfig.list : []
			},
			//图标设置
			logoConfig() {
				return this.dataConfig.logoConfig && this.dataConfig.logoConfig.url ? this.dataConfig.logoConfig.url : ''
			},
			//文本滚动方向
			direction() {
				return this.dataConfig.directionConfig && this.dataConfig.directionConfig.tabVal ? this.dataConfig.directionConfig.tabVal : 0
			},
			//最外层盒子的样式
			boxStyle() {
				const color1 = this.dataConfig.bgColor.color[0].item;
				const color2 = this.dataConfig.bgColor.color[1].item;
				
				return {
					borderRadius: this.dataConfig.bgStyle.val * 2 + 'rpx',
					// 优先使用渐变，小程序不支持时使用第一个颜色作为备用
					background: color1 === color2 ? color1 : `linear-gradient(${color1}, ${color2})`,
					// #ifdef MP
					backgroundColor: color1, // 小程序备用方案
					// #endif
					margin: this.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + this.dataConfig.lrConfig.val * 2 + 'rpx' +
						' ' + 0,
					padding: this.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + '20rpx' + ' ' + this.dataConfig.downConfig
						.val * 2 + 'rpx',
					color: this.dataConfig.textColor.color[0].item
				}
			},
			//文字位置
			txtStyle() {
				let txtStyle = this.dataConfig.textPosition.tabVal;
				if (txtStyle == 0) {
					return {
						'text-align': 'left'
					}
				} else if (txtStyle == 1) {
					return {
						'text-align': 'center'
					}
				} else {
					return {
						'text-align': 'right'
					}
				}
			},
			//文字颜色
			textColor() {
				return this.dataConfig.textColor.color[0].item
			},
		},
		methods: {
			moreTab(url) {
				this.$util.navigateTo(url);
			}
		}
	}
</script>

<style lang="scss" scoped>
	.news {
		display: flex;
		align-items: center;
		flex-wrap: nowrap;
		box-shadow: 0 10rpx 30rpx #f5f5f5;
	}

	.news .pictrue {
		width: 130rpx;
		height: 36rpx;
		border-right: 1rpx solid #ddd;
		padding-right: 23rpx;
		box-sizing: content-box;
	}

	.news .pictrue image {
		width: 100%;
		height: 100%;
	}

	.news .swiperTxt {
		width: 536rpx;
		height: 78rpx;
		line-height: 78rpx;
		overflow: hidden;
		margin-left: 22rpx;
	}

	.news .swiperTxt .text {
		width: 100%;
	}

	.news .swiperTxt .text .label {
		font-size: 20rpx;
		color: #ff4c48;
		width: 64rpx;
		height: 30rpx;
		border-radius: 40rpx;
		text-align: center;
		line-height: 28rpx;
		border: 2rpx solid #ff4947;
	}

	.news .swiperTxt .text .newsTitle {
		width: 100%;
		font-size: 24rpx;
		color: #666;
	}

	.news .swiperTxt .iconfont {
		font-size: 28rpx;
		color: #888;
	}

	.news .swiperTxt swiper {
		height: 100%;
	}

	/* 小程序专用滚动样式 */
	/* #ifdef MP */
	.mp-scroll-container {
		width: 100%;
		height: 78rpx;
		overflow: hidden;
		position: relative;
	}

	.mp-scroll-content {
		height: 100%;
		position: relative;
		overflow: hidden;
	}

	.mp-scroll-text {
		display: inline-block;
		white-space: nowrap;
		height: 78rpx;
		line-height: 78rpx;
		font-size: 24rpx;
		animation: mp-scroll 15s linear infinite;
		padding-left: 100%;
	}

	@keyframes mp-scroll {
		0% {
			transform: translateX(0);
		}
		100% {
			transform: translateX(-100%);
		}
	}
	/* #endif */
</style>