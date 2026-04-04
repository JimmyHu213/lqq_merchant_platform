// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import Cache from '@/utils/cache.js';
let app = getApp();

//主特色
export function setThemeColor(){
	switch (app.globalData.theme) {
		case 'theme1':
			return '#e93323';
			break;
		case 'theme2':
			return '#FE5C2D';
			break;
		case 'theme3':
			return '#42CA4D';
			break;
		case 'theme4':
			return '#1DB0FC';
			break;
		case 'theme5':
			return '#FF448F';
			break;
	}
}

// 渐变色
export function setLinearThemeColor(){
	switch (app.globalData.theme) {
		case 'theme1':
			return 'linear-gradient(90deg, #E93323 0%, #FF7931 100%)';
			break;
		case 'theme2':
			return 'linear-gradient(90deg, #FE5C2D 0%, #FF7931 100%)';
			break;
		case 'theme3':
			return 'linear-gradient(90deg, #42Ca4D 0%, #70E038 100%)';
			break;
		case 'theme4':
			return 'linear-gradient(90deg, #1DB0FC 0%, #40D1F4 100%)';
			break;
		case 'theme5':
			return 'linear-gradient(90deg, #FF448F 0%, #FF67AD 100%)';
			break;
	}
}

// 主题色淡色
export function themeRgba(){
	switch (app.globalData.theme) {
		case 'theme1':
			return 'rgba(233, 51, 35, .05)';
			break;
		case 'theme2':
			return 'rgba(254, 92, 45, .05)';
			break;
		case 'theme3':
			return 'rgba(66, 202, 77, .05)';
			break;
		case 'theme4':
			return 'rgba(29, 176, 252, .05)';
			break;
		case 'theme5':
			return 'rgba(255, 69, 144, .05)';
			break;
	}
}

// 主题色淡色
export function lightColor(){
	switch (app.globalData.theme) {
		case 'theme1':
			return '#FFF6F6';
			break;
		case 'theme2':
			return '#FEE0D2';
			break;
		case 'theme3':
			return '#DBF5D6';
			break;
		case 'theme4':
			return '#D1F1FB';
			break;
		case 'theme5':
			return '#FFD8E7';
			break;
	}
}

// 我的账户背景浅色渐变色
export function shallowGradientColor(){
	switch (app.globalData.theme) {
		case 'theme1':
			return 'linear-gradient(180deg, #F2CECB, #F5F5F5)';
			break;
		case 'theme2':
			return 'linear-gradient(180deg, #F7D5CB, #F5F5F5)';
			break;
		case 'theme3':
			return 'linear-gradient(180deg, #CFECD1, #F5F5F5)';
			break;
		case 'theme4':
			return 'linear-gradient(180deg, #C6E6F6, #F5F5F5)';
			break;
		case 'theme5':
			return 'linear-gradient(180deg, #F7CEDE, #F5F5F5)';
			break;
	}
}
