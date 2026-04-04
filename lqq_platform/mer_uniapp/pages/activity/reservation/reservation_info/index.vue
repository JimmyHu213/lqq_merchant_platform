<template>
	<view class="reservation" :style="viewColor">
		<!-- 自定义顶部背景颜色 -->
		<view class="top">
			<!-- #ifdef MP || APP-PLUS -->
			<view class="sys-head fixed">
				<view class="sys-bar" :style="{height:sysHeight}"></view>
				<!-- #ifdef MP -->
				<view class="sys-title">
					<view class='iconfont icon-ic_leftarrow' :style="{lineHeight:sysHeight}" @tap='goBack'></view>
					预约信息
				</view>
				<!-- #endif -->
				<view class="bg"></view>
			</view>
			<!-- #endif -->
		</view>
	  <view class="header" :style="{marginTop:marTop}"></view>
	  <view class="pad20 reservation-main">
			<view>
				<view class="store-info bg-f boder-24" v-for="(item,index) in cartInfo" :key="index">
					<view class="store-title acea-row row-middle"@click="goStore(item.mer_id)">
						<text class="iconfont icon-ic_mall"></text>
						<view class="txt font-500">{{item.mer_name}}</view>
						<text class="iconfont icon-ic_rightarrow"></text>
					</view>
					<view class="product-item" v-for="(itm,idx) in item.list" :key="idx">
						<view class="img-box">
							<image :src="(itm.productAttr && itm.productAttr.image) || itm.product.image"></image>
						</view>
						<view class="content">
							<view class="name line1">{{itm.product.store_name}}</view>
							<view v-if="itm.productAttr.sku" class="label" style="width: 70%;">{{itm.productAttr.sku}}</view>
							<view class="acea-row row-middle">
								<view class="price acea-row row-bottom">
									<priceFormat :price="itm.productAttr.price" weight intSize="28" floatSize="20" labelSize="20"></priceFormat>
									<view v-if="itm.productAttr.show_svip_price" class="acea-row row-middle vip-price">
										<text class="vip-money regular">¥{{itm.productAttr.product.svip_price}}</text>
										<image class="vip-image" :src="`${domain}/static/images/svip.png`"></image>
									</view>
								</view>
							</view>
						</view>
						<view class="item-count">x{{itm.cart_num}}</view>
					</view>
				</view>
        <systemFrom v-model="orderInfoVo.systemFormValue"></systemFrom>
<!--				<associationForm ref="associationForm" :formsValue="formList" @getDatas="getDatas"></associationForm>-->
			 </view>
	  </view>
	  <view class="heights"></view>
		<view class="reservation-bottom">
			<view class='footer acea-row row-between-wrapper'>
			 	<view class="footer_count">
			 		<view class="acea-row">
			 			<view>合计:</view>
			 			<view class='pColor'>
			 				<priceFormat :price="totalPrice" weight intSize="48" floatSize="32" labelSize="32"></priceFormat>
			 			</view>
			 		</view>
			 	</view>
			 	<view class='settlement' style='z-index:100' @click="SubOrder">确认下单</view>
			 </view>
		 </view>	
	</view>
</template>

<script>
let sysHeight = uni.getSystemInfoSync().statusBarHeight + 'px';
const CACHE_CITY = {};
import { mapGetters } from "vuex";
import { HTTP_REQUEST_URL } from '@/config/app';
import { getOrderConfirm } from '@/api/order.js';
//import associationForm from '../components/associationForm';

