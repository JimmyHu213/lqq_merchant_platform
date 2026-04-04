<template>
  <view :data-theme="theme">
    <view class='my-account mb-20 pt-28 relative'>
      <!-- #ifdef MP -->
      <view class="relative">
        <nav-bar :isBackgroundColor="false" ref="navBarRef"  navTitle='购物金明细' :isShowMenu="false" :backgroundColor="backgroundColor">
        </nav-bar>
      </view>
      <!-- #endif -->
      <view class='wrapper borderPad'>
        <view class='header borRadius14'>
          <view class='headerCon'>
            <view class="pictrue">
              <image :src="urlDomain+'crmebimage/presets/jar.png'"></image>
            </view>
            <view class='account acea-row row-top row-between'>
              <view class='assets'>
                <view>购物金余额(元)</view>
                <view class="flex align-baseline mt-22">
                  <view class='money semiBold'>{{ merMemberAsset.shoppingCreditsBalance }}</view>
                  <view class="f-s-24 ml-12 regular">包含赠送 {{merMemberAsset.giftAmount}}</view>
                </view>
              </view>
            </view>
          </view>
          <view class="flex justify-between w-full absolute bottom-40 left-0 box-border">
            <view v-for="(item,index) in detailsList" :key="index">
              <view class="f-s-24 mb-12">{{item.name}}</view>
              <view class="semiBold f-s-40">{{item.value}}</view>
            </view>
          </view>
        </view>
      </view>
    </view>
    <flow-list :merId="merId" ref="flowRef"></flow-list>
  </view>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import navBar from "../../../components/navBar";
import {setLinearThemeColor} from "../../../utils/setTheme";
import {shoppingCreditsUserAssetApi} from "../memberApi";
import flowList from "./flowList";

let app = getApp();
export default {
  name: "index",
  components: {
    navBar,
    flowList
  },
  data() {
    return {
      merId: 0,
      merMemberAsset: {},
      detailsList: [],
      urlDomain: this.$Cache.get("imgHost"),
      theme: app.globalData.theme,
      isNoCommodity: false,
      backgroundColor: '',
    }
  },
  onLoad(options) {
    this.merId = Number(options.merId)
    this.getMerMemberInfo();
  },
  methods: {
    //店铺会员信息
    async getMerMemberInfo(){
      let { data } = await shoppingCreditsUserAssetApi(this.merId)
      this.merMemberAsset = data
      this.detailsList = [
        {
          name: '累计充值(元)',
          value: data.rechargeTotalAmount
        },
        {
          name: '累计赠送(元)',
          value: data.giftTotalAmount
        },
        {
          name: '累计消费(元)',
          value: data.consumeTotalAmount
        }
      ]
    },
    changeType(type) {
      this.type = type;
      // 这里可以添加重新获取列表的逻辑
    }
  },
  // 滚动监听
  onPageScroll(e) {
    // 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
    uni.$emit('scroll');
    // #ifdef MP
    if (e.scrollTop > 50) {
      this.backgroundColor = '#fff';
    } else if (e.scrollTop < 50) {
      this.backgroundColor = '';
    }
    // #endif
  },
  onReachBottom() {
    console.log('触底')
    this.$refs.flowRef.getFlowList();
  }
}
</script>

<style scoped lang="scss">
.my-account{
  @include shallow-gradient(theme);
}
.box-border{
  width: 702rpx;
  height: 142rpx;
  padding: 30rpx 36rpx;
  background: rgba(255,255,255,0.1);
}
.wrapper .header {
  overflow: hidden;
  width: 100%;
  height: 362rpx;
  @include linear-gradient(theme);
  margin: 0 auto;
  box-sizing: border-box;
  color: rgba(255, 255, 255, 0.9);
  font-size: 24rpx;
  position: relative;
  
  .money {
    font-size: 60rpx;
    font-weight: bold;
    color: #fff;
    line-height: 1;
  }
}
.headerCon{
  position: relative;
  padding: 40rpx 30rpx 64rpx 30rpx;
  .pictrue {
    width: 250rpx;
    height: 166rpx;
    position: absolute;
    right: 0;
    top: 54rpx;
    image{
      width: 100%;
      height: 100%;
    }
  }
}


.align-baseline {
  align-items: baseline;
}
</style>