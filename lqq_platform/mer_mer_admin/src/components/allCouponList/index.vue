<template>
  <div>
    <base-table
      ref="table"
      :loading="listLoading"
      :table-data="tableData"
      :columns="tableColumns"
      :showSelection="showSelection"
      :pagination="{
        pageNum: tableFrom.page,
        pageSize: tableFrom.limit,
      }"
      :checked-list="checkedList"
      @size-change="handleSizeChange"
      @current-change="pageChange"
      @switch-change="onchangeIsShow"
      @selection-change="selectionChange"
    >
      <template #number="{ row, index }">
        <span v-if="!row.isLimited">不限量</span>
        <div v-else>
          <div class="fa">发布：{{ row.total }}</div>
          <div class="sheng">剩余：{{ row.lastTotal }}</div>
        </div>
      </template>
      <template #operate="{ row, index }">
        <slot name="operate" :row="row" :index="index"></slot>
      </template>
    </base-table>
  </div>
</template>

<script>
import { couponTableColumns } from './config';
import { marketingListApi } from '@/api/product';
import { checkPermi } from '@/utils/permission';

export default {
  name: 'index',
  props: {
    checkedList: {
      //选中的数据
      type: Array,
      default: () => [],
    },
    tableFrom: {
      type: Object,
      default: () => ({}),
    },
    name: {
      type: String,
      default: '',
    },
    showOperation: {
      type: Boolean,
      default: true,
    },
    showSelection: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      listLoading: false,
      tableData: {
        data: [],
        total: 0,
      },
      tableColumns: [],
     // checkedList: []
    };
  },
  mounted() {
    if (checkPermi(['merchant:coupon:page:list'])) this.getList();
    if (this.showOperation) {
      this.tableColumns = couponTableColumns;
    } else {
      this.tableColumns = couponTableColumns.slice(0, -2);
    }
  },
  methods: {
    checkPermi,
    selectionChange(selection) {
     // this.checkedList = selection
      this.$emit('changeSelection', selection);
    },
    // 列表
    getList() {
      this.listLoading = true;
      this.tableFrom.name = encodeURIComponent(this.name);
      marketingListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.listLoading = false;
        })
        .catch((res) => {
          this.listLoading = false;
        });
    },
    pageChange(page) {
      this.tableFrom.page = page;
      this.getList();
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val;
      this.getList();
    },
    onchangeIsShow(row) {
      this.$emit('onchangeIsShow', row);
    },
  },
};
</script>

<style scoped></style>
