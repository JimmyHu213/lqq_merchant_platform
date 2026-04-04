<template>
  <view class="card mt20">
    <view class="form-list booking-form bg--w111-fff borRadius14">
      <!-- 预约方式 -->
      <view class="form-item">
        <text class="label">预约方式</text>
        <view>{{ workOrderNoInfo.serviceType | serviceTypeFilter}}</view>
      </view>

      <!-- 预约时间 -->
      <view class="form-item">
        <text class="label">预约日期</text>
        <uni-datetime-picker v-model="localForm.reservationDate" type="date" :clear-icon="false">
          <view class="picker flex-y-center justify-end">
            <text class="value" :class="{ placeholder: !localForm.reservationDate }">{{ localForm.reservationDate || '请选择' }}</text>
            <text class="iconfont icon-ic_rightarrow"></text>
          </view>
        </uni-datetime-picker>
      </view>

      <!-- 时间选择 -->
      <view class="form-item flex-y-center justify-end"
            @click="changeTimeranges">
        <text class="label">预约时间</text>
        <view class="flex flex-y-center">
          <view v-if="localForm.reservationTimeSlot" class="text-28rpx">{{localForm.reservationTimeSlot}}</view>
          <view v-else class="text--w111-ccc text-28rpx">请选择</view>
          <text class='iconfont icon-ic_rightarrow'></text>
        </view>
      </view>

      <!-- 联系人 -->
      <view class="form-item">
        <text class="label">联系人</text>
        <input class="input" v-model.trim="localForm.userName" :disabled="readonly" placeholder="请输入" placeholder-style="color:#cdcdcd;" maxlength="20" />
      </view>

      <!-- 联系电话 -->
      <view class="form-item">
        <text class="label">联系电话</text>
        <input class="input" v-model.trim="localForm.userPhone" :disabled="readonly" type="number" placeholder="请输入" placeholder-style="color:#cdcdcd;" maxlength="11" />
      </view>

      <!-- 详细地址 -->
      <view v-if="workOrderNoInfo.serviceType === 1" class="form-item">
        <text class="label">上门地址</text>
        <input class="input" v-model.trim="localForm.userAddress" :disabled="readonly" placeholder="请输入详细地址" placeholder-style="color:#cdcdcd;" maxlength="100" />
      </view>
    </view>

    <!-- 时间选择 -->
    <timerangesFrom :isShow='isShow' :time='timeranges' @confrim="confrim" @cancel="cancels"></timerangesFrom>
  </view>
</template>

<script>
import timerangesFrom from '@/components/timeranges';
export default {
  name: 'BookingForm',
  components: {
    timerangesFrom
  },
  props: {
    value: {
      type: Object,
      default: () => ({})
    },
    workOrderNoInfo: {
      type: Object,
      default: () => ({})
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  // emits: ['update:modelValue','input'],
  data() {
    return {
      timeranges: [],
      localForm: JSON.parse(JSON.stringify(this.value)),
      display: false,
      isShow: false,
    }
  },
  computed: {
    addressText() {
      return (this.localForm.addressInfo || []).map(v => v.regionName).join('/')
    }
  },
  watch: {
    localForm: {
      deep: true,
      handler(n) {
        const clone = JSON.parse(JSON.stringify(n))
        //this.$emit('update:modelValue', clone)
        this.$emit('input', clone)
      }
    }
  },
  methods: {
    //时间选择
    changeTimeranges() {
      this.isShow = true;
    },
    //时间选择回调
    confrim(e) {
      this.localForm.reservationTimeSlot = e.time
      this.isShow = false;
      this.getTimeranges(e.val)
    },
    getTimeranges(val){
      let arrayNew = [];
      val.forEach(item => {
        arrayNew.push(Number(item))
      })
      this.timeranges = arrayNew;
    },
    //关闭时间选择弹窗
    cancels() {
      this.isShow = false;
    },
    // modelOrValue() {
    //   // 优先使用 modelValue（Vue3），fallback 到 value（Vue2）
    //   return (this.modelValue && Object.keys(this.modelValue || {}).length)
    //       ? this.modelValue
    //       : this.value
    // },
  }
}
</script>

<style scoped lang="scss">
.booking-form {
  padding: 16rpx 24rpx;

  .form-list {
    background: #fff;
    border-radius: 24rpx;
  }

  .form-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    min-height: 92rpx;
    padding: 20rpx 0;
    font-size: 28rpx;

    &:last-child { border-bottom: none; }
    &.disabled { opacity: 0.7; }

    .label { font-size: 28rpx; color: #333; }

    .picker, .input {
      flex: 1;
      text-align: right;
      margin-left: 24rpx;
      font-size: 28rpx;
      color: #333;
    }

    .value.placeholder { color: #cdcdcd; }
  }
}
</style>

