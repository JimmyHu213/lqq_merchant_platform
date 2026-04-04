<template>
  <view class="flex flex-wrap">
    <view class="pic-box pic-box-color acea-row row-center-wrapper row-column"
          :class="activePic === index ? 'pic-box-color-active' : ''" v-for="(item, index) in packageList"
          :key="index" @click="picCharge(index, item)">
      <view class="pic-number-pic semiBold" :class="activePic === index ? 'font-color' : 'text-333'">
        ￥{{ item.price }}
      </view>
      <view class="pic-number" :class="activePic === index ? 'font-color' : 'text-999'">赠送{{ item.giveMoney }} 元</view>
    </view>
    <slot></slot>
  </view>
</template>

<script>
export default {
  name: "index",
  props: {
    packageList: {
      type: Array,
      default: () => []
    }
  },
  data(){
    return {
      activePic: 0
    }
  },
  methods: {
    // 选择插槽的内容成功回调
    changeSlotSuccess(){
      this.activePic = null
    },
    picCharge(idx, item){
      this.activePic = idx;
      this.$emit('changePriceSuccess',{
        idx: idx,
        id: item.id,
        price: item.price
      })
    }
  }
}
</script>

<style scoped lang="scss">
.pic-box-color-active {
  @include border_color(theme);
  @include main_color(theme);
}
.pic-box {
  width: 216rpx;
  height: 126rpx;
  border-radius: 16rpx;
  margin-top: 21rpx;
  padding: 18rpx 0;
  margin-right: 26rpx;

  &:nth-child(3n) {
    margin-right: 0;
  }
}

.pic-box-color {
  background-color: #f4f4f4;
  color: #656565;
}

.pic-number {
  font-size: 24rpx;
}

.pic-number-pic {
  font-size: 36rpx;
  text-align: center;
}
</style>