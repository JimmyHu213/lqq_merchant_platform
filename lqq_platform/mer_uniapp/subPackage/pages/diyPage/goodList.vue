<template>
	<!-- 商品列表 -->
	<view>
		<view v-if="tempArr.length" :style="[boxStyle]">
			<!-- 单列 -->
			<block v-if="itemStyle == 0">
				<view class="listA" :style="[gridGap]">
					<view class="item" v-for="(item, index) in tempArr" :key="index" @click="goDetail(item)">
						<view class="pictrue">
							<view v-show="item.stock===0" class="sellOut">已售罄</view>
							<easy-loadimage :image-src="item.image" :radius="dataConfig.contentStyle.val*2+'rpx'">
							</easy-loadimage>
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
									<svip-price :svipIconStyle="svipIconStyle" :priceColor="priceColor"
										:productPrice="item" :svipPriceStyle="svipPriceStyle"></svip-price>
								</view>
							</view>
							<view class="old-price line-heightOne mt10" :style="[soldColor]" v-if="soldShow">已售
								{{ item.sales || 0 }} {{item.unitName}}
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
							<easy-loadimage :image-src="item.image" :radius="dataConfig.contentStyle.val*2+'rpx'">
							</easy-loadimage>
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
							<view class="acea-row row-middle price" :style="[priceColor]">
								<view v-if="priceShow" class="mt-10">
									<svip-price :svipIconStyle="svipIconStyle" :priceColor="priceColor"
										:productPrice="item" :svipPriceStyle="svipPriceStyle"></svip-price>
								</view>
							</view>
							<view class="old-price line-heightOne mt10" :style="[soldColor]" v-if="soldShow">已售
								{{ item.sales || 0 }} {{item.unitName}}
							</view>
						</view>
					</view>
				</view>
			</block>
			<!-- 三列 -->
			<block v-if="itemStyle == 2">
				<view class="listB" :style="[gridGap]">
					<view class="item" v-for="(item, index) in tempArr" :key="index" @click="goDetail(item)">
						<view class="pictrue" :style="[contentStyle]">
							<view v-show="item.stock===0" class="sellOut">已售罄</view>
							<easy-loadimage :image-src="item.image" :radius="dataConfig.contentStyle.val*2+'rpx'">
							</easy-loadimage>
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
							<easy-loadimage :image-src="item.image" :radius="dataConfig.contentStyle.val*2+'rpx'">
							</easy-loadimage>
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
						<view class="acea-row row-middle price" :style="[priceColor]">
							<view v-if="priceShow" class="mt-10">
								<svip-price :svipIconStyle="svipIconStyle" :priceColor="priceColor" :productPrice="item"
									:svipPriceStyle="svipPriceStyle"></svip-price>
							</view>
						</view>
						<view class="old-price line-heightOne mt10" :style="[soldColor]" v-if="soldShow">已售
							{{ item.sales || 0 }} {{item.unitName}}
						</view>
					</view>
				</view>
			</block>
			<view class='loadingicon acea-row row-center-wrapper' :hidden='loading==false'>
				<text class='loading iconfont icon-jiazai'></text>
			</view>
			<!-- <view class="mores-txt" v-if="goodScroll">
				<text>我是有底线的</text>
			</view> -->
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
    indexProductApi,
	} from '@/api/product.js';
	import {
		getMerProListApi
	} from '@/api/merchant.js';
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	import svipPrice from '@/components/svipPrice.vue';
	let app = getApp();
	export default {
		name: 'goodList',
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
      }
		},
		components: {
			easyLoadimage,
			svipPrice
		},
		data() {
			return {
				//普通价格
				svipPriceStyle: {
					svipBox: {
						height: '26rpx',
						borderRadius: '60rpx 56rpx 56rpx 20rpx',
						marginTop: '8rpx',
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
				numConfig: this.dataConfig.numConfig.val, //展示多少条
				itemStyle: this.dataConfig.itemStyle.tabVal, //商品列表展示方式  单列 两列 三列
				changeType: this.dataConfig.tabConfig.tabVal || 0, //商品类型 0指定分类，1指定品牌，2指定商户 3指定商品，
				classifyId: this.dataConfig.selectConfig ? this.dataConfig.selectConfig.activeValue : [], //分类
        brandIds: this.dataConfig.activeValueBrand ? this.dataConfig.activeValueBrand.activeValue : [], //品牌
				productIds: this.dataConfig.goodsList.ids || [],
				homeMerId: this.dataConfig.activeValueMer ? this.dataConfig.activeValueMer.activeValue : [], //商户
				params: { //精品推荐分页
					page: 1,
          num: 10, // 商城首页字段
          limit: 10, //商户字段
					cids: '',
          brandIds: '',
          merIds: '',
					priceOrder: '',
					salesOrder: '',
          proIds: '',
          circleIds: ''
				},
				goodScroll: false,
				loading: false,
				themeColor: this.$options.filters.filterTheme(app.globalData.theme),
        num: 50
			};
		},
		computed: {
			//商品名称颜色
			titleColor() {
				return {
					'color': this.dataConfig.titleColor.color[0].item,
				}
			},
			//最外层盒子的样式
			boxStyle() {
				return {
					borderRadius: this.dataConfig.bgStyle.val * 2 + 'rpx',
					background: `linear-gradient(${this.dataConfig.bgColor.color[0].item}, ${this.dataConfig.bgColor.color[1].item})`,
					margin: this.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + this.dataConfig.lrConfig.val * 2 + 'rpx' +
						' ' + 0,
					padding: this.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + '16rpx' + ' ' + this.dataConfig.downConfig
						.val *
						2 + 'rpx'
				}
			},
			//图片展示样式
			gridGap() {
				return {
					'grid-gap': this.dataConfig.contentConfig.val * 2 + 'rpx'
				}
			},
			//文章图片的圆角和高度
			imgStyle() {
				return {
					'border-radius': this.dataConfig.contentStyle.val * 2 + 'rpx',
				}
			},
			//价格颜色
			priceColor() {
				return {
					'color': this.dataConfig.themeStyleConfig.tabVal ? this.dataConfig.priceColor.color[0].item : this
						.themeColor,
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
				if (this.dataConfig.typeConfig.activeValue.includes(0)) {
					return true;
				} else {
					return false;
				}
			},
			//价格
			priceShow() {
				if (this.dataConfig.typeConfig.activeValue.includes(1)) {
					return true;
				} else {
					return false;
				}
			},
			//销量
			soldShow() {
				if (this.dataConfig.typeConfig.activeValue.includes(2)) {
					return true;
				} else {
					return false;
				}
			},
			//排序，0综合，1销量，2价格
			goodsSort() {
				return this.dataConfig.goodsSort.tabVal
			},
			//排序规则
			sortCollation() {
				return this.dataConfig.sortCollation.tabVal
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
          return this.changeType === 1
        }else{
          return this.changeType === 3
        }
      }
		},
		mounted() {
			this.params.page = 1;
			this.goodScroll = false;
			this.tempArr = [];
			
			// 监听位置变化事件
			uni.$on('locationChanged', this.handleLocationChanged);

      this.productsList();
		},
		//uniapp小程序用deep重写组件样式不生效
		options: {
			styleIsolation: 'shared'
		},
		methods: {
			// 处理位置变化事件
			handleLocationChanged() {
				// 重新加载商品数据
				this.params.page = 1;
				this.goodScroll = false;
				this.tempArr = [];

        // 根据类型重新加载商品列表
        this.productsList();
			},
      // 商品选择方式
			getProList() {
        // 类型为0时，直接加载选中的商品，不为0时根据条件加载商品列表 proIds
        if (this.specifiedGoods){
          let data = this.dataConfig.goodsList.list
          if (!data.length) return;
          let ids = data.map((item) => item.id).join(',');
          this.params.proIds = ids
          delete this.params.limit;
          delete this.params.num;
        }else{
          this.params.proIds = ''
          this.params.num = this.numConfig;
          this.params.limit = this.numConfig;
        }
        this.params.circleIds = this.regionIds
			},
			productsList() {
				if (this.goodScroll) return;
				this.loading = true
        this.getProList()
				//如果有商户id，调用商户商品列表
				if (this.merId) {
					this.params.merId = this.merId;
					delete this.params.brandIds;
          delete this.params.num;
          delete this.params.merIds
					this.params.cids = this.classifyId.join(',');
				} else {
          delete this.params.limit;
					switch (this.changeType) {
						case 0:
							this.params.merIds = '';
							this.params.brandIds = '';
							this.params.cids = this.classifyId.join(',');
							break;
						case 1:
							this.params.cids = '';
							this.params.merIds = '';
							this.params.brandIds = this.brandIds.join(',');
							break;
						case 2:
							this.params.cids = '';
							this.params.brandIds = '';
							this.params.merIds = this.homeMerId.join(',');
							break;
					}
				}
				if (this.goodsSort === 0) {
					this.params.priceOrder = '';
					this.params.salesOrder = '';
				} else if (this.goodsSort === 1) {
					if (this.sortCollation === 0) {
						this.params.priceOrder = '';
						this.params.salesOrder = 'asc';
					} else {
						this.params.priceOrder = '';
						this.params.salesOrder = 'desc';
					}
				} else {
					if (this.sortCollation === 0) {
						this.params.priceOrder = 'asc';
						this.params.salesOrder = '';
					} else {
						this.params.priceOrder = 'desc';
						this.params.salesOrder = '';
					}
				}
        this.getRequestList();
			},
      getRequestList(){
        // 原有逻辑
        !this.merId ? indexProductApi(this.params).then(res => {
          this.$set(this.params, 'page', this.params.page + 1);
          this.goodScroll = this.params.page > res.data.totalPage;
          this.tempArr = res.data || [];
          this.loading = false
        }) : getMerProListApi(this.params).then(res => {
          this.$set(this.params, 'page', this.params.page + 1);
          this.goodScroll = this.params.page > res.data.totalPage;
          this.tempArr = this.tempArr.concat(res.data.list || []);
          this.loading = false
        });
      },
			goDetail(item) {
				this.$emit('detail', item);
			}
		},
		beforeDestroy() {
			// 清理事件监听器
			uni.$off('locationChanged', this.handleLocationChanged);
		}
	};
</script>

<style lang="scss" scoped>
	.mores-txt {
		text-align: center;
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
				font-weight: bold;
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
				margin-left: 20rpx;
				flex: 1
			}
		}
	}

	.listB {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		grid-template-rows: auto;
		width: 100%;

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
				padding-top: 14rpx;
			}
		}
	}

	.listC {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		grid-template-rows: auto;
		width: 100%;

		/deep/.origin-img,
		/deep/.easy-loadimage {
			border-bottom-left-radius: 0 !important;
			border-bottom-right-radius: 0 !important;
		}

		.item {
			background-color: #fff;

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
				padding: 14rpx 0;

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

	.box-line2 {
		overflow: hidden;
	}
</style>