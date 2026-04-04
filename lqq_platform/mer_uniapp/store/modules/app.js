// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import Auth from '../../libs/wechat';
import {
	getPayConfigApi
} from '../../api/order.js';
import {
	getUserInfoApi,
	getLogout
} from "../../api/user.js";
import {
	getMerTypeListApi,
	getMerCategoryListApi
} from '@/api/merchant.js';
import {
	getTheme,
	tokenIsExistApi
} from '../../api/api.js';
import {
	LOGIN_STATUS,
	UID,
	PLATFORM,
	GLOBAL_DATA,
	SELECT_MERID,
	MERCHANT_EMPLOYEE_LIST,
	SELECT_MERCHANT,
	MANAGER_STATUS,
	IS_EMPLOYEE
} from '../../config/cache';
import Cache from '../../utils/cache';
import {
	USER_INFO
} from '../../config/cache';
import util from '../../utils/util';
import {
	globalConfigApi, loginConfigApi
} from "../../api/public";
import store from "../index";
import Routine from "../../libs/routine";
const state = {
	token: Cache.get(LOGIN_STATUS) || false, // 用户登录
	managerToken: Cache.get(MANAGER_STATUS) || false, // 移动端商家管理登录
	workOrderToken: Cache.get('workOrderToken') || false, // 工单管理员登录
	backgroundColor: "#fff",
	userInfo: Cache.get(USER_INFO) ? JSON.parse(Cache.get(USER_INFO)) : null,
	uid: Cache.get(UID) || '',
	homeActive: false,
	chatUrl: Cache.get('chatUrl') || '',
	systemPlatform: Cache.get(PLATFORM) ? Cache.get(PLATFORM) : '',
	productType: Cache.get('productType') || '',
	globalData: Cache.get(GLOBAL_DATA)?JSON.parse(Cache.get(GLOBAL_DATA)): {},
	merchantClassify: Cache.get('merchantClassify') ? JSON.parse(Cache.get('merchantClassify')) : [],
	/** 商户分类 **/
	merchantType: Cache.get('merchantType') ? JSON.parse(Cache.get('merchantType')) : [],
	/** 商户类型 **/
	merchantAPPInfo: Cache.get('merchantAPPInfo') ? JSON.parse(Cache.get('merchantAPPInfo')) : {},
	merSttledData: Cache.get('merSttledData') ? JSON.parse(Cache.get('merSttledData')) : {},
	bottomNavigationIsCustom: false, //是否使用自定义导航
	merTokenIsExist: Cache.get('merTokenIsExist') || false,
	discoverTopic: [],
	merchantEmployeeList: Cache.get('merchantEmployeeList') ? JSON.parse(Cache.get('merchantEmployeeList')) : [],
	isEmployee: Cache.get(IS_EMPLOYEE) ? JSON.parse(Cache.get(IS_EMPLOYEE)) : null,
	selectMerId: Cache.get('selectMerId') ? JSON.parse(Cache.get('selectMerId')) : null,
	selectMerchantRole: Cache.get('selectMerchantRole') || null,
	isAdvertisement: Cache.get('isAdvertisement') || false, //是否展示开屏广告
};

