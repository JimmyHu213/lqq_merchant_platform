package com.zbkj.front.service;

import com.zbkj.common.vo.BottomNavigationVo;

import java.util.List;

/**
 * 移动端商户服务
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/21
 */
public interface FrontMerchantService {

    /**
     * 获取商户底部导航信息
     */
    List<BottomNavigationVo> getBottomNavigationInfo(Integer merId);
}
