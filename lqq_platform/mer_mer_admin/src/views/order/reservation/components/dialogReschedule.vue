<template>
  <div>
    <el-dialog title="改约" :visible.sync="dialogTableVisible" width="600px">
      <div class="form-box">
        <div class="title mb20">预约信息</div>
        <el-table v-show="productList.length" :data="productList" size="small" class="mt20 mb20">
          <el-table-column label="商品信息" min-width="300">
            <template slot-scope="scope">
              <div class="tab">
                <div class="demo-image__preview">
                  <el-image :src="scope.row.image" :preview-src-list="[scope.row.image]" />
                </div>
                <div>
                  <div>{{ scope.row.productName }}</div>
                  <div class="line1 gary">规格：{{ scope.row.sku }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-form :model="form" ref="form" :rules="rules" label-width="auto">
          <el-form-item label="预约时间：" prop="reservationDate">
            <el-date-picker
              v-model="form.reservationDate"
              value-format="yyyy-MM-dd"
              type="date"
              format="yyyy-MM-dd"
              :picker-options="pickerOptions"
              placeholder="选择日期"
              style="width: 200px"
              class="mr10"
            >
            </el-date-picker>
            <el-time-picker
              is-range
              value-format="HH:mm"
              format="HH:mm"
              type="datetimerange"
              v-model="reservationTime"
              placement="bottom-end"
              placeholder="选择时间"
              :picker-options="pickerOptions"
              size="small"
              style="width: 200px"
              :clearable="false"
            />
          </el-form-item>
          <el-form-item v-if="productList.length" label="服务方式：" prop="serviceType">
            <el-radio-group v-model="form.serviceType" disabled>
              <el-radio :label="1">上门服务</el-radio>
              <el-radio :label="2">到店服务</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="productList.length" label="联系人：" prop="userName">
            <el-input v-model="form.userName" size="small" placeholder="请输入联系人" style="width: 300px"></el-input>
          </el-form-item>
          <el-form-item v-if="productList.length" label="联系电话：" prop="userPhone">
            <el-input
              v-model="form.userPhone"
              size="small"
              type="number"
              placeholder="请输入联系电话"
              style="width: 300px"
            ></el-input>
          </el-form-item>
          <el-form-item label="上门地址：" prop="userAddress" v-if="form.serviceType == 1 && productList.length">
            <el-input
              v-model="form.userAddress"
              size="small"
              placeholder="请输入上门地址"
              style="width: 300px"
            ></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogTableVisible = false">取消</el-button>
        <el-button type="primary" @click="submitFn">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { workOrderUpdateAgreementApi } from '@/api/reservation';

const cacheAddress = {};
export default {
  name: 'DialogReschedule',
  props: {
    orderDetailList: {
      // 订单详情
      type: Object,
      default: () => ({}),
    },
    //商品列表
    productList: {
      type: Array,
      default: () => [],
    },
    // 订单号
    workOrderNoList: {
      type: Array,
      default: () => [],
    },
    isBatch: {
      type: Boolean,
      default: false,
    }
  },
  data() {
    return {
      pickerOptions: {
        disabledDate(time) {
          return Date.now() - 8.64e7 > time.getTime();
        },
      },
      formInfoData: {},
      checkList: ['选中且禁用', '复选框 A'],
      form: {
        reservationDate: '',
        reservationTimeSlot: '',
        serviceType: 1,
        userName: '',
        userPhone: '',
        userAddress: '',
        workOrderNoList: [],
        isBatch: false
      },
      reservationTime: ["",""], //时间段
      requiredList: [], // 必填项数据
      formProps: {},
      citysData: [], // 所有省市区数据
      rules: {
        reservationDate: [{ required: true, message: '请选择日期', trigger: 'blur' }],
        userName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
        userPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
        userAddress: [{ required: true, message: '请输入上门地址', trigger: 'blur' }],
      },
      showPopover: false,
      dialogTableVisible: false,
      radio: null, // 初始化 radio 变量
    };
  },
  watch: {
    orderDetailList: {
      handler(val) {
        this.setForm(val);
      },
      deep: true,
    },
  },

  methods: {
    setForm(val) {
      this.form.reservationDate = val.reservationDate;
      this.reservationTime = val.reservationTimeSlot ? val.reservationTimeSlot.split('-') : '';
      this.form.serviceType = val.serviceType;
      this.form.userName = val.userName;
      this.form.userPhone = val.userPhone;
      this.form.userAddress = val.userAddress;
    },
    openBox() {
      // 获取整个省市区数据
      this.dialogTableVisible = true;
      if (this.orderDetailList) {
        this.setForm(this.orderDetailList);
      }
    },
    submitFn() {
      this.form.workOrderNoList = this.workOrderNoList;
      this.form.reservationTimeSlot = this.reservationTime.join('-');
      this.form.isBatch = this.isBatch
      this.$refs.form.validate((valid) => {
        if (valid) {
          workOrderUpdateAgreementApi(this.form).then((res) => {
            this.$message.success('改约成功');
            this.dialogTableVisible = false;
            this.$emit('selectStaff');
          });
        } else {
          return false;
        }
      });
    },
  },
};
</script>
<style scoped lang="scss">
.form-box {
  overflow-y: auto;
  scrollbar-width: none; /* firefox */
  -ms-overflow-style: none; /* IE 10+ */
  .label {
    .required {
      color: red;
      font-size: 13px;
      margin-right: 5px;
    }
  }
  .pictrue {
    width: 60px;
    height: 60px;
    border: 1px dotted rgba(0, 0, 0, 0.1);
    margin-right: 10px;
    position: relative;
    cursor: pointer;
    img {
      width: 100%;
      height: 100%;
    }
    .btndel {
      position: absolute;
      z-index: 1;
      width: 20px !important;
      height: 20px !important;
      left: 46px;
      top: -4px;
    }
  }
}
.title {
  padding-left: 10px;
  border-left: 3px solid var(--prev-color-primary);
  font-size: 15px;
  line-height: 15px;
  color: #303133;
}
.tab {
  display: flex;
  align-items: center;
  .el-image {
    width: 36px;
    height: 36px;
    margin-right: 10px;
  }
}
.plan-footer-one {
  position: relative;
  cursor: pointer;
  -webkit-appearance: none;
  background-color: #fff;
  background-image: none;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  color: #c0c4cc;
  display: inline-block;
  font-size: inherit;
  min-height: 32px;
  line-height: 30px;
  outline: none;
  font-size: 13px;
  padding: 0 10px;

  -webkit-transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
  transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
  width: 100%;
}
.el-icon-arrow-down {
  font-weight: 400;
  position: absolute;
  right: 10px;
  top: 8px;
}
.flex-box {
  display: flex;
}
.item-box {
  padding: 5px 10px;
  font-size: 13px;
  cursor: pointer;
}
</style>