const mutations = {
	LOGIN(state, opt) {
		state.token = opt.token;
		Cache.set(LOGIN_STATUS, opt.token);
	},
	// 商家管理token
	SET_MANAGER_TOKEN(state, opt) {
		state.managerToken = opt.managerToken;
		Cache.set(MANAGER_STATUS, opt.managerToken);
	},
	// 工单管理token
	SET_WORK_ORDER_TOKEN(state, token){
		state.workOrderToken = token;
		Cache.set('workOrderToken', token);
	},
	SETUID(state, val) {
		state.uid = val;
		Cache.set(UID, val);
	},
	UPDATE_LOGIN(state, token) {
		state.token = token;
	},
	LOGOUT(state) {
		//uni.clearStorageSync();
		state.token = '';
		state.managerToken = '';
		state.workOrderToken = ''
		state.selectMerchantRole = '';
		state.uid = '';
		state.merchantEmployeeList = [];
		state.isEmployee = false;
		state.selectMerId = '';
		state.selectMerchantRole = '';
		Cache.clear(LOGIN_STATUS);
		Cache.clear(UID);
		Cache.clear(USER_INFO);
		Cache.clear(SELECT_MERID);
		Cache.clear(MERCHANT_EMPLOYEE_LIST);
		Cache.clear(SELECT_MERCHANT);
		Cache.clear(MANAGER_STATUS);
		Cache.clear(IS_EMPLOYEE);
		Cache.clear('workOrderToken');
	},
	//清除所有本地缓存
	clearStorage(state) {
		uni.clearStorageSync();
	},
	BACKGROUND_COLOR(state, color) {
		state.color = color;
		document.body.style.backgroundColor = color;
	},
	UPDATE_USERINFO(state, userInfo) {
		state.userInfo = userInfo;
		Cache.set(USER_INFO, userInfo);
	},
	OPEN_HOME(state) {
		state.homeActive = true;
	},
	CLOSE_HOME(state) {
		state.homeActive = false;
	},
	SET_CHATURL(state, chatUrl) {
		state.chatUrl = chatUrl;
	},
	SYSTEM_PLATFORM(state, systemPlatform) {
		state.systemPlatform = systemPlatform;
		Cache.set(PLATFORM, systemPlatform);
	},
	//更新useInfo数据
	changInfo(state, payload) {
		state.userInfo[payload.amount1] = payload.amount2;
		Cache.set(USER_INFO, state.userInfo);
	},
	//商品类型，用于区分视频号商品与一般商品
	PRODUCT_TYPE(state, productType) {
		state.productType = productType;
		Cache.set('productType', productType);
	},
	SET_GLOBAL_DATA(state, key) {
		Cache.set(GLOBAL_DATA, key);
		state.globalData = key;
	},
	/** 商户全部分类  **/
	SET_MerchantClassify: (state, merchantClassify) => {
		state.merchantClassify = changeNodes(merchantClassify)
		Cache.set('merchantClassify', JSON.stringify(changeNodes(merchantClassify)));
	},
	/** 商户全部类型 **/
	SET_MerchantType: (state, merchantType) => {
		state.merchantType = changeNodes(merchantType)
		Cache.set('merchantType', JSON.stringify(changeNodes(merchantType)));
	},
	/** 商户信息 **/
	MERCHANTJINFO: (state, merchantJInfo) => {
		state.merchantAPPInfo = merchantJInfo
		Cache.set('merchantAPPInfo', merchantJInfo);
	},
	/** 入驻申请信息 **/
	MERSTTLEDDATA: (state, merSttledData) => {
		state.merSttledData = merSttledData
		Cache.set('merSttledData', merSttledData);
	},
	/** 是否使用自定义导航 **/
	BottomNavigationIsCustom: (state, bottomNavigationIsCustom) => {
		state.bottomNavigationIsCustom = bottomNavigationIsCustom
	},
	/** 校验token是否有效 **/
	TokenIsExist: (state, merTokenIsExist) => {
		state.merTokenIsExist = merTokenIsExist
		Cache.set('merTokenIsExist', merTokenIsExist);
	},
	/** 选中的话题列表 **/
	DiscoverTopic: (state, discoverTopic) => {
		state.discoverTopic = discoverTopic
		//Cache.set('merTokenIsExist', merTokenIsExist);
	},
	/** 商家管理列表 **/
	SetMerchantEmployeeList(state, val) {
		state.merchantEmployeeList = val;
		Cache.set('merchantEmployeeList', val);
	},
	SetIsEmployee(state, data) {
		state.isEmployee = data.isEmployee || data.isServiceStaff;
		Cache.set(IS_EMPLOYEE, state.isEmployee);
	},
	/** 当前商铺ID **/
	SetSelectMerId(state, val) {
		state.selectMerId = val;
		Cache.set('selectMerId', val);
	},
	CLEAR_SELECTMERID(state, val) {
		state.selectMerId = null;
		Cache.clear(SELECT_MERID);
	},
	/** 当前管理员权限 **/
	SetSelectMerchantRole(state, val) {
		state.selectMerchantRole = val;
		Cache.set('selectMerchantRole', val);
	},
	updatePaidMember: (state, userIsPaidMember) => {
		state.globalData.userIsPaidMember = userIsPaidMember;
		Cache.set(GLOBAL_DATA, state.globalData);
	},
	//修改globalData的值
	Change_GLOBAL_DATA(state, data) {
		//公司名称
		state.globalData.companyName = data.siteName || '欢迎你';
		uni.setStorageSync('companyName', data.siteName);
		state.globalData.paidMemberPriceDisplay = data.paidMemberPriceDisplay;
		state.globalData.userIsPaidMember = data.userIsPaidMember;
		state.globalData.changeColorConfig = data.changeColorConfig;
		state.globalData.copyrightCompanyImage = data.copyrightCompanyImage;
		state.globalData.frontDomain = data.frontDomain;
		state.globalData.imageDomain = data.imageDomain;
		state.globalData.authorizeAddress = data.authorizeAddress;
		state.globalData.authorizeFilingNum = data.authorizeFilingNum;
		state.globalData.authorizeInfo = data.authorizeInfo;
		state.globalData.authorizePhone = data.authorizePhone;
		state.globalData.merchantApplySwitch = data.merchantApplySwitch;
		Cache.set(GLOBAL_DATA, state.globalData);
	},
	// 分销码
	Change_Spread(state, spread) {
		state.globalData.spread = spread;
		Cache.set(GLOBAL_DATA, state.globalData);
	},
	//修改globalData的值
	Change_GLOBAL_DATA_loginConfig(state, data) {
		//公众号登录方式(单选),1微信授权，2手机号登录
		state.globalData.publicLoginType = data.wechatBrowserVisit;
		Cache.set('publicLoginType', data.wechatBrowserVisit);
		//小程序手机号校验类型（多选）1微信小程序验证 2短信验证
		state.globalData.routinePhoneVerification = data.routinePhoneVerification;
		//登录页logo
		state.globalData.mobileLoginLogo = data.mobileLoginLogo;
		Cache.set(GLOBAL_DATA, state.globalData);
		uni.setStorageSync('mobileLoginLogo', data.mobileLoginLogo);
	},
	//修改globalData中的值，分销码，id等
	Change_GLOBAL_DATA_Spread(state, data) {
		state.globalData.spread = data.sd ? data.sd : '';
		state.globalData.id = data.id ? data.id : '';
		state.globalData.marketingType = data.mt ? data.mt : '0';
		Cache.set(GLOBAL_DATA, state.globalData);
	},
	// 颜色
	Change_GLOBAL_theme(state,data){
		state.globalData.theme = data;
		Cache.set(GLOBAL_DATA, state.globalData);
	},
	// 设置开屏广告显示
	Change_Advertisement(state,data){
		state.isAdvertisement = data;
		Cache.set('isAdvertisement', data);
	}
};

