<template>
  <div class="h-full relative">
    <!-- 顶部日期选择组件 -->
<!--    <div class="top-date-picker">-->
<!--      <el-date-picker-->
<!--          v-model="selectedDate"-->
<!--          type="date"-->
<!--          value-format="yyyy-MM-dd"-->
<!--          @change="handleDateChange"-->
<!--          placeholder="选择日期"-->
<!--          style="margin-bottom: 10px;"-->
<!--      >-->
<!--      </el-date-picker>-->
<!--    </div>-->
    
    <FullCalendar ref="fullCalendarRef" :options="calendarOptions">
      <template v-slot:eventContent="arg">
        <el-tooltip>
          <div slot="content">
            <div class="reservation-name flex">
              <div
                class="reservation-type border-color"
                v-if="arg.event.extendedProps.serviceType == 2"
              >
                到店
              </div>
              <div
                class="reservation-type border-color"
                v-else-if="arg.event.extendedProps.serviceType === 1"
              >
                上门
              </div>
              <div style="display: inline-block" class="name">
                {{ arg.event.extendedProps.userName }}
              </div>
              <div style="display: inline-block" class="phone text-1">
                {{ arg.event.extendedProps.userPhone }}
                <span v-if="arg.event.extendedProps.serviceType === 1">
                  {{ arg.event.extendedProps.userAddress }}</span
                >
              </div>
            </div>
            <div class="text-1" style="margin-top: 4px;">
              {{ arg.event.extendedProps.productName }}
            </div>
          </div>
          <div>
            <div class="reservation-name">
              <div
                class="reservation-type bg-color-fff"
                :style="{color:getColor(arg.event.extendedProps.serviceStatus)}"
                v-if="arg.event.extendedProps.serviceType === 2"
              >
                到店
              </div>
              <div
                class="reservation-type bg-color-fff"
                :style="{color:getColor(arg.event.extendedProps.serviceStatus)}"
                v-else-if="arg.event.extendedProps.serviceType === 1"
              >
                上门
              </div>
              <div style="display: inline-block" class="name">
                {{ arg.event.extendedProps.userName }}
              </div>
              <div style="display: inline-block" class="phone text-1">
                {{ arg.event.extendedProps.userPhone }}
                <span v-if="arg.event.extendedProps.serviceType === 1">
                  {{ arg.event.extendedProps.userAddress }}</span
                >
              </div>
            </div>
            <div class="text-1">
              {{ arg.event.extendedProps.productName }}
            </div>
          </div>
        </el-tooltip>
      </template>
    </FullCalendar>
    <div class="float-box">
      <div class="list">
        <div class="item">
          <span
            class="iconfont  mark"
            :class="
              checkBox.includes(2)
                ? 'icon-xuanzhong11'
                : 'icon-weixuan'
            "
            @click="handleMark(2)"
          />
          待服务 {{`(${viewNum.toService})`}}
        </div>
        <div class="item">
          <span
            class="iconfont  mark mark2"
            :class="
              checkBox.includes(3)
                ? 'icon-xuanzhong11'
                : 'icon-weixuan'
            "
            @click="handleMark(3)"
          ></span>
          服务中 {{`(${viewNum.servicing})`}}
        </div>
        <div class="item">
          <span
            class="iconfont mark mark3"
            :class="
              checkBox.includes(4)
                ? 'icon-xuanzhong11'
                : 'icon-weixuan'
            "
            @click="handleMark(4)"
          ></span>
          已完成 {{`(${viewNum.serviced})`}}
        </div>
      </div>
    </div>
    <div class="slider">
      <el-slider
          v-model="sliderValue"
          :min="30"
          :max="180"
          :step="30"
          :marks="marks"
          show-stops
          @change="setConfig"
          style="width: 226;white-space: nowrap"
      >
      </el-slider>
    </div>
    <!-- 日期时间选择器组件 -->
    <el-dialog title="日期时间选择" :visible.sync="dialogVisible" width="504px">
      <div class="mb30">
        <el-date-picker
            v-model="selectedDate"
            type="date"
            value-format="yyyy-MM-dd"
            @change="handleDateChange"
            style="margin: 10px;"
        >
        </el-date-picker>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import resourceTimelinePlugin from "@fullcalendar/resource-timeline";
