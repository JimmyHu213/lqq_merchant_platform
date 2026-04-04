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
	HTTP_REQUEST_URL,
	HEADER,
	TOKENNAME,
	HEADERPARAMS
} from '@/config/app';
import {
	toLogin,
	checkLogin
} from '../libs/login';
import store from '../store';

// 获取对应的token
const getToken = (url) => {
	// 工单登录 - staff相关接口（优先级最高）
	if (url.includes('staff') && !url.includes('employee/service/staff/list') && store.state.app.workOrderToken) {
		return store.state.app.workOrderToken;
	}

	// 管理员登录 - employee相关接口
	// 包含employee关键字，但排除特殊接口
	if (url.includes('employee') &&
		!url.includes('employee/merchant/belong/List') &&
		!url.includes('employee/merchant/active') &&
		store.state.app.managerToken) {
		return store.state.app.managerToken;
	}

	// 普通用户登录 - 其他所有接口
	if (store.state.app.token) {
		return store.state.app.token;
	}
};

/**
 * 发送请求
 */
function baseRequest(url, method, data, {
	noAuth = false,
	noVerify = false
}, params) {
	let Url = HTTP_REQUEST_URL,
		header = HEADER
	if (params != undefined) {
		header = HEADERPARAMS;
	}
	if (!noAuth) {
		//登录过期自动登录
		if (!store.state.app.token && !checkLogin()) {
			toLogin();
			return Promise.reject({
				msg: '未登录'
			});
		}
	}
	
	// 设置token到header
	const token = getToken(url);
	if (token) {
		header[TOKENNAME] = token;
	}
	return new Promise((reslove, reject) => {
		uni.request({
			url: Url + '/api/front/' + url,
			method: method || 'GET',
			header: header,
			data: data || {},
			success: (res) => {
				if (noVerify)
					reslove(res.data, res);
				else if (res.data.code == 200)
					reslove(res.data, res);
				else if ([410000, 410001, 410002, 401, 402].indexOf(res.data.code) !== -1) {
					let routes = getCurrentPages(); 
					//获取当前页面路由
					let curRoute = routes[routes.length - 1].route
					//工作台内页面跳转登录前，先跳转到个人中心，防止双token陷入登录循环
					if(curRoute.split('/')[1]==='admin'){
						uni.switchTab({
							url:'/pages/user/index'
						})
					}
					store.commit("LOGOUT");
					toLogin();
					reject(res.data);
				} else if (res.data.code == 500) {
					if (res.data.message && res.data.message.indexOf('登录信息已过期') != -1) {
						store.commit("LOGOUT");
					}
					reject(res.data.message || '系统异常');
				} else if (res.data.code == 400) {
					reject(res.data.message || '参数校验失败');
				} else if (res.data.code == 404) {
					reject(res.data.message || '没有找到相关数据');
				} else if (res.data.code == 403) {
					reject(res.data.message || '没有相关权限');
				} else
					reject(res.data.message || '系统错误');
			},
			fail: (msg) => {
				reject('请求失败');
			}
		})
	});
}

const request = {};

['options', 'get', 'post', 'put', 'head', 'delete', 'trace', 'connect'].forEach((method) => {
	request[method] = (api, data, opt, params) => baseRequest(api, method, data, opt || {}, params)
});



export default request;