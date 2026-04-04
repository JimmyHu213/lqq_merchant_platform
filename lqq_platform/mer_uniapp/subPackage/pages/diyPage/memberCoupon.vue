<template>
	<view class="mobile-page" v-if="configObj && couponList.length">
		<view v-if="listStyle === 0" class="member-coupon" :style="[boxStyle]">
			<!-- 标题部分 -->
			<view class="acea-row row-between-wrapper mb20">
				<view class="f-s-30 fw-500 text-333" :style="[titleColor]">
					{{ configObj.titleConfig.val }}
				</view>
				<view @click="handleMore" v-show="isLogin && merMemberInfo.isMerchantMember" class="f-s-24 text-999 flex-y-center" :style="[moreColor]">更多<i class="iconfont icon-ic_rightarrow f-s-24"></i>
				</view>
			</view>

			<!-- 优惠券列表 -->
			<view class="memCoupon-list">
				<view class="coupon-item flex-y-center relative rd-16" v-for="(item, index) in couponList" :key="index"
					:style="[couponListStyle]">
					<!-- 左侧金额/门槛 -->
					<view class="left-content w-190 flex-col flex-center" :style="[{ color: priceColorStyle }]">
						<view class="price fw-600 fs-52 items-baseline">
							<span class="f-s-32 mr-4">¥</span>
							<span class="num semiBold">{{ item.money }}</span>
						</view>
						<view class="f-s-22 regular">
							{{ item.minPrice > 0 ? '满' + item.minPrice + '可用' : '无门槛' }}
						</view>
					</view>

					<!-- 中间说明 -->
					<view class="middle-content flex-column flex-x-center">
						<view class="f-s-30 mb-12 fw-500 line1 w-280" :style="[scopeColor]">
							{{ item.name }}
						</view>
						<view v-if="item.day" class="f-s-24" :style="[{ color: configObj.deadlineColor.color[0].item }]">
              {{ `领取后${item.day}天内可用`  }}
						</view>
            <view v-else class="f-s-24" :style="[{ color: configObj.deadlineColor.color[0].item }]">
              {{ item.useStartTimeStr + ' - ' + item.useEndTimeStr  }}
            </view>
					</view>

					<!-- 右侧按钮 -->
					<view @click="receiveCoupon(item)" class="right-btn text-center ml20 fs-22 w-136 h-60 lh-60rpx" :style="[btnColorStyle]">{{isLogin&& item.isUse ? '去使用' : '去领取'}}</view>
				</view>
			</view>
		</view>
		<view v-else class="member-coupon" :style="[boxStyle]">
			<!-- 标题部分 -->
			<view class="acea-row row-between-wrapper mb20">
				<view class="title" :style="[titleColor]">
					{{ configObj.titleConfig.val }}
				</view>
				<view @click="handleMore" v-show="isLogin && merMemberInfo.isMerchantMember" class="f-s-24 text-999 flex-y-center" :style="[moreColor]">更多<i class="iconfont icon-ic_rightarrow f-s-24"></i></view>
			</view>
			<!-- 优惠券列表 -->
			<view class="coupon-scroll-list flex pb-10">
				<view class="coupon-card w-300 h-152 rd-12rpx relative flex-y-center"
					v-for="(item, index) in couponList" :key="index" :style="[
            { marginRight: contentConfig },
            { backgroundColor: themeStyle ? couponBgColor : themeRgba },
            { borderColor: priceColorStyle },
            { borderRadius: configObj.contentStyle.val ? configObj.contentStyle.val + 'px' : '0' },
          ]">
					<!-- 标签 -->
					<view class="tag absolute f-s-20 text--w111-fff lh-36rpx" :style="[
              {
              backgroundColor: themeStyle ? labelColor :lightColor,
              color: priceColorStyle,
              borderTopLeftRadius: `${configObj.contentStyle.val}px`,
            }
					]">
						会员专享
					</view>

					<!-- 左侧金额 -->
					<view class="w-154 flex-center" :style="[{ color: priceColorStyle }]">
						<view class="price f-w-600 fs-48 acea-row items-baseline">
							<span class="symbol f-s-32 mr-2">¥</span>
							<span class="num semiBold">{{ item.money }}</span>
						</view>
					</view>

					<!-- 分割线 -->
					<view class="split-line-vertical h-100" :style="[{ borderLeftColor: lightColor }]"></view>

					<!-- 右侧内容 -->
					<view class="right-box w-164 flex-column flex-x-center">
						<view class="condition f-s-24 regular" :style="[{ color: priceColorStyle }]">
							{{ item.minPrice > 0 ? '满' + item.minPrice + '可用' : '无门槛' }}
						</view>
						<view class="scope f-s-22" :style="[scopeColor]">-{{ item.category === 1 ? '商家券' : '商品券' }}-</view>
					</view>

					<!-- 装饰圆角 -->
					<view class="decoration left" :style="[{ background: bgColor }]"></view>
					<view class="decoration right" :style="[{ background: bgColor }]"></view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		setThemeColor,
		setLinearThemeColor,
		themeRgba,
		lightColor
	} from "../../../utils/setTheme";
  import { mapGetters } from "vuex";
  import {toLogin} from "../../../libs/login";
  import {getMemberCouponApi} from "../../../pages/member/memberApi";
  import couponListMixins from "../../../mixins/couponList";
	const coupon = [{
			money: '450',
			minPrice: 299,
			name: '全店通用',
			time: '领取后10天内可用'
		},
		{
			money: '450',
			minPrice: 299,
			name: '指定商品',
			time: '领取后10天内可用'
		},
		{
			money: '450',
			minPrice: 299,
			name: '指定商品',
			time: '领取后10天内可用'
		},
	]
	export default {
		name: "memberCoupon",
    mixins: [couponListMixins],
		props: {
      merMemberInfo:{ //店铺会员信息
        type: Object,
        default: () => {}
      },
			merId: { // 商户id
				type: Number,
				default: 0
			},
			configObj: {
				type: Object,
				default: () => {}
			}
		},
		data() {
			return {
				themeColor: setThemeColor(),
				themeRgba: themeRgba(),
				lightColor: lightColor(),
				couponList: [],
			}
		},
		computed: {
      ...mapGetters(['isLogin']),
			listStyle() {
				return this.configObj.tabConfig.tabVal;
			},
			bgColor() {
				return this.configObj.bgColor.color[0].item;
			},
			//最外层盒子的样式
			boxStyle() {
				return {
             'border-radius': this.configObj.bgStyle.val ? 2 * this.configObj.bgStyle.val + 'rpx' : '0',
              background: this.bgColor,
              margin: 2 * this.configObj.mbConfig.val + 'rpx' + ' ' + 2 * this.configObj.lrConfig.val +'rpx' + ' ' + 0,
               padding: 2 * this.configObj.upConfig.val + 'rpx' + ' ' + '20rpx' + ' ' + 2 * this.configObj.downConfig.val + 'rpx'
            }
			},
			//内容边距
			contentConfig() {
				return this.configObj.contentConfig.val ? 2 * this.configObj.contentConfig.val + 'rpx' : '0';
			},
			scopeColor() {
				return {
          color: this.configObj.scopeColor.color[0].item
        };
			},
			titleColor() {
				return {
          color: this.configObj.titleColor.color[0].item
        };
			},
			labelColor() {
				return this.configObj.labelColor.color[0].item;
			},
			moreColor() {
				return {
          color: this.configObj.moreColor.color[0].item
        };
			},
			themeStyle() {
				return this.configObj.themeStyleConfig.tabVal
			},
			//优惠金额颜色
			priceColorStyle() {
				return this.themeStyle ? this.configObj.priceColor.color[0].item : this.themeColor;
			},
			couponBgColor() {
				return this.configObj.couponBgColor.color[0].item;
			},
			// 优惠券
			couponListStyle() {
				return {
          marginBottom: 2 * this.configObj.contentConfig.val + 'rpx',
          backgroundColor: this.themeStyle ? this.couponBgColor : this.themeRgba,
          borderRadius: 2 * this.configObj.contentStyle.val + 'rpx'
        }
			},
			//领取按钮
			btnColorStyle() {
				return {
          background: this.themeStyle ?
              `linear-gradient(90deg,${this.configObj.btnColor.color[0].item}, ${this.configObj.btnColor.color[1].item})` :
              setLinearThemeColor(),
          color: this.configObj.btnBgColor.color[0].item,
        };
			},
		},
    mounted(){
      this.getCoupon()
    },
    methods: {
      handleMore(){ //去更多
        this.$util.navigateTo(`/pages/member/member_coupon/index?merId=${this.merId}`)
      },
      async getCoupon(){
        if(this.configObj.couponConfig.ids){
          let { data } = await getMemberCouponApi(this.configObj.couponConfig.ids)
          this.couponList  = [...data]
        }else{
          this.couponList = []
        }
      },
      receiveCoupon(item){
        if(this.isLogin){
          if(!this.merMemberInfo.isMerchantMember){
            this.$emit('activate-membership')
          }else{
            if(item.isUse){
              this.goUseCouponPro(item,1, true)
            }else{
              this.fetchReceiveCoupon(item)
            }
          }
        }else{
          toLogin();
        }
      },
    }
	}