import {workOrderOverViewApi, workOrderOverViewNumApi} from "@/api/reservation";
import moment from "moment";
export default {
  components: {
    FullCalendar: () => import("@fullcalendar/vue")
  },
  props: {
    value: {
      type: Object,
      default: {},
    }
  },
  watch: {
    where: {
      handler(newVal) {
        this.$emit('input', newVal);
      },
      deep: true,
    },
  },
  data() {
    return {
      marks: {
        30: "30分钟",
        60: "1小时",
        90: "",
        120: "2小时",
        150: "",
        180: "3小时"
      },
      sliderValue: 30,
      checkBox: [2, 3, 4],
      dialogVisible: false,
      calendarOptions: {
        height: "calc(100vh - 276px)", // 调整高度以适应顶部日期选择器
        slotMinWidth: 90,
        eventMaxStack: 50,
        moreLinkContent: {
          html:
            '<a>全部预约<span class="fc-icon fc-icon-chevron-right"></span></a>'
        },
        // moreLinkClick: this.handleMoreLinkClick,
        locale: "zh-cn",
        plugins: [resourceTimelinePlugin],
        initialView: "resourceTimelineDay",
        aspectRatio: 3,
        headerToolbar: {
          left: "prev,customTitle,next,volume,number",
          right: ""
        },
        customTitle: {
          text: this.formatDate(this.selectedDate),
          click: () => {
            this.dialogVisible = !this.dialogVisible;
          }
        },
        customButtons: {
          volume: {
            text: "当日预约量"
          },
          number: {
            text: "0"
          },
          customTitle: {
            text: this.formatDate(this.selectedDate),
            click: () => {
              this.dialogVisible = !this.dialogVisible;
            }
          }
        },
        slotLabelFormat: {
          hour: "2-digit", // 两位数小时（如 10）
          minute: "2-digit", // 两位数分钟（如 00）
          hour12: false, // 24小时制
          omitZeroMinute: false, // 不省略分钟（强制显示:00）
          locale: "zh-cn" // 本地化（需加载中文语言包）
        },
        nowIndicator: true,
        editable: false,
        droppable: false,
        resourceAreaWidth: "15%",
        scrollTime: "9:00",
        resourceAreaHeaderContent: "",
        resources: [],
        events: [],
        slotDuration: "00:30:00",
        slotLabelInterval: "00:30", // 标签间隔2小时
        datesSet: this.handleDatesSet,
        dateClick: this.handleDateChange,
        eventClick: this.handleEventClick,
        schedulerLicenseKey: "GPL-My-Project-Is-Open-Source"
      },
      reservationTime: "",
      where: this.value,
      viewNum: {},
      selectedDate: this.moment().format('YYYY-MM-DD'),
      selectedTime: '',
      calendarApi: null,
    };
  },
  beforeDestroy() {
    // 清理事件监听
    document.removeEventListener("click", this.closePopover);
  },
  mounted() {
    this.getConfig();
    this.getList();
    this.getViewNum();
    // 初始化日期
    this.where.reservationDate = this.selectedDate;
  },
  methods: {
    getColor(serviceStatus){
      const statusMap = {
        1: '#9fdb1d',
        2: '#377DFF',
        3: '#FF8D30',
        4: '#23C471'
      };
      return statusMap[serviceStatus];
    },
    async getViewNum(){
      this.viewNum = await workOrderOverViewNumApi(this.where)
    },
    // 预约工单甘特图
    getList() {
      for (let key in this.tableFrom) {
        this.where[key] = this.tableFrom[key];
      }
      workOrderOverViewApi(this.where).then(res => {
         let data = res.list;
        let staff = [];
        let list = [];
        data.forEach(dataItem => {
          staff.push({
            id: dataItem.staffId,
            title: dataItem.staffId == 0 ? "未指派" : dataItem.name
          });
          // 蓝色 待服务，黄色 服务中，绿色 已完成，serviceType	服务类型:1-上门服务，2-到店服务，serviceStatus	服务状态:1-未分配，2-已分配，3-服务中，4-服务结束
          dataItem.workOrderList.forEach(item => {
            let color = "#9fdb1d";
            if (item.serviceStatus === 2) {
              // s上面 1
              // 到店 01
              color = "#377DFF";
            } else if (item.serviceStatus === 3) {
              color = "#FF8D30";
            } else if (item.serviceStatus === 4) {
              color = "#23C471";
            }
            let reservationTimeSlotAttr = item.reservationTimeSlot.split('-')
            list.push({
              ...item,
              id: item.workOrderNo,
              staff_name: dataItem.name,
              resourceId: dataItem.staffId + "",
              start: this.moment(
                item.reservationDate +
                  " " +
                  reservationTimeSlotAttr[0]
              ).format("YYYY-MM-DD HH:mm"),
              end: this.moment(
                item.reservationDate +
                  " " +
                  reservationTimeSlotAttr[1]
              ).format("YYYY-MM-DD HH:mm"),
              color
            });
          });
        });
        this.calendarOptions.resources = staff;
        this.$nextTick(function () {
          this.$set(this.calendarOptions, "events", list);
        });
        this.calendarOptions.customButtons.number.text = res.count + "";
      });
    },
    showPopover() {
      this.dialogVisible = true;
    },
    handleMark(type) {
      if (this.checkBox.includes(type)) {
        this.checkBox = this.checkBox.filter(item => item != type);
        this.where.serviceStatus = this.checkBox.join(",");
      } else {
        this.checkBox.push(type);
        this.where.serviceStatus = this.checkBox.join(",");
      }
      this.getList();
      this.getViewNum();
    },
    handleDateChange(date) {
      if (date) {
        // 如果传入的是日期对象，更新selectedDate
        if (date instanceof Date || typeof date === 'string') {
          this.selectedDate = date instanceof Date ? this.moment(date).format('YYYY-MM-DD') : date;
        }
        this.where.reservationDate = this.selectedDate;
        
        // 如果有选择时间，更新到where对象中
        if (this.selectedTime) {
          this.where.reservationTime = this.selectedTime;
          this.calendarOptions.customButtons.customTitle.text = `${this.selectedDate} ${this.selectedTime}`;
        } else {
          this.calendarOptions.customButtons.customTitle.text = this.selectedDate;
        }
        
        // 立即刷新数据，不使用setTimeout
        this.getList();
        this.getViewNum();
        
        // 获取日历实例并强制刷新视图
        if (this.$refs.fullCalendarRef) {
          this.calendarApi = this.$refs.fullCalendarRef.getApi();
          if (this.calendarApi) {
            // 设置日期视图到选中的日期
            this.calendarApi.gotoDate(this.selectedDate);
            // 强制重新渲染
            this.calendarApi.refetchEvents();
          }
        }
      }
      // 关闭日期选择器
      this.dialogVisible = false;
    },
    handleDatesSet(arg) {
      this.where.reservationDate = this.moment(arg.start).format("YYYY-MM-DD");
      this.selectedDate = this.where.reservationDate;
      // 如果有选择时间，更新标题
      if (this.selectedTime) {
        this.calendarOptions.customButtons.customTitle.text = `${this.where.reservationDate} ${this.selectedTime}`;
      } else {
        this.calendarOptions.customButtons.customTitle.text = this.where.reservationDate;
      }
      this.getList();
      this.getViewNum();
    },
    // 日期选择相关方法
    formatDate(date) {
      if (!date) {
        return moment().format('YYYY年MM月DD日');
      }
      return moment(date).format('YYYY年MM月DD日');
    },
    handleEventClick({ event }) {
      this.$emit("onOrderDetails", event.extendedProps.workOrderNo);
    },

    // 保存看板配置
    setConfig() {
      localStorage.setItem("sliderValue", this.sliderValue);
      this.dialogVisible = false;
      this.getConfig();
    },
    getConfig() {
      let value = 30;
      if (localStorage.getItem("sliderValue")) {
        value = localStorage.getItem("sliderValue");
      }
      this.sliderValue = Number(value);
      this.calendarOptions.slotDuration = this.moment
        .utc(this.sliderValue * 60 * 1000)
        .format("HH:mm:ss");
      this.calendarOptions.slotLabelInterval = this.moment
        .utc(this.sliderValue * 60 * 1000)
        .format("HH:mm:ss");
    },
    close() {
      this.modal = false;
    },
    handleRefresh() {
      this.isRefresh = true;
    },
    handleMoreLinkClick(info) {
      info.jsEvent.preventDefault();
      const currentEvent = info.hiddenSegs[0].event;
      const resources = currentEvent.getResources();
      const rawEvents = resources[0].getEvents();
      const overlapEvents = rawEvents.filter(({ start, end }) => {
        return currentEvent.start <= end && start < currentEvent.end;
      });
      overlapEvents.sort((a, b) => {
        return a.start - b.start;
      });
      let eventList = overlapEvents.map(({ extendedProps, id }) => {
        return {
          ...extendedProps,
          id
        };
      });
      this.$emit("serviceTap", eventList);
    }
  }
};
</script>

