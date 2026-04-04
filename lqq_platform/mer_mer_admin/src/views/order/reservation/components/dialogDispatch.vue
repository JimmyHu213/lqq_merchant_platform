<template>
  <div>
    <el-dialog title="选择服务人员" :visible.sync="dialogTableVisible" :before-close="handleClose" width="600px">
      <div class="mb10">
        员工搜索：
        <el-input
          v-model.trim="keywords"
          placeholder="请输入员工姓名或手机号搜索"
          clearable
          class="form_content_width"
        />
        <el-button type="primary" size="mini" class="ml20" @click="getSelectStaff">查询</el-button>
      </div>
      <el-table :data="staffList" height="400px">
        <el-table-column width="55">
          <template slot-scope="scope">
            <span v-if="staffId == scope.row.id" class="iconfont icon-xuanzhong11" @click="iconChange(scope.row.id)" />
            <span v-else class="iconfont icon-weixuan" @click="iconChange(scope.row.id)" />
          </template>
        </el-table-column>
        <el-table-column property="id" label="Id" min-width="80"> </el-table-column>
        <el-table-column property="name" label="服务人员" min-width="150"></el-table-column>
        <el-table-column property="phone" label="手机号码" min-width="150"></el-table-column>
      </el-table>
      <div class="mt10" slot="footer">
        <el-button @click="handleClose()" size="small">取消</el-button>
        <el-button type="primary" size="small" @click="submitOk()">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { serviceStaffListApi } from '@/api/staff';
import { workOrderAssignApi, workOrderReassignApi } from '@/api/reservation';
export default {
  props: {
    workOrderNoList: {
      type: Array,
      default: [],
    },
  },
  data() {
    return {
      keywords: '',
      staffList: [],
      staffId: '', // 服务人员id
      type: '',
      dialogTableVisible: false,
    };
  },
  methods: {
    // 选择服务人员
    async getSelectStaff() {
      let res = await serviceStaffListApi({
        page: 1,
        limit: 999,
        status: 1,
        keywords: encodeURIComponent(this.keywords),
      });
      this.staffList = res.list;
    },
    handleClose() {
      this.dialogTableVisible = false;
      this.staffId = '';
    },
    iconChange(id) {
      this.staffId = id;
    },
    submitOk() {
      if (!this.staffId) return this.$message.warning('请选择服务人员');
      this.$modalSure('确认派单给该服务人员吗？').then(() => {
        this.type === 1
          ? workOrderAssignApi({ serviceStaffId: this.staffId, workOrderNoList: this.workOrderNoList }).then((data) => {
              this.$message.success('派单成功');
              this.$emit('selectStaff');
              this.handleClose();
            })
          : workOrderReassignApi({ serviceStaffId: this.staffId, workOrderNoList: this.workOrderNoList }).then(
              (data) => {
                this.$message.success('改派成功');
                this.$emit('selectStaff');
                this.handleClose();
              },
            );
      });
    },
    openBox(type) {
      this.type = type;
      this.getSelectStaff();
      this.dialogTableVisible = true;
    },
  },
};
</script>
<style scoped lang="scss">
.icon-xuanzhong11 {
  cursor: pointer;
  font-size: 16px;
  margin-left: 10px;
  cursor: pointer;
  color: var(--prev-color-primary);
}
.icon-weixuan {
  cursor: pointer;
  font-size: 16px;
  margin-left: 10px;
  cursor: pointer;
  color: #cccccc;
}
</style>
