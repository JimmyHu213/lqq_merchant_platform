<template>
  <view class="lottery-page" :data-theme="theme">
    <view class="header">
      <view class="title">抽奖活动</view>
    </view>
    <view class="list-box">
      <view class="activity-card" v-for="item in list" :key="item.id" @click="goDetail(item)">
        <view class="card-top acea-row row-between-wrapper">
          <view class="activity-name line1">{{ item.name }}</view>
          <view class="status-tag" :class="getStatusClass(item)">{{ getStatusText(item) }}</view>
        </view>
        <view class="card-body">
          <view class="info-row acea-row row-between-wrapper">
            <text class="label">奖品</text>
            <text class="value line1">{{ item.prizeName }}</text>
          </view>
          <view class="info-row acea-row row-between-wrapper">
            <text class="label">所需积分</text>
            <text class="value integral">{{ item.pointsCost }} 积分</text>
          </view>
          <view class="info-row acea-row row-between-wrapper">
            <text class="label">开奖人数</text>
            <text class="value">{{ item.participantThreshold }} 人</text>
          </view>
        </view>
        <view class="card-bottom acea-row row-between-wrapper">
          <text class="participants">第 {{ item.currentPeriod || 1 }} 期</text>
          <view class="join-btn bg-color" v-if="item.status === 1 && item.auditStatus === 1" @click.stop="joinLottery(item)">
            立即参与
          </view>
          <view class="join-btn disabled" v-else>
            {{ item.auditStatus !== 1 ? '审核中' : '已结束' }}
          </view>
        </view>
      </view>
    </view>
    <view class="loadingicon acea-row row-center-wrapper" v-if="list.length">
      <text class="loading iconfont icon-jiazai" :hidden="!loading"></text>{{ loadTitle }}
    </view>
    <emptyPage v-if="!list.length && !loading" title="暂无抽奖活动~" mTop="30%"></emptyPage>
  </view>
</template>

<script>
import { lotteryListApi, lotteryParticipateApi } from '@/api/lqq.js';
import emptyPage from '@/components/emptyPage.vue';
let app = getApp();
export default {
  components: { emptyPage },
  data() {
    return {
      theme: app.globalData.theme,
      list: [],
      page: 1,
      limit: 10,
      loading: false,
      loadend: false,
      loadTitle: ''
    };
  },
  onLoad() {
    this.getList();
  },
  methods: {
    getList() {
      if (this.loadend || this.loading) return;
      this.loading = true;
      lotteryListApi({ page: this.page, limit: this.limit }).then(res => {
        let newList = res.data.list || [];
        this.list = this.list.concat(newList);
        this.loadend = newList.length < this.limit;
        this.loadTitle = this.loadend ? '没有更多了' : '加载更多';
        this.page++;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
        this.loadTitle = '加载更多';
      });
    },
    goDetail(item) {
      uni.navigateTo({
        url: `/pages/activity/lottery_detail/index?id=${item.id}`
      });
    },
    joinLottery(item) {
      uni.showModal({
        title: '确认参与',
        content: `参与本次抽奖需消耗 ${item.pointsCost} 积分，确认参与？`,
        success: (res) => {
          if (res.confirm) {
            lotteryParticipateApi(item.id).then(() => {
              uni.showToast({ title: '参与成功', icon: 'success' });
            }).catch(err => {
              uni.showToast({ title: err || '参与失败', icon: 'none' });
            });
          }
        }
      });
    },
    getStatusClass(item) {
      if (item.auditStatus !== 1) return 'pending';
      return item.status === 1 ? 'active' : 'ended';
    },
    getStatusText(item) {
      if (item.auditStatus !== 1) return '审核中';
      return item.status === 1 ? '进行中' : '已结束';
    }
  },
  onReachBottom() {
    this.getList();
  }
};
</script>

<style lang="scss" scoped>
.lottery-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}
.header {
  padding: 30rpx;
  background-color: #fff;
  .title {
    font-size: 36rpx;
    font-weight: bold;
    color: #282828;
  }
}
.list-box {
  padding: 20rpx 24rpx;
}
.activity-card {
  background-color: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  .card-top {
    padding: 24rpx 24rpx 16rpx;
    border-bottom: 1rpx solid #f0f0f0;
    .activity-name {
      font-size: 32rpx;
      font-weight: 600;
      color: #282828;
      max-width: 480rpx;
    }
    .status-tag {
      font-size: 22rpx;
      padding: 4rpx 16rpx;
      border-radius: 20rpx;
      &.active {
        color: #fff;
        @include main_bg_color(theme);
      }
      &.ended {
        color: #999;
        background-color: #f0f0f0;
      }
      &.pending {
        color: #ff9900;
        background-color: #fff5e6;
      }
    }
  }
  .card-body {
    padding: 16rpx 24rpx;
    .info-row {
      padding: 8rpx 0;
      .label {
        font-size: 26rpx;
        color: #999;
        min-width: 130rpx;
      }
      .value {
        font-size: 26rpx;
        color: #282828;
        max-width: 480rpx;
        &.integral {
          @include main_color(theme);
          font-weight: 600;
        }
        &.time {
          font-size: 22rpx;
          color: #666;
        }
      }
    }
  }
  .card-bottom {
    padding: 16rpx 24rpx 24rpx;
    border-top: 1rpx solid #f0f0f0;
    .participants {
      font-size: 24rpx;
      color: #999;
    }
    .join-btn {
      font-size: 26rpx;
      color: #fff;
      padding: 10rpx 32rpx;
      border-radius: 30rpx;
      &.disabled {
        background-color: #ccc;
      }
    }
  }
}
</style>
