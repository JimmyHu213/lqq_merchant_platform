<template>
  <div>
    <el-drawer :visible.sync="dialogVisible" :direction="direction" size="1000px" :show-close="false">
      <div v-if="orderDatalist" v-loading="loading">
        <div class="detailHead">
          <div class="acea-row row-between headerBox">
            <div class="full">
              <div class="order_icon"><span class="iconfont icon-dingdan"></span></div>
              <div class="text">
                <div class="title">
                  {{ orderDatalist.serviceType === 1 ? '上门服务' : '到店服务' }} --
                  {{ orderDatalist.allocateType === 0 ? '未分配' : orderDatalist.allocateType === 1 ? '派单' : '抢单' }}
                </div>
                <div>
                  <span class="mr20">工单号：{{ orderDatalist.workOrderNo }}</span>
                </div>
              </div>
            </div>
            <div v-show="orderDatalist.refundStatus===0" class="acea-row justify-content">
              <el-button size="small" v-if="orderDatalist.serviceStatus === 1 && checkPermi(['merchant:workOrder:assign'])" type="primary" @click="handleDispatch(1)">派单</el-button>
              <el-button size="small" v-if="(orderDatalist.serviceStatus === 2 || orderDatalist.serviceStatus === 3) && checkPermi(['merchant:workOrder:reassign'])" type="primary"  @click="handleDispatch(2)" style="margin-left: 0">改派</el-button>
              <el-button size="small" v-if="orderDatalist.serviceStatus < 3 && checkPermi(['merchant:workOrder:updateAgreement'])"  v-debounceClick="handleRescheduling" type="primary" style="margin-left: 0">改约</el-button>
            </div>
          </div>
          <ul class="list">
            <li class="item">
              <div class="title">工单状态</div>
              <div class="color-warning">
                <span v-if="orderDatalist.refundStatus === 2">已退款</span>
                <span v-else>{{ orderDatalist.serviceStatus | serviceStatusFilter }}</span>
              </div>
            </li>
            <li class="item">
              <div class="title">预约时间</div>
              <div>
                <span>{{ orderDatalist.reservationDate }}</span>
                <span class="ml10">{{ orderDatalist.reservationTimeSlot }}</span>
              </div>
            </li>
          </ul>
        </div>
        <el-tabs type="border-card" v-model="activeName">
          <el-tab-pane label="基本信息" name="detail">
            <div class="detailSection" style="border: none">
              <div class="title">预约信息</div>
              <ul class="list">
                <li class="item">
                  <div class="lang">联系人：</div>
                  <div class="value">{{ orderDatalist.userName }}</div>
                </li>
                <li class="item">
                  <div class="lang">联系电话：</div>
                  <div class="value">{{ orderDatalist.userPhone }}</div>
                </li>
                <li class="item">
                  <div class="lang">预约时间：</div>
                  <div class="value">
                    <span>{{ orderDatalist.reservationDate }}</span>
                    <span class="ml10">{{ orderDatalist.reservationTimeSlot }}</span>
                  </div>
                </li>
              </ul>
              <div v-show="orderDatalist.serviceType === 1" class="item">
                <div class="lang">上门地址：</div>
                <div class="value">
                  {{ orderDatalist.userAddress }}
                </div>
              </div>
            </div>
            <div class="detailSection">
              <div class="title">商品信息</div>
              <el-table class="mt20 orderDetailList" :data="productList" size="small">
                <el-table-column label="商品信息" min-width="350" :show-overflow-tooltip="true">
                  <template slot-scope="scope">
                    <div class="acea-row row-middle">
                      <div class="demo-image__preview mr15">
                        <el-image :src="scope.row.image" :preview-src-list="[scope.row.image]" />
                      </div>
                      <div style="width: 300px">
                        <div class="line1 mb10">{{ scope.row.productName }}</div>
                        <div class="line1 color-909399 line-heightOne">规格：{{ scope.row.sku }}</div>
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="商品售价" min-width="120">
                  <template slot-scope="scope">
                    <div class="acea-row row-middle">
                      <div class="line1">
                        {{ scope.row.price }}
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="购买数量" min-width="120">
                  <template slot-scope="scope">
                    <div class="acea-row row-middle">
                      <div class="line1">
                        {{ scope.row.payNum }}
                      </div>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div class="detailSection">
              <div class="title">买家留言</div>
              <ul class="list">
                <li class="item">
                  <div>{{ orderDatalist.userRemark | filterEmpty }}</div>
                </li>
              </ul>
            </div>
            <div class="detailSection">
              <div class="title">工单备注</div>
              <ul class="list">
                <li class="item">
                  <div>{{ orderDatalist.remark | filterEmpty }}</div>
                </li>
              </ul>
            </div>
            <div v-if="reservationFormData.length" class="detailSection">
              <div class="title">自定义留言</div>
              <ul class="">
                <li class="item" v-for="(item, index) in reservationFormData" :key="index">
                  <system-from-info :item="item"></system-from-info>
