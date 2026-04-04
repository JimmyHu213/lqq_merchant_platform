<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="直接退款"
    destroy-on-close
    :close-on-click-modal="false"
    width="1100px"
    @close="handlerClose"
    class="dialog-bottom"
  >
    <el-form ref="pram" size="small" :model="pram" label-width="95px" @submit.native.prevent>
      <el-form-item label="退款类型：" prop="returnType">
        <el-radio-group v-model="pram.returnType" :disabled="type == 2 || secondType == OrderSecondTypeEnum.PunchCard">
          <el-radio :label="1">整单退款</el-radio>
          <el-radio :label="2">分单退款</el-radio>
        </el-radio-group>
        <div v-show="pram.returnType === 2" class="from-tips">可选择下方表格中的商品进行退款，退款后不能撤回！</div>
      </el-form-item>
      <div v-if="pram.returnType === 2 && workOrderList.length" class="tableLeft mb20 font12">
        <div class="mb10">预约类型：{{ workOrderList[0].serviceType === 1 ? '上门服务' : '到店服务' }}</div>
        <div>
          预约信息：<span class="priceBox">{{ workOrderList[0].userName }} {{ workOrderList[0].userPhone }}</span>
          <span class="ml10">{{ workOrderList[0].userAddress }}</span>
        </div>
      </div>
      <el-table
        v-if="pram.returnType === 2"
        v-loading="loading"
        class="mb15 tableSelection tableLeft"
        style="width: 91%"
        ref="multipleSelection"
        :data="productList"
        border
        tooltip-effect="dark"
        size="mini"
        :row-key="
          (row) => {
            return row.id;
          }
        "
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          v-if="shouldShowColumn('workOrderList')"
          type="selection"
          :reserve-selection="true"
          min-width="50"
        />
        <el-table-column label="商品信息" width="200">
          <template slot-scope="scope">
            <div class="acea-row" style="align-items: center">
              <div class="demo-image__preview mr5 line-heightOne refundImg">
                <el-image :src="scope.row.image" :preview-src-list="[scope.row.image]" />
                <span class="priceBox product-info-text" style="width: 120px">{{ scope.row.productName }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="规格" min-width="60">
          <template slot-scope="scope">
            <span class="priceBox">{{ scope.row.sku }}</span>
          </template>
        </el-table-column>
        <el-table-column label="总数（件）" min-width="80">
          <template slot-scope="scope">
            <span class="priceBox">购买数量：{{ scope.row.payNum }}</span>
            (
            <span class="priceBox">已发货{{ scope.row.deliveryNum }}</span
            >)

            <div class="priceBox textE93323 mt3">
              可退款{{ scope.row.payNum - scope.row.refundNum - scope.row.applyRefundNum }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="可退总额（元）" min-width="60">
          <template slot-scope="scope">
            <span class="priceBox">{{ scope.row.payPrice - scope.row.refundPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="shouldShowColumn('workOrderList')" label="退款数量（件）" min-width="100">
          <template slot-scope="scope">
            <el-input-number
              v-model.trim="scope.row['num']"
              :min="1"
              :max="scope.row.payNum - scope.row.refundNum - scope.row.applyRefundNum"
              class="priceBox"
              :step="1"
              @change="limitCount(scope.row, scope.$index)"
            />
          </template>
        </el-table-column>
      </el-table>
      <!--  预约商品选择工单-->
      <el-table
        border
        v-if="secondType === OrderSecondTypeEnum.Reservation && pram.returnType === 2 && workOrderList.length"
        v-loading="loading"
        class="mb15 tableSelection tableLeft"
        style="width: 91%"
        ref="multipleSelection"
        :data="workOrderList"
        tooltip-effect="dark"
        size="mini"
        :row-key="
          (row) => {
            return row.id;
          }
        "
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" :reserve-selection="true" min-width="50" />
        <el-table-column label="工单号" prop="workOrderNo" min-width="200" />
        <el-table-column label="表单信息" min-width="250">
          <template slot-scope="scope">
            <ul v-if="scope.row.reservationFormData">
              <li class="item" v-for="(item, index) in JSON.parse(scope.row.reservationFormData)" :key="index">
                <system-from-info :item="item"></system-from-info>
              </li>
            </ul>
            <div v-else>--</div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 次卡商品预计退款金额 -->
      <template v-if="secondType === OrderSecondTypeEnum.PunchCard && productList[0]">
        <el-form-item label="剩余核销次数：">
          <div>{{ productList[0].verifyRemainingTimes }}/{{ productList[0].verifyTotalTimes }}</div>
        </el-form-item>
        <el-form-item label="预计退款金额：">
          <div>{{ productList[0].verifyRefundPrice }} 元</div>
          <div class="from-tips">具体退款金额以实际为准</div>
        </el-form-item>
      </template>
      <!-- 非次卡商品预计退款金额 -->
      <el-form-item v-else label="预计退款金额：" prop="resource">
        <div>{{ refundPrice.toFixed(2) }} 元</div>
        <div class="from-tips">具体退款金额以实际为准</div>
      </el-form-item>
    </el-form>
    <div class="acea-row row-right dialog-footer-inner dialog-btn-top">
      <el-button size="small" @click="handlerClose">取 消</el-button>
      <el-button size="small" type="primary" @click="handlerSubmit">确 定</el-button>
    </div>
  </el-dialog>
</template>
<script>
import { orderDirectRefundApi, orderProDetailApi, writeUpdateApi } from '@/api/order';
import { OrderSecondTypeEnum } from '@/enums/productEnums';
import systemFromInfo from '@/views/order/components/systemFromInfo';

export default {
  name: 'directRefund',
  components: {
    systemFromInfo,
  },
  props: {
    dialogVisibleDirectRefund: {
      type: Boolean,
      default: function () {
        return false;
      },
    },
    orderNo: {
      type: String,
      default: function () {
        return '';
      },
    },
    type: {
      type: Number,
      default: 0,
    },
    secondType: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      OrderSecondTypeEnum,
      pram: {
        returnType: 1,
        orderNo: '',
        detailList: [],
      },
      dialogVisible: this.dialogVisibleDirectRefund,
      productList: [],
      multipleSelection: [],
      refundPrice: 0, //预计退款金额
      loading: false,
      workOrderList: [], // 工单列表
    };
  },
  watch: {
    dialogVisibleDirectRefund: {
      handler: function (val) {
        this.dialogVisible = val;
      },
      deep: true,
    },
    'pram.returnType': {
      handler: function (val) {
        if (this.multipleSelection.length === 0 && val === 2) {
          this.refundPrice = 0;
        } else {
          this.multipleSelection = [];
          this.getRefundPrice(this.productList);
        }
      },
      deep: true,
    },
  },
  mounted() {
    this.orderProDetail();
  },
  methods: {
    shouldShowColumn(columnName) {
      // 检查至少有一行的该列值存在（非null、非undefined、非空字符串等）
      return this.productList.some((row) => {
        const value = row[columnName];
        return !value;
      });
    },
    //决定这一行的 CheckBox 是否可以勾选
    selectable(row, index) {
      if (row.deliveryNum === row.payNum) {
        return false;
      } else {
        return true;
      }
    },
    limitCount(row, i) {
      if (row.num > row.payNum) row.num = row.payNum;
      this.getRefundPrice(this.multipleSelection);
    },
    // 商品信息
    orderProDetail() {
      this.loading = true;
      orderProDetailApi(this.orderNo)
        .then(async (res) => {
          this.productList = res;
          this.productList.map((item) => this.$set(item, 'num', 1));
          this.workOrderList = res[0].workOrderList || [];
          this.getRefundPrice(this.productList);
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    // 获取一件的价格
    getOnePrice(currentValue) {
      return this.$selfUtil.Division(
        currentValue.payPrice - currentValue.refundPrice,
        currentValue.payNum - currentValue.refundNum - currentValue.applyRefundNum,
      );
    },
    //预计退款金额
    getRefundPrice(multipleSelection) {
      // 预计退款金额 = （可退总额/可退数量 * 退款数量）表格选中之和
      // 可退总额 = 购买金额-已退款金额，payPrice - refundPrice
      // 可退数量 = 购买数量-已退款数量-申请退款数量，payNum-refundNum-applyRefundNum
      // 可退总额/可退数量 onePrice = this.$selfUtil.Division(payPrice , refundPrice); //除法
      this.refundPrice = multipleSelection.reduce((accumulator, currentValue) => {
        let onePrice = this.getOnePrice(currentValue);
        //可退数量
        let refundNum =
          this.pram.returnType === 1
            ? currentValue.payNum - currentValue.refundNum - currentValue.applyRefundNum
            : currentValue.num;
        return accumulator + onePrice * refundNum;
      }, 0);
    },
    // 选择商品
    handleSelectionChange(val) {
      this.multipleSelection = val;
      let data = [];
      this.multipleSelection.map((item) => {
        data.push({
          id: item.id,
          num: this.secondType === OrderSecondTypeEnum.Reservation ? 1 : item.num,
        });
      });
      if (this.secondType === OrderSecondTypeEnum.Reservation) {
        this.getReservationRefundPrice(this.multipleSelection);
      } else {
        this.getRefundPrice(this.multipleSelection);
      }
    },
    // 计算预约退款价格
    getReservationRefundPrice(multipleSelection) {
      let onePrice = this.getOnePrice(this.productList[0]);
      this.refundPrice = onePrice * multipleSelection.length;
    },
    //提交
    handlerSubmit() {
      if (this.pram.returnType === 2 && this.multipleSelection.length === 0)
        return this.$message.warning('分单退款请选择商品！');
      this.pram.orderNo = this.orderNo;
      if (this.secondType !== OrderSecondTypeEnum.Reservation) {
        this.multipleSelection.map((item, index) => {
          this.pram.detailList.push({ num: item.num, orderDetailId: item.id });
        });
      } else {
        const workOrderIdList = this.multipleSelection.map((item) => item.id);
        this.pram.detailList.push({
          num: this.multipleSelection.length,
          orderDetailId: this.productList[0].id,
          workOrderIdList: workOrderIdList,
        });
      }
      orderDirectRefundApi(this.pram).then(() => {
        this.$message.success('操作成功');
        this.$emit('handlerSuccessSubmit');
      });
    },
    //取消
    handlerClose() {
      this.$emit('handlerSuccessClose');
    },
  },
};
</script>
<style scoped lang="scss">
::v-deep .el-input-number--medium {
  width: 170px !important;
}
.from-tips {
  margin-top: 5px !important;
}
.tableLeft {
  margin-left: 95px;
}
.refundImg {
  display: flex;
  align-items: center;
}
.product-info-text {
  display: block;
  white-space: nowrap; /* 确保文本在一行内显示 */
  overflow: hidden; /* 超出容器部分隐藏 */
  text-overflow: ellipsis; /* 使用省略号表示被截断的文本 */
  margin-left: 5px;
}
</style>
