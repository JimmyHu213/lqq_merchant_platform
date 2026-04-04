// 已领取的工单
import {checkinStaffOrderApi, workOrderReceiveApi, workOrderServiceEndApi} from "./workOrder";
import util from "../../../utils/util";

export const ASSIGNED = 2;
// 待领取工单
export const UNASSIGNED = 1;
// 工单主页
export const HOMEPAGE = 3;

/**
 * 工单服务类型
 */
export const serviceStatusEnum = {
	Unabsorbed: 1, //未分配
	WaitingService: 2, //已分配 待服务
	inService: 3, // 服务中
	ServiceEnd: 4, //服务结束
};

/**
 * 工单操作按钮判断
 * @type {{COMPLETE: string, SERVICE_RECORD: string, SIGN_IN: string, RUSH_ORDER: string, START: string}}
 */
const BTN_EVENT = {
	SERVICE_RECORD: "serviceRecord",
	SIGN_IN: "signIn",
	COMPLETE: 'complete',
	RUSH_ORDER: "rush_order",
	START: "start",
}
export function workOrderBottomBar(workOrderNoInfo) {
	const reservationConfig = uni.getStorageSync('reservationConfig'); // 商户预约设置
	//   if (!this.orderInfo || !this.merServiceConfig) return [];
	const config = [];
	if( workOrderNoInfo.refundStatus > 0 ) return [];
	// 上门serviceType === 1,2到店
	if (workOrderNoInfo.serviceType === 1) {
		// allocateType	分配类型：0-未分配，1-派单，2-抢单
		if (workOrderNoInfo.allocateType === 0) {
			config.push({
				text: "领取工单",
				type: "primary",
				event: BTN_EVENT.RUSH_ORDER
			});
		}
		if (workOrderNoInfo.serviceStatus === serviceStatusEnum.WaitingService) {
			config.push({
				text: "上门打卡",
				type: "primary",
				event: BTN_EVENT.SIGN_IN
			});
		}
		if (workOrderNoInfo.serviceStatus === serviceStatusEnum.inService && reservationConfig
			.serviceEvidenceSwitch && !workOrderNoInfo.serviceEvidenceFormId) {
			config.push({
				text: "服务留凭",
				type: "primary",
				event: BTN_EVENT.SERVICE_RECORD
			});
		}
	}else{
		if (workOrderNoInfo.serviceStatus === serviceStatusEnum.WaitingService) {
			config.push({
				text: "服务开始",
				type: "primary",
				event: BTN_EVENT.START
			});
		}
	}
	if (workOrderNoInfo.serviceStatus === serviceStatusEnum.inService && ((workOrderNoInfo.serviceEvidenceFormId >0 || !reservationConfig.serviceEvidenceSwitch) || workOrderNoInfo.serviceType === 2)) {
		config.push({
			text: "服务完成",
			type: "primary",
			event: BTN_EVENT.COMPLETE
		});
	}
	return config;
}

/**
 * 工单按钮操作
 * @param event 操作名称
 * @param workOrderNo 工单号
 * @returns {Promise<unknown>}
 */
export async function handleWorkOrderBarAction(event, workOrderNo) {
	switch (event) {
		case BTN_EVENT.SERVICE_RECORD:
			uni.navigateTo({
				url: `/pages/goods/service_record/index?workOrderNo=${workOrderNo}`
			});
			break;
		case BTN_EVENT.SIGN_IN:
			util.navigateTo(`/pages/admin/workOrder_manage/checkin?workOrderNo=${workOrderNo}`)
			break;
		case BTN_EVENT.COMPLETE:
			return new Promise(async (resolve) => {
				const result = await uni.showModal({
					content: "您确定要完成服务吗?",
				});
				if (result[0] || result[1].cancel) return;
				let data = await workOrderServiceEndApi(workOrderNo)
				if (data.code === 200) {
					util.Tips({
						title: '服务完成成功'
					});
					await resolve(BTN_EVENT.COMPLETE)
				}
			});
			break;
		case BTN_EVENT.RUSH_ORDER:
			return new Promise(async (resolve) => {
				const result = await uni.showModal({
					content: "您确定要领取此工单服务吗?",
				});
				if (result[0] || result[1].cancel) return;
				try {
					await workOrderReceiveApi(workOrderNo);
					util.Tips({
						title: '领取成功',
						icon: "success"
					});
					await resolve(BTN_EVENT.RUSH_ORDER)
					// uni.navigateTo({
					// 	url: `/pages/admin/workOrder_manage/workOrder_detail?workOrderNo=${workOrderNo}`
					// });
				} catch (err) {
					util.Tips({
						title: err,
						icon: "none"
					});
				}
			});
			break;
		case BTN_EVENT.START:
			return new Promise(async (resolve) => {
				const result = await uni.showModal({
					content: "您确定要开始服务吗?",
				});
				if (result[0] || result[1].cancel) return;
				try {
					await checkinStaffOrderApi({workOrderNo: workOrderNo});
					uni.hideLoading();
					util.Tips({
						title: '服务开始',
						icon: "none"
					});
					await resolve(BTN_EVENT.START)
				} catch (err) {
					uni.hideLoading();
					util.Tips({
						title: err,
						icon: "none"
					});
				}
			});
			break;
	}
}
