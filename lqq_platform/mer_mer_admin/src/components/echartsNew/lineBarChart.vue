<template>
  <div class="line-bar-chart-container">
    <div v-if="title" class="chart-title">{{ title }}</div>
    <echarts-new :styles="chartStyles" :option-data="chartOption" />
  </div>
</template>

<script>
import echartsNew from './index';

export default {
  name: 'LineBarChart',
  components: {
    echartsNew,
  },
  props: {
    // 图表标题
    title: {
      type: String,
      default: '',
    },
    // 颜色列表
    colorList: {
      type: Array,
      default: () => {
        return ['#1890ff', '#fa8c16', '#e6a23c', '#52c41a', '#A277FF'];
      },
    },
    // 图表高度
    height: {
      type: String,
      default: '400px',
    },
    // 图表宽度
    width: {
      type: String,
      default: '100%',
    },
    // 图例名称列表
    legendNameList: {
      type: Array,
      default: () => [],
    },
    // 图例图标列表 ['circle', 'rect'] (圆形，矩形)
    legendIconList: {
      type: Array,
      default: () => [],
    },
    // 图例位置配置
    legend: {
      type: Object,
      default: function () {
        return {
          orient: 'horizontal',
          top: 'bottom',
          left: 'center',
        };
      },
    },
    // x轴类型
    xType: {
      type: String,
      default: 'category',
    },
    // x轴数据
    xData: {
      type: Array,
      default: () => [],
    },
    // x轴配置
    xAxis: {
      type: Object,
      default: () => {
        return {
          type: 'category',
          boundaryGap: true,
          axisLine: { lineStyle: { color: '#E5E8EF' } },
          axisLabel: { color: '#666' },
          axisTick: { alignWithLabel: true },
        };
      },
    },
    // 图表类型 ['line', 'bar']
    ySeriesType: {
      type: Array,
      default: () => [],
    },
    // 折线是否平滑 [true, false]
    isSmoothList: {
      type: Array,
      default: () => [],
    },
    // 图表数据 {'标签名1': [数据1], '标签名2': [数据2]}
    ySeriesData: {
      type: Object,
      default: () => ({}),
    },
    // y轴名称 ['名称1', '名称2']
    yAxisName: {
      type: Array,
      default: () => [],
    },
    // y轴数据类型 ['value', 'category']
    yAxisType: {
      type: Array,
      default: () => [],
    },
    // y轴位置 ['left', 'right']
    yAxisPosition: {
      type: Array,
      default: () => [],
    },
    // 图表数据对应y轴的索引 [0, 1]
    yAxisIndex: {
      type: Array,
      default: () => [],
    },
    // y轴配置
    yAxis: {
      type: Array,
      default: function () {
        const yAxisOpt = this.yAxisName.map((item, index) => {
          return {
            type: this.yAxisType[index] || 'value',
            name: item || '值',
            position: this.yAxisPosition[index] || 'left',
            min: 0,
            axisLine: { show: false },
            splitLine: { lineStyle: { color: '#F0F0F0' } },
            axisLabel: { color: '#666', margin: 30 },
          };
        });
        return yAxisOpt;
      },
    },
  },
  computed: {
    // 图表样式
    chartStyles() {
      return {
        width: this.width,
        height: this.height,
      };
    },
    // 图表配置
    chartOption() {
      let legendData = [];
      const seriesOpt = this.legendNameList.map((item, index) => {
        legendData.push({
          name: item,
          icon: this.legendIconList[index] || '',
        });
        return {
          name: item,
          type: this.ySeriesType[index],
          yAxisIndex: this.yAxisIndex[index] || 0,
          smooth: this.isSmoothList[index] || false,
          symbol: 'circle',
          symbolSize: '6',
          lineStyle: { width: 2 },
          barMaxWidth: 20,
          data: this.ySeriesData[item],
        };
      });
      return {
        color: this.colorList,
        // 指针数据显示
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'none' },
        },
        // 图例
        legend: {
          data: legendData,
          ...this.legend,
        },
        // x轴配置
        xAxis: {
          data: this.xData,
          ...this.xAxis,
        },
        // y轴配置
        yAxis: this.yAxis,
        // 数据配置
        series: seriesOpt,
      };
    },
  },
};
</script>

<style scoped>
.line-bar-chart-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.chart-title {
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}
</style>
