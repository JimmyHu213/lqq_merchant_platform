<template>
	<view :data-theme="theme">
		<!-- #ifdef MP || APP -->
		<view class='cart_nav'>
			<nav-bar navTitle='立即预约' iconColor='#fff' :isBackgroundColor="true" ref="navBarRef"></nav-bar>
		</view>
		<!-- #endif -->
		<!-- 选择配送方式 deliveryMethod	配送方式：1-商家配送，2-到店核销,3-到店服务，4-上门服务-->
		<!--    shippingType	快递类型: 1-快递配送，2-到店自提，3-虚拟发货,4-到店服务,5-上门服务-->
		<view class='order-submission'>
			<view class="allAddress">
				<view v-show="deliveryMethod.split(',').length == 2" class="h-96 relative">
					<view class="w-full abs-lb rd-t-24rpx flex bg--w111-fff">
						<view class="flex-center w-50p h-76 fs-28 rd-lt-24rpx z-10"
							:class="shippingType === 5 ? 'bg--w111-fff font-color' : 'bg-primary-light'" @tap="addressType(5)">上门服务
						</view>
						<view class="flex-center w-50p h-76 fs-28 rd-rt-24rpx z-10"
							:class="shippingType === 4 ? 'bg--w111-fff font-color' : 'bg-primary-light'" @tap="addressType(4)">到店服务
						</view>
					</view>
					<view class="w-50p rd-t-24rpx bg--w111-fff h-96" :class="shippingType === 5 ? 'abs-lt' : 'abs-rt'">
						<view class="w-full h-full relative active-card"></view>
					</view>
				</view>
				<view v-if='deliveryMethod.includes("4") && shippingType===5' class='address group acea-row row-between-wrapper'
					:class="deliveryMethod.split(',').length == 2?'bd-r-14':''" @tap='onAddress'>
					<view class='f-s-26 text-666 w-600' v-if="addressInfo">
						<view class='name acea-row mb-16'>
							<view class="line2 select-name f-s-30 f-w-500">{{addressInfo.realName}}</view>
							<view class='phone f-s-30 ml-24 f-w-500'>{{addressInfo.phone}}</view>
						</view>
						<view class="acea-row">
							<view class="line2"><text class='default  font-color'
									v-if="addressInfo.isDefault">[默认]</text>{{addressInfo.province}}{{addressInfo.city}}{{addressInfo.district}}{{ addressInfo.street}}{{addressInfo.detail}}
							</view>
						</view>
					</view>
					<view class='addressCon' v-else>
						<view class='setaddress'>设置上门地址</view>
					</view>
					<view class='iconfont icon-ic_rightarrow mt30'></view>
				</view>
				<view v-if='deliveryMethod.includes("3")  && shippingType===4'
					class='address group acea-row row-between-wrapper'
					:class="deliveryMethod.split(',').length == 2?'bd-r-14':''  ">
					<view class='addressCon'>
						<view class='name acea-row mb-16'>
							<view class="line1 select-name">{{productInfo.merchantName}}</view>
						</view>
						<view class="acea-row">
							<view>{{productInfo.merchantAddressDetail}}</view>
						</view>
					</view>
					<view class="address-location acea-row " @click="showMaoLocation"
						:style="{ backgroundImage: `url(${urlDomain}crmebimage/presets/type_loaction.png)`}">
						<view class="location-distance acea-row row-center row-middle">距您{{distance}}km</view>
						<image class="loaction-avatar" :src="productInfo.coverImage"></image>
						<view class="location-point"></view>
					</view>
				</view>
				<view v-show="deliveryMethod.split(',').length == 2" class='line'>
					<image src='../../static/images/line.png'></image>
				</view>
			</view>
		</view>

		<view class="borderPad reservation-main">
			<!--商品选择规格-->
			<view class="service-content bg-f bg--w111-fff borRadius14">
				<view class="service-informatin acea-row row-between">
					<image class="service-image" :src="productInfo.image"></image>
					<view class="service-name acea-row">
						<view class="name line2">
							{{productInfo.name}}
						</view>
						<view class="price acea-row row-bottom">
							<svip-price :productPrice="attr.productSelect" :svipPriceStyle="svipPriceStyle"
								:svipIconStyle="svipIconStyle" :priceColor="priceColor"></svip-price>
						</view>
					</view>
				</view>
				<view class="service-specs" v-for="(item, indexw) in attr.productAttr" :key="indexw">
					<view>{{ item.attributeName }}</view>
					<scroll-view show-scrollbar="false" class="spec-scroll">
						<view class="acea-row">
							<view v-for="(itemn, indexn) in item.optionList" :key="indexn"
								class="acea-row spec-item row-middle row-center"
								:class="item.index === itemn.optionName ? 'bntActive' : ''" @click="tapAttr(indexw, indexn)">
								<view v-show="itemn.image" class="spec-image">
									<image :src="itemn.image" class="image"></image>
								</view>
								<view class="spec-attr">{{itemn.optionName}}</view>
							</view>
						</view>
					</scroll-view>
				</view>
				<!--购买数量-->
				<view class="service-count acea-row row-between-wrapper">
					<view class="fs-28">购买数量</view>
					<view>
						<view class="acea-row row-middle">
							<view class="acea-row row-middle">
								<text class="iconfont icon-ic_Reduce"
									:class="(cartNum<=1 || (min_count>0 && cartNum<=min_count))?'on':''" @click.stop="addCart(0)"></text>
								<input type="number" pattern="[0-9]*" :always-embed="true" min="1" :adjust-position="true"
									cursor-spacing="50" maxlength="3" class="input-count" v-model="cartNum" />
								<text class="iconfont icon-ic_increase" :class="max_count>0&&cartNum>=max_count?'on':''"
									@click.stop="addCart(1)"></text>
							</view>
						</view>
					</view>
				</view>
			</view>
			<!--日历-->
			<view>
				<calendar v-if="currentDate" @dateChange="getCalendar" @dayChange="dayChange" :initDay="currentDate"
					:dotList='userDate' :reservationDefaultDate='reservationDefaultDate'></calendar>
			</view>
			<!--时间段-->
			<view v-if="reservationTimeData.length>0" class="time-solt bg--w111-fff borRadius14">
				<view class="list acea-row" :class="timeArrow?'on':''">
					<view v-for="(item,index) in reservationTimeData" :key="index" class="time" @click="timeDataTap(item)">
						<view class="time-item acea-row row-middle row-center"
							:class="[item.stock>0 && item.isClick ?'':'bntHui',currentTimeId==item.attrValueReservationId?'bntActive':'']">
							<view>{{item.timeSlot}}</view>
							<view v-show="productInfo.remainingNumShouSwitch" class="item-stock">
								<view v-if="item.stock<=0">约满</view>
								<view v-else>余{{item.stock}}</view>
							</view>
						</view>
					</view>
				</view>
				<view v-if="reservationTimeData.length>9" class="item-toggle" @click="timetap">
					{{timeArrow?'收起':'展开全部'}}<text class="iconfont fs-22 ml-4"
						:class="timeArrow?'icon-ic_uparrow':'icon-ic_downarrow'"></text>
				</view>
			</view>
			<!--到店-->
			<view v-if="shippingType=== 4">
				<view class="wrapper virtual_form bg--w111-fff borRadius14 borderPad mt20">
					<view class='item acea-row row-between-wrapper mb-32'>
						<view class="name">联系人</view>
						<view class="discount">
							<input type="text" placeholder="请填写姓名" placeholder-class="placeholderc" maxlength="12"
								v-model="customer.reservationName" />
						</view>
					</view>
					<view class='item acea-row row-between-wrapper'>
						<view class="name">联系电话</view>
						<view class="discount">
							<input type="number" placeholder="请填写电话" placeholder-class="placeholderc"
								v-model="customer.reservationPhone" />
						</view>
					</view>
				</view>
			</view>
			<!--预约须知-->
			<view class="bg--w111-fff borRadius14 service-notice">
				<view class="notice-title">预约须知</view>
				<view class="notice-info">
					<view class="text">预定规则：预定成功后，系统不支持修改订单，如您有时间调整等需求，请联系商家协商</view>
					<view class="text">放号规则：用户只能预约{{productInfo.reservationDateDayRange}}天之内的日期</view>
					<view class="text" v-if="productInfo.cancelOrderSwitch">取消预约：请于预约开始时间前
						{{productInfo.cancelOrderHour}} 小时进行操作。若距离预约开始已不足 {{productInfo.cancelOrderHour}} 小时，请您联系商家协商处理。
					</view>
					<view class="text" v-else>取消预约：预定成功后，不支持取消预约，若您有相关需求请联系商家处理</view>
				</view>
			</view>
		</view>
		<view class="heights"></view>
		<view class="reservation-bottom">
			<view class='footer acea-row row-between-wrapper'>
				<view class='settlement' style='z-index:100' @click="nextStep">下一步</view>
			</view>
		</view>
		<!--		<timeranges :isShow='isShow' :time='timeranges' @confrim="confrim" @cancel="cancels"></timeranges>	-->
	</view>
