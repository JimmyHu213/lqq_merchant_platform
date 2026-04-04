<template>
  <div>
    <el-drawer ref="userDetailFrom" :visible.sync="dialogUserDetail" size="1200px" @close="handleClose">
      <div slot="title" class="demo-drawer_title"></div>
      <div class="demo-drawer__content">
        <div class="description" v-if="userDetailData">
          <div class="con-head">
            <img :src="userDetailData.avatar" alt="" />
            <span class="nickname">{{ userDetailData.nickname }}</span>
          </div>
          <div class="acea-row info-row">
            <div class="info-row-item">
              <div class="info-row-item-title">购物金余额</div>
              <div>充值金额：{{ userDetailData.rechargeAmount }}，赠送金额：{{ userDetailData.giftAmount }}</div>
            </div>
            <div class="info-row-item">
              <div class="info-row-item-title">余额状态</div>
              <div>{{ userDetailData.financialStatus === 0 ? '冻结' : '正常' }}</div>
            </div>
            <div class="info-row-item">
              <div class="info-row-item-title">会员等级</div>
              <div>{{ userDetailData.levelName }}</div>
            </div>
            <div class="info-row-item">
              <div class="info-row-item-title">累计消费金额/单数</div>
              <div>¥ {{ userDetailData.consumeTotalAmount }} / {{ userDetailData.consumeTotalOrderNum }} 单</div>
            </div>
            <!--            <div class="info-row-item">-->
            <!--              <div class="info-row-item-title">用户积分</div>-->
            <!--              <div>{{ userDetailData.integral }}</div>-->
            <!--            </div>-->
            <!--            <div class="info-row-item">-->
            <!--              <div class="info-row-item-title">用户购买次数</div>-->
            <!--              <div>{{ userDetailData.payCount }}</div>-->
            <!--            </div>-->
            <!--            <div class="info-row-item">-->
            <!--              <div class="info-row-item-title">连续签到天数</div>-->
            <!--              <div>{{ userDetailData.signNum }}</div>-->
            <!--            </div>-->
          </div>
          <!-- Tabs -->
          <el-tabs type="border-card" v-model="tabsVal">
            <!-- 用户信息 -->
            <el-tab-pane name="1" label="用户信息"></el-tab-pane>
            <!-- 余额变更 -->
            <el-tab-pane name="2" label="购物金变动"></el-tab-pane>
          </el-tabs>
          <div v-if="tabsVal == 1">
            <div class="user-info">
              <div class="section">
                <div class="section-hd">基本信息</div>
                <div class="section-bd">
                  <div class="item">
                    <div>用户电话：</div>
                    <div class="value">{{ userDetailData.phone }}</div>
                  </div>
                  <div class="item">
                    <div>真实姓名：</div>
                    <div class="value">{{ userDetailData.realName || '-' }}</div>
                  </div>
                  <div class="item">
                    <div>用户账号：</div>
                    <div class="value">{{ userDetailData.account || '-' }}</div>
                  </div>
                  <div class="item">
                    <div>生日：</div>
                    <div class="value">{{ userDetailData.birthday || '-' }}</div>
                  </div>
                  <div class="item">
                    <div>性别：</div>
                    <div class="value">
                      {{
                        userDetailData.sex == 0
                          ? '未知'
                          : userDetailData.sex == 1
                          ? '男'
                          : userDetailData.sex == 2
                          ? '女'
                          : '保密'
                      }}
                    </div>
                  </div>
                  <div class="item">
                    <div>国家：</div>
                    <div class="value">{{ userDetailData.country == 'CN' ? '中国' : '其他' || '-' }}</div>
                  </div>
                  <div class="item">
                    <div>用户地址：</div>
                    <div class="value">
                      {{userDetailData.userAddress}}
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="user-info">
              <div class="section">
                <div class="section-hd-other">用户概览</div>
                <div class="section-bd">
                  <div class="item">
                    <div>首次访问：</div>
                    <div class="value">{{ userDetailData.firstVisitTime || '-' }}</div>
                  </div>
                  <div class="item">
                    <div>最后访问：</div>
                    <div class="value">{{ userDetailData.lastLoginTime || '-' }}</div>
                  </div>
                  <div class="item">
                    <div>入会时间：</div>
                    <div class="value">{{ userDetailData.membershipTime || '-' }}</div>
                  </div>
                  <div class="item">
                    <div>注册类型：</div>
                    <div class="value">
                      {{
                        userDetailData.registerType == 'wechat'
                          ? '公众号'
                          : userDetailData.registerType == 'routine'
                          ? '小程序'
                          : userDetailData.registerType == 'h5'
                          ? 'H5'
                          : userDetailData.registerType == 'iosWx'
                          ? '微信ios'
                          : userDetailData.registerType == 'androidWx'
                          ? '微信安卓'
                          : 'ios' || '-'
                      }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <base-table
            v-if="tabsVal == 2"
            ref="table"
            :table-data="tableData"
            :showPagination="false"
            :columns="shoppingCreditColumns"
          >
            <template #recharge="{ row }">
              <span
                >本金：<span :class="row.type === 1 ? 'color-red' : 'colorAuxiliary'"
                  >{{ row.type === 1 ? '+' : '-' }} {{ row.rechargeAmount }}</span
                ></span
              >
              <span class="ml10"
                >赠送：<span :class="row.type === 1 ? 'color-red' : 'colorAuxiliary'"
                  >{{ row.type === 1 ? '+' : '-' }} {{ row.giftAmount }}</span
                ></span
              >
            </template>
          </base-table>
        </div>
      </div>
    </el-drawer>
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
import { userDetailApi, shoppingCreditsRecordApi } from '@/api/user';
import { shoppingCreditColumns } from '@/views/user/list/default';

export default {
  name: 'detailUser',
  props: {
    userNo: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      shoppingCreditColumns,
      tabsVal: '1',
      dialogUserDetail: false,
      userDetailData: {},
      tableData: {
        data: [],
        total: 0,
      },
    };
  },
  methods: {
    handleClose() {
      this.dialogUserDetail = false;
      this.tabsVal = '1';
    },
    async getShoppingCreditsRecord(id) {
      let data = await shoppingCreditsRecordApi(id);
      this.tableData.data = data;
    },
    getUserDetail(id) {
      userDetailApi(id).then((res) => {
        this.userDetailData = res;
      });
    },
  },
};
</script>

