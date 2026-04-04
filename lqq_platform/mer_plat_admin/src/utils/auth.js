// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import Cookies from 'js-cookie';
import { isPlatform } from '@/utils/settingMer';

const TokenKey = 'Java-platformToken';

export function getToken() {
  return isPlatform ? Cookies.get(TokenKey) : Cookies.get('circleToken');
}

export function setToken(token) {
  if (isPlatform) {
    return Cookies.set(TokenKey, token);
  } else {
    return Cookies.set('circleToken', token);
  }
}

export function removeToken() {
  if (isPlatform) {
    return Cookies.remove(TokenKey);
  } else {
    return Cookies.remove('circleToken');
  }
}