<!--                  <div class="lang" :title="item.title">{{ item.title }}</div>-->
<!--                  <div>{{ item.title.includes(':') ? '' : '：' }}</div>-->
<!--                  <div v-if="!Array.isArray(item.value)" class="value">{{ item.value | filterEmpty }}</div>-->
<!--                  <div v-else class="flex conter">-->
<!--                    <template v-if="item.value">-->
<!--                      <div v-for="(pic, idx) in item.value" :key="idx">-->
<!--                        <el-image v-if="pic.includes('http')" class="pictrue" :src="pic" :preview-src-list="[pic]" />-->
<!--                        <div v-else class="text-14px fontColor333 ml-5px acea-row row-middle mr5">-->
<!--                          {{ pic }}-->
<!--                          <div style="margin-left: 6px" v-show="idx < item.value.length - 1">-</div>-->
<!--                        </div>-->
<!--                      </div>-->
<!--                    </template>-->
<!--                    <template v-else> - </template>-->
<!--                  </div>-->
                </li>
              </ul>
            </div>
          </el-tab-pane>
          <el-tab-pane label="服务信息" name="goods">
            <div class="detailSection" style="border: none">
              <div class="title">服务信息</div>
              <ul class="list">
                <li class="item">
                  <div class="">服务人员：</div>
                  <div class="value">{{ orderDatalist.serviceStaff ? orderDatalist.serviceStaff.name : '-' }}</div>
                </li>
                <li class="item">
                  <div class="">联系电话：</div>
                  <div class="value">{{ orderDatalist.serviceStaff ? orderDatalist.serviceStaff.phone : '-' }}</div>
                </li>
                <li class="item">
                  <div class="">开始服务时间：</div>
                  <div class="value">
                    <span>{{ orderDatalist.serviceStartTime || '-' }}</span>
                  </div>
                </li>
                <li class="item">
                  <div class="">结束服务时间：</div>
                  <div class="value">
                    <span>{{ orderDatalist.serviceEndTime || '-' }}</span>
                  </div>
                </li>
                <li class="item">
                  <div class="">服务时长：</div>
                  <div class="value">
                    <span>{{ orderDatalist.serviceDuration }}</span>
                  </div>
                </li>
                <li v-if="orderDatalist.collaboratorStaffs && orderDatalist.collaboratorStaffs.length">
                  <div v-for="item in orderDatalist.collaboratorStaffs" :key="item.id" class="item">
                    <div class="">协作者：</div>
                    <div class="value">
                      <span>{{ item.name }}</span>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
            <div class="detailSection" v-if="orderDatalist.serviceType === 1">
              <div class="title">打卡信息</div>
              <ul class="list">
                <li class="item item100">
                  <div>打卡备注：</div>
                  <div class="value">
                    {{ orderDatalist.clockInRemark | filterEmpty }}
                  </div>
                </li>
                <li class="item item100">
                  <div>打卡照片：</div>
                  <template v-if="orderDatalist.clockInPhoto">
                    <div class="flex" v-for="(item, index) in orderDatalist.clockInPhoto.split(',')" :key="index">
                      <el-image
                        :src="item"
                        :preview-src-list="[item]"
                        style="width: 40px; height: 40px; margin-right: 12px"
                      />
                    </div>
                  </template>
                  <div v-else>--</div>
                </li>
              </ul>
            </div>
            <div class="detailSection" v-if="orderDatalist.serviceType === 1 && serviceEvidenceForm.length">
              <div class="title">服务过程留凭</div>
              <ul class="list">
                <li class="item" v-for="(item, index) in serviceEvidenceForm" :key="index">
                  <div class="lang" :title="item.title">{{ item.title }}</div>
                  <div>{{ item.title.includes(':') ? '' : '：' }}</div>
                  <div v-if="!Array.isArray(item.value)" class="value">{{ item.value | filterEmpty }}</div>
                  <div v-else class="flex conter">
                    <template v-if="item.value">
                      <div v-for="(pic, idx) in item.value" :key="idx">
                        <el-image v-if="pic.includes('http')" class="pictrue" :src="pic" :preview-src-list="[pic]" />
                        <div v-else class="text-14px fontColor333 ml-5px acea-row row-middle mr5">
                          {{ pic }}
                          <div style="margin-left: 6px" v-show="idx < item.value.length - 1">-</div>
                        </div>
                      </div>
                    </template>
                    <template v-else> - </template>
                  </div>
                </li>
              </ul>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-drawer>

    <!-- 派单 -->
    <dialogDispatch ref="dialogDispatchRef" @selectStaff="selectStaff" :workOrderNoList="[workOrderNo]"/>

    <!-- 改约 -->
    <dialogReschedule
        ref="dialogRescheduleRef"
        :orderDetailList="orderDatalist"
        :productList="productList"
        @selectStaff="selectStaff"
        :workOrderNoList="[workOrderNo]"
    />
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

