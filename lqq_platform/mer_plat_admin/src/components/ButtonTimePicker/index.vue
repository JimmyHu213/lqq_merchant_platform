<template>
  <div>
    <el-form size="small" :inline="true">
      <el-form-item label="时间选择：">
        <el-date-picker
          v-model="timeVal"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          format="yyyy-MM-dd"
          :picker-options="pickerOptions"
          @change="timeChange"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-radio-group v-model="timeType" class="time-radio-group" @input="changeTimeRange">
          <el-radio-button
            v-for="(item, index) in timeFromList"
            :class="timeType == item.val ? 'time-radio-active' : ''"
            :label="item.val"
            :key="index"
          >
            {{ item.text }}
          </el-radio-button>
        </el-radio-group>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  name: 'ButtonTimePicker',
  components: {},
  mixins: [],
  props: {},
  data() {
    return {
      timeVal: [], // 时间段
      timeType: 'lately30', // 时间单选框时间类型
      timeFromList: [
        { text: '今天', val: 'today' },
        { text: '昨天', val: 'yesterday' },
        { text: '最近7天', val: 'lately7' },
        { text: '最近30天', val: 'lately30' },
        { text: '本月', val: 'month' },
        { text: '本年', val: 'year' },
      ],
      // 时间选择器选项
      pickerOptions: {
        disabledDate(time) {
          const today = new Date();
          today.setHours(0, 0, 0, 0);
          const startDate = new Date();
          startDate.setTime(startDate.getTime() - 364 * 24 * 60 * 60 * 1000);
          startDate.setHours(0, 0, 0, 0);
          return time.getTime() < startDate.getTime() || time.getTime() > today.getTime();
        },
      },
    };
  },
  computed: {},
  watch: {
    timeVal(nVal) {
      if (nVal) {
        this.$emit('dateRangeChange', nVal)
      }
    },
  },
  created() {
    // 初始化时间选择器范围为最近七天
    this.changeTimeRange();
  },
  methods: {
    //格式化日期为 YYYY-MM-DD
    formatDate(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    // 修改时间范围
    changeTimeRange() {
      const endDate = new Date();
      const startDate = new Date();
      const now = new Date();
      const year = now.getFullYear();
      const month = now.getMonth();
      switch (this.timeType) {
        case 'today':
          this.timeVal = [this.formatDate(startDate), this.formatDate(endDate)];
          break;
        case 'yesterday':
          startDate.setDate(endDate.getDate() - 1);
          endDate.setDate(endDate.getDate() - 1);
          this.timeVal = [this.formatDate(startDate), this.formatDate(endDate)];
          break;
        case 'lately7':
          startDate.setDate(endDate.getDate() - 6);
          this.timeVal = [this.formatDate(startDate), this.formatDate(endDate)];
          break;
        case 'lately30':
          startDate.setDate(endDate.getDate() - 29);
          this.timeVal = [this.formatDate(startDate), this.formatDate(endDate)];
          break;
        case 'month':
          // 本月第一天
          const firstDayInMonth = new Date(year, month, 1);
          // 当天
          const lastDayInMonth = new Date(year, month, now.getDate());
          this.timeVal = [this.formatDate(firstDayInMonth), this.formatDate(lastDayInMonth)];
          break;
        case 'year':
          // 本年第一天：1月1日
          const firstDayInYear = new Date(year, 0, 1);
          // 当天
          const lastDayInYear = new Date(year, now.getMonth(), now.getDate());
          this.timeVal = [this.formatDate(firstDayInYear), this.formatDate(lastDayInYear)];
          break;
      }
    },
    // 修改时间选择器
    timeChange(value) {
      this.timeType = '';
    },
  },
};
</script>
<style lang="scss" scoped>
.time-radio-group {
  ::v-deep .el-radio-button__inner:hover {
    background-color: #fff;
    color: var(--prev-color-primary) !important;
  }
}
.time-radio-active {
  ::v-deep .el-radio-button__inner {
    background-color: #fff !important;
    color: var(--prev-color-primary) !important;
  }
}
</style>
