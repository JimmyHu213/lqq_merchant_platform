<template>
  <view class="wrapper borRadius14">
    <view class="nav acea-row row-around">
      <view class="item" :class="active == index ? 'on' : ''" v-for="(item,index) in navList"
            :key="index" @click="switchTap(index)">
        {{ item }}
      </view>

    </view>
    <view class="list" v-if="rankList.length">
      <view class="item acea-row row-between-wrapper" v-for="(item,index) in rankList" :key="index">
        <view class="acea-row row-middle">
          <view class="num" v-if="index <= 2">
            <image :src="require('../../static/images/no'+(index+1)+'.png')"></image>
          </view>
          <view class="num" v-else>
            {{index+1}}
          </view>
          <view class="pictrue">
            <image :src="item.avatar"></image>
          </view>
          <view class="text line1">{{item.nickname}}</view>
        </view>
        <view v-if="fromType === 'promoter'" class="people">{{item.spreadCount}}人</view>
        <view v-else class="people">￥{{item.brokeragePrice}}</view>
      </view>
    </view>
    <view v-if="!rankList.length && !loading">
      <emptyPage title="暂无排行榜数据哦～" mTop="0" :imgSrc="urlDomain+'crmebimage/presets/noJilu.png'"></emptyPage>
    </view>
  </view>
</template>

<script>
import emptyPage from '@/components/emptyPage.vue';
export default {
  name: "rankingList",
  components: {
    emptyPage,
  },
  data(){
    return{
      active: 0,
      navList: ["周排行", "月排行"],
      urlDomain: this.$Cache.get("imgHost"),
    }
  },
  props: {
    //排行列表
    rankList: {
      type: Array,
      default: function() {
        return []
      }
    },
    loading: {
      type: Boolean,
      default: false
    },
    fromType: {
      type: String,
      default: ''
    }
  },
  methods: {
    //tab切换事件
    switchTap: function(index) {
      if (this.active === index) return;
      this.active = index;
     this.$emit('getChangeTab', index)
      // this.type = index ? 'month' : 'week';
      // this.$set(this, 'rankList', []);
      // this.getRanklist();
    },
  }
}
</script>

<style scoped lang="scss">
.wrapper{
  width: auto;
  background-color: #fff;
  margin: -278rpx 24rpx 0 24rpx;

  .nav{
    font-size: 28rpx;
    font-weight: 400;
    color: #999;
    .item{
      color: #999999;
      padding: 32rpx 0 12rpx 0;
      &.on{
        color: #333;
        font-size: 32rpx;
        position: relative;
        font-weight: 600;
        &::after{
          position: absolute;
          content: '';
          width: 56rpx;
          height: 4rpx;
          background: #E93323;
          border-radius: 2rpx;
          left:50%;
          margin-left: -28rpx;
          bottom: 0;
        }
      }
    }
  }

  .list{
    padding: 0 44rpx 72rpx 28rpx;
    .item{
      margin-top: 48rpx;
      .num{
        font-weight: 400;
        color: #999;
        font-size: 28rpx;
        width: 64rpx;
        height: 64rpx;
        text-align: center;
        line-height: 64rpx;

        image{
          width: 100%;
          height: 100%;
          display: block;
        }
      }
      .pictrue{
        width: 80rpx;
        height: 80rpx;
        margin-left: 28rpx;
        image{
          width: 100%;
          height: 100%;
          border-radius: 50%;
        }
      }
      .text{
        font-weight: 400;
        color: #333333;
        font-size: 28rpx;
        margin-left: 24rpx;
        width: 180rpx;
      }
    }
  }
}
</style>