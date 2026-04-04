package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.page.PageDiy;
import com.zbkj.common.request.CommonSearchRequest;
import com.zbkj.common.request.FrontPageDiyRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.page.PageDiyEditNameRequest;
import com.zbkj.common.request.page.PageDiyRequest;
import com.zbkj.common.response.page.PageDiyDefaultResponse;
import com.zbkj.common.response.page.PageDiyResponse;

import java.util.List;
import java.util.Map;

/**
 * @author dazongzi
 * @description PageDiyService 接口
 * @date 2023-05-16
 */
public interface PageDiyService extends IService<PageDiy> {

    /**
     * 列表
     *
     * @return List<PageDiy>
     * @author dazongzi
     * @since 2023-05-16
     */
    PageInfo<PageDiy> findPlatPage(String name, PageParamRequest pageParamRequest);

    /**
     * 商户DIY列表
     */
    PageInfo<PageDiy> findMerchantPage(CommonSearchRequest request);

    /**
     * 添加DIY模板
     */
    PageDiy addPageDiy(PageDiyRequest request);

    /**
     * 编辑DIY模板
     */
    Boolean editPageDiy(PageDiyRequest request);

    /**
     * 编辑diy名称
     *
     * @param pageDiyEditNameRequest 待编辑名称对象
     */
    Boolean editPageDiyName(PageDiyEditNameRequest pageDiyEditNameRequest);

    /**
     * 设置DIY首页模版
     *
     * @param diyId 被设置为首页的diy模版id
     * @return 设置结果
     */
    Boolean setDiyDefault(Integer diyId);

    /**
     * 获取移动端DIY模板
     */
    PageDiyResponse getDiyPageByFront(FrontPageDiyRequest request);

    /**
     * 针对Admin
     * 根据id加载diy模版配置
     *
     * @param id 对应的模版id
     *           描述：
     *           id=0加载商城首页的，也就是setDiyPageHome后的数据，平台端会默认设置已经有首页的数据，也会预制
     *           id>1时加载对应的模版即可
     */
    PageDiyResponse getDiyPageByPageIdForAdmin(Integer id);

    /**
     * 获取DIY默认模板ID
     */
    Integer getDefaultId();

    /**
     * 保存商户diy模板
     */
    PageDiy saveMerchantPageDiy(PageDiyRequest request);

    /**
     * 编辑商户diy模板
     */
    Boolean editMerchantPageDiy(PageDiyRequest request);

    /**
     * 删除DIY模板
     */
    Boolean deletePageDiy(Integer id);

    /**
     * 获取商户端装修模板详情
     */
    PageDiyResponse getMerchantPageDiyInfo(Integer id);

    /**
     * 获取移动端商户装修默认模板
     */
    PageDiyResponse getMerchantPageDiyInfoByFront(Integer merId, Integer id);

    /**
     * 获取商户创建默认模板
     */
    PageDiy getMerchantDefDiy();

    /**
     * 获取已经设置的商城首页模版数据
     *
     * @return PageDiyDefaultResponse
     */
    PageDiyDefaultResponse getDefault();

    /**
     * 通过id列表获取名称map
     */
    Map<Integer, String> getNameMapByIdList(List<Integer> diyIdList);
}
