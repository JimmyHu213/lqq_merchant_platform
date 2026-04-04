<template>
	<view id="stores" class="store" :style="[isHome?contentStyle:'']">
		<view class='pictrue' :style="[isHome || isHomeComb?logoStyleRadius:'']">
			<image :src="merchantInfo.avatar" class=""></image>
		</view>
		<view class="text">
			<navigator :url="`/pages/merchant/${type}/index?merId=${merId}`" hover-class="none">
				<view class="flex merchantInfo">
					<text class="name" :style="!isHome?'color: #FFFFFF;':''">{{merchantInfo.name}}</text>
					<text v-if="isShowTypeId" class="iconfont icon-ic_rightarrow" :style="!isHome?'color: #FFFFFF;':''"></text>
				</view>
			</navigator>
			<view class="score">
				<text v-if="merchantInfo.isSelf" :class="marketingType !== ProductMarketingTypeEnum.Groupbuying && marketingType !== ProductMarketingTypeEnum.Seckill?'bg-color bg-color':'bg-red'" class="bt-color mr10 self_min merType">自营</text>
				<text v-if="isShowTypeId && merchantInfo.typeId"
					class="bt-color mr10 merType color-FAAD14">{{merchantInfo.typeId | merchantTypeFilter}}</text>
				<view class='starsList'>
					<block v-for="(itemn, indexn) in merchantInfo.starLevel" :key="indexn">
						<text class='iconfont icon-ic_star1 mr-4' :class="marketingType !== ProductMarketingTypeEnum.Groupbuying && marketingType !== ProductMarketingTypeEnum.Seckill?'font-color':'font-red'"></text>
					</block>
					<block v-show="Number(merchantInfo.starLevel)<5">
						<text v-for="(itemn, indexn) in noStarLevel" :key="indexn"
							class='iconfont icon-ic_star1 noCheck'></text>
					</block>
				</view>
			</view>
		</view>
		<button v-if="type!=='home'" hover-class="none" class="merCollect merCollectBg"
			:class="merchantInfoNew.isCollect ? 'care' : ''" @click="followToggle" :style="[(isHome || isHomeComb) && merchantInfoNew.isCollect ? followColor : '']">
			<view v-if="!merchantInfoNew.isCollect" class="iconfont icon-ic_love"></view>
			<view class="line-heightOne">{{ merchantInfoNew.isCollect ? '已关注' : '关注' }}</view>
		</button>
		<navigator v-if="!isShowTypeId" :url="`/pages/merchant/${type}/index?merId=${merId}`" hover-class="none">
			<button class="merCollect" :class="marketingType !== ProductMarketingTypeEnum.Groupbuying && marketingType !== ProductMarketingTypeEnum.Seckill?'merCollectBg':'bg-red'" hover-class="none">进店</button>
		</navigator>
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
	import {
		getMerCollectAddApi,
		getMerCollectCancelApi
	} from '@/api/merchant.js';
	import {
		followMer
	} from '@/libs/follow.js';
	import {
		mapGetters
	} from "vuex";
	import {
		toLogin
	} from '@/libs/login.js';
  import {ProductMarketingTypeEnum} from "../../enums/productEnums";
	export default {
		computed: {
			...mapGetters(["merchantClassify", "merchantType", 'isLogin', 'uid']),
			//最外层盒子的样式
			boxStyle() {
				return [];
			},
			//头像圆角
			logoStyleRadius() {
				if (this.dataConfig) {
					return {
						'borderRadius': this.dataConfig.logoStyleRadius.val ? this.dataConfig.logoStyleRadius
							.val + 'px' : '0'
					}
				}

			},
			contentStyle() {
				if (this.dataConfig) {
					return {
						'background-color': '#fff',
						'border-radius': this.dataConfig.bgStyle ? 2 * this.dataConfig.bgStyle.val + 'rpx' : '0',
						'margin': this.dataConfig.mbConfig ? 2 * this.dataConfig.mbConfig.val + 'rpx' + ' ' + 2 * this.dataConfig.lrConfig
							.val +
							'rpx' +
							' ' + 0 : '0',

						'padding': this.dataConfig.upConfig ? 2 * this.dataConfig.upConfig.val + 'rpx' + ' ' + '20rpx' + ' ' + 2 * this
							.dataConfig
							.downConfig
							.val + 'rpx' : '0'
					}
				}
			},
			// 关注颜色
			followColor() {
				if (this.dataConfig) {
					return {
						border: '1px solid',
						color: this.dataConfig.followColor.color[0].item,
						background: '#fff'
					}
				}
			}
		},
		props: {
      // 商品类型:0-普通，1-秒杀seckill，2-拼团
      marketingType: {
        type: Number,
        default: () => 0
      },
			// 店铺diy中设置的数据，店铺首页diy中使用
			dataConfig: {
				type: Object,
				default: () => {}
			},
			merchantInfo: {
				type: Object,
				default: () => {}
			},
      // 商户id
			merId: {
				type: Number,
				default: () => 0
			},
			type: {
				type: String,
				default: () => 'detail'
			},
			isShowTypeId: {
				type: Boolean,
				default: () => true
			},
			//是否是首页展示，true是，false不是
			isHome: {
				type: Boolean,
				default: () => false
			},
			//店铺主页是否是头部组件中使用的diy
			isHomeComb: {
				type: Boolean,
				default: () => false
			}
		},
		data() {
			return {
        ProductMarketingTypeEnum: ProductMarketingTypeEnum,
				skeletonShow: true,
				isShow: true,
				avatar: '',
				merchantInfoNew: {
					...this.merchantInfo
				},
				noStarLevel: 0
			}
		},
    watch: {
      merchantInfo: {
        handler(newValue, oldValue) {
          this.merchantInfoNew = newValue
          this.getnoStarLevel()
        },
        immediate: true
      }
    },
		methods: {
			getnoStarLevel: function(nVal, oVal) {
				if (parseInt(this.merchantInfoNew.starLevel) < 5) this.noStarLevel = 5 - parseInt(this.merchantInfoNew
					.starLevel);
			},
			// 设置是否关注
			followToggle: function() {
				if (this.isLogin === false) {
					toLogin();
				} else {
					followMer(this.merchantInfoNew.isCollect, this.merId).then(() => {
						this.$set(this.merchantInfoNew, 'isCollect', !this.merchantInfoNew.isCollect);
						this.$store.commit('MERCHANTJINFO', {
							...this.merchantInfoNew
						});
					});
				}
			}
		}
	};
