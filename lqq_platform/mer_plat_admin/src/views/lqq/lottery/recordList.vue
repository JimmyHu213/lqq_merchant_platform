<template>
  <div class="divBox relative">
    <el-card class="box-card" :body-style="{ padding: '20px' }" shadow="never" :bordered="false">
      <div class="mb20">
        <span class="table-title">全平台抽奖记录</span>
      </div>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" size="small">
        <el-table-column prop="id" label="记录ID" min-width="80" />
        <el-table-column prop="uid" label="用户ID" min-width="80" />
        <el-table-column prop="activityId" label="活动ID" min-width="80" />
        <el-table-column prop="activityName" label="活动名称" min-width="180" :show-overflow-tooltip="true" />
        <el-table-column prop="merId" label="商户ID" min-width="80" />
        <el-table-column prop="pointsCost" label="消耗积分" min-width="90" />
        <el-table-column label="中奖状态" min-width="90">
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.isWinner === 1">已中奖</el-tag>
            <el-tag type="info" v-else>未中奖</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="参与时间" min-width="155" />
        <el-table-column prop="drawTime" label="开奖时间" min-width="155" />
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
import { lotteryRecordListApi } from '@/api/lqq';

export default {
  name: 'PlatLotteryRecords',
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
      lotteryRecordListApi(this.tableFrom)
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
