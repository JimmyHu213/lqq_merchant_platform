<template>
  <div>
    <el-drawer :visible.sync="drawerVisible" :direction="direction" size="1000px" :before-close="handleClose">
      <div v-loading="loading">
        <DetailHeader
          :title="`退款单号：${refundInfo.refundOrderNo}`"
          icon="icon-shouhou_tuikuan-2"
          colClass="color-warning"
          :titleLable="refundInfo.refundStatus | memRefundStatusFilter"
        >s
          <template slot="operation">
            <!-- 审核 -->
            <div
              v-if="refundInfo.refundStatus === 0 && checkPermi(['merchant:shipping:credits:refund:order:audit'])"
              class="acea-row row-center-wrappe mr14"
            >
              <el-button
                type="primary"
                v-debounceClick="
                  () => {
                    handleApprovedReview('success');
                  }
                "
                >{{ loadingBtn ? '提交中 ...' : '审核通过' }}</el-button
              >
              <el-button
                type="danger"
                v-debounceClick="
                  () => {
                    handleOrderRefuse('refuse');
                  }
                "
                >拒绝</el-button
              >
            </div>
          </template>
        </DetailHeader>
        <div class="detail-box">
          <DetailInfo :list="orderDetailList">
            <template slot="nickname">
              <span class="mr5">{{ refundInfo.nickname }}</span>
              <span class="mr5"> | </span>
              <span>{{ refundInfo.uid }}</span>
            </template>
          </DetailInfo>
        </div>
      </div>
    </el-drawer>
    <!-- 同意退款,退货退款-->
    <!--    <el-dialog-->
    <!--      title="同意退款"-->
    <!--      :visible.sync="dialogVisible"-->
    <!--      width="900px"-->
    <!--      :before-close="handleCloseAgreeToReturn"-->
    <!--      class="dialog-bottom"-->
    <!--    >-->
    <!--      <agree-to-return-->
    <!--        ref="agreeToReturn"-->
    <!--        @onHandleCancel="handleCloseAgreeToReturn"-->
    <!--        @onHandleSuccess="handleSuccess"-->
    <!--        :refundInfo="refundInfo"-->
    <!--        v-if="dialogVisible"-->
    <!--      ></agree-to-return>-->
    <!--    </el-dialog>-->
  </div>
</template>
<script>
import DetailHeader from '@/components/base/DetailHeader.vue';
import DetailInfo from '@/components/base/DetailInfo.vue';
import { filterEmpty, refundStatusFilter } from '@/filters';
import { checkPermi } from '@/utils/permission';
import { OrderSecondTypeEnum } from '@/enums/productEnums';
import { merchantShippingRefundInfoApi, merchantShippingRefundAuditApi } from '@/api/member';
import {onApprovedReview, onOrderRefuse} from './config';
export default {
  name: 'refundOrderDetail',
  props: {
    //退款单号
    refundOrderNo: {
      type: String,
      default: 0,
    },
    //是否显示隐藏
    drawerVisible: {
      type: Boolean,
      default: false,
    },
  },
  components: {
    DetailHeader,
    DetailInfo,
    // AgreeToReturn,
  },
  computed: {
    headerList() {
      return [
        {
          label: '退款状态',
          value: refundStatusFilter(this.refundInfo.refundStatus),
          color: 'color-warning',
        },
        {
          label: '退款金额',
          value: '¥ ' + (this.refundInfo.refundPrice || '0.0'),
        },
        {
          label: '实付金额',
          value: this.refundInfo.payPrice,
        },
        {
          label: '创建时间',
          value: this.refundInfo.orderInfoVo ? this.refundInfo.orderInfoVo.createTime : '',
        },
      ];
    },
    orderDetailList() {
      const info = this.refundInfo || {};
      const { filterEmpty } = this.$options.filters;
      const list = [
        {
          title: '用户信息',
          list: [
            { label: '用户昵称', slot: 'nickname' },
            { label: '用户电话', value: info.phone },
          ],
        },
      ];
      list.push({
        title: '申请信息',
        list: [
          { label: '退款金额', value: `￥${info.refundAmount}` },
          // { label: '退款后余额', value: filterEmpty(info.userPhone) },
          { label: '申请时间', value: info.createTime },
          { label: '退款原因', value: info.refundReason, colClass: 'width100' },
        ],
      });
      list.push({
        title: '退款信息',
        list: [
          { label: '审核人', value: info.auditName },
          { label: '审核时间', value: filterEmpty(info.auditTime) },
          { label: '拒绝原因', value: filterEmpty(info.refusingRefundReason), colClass: 'width100' },
        ],
      });
      return list;
    },
  },
  data() {
    return {
      activeName: 'refund',
      dialogVisible: false,
      loadingBtn: false,
      direction: 'rtl',
      reverse: true,
      orderDatalist: {},
      loading: false,
      modal2: false,
      result: [],
      resultInfo: {},
      refundInfo: {},
      OrderSecondTypeEnum,
    };
  },
  mounted() {
    this.getRefundOrderDetail(this.refundOrderNo);
  },
  methods: {
    checkPermi,
    //审核同意
    handleApprovedReview() {
      onApprovedReview.call(this, this.refundOrderNo);
    },
    //审核成功回调
    handleSuccess() {
      this.dialogVisible = false;
      this.getSuccessful();
    },
    //操作成功之后的回调，比如关闭弹窗，刷新列表等
    getSuccessful() {
      this.getRefundOrderDetail(this.refundOrderNo);
      this.$emit('getReviewSuccessful');
    },
    //同意弹窗
    handleCloseAgreeToReturn() {
      this.dialogVisible = false;
    },
    //审核拒绝
    handleOrderRefuse() {
      onOrderRefuse.call(this, this.refundOrderNo);
    },
    handleClose() {
      this.$emit('onClosedrawerVisible');
    },
    // 获取订单退款信息
    getRefundOrderDetail(id) {
      this.loading = true;
      merchantShippingRefundInfoApi(id)
        .then(async (res) => {
          this.refundInfo = res;
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
  },
};
</script>
<style scoped lang="scss">
.detail-box {
  padding: 0 35px;
}
.productName {
  width: 633px;
}
.detail-centent {
  margin-top: 16px;
}
::v-deep .el-step__main {
  margin-bottom: 30px !important;
}
::v-deep .el-step__title {
  font-size: 14px !important;
}
.flow-path {
  margin-bottom: 70px;
}
.refundReasonWap {
  width: 720px;
  height: auto;
  padding: 10px 25px 0 0;
  border-radius: 14px;
  background-color: #f3f8fe;
  overflow: hidden;
}
.image {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  overflow: hidden;
}
.refund {
  &-title {
    font-size: 17px;
    color: #333333;
    font-weight: 600;
  }
  &-orderNo {
    font-size: 14px;
    color: #333333;
  }
  &-price {
    margin-right: 100px;
  }
}
</style>
