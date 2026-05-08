<template>
  <view class="coupon-transfer" :data-theme="theme">
    <view class="transfer-card" v-if="status === 0">
      <view class="card-header">
        <text class="title">优惠券转赠</text>
      </view>
      <view class="coupon-info" v-if="couponInfo">
        <view class="coupon-name line1">{{ couponInfo.name }}</view>
        <view class="coupon-money">
          <text class="symbol">￥</text>
          <text class="amount">{{ Number(couponInfo.money) }}</text>
        </view>
        <view class="coupon-condition">满{{ Number(couponInfo.minPrice) }}元可用</view>
      </view>
      <view class="input-section">
        <text class="label">请输入受赠者用户ID</text>
        <input class="uid-input" type="number" v-model="recipientUid" placeholder="请输入对方的用户ID" />
      </view>
      <view class="btn-section">
        <view class="transfer-btn bg-color" @click="doTransfer">立即赠送</view>
      </view>
      <view class="tips">
        <text class="tip-text">提示：转赠后优惠券将从您的账户中移除，无法撤回。</text>
      </view>
    </view>

    <view class="success-card" v-if="status === 1">
      <view class="success-icon">
        <text class="iconfont icon-duigou"></text>
      </view>
      <view class="success-text">赠送成功</view>
      <view class="back-btn" @click="goBack">返回我的优惠券</view>
    </view>
  </view>
</template>

<script>
import { couponTransferApi } from '@/api/lqq.js';
import { toLogin } from '@/libs/login.js';
import { mapGetters } from 'vuex';
let app = getApp();
export default {
  data() {
    return {
      theme: app.globalData.theme,
      couponUserId: 0,
      couponInfo: null,
      recipientUid: '',
      status: 0 // 0=输入, 1=成功
    };
  },
  computed: mapGetters(['isLogin']),
  onLoad(options) {
    if (!this.isLogin) {
      toLogin();
      return;
    }
    this.couponUserId = options.couponUserId || 0;
    if (options.name) this.couponInfo = {
      name: decodeURIComponent(options.name),
      money: options.money || 0,
      minPrice: options.minPrice || 0
    };
  },
  methods: {
    doTransfer() {
      if (!this.recipientUid) {
        uni.showToast({ title: '请输入受赠者用户ID', icon: 'none' });
        return;
      }
      if (!this.couponUserId) {
        uni.showToast({ title: '优惠券信息缺失', icon: 'none' });
        return;
      }
      uni.showModal({
        title: '确认转赠',
        content: '确认将此优惠券转赠给该用户？转赠后无法撤回。',
        success: (res) => {
          if (res.confirm) {
            couponTransferApi(this.couponUserId, parseInt(this.recipientUid)).then(() => {
              this.status = 1;
            }).catch(err => {
              uni.showToast({ title: err || '转赠失败', icon: 'none' });
            });
          }
        }
      });
    },
    goBack() {
      uni.navigateBack();
    }
  }
};
</script>

<style lang="scss" scoped>
.coupon-transfer {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 30rpx 24rpx;
}
.transfer-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  .card-header {
    margin-bottom: 30rpx;
    .title {
      font-size: 36rpx;
      font-weight: bold;
      color: #282828;
    }
  }
  .coupon-info {
    background-color: #f8f8f8;
    border-radius: 12rpx;
    padding: 24rpx;
    margin-bottom: 40rpx;
    text-align: center;
    .coupon-name {
      font-size: 28rpx;
      color: #282828;
      margin-bottom: 12rpx;
    }
    .coupon-money {
      @include main_color(theme);
      .symbol {
        font-size: 28rpx;
      }
      .amount {
        font-size: 56rpx;
        font-weight: bold;
      }
    }
    .coupon-condition {
      font-size: 24rpx;
      color: #999;
      margin-top: 8rpx;
    }
  }
  .input-section {
    margin-bottom: 40rpx;
    .label {
      font-size: 28rpx;
      color: #666;
      margin-bottom: 16rpx;
      display: block;
    }
    .uid-input {
      width: 100%;
      height: 88rpx;
      border: 2rpx solid #e0e0e0;
      border-radius: 12rpx;
      padding: 0 24rpx;
      font-size: 30rpx;
      box-sizing: border-box;
    }
  }
  .btn-section {
    .transfer-btn {
      text-align: center;
      font-size: 32rpx;
      color: #fff;
      padding: 22rpx 0;
      border-radius: 44rpx;
    }
  }
  .tips {
    margin-top: 24rpx;
    .tip-text {
      font-size: 22rpx;
      color: #999;
    }
  }
}
.success-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 80rpx 30rpx;
  text-align: center;
  .success-icon {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    background-color: #09bb07;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 30rpx;
    .iconfont {
      font-size: 60rpx;
      color: #fff;
    }
  }
  .success-text {
    font-size: 36rpx;
    font-weight: bold;
    color: #282828;
    margin-bottom: 40rpx;
  }
  .back-btn {
    display: inline-block;
    font-size: 28rpx;
    @include main_color(theme);
    padding: 16rpx 40rpx;
    border-radius: 30rpx;
    @include coupons_border_color(theme);
  }
}
</style>
