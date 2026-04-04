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
	preOrderApi,refundOrderRevokeApi
} from '@/api/order.js';
import util from '@/utils/util'
import animationType from '@/utils/animationType.js'
import { ProductMarketingTypeEnum, ProductTypeEnum } from "@/enums/productEnums";
import {orderDel} from "../api/order";

/**
 * 去订单详情
 */
export function goOrderDetail(orderNo) {
	return new Promise(resolve => {
		if (!orderNo) return that.$util.Tips({
			title: '缺少订单号无法查看订单详情'
		});
		// #ifdef MP
		uni.navigateTo({
			url: '/pages/goods/order_details/index?orderNo=' + orderNo
		})
		// #endif  
		// #ifndef MP
		uni.navigateTo({
			animationType: animationType.type,
			animationDuration: animationType.duration,
			url: '/pages/goods/order_details/index?orderNo=' + orderNo
		})
		// #endif
	});
}

/**
 * 去商品详情
 */
export function goProductDetail(id, marketingType, params) {
	return new Promise(resolve => {
		// #ifdef MP
		uni.navigateTo({
			url: `/pages/goods/goods_details/index?id=${id}&mt=${marketingType}${params}`
		})
		// #endif
		// #ifndef MP
		uni.navigateTo({
			animationType: animationType.type,
			animationDuration: animationType.duration,
			url: `/pages/goods/goods_details/index?id=${id}&mt=${marketingType}${params}`
		})
		// #endif
	});
}

/**
 * 活动商品、普通商品、购物车、再次购买预下单
 * time 预约商品时需要
 */
export function onGetPreOrder(preOrderType, orderDetails) {
	return new Promise((resolve, reject) => {
		preOrderApi({
			"preOrderType": preOrderType,
			"orderDetails": orderDetails
		}).then(res => {
			uni.navigateTo({
				url: '/pages/goods/order_confirm/index?orderNo=' + res.data.orderNo
			});
		}).catch(err => {
			return util.Tips({
				title: err
			});
		})
	});
}

/**
 * 售后，撤销售后申请
 */
export function onRevokeRefund(refundOrderNo) {
	return new Promise((resolve, reject) => {
		uni.showModal({
			title: '提示',
			content: '确定要撤销本次退款申请吗？',
			confirmColor: '#f55850',
			success: function(res) {
				if (res.confirm) {
					refundOrderRevokeApi(refundOrderNo).then(res => {
						resolve()
					}).catch(err => {
						return util.Tips({
							title: err
						});
					})
				} else if (res.cancel) {
					console.log('用户点击取消');
				}
			}
		});
	});
}

/**
 * 去协议详情
 */
export function goToAgreement(from) {
    return new Promise(resolve => {
        // #ifdef MP
        uni.navigateTo({
            url: `/pages/goods/agreement_info/index?from=${from}`
        })
        // #endif
        // #ifndef MP
        uni.navigateTo({
            animationType: animationType.type,
            animationDuration: animationType.duration,
            url: `/pages/goods/agreement_info/index?from=${from}`
        })
        // #endif
    });
}

// 普通商品、虚拟（开启可申请退款开关）、视频号可申请退款
export function isRefund(orderInfo) {
	return (
		orderInfo.type === ProductMarketingTypeEnum.Normal &&
		(orderInfo.secondType === ProductTypeEnum.Normal ||
			orderInfo.secondType === ProductTypeEnum.Fictitious ||
			orderInfo.secondType === ProductTypeEnum.Video)
	)
}

/**
 * 删除订单
 */
export function onDelOrder(orderNo) {
	return new Promise((resolve, reject) => {
		uni.showModal({
			content: '确定删除该订单',
			cancelText: "取消",
			confirmText: "确定",
			showCancel: true,
			cancelColor: '#f55850',
			success: (res) => {
				if (res.confirm) {
					let that = this;
					orderDel(orderNo).then(res => {
						return util.Tips({
							title: '删除成功',
							icon: 'success'
						}, {
							tab: 4,
							url: '/pages/goods/order_list/index'
						});
					}).catch(err => {
						return util.Tips({
							title: err
						});
					});
				} else {

				}
			}
		})
	});
}

//拨打电话
export function handleMakeCallPhone(phone) {
	uni.makePhoneCall({
		phoneNumber: phone
	});
}

// 去定位
export const handleOpenLocation = async (location) => {
	uni.showLoading({ mask: true });
	try {
		uni.hideLoading();
		uni.openLocation({
			latitude: Number(location.latitude),
			longitude: Number(location.longitude),
			address: location.userAddress
		});
	} catch (err) {
		uni.hideLoading();
		uni.showToast({
			title: err,
			icon: "none"
		});
	}
}

/**
 * 通过经纬度计算两点间距离（单位：km，保留2位小数）
 * @param {number} lat1 起点纬度
 * @param {number} lng1 起点经度
 * @param {number} lat2 终点纬度
 * @param {number} lng2 终点经度
 * @returns {number} 距离（km，保留2位小数）
 */
export function getDistanceFromLatLonInMeter(lat2, lng2) {
	let lng1 = uni.getStorageSync('user_longitude')
	let lat1 = uni.getStorageSync('user_latitude')
	const toRad = d => d * Math.PI / 180;
	const R = 6371000; // 地球半径，单位米
	const dLat = toRad(lat2 - lat1);
	const dLng = toRad(lng2 - lng1);
	const a =
		Math.sin(dLat / 2) * Math.sin(dLat / 2) +
		Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
		Math.sin(dLng / 2) * Math.sin(dLng / 2);
	const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	const distanceMeter = R * c;
	const distanceKm = distanceMeter / 1000;
	return Number(distanceKm.toFixed(2));
}

/**
 * 复制粘贴
 * @param text
 */
export function handleSetClipboard(text) {
	uni.setClipboardData({
		data: text,
		success: () => {
			uni.showToast({ title: '复制成功' });
		}
	});
}