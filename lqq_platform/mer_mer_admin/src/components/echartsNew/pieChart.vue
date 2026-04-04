<template>
  <div class="pie-chart-container">
    <div v-if="title" class="chart-title">{{ title }}</div>
    <echarts-new :styles="chartStyles" :option-data="chartOption" />
  </div>
</template>

<script>
import echartsNew from './index';

export default {
  name: 'PieChart',
  components: {
    echartsNew,
  },
  props: {
    // 图表标题
    title: {
      type: String,
      default: '',
    },
    // 图表数据 { '名称1': 值1, '名称2': 值2 }
    chartData: {
      type: Object,
      default: () => ({}),
    },
    // 颜色映射 { '名称1': '#颜色1', '名称2': '#颜色2' }
    colorMap: {
      type: Object,
      default: () => ({}),
    },
    // 颜色列表
    colorList: {
      type: Array,
      default: () => {
        return ['#1890ff', '#fa8c16', '#e6a23c', '#52c41a', '#A277FF']
      }
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
    // 图例位置配置
    legend: {
      type: Object,
      default: function () {
        return {
          orient: 'vertical',
          right: 10,
          top: 'center',
          formatter: (name) => {
            let info = this.chartData;
            let total = this.dataTotal
            let value = info[name]
            let pValue= total == 0 ? 0 : (value / total * 100).toFixed(2);
            return `${name}: ${value}人 (${pValue}%)`
          },
        };
      },
      // default: () => ({
      //   orient: 'vertical',
      //   right: 10,
      //   top: 'center',
      //   formatter: '{name}: {c}人 ({d}%)'
      // })
    },
    // 数据系列名称
    seriesName: {
      type: String,
      default: '数据统计',
    },
    // 饼图半径
    radius: {
      type: Array,
      default: () => ['40%', '70%'],
    },
    // 饼图中心位置
    center: {
      type: Array,
      default: () => ['35%', '50%'],
    },
    // 是否显示扇区标签
    showLabel: {
      type: Boolean,
      default: true,
    },
    // 扇区标签位置
    labelPosition: {
      type: String,
      default: 'outside',
    },
    // 扇区标签格式化
    labelFormatter: {
      type: String,
      default: '{b}: {d}%',
    },
    // 扇区标签样式
    labelStyle: {
      type: Object,
      default: () => ({
        fontSize: '12px',
        color: '#303133',
      }),
    },
    // 是否显示标签线
    showLabelLine: {
      type: Boolean,
      default: true,
    },
    // 标签线样式
    labelLineStyle: {
      type: Object,
      default: () => ({
        color: '#ccc',
        width: 1,
      }),
    },
  },
  computed: {
    // 饼图数据和
    dataTotal() {
      return Object.values(this.chartData).reduce((total, current) => total + current, 0);
    },
    // 图表样式
    chartStyles() {
      return {
        width: this.width,
        height: this.height,
      };
    },
    // 图表配置
    chartOption() {
      // 转换数据格式
      const seriesData = Object.entries(this.chartData).map(([name, value]) => ({
        name,
        value,
        // itemStyle: {
        //   color: this.colorMap[name] || '#909399',
        // },
      }));
      return {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}人 ({d}%)',
        },
        legend: {
          ...this.legend,
          data: seriesData.map((item) => item.name),
        },
        color: this.colorList,
        series: [
          {
            name: this.seriesName,
            type: 'pie',
            radius: this.radius,
            center: this.center,
            avoidLabelOverlap: true, // 是否开启标签重叠处理
            itemStyle: {
              borderRadius: 8,
              borderColor: '#fff',
              borderWidth: 2,
            },
            label: {
              show: this.showLabel,
              position: this.labelPosition,
              formatter: this.labelFormatter,
              fontSize: this.labelStyle.fontSize,
              color: this.labelStyle.color,
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '14',
                fontWeight: 'bold',
              },
            },
            labelLine: {
              show: this.showLabelLine,
              lineStyle: {
                color: this.labelLineStyle.color,
                width: this.labelLineStyle.width,
              },
              length: 14,
              length2: 10,
            },
            data: seriesData,
          },
        ],
      };
    },
  },
};
</script>

<style scoped>
.pie-chart-container {
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
