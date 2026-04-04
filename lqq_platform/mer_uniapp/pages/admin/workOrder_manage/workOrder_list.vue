<template>
  <view :data-theme="theme">
    <!-- #ifdef MP || APP-PLUS -->
    <NavBar titleText="工单管理" bagColor="#f5f5f5" iconColor='#333333' textColor="#333333" :isScrolling="isScrolling" showBack goBack="pages/user/index"></NavBar>
    <!-- #endif -->
    <view class="order-top-bar">
      <view class="order-top-bar-content">
        <SearchBar @confirm="handleSearch" placeholder="请输入工单号/订单号/商品名称">
          <view v-show="orderType === ASSIGNED" class="calendar-btn" :style="{ 'background-image': `url(${urlDomain}crmebimage/presets/ic_calendar.png)` }">
            <uni-datetime-picker ref="daterange" type="daterange" @change="handleChangeDateRange">
              <view class="daterange-placeholder"></view>
              <template #header>
                <button hover-class="none" class="calendar-clear-btn" @click="handleClearDateRange">清空</button>
              </template>
            </uni-datetime-picker>
          </view>
        </SearchBar>
      </view>
      <view class="order-category-bg" v-if="orderType === ASSIGNED">
        <OrderCategory v-model="orderStatus" :orderType="orderType"/>
      </view>
    </view>
    <view class="order-list-container" :class="{ 'is-assign-order': orderType === UNASSIGNED }">
      <OrderList :orderList="orderList" :orderType="orderType" />
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
    <BottomTabs v-model="orderType" />
  </view>
</template>

<script>
// #ifdef MP || APP-PLUS
import NavBar from '../components/NavBar.vue';
// #endif
import BottomTabs from "./components/BottomTabs";
import {workOrderAwaitListApi, workOrderListApi} from "./workOrder";
import {ASSIGNED, HOMEPAGE, serviceStatusEnum, UNASSIGNED} from "./config";
import OrderCategory from "../components/OrderCategory";
import emptyPage from "@/components/emptyPage";
import OrderList from "../components/OrderList";
import SearchBar from "../components/SearchBar";
const app = getApp();
export default {
  name: "workOrder_list",
  components: {
    SearchBar,
    // #ifdef MP || APP-PLUS
    NavBar,
    // #endif
    BottomTabs,
    OrderCategory,
    emptyPage,
    OrderList
  },
  data() {
    return {
      ASSIGNED,
      UNASSIGNED,
      serviceStatusEnum,
      urlDomain: this.$Cache.get("imgHost"),
      theme: app.globalData.theme,
      isScrolling: false,
      orderType: null,
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
      dateLimit: '',
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
  onLoad(options) {
    this.orderType = Number(options.orderType);
  },
  onUnload() {
  },
  onShow(){
    this.orderList = [];
    this.handleForceRefetch();
  },
  computed: {
    // 搜索条件
    queryParams() {
      const params = {
        keywords: this.searchText,
        status : this.orderStatus
      };

      if (this.orderType === 1) {
         delete params.status
      }

      if (this.dateLimit) {
        params.dateLimit = this.dateLimit
      }

      return params;
    }
  },
  watch: {
    // 底部tab选项卡
    orderType: {
      handler(newValue, oldValue) {
        if (newValue === HOMEPAGE){
          uni.redirectTo({
            url: `/pages/admin/workOrder_manage/index`
          })
        }
        if(oldValue) this.handleForceRefetch()
      }
    },
    // 底部tab选项卡
    orderStatus() {
      this.handleForceRefetch()
    }
  },
  methods: {
    handleStaffCheckinSucc(orderId) {
      if (this.orderType === ASSIGNED) {
        const order = this.orderList.find(item => item.order_id === Number(orderId));
        if (order) {
          // 更新打卡后的订单状态为已上门打卡，待服务
          order.status = 20;
        }
      }
    },
    // 选择时间
    handleChangeDateRange(e) {
      const [before, after] = e;
      this.startDate = before;
      this.endDate = after;
      this.dateLimit = e.join(",")
      this.orderList = [];
      this.handleForceRefetch();
    },
    handleClearDateRange() {
      this.$refs.daterange.close();
      this.$refs.daterange.clear();
      this.startDate = "";
      this.endDate = "";
      this.handleForceRefetch()
    },
    handleForceRefetch() {
      this.handleResetLoadOptions();
      this.orderList = [];
      this.handleGetOrderList();
    },
    handleResetLoadOptions() {
      this.loadOptions.page = 1;
      this.loadOptions.total = 0;
      this.loadOptions.loaded = false;
      this.loadOptions.loading = false;
    },
    handleSearch(searchText) {
      this.searchText = searchText;
      this.handleForceRefetch();
    },
    // 列表
    async handleGetOrderList() {
      const { loading, loaded, page, pageSize } = this.loadOptions;
      if (loading || loaded) return;
      this.loadOptions.loading = true;
      try {
        const res = this.orderType === ASSIGNED ? await workOrderListApi({
          ...this.queryParams,
          page,
          limit: pageSize
        }) : await workOrderAwaitListApi({
          ...this.queryParams,
          page,
          limit: pageSize
        });
        // res.data.list.forEach(item => {
        //   addBookingOrderType(item);
        // });
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