<style lang="scss" scoped>
// 添加顶部日期选择器的样式
.top-date-picker {
  padding: 10px 0 0 20px;
  background-color: #fcfcfc;
  border-bottom: 1px solid #ededed;
}

.bg-color-fff{
  background-color: rgba(255,255,255,.7);
}
.slider {
  position: absolute;
  width: 500px;
  top: 0;
  right: 0;
  padding: 20px;
  padding-top: 0;
  ::v-deep .el-slider__marks-text {
    font-size: 13px;
    color: #303133;
  }
  ::v-deep .el-slider__stop {
    position: absolute;
    height: 10px;
    width: 2px;
    background-color: #dddddd;
    -webkit-transform: translateY(-50%);
    transform: translateY(-80%);
  }

  ::v-deep .el-slider__runway {
    height: 2px;
    background: #dddddd;
  }
  ::v-deep .el-slider__button {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    border: 2px solid var(--prev-color-primary);
  }
  ::v-deep .el-slider__bar {
    background-color: transparent;
  }
}
::v-deep.fc {
  .fc-button-primary, .fc-button-primary:focus, .fc-button:focus,{
    background: none;
    color: #303133;
    border-style: none;
  }
  .fc-more-popover .fc-popover-body {
    max-height: 300px;
    overflow-y: scroll;
  }
  .fc-timeline-lane-frame{
    padding-top: 6px;
  }
  .fc-popover {
    z-index: 22;
  }
  .fc-datagrid-cell-frame {
    display: flex;
    align-items: center;
  }
  .fc-datagrid-header {
    background-color: #fcfcfc;
  }
  .fc-datagrid-body {
    background-color: #fcfcfc;
  }
  .fc-timeline-slot-minor {
    border-style: none;
  }
  .fc-event {
    padding: 5px 6px;
    border-radius: 4px;
    white-space: nowrap;
    font-size: 12px;
    overflow: hidden;
  }
  .reservation-name {
    display: flex;
    align-items: center;
    margin-bottom: 2px;
  }
  .reservation-type {
    flex-shrink: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 29px;
    height: 17px;
    border: 1px solid rgba(255, 255, 255, 0.5);
    border-radius: 3px;
    margin-right: 4px;
    font-family: PingFang SC, PingFang SC;
    font-weight: 400;
    font-size: 12px;
    color: #ffffff;
  }

  .fc-prev-button {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    width: 30px;
    height: 30px;
    border: 1px solid #dddddd;
    border-radius: 50% !important;
    background: none;
    font-size: 13px;
    color: #303133;
    vertical-align: middle;
  }
  .fc-prev-button:not(:disabled):active {
    border: 1px solid #dddddd;
    background: none;
    color: #303133;
  }
  .fc-prev-button:focus, .fc-button-primary:focus{
    box-shadow: none;
  }
  .fc-prev-button:not(:disabled):active:focus,.fc-button-primary:not(:disabled):active:focus {
    box-shadow: none;
  }
  .fc-next-button {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    width: 30px;
    height: 30px;
    border: 1px solid #dddddd;
    border-radius: 50% !important;
    background: none;
    font-size: 16px;
    color: #303133;
    vertical-align: middle;
  }
  .fc-next-button:not(:disabled):active {
    border: 1px solid #dddddd;
    background: none;
    color: #303133;
  }
  .fc-next-button:focus {
    box-shadow: none;
  }
  .fc-next-button:not(:disabled):active:focus {
    box-shadow: none;
  }
  .fc-toolbar-title {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    width: auto;
    min-width: 200px;
    font-weight: 500;
    font-size: 16px;
    color: #303133;
    vertical-align: middle;
  }
  .fc-volume-button {
    border: 0;
    background: none;
    font-size: 16px;
    color: #303133;
    cursor: auto;
  }
  .fc-volume-button:not(:disabled):active {
    border: 0;
    background: none;
    color: #303133;
  }
  .fc-volume-button:focus {
    box-shadow: none;
  }
  .fc-volume-button:not(:disabled):active:focus {
    box-shadow: none;
  }
  .fc-number-button {
    padding: 0;
    border: 0;
    background: none;
    font-weight: 500;
    font-size: 16px;
    color: #377dff;
    cursor: auto;
  }
  .fc-number-button:not(:disabled):active {
    border: 0;
    background: none;
    color: #303133;
  }
  .fc-number-button:focus {
    box-shadow: none;
  }
  .fc-number-button:not(:disabled):active:focus {
    box-shadow: none;
  }
  .fc-setup-button {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    width: 100px;
    height: 32px;
    border: 1px solid var(--prev-color-primary);
    border-radius: 20px;
    background: var(--prev-color-primary);
    font-size: 13px;
    vertical-align: middle;
  }
  .fc-setup-button:not(:disabled):active {
    border: 1px solid var(--prev-color-primary);
    background: var(--prev-color-primary);
  }
  .fc-setup-button:focus {
    box-shadow: none;
  }
  .fc-setup-button:not(:disabled):active:focus {
    box-shadow: none;
  }
  .fc-timeline-slot-cushion {
    font-weight: 400;
    font-size: 14px;
    color: #303133;
  }
  .fc-datagrid-cell-cushion {
    font-size: 14px;
    color: #303133;
    white-space: pre-wrap;
  }
  .fc-timeline-now-indicator-container {
    z-index: auto;
  }
  .fc-timeline-now-indicator-arrow {
    border-top-color: #377dff;
  }
  .fc-timeline-now-indicator-line {
    border-width: 0px 0px 0px 2px;
    border-color: #377dff;
  }
  td {
    border-color: #ededed;
    cursor: auto;
  }
  .fc-scrollgrid-section-header > th:last-child {
    border-color: transparent;
  }
  .fc-scrollgrid-section-body > td:last-child {
    border-color: transparent;
  }
  th {
    border-color: #ededed;
  }
  .fc-scrollgrid {
    border-color: transparent;
  }
  .ivu-tooltip {
    width: 100%;
  }
}
.text-1 {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  color: #fff;
}

.name {
  font-family: PingFang SC, PingFang SC;
  font-weight: 400;
  font-size: 12px;
  color: #ffffff;
  margin: 0 4px;
}
.phone {
  color: rgba(255, 255, 255, 0.5);
  font-family: PingFang SC, PingFang SC;
  font-weight: 400;
  font-size: 12px;
}

.float-box {
  position: fixed;
  right: 44px;
  bottom: 87px;
  z-index: 9;
  display: flex;
  align-items: center;
  height: 61px;
  padding: 0 13px 0 26px;
  border-radius: 84px;
  background: #ffffff;
  box-shadow: 0px 4px 16px 0px rgba(0, 0, 0, 0.08);
  .list {
    display: flex;
    align-items: center;
    font-size: 14px;
    color: #303133;
  }
  .item {
    cursor: pointer;
    display: flex;
    align-items: center;
    margin-right: 18px;
  }
  .mark {
    font-size: 14px;
    color: #377dff;
    margin-right: 8px;
    &.mark2 {
      color: #ff8d30;
    }
    &.mark3 {
      color: #23c471;
    }
    &.mark4 {
      color: #f95e45;
    }
  }
}

.ml0 {
  margin-left: 0 !important;
}
</style>
