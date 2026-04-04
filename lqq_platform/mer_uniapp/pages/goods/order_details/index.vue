<template>
	<view :data-theme="theme">
		<view class='order-details'>
			<!-- 给header上与data上加on为退款订单-->
			<view class='header bg-color'>
				<view class='picTxt acea-row row-middle'>
					<view class='data'>
						<view class="flex">
							<view v-if="orderInfo.refundStatus == 3" class='state fs-36'>已退款</view>
							<!-- 次卡商品部分核销 -->
							<view v-else-if="orderInfo.status == 3 && secondType == ProductTypeEnum.PunchCard && 
								punchCardOrderInfo.verifyRemainingTimes!=punchCardOrderInfo.verifyTotalTimes" class="state fs-36">
								<text>部分使用</text>
							</view>
							<view v-else class='state fs-36'>{{orderInfo.status | orderStatusFilter}}</view>
							<!-- 次卡商品核销次数 -->
							<view v-if="orderInfo.status == 3 && secondType == ProductTypeEnum.PunchCard" class='state fs-36 ml-12'>
								{{punchCardOrderInfo.verifyTotalTimes-punchCardOrderInfo.verifyRemainingTimes}} /
								{{punchCardOrderInfo.verifyTotalTimes}}
							</view>
						</view>
						<view v-if="secondType === ProductTypeEnum.Reservation" class="fs-26 flex">
							<view class="">
								预约时间：{{merchantOrderList.reservationDate}}
							</view>
							<view class="ml-16">
								{{merchantOrderList.reservationTimeSlot}}
							</view>
						</view>
						<view v-else-if="secondType === ProductTypeEnum.PunchCard" class="fs-26 flex">
							使用时间：
							<view v-if="punchCardOrderInfo.verifyEndDate" class="flex">
								<view>{{punchCardOrderInfo.verifyStartDate}}</view>
								<view>~</view>
								<view>{{punchCardOrderInfo.verifyEndDate}}</view>
								<view class="ml-8">到期</view>
							</view>
							<view v-else>无限期</view>
						</view>
						<view v-else class="fs-26">
							<view v-if="orderInfo.createTime && orderInfo.status>0">{{orderInfo.createTime}}</view>
							<view v-if="orderInfo.refundReasonTime !== null">{{orderInfo.refundReasonTime}}</view>
							<view v-if="orderInfo.status===0">请在{{orderInfo.expirationTime}}前完成支付！</view>
						</view>
					</view>
					<view class="">
						<image class="orderStatusPng"
							:src="urlDomain+`crmebimage/presets/orderStatus/${statusPng[orderInfo.status]}.png`" mode=""></image>
					</view>
				</view>
				<view class="gradient-bg absolute" style="height: 100rpx;"></view>
			</view>
			<view style="margin-top: -100rpx;">
				<view class="borderPad relative z-10">
					<!-- 进度条 -->
					<progress-bar
						v-if="orderInfo.status !== 9 && orderInfo.refundStatus !== 3 && shippingType != 2 && shippingType != 4"
						:orderInfo="orderInfo" :status="orderInfo.status" :shippingType="shippingType" :secondType="secondType">
					</progress-bar>
					<!-- 预约信息 -->
					<reservation-info v-if="merchantOrderList && secondType === ProductTypeEnum.Reservation"
						:orderInfo="merchantOrderList" :codeUrl="codeImg" @copy="handleCopy" @showMaoLocation="showMaoLocation"
						@makePhone="makePhone">
					</reservation-info>
					<!-- 用户信息 -->
					<top-order-info
						v-if="merchantOrderList && Object.keys(merchantOrderList).length > 0 && shippingType == 1 && secondType !== ProductTypeEnum.Fictitious"
						:orderInfo="merchantOrderList" :secondType="secondType" :type="'merDelivery'"
						@showMaoLocation="showMaoLocation">
					</top-order-info>
					<!-- 商户信息 -->
					<top-order-info v-if="merchantOrderList && Object.keys(merchantOrderList).length > 0 && shippingType == 2"
						:orderInfo="merchantOrderList" :secondType="secondType" :type="'pickUp'" @showMaoLocation="showMaoLocation">
					</top-order-info>
					<block v-for="(item, index) in orderInfo.merchantOrderList" :key="item.merId">
						<view v-if="item.shippingType == 2 && orderInfo.status == 3 && orderInfo.refundStatus !== 3">
							<!-- 核销码 -->
							<view class="writeOff borRadius14">
								<VerificationQRCode :codeImg="codeImg" :merOrderInfo="item" @handleCopy="handleCopy">
								</VerificationQRCode>
							</view>
						</view>
						<view class="borRadius14 orderGoodsBox mt20">
							<view class="borderPad">
								<orderGoods :orderInfo="item" :orderNo="orderNo" :cartInfo="item.orderInfoList" :jump="true"
									:orderData="orderInfo" :secondType="orderInfo.secondType">
								</orderGoods>
							</view>
						</view>
					</block>
				</view>
			</view>
			<!-- 预约信息 -->
			<view v-if="orderInfo.secondType == ProductTypeEnum.Reservation" class="borderPad">
				<view class="writeOff borRadius14 pad24">
					<view class='item acea-row row-between'>
						<view>预约时间：</view>
						<view>
							{{ merchantOrderList.orderInfoList[0].reservationDate || '-' }}
							{{ merchantOrderList.orderInfoList[0].reservationTimeSlot || '-' }}
						</view>
					</view>
					<view class='item acea-row row-between mt-32'>
						<view>预约数量：</view>
						<view>
							{{merchantOrderList.orderInfoList[0].payNum}}
						</view>
					</view>
				</view>
			</view>
			<!-- 系统表单信息 "-->
			<view v-if="orderExtend.length" class="borderPad">
				<view class="w-100-p111- overflow-hidden">
					<systemFromInfo :orderExtend="orderExtend"></systemFromInfo>
				</view>
			</view>

			<!-- 订单信息 "-->
			<view class="borderPad">
				<view class='wrapper borRadius14'>
					<view class='item acea-row row-between'>
						<view>订单编号：</view>
						<view class='conter acea-row row-middle row-right' style="width: auto;"><text
								class="text-overflow">{{orderInfo.orderNo}}</text>
							<!-- #ifndef H5 -->
							<text class='order-copy-btn line-heightOne' @tap='handleCopy(orderInfo.orderNo)'>复制</text>
							<!-- #endif -->
							<!-- #ifdef H5 -->
							<text class='order-copy-btn copy-data' :data-clipboard-text="orderInfo.orderNo">复制</text>
							<!-- #endif -->
						</view>
					</view>
					<view
						v-show="(orderInfo.payType && secondType !== ProductTypeEnum.Integral) || (secondType === ProductTypeEnum.Integral && orderInfo.payPrice!=0)"
						class='item acea-row row-between'>
						<view>支付方式：</view>
						<view class='conter'>{{orderInfo.payType | payTypeFilter}}</view>
					</view>
					<view class='item acea-row row-between'>
						<view>支付状态：</view>
						<view class='conter' v-if="orderInfo.paid">已支付</view>
						<view class='conter' v-else>未支付</view>
					</view>
					<view class='item acea-row row-between'>
						<view>支付时间：</view>
						<view class='conter'>{{orderInfo.payTime || '-'}}</view>
					</view>
					<view class='item acea-row row-between'>
						<view>下单时间：</view>
						<view class='conter'>{{(orderInfo.createTime || 0)}}</view>
					</view>
					<view class='item acea-row row-between'>
						<view>买家留言：</view>
						<view class='conter'>{{merchantOrderList.userRemark || '-'}}</view>
					</view>
				</view>
				<view>
					<view class='wrapper borRadius14' v-if='orderInfo.deliveryType=="express"'>
						<view class='item acea-row row-between'>
							<view>配送方式：</view>
							<view class='conter'>发货</view>
						</view>
						<view class='item acea-row row-between'>
							<view>快递公司：</view>
							<view class='conter'>{{orderInfo.deliveryName || ''}}</view>
						</view>
						<view class='item acea-row row-between'>
							<view>快递号：</view>
							<view class='conter'>{{orderInfo.deliveryId || ''}}</view>
						</view>
					</view>
					<view class='wrapper borRadius14' v-else-if='orderInfo.deliveryType=="send"'>
						<view class='item acea-row row-between'>
							<view>配送方式：</view>
							<view class='conter'>送货</view>
						</view>
						<view class='item acea-row row-between'>
							<view>配送人姓名：</view>
							<view class='conter'>{{orderInfo.deliveryName || ''}}</view>
						</view>
						<view class='item acea-row row-between'>
							<view>联系电话：</view>
							<view class='conter acea-row row-middle row-right'>{{orderInfo.deliveryId || ''}}<text class='copy'
									@tap='goTel'>拨打</text></view>
						</view>
					</view>
					<view class='wrapper borRadius14' v-else-if='orderInfo.deliveryType=="fictitious"'>
						<view class='item acea-row row-between'>
							<view>虚拟发货：</view>
							<view class='conter'>已发货，请注意查收</view>
						</view>
					</view>
				</view>
				<view class='wrapper borRadius14'>
					<view class='item acea-row row-between'>
						<view>商品总价：</view>
						<!-- 积分价格 -->
						<PointsPrice v-if="secondType === ProductTypeEnum.Integral" :pointsPrice="pointsPrice"
							:pointsGoodsStyle="hotPointsStyle"></PointsPrice>
						<!-- 其他价格 -->
						<view v-else class='conter'>￥{{orderInfo.proTotalPrice || 0}}</view>
					</view>
					<view class='item acea-row row-between' v-if="orderInfo.payPostage > 0">
						<view>运费：</view>
						<view class='conter'>￥{{orderInfo.payPostage || 0}}</view>
					</view>
					<view class='item acea-row row-between' v-if='orderInfo.svipDiscountPrice > 0'>
						<view>SVIP抵扣：</view>
						<view class='conter'>-￥{{orderInfo.svipDiscountPrice || 0}}</view>
					</view>
					<view class='item acea-row row-between' v-if='orderInfo.merchantMemberDiscountPrice > 0'>
						<view>商户会员抵扣：</view>
						<view class='conter'>-￥{{orderInfo.merchantMemberDiscountPrice || 0}}</view>
					</view>
					<view class='item acea-row row-between' v-if='orderInfo.merCouponPrice > 0'>
						<view>店铺优惠：</view>
						<view class='conter'>-￥{{orderInfo.merCouponPrice || 0}}</view>
					</view>
					<view class='item acea-row row-between' v-if='orderInfo.platCouponPrice > 0'>
						<view>平台优惠：</view>
						<view class='conter'>-￥{{orderInfo.platCouponPrice || 0}}</view>
					</view>
					<view class='item acea-row row-between' v-if="orderInfo.integralPrice > 0">
						<view>积分抵扣：</view>
						<view class='conter'>-￥{{orderInfo.integralPrice || 0}}</view>
					</view>
					<view v-show="secondType !== ProductTypeEnum.Integral" class='actualPay acea-row row-right'>
						<text>实付款：</text>
						<text class='money semiBold'>
							<text class="fs-24">￥</text>
							<text class="fs-40">{{orderInfo.payPrice || 0}}</text>
						</text>
					</view>
				</view>
				<view style='height:200rpx;'></view>

				<!-- 底部按钮 --> <!-- v-show="isShowBtn" 订单状态（0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消） -->
				<view class='footer acea-row row-right row-middle'>
					<view class="qs-btn" v-show="orderInfo.status==0" @click.stop="cancelOrder">取消订单</view>
					<view class='bnt cancel'
						v-show="Number(orderInfo.status) >1 &&Number(orderInfo.status) < 9 && orderInfo.status!=3&&shippingType===1"
						@tap='godeliverRecord'>发货记录
					</view>
					<view class='bnt cancel' v-show="orderInfo.status==6 || orderInfo.status==9" @tap='delOrder'>删除订单
					</view>
					<view class='bnt bg-color' v-show="orderInfo.combinationId > 0" @tap='goJoinPink'>查看拼团</view>
					<view class='bnt' :class="orderInfo.status==4?'bg-color':'cancel'" v-show="(orderInfo.status>0) && isBuyAgain"
						@tap='goOrderConfirm'>
						再次购买</view>
					<view class='bnt bg-color' v-show="orderInfo.status==4 && orderInfo.refundStatus!==3" @tap='confirmOrder'>确认收货
					</view>
					<view class='bnt bg-color' v-show="orderInfo.status==0" @tap='goPay(orderInfo)'>立即付款</view>
					<view class='bnt bg-color'
						v-show="orderInfo.status==3 && orderInfo.refundStatus !== 3 && secondType === ProductTypeEnum.Reservation && orderInfo.isCancelServiceOrder"
						@tap='handleCancelReservation'>取消订单</view>
				</view>
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
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	import {
		getOrderDetail,
		orderTake,
		orderCancel,
		strToBase64Api,
		orderStatusImgApi
	} from '@/api/order.js';
	import {
		cartBatchAddApi
	} from '@/api/product.js';
	import orderGoods from "@/components/orderGoods";
	import ClipboardJS from "@/plugin/clipboard/clipboard.js";
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	// #ifdef MP
	import uQRCode from '@/js_sdk/Sansnn-uQRCode/uqrcode.js'
	// #endif
	import {
		setThemeColor
	} from '@/utils/setTheme.js'
	import {
		Debounce
	} from '@/utils/validate.js'
	import {
		ProductTypeEnum,
		ProductMarketingTypeEnum
	} from "../../../enums/productEnums";
	import systemFromInfo from '@/components/systemFromInfo';
	import PointsPrice from '@/components/PointsPrice.vue';
	import ReservationInfo from './components/reservationInfo.vue';
	import VerificationQRCode from './components/verificationQRCode.vue'
	import topOrderInfo from './components/topOrderInfo.vue'
	import progressBar from './components/progressBar.vue'
	import {
		onDelOrder
	} from "../../../libs/order";
	import DeliveryMethod from "../../../components/DeliveryMethod";
	import {
		cancelReservationApi
	} from "../../../api/order";
	import util from "../../../utils/util";
	/**
	 * 积分商品推荐样式
	 */
	const hotPointsStyle = {
		iconStyle: {
			width: '28rpx',
			height: '28rpx'
		},
		priceStyle: {
			fontSize: '28rpx',
		},
		unitStyle: {
			fontSize: '26rpx',
		},
	}
	const app = getApp();
	export default {
		components: {
			orderGoods,
			easyLoadimage,
			systemFromInfo,
			PointsPrice,
			ReservationInfo,
			DeliveryMethod,
			VerificationQRCode,
			topOrderInfo,
			progressBar
		},
		data() {
			return {
				codeImg: '',
				qrcodeSize: 100,
				orderNo: '',
				cartInfo: [], //购物车产品
				orderInfo: {
					systemStore: {},
					pstatus: {}
				}, //订单详情
				status: {}, //订单底部按钮状态
				totalPrice: '0',
				id: 0, //订单id
				uniId: '',
				utils: this.$util,
				againStatus: 0,
				type: 0, //订单类型 0-普通订单，1-视频号订单,2-秒杀订单
				secondType: 0, //订单二级类型:0-普通订单，1-积分订单，2-虚拟订单，4-视频号订单，5-云盘订单，6-卡密订单
				isShow: true,
				theme: app.globalData.theme,
				bgColor: '#e93323',
				userAddress: [],
				shippingType: 1,
				statusPic: '',
				isUserAddress: false, //是否只显示一次收货地址
				ProductMarketingTypeEnum: ProductMarketingTypeEnum,
				ProductTypeEnum: ProductTypeEnum,
				orderExtend: [], //系统表单数据
				isSplitDelivery: false, //是否拆单发货
				deliveryType: '', // 配送方式
				hotPointsStyle: hotPointsStyle,
				urlDomain: this.$Cache.get("imgHost"),
				statusPng: ['beforPay', 'beforSend', 'beforSend', 'beforPay', 'beforResaved', 'orderDone', 'orderDone',
					'', '', 'orderCancel'
				],
				isShowBtn: false, //是否展示底部按钮
				merchantOrderList: {}, //商户信息
				punchCardOrderInfo: {}, // 次卡商品信息
				spaceStr: '&ensp;', // 空格字符
			};
		},
		computed: {
			//是否可以再次购买
			isBuyAgain() {
				return this.type === ProductMarketingTypeEnum.Normal && this.secondType === this.ProductTypeEnum.Normal && !this
					.orderExtend.length
			},
			...mapGetters(['isLogin', 'chatUrl', 'userInfo']),
			//积分价格
			pointsPrice() {
				return {
					redeemIntegral: this.orderInfo.redeemIntegral,
					price: this.orderInfo.proTotalPrice
				}
			},
		},
		onLoad: function(options) {
			if (!options.orderNo) return this.$util.Tips({
				title: '缺少参数'
			}, {
				tab: 3,
				url: 1
			});
			this.$set(this, 'orderNo', options.orderNo);
			this.bgColor = setThemeColor();
			uni.setNavigationBarColor({
				frontColor: '#ffffff',
				backgroundColor: this.bgColor,
			});
		},
		onShow() {
			if (this.isLogin) {
				this.getOrderInfo();
			} else {
				toLogin();
			}
		},
		onHide: function() {
			this.isClose = true;
		},
		// 滚动监听
		onPageScroll(e) {
			// 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
			uni.$emit('scroll');
		},
		onReady: function() {
			// #ifdef H5
			this.$nextTick(function() {
				const clipboard = new ClipboardJS(".copy-data");
				clipboard.on("success", () => {
					this.$util.Tips({
						title: '复制成功'
					});
				});
			});
			// #endif

		},
		methods: {
			//发货记录
			godeliverRecord() {
				uni.showLoading({
					title: '加载中...'
				});
				let order_logistics = `/pages/goods/order_logistics/index?orderNo=${this.orderInfo.orderNo}`
				let send_record = `/pages/goods/send_record/index?orderNo=${this.orderNo}&index=0`
				if (this.isSplitDelivery) {
					uni.navigateTo({
						url: order_logistics
					});
				} else {
					let url = this.deliveryType === 'express' ? order_logistics : send_record
					uni.navigateTo({
						url: url
					})
				}
			},
			// 取消预约
			async handleCancelReservation() {
				const result = await uni.showModal({
					content: "取消订单将自动退款，确定取消吗？",
				});
				if (result[0] || result[1].cancel) return;
				try {
					await cancelReservationApi(this.orderNo)
					await this.$util.Tips({
						title: '取消预约成功'
					});
					await this.getOrderInfo();
				} catch (e) {
					this.$util.Tips({
						title: e
					});
				}
			},
			/**
			 * 打开支付组件
			 *
			 */
			goPay: Debounce(function(item) {
				uni.showLoading({
					title: '加载中...'
				});

				uni.navigateTo({
					url: `/pages/goods/order_payment/index?orderNo=${item.orderNo}&payPrice=${item.payPrice}`
				});
			}),
			/**
			 * 拨打电话
			 */
			makePhone: function(e) {
				uni.makePhoneCall({
					phoneNumber: e
				})
			},
			/**
			 * 打开地图
			 *
			 */
			showMaoLocation: function(e) {
				if (!e.merLatitude || !e.merLongitude) return this.$util.Tips({
					title: '缺少经纬度信息无法查看地图！'
				});
				//#ifdef H5
				if (this.$wechat.isWeixin() === true) {
					this.$wechat.seeLocation({
						latitude: parseFloat(e.merLatitude),
						longitude: parseFloat(e.merLongitude),
						name: e.merName,
						address: e.merAddressDetail,
					}).then(res => {
						console.log('success');
					})
				}
				//#endif
				uni.openLocation({
					latitude: parseFloat(e.merLatitude),
					longitude: parseFloat(e.merLongitude),
					scale: 8,
					name: e.merName,
					address: e.merAddressDetail,
					success: function(res) {
						console.log('res地址', res)
					},
					fail: function(error) {
						console.error('openLocation failed:', error); // 输出错误信息
					}
				});
			},
			orderStatusImg() {
				let that = this;
				orderStatusImgApi().then(res => {
					res.data.map(item => {
						if (item.title === 'await_pay' && this.orderInfo.status === 0) {
							this.statusPic = item.pic
						} else if (item.title === 'await_shipped' && this.orderInfo.status === 1) {
							this.statusPic = item.pic
						} else if (item.title === 'receiving' && this.orderInfo.status === 3) {
							this.statusPic = item.pic
						} else if (item.title === 'spike' && this.orderInfo.status === 2) {
							this.statusPic = item.pic
						} else if (item.title === 'complete' && this.orderInfo.status === 4) {
							this.statusPic = item.pic
						}
					})
				}).catch(err => {
					that.$util.Tips({
						title: err
					});
				});
			},
			/**
			 * 获取订单详细信息
			 *
			 */
			getOrderInfo: function() {
				let that = this;
				uni.showLoading({
					title: "正在加载中"
				});
				getOrderDetail(that.orderNo).then(res => {
					uni.hideLoading();
					let data = res.data;
					that.$set(that, 'orderInfo', data);
					that.merchantOrderList = data.merchantOrderList[0] // 商户信息
					that.punchCardOrderInfo = data.merchantOrderList[0].orderInfoList[0] // 第一个商户的商品信息
					that.$set(that, 'id', data.id);
					that.$set(that, 'type', data.type);
					that.$set(that, 'secondType', data.secondType);
					if (this.secondType === ProductTypeEnum.Reservation) {
						that.$set(that, 'orderExtend', data.orderExtend ? JSON.parse(data.orderExtend) : []);
					} else {
						that.$set(that, 'orderExtend', data.orderExtend ? [JSON.parse(data.orderExtend)] : []);
					}
					that.orderStatusImg();
					that.userAddress = data.merchantOrderList.filter(item => {
						return item.shippingType === 1
					})
					that.isUserAddress = that.userAddress.length > 0 ? true : false;
					if (data.status !== 0 || data.status !== 9) {
						if (data.merchantOrderList[0].shippingType == 2 || data.merchantOrderList[0]
							.shippingType == 4) that.markCode(data.merchantOrderList[
							0].verifyCode);
					}
					that.$set(that, 'shippingType', data.merchantOrderList[0].shippingType);
					that.$set(that, 'cartInfo', data.merchantOrderList[0].orderInfoList);
					that.$set(that, 'isSplitDelivery', data.merchantOrderList[0].isSplitDelivery);
					that.$set(that, 'deliveryType', data.merchantOrderList[0].deliveryType);
					that.getIsShowBtn();
					if (that.orderInfo.refundStatus > 0) {
						uni.setNavigationBarColor({
							frontColor: '#fff',
							backgroundColor: '#666666'
						})
					}

				}).catch(err => {
					that.$util.Tips({
						title: err
					}, {
						tab: 4,
						url: '/pages/user/index'
					});
				});
			},
			//是否展示底部操作按钮
			//<!-- 订单状态（0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消） -->
			//	<!-- 已退款的订单详情中，不支持任何操作。 orderInfo.refundStatus !== 3,拼团已退款订单还可进行【查看拼团】操作-->
			getIsShowBtn() {
				this.isShowBtn = (this.orderInfo.status != 3 && this.orderInfo.status != 1) || this.orderInfo
					.refundStatus !== 3 || (this.orderInfo.refundStatus == 3 && this.type !== this.ProductMarketingTypeEnum
						.Groupbuying) || (this.orderInfo.merchantOrderList && this.orderInfo.merchantOrderList[0]
						.shippingType === 2 && this.orderInfo.status === 1)
			},
			/**
			 *
			 * 生成二维码
			 */
			markCode(text) {
				strToBase64Api({
					height: '145',
					text: text,
					width: '145'
				}).then(res => {
					this.codeImg = res.data.code
				});
			},
			/**
			 *
			 * 剪切订单号
			 */
			// #ifndef H5
			handleCopy: function(num) {
				uni.setClipboardData({
					data: num
				});
			},
			// #endif
			/**
			 * 打电话
			 */
			goTel: function() {
				uni.makePhoneCall({
					phoneNumber: this.orderInfo.deliveryId
				})
			},
			/**
			 * 去拼团详情
			 *
			 */
			goJoinPink: function() {
				uni.navigateTo({
					url: '/pages/activity/goods_combination_status/index?id=' + this.orderInfo.pinkId,
				});
			},
			/**
			 * 再此购买
			 *
			 */
			goOrderConfirm: Debounce(function() {
				uni.showLoading({
					title: '加载中...'
				});
				let cartListRequest = []
				let that = this;
				this.cartInfo.map(item => {
					cartListRequest.push({
						productId: parseFloat(item.productId),
						productAttrUnique: item.attrValueId,
						cartNum: parseFloat(item.payNum)
					})
				})
				cartBatchAddApi(cartListRequest).then(function(res) {
					uni.switchTab({
						url: '/pages/order_addcart/order_addcart'
					})
				}).catch(res => {
					return this.$util.Tips({
						title: res
					});
				});
			}),
			//确认收货
			confirmOrder: Debounce(function() {
				let that = this;
				let applyRefundNum = 0;
				that.orderInfo.merchantOrderList.map((item) => {
					item.orderInfoList.map((i) => {
						applyRefundNum = Number(i.applyRefundNum);
					});
				});
				// 如果有售后中的，提示
				if (applyRefundNum > 0) return uni.showModal({
					confirmColor: '#f55850',
					title: '提示',
					content: '请先撤销售后，再进行收货操作',
					confirmText: '我知道了',
					success(res) {
						if (res.confirm) {
							return
						}
					}
				});
				uni.showModal({
					title: '确认收货',
					content: '为保障权益，请收到货确认无误后，再确认收货',
					confirmColor: '#f55850',
					success: function(res) {
						if (res.confirm) {
							orderTake(that.orderNo).then(res => {
								return that.$util.Tips({
									title: '操作成功',
									icon: 'success'
								}, function() {
									that.getOrderInfo();
								});
							}).catch(err => {
								return that.$util.Tips({
									title: err
								});
							})
						}
					}
				})
			}),
			/**
			 *
			 * 删除订单
			 */
			delOrder: Debounce(function() {
				onDelOrder(this.orderNo);
			}),
			cancelOrder: Debounce(function() {
				let self = this
				uni.showModal({
					title: '提示',
					content: '确认取消该订单?',
					cancelColor: '#f55850',
					success: function(res) {
						if (res.confirm) {
							orderCancel(self.orderNo)
								.then((data) => {
									self.$util.Tips({
										title: '取消成功'
									}, {
										tab: 4,
										url: '/pages/user/index'
									})
								}).catch((err) => {
									self.$util.Tips({
										title: err
									})
									self.getDetail();
								});
						} else if (res.cancel) {
							console.log('用户点击取消');
						}
					}
				});
			})
		}
	}
