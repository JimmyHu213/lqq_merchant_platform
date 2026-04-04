// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

// 调后端接口获取tmplIds给后端传的值的拼接，不同情况下
const arrTemp =  ["beforePay","afterPay", "createBargain","pink"];

/**
 * 支付成功后订阅消息id,tmplIds最多放三个
 * 订阅  确认收货通知 订单支付成功  订单发货提醒
 */
export function openPaySubscribe() {
	let tmplIdsPay = uni.getStorageSync('tempID' + arrTemp[0]);
	let tmplIdsOrder = uni.getStorageSync('tempID' + arrTemp[1]);
	return subscribe([...tmplIdsPay,...tmplIdsOrder]);
}

/**
 * 订单相关订阅消息
 * 送货 发货 取消订单
 */
export function openOrderSubscribe() {
	let tmplIds = uni.getStorageSync('tempID' + arrTemp[1]);
	return subscribe(tmplIds);
}

/**
 * 提现消息订阅
 * 成功 和 失败 消息
 */
// export function openExtrctSubscribe() {
// 	let tmplIds = uni.getStorageSync('tempID' + arrTemp[2]);
// 	return subscribe(tmplIds);
// }

/**
 * 拼团成功
 */
export function openPinkSubscribe() {
	let tmplIds = uni.getStorageSync('tempID' + arrTemp[3]);
	return subscribe(tmplIds);
}

/**
 * 调起订阅界面
 * array tmplIds 模板id
 */
export function subscribe(tmplIds) {
	return new Promise((reslove, reject) => {
		uni.requestSubscribeMessage({
			tmplIds: tmplIds,
			success(res) {
				return reslove(res);
			},
			fail(err) {
				console.log('调用模板消息失败',err)
				return reslove(err);
			}
		})
	});
}
