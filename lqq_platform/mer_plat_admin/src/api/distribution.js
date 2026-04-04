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
 * @description 分销设置 -- 详情
 */
export function configApi() {
  return request({
    url: '/admin/platform/retail/store/config/get',
    method: 'get',
  });
}

/**
 * @description 分销设置 -- 表单提交
 */
export function configUpdateApi(data) {
  return request({
    url: '/admin/platform/retail/store/config/save',
    method: 'post',
    data,
  });
}

/**
 * @description 分销员 -- 列表
 */
export function promoterListApi(params) {
  return request({
    url: '/admin/platform/retail/store/people/list',
    method: 'get',
    params,
  });
}

/**
 * @description 根据条件获取下级推广用户列表
 */
export function spreadListApi(params) {
  return request({
    url: '/admin/platform/retail/store/sub/user/list',
    method: 'get',
    params,
  });
}

/**
 * @description 推广人订单 -- 列表
 */
export function spreadOrderListApi(params) {
  return request({
    url: '/admin/platform/retail/store/promotion/order/list',
    method: 'get',
    params,
  });
}

/**
 * @description 推广人 -- 清除上级推广人
 */
export function spreadClearApi(id) {
  return request({
    url: `/admin/platform/retail/store/clean/spread/${id}`,
    method: 'get',
  });
}

/**
 * 导出分销员Excel
 * @param params 对象
 */
export function peopleExcelApi(params) {
  return request({
    url: `/admin/platform/export/retail/store/people/excel`,
    method: 'get',
    params,
  });
}

/**
 *  分销基础配置 -- 信息获取
 */
export function getBaseConfigApi() {
  return request({
    url: `/admin/platform/retail/store/base/config/get`,
    method: 'get',
  });
}

/**
 * @description 分销基础配置 -- 信息保存
 */
export function saveBaseConfigApi(data) {
  return request({
    url: '/admin/platform/retail/store/base/config/save',
    method: 'post',
    data,
  });
}

/**
 *  分销提现配置 -- 信息获取
 */
export function getExtractConfigApi() {
  return request({
    url: `/admin/platform/retail/store/extract/config/get`,
    method: 'get',
  });
}

/**
 * @description 分销提现配置 -- 信息保存
 */
export function saveExtractConfigApi(data) {
  return request({
    url: '/admin/platform/retail/store/extract/config/save',
    method: 'post',
    data,
  });
}