<template>
	<view class="order-list" @click="handleOrderListClick">
		<view class="order-item" v-for="(item, index) of orderList" :key="item.order_id">
			<view class="order-item-header">
				<view class="order-item-type">
					{{ item.serviceType === 1 ? '上门' : '到店' }}
				</view>
				<view class="order-item-sn overflow-text">{{ item.workOrderNo }}</view>
				<view v-if="item.refundStatus === 0" class="order-item-status">{{ item.serviceStatus | serviceStatusFilter }}</view>
        <view v-else class="order-item-status">{{ item.refundStatus === 1 ? '退款审核中' : '已退款'}}</view>
			</view>

			<view class="order-item-contact-info mt-32" v-if="item.serviceType === 1">
				<view class="contact-info-left">
					<view class="contact-info-name overflow-text">
						{{ item.userName }}
						<text class="contact-info-phone">{{ item.userPhone }}</text>
					</view>
					<view class="contact-info-address overflow-text">
						{{ item.userAddress }}
					</view>
				</view>
				<button class="contact-action-btn iconfont icon-ic_Phone" :data-event="EVENT.CALL"
					:data-index="index"></button>
				<button class="contact-action-btn iconfont icon-icon_Location" :data-event="EVENT.LOCATION"
					:data-index="index"></button>
			</view>
      <navigator hover-class="none"
                 :url="orderType?`/pages/admin/workOrder_manage/workOrder_detail?workOrderNo=${item.workOrderNo}`:`/pages/admin/work_order/detail?workOrderNo=${item.workOrderNo}`">
        <OrderGoods :cartInfo="[item]" :isShowBtn="false">
        </OrderGoods>
        <view class="order-item-reverse-time overflow-text">
          预约日期：
          {{ item.reservationDate }}
          {{ item.reservationTimeSlot }}
        </view>
      </navigator>
      <!--工单管理操作按钮-->
			<view v-if="orderType && btnConfig(item).length" class="order-item-btn-group mt-40">
        <button class="order-item-btn" v-for="itms of btnConfig(item)" :key="itms.text"
                :type="itms.type" @click="handleBottomBarAction(itms.event,item.workOrderNo)">
          {{ itms.text }}
        </button>
			</view>
      <!--移动端商家管理操作按钮-->
      <view v-if="!orderType && adminBtnConfig(item).length" class="order-item-btn-group mt-40">
        <button class="order-item-btn" :type="itms.type" v-for="itms of adminBtnConfig(item)" :key="itms.text"
                @click="handleBottomAdminBarAction(itms.event,item.workOrderNo)" :class="itms.type==='default'?'defaultColor':''">
          {{ itms.text }}
        </button>
      </view>
		</view>

    <!-- 派单 改派 -->
    <OrderAssign ref="orderAssignRef" v-if="!orderType" @refresh="goDetail"></OrderAssign>
	</view>
</template>

