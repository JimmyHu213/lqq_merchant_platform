<template>
  <div class="divBox relative">
    <base-search-form
        :form-items="orderSearchFormItems"
        :form-data="tableFrom"
        @search="handleSearch"
        @reset="handleSearch"
    ></base-search-form>
    <base-table
        ref="table"
        class="mt20"
        :loading="listLoading"
        :table-data="tableData"
        :columns="tableColumns"
        :pagination="{
          pageNum: tableFrom.page,
          pageSize: tableFrom.limit,
        }"
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
      <template #amount="{ row }">
        <div>本金：{{row.rechargeAmount}}</div>
        <div>赠送：{{row.giftAmount}}</div>
      </template>
      <!-- 操作插槽 -->
      <template #operate="{ row }">
        <a v-if="checkPermi(['merchant:user:detail'])" @click="onDetails(row.id)">用户详情</a>
      </template>
    </base-table>
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
import { orderTableColumns, orderSearchFormItems } from './config';
import * as $constants from '@/utils/constants';
import { productLstApi } from '@/api/product';
import {shippingOrderApi} from "@/api/member";
// 搜索表单配置
const searchFormDatas = {
  searchType: 'all',
  content: '',
  dateLimit: '',
  dateLimitAttr: [],
  page: 1,
  limit: $constants.page.limit[0],
  payType: '',
  orderNo: '',
};
export default {
  name: 'CreditOrder',
  data() {
    return {
      tableColumns: orderTableColumns,
      tableData: {
        data: [],
        total: 0,
      },
      orderSearchFormItems,
      listLoading: false,
      tableFrom: Object.assign({}, searchFormDatas),
    };
  },
  mounted() {
    if (checkPermi(['merchant:shipping:credits:order:page'])) this.getList();
  },
  methods: {
    checkPermi,
    // 搜索处理
    handleSearch(formData) {
      this.tableFrom =  { ...formData };
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
        let res = await shippingOrderApi(this.tableFrom);
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
