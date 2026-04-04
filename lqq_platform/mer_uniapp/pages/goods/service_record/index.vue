<template>
  <view class="relative" :data-theme="theme">
    <view v-if="serviceEvidenceForm.length" class="form-wrapper">
      <system-from v-model="serviceEvidenceForm" ref="systemFromRef"></system-from>
    </view>
    <view class="list-bottom-tab-placeholder"></view>
    <bottom-button @handleConfirm="handleConfirm"></bottom-button>
  </view>
</template>

<script>
import SystemFrom from "../components/systemFrom/index.vue";
import {serviceEvidenceApi, serviceFormInfoApi} from "../../admin/workOrder_manage/workOrder";
import BottomButton from "../../../components/bottomButton";
let app = getApp();
export default {
  components: {
    SystemFrom,
    BottomButton
  },
  data() {
    return {
      theme: app.globalData.theme,
      merId: null,
      formId: null,
      workOrderNo: null,
      orderInfo: null,
      checkinConfig: null,
      isScroll: false,
      serviceEvidenceForm: []
    };
  },
  computed: {
  },
  onPageScroll(e) {
    this.isScroll = e.scrollTop > 5;
  },
  onLoad(options) {
    this.workOrderNo = options.workOrderNo;
    this.handleGetWorkOrderDetail();
  },
  methods: {
    // 工单详情
    async handleGetWorkOrderDetail() {
      const {
        data
      } = await serviceFormInfoApi();
      this.serviceEvidenceForm = data.formValue ? this.$util.objToArr(JSON.parse(
          data.formValue)) : [];
      this.$nextTick(() => {
        if(this.serviceEvidenceForm.length){
          this.$refs.systemFromRef.getFormList(1)
        }
      })
    },
    async handleConfirm() {
      let systemFormData = await this.$refs.systemFromRef.getSubmitFromData(1)
      if(!systemFormData.length) return;
      uni.showLoading({
        mask: true
      });
      try {
        await serviceEvidenceApi( {
          workOrderNo: this.workOrderNo,
          serviceEvidenceForm: JSON.stringify(systemFormData)
        });
        uni.hideLoading();
        this.$util.Tips({
          title: '提交成功',
          icon: "success"
        });
        setTimeout(() => {
          this.$util.navigateTo(`/pages/admin/workOrder_manage/workOrder_detail?workOrderNo=${this.workOrderNo}`)
        }, 1000);
      } catch (err) {
        uni.hideLoading();
        this.$util.Tips({
          title: err,
          icon: "none"
        });
      }
    },
  }
}
</script>

<style scoped lang="scss">
/deep/.uni-checkbox-input.uni-checkbox-input-checked, /deep/ .uni-radio-input-checked {
  border: 1px solid #2A7EFB !important;
  background-color: #2A7EFB !important;
  color: #FFF !important;
}

/deep/.wx-checkbox-input.wx-checkbox-input-checked , /deep/ .wx-radio-input-checked{
  border: 1px solid #2A7EFB !important;
  background-color: #2A7EFB !important;
  color: #FFF !important;
}
$btn-height: calc(10rpx + var(--safe-area-inset-bottom));

.form-wrapper {
  margin: 20rpx 20rpx 0;
  background-color: #fff;
  border-radius: 24rpx;
  margin-bottom: calc(#{$btn-height} + 20rpx);
}

.nav-bar-wrapper {
  position: sticky;
  top: 0;
  z-index: 1;
  background-color: #f5f5f5;
  &.is-scroll {
    background-color: #fff;
  }
}

</style>
