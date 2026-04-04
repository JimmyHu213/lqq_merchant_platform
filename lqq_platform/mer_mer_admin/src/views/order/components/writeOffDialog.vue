<template>
  <div>
    <el-dialog
      class="dialog-box"
      title="核销"
      v-if="writeOffData.infoResponseList"
      :visible.sync="dialogTableVisible"
      @submit.native.prevent
      @closed="closeDialog"
    >
      <div>
        <el-form label-width="70px">
          <el-form-item label="已核销：">
            <span>
              {{
                writeOffData.infoResponseList[0].verifyTotalTimes -
                writeOffData.infoResponseList[0].verifyRemainingTimes
              }}
            </span>
            <span>/</span>
            <span>{{ writeOffData.infoResponseList[0].verifyTotalTimes }}</span>
          </el-form-item>
          <el-form-item label="核销次数：">
            <el-input-number
              v-model="verifyCount"
              controls-position="right"
              class="ver-count-input"
              :step="1"
              step-strictly
              :min="1"
              :max="writeOffData.infoResponseList[0].verifyRemainingTimes"
            ></el-input-number>
          </el-form-item>
          <el-form-item>
            <div class="flex-right">
              <el-button size="small" @click="closeDialog">取消</el-button>
              <el-button type="primary" size="small" @click="writeOff">确认</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { writeUpdateApi } from '@/api/order';
import { Debounce } from '@/utils/validate';
export default {
  name: '',
  components: {},
  mixins: [],
  props: {
    writeOffData: {
      type: Object,
      default: () => {
        return {};
      },
    },
  },
  data() {
    return {
      dialogTableVisible: false, // 弹窗是否显示
      verifyCount: 1, // 核销次数
    };
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  destoryed() {},
  methods: {
    // 开启弹窗
    openDialog() {
      this.dialogTableVisible = true;
    },
    // 关闭弹窗
    closeDialog() {
      this.dialogTableVisible = false;
      this.verifyCount = 1
      // this.$emit('handleSearchList');
    },
    // 核销
    writeOff: Debounce(function () {
      {
        if (!this.verifyCount) {
          this.$message.error('核销次数不能为空！');
          return
        }
        const param = {
          orderNo: this.writeOffData.orderNo,
          verifyCount: this.verifyCount,
        };
        writeUpdateApi(param).then(() => {
          this.$message.success('核销成功');
          this.closeDialog();
          this.$emit('handleSearchList');
          this.verifyCount = 1;
        });
      }
    }),
  },
};
</script>
<style lang="scss" scoped>
.dialog-box {
  .ver-count-input {
    width: 100%;
  }
  ::v-deep .el-dialog {
    margin-top: 30vh !important;
    width: 410px;
  }
}
.flex-right {
  display: flex;
  justify-content: flex-end;
}
</style>
