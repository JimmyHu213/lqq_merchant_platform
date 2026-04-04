<template>
	<view :data-theme="theme">
		<!-- #ifdef MP || APP -->
		<view class='cart_nav'>
			<nav-bar :navTitle='navTitle' iconColor='#fff' :isBackgroundColor="true" ref="navBarRef"></nav-bar>
		</view>
		<!-- #endif -->
		<view class="borderPad pt20 pick-gradient-bg" v-if="secondType == ProductTypeEnum.PunchCard">
			<view class="pick-up-info-top-box">
				<pick-up-info v-if="merchantOrderVoList[0] && Object.keys(merchantOrderVoList[0]).length > 0 && 
					merchantOrderVoList[0].latitude && merchantOrderVoList[0].longitude" :isPunchCard="true"
					:merAddressInfo="merchantOrderVoList[0]" :secondType="secondType">
				</pick-up-info>
			</view>
		</view>
		<view class='order-submission' @touchstart="touchStart">
			<!-- 拼团切换样式 -->
			<view class="allAddress"
				v-if="Number(orderInfoVo.secondType)<5 && Number(orderInfoVo.secondType)!==2&&orderInfoVo.type==2">
				<view class="h-96 relative" v-if="orderInfoVo.merchantInfoList[0].deliveryMethodMer.split(',').length==2">
					<view class="w-full abs-lb rd-t-24rpx flex bg--w111-fff">
						<view class="flex-center w-50p h-76 fs-28 rd-lt-24rpx z-2"
							:class="shippingType == 1 ? 'bg--w111-fff font-color' : 'bg-primary-light'" @tap="addressType(1)">商家配送
						</view>
						<view class="flex-center w-50p h-76 fs-28 rd-rt-24rpx z-2"
							:class="shippingType == 2 ? 'bg--w111-fff font-color' : 'bg-primary-light'" @tap="addressType(2)">到店自提
						</view>
					</view>
					<view class="w-50p rd-t-24rpx bg--w111-fff h-96" :class="shippingType == 1 ? 'abs-lt' : 'abs-rt'">
						<view class="w-full h-full relative active-card"></view>
					</view>
				</view>
				<view class='address group acea-row row-between-wrapper'
					:class="orderInfoVo.merchantInfoList[0].deliveryMethodMer.split(',').length == 2?'bd-r-14':''  "
					@tap='onAddress' v-if='shippingType == 1'>
					<view class='addressCon' v-if="addressInfo.realName">
						<view class='name acea-row'>
							<view class="line1 select-name">{{addressInfo.realName}}</view>
							<view class='phone'>{{addressInfo.phone}}</view>
						</view>
						<view class="acea-row">
							<view class="line1"><text class='default  font-color'
									v-if="addressInfo.isDefault">[默认]</text>{{addressInfo.province}}{{addressInfo.city}}{{addressInfo.district}}{{ addressInfo.street}}{{addressInfo.detail}}
							</view>
						</view>
					</view>
					<view class='addressCon' v-else>
						<view class='setaddress'>设置收货地址</view>
					</view>
					<view class='iconfont icon-ic_rightarrow mt30'></view>
				</view>
				<view class='address group acea-row row-between-wrapper' v-if='shippingType == 2'
					:class="orderInfoVo.merchantInfoList[0].deliveryMethodMer.split(',').length == 2?'bd-r-14':''  ">
					<view class='addressCon' v-for="(item, index) in merchantOrderVoList" :key="index" @click="goMap(item)">
						<view class='name acea-row'>
							<view class="line1 select-name">{{item.merName}}</view>
							<view class='phone ml-24'>{{item.phone}}</view>
						</view>
						<view class="acea-row">
							<view class="line1">{{item.addressDetail}}</view>
						</view>
					</view>
					<view class='iconfont icon-ic_rightarrow mt30' @click="goMap(merchantOrderVoList[0])"></view>
				</view>
				<view class='line'>
					<image src='../static/images/line.png'></image>
				</view>
			</view>
			<!-- 只有用户地址样式，预约商品不展示 -->
			<view
				v-if="Number(orderInfoVo.secondType)<5 && Number(orderInfoVo.secondType)!==2&&orderInfoVo.type!=2 &&orderInfoVo.type!==7"
				class="allAddress">
				<view class='address acea-row row-between-wrapper' @tap='onAddress'>
					<view class='addressCon' v-if="addressInfo.realName">
						<view class='name mb-16'>{{addressInfo.realName}}
							<text class='phone ml-24'>{{addressInfo.phone}}</text>
						</view>
						<view class="acea-row line2">
							<text class='default font-color' v-if="addressInfo.isDefault">[默认]</text>
							<text>{{addressInfo.province}}{{addressInfo.city}}{{addressInfo.district}}{{ addressInfo.street}}{{addressInfo.detail}}</text>
						</view>
					</view>
					<view class='addressCon' v-else>
						<view class='setaddress'>设置收货地址</view>
					</view>
					<view class='iconfont icon-ic_rightarrow'></view>
				</view>
				<view class='line'>
					<image src='../static/images/line.png'></image>
				</view>
			</view>
			<!-- 预约商品，预约时间 -->
			<view v-show="orderInfoVo.secondType === ProductTypeEnum.Reservation" class="allAddress">
				<view class='address acea-row row-between-wrapper'>
					<view class='f-s-32'>
						<view class='name'>预约时间：
							<text class=''>{{reservationInfo.reservationDate}}</text>
							<text class="ml-20">{{reservationInfo.reservationTime}}</text>
						</view>
					</view>
				</view>
			</view>
			<view class="borderPad mt20">
				<view v-for="(item, index) in merchantOrderVoList" :key="index" class='wrapper borRadius14 pb-8'>
					<view class="borderPad">
						<orderGoods :cartInfo="item.orderInfoList" :orderInfo="item" :secondType="secondType"
							:orderProNum="orderProNum" :isShowBtn="false" :isShowRecord="false">
						</orderGoods>
					</view>
					<view v-show="orderInfoVo.secondType ===0&&orderInfoVo.type!=2" class="boxs">
						<view class='item acea-row row-between-wrapper'>
							<view>配送方式</view>
							<view v-if="item.deliveryMethodMer.length===3 && item.takeTheirSwitch"
								class='discount acea-row row-middle'>
								<text @tap="openShowBox(item,index)">{{item.shippingType === 1 ? '商家配送' : '到店自提'}}</text>

								<text @tap="openShowBox(item,index)" class='text-bbb iconfont icon-ic_rightarrow ml-4'></text>
							</view>
							<view v-else class='discount'>
								{{item.shippingType === 1 ? '商家配送' : '到店自提'}}
							</view>
						</view>
						<view v-if="item.shippingType == 2 && item.takeTheirSwitch" class="store-address acea-row">
							<pick-up-info v-if="Object.keys(item).length > 0 && item.latitude && item.longitude"
								:merAddressInfo="item" :secondType="secondType">
							</pick-up-info>
							<!-- <view>
								<view class="name phone">{{item.phone}}</view>
								<view class="name w-480px">{{item.addressDetail}}</view>
							</view>
							<view class="map" @click="goMap(item)">
								<text class="iconfont icon-ic_location51"></text>
								<view class="map_text">查看地图</view>
							</view> -->
						</view>
					</view>
					<view v-show="item.shippingType === 1 && Number(orderInfoVo.secondType)===0"
						class='item acea-row row-between-wrapper'>
						<view>快递费用</view>
						<view v-if='!item.freightFee || item.freightFee == 0' class="noCoupon">免运费</view>
						<view v-else class='money'>￥{{item.freightFee || 0}}</view>
					</view>
					<view v-show="item.svipDiscountPrice != 0&&orderInfoVo.type!=2" class='item acea-row row-between-wrapper'>
						<view>SVIP优惠</view>
						<view class='money'>-￥{{item.svipDiscountPrice || 0}}</view>
					</view>
					<view v-show="item.merchantMemberDiscountPrice != 0&&orderInfoVo.type!=2" class='item acea-row row-between-wrapper'>
						<view>商户会员优惠</view>
						<view class='money'>-￥{{item.merchantMemberDiscountPrice || 0}}</view>
					</view>
					<view v-if="isProductType" class='item acea-row row-between-wrapper'>
						<view>店铺优惠</view>
						<view v-if="item.merCouponUserList && item.merCouponUserList.length"
							@tap='couponTap(item.merCouponUserList,item.merId, index)'>
							<view class='discount acea-row row-between-wrapper'>
								<text
									class="couponTitle line1">{{item.couponFee==0?`有${item.merCouponUserList.length}张优惠券可选`:`-￥${item.couponFee}`}}</text>
								<text class='iconfont text-bbb icon-ic_rightarrow ml-4'></text>
							</view>
						</view>
						<view v-else class="noCoupon">暂无优惠券</view>
					</view>
					<view class='item acea-row row-between-wrapper' v-if="textareaStatus" style="height: auto;">
						<view>买家留言</view>
						<input class="fs-28" placeholder-class='placeholder' value="" name="mark" placeholder='选填买家留言'
							v-model="item.remark" @input='bindHideKeyboard(item.remark,index)'
							style="width: 484rpx;text-align: right;"></input>
					</view>
				</view>
				<!--商品关联系统表单-->
				<view v-if="orderInfoVo.systemFormValue && orderInfoVo.systemFormValue.length" class='borRadius14'>
					<system-from v-model="orderInfoVo.systemFormValue" ref="systemFromRef"></system-from>
				</view>
				<view v-show="secondType !== ProductTypeEnum.Integral" class='moneyList borRadius14'>
					<view class='item acea-row row-between-wrapper'>
						<view>商品总价</view>
						<view class='money'>￥{{orderInfoVo.proTotalFee || 0}}</view>
					</view>
					<view class='item acea-row row-between-wrapper' v-if="parseInt(orderInfoVo.freightFee) > 0">
						<view>运费：</view>
						<view class='money'>+￥{{orderInfoVo.freightFee || 0}}</view>
					</view>
					<view v-show="orderInfoVo.svipDiscountPrice != 0 &&orderInfoVo.type!=2"
						class='item acea-row row-between-wrapper'>
						<view>SVIP优惠</view>
						<view class='money'>-￥{{orderInfoVo.svipDiscountPrice || 0}}</view>
					</view>
					<view v-show="orderInfoVo.merchantMemberDiscountPrice != 0 &&orderInfoVo.type!=2"
						class='item acea-row row-between-wrapper'>
						<view>商户会员优惠</view>
						<view class='money'>-￥{{orderInfoVo.merchantMemberDiscountPrice || 0}}</view>
					</view>
					<view class='item acea-row row-between-wrapper' v-if="parseInt(merCouponFee) > 0 && isProductType">
						<view>店铺优惠</view>
						<view class='money'>-￥{{merCouponFee}}</view>
					</view>
					<view v-if="isProductType" class='item acea-row row-between-wrapper'>
						<view>平台优惠</view>
						<view v-if="orderInfoVo.platCouponUserList && orderInfoVo.platCouponUserList.length"
							class='discount acea-row row-between-wrapper' @tap='couponTap(orderInfoVo.platCouponUserList,0)'>
							<text
								class="couponTitle line1">{{platCouponFee==0?`有${orderInfoVo.platCouponUserList.length}张优惠券可选`:`-￥${platCouponFee}`}}</text>
							<text class='text-bbb iconfont icon-ic_rightarrow ml-4'></text>
						</view>
						<view v-else class="noCoupon">暂无优惠券</view>
					</view>
					<view class='item acea-row row-between-wrapper' v-if="orderInfoVo.integralDeductionSwitch && isProductType">
						<view>积分抵扣</view>
						<view class='discount acea-row row-middle'>
							<view class="mr14"> {{isUseIntegral ? "使用积分":"当前积分"}}
								<text
									class='num font-color'>{{ isUseIntegral ? orderInfoVo.surplusIntegral : orderInfoVo.userIntegral}}</text>
							</view>
							<checkbox-group @change="ChangeIntegral">
								<checkbox :checked='isUseIntegral ? true : false'
									:disabled="orderInfoVo.userIntegral==0 && !isUseIntegral" />
							</checkbox-group>
						</view>
					</view>

					<view class='item acea-row row-between-wrapper' v-if="Number(orderInfoVo.deductionPrice) > 0">
						<view>抵扣金额</view>
						<view class='money'>-￥{{orderInfoVo.deductionPrice || 0}}</view>
					</view>

				</view>
				<view style='height:120rpx;'></view>
			</view>
			<view class='footer acea-row row-between-wrapper'>
				<view class="acea-row row-middle">合计：
					<!-- 积分价格 -->
					<PointsPrice v-if="orderInfoVo.secondType === ProductTypeEnum.Integral" :pointsPrice="orderInfoVo"
						:pointsGoodsStyle="hotPointsStyle"></PointsPrice>
					<!-- 其他价格 -->
					<text v-else class='price_color f-s-38 fw-500 regular line-heightOne'>￥{{orderInfoVo.payFee || 0}}</text>
				</view>
				<view class='settlement' style='z-index:100' @tap="SubOrder">
					{{secondType === ProductTypeEnum.Integral?"确认兑换":"立即下单"}}
				</view>
			</view>
		</view>
		<couponListWindow :coupon='coupon' @ChangCouponsClone="ChangCouponsClone" :openType='openType'
			@ChangCoupons="ChangCoupons" :orderShow="orderShow" :surplusFee="surplusFee"></couponListWindow>

		<view v-if="isShowBox">
			<checkDelivery :isShowBox="isShowBox" :activeObj="activeObj" @close="boxClose" @confirmBtn="getShippingType">
			</checkDelivery>
		</view>
		<!-- 满员提示 -->
		<uni-popup ref="sh_popup" background-color="#fff" borderRadius="10px">
			<view class="sh_popup-content">
				<view class="sh_popup_title">
					提示
				</view>
				<view class="sh_popup_text">
					该团已拼成，是否自行开团？
				</view>
				<view class="sh_popup_btn">
					<view class="no_btn btn font-color" @click="popupClose">返回首页</view>
					<view class="yes_btn btn" @click="toAudit">去开团</view>
				</view>
			</view>
		</uni-popup>
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
		orderCreate,
		postOrderComputed,
		loadPreOrderApi
	} from '@/api/order.js';
	import {
		takeTheirApi
	} from '@/api/merchant.js';
	import couponListWindow from '../components/couponListWindow';
	import orderGoods from '@/components/orderGoods'
	import navBar from '@/components/navBar';
	import checkDelivery from '../components/checkDelivery/index.vue';
	import systemFrom from '../components/systemFrom/index.vue';
	import PointsPrice from '@/components/PointsPrice.vue';
	import pickUpInfo from './components/pickUpInfo.vue';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import {
		Debounce
	} from '@/utils/validate.js'
	import {
		ProductTypeEnum,
		ProductMarketingTypeEnum
	} from "../../../enums/productEnums";
	import orderPay from "@/mixins/OrderPay.js";
	let app = getApp();
	/**
	 * 积分商品推荐样式
	 */
	const hotPointsStyle = {
		iconStyle: {
			width: '28rpx',
			height: '28rpx'
		},
		priceStyle: {
			fontSize: '38rpx',
		},
		unitStyle: {
			fontSize: '26rpx',
		},
	}
	export default {
		mixins: [orderPay],
		components: {
			navBar,
			couponListWindow,
			orderGoods,
			checkDelivery,
			systemFrom,
			PointsPrice,
			pickUpInfo
		},
		computed: {
			// 是否可以使用优惠券、积分抵扣。 基础订单/云盘订单，卡密,虚拟，预约，可以使用优惠券、积分抵扣
			isProductType() {
				return this.type === ProductMarketingTypeEnum.Normal && this.secondType !== this.ProductTypeEnum
					.Integral && this.secondType !== this.ProductTypeEnum.Video
			},
			...mapGetters(['productType', 'isLogin'])
		},
		data() {
			return {
				hotPointsStyle: hotPointsStyle,
				ProductMarketingTypeEnum: ProductMarketingTypeEnum,
				ProductTypeEnum: ProductTypeEnum,
				navTitle: '提交订单',
				homeTop: 20,
				orderShow: 'orderShow', //下单页面使用优惠券组件不展示tab切换页
				textareaStatus: true,
				openType: 1, //优惠券打开方式 1=使用
				couponShow: false,
				coupon: {
					coupon: false,
					list: [], //商户优惠券
					statusTile: '立即使用',
					couponMoney: 0
				}, //优惠券组件
				addressInfo: {}, //地址信息
				addressId: 0, //地址id
				orderMerchantRequestList: [], //商户属性集合
				cartId: '', //购物车id
				userInfo: {}, //用户信息
				mark: '', //买家留言
				couponFee: '请选择', //优惠券
				coupon_price: 0, //优惠券抵扣金额
				isUseIntegral: false, //是否使用积分
				integral_price: 0, //积分抵扣金额
				integral: 0,
				ChangePrice: 0, //使用积分抵扣变动后的金额
				formIds: [], //收集formid
				status: 0,
				toPay: false, //修复进入支付时页面隐藏从新刷新页面
				shippingType: 1,
				storePostage: 0,
				contacts: '',
				contactsTel: '',
				mydata: {},
				merchantOrderVoList: [],
				priceGroup: {},
				animated: false,
				totalPrice: 0,
				integralRatio: "0",
				orderKey: "",
				// usableCoupon: {},
				offlinePostage: "",
				news: true,
				again: false,
				addAgain: false,
				bargain: false, //是否是砍价
				combination: false, //是否是拼团
				secKill: false, //是否是秒杀
				orderInfoVo: {},
				addressList: [], //地址列表数据
				orderProNum: 0,
				orderNo: '', //预下单订单号
				theme: app.globalData.theme,
				addressChangeId: 0,
				isShowBox: false,
				activeObj: {},
				activeIndex: 0, // 选中店铺索引
				type: 0, // 0-基础订单,1-秒杀订单,2-拼团订单
				secondType: 0, //订单二级类型:0-普通订单，1-积分订单，2-虚拟订单，4-视频号订单，5-云盘订单，6-卡密订单
				merId: 0, //商户id，用于判断商户优惠券还是平台优惠券，平台优惠券商户id为0
				platUserCouponObj: {}, //平台优惠券对象，用于缓存数据使用
				platUserCouponId: 0, //平台优惠券id
				platCouponFee: '', //平台优惠券金额
				merCouponFee: '', //店铺优惠券总金额
				surplusFee: 0, //商品总金额-商户优惠券金额=平台端可使用优惠券的门槛
				merUserCouponId: 0, //店铺使用优惠券的id
				tempCouponObj: {}, //临时优惠券数据
				merchangtInfo: {}, //商户信息
				orderForm: [], //系统表单配置的数据
				orderExtend: {}, //提交接口表单的数据
				productId: '',
				groupActivityId: '',
				reservationInfo: {} // 预约信息
			};
		},
		watch: {
			isLogin: {
				handler: function(newV, oldV) {
					if (newV) {
						this.getloadPreOrder();
					}
				},
				deep: true
			},
		},
		onLoad(options) {
			this.orderNo = options.orderNo || 0;
			this.addressChangeId = parseInt(options.addressId) || 0;
			if (this.isLogin) {
				this.getloadPreOrder();
			} else {
				toLogin();
			}
		},
		/**
		 * 生命周期函数--监听页面显示
		 */
		onShow: function() {
			let _this = this
			this.textareaStatus = true;
		},
		methods: {
			//滚动
			touchStart() {
				// #ifdef MP || APP
				this.$refs.navBarRef.currentPage = false;
				// #endif
			},
			popupClose() {
				this.$refs.sh_popup.close()
				this.$util.navigateTo('/pages/index/index');
			},
			//去开团
			toAudit() {
				this.$refs.sh_popup.close()
				uni.navigateTo({
					url: `/pages/goods/goods_details/index?id=${this.productId}&mt=2&gd=${this.groupActivityId}`
				})
			},
			addressType(type) {
				this.merchantOrderVoList[0].shippingType = type
				this.getShippingType(this.merchantOrderVoList[0])
				if (type == 2) {
					setTimeout(() => {
						this.shippingType = type
					}, 300)
				} else {
					this.shippingType = type
				}
			},
			//选择配送方式回调 
			getShippingType(item) {
				this.orderMerchantRequestList[this.activeIndex].shippingType = item.shippingType;
				this.$set(this.merchantOrderVoList[this.activeIndex], 'shippingType', item.shippingType);
				if (item.shippingType === 2) this.getTakeTheir(item.merId);
				this.computedPrice();
				this.isShowBox = false;
			},
			getTakeTheir(id) {
				takeTheirApi(id).then(res => {
					this.merchangtInfo = res.data; //商户信息
					this.$set(this.merchantOrderVoList[this.activeIndex], 'addressDetail', res.data.addressDetail);
					this.$set(this.merchantOrderVoList[this.activeIndex], 'phone', res.data.phone);
					this.$set(this.merchantOrderVoList[this.activeIndex], 'latitude', res.data.latitude);
					this.$set(this.merchantOrderVoList[this.activeIndex], 'longitude', res.data.longitude);
					this.$set(this.merchantOrderVoList[this.activeIndex], 'coverImage', res.data.coverImage);
				}).catch(err => {
					return this.$util.Tips({
						title: err
					});
				})
			},
			//查看内置地图
			goMap(item) {
				let that = this;
				//#ifdef H5
				if (that.$wechat.isWeixin() === true) {
					that.$wechat.seeLocation({
						latitude: parseFloat(this.merchantOrderVoList[this.activeIndex].latitude),
						longitude: parseFloat(this.merchantOrderVoList[this.activeIndex].longitude),
						name: item.merName,
						address: item.addressDetail ? item.addressDetail : '',
					}).then(res => {
						console.log('success');
					})
				} else {
					//#endif
					uni.openLocation({
						latitude: parseFloat(this.merchantOrderVoList[this.activeIndex].latitude),
						longitude: parseFloat(this.merchantOrderVoList[this.activeIndex].longitude),
						scale: 8,
						name: item.merName,
						address: item.addressDetail ? item.addressDetail : '',
						success: function(res) {
							that.go_map = true
						},
					});
					// #ifdef H5
				}
				//#endif
			},
			// 打开配送方式弹窗
			openShowBox(item, index) {
				this.activeObj = item
				this.activeIndex = index
				this.isShowBox = true
			},
			boxClose() {
				this.isShowBox = false
			},
			// 订单详情
			getloadPreOrder: function() {
				loadPreOrderApi(this.orderNo).then(res => {
					let orderInfoVo = res.data;
					if (orderInfoVo.merchantInfoList[0].orderInfoList[0].groupBuyActivityId) {
						this.groupActivityId = orderInfoVo.merchantInfoList[0].orderInfoList[0].groupBuyActivityId
						this.productId = orderInfoVo.merchantInfoList[0].orderInfoList[0].productId
					}
					if (orderInfoVo.merchantInfoList[0].deliveryMethodMer.split(',').length == 1) {
						this.shippingType = orderInfoVo.merchantInfoList[0].deliveryMethodMer
					}
					this.reservationInfo = uni.getStorageSync('reservationInfo') || {} //缓存的预约信息，地址id，配送类型
					orderInfoVo.merchantInfoList.map(item => {
						if (item.shippingType === 2) this.getTakeTheir(item.merId)
						this.orderMerchantRequestList.push({
							shippingType: orderInfoVo.secondType === this.ProductTypeEnum.Reservation ? this
								.reservationInfo.shippingType : item.shippingType,
							merId: item.merId,
							remark: '',
							userCouponId: item.userCouponId
						})
						item.addressDetail = this.merchangtInfo.addressDetail;
						item.phone = this.merchangtInfo.phone;
						item.latitude = this.merchangtInfo.latitude;
						item.longitude = this.merchangtInfo.longitude;
					});
					this.merchantOrderVoList = orderInfoVo.merchantInfoList; //商户端数据
					this.platCouponFee = orderInfoVo.platCouponFee; //平台优惠券总金额
					this.platUserCouponId = orderInfoVo.platUserCouponId;
					this.merCouponFee = orderInfoVo.merCouponFee; //店铺优惠券总金额
					this.orderInfoVo = {
						...orderInfoVo,
						systemFormValue: orderInfoVo.systemFormValue ? this.$util.objToArr(JSON.parse(
							orderInfoVo.systemFormValue)) : []
					};
					let payNum = orderInfoVo.formDataSubmitType === 1 ? orderInfoVo.merchantInfoList[0].orderInfoList[0]
						.payNum : 1 //预约单的商品下单数量
					this.$nextTick(() => {
						if (this.orderInfoVo.systemFormValue.length) {
							this.$refs.systemFromRef.getFormList(payNum)
						}
					})
					this.type = orderInfoVo.type; //订单类型
					this.secondType = orderInfoVo
						.secondType; //订单二级类型:0-普通订单，1-积分订单，2-虚拟订单，4-视频号订单，5-云盘订单，6-卡密订单
					this.orderProNum = orderInfoVo.orderProNum;
					if (orderInfoVo.addressId && this.addressChangeId === 0) {
						this.addressId = orderInfoVo.addressId;
					} else {
						this.addressId = this.addressChangeId;
						if (orderInfoVo.addressId != this.addressChangeId && this.addressChangeId > 0)
							this.computedPrice();
					}
					this.getAddressInfo();
				}).catch(err => {
					uni.navigateTo({
						url: '/pages/goods/order_list/index'
					});
				})
			},
			// 计算订单价格
			computedPrice: function() {
				uni.showLoading({
					title: '加载中...'
				});
				let shippingType = this.shippingType;
				postOrderComputed({
					addressId: this.addressId,
					isUseIntegral: this.isUseIntegral,
					orderMerchantRequestList: this.orderMerchantRequestList,
					preOrderNo: this.orderNo,
					platUserCouponId: this.platUserCouponId
				}).then(res => {
					let data = res.data;
					//usedIntegral 使用的积分，surplusIntegral 剩余积分
					data.merOrderResponseList.map((item, i) => {
						this.merchantOrderVoList[i].freightFee = item.freightFee
						this.merchantOrderVoList[i].couponFee = item.couponFee
					});
					this.orderInfoVo.platCouponUserList = data.platCouponUserList; //平台优惠券数据
					this.merCouponFee = data.merCouponFee; //店铺优惠券总金额
					this.orderInfoVo.couponFee = data.couponFee; //优惠券优惠金额
					this.orderInfoVo.userIntegral = data.surplusIntegral; //使用的积分
					this.orderInfoVo.deductionPrice = data.deductionPrice;
					this.orderInfoVo.freightFee = data.freightFee;
					this.orderInfoVo.payFee = data.payFee;
					this.orderInfoVo.proTotalFee = data.proTotalFee;
					this.orderInfoVo.surplusIntegral = data.usedIntegral; //剩余积分
					this.platCouponFee = data.platCouponFee; //平台优惠金额
					this.orderInfoVo.integralDeductionSwitch = data.integralDeductionSwitch //积分抵扣开关
					//选中商户优惠券的值
					this.merchantOrderVoList[this.activeIndex].merCouponUserList = data.merOrderResponseList[
						this.activeIndex].merCouponUserList; //商户数据
					this.merUserCouponId = data.merOrderResponseList[this.activeIndex]
						.userCouponId //店铺使用优惠券的id
					uni.hideLoading();
				}).catch(err => {
					uni.hideLoading();
					return this.$util.Tips({
						title: err
					});
				});
			},
			bindPickerChange: function(e) {
				let value = e.detail.value;
				this.shippingType = value;
				this.computedPrice();
			},
			/**
			 * 使用积分抵扣
			 */
			ChangeIntegral: function() {
				this.isUseIntegral = !this.isUseIntegral;
				this.computedPrice();
			},
			bindHideKeyboard: function(e, i) {
				this.orderMerchantRequestList[i].remark = e;
			},


			//关闭优惠券弹窗
			ChangCouponsClone: function(coupon) {
				this.coupon.list = [];
				this.coupon.coupon = false;
			},
			//点击优惠券弹窗确定后
			ChangCoupons: function(item) {
				this.coupon.list = [];
				if (item.merId === 0) {
					this.platUserCouponId = item.isChecked ? item.id : 0;
				} else {
					this.$set(this.orderMerchantRequestList[this.activeIndex], 'userCouponId', item.isChecked ? item
						.id : 0);

				}
				this.coupon.coupon = false;
				this.computedPrice();
			},
			/**
			 * 选择优惠券
			 * @param {Object} item优惠券对象
			 * @param {Number} merId商户id
			 * @param {Number} index索引
			 */
			couponTap: function(item, merId, index) {
				this.$set(this.coupon, 'list', item);
				this.$set(this.coupon, 'couponMoney', item);
				this.coupon.coupon = true;
				this.merId = merId; //商户id
				if (merId !== 0) this.activeIndex = index;
				this.coupon.loading = false;
			},
			/**
			 * 获取当前金额可用优惠券
			 *
			 */
			getCouponList: function(item, merId) {
				this.$set(this.coupon, 'list', item);
				this.openType = 1;
				uni.hideLoading();
			},
			onAddress: function() {
				uni.navigateTo({
					url: '/pages/address/user_address_list/index?orderNo=' + this.orderNo
				});
			},
			realName: function(e) {
				this.contacts = e.detail.value;
			},
			phone: function(e) {
				this.contactsTel = e.detail.value;
			},
			payment: function(data) {
				let that = this;
				// #ifdef MP
				uni.checkBeforeAddOrder({
					success(res) {
						console.log("下单前置检查 成功：", JSON.stringify(res));
						const traceId = res.data.traceId;
						data.traceId = traceId;
						that.onCreate(data);
					},
					fail(res) {
						console.log("下单前置检查 失败：", JSON.stringify(res));
					}
				});
				// #endif
				// #ifndef MP
				that.onCreate(data);
				// #endif
			},
			onCreate(data) {
				orderCreate(data).then(res => {
					if (res.data.groupBuyIsFull == 1) {
						uni.hideLoading();
						return this.$refs.sh_popup.open('center')
					}
					if (this.secondType === this.ProductTypeEnum.Integral && this.orderInfoVo.payFee == 0) {
						// 积分商品并且支付金额为0时，直接默认走余额支付的逻辑，订单支付成功跳转到支付结果页
						this.changeOrderPay(res.data.orderNo, 'yue', 'yue', 'integral', 'integral', '0')
					} else {
						// 其他商品走正常流程，去支付收银台页面
						this.getToPayment(this.secondType, res.data)
					}

					uni.hideLoading();
				}).catch(err => {
					uni.hideLoading();
					return this.$util.Tips({
						title: err
					});
				});
			},
			//立即下单
			SubOrder: Debounce(async function(e) {
				let that = this,
					data = {};
				let flag = false;
				that.orderMerchantRequestList.map(function(v) {
					if (v.shippingType === 1 && !that.addressId && that.orderInfoVo.secondType !== 2) {
						flag = true;
					}
				});
				if (flag && this.secondType !== this.ProductTypeEnum.Reservation) {
					that.$util.Tips({
						title: '请选择收货地址'
					});
					return;
				}
				let systemFormData = []
				if (this.orderInfoVo.systemFormValue.length) {
					systemFormData = await this.$refs.systemFromRef.getSubmitFromData()
					if (!systemFormData.length) return;
				}

				data = {
					addressId: this.secondType === this.ProductTypeEnum.Reservation ? this.reservationInfo.addressId : that
						.addressId,
					orderMerchantRequestList: that.orderMerchantRequestList,
					isUseIntegral: that.isUseIntegral,
					preOrderNo: that.orderNo,
					platUserCouponId: this.platUserCouponId,
					systemFormId: this.orderInfoVo.systemFormId,
					orderExtend: systemFormData.length ? JSON.stringify(systemFormData) : '',
					reservationName: this.reservationInfo.reservationName,
					reservationPhone: this.reservationInfo.reservationPhone
				};
				uni.showLoading({
					title: '订单提交中'
				});
				await that.payment(data);
			})
		}
	}
