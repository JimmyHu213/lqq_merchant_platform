<template>
  <!-- 白色背景 -->
  <view class="search-bar" :class="customClass">
    <view class="input-wrapper">
      <view class="iconfont icon-ic_search" />
      <input class="search-input" :placeholder="placeholder" :value="value"
             @input="$emit('change', $event.target.value)" @confirm="$emit('confirm', $event.target.value)"
             confirm-type="search" placeholder-style="color: var(--color)" />
    </view>
    <slot />
  </view>
</template>

<script>
export default {
  name: "SearchBar",
  model: {
    prop: "value",
    event: "change"
  },
  props: {
    customClass: {
      type: String,
      default: ""
    },
    placeholder: {
      type: String,
      default: ""
    },
    value: {
      type: String,
      default: ""
    }
  }
}
</script>

<style scoped lang="scss">
.search-bar {
  --content-height: 72rpx;
  --font-size: 24rpx;
  --color: #ccc;
  --bg-color: #fff;
  display: flex;
}

.input-wrapper {
  height: var(--content-height);
  flex: 1;
  background-color: var(--bg-color);
  border-radius: 100rpx;
  padding-inline: 32rpx;

  display: flex;
  align-items: center;

  .iconfont {
    margin-right: 16rpx;
    color: #999;
  }

  .search-input {
    flex: 1;
    font-size: var(--font-size);
  }
}
</style>