</script>

<style lang="scss" scoped>
	.noCheck {
		color: rgba(204, 204, 204, .5);
	}

	.care {
		border: 1px solid #FFFFFF;
		background: inherit !important;
	}

	.iconfont {
		//font-size: 24rpx !important;
	}

	.icon-pingweifen {
		color: #ccc;
	}

	.merchantInfo {
		align-items: center;
		margin-bottom: 16rpx;
	}

	.store {
		position: relative;
		// z-index: 5;
		display: flex;
		align-items: center;
		padding: 24rpx;
		//width: 100%;

		// top:12rpx;
		.pictrue {
			width: 80rpx;
			height: 80rpx;
			border-radius: 50%;
			overflow: hidden;
		}

		.easy-loadimage,
		image,
		uni-image {
			width: 100%;
			height: 100%;
		}

		.text {
			flex: 1;
			min-width: 0;
			margin-right: 20rpx;
			margin-left: 12rpx;

			navigator {
				// display: inline-flex;
				align-items: center;
				max-width: 100%;

				.name {
					min-width: 0;
					overflow: hidden;
					white-space: nowrap;
					text-overflow: ellipsis;
					font-weight: bold;
					font-size: 30rpx;
					line-height: 1;
				}

				.iconfont {
					margin-left: 10rpx;
					font-size: 24rpx;
				}
			}

			.score {
				display: flex;
				align-items: center;
				margin-top: 8rpx;
				font-weight: 500;
				font-size: 24rpx;
				line-height: 1;

				.iconfont {
					font-size: 26rpx;
				}
			}
		}

	}

	.self_min {
		text-align: center;
	}

	.starsList {
		padding-bottom: 4rpx;
	}
</style>