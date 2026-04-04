<template>
	<view :data-theme="theme" class='productList'>
    <!-- #ifdef MP-->
    <view class='cart_nav'>
      <nav-bar iconColor='#fff' @getNavH='getNavH' ref="navBarRef" navTitle='商品搜索' :isShowMenu="false">
      </nav-bar>
    </view>
    <!-- #endif -->
   <view :style='{ top: marTop + "rpx"}' class="fixed w-100-p111- z-10">
     <view class='search bg-color acea-row row-between-wrapper' >
       <!-- #ifdef H5 -->
       <view class="iconfont icon-ic_left" @click="goback()"></view>
       <!-- #endif -->
       <view :class="tabIndex !== 2?'searchIpt':''" class='input flex-between-center'>
         <view class="flex-y-center w-100-p111-">
           <text class='iconfont icon-ic_search mr20'></text>
           <input :placeholder="tabIndex===1?'搜索商品名称':'搜索店铺名称'" placeholder-class='placeholder'
                  confirm-type='search' name="search" v-model="keyword" @confirm="searchSubmit"
                  maxlength="20" />
         </view>
         <text class="iconfont icon-ic_close1" @click="handleClear"></text>
       </view>
       <view v-if="tabIndex === 2" class='iconfont icon-ic_sort' @click='open(2)'></view>
     </view>
     <view class="nav-wrapper mr-20px" v-if="merId===0 && cid === 0" :class="{'merTab':tabIndex===1,'navWrapperHeight': tabIndex===2&&merchantList.length}">
       <view class="tab-bar">
         <view class="tab-item" :class="{on:tabIndex===1}" @click="changetab(1)">商品</view>
         <view class="tab-item" :class="{on:tabIndex===2}" @click="changetab(2)">店铺</view>
       </view>
       <view v-if="merchantList.length>0 && tabIndex===2" class="gradient-bg absolute"></view>
     </view>
     <view v-if="(tabIndex===2 && !merchantList.length) || tabIndex===1" class='nav' :class="{'mer-nav':merId > 0 || cid > 0, 'bg-f5f5':tabIndex===2}">
       <view v-if="tabIndex===1" class="acea-row row-middle">
         <view class='item' :class='title ? "font_color":""' @click='set_where(1,tabIndex)'>
           {{title ? title:'默认'}}
         </view>
         <view class='item flex-center' @click='set_where(2,tabIndex)'>
           价格
           <image v-if="price==1" :src='upPng'></image>
           <image v-else-if="price==2" :src='downPng'></image>
           <image v-else :src="urlDomain+'crmebimage/presets/horn.png'"></image>
         </view>
         <view class='item-c item flex-center' @click='set_where(3,tabIndex)'>
           销量
           <image v-if="stock==1" :src='upPng'></image>
           <image v-else-if="stock==2" :src='downPng'></image>
           <image v-else :src="urlDomain+'crmebimage/presets/horn.png'"></image>
         </view>
         <!-- down -->
         <view class="item-shu"></view>
         <view class='item-icon iconfont' :class="is_switch?'icon-a-ic_Imageandtextsorting':'icon-a-ic_Picturearrangement'" @click='Changswitch()'>
         </view>
       </view>
     </view>
   </view>
    <view>
<!--      && cid === 0-->
      <view v-if="tabIndex===1" :class="(merId > 0 || cid > 0) ? 'mer-listBox': 'proList'">
        <view class="goods-bg fixed"></view>
        <view v-if="productList.length>0">
          <product-list-vertical v-if="!is_switch" :productLists="productList" :is_switch="is_switch" :merId="merId" :cid="cid"></product-list-vertical>
          <view v-else class="goods pt-10">
            <WaterfallsFlow :wfList='productList' :type="1">
              <template slot-scope="{item}">
                <WaterfallsFlowItem :item="item" :type="1" :isStore="1" />
              </template>
            </WaterfallsFlow>
          </view>
        </view>
        <view v-if="productList.length>0" class='loadingicon acea-row row-center-wrapper'>
          <text class='loading iconfont icon-jiazai'
                :hidden='loading==false'></text>{{productList.length>0?loadTitle:''}}
        </view>
      </view>
      <view class="merList" v-if="tabIndex == 2">
