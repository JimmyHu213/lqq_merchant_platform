<template>
	<view :data-theme="theme" class="user_payment bg--w111-fff">
    <view class="user_heard">
      <!-- #ifdef MP || APP-->
      <nav-bar ref="navBarRef" navTitle="充值购物金" :isShowMenu="false" :isBackgroundColor="false"></nav-bar>
      <!-- #endif -->
      <view class="payment-box">
        <view
            :style="{ background: bgColor}"
            class="balance-container relative borRadius14">
          <view class="w-168 h-168 absolute member_bg"><img :src="`${urlDomain}crmebimage/presets/member_bg.png`" class="w-100-p111- h-100-p111-"></view>
          <view class="flex-between-center balance-header">
            <view>购物金余额(元)</view>
            <view class="balance-links px-16rpx">
              <navigator :url="`/pages/member/recharge_record/index?merId=${merId}`">
                <text>充值记录</text>
              </navigator>
              <text class="link-separator">|</text>
              <navigator :url="`/pages/member/shoppingFund_details/index?merId=${merId}`">
                <text>明细</text>
              </navigator>
            </view>
          </view>
          <view class="flex items-baseline mb-40">
            <text class="f-s-60 text--w111-fff semiBold mr-12">{{merMemberInfo.shoppingCreditsBalance}}</text>
            <text class="f-s-26 text--w111-fff regular">包含赠送 {{merMemberInfo.giftAmount}}</text>
          </view>
        </view>
      </view>
    </view>
    <view class="bg--w111-fff balance-amount relative borRadius14">
      <recharge-page :packageList="packageList" @changePriceSuccess="changePriceSuccess"></recharge-page>
      <view class="mt-48">
        <view class="mb-24 f-s-32 text-333">注意事项：</view>
        <view class="tips-samll f-s-24 text-999 lh-40rpx">
          1.赠送金额仅本店消费使用，不可提现；<br>
          2.充值本金可在「充值记录」中申请退款，赠送金额不单独还，退款时自动扣除；<br>
          3.消费优先抵扣充值本金；<br>
          4.充值如遇问题，请联系店铺客服。
        </view>
      </view>
      <bottom-button @handleConfirm="handleConfirm" :backgroundColor="bgColor"></bottom-button>
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
    setLinearThemeColor, shallowGradientColor, themeRgba
  } from "../../../utils/setTheme";
	import navBar from '@/components/navBar';
	import rechargePage from "../../../subPackage/pages/recharge";
	import BottomButton from "../../../components/bottomButton";
  import { mapGetters } from "vuex";
  import {memberShoppingCreditsApi, memberUserInfoApi} from "../memberApi";
  import {handleGoRecharge} from "../member";
	let app = getApp();
	export default {
		name: "index",
		components: {
			navBar,
			rechargePage,
			BottomButton
		},
		data() {
			return {
        merMemberInfo: {},
        merId: 0,
				urlDomain: this.$Cache.get("imgHost"),
				theme: app.globalData.theme,
				bgColor: '',
				iconColor: '#FFFFFF',
				packageList: [],
        recharge: {
          id: '',
          price: ''
        },
        backgroundColor: ''
			}
		},
		onLoad(options) {
      this.merId = Number(options.merId)
			this.bgColor = setLinearThemeColor();
      this.backgroundColor = shallowGradientColor();
      this.getMerMemberInfo();
      this.getPackageList()
		},
		methods: {
      //店铺会员信息
      async getMerMemberInfo(){
        this.$store.dispatch("getMerMemberInfo", this.merId).then(data => {
          this.merMemberInfo = data
        });
      },
      async getPackageList() {
        try {
          const { data } = await memberShoppingCreditsApi(this.merId)
          this.packageList = (data || []).map(({ rechargeAmount, giftAmount, id }) => ({
            price: rechargeAmount,
            giveMoney: giftAmount,
            id: id
          }))
          this.getPrice(this.packageList[0])
        } catch (e) {
          this.packageList = []
        }
      },
			/**
			 * 选择金额
			 */
      changePriceSuccess(e) {
				// 兼容处理：优先直接取值，取不到则尝试从 detail 中取（小程序原生事件机制）
        // #ifdef MP
				const data = e.detail || e;
        const item = data.__args__[0]
				console.log('充值金额 item:', item);
        // #endif
        // #ifndef MP
        const item = e;
        console.log('充值金额 item:', item);
        // #endif
				if (item) {
					this.getPrice(item);
				}
			},
      getPrice(item){ //充值金额
        this.recharge = {
          id: item.id,
          price: item.price
        }
        console.log('this.recharge',this.recharge)
      },
			handleConfirm() {

        handleGoRecharge(this.recharge, this.merId)
      }
		}
	}
</script>

<style scoped lang="scss">
.member_bg{
  right: 52rpx;
  top: 84rpx;
}
.user_heard{
  @include shallow-gradient(theme);
}
.payment-box{
  padding: 28rpx 24rpx 0 24rpx;
}
	.user_payment {
		height: 100vh;
	}

	.balance-amount {
		padding: 12rpx 24rpx;
		top: -188rpx;
	}

	.balance-container {
		position: relative;
		height: 396rpx;
		padding: 32rpx 0 24rpx 44rpx;
	}

	.balance-bg {
		width: 100%;
		height: 278rpx;
	}

	.balance-header {
		margin-bottom: 14rpx;
		color: #fff;
	}

	.balance-links {
		font-size: 24rpx;
		display: flex;
		align-items: center;
		width: 202rpx;
		height: 48rpx;
		line-height: 48rpx;
		background-color: rgba(255, 255, 255, .2);
		border-radius: 24rpx 0 0 24rpx;
	}

	.link-separator {
		opacity: 0.7;
		margin: 0 8rpx
	}
</style>