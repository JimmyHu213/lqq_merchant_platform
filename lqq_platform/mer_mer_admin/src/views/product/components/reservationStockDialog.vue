<template>
  <div>
    <el-dialog
      title="可约数量设置"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      :before-close="handleClose"
      width="700px"
    >
      <el-table
        :data="reservationDataNew"
        :key="itemKey"
        max-height="500"
        style="width: 100%"
        :class="!isShow ? 'hide-first-row' : ''"
      >
        <el-table-column property="image" label="图片" width="150">
          <template slot-scope="scope">
            <div>
              <div
                v-if="scope.row.image"
                class="tabPic"
              >
                <img :src="scope.row.image" />
              </div>
              <div v-else>--</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          property="price"
          label="产品规格"
          width="200"
          v-show="specType"
        >
          <template slot-scope="scope">
            <div class="time-height acea-row row-middle mb10">
              {{ scope.row.attr_arr?scope.row.attr_arr[0]:'默认' }}
            </div>
          </template>
        </el-table-column>

        <el-table-column property="address" label="时间段">
          <template slot-scope="scope">
            <div
              v-for="(item, index) in scope.row.attrValueReservationList"
              :key="index"
              class="time-height acea-row row-middle mb10"
            >
              {{ item.timeSlot}}
            </div>
          </template>
        </el-table-column>

        <el-table-column label="预约数量">
          <template slot="header" slot-scope="scope">
            <span>预约数量</span>
            <el-popover placement="top" trigger="manual" v-model="visible">
              <div class="title">批量修改</div>
              <div class="flex">
                <el-input-number
                  v-model="batchStockValue"
                  :min="0"
                  :max="99999"
                  :controls="false"
                  :precision="0"
                  label="请输入预约数量"
                  style="width: 120px;"
                  size="small"
                  class="mr10"
                ></el-input-number>
                <el-button size="small" @click="close">取消</el-button>
                <el-button type="primary" size="small" @click="batchUpdateStock"
                  >确定</el-button
                >
              </div>
              <span
                slot="reference"
                class="el-icon-edit-outline"
                @click="visible = true"
              ></span>
            </el-popover>
          </template>

          <template slot-scope="scope">
            <div
              v-for="(item, index) in scope.row.attrValueReservationList"
              :key="index"
              class="mb10"
            >
              <el-input-number
                v-model="item.stock"
                :min="0"
                :max="99999"
                :precision="0"
                :controls="false"
                label="请输入预约数量"
                style="width: 120px;"
                size="small"
              ></el-input-number>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitOk" size="small"
          >确 定</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "reservationStockDialog",
  props: {
    dialogVisible: {
      type: Boolean,
      default: false
    },
    isShow: {
      type: Boolean,
      default: true
    },
    reservationData: {
      type: Array,
      default: () => []
    },
    specType: {
      // false:单规格  true:多规格
      type: Boolean,
      default: 0
    }
  },
  data() {
    return {
      visible: false,
      batchStockValue: 0,
      itemKey: 0,
      reservationDataNew: []
    };
  },
  watch: {
    dialogVisible(newVal) {
      if (newVal) {
        this.itemKey = Math.random();
        this.reservationDataNew = this.reservationData
      }
    }
  },
  methods: {
    handleClose() {
      this.$emit("close");
      this.visible = false;
    },
    close() {
      this.visible = false;
      this.batchStockValue = 0;
    },

    batchUpdateStock() {
      // 使用 for...of 循环替代 forEach，便于后续可能的提前终止操作
      for (const item of this.reservationDataNew) {
        for (const res of item.attrValueReservationList) {
          res.stock = this.batchStockValue;
        }
      }
      this.itemKey = Math.random();
      // 关闭批量修改弹窗并重置值
      this.close();
    },

    submitOk() {
      this.$emit("submitOk", this.reservationDataNew);
      this.visible = false;
    },

    handleStockInput(value, item) {
      let num = parseInt(value);
      if (isNaN(num) || num < 0) {
        item.stock = 0;
      } else if (num > 99999) {
        item.stock = 99999;
      } else {
        item.stock = num;
      }
    }
  }
};
</script>

<style scoped lang="scss">
.time-height{
  height: 32px;
}
.title {
  font-size: 13px;
  margin-bottom: 10px;
}
/* 隐藏表格的第一行 */
.hide-first-row {
  ::v-deep(.el-table__body tr:nth-child(1)) {
    display: none;
  }
}
</style>
