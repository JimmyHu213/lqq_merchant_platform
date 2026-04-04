<template>
  <view class="relative" :style="cssVarStyle">
    <!-- #ifdef MP || APP-PLUS -->
    <NavBar navTitle="服务打卡" :iconColor="iconColor" :isBackgroundColor="false" :textColor="iconColor" :isScrolling="isScrolling" showBack>
    </NavBar>
    <!-- #endif -->
    <TopHeaderfixed></TopHeaderfixed>
    <view class="checkin-container" :style="{ height: screenHeight }">
      <view class="checkin-body">
        <OrderAddress :orderInfo="checkInConfig" v-if="checkInConfig" />
        <view class="checkin-box">
          <button class="checkin-btn" :disabled="!allowCheckin" @click="handleCheckin">
            <view class="checkin-btn-text">{{ checkinBtnText }}</view>
            <view class="checkin-btn-time">{{ formatTime }}</view>
          </button>
          <view class="checkin-box-tips">
            {{
              allowCheckin ? "您已进入服务打卡区域" : "您当前不在服务打卡区域"
            }}
          </view>
          <view class="checkin-address-info borderPad">
            <text class="iconfont icon-ic_location51"></text>
            <text class="overflow-text checkin-address-info__text">{{ addressInfo }}</text>
            <button class="checkin-address__refresh" @click="refreshLocation">刷新</button>
          </view>
        </view>
      </view>
      <!-- takePhoto: checkInConfig.clockInPhotoSwitch拍照开关 true是开启-->
      <CheckinPopup v-if="checkinPopupVisible" :time="formatTime" :address="addressInfo" :workOrderNo="workOrderNo"
        @cancel="handleCheckinCancel" :takePhoto="checkInConfig && checkInConfig.clockInPhotoSwitch" />
    </view>
  </view>
</template>

<script>
import OrderAddress from "../components/OrderAddress";
import NavBar from "@/components/navBar.vue"
import dayjs from "@/plugin/dayjs/dayjs.min.js";
import {clockInfoApi, getCoordinateAddressApi} from "./workOrder";
import { getDistanceFromLatLonInMeter} from "@/libs/order";
import CheckinPopup from "./components/CheckinPopup.vue";
import TopHeaderfixed from "../../../components/TopHeaderfixed";

export default {
  components: {
    TopHeaderfixed,
    NavBar,
    OrderAddress,
   CheckinPopup
  },
  data() {
    return {
      iconColor: '#FFFFFF',
      isScrolling: false,
      workOrderNo: null,
      customerLocation: {}, //商户经纬度
      checkInConfig: {}, // 打卡配置
      formatTime: "00:00:00",
      inCheckinArea: false,
      addressInfo: "获取位置中...",
      location: null, // 当前定位经纬度
      checkinLocation: null,
      checkinPopupVisible: false,
      distance: ''
    };
  },
  onLoad(options) {
    this._cssVarsHandler = vars => { this.$forceUpdate(); };
    this.$eventHub.$on('css-vars:updated', this._cssVarsHandler);

    this.workOrderNo = options.workOrderNo;
    this.handleGetWorkOrderDetail();
    this.getLocation(true);
    this.timeDisplayTimer();
  },
  onUnload() {
    this.$eventHub.$off('css-vars:updated', this._cssVarsHandler);
  },
  computed: {
    // 打卡文字展示
    checkinBtnText() {
      if (this.hasClockRecord) return "已打卡";
      if (!this.allowCheckin) return "无法打卡";
      return "点击打卡";
    },
    locationDiff() {
      return {
        location: this.location,
        customerLocation: this.customerLocation,
        checkInConfig: this.checkInConfig
      };
    },
    hasClockRecord() {
      if (!this.checkInConfig) return false;
      const { clock_in_info } = this.checkInConfig;
      if (clock_in_info && clock_in_info.clock_time) {
        return true;
      }
      return false;
    },
    allowCheckin() {
      // 在这里判断是否需要在范围内进行定位打卡
      // 必须获取到打卡配置和订单信息之后再判断是否允许打卡
      if (!this.checkInConfig) return false;
      return this.inCheckinArea;
    },
    cssVarStyle() {
      return this.$getCssVarStyle();
    },
    screenHeight() {
      const vars = this.$getCssVarStyle();
      return vars['--screen-height'] || '100vh';
    },
    navBarHeight() {
      const vars = this.$getCssVarStyle();
      return vars['--nav-bar-height'] || '44px';
    },
    safeAreaBottom() {
      const vars = this.$getCssVarStyle();
      return vars['--safe-area-inset-bottom'] || '0px';
    }
  },
  watch: {
    locationDiff({ location, customerLocation, checkInConfig }) {
      if (!checkInConfig) return;

      // 如果不限制打卡地址，则设置为在范围内
      if (!checkInConfig.clockInAddressSwitch) {
        this.inCheckinArea = true;
        return;
      };
      if (!location || !customerLocation) return;
      // 单位是km
      const distance = getDistanceFromLatLonInMeter(customerLocation.latitude, customerLocation.longitude);
      this.distance = distance
      this.inCheckinArea = distance * 1000 < checkInConfig.clockInDistance;
    }
  },
  methods: {
    //  getMerStaffCheckinConfig() {
    //   this.checkInConfig = uni.getStorageSync('reservationConfig');
    // },
    // async getLocationByAddress(address) {
    //   try {
    //     const result = await geocoding(address);
    //     const { lng: longitude, lat: latitude } = result.data.location;
    //     this.customerLocation = {
    //       latitude: Number(latitude),
    //       longitude: Number(longitude)
    //     };
    //   } catch (error) {
    //     this.$util.Tips({
    //       title: error,
    //       icon: "none"
    //     });
    //   }
    // },
    // 打卡信息
    async handleGetWorkOrderDetail() {
      try {
        const { data } = await clockInfoApi(this.workOrderNo);
        this.checkInConfig = data;
        this.customerLocation = {
          latitude: Number(data.latitude),
          longitude: Number(data.longitude)
        };
     //   this.getLocationByAddress(this.orderInfo.user_address);
      } catch (err) {
        this.$util.Tips({
          title: err,
          icon: "none"
        });
      }
    },
    handleCheckinCancel() {
      this.checkinPopupVisible = false;
    },
    //点击打卡
    async handleCheckin() {
      this.checkinPopupVisible = true;
    },
    async refreshLocation() {
      await this.getLocation();
    },
    // 获取当前定位
    async getLocation(firstCheck = false) {
      if (this._checkLocationLoading) return;
      this._checkLocationLoading = true;
      try {
        const { latitude, longitude } = await this.$util.$L.getLocation();
        this.location = {
          latitude,
          longitude
        };
        const geoCoderRes = await getCoordinateAddressApi({
          latitude: latitude,
          longitude: longitude
        });
        this.addressInfo = geoCoderRes.data.address;
        firstCheck !== true && this.$util.Tips({
          title: "刷新位置成功",
          icon: "none"
        });
      } catch (error) {
        this.$util.Tips({
          title: error,
          icon: 'none'
        });
      } finally {
        this._checkLocationLoading = false;
      }
    },
    timeDisplayTimer() {
      const setCurrentTime = () => {
        this.formatTime = dayjs().format("HH:mm:ss");
      };
      setCurrentTime();
      this._displayTimer = setInterval(setCurrentTime, 1000);
    }
  },
  destroyed() {
    clearInterval(this._displayTimer);
  }
}
</script>

