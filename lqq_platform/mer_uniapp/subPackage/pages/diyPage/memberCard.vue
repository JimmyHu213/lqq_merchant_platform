<template>
	<view>
		<view v-if="configObj" class="member-card-container" :style="[boxStyle]">
			<!-- 顶部会员信息栏 -->
			<view v-if="isLogin && merMemberInfo.isMerchantMember" class="member-header acea-row justify-between" :style="[bgImgStyle]">
				<view class="member-brand">
					<img v-if="configObj.logoConfig.url && logoShow" :src="configObj.logoConfig.url" alt="品牌logo"
						class="brand-image" />
					<span class="brand-name" :style="[merNameColor]">{{ configObj.titleConfig.val }}</span>
					<span v-if="levelShow" class="member-level" :style="[memNameColor]">{{merMemberInfo.levelName}}</span>
				</view>
				<view v-if="ruleShow" @click="handleRules" class="member-rule" :style="[rulesNameColor]">会员规则</view>
				<!-- 购物余额信息 -->
				<view class="balance-section">
				 <view>
           <view v-if="priceShow" class="balance-info" :style="[moneyColor]">
             <span class="balance-label f-s-24">购物余额：</span>
             <span class="balance-amount line-heightOne f-s-36 fw-600 semiBold">¥ {{merMemberInfo.shoppingCreditsBalance}}</span>
             <navigator :url="`/pages/member/shoppingFund_details/index?merId=${merId}`" hover-class="none">
               <i :style="[moneyColor]" class="iconfont icon-ic_rightarrow"></i>
             </navigator>
           </view>
         </view>
					<view v-show="btnShow" class="recharge-btn-box" @click="handleMore">
						<view class="recharge-btn" :style="[btnColorStyle]">充值</view>
					</view>
				</view>
			</view>
      <view v-else class="member-header flex-column flex-y-center" :style="[bgImgStyleShip]">
        <view class="f-s-32 text--w111-fff">邀您加入店铺会员</view>
        <view class="text--w111-fff f-s-28 mt-12 mb-28">-0元入会，尊享丰富会员特权-</view>
        <view class="bg--w111-fff w-264 rd-8rpx" @click="handleMemberShip">
          <view :style="[btnColorStyle]" class="h-64 flex-center">立即加入会员</view>
        </view>
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
	import {
		setThemeColor,
		themeRgba
	} from "../../../utils/setTheme";
  import { mapGetters } from "vuex";
  import {toLogin} from "../../../libs/login";
	let app = getApp();
	export default {
		name: "memberCard",
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
				urlDomain: this.$Cache.get("imgHost"),
				themeColor: setThemeColor(),
				themeRgba: themeRgba()
			};
		},
		computed: {
      ...mapGetters(['isLogin']),
			bgImgUrl() {
				return this.configObj.cardLogoConfig.url ? this.configObj.cardLogoConfig.url :
					`${this.urlDomain}+'crmebimage/presets/cardBg.png'`;
			},
      //背景图片
      bgImgStyleShip() {
        return {
          'background-image': `linear-gradient(rgba(0, 0, 0, 0.4), rgba(0, 0, 0, 0.4)), url(${this.bgImgUrl})`,
          'border-radius': this.configObj.contentStyle.val ? 2 * this.configObj.contentStyle.val + 'rpx' : '0',
        };
      },
			//背景图片
			bgImgStyle() {
				return {
					'background-image': `url(${this.bgImgUrl})`,
					'border-radius': this.configObj.contentStyle.val ? 2 * this.configObj.contentStyle.val + 'rpx' : '0',
				};
			},
			//最外层盒子的样式
			boxStyle() {
				return {
					background: `linear-gradient(${this.configObj.bgColor.color[0].item}, ${this.configObj.bgColor.color[1].item})`,
          'border-radius': this.configObj.bgStyle.val ? 2 * this.configObj.bgStyle.val + 'rpx' : '0',
          margin: 2 * this.configObj.mbConfig.val + 'rpx' + ' ' + 2 * this.configObj.lrConfig.val + 'rpx' + ' ' + 0,
          padding: 2 * this.configObj.upConfig.val + 'rpx' + ' ' + '20rpx' + ' ' + 2 * this.configObj.downConfig.val + 'rpx'
				}
			},
			themeStyle() {
				return this.configObj.themeStyleConfig.tabVal
			},
			// 领取按钮
			btnColorStyle() {
				return {
					background: this.themeStyle ? this.configObj.btnColor.color[0].item : this.themeRgba,
					color: this.themeStyle ? this.configObj.itemBgColor.color[0].item : this.themeColor,
				}
			},
			// 店铺名称
			merNameColor() {
				return {
					color: this.configObj.merNameColor.color[0].item
				};
			},
			// 会员文字
			memNameColor() {
				return {
					color: this.configObj.memNameColor ? this.configObj.memNameColor.color[0].item : '',
					backgroundColor: 'rgba(0, 0, 0, 0.3)'
				};
			},
			// 规则文字
			rulesNameColor() {
				return {
					color: this.configObj.rulesNameColor.color[0].item
				};
			},
			// 购物金文字
			moneyColor() {
				return {
					color: this.configObj.moneyColor.color[0].item
				};
			},
			logoShow() {
				if (this.configObj.typeConfig.activeValue.indexOf(0) !== -1) {
					return true;
				} else {
					return false;
				}
			},
			levelShow() {
				//等级
				if (this.configObj.typeConfig.activeValue.indexOf(1) !== -1) {
					return true;
				} else {
					return false;
				}
			},
			ruleShow() {
				//规则
				if (this.configObj.typeConfig.activeValue.indexOf(2) !== -1) {
					return true;
				} else {
					return false;
				}
			},
			btnShow() {
				//充值按钮
				if (this.configObj.typeConfig.activeValue.indexOf(3) !== -1) {
					return true;
				} else {
					return false;
				}
			},
			priceShow() {
				//购物金
				if (this.configObj.typeConfig.activeValue.indexOf(4) !== -1) {
					return true;
				} else {
					return false;
				}
			},
			// 会员权益列表
			benefitList() {
				return [{
						name: '会员专享价',
						icon: '💎'
					},
					{
						name: '充值赠送',
						icon: '💰'
					},
					{
						name: '会员专享券',
						icon: '🎫'
					},
					{
						name: '入会有礼',
						icon: '🎁'
					},
				];
			},
		},
    methods: {
      handleMore(){ //去充值
        this.$util.navigateTo(`/pages/member/recharge_amount/index?merId=${this.merId}`)
      },
      // 会员规则
      handleRules(){
        this.$util.navigateTo(`/pages/member/membership_rules/index?merId=${this.merId}&level=${this.merMemberInfo.level}`)
      },
      handleMemberShip(){
        if(this.isLogin){
          this.$emit('activate-membership')
        }else{
          toLogin();
        }
      },
    }
	}
