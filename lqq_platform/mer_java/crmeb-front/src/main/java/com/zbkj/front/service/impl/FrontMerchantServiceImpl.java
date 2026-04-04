package com.zbkj.front.service.impl;

import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.GroupConfigConstants;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.common.vo.BottomNavigationVo;
import com.zbkj.front.service.FrontMerchantService;
import com.zbkj.service.service.GroupConfigService;
import com.zbkj.service.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 移动端商户服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/21
 */
@Service
public class FrontMerchantServiceImpl implements FrontMerchantService {

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private GroupConfigService groupConfigService;

    /**
     * 获取商户底部导航信息
     */
    @Override
    public List<BottomNavigationVo> getBottomNavigationInfo(Integer merId) {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_BOTTOM_NAVIGATION, merId, Constants.SORT_ASC, Boolean.TRUE);
        return configList.stream().map(config -> {
            BottomNavigationVo vo = new BottomNavigationVo();
            vo.setName(config.getName());
            vo.setSelectedIconLinkUrl(config.getLinkUrl());
            vo.setUnselectedIconLinkUrl(config.getImageUrl());
            vo.setLinkUrl(config.getValue());
            vo.setSort(config.getSort());
            return vo;
        }).collect(Collectors.toList());
    }
}
