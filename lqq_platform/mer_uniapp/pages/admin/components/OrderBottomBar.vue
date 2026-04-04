<template>
  <view class="order-detail-bottom-bar">
    <view class="bar-body">
      <button class="bar-btn" :class="item.type" v-for="item of config" :key="item.text" @click="$emit('action', item.event)">
        {{ item.text }}
      </button>
    </view>
  </view>
</template>

<script>
export default {
  props: {
    config: {
      type: Array,
      default: () => []
    }
  }
}
</script>

<style scoped lang="scss">
.lang{
  height: 72rpx !important;
  width: 100% !important;
}
.order-detail-bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding-bottom: var(--safe-area-inset-bottom);
  background-color: #fff;
}

.bar-body {
  height: 108rpx;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 18rpx;
  padding: 0 20rpx;
}

.bar-btn {
  height: 64rpx;
  border-radius: 50rpx;
  padding-inline: 32rpx;
  font-size: 24rpx;
  color: #fff;
  background: #2291F8;
  display: flex;
  align-items: center;
  justify-content: center;
  &.plain {
    background: none;
    border: 1px solid #DDDDDD;
    color: #666;
  }
}
</style>
