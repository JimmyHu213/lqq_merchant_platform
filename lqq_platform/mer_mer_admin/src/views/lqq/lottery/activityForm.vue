<template>
  <div class="divBox relative">
    <el-card class="box-card" :body-style="{ padding: '30px' }" shadow="never" :bordered="false">
      <div class="mb20">
        <span class="form-title">{{ isEdit ? '编辑抽奖活动' : '创建抽奖活动' }}</span>
      </div>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px" size="small">
        <el-form-item label="活动名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入活动名称" maxlength="50" show-word-limit style="width: 400px" />
        </el-form-item>
        <el-form-item label="活动图片" prop="image">
          <el-input v-model="form.image" placeholder="请输入图片URL" style="width: 400px" />
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入活动规则描述" maxlength="500" show-word-limit style="width: 400px" />
        </el-form-item>
        <el-form-item label="奖品名称" prop="prizeName">
          <el-input v-model="form.prizeName" placeholder="请输入奖品名称" maxlength="100" show-word-limit style="width: 400px" />
        </el-form-item>
        <el-form-item label="奖品价值" prop="prizeValue">
          <el-input-number v-model="form.prizeValue" :min="0" :max="999999" :precision="2" :step="10" controls-position="right" />
          <span class="form-tip">元</span>
        </el-form-item>
        <el-form-item label="所需积分" prop="pointsCost">
          <el-input-number v-model="form.pointsCost" :min="1" :max="99999" />
        </el-form-item>
        <el-form-item label="开奖人数" prop="participantThreshold">
          <el-input-number v-model="form.participantThreshold" :min="2" :max="9999" />
          <span class="form-tip">参与人数达到此数量后自动开奖</span>
        </el-form-item>
        <el-form-item label="中奖名额" prop="winnerCount">
          <el-input-number v-model="form.winnerCount" :min="1" :max="9999" />
          <span class="form-tip">每期中奖人数</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">{{ isEdit ? '保存修改' : '提交创建' }}</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { lotteryCreateApi, lotteryUpdateApi, lotteryDetailApi } from '@/api/lqq';

export default {
  name: 'LotteryForm',
  data() {
    return {
      isEdit: false,
      submitting: false,
      form: {
        id: null,
        name: '',
        image: '',
        description: '',
        prizeName: '',
        prizeValue: null,
        pointsCost: 1,
        participantThreshold: 10,
        winnerCount: 1,
      },
      rules: {
        name: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
        prizeName: [{ required: true, message: '请输入奖品名称', trigger: 'blur' }],
        pointsCost: [{ required: true, message: '请输入所需积分', trigger: 'blur' }],
        participantThreshold: [{ required: true, message: '请输入开奖人数', trigger: 'blur' }],
        winnerCount: [{ required: true, message: '请输入中奖名额', trigger: 'blur' }],
      },
    };
  },
  mounted() {
    if (this.$route.params.id) {
      this.isEdit = true;
      this.loadDetail(this.$route.params.id);
    }
  },
  methods: {
    loadDetail(id) {
      lotteryDetailApi(id).then((res) => {
        const data = res.data;
        this.form = {
          id: data.id,
          name: data.name,
          image: data.image || '',
          description: data.description || '',
          prizeName: data.prizeName,
          prizeValue: data.prizeValue,
          pointsCost: data.pointsCost,
          participantThreshold: data.participantThreshold,
          winnerCount: data.winnerCount,
        };
      });
    },
    handleSubmit() {
      this.$refs.form.validate((valid) => {
        if (!valid) return;
        this.submitting = true;
        const api = this.isEdit ? lotteryUpdateApi : lotteryCreateApi;
        api(this.form)
          .then(() => {
            this.$message.success(this.isEdit ? '修改成功，需重新审核' : '创建成功，等待平台审核');
            this.$router.push('/lqq/lottery/list');
          })
          .catch(() => {
            this.submitting = false;
          });
      });
    },
  },
};
</script>

<style scoped>
.form-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.form-tip {
  margin-left: 12px;
  color: #909399;
  font-size: 12px;
}
</style>
