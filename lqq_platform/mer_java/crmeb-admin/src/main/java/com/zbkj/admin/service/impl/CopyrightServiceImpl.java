package com.zbkj.admin.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.admin.copyright.CopyrightInfoResponse;
import com.zbkj.admin.copyright.CopyrightUpdateInfoRequest;
import com.zbkj.admin.service.CopyrightService;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.utils.RestTemplateUtil;
import com.zbkj.service.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zbkj.common.utils.CrmebUtil;

import java.util.Map;

/**
 * 版权服务实现类
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
public class CopyrightServiceImpl implements CopyrightService {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CrmebConfig crmebConfig;
    @Autowired
    private RestTemplateUtil restTemplateUtil;

    /**
     * 获取版权信息
     */
    @Override
    public CopyrightInfoResponse getInfo() {
        CopyrightInfoResponse response = new CopyrightInfoResponse();
        String domainName = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_API_URL);
        if (StrUtil.isBlank(domainName)) {
            response.setStatus(-2);
            return response;
        }
        String label = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_LABEL);
        String version = crmebConfig.getVersion();
        if (StrUtil.isBlank(version)) {
            throw new CrmebException("请先在yml中配置版本号");
        }
        response.setDomainUrl(domainName);
        response.setLabel(Integer.parseInt(label));
        response.setVersion(version);
        JSONObject result = CrmebUtil.queryAuthCert(
                domainName,
                label,
                version
        );
        CrmebUtil.VAAAuth(domainName, label, version);
        JSONObject dataJson =  new JSONObject();
        if (result.getBooleanValue("success")) {
            dataJson = result.getJSONObject("data").getJSONObject("data");
        } else {
        }


        response.setStatus(dataJson.getInteger("status"));
        response.setCopyright(dataJson.getString("copyright"));
        if (!dataJson.getInteger("status").equals(1)) {
            return response;
        }
        response.setAuthCode(dataJson.getString("auth_code"));
        response.setCompanyName(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_INFO));
        response.setCompanyImage(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_IMAGE));
        return response;
    }

    /**
     * 编辑公司版权信息
     */
    @Override
    @Transactional
    public Boolean updateCompanyInfo(CopyrightUpdateInfoRequest request) {
        Boolean update = systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_INFO, request.getCompanyName());
        String path = StrUtil.isNotBlank(request.getCompanyImage()) ? request.getCompanyImage() : "";
        Boolean update1 = systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_IMAGE, path);
        return update && update1;
    }

    /**
     * 获取商户版权信息
     */
    @Override
    public String getCompanyInfo() {
        return systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_INFO);
    }
}