/** tree去除 childList=[] 的结构**/
const changeNodes = function(data) {
	if (data.length > 0) {
		for (var i = 0; i < data.length; i++) {
			if (!data[i].childList || data[i].childList.length < 1) {
				data[i].childList = undefined;
			} else {
				changeNodes(data[i].childList);
			}
		}
	}
	return data
};

const actions = {
	/**
	 * 全局配置信息
	 */
	GetGlobalConfig({
		state,
		commit
	}) {
		return new Promise(reslove => {
			globalConfigApi().then(res => {
				let data = res.data;
				Cache.set('imgHost', data.imageDomain + '/');
				commit('Change_GLOBAL_DATA', data)
			});
		}).catch(err => {
			return util.Tips({
				title: err
			});
		});
	},
	//获取登录配置
	GetLoginConfig({
		state,
		commit
	}) {
		return new Promise(reslove => {
			loginConfigApi().then(res => {
				let data = res.data;
				commit('Change_GLOBAL_DATA_loginConfig', data)
			})
		}).catch(err => {
			return util.Tips({
				title: err
			});
		});
	},
	//获取diy颜色配置
	GetThemeConfig({
		state,
		commit
	}) {
		return new Promise(reslove => {
			getTheme().then(res => {
				Cache.set('theme', `theme${Number(res.data.value)}`);
				commit('Change_GLOBAL_theme', `theme${Number(res.data.value)}`)
				// #ifdef H5
				window.document.documentElement.setAttribute('data-theme', res.data.value);
				// #endif
			})
		}).catch(err => {
			return util.Tips({
				title: err
			});
		});
	},
	/**
	 * 校验token是否有效,true为有效，false为无效
	 */
	GetTokenIsExist({
		state,
		commit
	}, force) {
		return new Promise(reslove => {
			tokenIsExistApi().then(res => {
				commit('TokenIsExist', res.data)
				if (!res.data) {
					 commit("LOGOUT");
					 commit('updatePaidMember', false);
				}
				reslove(res.data);
			});
		}).catch(err => {
			return util.Tips({
				title: err
			});
		});
	},
	/**
	 * 用户信息
	 */
	USERINFO({
		state,
		commit
	}, force) {
		return new Promise(reslove => {
			getUserInfoApi().then(res => {
				commit("UPDATE_USERINFO", res.data);
				commit("SETUID", res.data.id);
				reslove(res.data);
			});
		}).catch(() => {

		});
	},

	/**
	 * 退出登录
	 */
	GETLOGOUT({
		state,
		commit,
		dispatch
	}, force) {
		return new Promise(reslove => {
			getLogout().then(async () => {
				await commit("LOGOUT");
				await dispatch("GetTokenIsExist");
				await commit('updatePaidMember', false);
				util.navigateTo('/pages/index/index');
			});
		}).catch(() => {

		});
	},
	MerCategoryList({
		state,
		commit
	}, force) {
		return new Promise(reslove => {
			getMerCategoryListApi().then(res => {
				commit('SET_MerchantClassify', res.data)
				reslove(res.data);
			});
		}).catch(err => {
			return util.Tips({
				title: err
			});
		});
	},
	MerTypeList({
		state,
		commit
	}, force) {
		return new Promise(reslove => {
			getMerTypeListApi().then(res => {
				commit('SET_MerchantType', res.data)
				reslove(res.data);
			});
		}).catch(err => {
			return util.Tips({
				title: err
			});
		});
	},
	getPayConfig({
		state,
		commit
	}, force) {
		return new Promise(reslove => {
			getPayConfigApi(force.orderNo).then(res => {
				let data = res.data;
				let payConfig = [];
				if (data.payWechatOpen) {
					payConfig.push({
						name: "微信支付",
						icon: "icon-a-ic_wechatpay",
						value: 'weixin',
						title: '微信快捷支付',
					});
				}
				// #ifndef MP
				let aliPayStatus = data.aliPayStatus;
				// #ifdef H5
				if (Auth.isWeixin()) {
					aliPayStatus = false;
				}
				// #endif
				if (aliPayStatus) {
					payConfig.push({
						name: "支付宝支付",
						icon: "icon-a-ic_alipay",
						value: 'alipay',
						title: '支付宝快捷支付',
					});
				}
				// #endif
				if (data.yuePayStatus) {
					payConfig.push({
						name: "余额支付",
						icon: "icon-ic_Money2",
						value: 'yue',
						title: '可用余额:',
						userBalance: data.userBalance || 0
					});
				}
				if (data.shoppingCreditsStatus) {
					payConfig.push({
						name: "购物金支付",
						icon: "icon-ic_gouwujin",
						value: 'shoppingCredits',
						title: '可用购物金:',
						shoppingCreditsBalance: data.shoppingCreditsBalance || 0
					});
				}
				reslove({
					userBalance: data.userBalance,
					payConfig: payConfig
				});
			})
		}).catch(err => {
			return util.Tips({
				title: err
			});
		});
	}
}
export default {
	state,
	mutations,
	actions
};