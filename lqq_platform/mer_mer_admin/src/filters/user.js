// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

//会员过滤器

/**
 * 等级
 */
export function levelFilter(status) {
  if (!status) {
    return '';
  }
  let arrayList = JSON.parse(localStorage.getItem('levelKey'));
  let array = arrayList.filter((item) => status === item.id);
  if (array.length) {
    return array[0].name;
  } else {
    return '';
  }
}

/**
 * 用户注册类型
 */
export function registerTypeFilter(status) {
  const statusMap = {
    wechat: '公众号',
    routine: '小程序',
    h5: 'H5',
    iosWx: '微信ios',
    androidWx: '微信安卓',
    ios: 'ios',
  };
  return statusMap[status];
}

/**
 * 用户类型
 */
export function filterIsPromoter(status) {
  const statusMap = {
    true: '推广员',
    false: '普通用户',
  };
  return statusMap[status];
}

//移动端主题色
export function filterTheme(status) {
  const statusMap = {
    0: '#e93323',
    1: '#fe5c2d',
    2: '#42ca4d',
    3: '#1ca5e9',
    4: '#ff448f',
  };
  return statusMap[status];
}

//移动端主色带透明度
export function filterThemeRgba(status) {
  const statusMap = {
    0: 'rgba(233, 51, 35, .05)',
    1: 'rgba(254, 92, 45, .05)',
    2: 'rgba(66, 202, 77, .05)',
    3: 'rgba(29, 176, 252, .05)',
    4: 'rgba(255, 69, 144, .05)',
  };
  return statusMap[status];
}

//移动端主色渐变色
export function filterLinearGradient(status) {
  const statusMap = {
    0: '90deg, #E93323 0%, #FF7931 100%',
    1: '90deg, #FE5C2D 0%, #FF7931 100%',
    2: '90deg, #42Ca4D 0%, #70E038 100%',
    3: '90deg, #1DB0FC 0%, #40D1F4 100%',
    4: '90deg, #FF448F 0%, #FF67AD 100%',
  };
  return statusMap[status];
}

//移动端主色渐变色
export function filterLightColor(status) {
  const statusMap = {
    0: '#FFF6F6',
    1: '#FEE0D2',
    2: '#DBF5D6',
    3: '#D1F1FB',
    4: '#FFD8E7',
  };
  return statusMap[status];
}