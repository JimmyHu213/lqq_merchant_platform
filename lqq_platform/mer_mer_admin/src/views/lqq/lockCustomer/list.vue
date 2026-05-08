<template>
  <div class="divBox relative">
    <!-- 统计卡片 -->
    <el-card shadow="never" :bordered="false" class="ivu-mt" :body-style="{ padding: '20px' }">
      <div class="stat-row">
        <div class="stat-item">
          <div class="stat-num">{{ countData.total || 0 }}</div>
          <div class="stat-label">锁客总数</div>
        </div>
      </div>
    </el-card>

    <!-- 列表 -->
    <el-card class="box-card mt14" :body-style="{ padding: '20px' }" shadow="never" :bordered="false">
      <div class="mb20">
        <span class="table-title">锁客用户列表</span>
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
import { lockCustomerListApi, lockCustomerCountApi } from '@/api/lqq';

export default {
  name: 'LockCustomerList',
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
      countData: {
        total: 0,
      },
    };
  },
  mounted() {
    this.getCount();
    this.getList();
  },
  methods: {
    getCount() {
      lockCustomerCountApi().then((res) => {
        this.countData = res.data || { total: 0 };
      });
    },
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
.stat-row {
  display: flex;
  gap: 40px;
}
.stat-item {
  text-align: center;
}
.stat-num {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}
.table-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
</style>
