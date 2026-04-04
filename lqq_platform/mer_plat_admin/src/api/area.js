// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import request from '@/utils/request';

/**
 * 圈层代理关联商户
 * @param data
 */
export function areaBindMerchantApi(data) {
  return request({
    url: `/admin/platform/circle/bind/merchant`,
    method: 'post',
    data,
  });
}

/**
 * 删除圈层代理
 * @param id
 */
export function areaDeleteApi(id) {
  return request({
    url: `/admin/platform/circle/delete/${id}`,
    method: 'post',
  });
}

/**
 * 查询圈层代理详情信息
 * @param id
 */
export function areaInfoApi(id) {
  return request({
    url: `/admin/platform/circle/info/${id}`,
    method: 'get',
  });
}

/**
 * 获取圈层代理tree结构的列表
 * @param params
 */
export function areaListTreeApi(params) {
  return request({
    url: `/admin/platform/circle/list/tree`,
    method: 'get',
    params,
  });
}

/**
 * 查询圈层代理相关商户
 * @param id
 */
export function areaMerchantApi(id) {
  return request({
    url: `/admin/platform/circle/merchant/${id}`,
    method: 'get',
  });
}

/**
 * 圈层代理解绑商户
 * @param data
 */
export function areaRemoveMerchantApi(data) {
  return request({
    url: `/admin/platform/circle/remove/merchant`,
    method: 'post',
    data,
  });
}

/**
 * 重置圈层代理密码
 * @param id
 */
export function areaResetPasswordApi(id) {
  return request({
    url: `/admin/platform/circle/reset/password/${id}`,
    method: 'post',
  });
}

/**
 * 新增圈层代理
 * @param data
 */
export function areaSaveApi(data) {
  return request({
    url: `/admin/platform/circle/save`,
    method: 'post',
    data,
  });
}

/**
 * 圈层代理状态开关
 * @param id
 */
export function areaStatusApi(id) {
  return request({
    url: `/admin/platform/circle/status/${id}`,
    method: 'post',
  });
}
/**
 * 圈层代理状态开关
 * @param id
 */
export function areaIsShowApi(id) {
  return request({
    url: `/admin/platform/circle/show/${id}`,
    method: 'post',
  });
}

/**
 * 修改圈层代理
 * @param data
 */
export function areaUpdateApi(data) {
  return request({
    url: `/admin/platform/circle/update`,
    method: 'post',
    data,
  });
}

/**
 * 圈层菜单列表
 * @param params
 */
export function menuAreaListApi(params) {
  const data = {
    menuType: params.menuType, //菜单类型:M-目录，C-菜单，A-按钮
    name: params.name, //菜单名称
    systemType: params.systemType,
  };
  return request({
    url: `/admin/platform/circle/menu/list`,
    method: 'get',
    params: data,
  });
}

/**
 * 圈层菜单新增
 * @param data
 */
export function menuAreaAdd(data) {
  let systemMenuRequest = data;
  return request({
    url: `/admin/platform/circle/menu/add`,
    method: 'post',
    data: systemMenuRequest,
  });
}

/**
 * 圈层菜单删除
 * @param id
 */
export function menuAreaDelete(id) {
  return request({
    url: `/admin/platform/circle/menu/delete/${id}`,
    method: 'post',
  });
}

/**
 * 圈层菜单详情
 * @param id
 */
export function menuAreaInfo(id) {
  return request({
    url: `/admin/platform/circle/menu/info/${id}`,
    method: 'get',
  });
}

/**
 * 圈层菜单修改
 * @param data
 */
export function menuAreaUpdate(data) {
  let systemMenuRequest = data;
  return request({
    url: `/admin/platform/circle/menu/update`,
    method: 'post',
    data: systemMenuRequest,
  });
}

/**
 * 圈层菜单修改显示状态
 * @param params
 */
export function menuAreaUpdateShowStatus(params) {
  return request({
    url: `/admin/platform/circle/menu/update/show/${params.id}`,
    method: 'post',
  });
}

// ==================== 圈层端-父级-主页控制器 ====================

/**
 * 圈层父级首页数据
 */
export function areasParentHomeIndexApi() {
  return request({
    url: `/admin/circle/statistics/home/index`,
    method: 'get',
  });
}

// ==================== 圈层端-管理员控制器 ====================

/**
 * 删除管理员
 * @param {number} id
 */
export function areasAdminDeleteApi(id) {
  return request({
    url: `/admin/circle/admin/delete/${id}`,
    method: 'get',
  });
}

/**
 * 后台管理员详情
 * @param {number} id
 */
export function areasAdminInfoApi(id) {
  return request({
    url: `/admin/circle/admin/info/${id}`,
    method: 'get',
  });
}

/**
 * 管理员分页列表
 * @param {object} params
 */
export function areasAdminListApi(params) {
  return request({
    url: `/admin/circle/admin/list`,
    method: 'get',
    params,
  });
}

/**
 * 新增后台管理员
 * @param {object} data
 */
export function areasAdminSaveApi(data) {
  return request({
    url: `/admin/circle/admin/save`,
    method: 'post',
    data,
  });
}

