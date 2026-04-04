package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.constants.UploadConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.circle.Circle;
import com.zbkj.common.model.page.PageDiy;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CommonSearchRequest;
import com.zbkj.common.request.FrontPageDiyRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.page.PageDiyEditNameRequest;
import com.zbkj.common.request.page.PageDiyRequest;
import com.zbkj.common.response.page.PageDiyDefaultResponse;
import com.zbkj.common.response.page.PageDiyResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.SystemConfigResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.page.PageDiyDao;
import com.zbkj.service.service.CircleService;
import com.zbkj.service.service.PageDiyService;
import com.zbkj.service.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dazongzi
 * @description PageDiyServiceImpl 接口实现
 * @date 2023-05-16
 */
@Service
public class PageDiyServiceImpl extends ServiceImpl<PageDiyDao, PageDiy> implements PageDiyService {

    private static final Logger logger = LoggerFactory.getLogger(PageDiyServiceImpl.class);

    @Resource
    private PageDiyDao dao;

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private CircleService circleService;

    /**
     * 列表
     *
     * @return List<PageDiy>
     * @author dazongzi
     * @since 2023-05-16
     */
    @Override
    public PageInfo<PageDiy> findPlatPage(String name, PageParamRequest pageParamRequest) {
        Page<PageDiy> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<PageDiy> pageDiyList = findList(name, 0);
        if (CollUtil.isNotEmpty(pageDiyList)) {
            List<Integer> diyIdList = pageDiyList.stream().map(PageDiy::getId).collect(Collectors.toList());
            Map<Integer, String> map = circleService.getCircleNameMapByDiyIdList(diyIdList);
            pageDiyList.forEach(e -> {
                if (StrUtil.isNotBlank(map.get(e.getId()))) {
                    e.setCircleNames(map.get(e.getId()));
                } else {
                    e.setCircleNames("-");
                }
            });
        }
        return CommonPage.copyPageInfo(page, pageDiyList);
    }

    /**
     * 商户DIY列表
     */
    @Override
    public PageInfo<PageDiy> findMerchantPage(CommonSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Page<PageDiy> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<PageDiy> pageDiyList = findList(request.getKeywords(), systemAdmin.getMerId());
        return CommonPage.copyPageInfo(page, pageDiyList);
    }

    private List<PageDiy> findList(String keywords, Integer merId) {
        LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
        // 列表查询时忽略掉value字段，以免影响查询速度
        lqw.select(PageDiy.class, i -> !i.getColumn().equals("value"));
        if (StrUtil.isNotBlank(keywords)) {
            lqw.like(PageDiy::getName, URLUtil.decode(keywords));
        }
        lqw.eq(PageDiy::getMerId, merId);
        lqw.eq(PageDiy::getIsDel, 0);
        lqw.orderByDesc(PageDiy::getIsDefault, PageDiy::getId);
        return dao.selectList(lqw);
    }

    /**
     * 添加DIY模板
     */
    @Override
    public PageDiy addPageDiy(PageDiyRequest request) {
        // 检查diy模版名称唯一
        checkPageDiyNameUnique(request.getName(), 0, 0);
//        String value = JSON.toJSONString(request.getValue());
//        DocumentContext jsonContext = JsonPath.parse(value);
//        String adminApiPath = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_API_URL);
//        if (StrUtil.isBlank(adminApiPath)) {
//            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "应用设置中 微信小程序数据配置 或者 支付回调地址以及网站地址 配置不全");
//        }
        // 通配符去掉关键子 再存储
//        List<Map<String, Object>> fields = jsonContext.read("$.fields[?(@.value contains '" + adminApiPath + "')]");
//        fields.forEach(field -> {
//            String oldValue = (String) field.get("value");
//            field.put("value", oldValue.replace(adminApiPath, ""));
//        });

        //
        ;

        PageDiy pageDiy = new PageDiy();
        BeanUtils.copyProperties(request, pageDiy);
        pageDiy.setName(URLUtil.decode(request.getName()));
        pageDiy.setId(null);
        pageDiy.setMerId(0);
        pageDiy.setValue(sortWithSerializerFeature(request.getValue()).toJSONString());
//        pageDiy.setValue(jsonContext.jsonString());
        boolean save = save(pageDiy);
        if (!save) throw new CrmebException(CommonResultCode.ERROR, "DIY模板保存失败");
        return pageDiy;
    }

