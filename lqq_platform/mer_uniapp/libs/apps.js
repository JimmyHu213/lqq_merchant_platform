// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { appAuth } from '../api/public';
import { tokenIsExistApi } from '@/api/api.js';

class Apps{
	/**
	 * 校验token是否有效,true为有效，false为无效
	 */
	getTokenIsExist(){
		return new Promise( (resolve,reject) => {
			tokenIsExistApi().then(res => {
				resolve(res.data);
			}).catch(reject);
		})
	}
	
	/**
	 * 授权登录获取token
	 * @param {Object} code
	 */
	authApp(code) {
		return new Promise((resolve, reject) => {
			appAuth(code,{'spread_spid': 0})
				.then(({
					data
				}) => {
					resolve(data);
					Cache.set(WX_AUTH, code);
					Cache.clear(STATE_KEY);
					loginType && Cache.clear(LOGINTYPE);
					
				})
				.catch(reject);
		});
	}

	/**
	 * 滚动页面颜色变化
	 * @param e
	 * @returns {{backgroundColor: string, iconColor: string}}
	 */
	onPageScrollGetColor(e){
		let backgroundColor = ''
		let iconColor = ''
		if (e.scrollTop > 50) {
			backgroundColor = '#fff';
			iconColor = '#333333'
		} else if (e.scrollTop < 50) {
			backgroundColor = '';
			iconColor = '#fff'
		}
		return {
			backgroundColor,
			iconColor
		}
	}

}
export default new Apps();
