<template>
  <div class="statistic-list-box">
    <el-card
      v-for="(item, index) in statisticsData"
      class="statistic-card"
      :key="index"
      :bordered="false"
      dis-hover
      :padding="12"
      shadow="never"
    >
      <div class="statistic-box flex-column color-333">
        <div class="fs-16 fw-500">{{ item.name }}</div>
        <div class="fs-34 fw-600 mt-16">{{ item.value }}</div>
        <div class="mt-16" v-if="item.isCumulativeData">
          <span>累计至：{{ todayDate }}</span>
        </div>
        <div class="mt-16" v-else>
          <span>环比增长：</span>
          <span :class="item.increased ? 'color-up' : 'color-down'"
            >{{ item.growthRate }}
            <i v-if="item.increased" class="el-icon-caret-top"></i>
            <i v-else class="el-icon-caret-bottom"></i>
          </span>
        </div>
      </div>
    </el-card>
  </div>
</template>
<script>
export default {
  name: 'StatisticsDataCardList',
  components: {},
  mixins: [],
  props: {
    statisticsData: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {};
  },
  computed: {
    // 今天日期
    todayDate() {
      const date = new Date();
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
  },
  mounted() {},
  methods: {},
};
</script>
<style lang="scss" scoped>
.statistic-list-box {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 14px;
}
@media (min-width: 1000px) and (max-width: 1600px) {
  .statistic-list-box {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 14px;
  }
}
.mt-16 {
  margin-top: 16px;
}
.color-333 {
  color: #333;
}
.color-up {
  color: #f64f54;
}
.color-down {
  color: #19be6b;
}
.fs-16 {
  font-size: 16px;
}
.fs-34 {
  font-size: 34px;
}
.fw-500 {
  font-weight: 500;
}
.fw-600 {
  font-weight: 600;
}
</style>
