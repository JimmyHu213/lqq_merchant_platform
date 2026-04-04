<template>
  <view class="record-list">
    <!-- 单条记录 -->
    <view class="record-item">
      <!-- 记录详情循环展示 -->
      <view class="record-row flex-between-center" v-for="(detail, index) in item.details" :key="detail.key">
        <view>
          <text v-show="detail.label" class="f-s-28 text-666">{{ detail.label }}：</text>
          <text
              class="f-s-28 text-333"
              :class="{
              'font-color': detail.key === 'paidAmount'
            }"
          >
            {{ detail.prefix }} {{ item[detail.key] | filterEmpty }}
          </text>
        </view>
        <text class="font-color f-s-28">
          <text v-if="index === 0" class="font-color f-s-28"><slot name="status"></slot></text>
          <!--          <text v-if="index === 0 && item.refundStatus>0">{{ items.refundStatus  }}</text>-->
        </text>
      </view>
      <!-- 操作按钮 -->
      <view class="record-actions">
        <slot name="operation" :items="item"></slot>
      </view>
    </view>
  </view>
</template>

<script>

export default {
  name: "recordList",
  props: {
    item:{
      type: Object,
      default: function() {
        return {};
      }
    },
    index: {
      type: Number,
      default: 0,
    },
    title: {
      type: String,
      default: '',
    }
  },
  data(){
    return {
      orderNo: ''
    }
  },
  methods: {
  }
}
</script>

<style scoped lang="scss">
/* 记录列表 */
.record-list {
  padding: 28rpx 24rpx 0 24rpx;
}

/* 单条记录 */
.record-item {
  background-color: #ffffff;
  border-radius: 12rpx;
  padding: 32rpx 24rpx;
}

/* 记录行 */
.record-row {
  margin-bottom: 24rpx;
}

/* 最后一行记录行 */
.record-row:last-child {
  margin-bottom: 30rpx;
}

/* 操作按钮容器 */
.record-actions {
  display: flex;
  justify-content: flex-end;
  gap: 20rpx;
}
</style>