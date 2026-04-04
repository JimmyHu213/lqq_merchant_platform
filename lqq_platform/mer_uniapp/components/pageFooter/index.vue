<template>
	<!-- 底部导航 -->
	<view :data-theme="theme">
		<view v-if="bottomNavigationList.length">
			<view class="page-footer" id="target" :style="[isSmallPage?boxStyle:'']">
				<view :style="[bgColor]" class="acea-row row-middle row-around bg-box">
					<view class="foot-item" v-for="(item,index) in bottomNavigationList" :key="index"
						@click="goRouter(item)">
						<block v-if="item.link.split('?')[0] == activeRouter">
							<image :src="item.checked"></image>
							<view v-if="isSmallPage" class="txtchecked" :style="[checkColor]">{{item.name}}</view>
							<view v-else class="txt">{{item.name}}</view>
						</block>
						<block v-else>
							<image :src="item.unchecked"></image>
							<view class="unchecked" :style="[isSmallPage?fontColor:'']">{{item.name}}</view>
						</block>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		mapState,
		mapGetters
	} from "vuex"
	import {
		getBottomNavigationApi
	} from '@/api/api.js';
  import {getMerchantNavigationApi} from "../../api/merchant";
	let app = getApp();
	export default {
		name: 'pageFooter',
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			},
			isSmallPage: { //是否是微页面
				type: Boolean,
				default: () => false
			},
			// 控制是否是开屏广告展示的时候
			guidePages: {
				type: Boolean,
				default: () => false
			},
      merId: {
        type: Number | String,
        default: 0
      },
      fromTo: { //页面来源，为了区分店铺中底部导航使用的微页面，展示店铺配置的底部导航，
        type: String,
        default: ''
      }
		},
		computed: {
			//外部盒子
			boxStyle() {
				if (this.dataConfig) {
					return {
						borderRadius: this.dataConfig.bgStyle.val ? this.dataConfig.bgStyle.val + 'px' : '0',
						padding: '0' + ' ' + this.dataConfig.lrConfig.val + 'px' + ' ' + 0
					}
				}
			},
			bgColor() {
				if (this.dataConfig) {
					return {
						background: `linear-gradient(${this.dataConfig.bgColor.color[0].item}, ${this.dataConfig.bgColor.color[1].item})`,
					}
				}
			},
			//标签文字颜色
			fontColor() {
				if (this.dataConfig) {
					return {
						color: this.dataConfig.fontColor.color[0].item
					};
				}

			},
			//选中颜色
			checkColor() {
				if (this.dataConfig) {
					return {
						color: this.dataConfig.themeStyleConfig.tabVal ? this.dataConfig.checkColor.color[0].item : this
							.themeColor
					};
				}
			},
		},
		created() {
			let routes = getCurrentPages(); //获取当前打开过的页面路由数组
			let curRoute = routes[routes.length - 1].route //获取当前页面路由
			this.activeRouter = '/' + curRoute;
			if (this.isSmallPage && !this.fromTo) {
        let data = this.dataConfig.menuList.list;
				this.bottomNavigationList = (data || []).map(item => ({
          ...item,
          link: this.$util.appendMerIdToLink(item.link, this.merId, false),
        }));
			}else if(this.activeRouter.includes('merchant') || this.fromTo){
        this.getMerNavigation()
      } else {
				this.navigationInfo();
			}
		},
		watch: {
			guidePages: {
				handler: function(newV, oldV) {
					if (newV && this.isCustom == 1) {
						uni.hideTabBar()
					} else if (this.isCustom == 1) {
						uni.hideTabBar()
					} else if (newV && this.isCustom == 0) {
						uni.hideTabBar()
					} else {
						uni.showTabBar();
					}
				},
				deep: true
			}
		},
		data() {
			return {
				theme: this.$Cache.get('theme'),
				isCustom: '',
				bottomNavigationList: [],
				activeRouter: '',
				themeColor: this.$options.filters.filterTheme(app.globalData.theme)
			}
		},
		methods: {
      // 商户底部导航
      async getMerNavigation() {
         let data = await this.$store.dispatch("getMerNavigation", this.merId);
        this.bottomNavigationList  = (data || []).map(item => ({
          ...item,
          link: this.$util.appendMerIdToLink(item.linkUrl, this.merId, true),
          checked: item.selectedIconLinkUrl,
          unchecked: item.unselectedIconLinkUrl
        }));
      },
			navigationInfo() {
				getBottomNavigationApi().then(res => {
					let data = res.data;
					this.isCustom = data.isCustom; //是否使用自定义导航，1使用，0不使用
					this.$store.commit('BottomNavigationIsCustom', this.isCustom == 1 ? true : false);
					if (data.isCustom == 1) {
						uni.hideTabBar()
						this.bottomNavigationList = data.bottomNavigationList;
					} else {
						uni.showTabBar();
					}
				})
			},
			goRouter(item) {
				this.$store.commit('Change_Advertisement', false);
				var pages = getCurrentPages();
				var page = (pages[pages.length - 1]).$page.fullPath;
				if (item.link == page) return
				if (['/pages/index/index', '/pages/order_addcart/order_addcart',
						'/pages/user/index', '/pages/discover_index/index', '/pages/goods_cate/index'
					].indexOf(item.link) > -1) {
					uni.switchTab({
						url: item.link,
						fail(err) {
							uni.redirectTo({
								url: item.link
							})
						}
					})
				} else {
					uni.redirectTo({
						url: item.link
					})
				}
			}
		}
	}
</script>

<style scoped lang="scss">
	.overlay {
		position: absolute;
		bottom: 0;
		left: 0;
		width: 100%;
		height: calc(98rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		height: calc(98rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
	}

	.unchecked {
		color: #333;
		font-size: 24rpx;
	}

	.page-footer {
		position: fixed;
		bottom: 0;
		z-index: 998;
		display: flex;
		align-items: center;
		justify-content: space-around;
		width: 100%;
		height: calc(98rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		height: calc(98rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
		box-sizing: border-box;
		border-top: solid 1rpx #F3F3F3;
		background-color: #fff;
		box-shadow: 0px 0px 17rpx 1rpx rgba(206, 206, 206, 0.32);
		padding-bottom: constant(safe-area-inset-bottom); ///兼容 IOS<11.2/
		padding-bottom: env(safe-area-inset-bottom); ///兼容 IOS>11.2/

		.foot-item {
			display: flex;
			width: max-content;
			align-items: center;
			justify-content: center;
			flex-direction: column;
			position: relative;

			.count-num {
				position: absolute;
				display: flex;
				justify-content: center;
				align-items: center;
				width: 40rpx;
				height: 40rpx;
				top: 0rpx;
				right: -15rpx;
				color: #fff;
				font-size: 20rpx;
				background-color: #FD502F;
				border-radius: 50%;
				padding: 4rpx;
			}
		}

		.bg-box {
			width: 100%;
			height: 100%;
		}

		.foot-item image {
			height: 40rpx;
			width: 40rpx;
			text-align: center;
			margin: 0 auto;
		}

		.txtchecked {
			font-size: 24rpx;
		}

		.foot-item .txt {
			font-size: 24rpx;
      color: var(--view-theme);
			//@include main-color(theme);
		}
	}
</style>