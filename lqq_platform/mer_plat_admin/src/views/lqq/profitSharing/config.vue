<template>
  <div class="divBox relative">
    <el-card class="box-card" :body-style="{ padding: '20px' }" shadow="never" :bordered="false">
      <div class="mb20">
        <span class="table-title">分账配置</span>
      </div>
      <el-form ref="configForm" :model="configForm" label-width="180px" v-loading="loading" style="max-width: 600px">
        <el-form-item label="首单系数">
          <el-input-number
            v-model="configForm.firstOrderCoefficient"
            :min="1"
            :max="10"
            :precision="1"
            :step="0.1"
            controls-position="right"
            style="width: 100%"
          />
          <div class="form-tip">首笔订单分账金额的放大倍数，默认 1.5</div>
        </el-form-item>
        <el-form-item label="裂变佣金比例(%)">
          <el-input-number
            v-model="configForm.referralCommissionRate"
            :min="0"
            :max="100"
            :precision="1"
            :step="0.5"
            controls-position="right"
            style="width: 100%"
          />
          <div class="form-tip">推荐人(裂变)获得的佣金比例，默认 5%</div>
        </el-form-item>
        <el-form-item label="佣金冻结天数">
          <el-input-number
            v-model="configForm.commissionFreezeDays"
            :min="0"
            :max="30"
            :precision="0"
            :step="1"
            controls-position="right"
            style="width: 100%"
          />
          <div class="form-tip">佣金到账后冻结天数，到期自动解冻到用户余额，默认 3 天</div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitLoading" @click="handleSave">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { configGetUniq, configSaveUniq } from '@/api/systemConfig';

const CONFIG_KEYS = {
  firstOrderCoefficient: 'lqq_first_order_coefficient',
  referralCommissionRate: 'lqq_referral_commission_rate',
  commissionFreezeDays: 'lqq_commission_freeze_days',
};

export default {
  name: 'PlatProfitSharingConfig',
  data() {
    return {
      loading: false,
      submitLoading: false,
      configForm: {
        firstOrderCoefficient: 1.5,
        referralCommissionRate: 5,
        commissionFreezeDays: 3,
      },
    };
  },
  mounted() {
    this.loadConfig();
  },
  methods: {
    loadConfig() {
      this.loading = true;
      const keys = Object.values(CONFIG_KEYS);
      Promise.all(keys.map((key) => configGetUniq({ key }).catch(() => ({ data: null }))))
        .then(([coefRes, rateRes, daysRes]) => {
          if (coefRes.data) this.configForm.firstOrderCoefficient = parseFloat(coefRes.data) || 1.5;
          if (rateRes.data) this.configForm.referralCommissionRate = parseFloat(rateRes.data) || 5;
          if (daysRes.data) this.configForm.commissionFreezeDays = parseInt(daysRes.data) || 3;
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    handleSave() {
      this.submitLoading = true;
      const tasks = [
        configSaveUniq({ key: CONFIG_KEYS.firstOrderCoefficient, value: String(this.configForm.firstOrderCoefficient) }),
        configSaveUniq({ key: CONFIG_KEYS.referralCommissionRate, value: String(this.configForm.referralCommissionRate) }),
        configSaveUniq({ key: CONFIG_KEYS.commissionFreezeDays, value: String(this.configForm.commissionFreezeDays) }),
      ];
      Promise.all(tasks)
        .then(() => {
          this.$message.success('分账配置保存成功');
          this.submitLoading = false;
        })
        .catch(() => {
          this.$message.error('保存失败');
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
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}
</style>
