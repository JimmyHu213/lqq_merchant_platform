<template>
  <view class="lottery-records" :data-theme="theme">
    <view class="record-card" v-for="item in list" :key="item.id">
      <view class="card-header acea-row row-between-wrapper">
        <text class="activity-name line1">{{ item.activityName || ('活动#' + item.activityId) }}</text>
        <text class="result" :class="item.isWinner === 1 ? 'winner' : 'normal'">
          {{ item.isWinner === 1 ? '已中奖' : '未中奖' }}
        </text>
      </view>
      <view class="card-body">
        <view class="info-row">
          <text class="label">消耗积分：</text>
          <text class="value">{{ item.pointsCost || 0 }}</text>
        </view>
        <view class="info-row">
          <text class="label">参与时间：</text>
          <text class="value time">{{ item.createTime }}</text>
        </view>
        <view class="info-row" v-if="item.drawTime">
          <text class="label">开奖时间：</text>
          <text class="value time">{{ item.drawTime }}</text>
        </view>
      </view>
    </view>
    <view class="loadingicon acea-row row-center-wrapper" v-if="list.length">
      <text class="loading iconfont icon-jiazai" :hidden="!loading"></text>{{ loadTitle }}
    </view>
    <emptyPage v-if="!list.length && !loading" title="暂无抽奖记录~" mTop="30%"></emptyPage>
  </view>
</template>

<script>
import { lotteryMyRecordsApi } from '@/api/lqq.js';
import emptyPage from '@/components/emptyPage.vue';
import { toLogin } from '@/libs/login.js';
import { mapGetters } from 'vuex';
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
  computed: mapGetters(['isLogin']),
  onLoad() {
    if (this.isLogin) {
      this.getList();
    } else {
      toLogin();
    }
  },
  methods: {
    getList() {
      if (this.loadend || this.loading) return;
      this.loading = true;
      lotteryMyRecordsApi({ page: this.page, limit: this.limit }).then(res => {
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
    }
  },
  onReachBottom() {
    this.getList();
  }
};
</script>

<style lang="scss" scoped>
.lottery-records {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx 24rpx;
}
.record-card {
  background-color: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  .card-header {
    padding: 24rpx;
    border-bottom: 1rpx solid #f0f0f0;
    .activity-name {
      font-size: 30rpx;
      font-weight: 600;
      color: #282828;
      max-width: 480rpx;
    }
    .result {
      font-size: 26rpx;
      font-weight: 600;
      &.winner {
        color: #e93323;
      }
      &.normal {
        color: #999;
      }
    }
  }
  .card-body {
    padding: 16rpx 24rpx 24rpx;
    .info-row {
      padding: 6rpx 0;
      .label {
        font-size: 26rpx;
        color: #999;
      }
      .value {
        font-size: 26rpx;
        color: #282828;
        &.time {
          color: #666;
        }
      }
    }
  }
}
</style>