    /**
     * 处理value顺序
     */
    private static JSONObject sortWithSerializerFeature(JSONObject jsonObject) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return new JSONObject();
        }
        // 先将JSONObject转换为有序的LinkedHashMap
        Map<String, Object> map = new LinkedHashMap<>();
        jsonObject.keySet().stream()
                .sorted(Comparator.comparingLong(Long::parseLong))
                .forEach(key -> map.put(key, jsonObject.get(key)));
        return new JSONObject(map); // 创建有序JSONObject
    }

    /**
     * 编辑DIY模板
     */
    @Override
    public Boolean editPageDiy(PageDiyRequest request) {
        if (ObjectUtil.isNull(request.getId()))
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择DIY模板");
        PageDiy oldPageDiy = getByIdException(request.getId());
        if (!oldPageDiy.getMerId().equals(0)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前DIY模版不存在");
        }
        String name = URLUtil.decode(request.getName());
        if (!oldPageDiy.getName().equals(name)) {
            // 检查diy模版名称唯一
            checkPageDiyNameUnique(request.getName(), request.getId(), 0);
        }
        PageDiy pageDiy = new PageDiy();
        BeanUtils.copyProperties(request, pageDiy);
//        DocumentContext jsonContext = JsonPath.parse(JSON.toJSONString(request.getValue()));
        pageDiy.setName(name);
        pageDiy.setValue(sortWithSerializerFeature(request.getValue()).toJSONString());
        pageDiy.setMerId(0);
        pageDiy.setUpdateTime(DateUtil.date());
        return dao.updateById(pageDiy) > 0;
    }

    /**
     * 编辑diy名称
     *
     * @param pageDiyEditNameRequest 待编辑名称对象
     */
    @Override
    public Boolean editPageDiyName(PageDiyEditNameRequest pageDiyEditNameRequest) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        PageDiy pageDiy = getByIdException(pageDiyEditNameRequest.getId());
        if (!pageDiy.getMerId().equals(admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前DIY模版不存在");
        }
        String name = URLUtil.decode(pageDiyEditNameRequest.getName());
        if (!pageDiy.getName().equals(name)) {
            // 检查diy模版名称唯一
            checkPageDiyNameUnique(name, pageDiyEditNameRequest.getId(), admin.getMerId());
        }
        LambdaUpdateWrapper<PageDiy> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(PageDiy::getName, name);
        wrapper.eq(PageDiy::getId, pageDiyEditNameRequest.getId());
        return update(wrapper);
    }

    /**
     * 设置DIY首页模版
     * isDefault =1 是平台首页
     *
     * @param diyId 被设置为首页的diy模版id
     * @return 设置结果
     */
    @Override
    public Boolean setDiyDefault(Integer diyId) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        PageDiy pageDiy = getByIdException(diyId);
        if (!admin.getMerId().equals(pageDiy.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前DIY模版不存在");
        }
        return transactionTemplate.execute(e -> {
            clearDiyDefault(admin.getMerId());
            LambdaUpdateWrapper<PageDiy> wrapper = Wrappers.lambdaUpdate();
            wrapper.set(PageDiy::getIsDefault, 1);
            wrapper.eq(PageDiy::getMerId, admin.getMerId());
            wrapper.eq(PageDiy::getId, diyId);
            update(wrapper);
            return Boolean.TRUE;
        });
    }

    /**
     * 清除DIY默认模板
     *
     * @param merId 商户id
     */
    private void clearDiyDefault(Integer merId) {
        LambdaUpdateWrapper<PageDiy> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(PageDiy::getIsDefault, 0);
        wrapper.eq(PageDiy::getMerId, merId);
        wrapper.eq(PageDiy::getIsDefault, 1);
        update(wrapper);
    }

    private PageDiy getByIdException(Integer id) {
        PageDiy pageDiy = getById(id);
        if (ObjectUtil.isNull(pageDiy) || pageDiy.getIsDel() > 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前DIY模版不存在");
        }
        return pageDiy;
    }

    /**
     * 针对Admin
     * 根据id加载diy模版配置
     */
    @Override
    public PageDiyResponse getDiyPageByPageIdForAdmin(Integer id) {
        PageDiy pageDiy;
        if (0 == id) {
            LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
            lqw.eq(PageDiy::getIsDefault, 1);
            lqw.eq(PageDiy::getMerId, 0);
            pageDiy = getOne(lqw);
        } else {
            pageDiy = getByIdException(id);
        }
        if (ObjectUtil.isNull(pageDiy)) throw new CrmebException(SystemConfigResultCode.PAGE_DIY_NOT_EXIST);
//        String modifiedJsonString = getModifiedJsonString(pageDiy.getValue());
//        pageDiy.setValue(modifiedJsonString);
        PageDiyResponse response = new PageDiyResponse();
        BeanUtils.copyProperties(pageDiy, response);
        response.setValue(JSON.parseObject(pageDiy.getValue()));
        return response;
    }

    /**
     * 获取DIY默认模板ID
     */
    @Override
    public Integer getDefaultId() {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
        lqw.select(PageDiy::getId);
        lqw.eq(PageDiy::getMerId, admin.getMerId());
        lqw.eq(PageDiy::getIsDefault, 1);
        lqw.eq(PageDiy::getIsDel, 0);
        lqw.last(" limit 1");
        PageDiy pageDiy = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(pageDiy) ? pageDiy.getId() : 0;
    }

    /**
     * 保存商户diy模板
     */
    @Override
    public PageDiy saveMerchantPageDiy(PageDiyRequest request) {
        String adminApiPath = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_API_URL);
        if (StrUtil.isBlank(adminApiPath)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "应用设置中 微信小程序数据配置 或者 支付回调地址以及网站地址 配置不全");
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        // 检查diy模版名称唯一
        checkPageDiyNameUnique(request.getName(), 0, admin.getMerId());
        PageDiy pageDiy = new PageDiy();
        BeanUtils.copyProperties(request, pageDiy);
        pageDiy.setId(null);
        pageDiy.setValue(JSON.toJSONString(request.getValue()));
        pageDiy.setMerId(admin.getMerId());
        pageDiy.setIsDefault((ObjectUtil.isNotNull(request.getIsDefault()) && request.getIsDefault() > 0) ? 1 : 0);
        DocumentContext jsonContext = JsonPath.parse(pageDiy.getValue());
        // 通配符去掉关键子 再存储
        jsonContext.delete("$..*[?(@ == '" + adminApiPath + "')]");
        pageDiy.setValue(jsonContext.jsonString());
        boolean save = save(pageDiy);
        if (!save) {
            throw new CrmebException(CommonResultCode.ERROR, "保存diy模板失败");
        }
        return pageDiy;
    }

    /**
     * 编辑商户diy模板
     */
    @Override
    public Boolean editMerchantPageDiy(PageDiyRequest request) {
        String adminApiPath = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_API_URL);
        if (StrUtil.isBlank(adminApiPath)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "应用设置中 微信小程序数据配置 或者 支付回调地址以及网站地址 配置不全");
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        PageDiy tempPageDiy = getByIdException(request.getId());
        if (!tempPageDiy.getMerId().equals(admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前DIY模版不存在");
        }
        // 检查diy模版名称唯一
        checkPageDiyNameUnique(request.getName(), tempPageDiy.getId(), admin.getMerId());
        PageDiy pageDiy = new PageDiy();
        BeanUtils.copyProperties(request, pageDiy);
        pageDiy.setValue(JSON.toJSONString(request.getValue()));
        DocumentContext jsonContext = JsonPath.parse(pageDiy.getValue());
        // 通配符去掉关键子 再存储
        jsonContext.delete("$..*[?(@ == '" + adminApiPath + "')]");
        pageDiy.setValue(jsonContext.jsonString());
        pageDiy.setUpdateTime(DateUtil.date());
        return dao.updateById(pageDiy) > 0;
    }

    /**
     * 删除DIY模板
     */
    @Override
    public Boolean deletePageDiy(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        PageDiy pageDiy = getByIdException(id);
        if (!pageDiy.getMerId().equals(admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前DIY模版不存在");
        }
        return removeById(id);
    }

    /**
     * 获取商户端装修模板详情
     */
    @Override
    public PageDiyResponse getMerchantPageDiyInfo(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        PageDiy pageDiy;
        if (0 == id) {
            LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
            lqw.eq(PageDiy::getIsDefault, 1);
            lqw.eq(PageDiy::getMerId, admin.getMerId());
            pageDiy = getOne(lqw);
        } else {
            pageDiy = getByIdException(id);
        }
        if (ObjectUtil.isNull(pageDiy) || !pageDiy.getMerId().equals(admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前DIY模版不存在");
        }
        String modifiedJsonString = getModifiedJsonString(pageDiy.getValue());
        pageDiy.setValue(modifiedJsonString);
        PageDiyResponse response = new PageDiyResponse();
        BeanUtils.copyProperties(pageDiy, response);
        response.setValue(JSON.parseObject(pageDiy.getValue()));
        return response;

    }

    /**
     * 获取移动端商户装修默认模板
     */
    @Override
    public PageDiyResponse getMerchantPageDiyInfoByFront(Integer merId, Integer id) {
        PageDiy pageDiy;
        if (ObjectUtil.isNotNull(id) && id > 0) {
            pageDiy = getDetailByMerId(id, merId);
        } else {
            pageDiy = getDefaultByMerId(merId);
        }
        if (ObjectUtil.isNull(pageDiy)) {
            throw new CrmebException(SystemConfigResultCode.PAGE_DIY_NOT_EXIST);
        }

        // 优化front比必要的字段 提高diy组件渲染效率
        DocumentContext jsonContext = JsonPath.parse(pageDiy.getValue());
        jsonContext.delete("$..*[?(@.name == 'goodList')].itemStyle.list");
        jsonContext.delete("$..*[?(@.name == 'goodList')].tabConfig.list");
        jsonContext.delete("$..*[?(@.name == 'goodList')].typeConfig.list");
//        jsonContext.delete("$..*[?(@.name == 'goodList')].selectConfig");
        jsonContext.delete("$..*[?(@.name == 'goodList')].goodsSort.list");
        jsonContext.delete("$..*[?(@.name == 'goodList')].goodsList.list[*].attrValue");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].activeValueBrand");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].goodsList");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].selectConfig");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].productList..*.goods[*].attrValue");

        PageDiyResponse response = new PageDiyResponse();
        BeanUtils.copyProperties(pageDiy, response);
