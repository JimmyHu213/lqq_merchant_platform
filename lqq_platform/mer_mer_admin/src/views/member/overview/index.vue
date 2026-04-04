<template>
  <div class="divBox relative">
    <!-- 统计时间选择 -->
    <div class="time-box">
      <el-card :bordered="false" dis-hover :padding="12" shadow="never">
        <button-time-picker @dateRangeChange="dateRangeChange"></button-time-picker>
      </el-card>
    </div>
    <!-- 统计数据 -->
    <div
      v-if="memberStatisticsDataList.length && checkPermi(['merchant:member:statistics:mom:data'])"
      class="member-statistic-box mt14"
    >
      <statistics-data-card-list :statisticsData="memberStatisticsDataList"></statistics-data-card-list>
    </div>
    <!-- 统计图像 -->
    <div class="statistic-image-box">
      <el-row class="mt14" :gutter="14">
        <!-- 会员等级分布 -->
        <el-col :span="12" v-if="checkPermi(['merchant:member:statistics:overview'])">
          <el-card :bordered="false" dis-hover :padding="12" shadow="never">
            <pie-chart
              :title="'当前会员等级分布'"
              :chart-data="memberLevelMap"
              :series-name="'会员等级'"
              :height="'300px'"
            />
          </el-card>
        </el-col>
        <!-- 会员来源渠道分布 -->
        <el-col :span="12" v-if="checkPermi(['merchant:member:statistics:overview'])">
          <el-card :bordered="false" dis-hover :padding="12" shadow="never">
            <pie-chart
              :title="'当前会员来源渠道分布'"
              :chart-data="memberSourceMap"
              :series-name="'会员来源渠道'"
              :height="'300px'"
            />
          </el-card>
        </el-col>
      </el-row>
      <!-- 会员新增趋势图像 -->
      <el-row
        class="mt14"
        v-if="Object.keys(newMemberYData).length > 0 && checkPermi(['merchant:member:statistics:new:trend:data'])"
      >
        <el-col :span="24">
          <el-card :bordered="false" dis-hover :padding="12" shadow="never">
            <line-bar-chart
              :title="'新增会员趋势图'"
              :legend-name-list="['新增会员人数', '充值人数']"
              :x-data="newMemberXData"
              :y-axis-name="['(人)']"
              :y-series-type="['line', 'line']"
              :y-series-data="newMemberYData"
              :height="'400px'"
            />
          </el-card>
        </el-col>
      </el-row>
      <!-- 会员消费趋势图像 -->
      <el-row
        class="mt14"
        v-if="Object.keys(consumeYData).length > 0 && checkPermi(['merchant:member:statistics:consume:trend:data'])"
      >
        <el-col :span="24">
          <el-card :bordered="false" dis-hover :padding="12" shadow="never">
            <line-bar-chart
              :title="'会员消费趋势图'"
              :color-list="consumeColorList"
              :legend-name-list="['下单数量', '消费金额']"
              :x-data="consumeXData"
              :y-axis-name="['(单)', '(元)']"
              :y-axis-position="['left', 'right']"
              :y-axis-index="[0, 1]"
              :is-smooth-list="[false, true]"
              :y-series-type="['bar', 'line']"
              :y-series-data="consumeYData"
              :height="'400px'"
            />
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import {
  getMemberStatisticsMomDataApi,
  getMemberOverviewDataApi,
  getMemberNewTrendDataApi,
  getMemberConsumeTrendDataApi,
} from '@/api/member';
import { checkPermi } from '@/utils/permission';
import ButtonTimePicker from '@/components/ButtonTimePicker/index';
import StatisticsDataCardList from '@/components/StatisticsDataCardList/index';
import PieChart from '@/components/echartsNew/pieChart';
import LineBarChart from '@/components/echartsNew/lineBarChart';