</script>

<style scoped lang="scss">
	@import '../static/css/order_details.scss';

	.orderStatusPng {
		width: 128rpx;
		height: 128rpx;
	}

	.picTxt {
		justify-content: space-between;
	}

	.writeOff {
		padding: 0 24rpx 42rpx 24rpx !important;
	}

	.verification-record-box {}

	.verification-code-box {
		justify-content: center;
		align-items: center;
		padding-top: 48rpx;

		.verification-code-img {
			width: 320rpx;
			height: 320rpx;
		}
	}

	.copy-btn {
		margin-left: 14rpx;
		// border-radius: 26rpx;
		// border: 1rpx solid #ddd;
		// padding: 4rpx 20rpx;
		// margin-left: 12rpx;
		// line-height: 28rpx;
	}

	.verification-time {
		color: #606266;
	}

	.flex-baseline {
		display: flex;
		align-items: baseline;
	}

	.color-666 {
		color: #666;
	}

	.pad24 {
		padding: 24rpx !important;
	}

	.order-copy-btn {
		height: 36rpx;
		line-height: 36rpx;
		font-size: 20rpx;
		border: 0.5px solid #ddd;
		border-radius: 20px !important;
		margin-left: 12rpx;
		padding: 0 20rpx;
	}

	.fs-40 {
		font-size: 40rpx !important;
	}

	.fs-36 {
		font-size: 36rpx !important;
	}

	// .record {
	// 	position: relative;
	// 	display: flex;
	// 	align-items: center;
	// 	// top: 40rpx;
	// 	// right: 0;
	// 	// color: #FFDDB8;
	// 	// font-size: 22rpx;
	// 	// background-color: rgba(0, 0, 0, 0.3);
	// 	padding: 8rpx 0;
	// 	border-radius: 20px 0px 0px 20px;
	// }
</style>