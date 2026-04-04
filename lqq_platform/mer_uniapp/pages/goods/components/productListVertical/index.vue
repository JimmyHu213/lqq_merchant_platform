<template>
	<view class='list acea-row row-between-wrapper pt-10 relative'
		:class="[is_switch==true ? '' : 'on', (merId > 0 || cid > 0) ? 'mer-list' : ''] ">
		<view class='item' :class='is_switch==true?"":"on"' hover-class='none' v-for="(item,index) in productLists"
			:key="index" @click="goDetail(item)">
			<view class='pictrue relative' :class='is_switch==true?"":"on"'>
				<view v-show="item.stock===0" class="sellOut">已售罄</view>
				<easy-loadimage :image-src="item.image" :class='is_switch==true?"":"on"' radius="16rpx" width="240rpx" height="240rpx">
				</easy-loadimage>
				<span class="pictrue_log_class" :class="is_switch === true ? 'pictrue_log_big' : 'pictrue_log'"
					v-if="item.activityH5 && item.activityH5.type === '1'">秒杀</span>
				<span class="pictrue_log_class" :class="is_switch === true ? 'pictrue_log_big' : 'pictrue_log'"
					v-if="item.activityH5 && item.activityH5.type === '2'">砍价</span>
				<span class="pictrue_log_class" :class="is_switch === true ? 'pictrue_log_big' : 'pictrue_log'"
					v-if="item.activityH5 && item.activityH5.type === '3'">拼团</span>
			</view>
			<view class='text flex-col justify-between' :class='is_switch==true?"":"on"'>
				<view class='name box-line2 line2'>
					<span v-if="item.productTags && item.productTags.locationLeftTitle.length"
						class="font-bg-red mr10 merType bg-color">{{item.productTags.locationLeftTitle[0].tagName}}</span>
					{{item.name}}
				</view>
				<!-- 价格 -->
				<view>
          <view v-if="item.productTags && item.productTags.locationUnderTitle.length" class="flex flex-wrap pad2">
            <text
                v-for="items in item.productTags.locationUnderTitle.length>3?item.productTags.locationUnderTitle.slice(0,3):item.productTags.locationUnderTitle"
                :key="items.id" class="mr10 tagSolid">{{items.tagName}}</text>
          </view>
          <view class="mt-10">
            <svip-price :svipIconStyle="svipIconStyle" :productPrice="item"
                        :svipPriceStyle="svipPriceStyle"></svip-price>
          </view>
          <view class='vip acea-row row-between-wrapper mt-10 line-heightOne' :class='is_switch==true?"":"on"'>
            <view class="text-666">已售{{item.sales}}{{item.unitName}}</view>
          </view>
          <view v-if="item.merName" class="company line-heightOne mt-8rpx" @click.stop="goStore(item.merId)">
            <text class='name line1'>{{item.merName}}</text>
            <view class="flex">
              进店
              <text class="iconfont f-s-22 icon-ic_rightarrow"></text>
            </view>
          </view>
        </view>
			</view>

		</view>
	</view>
</template>

<script>
import svipPrice from '@/components/svipPrice.vue';
import easyLoadimage from '@/components/base/easy-loadimage.vue';
import {goProductDetail} from "../../../../libs/order";
	export default {
    components: {
      svipPrice,
      easyLoadimage
    },
    props: {
      //商品列表
      productLists: {
        type: Array,
        default: function() {
          return []
        }
      },
      is_switch: {
        type: Boolean,
        default: false,
      },
      // 商户id
      merId: {
        type: Number,
        default: 0,
      },
      // 分类
      cid: {
        type: Number,
        default: 0,
      },
    },
    data(){
      return {
        //普通价格
        svipPriceStyle: {
          svipBox: {
            height: '26rpx',
            borderRadius: '60rpx 56rpx 56rpx 20rpx',
          },
          icon: {
            height: '26rpx',
            fontSize: '18rpx',
            borderRadius: '12rpx 0 12rpx 2rpx'
          },
          price: {
            fontSize: '38rpx'
          },
          svipPrice: {
            fontSize: '22rpx'
          }
        },
        //svip价格
        svipIconStyle: {
          svipBox: {
            height: '26rpx',
            borderRadius: '24rpx 40rpx 40rpx 0.4rpx',
          },
          price: {
            fontSize: '38rpx'
          },
          svipPrice: {
            fontSize: '18rpx'
          }
        },
      }
    },
    methods: {
      goStore(id) {
        this.$util.goToMerHome(id)
      },
      // 去详情页
      goDetail(item) {
        goProductDetail(item.id, 0,'');
      },
    }
	}
</script>

<style scoped lang="scss">
.company {
  display: flex;
  align-items: center;
  color: #666666 !important;
  font-size: 22rpx;

  .name {
    display: inline-block;
    // width: 120rpx;
    height: auto !important;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  .flex {
    display: flex;
    align-items: center;
    margin-left: 10rpx;
    color: #282828 !important;
    width: 100rpx;
  }
}
.list.on {
  background-color: #fff;
}

 .list .item {
  width: 335rpx;
  background-color: #fff;
  border-radius: 14rpx;
  margin-bottom: 20rpx;
}

 .list .item.on {
  width: 100%;
  display: flex;
  padding: 0 24rpx 32rpx 24rpx;
  margin: 0;
  border-radius: 14rpx;
}

 .list .item .text {
  padding: 18rpx 20rpx;
  font-size: 30rpx;
  color: #222;
}

 .list .item .text.on {
  width: 456rpx;
  padding: 0 0 0 20rpx;
  position: relative;
}

 .list .item .text .money {
  font-size: 26rpx;
  font-weight: bold;
  margin-top: 8rpx;
}

 .list .item .text .money.on {
  margin-top: 50rpx;
}

 .list .item .text .money .num {
  font-size: 34rpx;
}

 .list .item .text .vip {
  font-size: 22rpx;
  color: #aaa;
}

</style>