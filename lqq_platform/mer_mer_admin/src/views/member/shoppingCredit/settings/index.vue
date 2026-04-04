<template>
  <div class="divBox relative">
    <base-table
      ref="table"
      :loading="listLoading"
      :table-data="tableData"
      :columns="tableColumns"
      :showSelection="showSelection"
      :checked-list="checkedList"
      @switch-change="onchangeIsShow"
      @selection-change="selectionChange"
      :showPagination="false"
    >
      <!-- 使用table-header插槽放置添加单位按钮 -->
      <template v-if="showOperation" #table-header>
        <el-button
          class="mt20"
          v-hasPermi="['merchant:shipping:credits:package:add']"
          size="small"
          type="primary"
          @click="handleEdit(0)"
        >
          添加套餐
        </el-button>
      </template>
      <!-- 操作插槽 -->
      <template #operate="{ row }">
        <a v-hasPermi="['merchant:shipping:credits:package:update']" @click="handleEdit(1, row)">编辑</a>
        <el-divider direction="vertical"></el-divider>
        <a v-hasPermi="['merchant:shipping:credits:package:delete']" @click="handleDelete(row.id)">删除</a>
      </template>
    </base-table>
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
import { settingsTableColumns } from './config';
import { checkPermi } from '@/utils/permission';
import { creditsAddApi, creditsDeleteApi, creditsListApi, creditsUpdateApi, creditsUpdateShowApi } from '@/api/member';
import {couponTableColumns} from "@/components/allCouponList/config";

export default {
  name: 'index',
  props: {
    checkedList: {
      //选中的数据
      type: Array,
      default: () => [],
    },
    showOperation:{
      type: Boolean,
      default: true,
    },
    showSelection: {
      type: Boolean,
      default: false,
    }
  },
  data() {
    return {
      listLoading: false,
      tableColumns: [],
      tableData: {
        data: [],
        total: 0,
      },
      keyNum: 0
    };
  },
  mounted() {
    if (this.showOperation){
      this.tableColumns = settingsTableColumns
    }else{
      this.tableColumns = settingsTableColumns.slice(0, -2)
    }
    if (checkPermi(['merchant:shipping:credits:package:list'])) this.getList();
  },
  methods: {
    checkPermi,
    selectionChange(selection){
      this.$emit('changeSelection', selection);
    },
    // 修改状态
    async onchangeIsShow(row) {
      try {
        await creditsUpdateShowApi(row.id);
        this.$message.success('修改成功');
        this.getList();
      } catch (e) {
        row.showStatus = !row.showStatus;
      }
    },
    handleEdit(isCreate, editDate) {
      this.id = editDate ? editDate.id : 0;
      this.$modalParserFrom(
        isCreate === 0 ? '添加充值套餐' : '编辑充值套餐',
        'rechargePackage',
        isCreate,
        isCreate === 0
          ? {
              id: 0,
              giftAmount: null,
              sort: null,
              rechargeAmount: null,
              showStatus: 0,
            }
          : Object.assign({}, editDate),
        (formValue) => {
          this.submit(formValue);
        },
        (this.keyNum += 2),
      );
    },
    //表单提交
    submit(formValue) {
      const data = {
        ...formValue,
        id: this.id,
      };
      !this.id
        ? creditsAddApi(data)
            .then((res) => {
              this.$message.success('操作成功');
              this.$msgbox.close();
              this.getList();
            })
            .catch(() => {
              this.loading = false;
            })
        : creditsUpdateApi(data)
            .then((res) => {
              this.$message.success('操作成功');
              this.$msgbox.close();
              this.getList();
            })
            .catch(() => {
              this.loading = false;
            });
    },
    // 列表
    getList() {
      this.listLoading = true;
      creditsListApi()
        .then((res) => {
          this.tableData.data = res;
          this.listLoading = false;
        })
        .catch((res) => {
          this.listLoading = false;
        });
    },
    // 删除
    handleDelete(id) {
      this.$modalSure('您确定要删除此充值套餐吗？').then(() => {
        creditsDeleteApi(id).then(() => {
          this.$message.success('删除成功');
          this.getList();
        });
      });
    },
  },
};
</script>

<style scoped></style>
