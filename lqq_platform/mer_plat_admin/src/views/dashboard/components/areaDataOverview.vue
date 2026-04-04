<template>
  <div class="divBox" style="padding-bottom: 0">
    <el-row :gutter="14" class="areaDataOverview">
      <!-- 圈层商户数 -->
      <el-col v-bind="grid" class="ivu-mb">
        <el-card :bordered="false" dis-hover :padding="20" shadow="never">
          <div class="data-card">
            <div class="icon-wrapper merchant-icon">
              <img src="@/assets/imgs/area/shanghu.png" alt="商户" />
            </div>
            <div class="data-content">
              <div class="data-number">{{ statisticsData.merchantNum }}</div>
              <div class="data-label">圈层商户数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 圈层商品数 -->
      <el-col v-bind="grid" class="ivu-mb">
        <el-card :bordered="false" dis-hover :padding="20" shadow="never">
          <div class="data-card">
            <div class="icon-wrapper product-icon">
              <img src="@/assets/imgs/area/shangpin.png" alt="商品" />
            </div>
            <div class="data-content">
              <div class="data-number">{{ statisticsData.productNum || 0 }}</div>
              <div class="data-label">圈层商品数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 圈层订单量 -->
      <el-col v-bind="grid" class="ivu-mb">
        <el-card :bordered="false" dis-hover :padding="20" shadow="never">
          <div class="data-card">
            <div class="icon-wrapper sales-icon">
              <img src="@/assets/imgs/area/xiaoliang.png" alt="订单" />
            </div>
            <div class="data-content">
              <div class="data-number">{{ statisticsData.orderNum || 0 }}</div>
              <div class="data-label">圈层订单量</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 圈层销售额 -->
      <el-col v-bind="grid" class="ivu-mb">
        <el-card :bordered="false" dis-hover :padding="20" shadow="never">
          <div class="data-card">
            <div class="icon-wrapper sales-icon">
              <img src="@/assets/imgs/area/xiaoliang.png" alt="销售额" />
            </div>
            <div class="data-content">
              <div class="data-number">{{ formatSales(statisticsData.sales) }}</div>
              <div class="data-label">圈层销售额</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
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
import * as areaApi from '@/api/area.js';
import { isPlatform } from '@/utils/settingMer';

export default {
  name: 'AreaDataOverview',
  data() {
    return {
      grid: { xl: 6, lg: 6, md: 12, sm: 12, xs: 24 },
      statisticsData: {}, // 统计数据
    };
  },
  methods: {
    // 获取统计数据
    async getStatisticsData() {
      try {
        const res = await areaApi.areasParentHomeIndexApi();
        this.statisticsData = res;
      } catch (error) {
        this.statisticsData = {};
      }
    },

    // 格式化销售额显示
    formatSales(value) {
      if (!value) return '0.00';
      return parseFloat(value).toFixed(2);
    }
  },
  mounted() {
    this.getStatisticsData();
  },
};
</script>

<style scoped lang="scss">
.ivu-mb {
  height: 120px;
  margin-bottom: 14px;
  .el-card {
    height: 120px;
    border: 1px solid #f0f0f0;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      transform: translateY(-2px);
    }
  }
}

.areaDataOverview {
  ::v-deep .el-card__body {
    padding: 20px !important;
    height: 100%;
    display: flex;
    align-items: center;
  }
}

.data-card {
  display: flex;
  align-items: center;
  width: 100%;

  .icon-wrapper {
    width: 60px;
    height: 60px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;

    img {
      width: 62px;
      height: 62px;
      object-fit: contain;
    }

    // &.merchant-icon {
    //   background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    // }

    // &.product-icon {
    //   background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    // }

    // &.sales-icon {
    //   background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    // }
  }

  .data-content {
    flex: 1;

    .data-number {
      font-size: 28px;
      font-weight: 600;
      color: #333;
      line-height: 1.2;
      margin-bottom: 4px;
    }

    .data-label {
      font-size: 14px;
      color: #666;
      font-weight: 400;
    }
  }
}

@media (max-width: 768px) {
  .data-card {
    .icon-wrapper {
      width: 50px;
      height: 50px;
      margin-right: 12px;

      img {
        width: 20px;
        height: 20px;
      }
    }

    .data-content {
      .data-number {
        font-size: 24px;
      }

      .data-label {
        font-size: 12px;
      }
    }
  }
}
</style>
