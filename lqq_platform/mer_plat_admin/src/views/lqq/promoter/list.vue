<template>
  <div class="divBox relative">
    <el-card class="box-card" :body-style="{ padding: '20px' }" shadow="never" :bordered="false">
      <div class="mb20">
        <span class="table-title">推广员-商户绑定关系</span>
      </div>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" size="small">
        <el-table-column prop="id" label="ID" min-width="60" />
        <el-table-column prop="uid" label="推广员UID" min-width="100" />
        <el-table-column prop="merId" label="商户ID" min-width="100" />
        <el-table-column prop="commissionRate" label="佣金比例(%)" min-width="110" />
        <el-table-column label="审核状态" min-width="100">
          <template slot-scope="scope">
            <el-tag :type="auditStatusType(scope.row.auditStatus)" size="small">
              {{ auditStatusText(scope.row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
              {{ scope.row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="绑定时间" min-width="160" />
        <el-table-column label="操作" min-width="200" fixed="right">
          <template slot-scope="scope">
            <template v-if="scope.row.auditStatus === 0">
              <el-button type="text" size="small" @click="handleAudit(scope.row, 1)">通过</el-button>
              <el-button type="text" size="small" class="danger-text" @click="handleAudit(scope.row, 2)">拒绝</el-button>
            </template>
            <el-button
              v-if="scope.row.auditStatus === 1 && scope.row.status === 1"
              type="text"
              size="small"
              class="danger-text"
              @click="handleForceUnbind(scope.row)"
            >强制解绑</el-button>
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
    <el-dialog title="拒绝原因" :visible.sync="rejectVisible" width="400px" :close-on-click-modal="false">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入拒绝原因" />
      <span slot="footer">
        <el-button @click="rejectVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitReject">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { promoterMerchantListApi, promoterMerchantAuditApi, promoterMerchantForceUnbindApi } from '@/api/lqq';

export default {
  name: 'PlatPromoterList',
  data() {
    return {
      listLoading: false,
      submitLoading: false,
      tableFrom: {
        page: 1,
        limit: 20,
      },
      tableData: {
        data: [],
        total: 0,
      },
      rejectVisible: false,
      rejectReason: '',
      rejectRow: null,
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    getList() {
      this.listLoading = true;
      promoterMerchantListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.data.list || [];
          this.tableData.total = res.data.total || 0;
          this.listLoading = false;
        })
        .catch(() => {
          this.listLoading = false;
        });
    },
    auditStatusText(status) {
      const map = { 0: '待审核', 1: '已通过', 2: '已拒绝' };
      return map[status] || '未知';
    },
    auditStatusType(status) {
      const map = { 0: 'warning', 1: 'success', 2: 'danger' };
      return map[status] || 'info';
    },
    handleAudit(row, auditStatus) {
      if (auditStatus === 2) {
        this.rejectRow = row;
        this.rejectReason = '';
        this.rejectVisible = true;
        return;
      }
      this.$confirm('确定通过该推广员绑定申请？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info',
      }).then(() => {
        promoterMerchantAuditApi({ id: row.id, auditStatus: 1 }).then(() => {
          this.$message.success('审核通过');
          this.getList();
        });
      }).catch(() => {});
    },
    submitReject() {
      if (!this.rejectReason.trim()) {
        this.$message.warning('请输入拒绝原因');
        return;
      }
      this.submitLoading = true;
      promoterMerchantAuditApi({
        id: this.rejectRow.id,
        auditStatus: 2,
        reason: this.rejectReason,
      }).then(() => {
        this.$message.success('已拒绝');
        this.rejectVisible = false;
        this.submitLoading = false;
        this.getList();
      }).catch(() => {
        this.submitLoading = false;
      });
    },
    handleForceUnbind(row) {
      this.$confirm(`确定强制解绑推广员(UID: ${row.uid})与商户(ID: ${row.merId})的绑定关系？`, '警告', {
        confirmButtonText: '确定解绑',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        promoterMerchantForceUnbindApi(row.id).then(() => {
          this.$message.success('已解绑');
          this.getList();
        });
      }).catch(() => {});
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
.danger-text {
  color: #f56c6c;
}
</style>
