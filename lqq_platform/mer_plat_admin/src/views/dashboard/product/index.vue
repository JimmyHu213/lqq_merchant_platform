<template>
  <div>
    <!-- 统计时间选择 -->
    <div class="time-box">
      <el-card :bordered="false" dis-hover :padding="12" shadow="never">
        <button-time-picker @dateRangeChange="dateRangeChange"></button-time-picker>
      </el-card>
    </div>
    <!-- 统计数据 -->
    <div v-if="productStatisticsData.length && checkPermi(['platform:product:statistics:top'])" class="mt14">
      <statistics-data-card-list :statisticsData="productStatisticsData"></statistics-data-card-list>
    </div>
    <!-- 统计图像 -->
    <div class="statistic-image-box">
      <!-- 商品折线图 -->
      <el-row class="mt14" v-if="checkPermi(['platform:product:statistics:chart'])">
        <el-col :span="24">
          <el-card :bordered="false" dis-hover :padding="12" shadow="never" v-loading="productLineLoading">
            <echarts-new :option-data="productLineOption" :styles="style" height="100%" width="100%"> </echarts-new>
          </el-card>
        </el-col>
      </el-row>
      <el-row class="mt14" :gutter="14">
        <!-- 商品分类图 -->
        <el-col :span="12" v-if="checkPermi(['platform:product:statistics:category'])">
          <el-card :bordered="false" dis-hover :padding="12" shadow="never" v-loading="productCategoryLoading">
            <echarts-new :option-data="productCategoryOption" :styles="style" height="100%" width="100%"> </echarts-new>
          </el-card>
        </el-col>
        <!-- 商品类型图 -->
        <el-col :span="12" v-if="checkPermi(['platform:product:statistics:type'])">
          <el-card :bordered="false" dis-hover :padding="12" shadow="never" v-loading="productTypeLoading">
            <echarts-new :option-data="productTypeOption" :styles="style" height="100%" width="100%"> </echarts-new>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import {
  getProductStatisticsDataApi,
  getProductLineDataApi,
  getProductCategoryPieDataApi,
  getProductTypePieDataApi,
} from '@/api/dashboard.js';
import echartsNew from '@/components/echartsNew/index';
import ButtonTimePicker from '@/components/ButtonTimePicker/index';
import StatisticsDataCardList from '@/components/StatisticsDataCardList/index';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'productStatistic',
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
      productStatisticsData: [], // 商品统计数据
      productLineLoading: true, // 商品折线统计图是否加载
      productCategoryLoading: true, // 商品分类饼图图是否加载
      productTypeLoading: true, // 商品类型饼图图是否加载
      style: { height: '320px' },
      // 商品线形图选项
      productLineOption: {
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
          data: ['商品浏览量', '收藏量', '下单量', '支付量'],
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
          min: 0,
          axisLine: { show: false },
          splitLine: { lineStyle: { color: '#F0F0F0' } },
          axisLabel: { color: '#666', margin: 30 },
        },
        series: [
          {
            name: '商品浏览量',
            type: 'line',
            smooth: true,
            symbol: 'none',
            symbolSize: '8',
            lineStyle: { width: 2 },
            data: [],
          },
          {
            name: '收藏量',
            type: 'line',
            smooth: true,
            symbol: 'none',
            symbolSize: '8',
            lineStyle: { width: 2 },
            data: [],
          },
          {
            name: '下单量',
            type: 'line',
            smooth: true,
            symbol: 'none',
            symbolSize: '8',
            lineStyle: { width: 2 },
            data: [],
          },
          {
            name: '支付量',
            type: 'line',
            smooth: true,
            symbol: 'none',
            symbolSize: '8',
            lineStyle: { width: 2 },
            data: [],
          },
        ],
      },
      // 商品分类饼图选项
      productCategoryOption: {
        title: { text: '商品分类', left: 0, top: 10, textStyle: { fontSize: 18, fontWeight: 600 } },
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
            name: '商品分类',
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
      // 商品类型饼图选项
      productTypeOption: {
        title: { text: '商品类型', left: 0, top: 10, textStyle: { fontSize: 18, fontWeight: 600 } },
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
            name: '商品类型',
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
  mounted() {
    // 获取商品分类饼图数据
    this.getProductCategoryPieData();
    // 获取商品类型饼图数据
    this.getProductTypePieData();
  },
  methods: {
    checkPermi,
    // 获取商品统计数据
    getProductStatisticsData() {
      const param = {
        startTime: this.startTime,
        endTime: this.endTime,
      };
      getProductStatisticsDataApi(param)
        .then((res) => {
          this.productStatisticsData = res;
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 获取商品折线图数据
    getProductLineData() {
      const param = {
        startTime: this.startTime,
        endTime: this.endTime,
      };
      getProductLineDataApi(param)
        .then((res) => {
          let xAxis = [],
            visitVolume = [],
            collectionVolume = [],
            orderVolume = [],
            paidVolume = [];
          res.forEach((item, index) => {
            xAxis.push(item.date);
            visitVolume.push(item.visitVolume);
            collectionVolume.push(item.collectionVolume);
            orderVolume.push(item.orderVolume);
            paidVolume.push(item.paidVolume);
          });
          this.productLineOption.xAxis.data = xAxis;
          this.productLineOption.series[0].data = visitVolume;
          this.productLineOption.series[1].data = collectionVolume;
          this.productLineOption.series[2].data = orderVolume;
          this.productLineOption.series[3].data = paidVolume;
          this.productLineLoading = false;
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 获取商品分类饼图数据
    getProductCategoryPieData() {
      const param = {
        startTime: this.startTime,
        endTime: this.endTime,
      };
      getProductCategoryPieDataApi(param)
        .then((res) => {
          this.productCategoryOption.series[0].data = res;
          this.productCategoryLoading = false;
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 获取商品类型饼图数据
    getProductTypePieData() {
      const param = {
        startTime: this.startTime,
        endTime: this.endTime,
      };
      getProductTypePieDataApi(param)
        .then((res) => {
          this.productTypeOption.series[0].data = res;
          this.productTypeLoading = false;
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 日期范围更改
    dateRangeChange(timeVal) {
      this.productLineLoading = true;
      this.startTime = timeVal[0];
      this.endTime = timeVal[1];
      if (timeVal && timeVal[0] == timeVal[1]) {
        this.productLineOption.series.forEach((item, index) => {
          item.symbol = 'circle';
        });
      } else {
        this.productLineOption.series.forEach((item, index) => {
          item.symbol = 'none';
        });
      }
      // 获取商品统计数据
      this.getProductStatisticsData();
      // 获取商品折线图数据
      this.getProductLineData();
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep .el-form-item {
  margin-bottom: 0 !important;
}
</style>