import { orderRefundStatusFilter } from '@/filters';
import { checkPermi } from '@/utils/permission';
import { OrderSecondTypeEnum } from '@/enums/productEnums';
import { workOrderDetailApi } from '@/api/reservation'; // 权限判断函数
import dialogDispatch from "./dialogDispatch";
import dialogReschedule from "./dialogReschedule";
import systemFromInfo from "@/views/order/components/systemFromInfo";
export default {
  name: 'WorkOrderDetail',
  components: {
    dialogDispatch,
    dialogReschedule,
    systemFromInfo
  },
  props: {
    workOrderNo: {
      type: String,
      default: 0,
    },
  },
  data() {
    return {
      OrderSecondTypeEnum: OrderSecondTypeEnum,
      activeName: 'detail',
      direction: 'rtl',
      reverse: true,
      dialogVisible: false,
      orderDatalist: {},
      loading: false,
      result: [],
      resultInfo: {},
      InvoiceList: [],
      refundInfo: {},
      editData: {},
      reservationFormData: [], //系统表单数据,用户下单填写
      serviceEvidenceForm: [], //系统表单数据,用户下单填写
      expressName: '', //快递名称
      productList: [], //商品信息
    };
  },
  watch: {},
  mounted() {},
  methods: {
    checkPermi,
    // 派单
    handleDispatch(type){
      this.$refs.dialogDispatchRef.openBox(type, this.orderDatalist);
    },
    // 改约
    handleRescheduling(){
      this.$refs.dialogRescheduleRef.openBox();
    },
    // 服务时长
    getTimes() {
      let str = '';
      if (this.orderDatalist.serviceStartTime && this.orderDatalist.serviceEndTime) {
        let startTime = this.orderDatalist.serviceStartTime;
        let endTime = this.orderDatalist.serviceEndTime;
        const formatISOTime = (timeStr) => timeStr.replace(' ', 'T') + 'Z';
        const start = new Date(formatISOTime(startTime));
        const end = new Date(formatISOTime(endTime));

        // 计算时间差
        const diffMs = end - start;
        const diffMinute = Math.ceil(diffMs / 60000);
        return (diffMinute > 0 ? diffMinute : 1) + '分钟'; // 1分钟 = 60,000毫秒
      } else {
        return (str = '--');
      }
    },
    orderRefundStatusFilter,
    handleClose() {
      this.dialogVisible = false;
    },
    // 操作回调
    selectStaff() {
      this.getDetail(this.workOrderNo);
      this.$emit('changeStaff')
    },
    getDetail(id) {
      this.loading = true;
      workOrderDetailApi(id)
        .then((res) => {
          this.orderDatalist = res;
          this.reservationFormData = res.reservationFormData ? JSON.parse(res.reservationFormData) : [];
          this.serviceEvidenceForm = res.serviceEvidenceForm ? JSON.parse(res.serviceEvidenceForm) : [];
          this.activeName = 'detail';
          this.productList = res.orderDetail ? [res.orderDetail] : [];
          this.loading = false;
        })
        .catch(() => {
          this.orderDatalist = null;
          this.loading = false;
        });
    },
  },
};
</script>

