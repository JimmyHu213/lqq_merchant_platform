package com.zbkj.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.anji.captcha.model.common.ResponseModel;
import com.zbkj.admin.filter.TokenComponent;
import com.zbkj.admin.service.CircleLoginService;
import com.zbkj.common.constants.RedisConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.enums.RoleEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.admin.SystemPermissions;
import com.zbkj.common.model.circle.CircleAdmin;
import com.zbkj.common.model.circle.CircleMenu;
import com.zbkj.common.request.AjAdminLoginRequest;
import com.zbkj.common.request.LoginAdminUpdatePasswordRequest;
import com.zbkj.common.request.LoginAdminUpdateRequest;
import com.zbkj.common.response.AdminLoginPicResponse;
import com.zbkj.common.response.LoginAdminResponse;
import com.zbkj.common.response.MenusResponse;
import com.zbkj.common.response.SystemLoginResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.LoginUserVo;
import com.zbkj.common.vo.MenuTree;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 圈层端登录服务实现类
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
@Service
public class CircleLoginServiceImpl implements CircleLoginService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private TokenComponent tokenComponent;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private CircleAdminService circleAdminService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private CircleMenuService circleMenuService;

    @Autowired
    private AsyncService asyncService;

    private MerchantService merchantService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SafetyService safetyService;


    @Override
    public Integer accountDetection(String account, Integer type) {
        CircleAdmin admin = circleAdminService.selectUserByAccountAndType(account, type);
        if (ObjectUtil.isNull(admin)) {
            return 0;
        }
        String key = StrUtil.format(RedisConstants.CIRCLE_ACCOUNT_LOGIN_ERROR_NUM_KEY, account);
        if (!redisUtil.exists(key)) {
            return 0;
        }
        return redisUtil.get(key);
    }

    @Override
    public SystemLoginResponse circleLogin(AjAdminLoginRequest request, String ip) {
        Integer adminType = RoleEnum.CIRCLE_ADMIN.getValue();
        Integer errorNum = accountDetection(request.getAccount(), adminType);
        if (errorNum > 3) {
            if (ObjectUtil.isNull(request.getCaptchaVO())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "验证码信息不存在");
            }
            // 校验验证码
            ResponseModel responseModel = safetyService.verifySafetyCode(request.getCaptchaVO());
            if (!responseModel.getRepCode().equals("0000")) {
                logger.error("验证码登录失败，repCode = {}, repMsg = {}", responseModel.getRepCode(), responseModel.getRepMsg());
                accountErrorNumAdd(request.getAccount());
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "验证码校验失败");
            }
        }
        // 用户验证
        Authentication authentication = null;
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        try {
            String principal = request.getAccount() + adminType;
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(principal, request.getPwd()));
        } catch (AuthenticationException e) {
            accountErrorNumAdd(request.getAccount());
            if (e instanceof BadCredentialsException) {
                throw new CrmebException("用户不存在或密码错误");
            }
            throw new CrmebException(e.getMessage());
        } catch (CrmebException e) {
            accountErrorNumAdd(request.getAccount());
            throw new CrmebException("账号或者密码不正确");
        }
        LoginUserVo loginUser = (LoginUserVo) authentication.getPrincipal();
        SystemAdmin systemAdmin = loginUser.getUser();
        String token = tokenComponent.createToken(loginUser);

        SystemLoginResponse response = adminToLoginResponse(token, systemAdmin, adminType, ip);
        accountErrorNumClear(request.getAccount());
        return response;
    }

    private SystemLoginResponse adminToLoginResponse(String token, SystemAdmin systemAdmin, Integer adminType, String ip) {
        SystemLoginResponse systemAdminResponse = new SystemLoginResponse();
        systemAdminResponse.setToken(token);
        BeanUtils.copyProperties(systemAdmin, systemAdminResponse);
        //更新最后登录信息
        systemAdmin.setUpdateTime(DateUtil.date());
        systemAdmin.setLoginCount(systemAdmin.getLoginCount() + 1);
        systemAdmin.setLastIp(ip);
        CircleAdmin circleAdmin = new CircleAdmin();
        BeanUtils.copyProperties(systemAdmin, circleAdmin);
        circleAdminService.updateById(circleAdmin);
        // 返回后台LOGO图标
        systemAdminResponse.setLeftTopLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LOGO_LEFT_TOP));
        systemAdminResponse.setLeftSquareLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_SITE_LOGO_SQUARE));
        return systemAdminResponse;
    }


    @Override
    public Boolean logout() {
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        if (ObjectUtil.isNotNull(loginUserVo)) {
            // 删除用户缓存记录
            tokenComponent.delLoginUser(loginUserVo);
        }
        return true;
    }

    @Override
    public LoginAdminResponse getInfoByToken() {
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        SystemAdmin systemAdmin = loginUserVo.getUser();
        LoginAdminResponse loginAdminResponse = new LoginAdminResponse();
        BeanUtils.copyProperties(systemAdmin, loginAdminResponse);
        // 组装管理员类型
        if (systemAdmin.getType().equals(RoleEnum.CIRCLE_ADMIN.getValue()) || systemAdmin.getType().equals(RoleEnum.CIRCLE_SUPER.getValue())) {
            loginAdminResponse.setType(RoleEnum.CIRCLE_ADMIN.getValue());
        }
        List<String> roleList = Stream.of(systemAdmin.getRoles().split(",")).collect(Collectors.toList());
        List<String> permList = loginUserVo.getPermissions().stream().map(SystemPermissions::getPath).collect(Collectors.toList());

        loginAdminResponse.setPermissionsList(permList);
        return loginAdminResponse;
    }

    @Override
    public AdminLoginPicResponse getLoginPic() {
        AdminLoginPicResponse loginPicResponse = new AdminLoginPicResponse();
        loginPicResponse.setBackgroundImage(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_BACKGROUND_IMAGE));
        loginPicResponse.setLoginLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LOGO_LOGIN));
        loginPicResponse.setLeftLogo(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_LOGIN_LEFT_LOGO));
        loginPicResponse.setSiteName(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_SITE_NAME));
        return loginPicResponse;
    }

    @Override
    public List<MenusResponse> getMenus() {
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        List<String> roleList = Stream.of(loginUserVo.getUser().getRoles().split(",")).collect(Collectors.toList());
        List<CircleMenu> menuList = circleMenuService.getMenusByUserId(loginUserVo.getUser().getId());
        // 组装前端对象
        List<MenusResponse> responseList = menuList.stream().map(e -> {
            MenusResponse response = new MenusResponse();
            BeanUtils.copyProperties(e, response);
            // 针对管理端UI修改Response数据
            response.setPath(e.getComponent());
            response.setTitle(e.getName());
            return response;
        }).collect(Collectors.toList());

        MenuTree menuTree = new MenuTree(responseList);
        return menuTree.buildTree();
    }

    @Override
    public Boolean loginAdminUpdate(LoginAdminUpdateRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        CircleAdmin circleAdmin = new CircleAdmin();
        circleAdmin.setId(admin.getId());
        circleAdmin.setRealName(request.getRealName());
        circleAdmin.setUpdateTime(DateUtil.date());
        return circleAdminService.updateById(circleAdmin);
    }

    @Override
    public Boolean loginAdminUpdatePwd(LoginAdminUpdatePasswordRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        CircleAdmin circleAdmin = circleAdminService.getById(admin.getId());
        String encryptPassword = CrmebUtil.encryptPassword(request.getOldPassword(), circleAdmin.getAccount());
        if (!circleAdmin.getPwd().equals(encryptPassword)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "原密码不正确");
        }
        CircleAdmin newAdmin = new CircleAdmin();
        newAdmin.setId(admin.getId());
        String pwd = CrmebUtil.encryptPassword(request.getPassword(), admin.getAccount());
        newAdmin.setPwd(pwd);
        newAdmin.setUpdateTime(DateUtil.date());
        return circleAdminService.updateById(newAdmin);
    }

    private void accountErrorNumAdd(String account) {
        redisUtil.incr(StrUtil.format(RedisConstants.CIRCLE_ACCOUNT_LOGIN_ERROR_NUM_KEY, account), 1);
    }
    private void accountErrorNumClear(String account) {
        String key = StrUtil.format(RedisConstants.CIRCLE_ACCOUNT_LOGIN_ERROR_NUM_KEY, account);
        if (redisUtil.exists(key)) {
            redisUtil.delete(StrUtil.format(RedisConstants.CIRCLE_ACCOUNT_LOGIN_ERROR_NUM_KEY, account));
        }
    }
}
