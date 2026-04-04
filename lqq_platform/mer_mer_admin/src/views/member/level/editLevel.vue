<template>
  <el-drawer
    :visible.sync="editDialogConfig.visible"
    size="900px"
    class="dialog-bottom"
  >
    <div class="detailHead bdbtmSolid">
      <div class="acea-row row-between headerBox">
        <div class="full">
          <div class="order_icon"><span class="iconfont icon-shanghuliebiao"></span></div>
          <div class="text">
            <div class="acea-row">
              <div class="title mr10">
                {{ editDialogConfig.isCreate === 0 ? `添加会员等级-LV${form.level}` : `编辑会员等级-LV${form.level}` }}
              </div>
            </div>
          </div>
        </div>
        <div class="dialog-footer">
          <el-button size="small" @click="handleClose">取消</el-button>
          <el-button
            type="primary"
            size="small"
            v-hasPermi="['merchant:member:level:add', 'merchant:member:level:update']"
            v-debounceClick="saveForm"
            >保存</el-button
          >
        </div>
      </div>
    </div>
    <el-form label-width="95px" ref="form" class="mt20" :model="form" :rules="rules" @submit.native.prevent>
      <el-form-item label="等级名称：" prop="name">
        <el-input
          v-model="form.name"
          placeholder="请输入等级名称"
          class="selWidth"
          size="small"
          clearable
          maxlength="10"
        >
        </el-input>
      </el-form-item>
      <el-form-item v-if="form.level > 1" label="升级条件：" prop="thresholdAmount">
        <span class="font12 color-606266">当消费金额累计达到 </span>
        <el-input-number
          :min="0"
          :max="999999"
          type="number"
          :controls="false"
          v-model="form.thresholdAmount"
          :precision="2"
          :step="0.01"
          placeholder="请输入金额"
        ></el-input-number>
        <span class="font12 color-606266"> 元，可晋升为本等级。 </span>
      </el-form-item>
      <el-form-item label="等级权益：" prop="benefitsList">
        <el-checkbox-group v-model="form.benefitsList" @change="changeBenefits">
          <el-checkbox v-for="item in benefitOptions" :key="item.id" :label="item.id">
            <span>{{ item.name }}</span>
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item v-if="hasGiftBenefit" label="升级奖励：" prop="couponIds">
        <div class="reward-section">
          <el-button size="small" type="primary" @click="addCoupon">添加优惠券</el-button>
          <div class="from-tips">当用户达到此等级时，系统自动发送升级奖励中选中的优惠券。</div>
          <div class="coupon-list" v-if="tableData.data.length > 0">
            <base-table ref="table" :table-data="tableData" :columns="tableColumns" :showPagination="false"> </base-table>
          </div>
          <div class="no-coupon" v-else>暂无优惠券</div>
        </div>
      </el-form-item>
    </el-form>
  </el-drawer>
</template>

<script>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import { couponTableColumns } from '@/views/member/level/config';
import { memberBenefitsListApi, memberLevelAddApi, memberLevelUpdateApi } from '@/api/member';

