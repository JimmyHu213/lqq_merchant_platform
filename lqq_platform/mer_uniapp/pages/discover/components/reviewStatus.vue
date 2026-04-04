<template>
  <view class="video-status" :style="'top: '+ statusTop +'px;'">
    <view v-if="auditStatus == 2 || auditStatus == 3" class="status-title">
      <text class="iconfont icon-a-ic_tanhao1 font-color-red"></text>
      <text class="title">{{auditStatus == 2 ? '审核未通过' : '平台关闭'}}，内容仅自己可见</text>
    </view>
    <view v-else class="status-title">
      <text class="iconfont icon-icon_clock1"></text>
      <text class="title">正在审核，内容仅自己可见</text>
    </view>
    <view class="status_info">
      <text class="refusal">{{(auditStatus == 2 || auditStatus == 3) ? refusal : '发布的内容审核通过后，将在首页展示！'}}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: "reviewStatus",
  props: {
    auditStatus: {
      type: [String, Number],
      default: ''
    },
    refusal: {
      type: String,
      default: ''
    }
  },
  data(){
    return{
      getHeight: this.$util.getWXStatusHeight(),
      statusTop: 0, //审核状态样式
    }
  },
  mounted(){
    // #ifdef MP
    this.statusTop = this.getHeight.barTop;
    // #endif
    // #ifdef H5
    this.statusTop = 60;
    // #endif
  }
}
</script>

<style lang="scss" scoped>
.video-status {
  width: 690rpx;
  position: absolute;
  left: 30rpx;
  background: rgba(0, 0, 0, .55);
  border-radius: 10rpx;
  // top: 60rpx;
  padding: 26rpx 0 24rpx 30rpx;

  .iconfont {
    font-size: 30rpx;
  }

  .icon-icon_clock1 {
    color: #FC8327;
  }

  .status-title {
    flex-direction: row;
    align-items: center;

    .title {
      margin-left: 20rpx;
      color: #ffffff;
      font-size: 28rpx;
    }
  }

  .refusal {
    color: #ffffff;
    font-size: 22rpx;
    margin: 15rpx 0 0 48rpx;
  }

  .image {

    width: 28rpx;
    height: 28rpx;
  }
}
</style>