</script>

<style lang="scss" scoped>
	.w-480px {
		width: 480rpx;
	}

	.noCoupon {
		color: #999999;

	}

	.mr14 {
		margin-right: 14rpx;
	}

	.couponTitle {
		width: 430rpx;
		display: inline-block;
		text-align: right;
		color: #666666;
		// @include main_color(theme);
	}

	.store-address {
		// padding: 20rpx;
		margin: 0 24rpx;
		background: #F6F6F6;
		// background: #FFF;
		// border: solid 2rpx #F6F6F6;
		border-radius: 16rpx;
		font-size: 24rpx;
		/*垂直居中*/
		-webkit-box-align: center;
		/*旧版本*/
		-moz-box-align: center;
		/*旧版本*/
		-ms-flex-align: center;
		/*混合版本*/
		-webkit-align-items: center;
		/*新版本*/
		align-items: center;

		/*新版本*/
		.phone {
			margin-bottom: 10rpx;
			color: #282828;
			font-weight: bold;
		}

		.name {
			padding-right: 20rpx;
		}

		.info {
			flex: 1;
		}

		.line2 {
			width: 456rpx !important;
		}

		.map {
			text-align: center;
			padding-left: 36rpx;
			position: relative;
			@include main_color(theme);

			&::before {
				content: '';
				display: inline-block;
				width: 2rpx;
				height: 42rpx;
				background-color: #DDDDDD;
				position: absolute;
				left: 0;
				top: 18rpx;
			}

			.iconfont {
				color: var(--view-theme);
			}

			.map_text {
				color: var(--view-theme);
			}
		}
	}

	.price_color {
		@include price_color(theme);
	}

	.line2 {
		width: 624rpx;
	}

	.textR {
		text-align: right;
	}


	.order-submission .wrapper .item .discount .placeholder {
		color: #ccc;
	}

	.order-submission .wrapper {
		background-color: #fff;
	}

	.order-submission .wrapper .item {
		padding: 20rpx 24rpx;
		font-size: 28rpx;
		color: #333333;
	}

	.order-submission .wrapper .item .discount {
		font-size: 28rpx;
		color: #333;
	}

	.order-submission .wrapper .item .discount .num {
		font-size: 32rpx;
		margin-right: 20rpx;
	}

	.order-submission .wrapper .item .shipping {
		font-size: 28rpx;
		color: #999;
		position: relative;
		padding-right: 58rpx;
	}

	.order-submission .wrapper .item .shipping .iconfont {
		font-size: 35rpx;
		color: #707070;
		position: absolute;
		right: 0;
		top: 50%;
		transform: translateY(-50%);
		margin-left: 30rpx;
	}

	.order-submission .wrapper .item textarea {
		background-color: #f9f9f9;
		width: auto !important;
		height: 140rpx;
		border-radius: 14rpx;
		margin-top: 30rpx;
		padding: 15rpx;
		box-sizing: border-box;
		font-weight: 400;
	}

	.order-submission .wrapper .item .placeholder {
		color: #ccc;
	}

	.order-submission .wrapper .item .list {
		margin-top: 35rpx;
	}

	.order-submission .wrapper .item .list .payItem {
		border: 1px solid #eee;
		border-radius: 14rpx;
		height: 86rpx;
		width: 100%;
		box-sizing: border-box;
		margin-top: 20rpx;
		font-size: 28rpx;
		color: #282828;
	}

	.order-submission .wrapper .item .list .payItem.on {
		// border-color: #fc5445;
		@include coupons_border_color(theme);
		color: $theme-color;
	}

	.order-submission .wrapper .item .list .payItem .name {
		width: 50%;
		text-align: center;
		border-right: 1px solid #eee;
	}

	.order-submission .wrapper .item .list .payItem .name .iconfont {
		width: 44rpx;
		height: 44rpx;
		border-radius: 50%;
		text-align: center;
		line-height: 44rpx;
		background-color: #fe960f;
		color: #fff;
		font-size: 28rpx;
		margin-right: 15rpx;
	}

	.order-submission .wrapper .item .list .payItem .name .iconfont.icon-weixin2 {
		background-color: #41b035;
	}

	.order-submission .wrapper .item .list .payItem .name .iconfont.icon-a-ic_alipay {
		background-color: #00AAEA;
	}

	.order-submission .wrapper .item .list .payItem .tip {
		width: 49%;
		text-align: center;
		font-size: 26rpx;
		color: #aaa;
	}

	.order-submission .moneyList {
		margin-top: 20rpx;
		background-color: #fff;
		padding: 10rpx 24rpx;
		margin-bottom: calc(constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		margin-bottom: calc(env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
	}

	.order-submission .moneyList .item {
		font-size: 28rpx;
		color: #282828;
		height: 80rpx;
	}

	.order-submission .moneyList .item~.item {
		// margin-top: 20rpx;
	}

	.order-submission .moneyList .item .money {
		color: #666666;
	}

	.order-submission .footer {
		width: 100%;
		height: 100rpx;
		background-color: #fff;
		padding: 0 30rpx;
		font-size: 28rpx;
		color: #333;
		box-sizing: border-box;
		position: fixed;
		bottom: 0;
		left: 0;
		height: calc(100rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		height: calc(100rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
		z-index: 999;
	}

	.order-submission .footer .settlement {
		background-color: #fff;
		font-size: 26rpx;
		color: #fff;
		width: 200rpx;
		height: 72rpx;
		@include main_bg_color(theme);
		border-radius: 60rpx;
		text-align: center;
		line-height: 72rpx;
	}

	.footer .transparent {
		opacity: 0
	}

	/deep/ checkbox .uni-checkbox-input.uni-checkbox-input-checked,
	/deep/ .uni-radio-input-checked {
		@include main_bg_color(theme);
		border: none !important;
		color: #fff !important
	}

	/deep/ checkbox .wx-checkbox-input.wx-checkbox-input-checked,
	/deep/ .wx-radio-input-checked {
		@include main_bg_color(theme);
		border: none !important;
		color: #fff !important;
		margin-right: 0 !important;
	}

	// 切换
	/deep/.uni-date-x--border {
		border: 0;
	}

	/deep/.uni-icons {
		font-size: 0 !important;
	}

	/deep/.uni-date-x {
		color: #999;
		font-size: 15px;
	}

	/deep/.uni-date__x-input {
		font-size: 15px;
	}

	/deep/uni-checkbox[disabled] .uni-checkbox-input {
		background-color: #eee;
	}

	.address {
		background-color: #fff;
		box-sizing: border-box;
	}

	.footer .transparent {
		opacity: 0
	}

	.w-322 {
		width: 322rpx;
	}

	.w-450 {
		width: 450rpx;
	}

	.h-auto {
		height: auto;
	}

	.mt30 {
		margin-top: 30rpx;
	}

	.bg-primary-light {
		@include main_rgba_color(theme);
	}

	.rd-lt-24rpx {
		border-radius: 24rpx 0 0 0;
	}

	.rd-rt-24rpx {
		border-radius: 0 24rpx 0 0;
	}

	.z-2 {
		z-index: 2;
	}

	.gold {
		color: #DCA658;
	}

	.select-name {
		max-width: 300rpx;
	}

	.font-color {
		font-weight: 500 !important;
	}

	/deep/ .uni-popup__wrapper {
		background: #FFFFFF;
		border-radius: 32rpx;
	}

	.sh_popup-content {
		padding: 40rpx;

		.sh_popup_title {
			font-weight: 500;
			font-size: 32rpx;
			color: #333333;
			text-align: center;
		}

		.sh_popup_text {
			font-weight: 400;
			font-size: 28rpx;
			color: #666666;
			text-align: center;
			margin-top: 36rpx;
		}

		.sh_popup_btn {
			display: flex;
			margin-top: 60rpx;
			justify-content: space-between;

			.btn {
				width: 244rpx;
				height: 72rpx;
				border-radius: 50rpx;
				text-align: center;
				line-height: 72rpx;
			}

			.no_btn {
				@include coupons_border_color(theme);
				color: $theme-color;
				margin-right: 32rpx;
			}

			.yes_btn {
				@include main_bg_color(theme);
				color: #FFFFFF;
			}
		}
	}

	.pick-gradient-bg {
		@include index-gradient(theme);
	}

	.pick-up-info-top-box {
		background: #FFFFFF;
		border-radius: 24rpx;
		overflow: hidden;
	}
	
	.wrapper {
		& + .wrapper {
			margin-top: 20rpx;
		}
	}
	
	.pt-24 {
		padding-top: 24rpx;
	}

	.pt20 {
		padding-top: 20rpx;
	}

	.pb-24 {
		padding-bottom: 24rpx;
	}
</style>