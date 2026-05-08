<template>
  <view class="promoter-page" :data-theme="theme">
    <!-- 佣金统计 -->
    <view class="stats-card" v-if="stats">
      <view class="stats-header">
        <text class="title">推广员中心</text>
      </view>
      <view class="stats-body acea-row row-around">
        <view class="stat-item">
          <text class="stat-num">{{ stats.totalCommission || '0.00' }}</text>
          <text class="stat-label">累计佣金(元)</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ stats.agentTotal || '0.00' }}</text>
          <text class="stat-label">代理佣金</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ stats.referralTotal || '0.00' }}</text>
          <text class="stat-label">裂变佣金</text>
        </view>
      </view>
      <view class="stats-row acea-row row-around">
        <view class="stat-item">
          <text class="stat-num small">{{ stats.merchantCount || 0 }}</text>
          <text class="stat-label">绑定商铺</text>
        </view>
        <view class="stat-item" @click="goWithdraw">
          <text class="stat-num small">{{ userInfo.brokeragePrice || '0.00' }}</text>
          <text class="stat-label">可提现佣金 ></text>
        </view>
      </view>
    </view>

    <!-- 佣金状态卡片（冻结/可提现/已提现） -->
    <view class="commission-status-card">
      <view class="commission-status-item">
        <text class="cs-num frozen">{{ frozenAmount }}</text>
        <text class="cs-label">冻结中(元)</text>
        <text class="cs-hint">3天内到账</text>
      </view>
      <view class="commission-status-divider"></view>
      <view class="commission-status-item" @click="goWithdraw">
        <text class="cs-num available">{{ userInfo.brokeragePrice || '0.00' }}</text>
        <text class="cs-label">可提现(元)</text>
        <text class="cs-hint withdraw-btn">去提现</text>
      </view>
      <view class="commission-status-divider"></view>
      <view class="commission-status-item">
        <text class="cs-num settled">{{ settledAmount }}</text>
        <text class="cs-label">已提现(元)</text>
      </view>
    </view>

    <!-- 佣金明细切换 -->
    <view class="section-card">
      <view class="tab-header acea-row">
        <view class="tab-item" :class="{ active: activeTab === 'merchants' }" @click="activeTab = 'merchants'">
          <text>代理商铺</text>
        </view>
        <view class="tab-item" :class="{ active: activeTab === 'detail' }" @click="switchToDetail">
          <text>佣金明细</text>
        </view>
      </view>

      <!-- 绑定商铺列表 -->
      <view v-show="activeTab === 'merchants'">
        <view class="merchant-item" v-for="item in merchants" :key="item.id" @click="goMerchant(item)">
          <view class="mer-row acea-row row-between-wrapper">
            <view class="mer-info">
              <view class="mer-name">商户ID: {{ item.merId }}</view>
              <view class="commission-rate">
                <text class="label">佣金比例：</text>
                <text class="rate">{{ item.commissionRate }}%</text>
              </view>
              <view class="bind-time">绑定时间：{{ item.createTime }}</view>
            </view>
            <view class="status-col">
              <view class="status-tag approved" v-if="item.auditStatus === 1">已生效</view>
              <view class="status-tag pending" v-else-if="item.auditStatus === 0">审核中</view>
              <view class="status-tag rejected" v-else>已拒绝</view>
            </view>
          </view>
        </view>
        <emptyPage v-if="!merchants.length && !loading" title="暂无代理商铺~" mTop="10%"></emptyPage>
      </view>

      <!-- 佣金明细列表 -->
      <view v-show="activeTab === 'detail'">
        <view class="detail-item" v-for="item in commissionList" :key="item.id">
          <view class="detail-row acea-row row-between-wrapper">
            <view class="detail-left">
              <view class="detail-type">
                <text class="type-tag" :class="receiverTypeClass(item.receiverType)">{{ receiverTypeText(item.receiverType) }}</text>
              </view>
              <view class="detail-order">订单: {{ item.orderNo }}</view>
              <view class="detail-time">{{ item.createTime }}</view>
            </view>
            <view class="detail-right">
              <text class="detail-amount">+{{ item.amount }}</text>
              <text class="detail-status" :class="commissionStatusClass(item)">{{ commissionStatusText(item) }}</text>
            </view>
          </view>
        </view>
        <emptyPage v-if="!commissionList.length && !detailLoading" title="暂无佣金记录~" mTop="10%"></emptyPage>
      </view>
    </view>
  </view>