</template>

<script>
	import {
		getReservationDateApi,
		getReservationProDetailApi,
		getReservationTimeSlotApi
	} from "../../../../api/order";
	import {
		mapGetters
	} from "vuex";
	import calendar from '../components/calendar/index.vue';
	import {
		Debounce
	} from '@/utils/validate.js';
	import {
		getReservationMerData,
		getReservationMerDistance
	} from '@/api/activity.js';
	import {
		getAddressDefaultApi,
		getAddressLocationApi
	} from "../../../../api/user";
	import navBar from '@/components/navBar';
	import svipPrice from "@/components/svipPrice.vue";
	import productAttr from '@/mixins/productAttr.js';
	import orderPay from "@/mixins/OrderPay.js";
	import {
		getDistanceFromLatLonInMeter,
		onGetPreOrder
	} from "../../../../libs/order";
	import geoFenceExample from "../../utils/geoUtilExample";
	let app = getApp();

	export default {
		name: 'reservation',
		components: {
			navBar,
			svipPrice,
			calendar,
		},
		mixins: [productAttr, orderPay],
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				theme: app.globalData.theme,
				shippingType: 0,
				deliveryMethod: '', //配送方式
				attr: {
					cartAttr: false,
					productAttr: [],
					productSelect: {}
				},
				productPrice: {
					price: "",
					vipPrice: "",
					isPaidMember: false, //是否是付费会员商品
					redeemIntegral: 0,
				},
				//普通价格
				svipPriceStyle: {
					svipBox: {
						height: "34rpx",
						borderRadius: "60rpx 56rpx 56rpx 20rpx",
					},
					icon: {
						fontSize: "23rpx",
						height: "34rpx",
						borderRadius: "16rpx 0 16rpx 2rpx",
					},
					price: {
						fontSize: "44rpx",
					},
					svipPrice: {
						fontSize: "27rpx",
					},
					topStyle: {
						top: "6rpx",
					},
				},
				//svip价格
				svipIconStyle: {
					svipBox: {
						height: '34rpx',
						borderRadius: '36rpx 40rpx 40rpx 0.4rpx',
					},
					price: {
						fontSize: '44rpx'
					},
					svipPrice: {
						fontSize: '22rpx'
					}
				},


				// 日历组件数据
				targetDate: 0, //本月
				timeArrow: false,
				productId: 0, //商品id；
				skuId: "", //规格id
				unique: '', //默认选中的属性唯一值；

				productValue: {},
				cartNum: 1, //预约数量
				reservationTimeData: [], //时间段
				productInfo: {},
				reservation_type: 0,
				currentService: 0,
				userDate: [], // 可约日期
				targetDay: 0, //选中天数
				currentTimeId: 0, //选中时间段id
				discountInfo: {
					discount: []
				},
				totalPrice: 0,
				totalSvipPrice: 0,
				orderId: '',
				cartInfoId: 0,
				reservationDefaultDate: '',
				address: {
					address: false
				}, //地址组件
				addressInfo: {

				}, //地址信息
				addressId: 0, //地址id
				pagesUrl: "",
				serviceTypeList: [{
						name: "上门服务",
						type: 2
					},
					{
						name: "到店服务",
						type: 1
					},
				],
				customer: {},
				max_count: 0,
				pay_limit: 0,
				min_count: 0,
				merData: {},
				currentDate: "",
				distance: "", //距离
				reservationTime: '', //选择的时间段
				location: {}, // 经纬度
				currentLocation: {}, // 上门地址经纬度
				isServiceAreaRange: false //上门地址是否在电子围栏内
			}
		},
		computed: {
			...mapGetters(['isLogin']),
			//价格颜色
			priceColor() {
				return {
					'color': this.isMarketingGoods ? '#e93323' : '',
				}
			},
			confirmShow() {
				let obj = this.orderId && this.confirm.length && this.confirm[0].length;
				return obj;
			}
		},
		onLoad(options) {
			this.getLocation();
			let m = (parseInt(new Date().getMonth() + 1) < 10 ? '0' : '') + parseInt(new Date().getMonth() + 1);
			let d = (parseInt(new Date().getDate()) < 10 ? '0' : '') + parseInt(new Date().getDate());
			this.targetDate = parseInt(new Date().getFullYear()) + '-' + m;
			this.targetDay = d;
			this.currentDate = this.targetDate + '-' + d
			this.productId = Number(options.id) || 0;
			this.addressId = parseInt(options.addressId) || 0;

			if (this.addressId) {
				this.getAddressInfo(); // 获取选择的地址
				this.getAddressLocation()
			} else {
				this.getAddressDefault(); // 获取默认地址
			}
		},
		mounted() {
			// this.reservationOrderInfo();
			this.getReservationProDetail();
		},
		methods: {
			// 获取用户定位
			async getLocation() {
				let {
					latitude,
					longitude
				} = await this.$util.$L.getLocation();
				this.location = {
					latitude,
					longitude
				};
			},
			//获取默认地址
			async getAddressDefault() {
				let res = await getAddressDefaultApi();
				if (res.code === 200) {
					this.addressInfo = res.data;
					this.addressId = res.data ? res.data.id : 0
					await this.getAddressLocation()
				}
			},
			// 获取用户地址经纬度
			async getAddressLocation() {
				let {
					data
				} = await getAddressLocationApi(this.addressId);
				this.currentLocation = data
				// 判断是否在电子围栏地址内
				this.isServiceAreaRange = geoFenceExample.checkMultipleFences({
					lng: this.currentLocation.longitude,
					lat: this.currentLocation.latitude
				}, this.serviceAreaRange)
			},
			// 获取商品规格 预约设置详情
			async getReservationProDetail() {
				let res = await getReservationProDetailApi(this.productId)
				if (res.code === 200) {
					let data = res.data
					this.productInfo = data
					this.serviceAreaRange = data.serviceAreaRange ? JSON.parse(data.serviceAreaRange) : []

					// deliveryMethod	配送方式：1-商家配送，2-到店核销,3-到店服务，4-上门服务, 快递类型: 1-快递配送，2-到店自提，3-虚拟发货,4-到店服务,5-上门服务
					this.deliveryMethod = data.deliveryMethod
					// 设置配送方式下单传的值
					if (this.deliveryMethod === '3') {
						this.shippingType = 4
					} else {
						this.shippingType = 5
					}
					this.$set(this, 'productValue', data.skuMap);
					let productAttr = data.attributeList.map((item) => {
						return {
							...item,
							attrValues: item.optionList.map(items => items.optionName),
						};
					});
					this.$set(this.attr, "productAttr", productAttr);
					this.defaultSelect(); // 默认规格
					this.skuId = this.attr.productSelect.unique
					// 如果库存没了 不调接口，不获取默认的时间段
					if (this.skuId != this.productId) {
						this.reservationDate(); //可约日期
						this.reservationTimes();
					}
					if (this.location) {
						this.distance = getDistanceFromLatLonInMeter(Number(data.merchantLatitude), Number(data.merchantLongitude))
					}
				}
			},
			// 当前展示的金额
			getProductPrice() {
				this.productPrice = {
					price: this.attr.productSelect.price,
					vipPrice: this.attr.productSelect.vipPrice,
					isPaidMember: this.productInfo.isPaidMember, //是否是付费会员商品
					redeemIntegral: 0, //积分
				};
			},
			//选择服务方式
			addressType(type) {
				if (type === 4) {
					setTimeout(() => {
						this.shippingType = type
					}, 300)
				} else {
					this.shippingType = type
				}
			},
			// 更多地址选择
			onAddress: function() {
				uni.navigateTo({
					url: '/pages/address/user_address_list/index?id=' + this.productId
				});
			},

			/**
			 * 下一步：验证表单数据并提交预订单
			 */
			/**
			 * 验证预约表单数据
			 * @returns {boolean} 是否验证通过
			 */
			validateReservationData() {
				// 1. 验证地址选择
				if (this.shippingType === 5 && !this.addressId) {
					this.$util.Tips({
						title: '请选择地址'
					});
					return false;
				}
				// 2. 验证服务圈层
				if (this.productInfo.serviceAreaSwitch && !this.isServiceAreaRange && this.shippingType === 5) {
					const result = uni.showModal({
						content: "您选择的上门地址不在服务区域内",
					});
					if (result[0] || result[1].cancel) return false;
				}
				// 3. 验证联系方式（到店服务时）
				if (this.shippingType === 4) {
					if (!this.customer.reservationName) {
						this.$util.Tips({
							title: '请输入联系人'
						});
						return false;
					}
					if (!this.customer.reservationPhone) {
						this.$util.Tips({
							title: '请输入联系电话'
						});
						return false;
					}
					if (!/^1(3|4|5|7|8|9|6)\d{9}$/i.test(this.customer.reservationPhone)) {
						this.$util.Tips({
							title: '请输入正确的联系电话'
						});
						return false;
					}
				}

				// 4. 验证时间段选择
				if (!this.currentTimeId) {
					this.$util.Tips({
						title: '请选择时间段'
					});
					return false;
				}

				// 5. 验证库存
				const timeData = this.reservationTimeData.find(item => item.attrValueReservationId === this.currentTimeId);
				if (this.cartNum > timeData.stock) {
					this.$util.Tips({
						title: `该时间段库存最大为${timeData.stock}`
					});
					return false;
				}

				return true;
			},

			/**
			 * 下一步：验证表单数据并提交预订单
			 */
			nextStep() {
				// 验证表单数据
				if (!this.validateReservationData()) {
					return;
				}

				// 准备预约信息
				const reservationDate = `${this.targetDate}-${this.targetDay}`;
				const reservationInfo = {
					addressId: this.addressId, // 地址id
					shippingType: this.shippingType, // 配送类型
					reservationName: this.customer.reservationName,
					reservationPhone: this.customer.reservationPhone,
					reservationDate,
					reservationTime: this.reservationTime
				};
				uni.setStorageSync('reservationInfo', reservationInfo);

				// 提交预订单
				onGetPreOrder('buyNow', [{
					attrValueId: parseFloat(this.attr.productSelect.unique),
					productId: parseFloat(this.productId),
					productNum: parseFloat(this.cartNum),
					attrValueReservationId: this.currentTimeId,
					reservationDate
				}]);
			},
			/**
			 * 获取商户信息
			 *
			 */
			getMerData(id) {
				getReservationMerData(id).then(res => {
					this.merData = res.data
					this.getMerLocation(id, {
						latitude: res.data.lat,
						longitude: res.data.long
					})
				})
			},
			getMerLocation(id, data) {
				getReservationMerDistance(id, data).then(res => {
					this.distance = res.data.distance
				})
			},
			// 选择时间段
			timeDataTap(item) {
				this.reservationTime = item.timeSlot;
				if (item.stock > 0 && item.isClick) {
					if (this.currentTimeId == item.attrValueReservationId) {
						this.currentTimeId = 0;
					} else {
						this.currentTimeId = item.attrValueReservationId;
					}
				}
			},
			// 日期选择
			dayChange(e) {
				this.currentTimeId = 0;
				const dateObj = new Date(e);
				this.targetDay = String(dateObj.getDate()).padStart(2, '0');
				this.reservationDefaultDate = e;
				this.reservationTimes();
			},
			// 预约商品可预约时段
			reservationTimes() {
				let data = {
					attValueId: this.skuId,
					date: this.targetDate + '-' + this.targetDay,
					proId: this.productId
				}
				this.reservationTimeData = []
				getReservationTimeSlotApi(data).then(res => {
					this.reservationTimeData = res.data;
				}).catch(err => {
					return this.$util.Tips({
						title: err
					});
				})
			},
			// 点击年月
			getCalendar(e) {
				this.targetDate = e;
				this.reservationDate();
				this.reservationTimeData = []
				let m = (parseInt(new Date().getMonth() + 1) < 10 ? '0' : '') + parseInt(new Date().getMonth() + 1);
				let date = parseInt(new Date().getFullYear()) + '-' + m
				this.reservationTimes()
			},
			// 获取可选择日期；
			reservationDate() {
				let data = {
					attValueId: this.skuId,
					yearMoth: this.targetDate,
					proId: this.productId
				}
				getReservationDateApi(data).then(res => {
					this.userDate = res.data;
				}).catch(err => {
					return this.$util.Tips({
						title: err
					});
				})
			},
			tapAttr: function(indexw, indexn) {
				this.$set(this.attr.productAttr[indexw], 'index', this.attr.productAttr[indexw].attrValues[indexn]);
				let value = this.getCheckedValue().join(",");
				this.checkedAttr = value
				let productSelect = this.productValue[value];
				this.skuId = productSelect.id
				if (!productSelect) {
					this.$util.Tips({
						title: '重新选择',
						success: () => {
							this.attr.productSelect.stock = 0;
							this.attr.productSelect.quota = 0;
							this.attr.productSelect.cartNum = 0;
							this.attr.productSelect.image = this.productInfo.image;
						}
					});
				} else {
					this.setCheckedAttr(productSelect);
					this.reservationTimes()
				}
				this.getProductPrice();
			},
			timetap() {
				this.timeArrow = !this.timeArrow;
			},
			addCart(type) {
				//changeValue:是否 加|减
				//获取当前变动属性
				let productSelect = this.attr.productSelect;
				//如果没有属性,赋值给商品默认库存
				if (productSelect === undefined && !this.attr.productAttr.length)
					productSelect = this.attr.productSelect;
				//无属性值即库存为0；不存在加减；
				if (productSelect === undefined) return;
				if (type) {
					this.cartNum++;
					if (this.cartNum > this.max_count && this.max_count != 0 && this.pay_limit != 0) {
						this.$set(this, "cartNum", this.max_count);
						return this.$util.Tips({
							title: "单次购买件数不能超过" + this.max_count + "件！"
						});
					}
				} else {
					this.cartNum--;
					if (this.cartNum < 1) {
						this.$set(this, "cartNum", 1);
					}
					if (this.cartNum < this.min_count && this.min_count != 0) {
						this.$set(this, "cartNum", this.min_count);
						return this.$util.Tips({
							title: "单次购买件数不能少于" + this.min_count + "件！"
						});
					}
				}
			},
			setValue: Debounce(function(e) {
				this.cartNum = e.detail.value;
			}),
			// 导航
			showMaoLocation() {
				let that = this;
				//#ifdef H5
				if (that.$wechat.isWeixin() === true) {
					that.$wechat.seeLocation({
						latitude: parseFloat(that.productInfo.merchantLatitude),
						longitude: parseFloat(that.productInfo.merchantLongitude),
						name: that.productInfo.merchantName,
						address: that.productInfo.merchantAddressDetail ? that.productInfo.merchantAddressDetail : '',
					}).then(res => {
						console.log('success');
					})
				} else {
					//#endif
					uni.openLocation({
						latitude: parseFloat(that.productInfo.merchantLatitude),
						longitude: parseFloat(that.productInfo.merchantLongitude),
						scale: 8,
						name: that.productInfo.merchantName,
						address: that.productInfo.merchantAddressDetail ? that.productInfo.merchantAddressDetail : '',
						success: function(res) {
							// that.go_map = true
						},
					});
					// #ifdef H5
				}
				//#endif
			},
		}
	};
