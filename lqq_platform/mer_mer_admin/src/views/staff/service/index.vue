<template>
  <div class="divBox">
    <el-card
        :bordered="false"
        shadow="never"
        class="ivu-mt"
        :body-style="{
        padding: 0,
      }"
    >
      <div class="padding-add">
        <el-form :model="tableFrom" ref="searchForm" inline size="small">
          <el-form-item label="员工搜索：">
            <el-input
                v-model.trim="keywords"
                placeholder="请输入员工姓名或联系电话搜索"
                clearable
                class="form_content_width"
            />
          </el-form-item>
          <el-form-item label="员工状态：" prop="status">
            <el-select
                v-model="tableFrom.status"
                placeholder="请选择员工状态"
                clearable
                @change="getList(1)"
                class="selWidth"
            >
              <el-option v-for="item in optionsData" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="handleSearchList">搜索</el-button>
            <el-button size="small" @click="searchReset()">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="mt14">
      <el-button size="small" type="primary" class="mb20" @click="handleAdd">添加员工</el-button>
      <el-table v-loading="loading" :data="tableData.data" size="small">
        <el-table-column prop="id" label="ID" min-width="60" />
        <el-table-column prop="name" label="员工姓名" min-width="130" />
        <el-table-column label="证件照" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image class="tabImage" :src="scope.row.idPhoto" :preview-src-list="[scope.row.idPhoto]" />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="用户昵称" min-width="150">
          <template slot-scope="scope"> {{scope.row.nickName}} | {{scope.row.userId}} </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" min-width="130" />
        <el-table-column label="人员状态" min-width="100">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              active-text="开启"
              inactive-text="关闭"
              @click.native="onchangeIsShow(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" min-width="80" />
        <el-table-column prop="createTime" label="创建时间" min-width="150" />
        <el-table-column label="操作" min-width="100" fixed="right">
          <template slot-scope="scope">
            <a type="text" size="small" @click="handleEdit(scope.row)">编辑</a>
            <el-divider direction="vertical"></el-divider>
            <a type="text" size="small" @click="handleDel(scope.row.id)">删除</a>
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          background
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
        />
      </div>
    </el-card>

    <service-creat :edit-dialog-config="editDialogConfig" @hideEditDialog="hideEditDialog"></service-creat>
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
import { serviceStaffDelRoleApi, serviceStaffListApi, serviceStaffStatusApi } from '@/api/staff';
import serviceCreat from './serviceCreat';
import { handleDeleteTable } from '@/libs/public';
const optionsData = [
  {
    value: '1',
    label: '开启',
  },
  {
    value: '0',
    label: '关闭',
  },
];
export default {
  name: 'Service',
  components: { serviceCreat },
  data() {
    return {
      dialogTableVisible: false,
      optionsData: optionsData,
      loading: false,
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        page: 1,
        limit: 20,
        keyword: '',
        status: '',
      },
      keywords: '',
      uid: '',
      select: 'name',
      searchSelectList: [
        { label: '姓名', value: 'name' },
        { label: '用户ID', value: 'uid' },
        { label: '手机号', value: 'phone' },
      ],
      editDialogConfig: {
        visible: false,
        isCreate: 0, // 0=添加，1=编辑
        data: {},
      },
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    /**重置 */
    searchReset() {
      this.keywords = '';
      this.$refs.searchForm.resetFields();
      this.getList(1);
    },
    handleSearchList(data) {
      this.tableFrom = { ...this.tableFrom, ...data };
      this.getList(1);
    },
    // 列表
    getList(num) {
      this.loading = true;
      this.tableFrom.keywords = encodeURIComponent(this.keywords);
      this.tableFrom.page = num ? num : this.tableFrom.page;
      serviceStaffListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.loading = false;
        })
        .catch((res) => {
          this.$message.error(res.message);
          this.loading = false;
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
    // 添加
    handleAdd() {
      this.editDialogConfig.isCreate = 0;
      this.editDialogConfig.visible = true;
    },
    // 编辑
    handleEdit(row) {
      this.editDialogConfig.data = row;
      this.editDialogConfig.isCreate = 1;
      this.editDialogConfig.visible = true;
    },
    //关闭弹出框
    hideEditDialog(type, isCreate) {
      this.editDialogConfig.visible = false;
      if (type === 0) return;
      this.getList();
      isCreate == 0 && this.$message.success('添加成功');
      isCreate == 1 && this.$message.success('编辑成功');
    },
    handleDel(id) {
      this.$modalSure('该员工删除后，将无法服务工单').then(() => {
        serviceStaffDelRoleApi(id).then((res) => {
          this.$message.success('删除成功');
          handleDeleteTable(this.tableData.data.length, this.tableFrom);
          this.getList();
        });
      });
    },
    // 修改状态
    onchangeIsShow(row) {
      serviceStaffStatusApi(row.id)
        .then(() => {
          this.$message.success('修改成功');
          this.getList();
        })
        .catch(() => {
          row.status = !row.status;
        });
    },
  },
};
</script>

<style scoped lang="scss">
//
.tabImage {
  width: 36px;
  height: px;
}
</style>
