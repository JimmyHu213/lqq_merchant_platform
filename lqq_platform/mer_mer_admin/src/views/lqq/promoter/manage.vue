<template>
  <div class="divBox relative">
    <!-- 当前推广员信息 -->
    <el-card shadow="never" :bordered="false" class="ivu-mt" :body-style="{ padding: '20px' }">
      <div class="mb20">
        <span class="table-title">我的代理推广员</span>
      </div>
      <div v-loading="infoLoading">
        <template v-if="promoterInfo && promoterInfo.id">
          <el-descriptions :column="2" border size="medium">
            <el-descriptions-item label="推广员用户ID">{{ promoterInfo.uid }}</el-descriptions-item>
            <el-descriptions-item label="审核状态">
              <el-tag :type="auditStatusType(promoterInfo.auditStatus)" size="small">
                {{ auditStatusText(promoterInfo.auditStatus) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="佣金比例">{{ promoterInfo.commissionRate }}%</el-descriptions-item>
            <el-descriptions-item label="绑定时间">{{ promoterInfo.createTime }}</el-descriptions-item>
          </el-descriptions>
          <div class="action-row" v-if="promoterInfo.auditStatus === 1">
            <el-button type="primary" size="small" @click="showRateDialog">修改佣金比例</el-button>
            <el-button type="danger" size="small" @click="handleDismiss">解除代理</el-button>
          </div>
          <div class="action-row" v-else-if="promoterInfo.auditStatus === 0">
            <el-tag type="warning">等待平台审核中...</el-tag>
          </div>
          <div class="action-row" v-else-if="promoterInfo.auditStatus === 2">
            <el-tag type="danger">审核被拒绝</el-tag>
            <span class="reject-reason" v-if="promoterInfo.auditReason">原因：{{ promoterInfo.auditReason }}</span>
          </div>
        </template>
        <template v-else>
          <el-empty description="暂未绑定推广员">
            <el-button type="primary" size="small" @click="showInviteDialog">邀请推广员</el-button>
          </el-empty>
        </template>
      </div>
    </el-card>

    <!-- 邀请推广员弹窗 -->
    <el-dialog title="邀请推广员" :visible.sync="inviteVisible" width="450px" :close-on-click-modal="false">
      <el-form ref="inviteForm" :model="inviteForm" :rules="inviteRules" label-width="100px">
        <el-form-item label="用户ID" prop="uid">
          <el-input-number v-model="inviteForm.uid" :min="1" controls-position="right" placeholder="推广员用户ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="佣金比例(%)" prop="commissionRate">
          <el-input-number v-model="inviteForm.commissionRate" :min="0.1" :max="100" :precision="1" :step="0.5" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="inviteVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleInvite">提交邀请</el-button>
      </span>
    </el-dialog>

    <!-- 修改佣金比例弹窗 -->
    <el-dialog title="修改佣金比例" :visible.sync="rateVisible" width="400px" :close-on-click-modal="false">
      <el-form label-width="100px">
        <el-form-item label="佣金比例(%)">
          <el-input-number v-model="newRate" :min="0.1" :max="100" :precision="1" :step="0.5" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="rateVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleUpdateRate">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { promoterInfoApi, promoterInviteApi, promoterDismissApi, promoterUpdateRateApi } from '@/api/lqq';

export default {
  name: 'PromoterManage',
  data() {
    return {
      infoLoading: false,
      submitLoading: false,
      promoterInfo: null,
      inviteVisible: false,
      rateVisible: false,
      newRate: 5,
      inviteForm: {
        uid: null,
        commissionRate: 5,
      },
      inviteRules: {
        uid: [{ required: true, message: '请输入推广员用户ID', trigger: 'blur' }],
        commissionRate: [{ required: true, message: '请输入佣金比例', trigger: 'blur' }],
      },
    };
  },
  mounted() {
    this.getInfo();
  },
  methods: {
    getInfo() {
      this.infoLoading = true;
      promoterInfoApi()
        .then((res) => {
          this.promoterInfo = res.data || null;
          this.infoLoading = false;
        })
        .catch(() => {
          this.infoLoading = false;
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
    showInviteDialog() {
      this.inviteForm = { uid: null, commissionRate: 5 };
      this.inviteVisible = true;
    },
    handleInvite() {
      this.$refs.inviteForm.validate((valid) => {
        if (!valid) return;
        this.submitLoading = true;
        promoterInviteApi(this.inviteForm)
          .then((res) => {
            this.$message.success(res.data.message || '邀请已提交');
            this.inviteVisible = false;
            this.submitLoading = false;
            this.getInfo();
          })
          .catch(() => {
            this.submitLoading = false;
          });
      });
    },
    handleDismiss() {
      this.$confirm('确定解除与该推广员的代理关系？解除后将不再分润。', '提示', {
        confirmButtonText: '确定解除',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        promoterDismissApi().then((res) => {
          this.$message.success(res.data.message || '已解除');
          this.getInfo();
        });
      }).catch(() => {});
    },
    showRateDialog() {
      this.newRate = this.promoterInfo.commissionRate || 5;
      this.rateVisible = true;
    },
    handleUpdateRate() {
      if (!this.newRate || this.newRate <= 0) {
        this.$message.warning('请输入有效的佣金比例');
        return;
      }
      this.submitLoading = true;
      promoterUpdateRateApi(this.newRate)
        .then((res) => {
          this.$message.success(res.data.message || '修改成功');
          this.rateVisible = false;
          this.submitLoading = false;
          this.getInfo();
        })
        .catch(() => {
          this.submitLoading = false;
        });
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
.action-row {
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.reject-reason {
  color: #f56c6c;
  font-size: 13px;
  margin-left: 8px;
}
</style>
