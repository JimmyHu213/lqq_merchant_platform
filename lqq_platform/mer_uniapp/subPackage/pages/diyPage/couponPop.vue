<template>
  <view class="relative coupon-pop-box">
    <view class="coupon-pop-container" :style="{ backgroundImage: `url(${urlDomain}crmebimage/presets/couponPop.png)`}">
      <!-- 内容区域 -->
      <view class="coupon-pop-content">
        <view class="text-333 f-s-40 fw-500 text-center mb-12">恭喜解锁会员身份</view>
        <view class="coupon-pop-subtitle mb-40 text-center f-s-28">- 会员专享优惠券 -</view>
        <!-- 优惠券列表 -->
        <view class="px-40">
          <view v-for="item in couponList" :key="item.id" class="coupon-item mb20" :style="{'background-image': `url(${urlDomain}crmebimage/presets/couponBgSmall.png)`}">
            <view class="h-120 flex items-center mb20">
              <view class="w-154 f-s-56 semiBold text-center font-color-red"><span class="f-s-28">¥</span> {{item.money}}</view>
              <view class="coupon-info ml-12">
                <view class="fw-500 f-s-28 text-333 line1 w-300">{{item.name}}</view>
                <view class="f-s-24 text-666 mt-6">{{item.useStartTime}} - {{ item.useEndTime }}</view>
              </view>
            </view>
          </view>
        </view>

      </view>
    </view>
    <!-- 关闭按钮 -->
    <view class="coupon-pop-close" @click="closeCouponPop">
      <view class="iconfont icon-ic_close1 f-s-50 font-color"></view>
    </view>
  </view>
</template>

<script>
export default {
  name: "couponPop",
  props: {
    couponList:{
      type: Array,
      default: () => []
    },
  },
  data(){
    return {
      urlDomain: this.$Cache.get("imgHost"),
    }
  },
  methods: {
    closeCouponPop(){
      this.$emit('close');
    }
  }
}
</script>

<style scoped lang="scss">
.px-40{
  max-height: 420rpx;
  overflow: auto;
}
.coupon-pop-box{
  margin-top: -228rpx;
}
.coupon-item{
  width: 470rpx;
  height: 120rpx;
  background-repeat: no-repeat;
  background-size: 100% 100%;
}
/* 优惠券弹窗样式 */
.coupon-pop-container {
  position: relative;
  width: 550rpx;
  padding-bottom: 32rpx;
  background-size: 100% 100%;
  background-repeat: no-repeat;
}

.coupon-pop-content {
  padding-top: 188rpx;
}

.coupon-pop-subtitle {
  color: #6E4933;
}

.coupon-info {
  flex: 1;
}



.coupon-date {
  font-size: 22rpx;
  color: #999;
}

.coupon-pop-close {
  position: absolute;
  z-index: 3;
  bottom: -100rpx;
  right: 262rpx;
}
</style>