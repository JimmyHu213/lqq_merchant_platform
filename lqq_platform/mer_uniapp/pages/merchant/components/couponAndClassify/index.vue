<template>
	<div :data-theme="theme" class="store-home productSort"
		:style="{backgroundImage: `linear-gradient(360deg, rgba(0, 0, 0, 0.5) 0%, rgba(0, 0, 0, 0.9) 100%),url(${merchantAPPInfo.backImage})`}">
		<tui-skeleton v-if="skeletonShow"></tui-skeleton>
	 <view>
     <view class="sys-head skeleton-rect" :style="{ height: statusBarHeight + 'px' }"></view>
     <view class="fixed-top">
       <view class='navbarCon acea-row' :style="{ paddingRight: navbarRight + 'px',width:leftWindowWidth+ 'px' }">
         <view class="select_nav acea-row row-around tui-skeleton-rect homeBox" id="home" ref="selectNav">
           <text class="iconfont icon-ic_leftarrow px-20" @tap="returns"></text>
           <text class="nav_line"></text>
           <text class="iconfont icon-ic_home px-20" @tap="goIndex"></text>
         </view>

         <!-- #ifndef MP -->
         <navigator :url="'/pages/goods/goods_list/index?merId='+merId" class="input tui-skeleton-rect searchIpt"
                    hover-class="none" :style="{ top: homeTop + 'rpx',width:searchIptWindowWidth+ 'px' }"><text
             class="iconfont icon-ic_search"></text>
           输入商品名称搜索</navigator>
         <!-- #endif -->
         <!-- #ifdef MP -->
         <navigator :url="'/pages/goods/goods_list/index?merId='+merId" class="input tui-skeleton-rect searchIpt"
                    hover-class="none" :style="{ top: searchTop + 'px',width:searchIptWindowWidth+ 'px' ,height:searchHeight+'px'}"><text
             class="iconfont icon-ic_search"></text>
           输入商品名称搜索</navigator>
         <!-- #endif -->
       </view>
     </view>
     <merHome class="tui-skeleton-rect" :merchantInfo="merchantAPPInfo" :merId="merId" type="detail"></merHome>
   </view>
    <view class="tab-cont">
      <!-- 分类 -->
      <view v-if="tabActive === 'classify'">
        <mer-classify :merId="merId"></mer-classify>
      </view>
      <!-- 优惠券 -->
      <view v-if="tabActive === 'coupon'" class="couponBox">
        <mer-coupon :merId="merId"></mer-coupon>
      </view>
    </view>
	</div>
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

	import WaterfallsFlow from '@/components/WaterfallsFlow/WaterfallsFlow.vue'
	import WaterfallsFlowItem from '@/components/WaterfallsFlowItem/WaterfallsFlowItem.vue'
	import merCoupon from '../coupon/index.vue';
	import merClassify from '../classify/index.vue';
	import {
		mapGetters
	} from "vuex";
	import emptyPage from '@/components/emptyPage.vue'
	import merHome from '@/components/merHome/index.vue'
	import tuiSkeleton from '@/components/base/tui-skeleton.vue';
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	var statusBarHeight = uni.getSystemInfoSync().statusBarHeight;
	let app = getApp();
	export default {
		components: {
			WaterfallsFlow,
			emptyPage,
			merHome,
			tuiSkeleton,
			easyLoadimage,
			WaterfallsFlowItem,
			merClassify,
			merCoupon
		},
		props: {
			merId: { // 商户id
				type: Number,
				default: 0
			}, //底部菜单选中的索引
			tabActive: {
				type: String,
				default: ''
			}
		},
		data() {
			return {
				statusBarHeight: 0,
				scrollTop: 0,
				skeletonShow: true, //骨架屏显示隐藏
				homeTop: 20,
				navbarRight: 0,
				theme: app.globalData.theme,
				leftWindowWidth: 0, //小程序头部左侧除去右上角的胶囊菜单宽度的长度
				searchIptWindowWidth: 0, //小程序端搜索框长度
				searchTop:0,
				searchHeight:0,
			}
		},
		computed: {
			...mapGetters(["merchantClassify", "merchantType", 'isLogin', 'uid', 'merchantAPPInfo']),
		},
		// 滚动监听

		onPageScroll(e) {
			// 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
			uni.$emit('scroll');
		},
		onShow() {
			// #ifdef MP || APP-PLUS
			this.statusBarHeight = statusBarHeight;
			// #endif
			// #ifdef H5
			this.statusBarHeight = 20;
			// #endif
		},

		created() {
			// #ifdef MP
			// 获取窗口信息
			const windowInfo = uni.getWindowInfo()
			// 获取胶囊左边界坐标

			this.leftWindowWidth = uni.getMenuButtonBoundingClientRect().left;
			const query = uni.createSelectorQuery().in(this);
			const homeWidth = 0
			//搜索框宽度

			query.select('#home').boundingClientRect(data => {
				this.searchIptWindowWidth = this.leftWindowWidth - data.width - 60
			}).exec();
			// #endif
			
			// #ifdef MP
			const res = uni.getMenuButtonBoundingClientRect()
			const statusHeight = res.top //胶囊距离顶部
			const jnHeight = res.height //胶囊高度
			this.searchTop=statusHeight-this.statusBarHeight
			this.searchHeight=jnHeight
			// #endif
		},
		onLoad() {
			// #ifdef MP || APP-PLUS
			this.statusBarHeight = statusBarHeight;
			// #endif
			// #ifdef H5
			this.statusBarHeight = 20;
			// #endif
		},
		mounted: function() {
			// #ifdef MP || APP-PLUS
			this.statusBarHeight = statusBarHeight;
			// #endif

			// #ifdef H5
			this.statusBarHeight = 20;
			// #endif
			this.$store.dispatch('MerTypeList');
			this.skeletonShow = false;
		},
		methods: {
			goIndex() {
        this.$util.navigateTo('/pages/index/index');
			},
			// 后退
			returns: function() {
				uni.navigateBack()
			},
		},
	}
