<template>
  <div class="divBox">
    <el-card
      :bordered="false"
      shadow="never"
      class="ivu-mt"
      :body-style="{ padding: 0 }"
      v-if="checkPermi(['merchant:order:page:list'])"
    >
      <div class="padding-add">
        <el-form inline label-position="right" @submit.native.prevent>
          <el-form-item label="预约人员：" label-width="66px">
            <el-input
              v-model.trim="tableFrom.reservationKeyword"
              placeholder="请输入预约人员姓名电话搜索"
              class="form_content_width"
              size="small"
              @keyup.enter.native="handleSearchList"
              clearable
            >
            </el-input>
          </el-form-item>
          <el-form-item label="服务方式：">
            <el-select
              v-model="tableFrom.serviceType"
              placeholder="请选择服务方式"
              class="selWidth"
              clearable
              style="width: 200px"
              @change="handleSearchList"
            >
              <el-option v-for="item in serviceList" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="handleSearchList">查询</el-button>
            <el-button size="small" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="mt14 board-card">
      <!-- 预约服务展示面板 -->
      <fullCalendar ref="fullCalendar" v-model="tableFrom" @onOrderDetails="onOrderDetails" />
    </el-card>

    <!--详情-->
    <details-from ref="orderDetail" :workOrderNo="workOrderNo" />
  </div>
</template>
<script>
import detailsFrom from './components/workOrderDetail';
import fullCalendar from './components/fullCalendar';
import { checkPermi } from '@/utils/permission';
import { serviceStaffListApi } from '@/api/staff';
export default {
  name: '',
  components: { fullCalendar, detailsFrom },
  props: {},
  data() {
    return {
      tableFrom: {
        reservationDate: '',
        reservationKeyword: '',
        serviceType: '',
        serviceStatus: '2,3,4',
      },
      workOrderNo: '',
      drawer: false,
      select: '',
      staffList: [],
      serviceList: [
        {
          id: '1',
          name: '上门服务',
        },
        {
          id: 2,
          name: '到店服务',
        },
      ],
    };
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {
    this.getSelectStaff();
  },
  methods: {
    checkPermi,
    // 查询
    handleSearchList() {
      this.$refs.fullCalendar.getList();
      this.$refs.fullCalendar.getViewNum();
    },
    // 重置
    handleReset() {
      this.tableFrom.reservationKeyword = '';
      this.tableFrom.serviceType = '';
      this.tableFrom.searchType = 'all';
      this.tableFrom.content = ''
      this.handleSearchList();
    },
    // 选择服务人员
    async getSelectStaff() {
      let res = await serviceStaffListApi({
        page: 1,
        limit: 999,
        status: 1,
      });
      this.staffList = res.list;
    },
    onOrderDetails(id) {
      this.workOrderNo = id;
      this.$refs.orderDetail.getDetail(id);
      this.$refs.orderDetail.dialogVisible = true;
    },
    closeDrawer() {
      this.drawer = false;
    },
    changeDrawer(v) {
      this.drawer = v;
    },
    getSelectList(val) {
      this.tableFrom.nickname = val.nickname;
      this.tableFrom.phone = val.phone;
      this.handleSearchList();
    },
  },
};
</script>
<style scoped lang="scss"></style>
