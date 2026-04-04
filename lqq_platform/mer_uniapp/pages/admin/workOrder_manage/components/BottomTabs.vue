<template>
  <view class="order-list-bottom-tabs">
    <view class="tab-item" v-for="item of tabs" :key="item.index" :class="{ active: item.index === value }"
          @click="$emit('input', item.index)">
      <view class="iconfont f-s-40" :class="item.icon"></view>
      <view class="tab-item-label">{{ item.label }}</view>
    </view>
  </view>
</template>

<script>
import {ASSIGNED, HOMEPAGE, UNASSIGNED} from "../config";

export default {
  name: "BottomTabs",
  model: {
    prop: "value",
    event: "input"
  },
  props: {
    value: {
      type: Number,
      default: 2
    }
  },
  data() {
    return {
      tabs: [
        {
          index: HOMEPAGE,
          label: "工单",
          icon: "icon-gongdan-weixuanzhong"
        },
        {
          index: UNASSIGNED,
          label: "接单",
          icon: "icon-jiedan-weixuanzhong"
        },
        {
          index: ASSIGNED,
          label: "我的工单",
          icon: "icon-dingdan-weixuanzhong"
        }
      ]
    }
  }
}
</script>

<style scoped lang="scss">
//@import "@/static/iconfont/iconfont-next.css";

.order-list-bottom-tabs {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ffffff;
  padding-bottom: env(safe-area-inset-bottom);
  display: flex;
  z-index: 1;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-flow: column nowrap;
  align-items: center;
  justify-content: center;
  height: 100rpx;
  font-size: 20rpx;
  line-height: 28rpx;
  color: #333;

  &.active {
    color: #2A7EFB;
  }

  .iconfont-next {
    font-size: 38rpx;
    width: 40rpx;
    height: 40rpx;
    text-align: center;
    line-height: 40rpx;
  }

  .tab-item-label {
    margin-top: 6rpx;
  }
}
</style>