<style scoped lang="scss">
.checkin-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 256rpx; /* 44px + 212rpx */
  background-image: linear-gradient(90deg, #2291F8 0%, #1CD1DC 100%);
}

.checkin-bg2 {
  position: absolute;
  top: 208rpx; /* 44px + 164rpx */
  left: 0;
  width: 100%;
  height: 50rpx;
  background-image: linear-gradient(0deg, #F5F5F5 0%, rgba(245, 245, 245, 0) 100%);
}

.checkin-container {
  position: relative;
  display: flex;
  flex-direction: column;
}

.checkin-body {
  padding: 36rpx 24rpx 24rpx;
  display: flex;
  flex-direction: column;
  flex: 1;
  gap: 10px;

  &.safe-bottom-env {
    padding-bottom: 0px; /* 移除 CSS 变量 */
  }
}

.checkin-box {
  flex: 1;
  background-color: #fff;
  border-radius: 24rpx;
}

.checkin-btn {
  display: flex;
  flex-flow: column;
  align-items: center;
  gap: 20rpx;
  justify-content: center;
  color: #fff;
  width: 272rpx;
  height: 272rpx;
  border-radius: 50%;
  margin: 164rpx auto 0;

  background: linear-gradient(159deg, #1CD1DC -13%, #2291F8 43%), linear-gradient(139deg, #47B5FF 12%, #0F86F5 86%);
  box-shadow: 0rpx 10rpx 32rpx 0rpx rgba(48, 139, 248, 0.5);

  &[disabled] {
    background: linear-gradient(139deg, #D0D3D9 12%, #C3C7CE 85%);
    box-shadow: 0rpx 10rpx 32rpx 0rpx rgba(122, 140, 162, 0.3);
    color: #fff;
  }
}

.checkin-btn-text {
  font-size: 40rpx;
  font-weight: 500;
}

.checkin-btn-time {
  font-size: 32rpx;
  font-weight: 400;
  opacity: 0.7;
  font-variant-numeric: tabular-nums;
  font-family: initial;
}

.checkin-box-tips {
  text-align: center;
  font-size: 28rpx;
  margin-top: 60rpx;
  margin-bottom: 18rpx;
}

.checkin-address-info {
  margin: 0 auto;
  overflow: hidden;
  display: flex;
  align-items: center;
  color: #999;
  justify-content: center;

  .iconfont {
    font-size: 28rpx;
    margin-right: 4rpx;
  }

  .checkin-address__refresh {
    flex-shrink: 0;
    margin-left: 12rpx;
    color: #308Bf8;
    font-size: 12px;
  }
}
</style>
