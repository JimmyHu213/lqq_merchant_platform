<template>
  <div class="coupon-select borderPadding" v-if="configData">
    <div class="acea-row row-middle mb15">
      <div class="title labelwidth" v-if="configData.title">{{ configData.title }}</div>
      <el-button size="small" class="ml22" plain @click="handleAdd">{{ configData.btnTitle }}</el-button>
    </div>

    <div class="list-box">
      <div class="item" v-for="(item, index) in configData.list" :key="index">
        <div class="info" v-if="configData.type === 'coupon'">
          <div class="price line-heightOne"><span class="font12 fw-500">¥ </span>{{ item.money }}</div>
          <div class="condition">{{ item.minPrice > 0 ? '满' + item.minPrice + '可用' : '无门槛' }}</div>
        </div>
        <div class="info" v-else>
          <div class="price">
            <span class="font12 fw-500">充 </span><span class="line-heightOne">{{ item.rechargeAmount }}</span>
          </div>
          <div class="condition">送{{ item.giftAmount }}</div>
        </div>
        <div class="delete-btn" @click.stop="deleteCoupon(index)">
          <i class="el-icon-error"></i>
        </div>
      </div>
    </div>

    <!-- 选择弹窗 -->
    <el-dialog :visible.sync="dialogVisible" title="请选择" width="1200px" append-to-body :modal-append-to-body="false">
      <!-- 优惠券-->
      <all-coupon-list
        v-if="configData.type === 'coupon' && dialogVisible"
        :tableFrom="tableFrom"
        :showOperation="false"
        :showSelection="true"
        :checkedList="list"
        @changeSelection="changeSelection"
      ></all-coupon-list>
      <!-- 购物金 -->
      <shopping-credit
        v-if="configData.type === 'recharge' && dialogVisible"
        :checkedList="list"
        :showOperation="false"
        :showSelection="true"
        @changeSelection="changeSelection"
      ></shopping-credit>
      <div class="dialog-footer-inner mb20">
        <el-button size="small" @click="handleClose">取消</el-button>
        <el-button size="small" type="primary" @click="handleConfirm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import vuedraggable from 'vuedraggable';
import allCouponList from '@/components/allCouponList';
import shoppingCredit from '@/views/member/shoppingCredit/settings/index';
import * as $constants from '@/utils/constants';
const tableFroms = {
  page: 1,
  limit: $constants.page.limit[0],
  identityType: 1,
  status: 1,
};
export default {
  name: 'c_coupon_select',
  props: {
    configObj: {
      type: Object,
    },
    configNme: {
      type: String,
    },
  },
  components: {
    draggable: vuedraggable,
    allCouponList,
    shoppingCredit,
  },
  data() {
    return {
      tableFrom: Object.assign({}, tableFroms),
      defaults: {},
      configData: {},
      dialogVisible: false,
      keyNum: 0,
      list: [],
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.defaults = this.configObj;
      this.configData = this.configObj[this.configNme];
      this.getChecked()
    });
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.defaults = nVal;
        this.configData = nVal[this.configNme];
        this.getChecked()
      },
      deep: true,
    },
  },
  methods: {
    getChecked(){
      if (!this.configData.list.length){
        this.list = [];
      }else{
        this.list = this.configData.list
      }
    },
    handleClose() {
      this.dialogVisible = false;
    },
    handleConfirm() {
      this.configData.list = this.list;
      this.configData.ids = this.configData.list.map((item) => item.id).join(',');
      this.$emit('getConfig', this.list);
      this.dialogVisible = false;
    },
    handleAdd() {
      this.keyNum += 1;
      this.dialogVisible = true;
    },
    changeSelection(selection) {
      this.list = selection;
    },
    getCouponId(data) {
      // data 是选中的优惠券列表
      // 我们需要将选中的优惠券添加到 configData.list 中
      // 这里可能需要去重，或者直接覆盖，取决于需求。通常是追加或替换。
      // 假设是替换选中的集合 (CouponList logic usually returns all selected)
      // 但 CouponList 的 getCouponId 返回的是 multipleSelectionAll

      // 简单处理：将返回的数据映射为我们需要的数据格式
      // 注意：configData.list 中的数据结构应该包含 id, money, minPrice 等
      // 假设 CouponList 返回的对象已经包含了这些字段
      this.configData.list = data.map((item) => ({
        id: item.id,
        money: item.money,
        minPrice: item.minPrice,
        name: item.name,
        // 保留其他需要的字段
        ...item,
      }));
      this.dialogVisible = false;
    },
    deleteCoupon(index) {
      this.configData.list.splice(index, 1);
      this.configData.ids = this.configData.list.map((item) => item.id).join(',');
    },
  },
};
</script>

<style scoped lang="scss">
::v-deep.el-dialog__body {
  padding: 0 24px 0 24px !important;
}
.coupon-select {
  margin-bottom: 20px;

  .title {
    color: #999;
    font-size: 12px;
  }

  .list-box {
    .item {
      position: relative;
      display: flex;
      align-items: center;
      background: #f5f5f5;
      padding: 10px 12px;
      margin-bottom: 12px;
      border-radius: 4px;
      margin-left: 93px;

      .move-icon {
        cursor: move;
        margin-right: 10px;
        color: #ddd;
        .iconfont {
          font-size: 20px;
        }
      }

      .info {
        flex: 1;
        display: flex;
        align-items: center;

        .price {
          font-weight: 600;
          font-size: 16px;
          margin-right: 10px;
          color: #333;
        }

        .condition {
          font-size: 12px;
          color: #666;
        }
      }

      .delete-btn {
        cursor: pointer;
        position: absolute;
        right: -5px;
        top: -5px;
        .el-icon-error {
          font-size: 18px;
          color: #bbbbbb;
          background: #fff;
          border-radius: 50%;
        }
        &:hover .el-icon-error {
          color: #ff4949;
        }
      }
    }
  }
}
</style>