/**
 * 修改管理员
 * @param {object} data
 */
export function areasAdminUpdateApi(data) {
  return request({
    url: `/admin/circle/admin/update`,
    method: 'post',
    data,
  });
}

/**
 * 修改后台管理员密码
 * @param {object} data
 */
export function areasAdminUpdatePasswordApi(data) {
  return request({
    url: `/admin/circle/admin/update/password`,
    method: 'post',
    data,
  });
}

/**
 * 修改后台管理员状态
 * @param {object} params
 */
export function areasAdminUpdateStatusApi(params) {
  return request({
    url: `/admin/circle/admin/updateStatus/${params.id}`,
    method: 'get',
    params: { status: params.status },
  });
}

// ==================== 圈层端-管理角色控制器 ====================

/**
 * 删除角色
 * @param {number} id
 */
export function areasRoleDeleteApi(id) {
  return request({
    url: `/admin/circle/role/delete/${id}`,
    method: 'post',
  });
}

/**
 * 角色详情
 * @param {number} id
 */
export function areasRoleInfoApi(id) {
  return request({
    url: `/admin/circle/role/info/${id}`,
    method: 'get',
  });
}

/**
 * 角色分页列表
 * @param {object} params
 */
export function areasRoleListApi(params) {
  return request({
    url: `/admin/circle/role/list`,
    method: 'get',
    params,
  });
}

/**
 * 新增角色
 * @param {object} data
 */
export function areasRoleSaveApi(data) {
  return request({
    url: `/admin/circle/role/save`,
    method: 'post',
    data,
  });
}

/**
 * 修改角色
 * @param {object} data
 */
export function areasRoleUpdateApi(data) {
  return request({
    url: `/admin/circle/role/update`,
    method: 'post',
    data,
  });
}

/**
 * 修改角色状态
 * @param {object} data
 */
export function areasRoleUpdateStatusApi(data) {
  return request({
    url: `/admin/circle/role/updateStatus`,
    method: 'post',
    data,
  });
}

// ==================== 圈层端-结点-主页控制器 ====================

/**
 * 圈层结点用户渠道数据
 */
export function areasNodeHomeChannelApi() {
  return request({
    url: `/admin/circle/statistics/home/channel`,
    method: 'get',
  });
}

/**
 * 圈层结点首页数据
 */
export function areasNodeHomeIndexApi() {
  return request({
    url: `/admin/circle/statistics/home/index`,
    method: 'get',
  });
}

/**
 * 圈层结点经营数据
 */
export function areasNodeHomeOperatingDataApi() {
  return request({
    url: `/admin/circle/statistics/home/operating/data`,
    method: 'get',
  });
}

// ==================== 圈层端商品分类控制器 ====================

/**
 * 商户商品分类列表
 */
export function areasProductCategoryListApi() {
  return request({
    url: `/admin/circle/product/category/list`,
    method: 'get',
  });
}

export function areasProductCategoryCacheTree() {
  return request({
    url: `/admin/circle/product/category/cache/tree`,
    method: 'get',
  });
}

// ==================== 圈层端商品控制器 ====================

/**
 * 商品详情
 * @param {number} id
 */
export function areasProductInfoApi(id) {
  return request({
    url: `/admin/circle/product/info/${id}`,
    method: 'get',
  });
}

/**
 * 商品列表
 * @param {object} params
 */
export function areasProductListApi(params) {
  return request({
    url: `/admin/circle/product/list`,
    method: 'get',
    params,
  });
}

/**
 * 商品tabs头部
 */
export function areasProductTabsHeadersApi(params) {
  return request({
    url: `/admin/circle/product/tabs/headers`,
    method: 'get',
    params,
  });
}

/**
 * 商品评价列表
 * @param {object} params
 */
export function areasProductReplyListApi(params) {
  return request({
    url: `/admin/circle/product/reply/list`,
    method: 'get',
    params,
  });
}

// ==================== 圈层端商户控制器 ====================

/**
 * 商户详情
 * @param {number} id
 */
export function areasMerchantDetailApi(id) {
  return request({
    url: `/admin/circle/merchant/detail/${id}`,
    method: 'get',
  });
}

/**
 * 商户列表
 * @param {object} params
 */
export function areasMerchantListApi(params) {
  return request({
    url: `/admin/circle/merchant/list`,
    method: 'get',
    params,
  });
}

/**
 * 商户列表头部数量
 */
export function areasMerchantListHeaderNumApi(params) {
  return request({
    url: `/admin/circle/merchant/list/header/num`,
    method: 'get',
    params,
  });
}

/**
 * 获取商户类型列表
 */
export function areasMerchantTypeListApi() {
  return request({
    url: `/admin/circle/merchant/type/list`,
    method: 'get',
  });
}

/**
 * 获取商户分类列表
 */
export function areasMerchantCategoryListApi() {
  return request({
    url: `/admin/circle/merchant/category/list`,
    method: 'get',
  });
}

// ==================== 圈层端登录控制器 ====================

/**
 * 获取管理员信息通过token
 */
export function areasGetAdminInfoByTokenApi() {
  return request({
    url: `/admin/circle/getAdminInfoByToken`,
    method: 'get',
  });
}