<script>
	import {
		handleWorkOrderBarAction,
		serviceStatusEnum,
		workOrderBottomBar
	} from "../workOrder_manage/config";
	import OrderGoods from '@/components/orderGoods'
  import {handleMakeCallPhone, handleOpenLocation} from "@/libs/order";
  import {adminOrderBottomBar, handleAdminOrderBarAction} from "../work_order/config";
  import OrderAssign from "../work_order/components/orderAssign";

	const EVENT = {
		CALL: "call",
		LOCATION: "location"
	}

	export default {
		components: {
			OrderGoods,
      OrderAssign
		},
		props: {
			orderList: {
				type: Array,
				default: []
			},
      // 底部操作tab，用于区分工单还是移动端商家管理，有值是工单
			orderType: {
        type: Number,
        default: 0
      }
		},
		data() {
			return {
				serviceStatusEnum,
				// BtnUtil,
				EVENT,
				reservationConfig: {},
			};
		},
		mounted() {
			this.reservationConfig = uni.getStorageSync('reservationConfig'); // 商户预约设置
		},
		methods: {
      // 工单操作按钮回调
      async handleBottomBarAction(event,workOrderNo) {
        let data = await handleWorkOrderBarAction(event, workOrderNo)
        // 服务完成 / 服务开始
        if (data === 'complete' || data === 'start' || data ==='rush_order') {
         this.goWorkOrderDetail(workOrderNo)
        }
      },
      // 工单管理详情
      goWorkOrderDetail(workOrderNo){
        this.$util.navigateTo(`/pages/admin/workOrder_manage/workOrder_detail?workOrderNo=${workOrderNo}`)
      },
      // 移动端商家 工单详情
      goAdminWorkOrderDetail(workOrderNo){
        this.$util.navigateTo(`/pages/admin/work_order/detail?workOrderNo=${workOrderNo}`)
      },
      // 商家管理操作按钮回调
      async handleBottomAdminBarAction(event, workOrderNo) {
        let data = await handleAdminOrderBarAction(event, workOrderNo)
        // 服务完成 / 服务开始
        if (data === 'revise_reservation' || data === 'Compulsory_completion') await this.goAdminWorkOrderDetail(workOrderNo)
        if (data === 'revise_dispatch' || data === 'dispatch') await this.handleOpenAssignPage(data, workOrderNo)
      },
      // 派单 改派
      handleOpenAssignPage(isReassign, workOrderNo) {
        this.$refs.orderAssignRef.openPanel(workOrderNo, isReassign=== 'revise_dispatch'? true :false);
      },
      // 去订单详情
      goDetail(workOrderNo){
        if(this.orderType){
          this.goWorkOrderDetail(workOrderNo)
        }else{
          this.$util.navigateTo(`/pages/admin/work_order/detail?workOrderNo=${workOrderNo}`)
        }
      },
      // 工单操作按钮
			btnConfig(workOrderNoInfo) {
				return workOrderBottomBar(workOrderNoInfo)
			},
			async handleOrderListClick(e) {
				const {
					event,
					index
				} = e.target.dataset;
				if (event === undefined || index === undefined) return;
				const item = this.orderList[index];
				if (event === EVENT.CALL) {
					handleMakeCallPhone(item.userPhone);
				} else if (event === EVENT.LOCATION) {
					 await handleOpenLocation(item);
				}
			},
      //移动端商家管理操作按钮
      adminBtnConfig(adminOrderNoInfo){
        return adminOrderBottomBar(adminOrderNoInfo)
      },
		}
	}
</script>

<style scoped lang="scss">
.defaultColor{
  background-color: #fff !important;
  color: #666666 !important;
  border: 1px solid #DDDDDD;
}
	.order-list {
		padding: 0 24rpx 24rpx;
		color: #333;
	}

	.order-item {
		background-color: #fff;
		border-radius: 24rpx;
		padding: 32rpx 24rpx;

		&+.order-item {
			margin-top: 20rpx;
		}
	}

	.order-item-header {
		display: flex;
		align-items: baseline;

		.order-item-type {
			height: 32rpx;
			display: flex;
			align-items: center;
			padding-inline: 6rpx;
			font-size: 20rpx;
			color: #2A7EFB;
			border: 1px solid currentColor;
			border-radius: 6rpx;
		}

		.order-item-sn {
			font-size: 28rpx;
			margin-left: 16rpx;
			flex: 1;
		}

		.order-item-status {
			margin-left: auto;
			font-size: 26rpx;
			color: #FF9600;
		}
	}

	.order-item-reverse-time {
		font-size: 24rpx;
	}

	.order-item-btn-group {
		display: flex;
		justify-content: flex-end;

		.order-item-btn {
			height: 64rpx;
			padding-inline: 32rpx;
			border-radius: 50rpx;
			//background-color: #2291F8;
			font-size: 24rpx;
			color: #fff;
			display: flex;
			align-items: center;
			justify-content: center;

			&+.order-item-btn {
				margin-left: 20rpx;
			}
		}
	}

	.order-item-contact-info {
		background-color: #f9f9f9;
		border-radius: 16rpx;
		padding: 32rpx;
		display: flex;
		align-items: center;

		.contact-info-left {
			margin-right: 28rpx;
			flex: 1;
			overflow: hidden;
			font-size: 28rpx;
			color: #282828;

			.contact-info-phone {
				margin-left: 30rpx;
			}

			.contact-info-address {
				font-size: 24rpx;
				color: #999;
				margin-top: 6rpx;
			}
		}

		.contact-action-btn {
			width: 56rpx;
			height: 56rpx;
			background-color: #edf2f9;
			border-radius: 50%;
			display: flex;
			align-items: center;
			justify-content: center;
			font-size: 28rpx;
			color: #2a7efb;

			&+.contact-action-btn {
				margin-left: 32rpx;
			}
		}
	}
</style>