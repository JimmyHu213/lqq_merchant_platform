package com.zbkj.service.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.zbkj.common.vo.WeChatMiniAuthorizeVo;
import com.zbkj.service.service.WechatService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 5.1 微信小程序登录全链路测试
 * Mock WxMaService → 验证 openid/unionid/sessionKey 正确传递
 */
@ExtendWith(MockitoExtension.class)
public class WechatLoginTest {

    @InjectMocks
    private WechatServiceImpl wechatService;

    @Mock
    private WxMaService wxMaService;

    @Mock
    private WxMaUserService wxMaUserService;

    @BeforeEach
    void setUp() {
        when(wxMaService.getUserService()).thenReturn(wxMaUserService);
    }

    @Test
    @DisplayName("5.1.1 正常登录 — code 换取 openid+sessionKey 成功")
    void testMiniAuthCode_success() throws WxErrorException {
        // 准备: 模拟微信返回
        WxMaJscode2SessionResult sessionResult = new WxMaJscode2SessionResult();
        sessionResult.setOpenid("mock_openid_123");
        sessionResult.setUnionid("mock_unionid_456");
        sessionResult.setSessionKey("mock_session_key_789");

        when(wxMaUserService.getSessionInfo("test_code")).thenReturn(sessionResult);

        // 执行
        WeChatMiniAuthorizeVo result = wechatService.miniAuthCode("test_code");

        // 验证
        assertNotNull(result);
        assertEquals("mock_openid_123", result.getOpenId());
        assertEquals("mock_unionid_456", result.getUnionId());
        assertEquals("mock_session_key_789", result.getSessionKey());

        verify(wxMaUserService).getSessionInfo("test_code");
    }

    @Test
    @DisplayName("5.1.2 登录失败 — 微信返回错误时抛出异常")
    void testMiniAuthCode_wxError() throws WxErrorException {
        // 准备: 模拟微信报错
        when(wxMaUserService.getSessionInfo("invalid_code"))
                .thenThrow(new WxErrorException("invalid code"));

        // 执行 + 验证: 应抛出 CrmebException
        assertThrows(Exception.class, () -> wechatService.miniAuthCode("invalid_code"));
    }

    @Test
    @DisplayName("5.1.3 登录无 unionid — unionid 为空时不报错")
    void testMiniAuthCode_noUnionId() throws WxErrorException {
        WxMaJscode2SessionResult sessionResult = new WxMaJscode2SessionResult();
        sessionResult.setOpenid("mock_openid_only");
        sessionResult.setUnionid(null);
        sessionResult.setSessionKey("mock_key");

        when(wxMaUserService.getSessionInfo("code_no_union")).thenReturn(sessionResult);

        WeChatMiniAuthorizeVo result = wechatService.miniAuthCode("code_no_union");

        assertNotNull(result);
        assertEquals("mock_openid_only", result.getOpenId());
        assertNull(result.getUnionId());
    }
}
