<template>
	<view :data-theme="theme">
		<tui-skeleton v-if="skeletonShow"></tui-skeleton>
		<view class="street-box">
			<view style="position: relative;" class="pad-30">
				<merchant-list :merchantList="merchantList" :isStreet="true"></merchant-list>
			</view>
			<view class='loadingicon acea-row row-center-wrapper'>
				<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
			</view>
      <emptyPage v-if="!merchantList.length && !loading" title="暂无店铺~" mTop="13%" :imgSrc="urlDomain+'crmebimage/presets/noguanzhu.png'"></emptyPage>
		</view>
		<view v-if="bottomNavigationIsCustom" class="footerBottom"></view>
		<pageFooter></pageFooter>
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
		getMerStreetApi,
		getRegionMerListApi
	} from '@/api/merchant.js';
	import merchantList from '@/components/merchantList/index.vue'
	import tuiSkeleton from '@/components/base/tui-skeleton.vue';
	import pageFooter from "@/components/pageFooter/index.vue";
	import {
		mapGetters
	} from "vuex";
  import emptyPage from "@//components/emptyPage";
	let app = getApp();
	export default {
		data() {
			return {
        urlDomain: this.$Cache.get("imgHost"),
				theme: app.globalData.theme,
				where: {
					page: 1,
					limit: 20,
          circleIds: ''
				},
				merchantList: [],
				skeletonShow: false,
				loadTitle: '',
				loading: false,
				loadend: false,
				// 圈层相关参数
				fromRegion: false,        // 是否从圈层进入
				regionIds: '',            // 圈层ID集合
				allRegionMerchants: []    // 缓存圈层所有商户数据
			}
		},
		components: {
			merchantList,
			tuiSkeleton,
			pageFooter,
      emptyPage
		},
		computed: mapGetters(['bottomNavigationIsCustom']),
		onLoad: function(options) {
			// 检查是否从圈层进入
			if (options.regionIds) {
				this.fromRegion = true;
				this.regionIds = options.regionIds;
        this.where.circleIds = this.regionIds;
				uni.setNavigationBarTitle({
					title: '圈层店铺街'
				});
			} else {
				uni.setNavigationBarTitle({
					title: '店铺街'
				});
			}
			
			this.skeletonShow = true;
			this.getMerStreet();
		},
		// 滚动监听
		onPageScroll(e) {
		   // 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
		   uni.$emit('scroll');
		},
		methods:{
			goback() {
				uni.navigateBack()
			},
			getMerStreet: function(isPage) {
				let that = this;
				if (that.loadend) return;
				if (that.loading) return;
				if (isPage === true) that.$set(that, 'merchantList', []);
				that.loading = true;
				that.loadTitle = '';
        this.getPlatformMerStreet();
			},
			
			// 平台商户街API
			getPlatformMerStreet: function() {
				let that = this;
				getMerStreetApi(that.where).then(res => {
					this.skeletonShow = false
					let list = res.data.list;
					let merchantList = that.$util.SplitArray(list, that.merchantList);
					let loadend = list.length < that.where.limit;
					that.loadend = loadend;
					that.loadTitle = loadend ? '没有了' : '加载更多';
					that.$set(that, 'merchantList', merchantList);
					that.$set(that.where, 'page', that.where.page + 1);
					that.loading = false;
				}).catch(err => {
					that.loading = false;
					that.loadTitle = '加载更多';
				});
			},
			
			// 圈层商户街API（使用前端分页，避免后端SQL错误）
			getRegionMerStreet: async function() {
				let that = this;
				try {
					// 如果是第一页，重新获取所有数据
					if (that.where.page === 1) {
						const params = {
							num: 500, // 获取足够多的数据，避免频繁请求
              circleIds: that.regionIds
						};
						const { data } = await getRegionMerListApi(params);
						this.allRegionMerchants = data || []; // 保存所有数据
					}
					
					this.skeletonShow = false;
					
					// 前端分页处理
					let allList = this.allRegionMerchants || [];
					let startIndex = (that.where.page - 1) * that.where.limit;
					let endIndex = startIndex + that.where.limit;
					let pageList = allList.slice(startIndex, endIndex);
					
					let merchantList = that.$util.SplitArray(pageList, that.merchantList);
					let loadend = pageList.length < that.where.limit || endIndex >= allList.length;
					that.loadend = loadend;
					that.loadTitle = loadend ? '没有了' : '加载更多';
					that.$set(that, 'merchantList', merchantList);
					that.$set(that.where, 'page', that.where.page + 1);
					that.loading = false;
				} catch (error) {
					console.error('获取圈层商户街失败:', error);
					that.loading = false;
					that.loadTitle = '加载更多';
					this.skeletonShow = false;
				}
			},
		},
		onReachBottom() {
			if (this.merchantList.length > 0) {
				this.getMerStreet();
			}
		}
	}
</script>
<style>
	page {
		height: auto;
		display: flex;
		flex-direction: column;
		height: 100%;
	}
</style>
<style scoped lang="scss">
	.street-box{
		/deep/.mer_name {
			color: #fff;
		}
	}
	.pad-30{
		padding: 30rpx 24rpx;
		/deep/.street-pad20{
			padding: 0;
		}
	}
	.mp-bg {
			position: absolute;
			left: 0;
			top: 88rpx;
			width: 100%;
			height: 140rpx;
			@include index-gradient(theme);
	}
	.productList .search {
		width: 100%;
		height: 86rpx;
		padding-left: 30rpx;
		box-sizing: border-box;
		position: fixed;
		top: 88rpx;
		left: 0;
		z-index: 9;
	}

	.productList .search .input {
		width: 630rpx;
		height: 60rpx;
		background-color: #fff;
		border-radius: 50rpx;
		padding: 0 20rpx;
		box-sizing: border-box;
	}

	.productList .search .input input {
		width: 528rpx;
		height: 100%;
		font-size: 26rpx;
	}

	.productList .search .input .placeholder {
		color: #999;
	}

	.productList .search .input .iconfont {
		font-size: 35rpx;
		color: #555;
	}

	.productList .search .icon-yangshi1 {
		color: #fff;
		width: 62rpx;
		font-size: 40rpx;
		height: 86rpx;
		line-height: 86rpx;
	}

</style>
