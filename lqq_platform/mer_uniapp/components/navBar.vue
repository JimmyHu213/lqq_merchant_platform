<template>
	<view :data-theme="theme">
		<view class="cart_nav acea-row" :style="{ height: navH + 'rpx', background: isScrolling ? '#fff' : backgroundColor}"
			:class="isBackgroundColor?'backgroundColor':''">
			<!-- #ifdef MP-->
			<view class="mp-header" :style="{ height: navH + 'rpx'}">
				<view class="sys-head" :style="{ height: sysHeight+'px' }"></view>
        <view v-if="isShowMenu && isShowBack" class="select_nav flex justify-center align-center" id="home" :style="{top: sysHeight+6+'px'}">
          <text class="iconfont f-s-36 icon-ic_left px-20" @tap="returns"></text>
          <text class="iconfont f-s-36 icon-ic_menu3 px-20" @tap.stop="showNav"></text>
          <text class="nav_line"></text>
        </view>
        <view v-if="!isShowMenu && isShowBack" @tap="returns" class="return-box" :style="{top: sysHeight+6+'px'}">
          <text class="iconfont f-s-36 icon-ic_leftarrow px-20"  :style="{ color:iconColor}"></text>
        </view>
        <view class="nav_title" :style="{ height:  '80rpx',lineHeight:  '80rpx', color:iconColor}">
          {{navTitle}}
          <slot name="default"></slot>
        </view>
			</view>
			<!-- #endif -->
			<!-- #ifdef H5 -->
			<view v-show="isShowMenu && isShowBack" id="home" class="home acea-row row-center-wrapper iconfont f-s-36 icon-ic_leftarrow h5_back z-999"
				:style="{ top: homeTop + 'rpx'}" @tap="goToHome">
			</view>
			<view class="nav_title" :style="{ height:  '80rpx',lineHeight:  '80rpx', color:iconColor}">
				{{navTitle}}
				<slot name="default"></slot>
			</view>
			<view v-show="isShowMenu && isShowBack" class="right_select" :style="{ top: homeTop + 'rpx' }" @tap="showNav">
				<text class="iconfont icon-ic_more"></text>
			</view>
			<!-- #endif -->
			<!-- #ifdef APP-PLUS -->
			<view v-if="isShowBack" id="home" class="home acea-row row-center-wrapper f-s-36 iconfont icon-ic_leftarrow h5_back z-999"
				:style="{ top: (homeTop+20) + 'rpx' , color:iconColor}" @tap="returns">
			</view>
			<view v-if="isShowBack" class="nav_title" :style="{ top: (homeTop+20) + 'rpx' , color:iconColor}">
				{{navTitle}}
				<slot name="default"></slot>
			</view>
			<!-- #endif -->
		</view>


		<view class="dialog_nav" :style='"top:"+navH+"rpx;"' v-show="currentPage" style="z-index: 99999999;">
			<view class="dialog_nav_item" v-for="(item,index) in selectNavList" :key="index"
				@click="linkPage(item.url)" :class="item.after">
				<text class="iconfont" :class="item.icon"></text>
				<text class="pl-20">{{item.name}}</text>
			</view>
		</view>
		<view v-if="isHeight" :style="{ height: `${navH}rpx` }"></view>
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
	import animationType from '@/utils/animationType.js'
	import {
		mapGetters
	} from "vuex";
	let app = getApp();
	let sysHeight = uni.getSystemInfoSync().statusBarHeight;
	export default {
    props: {
      //是否展示返回按钮
      isShowBack: {
        type: Boolean,
        default: true
      },
      navTitle: {
        type: String,
        default: ''
      },
      backgroundColor: {
        type: String,
        default: 'transparent'
      },
      //头部背景色是否使用主题色颜色
      isBackgroundColor: {
        type: Boolean,
        default: true
      },
      // icon、标题颜色
      iconColor: {
        type: String,
        default: '#333333'
      },
      isHeight: {
        type: Boolean,
        default: true
      },
      //是否展示菜单标
      isShowMenu: {
        type: Boolean,
        default: true
      },
      // 滚动至下部
      isScrolling: {
        type: Boolean,
        default: false
      },
      goBack: {
        type: String,
        default: ''
      }
    },
    computed: mapGetters(['globalData']),
		data() {
			return {
				theme: app.globalData.theme,
				sysHeight: sysHeight,
				homeTop: 18,
				navH: "",
				currentPage: false,
				returnShow: false,
				selectNavList: [{
						name: '首页',
						icon: 'icon-ic_mall',
						url: '/pages/index/index',
            after:'dialog_after'
					},
					{
						name: '搜索',
						icon: 'icon-ic_search',
						url: '/pages/goods/goods_search/index',
            after:'dialog_after'
					},
					// {
					// 	name: '我的收藏',
					// 	icon: 'icon-shoucang3',
					// 	url: '/pages/goods/user_goods_collection/index',
          //   after:'dialog_after'
					// },
					{
						name: '个人中心',
						icon: 'icon-a-ic_user1',
						url: '/pages/user/index'
					},
				]
			}
		},
		created() {
			var pages = getCurrentPages();
			this.returnShow = pages.length === 1 ? false : true;
			this.$nextTick(function() {
				// #ifdef MP
				const menuButton = uni.getMenuButtonBoundingClientRect();
				const query = uni.createSelectorQuery().in(this);
				query
					.select('#home')
					.boundingClientRect(data => {
						if (data) this.homeTop = menuButton.top * 2 + menuButton.height - data.height;
					})
					.exec();
				// #endif
				// #ifdef APP-PLUS
				this.homeTop = sysHeight * 2;
				// #endif
			});
			// #ifdef MP || APP-PLUS
			// 获取导航高度；
			this.navH = this.globalData.navHeight;
			// #endif
			// #ifdef H5
			this.navH = 80;
			// #endif
			this.$emit('getNavH', this.navH)
		},
		methods: {
			returns: function() {
				if(this.returnShow){
          if(this.goBack){
            this.$util.navigateTo(`${this.goBack}`);
          }else{
            uni.navigateBack(-1);
          }
				}else{
          this.$util.navigateTo('/pages/index/index');
				}
			},
			//返回首页
			goToHome() {
        uni.navigateBack(-1);
			},
			showNav() {
				this.currentPage = !this.currentPage;
			},
			//下拉导航页面跳转
			linkPage(url) {
				this.$util.navigateTo(url);
				this.currentPage = false
			},
			touchStart() {
				this.currentPage = false;
			}
		}
	}