//        String modifiedJsonString = getModifiedJsonString(jsonContext.jsonString());
        response.setValue(JSON.parseObject(jsonContext.jsonString()));
        return response;
    }

    /**
     * 获取商户创建默认模板
     */
    @Override
    public PageDiy getMerchantDefDiy() {
        LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
        lqw.eq(PageDiy::getMerId, -9);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取已经设置的商城首页模版数据
     *
     * @return PageDiyDefaultResponse
     */
    @Override
    public PageDiyDefaultResponse getDefault() {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
        lqw.select(PageDiy::getId);
        lqw.eq(PageDiy::getMerId, admin.getMerId());
        lqw.eq(PageDiy::getIsDefault, 1);
        lqw.eq(PageDiy::getIsDel, 0);
        lqw.last(" limit 1");
        PageDiy pageDiy = dao.selectOne(lqw);
        PageDiyDefaultResponse response = new PageDiyDefaultResponse();
        if (ObjectUtil.isNull(pageDiy)) {
            throw new CrmebException("当前商城首页未设置或数据有误");
        }
        response.setId(pageDiy.getId());
        response.setType("homePage");
        return response;
    }

    /**
     * 通过id列表获取名称map
     */
    @Override
    public Map<Integer, String> getNameMapByIdList(List<Integer> diyIdList) {
        if (CollUtil.isEmpty(diyIdList)) {
            return new HashMap<>();
        }
        LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
        lqw.in(PageDiy::getId, diyIdList);
        lqw.eq(PageDiy::getIsDel, 0);
        List<PageDiy> diyList = dao.selectList(lqw);
        if (CollUtil.isEmpty(diyList)) {
            return new HashMap<>();
        }
        return diyList.stream().collect(Collectors.toMap(PageDiy::getId, PageDiy::getName));
    }

    private PageDiy getDetailByMerId(Integer id, Integer merId) {
        LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
        lqw.eq(PageDiy::getId, id);
        lqw.eq(PageDiy::getMerId, merId);
        lqw.eq(PageDiy::getIsDel, 0);
        return dao.selectOne(lqw);
    }

    private PageDiy getDefaultByMerId(Integer merId) {
        LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
        lqw.eq(PageDiy::getMerId, merId);
        lqw.eq(PageDiy::getIsDefault, 1);
        lqw.eq(PageDiy::getIsDel, 0);
        return dao.selectOne(lqw);
    }

    /**
     * 获取移动端DIY模板
     */
    @Override
    public PageDiyResponse getDiyPageByFront(FrontPageDiyRequest request) {
        List<Integer> circleIdList = new ArrayList<>();
        if (StrUtil.isNotBlank(request.getCircleIds())) {
            circleIdList = CrmebUtil.stringToArray(request.getCircleIds());
        }
        if (ObjectUtil.isNull(request.getId())) request.setId(0);

        PageDiy pageDiy;
        if (StrUtil.isBlank(request.getCircleIds()) || request.getCircleIds().equals("0") || circleIdList.size() > 1) {
            if (request.getId() > 0) {
                pageDiy = getByIdException(request.getId());
            } else {
                LambdaQueryWrapper<PageDiy> getHomeDefault = Wrappers.lambdaQuery();
                getHomeDefault.eq(PageDiy::getMerId, 0);
                getHomeDefault.eq(PageDiy::getIsDefault, 1);
                pageDiy = getOne(getHomeDefault);
            }
        } else {// circleIdList.size() == 1 && circleIdList.get(0) > 0
            if (request.getId() > 0) {
                pageDiy = getByIdException(request.getId());
            } else {
                // 查询默认的商城首页模板，判断是商城首页还是圈层首页
                LambdaQueryWrapper<PageDiy> getHomeDefault = Wrappers.lambdaQuery();
                getHomeDefault.eq(PageDiy::getMerId, 0);
                getHomeDefault.eq(PageDiy::getIsDefault, 1);
                PageDiy defaultDiy = getOne(getHomeDefault);
                JSONObject defaultJsonObject = JSONObject.parseObject(defaultDiy.getValue(), Feature.OrderedField);
                if (defaultJsonObject.isEmpty()) {
                    pageDiy = defaultDiy;
                } else {
                    JSONObject jsonObject = getFirstJSONObject(defaultJsonObject);
                    String templeName = jsonObject.getString("name");
                    if (!templeName.equals("homeComb") && !templeName.equals("headerSerch")) {
                        pageDiy = defaultDiy;
                    } else {
                        JSONObject searObject = jsonObject.getJSONObject("searConfig");
                        Integer tabVal = searObject.getInteger("tabVal");
                        if (tabVal < 2) {
                            pageDiy = defaultDiy;
                        } else {
                            Circle circle = circleService.getById(circleIdList.get(0));
                            PageDiy circleDiy = getById(circle.getDiyId());
                            if (ObjectUtil.isNotNull(circleDiy)) {
                                pageDiy = circleDiy;
                            } else {
                                pageDiy = defaultDiy;
                            }
                        }
                    }
                }
            }
        }

        if (ObjectUtil.isNull(pageDiy)) throw new CrmebException(SystemConfigResultCode.PAGE_DIY_NOT_EXIST);


        // 优化front比必要的字段 提高diy组件渲染效率
        DocumentContext jsonContext = JsonPath.parse(pageDiy.getValue());
        jsonContext.delete("$..*[?(@.name == 'goodList')].itemStyle.list");
        jsonContext.delete("$..*[?(@.name == 'goodList')].tabConfig.list");
        jsonContext.delete("$..*[?(@.name == 'goodList')].typeConfig.list");
//        jsonContext.delete("$..*[?(@.name == 'goodList')].selectConfig");
        jsonContext.delete("$..*[?(@.name == 'goodList')].goodsSort.list");
        jsonContext.delete("$..*[?(@.name == 'goodList')].goodsList.list[*].attrValue");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].activeValueBrand");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].goodsList");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].selectConfig");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].productList..*.goods[*].attrValue");

        PageDiyResponse response = new PageDiyResponse();
        BeanUtils.copyProperties(pageDiy, response);
