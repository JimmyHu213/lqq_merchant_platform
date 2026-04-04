<template>
  <view class="order-address-wrapper" :style="{ '--line-bg': `url(${lineBg})` }">
    <view class="order-address-left">
      <view class="order-user-info f-s-30">
        <text class="order-user-name-text">{{ orderInfo.userName || orderInfo.realName }}</text>
        <text>{{ orderInfo.userPhone }}</text>
      </view>
      <view class="order-address">地址：{{ orderInfo.userAddress }}</view>
    </view>
    <button class="contact-action-btn iconfont icon-ic_Phone" @click="handleMakeCallPhone(orderInfo.userPhone)"></button>
    <button class="contact-action-btn iconfont icon-icon_Location" @click="handleOpenLocation(orderInfo)"></button>
  </view>
</template>

<script>
import { handleMakeCallPhone, handleOpenLocation } from "@/libs/order";

export default {
  name: "OrderAddress",
  props: {
    orderInfo: Object
  },
  data() {
    return {
      lineBg: `${this.$Cache.get("imgHost")}/static/images/line.jpg`
    };
  },
  methods: {
    handleMakeCallPhone,
    handleOpenLocation
  }
};
</script>

<style scoped lang="scss">
.order-address-wrapper {
  display: flex;
  border-radius: 24rpx;
  padding: 32rpx 32rpx;
  align-items: center;
  color: #282828;
  background: var(--line-bg) no-repeat left bottom / 100% 4rpx #fff;

  .order-address-left {
    flex: 1;
    overflow: hidden;
    margin-right: 62rpx;
  }

  .order-user-name-text {
    margin-right: 24rpx;
  }

  .order-address {
    margin-top: 16rpx;
    font-size: 24rpx;
    color: #999999;
  }



  .contact-action-btn {
    width: 56rpx;
    height: 56rpx;
    background-color: #edf2f9;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28rpx;
    color: #2a7efb;

    &+.contact-action-btn {
      margin-left: 32rpx;
    }
  }
}
</style>
