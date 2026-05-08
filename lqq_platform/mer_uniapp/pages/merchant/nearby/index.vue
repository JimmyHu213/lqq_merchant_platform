<template>
  <view class="nearby-page" :data-theme="theme">
    <view class="search-header">
      <view class="search-bar acea-row row-middle">
        <text class="iconfont icon-sousuo"></text>
        <input class="search-input" v-model="keywords" placeholder="搜索附近商铺" @confirm="doSearch" />
      </view>
      <view class="distance-filter acea-row row-around">
        <view class="filter-item" :class="{ active: selectedDistance === item.value }"
          v-for="item in distanceOptions" :key="item.value" @click="selectDistance(item.value)">
          {{ item.label }}
        </view>
      </view>
    </view>

    <view class="location-info acea-row row-middle" v-if="locationName">
      <text class="iconfont icon-weizhi"></text>
      <text class="loc-text line1">{{ locationName }}</text>
      <text class="refresh" @click="getLocation">刷新位置</text>
    </view>

    <view class="merchant-list">
      <view class="merchant-card" v-for="item in filteredList" :key="item.id" @click="goMerchant(item)">
        <view class="card-content acea-row">
          <image class="mer-avatar" :src="item.avatar || item.rectangleLogo" mode="aspectFill"></image>
          <view class="mer-info">
            <view class="mer-name line1">{{ item.name }}</view>
            <view class="mer-category" v-if="item.categoryName">{{ item.categoryName }}</view>
            <view class="mer-address line1">{{ item.detailedAddress || item.address }}</view>
          </view>
          <view class="mer-distance" v-if="item.distance !== undefined">
            <text class="dist-num">{{ formatDistance(item.distance) }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="loadingicon acea-row row-center-wrapper" v-if="filteredList.length">
      <text class="loading iconfont icon-jiazai" :hidden="!loading"></text>{{ loadTitle }}
    </view>
    <emptyPage v-if="!filteredList.length && !loading && searched" title="附近没有找到商铺~" mTop="20%"></emptyPage>

    <view class="no-location" v-if="!hasLocation && !loading">
      <text class="tip">需要获取您的位置信息来搜索附近商铺</text>
      <view class="loc-btn bg-color" @click="getLocation">获取位置</view>
    </view>
  </view>
</template>

<script>
import { nearbyMerchantSearchApi } from '@/api/lqq.js';
import emptyPage from '@/components/emptyPage.vue';
let app = getApp();
export default {
  components: { emptyPage },
  data() {
    return {
      theme: app.globalData.theme,
      keywords: '',
      latitude: 0,
      longitude: 0,
      locationName: '',
      hasLocation: false,
      searched: false,
      selectedDistance: 3,
      distanceOptions: [
        { label: '0.5km', value: 0.5 },
        { label: '1km', value: 1 },
        { label: '3km', value: 3 },
        { label: '5km', value: 5 },
        { label: '10km', value: 10 }
      ],
      list: [],
      page: 1,
      limit: 15,
      loading: false,
      loadend: false,
      loadTitle: ''
    };
  },
  computed: {
    filteredList() {
      // 前端按距离筛选（后端不支持distance参数）
      return this.list.filter(item => {
        if (item.distance === undefined) return true;
        return item.distance <= this.selectedDistance;
      });
    }
  },
  onLoad() {
    this.getLocation();
  },
  methods: {
    getLocation() {
      uni.getLocation({
        type: 'gcj02',
        success: (res) => {
          this.latitude = res.latitude;
          this.longitude = res.longitude;
          this.hasLocation = true;
          this.locationName = `${res.latitude.toFixed(4)}, ${res.longitude.toFixed(4)}`;
          // 尝试逆地理编码获取地名
          // #ifdef MP-WEIXIN
          this.reverseGeocode(res.latitude, res.longitude);
          // #endif
          this.resetAndSearch();
        },
        fail: () => {
          uni.showToast({ title: '获取位置失败，请检查定位权限', icon: 'none' });
          this.hasLocation = false;
        }
      });
    },
    reverseGeocode(lat, lng) {
      // 微信小程序可使用逆地理编码
      // 这里简单显示坐标，实际项目中可以对接腾讯地图API
    },
    selectDistance(distance) {
      this.selectedDistance = distance;
    },
    doSearch() {
      this.resetAndSearch();
    },
    resetAndSearch() {
      this.page = 1;
      this.list = [];
      this.loadend = false;
      this.searched = false;
      this.getList();
    },
    getList() {
      if (!this.hasLocation) return;
      if (this.loadend || this.loading) return;
      this.loading = true;
      nearbyMerchantSearchApi({
        latitude: this.latitude,
        longitude: this.longitude,
        keywords: this.keywords,
        page: this.page,
        limit: this.limit
      }).then(res => {
        let newList = res.data.list || [];
        this.list = this.list.concat(newList);
        this.loadend = newList.length < this.limit;
        this.loadTitle = this.loadend ? '没有更多了' : '加载更多';
        this.page++;
        this.loading = false;
        this.searched = true;
      }).catch(() => {
        this.loading = false;
        this.loadTitle = '加载更多';
        this.searched = true;
      });
    },
    goMerchant(item) {
      uni.navigateTo({
        url: `/pages/merchant/home/index?id=${item.id}`
      });
    },
    formatDistance(distance) {
      if (distance < 1) {
        return Math.round(distance * 1000) + 'm';
      }
      return distance.toFixed(1) + 'km';
    }
  },
  onReachBottom() {
    this.getList();
  }
};
</script>

<style lang="scss" scoped>
.nearby-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}
.search-header {
  background-color: #fff;
  padding: 20rpx 24rpx;
  .search-bar {
    background-color: #f5f5f5;
    border-radius: 36rpx;
    padding: 0 24rpx;
    height: 72rpx;
    .iconfont {
      font-size: 32rpx;
      color: #999;
      margin-right: 12rpx;
    }
    .search-input {
      flex: 1;
      font-size: 28rpx;
      height: 72rpx;
    }
  }
  .distance-filter {
    margin-top: 20rpx;
    .filter-item {
      font-size: 26rpx;
      color: #666;
      padding: 10rpx 24rpx;
      border-radius: 24rpx;
      background-color: #f5f5f5;
      &.active {
        color: #fff;
        @include main_bg_color(theme);
      }
    }
  }
}
.location-info {
  padding: 16rpx 24rpx;
  background-color: #fffbe6;
  .iconfont {
    font-size: 28rpx;
    @include main_color(theme);
    margin-right: 8rpx;
  }
  .loc-text {
    font-size: 24rpx;
    color: #666;
    flex: 1;
  }
  .refresh {
    font-size: 24rpx;
    @include main_color(theme);
    margin-left: 12rpx;
  }
}
.merchant-list {
  padding: 20rpx 24rpx;
}
.merchant-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  .card-content {
    .mer-avatar {
      width: 140rpx;
      height: 140rpx;
      border-radius: 12rpx;
      margin-right: 20rpx;
      flex-shrink: 0;
    }
    .mer-info {
      flex: 1;
      min-width: 0;
      .mer-name {
        font-size: 30rpx;
        font-weight: 600;
        color: #282828;
        margin-bottom: 8rpx;
      }
      .mer-category {
        font-size: 22rpx;
        @include main_color(theme);
        @include coupons_border_color(theme);
        display: inline-block;
        padding: 2rpx 12rpx;
        border-radius: 8rpx;
        margin-bottom: 8rpx;
      }
      .mer-address {
        font-size: 24rpx;
        color: #999;
      }
    }
    .mer-distance {
      flex-shrink: 0;
      margin-left: 12rpx;
      .dist-num {
        font-size: 26rpx;
        @include main_color(theme);
        font-weight: 600;
      }
    }
  }
}
.no-location {
  text-align: center;
  padding: 100rpx 60rpx;
  .tip {
    font-size: 28rpx;
    color: #666;
    display: block;
    margin-bottom: 30rpx;
  }
  .loc-btn {
    display: inline-block;
    font-size: 30rpx;
    color: #fff;
    padding: 16rpx 60rpx;
    border-radius: 40rpx;
  }
}
</style>
