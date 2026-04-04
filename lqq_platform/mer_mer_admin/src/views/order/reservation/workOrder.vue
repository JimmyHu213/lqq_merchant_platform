<template>
  <div class="divBox relative">
    <el-card
      :bordered="false"
      shadow="never"
      class="ivu-mt"
      :body-style="{ padding: 0 }"
      v-if="checkPermi(['merchant:workOrder:page:list'])"
    >
      <div class="padding-add">
        <el-form inline label-position="right" @submit.native.prevent>
          <el-form-item label="工单编号：" label-width="66px">
            <el-input
                v-model.trim="tableFrom.workOrderNo"
                placeholder="请输入工单号"
                class="form_content_width"
                size="small"
                @keyup.enter.native="handleSearchList"
                clearable
            >
            </el-input>
          </el-form-item>
          <el-form-item label="订单编号：" label-width="66px">
            <el-input
              v-model.trim="tableFrom.orderNo"
              placeholder="请输入订单号"
              class="form_content_width"
              size="small"
              @keyup.enter.native="handleSearchList"
              clearable
            >
            </el-input>
          </el-form-item>
          <el-form-item label="预约类型：">
            <el-select
              v-model="tableFrom.serviceType"
              clearable
              size="small"
              placeholder="请选择"
              class="form_content_width"
              @change="handleSearchList"
            >
              <el-option v-for="(item, i) in fromType" :key="i" :label="item.text" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="时间选择：">
            <el-date-picker
              v-model="timeVal"
              value-format="yyyy-MM-dd"
              format="yyyy-MM-dd"
              size="small"
              type="daterange"
              placement="bottom-end"
              placeholder="自定义时间"
              @change="onchangeTime"
              class="form_content_width"
            />
          </el-form-item>
          <el-form-item label="用户搜索：" label-for="nickname">
            <UserSearchInput v-model="tableFrom" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="handleSearchList">查询</el-button>
            <el-button size="small" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card
      shadow="never"
      :bordered="false"
      class="box-card mt16"
      :body-style="{ padding: '0 20px 20px', position: 'relative' }"
      v-if="checkPermi(['merchant:workOrder:status:num', 'merchant:workOrder:page:list'])"
    >
      <el-tabs class="list-tabs" v-model="tableFrom.status" @tab-click="handleSearchList">
        <el-tab-pane name="0" :label="`全部(${orderChartType.all || 0})`"></el-tab-pane>
        <el-tab-pane name="1" :label="`待领取(${orderChartType.unReceive || 0})`"></el-tab-pane>
        <el-tab-pane name="2" :label="`已领取(${orderChartType.received || 0})`"></el-tab-pane>
        <el-tab-pane name="3" :label="`服务中(${orderChartType.inService || 0})`"></el-tab-pane>
        <el-tab-pane name="4" :label="`服务结束(${orderChartType.endService || 0})`"></el-tab-pane>
        <el-tab-pane name="9" :label="`已退款(${orderChartType.refunded || 0})`"></el-tab-pane>
      </el-tabs>
      <div class="mt5">
        <el-dropdown size="small">
          <el-button :class="checkedIds.length > 0 ? '' : 'active'" :disabled="isBatch">
            批量设置<i class="el-icon-arrow-down el-icon--right"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <template>
              <el-dropdown-item
                v-if="tableFrom.status == 1 && checkPermi(['merchant:workOrder:assign']) && checkedIds.length > 0"
                @click.native="handleDispatch(1)"
                >派单</el-dropdown-item
              >
              <el-dropdown-item v-if="dispatch" @click.native="handleDispatch(2)">改派</el-dropdown-item>
              <el-dropdown-item v-if="rescheduling" @click.native="handleRescheduling">改约</el-dropdown-item>
            </template>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        size="mini"
        class="mt20"
        highlight-current-row
        :highlight-current-row="true"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"> </el-table-column>
        <el-table-column label="工单号" min-width="190">
          <template slot-scope="scope">
            <div class="acea-row">
              <span style="display: block" v-text="scope.row.workOrderNo" />
            </div>
            <div class="flex">
              <span class="colorPrompt" v-show="parseInt(scope.row.refundStatus) > 0" style="display: block">{{
                scope.row.refundStatus === 1 ? '退款审核中' : '已退款'
              }}</span>
            </div>
            <span v-show="scope.row.isUserDel" class="colorPrompt" style="display: block">用户已删除</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="订单号" min-width="190" />
        <el-table-column prop="userPhone" label="服务类型" min-width="80">
          <template slot-scope="scope">
            {{ scope.row.serviceType === 1 ? '上门服务' : '到店服务' }}
          </template>
        </el-table-column>
        <el-table-column label="服务状态" min-width="100">
          <template slot-scope="scope">
            <span v-if="scope.row.refundStatus === 0"
              class="statusBox"
              :style="{
                color: orderColorFilter(scope.row.serviceStatus),
                borderColor: orderColorFilter(scope.row.serviceStatus),
              }"
            >
              {{ scope.row.serviceStatus | serviceStatusFilter }}
            </span>
            <span v-else class="statusBox notStartTag">已退款</span>
          </template>
        </el-table-column>
        <el-table-column label="预约时间" min-width="160">
          <template slot-scope="scope">
            <span> {{ scope.row.reservationDate }} </span>
            <span class="ml10"> {{ scope.row.reservationTimeSlot }} </span>
          </template>
        </el-table-column>
        <el-table-column label="预约人名称" min-width="150" prop="userName" />
        <el-table-column prop="userPhone" label="预约人电话" min-width="120" />
        <el-table-column prop="userAddress" label="预约地址" min-width="200" :show-overflow-tooltip="true" />
        <el-table-column width="185" fixed="right" label="操作">
          <template slot-scope="scope">
            <a @click="onOrderDetails(scope.row.workOrderNo)" v-if="checkPermi(['merchant:workOrder:detail'])">详情 </a>
            <el-divider direction="vertical" v-if="scope.row.groupBuyRecordStatus != 0"></el-divider>
            <a @click="handleOrderMark(scope.row)" v-if="checkPermi(['merchant:workOrder:mark'])">工单备注 </a>
            <template v-if="scope.row.serviceStatus === 3 && checkPermi(['merchant:workOrder:forceFinish']) &&scope.row.refundStatus===0 ">
              <el-divider direction="vertical"></el-divider>
              <a @click="handleForceFinish(scope.row)">完成工单 </a>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          background
          :page-sizes="$constants.page.limit"
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
        />
      </div>
    </el-card>

    <!-- 派单 -->
    <dialogDispatch ref="dialogDispatchRef" @selectStaff="handleSearchList" :workOrderNoList="checkedIds" />

    <!-- 改约 -->
    <dialogReschedule ref="dialogRescheduleRef" @selectStaff="handleSearchList" :workOrderNoList="checkedIds" :isBatch="true"/>

    <!--详情-->
    <details-from ref="orderDetail" :workOrderNo="workOrderNo" @changeStaff="handleSearchList"/>
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