export default {
  name: 'editLevel',
  props: {
    editDialogConfig: {
      type: Object,
      default: () => {
        return {};
      },
    },
    level: {
      type: Number,
      default: () => {
        return 1;
      },
    },
  },
  data() {
    return {
      tableColumns: couponTableColumns,
      tableData: {
        data: [],
      },
      form: this.editDialogConfig.data,
      rules: {
        name: [
          { required: true, message: '请输入等级名称', trigger: 'blur' },
          { maxlength: 10, message: '等级名称最多10个字符', trigger: 'blur' },
        ],
        thresholdAmount: [
          { required: true, message: '请输入升级金额', trigger: 'blur' },
          { type: 'number', min: 0.01, message: '升级金额必须大于0', trigger: 'blur' },
        ],
        benefitsList: [{ required: true, message: '请至少选择一个等级权益', trigger: 'change', type: 'array' }],
      },
      benefitOptions: [],
      hasGiftBenefit: false, // 判断是否有名称包含"入会/升级有礼"的权益
      giftBenefitId: 0, //入会升级有礼的id
    };
  },
  mounted() {
    this.getBenefits();
    this.getInfo()
    setTimeout(() => {
      this.changeBenefits();
    }, 100);
  },
  methods: {
    getInfo(){
      this.form.benefitsList = this.form.benefits ? this.form.benefits.split(',').map(Number) : [];
      this.tableData.data = this.editDialogConfig.data.couponList ? this.editDialogConfig.data.couponList : [];
    },
    handleClose() {
      this.$emit('update-close')
    },
    async getBenefits() {
      this.benefitOptions = await memberBenefitsListApi();
      const giftBenefit = this.benefitOptions.find((item) => item.name.includes('入会/升级有礼'));
      if (giftBenefit) {
        this.giftBenefitId = giftBenefit.id;
      }
    },
    changeBenefits(){
      // 获取所有选中的权益ID
      const selectedBenefitIds = this.form.benefitsList || [];
      // 判断selectedBenefitIds中是否包含this.giftBenefitId的值
      this.hasGiftBenefit = selectedBenefitIds.includes(this.giftBenefitId);
    },
    // 根据权益ID获取权益信息
    getBenefitById(benefitId) {
      return this.benefitOptions.find((item) => item.id == benefitId);
    },
    // 保存表单
    saveForm() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          // 处理表单数据
          const formData = {
            ...this.form,
            level: this.level,
            // 将权益数组转换为字符串
            benefits: this.form.benefitsList.join(','),
            // 将优惠券对象数组转换为优惠券ID数组
            couponIds: this.tableData.data.map((item) => item.id).join(','),
          };
          !this.form.id ? await memberLevelAddApi(formData) : await memberLevelUpdateApi(formData);
          this.$message.success('保存成功');
          this.editDialogConfig.visible = false;
          // 通知父组件刷新列表
          this.$emit('update-success');
        } else {
          return false;
        }
      });
    },

    // 取消表单
    cancelForm() {
      this.editDialogConfig.visible = false;
    },

    // 添加优惠券
    addCoupon() {
      this.$modalCoupon(
        'wu',
        (this.keyNum += 1),
          this.tableData.data,
        (row) => {
          this.tableData.data = row;
        },
        this.userIds,
        'member',
      );
    },

    // 删除优惠券
    removeCoupon(index) {
      this.form.couponIds.splice(index, 1);
    },
  },
};
</script>

<style scoped lang="scss">
::v-deep .el-card__body{
  padding: 0 !important;
}
.images {
  width: 45px;
  height: 45px;
}
::v-deep .el-form {
  padding-left: 30px;
}
.selWidth {
  width: 300px;
}

.from-ipt-width {
  width: 120px;
  display: inline-block;
  margin: 0 5px;
}

.benefit-list {
  display: flex;
  flex-wrap: wrap;
}

.benefit-item {
  width: 120px;
  height: 100px;
  margin-right: 20px;
  margin-bottom: 20px;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  cursor: pointer;
  text-align: center;
  transition: all 0.3s;
}

.benefit-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.benefit-icon {
  width: 40px;
  height: 40px;
  margin: 0 auto 10px;
  background-size: cover;
  background-position: center;
}

/* 权益图标样式，实际项目中应替换为真实图标 */
.icon-discount {
  background-color: #ff6b6b;
  border-radius: 50%;
}

.icon-recharge {
  background-color: #4ecdc4;
  border-radius: 50%;
}

.icon-exclusive {
  background-color: #45b7d1;
  border-radius: 50%;
}

.icon-gift {
  background-color: #ffe66d;
  border-radius: 50%;
}

.reward-section {
  padding-right: 20px;
}

.no-coupon {
  margin-top: 10px;
  text-align: center;
  color: #909399;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>

<style scoped></style>
