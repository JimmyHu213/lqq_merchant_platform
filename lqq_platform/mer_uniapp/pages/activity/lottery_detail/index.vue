<template>
  <view class="lottery-detail" :data-theme="theme">
    <view class="detail-card" v-if="activity">
      <view class="activity-header">
        <view class="name">{{ activity.name }}</view>
        <view class="status-tag" :class="activity.status === 1 ? 'active' : 'ended'">
          {{ activity.status === 1 ? '进行中' : '已结束' }}
        </view>
      </view>
      <view class="prize-section">
        <view class="section-title">奖品信息</view>
        <view class="prize-name">{{ activity.prizeName }}</view>
        <view class="prize-value" v-if="activity.prizeValue">价值：¥{{ activity.prizeValue }}</view>
      </view>
      <image class="activity-image" v-if="activity.image" :src="activity.image" mode="widthFix"></image>
      <view class="desc-section" v-if="activity.description">
        <view class="section-title">活动规则</view>
        <view class="desc-text">{{ activity.description }}</view>
      </view>
      <view class="info-section">
        <view class="info-item">
          <text class="label">所需积分</text>
          <text class="value highlight">{{ activity.pointsCost }} 积分</text>
        </view>
        <view class="info-item">
          <text class="label">开奖人数</text>
          <text class="value">{{ activity.participantThreshold }} 人</text>
        </view>
        <view class="info-item">
          <text class="label">中奖名额</text>
          <text class="value">{{ activity.winnerCount }} 人</text>
        </view>
        <view class="info-item">
          <text class="label">当前期号</text>
          <text class="value">第 {{ activity.currentPeriod || 1 }} 期</text>
        </view>
      </view>
      <view class="action-section">
        <view class="join-btn bg-color" v-if="activity.status === 1" @click="joinLottery">立即参与</view>
        <view class="join-btn disabled" v-else>活动已结束</view>
      </view>
    </view>

    <view class="participants-section" v-if="participants.length">
      <view class="section-header acea-row row-between-wrapper">
        <text class="section-title">参与者列表</text>
        <text class="count">共 {{ participants.length }} 人</text>
      </view>
      <view class="participant-item acea-row row-between-wrapper" v-for="item in participants" :key="item.id">
        <view class="user-info acea-row row-middle">
          <text class="nickname">{{ item.nickname || ('用户' + item.uid) }}</text>
        </view>
        <view class="result">
          <text v-if="item.isWinner === 1" class="winner">已中奖</text>
          <text v-else class="waiting">等待开奖</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { lotteryDetailApi, lotteryParticipateApi, lotteryParticipantsApi } from '@/api/lqq.js';
let app = getApp();
export default {
  data() {
    return {
      theme: app.globalData.theme,
      activityId: 0,
      activity: null,
      participants: []
    };
  },
  onLoad(options) {
    this.activityId = options.id;
    this.loadDetail();
    this.loadParticipants();
  },
  methods: {
    loadDetail() {
      lotteryDetailApi(this.activityId).then(res => {
        this.activity = res.data;
      });
    },
    loadParticipants() {
      lotteryParticipantsApi(this.activityId, { page: 1, limit: 50 }).then(res => {
        this.participants = res.data.list || [];
      });
    },
    joinLottery() {
      uni.showModal({
        title: '确认参与',
        content: `参与本次抽奖需消耗 ${this.activity.pointsCost} 积分，确认参与？`,
        success: (res) => {
          if (res.confirm) {
            lotteryParticipateApi(this.activityId).then(() => {
              uni.showToast({ title: '参与成功', icon: 'success' });
              this.loadDetail();
              this.loadParticipants();
            }).catch(err => {
              uni.showToast({ title: err || '参与失败', icon: 'none' });
            });
          }
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.lottery-detail {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx 24rpx;
}
.detail-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  .activity-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;
    .name {
      font-size: 36rpx;
      font-weight: bold;
      color: #282828;
    }
    .status-tag {
      font-size: 22rpx;
      padding: 6rpx 20rpx;
      border-radius: 20rpx;
      &.active {
        color: #fff;
        @include main_bg_color(theme);
      }
      &.ended {
        color: #999;
        background-color: #f0f0f0;
      }
    }
  }
  .prize-section {
    margin-bottom: 20rpx;
    padding-bottom: 20rpx;
    border-bottom: 1rpx solid #f0f0f0;
    .section-title {
      font-size: 26rpx;
      color: #999;
      margin-bottom: 10rpx;
    }
    .prize-name {
      font-size: 32rpx;
      color: #282828;
      font-weight: 600;
    }
    .prize-value {
      font-size: 26rpx;
      color: #e93323;
      margin-top: 6rpx;
    }
  }
  .activity-image {
    width: 100%;
    border-radius: 12rpx;
    margin-bottom: 20rpx;
  }
  .desc-section {
    margin-bottom: 20rpx;
    padding-bottom: 20rpx;
    border-bottom: 1rpx solid #f0f0f0;
    .section-title {
      font-size: 26rpx;
      color: #999;
      margin-bottom: 10rpx;
    }
    .desc-text {
      font-size: 28rpx;
      color: #666;
      line-height: 1.6;
    }
  }
  .info-section {
    .info-item {
      display: flex;
      justify-content: space-between;
      padding: 12rpx 0;
      .label {
        font-size: 28rpx;
        color: #666;
      }
      .value {
        font-size: 28rpx;
        color: #282828;
        &.highlight {
          @include main_color(theme);
          font-weight: 600;
        }
      }
    }
  }
  .action-section {
    margin-top: 30rpx;
    .join-btn {
      text-align: center;
      font-size: 32rpx;
      color: #fff;
      padding: 20rpx 0;
      border-radius: 44rpx;
      &.disabled {
        background-color: #ccc;
      }
    }
  }
}
.participants-section {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  .section-header {
    margin-bottom: 20rpx;
    .section-title {
      font-size: 30rpx;
      font-weight: 600;
      color: #282828;
    }
    .count {
      font-size: 24rpx;
      color: #999;
    }
  }
  .participant-item {
    padding: 16rpx 0;
    border-bottom: 1rpx solid #f8f8f8;
    .nickname {
      font-size: 28rpx;
      color: #282828;
    }
    .winner {
      font-size: 24rpx;
      color: #e93323;
      font-weight: 600;
    }
    .waiting {
      font-size: 24rpx;
      color: #999;
    }
  }
}
</style>