</script>

<style scoped lang="scss">
	/* #ifdef MP || APP-PLUS */
	.mp-header {
		z-index: 999;
		position: fixed;
		left: 0;
		top: 0;
		width: 100%;
		/* #ifdef H5 */
		padding-bottom: 20rpx;
		/* #endif */
	}

	/* #endif */
	.pl-20 {
		padding-left: 20rpx;
	}

	.backgroundColor {
		@include main_bg_color(theme);
	}

	.cart_nav {
		position: fixed;
		top: 0;
		left: 0;
		z-index: 99;
		width: 100%;
	}

	.navbarCon {
		position: absolute;
		bottom: 0;
		height: 100rpx;
		width: 100%;
	}

	.h5_back {
		color: #000;
		position: fixed;
		left: 20rpx;
		text-align: center;
		width: 50rpx;
		height: 50rpx;
		line-height: 50rpx;
		background: rgba(255, 255, 255, 0.3);
		border: 1px solid rgba(0, 0, 0, 0.1);
		border-radius: 50%;
	}
  .return-box{
    height: 60rpx;
    line-height: 58rpx;
    left: 14rpx;
    position: fixed;
    font-size: 18px;
    z-index: 999;
  }
	.select_nav {
		width: 170rpx !important;
		height: 60rpx !important;
		border-radius: 33rpx;
		background: rgba(255, 255, 255, 0.6);
		border: 1rpx solid rgba(0, 0, 0, 0.1);
		color: #000;
		position: fixed;
		font-size: 18px;
		line-height: 58rpx;
		z-index: 1000;
		left: 14rpx;
	}

	.px-20 {
		padding: 0 20rpx 0;
	}

	.nav_line {
		content: '';
		display: inline-block;
		width: 1px;
		height: 34rpx;
		background: #e8dfdf;
		position: absolute;
		left: 0;
		right: 0;
		margin: auto;
	}

	.container_detail {
		/* #ifdef MP */
		margin-top: 32rpx;
		/* #endif */
	}

	.tab_nav {
		width: 100%;
		height: 48px;
		padding: 0 30rpx 0;
	}

	.nav_title {
		//width: 200rpx;
    font-weight: 500;
		color: #fff;
		font-size: 36rpx;
		position: fixed;
		text-align: center;
		left: 0;
		right: 0;
		margin: auto;
	}

	.right_select {
		width: 50rpx;
		height: 50rpx;
		background: rgba(255, 255, 255, 0.3);
		border: 1px solid rgba(0, 0, 0, 0.1);
		border-radius: 50%;
		position: fixed;
		right: 20rpx;
		text-align: center;
		line-height: 50rpx;
	}

	.px-20 {
		padding: 0 20rpx 0;
	}

	.justify-center {
		justify-content: center;
	}

	.align-center {
		align-items: center;
	}

	.dialog_nav {
		position: fixed;
		/* #ifdef MP */
		left: 14rpx;
		/* #endif */
		/* #ifdef H5 || APP-PLUS*/
		right: 14rpx;
		/* #endif */
		background: #FFFFFF;
		box-shadow: 0px 0px 16rpx rgba(0, 0, 0, 0.08);
		z-index: 99999999 !important;
		border-radius: 14rpx;

		&::before {
			content: '';
			width: 0;
			height: 0;
			position: absolute;
			/* #ifdef MP */
			left: 0;
			right: 0;
			margin: auto;
			/* #endif */
			/* #ifdef H5 || APP-PLUS */
			right: 8px;
			/* #endif */
			top: -9px;
			border-bottom: 10px solid #fff;
			border-left: 10px solid transparent;
			/*transparent 表示透明*/
			border-right: 10px solid transparent;
		}
	}

	.dialog_nav_item {
		width: 100%;
		height: 84rpx;
		line-height: 84rpx;
		padding: 0 28rpx 0;
		box-sizing: border-box;
		border-bottom: #eee;
		font-size: 28rpx;
		color: #333;
		position: relative;

		.iconfont {
			font-size: 32rpx;
      color: #333 !important;
		}
	}
  .dialog_after{
    ::after {
      content: '';
      position: absolute;
      width: 86px;
      height: 1px;
      background-color: #EEEEEE;
      bottom: 0;
      right: 0;
    }
  }
</style>