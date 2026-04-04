<template>
  <div>
    <!-- 统计时间选择 -->
    <div class="time-box">
      <el-card :bordered="false" dis-hover :padding="12" shadow="never">
        <button-time-picker @dateRangeChange="dateRangeChange"></button-time-picker>
      </el-card>
    </div>
    <!-- 统计数据 -->
    <div v-if="userStatisticsData.length && checkPermi(['merchant:user:statistics:top'])" class="mt14">
      <statistics-data-card-list :statisticsData="userStatisticsData"></statistics-data-card-list>
    </div>
    <!-- 统计图像 -->
    <div class="statistic-image-box">
      <!-- 新增用户折线图 -->
      <el-row class="mt14" :gutter="14">
        <el-col :span="12" v-if="checkPermi(['platform:user:statistics:add'])">
          <el-card :bordered="false" dis-hover :padding="12" shadow="never" v-loading="newUserLoading">
            <echarts-new :option-data="newUserOption" :styles="style" height="100%" width="100%"></echarts-new>
          </el-card>
        </el-col>
        <!-- 活跃用户折线图 -->
        <el-col :span="12" v-if="checkPermi(['platform:user:statistics:alive'])">
          <el-card :bordered="false" dis-hover :padding="12" shadow="never" v-loading="activeUserLoading">
            <echarts-new :option-data="activeUserOption" :styles="style" height="100%" width="100%"></echarts-new>
          </el-card>
        </el-col>
      </el-row>
      <el-row class="mt14" :gutter="14">
        <!-- 成交用户柱形图 -->
        <el-col :span="12" v-if="checkPermi(['platform:user:statistics:member'])">
          <el-card :bordered="false" dis-hover :padding="12" shadow="never" v-loading="boughtUserLoading">
            <echarts-new :option-data="boughtUserOption" :styles="style" height="100%" width="100%"></echarts-new>
          </el-card>
        </el-col>
        <!-- 新增商户会员折线图 -->
        <el-col :span="12" v-if="checkPermi(['platform:user:statistics:bought'])">
          <el-card :bordered="false" dis-hover :padding="12" shadow="never" v-loading="newMemberLoading">
            <echarts-new :option-data="newMemberOption" :styles="style" height="100%" width="100%"></echarts-new>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import {
  getUserStatisticsDataApi,
  getNewUserDataApi,
  getActiveUserDataApi,
  getBoughtUserDataApi,
  getNewMemberDataApi,
} from '@/api/dashboard.js';
import echartsNew from '@/components/echartsNew/index';
import ButtonTimePicker from '@/components/ButtonTimePicker/index';
import StatisticsDataCardList from '@/components/StatisticsDataCardList/index';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'orderStatistic',
  components: {
    echartsNew,
    ButtonTimePicker,
    StatisticsDataCardList,
  },
  mixins: [],
  props: {},
  data() {
    return {
      startTime: '', // 开始时间
      endTime: '', // 结束时间
      newUserLoading: true, // 新增用户图像是否加载中
      activeUserLoading: true, // 活跃用户图像是否加载中
      boughtUserLoading: true, // 成交用户图像是否加载中
      newMemberLoading: true, // 新增商户会员图像是否加载中
      userStatisticsData: [], // 用户统计数据
      style: { height: '320px' },
      // 新增用户线形图配置
      newUserOption: {
        title: { text: '新增用户数量', left: 0, top: 10, textStyle: { fontSize: 18, fontWeight: 600 } },
        // 图像位置
        grid: { top: 80, left: 0, right: 50, bottom: 40, containLabel: true },
        color: ['#5B8FF9'],
        // 指针数据显示
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'line' },
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          axisLine: { lineStyle: { color: '#E5E8EF' } },
          axisLabel: { color: '#666' },
          data: [],
        },
        yAxis: {
          type: 'value',
          name: '数量',
          position: 'left',
          min: 0,
          axisLine: { show: false },
          splitLine: { lineStyle: { color: '#F0F0F0' } },
          axisLabel: { color: '#666', margin: 30 },
        },
        series: {
          name: '新增用户数量',
          type: 'line',
          smooth: true,
          symbol: 'none',
          symbolSize: '8',
          lineStyle: { width: 2 },
          data: [],
        },
      },
      // 活跃用户线形图配置
      activeUserOption: {
        title: { text: '活跃用户数量', left: 0, top: 10, textStyle: { fontSize: 18, fontWeight: 600 } },
        // 图像位置
        grid: { top: 80, left: 0, right: 50, bottom: 40, containLabel: true },
        color: ['#5B8FF9'],
        // 指针数据显示
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'line' },
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          axisLine: { lineStyle: { color: '#E5E8EF' } },
          axisLabel: { color: '#666' },
          data: [],
        },
        yAxis: {
          type: 'value',
          name: '数量',
          position: 'left',
          min: 0,
          axisLine: { show: false },
          splitLine: { lineStyle: { color: '#F0F0F0' } },
          axisLabel: { color: '#666', margin: 30 },
        },
        series: {
          name: '活跃用户数量',
          type: 'line',
          smooth: true,
          symbol: 'none',
          symbolSize: '8',
          lineStyle: { width: 2 },
          data: [],
        },
      },
      // 成交用户柱形图配置
      boughtUserOption: {
        title: { text: '成交用户数量', left: 0, top: 10, textStyle: { fontSize: 18, fontWeight: 600 } },
        // 图像位置
        grid: { top: 80, left: 0, right: 50, bottom: 40, containLabel: true },
        color: ['#5B8FF9', '#5AD8A6'],
        // 图例
        legend: {
          top: 10,
          left: 'center',
          icon: 'circle',
          itemWidth: 8,
          itemHeight: 8,
          data: ['老用户', '新用户'],
        },
        // 指针数据显示
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'line' },
        },
        xAxis: {
          type: 'category',
          boundaryGap: true,
          axisLine: { lineStyle: { color: '#E5E8EF' } },
          axisLabel: { color: '#666' },
          data: [],
        },
        yAxis: {
          type: 'value',
          name: '数量',
          position: 'left',
          min: 0,
          axisLine: { show: false },
          splitLine: { lineStyle: { color: '#F0F0F0' } },
          axisLabel: { color: '#666', margin: 30 },
        },
        series: [
          {
            name: '老用户',
            type: 'bar',
            stack: 'bought',
            barMaxWidth: 20,
            data: [],
          },
          {
            name: '新用户',
            type: 'bar',
            stack: 'bought',
            barMaxWidth: 20,
            data: [],
          },
        ],
      },
      // 新增商户会员线形图配置
      newMemberOption: {
        title: { text: '新增商户会员数量', left: 0, top: 10, textStyle: { fontSize: 18, fontWeight: 600 } },
        // 图像位置
        grid: { top: 80, left: 0, right: 50, bottom: 40, containLabel: true },
        color: ['#5B8FF9'],
        // 指针数据显示
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'line' },
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          axisLine: { lineStyle: { color: '#E5E8EF' } },
          axisLabel: { color: '#666' },
          data: [],
        },
        yAxis: {
          type: 'value',
          name: '数量',
          position: 'left',
          min: 0,
          axisLine: { show: false },
          splitLine: { lineStyle: { color: '#F0F0F0' } },
          axisLabel: { color: '#666', margin: 30 },
        },
        series: {
          name: '新增商户会员数量',
          type: 'line',
          smooth: true,
          symbol: 'none',
          symbolSize: '8',
          lineStyle: { width: 2 },
          data: [],
        },
      },
    };
  },
  computed: {
    todayDate() {
      const date = new Date();
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
  },
  mounted() {},
  methods: {
    checkPermi,
    // 获取用户统计数据
    getUserStatisticsData() {
      const param = {
        startTime: this.startTime,
        endTime: this.endTime,
      };
      getUserStatisticsDataApi(param)
        .then((res) => {
          this.userStatisticsData = res;
          this.$set(this.userStatisticsData[0], 'isCumulativeData', true)
          this.$set(this.userStatisticsData[1], 'isCumulativeData', true)
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 获取新增用户数据
    getNewUserData() {
      const param = {
        startTime: this.startTime,
        endTime: this.endTime,
      };
      getNewUserDataApi(param)
        .then((res) => {
          let xAxis = [],
            yAxis = [];
          res.forEach((item, index) => {
            xAxis.push(item.name);
            yAxis.push(item.value);
          });
          this.newUserOption.xAxis.data = xAxis;
          this.newUserOption.series.data = yAxis;
          this.newUserLoading = false;
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 获取活跃用户数据
    getActiveUserData() {
      const param = {
        startTime: this.startTime,
        endTime: this.endTime,
      };
      getActiveUserDataApi(param)
        .then((res) => {
          let xAxis = [],
            yAxis = [];
          res.forEach((item, index) => {
            xAxis.push(item.name);
            yAxis.push(item.value);
          });
          this.activeUserOption.xAxis.data = xAxis;
          this.activeUserOption.series.data = yAxis;
          this.activeUserLoading = false;
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 获取成交用户数据
    getBoughtUserData() {
      const param = {
        startTime: this.startTime,
        endTime: this.endTime,
      };
      getBoughtUserDataApi(param)
        .then((res) => {
          let xAxis = [],
            newUser = [],
            oldUser = [];
          res.forEach((item, index) => {
            xAxis.push(item.date);
            newUser.push(item.newUser);
            oldUser.push(item.oldUser);
          });
          this.boughtUserOption.xAxis.data = xAxis;
          this.boughtUserOption.series[0].data = oldUser;
          this.boughtUserOption.series[1].data = newUser;
          this.boughtUserLoading = false;
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 获取新增商户会员数据
    getNewMemberData() {
      const param = {
        startTime: this.startTime,
        endTime: this.endTime,
      };
      getNewMemberDataApi(param)
        .then((res) => {
          let xAxis = [],
            yAxis = [];
          res.forEach((item, index) => {
            xAxis.push(item.name);
            yAxis.push(item.value || 0);
          });
          this.newMemberOption.xAxis.data = xAxis;
          this.newMemberOption.series.data = yAxis;
          this.newMemberLoading = false;
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 日期范围更改
    dateRangeChange(timeVal) {
      this.resetLoading();
      this.startTime = timeVal[0];
      this.endTime = timeVal[1];
      if (timeVal && timeVal[0] == timeVal[1]) {
        this.newUserOption.series.symbol = 'circle';
        this.activeUserOption.series.symbol = 'circle';
        this.newMemberOption.series.symbol = 'circle';
      } else {
        this.newUserOption.series.symbol = 'none';
        this.activeUserOption.series.symbol = 'none';
        this.newMemberOption.series.symbol = 'none';
      }
      // 获取用户统计数据
      this.getUserStatisticsData();
      // 获取新增用户数据
      this.getNewUserData();
      // 获取活跃用户数据
      this.getActiveUserData();
      // 获取成交用户数据
      this.getBoughtUserData();
      // 获取新增商户会员数据
      this.getNewMemberData();
    },
    // 重置加载状态
    resetLoading() {
      this.newUserLoading = true;
      this.activeUserLoading = true;
      this.boughtUserLoading = true;
      this.newMemberLoading = true;
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep .el-form-item {
  margin-bottom: 0 !important;
}
</style>