import detailsFrom from './components/workOrderDetail';
import { checkPermi } from '@/utils/permission';
import DirectRefund from '@/views/order/components/directRefund.vue';
import * as $constants from '@/utils/constants';
import { OrderSecondTypeEnum } from '@/enums/productEnums';
import { workOrderForceFinishApi, workOrderListApi, workOrderMarkApi, workOrderStatusNumApi } from '@/api/reservation';
import dialogDispatch from './components/dialogDispatch';
import dialogReschedule from './components/dialogReschedule';
let tableFroms = {
  status: '0',
  dateLimit: '',
  orderNo: '',
  page: 1,
  limit: $constants.page.limit[0],
  serviceType: '',
  searchType: 'all',
  content: '',
  workOrderNo: '',
};
export default {
  name: 'orderlistDetails',
  components: {
    dialogDispatch,
    dialogReschedule,
    DirectRefund,
    detailsFrom,
  },
  computed: {
    // 批量改派
    dispatch() {
      return (
        (this.tableFrom.status == 2 || this.tableFrom.status == 3) &&
        checkPermi(['merchant:workOrder:reassign']) &&
        this.checkedIds.length > 0
      );
    },
    // 批量改约
    rescheduling() {
      return (
        checkPermi(['merchant:workOrder:updateAgreement']) &&
        (this.tableFrom.status == 2 || this.tableFrom.status == 1) &&
        this.checkedIds.length > 0
      );
    },
    // 是否能批量操作
    isBatch() {
      return this.checkedIds.length > 0 && Number(this.tableFrom.status) > 0 && Number(this.tableFrom.status) < 4
        ? false
        : true;
    },
  },
  data() {
    return {
      workOrderNo: '',
      dialogVisible: false,
      tableData: {
        data: [],
        total: 0,
      },
      listLoading: false,
      tableFrom: Object.assign({}, tableFroms),
      orderChartType: {},
      timeVal: [],
      fromType: [
        { value: 1, text: '上门服务' },
        { value: 2, text: '到店服务' },
      ],
      selectionList: [],
      type: 0, //订单类型
      OrderSecondTypeEnum: OrderSecondTypeEnum,
      checkedIds: [], //选中的id
    };
  },
  mounted() {
    if (checkPermi(['mmerchant:workOrder:page:list'])) this.getList();
    if (checkPermi(['merchant:workOrder:status:num'])) this.getOrderStatusNum();
  },
  methods: {
    checkPermi,
    // 派单
    handleDispatch(type) {
      this.$refs.dialogDispatchRef.openBox(type);
    },
    // 改约
    handleRescheduling() {
      this.$refs.dialogRescheduleRef.openBox();
    },
    // 服务状态
    orderColorFilter(status) {
      const statusMap = {
        3: '#0FC6C2',
        1: '#FF7D00',
        2: '#3491FA',
        4: '#CCCCCC',
        9: '#F56464',
      };
      return statusMap[status];
    },
    handleReset() {
      this.tableFrom.type = '';
      this.tableFrom.dateLimit = '';
      this.tableFrom.orderNo = '';
      this.tableFrom.workOrderNo = '';
      this.tableFrom.page = 1;
      this.tableFrom.content = '';
      this.tableFrom.searchType = 'all';
      this.tableFrom.serviceType = ''
      this.selectChange();
    },
    handleSearchList() {
      this.tableFrom.page = 1;
      this.getList();
      this.getOrderStatusNum();
    },
    // 详情
    onOrderDetails(id) {
      this.workOrderNo = id;
      this.$refs.orderDetail.getDetail(id);
      this.$refs.orderDetail.dialogVisible = true;
    },
    handleClose() {
      this.dialogVisible = false;
    },
    // 备注
    handleOrderMark(row) {
      this.$modalPrompt('textarea', '备注', row.remark, '工单备注').then((V) => {
        workOrderMarkApi({ remark: V, workOrderNo: row.workOrderNo }).then(() => {
          this.$message.success('操作成功');
          this.getList();
        });
      });
    },
    // 完成工单
    handleForceFinish(row) {
      this.$modalSure('确认强制完成工单吗？').then(() => {
        workOrderForceFinishApi({ workOrderNoList: [row.workOrderNo] }).then((res) => {
          this.$message.success('完成工单成功');
          this.getList();
          this.getOrderStatusNum();
        });
      });
    },
    handleSelectionChange(val) {
      this.selectionList = val;
      const data = [];
      this.selectionList.map((item) => {
        data.push(item.workOrderNo);
      });
      this.checkedIds = data;
    },
    // 选择时间
    selectChange(tab) {
      this.timeVal = [];
      this.tableFrom.page = 1;
      this.getList();
      this.getOrderStatusNum();
    },
    // 具体日期
    onchangeTime(e) {
      this.timeVal = e;
      this.tableFrom.dateLimit = e ? this.timeVal.join(',') : '';
      this.tableFrom.page = 1;
      this.getList();
      this.getOrderStatusNum();
    },
    // 列表
    getList() {
      this.listLoading = true;
      workOrderListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.list || [];
          this.tableData.total = res.total;
          this.listLoading = false;
        })
        .catch(() => {
          this.listLoading = false;
        });
    },
    // 获取各状态数量
    getOrderStatusNum() {
      let data = Object.assign({}, this.tableFrom);
      delete data.page;
      delete data.limit;
      delete data.status;
      workOrderStatusNumApi(data).then((res) => {
        this.orderChartType = res;
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
  },
};
</script>
<style lang="scss" scoped>
.statusBox {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 4px;
  border: 1px solid #cccccc;
}
font {
  color: var(--prev-color-primary);
}
.el-table__body {
  width: 100%;
  table-layout: fixed !important;
}

.demo-table-expand {
  ::v-deep .label {
    width: 83px !important;
  }
}

.refunding {
  span {
    display: block;
  }
}

.el-icon-arrow-down {
  font-size: 12px;
}

.tabBox_tit {
  font-size: 12px !important;
  /*margin: 0 2px 0 10px;*/
  letter-spacing: 1px;
  /*padding: 5px 0;*/
  box-sizing: border-box;
}

.text_overflow {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 400px;
}

.pup_card {
  width: 200px;
  border-radius: 5px;
  padding: 5px;
  box-sizing: border-box;
  font-size: 12px;
  line-height: 16px;
}

.flex-column {
  display: flex;
  flex-direction: column;
}

.relative {
  position: relative;
}

.cell_ht {
  height: 50px;
  padding: 15px 20px;
  box-sizing: border-box;
  border-bottom: 1px solid #eeeeee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.check_cell {
  width: 100%;
  padding: 15px 20px 0;
}

::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
  color: #606266;
}
</style>
