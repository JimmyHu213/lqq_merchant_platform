<template>
  <view :data-theme="theme">
    <nav-bar :isScrolling="isScrolling" :iconColor='iconColor' :isShowMenu="false" :isBackgroundColor="false" ref="navBarRef"  :navTitle="serviceStaffInfo.merName" goBack="/pages/user/index">
    </nav-bar>
    <TopHeaderfixed></TopHeaderfixed>
    <view class="merchant-info relative borderPad mt-24">
      <view class="merchant-info-wrapper">
        <image :src="serviceStaffInfo.idPhoto" class="merchant-user-avatar" />
        <view class="merchant-user-name overflow-text">{{ serviceStaffInfo.name }}</view>
      </view>
    </view>
    <Statistics :serviceStaffInfo="serviceStaffInfo"></Statistics>
    <view class="relative"> <OrderList :orderList="orderList" :orderType="orderType" /></view>
    <view v-if="!loadOptions.loading">
      <view class="order-list-empty text-center py-20 f-s-26 text--w111-ccc" v-if="loadOptions.loaded && orderList.length">
        暂无更多～
      </view>
      <view v-else-if="orderList.length === 0" class="nothing">
        <emptyPage title="暂无订单~" mTop="14%" :imgSrc="urlDomain+'crmebimage/presets/nodingdan.png'" />
      </view>
    </view>
    <view class="list-bottom-tab-placeholder"></view>
    <BottomTabs v-model="orderType"/>
  </view>
</template>

<script>
import NavBar from "@/components/navBar.vue"
import {reservationConfigApi, staffInfoApi, staffLoginApi, workOrderAwaitListApi, workOrderListApi} from "./workOrder";
import Cache from "@/utils/cache";
import BottomTabs from "./components/BottomTabs";
import { HOMEPAGE} from "./config";
import OrderList from "../components/OrderList";
import emptyPage from "@/components/emptyPage";
import Statistics from "./components/Statistics";
import TopHeaderfixed from "../../../components/TopHeaderfixed";
const app = getApp();
export default {
  components: {
    NavBar,
    BottomTabs,
    OrderList,
    emptyPage,
    Statistics,
    TopHeaderfixed
  },
  data() {
    return {
      urlDomain: this.$Cache.get("imgHost"),
      theme: app.globalData.theme,
      orderType: HOMEPAGE,
      serviceStaffInfo: {},
      loadOptions: {
        page: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        loaded: false,
      },
      orderList: [],
      iconColor: '#FFFFFF',
      isScrolling: false,
    }
  },
  watch: {
    // 底部tab选项卡
    orderType: {
      handler(newValue) {
        if (newValue === HOMEPAGE){
          uni.redirectTo({
            url: `/pages/admin/workOrder_manage/index`
          })
        }else{
          uni.redirectTo({
            url: `/pages/admin/workOrder_manage/workOrder_list?orderType=${newValue}`
          })
        }
      }
    },
  },
  async onPullDownRefresh() {
    await this.handleForceRefetch();
    uni.stopPullDownRefresh();
  },
  onPageScroll(e) {
    if (e.scrollTop > 50) {
      this.iconColor = '#333333';
      this.isScrolling = true;
    } else {
      this.iconColor = '#FFFFFF';
      this.isScrolling = false;
    }
    this.isScroll = e.scrollTop > 0;
  },
  onReachBottom() {
    const { loading, loaded } = this.loadOptions;
    if (loading || loaded) return;
    this.loadOptions.page++;
    this.handleGetOrderList();
  },
  onLoad() {
    if(!Cache.get('workOrderToken')){
      this.handleLogin();
    }else{
      this.getStaffInfo()
      this.getReservationConfig()
      this.handleGetOrderList()
    }
  },
  methods: {
    handleForceRefetch() {
      this.orderList = [];
      return this.handleGetOrderList();
    },
    async handleLogin(){
      try {
        const res = await staffLoginApi()
        this.$store.commit('SET_WORK_ORDER_TOKEN', res.data.token);
        await this.getReservationConfig()
        await this.getStaffInfo()
        await this.handleGetOrderList()
      } catch (err) {}
    },
    async getReservationConfig(){
      const { data } = await reservationConfigApi()
      uni.setStorageSync('reservationConfig', data);
    },
    // 用户信息
    async getStaffInfo(){
      const { data } = await staffInfoApi()
      this.serviceStaffInfo = data
    },
    // 列表
    async handleGetOrderList() {
      const { loading, loaded, page, pageSize } = this.loadOptions;
      if (loading || loaded) return;
      this.loadOptions.loading = true;
      try {
        const res = await workOrderListApi({
          status : 3,
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
.business-bg {
  height: calc(var(--nav-bar-height) + 290rpx);
  background: linear-gradient(180deg, #2291F8 0%, #2291F8 var(--nav-bar-height), rgba(34, 145, 248, 0) 100%);
  position: fixed;
  width: 100%;
  top: 0;
  left: 0;
}
.merchant-info {

  &-wrapper {
    display: flex;
    align-items: center;
  }

  .merchant-user-avatar {
    width: 60rpx;
    height: 60rpx;
    border-radius: 50%;
  }

  .merchant-user-name {
    font-size: 30rpx;
    font-weight: 500;
    color: #fff;
    max-width: 60  0rpx;
    margin: 0 16rpx;
  }
}
</style>
