<template>
  <view class="order-detail-table" :class="{ 'has-style': hasStyle }">
    <view class="order-detail-title" v-if="title">{{ title }}</view>
    <view class="order-table-item" v-for="(item, pindex) in list" :key="pindex" :class="{ 'mini-gap': miniGap }">
      <view v-show="!item.isShow" class="order-label">{{ item.label }}</view>
      <view class="order-value image-list" v-if="item.isImage && !item.isShow" @click="handlePreviewImage">
        <image :src="img" mode="aspectFill" v-for="(img, index) in item.value" :key="index"
          class="image-item" :data-index="index" :data-pindex="pindex" />
      </view>
      <view class="order-value acea-row row-middle row-right" :class="[item.overflow ? 'overflow-text' : '', item.isPhone ? 'phone-value' : '', item.isDate ? 'is-date' : '', item.value && item.value.length > 2 ? 'not-empty' : '']"
            v-if="!item.isImage && !item.isShow">
        {{ item.value | filterEmpty }}
        <text @click="handleMakeCallPhone(item.value)" class="iconfont icon-ic_phone" v-if="item.isPhone" />
				<view v-if="item.copy" class="copy-btn-box" @click="handleSetClipboard(item.value)">
					<text>复制</text>
				</view>
        <!-- <button  v-if="item.copy" class="copy-btn ml-16 f-s-28" @click="handleSetClipboard(item.value)">复制</button> -->
      </view>
    </view>
  </view>
</template>

<script>
import {handleMakeCallPhone, handleSetClipboard} from "../../../../libs/order";
export default {
  name: "OrderDetailTable",
  props: {
    hasStyle: {
      type: Boolean,
      default: true
    },
    title: {
      type: String,
      default: ""
    },
    miniGap: {
      type: Boolean,
      default: false
    },
    list: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {

    };
  },
  methods: {
    handleMakeCallPhone,
    handleSetClipboard,
    handlePreviewImage(e) {
      const { index, pindex } = e.target.dataset;
      if (index === undefined || pindex === undefined) return;
      uni.previewImage({
        current: +index,
        urls: this.list[pindex].value
      });
    }
  }
}
</script>

<style scoped lang="scss">
.copy-btn{
  color: #2291F8FF;
}
.order-detail-table {
  font-size: 28rpx;
  color: #333333FF;

  &.has-style {
    margin-top: 20rpx;
    background-color: #fff;
    border-radius: 24rpx;
    padding: 32rpx 24rpx;
  }
}

.order-detail-title {
  font-weight: 500;
  font-size: 30rpx;
}

.order-table-item {
  display: flex;
  margin-top: 32rpx;
  align-items: center;

  &.mini-gap {
   // margin-top: 32rpx;
  }
}

.order-label {
  min-width: 134rpx;
  // width: 170rpx;
  flex-shrink: 0;
}

.order-value {
  &.is-date {
    min-width: 290rpx;

    &.not-empty {
      text-align: initial;
    }
  }
  margin-left: auto;
  text-align: right;

  &.phone-value {
    display: flex;
    align-items: center;

    .iconfont {
      margin-left: 12rpx;
      color: #2A7EFB;
    }
  }

  &.image-list {
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-end;
    gap: 12rpx;

    .image-item {
      width: 106rpx;
      height: 106rpx;
      border-radius: 12rpx;

    }
  }
	.copy-btn-box {
		background: #F5F5F5;
		border-radius: 200rpx 200rpx 200rpx 200rpx;
		padding: 0 12rpx;
		margin-left: 8rpx;
		font-size: 22rpx;
	}
}
</style>