export default {
	name: 'reservationInfo',
	components: {
		//associationForm
	},
	data() {
		return {
			//#ifdef H5
			marTop: 0,
			//#endif
			//#ifdef MP
			marTop: uni.getSystemInfoSync().statusBarHeight+43+'px',
			//#endif
			//#ifdef APP-PLUS
			marTop: uni.getSystemInfoSync().statusBarHeight+'px',
			//#endif
			domain: HTTP_REQUEST_URL,
			sysHeight: sysHeight,
			formList: {},
			formValue: [],
			storeInfo: {},
			totalPrice: "",
			address_id: 0,
			cart_id: "",
			service_type: "",
			cartInfo: {},
			extend: {},
			post: {},
			payType: 'weixin', //支付方式
			order_form: [],
			mer_id: "",
			formType: 2
		}
	},
	computed: {
		...mapGetters(['viewColor','isLogin','hide_mer_status']),
	},
	onLoad(options){
		this.address_id = options.address_id
		this.cart_id = options.cart_id
		this.service_type = options.service_type
		this.mer_id = options.mer_id
		this.formType = options.formType
	},
	mounted(){
		uni.setStorageSync('extendInfo',[])
		this.getConfirm();
	},
	methods: {
		goBack: function() {
			uni.navigateBack();
		},
		// 进店
		goStore: function(id) {
			if (this.hide_mer_status != 1) {
				uni.navigateTo({
					url: `/pages/store/home/index?id=${id}`
				})
			}
		},	
		/**
		 * 获取当前订单详细信息
		 *
		 */
		getConfirm: function() {
			let that = this;
			uni.showLoading({
				title: '',
				mask: true
			});
			let data = {
				cart_id: that.cart_id.split(",")
			}
			if(that.service_type == 1){
				data.takes = [that.mer_id]
			}else {
				data.address_id = that.address_id
			}
			getOrderConfirm(data).then(res => {
				that.$set(that, 'cartInfo', res.data.order);		
				let count = that.formType == 1 ? res.data.order[0]['list'][0]['cart_num'] : 1;
				that.$set(that, 'totalPrice', res.data.order_price);
				that.$set(that, 'formList', res.data.mer_form_info);
				if(res.data.mer_form_info&&res.data.mer_form_info.name)that.$refs.associationForm.getFormList(res.data.mer_form_info,count);
				uni.hideLoading();
			}).catch(err => {
				return this.$util.Tips({
					title: err
				}, {
					tab: 3,
					url: 1
				});
			});
		},
		getDatas() {
			let data = this.$refs.associationForm.getFormValue();
			return data;
		},
		SubOrder: function(e) {
			let that = this,data = {};	
			if(that.formList && that.formList.name)that.order_form = that.getDatas();
			let extendArr = [];
			for(var i=0; i<that.order_form.length; i++) {
				let extendData = {}
				for (var j = 0; j < that.order_form[i].length; j++) {
				  let curdata = that.order_form[i][j]
				  if (['radios'].indexOf(curdata.name) == -1 && (curdata.titleShow.val || (['uploadPicture','dateranges'].indexOf(curdata.name) == -1 && curdata.value && curdata.value.trim()))) {
				    if ((curdata.name === 'texts' && curdata.valConfig.tabVal == 0) || ['dates','times','selects','citys','checkboxs'].indexOf(curdata.name) != -1) {
							if (!curdata.value || (curdata.value && !curdata.value.trim())) {
								return that.$util.Tips({
									title: `请填写第${i+1}个表单的${curdata.titleConfig.value}`
								});
							}
						}
						if(curdata.name === 'timeranges'){
							if(!curdata.value){
								return that.$util.Tips({
									title: `请选择第${i+1}个表单的${curdata.titleConfig.value}`
								});
							}
						}
						if (curdata.name === 'dateranges') {
							if (!curdata.value.length) {
								return that.$util.Tips({
									title: `请选择第${i+1}个表单的${curdata.titleConfig.value}`
								});
							}
						}
				    if (curdata.name === 'texts' && curdata.valConfig.tabVal == 4) {
				      if (curdata.value <= 0) {
				        return that.$util.Tips({
				          title: `第${i+1}个表单请填写大于0的${curdata.titleConfig.value}`
				        });
				      }
				    }
				    if (curdata.name === 'texts' && curdata.valConfig.tabVal == 3) {
				      if (!/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(curdata.value)) {
				        return that.$util.Tips({
				          title: `第${i+1}个表单请填写正确的${curdata.titleConfig.value}`
				        });
				      }
				    }
				    if (curdata.name === 'texts' && curdata.valConfig.tabVal == 1) {
				      if (!/^1(3|4|5|7|8|9|6)\d{9}$/i.test(curdata.value)) {
				        return that.$util.Tips({
				          title: `第${i+1}个表单请填写正确的${curdata.titleConfig.value}`
				        });
				      }
				    }
				    if (curdata.name === 'texts' && curdata.valConfig.tabVal == 2) {
				      if (!
				        /^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/i
				        .test(curdata.value)) {
				        return that.$util.Tips({
				          title: `第${i+1}个表单请填写正确的${curdata.titleConfig.value}`
				        });
				      }
				    }
				    if (curdata.name === 'uploadPicture') {
				      if (!curdata.value.length) {
				        return that.$util.Tips({
				          title: `请上传第${i+1}个表单的${curdata.titleConfig.value}`
				        });
				      }
				    }
				  }
					extendData[curdata.key] = curdata.value
				}	
				extendArr.push(extendData)
			}
			let formId = extendArr.length > 0 ? 1 : 0
			uni.setStorageSync('extendInfo',extendArr)
			uni.navigateTo({
				url: `/pages/users/order_confirm/index?cartId=${this.cart_id}&serviceType=${this.service_type}&addressId=${this.address_id}&formId=${formId}&merId=${that.mer_id}`
			})
			return
		},
	}
};
</script>

