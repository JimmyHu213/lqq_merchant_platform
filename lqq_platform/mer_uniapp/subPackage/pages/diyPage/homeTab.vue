<template>
	<!-- tab选项卡 -->
	<view class="index-product-wrapper" :style="[mbConfig]">
		<view class="nav-bd longTab" :style="[tabBgColor]">
			<scroll-view scroll-x="true" style="white-space: nowrap; display: flex" :scroll-left="tabLeft">
				<view class="longItem"
					:style="'color:' + (index == ProductNavindex ? checkColor : fontColor)+';--color:'+checkColor"
					:data-index="index" :class="index===ProductNavindex?'click':''" v-for="(item,index) in navList"
					:key="index" :id="'id'+index" @click="ProductNavTab(item, index)">{{ item.val }}
				</view>
			</scroll-view>
		</view>
		<view :style="[boxStyle]">
			<!-- 单列 -->
			<block v-if="itemStyle == 0">
				<view class="listA" :style="[gridGap]">
					<view class="item" v-for="(item, index) in tempArr" :key="index" @click="goDetail(item)">
						<view class="pictrue">
							<view v-show="item.stock===0" class="sellOut">已售罄</view>
							<easy-loadimage :image-src="item.image"
								:radius="dataConfig.contentStyle.val*2+'rpx'"></easy-loadimage>
							<view v-if="item.activityStyle" :style="{ backgroundImage: `url(${item.activityStyle})` }"
								class="border-picture"></view>
						</view>
						<view class="text-info text-add">
							<view class="title box-line2" :style="[titleColor]" :class="titleShow?'titHeight':''">
								<text v-if="item.productTags && item.productTags.locationLeftTitle.length"
									class="font-bg-red mr10 merType bg-color">{{item.productTags.locationLeftTitle[0].tagName}}</text>
								<text v-if="titleShow">{{ item.name }}</text>
							</view>
							<view class="flex flex-wrap pad2" v-if="item.productTags && item.productTags.locationUnderTitle.length">
								<text
									v-for="(items, idx) in item.productTags.locationUnderTitle.length>3?item.productTags.locationUnderTitle.slice(0,3):item.productTags.locationUnderTitle"
									:key="idx" class="mr10 tagSolid">{{items.tagName}}</text>
							</view>
							<view class="price acea-row row-middle" :style="[priceColor]">
								<view v-if="priceShow" class="mt-10">
									<svip-price :svipIconStyle="svipIconStyle" :productPrice="item"
										:svipPriceStyle="svipPriceStyle" :priceColor="priceColor"></svip-price>
								</view>
							</view>
							<view class="old-price line-heightOne mt10" v-if="soldShow" :style="[soldColor]">
								已售 {{ item.sales || 0 }} {{ item.unitName }}
							</view>
						</view>
					</view>
				</view>
			</block>
			<!-- 两列 -->
			<block v-if="itemStyle == 1">
				<view class="listC" :style="[gridGap]">
					<view class="item" :style="[contentStyle]" v-for="(item, index) in tempArr" :key="index"
						@click="goDetail(item)">
						<view class="pictrue">
							<view v-show="item.stock===0" class="sellOut">已售罄</view>
							<easy-loadimage :image-src="item.image"
								:radius="dataConfig.contentStyle.val*2+'rpx'"></easy-loadimage>
							<view v-if="item.activityStyle" :style="{ backgroundImage: `url(${item.activityStyle})` }"
								class="border-picture"></view>
						</view>
						<view class="text-info">
							<view class="title box-line2" :style="[titleColor]" :class="titleShow?'titHeight':''">
								<text v-if="item.productTags && item.productTags.locationLeftTitle.length"
									class="font-bg-red mr10 merType bg-color">{{item.productTags.locationLeftTitle[0].tagName}}</text>
								<text v-if="titleShow">{{ item.name }}</text>
							</view>
							<view class="flex flex-wrap pad2" v-if="item.productTags && item.productTags.locationUnderTitle.length">
								<text
									v-for="(items, idx) in item.productTags.locationUnderTitle.length>3?item.productTags.locationUnderTitle.slice(0,3):item.productTags.locationUnderTitle"
									:key="idx" class="mr10 tagSolid">{{items.tagName}}</text>
							</view>
							<view class="row-middle price" :style="[priceColor]">
								<view v-if="priceShow" class="mt-10">
									<svip-price :svipIconStyle="svipIconStyle" :priceColor="priceColor"
										:productPrice="item" :svipPriceStyle="svipPriceStyle"></svip-price>
								</view>
								<view class="old-price line-heightOne mt10" v-if="soldShow" :style="[soldColor]">
									已售 {{ item.sales || 0 }} {{ item.unitName }}
								</view>
							</view>
						</view>
					</view>
				</view>
			</block>
			<!-- 三列 -->
			<block v-if="itemStyle == 2">
				<view class="listB" :style="[gridGap]">
					<view class="item" v-for="(item, index) in tempArr" :key="index" @click="goDetail(item)">
						<view class="pictrue">
							<view v-show="item.stock===0" class="sellOut">已售罄</view>
							<easy-loadimage :image-src="item.image"
								:radius="dataConfig.contentStyle.val*2+'rpx'"></easy-loadimage>
							<view v-if="item.activityStyle" :style="{ backgroundImage: `url(${item.activityStyle})` }"
								class="border-picture"></view>
						</view>
						<view class="text-info">
							<view class="title box-line2" :style="[titleColor]" :class="titleShow?'titHeight':''">
								<text v-if="item.productTags && item.productTags.locationLeftTitle.length"
									class="font-bg-red mr10 merType bg-color">{{item.productTags.locationLeftTitle[0].tagName}}</text>
								<text v-if="titleShow">{{ item.name }}</text>
							</view>
							<view class="flex flex-wrap pad2" v-if="item.productTags && item.productTags.locationUnderTitle.length">
								<text
									v-for="(items, idx) in item.productTags.locationUnderTitle.length>3?item.productTags.locationUnderTitle.slice(0,3):item.productTags.locationUnderTitle"
									:key="idx" class="mr10 tagSolid">{{items.tagName}}</text>
							</view>
							<view class="price" :style="[priceColor]">
								<view v-if="priceShow" class="mt-10">
									<svip-price :svipIconStyle="svipIconStyle" :priceColor="priceColor"
										:productPrice="item" :svipPriceStyle="svipPriceStyle"></svip-price>
								</view>
							</view>
							<view class="old-price line-heightOne mt10" v-if="soldShow" :style="[soldColor]">
								已售 {{ item.sales || 0 }} {{ item.unitName }}
							</view>
						</view>
					</view>
				</view>
			</block>
			<!-- 大图 -->
			<block v-if="itemStyle == 3 && tempArr.length">
				<view class="listBig" :style="[gridGap]">
					<view class="itemBig" v-for="(item,index) in tempArr" :key="index" @click="goDetail(item)">
						<view class="img-box">
							<view v-show="item.stock===0" class="sellOut">已售罄</view>
							<easy-loadimage :image-src="item.image"
								:radius="dataConfig.contentStyle.val*2+'rpx'"></easy-loadimage>
							<view v-if="item.activityStyle" :style="{ backgroundImage: `url(${item.activityStyle})` }"
								class="border-picture"></view>
						</view>
						<view class="name box-line2" :style="[titleColor]">
							<text v-if="item.productTags && item.productTags.locationLeftTitle.length"
								class="font-bg-red merType mr10 bg-color">{{item.productTags.locationLeftTitle[0].tagName}}</text>
							<text v-if="titleShow">{{item.name}}</text>
						</view>
						<view class="flex flex-wrap pad2" v-if="item.productTags && item.productTags.locationUnderTitle.length">
							<text
								v-for="(items, idx) in item.productTags.locationUnderTitle.length>3?item.productTags.locationUnderTitle.slice(0,3):item.productTags.locationUnderTitle"
								:key="idx" class="mr10 tagSolid">{{items.tagName}}</text>
						</view>
						<slot name="center"></slot>
						<view class="row-middle price" :style="[priceColor]">
							<view v-if="priceShow" class="mt-10">
								<svip-price :svipIconStyle="svipIconStyle" :priceColor="priceColor" :productPrice="item"
									:svipPriceStyle="svipPriceStyle"></svip-price>
							</view>
							<view class="old-price line-heightOne mt10" v-if="soldShow" :style="[soldColor]">
								已售 {{ item.sales || 0 }} {{ item.unitName }}
							</view>
						</view>
					</view>
				</view>
			</block>
      <view v-if="tempArr.length==0 && loading" class="empty-box">
        <emptyPage title="暂无商品，去看看别的吧～" mTop="0" :imgSrc="urlDomain+'crmebimage/presets/noShopper.png'">
        </emptyPage>
      </view>
		</view>
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
		productList,
		productByidsApi
	} from '@/api/product.js';
	let app = getApp();
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	import svipPrice from '@/components/svipPrice.vue';
  import emptyPage from '@/components/emptyPage.vue';
	import {
		getMerProListApi
	} from "@/api/merchant";
  import {indexProductApi} from "@/api/product";
	export default {
		name: 'homeTab',
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			},
			merId: { // 商户id
				type: Number,
				default: 0
			},
      // 圈层ID集合，逗号分隔
      regionIds: {
        type: String,
        default: ''
      },
		},
		data() {
			return {
        urlDomain: this.$Cache.get("imgHost"),
				//普通价格
				svipPriceStyle: {
					svipBox: {
						height: '26rpx',
						borderRadius: '60rpx 56rpx 56rpx 20rpx',
						marginTop: '8rpx'
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
						marginTop: '8rpx'
					},
					price: {
						fontSize: '38rpx'
					},
					svipPrice: {
						fontSize: '18rpx'
					}
				},
				tempArr: [],
				iSshowH: false,
				ProductNavindex: 0,
				itemStyle: 0, //样式类型
				titleConfig: 1, //标题位置
				infoColor: '#999',
				goodType: 3,
				loadend: false,
				loading: false,
				page: 1,
				goodsSort: 0, //排序，0综合，1销量，2价格
				isWidth: 0, //每个导航栏占位
				tabLeft: 0,
				limit: 0, //分页条数
				themeColor: this.$options.filters.filterTheme(app.globalData.theme),
        activeProTabIndex: 0 // 商品列表类型
			};
		},
		components: {
			easyLoadimage,
			svipPrice,
      emptyPage
		},
		created() {
			let that = this
			uni.getSystemInfo({
				success(e) {
					that.isWidth = (e.windowWidth) / 5;
				}
			})
		},
		computed: {
			//标签文字颜色
			fontColor() {
				return this.dataConfig.fontColor.color[0].item
			},
			//选中颜色
			checkColor() {
				return this.dataConfig.checkThemeStyleConfig.tabVal ? this.dataConfig.checkColor.color[0].item : this
					.themeColor
			},
			//选项卡背景颜色
			tabBgColor() {
				return {
					background: `linear-gradient(${this.dataConfig.tabBgColor.color[0].item}, ${this.dataConfig.tabBgColor.color[1].item})`,
				};
			},
			//页面间距
			mbConfig() {
				return {
					marginTop: this.dataConfig.mbConfig.val ? this.dataConfig.mbConfig.val + 'px' : 0
				}
			},
			//分类列表
			navList() {
				return this.dataConfig.tabItemConfig.list;
			},
			//最外层盒子的样式
			boxStyle() {
				return {
					borderRadius: this.dataConfig.bgStyle.val * 2 + 'rpx',
					background: `linear-gradient(${this.dataConfig.bgColor.color[0].item}, ${this.dataConfig.bgColor.color[1].item})`,
					margin: this.dataConfig.topConfig.val * 2 + 'rpx' + ' ' + this.dataConfig.lrConfig.val * 2 + 'rpx' +
						' ' + 0,
					padding: this.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + 0 + ' ' + this.dataConfig.downConfig.val *
						2 + 'rpx'
				}
			},
			//商品间距
			gridGap() {
				return {
					'grid-gap': this.dataConfig.contentConfig.val * 2 + 'rpx'
				}
			},
			//图片的圆角和高度
			imgStyle() {
				return {
					'border-radius': this.dataConfig.contentStyle.val * 2 + 'rpx',
				}
			},
			//价格颜色
			priceColor() {
				return {
					'color': this.dataConfig.priceThemeStyleConfig.tabVal ? this.dataConfig.priceColor.color[0].item : this
						.themeColor,
				}
			},
			//商品名称颜色
			titleColor() {
				return {
					'color': this.dataConfig.titleColor.color[0].item,
				}
			},
			//已售数量
			soldColor() {
				return {
					'color': this.dataConfig.soldColor.color[0].item,
				}
			},
			//商品名称
			titleShow() {
				if (this.dataConfig.tabItemConfig.list[this.ProductNavindex] && this.dataConfig.tabItemConfig.list[this
						.ProductNavindex].activeList.showContent.includes(0)) {
					return true;
				} else {
					return false;
				}
			},
			//价格
			priceShow() {
				if (this.dataConfig.tabItemConfig.list[this.ProductNavindex] && this.dataConfig.tabItemConfig.list[this
						.ProductNavindex].activeList.showContent.includes(1)) {
					return true;
				} else {
					return false;
				}
			},
			//销量
			soldShow() {
				if (this.dataConfig.tabItemConfig.list[this.ProductNavindex] && this.dataConfig.tabItemConfig.list[this
						.ProductNavindex].activeList.showContent.includes(2)) {
					return true;
				} else {
					return false;
				}
			},
			//内容圆角
			contentStyle() {
				return {
					'border-radius': this.dataConfig.contentStyle.val ? this.dataConfig.contentStyle.val + 'px' : '0'
				};
			},
      // 是否是指定商品
      specifiedGoods(){
        if(this.merId){
          return this.activeProTabIndex === 1
        }else{
          return this.activeProTabIndex === 3
        }
      }
		},
		mounted() {
			//默认加载第一项的商品数据
			if (this.navList && this.navList.length) {
				this.itemStyle = this.navList[0].activeList ? this.navList[0].activeList.styleType : 0;
        this.limit = this.navList[0].activeList ? this.navList[0].activeList.num : 3;
        if (this.navList[0].activeList) {
          if(this.navList[0].activeList.activeProTabIndex == 0) this.activeProTabIndex = 0
          this.getGoodList(this.navList[0].activeList);
        }
			}
		},
		//uniapp小程序用deep重写组件样式不生效 
		options: {
			styleIsolation: 'shared'
		},
		methods: {
			// 选项卡切换点击事件；商品类型选择除第一个指定商品，加载商品从平台端获取数据，其余选项均请求接口加载
			changeTab(item, index) {
				this.tempArr = [];
        this.page = 1;
        this.loadend = false;
        this.loading = false;
        this.getGoodList(item.activeList);
			},
			// 商品列表
			async getGoodList(item) {
				let val = item.activeValue.length ? item.activeValue : ''; //分类id，品牌id，商户id
				let goodsSort = item.goodsSort // 商品排序，0综合，1按销量，2按价格
				this.activeProTabIndex = item.activeProTabIndex //1分类，2品牌，3商户
				let sortCollation = item.sortCollation // 排序规则 正序、降序
				let priceOrder = '';
				let salesOrder = '';
				if (this.loadend) return false;
				if (this.loading) return false;
				if (goodsSort === 0) {
					priceOrder = '';
					salesOrder = '';
				} else if (goodsSort === 1) {
					if (sortCollation === 0) {
						priceOrder = '';
						salesOrder = 'asc';
					} else {
						priceOrder = '';
						salesOrder = 'desc';
					}
				} else {
					if (sortCollation === 0) {
						priceOrder = 'asc';
						salesOrder = '';
					} else {
						priceOrder = 'desc';
						salesOrder = '';
					}
				}
        let ids = item.goods.map((item) => item.id).join(',');
				let params = {
					page: this.page,
          num: this.specifiedGoods ? '' : this.limit, // 商品列表数量
          limit: this.limit,  // 商户商品列表数量
          brandIds: this.merId ? '' : this.activeProTabIndex == 1 ? val : '',
          merIds: this.merId ? this.merId : this.activeProTabIndex == 2 ? val : '', //首页商户列表
					priceOrder: priceOrder,
					salesOrder: salesOrder,
          cids: this.activeProTabIndex == 0 ? val : '',
          proIds: this.specifiedGoods ? ids : '',
          circleIds: this.regionIds,
          merId: this.merId
				}
				if (this.merId) {
					delete params.brandIds;
          delete params.num;
          delete params.merIds;
				}else{
          delete params.merId;
        }
				const {
					code,
					data
				} = this.merId ? await getMerProListApi(params) : await indexProductApi(params)
				if (code == 200) {
          if(this.merId){
            let list =  data.list;
            this.tempArr = this.$util.SplitArray(list, this.tempArr);
            this.loadend = list.length < this.limit;
            this.page = this.page + 1;
          }else{
            this.tempArr = data || [];
          }
				}
				this.loading = true;
			},
			// 选项卡切换
			ProductNavTab(item, index) {
				this.ProductNavindex = index;
				this.itemStyle = this.navList[index].activeList.styleType;
				this.goodsSort = item.activeList.goodsSort;
				this.$nextTick(() => {
					let id = 'id' + index;
					this.tabLeft = (index - 2) * this.isWidth //设置下划线位置
				})
				this.limit = item.activeList.num;
				this.changeTab(item, index);
			},
			goDetail(item) {
				this.$emit('detail', item);
			}
		}
	}