</script>

<style lang="scss" scoped>
	.reservation-main {}

	.reservation-header {
		background: rgba(255, 255, 255, .9);
	}

	.reservation-type {
		height: 80rpx;

		.type-name {
			height: 80rpx;
			text-align: center;
			flex: 0 0 50%;
			position: relative;

			&.on {
				height: 96rpx;
				background: #fff;
				@include main_color(theme);
				font-weight: 500;
				top: -16rpx;
				border-radius: 24rpx 24rpx 0 0;
			}
		}
	}

	.reservation-address {
		padding: 32rpx 26rpx 32rpx 32rpx;
		position: relative;

		.line {
			position: absolute;
			left: 2%;
			bottom: 0;
			width: 96%;
			height: 4rpx;

			image {
				width: 100%;
				height: 100%;
				display: block;
			}
		}
	}

	.address-info {
		width: 580rpx;

		&.info-arrive {
			width: 450rpx;
		}

		.address-name {
			font-size: 30rpx;
			font-weight: 600;
		}

		.address-detail {
			margin-top: 12rpx;
		}
	}

	.address-location {
		width: 188rpx;
		height: auto;
		background-size: 100% 100%;
		flex-direction: column;
		align-items: center;

		.location-distance {
			background: #fff;
			box-shadow: 0px 0px 8px 0px rgba(0, 0, 0, 0.0784);
			min-width: 130rpx;
			max-width: 160rpx;
			height: 36rpx;
			position: relative;
			font-size: 20rpx;

			&::after {
				content: "";
				display: block;
				width: 9rpx;
				height: 9rpx;
				border: 9rpx solid transparent;
				border-top-color: #fff;
				position: absolute;
				bottom: -20rpx;
			}
		}

		.loaction-avatar {
			margin-top: 8rpx;
			width: 52rpx;
			height: 52rpx;
			border-radius: 8rpx;
			border: 6rpx solid #fff;
			box-shadow: 0px 0px 6px 0px rgba(0, 0, 0, 0.0784);
		}

		.location-point {
			width: 6rpx;
			height: 6rpx;
			@include main_bg_color(theme);
			border-radius: 100%;
			margin-top: 2rpx;
		}
	}

	.service-content {
		padding: 32rpx 24rpx;
		margin-top: 20rpx;

		.service-image {
			width: 140rpx;
			height: 140rpx;
			border-radius: 16rpx;
		}

		.service-name {
			width: 504rpx;
			flex-direction: column;
			justify-content: space-between;
		}

		.price {
			@include main_color(theme);
		}
	}

	.vip-money {
		margin-left: 13rpx;
		color: #282828;
		font-size: 26rpx;
		font-weight: 700;
	}

	.vip-image {
		width: 70rpx;
		height: 30rpx;
		margin-left: 6rpx;
	}

	.service-specs {
		margin-top: 32rpx;

		.spec-scroll {
			margin-top: 8rpx;
		}

		.spec-item {
			position: relative;
			margin-right: 28rpx;
			border-radius: 50rpx;
			background: #F5F5F5;
			border: 2rpx solid #F5F5F5;
			margin-top: 24rpx;
			padding: 4rpx 20rpx 4rpx 4rpx;

			&.bntActive {
				background: var(--view-bgColor);
			}

			.spec-image,
			.image {
				width: 48rpx;
				height: 48rpx;
				border-radius: 100%;
			}

			.spec-attr {
				font-size: 24rpx;
				margin-left: 8rpx;
				max-width: 540rpx;
			}
		}
	}

	.service-count {
		margin-top: 32rpx;

		.input-count {
			width: 72rpx;
			height: 36rpx;
			background: #F5F5F5;
			border-radius: 4rpx;
			text-align: center;
			font-size: 24rpx;
			font-weight: 600;
			margin: 0 8rpx;
		}

		.iconfont {
			font-size: 26rpx;

			&.on {
				color: #bbb;
			}
		}

		.buy_limit {
			font-size: 22rpx;
			@include main_color(theme);
			margin-right: 30rpx;
			border: none;
		}
	}

	.time-solt {
		margin-top: 20rpx;
		padding: 12rpx 24rpx 32rpx;

		.time {
			margin-right: 18rpx;

			&:nth-child(3n) {
				margin-right: 0;
			}
		}

		.time-item {
			width: 206rpx;
			flex-direction: column;
			border: 1px solid #ddd;
			margin-top: 20rpx;
			padding: 24rpx 0;
			border-radius: 16rpx;

			.item-stock {
				margin-top: 10rpx;
			}
		}

		.item-toggle {
			margin-top: 24rpx;
			text-align: center;
			color: #999999;
			font-size: 22rpx;

			.iconfont {
				font-size: 22rpx;
				margin-left: 4rpx;
			}
		}
	}

	.service-notice {
		margin-top: 20rpx;
		padding: 32rpx 24rpx;

		.notice-title {
			font-size: 28rpx;
		}

		.notice-info {
			color: #666666;
			font-size: 26rpx;
			margin-top: 16rpx;

			.text {
				margin-bottom: 6rpx;
			}
		}
	}

	.placeholderc {
		font-size: 28rpx;
		color: #ccc;
	}

	.virtual_form {
		font-size: 28rpx;
		padding: 32rpx 24rpx;
	}

	.virtual_form .discount {
		text-align: right;
	}

	/deep/.uni-input-input {
		font-size: 28rpx;
	}

	.reservation-bottom {
		width: 100%;
		position: fixed;
		bottom: 0;
		left: 0;
		z-index: 100;
		background-color: #fff;

		.area-tip {
			padding: 0 32rpx;
			height: 80rpx;
			@include main_color(theme);
			@include main_bg_color(theme);

			.iconfont {
				margin-right: 10rpx;
			}
		}
	}

	.footer {
		width: 100%;
		height: 100rpx;
		height: calc(120rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		height: calc(120rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/	
		padding: 0 30rpx;
		font-size: 28rpx;
		color: #333;
		box-sizing: border-box;
		z-index: 30;

		.footer_count {
			font-size: 28rpx;

			>view {
				align-items: baseline;
			}

			.pColor {
				margin-left: 12rpx;
			}
		}

		.coupon_price {
			color: #999999;
			font-size: 22rpx;
			margin-top: 10rpx;

			.detail {
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

	.pColor {
		color: var(--view-priceColor);
	}

	.footer .settlement {
		font-size: 30rpx;
		color: #fff;
		width: 100%;
		height: 72rpx;
		@include main_bg_color(theme);
		border-radius: 60rpx;
		text-align: center;
		line-height: 72rpx;
		font-size: 26rpx;

		&.disabled {
			background-color: #cccccc;
		}
	}

	.heights {
		height: calc(150rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		height: calc(150rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
	}

	.time:nth-of-type(3n) {
		margin-right: 0;
	}

	.list {
		/*#ifdef MP*/
		max-height: 470rpx;
		/*#endif*/
		/*#ifndef MP*/
		max-height: 486rpx;
		/*#endif*/

		transition: all 0.3s;
		overflow: hidden;

		&.on {
			max-height: unset;
			height: max-content;
		}
	}

	.header {
		height: 160rpx;
		background: linear-gradient(360deg, #F5F5F5 0%, rgba(245, 245, 245, 0) 100%);
		background-image: linear-gradient(90deg, var(--view-bntColor22) 0%, var(--view-bntColor21) 100%);
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

	.bntHui {
		color: #CCCCCC;
		background: #F9F9F9;
		border-color: #F9F9F9 !important;
	}

	.bntActive {
		@include coupons_border_color(theme);
		@include main_color(theme);
		@include coupons_light_color(theme);
	}
</style>