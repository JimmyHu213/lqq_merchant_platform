<template>
  <view class="record-list">
    <view class="record-actions" @click="requestRefund(item)">
      <button class="refund-btn text-333 f-s-24">{{title}}</button>
    </view>
    <!-- 单条记录 -->
    <view class="record-item" v-for="(item, index) in records" :key="index">
      <!-- 记录详情循环展示 -->
      <view class="record-row flex-between-center" v-for="(detail, index) in item.details" :key="detail.key">
       <view>
         <text class="f-s-28 text-666">{{ detail.label }}：</text>
         <text
             class="f-s-28 text-333"
             :class="{
              'font-color': detail.key === 'paidAmount'
            }"
         >
           {{ detail.prefix }} {{ item[detail.key] }}
         </text>
       </view>
        <text v-if="index === 0" class="font-color f-s-28">衣柜看</text>
      </view>

      <!-- 操作按钮 -->
      <view class="record-actions" @click="requestRefund(item)">
        <button class="refund-btn text-333 f-s-24">{{title}}</button>
      </view>
    </view>
    <!-- 申请退款 -->
    <request-refund ref="requestRefundRef" :orderNo="orderNo" @handleSubmit="handleSubmit"></request-refund>
  </view>
</template>

<script>
import requestRefund from "./requestRefund";

export default {
  name: "recordList",
  components: {
    requestRefund
  },
  props: {
    records:{
      type: Array,
      default: function() {
        return [];
      }
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
    // 申请退款
    requestRefund(item){
      this.orderNo = item.orderNo
      this.$refs.requestRefundRef.open()
    },
    handleSubmit(){
      this.$emit('handle-submit')
    }
  }
}
</script>

<style scoped lang="scss">
/* 记录列表 */
.record-list {
  padding: 28rpx 24rpx;
}

/* 单条记录 */
.record-item {
  background-color: #ffffff;
  border-radius: 12rpx;
  padding: 32rpx 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
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

/* 申请退款按钮 */
.refund-btn {
  padding: 12rpx 32rpx;
  border-radius: 30rpx;
  border: 1rpx solid #CCCCCC;
}
</style>