package com.zbkj.admin.service;

import com.zbkj.common.request.AjAdminLoginRequest;
import com.zbkj.common.request.LoginAdminUpdatePasswordRequest;
import com.zbkj.common.request.LoginAdminUpdateRequest;
import com.zbkj.common.response.AdminLoginPicResponse;
import com.zbkj.common.response.LoginAdminResponse;
import com.zbkj.common.response.MenusResponse;
import com.zbkj.common.response.SystemLoginResponse;

import java.util.List;

/**
 * 圈层端登录服务
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface CircleLoginService {


    Integer accountDetection(String account, Integer type);

    SystemLoginResponse circleLogin(AjAdminLoginRequest request, String ip);

    Boolean logout();

    LoginAdminResponse getInfoByToken();

    AdminLoginPicResponse getLoginPic();

    List<MenusResponse> getMenus();

    Boolean loginAdminUpdate(LoginAdminUpdateRequest request);

    Boolean loginAdminUpdatePwd(LoginAdminUpdatePasswordRequest request);
}
