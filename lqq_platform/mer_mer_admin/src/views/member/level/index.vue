<template>
  <div class="divBox relative">
    <el-card class="box-card mt14" :body-style="{ padding: 0 }" shadow="never" :bordered="false">
      <el-button
        size="small"
        class="mt20 ml20"
        type="primary"
        :disabled="tableData.data.length ===10 ? true :false"
        @click="handleEditLevel(0)"
        v-if="checkPermi(['merchant:member:level:add'])"
        >添加等级</el-button
      >
      <base-table ref="table" :loading="listLoading" :table-data="tableData" :columns="tableColumns" :showPagination="false">
        <!-- 等级权益插槽 -->
        <template #interests="{ row }">
          <div v-for="benefitId in row.benefits.split(',')" :key="benefitId" class="benefit-item">
            <el-image
              v-if="getBenefitById(benefitId) && getBenefitById(benefitId).selectedIcon"
              :src="getBenefitById(benefitId).selectedIcon"
              class="benefit-icon mr5"
            />
            <span class="benefit-text">{{ getBenefitById(benefitId) && getBenefitById(benefitId).name }}</span>
          </div>
        </template>

        <!-- 操作插槽 -->
        <template #operate="{ row,index }">
          <a v-if="checkPermi(['merchant:member:level:update'])" @click="handleEditLevel(1, row)">编辑</a>
          <template v-if="isShowEdit(index)">
            <el-divider direction="vertical"></el-divider>
            <a @click="handleDeleteLevel(row.id)">删除</a>
          </template>
        </template>
      </base-table>
    </el-card>
    <edit-level
      v-if="editDialogConfig.visible"
      :editDialogConfig="editDialogConfig"
      :level="tableData.data.length + 1"
      @update-success="getList"
      @update-close="handleClose"
    ></edit-level>
  </div>
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
import { checkPermi } from '@/utils/permission';
import { levelTableColumns } from '@/views/member/level/config';
import {memberBenefitsListApi, memberLevelDeleteApi, memberLevelListApi} from '@/api/member';
import EditLevel from '@/views/member/level/editLevel';
const configData = {
  benefitsList: [],
  benefits: '',
  couponIds: '',
  id: 0,
  level: 1,
  name: '',
  thresholdAmount: 0,
};
export default {
  name: 'index',
  components: { EditLevel },
  data() {
    return {
      listLoading: false,
      tableData: {
        data: [],
      },
      tableColumns: levelTableColumns,
      editDialogConfig: {
        visible: false,
        isCreate: 0, // 0=创建，1=编辑
        data: {},
      },
      benefitOptions: [],
    };
  },
  mounted() {
    if (checkPermi(['merchant:member:level:list'])) this.getList();
    this.getBenefits();
  },
  methods: {
    checkPermi,
    isShowEdit(index){
      return checkPermi(['merchant:member:level:delete']) && this.tableData.data.length-1 === index && index!==0
    },
    async getBenefits() {
      this.benefitOptions = await memberBenefitsListApi();
    },
    // 列表
    async getList() {
      try {
        let res = await memberLevelListApi();
        this.tableData.data = res;
        this.listLoading = false;
      } catch (e) {
        this.listLoading = false;
      }
    },
    handleClose() {
      this.editDialogConfig.visible = false;
    },
    handleEditLevel(isCreate, row) {
      this.editDialogConfig.isCreate = isCreate;
      this.editDialogConfig.data = row
        ? { ...row, benefitsList: row.benefits ? row.benefits.split(',').map(Number) : [] }
        : { ...configData, level: this.tableData.data.length + 1 };
      this.editDialogConfig.visible = true;
    },
    handleDeleteLevel(id) {
      this.$modalSure('删除会员等级会影响现有会员等级，请谨慎操作！').then(() => {
        memberLevelDeleteApi(id).then(() => {
          this.$message.success('删除成功');
          this.getList();
        });
      });
    },
    // 根据权益ID获取权益信息
    getBenefitById(benefitId) {
      return this.benefitOptions.find((item) => item.id == benefitId);
    },
  },
};
</script>

<style scoped>
.benefits-container {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.benefit-item {
  margin-bottom: 5px;
  display: flex;
  align-items: center;
}

.benefit-icon {
  width: 24px;
  height: 24px;
  object-fit: contain;
}

.benefit-text {
  font-size: 12px;
  color: #606266;
}

.no-benefits {
  font-size: 12px;
  color: #909399;
}
</style>