</script>


<style lang="scss" scoped>
	.select_nav {
		border-radius: 33rpx;
		border: 1px solid rgba(255, 255, 255, .5);
		color: #fff;
		font-size: 18px;
		line-height: 60rpx;
		align-items: center;
	}

	.nav_line {
		content: '';
		display: inline-block;
		width: 1px;
		height: 34rpx;
		background: #b3b3b3 !important;
		color: #b3b3b3 !important;
	}

	.navbarCon {
		padding: 10rpx 20rpx 0 20rpx;
		/* #ifdef H5 || APP */
		width: 800rpx !important;
		/* #endif */
	}

	.input {
		display: flex;
		align-items: center;
		/* #ifdef MP */
		//width: 42%;

		/* #endif */
		/* #ifndef MP */
		width: 538rpx;
		/* #endif */
		height: 55rpx;
		padding: 0 0 0 30rpx;
		border: 1px solid rgba(0, 0, 0, 0.07);
		border-radius: 33rpx;
		color: #D9D9D9;
		font-size: 26rpx;
		// margin: auto;

		background: rgba(255, 255, 255, 0.3);
		margin-left: 20rpx;

		.iconfont {
			margin-right: 20rpx;
			font-size: 26rpx;
			color: #D9D9D9;
		}
	}

	.tab-cont {
		background: #f5f5f5;
		min-height: 500rpx;
    overflow: auto;
	}

	.couponBox {
		/deep/.empty-box {
			padding-top: 200rpx;
			margin-top: 0 !important;
		}
	}

	.store-home {
		position: fixed;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		display: flex;
		flex-direction: column;
		padding-bottom: 100rpx;
		background: left top/750rpx 390rpx no-repeat fixed;
		overflow: hidden;
	}

	.fixed-top {
		top: 0;
		left: 0;
		z-index: 9;
		margin-bottom: 24rpx;
	}
	.searchIpt {
		/* #ifdef H5 || APP */
		width: 550rpx !important;
		height: 60rpx;
		line-height: 60rpx;
		/* #endif */
	}
</style>