<style lang="scss" scoped>
	.sys-head .bg{
		background-image: linear-gradient( 90deg, var(--view-bntColor22) 0%, var(--view-bntColor21) 100%);
	}
	.reservation-main {
		position: relative;
		margin-top: -100rpx;
	}
	.reservation-header {
		background: rgba(255,255,255,.9);
	}
	.store-info {
		.store-title {
			display: flex;
			align-items: center;
			padding: 28rpx 30rpx;
			.icon-ic_mall {
				font-size: 32rpx;
			}
			.icon-ic_rightarrow {
				font-size: 26rpx;
			}
			.txt {
				margin: 0 8rpx;
			}
		}
		.product-item {
			display: flex;
			padding: 0 30rpx 26rpx;
			.img-box {
				width: 130rpx;
				height: 130rpx;
				image {
					width: 130rpx;
					height: 130rpx;
					border-radius: 16rpx;
				}
			}
			.content {
				position: relative;
				width: 550rpx;
				margin-left: 20rpx;
				font-size: 28rpx;
				color: #282828;
				overflow: hidden;
				.line1 {
					width: 480rpx;
				}
				.label {
					margin-top: 8rpx;
					color: #999999;
					font-size: 20rpx;
				}
				.price {
					margin-top: 10rpx;
					position: relative;
					color: var(--view-theme);
					.vip-price {
						position: relative;
						top: -2rpx;
					}
				}
				.vip-money{
					margin-left: 13rpx;
					color: #282828;
					font-size: 22rpx;
					font-weight: 700;
					margin-left: 12rpx;
				}
				.vip-image{
					width: 60rpx;
					height: 25rpx;
					margin-left: 6rpx;
				}
			}
			.item-count {
				color: #999999;
				font-size: 26rpx;
			}
		}
	}
	
	.reservation-bottom {
		width: 100%;
		position: fixed;
		bottom: 0;
		left: 0;
		background-color: #fff;
		.area-tip {
			padding: 0 32rpx;
			height: 80rpx;
			background: var(--view-bgColor);
			color: var(--view-theme);
			.iconfont {
				margin-right: 10rpx;
			}
		}
	}
	.reservation .footer {
		width: 100%;
		height: 100rpx;
		height: calc(120rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		height: calc(120rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
		padding: 0 30rpx;
		font-size: 28rpx;
		color: #333;
		box-sizing: border-box;
		z-index: 30;
		.footer_count{
			font-size: 28rpx;
			>view {
				align-items: baseline;
			}
			.pColor {
				margin-left: 12rpx;
			}
		}
		.coupon_price{
			color: #999999;
			font-size: 22rpx;
			margin-top: 10rpx;
			.detail{
				color: #666666;
				background: #F5F5F5;
				border-radius: 26rpx;
				margin-left: 20rpx;
				height: 34rpx;
				line-height: 34rpx;
				display: inline-block;
				width: 104rpx;
				text-align: center;
				font-size: 18rpx;
			}
		}
	}
	.pColor{
		color: var(--view-priceColor);
	}
	.reservation .footer .settlement {
		font-size: 30rpx;
		color: #fff;
		width: 200rpx;
		height: 72rpx;
		background-color: var(--view-theme);
		border-radius: 60rpx;
		text-align: center;
		line-height: 72rpx;
		font-size: 26rpx;
		&.disabled {
			background-color: #cccccc;
		}
	}
	.heights{
		height: calc(140rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		height: calc(140rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
	}
	.header{
		height: 160rpx;
		background: linear-gradient( 360deg, #F5F5F5 0%, rgba(245,245,245,0) 100%);
		background-image: linear-gradient( 90deg, var(--view-bntColor22) 0%, var(--view-bntColor21) 100%);
		position: relative;
		&::after {
			content: "";
			position: absolute;
			left: 0;
			bottom: 0;
			width: 100%;
			height: 86rpx;        
			background: linear-gradient(0deg, #F5F5F5 0%, rgba(245, 245, 245, 0) 100%);	
		}
	}
</style>