<!--        <view v-show="merchantList.length===0" class='nav acea-row row-middle mer-height'-->
<!--              :class="(merId > 0 || cid > 0) ? 'mer-nav' : ''">-->
<!--        </view>-->
        <view class="px-24 relative z-999">
          <merchant-list :merchantList="merchantList"></merchant-list>
          <view :hidden='loading==false' class='loadingicon acea-row row-center-wrapper'>
            <text class='loading iconfont icon-jiazai'></text>
          </view>
        </view>
      </view>
      <view class='noCommodity relative' :class="{'mer-mt194': tabIndex ==2}" :style="merId?'padding-top: 42%;':''"
            v-if="(productList.length==0 && isShow && tabIndex===1 && !loading )  || (merchantList.length==0 && isShow && tabIndex===2 && !loading)">
        <view class='pictrue'>
          <image :src="urlDomain+'crmebimage/presets/noSearch.png'"></image>
        </view>
        <text class="text-ccc mb-88">{{tabIndex===1?'暂无商品~':'暂无店铺~'}}</text>
        <recommend v-if="!merId" ref="recommendIndex"></recommend>
      </view>
    </view>


		<tui-drawer mode="right" :visible="rightDrawer" @close="closeDrawer" mask maskClosable>
			<merSeach :whereMer="whereMer" @close="closeDrawer" @confirm="confirm"></merSeach>
		</tui-drawer>
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
		productList
	} from '@/api/product.js';
	import {
		getMerSearchApi,
		getMerProListApi
	} from '@/api/merchant.js';
	import {
		mapGetters
	} from "vuex";
	import {
		setThemeColor
	} from '@/utils/setTheme.js'
	import merSeach from '../components/merSeach/index.vue'
	import merchantList from '@/components/merchantList/index.vue'
	import WaterfallsFlow from '@/components/WaterfallsFlow/WaterfallsFlow.vue'
	import tuiDrawer from '@/components/tui-drawer/tui-drawer.vue'
	import recommend from "@/components/base/recommend.vue";
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	import WaterfallsFlowItem from '@/components/WaterfallsFlowItem/WaterfallsFlowItem.vue'
  import productListVertical from '../components/productListVertical/index.vue'
  import navBar from '@/components/navBar';
	let app = getApp();
	export default {
		computed: mapGetters(['uid']),
		components: {
			merchantList,
			merSeach,
			WaterfallsFlow,
			tuiDrawer,
			recommend,
			easyLoadimage,
			WaterfallsFlowItem,
      productListVertical,
      navBar
		},
		mounted() {
			this.$store.dispatch('MerCategoryList');
			this.$store.dispatch('MerTypeList');
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				keyword: '',
				drawers: 0,
				tabIndex: 1,
				productList: [],
				is_switch: true,
				where: {
					keyword: '',
					priceOrder: '',
					salesOrder: '',
					//news: 0,
					page: 1,
					limit: 20,
					cid: '', //商城使用
					merId: '',
					cids: '' //商户使用
				},
				cid: 0, //分类id
				price: 0,
				stock: 0,
				nows: false,
				loadend: false,
				loading: false,
				loadTitle: '加载更多',
				title: '',
        theme: this.$Cache.get('theme'),
				upPng: '',
				downPng: '',
				whereMer: {
					categoryIds: '',
					isSelf: '',
					keywords: '',
					page: 1,
					limit: 20,
					typeIds: ''
				},
				rightDrawer: false,
				merchantList: [],
				merId: 0,
				goodScroll: true,
				isShow: false,
				bgColor: '#e93323',
        marTop: 0,
			};
		},
		// 滚动监听
		onPageScroll(e) {
			// 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
			uni.$emit('scroll');
		},
		onLoad: function(options) {
			this.bgColor = setThemeColor();
			uni.setNavigationBarColor({
				frontColor: '#ffffff',
				backgroundColor:this.bgColor,   
			});
			
			this.downPng = `${this.urlDomain}crmebimage/presets/down_red.png`;
			this.upPng = `${this.urlDomain}crmebimage/presets/up_red.png`;
			this.merId = options.merId ? Number(options.merId) : 0;
			this.$set(this, 'cid', Number(options.cid) || 0);
			this.title = options.title || '';
			this.$set(this, 'keyword', options.searchValue || '');
			if (this.tabIndex === 1) this.get_product_list();
			switch (this.theme) {
				case 'theme2':
					this.upPng = `${this.urlDomain}crmebimage/presets/up_orange.png`;
					this.downPng = `${this.urlDomain}crmebimage/presets/down_orange.png`;
					break;
				case 'theme3':
					this.upPng = `${this.urlDomain}crmebimage/presets/up_green.png`;
					this.downPng = `${this.urlDomain}crmebimage/presets/down_green.png`;
					break;
				case 'theme4':
					this.upPng = `${this.urlDomain}crmebimage/presets/up_blue.png`;
					this.downPng = `${this.urlDomain}crmebimage/presets/down_blue.png`;
					break;
				case 'theme5':
					this.upPng = `${this.urlDomain}crmebimage/presets/up_pink.png`;
					this.downPng = `${this.urlDomain}crmebimage/presets/down_pink.png`;
					break;
				default:
					this.upPng = `${this.urlDomain}crmebimage/presets/up_red.png`;
					this.downPng = `${this.urlDomain}crmebimage/presets/down_red.png`;
					break;
			}
		},
		methods: {
      handleClear(){
        this.keyword = ''
      },
      // 获取头部高度
      getNavH(marTop) {
        this.marTop = marTop;
      },
			confirm(data) {
				this.whereMer.typeIds = data.typeId;
				this.whereMer.categoryIds = data.categoryId;
				this.whereMer.keywords = this.where.keyword;
				this.loadend = false;
				this.$set(this.whereMer, 'page', 1)
				this.merchantList = [];
				this.rightDrawer = false
				this.getMerStreet();
			},
			changetab(n) {
				this.tabIndex = n;
				this.nows = 1;
				this.stock = 0;
				this.loadend = false;
				this.loading = false;
				if (n === 2) {
					this.merchantList = [];
					this.$set(this.whereMer, 'page', 1);
					this.getMerStreet();
					this.title = '';
				} else {
					this.whereMer.typeIds = '';
					this.whereMer.categoryIds = '';
					this.productList = [];
					this.$set(this.where, 'page', 1);
					this.get_product_list();
				}
			},
			open(n) {
				if (this.tabIndex === 2) {
					this.rightDrawer = true;
					this.drawers = n;
				}
			},
			closeDrawer(e) {
				this.rightDrawer = false
				if (!e) {
					this.rightDrawer = false
				}
			},
			goback() {
				// #ifdef H5
				return history.back();
				// #endif
				// #ifndef H5
				return uni.navigateBack({
					delta: 1,
				})
				// #endif
			},
			getMerStreet: function(isPage) {
				this.skeletonShow = true
				let that = this;
				that.setWhere();
				if (that.loadend) return;
				if (that.loading) return;
				that.loading = true;
				if (isPage === true) that.$set(that, 'merchantList', []);
				that.loadTitle = '';
				that.whereMer.keywords = encodeURIComponent(that.keyword);
				getMerSearchApi(that.whereMer).then(res => {
					let list = res.data.list;
					let merchantList = that.$util.SplitArray(list, that.merchantList);
					let loadend = list.length < that.whereMer.limit;
					that.loadend = loadend;
					that.loadTitle = loadend ? '没有了' : '加载更多';
					that.$set(that, 'merchantList', merchantList);
					that.$set(that.whereMer, 'page', that.whereMer.page + 1);
					that.loading = false;
					this.skeletonShow = false;
					this.isShow = true
				}).catch(err => {
					that.loading = false;
					that.goodScroll = false;
					that.loadTitle = '加载更多';
				});
			},
			Changswitch: function() {
				let that = this;
				that.is_switch = !that.is_switch
			},
			searchSubmit: function(e) {
				let that = this;
				that.loadend = false;
				that.$set(that, 'keyword', e.detail.value || '');
				if (that.tabIndex === 1) {
					that.$set(that.where, 'page', 1)
					this.get_product_list(true);
				} else {
					that.$set(that, 'merchantList', []);
					this.$set(this.whereMer, 'page', 1)
					this.getMerStreet();
				}
			},
			//点击事件处理
			set_where: function(e, n) {
				switch (e) {
					case 1:
						this.price = 0;
						this.stock = 0;
						break;
					case 2:
						if (this.price == 0) this.price = 1;
						else if (this.price == 1) this.price = 2;
						else if (this.price == 2) this.price = 0;
						this.stock = 0;
						break;
					case 3:
						if (this.stock == 0) this.stock = 1;
						else if (this.stock == 1) this.stock = 2;
						else if (this.stock == 2) this.stock = 0;
						this.price = 0
						break;
					case 4:
						this.price = 0;
						this.stock = 0;
						break;
				}
				if (n === 1) {
					this.loadend = false;
					this.$set(this.where, 'page', 1);
					this.get_product_list(true);
				} else {
					this.nows = 1;
				}
			},
			//设置where条件
			setWhere: function() {
				if (this.price == 0) this.where.priceOrder = '';
				else if (this.price == 1) this.where.priceOrder = 'asc';
				else if (this.price == 2) this.where.priceOrder = 'desc';
				if (this.stock == 0) this.where.salesOrder = '';
				else if (this.stock == 1) this.where.salesOrder = 'asc';
				else if (this.stock == 2) this.where.salesOrder = 'desc';
				//this.where.news = this.nows ? 1 : 0;
			},
			//查找产品
			get_product_list: function(isPage) {
				let that = this;
				that.setWhere();
				if (that.loadend) return;
				if (that.loading) return;
				if (isPage === true) that.$set(that, 'productList', []);
				that.loading = true;
				that.loadTitle = '';
				if (this.merId > 0) that.where.merId = that.merId
				that.where.keyword = encodeURIComponent(that.keyword);
				if (that.cid === 0) {
					that.where.cid = '';
					that.where.cids = '';
				} else {
					if (this.merId > 0){
						that.where.cids = that.cid
						that.where.cid = '';
					}else{
						that.where.cid = that.cid;
						that.where.cids = ''
					}
					
				}
				this.merId === 0 ? productList(that.where).then(res => {
					let list = res.data.list;
					let productList = that.$util.SplitArray(list, that.productList);
					let loadend = list.length < that.where.limit;
					that.loadend = loadend;
					that.loading = false;
					that.loadTitle = loadend ? '已全部加载' : '加载更多';
					that.$set(that, 'productList', productList);
					that.$set(that.where, 'page', that.where.page + 1);
					this.isShow = true
				}).catch(err => {
					that.loading = false;
					that.loadTitle = '加载更多'
				}) : getMerProListApi(that.where).then(res => {
					let list = res.data.list;
					let productList = that.$util.SplitArray(list, that.productList);
					let loadend = list.length < that.where.limit;
					that.loadend = loadend;
					that.loading = false;
					that.loadTitle = loadend ? '已全部加载' : '加载更多';
					that.$set(that, 'productList', productList);
					that.$set(that.where, 'page', that.where.page + 1);
					this.isShow = true
				}).catch(err => {
					that.loading = false;
					that.loadTitle = '加载更多'
				})
			},
		},
		onReachBottom() {
			if(this.tabIndex === 2){
				this.getMerStreet();
			}
			if (this.productList.length > 0) {
				this.get_product_list();
			}
			if ((this.productList.length == 0 && this.isShow && this.tabIndex === 1 && !this.loading) || (this.merchantList
					.length == 0 && this.isShow && this.tabIndex === 2 && !this.loading)) {
				this.$refs.recommendIndex.get_host_product();
			}
		}
	}
