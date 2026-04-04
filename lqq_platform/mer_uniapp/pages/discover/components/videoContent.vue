<template>
  <!-- 最底下的文字部分 -->
  <view class="content">
    <view class="cart">
      <text class="cartName">@{{list.authorName}}</text>
    </view>
    <view class="words mb20" :style="'width: '+ (windowWidth - 120) +'px;'">
      <BaseTextMore v-if="list.content" :content="list.content" fontColor="rgba(255,255,255,0.9)"
                    actionFontColor="#fff" :font-size="28" :rows="2" expand-text="展开" collapse-text="收起">
      </BaseTextMore>
      <view class="topicList acea-row" v-if="list.topicList && list.topicList.length">
        <view @click="goTopic(item.id)" v-for="item in list.topicList" :key="item.id">
          <text class="icon">#</text><text class="text">{{item.name}}</text>
        </view>
      </view>
    </view>
    <view class="product mb20">
      <scroll-view scroll-x="true" style="white-space: nowrap; display: flex;"
                   scroll-with-animation show-scrollbar="true">
        <view class="product-item" v-for="(goods,idx) in list.productList" :key="idx">
          <view class="item-count acea-row" @click="goDetail(goods)">
            <view class="picture">
              <image :src="goods.productImage"></image>
            </view>
            <!--  -->
            <view class="product-text">
              <view class="h-70">
                <view class="name line1 line-heightOne">{{goods.productName}}</view>
                <view v-show="goods.isPay===1" class="buy">作者买过</view>
              </view>
              <view class="product-price">
                <view class="price">¥<text>{{goods.price}}</text></view>
                <view class="buy-btn"><view class="line-heightOne">购买</view></view>
              </view>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

  </view>
</template>

<script>
import {
  mapGetters
} from "vuex";
import {goProductDetail} from "../../../libs/order";
import BaseTextMore from "./BaseTextMore";
export default {
  name: "videoContent",
  components: {
    BaseTextMore
  },
  computed: {
    ...mapGetters(['userInfo', 'isLogin', 'uid', 'globalData']),
    windowWidth() {
      const vars = this.$getCssVarStyle();
      return vars['--screen-width'] || '100%';
    },
  },
  props: {
    list: {
      type: Object,
      default: function() {
        return {};
      }
    },
    noteId: {
      type: [String, Number],
      default: ''
    }
  },
  mounted() {
  },
  methods: {
    // 去详情页
    goDetail(item) {
      goProductDetail(item.productId, 0, '');
    },
  }
}
</script>

<style lang="scss" scoped>
.content {
  width: 590rpx;
  position: absolute;
  bottom: 80rpx;
  /* justify-content: center; */
  padding: 15rpx 0;
  flex-direction: column;
  justify-content: flex-start;
  color: #ffffff;
  left: 24rpx;

  .time {
    font-size: 24rpx;
    color: rgba(255, 255, 255, 0.5);
    margin-left: 12rpx;
  }

  .cart {
    height: 48rpx;
    flex-direction: row;

    .cartName {
      font-size: 28rpx;
      color: #fff;
    }
  }
}
.product {
  display: block;
  margin-top: 27rpx;

  .product-item {
    display: inline-block;
    width: 444rpx;
    height: 136rpx;
    background: rgba(0, 0, 0, .55);
    border-radius: 16rpx;
    padding: 16rpx 15rpx;
    margin-right: 30rpx;
  }

  /deep/uni-swiper,
  /deep/swiper {
    display: block;
    width: 500rpx !important;
    height: 136rpx !important;

    .swiper-count {
      display: block;
      width: 444rpx !important;
      height: 136rpx !important;
      background: rgba(0, 0, 0, .55);
      border-radius: 12rpx;
      padding: 16rpx 15rpx;
    }

    .swiper-item {
      display: block;
    }
  }

  .item-count {
    width: 414rpx;
    flex-direction: row;
    justify-content: space-between;
    border-radius: 12rpx;

    .picture {
      width: 104rpx;
      height: 104rpx;
      border-radius: 16rpx;

      image {
        width: 104rpx;
        height: 104rpx;
        border-radius: 10rpx;
      }
    }

    .product-text {
      width: 296rpx;
      justify-content: space-between;

      .buy {
        padding: 4rpx 7rpx;
        background: #5D5747;
        border-radius: 3px 3px 3px 3px;
        font-size: 16rpx;
        display: inline-block;
      }

      .name {
        width: 266rpx;
        font-size: 26rpx;
      }

      .product-price {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;

        .price {
          display: flex;
          flex-direction: row;
          align-items: flex-end;
          font-size: 24rpx;

          text {
            font-size: 26rpx;
          }
        }

        .buy-btn {
          width: 89rpx;
          height: 36rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #fff;
          border-radius: 26rpx;
          background: #E93323;
          font-size: 20rpx;
        }
      }
    }
  }
}
</style>