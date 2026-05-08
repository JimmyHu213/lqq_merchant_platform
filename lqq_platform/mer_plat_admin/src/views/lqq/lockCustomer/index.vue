<template>
  <div class="divBox relative">
    <el-card class="box-card" :body-style="{ padding: '20px' }" shadow="never" :bordered="false">
      <div class="mb20">
        <span class="table-title">全平台锁客记录</span>
      </div>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" size="small">
        <el-table-column prop="id" label="用户ID" min-width="80" />
        <el-table-column label="头像" min-width="70">
          <template slot-scope="scope">
            <el-avatar :src="scope.row.avatar" :size="36"></el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="lockedMerchantId" label="锁定商户ID" min-width="100" />
        <el-table-column prop="lockedMerchantTime" label="锁定时间" min-width="160" />
      </el-table>
      <div class="block">
        <el-pagination
          background
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout="total, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import { lockCustomerListApi } from '@/api/lqq';

export default {
  name: 'PlatLockCustomer',
  data() {
    return {
      listLoading: false,
      tableFrom: {
        page: 1,
        limit: 20,
      },
      tableData: {
        data: [],
        total: 0,
      },
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    getList() {
      this.listLoading = true;
      lockCustomerListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.data.list || [];
          this.tableData.total = res.data.total || 0;
          this.listLoading = false;
        })
        .catch(() => {
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
  },
};
</script>

<style scoped>
.table-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
</style>
