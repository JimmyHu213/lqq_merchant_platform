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
 * 获取主题色
 */
export function getChangeColorApi() {
  return request({
    url: '/publicly/config/get/change/color',
    method: 'get',
  });
}

/**
 * 行为验证码二次校验
 */
export function safetyVerifyApi(data) {
  return request({
    url: '/publicly/safety/verify',
    method: 'post',
    data,
  });
}

/**
 * 文件上传
 */
export function uploadFileApi(data, params) {
  return request({
    url: '/publicly/upload/file',
    method: 'post',
    data,
    params,
  });
}

/**
 * 图片上传
 */
export function uploadImageApi(data, params) {
  return request({
    url: '/publicly/upload/image',
    method: 'post',
    data,
    params,
  });
}

/**
 * 微信推送地址验证
 */
export function wechatPublicCallbackWebHookGetApi(params) {
  return request({
    url: '/publicly/wechat/public/callback/webHook',
    method: 'get',
    params,
  });
}

/**
 * 接受微信推送过来的消息
 */
export function wechatPublicCallbackWebHookPostApi(data) {
  return request({
    url: '/publicly/wechat/public/callback/webHook',
    method: 'post',
    data,
  });
}

/**
 * 获取微信二维码
 */
export function wechatMiniGetQrcodeApi(data) {
  return request({
    url: '/publicly/wechat/mini/get/qrcode',
    method: 'post',
    data,
  });
}

/**
 * 微信小程序回调验证
 */
export function wechatMiniCallbackGetApi(params) {
  return request({
    url: '/publicly/wechat/mini/callback/index',
    method: 'get',
    params,
  });
}

/**
 * 小程序回调
 */
export function wechatMiniCallbackPostApi(data) {
  return request({
    url: '/publicly/wechat/mini/callback/index',
    method: 'post',
    data,
  });
}

/**
 * 小程序回调-自己模拟
 */
export function wechatMiniCallbackTestApi(data) {
  return request({
    url: '/publicly/wechat/mini/callback/test',
    method: 'post',
    data,
  });
}

/**
 * 微信小程序-新增临时素材-本地
 */
export function wechatMaterialMaMediaUploadLocalhostApi(data) {
  return request({
    url: '/publicly/wechat/material/ma/media/upload/localhost',
    method: 'post',
    data,
  });
}

/**
 * 微信公众号-新增其他类型永久素材
 */
export function wechatMaterialMpMaterialAddMaterialApi(data, params) {
  return request({
    url: '/publicly/wechat/material/mp/material/add/material',
    method: 'post',
    data,
    params,
  });
}

/**
 * 支付宝支付回调
 */
export function paymentCallbackAlipayApi(data) {
  return request({
    url: '/publicly/payment/callback/alipay',
    method: 'post',
    data,
  });
}

/**
 * 微信APP支付回调
 */
export function paymentCallbackWechatAppApi(data) {
  return request({
    url: '/publicly/payment/callback/wechat/app',
    method: 'post',
    data,
  });
}

/**
 * 微信小程序支付回调
 */
export function paymentCallbackWechatMaApi(data) {
  return request({
    url: '/publicly/payment/callback/wechat/ma',
    method: 'post',
    data,
  });
}

/**
 * 微信公众号支付回调
 */
export function paymentCallbackWechatMpApi(data) {
  return request({
    url: '/publicly/payment/callback/wechat/mp',
    method: 'post',
    data,
  });
}

/**
 * 微信APP退款回调
 */
export function paymentCallbackWechatRefundAppApi(data) {
  return request({
    url: '/publicly/payment/callback/wechat/refund/app',
    method: 'post',
    data,
  });
}

/**
 * 微信小程序退款回调
 */
export function paymentCallbackWechatRefundMaApi(data) {
  return request({
    url: '/publicly/payment/callback/wechat/refund/ma',
    method: 'post',
    data,
  });
}

/**
 * 微信公众号退款回调
 */
export function paymentCallbackWechatRefundMpApi(data) {
  return request({
    url: '/publicly/payment/callback/wechat/refund/mp',
    method: 'post',
    data,
  });
}

/**
 * 通过key删除缓存
 */
export function cacheDeleteApi(key) {
  return request({
    url: `/publicly/cache/delete/${key}`,
    method: 'get',
  });
}

/**
 * 获取验证码
 */
export function validateCodeGetApi() {
  return request({
    url: '/publicly/validate/code/get',
    method: 'get',
  });
}