<template>
  <div class="divBox overflow-box">
    <base-table
        ref="table"
        :loading="listLoading"
        :table-data="tableData"
        :columns="tableColumns"
        :showPagination="false"
    >
      <!-- 使用table-header插槽放置添加单位按钮 -->
      <template #table-header>
        <el-button
            class="mt20"
            v-hasPermi="['merchant:product:unit:save']"
            size="small"
            type="primary"
            @click="handleEdit(0)"
        > 添加单位
        </el-button>
      </template>
      <!-- 操作插槽 -->
      <template #operate="{ row }">
        <a
            v-hasPermi="['merchant:product:unit:update']"
            @click="handleEdit(1, row)"
        >编辑</a
        >
        <el-divider direction="vertical"></el-divider>
        <a v-hasPermi="['merchant:product:unit:delete']" @click="handleDelete(row.id)"
        >删除</a
        >
      </template>
    </base-table>
  </div>
</template>
<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import {
  liveAssistantSaveApi,
  liveAssistantListApi,
  liveAssistantUpdateApi,
  liveAssistantDelApi,
} from '@/api/marketing';
import { checkPermi } from '@/utils/permission';
import { handleDeleteTable } from '@/libs/public';
import {productUnitDeleteApi, productUnitListApi, productUnitsaveApi, productUnitUpdateApi} from "@/api/product"; // 权限判断函数
export default {
  name: 'ProductUnit',
  data() {
    return {
      listLoading: true,
      tableData: {
        data: []
      },
      loading: false,
      keyNum: 0,
     tableColumns: [
        {
          prop: 'id',
          label: 'ID',
          minWidth: 150,
        },
        {
          label: '单位名称',
          minWidth: 150,
          prop: 'name'
        },
        {
          label: '创建时间',
          minWidth: 200,
          prop: 'createTime' // 使用插槽自定义显示（包含注销状态）
        },
        {
          // 操作列
          label: '操作',
          width: 100,
          fixed: 'right',
          slotName: 'operate' // 使用插槽自定义操作按钮
        }
      ]
    };
  },
  watch: {},
  mounted() {
    if (checkPermi(['merchant:product:unit:list'])) this.getList('');
  },
  methods: {
    checkPermi,
    handleEdit(isCreate, editDate) {
      const _this = this;
      this.id = editDate ? editDate.id : 0;
      this.$modalParserFrom(
          isCreate === 0 ? '新建单位' : '编辑单位',
          'productUnit',
          isCreate,
          isCreate === 0
              ? {
                id: 0,
                name: '',
                sort: '',
              }
              : Object.assign({}, editDate),
          function (formValue) {
            _this.submit(formValue);
          },
          (this.keyNum += 4),
      );
    },
    //表单提交
    submit(formValue) {
      const data = {
        id: this.id,
        name: formValue.name,
        sort: formValue.sort
      };
      !this.id
          ? productUnitsaveApi(data)
              .then((res) => {
                this.$message.success('操作成功');
                this.$msgbox.close();
                this.getList();
              })
              .catch(() => {
                this.loading = false;
              })
          : productUnitUpdateApi(data)
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
    getList(num) {
      this.listLoading = true;
      productUnitListApi(this.tableForm)
          .then((res) => {
            this.tableData.data = res;
            this.listLoading = false;
          })
          .catch((res) => {
            this.listLoading = false;
          });
    },
    // 删除
    handleDelete(id, idx) {
      this.$modalSure('删除商品单位吗？').then(() => {
        productUnitDeleteApi({id: id}).then(() => {
          this.$message.success('删除成功');
          handleDeleteTable(this.tableData.data.length, this.tableForm);
          this.getList('');
        });
      });
    },
  },
};
</script>
