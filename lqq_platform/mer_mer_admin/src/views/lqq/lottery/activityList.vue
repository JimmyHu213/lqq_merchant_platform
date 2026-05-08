<template>
  <div class="divBox relative">
    <el-card class="box-card" :body-style="{ padding: '20px' }" shadow="never" :bordered="false">
      <div class="mb20">
        <el-button type="primary" size="small" @click="$router.push('/lqq/lottery/create')">创建抽奖活动</el-button>
      </div>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" size="small">
        <el-table-column prop="id" label="ID" min-width="60" />
        <el-table-column prop="name" label="活动名称" min-width="180" :show-overflow-tooltip="true" />
        <el-table-column prop="prizeName" label="奖品" min-width="150" :show-overflow-tooltip="true" />
        <el-table-column prop="pointsCost" label="所需积分" min-width="90" />
        <el-table-column prop="participantThreshold" label="开奖人数" min-width="90" />
        <el-table-column prop="winnerCount" label="中奖名额" min-width="90" />
        <el-table-column label="审核状态" min-width="100">
          <template slot-scope="scope">
            <el-tag type="warning" v-if="scope.row.auditStatus === 0">待审核</el-tag>
            <el-tag type="success" v-else-if="scope.row.auditStatus === 1">已通过</el-tag>
            <el-tag type="danger" v-else-if="scope.row.auditStatus === 2">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="80">
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.status === 1">进行中</el-tag>
            <el-tag type="info" v-else>已关闭</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentPeriod" label="当前期号" min-width="90" />
        <el-table-column label="操作" fixed="right" min-width="200">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="$router.push(`/lqq/lottery/edit/${scope.row.id}`)">编辑</el-button>
            <el-button type="text" size="small" @click="handleSwitch(scope.row)">
              {{ scope.row.status === 1 ? '关闭' : '开启' }}
            </el-button>
            <el-button type="text" size="small" class="red" @click="handleDelete(scope.row)">删除</el-button>
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
import { lotteryListApi, lotterySwitchApi, lotteryDeleteApi } from '@/api/lqq';

export default {
  name: 'LotteryActivityList',
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
      lotteryListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.data.list || [];
          this.tableData.total = res.data.total || 0;
          this.listLoading = false;
        })
        .catch(() => {
          this.listLoading = false;
        });
    },
    handleSwitch(row) {
      const action = row.status === 1 ? '关闭' : '开启';
      this.$confirm(`确定${action}该活动吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        lotterySwitchApi(row.id).then(() => {
          this.$message.success(`${action}成功`);
          this.getList();
        });
      });
    },
    handleDelete(row) {
      this.$confirm('确定删除该活动吗？删除后无法恢复', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        lotteryDeleteApi(row.id).then(() => {
          this.$message.success('删除成功');
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
.red {
  color: #f56c6c;
}
</style>