</template>

<script>
import { promoterMyMerchantsApi, promoterCommissionStatsApi, promoterCommissionDetailApi } from '@/api/lqq.js';
import { getCommissionInfo, getClosingRecordApi } from '@/api/user.js';
import emptyPage from '@/components/emptyPage.vue';
import { toLogin } from '@/libs/login.js';
import { mapGetters } from 'vuex';
let app = getApp();
export default {
  components: { emptyPage },
  data() {
    return {
      theme: app.globalData.theme,
      stats: null,
      merchants: [],
      loading: true,
      activeTab: 'merchants',
      // 佣金明细
      commissionList: [],
      detailLoading: false,
      detailPage: 1,
      detailLoadEnd: false,
      // 冻结/已提现金额
      frozenAmount: '0.00',
      settledAmount: '0.00',
      userInfo: {}
    };
  },
  computed: mapGetters(['isLogin']),
  onLoad() {
    if (!this.isLogin) {
      toLogin();
      return;
    }
    this.loadData();
  },
  onReachBottom() {
    if (this.activeTab === 'detail') {
      this.loadCommissionDetail();
    }
  },
  methods: {
    loadData() {
      this.loading = true;
      Promise.all([
        promoterCommissionStatsApi(),
        promoterMyMerchantsApi()
      ]).then(([statsRes, merchantsRes]) => {
        this.stats = statsRes.data || {};
        this.merchants = merchantsRes.data || [];
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
      // 获取用户佣金余额信息（利用已有的JAVA-MER分销接口）
      this.loadUserBrokerageInfo();
    },
    loadUserBrokerageInfo() {
      // 使用已有的 JAVA-MER 接口获取冻结佣金和已提现金额
      getCommissionInfo({ page: 1, limit: 1 }).then(res => {
        if (res.data) {
          this.frozenAmount = res.data.freezePrice || '0.00';
          this.settledAmount = res.data.settledCommissionPrice || '0.00';
          this.userInfo = res.data;
        }
      }).catch(() => {});
    },
    switchToDetail() {
      this.activeTab = 'detail';
      if (this.commissionList.length === 0 && !this.detailLoading) {
        this.loadCommissionDetail();
      }
    },
    loadCommissionDetail() {
      if (this.detailLoadEnd || this.detailLoading) return;
      this.detailLoading = true;
      promoterCommissionDetailApi({
        page: this.detailPage,
        limit: 20
      }).then(res => {
        let list = res.data || [];
        if (Array.isArray(res.data)) {
          this.commissionList = this.commissionList.concat(list);
          this.detailLoadEnd = list.length < 20;
        } else if (res.data && res.data.list) {
          this.commissionList = this.commissionList.concat(res.data.list);
          this.detailLoadEnd = this.detailPage >= (res.data.totalPage || 1);
        }
        this.detailPage++;
        this.detailLoading = false;
      }).catch(() => {
        this.detailLoading = false;
      });
    },
    receiverTypeText(type) {
      const map = {
        'LOCKED_MERCHANT': '锁客分润',
        'REFERRER': '裂变佣金',
        'REFERRER_PARENT': '代理佣金',
        'PROMOTER': '代理佣金',
        'PLATFORM': '平台'
      };
      return map[type] || type;
    },
    receiverTypeClass(type) {
      const map = {
        'LOCKED_MERCHANT': 'lock',
        'REFERRER': 'referral',
        'REFERRER_PARENT': 'agent',
        'PROMOTER': 'agent',
        'PLATFORM': 'platform'
      };
      return map[type] || '';
    },
    commissionStatusText(item) {
      if (item.isUnfrozen === 1) return '已到账';
      if (item.status === 0) return '待分账';
      if (item.status === 1) return '冻结中';
      if (item.status === 2) return '分账失败';
      if (item.status === 3) return '已解冻';
      return '';
    },
    commissionStatusClass(item) {
      if (item.isUnfrozen === 1 || item.status === 3) return 'settled';
      if (item.status === 1) return 'frozen';
      if (item.status === 2) return 'failed';
      return 'pending';
    },
    goMerchant(item) {
      uni.navigateTo({
        url: `/pages/merchant/home/index?id=${item.merId}`
      });
    },
    goWithdraw() {
      uni.navigateTo({
        url: '/pages/users/user_transferOut/index'
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.promoter-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}
.stats-card {
  background-color: #fff;
  margin: 20rpx 24rpx;
  border-radius: 16rpx;
  overflow: hidden;
  .stats-header {
    padding: 24rpx 30rpx;
    border-bottom: 1rpx solid #f0f0f0;
    .title {
      font-size: 34rpx;
      font-weight: bold;
      color: #282828;
    }
  }
  .stats-body {
    padding: 30rpx 0 20rpx;
  }
  .stats-row {
    padding: 0 0 30rpx;
  }
  .stat-item {
    text-align: center;
    .stat-num {
      font-size: 44rpx;
      font-weight: bold;
      @include main_color(theme);
      display: block;
      &.small {
        font-size: 36rpx;
      }
    }
    .stat-label {
      font-size: 24rpx;
      color: #999;
      margin-top: 6rpx;
      display: block;
    }
  }
}
.commission-status-card {
  background-color: #fff;
  margin: 0 24rpx 20rpx;
  border-radius: 16rpx;
  padding: 30rpx 0;
  display: flex;
  align-items: center;
  .commission-status-item {
    flex: 1;
    text-align: center;
    .cs-num {
      display: block;
      font-size: 36rpx;
      font-weight: bold;
      margin-bottom: 8rpx;
      &.frozen { color: #e6a23c; }
      &.available { color: #67c23a; }
      &.settled { color: #909399; }
    }
    .cs-label {
      display: block;
      font-size: 24rpx;
      color: #666;
      margin-bottom: 6rpx;
    }
    .cs-hint {
      display: block;
      font-size: 20rpx;
      color: #999;
    }
    .withdraw-btn {
      @include main_color(theme);
      font-weight: 600;
    }
  }
  .commission-status-divider {
    width: 1rpx;
    height: 60rpx;
    background-color: #f0f0f0;
  }
}
.section-card {
  background-color: #fff;
  margin: 20rpx 24rpx;
  border-radius: 16rpx;
  padding: 0 30rpx 24rpx;
  .tab-header {
    border-bottom: 1rpx solid #f0f0f0;
    .tab-item {
      padding: 24rpx 30rpx;
      font-size: 28rpx;
      color: #666;
      position: relative;
      &.active {
        color: #282828;
        font-weight: 600;
        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
          width: 48rpx;
          height: 4rpx;
          border-radius: 2rpx;
          @include main_bg_color(theme);
        }
      }
    }
  }
  .merchant-item {
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f8f8f8;
    .mer-info {
      flex: 1;
      .mer-name {
        font-size: 28rpx;
        font-weight: 600;
        color: #282828;
        margin-bottom: 8rpx;
      }
      .commission-rate {
        font-size: 26rpx;
        margin-bottom: 6rpx;
        .label { color: #999; }
        .rate {
          @include main_color(theme);
          font-weight: 600;
        }
      }
      .bind-time {
        font-size: 22rpx;
        color: #999;
      }
    }
    .status-col {
      .status-tag {
        font-size: 22rpx;
        padding: 4rpx 16rpx;
        border-radius: 16rpx;
        &.approved { color: #67c23a; background: #f0f9eb; }
        &.pending { color: #e6a23c; background: #fdf6ec; }
        &.rejected { color: #f56c6c; background: #fef0f0; }
      }
    }
  }
  .detail-item {
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f8f8f8;
    .detail-row {
      .detail-left {
        flex: 1;
        .detail-type {
          margin-bottom: 8rpx;
          .type-tag {
            font-size: 22rpx;
            padding: 2rpx 12rpx;
            border-radius: 12rpx;
            &.referral { color: #409eff; background: #ecf5ff; }
            &.agent { color: #67c23a; background: #f0f9eb; }
            &.lock { color: #e6a23c; background: #fdf6ec; }
            &.platform { color: #909399; background: #f4f4f5; }
          }
        }
        .detail-order {
          font-size: 24rpx;
          color: #666;
          margin-bottom: 4rpx;
        }
        .detail-time {
          font-size: 22rpx;
          color: #999;
        }
      }
      .detail-right {
        text-align: right;
        .detail-amount {
          display: block;
          font-size: 32rpx;
          font-weight: bold;
          @include main_color(theme);
          margin-bottom: 6rpx;
        }
        .detail-status {
          display: block;
          font-size: 22rpx;
          &.settled { color: #67c23a; }
          &.frozen { color: #e6a23c; }
          &.failed { color: #f56c6c; }
          &.pending { color: #909399; }
        }
      }
    }
  }
}
</style>
