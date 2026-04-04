<template>
  <view class="recharge-record-container" :data-theme="theme">
    <view v-for="(item, index) in records" :key="index">
      <!-- 充值记录列表 -->
      <line-table v-if="item" :item="item" :index="index" title="申请退款">
        <template #operation>
          <button v-if="item && item.refundStatus === 0" class="refund-btn text-333 f-s-24" @click="requestRefund(item)">申请退款</button>
        </template>
        <template #status>
          <text v-if="item && item.refundStatus>0">{{memberRefundStatusFilter(item.refundStatus)}}</text>
        </template>
        <!--      memberRefundStatusFilter(items.refundStatus)-->
        <!--      <template slot="status" slot-scope="{ items }">-->
        <!--        <text v-if="items && items.refundStatus>0">{{ items.refundStatus  }}</text>-->
        <!--      </template>-->
      </line-table>
    </view>

    
    <!-- 空状态提示 -->
    <template v-if="!loadOptions.loading">
      <view class="text-center order-list-empty mt20" v-if="loadOptions.loaded && records.length">
        暂无更多～
      </view>
      <view v-else-if="records.length === 0" class="nothing mt20">
        <emptyPage title="暂无充值记录~" mTop="25%" :imgSrc="urlDomain+'crmebimage/presets/nodingdan.png'" />
      </view>
    </template>
    <view class="list-bottom-tab-placeholder"></view>
    <bottom-button @handleConfirm="handleConfirm" title="退款记录" backgroundColor="#fff" widthNum="144rpx" type="short"></bottom-button>
    <view class="list-bottom-tab-placeholder"></view>
    <!-- 申请退款 -->
    <request-refund ref="requestRefundRef" :refundAmount="refundAmount" :orderNo="orderNo" @handleSubmit="handleForceRefetch"></request-refund>
  </view>
</template>

<script>
import BottomButton from "../../../components/bottomButton";
import {shoppingCreditsRecordApi} from "../memberApi";
import emptyPage from "../../../components/emptyPage";

import requestRefund from "../components/requestRefund";
import lineTable from "../../../subPackage/pages/lineTable";
let app = getApp();
export default {
  name: "index",
  components: {
    BottomButton,
    lineTable,
    emptyPage,
    requestRefund
  },
  data() {
    return {
      theme: app.globalData.theme,
      urlDomain: this.$Cache.get("imgHost"),
      records: [],
      loadOptions: {
        page: 1,
        limit: 20,
        total: 0,
        loading: false,
        loaded: false
      },
      orderNo: '',
      refundAmount: '',
      merId: 0
    }
  },
  async onPullDownRefresh() {
    await this.handleForceRefetch();
    uni.stopPullDownRefresh();
  },
  computed: {
    // 搜索条件
    queryParams() {
      const params = {
        merId: this.merId
      };
      return params;
    }
  },
  onLoad(options) {
    this.merId = Number(options.merId)
    this.getRechargeRecords();
  },
  methods: {
    // 会员购物金记录退款状态
    memberRefundStatusFilter(status) {
      const statusMap = {
        0: '未退款',
        1: '申请中',
        2: '已退款',
      }
      return statusMap[status]
    },
    // 申请退款
    requestRefund(item){
      this.orderNo = item.orderNo
      this.refundAmount = item.rechargeAmount
      this.$refs.requestRefundRef.open()
    },
    handleConfirm(){
      this.$util.navigateTo(`/pages/member/refund_record/index?merId=${this.merId}`)
    },
    handleForceRefetch() {
      this.handleResetLoadOptions();
      this.records = [];
      return this.getRechargeRecords();
    },
    handleResetLoadOptions() {
      this.loadOptions.page = 1;
      this.loadOptions.total = 0;
      this.loadOptions.loaded = false;
      this.loadOptions.loading = false;
    },
    // 列表
    async getRechargeRecords() {
      const { loading, loaded, page, limit } = this.loadOptions;
      if (loading || loaded) return;
      this.loadOptions.loading = true;
      try {
        const res = await shoppingCreditsRecordApi({
          ...this.queryParams,
          page,
          limit
        })
        this.$set(this.loadOptions, 'page', this.loadOptions.page + 1);
        const list = res.data.list.map(item => {
          return {
            ...item,
            details: [
              { key: 'orderNo', label: '充值单号', prefix: '' },
              { key: 'rechargeAmount', label: '充值本金', prefix: '¥' },
              { key: 'giftAmount', label: '赠送金额', prefix: '¥' },
              { key: 'payTime', label: '充值时间', prefix: '' }
            ]
          }
        })
        this.records.push(...list);
        this.loadOptions.total = res.data.total;
        this.loadOptions.loaded = this.records.length >= this.loadOptions.total;
      } catch (err) {
        this.$util.Tips({
          title: err,
          icon: "none"
        });
      }
      this.loadOptions.loading = false;
    }
  },
  onReachBottom() {
    this.getRechargeRecords();
  }
}
</script>

<style scoped>
/* 申请退款按钮 */
.refund-btn {
  padding: 12rpx 32rpx;
  border-radius: 30rpx;
  border: 1rpx solid #CCCCCC;
}
/* 页面容器 */
.recharge-record-container {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 30rpx;
}

</style>