<template>
  <view class="coupon-container":data-theme="theme">
    <!-- 优惠券列表 -->
    <view v-if="couponsList.length" class="coupon-box">
      <!-- 可领取优惠券 -->
      <view v-for="(item,index) in couponsList" :key="index" class="coupon-item available">
        <!-- 左侧金额区域 -->
        <view class="coupon-left">
          <view class="coupon-amount font-color semiBold"><span class="f-s-28 semiBold">¥</span>{{item.money}}</view>
          <view class="coupon-condition font-color">满{{ item.minPrice }}可用</view>
        </view>
        
        <!-- 右侧信息区域 -->
        <view class="coupon-right flex-between-center">
         <view class="">
           <view class="coupon-name fw-500 line1">{{ item.name }}</view>
           <block v-if="item.isFixedTime">
             <view class="coupon-date">
               {{ item.useStartTimeStr | dateFormat }}-{{ item.useEndTimeStr | dateFormat }}
             </view>
           </block>
           <block v-else>
             <view class="coupon-date">{{'领取后'+ item.day+ '天内可用'}}</view>
           </block>
           <view class="coupon-range">{{item.category | couponTypeFilter}}</view>
         </view>
          <!-- 立即领取按钮 -->
          <view @click="getCoupon(item)" class="coupon-btn text-center" :class="item.isUse?'btn-use':'btn-get'">{{item.isUse ? '去使用' : '立即领取'}}</view>
        </view>
      </view>
      <!-- 没有更多提示 -->
      <view class="no-more">
        <text>没有更多优惠券了～</text>
      </view>
    </view>
    <view class='noCommodity' v-if="!couponsList.length && !loadingcoupon">
      <emptyPage title='暂无优惠券可使用哦~' :imgSrc="urlDomain+'crmebimage/presets/noCoupon.png'"></emptyPage>
    </view>
  </view>
</template>

<script>
import navBar from '@/components/navBar';
import couponListMixins from "../../../mixins/couponList";
import emptyPage from '@/components/emptyPage.vue'
let app = getApp();
export default {
  name: "index",
  mixins: [couponListMixins],
  components: {
    navBar,
    emptyPage
  },
  data() {
    return {
      urlDomain: this.$Cache.get("imgHost"),
      merid: 0,
      theme: app.globalData.theme,
      identityType: 1
    };
  },
  onLoad(options) {
    this.merid = parseInt(options.merId)
  },
  mounted() {
    this.getCouponList()
  },
  methods: {
    getCoupon(item){
      //item.isUse ? '去使用' : '立即领取'
      if(item.isUse){
        this.goUseCouponPro(item,1, true)
      }else{
        this.fetchReceiveCoupon(item)
      }
    }
  }
}
</script>

<style scoped lang="scss">
.use-style{

}
/* 页面容器 */
.coupon-container {
  background-color: #fff;
  min-height: 100vh;
}
.available{
  @include main_rgba_color(theme);
}
/* 优惠券列表 */
.coupon-box {
  padding: 36rpx 24rpx;
}

/* 优惠券项 */
.coupon-item {
  display: flex;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  position: relative;
}

/* 左侧金额区域 */
.coupon-left {
  width: 220rpx;
  color: #ffffff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  padding: 20rpx 0;
}

/* 装饰性虚线 */
.coupon-left::after {
  content: '';
  position: absolute;
  left: 216rpx;
  top: 25rpx;
  bottom: 10rpx;
  width: 2rpx;
  @include dashed_border_left(theme);
  //@include dashed_border_left(theme);
}

/* 左侧金额区域 - 已失效 */
.expired {
  background: #F5F5F5;
  color: #999999;
}

/* 优惠券金额 */
.coupon-amount {
  font-size: 52rpx;
  line-height: 1;
  margin-bottom: 8rpx;
}

/* 优惠券使用条件 */
.coupon-condition {
  font-size: 22rpx;
  opacity: 0.9;
}

/* 右侧信息区域 */
.coupon-right {
  flex: 1;
  padding: 26rpx 24rpx;
}

/* 右侧信息区域 - 已失效 */
.coupon-right.expired {
  color: #999999;
}

/* 优惠券名称 */
.coupon-name {
  font-size: 28rpx;
  color: #333333;
  margin-bottom: 8rpx;
  width: 296rpx;
}

/* 优惠券有效期 */
.coupon-date {
  font-size: 22rpx;
  color: #333;
  margin-bottom: 10rpx;
}

/* 优惠券使用范围 */
.coupon-range {
  font-size: 22rpx;
  color: #666666;
}

/* 领取按钮 */
.coupon-btn {
  width: 136rpx;
  height: 60rpx;
  line-height: 56rpx;
  border-radius: 127px;
  font-size: 24rpx;
  white-space: nowrap;
}

/* 去使用按钮 */
.btn-use {
  color: #ffffff;
  @include linear-gradient(theme);
}
.btn-get{
  @include border_color(theme);
  @include main_color(theme);
  @include coupons_light_color(theme);
}

/* 已失效按钮 */
.btn-expired {
  position: absolute;
  right: 20rpx;
  top: 50%;
  transform: translateY(-50%);
  background-color: #E0E0E0;
  color: #999999;
  padding: 12rpx 24rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  white-space: nowrap;
}

/* 没有更多提示 */
.no-more {
  text-align: center;
  padding: 40rpx;
  font-size: 28rpx;
  color: #999999;
}

/* 分隔线顶部半圆缺口 */
.coupon-item::before {
  content: '';
  position: absolute;
  left: 205rpx;
  top: -10rpx;
  width: 30rpx;
  height: 30rpx;
  background-color: #fff;
  border-radius: 50%;
  z-index: 2;
}

/* 分隔线底部半圆缺口 */
.coupon-item::after {
  content: '';
  position: absolute;
  left: 205rpx;
  bottom: -10rpx;
  width: 30rpx;
  height: 30rpx;
  background-color: #fff;
  border-radius: 50%;
  z-index: 2;
  /* 重置虚线属性，只保留半圆缺口样式 */
  top: auto;
  width: 30rpx;
  background-image: none;
}
</style>