</script>
<style>
	body {
		height: auto !important;
	}
</style>
<style scoped lang="scss">
/deep/.nav_title{
  font-size: 34rpx !important;
  font-weight: 600;
}
.mb-88{
  margin-bottom: 88rpx;
}
.bg-f5f5{
  background-color: #F5F5F5 !important;
}
/deep/.wf-page{
  background: none !important;
}
.navWrapperHeight{
  height: 195rpx !important;
}
	.mer-height{
		background-color: #F5F5F5 !important;
		height: 20rpx !important;
	}
	.goods-bg{
		width: 100%;
		height: 240rpx;
		background: linear-gradient( 180deg, #FFFFFF 0%, #F5F5F5 100%);
	}
	.mt80 {
		margin-top: 80rpx;
	}

	.searchIpt {
		/* #ifdef H5 */
		width: 91% !important;
		/* #endif */
		/* #ifndef H5 */
		width: 100% !important;
		/* #endif */
	}
  //  .merTab{
	//	height: 134rpx !important;
	//}

	.text-ccc {
		text-align: center;
		display: block;
	}

	.icon-ic_left {
		color: #fff;
	}

	.goods {
		display: flex;
		flex-wrap: wrap;
		justify-content: space-between;
		padding-left: 24rpx;
		background-color: #F5F5F5;
		width: 100%;
	}

	.proList {
		margin-top: 230rpx;
	}

	.merList {
		position: relative;
		top: 154rpx;
		z-index: 66;
	}

	.merBox {
		/deep/.tui-drawer-container {
			width: 635rpx;
		}
	}

	.mer {
		&-nav {
			top: 64rpx !important;
			border-radius: inherit !important;
      position: relative;
		}

		&-list {
			//margin-top: 232rpx !important;
		}

		&-listBox {
      margin-top: 168rpx !important;
		}

		&-mt194 {
			margin-top: 156rpx !important;
		}

		&-mt100 {
			top: -200rpx !important;
		}
	}

	.no-shop {
		margin-top: 6rpx;
		background-color: #fff;
		padding-bottom: calc(100% - 109rpx);

		.pictrue {
			display: flex;
			flex-direction: column;
			align-items: center;

			//color: $uni-nothing-text;
			image {
				width: 414rpx;
				height: 380rpx;
			}
		}
	}

	.with50 {
		width: 50% !important;
	}

	.nav-wrapper {
		width: 100%;
		height: 134rpx;
		@include main_bg_color(theme);

		.tab-bar {
			display: flex;
			align-items: center;

			.tab-item {
				position: relative;
				flex: 1;
				display: flex;
				justify-content: center;
				align-items: center;
				padding-bottom: 40rpx;
				color: #fff;
				font-size: 28rpx;
				font-weight: bold;
				// margin-top: -104rpx;

				&::after {
					content: ' ';
					position: absolute;
					left: 50%;
					bottom: 34rpx;
					width: 60rpx;
					height: 3rpx;
					background: transparent;
					transform: translateX(-50%);
				}

				&.on {
					&::after {
						background: #fff;
					}
				}
			}
		}
	}

	.font_color {
		@include main_color(theme);
	}

	.x-money {
		font-size: 38rpx;
		font-weight: 900;
		@include price_color(theme);
	}

	.iconfont {
		//color: #fff;
	}

	.listBox {
		margin-top: 236rpx !important;
		background-color: #FFFFFF;
	}

	.fixed-top {
		position: fixed;
		top: 0;
		left: 0;
		z-index: 9;
	}

	.productList .search {
		width: 100%;
		height: 86rpx;
		padding: 0 24rpx;
		box-sizing: border-box;
		left: 0;
		z-index: 666;
		color: #fff;
	}

	.productList .search .input {
    /* #ifdef H5 */
    width: 590rpx;
    /* #endif */
      /* #ifdef MP || APP */
    width: 640rpx;
    /* #endif */
		height: 60rpx;
		background-color: #fff;
		border-radius: 50rpx;
		padding: 0 24rpx;
		box-sizing: border-box;
		color: #333;
	}

	.productList .search .input input {
		width: 84%;
		height: 100%;
		font-size: 26rpx;
	}

	.productList .search .input .placeholder {
		color: #CCCCCC;
	}

	.productList .search .icon-yangshi1,
	.icon-ic_sort {
		color: #fff;
		width: 62rpx;
		font-size: 40rpx;
		height: 86rpx;
		line-height: 86rpx;
		text-align: right;
	}

	.productList .nav {
		height: 84rpx;
    line-height: 84rpx;
		color: #333;
		left: 0;
		width: 100%;
		font-size: 28rpx;
		background-color: #fff;
		border-radius: 24rpx 24rpx 0rpx 0rpx;
		padding-left: 20rpx;
    margin-top: -64rpx;
	}

	.productList .nav .item {
		width: 30%;
		text-align: center;

		.iconfont {
			//color: #fff;
		}
	}
	.item-c{
		width: 27% !important;
	}
	.item-icon{
		width: 8%;
		margin-left: 30rpx;
    font-size: 28rpx;
	}
	.item-shu{
		width: 1px;
		height: 12px;
		background: #666666;
		opacity: 0.2;
	}
	.productList .nav .item.font-color {
		font-weight: bold;
	}

	.productList .nav .item image {
		width: 15rpx;
		height: 19rpx;
		margin-left: 10rpx;
	}
	.row-between-wrapper .input {
		height: 60rpx;
		margin: 10rpx 0 16rpx;
	}
	.box-line2 {
		overflow: hidden;
	}
</style>