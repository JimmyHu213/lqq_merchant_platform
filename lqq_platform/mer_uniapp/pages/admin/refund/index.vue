<template>
	<view class="refund">
		<view class="money-section">
			<view class="acea-row row-middle item">
				<view class="">预计退款</view>
				<!-- 次卡商品 -->
				<input v-if="info[0] && info[0].secondType == ProductTypeEnum.PunchCard" disabled=""
					v-model="info[0].verifyRefundPrice" class="input" type="text" />
				<!-- 非次卡 -->
				<input v-else disabled="" v-model="refundMoney" class="input" type="text" />
			</view>
			<view class="acea-row row-middle item">
				<view class="">退款类型</view>
				<view class="acea-row row-right radio-group">
					<!-- 次卡商品不可分单退款 -->
					<view v-if="info[0] && info[0].secondType == ProductTypeEnum.PunchCard"
						class="acea-row row-middle radio-item on" :class="{ on: !isSplit}">
						整单退款
					</view>
					<!-- 非次卡商品可以分单退款 -->
					<block v-else>
						<view class="acea-row row-middle radio-item" :class="{ on: !isSplit}" @click="refundTypeChange(0)">
							<text class="iconfont" :class="isSplit?'icon-ic_unselect':'icon-a-ic_CompleteSelect'"></text>整单退款
						</view>
						<view v-if="type!=2" class="acea-row row-middle radio-item" :class="{ on: isSplit }"
							@click="refundTypeChange(1)">
							<text class="iconfont" :class="isSplit?'icon-a-ic_CompleteSelect':'icon-ic_unselect'"></text>分单退款
						</view>
					</block>
				</view>
			</view>
		</view>
		<splitOrder ref="splitOrder" :type="1" v-if="isSplit" :splitGoods="splitGoods" :secondType="secondType"
			:select_all="false" :parentAllSelected="isAll" @getList="getList">
		</splitOrder>
		<view class="list-bottom-tab-placeholder"></view>
		<view class="footer acea-row row-middle">
			<view class="all acea-row row-middle" v-if="isSplit" @click="allChange">
				<text class="iconfont" :class="isAll?'icon-a-ic_CompleteSelect':'icon-ic_unselect'"></text>
				全选
			</view>
			<view class="btn-box">
				<view class="btn" :style="{ width: isSplit?'auto':'100%'}" @click="openRefund">
					确认
					<text v-if="isSplit">({{ numTotal }})</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import splitOrder from '../components/splitOrder/index.vue';
	import {
		employeeDirectRefund,
		sendDetailList
	} from '@/api/work.js'
	import {
		ProductTypeEnum
	} from "../../../enums/productEnums";
	export default {
		components: {
			splitOrder,
		},
		data() {
			return {
				refundMoney: 0,
				isSplit: 0,
				isAll: false,
				splitGoods: [],
				goodsChecked: [],
				cart_ids: [],
				info: {},
				orderNo: '',
				type: '',
				secondType: 0,
				ProductTypeEnum,
				workOrderIdList: [] // 工单id集合
			}
		},
		computed: {
			total() {
				// 如果是预约类型且有工单ID列表，使用特殊计算逻辑
				if (this.splitGoods.length && this.splitGoods[0].workOrderList && this.workOrderIdList.length > 0) {
					// 使用第一个商品的payPrice与工单数量相乘
					const firstItem = this.splitGoods[0];
					const unitPrice = this.$util.$h.Div(firstItem.payPrice, firstItem.payNum);
					return this.$util.$h.Mul(unitPrice, this.workOrderIdList.length).toFixed(2);
				}

				// 普通商品的价格计算逻辑：使用单价乘以剩余数量

				return this.goodsChecked.reduce((total, item) => {
					const unitPrice = this.$util.$h.Div(item.payPrice, item.payNum);
					const refundAmount = this.$util.$h.Mul(unitPrice, item.surplus_num);
					const num = this.splitGoods[0].workOrderList && this.workOrderIdList.length > 0 ? item.payNum : total
					return this.$util.$h.Add(total, refundAmount);

				}, 0).toFixed(2);
			},
			numTotal() {
				// 如果是预约类型且有工单ID列表，返回工单数量
				if (this.splitGoods.length && this.splitGoods[0].workOrderList && this.workOrderIdList.length > 0) {
					return this.workOrderIdList.length;
				}

				// 普通商品返回剩余数量总和
				return this.goodsChecked.reduce((total, {
					surplus_num,
				}) => {
					return this.$util.$h.Add(total, surplus_num);
				}, 0);
			},
		},
		watch: {
			total(newValue, oldValue) {
				this.refundMoney = newValue
			},
		},
		onLoad(options) {
			this.getInfo(options.orderNo)
			this.orderNo = options.orderNo
			this.type = options.type
			this.secondType = Number(options.sdType)
		},
		methods: {
			getInfo(orderNo) {
				sendDetailList(orderNo).then(res => {
					this.info = res.data
					this.getIndex(res.data)
					// 触发价格重新计算
					this.$nextTick(() => {
						this.$forceUpdate();
					});
				})
			},
			allChange() {
				this.isAll = !this.isAll;

				if (this.splitGoods.length && this.splitGoods[0].workOrderList) {
					// 预约类型订单：显式调用子组件方法
					if (this.isAll) {
						this.$refs.splitOrder.selectAllWorkOrders();
					} else {
						this.$refs.splitOrder.unselectAllWorkOrders();
					}
				} else {
					// 普通商品订单：手动处理商品选中状态
					this.cart_ids = [];
					this.goodsChecked = [];
					for (let i = 0; i < this.splitGoods.length; i++) {
						this.splitGoods[i].checked = this.isAll;
						if (this.splitGoods[i].checked) {
							this.goodsChecked.push(this.splitGoods[i]);
							this.cart_ids.push(this.splitGoods[i].id);
						}
					}
				}

				// 触发价格重新计算
				this.$forceUpdate();
			},
			refundTypeChange(value) {
				this.isSplit = value;
				this.workOrderIdList = []
				if (value) {
					// 分单退款：清空选中状态
					this.goodsChecked = []
					this.cart_ids = []
					this.splitGoods.forEach(item => {
						item.checked = false
					})
					this.isAll = false
					// 重置退款金额
					this.refundMoney = 0
				} else {
					// 整单退款：选中所有商品
					this.splitGoods.forEach(item => {
						item.surplus_num = item.payNum - item.refundNum
						item.checked = true
						if (item.workOrderList && item.workOrderList.length) {
							item.workOrderList.forEach((workOrder) => {
								workOrder.checked = false;
							});
						}
					})
					this.goodsChecked = [...this.splitGoods]
					this.cart_ids = this.splitGoods.map(item => item.id)
					this.isAll = true
				}
			},
			getIndex(info) {
				const cartInfo = info
				let list = cartInfo.map(item => {
					return {
						id: item.id,
						checked: true, // 默认选中所有商品
						numShow: item.payNum - item.refundNum,
						surplus_num: item.payNum - item.refundNum,
						...item,
						refundPrice: this.$util.$h.Div(item.refundPrice, item.payNum),
					};
				});
				this.splitGoods = list;
				this.goodsChecked = [...list]; // 默认选中所有商品
				this.cart_ids = list.map(item => item.id); // 默认包含所有商品ID
				this.isAll = true; // 默认全选状态
				this.isSplit = 0; // 默认整单退款
			},
			getList(val) {
				if (this.splitGoods[0].workOrderList) {
					this.workOrderIdList = val
					if (!this.splitGoods[0].workOrderList) return
					const num = this.splitGoods[0].workOrderList.length
					let isAlls = val.length === num
					this.isAll = isAlls
				} else {
					// 直接使用子组件回传的列表（已带有最新的 checked 状态）
					this.splitGoods = val;
					// 重新构建选中集合
					const goodsChecked = val.filter(item => item.checked);
					const cart_ids = goodsChecked.map(item => item.id);
					this.goodsChecked = goodsChecked;
					this.cart_ids = cart_ids;
					this.isAll = this.goodsChecked.length === this.splitGoods.length;
				}
				// 触发价格重新计算
				this.$nextTick(() => {
					this.$forceUpdate();
				});
			},
			openRefund() {
				let cart_ids = [];

				// 根据订单类型构建不同的退款数据
				if (this.splitGoods.length && this.splitGoods[0].workOrderList && this.workOrderIdList.length > 0) {
					// 预约类型订单：使用第一个商品信息
					cart_ids = [{
						orderDetailId: this.splitGoods[0].id,
						num: this.workOrderIdList.length, // 使用工单数量
						workOrderIdList: this.workOrderIdList
					}];
				} else {
					// 普通商品订单：使用选中的商品列表
					cart_ids = this.goodsChecked.map(({
						id,
						surplus_num,
						workOrderIdList
					}) => {
						return {
							orderDetailId: id,
							num: surplus_num,
							workOrderIdList: this.workOrderIdList
						}
					});
				}

				let refundObj = {
					orderNo: this.orderNo,
					returnType: this.isSplit == 0 ? 1 : 2,
					detailList: cart_ids
				}
				employeeDirectRefund(refundObj).then(res => {
					if (res.code == 200) {
						this.$util.Tips({
							title: '操作成功'
						})
						setTimeout(() => {
							uni.navigateTo({
								url: '/pages/admin/order/index?type=complete'
							})
						}, 500)
					}
				}).catch(err => {
					this.$util.Tips({
						title: err
					})
				})
			},
		},
	}
