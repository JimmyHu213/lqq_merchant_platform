<template>
  <view class="form-btn-group" :class="type === 'short' ?'flex justify-end':''">
    <button class="form-btn-group__btn" :class="btnClass" :style="[btnStyle]" @click="onConfirm">{{title}}</button>
  </view>
</template>

<script>
export default {
  name: "index",
  props:{
    title: {
      type: String,
      default: '确定',
    },
    widthNum: {
      type: String,
      default: 'auto',
    },
    backgroundColor: {
      type: String,
      default: '#2291F8'
    },
    // btn样式，长还是短的
    type: {
      type: String,
      default: 'long',
    }
  },
  computed: {
    btnClass() {
      return this.type === 'short' ? 'confirm-short' : 'confirm-long'
    },
    btnStyle() {
      return {
        width: this.widthNum,
        background: this.backgroundColor
      }
    }
  },
  methods: {
     onConfirm() {
      this.$emit('handleConfirm')
    }
  }
}
</script>

<style scoped lang="scss">
$btn-height: calc(110rpx + var(--safe-area-inset-bottom));
.form-btn-group {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: $btn-height;
  background-color: #fff;
  padding: 20rpx 30rpx;
  padding-bottom: var(--safe-area-inset-bottom);

&__btn {
   height: 88rpx;
   display: flex;
   justify-content: center;
   align-items: center;
   border-radius: 43rpx;
   font-weight: 500;

&.confirm-long {
  // background: #2291F8;
   color: #fff;
  font-size: 28rpx;
 }
  &.confirm-short {
    border: 1px solid #CCCCCC;
    color: #333333;
    font-size: 24rpx;
  }
}
}
</style>