</script>

<style scoped lang="scss">
	.member-coupon {
		overflow: hidden;

		.memCoupon-list {
			.coupon-item {
				padding: 32rpx 24rpx 32rpx 0;

				.left-content {
					.price {
						margin-bottom: 6rpx;
					}
				}

				.middle-content {
					flex: 1;
				}

				.right-btn {
					border-radius: 508rpx;
				}
			}
		}

		.coupon-scroll-list {
			overflow-x: auto;

			&::-webkit-scrollbar {
				display: none;
			}

			.coupon-card {
				flex: 0 0 auto;

				.tag {
					top: 0;
					left: 0;
					padding: 0 8rpx;
					border-bottom-right-radius: 16rpx;
				}
			}

			.split-line-vertical {
				width: 2rpx;
				border-left: 2rpx dashed;
				opacity: 0.5;
			}

			.right-box {

				.condition {
					margin-bottom: 6rpx;
				}

				.scope {
					transform: scale(0.9);
					transform-origin: left center;
					white-space: nowrap;
				}
			}

			.decoration {
				position: absolute;
				width: 24rpx;
				height: 24rpx;
				border-radius: 50%;
				top: 50%;
				transform: translateY(-50%);
				z-index: 1;
				border-color: transparent;
			}

			.decoration.left{
        left: -12rpx;
      }
			.right {
        right: -12rpx;
			}
		}
	}
</style>