</script>

<style scoped lang="scss">
	.member-card-container {
		overflow: hidden;
	}

	.member-header {
    background-repeat: no-repeat;
    background-size: 100% 100%;
		color: #ffffff;
		padding: 28rpx 0 28rpx 28rpx;
		position: relative;
		height: 250rpx;
		overflow: hidden;
	}

	.member-brand {
		display: flex;
		z-index: 1;
		height: 48rpx;
		align-items: center;
	}

	.brand-image {
		width: 44rpx;
		height: 44rpx;
		margin-right: 14rpx;
		border-radius: 8rpx;
	}

	.brand-name {
		font-size: 28rpx;
		font-weight: 500;
		margin-right: 18rpx;
	}

	.member-box {
		height: 34rpx;
		border-radius: 8rpx;
	}

	.member-level {
		display: inline-block;
		line-height: 34rpx;
		font-size: 20rpx;
		height: 34rpx;
		padding: 0 8rpx;
		border-radius: 8rpx;
	}

	.member-rule {
		font-size: 24rpx;
		z-index: 1;
		width: 136rpx;
		height: 48rpx;
		text-align: center;
		line-height: 48rpx;
		background-color: rgba(0, 0, 0, 0.3);
		border-radius: 24rpx 0 0 24rpx;
	}

	.balance-section {
		position: absolute;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 28rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		background: rgba(0, 0, 0, 0.3);
	}

	.balance-info {
		display: flex;
		align-items: center;
		height: 52rpx;
	}

	.recharge-btn-box {
		background-color: #fff;
		border-radius: 8rpx;
		line-height: 54rpx;
		height: 52rpx;

		.recharge-btn {
			padding: 0 20rpx;
			border-radius: 8rpx;
			font-size: 28rpx;
		}
	}

	.level-progress {
		padding: 40rpx 24rpx;
		background-color: #ffffff;
	}

	.level-dots {
		display: flex;
		justify-content: space-between;
		align-items: center;
		position: relative;
		padding: 0;
	}

	.level-dots::before {
		content: '';
		position: absolute;
		top: 20rpx;
		left: 0;
		right: 0;
		height: 4rpx;
		background-color: #e8e8e8;
		z-index: 0;
	}

	.level-dot {
		position: relative;
		z-index: 1;
		display: flex;
		flex-direction: column;
		align-items: center;
		font-size: 24rpx;
		color: #999;
		width: 20%;
	}

	.level-dot::before {
		content: '';
		width: 24px;
		height: 24px;
		border-radius: 50%;
		background-color: #e8e8e8;
		margin-bottom: 8rpx;
		transition: all 0.3s ease;
		position: relative;
	}

	.level-dot.current::before {
		background-color: #ff4d4f;
		box-shadow: 0 0 0 6rpx rgba(255, 77, 79, 0.2);
		width: 32rpx;
		height: 32rpx;
		top: -4rpx;
	}

	.level-dot.current::after {
		content: '当前';
		position: absolute;
		top: -50rpx;
		left: 50%;
		transform: translateX(-50%);
		background-color: #ff4d4f;
		color: #ffffff;
		padding: 8rpx 20rpx;
		border-radius: 30rpx;
		font-size: 24rpx;
		font-weight: bold;
		white-space: nowrap;
	}

	.level-dot.current {
		color: #ff4d4f;
		font-weight: bold;
	}

	.member-benefits {
		display: flex;
		justify-content: space-around;
		padding: 32rpx 0;
		background-color: #ffffff;
		border-top: 2rpx solid #f0f0f0;
	}

	.benefit-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		cursor: pointer;
		width: 25%;
	}

	.benefit-icon {
		width: 120rpx;
		height: 120rpx;
		background-color: #fff5f5;
		border-radius: 50%;
		display: flex;
		justify-content: center;
		align-items: center;
		margin-bottom: 16rpx;
		transition: all 0.3s ease;
	}

	.benefit-icon-content {
		font-size: 56rpx;
		color: #ff4d4f;
	}

	.benefit-name {
		font-size: 24rpx;
		color: #666;
		text-align: center;
		word-break: keep-all;
	}

	.benefit-item:hover .benefit-icon {
		transform: scale(1.1);
	}
</style>