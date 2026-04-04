<template>
  <view :data-theme="theme">
    <!-- #ifdef MP || APP-PLUS -->
    <NavBar titleText="工单列表" bagColor="#f5f5f5" iconColor='#333333' textColor="#333333" :isScrolling="isScrolling" showBack></NavBar>
    <!-- #endif -->
    <view class="order-top-bar">
      <view class="order-top-bar-content">
        <SearchBar @confirm="handleSearch" placeholder="请输入工单号">
        </SearchBar>
      </view>
      <view class="order-category-bg">
        <OrderCategory v-model="orderStatus" />
      </view>
    </view>
    <view class="order-list-container">
      <OrderList :orderList="orderList" />
    </view>
    <template v-if="!loadOptions.loading">
      <view class="order-list-empty" v-if="loadOptions.loaded && orderList.length">
        暂无更多～
      </view>
      <view v-else-if="orderList.length === 0" class="nothing">
        <emptyPage title="暂无订单~" mTop="25%" :imgSrc="urlDomain+'crmebimage/presets/nodingdan.png'" />
      </view>
    </template>
    <view class="list-bottom-tab-placeholder"></view>

    <footerPage></footerPage>
  </view>
</template>

<script>
// #ifdef MP || APP-PLUS
import NavBar from '../components/NavBar.vue';
// #endif
import {employeeWorkOrderListApi} from "./workOrder";
import OrderCategory from "../components/OrderCategory";
import emptyPage from "@/components/emptyPage";
import OrderList from "../components/OrderList";
import footerPage from "../components/footerPage";
import SearchBar from "../components/SearchBar";
const app = getApp();
export default {
  name: "list",
  components: {
    footerPage,
    SearchBar,
    // #ifdef MP || APP-PLUS
    NavBar,
    // #endif
    OrderCategory,
    emptyPage,
    OrderList
  },
  data() {
    return {
      urlDomain: this.$Cache.get("imgHost"),
      theme: app.globalData.theme,
      isScrolling: false,
      isScroll: false,
      orderStatus: 0,
      loadOptions: {
        page: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        loaded: false,
      },
      orderList: [],
      startDate: "",
      endDate: "",
      searchText: ''
    }
  },
  async onPullDownRefresh() {
    await this.handleForceRefetch();
    uni.stopPullDownRefresh();
  },
  onPageScroll(e) {
    this.isScroll = e.scrollTop > 0;
  },
  onReachBottom() {
    const { loading, loaded } = this.loadOptions;
    if (loading || loaded) return;
    this.loadOptions.page++;
    this.handleGetOrderList();
  },
  onLoad() {
    this.handleGetOrderList();
  },
  onUnload() {
  },
  onShow(){
    // this.orderList = [];
    // this.handleGetOrderList();
  },
  computed: {
    // 搜索条件
    queryParams() {
      const params = {
        workOrderNo: this.searchText,
        status : this.orderStatus
      };
      return params;
    }
  },
  watch: {
    // 底部tab选项卡
    queryParams() {
      this.handleForceRefetch();
    }
  },
  methods: {
    handleChangeDateRange(e) {
      const [before, after] = e;
      this.startDate = before;
      this.endDate = after;
    },
    handleClearDateRange() {
      // this.$refs.daterange.close();
      // this.$refs.daterange.clear();
      this.startDate = "";
      this.endDate = "";
    },
    handleForceRefetch() {
      this.handleResetLoadOptions();
      this.orderList = [];
      return this.handleGetOrderList();
    },
    handleResetLoadOptions() {
      this.loadOptions.page = 1;
      this.loadOptions.total = 0;
      this.loadOptions.loaded = false;
      this.loadOptions.loading = false;
    },
    handleSearch(searchText) {
      this.searchText = searchText;
    },
    // 列表
    async handleGetOrderList() {
      const { loading, loaded, page, pageSize } = this.loadOptions;
      if (loading || loaded) return;
      this.loadOptions.loading = true;
      try {
        const res = await employeeWorkOrderListApi({
          ...this.queryParams,
          page,
          limit: pageSize
        })
        this.orderList.push(...res.data.list);
        this.loadOptions.total = res.data.total;
        this.loadOptions.loaded = this.orderList.length >= this.loadOptions.total;
      } catch (err) {
        this.$util.Tips({
          title: err,
          icon: "none"
        });
      }
      this.loadOptions.loading = false;
    }
  }
}
</script>

<style scoped lang="scss">
$bg-height: calc(238rpx + var(--safe-area-inset-top));
.order-top-bar-content{
  padding: 20rpx 24rpx;
}
.body-bg1 {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: $bg-height;
  background-image: linear-gradient(90deg, #2291F8 0%, #1CD1DC 100%);
}

.body-bg2 {
  position: absolute;
  bottom: -2rpx;
  left: 0;
  height: 48rpx;
  width: 100%;
  background: linear-gradient(to bottom, rgba(255, 255, 255, 0) 0%, #f5f5f5 100%);

  &.unassign-order-and-scroll {
    background: #f5f5f5;
  }
}

.order-category-bg {
  position: relative;

  &::after,
  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 48rpx;
  }

  &::before {
    // background-image: linear-gradient(90deg, #2291F8 0%, #1CD1DC 100%);
  }

  &::after {
    height: 50rpx;
    background: linear-gradient(to bottom, rgba(255, 255, 255, 0) 0%, #f5f5f5 100%);
  }
}


.order-top-bar {
  position: sticky;
  top: 0;
  z-index: 10;
  background-color: #f5f5f5;
  //min-height: $bg-height;
}

.list-bottom-tab-placeholder {
  height: calc(var(--safe-area-inset-bottom) + 100rpx);
}

.order-list-empty {
  text-align: center;
  margin: 20rpx 0 70rpx;
  color: #CCCCCC;
  font-size: 26rpx;
}

.nothing {
  margin-top: 0;

  /deep/ .empty-box {
    margin-top: 0;
  }
}

.calendar-btn {
  height: var(--content-height);
  aspect-ratio: 1;
  margin-left: 16rpx;
  background-color: #fff;
  border-radius: 50%;
  background: none no-repeat center / 36rpx #fff;

  .daterange-placeholder {
    height: var(--content-height);
  }

  .calendar-clear-btn {
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    display: flex;
    flex-direction: row;
    align-items: center;
    margin: 10px 25px 0;
    font-size: 26rpx;
    color: #737987;
  }
}

.order-list-container {
  position: relative;

  &.is-assign-order {
    //margin-top: -40rpx;
  }
}
</style>