export default {
  name: 'index',
  components: {
    ButtonTimePicker,
    StatisticsDataCardList,
    PieChart,
    LineBarChart,
  },
  data() {
    return {
      startTime: '', // 开始时间
      endTime: '', // 结束时间
      memberStatisticsDataList: [], // 会员统计数据
      memberNum: 0, // 现有会员总数
      memberLevelMap: {}, // 	会员等级分布数据
      memberSourceMap: {}, // 会员来源渠道分布数据
      newMemberXData: [], // 会员新增趋势x轴数据
      newMemberYData: {}, // 会员新增趋势y轴数据
      consumeXData: [], // 会员消费趋势x轴数据
      consumeYData: {}, // 会员消费趋势y轴数据
      // 新增会员趋势图图例配置
      newMemberLegend: {
        orient: 'horizontal',
        top: 'bottom',
        left: 'center',
        data: [
          { name: '新增会员人数', icon: 'rect' },
          { name: '充值人数', icon: 'rect' },
        ],
      },
      // 会员消费趋势图图例配置
      consumeLegend: {
        orient: 'horizontal',
        top: 'bottom',
        left: 'center',
        data: [
          { name: '下单数量', icon: 'circle' },
          { name: '消费金额', icon: 'rect' },
        ],
      },
      // 会员消费趋势图颜色列表
      consumeColorList: [
        {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            {
              offset: 0,
              color: '#8181FF', // 条形顶部的颜色
            },
            {
              offset: 1,
              color: '#E5E5FF', // 条形底部的颜色
            },
          ],
        },
        '#fa8c16',
      ],
    };
  },
  computed: {},
  mounted() {
    if (checkPermi(['merchant:member:level:distribution'])) {
      // this.getLevelDistribution();
    }
    // 获取会员概览数据
    this.getMemberOverviewData();
  },
  methods: {
    checkPermi,
    // 获取会员环比数据
    getMemberStatisticsMomData() {
      const param = {
        dateLimit: this.startTime + ',' + this.endTime,
      };
      getMemberStatisticsMomDataApi(param)
        .then((res) => {
          const statisticData = [
            { name: '现有会员总数', value: this.memberNum, isCumulativeData: true },
            {
              name: '新增会员数',
              value: res.newNum,
              growthRate: res.newNumMom,
              increased: parseFloat(res.newNumMom.replace('%', '')) > 0,
            },
            {
              name: '充值金额(元)',
              value: res.rechargeAmount,
              growthRate: res.rechargeAmountMom,
              increased: parseFloat(res.rechargeAmountMom.replace('%', '')) > 0,
            },
            {
              name: '充值订单数',
              value: res.rechargeOrderNum,
              growthRate: res.rechargeOrderNumMom,
              increased: parseFloat(res.rechargeOrderNumMom.replace('%', '')) > 0,
            },
            {
              name: '会员消费金额(元)',
              value: res.consumeAmount,
              growthRate: res.consumeAmountMom,
              increased: parseFloat(res.consumeAmountMom.replace('%', '')) > 0,
            },
            {
              name: '会员消费订单数',
              value: res.consumeOrderNum,
              growthRate: res.consumeOrderNumMom,
              increased: parseFloat(res.consumeOrderNumMom.replace('%', '')) > 0,
            },
            {
              name: '消费会员人数',
              value: res.consumeMemberNum || 0,
              growthRate: res.consumeMemberNumMom || 0,
              increased: res.consumeMemberNumMom ? parseFloat(res.consumeMemberNumMom.replace('%', '')) > 0 : false,
            },
            {
              name: '活跃会员人数',
              value: res.activeMemberAmount || 0,
              growthRate: res.activeMemberAmountMom || 0,
              increased: res.consumeMemberNumMom ? parseFloat(res.activeMemberAmountMom.replace('%', '')) > 0 : false,
            },
          ];
          this.$set(this, 'memberStatisticsDataList', JSON.parse(JSON.stringify(statisticData)));
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 获取会员概览数据
    getMemberOverviewData() {
      getMemberOverviewDataApi()
        .then((res) => {
          this.memberNum = res.memberNum;
          // 如果先调用了获取会员环比数据的接口，则对现有会员总数进行重新赋值
          if (this.memberStatisticsDataList[0]) {
            this.memberStatisticsDataList[0].value = res.memberNum;
          }
          this.memberLevelMap = res.memberLevelMap;
          this.memberSourceMap = res.memberSourceMap;
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 获取会员新增趋势数据
    getMemberNewTrendData() {
      const param = {
        dateLimit: this.startTime + ',' + this.endTime,
      };
      getMemberNewTrendDataApi(param)
        .then((res) => {
          let xAxis = [],
            yData = { 新增会员人数: [], 充值人数: [] };
          res.forEach((item, index) => {
            xAxis.push(item.date);
            yData['新增会员人数'].push(item.newNum);
            yData['充值人数'].push(item.rechargeNum);
          });
          this.newMemberXData = xAxis;
          this.newMemberYData = yData;
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 获取会员消费趋势数据
    getMemberConsumeTrendData() {
      const param = {
        dateLimit: this.startTime + ',' + this.endTime,
      };
      getMemberConsumeTrendDataApi(param)
        .then((res) => {
          let xAxis = [],
            yData = { 下单数量: [], 消费金额: [] };
          res.forEach((item, index) => {
            xAxis.push(item.date);
            yData['下单数量'].push(item.orderNum);
            yData['消费金额'].push(item.consumeAmount);
          });
          this.consumeXData = xAxis;
          this.consumeYData = yData;
        })
        .catch((err) => {
          this.Message.error(err.msg);
        });
    },
    // 日期范围更改
    dateRangeChange(timeVal) {
      this.startTime = timeVal[0];
      this.endTime = timeVal[1];
      // 获取会员环比数据
      this.getMemberStatisticsMomData();
      // 获取会员新增趋势数据
      this.getMemberNewTrendData();
      // 获取会员消费趋势数据
      this.getMemberConsumeTrendData();
    },
  },
};
</script>

<style lang="scss" scoped>
.member-statistic-box {
  ::v-deep .statistic-list-box {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 14px;
  }
}
::v-deep .el-form-item {
  margin-bottom: 0 !important;
}
</style>
