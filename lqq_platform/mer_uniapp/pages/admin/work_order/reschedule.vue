<template>
  <view>
    <view v-if="workOrderNoInfo" class="borderPad">
      <view class="bg--w111-fff borRadius14 mt20 goods-card">
        <OrderGoods v-if="workOrderNoInfo.orderDetail" :cartInfo="[workOrderNoInfo.orderDetail]" :orderInfo="workOrderNoInfo" :isShowBtn="false"></OrderGoods>
      </view>

      <booking-form v-model="formModel" :workOrderNoInfo="workOrderNoInfo" />

      <view class="safe-placeholder"></view>

      <bottom-button @handleConfirm="handleConfirm"></bottom-button>
    </view>
  </view>
</template>

<script>
import OrderGoods from '@/components/orderGoods'
import {employeeWorkOrderDetailApi, employeeWorkRescheduleApi} from "./workOrder";
import BookingForm from './components/bookingForm.vue'
import BottomButton from "../../../components/bottomButton";

export default {
  components: {
    OrderGoods,
    BookingForm,
    BottomButton
  },
  data() {
    return {
      index: 0,
      workOrderNo: null,
      merId: null,
      formData: null,
      workOrderNoInfo: null,
      serviceEvidenceForm: [],
      // 表单模型
      formModel: {
        reservationDate: '',
        userName: '',
        userPhone: '',
        userAddress: '',
        reservationTimeSlot: ''
      },
      // 地址选择（表单组件内维护 addressInfo，此处保留兼容字段）
      display: false,
      addressInfo: []
    }
  },
  onLoad(options) {
    this.workOrderNo = options.workOrderNo;
    this.getOrderInfo();
  },
  computed: {},
  methods: {
    async getOrderInfo() {
      uni.showLoading({ mask: true });
      try {
        const {
          data
        } = await employeeWorkOrderDetailApi(this.workOrderNo);
        this.getData(data)
        uni.hideLoading();
      } catch (err) {
        uni.hideLoading();
        this.$util.Tips({
          title: err,
          icon: "none"
        });
      }
    },
    // 赋值
    getData(data){
      this.workOrderNoInfo = data;
      this.formModel.userName = data.userName || ''
      this.formModel.userPhone = data.userPhone || ''
      this.formModel.userAddress = data.userAddress || ''
      // 预约时间（如果后端返回了预约时间，则回显）
      this.formModel.reservationDate = data.reservationDate;
      this.formModel.reservationTimeSlot = data.reservationTimeSlot;
    },
		// 表单校验
		validateForm() {
			const data = this.formModel || {};
			if (!data.reservationDate && !data.reservationTimeSlot) {
				return { ok: false, msg: "请选择预约时间" };
			}
			if (!data.userName) {
				return { ok: false, msg: "请输入联系人" };
			}
			const phone = (data.userPhone || '').trim();
			const cnMobile = /^1[3-9]\d{9}$/; // 大陆11位手机号
			const generalPhone = /^[0-9\-+()\s]{6,20}$/; // 通用座机/国际格式
			if (!phone || !(cnMobile.test(phone) || generalPhone.test(phone))) {
				return { ok: false, msg: "请输入正确的联系电话" };
			}
			if ((!data.userAddress || data.userAddress.trim().length < 3) && this.workOrderNoInfo.serviceType === 1) {
				return { ok: false, msg: "请输入上门地址" };
			}
			// 不早于当前时间
      // const now = new Date();
			// const dt = new Date((data.reservationDate + ' ' + data.reservationTimeSlot));
      // if (String(dt) !== 'Invalid Date' && dt.getTime() < now) {
			// 	return { ok: false, msg: "预约时间不能早于当前时间" };
			// }
			return { ok: true };
		},

		// 提交
		async handleConfirm() {
			const { ok, msg } = this.validateForm();
			if (!ok) {
				this.$util.Tips({ title: msg, icon: "none" });
				return;
			}
			uni.showLoading({ mask: true });

			try {
        let data = {
          ...this.formModel,
          workOrderNoList: [this.workOrderNo]
        }
				const res = await employeeWorkRescheduleApi(data);
				uni.hideLoading();
				this.$util.Tips({
					title: '改约成功',
					icon: "success"
				});
        setTimeout(() => {
          this.$util.navigateTo(`/pages/admin/work_order/detail?workOrderNo=${this.workOrderNo}`)
        }, 1000);
			} catch (err) {
				uni.hideLoading();
				this.$util.Tips({
					title: err,
					icon: "none"
				});
			}
		}
  }
}
</script>

<style scoped lang="scss">
.nav-bar-wrapper {
  position: sticky;
  top: 0;
  z-index: 1;
}
.goods-card{
  padding: 0 24rpx 32rpx 24rpx;
}
$base-btn-height: 120rpx;

.safe-placeholder {
  height: calc(#{$base-btn-height} + 20rpx + var(--safe-area-inset-bottom));
}
</style>
