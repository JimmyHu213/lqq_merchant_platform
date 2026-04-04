<template>
  <div>
    <!-- 统计时间选择 -->
    <div class="time-box">
      <el-card :bordered="false" dis-hover :padding="12" shadow="never">
        <button-time-picker @dateRangeChange="dateRangeChange"></button-time-picker>
      </el-card>
    </div>
    <!-- 统计数据 -->
    <div v-if="orderStatisticsData.length && checkPermi(['merchant:order:statistics:top'])" class="mt14">
      <statistics-data-card-list :statisticsData="orderStatisticsData"></statistics-data-card-list>
    </div>
    <!-- 统计图像 -->
    <div class="statistic-image-box">
      <!-- 订单折线图 -->
      <el-row class="mt14" v-if="checkPermi(['merchant:order:statistics:chart'])">
        <el-col :span="24">
          <el-card :bordered="false" dis-hover :padding="12" shadow="never" v-loading="orderLineLoading">
            <echarts-new :option-data="orderLineOption" :styles="style" height="100%" width="100%"> </echarts-new>
          </el-card>
        </el-col>
      </el-row>
      <el-row class="mt14" :gutter="14">
        <!-- 订单类型图 -->
        <el-col :span="12" v-if="checkPermi(['merchant:order:statistics:type'])">
          <el-card :bordered="false" dis-hover :padding="12" shadow="never" v-loading="orderTypeLoading">
            <echarts-new :option-data="orderTypeOption" :styles="style" height="100%" width="100%"> </echarts-new>
          </el-card>
        </el-col>
        <!-- 发货方式图 -->
        <el-col :span="12" v-if="checkPermi(['merchant:order:statistics:shipping'])">
          <el-card :bordered="false" dis-hover :padding="12" shadow="never" v-loading="orderShippingLoading">
            <echarts-new :option-data="orderShippingOption" :styles="style" height="100%" width="100%"> </echarts-new>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import {
  getOrderStatisticsDataApi,
  getOrderLineDataApi,
  getOrderTypePieDataApi,
  getProductShippingPieDataApi,
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
      orderStatisticsData: [], // 订单统计数据
      orderLineLoading: true, // 订单折线图是否加载
      orderTypeLoading: true, // 订单类型图是否加载
      orderShippingLoading: true, // 发货方式图是否加载
      style: { height: '320px' },
      // 订单线形图选项
      orderLineOption: {
        // 图像位置
        grid: { top: 50, left: 0, right: 50, bottom: 40, containLabel: true },
        color: ['#5B8FF9', '#5AD8A6', '#F6BD16', '#A78BFA'],
        // 指针数据显示
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'line' },
        },
        // 图例
        legend: {
          top: 10,
          left: 'center',
          icon: 'circle',
          itemWidth: 8,
          itemHeight: 8,
          data: ['订单金额', '退款金额', '订单数量', '退款数量'],
        },
        xAxis: {
          type: 'category',
          boundaryGap: true,
          axisLine: { lineStyle: { color: '#E5E8EF' } },
          axisLabel: { color: '#666' },
          data: [],
        },
        yAxis: [
          {
            type: 'value',
            name: '金额',
            position: 'left',
            min: 0,
            axisLine: { show: false },
            splitLine: { lineStyle: { color: '#F0F0F0' } },
            axisLabel: { color: '#666', margin: 30 },
          },
          {
            type: 'value',
            name: '数量',
            position: 'right',
            min: 0,
            axisLine: { show: false },
            splitLine: { lineStyle: { color: '#F0F0F0' } },
            axisLabel: { color: '#666', margin: 30 },
          },
        ],
        series: [
          {
            name: '订单金额',
            type: 'bar',
            yAxisIndex: 0,
            smooth: true,
            symbol: 'none',
            symbolSize: '8',
            lineStyle: { width: 2 },
            barMaxWidth: 20,
            data: [],
          },
          {
            name: '退款金额',
            type: 'bar',
            yAxisIndex: 0,
            smooth: true,
            symbol: 'none',
            symbolSize: '8',
            lineStyle: { width: 2 },
            barMaxWidth: 20,
            data: [],
          },
          {
            name: '订单数量',
            type: 'line',
            yAxisIndex: 1,
            smooth: true,
            symbol: 'none',
            symbolSize: '8',
            lineStyle: { width: 2 },
            data: [],
          },
          {
            name: '退款数量',
            type: 'line',
            yAxisIndex: 1,
            smooth: true,
            symbol: 'none',
            symbolSize: '8',
            lineStyle: { width: 2 },
            data: [],
          },
        ],
      },
      // 订单类型饼图选项
      orderTypeOption: {
        title: { text: '订单类型', left: 0, top: 10, textStyle: { fontSize: 18, fontWeight: 600 } },
        color: ['#3B82F6', '#34D399', '#60A5FA', '#F59E0B', '#FB923C', '#EF4444', '#A78BFA', '#F472B6', '#C084FC'],
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)',
        },
        legend: {
          type: 'scroll',
          orient: 'vertical',
          left: 0,
          top: 50,
          icon: 'circle',
          // itemWidth: 8,
          // itemHeight: 8,
        },
        series: [
          {
            name: '订单类型',
            type: 'pie',
            // radius: '65%',
            radius: ['40%', '70%'],
            center: ['60%', '55%'],
            avoidLabelOverlap: true, // 是否开启标签重叠处理
            label: { show: true, position: 'outside', formatter: '{b}' },
            labelLine: { show: true, length: 14, length2: 10, smooth: true },
            data: [],
          },
        ],
      },
      // 发货方式饼图选项
      orderShippingOption: {
        title: { text: '发货方式', left: 0, top: 10, textStyle: { fontSize: 18, fontWeight: 600 } },
        color: ['#3B82F6', '#34D399', '#60A5FA', '#F59E0B', '#FB923C', '#EF4444', '#A78BFA', '#F472B6', '#C084FC'],
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)',
        },
        legend: {
          type: 'scroll',
          orient: 'vertical',
          left: 0,
          top: 50,
          icon: 'circle',
          // itemWidth: 8,
          // itemHeight: 8,
        },
        series: [
          {
            name: '发货方式',
            type: 'pie',
            // radius: '65%',
            radius: ['40%', '70%'],
            center: ['60%', '55%'],
            avoidLabelOverlap: true, // 是否开启标签重叠处理
            label: { show: true, position: 'outside', formatter: '{b}' },
            labelLine: { show: true, length: 14, length2: 10, smooth: true },
            data: [],
          },
        ],
      },
    };
  },
  computed: {},
  created() {},
  mounted() {
    // 获取订单类型饼图数据
    this.getOrderTypePieData();
    // 获取发货方式饼图数据
    this.getProductShippingPieData();
  },
  destoryed() {},
  methods: {
    checkPermi,
    // 获取订单统计数据
    getOrderStatisticsData() {
      const param = {
        startTime: this.startTime,
        endTime: this.endTime,
      };
      getOrderStatisticsDataApi(param)
        .then((res) => {
          this.orderStatisticsData = res;
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 获取订单折线图数据
    getOrderLineData() {
      const param = {
        startTime: this.startTime,
        endTime: this.endTime,
      };
      getOrderLineDataApi(param)
        .then((res) => {
          let xAxis = [],
            orderPrice = [],
            refundPrice = [],
            orderVolume = [],
            refundVolume = [];
          res.forEach((item, index) => {
            xAxis.push(item.date);
            orderPrice.push(item.orderPrice);
            refundPrice.push(item.refundPrice);
            orderVolume.push(item.orderVolume);
            refundVolume.push(item.refundVolume);
          });
          this.orderLineOption.xAxis.data = xAxis;
          this.orderLineOption.series[0].data = orderPrice;
          this.orderLineOption.series[1].data = refundPrice;
          this.orderLineOption.series[2].data = orderVolume;
          this.orderLineOption.series[3].data = refundVolume;
          this.orderLineLoading = false;
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 获取订单分类饼图数据
    getOrderTypePieData() {
      const param = {
        startTime: this.startTime,
        endTime: this.endTime,
      };
      getOrderTypePieDataApi(param)
        .then((res) => {
          this.orderTypeOption.series[0].data = res;
          this.orderTypeLoading = false;
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 获取订单类型饼图数据
    getProductShippingPieData() {
      const param = {
        startTime: this.startTime,
        endTime: this.endTime,
      };
      getProductShippingPieDataApi(param)
        .then((res) => {
          this.orderShippingOption.series[0].data = res;
          this.orderShippingLoading = false;
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 日期范围更改
    dateRangeChange(timeVal) {
      this.orderLineLoading = true;
      this.startTime = timeVal[0];
      this.endTime = timeVal[1];
      if (timeVal && timeVal[0] == timeVal[1]) {
        this.orderLineOption.series.forEach((item, index) => {
          item.symbol = 'circle';
        });
      } else {
        this.orderLineOption.series.forEach((item, index) => {
          item.symbol = 'none';
        });
      }
      // 获取订单统计数据
      this.getOrderStatisticsData();
      // 获取订单折线图数据
      this.getOrderLineData();
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep .el-form-item {
  margin-bottom: 0 !important;
}
</style>
