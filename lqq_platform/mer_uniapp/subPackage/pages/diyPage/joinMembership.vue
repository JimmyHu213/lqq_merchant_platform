<template>
  <view class="membership-container relative" :data-theme="theme">
    <!-- 关闭按钮 -->
   <view class="relative" @click="handleClose">
     <CloseIcon></CloseIcon>
   </view>
    <!-- 品牌Logo -->
    <view class="brand-logo">
      <image :src="avatar" mode="aspectFill"></image>
    </view>
    
    <!-- 标题 -->
    <view class="title">
      <text class="main-title text-333">{{name}}邀您加入品牌会员</text>
      <text v-show="levelList.length" class="subtitle text-999">您将尊享以下权益</text>
    </view>
    
    <!-- 权益列表 -->
    <view class="benefits-list items-center mb-60">
      <view v-for="item in levelList" :key="item.id" class="benefit-item">
        <view class="benefit-icon diamond mr-20">
          <img v-show="item.selectedIcon" :src="item.selectedIcon" class="w-100-p111- h-100-p111-">
        </view>
        <text class="benefit-text text-333 f-s-28">{{item.name}}</text>
      </view>
    </view>
    
    <!-- 协议同意 -->
    <view class="agreement mb-40 f-s-24">
      <checkbox-group class="checkgroup acea-row" @change='isAgree=!isAgree' style="align-items: flex-end;">
        <label>
          <checkbox :checked="isAgree ? true : false" class="checkgroup acea-row" value="agreement" color="$theme-color" />
          <text class="agreement-text text-999 ml-12">我已阅读并同意</text>
          <text class="agreement-link font-color" @click.stop="userAgree(`member/card/open/info/${merId}`)">《会员开卡协议》</text>
          <text class="agreement-text font-color" @click.stop="userAgree(`shop/member/card/registration/authorization/info`)">《会员授权协议》</text>
        </label>
      </checkbox-group>
    </view>
    
    <!-- 开通按钮 -->
    <view class="join-btn bg-color" @click="handleJoin">
      <text class="f-s-28 fw-500 text--w111-fff">开通会员</text>
    </view>


  </view>
</template>

<script>
import {memberBecomeApi} from "../../../pages/member/memberApi";
import {goToAgreement} from "../../../libs/order";
let app = getApp();
export default {
  name: "joinMembership",
  props: {
    merId: {
      type: Number,
      default: 0,
    },
    name: {
      type: String,
      default: '',
    },
    avatar: {
      type: String,
      default: '',
    }
  },
  data(){
    return {
      theme: app.globalData.theme,
      isAgree: false,
      levelList: []
    }
  },
  watch: {
    merId: {
      handler() {
        this.getLevelData();
      },
      immediate: true
    }
  },
  methods: {
    userAgree(e){
      goToAgreement(e)
    },

    async getLevelData() {
      let data = await this.$store.dispatch("getMemberLevelList", this.merId);
      this.levelList = data.length ? data[0].benefitsList : []
    },
    // 关闭弹窗
    handleClose() {
      this.$emit('close');
    },
    // 开通会员
    async handleJoin() {
      if (!this.isAgree) return this.$util.Tips({
        title: '请勾选会员协议'
      });
      try {
        uni.showLoading({
          mask: true
        });
        let { data } = await memberBecomeApi(this.merId)
        uni.hideLoading();
        this.$emit('handleSuccessful', data.couponList);
      }catch (err) {
        uni.hideLoading();
        return this.$util.Tips({
          title: err
        });
      }
    }
  }
}
</script>

<style lang="scss" scoped>
/deep/ checkbox .uni-checkbox-input.uni-checkbox-input-checked {
  @include main_bg_color(theme);
  @include coupons_border_color(theme);
  color: #fff !important;
  border-radius: 50%;
}
.membership-container {
  padding: 40rpx 24rpx;
  background-color: $crmeb-bg-color;
  border-radius: 20rpx 20rpx 0 0;
  z-index: 99;
}

/* 品牌Logo */
.brand-logo {
  width: 136rpx;
  height: 136rpx;
  margin: 24rpx auto 30rpx;
  border-radius: 50%;
  overflow: hidden;
  border: 4rpx solid $crmeb-bg-color-grey;
}

.brand-logo image {
  width: 100%;
  height: 100%;
}

/* 标题 */
.title {
  text-align: center;
  margin-bottom: 48rpx;
}

.main-title {
  display: block;
  font-size: 34rpx;
  font-weight: 500;
  margin-bottom: 16rpx;
}

.subtitle {
  display: block;
  font-size: 24rpx;
}

/* 权益列表 */
.benefits-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  column-gap: 30rpx;
  row-gap: 40rpx;
  padding: 0 60rpx;
}

.benefit-item {
  display: flex;
  align-items: center;
}

.benefit-icon {
  width: 68rpx;
  height: 68rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #FFF2E8; /* 统一的淡橙色背景 */
  flex-shrink: 0;
}


.benefit-icon.wallet {
  background-color: rgba(233, 51, 35, 0.1);
}

.benefit-icon.coupon {
  background-color: rgba(29, 176, 252, 0.1);
}

.benefit-icon.gift {
  background-color: rgba(255, 152, 0, 0.1);
}

/* 协议 */
.agreement {
  label {
    display: flex;
    align-items: center;
  }
}


.agreement-text {
  margin: 0 8rpx;
}

/* 开通按钮 */
.join-btn {
  border-radius: 80rpx;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
}

</style>