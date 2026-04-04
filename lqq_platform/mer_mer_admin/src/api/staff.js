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
 * 分页列表
 * @param
 */
export function employeeRoleList(pram) {
  const data = {
    page: pram.page,
    limit: pram.limit,
    keywords: pram.keywords,
    status: pram.status,
  };
  return request({
    url: '/admin/merchant/employee/list',
    method: 'get',
    params: data,
  });
}
/**
 * 删除
 * @param
 */
export function employeeDelRole(id) {
  const data = {
    id: id,
  };
  return request({
    url: 'admin/merchant/employee/delete',
    method: 'get',
    params: data,
  });
}
/**
 * 详情
 * @param
 */
export function employeeGetInfo(pram) {
  return request({
    url: `/admin/merchant/employee/info/${pram}`,
    method: 'GET',
  });
}
/**
 * 新增
 * @param
 */
export function employeeAddRole(pram) {
  const data = {
    avatar: pram.avatar,
    name: pram.name,
    phone: pram.phone,
    role: pram.role,
    status: pram.status,
    uid: pram.uid,
    id: pram.id,
  };
  return request({
    url: '/admin/merchant/employee/save',
    method: 'POST',
    data: data,
  });
}
/**
 * 修改
 * @param
 */
export function employeeUpdateRole(pram) {
  const data = {
    avatar: pram.avatar,
    name: pram.name,
    phone: pram.phone,
    role: pram.role,
    status: pram.status,
    uid: pram.uid,
    id: pram.id,
  };
  return request({
    url: '/admin/merchant/employee/update',
    method: 'post',
    data: data,
  });
}

/**
 * 员工服务分页列表
 * @param
 */
export function serviceStaffListApi(pram) {
  const data = {
    page: pram.page,
    limit: pram.limit,
    keywords: pram.keywords,
    status: pram.status,
  };
  return request({
    url: '/admin/merchant/service/staff/list',
    method: 'get',
    params: data,
  });
}
/**
 * 员工服务删除
 * @param
 */
export function serviceStaffDelRoleApi(id) {
  return request({
    url: `/admin/merchant/service/staff/delete/${id}`,
    method: 'post',
  });
}

/**
 * 员工服务新增
 * @param
 */
export function serviceStaffAddApi(pram) {
  const data = {
    idPhoto: pram.idPhoto,
    name: pram.name,
    phone: pram.phone,
    sort: pram.sort,
    status: pram.status,
    userId: pram.userId,
    id: pram.id,
  };
  return request({
    url: '/admin/merchant/service/staff/save',
    method: 'POST',
    data: data,
  });
}
/**
 * 员工服务修改
 * @param
 */
export function serviceStaffUpdateApi(pram) {
  const data = {
    idPhoto: pram.idPhoto,
    name: pram.name,
    phone: pram.phone,
    sort: pram.sort,
    status: pram.status,
    userId: pram.userId,
    id: pram.id,
  };
  return request({
    url: '/admin/merchant/service/staff/update',
    method: 'post',
    data: data,
  });
}

/**
 * 员工服务修改状态
 * @param
 */
export function serviceStaffStatusApi(id) {
  return request({
    url: `/admin/merchant/service/staff/status/${id}`,
    method: 'post',
  });
}