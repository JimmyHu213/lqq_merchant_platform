<template>
  <div>
    <el-form-item label="服务模式：" prop="deliveryMethodList">
      <el-checkbox-group v-model="formValidate.deliveryMethodList" :disabled="isDisabled">
        <el-checkbox label="3">到店服务</el-checkbox>
        <el-checkbox label="4">上门服务</el-checkbox>
      </el-checkbox-group>
      <div class="from-tips">用户购买此预约服务商品，可以选择的服务方式</div>
    </el-form-item>
    <el-form-item label="剩余可约数量：" required>
      <el-switch
        v-model="formValidate.supplementReservation.remainingNumShouSwitch"
        size="small"
        :active-value="true"
        :inactive-value="false"
        active-text="开启"
        inactive-text="关闭"
        :disabled="isDisabled"
      />
      <div class="from-tips">关闭后，用户无法查看各时段的剩余预约数量</div>
    </el-form-item>
    <el-form-item label="可约日期：" required>
      <el-radio-group v-model="formValidate.supplementReservation.reservationDateType" :disabled="isDisabled">
        <el-radio :label="1">每天</el-radio>
        <el-radio :label="2">自定义</el-radio>
      </el-radio-group>
      <div v-if="formValidate.supplementReservation.reservationDateType === 2" class="flex mt10">
        <el-date-picker
          v-model="timeVal"
          type="daterange"
          size="small"
          style="width: 360px"
          class="mr20"
          :clearable="false"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          :disabled="isDisabled"
        />
        <div
          v-for="item in week"
          :key="item.id"
          :class="{
            'check-btn': true,
            selected: formValidate.supplementReservation.reservationDateWeekRangeList.includes(item.id),
          }"
          @click="toggleWeekday(item.id)"
        >
          {{ item.name }}
        </div>
      </div>
      <div class="from-tips">设置预约服务的可预约日期。<span v-show="formValidate.supplementReservation.reservationDateType === 2">例如：周六、周天不选中，则在可预约日期中的周一到周五可用.</span></div>
    </el-form-item>

    <el-form-item label="预约日期范围：" required>
      <div class="font12 color-606266">
        对用户展示
        <el-input-number
          v-model="formValidate.supplementReservation.reservationDateDayRange"
          size="small"
          controls-position="right"
          class="ml10 mr10"
          :precision="0"
          :min="1"
          :max="60"
          :disabled="isDisabled"
        ></el-input-number>
        天内的可预约日期
      </div>
      <div class="from-tips">用户端可以看到的可预约日期。示例：设置1天，则代表只可以预约当天</div>
    </el-form-item>

    <!-- 预约日期限量 -->
    <el-form-item label="提前预约：" required>
      <el-radio-group v-model="formValidate.supplementReservation.advanceReservationSwitch" class="quota-settings">
        <div class="radio-item">
          <el-radio :label="false" :disabled="isDisabled">无需提前预约</el-radio>
        </div>
        <div class="radio-item">
          <el-radio :label="true" :disabled="isDisabled">
            用户要求提前
            <el-input-number
              v-model="formValidate.supplementReservation.advanceReservationHour"
              :min="1"
              :max="999"
              :precision="0"
              controls-position="right"
              class="ml10 mr10"
              size="small"
              :disabled="isDisabled"
            />
            小时进行预约
          </el-radio>
        </div>
        <div class="from-tips mt10">
          用户只能预约间隔时间后的时段。示例：当前10:00,设置2h，则用户只可预约12:00往后的时段
        </div>
      </el-radio-group>
    </el-form-item>

    <el-form-item label="取消订单：" required>
      <el-radio-group v-model="formValidate.supplementReservation.cancelOrderSwitch" class="quota-settings">
        <div class="radio-item">
          <el-radio :label="false" :disabled="isDisabled">不允许取消</el-radio>
        </div>
        <div class="radio-item">
          <el-radio :label="true" :disabled="isDisabled">
            服务开始
            <el-input-number
              v-model="formValidate.supplementReservation.cancelOrderHour"
              :min="1"
              :max="999"
              :precision="0"
              controls-position="right"
              class="ml10 mr10"
              size="small"
              :disabled="isDisabled"
            />
            小时之前，允许取消并自动退款
          </el-radio>
        </div>
        <div class="from-tips mt10">
          设置用户最晚可以取消预约的时间。示例：设置2h，用户预约12:00-14:00，则当天10:00之前允许用户取消预约
        </div>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="表单信息：" required>
      <el-radio-group v-model="formValidate.supplementReservation.formDataSubmitType"  :disabled="isDisabled">
        <el-radio :label="1">每个预约提交一次</el-radio>
        <el-radio :label="2">每单提交一次 </el-radio>
      </el-radio-group>
      <div class="from-tips">
        一次购买2件预约商品，选择每个预约提交一次则需要填写两遍关联系统表单，选择每单提交一次则只需要填写一遍关联系统表单
      </div>
    </el-form-item>
    <el-form-item label="关联表单：" v-if="formValidate.type === 7">
      <el-select
        :disabled="isDisabled"
        class="from-ipt-width"
        v-model="formValidate.systemFormId"
        placeholder="请选择关联表单"
        clearable
        filterable
      >
        <el-option
          :value="item.id"
          v-for="(item, index) in systemFormList"
          :key="index.id"
          :label="item.formName"
        ></el-option>
      </el-select>
      <el-button
        size="small"
        class="ml15"
        v-if="checkPermi(['merchant:system:form:add']) && !isDisabled"
        @click="handlerCreatFrom(0, 'add')"
        >添加表单</el-button
      >
      <el-button
        size="small"
        class="ml15"
        v-if="checkPermi(['merchant:system:form:add']) && !isDisabled"
        @click="changeSystemFormList"
        >刷新</el-button
      >
      <div class="from-tips mb5">
        用户购买此商品时，必须填写关联表单中设置的字段内容才能够进行订单支付，例如：部分商品购买必须填写身份证号、预约时间等
      </div>
      <div class="item" v-if="formValidate.systemFormId">
        <div class="acea-row row-middle">
          <div>
            <iframe
              :src="`${$selfUtil.getFrontDomainUrl()}/pages/goods/systemIframe/index?id=${formValidate.systemFormId}`"
              style="width: 350px; height: 500px"
              frameborder="0"
            />
          </div>
        </div>
      </div>
    </el-form-item>
  </div>