//        String modifiedJsonString = getModifiedJsonString(jsonContext.jsonString());
        response.setValue(JSON.parseObject(jsonContext.jsonString()));
        return response;
    }

    private JSONObject getFirstJSONObject(JSONObject defaultJsonObject) {
        Iterator<String> iterator = defaultJsonObject.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = defaultJsonObject.get(key);
            if (value instanceof JSONObject) {
                return (JSONObject) value;
            }
        }
        return defaultJsonObject;
    }

    /**
     * 检查diy模版名称唯一
     *
     * @param pageDiyName 当前要检查的模版名称
     * @param id          更新时的 diy id
     */
    private void checkPageDiyNameUnique(String pageDiyName, Integer id, Integer merId) {
        LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
        lqw.eq(PageDiy::getName, pageDiyName);
        if (id > 0) {
            lqw.ne(PageDiy::getId, id);
        }
        lqw.eq(PageDiy::getMerId, merId);
        Integer count = dao.selectCount(lqw);
        if (count > 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前模版名称已经存在，请修改后再保存！");
        }
    }

////////////////////////////////// 内部处理json配置中的素材地址方法 START

    /**
     * 调用diy配置数据后结合本地配置，传递给递归方法替换
     *
     * @param diyValue 当前待操作的diy对象，其实仅仅用到了value
     * @return 替换后的json对象
     */
    private String getModifiedJsonString(String diyValue) {
        // 使用 Gson 解析 JSON 数据
        Gson gson = new Gson();
        JsonElement jsonElement = gson.fromJson(diyValue, JsonElement.class);

        // 替换指定前缀的键
        String adminApiPath = UploadConstants.UPLOAD_FILE_KEYWORD;
        String newPrefix = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_LOCAL_UPLOAD_URL);
        JsonElement modifiedJsonElement = replaceJsonValue(jsonElement, adminApiPath, newPrefix);

        // 将修改后的 JSON 数据转换回字符串
        return gson.toJson(modifiedJsonElement);
    }

    /**
     * 根据系统关键字匹配素材前缀，匹配后追加系统中对应的素材前缀
     *
     * @param jsonElement 当前操作的json对象
     * @param keyword     匹配的关键字
     * @param newValue    需要追加的前缀
     * @return 操作后的正确json数据
     */
    private JsonElement replaceJsonValue(JsonElement jsonElement, String keyword, String newValue) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject newJsonObject = new JsonObject();
            for (String key : jsonObject.keySet()) {
                JsonElement value = jsonObject.get(key);
                if (value.isJsonPrimitive() && value.getAsString().startsWith(keyword)) {
                    newJsonObject.addProperty(key, newValue + "/" + value.getAsString());
                } else {
                    newJsonObject.add(key, replaceJsonValue(value, keyword, newValue)); // 递归处理子元素
                }
            }
            return newJsonObject;
        } else if (jsonElement.isJsonArray()) {
            // 如果数组元素也可能含有关键字，可以按需处理
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            JsonArray newJsonArray = new JsonArray();
            for (JsonElement element : jsonArray) {
                newJsonArray.add(replaceJsonValue(element, keyword, newValue)); // 递归处理数组元素
            }
            return newJsonArray;
        } else {
            return jsonElement;
        }
    }

    ////////////////////////////////// 内部处理json配置中的素材地址方法 END
}

