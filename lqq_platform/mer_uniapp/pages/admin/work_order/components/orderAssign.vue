<template>
  <uni-popup ref="popup" type="bottom">
    <view class="order-assign-wrapper borderPad">
      <button class="close-btn" @click="handleClosePopup">
        <text class="iconfont icon-ic_close" />
      </button>
      <view class="popup-title">
        {{ isReassign ? "改派" : "派单" }}
      </view>
      <SearchBar placeholder="请输入姓名/手机号" custom-class="custom-search-bar" @confirm="handleConfirm" />
      <view class="assign-user-list-scroll mt20">
        <view class="scroll-inner-wrapper">
          <scroll-view scroll-y style="height: 100%;">
            <view class="assign-user-list" v-if="userList.length">
              <view class="assign-user-item borRadius14" v-for="item in userList" :key="item.id" @click="handleAssign(item)">
                <image class="assign-user-item__avatar" :src="item.idPhoto" />
                <view class="assign-user-item__info">
                  <view class="assign-user-item__name">
                    {{ item.name }}
                  </view>
                  <view class="assign-user-item__phone" @click.stop="handleMakeCallPhone(item.phone)">
                    {{ item.phone }}
                    <text class="iconfont icon-ic_phone" />
                  </view>
                </view>
              </view>
            </view>
            <view v-else>
              <EmptyPage title="暂无该员工~" mTop="0" :imgSrc="urlDomain+'crmebimage/presets/noJilu.png'" />
            </view>
          </scroll-view>
        </view>
      </view>
    </view>
  </uni-popup>
</template>

<script>
import EmptyPage from "@/components/emptyPage";
import {staffListApi, workerOrderAssignApi, workerOrderReassignApi} from "../workOrder";
import {handleMakeCallPhone} from "../../../../libs/order";
import SearchBar from "../../components/SearchBar";

export default {
  name: "OrderAssign",
  components: {
    SearchBar,
    EmptyPage
  },
  props: {
    merId: String
  },
  data() {
    return {
      urlDomain: this.$Cache.get("imgHost"),
      workOrder: "",
      isReassign: false,
      userList: [],
      loadOptions: {
        keywords: "",
        status: 1,
        loading: false,
      }
    }
  },
  methods: {
    handleMakeCallPhone,
    openPanel(workOrder, isReassign) {
      this.handleGetUserList();
      this.workOrder = workOrder;
      this.isReassign = isReassign;
      this.$refs.popup.open();
    },
    handleConfirm(value) {
      this.loadOptions.keywords = value;
      this.loadOptions.loaded = false;
      this.loadOptions.loading = false;
      this.userList = [];
      this.handleGetUserList();
    },
    async handleAssign(item) {
      const [err, { confirm }] = await uni.showModal({
        title: "提示",
        content: `确定${this.isReassign ? "改派" : "派单"}给${item.name}吗？`,
      });
      if (err || !confirm) return;
      uni.showLoading({
        mask: true
      });
      const api = this.isReassign ? workerOrderReassignApi : workerOrderAssignApi;
      try {
        const result = await api({serviceStaffId: item.id, workOrderNoList: [this.workOrder]});
        uni.hideLoading();
        this.$util.Tips({
          title: `${this.isReassign ? "改派" : "派单"}成功`,
          icon: "success"
        });
        this.handleClosePopup();
        this.$emit("refresh", this.workOrder);
      } catch (err) {
        uni.hideLoading();
        this.$util.Tips({
          title: err,
          icon: "none"
        });
      }
    },
    handleClosePopup() {
      this.$refs.popup.close();
    },
    async handleGetUserList() {
      const { loading } = this.loadOptions;
      if (loading ) return;
      this.loadOptions.loading = true;
      try {
        const { data } = await staffListApi(this.loadOptions);
        this.userList = data;
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

<style lang="scss" scoped>
.order-assign-wrapper {
  background-color: #f5f5f5;
  border-top-left-radius: 40rpx;
  border-top-right-radius: 40rpx;
  height: calc(61vh + var(--safe-area-inset-bottom));
  display: flex;
  flex-direction: column;
  position: relative;
}

.close-btn {
  position: absolute;
  top: 27rpx;
  right: 28rpx;
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  background: #EEEEEE;
  display: flex;
  align-items: center;
  justify-content: center;

  .iconfont {
    font-size: 28rpx;
    color: #999;
  }
}

/deep/ .custom-search-bar.custom-search-bar {
  --content-height: 72rpx;
  --font-size: 28rpx;
  --color: #ccc;
}

.popup-title {
  margin: 46rpx 0 28rpx;
  text-align: center;
  font-size: 32rpx;
  font-weight: 500;
}

.assign-user-list-scroll {
  flex: 1;
  position: relative;
}

.scroll-inner-wrapper {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.assign-user-list {
  padding-bottom: calc(20rpx + var(--safe-area-inset-bottom));
  font-size: 28rpx;
  color: #333;
}

.assign-user-item {
  display: flex;
  align-items: center;
  padding: 30rpx 24rpx 24rpx;
  background-color: #fff;

  &+.assign-user-item {
    margin-top: 20rpx;
  }

  &__avatar {
    width: 96rpx;
    height: 96rpx;
    border-radius: 50%;
  }

  &__info {
    flex: 1;
    margin-left: 24rpx;
    overflow: hidden;
  }

  &__name {
    font-weight: 500;
  }

  &__phone {
    display: flex;
    align-items: center;
    width: fit-content;
    margin: 4rpx 0;

    .iconfont {
      margin-left: 12rpx;
      font-size: 28rpx;
      color: #2a7efb;
    }
  }

  &__intro {
    font-size: 24rpx;
    color: #999;
  }
}
</style>
