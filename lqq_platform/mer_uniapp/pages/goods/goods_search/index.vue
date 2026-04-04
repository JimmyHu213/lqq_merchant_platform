<template>
	<view :data-theme="theme" class="bg--w111-fff">
    <searchBox :searchValue="searchValue" :toBackShow="false"  @searchChange="searchBut"></searchBox>
    <view class='searchGood bg--w111-fff'>
      <view class='title'>热门搜索</view>
      <view class='list acea-row'>
        <block v-for="(item,index) in hotSearchList" :key="index">
          <view class='item' @tap='setHotSearchValue(item.title)'>{{item.title}}</view>
        </block>
      </view>
      <view class='line'></view>
    </view>
		<recommend ref="recommend"></recommend>
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
	import {
		getSearchKeyword,
	} from '@/api/product.js';
	import recommend from "@/components/base/recommend.vue";
  import searchBox from "@/components/searchBox.vue";
	let app = getApp();
	export default {
		components: {
			recommend,
      searchBox
		},
		data() {
			return {
				hostProduct: [],
				searchValue: '',
				focus: true,
				bastList: [],
				hotSearchList: [],
				first: 0,
				limit: 8,
				page: 1,
				loading: false,
				loadend: false,
				loadTitle: '加载更多',
				hotPage:1,
				isScroll:true,
				theme:app.globalData.theme,
				searchTop:''
			};
		},
		// 滚动监听
		onPageScroll(e) {
			// 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
			uni.$emit('scroll');
		},
		onShow: function() {
			this.getRoutineHotSearch();
		},
		onLoad(e){
			// #ifdef MP
			this.searchTop=uni.getMenuButtonBoundingClientRect().top
			// #endif
			if(e.searchVal) this.searchValue = e.searchVal;
		},
		methods: {
			getRoutineHotSearch: function() {
				let that = this;
				getSearchKeyword().then(res => {
					that.$set(that, 'hotSearchList', res.data);
				});
			},
			setHotSearchValue: function(event) {
				this.$set(this, 'searchValue', event);
				this.page = 1;
				this.loadend = false;
				this.$set(this, 'bastList', []);
				uni.navigateTo({
					url: `/pages/goods/goods_list/index?searchValue=${this.searchValue}`
				})
			},
			setValue: function(event) {
				this.$set(this, 'searchValue', event.detail.value);
			},
			searchBut: function(e) {
				let that = this;
				let val = '';
				that.focus = false;
				if(e.detail.value){
					val =e.detail.value
				}
        that.searchValue = val;
				if (that.searchValue.length > 0||val) {
					uni.navigateTo({
						url: `/pages/goods/goods_list/index?searchValue=${val?val:that.searchValue}`
					})
				} else {
					return this.$util.Tips({
						title: '请输入要搜索的商品',
						icon: 'none',
						duration: 1000,
						mask: true,
					});
				}
			}
		},
		onReachBottom() {
			this.$refs.recommend.get_host_product();
		}
	}
</script>

<style lang="scss" scoped>
    page,/deep/.recommendList {
		background-color: #fff !important;
	}
	.searchGood .title {
		font-weight: 500;
		font-family: PingFang SC, PingFang SC;
		font-size: 28rpx;
		color: #333;
		margin: 0 30rpx 25rpx 30rpx;
		background-color: #fff;
	}

	.searchGood .list {
		padding-left: 10rpx;
	}

	.searchGood .list .item {
		font-size: 24rpx;
		color: #454545;
		padding: 0 24rpx;
		height: 60rpx;
		border-radius: 200rpx;
		line-height: 59rpx;
		background: #F5F5F5;
		margin: 0 0 16rpx 24rpx;
	}

	.searchGood .line {
		margin: 20rpx 30rpx 0 30rpx;
	}
</style>
