import util from "../../../utils/util";
import {workerOrderForceFinishApi} from "./workOrder";

/**
 * 工单服务类型
 */
export const serviceStatusEnum = {
	Unabsorbed: 1, //待领取
	WaitingService: 2, //已分配 待服务
	inService: 3, // 服务中
	ServiceEnd: 4, //服务结束
};

/**
 * 移动端商家管理操作按钮
 * @type {{REVISE_DISPATCH: string, DISPATCH: string, REVISE_RESERVATION: string, COMPULSORY_COMPLETION: string}}
 */
const BTN_EVENT = {
	DISPATCH: "dispatch",
	REVISE_DISPATCH: "revise_dispatch",
	REVISE_RESERVATION: 'revise_reservation',
	COMPULSORY_COMPLETION: 'Compulsory_completion'
}
export function adminOrderBottomBar(workOrderNoInfo) {
	const config = [];
	// 上门serviceType === 1,2到店
	// allocateType	分配类型：0-未分配，1-派单，2-抢单
	if( workOrderNoInfo.refundStatus > 0 ) return [];
	if (workOrderNoInfo.serviceStatus === serviceStatusEnum.inService || workOrderNoInfo.serviceStatus === serviceStatusEnum.WaitingService) {
		config.push({
			text: "改派",
			type: "primary",
			event: BTN_EVENT.REVISE_DISPATCH
		});
	}
	if (workOrderNoInfo.serviceStatus === serviceStatusEnum.Unabsorbed || workOrderNoInfo.serviceStatus === serviceStatusEnum.WaitingService) {
		config.push({
			text: "改约",
			type: "default",
			event: BTN_EVENT.REVISE_RESERVATION
		});
	}
	if( workOrderNoInfo.serviceStatus === serviceStatusEnum.inService ){
		config.push({
			text: "强制完成",
			type: "default",
			event: BTN_EVENT.COMPULSORY_COMPLETION
		});
	}
	if (workOrderNoInfo.serviceStatus === serviceStatusEnum.Unabsorbed) {
		config.push({
			text: "派单",
			type: "primary",
			event: BTN_EVENT.DISPATCH
		});
	}
	return config;
}

/**
 * 移动端商家管理操作
 * @param event 操作名称
 * @param workOrderNo 工单号
 * @returns {Promise<unknown>}
 */
export async function handleAdminOrderBarAction(event, workOrderNo) {
	switch (event) {
		// 派单
		case BTN_EVENT.DISPATCH:
			return new Promise(async (resolve) => {
				resolve(BTN_EVENT.DISPATCH)
			});
			break;
		// 改派
		case BTN_EVENT.REVISE_DISPATCH:
			return new Promise(async (resolve) => {
				resolve(BTN_EVENT.REVISE_DISPATCH)
			});
			break;
		case BTN_EVENT.REVISE_RESERVATION:  //改约
			util.navigateTo(`/pages/admin/work_order/reschedule?workOrderNo=${workOrderNo}`)
			break;
		case BTN_EVENT.COMPULSORY_COMPLETION: //强制完成
			return new Promise(async (resolve) => {
				const result = await uni.showModal({
					content: "您确定要强制完成服务吗?",
				});
				if (result[0] || result[1].cancel) return;
				let data = await workerOrderForceFinishApi({workOrderNoList: [workOrderNo]})
				if (data.code === 200) {
					util.Tips({
						title: '服务完成成功'
					});
					await resolve(BTN_EVENT.COMPULSORY_COMPLETION)
				}
			});
	}
}
