<template>
  <div class="divBox relative">
    <base-search-form
      :form-items="refundSearchFormItems"
      :form-data="tableFrom"
      @search="handleSearch"
      @reset="handleReset"
    ></base-search-form>
    <base-table
      ref="table"
      class="mt20"
      :tabs="refundTabs"
      :loading="listLoading"
      :table-data="tableData"
      :activeTab="activeTab"
      :columns="tableColumns"
      :pagination="{
        pageNum: tableFrom.page,
        pageSize: tableFrom.limit,
      }"
      @tab-click="tabSearch"
      @size-change="handleSizeChange"
      @current-change="pageChange"
    >
      <!-- 昵称插槽 -->
      <template #nickname="{ row }">
        <div class="acea-row row-middle">
          <div class="demo-image__preview line-heightOne mr5">
            <el-image :src="row.avatar" class="el-image" :preview-src-list="[row.avatar]" fit="cover"/>
          </div>
          <span :class="row.isLogoff ? 'red' : ''" class="mr5">{{ row.nickname || '--' }}</span>
          <span class="red"> | {{ row.uid }}</span>
          <span v-if="row.isLogoff" class="red"> | (已注销)</span>
        </div>
      </template>

      <!-- 操作插槽 -->
      <template #operate="{ row }">
        <a v-if="checkPermi(['merchant:shipping:credits:refund:order:info'])" @click="onOrderDetails(row)">详情</a>
        <template v-if="row.refundStatus === 0 && checkPermi(['merchant:refund:order:audit'])">
          <el-divider direction="vertical"></el-divider>
          <a @click="handleApprovedReview(row)">同意</a>
          <el-divider direction="vertical"></el-divider>
          <a @click="handleOrderRefuse(row)">拒绝</a>
        </template>
      </template>
    </base-table>

    <refund-detail
      ref="orderDetail"
      :drawerVisible="drawerVisible"
      :refundOrderNo="refundOrderNo"
      v-if="drawerVisible"
      @onClosedrawerVisible="onClosedrawerVisible"
      @getReviewSuccessful="getReviewSuccessful"
    ></refund-detail>
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

import { checkPermi } from '@/utils/permission';
import {refundTableColumns, refundSearchFormItems, refundTabs, onApprovedReview, onOrderRefuse} from './config';
import * as $constants from '@/utils/constants';
import { productLstApi } from '@/api/product';
import { shippingCreditsRefundListApi } from '@/api/member';
import AgreeToReturn from '@/views/order/components/agreeToReturn';
import RefundDetail from './refundDetail';
// 搜索表单配置
const searchFormDatas = {
  searchType: 'all',
  content: '',
  dateLimit: '',
  dateLimitAttr: [],
  page: 1,
  limit: $constants.page.limit[0],
  refundOrderNo: '',
  refundStatus: '99',
};
export default {
  name: 'CreditOrder',
  data() {
    return {
      activeTab: '99',
      refundTabs,
      tableColumns: refundTableColumns,
      tableData: {
        data: [],
        total: 0,
      },
      refundSearchFormItems,
      listLoading: false,
      tableFrom: Object.assign({}, searchFormDatas),
      refundOrderNo: '',
      drawerVisible: false,
    };
  },
  mounted() {
    if (checkPermi(['merchant:shipping:credits:refund:order:page'])) this.getList();
  },
  components: {
    RefundDetail,
  },
  methods: {
    checkPermi,
    onClosedrawerVisible() {
      this.drawerVisible = false;
    },
    //审核拒绝
    handleOrderRefuse(row) {
      onOrderRefuse.call(this, row.refundOrderNo);
    },
    //审核同意
    handleApprovedReview(row) {
      onApprovedReview.call(this, row.refundOrderNo);
    },
    //审核成功回调
    getSuccessful() {
      this.getList(1);
    },
    //详情中审核成功回调
    getReviewSuccessful() {
      this.getSuccessful();
    },
    // 详情
    onOrderDetails(row) {
      this.refundOrderNo = row.refundOrderNo;
      this.drawerVisible = true;
    },
    tabSearch(activeTabNew) {
      this.tableFrom.refundStatus = activeTabNew;
      // 执行搜索
      this.getList(1);
    },
    // 搜索处理
    handleSearch(formData) {
      this.tableFrom = { ...formData };
      // 执行搜索
      this.getList(1);
    },

    // 重置处理
    handleReset() {
      // 执行搜索
      this.getList(1);
    },
    // 列表
    async getList(num) {
      this.listLoading = true;
      this.tableFrom.page = num ? num : this.tableFrom.page;
      try {
        let res = await shippingCreditsRefundListApi(this.tableFrom);
        this.tableData.data = res.list;
        this.tableData.total = res.total;
        this.listLoading = false;
      } catch (e) {
        this.listLoading = false;
      }
    },
    pageChange(page) {
      this.tableFrom.page = page;
      this.getList();
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val;
      this.getList();
    },
  },
};
</script>

<style scoped></style>