</script>

<style lang="scss" scoped>
	/deep/ .has-style {
		padding: 1rpx 24rpx 32rpx 24rpx !important;
	}

	/deep/checkbox .uni-checkbox-input.uni-checkbox-input-checked {
		border: 1px solid #2A7EFB !important;
		background-color: #2A7EFB !important;
		color: #fff !important;
	}

	/deep/checkbox .wx-checkbox-input.wx-checkbox-input-checked {
		border: 1px solid #2A7EFB !important;
		background-color: #2A7EFB !important;
		color: #fff !important;
	}

	.refund {
		padding: 20rpx 24rpx;
	}

	.money-section {
		padding: 12rpx 0;
		border-radius: 24rpx;
		background: #FFFFFF;

		.item {
			height: 80rpx;
			padding: 0 24rpx;
			font-size: 28rpx;
			color: #333333;
		}

		.input {
			flex: 1;
			height: 80rpx;
			text-align: right;
			font-family: Regular;
			font-size: 36rpx;
			color: #FF7E00;
		}

		.icon-ic_edit {
			margin-left: 8rpx;
			font-size: 32rpx;
			color: #999999;
		}

		.radio-group {
			flex: 1;
		}

		.radio-item {
			font-size: 28rpx;
			color: #999999;

			+.radio-item {
				margin-left: 48rpx;
			}

			.iconfont {
				margin-right: 12rpx;
				font-size: 32rpx;
			}

			&.on {
				color: #333333;

				.iconfont {
					color: #2A7EFB;
				}
			}
		}

		.price-detail {
			padding: 20rpx 24rpx;
			border-top: 1rpx solid #f5f5f5;

			.detail-title {
				font-size: 24rpx;
				color: #666;
				margin-bottom: 16rpx;
			}

			.detail-item {
				display: flex;
				justify-content: space-between;
				align-items: center;
				padding: 12rpx 0;
				font-size: 22rpx;
				color: #333;

				text {
					flex: 1;
					text-align: center;
				}
			}
		}
	}

	.footer {
		position: fixed;
		bottom: 0;
		left: 0;
		width: 100%;
		padding: 16rpx 20rpx 16rpx 32rpx;
		padding-bottom: calc(16rpx + constant(safe-area-inset-bottom));
		padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
		background: #FFFFFF;

		.btn {
			display: inline-block;
			height: 64rpx;
			padding: 0 40rpx;
			border-radius: 32rpx;
			background: #2A7EFB;
			font-weight: 500;
			font-size: 26rpx;
			line-height: 64rpx;
			color: #FFFFFF;
		}

		.all {
			font-size: 26rpx;
			color: #333333;

			.iconfont {
				margin-right: 12rpx;
				font-size: 40rpx;
				color: #CCCCCC;

				&.icon-a-ic_CompleteSelect {
					color: #2A7EFB;
				}
			}
		}

		.btn-box {
			flex: 1;
			text-align: right;

			.btn {
				text-align: center;
			}
		}
	}
</style>