</template>

<script>
import { useProduct } from '@/hooks/use-product';
const { handlerCreatFromUse } = useProduct();
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  props: {
    value: {
      type: Object,
      default: function () {
        return {};
      },
    },
    isDisabled: {
      type: Boolean,
      default: false,
    },
    // 系統表单列表
    systemFormList: {
      type: Array,
      default: function () {
        return [];
      },
    },
  },
  data() {
    return {
      formValidate: this.value,
      // 表单数据
      timeVal: [],
      week: [
        { id: 1, name: '周一' },
        { id: 2, name: '周二' },
        { id: 3, name: '周三' },
        { id: 4, name: '周四' },
        { id: 5, name: '周五' },
        { id: 6, name: '周六' },
        { id: 7, name: '周日' },
      ],
      // 表单验证规则
      rules: {
        bookingPeriod: [{ required: true, message: '请选择可预约时段', trigger: 'change' }],
      },
    };
  },
  watch: {
    formValidate: {
      handler(newVal) {
        this.$emit('input', newVal);
      },
      deep: true,
    },
    timeVal(val) {
      this.formValidate.supplementReservation.reservationDateStartDate = val[0];
      this.formValidate.supplementReservation.reservationDateEndDate = val[1];
    },
  },
  mounted() {
    if (this.formValidate.supplementReservation.reservationDateType == 2) {
      this.timeVal = [this.formValidate.supplementReservation.reservationDateStartDate, this.formValidate.supplementReservation.reservationDateEndDate];
    }
  },
  methods: {
    checkPermi,
    //创建、编辑表单
    handlerCreatFrom(id, type) {
      handlerCreatFromUse(id, type);
    },
    // 刷新表单
    changeSystemFormList() {
      this.$emit('handleSystemFormList');
    },
    toggleWeekday(id) {
      if(this.isDisabled) return;
      const index = this.formValidate.supplementReservation.reservationDateWeekRangeList.indexOf(id);
      if (index > -1) {
        this.formValidate.supplementReservation.reservationDateWeekRangeList.splice(index, 1);
      } else {
        this.formValidate.supplementReservation.reservationDateWeekRangeList.push(id);
      }
    },
    getFormList() {
      this.$emit('getFormList');
    },
    getFormInfo() {
      this.$emit('getFormInfo');
    },
  },
};
</script>

<style lang="scss" scoped>
.quota-settings {
  display: flex;
  flex-direction: column;

  .radio-item {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
    margin-top: 8px;

    .radio-content {
      display: flex;
      align-items: center;
    }
  }
}

.check-btn {
  display: inline-block;
  height: 32px;
  font-size: 12px;
  line-height: 32px;
  padding: 0px 10px;
  margin-right: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  user-select: none;
}

.check-btn.selected {
  background-color: var(--prev-color-primary);
  color: white;
  border-color: var(--prev-color-primary);
}

.iframe-box {
  min-height: 300px;
}
</style>
