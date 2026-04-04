<template>
<view class="bg--w111-fff borRadius14 f-s-28 text-333">
  <view class="p-24" v-if="secondType === ProductTypeEnum.Reservation">
    <view v-if="item.orderDetailList" class='item acea-row row-between mb-24'>
      <view>预约时间：</view>
      <view class='conter'>{{ item.orderDetailList[0].reservationDate || '-' }} , {{ item.orderDetailList[0].reservationTimeSlot || '-' }}</view>
    </view>
    <view class='item acea-row row-between'>
      <view>预约方式：</view>
      <view class='conter'>{{item.shippingType == 4 ? '到店': '上门'}}</view>
    </view>
  </view>
  <!-- <view class="p-24" v-else>
    <view class='item acea-row row-between'>
      <view>配送方式：</view>
      <view v-if="orderInfo.secondType > 4 && orderInfo.secondType != ProductTypeEnum.PunchCard" class='conter'>自动发货</view>
      <view v-else-if="orderInfo.secondType === 2" class='conter'>虚拟发货</view>
      <view v-else class='conter'>{{item.shippingType ==1 ? '商家配送': '到店自提'}}</view>
    </view>
  </view> -->
</view>
</template>

<script>
import {
  ProductTypeEnum
} from "../../enums/productEnums";
export default {
  name: "index",
  props: {
    // secondType: 0, //订单二级类型:0-普通订单，1-积分订单，2-虚拟订单，4-视频号订单，5-云盘订单，6-卡密订单
    secondType: {
      type: Number,
      default: 0
    },
    orderInfo: {
      type: Object,
      default: {}
    },
    item: {
      type: Object,
      default: {}
    }
  },
  data(){
    return {
      ProductTypeEnum
    }
  }
}
</script>

<style scoped>

</style>