<template>
  <view>
    <!-- #ifdef MP || APP-PLUS -->
    <NavBar navTitle="工单详情" :iconColor="iconColor" :isShowMenu="false" :isBackgroundColor="false"
            :textColor="iconColor" :isScrolling="isScrolling" showBack>
    </NavBar>
    <!-- #endif -->
    <!-- 状态、备注 -->
    <OrderHeader :info="workOrderNoInfo" :remark="workOrderNoInfo.remark" @changeRemark="handleRemark('1')" title="工单"></OrderHeader>

    <view class="relative borderPad mt20" :class="btnConfig.length?'pb-120':'mb-20'">
      <!-- 地址信息 -->
      <OrderAddress v-if="workOrderNoInfo.serviceType === 1" :orderInfo="workOrderNoInfo"></OrderAddress>
      <!-- 商品信息、用户信息 -->
      <view class="borderPad bg--w111-fff borRadius14 mt20 pb-32">
        <OrderGoods v-if="workOrderNoInfo.orderDetail" :cartInfo="[workOrderNoInfo.orderDetail]" :orderInfo="workOrderNoInfo" :isShowBtn="false"></OrderGoods>
        <OrderTable :list="tableData" mini-gap :has-style="false"></OrderTable>
      </view>
      <!-- 系统表单信息 -->
      <view v-if="reservationFormData.length" class="bg--w111-fff borRadius14 mt20 pt-32">
        <view class="borderPad order-detail-table f-s-30 f-w-500" style="margin-bottom: -20rpx;">表单信息</view>
        <systemFromInfo :orderExtend="reservationFormData"></systemFromInfo>
      </view>
      <!-- 服务信息、打卡信息 -->
      <OrderTable :list="item.list" :title="item.title" v-for="(item, index) in tableList" :key="index">
      </OrderTable>

      <!-- 服务过程留凭信息 -->
      <view v-if="serviceEvidenceForm.length" class="bg--w111-fff borRadius14 mt20 pt-32 pb-32">
        <view class="borderPad order-detail-table f-s-30 f-w-500">服务过程留凭</view>
        <systemFromInfo :orderExtend="serviceEvidenceForm"></systemFromInfo>
      </view>
    </view>
    <!-- 操作按钮 -->
    <OrderBottomBar v-if="btnConfig.length" :config="btnConfig" @action="handleBottomBarAction"></OrderBottomBar>
    <PriceChange :change="change" :orderInfo="workOrderNoInfo" v-on:closechange="changeClose($event)"
                 v-on:savePrice="getRemark" :status="status" title="工单" :remark="workOrderNoInfo.remark"></PriceChange>
    <!-- 派单 改派 -->
    <OrderAssign ref="orderAssignRef" @refresh="handleGetEmployeeWorkOrderDetail"></OrderAssign>
  </view>
</template>

<script>
// #ifdef MP || APP-PLUS
import NavBar from "@/components/navBar.vue";
// #endif
import PriceChange from "../components/PriceChange";
import OrderHeader from "../components/OrderDetail/OrderHeader.vue";
import OrderTable from "../components/OrderDetail/OrderTable.vue";
import systemFromInfo from '@/components/systemFromInfo';
import OrderGoods from '@/components/orderGoods'
import { employeeWorkOrderMarkApi } from "./workOrder";
import OrderAddress from "../components/OrderAddress";
import {
  getTableList
} from "../order";
import { adminOrderBottomBar, handleAdminOrderBarAction } from "./config";
import OrderBottomBar from "../components/OrderBottomBar";
import {employeeWorkOrderDetailApi} from "../../../api/work";
import OrderAssign from "./components/orderAssign";

const BTN_EVENT = {
  SERVICE_RECORD: "serviceRecord",
  SIGN_IN: "signIn",
  COMPLETE: 'complete'
}
export default {
  name: "detail",
  components: {
    OrderBottomBar,
    OrderHeader,
    PriceChange,
    systemFromInfo,
    OrderAddress,
    OrderGoods,
    OrderTable,
    OrderAssign,
    // #ifdef MP || APP-PLUS
    NavBar,
    // #endif
  },
  data() {
    return {
      iconColor: '#FFFFFF',
      isScrolling: false,
      workOrderNo: '',
      workOrderNoInfo: {},
      status: '',
      change: false,
      tableData: [],
      reservationFormData: [], //系统表单数据
      serviceEvidenceForm: [] //服务留凭
    }
  },
  onPageScroll(e) {
    // #ifdef MP || APP-PLUS
    if (e.scrollTop > 50) {
      this.iconColor = '#333333';
      this.isScrolling = true;
    } else {
      this.iconColor = '#FFFFFF';
      this.isScrolling = false;
    }
    // #endif
  },
  computed: {
    tableList() {
      return getTableList(this.workOrderNoInfo);
    },
    // 底部操作按钮
    btnConfig() {
      return adminOrderBottomBar(this.workOrderNoInfo)
    },
  },
  onShow(){
    this.handleGetEmployeeWorkOrderDetail();
  },
  onLoad(options) {
    this.workOrderNo = options.workOrderNo;
  },
  methods: {
    // 工单详情
    async handleGetEmployeeWorkOrderDetail() {
      uni.showLoading({
        title: '加载中...'
      });
      const {
        data
      } = await employeeWorkOrderDetailApi(this.workOrderNo);
      this.workOrderNoInfo = data;
      this.getTableData();
      this.reservationFormData = data.reservationFormData ? [JSON.parse(data.reservationFormData)] :
          [] // 表单信息
      this.serviceEvidenceForm = data.serviceEvidenceForm ? [JSON.parse(data.serviceEvidenceForm)] :
          [] // 服务留凭
      uni.hideLoading();
    },
    // 联系人信息
    getTableData() {
      this.tableData = [{
        label: "订单号",
        value: this.workOrderNoInfo.orderNo,
        copy: true,
      },
        {
          label: "留言",
          value: this.workOrderNoInfo.userRemark,
          overflow: true,
        }
      ]
    },
    // 操作按钮回调
    async handleBottomBarAction(event) {
      let data = await handleAdminOrderBarAction(event, this.workOrderNo)
      // 改约
      if (data === 'revise_reservation' || data === 'Compulsory_completion') await this.handleGetEmployeeWorkOrderDetail()
      if (data === 'revise_dispatch' || data === 'dispatch') await this.handleOpenAssignPage(data)
    },
    // 派单 改派
    handleOpenAssignPage(isReassign) {
      this.$refs.orderAssignRef.openPanel(this.workOrderNo, isReassign=== 'revise_dispatch'? true :false);
    },
    // 备注
    handleRemark: function(status) {
      this.change = true;
      this.status = status;
    },
    async getRemark(opt) {
      if (!opt.remark) {
        return this.$util.Tips({
          title: '请输入备注'
        })
      } else {
        this.toMark(opt.remark)
      }
    },
    //备注
    toMark(remark) {
      employeeWorkOrderMarkApi({
        workOrderNo: this.workOrderNo,
        remark
      }).then(res => {
        res.code == 200 && (this.change = false);
        this.handleGetEmployeeWorkOrderDetail()
        return this.$util.Tips({
          title: '备注成功'
        })
      })
    },
    changeClose: function(msg) {
      this.change = msg;
    },
  },
}
</script>

<style scoped>
.pb-120 {
  padding-bottom: 120rpx;
}

.order-detail-table {
  color: #333333FF;
}
</style>