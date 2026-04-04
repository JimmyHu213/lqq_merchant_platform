// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

export default {
	token: state => state.app.token,  // 用户登录
	managerToken: state => state.app.managerToken, // 移动端商家管理登录
	workOrderToken: state => state.app.workOrderToken,  // 工单管理员登录
	isLogin: state => !!state.app.token,
	backgroundColor: state => state.app.backgroundColor,
	userInfo: state => state.app.userInfo || {},
	uid: state => state.app.uid,
	homeActive: state => state.app.homeActive,
	home: state => state.app.home,
	chatUrl: state => state.app.chatUrl,
	systemPlatform: state => state.app.systemPlatform,
	productType: state => state.app.productType || 'normal',
	globalData: state => state.app.globalData,
	merchantClassify: state => state.app.merchantClassify,
	merchantType: state => state.app.merchantType,
	merchantAPPInfo: state => state.app.merchantAPPInfo,
	merSttledData: state => state.app.merSttledData,
	bottomNavigationIsCustom: state => state.app.bottomNavigationIsCustom,
	merTokenIsExist: state => state.app.merTokenIsExist,
	discoverTopic: state => state.app.discoverTopic,
	merchantEmployeeList:state => state.app.merchantEmployeeList,
	isEmployee:state => state.app.isEmployee,
	selectMerId:state => state.app.selectMerId,
	selectMerchantRole:state => state.app.selectMerchantRole,
	isAdvertisement: state => state.app.isAdvertisement,
	merMemberInfo: state => state.app.merMemberInfo
};
