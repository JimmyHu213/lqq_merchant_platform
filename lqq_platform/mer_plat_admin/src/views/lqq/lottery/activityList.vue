<template>
  <div class="divBox relative">
    <el-card class="box-card" :body-style="{ padding: '20px' }" shadow="never" :bordered="false">
      <div class="mb20">
        <span class="table-title">抽奖活动审核</span>
      </div>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" size="small">
        <el-table-column prop="id" label="ID" min-width="60" />
        <el-table-column prop="name" label="活动名称" min-width="180" :show-overflow-tooltip="true" />
        <el-table-column prop="prizeName" label="奖品" min-width="150" :show-overflow-tooltip="true" />
        <el-table-column prop="merId" label="商户ID" min-width="80" />
        <el-table-column prop="pointsCost" label="所需积分" min-width="90" />
        <el-table-column prop="participantThreshold" label="开奖人数" min-width="90" />
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
        <el-table-column label="操作" fixed="right" min-width="250">
          <template slot-scope="scope">
            <el-button type="text" size="small" v-if="scope.row.auditStatus === 0" @click="handleAudit(scope.row, 1)">
              通过
            </el-button>
            <el-button type="text" size="small" class="orange" v-if="scope.row.auditStatus === 0" @click="handleReject(scope.row)">
              拒绝
            </el-button>
            <el-button type="text" size="small" v-if="scope.row.status === 1" @click="handleClose(scope.row)">
              强制关闭
            </el-button>
            <el-button type="text" size="small" class="red" @click="handleDelete(scope.row)">
              删除
            </el-button>
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

    <!-- 拒绝原因弹窗 -->
    <el-dialog title="拒绝原因" :visible.sync="rejectDialogVisible" width="400px">
      <el-input type="textarea" v-model="rejectReason" placeholder="请输入拒绝原因" :rows="4" />
      <span slot="footer">
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReject">确认拒绝</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { lotteryListApi, lotteryAuditApi, lotteryCloseApi, lotteryDeleteApi } from '@/api/lqq';

export default {
  name: 'PlatLotteryList',
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
      rejectDialogVisible: false,
      rejectReason: '',
      currentRejectRow: null,
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
    handleAudit(row, status) {
      this.$confirm('确定通过该活动审核？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        lotteryAuditApi({ activityId: row.id, auditStatus: status, reason: '' }).then(() => {
          this.$message.success('审核通过');
          this.getList();
        });
      });
    },
    handleReject(row) {
      this.currentRejectRow = row;
      this.rejectReason = '';
      this.rejectDialogVisible = true;
    },
    submitReject() {
      if (!this.rejectReason) {
        this.$message.warning('请输入拒绝原因');
        return;
      }
      lotteryAuditApi({
        activityId: this.currentRejectRow.id,
        auditStatus: 2,
        reason: this.rejectReason,
      }).then(() => {
        this.$message.success('已拒绝');
        this.rejectDialogVisible = false;
        this.getList();
      });
    },
    handleClose(row) {
      this.$confirm('确定强制关闭该活动？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        lotteryCloseApi(row.id).then(() => {
          this.$message.success('已关闭');
          this.getList();
        });
      });
    },
    handleDelete(row) {
      this.$confirm('确定删除该活动？', '提示', {
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
.table-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.red {
  color: #f56c6c;
}
.orange {
  color: #e6a23c;
}
</style>