<style scoped lang="scss">
.info-row-item {
  width: 288px !important;
}
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
.description-term img {
  width: 60px;
  height: 60px;
}
.description-term {
  display: flex;
  align-items: center;
}
.con-head {
  display: flex;
  align-items: center;
  img {
    width: 60px;
    height: 60px;
    margin-right: 15px;
    border-radius: 50%;
  }
  .nickname {
    font-weight: 500;
    font-size: 16px;
    line-height: 16px;
    color: rgba(0, 0, 0, 0.85);
  }
}
::v-deep .el-drawer__header {
  height: 0 !important;
  margin: 0 !important;
  padding: 15px !important;
}

.info-row {
  flex-wrap: nowrap;
  padding: 20px 35px 24px 0;

  &-item {
    flex: none;
    width: 155px;
    font-size: 14px;
    line-height: 14px;
    color: rgba(0, 0, 0, 0.85);

    &-title {
      margin-bottom: 12px;
      font-size: 13px;
      line-height: 13px;
      color: #666666;
    }
  }
}
.user-info {
  .section {
    padding: 25px 0;
    border-top: 1px dashed #eeeeee;

    &-hd {
      padding-left: 10px;
      border-left: 3px solid var(--prev-color-primary);
      font-weight: 500;
      font-size: 14px;
      line-height: 16px;
      color: #303133;
    }

    &-bd {
      display: flex;
      flex-wrap: wrap;
    }

    .item {
      width: 30%;
      display: flex;
      margin: 16px 30px 0 0;
      font-size: 13px;
      color: #606266;

      &:nth-child(3n + 3) {
        margin: 16px 0 0;
      }
    }

    .value {
      flex: 1;
    }
    .avatar {
      width: 60px;
      height: 60px;
      overflow: hidden;
      img {
        width: 100%;
        height: 100%;
      }
    }
  }
}
.section-hd-other {
  padding-left: 10px;
  border-left: 3px solid var(--prev-color-primary);
  font-weight: 500;
  font-size: 14px;
  line-height: 16px;
  color: #303133;
}
</style>
