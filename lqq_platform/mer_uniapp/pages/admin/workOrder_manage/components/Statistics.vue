<template>
  <view class="borderPad mt-28 mb20">
    <view class="borRadius14 bg--w111-fff px-24 py-32 color28 f-s-30 f-w-500 relative">
      <view class="mb-32">工单管理</view>
      <view class="borRadius14 word_order flex-between-center">
        <view class="list text-center">
          <view class="flex-center mb-20">
            <view class="w-36 h-36 rd-4rpx"><text class="iconfont icon-ic-gift3"></text></view>
            <view class="title">待服务工单</view>
          </view>
          <view class="f-s-40 semiBold">{{serviceStaffInfo.awaitWorkNum}}</view>
        </view>
        <view class="line"></view>
        <view class="list text-center">
          <view class="flex-center mb-20">
            <view class="w-36 h-36 rd-4rpx"><text class="iconfont icon-ic-gift3"></text></view>
            <view class="title">已服务工单</view>
          </view>
          <view class="f-s-40 semiBold">{{serviceStaffInfo.workedNum}}</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: "statistics",
  props: {
    serviceStaffInfo:{
      type: Object,
      default: {}
    }
  }
}
</script>

<style scoped lang="scss">
.word_order{
  width: 662rpx;
  height: 182rpx;
  background: #F9F9F9;
  .list{
    width: 330rpx;
  }
  .title{
    font-size: 24rpx;
    margin-left: 16rpx;
    color: #999999FF;
  }
  .line{
    width: 2rpx;
    height: 72rpx;
    background-color: #D8D8D8FF;
  }
}
</style>