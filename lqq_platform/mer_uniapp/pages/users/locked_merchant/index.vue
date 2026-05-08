<template>
  <view class="locked-merchant" :data-theme="theme">
    <view class="merchant-card" v-if="merchant">
      <view class="card-header">
        <text class="title">我的锁定商铺</text>
        <text class="sub-title">首次消费后自动绑定</text>
      </view>
      <view class="card-body" @click="goMerchant">
        <view class="mer-row acea-row">
          <image class="mer-avatar" :src="merchant.avatar || merchant.rectangleLogo" mode="aspectFill"></image>
          <view class="mer-info">
            <view class="mer-name">{{ merchant.name }}</view>
            <view class="mer-category" v-if="merchant.categoryName">{{ merchant.categoryName }}</view>
            <view class="mer-address line1" v-if="merchant.detailedAddress">{{ merchant.detailedAddress }}</view>
            <view class="mer-phone" v-if="merchant.phone">
              <text class="iconfont icon-dianhua"></text>
              <text>{{ merchant.phone }}</text>
            </view>
          </view>
        </view>
        <view class="go-arrow">
          <text class="iconfont icon-jiantou"></text>
        </view>
      </view>
    </view>

    <view class="no-merchant" v-if="!merchant && !loading">
      <view class="empty-icon">
        <text class="iconfont icon-dianpu"></text>
      </view>
      <text class="empty-text">您还没有绑定商铺</text>
      <text class="empty-tip">首次在某商铺消费后，将自动绑定为您的专属商铺</text>
      <view class="go-search bg-color" @click="goNearby">去附近看看</view>
    </view>
  </view>
</template>

<script>
import { getUserInfoApi } from '@/api/user.js';
import { getLockedMerchantApi } from '@/api/lqq.js';
import { toLogin } from '@/libs/login.js';
import { mapGetters } from 'vuex';
let app = getApp();
export default {
  data() {
    return {
      theme: app.globalData.theme,
      merchant: null,
      loading: true
    };
  },
  computed: mapGetters(['isLogin']),
  onLoad() {
    if (!this.isLogin) {
      toLogin();
      return;
    }
    this.loadData();
  },
  methods: {
    loadData() {
      this.loading = true;
      getUserInfoApi().then(res => {
        let userInfo = res.data;
        if (userInfo.lockedMerchantId) {
          return getLockedMerchantApi(userInfo.lockedMerchantId);
        }
        this.loading = false;
        return null;
      }).then(res => {
        if (res) {
          this.merchant = res.data;
        }
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    goMerchant() {
      if (this.merchant) {
        uni.navigateTo({
          url: `/pages/merchant/home/index?id=${this.merchant.id}`
        });
      }
    },
    goNearby() {
      uni.navigateTo({
        url: '/pages/merchant/nearby/index'
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.locked-merchant {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx 24rpx;
}
.merchant-card {
  background-color: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  .card-header {
    padding: 30rpx 30rpx 20rpx;
    border-bottom: 1rpx solid #f0f0f0;
    .title {
      font-size: 34rpx;
      font-weight: bold;
      color: #282828;
      display: block;
    }
    .sub-title {
      font-size: 24rpx;
      color: #999;
      margin-top: 8rpx;
      display: block;
    }
  }
  .card-body {
    padding: 24rpx 30rpx;
    display: flex;
    align-items: center;
    .mer-row {
      flex: 1;
      .mer-avatar {
        width: 140rpx;
        height: 140rpx;
        border-radius: 16rpx;
        margin-right: 20rpx;
        flex-shrink: 0;
      }
      .mer-info {
        flex: 1;
        min-width: 0;
        .mer-name {
          font-size: 32rpx;
          font-weight: 600;
          color: #282828;
          margin-bottom: 8rpx;
        }
        .mer-category {
          font-size: 22rpx;
          @include main_color(theme);
          @include coupons_border_color(theme);
          display: inline-block;
          padding: 2rpx 12rpx;
          border-radius: 8rpx;
          margin-bottom: 8rpx;
        }
        .mer-address {
          font-size: 24rpx;
          color: #999;
          margin-bottom: 6rpx;
        }
        .mer-phone {
          font-size: 24rpx;
          color: #666;
          .iconfont {
            font-size: 24rpx;
            margin-right: 6rpx;
          }
        }
      }
    }
    .go-arrow {
      flex-shrink: 0;
      margin-left: 12rpx;
      .iconfont {
        font-size: 28rpx;
        color: #ccc;
      }
    }
  }
}
.no-merchant {
  text-align: center;
  padding: 120rpx 60rpx;
  .empty-icon {
    width: 160rpx;
    height: 160rpx;
    border-radius: 50%;
    background-color: #f0f0f0;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 30rpx;
    .iconfont {
      font-size: 72rpx;
      color: #ccc;
    }
  }
  .empty-text {
    font-size: 32rpx;
    color: #282828;
    font-weight: 600;
    display: block;
    margin-bottom: 12rpx;
  }
  .empty-tip {
    font-size: 24rpx;
    color: #999;
    display: block;
    margin-bottom: 40rpx;
  }
  .go-search {
    display: inline-block;
    font-size: 28rpx;
    color: #fff;
    padding: 16rpx 60rpx;
    border-radius: 40rpx;
  }
}
</style>
