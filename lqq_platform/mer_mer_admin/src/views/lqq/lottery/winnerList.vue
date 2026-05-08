<template>
  <div class="divBox relative">
    <el-card class="box-card" :body-style="{ padding: '20px' }" shadow="never" :bordered="false">
      <div class="mb20">
        <span class="table-title">中奖记录</span>
      </div>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" size="small">
        <el-table-column prop="id" label="记录ID" min-width="80" />
        <el-table-column prop="uid" label="用户ID" min-width="80" />
        <el-table-column prop="nickname" label="用户昵称" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column prop="activityId" label="活动ID" min-width="80" />
        <el-table-column prop="activityName" label="活动名称" min-width="180" :show-overflow-tooltip="true" />
        <el-table-column label="中奖状态" min-width="90">
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.isWinner === 1">已中奖</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="兑奖状态" min-width="90">
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.isRedeemed === 1">已兑奖</el-tag>
            <el-tag type="warning" v-else>待兑奖</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="中奖时间" min-width="155" />
        <el-table-column label="操作" fixed="right" min-width="100">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              v-if="scope.row.isRedeemed !== 1"
              @click="handleRedeem(scope.row)"
            >核销兑奖</el-button>
            <span v-else class="text-muted">已核销</span>
          </template>
        </el-table-column>
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
import { lotteryWinnerListApi, lotteryRedeemApi } from '@/api/lqq';

export default {
  name: 'LotteryWinners',
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
      lotteryWinnerListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.data.list || [];
          this.tableData.total = res.data.total || 0;
          this.listLoading = false;
        })
        .catch(() => {
          this.listLoading = false;
        });
    },
    handleRedeem(row) {
      this.$confirm('确认核销兑奖？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        lotteryRedeemApi(row.id).then(() => {
          this.$message.success('兑奖成功');
          this.getList();
        });
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
.text-muted {
  color: #c0c4cc;
  font-size: 12px;
}
</style>
