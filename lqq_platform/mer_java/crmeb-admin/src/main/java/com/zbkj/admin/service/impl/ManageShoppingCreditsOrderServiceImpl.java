package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.admin.request.ShoppingCreditsOrderSearchRequest;
import com.zbkj.admin.service.ManageShoppingCreditsOrderService;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.UserConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.ShoppingCreditsOrderPageResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.service.ShoppingCreditsOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 类的详细说明
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/5
 */
@Service
public class ManageShoppingCreditsOrderServiceImpl implements ManageShoppingCreditsOrderService {

    @Autowired
    private ShoppingCreditsOrderService shoppingCreditsOrderService;

    /**
     * 购物金订单分页列表
     */
    @Override
    public PageInfo<ShoppingCreditsOrderPageResponse> findPage(ShoppingCreditsOrderSearchRequest request) {
        HashMap<String, Object> map = new HashMap<>();
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();

        map.put("merId", admin.getMerId());
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            map.put("orderNo", URLUtil.decode(request.getOrderNo()));
        }
        if (StrUtil.isNotBlank(request.getPayType())) {
            map.put("payType", request.getPayType());
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(request.getDateLimit());
            if (StrUtil.isNotBlank(dateLimitUtilVo.getStartTime()) && StrUtil.isNotBlank(dateLimitUtilVo.getEndTime())) {
                //判断时间
                int compareDateResult = CrmebDateUtil.compareDate(dateLimitUtilVo.getEndTime(), dateLimitUtilVo.getStartTime(), DateConstants.DATE_FORMAT);
                if (compareDateResult == -1) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
                }
                map.put("startTime", dateLimitUtilVo.getStartTime());
                map.put("endTime", dateLimitUtilVo.getEndTime());
            }
        }
        if (StrUtil.isNotBlank(request.getSearchType()) && StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        Page<ShoppingCreditsOrderPageResponse> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<ShoppingCreditsOrderPageResponse> responseList = shoppingCreditsOrderService.findPageByMerchant(map);
        if (CollUtil.isEmpty(responseList)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        return CommonPage.copyPageInfo(page, responseList);
    }
}