<style scoped lang="scss">
::v-deep .el-drawer__header {
  display: flex !important;
  align-items: flex-start !important;
  padding: 15px 15px 0 15px !important;
  margin: 0 !important;
}
::v-deep .el-drawer__body {
  padding: 0 0 30px 0 !important;
}
::v-deep .demo-drawer_title {
  width: 90%;
}
::v-deep .el-tabs__content {
  padding: 0 20px !important;
}
.detailSection {
  padding: 25px 15px !important;
}
::v-deep .el-table th.el-table__cell > .cell,
::v-deep.el-table .cell,
.el-table--border .el-table__cell:first-child .cell {
  padding-left: 15px;
}
.InvoiceList {
  ::v-deep.el-collapse-item__header {
    font-size: 12px;
    color: #606266;
  }
}

.wrapper {
  background-color: #fff;
  margin-top: 7px;
  padding: 10px 12px;
  &-num {
    font-size: 10px;
    color: #999999;
  }

  &-title {
    color: #666666;
    font-size: 12px;
  }

  &-img {
    width: 60px;
    height: 60px;
    margin-right: 10px;
    border-radius: 7px;
    overflow: hidden;
    margin-bottom: 10px;

    image {
      width: 100%;
      height: 100%;
    }

    &:nth-child(5n) {
      margin-right: 0;
    }
  }
}

.title {
  font-size: 36px;
}

.demo-drawer__content {
  padding: 0 30px;
}

.demo-image__preview {
  display: inline-block;
  .el-image {
    width: 50px;
    height: 50px;
  }
}

.logistics {
  align-items: center;
  padding: 10px 0px;
  .logistics_img {
    width: 45px;
    height: 45px;
    margin-right: 12px;
    img {
      width: 100%;
      height: 100%;
    }
  }
  .logistics_cent {
    span {
      display: block;
      font-size: 12px;
    }
  }
}

.trees-coadd {
  width: 100%;
  height: 400px;
  border-radius: 4px;
  overflow: hidden;
  .scollhide {
    width: 100%;
    height: 100%;
    overflow: auto;
    margin-left: 18px;
    padding: 10px 0 10px 0;
    box-sizing: border-box;
    .content {
      font-size: 12px;
    }

    .time {
      font-size: 12px;
      color: var(--prev-color-primary);
    }
  }
}

.title {
  margin-bottom: 14px;
  color: #303133;
  font-weight: 500;
  font-size: 14px;
}

.description {
  &-term {
    display: table-cell;
    padding-bottom: 5px;
    line-height: 20px;
    width: 50%;
    font-size: 12px;
    color: #606266;
  }
  ::v-deep .el-divider--horizontal {
    margin: 12px 0 !important;
  }
}
</style>