/**
 * 获取登录图片
 */
export function areasGetLoginPicApi() {
  return request({
    url: `/admin/circle/getLoginPic`,
    method: 'get',
  });
}

/**
 * 获取菜单
 */
export function areasGetMenusApi() {
  return request({
    url: `/admin/circle/getMenus`,
    method: 'get',
  });
}

/**
 * 圈层端登录
 * @param {object} data
 */
export function areasLoginApi(data) {
  return request({
    url: `/admin/circle/login`,
    method: 'post',
    data,
  });
}

/**
 * 登录账号检测
 * @param {object} data
 */
export function areasLoginAccountDetectionApi(data) {
  return request({
    url: `/admin/circle/login/account/detection`,
    method: 'post',
    data,
  });
}

/**
 * 登录管理员更新
 * @param {object} data
 */
export function areasLoginAdminUpdateApi(data) {
  return request({
    url: `/admin/circle/login/admin/update`,
    method: 'post',
    data,
  });
}

/**
 * 登录管理员更新密码
 * @param {object} data
 */
export function areasLoginAdminUpdatePasswordApi(data) {
  return request({
    url: `/admin/circle/login/admin/update/password`,
    method: 'post',
    data,
  });
}

/**
 * 圈层端退出登录
 */
export function areasLogoutApi() {
  return request({
    url: `/admin/circle/logout`,
    method: 'get',
  });
}

// ==================== 圈层端菜单控制器 ====================

/**
 * 圈层结点菜单缓存树
 */
export function areasMenuNodeCacheTreeApi() {
  return request({
    url: `/admin/circle/menu/cache/tree`,
    method: 'get',
  });
}

/**
 * 圈层父级菜单缓存树
 */
export function areasMenuParentCacheTreeApi() {
  return request({
    url: `/admin/circle/menu/cache/tree`,
    method: 'get',
  });
}

// ==================== 圈层端订单控制器 ====================

/**
 * 订单详情
 * @param {object} params
 */
export function areasOrderInfoApi(params) {
  return request({
    url: `/admin/circle/order/info`,
    method: 'get',
    params,
  });
}

/**
 * 订单列表
 * @param {object} params
 */
export function areasOrderListApi(params) {
  return request({
    url: `/admin/circle/order/list`,
    method: 'get',
    params,
  });
}

/**
 * 订单状态数量
 */
export function areasOrderStatusNumApi(params) {
  return request({
    url: `/admin/circle/order/status/num`,
    method: 'get',
    params,
  });
}

// ==================== 圈层端退款订单控制器 ====================

/**
 * 退款订单详情
 * @param {string} refundOrderNo
 */
export function areasRefundOrderDetailApi(refundOrderNo) {
  return request({
    url: `/admin/circle/refund/order/detail/${refundOrderNo}`,
    method: 'get',
  });
}

/**
 * 退款订单列表
 * @param {object} params
 */
export function areasRefundOrderListApi(params) {
  return request({
    url: `/admin/circle/refund/order/list`,
    method: 'get',
    params,
  });
}

/**
 * 退款订单状态数量
 */
export function areasRefundOrderStatusNumApi(params) {
  return request({
    url: `/admin/circle/refund/order/status/num`,
    method: 'get',
    params,
  });
}

// =========== 圈层超管角色管理 =========

/**
 * 超管角色分页列表
 * @param {object} params
 */
export function platformCircleRoleListApi(params) {
  return request({
    url: `/admin/platform/circle/role/list`,
    method: 'get',
    params,
  });
}

/**
 * 新增角色
 * @param {object} data
 */
export function platformCircleRoleSaveApi(data) {
  return request({
    url: `/admin/platform/circle/role/save`,
    method: 'post',
    data,
  });
}

/**
 * 修改角色
 * @param {object} data
 */
export function platformCircleRoleUpdateApi(data) {
  return request({
    url: `/admin/platform/circle/role/update`,
    method: 'post',
    data,
  });
}

/**
 * 修改角色状态
 * @param {object} data
 */
export function platformCircleRoleUpdateStatusApi(data) {
  return request({
    url: `/admin/platform/circle/role/updateStatus`,
    method: 'post',
    data,
  });
}

/**
 * 删除角色
 * @param {number} id
 */
export function platformCircleRoleDeleteApi(id) {
  return request({
    url: `/admin/platform/circle/role/delete/${id}`,
    method: 'post',
  });
}

/**
 * 角色详情
 * @param {number} id
 */
export function platformCircleRoleInfoApi(id) {
  return request({
    url: `/admin/platform/circle/role/info/${id}`,
    method: 'get',
  });
}

/**
 * 圈层结点菜单缓存树
 */
export function platformCircleMenuNodeCacheTreeApi() {
  return request({
    url: `/admin/platform/circle/menu/cache/tree`,
    method: 'get',
  });
}

/**
 * 圈层结点菜单缓存树
 */
export function conjunctiveDiyApi(data) {
  return request({
    url: `/admin/platform/circle/conjunctive/diy`,
    method: 'post',
    data
  });
}