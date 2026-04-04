<template>
  <view :data-theme="theme">
    <uni-popup ref="popup" type="bottom" @change="change">
      <view class="refund-content bg--w111-fff borders-radius-top">
        <view class="title relative text-center f-s-32 text-333 fw-500">
          退款申请
          <CloseIcon @handle-close="close"></CloseIcon>
        </view>
        <view class="content">
          <view class="textarea-box bg--w111-f5f5f5 rd-20px p-24 mb-30">
            <textarea
              v-model="refundReason"
              placeholder="请填写退款申明"
              placeholder-class="text-999"
              class="w-full h-200 f-s-28 text-333"
              maxlength="200"
            />
          </view>
          <view class="footer flex justify-between align-center items-center pt-20">
            <view class="f-s-28 text-333"></view>
            <view class="btn-submit bg-color text--w111-fff f-s-26 flex-center" @click="submit">提交</view>
          </view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import {shoppingCreditsRefundApplyApi} from "../memberApi";
const app = getApp();
export default {
  name: "requestRefund",
  props: {
    orderNo: {
      type: String,
      default: '',
    },
    refundAmount: {
      type: String,
      default: '',
    }
  },
  data() {
    return {
      theme: app.globalData.theme,
      refundReason: '',
    }
  },
  methods: {
    open() {
      this.$refs.popup.open()
    },
    close() {
      this.$refs.popup.close()
    },
    change(e) {
      this.$emit('change', e)
    },
    async submit() {
      if (!this.refundReason.trim()) {
        return uni.showToast({
          title: '请填写退款申明',
          icon: 'none'
        })
      }
      try {
        await shoppingCreditsRefundApplyApi({
          refundReason: this.refundReason,
          orderNo: this.orderNo
        })
        uni.showToast({
          title: '申请成功',
          icon: 'none'
        })
        this.refundReason = ''
        this.close()
        this.$emit('handleSubmit')
      }catch (e) {
        uni.showToast({
          title: e,
          icon: 'none'
        })
      }
    }
  }
}
</script>

<style scoped lang="scss">
.refund-content {
  background-color: #fff;
  border-radius: 40rpx 40rpx 0 0;
  padding: 32rpx 24rpx 50rpx;
  position: relative;
  
  .title {
    padding: 0 0 44rpx;
    
    .close {
      right: 0;
      top: 10rpx;
      width: 40rpx;
      height: 40rpx;
      
      .iconfont {
        font-size: 32rpx;
        color: #999;
      }
    }
  }
  
  .content {
    .textarea-box {
      background-color: #F5F5F5;
      padding: 24rpx;
      border-radius: 14rpx;
      border: 1px solid #eee;
      
      textarea {
        width: 100%;
        height: 260rpx;
        font-size: 28rpx;
        line-height: 1.5;
      }
    }
    
    .footer {
      margin-top: 20rpx;
      padding-bottom: constant(safe-area-inset-bottom);
      padding-bottom: env(safe-area-inset-bottom);
      
      .btn-submit {
        width: 168rpx;
        height: 72rpx;
        @include linear-gradient(theme);
        border-radius: 50rpx;
        
        &:active {
          opacity: 0.9;
        }
      }
    }
  }
}
</style>