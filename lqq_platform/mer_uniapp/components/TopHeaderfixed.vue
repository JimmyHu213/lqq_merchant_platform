<template>
  <view class="headerBg">
    <view :style="{ height: `${getHeight.barTop}px` }"></view>
    <view :style="{ height: `${getHeight.barHeight}px` }"></view>
    <view class="inner"></view>
  </view>
</template>

<script>
export default {
  name: "TopHeaderfixed",
  data(){
    return {
      getHeight: this.$util.getWXStatusHeight(),
    }
  },
}
</script>

<style scoped lang="scss">
.headerBg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  background-image: linear-gradient(360deg, #F5F5F5 0%, rgba(245, 245, 245, 0) 100%),
  linear-gradient(270deg, #01ABF8 0%, #2A7EFB 100%);
  background-position: left bottom, left top;
  background-repeat: no-repeat;
  background-size: 100% 120rpx, 100% 100%;

.inner {
  height: 356rpx;
}
}
</style>