</script>

<style lang="scss" scoped>
	/deep/.x-money {
		color: inherit !important;
	}

	.longTab {
		.longItem {
			height: 70rpx;
			display: inline-block;
			line-height: 70rpx;
			text-align: center;
			font-size: 28rpx;
			color: #333333;
			white-space: nowrap;
			margin-right: 46rpx;

			&.click {
				font-weight: bold;
				font-size: 30rpx;
				position: relative;

				&::after {
					content: '';
					width: 40rpx;
					height: 4rpx;
					background: var(--color);
					position: absolute;
					bottom: 0;
					left: 50%;
					transform: translateX(-50%);
				}
			}
		}
	}

	.index-product-wrapper {

		&.on {
			min-height: 1500rpx;
		}

		.nav-bd {
			height: 70rpx;
			line-height: 70rpx;
			padding-left: 20rpx;
			background: #fff;

			.item {
				display: inline-block;
				font-size: 28rpx;
				color: #333;
				font-weight: 400;
				padding-right: 48rpx;

				&.on {
					border-radius: 0;
				}
			}
		}
	}

	.text-add {
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.listBig {
		display: grid;
		grid-template-rows: auto;
		grid-template-columns: repeat(1, 1fr);
		padding: 0 20rpx;

		.itemBig {
			width: 100%;

			.img-box {
				width: 100%;
				height: 710rpx;
				position: relative;
			}

			.name {
				font-size: 28rpx;
				font-weight: bold;
				margin-top: 16rpx;
			}

			.price {
				font-size: 12px;
				margin-top: 10rpx;

				.num {
					font-size: 32rpx;
					margin-right: 10rpx;
				}

				.old-price {
					color: #aaa;
					font-weight: normal;
				}
			}
		}
	}

	.listA {
		display: grid;
		grid-template-columns: repeat(1, 1fr);
		grid-template-rows: auto;
		width: 100%;
		padding: 0 20rpx;

		.item {
			display: flex;
			width: 100%;

			.pictrue {
				width: 220rpx;
				height: 220rpx;
				position: relative;

				image {
					width: 100%;
					height: 100%;
				}
			}

			.text-info {
				margin-left: 14rpx;
				flex: 1
			}
		}
	}

	.listB {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		grid-template-rows: auto;
		width: 100%;
		padding: 0 20rpx;

		.item {
			.pictrue {
				width: 100%;
				height: 220rpx;
				position: relative;

				image {
					width: 100%;
					height: 100%;
				}
			}

			.text-info {
				padding: 16rpx 0;
			}
		}
	}

	.listC {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		grid-template-rows: auto;
		width: 100%;
		padding: 0 20rpx;

		/deep/.origin-img,
		/deep/.easy-loadimage {
			border-bottom-left-radius: 0 !important;
			border-bottom-right-radius: 0 !important;
		}

		.item {
			background-color: #fff;
			overflow: hidden;

			.pictrue {
				width: 100%;
				height: 345rpx;
				overflow: hidden;
				position: relative;

				image {
					width: 100%;
					height: 100%;
				}
			}

			.text-info {
				padding: 16rpx 0;

				.title {
					width: 300rpx;
				}
			}
		}
	}

	.titHeight {
		height: 80rpx;
		line-height: 40rpx;
	}

	.text-info {
		.title {
			width: 100%;
			color: #333;
		}

		.old-price {
			font-weight: normal;
			font-size: 24rpx;
			color: #999;
		}

		.price {
			font-size: 36rpx;
			font-weight: 550;

			text {
				padding-bottom: 4rpx;
				font-size: 26rpx;
				font-weight: normal;
			}
		}
	}

	.mer_badge {
		padding: 0 4rpx;
		background-color: theme;
		color: #fff;
		font-size: 20rpx;
		display: inline-block;
		border-radius: 4rpx;
		line-height: 28rpx;
		height: 28rpx;
	}

	.mt18 {
		margin-top: 16rpx;
	}

	.box-line2 {
		overflow: hidden;
	}
</style>