package com.zbkj.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.model.merchant.MerchantProductCategory;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductCategory;
import com.zbkj.common.model.record.ProductDayRecord;
import com.zbkj.common.model.record.ShoppingProductDayRecord;
import com.zbkj.common.request.DateRequest;
import com.zbkj.common.response.statistcs.ProductStatisticsChartResponse;
import com.zbkj.common.response.statistcs.StatisticsPieResponse;
import com.zbkj.common.response.statistcs.StatisticsTopResponse;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单管理 service 调用业务
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
public class ProductStatisticsServiceImpl implements ProductStatisticsService {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ShoppingProductDayRecordService shoppingProductDayRecordService;
    @Autowired
    private ProductDayRecordService productDayRecordService;
    @Autowired
    private MerchantProductCategoryService merchantProductCategoryService;


    /**
     * 平台端商品顶部核心数据统计
     * @param dateRequest 日期请求
     */
    @Override
    public List<StatisticsTopResponse> topPlatformData(DateRequest dateRequest) {
        // 获取上个时间段的数据
        DateRequest lastDateRequest = getLastDateRequest(dateRequest.getStartTime(), dateRequest.getEndTime());

        // 判断今天是否在时间段内
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
        if (today.compareTo(dateRequest.getStartTime()) >= 0 && today.compareTo(dateRequest.getEndTime()) <= 0) {
            dateRequest.setIsToday(Boolean.TRUE);
        }
        if (today.compareTo(lastDateRequest.getStartTime()) >= 0 && today.compareTo(lastDateRequest.getEndTime()) <= 0) {
            lastDateRequest.setIsToday(Boolean.TRUE);
        }

        List<StatisticsTopResponse> responses = initTopData();

        ShoppingProductDayRecord record = shoppingProductDayRecordService.getStatisticsByDateRequest(dateRequest);
        ShoppingProductDayRecord lastRecord = shoppingProductDayRecordService.getStatisticsByDateRequest(lastDateRequest);

        // 浏览量
        calculateGrowth(responses, 0, record.getPageView(), lastRecord.getPageView());
        // 收藏量
        calculateGrowth(responses, 1, record.getCollectNum(), lastRecord.getCollectNum());
        // 加购数
        calculateGrowth(responses, 2, record.getAddCartNum(), lastRecord.getAddCartNum());
        // 下单数
        calculateGrowth(responses, 3, record.getOrderProductNum(), lastRecord.getOrderProductNum());
        // 支付数
        calculateGrowth(responses, 4, record.getOrderSuccessProductNum(), lastRecord.getOrderSuccessProductNum());
        // 退款数
        calculateGrowth(responses, 5, record.getRefundProductNum(), lastRecord.getRefundProductNum());
        return responses;
    }

    /**
     * 平台端商品折线趋势图统计
     * @param dateRequest 日期请求
     */
    @Override
    public List<ProductStatisticsChartResponse> chartPlatformData(DateRequest dateRequest) {
        List<String> dateList = CrmebDateUtil.groupDatesStream(dateRequest.getStartTime(), dateRequest.getEndTime());
        List<ProductStatisticsChartResponse> responses = new ArrayList<>();
        dateList.forEach(date->{
            ProductStatisticsChartResponse response= shoppingProductDayRecordService.getStatisticsByDate(date, dateRequest);
            responses.add(response);
        });

        return responses;
    }

    /**
     * 平台端商品分类统计
     * @return 统计饼图响应
     */
    @Override
    public List<StatisticsPieResponse> categoryPlatformData() {
        // 数据统计
        List<Product> productList = productService.statistics(0);
        if (ObjectUtils.isNull(productList)) {
            return new ArrayList<>();
        }
        // 收集所有相关的分类ID
        Set<Integer> allCategoryIds = new HashSet<>();
        productList.forEach(product -> {
            collectAllCategoryIds(product.getCategoryId(), allCategoryIds);
        });

        // 批量获取所有相关分类
        Map<Integer, ProductCategory> categoryMap = loadCategoryMap(allCategoryIds);

        // 统计
        Map<String, Long> result = new HashMap<>();
        productList.forEach(product -> {
            String rootName = findRootCategoryName(product.getCategoryId(), categoryMap);
            if (StringUtils.isNotBlank(rootName)) {
                result.merge(rootName, 1L, Long::sum);
            }
        });
        // 数据封装
        return result.entrySet().stream()
                .map(entry -> {
                    StatisticsPieResponse response = new StatisticsPieResponse();
                    response.setName(entry.getKey());
                    response.setValue(entry.getValue().intValue());
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * 平台端商品类型统计
     * @return 统计饼图响应
     */
    @Override
    public List<StatisticsPieResponse> typePlatformData() {
        // 构建类型映射
        Map<Integer, String> typeMap = new HashMap<>();
        typeMap.put(0, "普通商品");
        typeMap.put(1, "积分商品");
        typeMap.put(2, "虚拟商品");
        //typeMap.put(4, "视频号商品");
        typeMap.put(5, "云盘商品");
        typeMap.put(6, "卡密商品");
        typeMap.put(7, "预约商品");
        typeMap.put(8, "次卡商品");
        // 数据获取
        List<Product> productList = productService.statistics(0);
        if (ObjectUtils.isNull(productList)) {
            return new ArrayList<>();
        }
        Map<Integer, Long> categoryCountMap = productList.stream()
                .filter(product -> ObjectUtils.isNotNull(product.getType()))
                .collect(Collectors.groupingBy(
                        Product::getType,
                        Collectors.counting()
                ));
        // 封装数据并返回
        List<StatisticsPieResponse> responses = new ArrayList<>();
        typeMap.forEach((typeCode, typeName) -> {
            StatisticsPieResponse response = new StatisticsPieResponse();
            response.setName(typeName);
            response.setValue(categoryCountMap.getOrDefault(typeCode, 0L).intValue());
            responses.add(response);
        });
        return responses;
    }

    /**
     * 商户端商品顶部核心数据统计
     * @param dateRequest 日期请求
     */
    @Override
    public List<StatisticsTopResponse> topMerchantData(DateRequest dateRequest) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        // 获取上个时间段的数据
        DateRequest lastDateRequest = getLastDateRequest(dateRequest.getStartTime(), dateRequest.getEndTime());

        // 判断今天是否在时间段内
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
        if (today.compareTo(dateRequest.getStartTime()) >= 0 && today.compareTo(dateRequest.getEndTime()) <= 0) {
            dateRequest.setIsToday(Boolean.TRUE);
        }
        if (today.compareTo(lastDateRequest.getStartTime()) >= 0 && today.compareTo(lastDateRequest.getEndTime()) <= 0) {
            lastDateRequest.setIsToday(Boolean.TRUE);
        }

        List<StatisticsTopResponse> responses = initTopData();
        ProductDayRecord record = productDayRecordService.getStatisticsByDateRequest(dateRequest, merId);
        ProductDayRecord lastRecord = productDayRecordService.getStatisticsByDateRequest(lastDateRequest, merId);

        // 浏览量
        calculateGrowth(responses, 0, record.getPageView(), lastRecord.getPageView());
        // 收藏量
        calculateGrowth(responses, 1, record.getCollectNum(), lastRecord.getCollectNum());
        // 加购数
        calculateGrowth(responses, 2, record.getAddCartNum(), lastRecord.getAddCartNum());
        // 下单数
        calculateGrowth(responses, 3, record.getOrderProductNum(), lastRecord.getOrderProductNum());
        // 支付数
        calculateGrowth(responses, 4, record.getOrderSuccessProductNum(), lastRecord.getOrderSuccessProductNum());
        // 退款数
        calculateGrowth(responses, 5, record.getRefundProductNum(), lastRecord.getRefundProductNum());
        return responses;
    }

    /**
     * 商户端商品折线趋势图统计
     * @param dateRequest 日期请求
     */
    @Override
    public List<ProductStatisticsChartResponse> chartMerchantData(DateRequest dateRequest) {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        List<String> dateList = CrmebDateUtil.groupDatesStream(dateRequest.getStartTime(), dateRequest.getEndTime());
        List<ProductStatisticsChartResponse> responses = new ArrayList<>();
        dateList.forEach(date->{
            ProductStatisticsChartResponse response = productDayRecordService.getStatisticsByDate(date, dateRequest, merId);
            responses.add(response);
        });

        return responses;
    }

    /**
     * 商户端商品分类统计
     * @return 统计饼图响应
     */
    @Override
    public List<StatisticsPieResponse> categoryMerchantData() {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        // 数据统计
        List<Product> productList = productService.statistics(merId);
        if (ObjectUtils.isNull(productList)) {
            return new ArrayList<>();
        }
        // 提取所有分类ID并统计
        Map<Integer, Long> categoryCountMap = productList.stream()
                .map(Product::getCateId)
                .filter(StrUtil::isNotBlank)
                .flatMap(cateIdStr -> Arrays.stream(cateIdStr.split(",")))
                .map(String::trim)
                .filter(StrUtil::isNotBlank)
                .map(Integer::parseInt)
                .collect(Collectors.groupingBy(
                        cateId -> cateId,
                        Collectors.counting()
                ));

        // 封装返回数据
        return categoryCountMap.entrySet().stream()
                .map(entry -> {
                    MerchantProductCategory category = merchantProductCategoryService.getById(entry.getKey());
                    if (category == null) {
                        return null;
                    }
                    StatisticsPieResponse response = new StatisticsPieResponse();
                    response.setName(category.getName());
                    response.setValue(entry.getValue().intValue());
                    return response;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 商户端商品类型统计
     * @return 统计饼图响应
     */
    @Override
    public List<StatisticsPieResponse> typeMerchantData() {
        Integer merId = SecurityUtil.getLoginUserVo().getUser().getMerId();
        // 构建类型映射
        Map<Integer, String> typeMap = new HashMap<>();
        typeMap.put(0, "普通商品");
        //typeMap.put(1, "积分商品");  // 商户端没有积分商品
        typeMap.put(2, "虚拟商品");
        //typeMap.put(4, "视频号商品");
        typeMap.put(5, "云盘商品");
        typeMap.put(6, "卡密商品");
        typeMap.put(7, "预约商品");
        typeMap.put(8, "次卡商品");
        // 数据获取
        List<Product> productList = productService.statistics(merId);
        if (ObjectUtils.isNull(productList)) {
            return new ArrayList<>();
        }
        Map<Integer, Long> categoryCountMap = productList.stream()
                .filter(product -> ObjectUtils.isNotNull(product.getType()))
                .collect(Collectors.groupingBy(
                        Product::getType,
                        Collectors.counting()
                ));
        // 封装数据并返回
        List<StatisticsPieResponse> responses = new ArrayList<>();
        typeMap.forEach((typeCode, typeName) -> {
            StatisticsPieResponse response = new StatisticsPieResponse();
            response.setName(typeName);
            response.setValue(categoryCountMap.getOrDefault(typeCode, 0L).intValue());
            responses.add(response);
        });
        return responses;
    }

    /**
     * 初始化顶部核心数据
     * @return 顶部核心数据封装对象集合
     */
    private List<StatisticsTopResponse> initTopData() {
        List<StatisticsTopResponse> responses = new ArrayList<>();
        StatisticsTopResponse res1= new StatisticsTopResponse("商品浏览量",0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res2= new StatisticsTopResponse("商品收藏量",0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res3= new StatisticsTopResponse("加购件数",0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res4= new StatisticsTopResponse("下单件数",0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res5= new StatisticsTopResponse("支付件数",0,"0.00%", Boolean.FALSE);
        StatisticsTopResponse res6= new StatisticsTopResponse("退款件数",0,"0.00%", Boolean.FALSE);
        Collections.addAll(responses,res1,res2,res3,res4,res5,res6);
        return responses;
    }

    /**
     * 计算增长率
     * @param responses 响应数据集合
     * @param index 索引位置
     * @param currentValue 当前值
     * @param lastValue 上一个时间段的值
     */
    private void calculateGrowth(List<StatisticsTopResponse> responses, Integer index, Integer currentValue, Integer lastValue) {
        String growthRate = "0.00%";
        Boolean increased = Boolean.FALSE;
        if (lastValue > 0) {
            BigDecimal growth = new BigDecimal(currentValue).subtract(new BigDecimal(lastValue))
                    .multiply(new BigDecimal("100"))
                    .divide(new BigDecimal(lastValue), 2, RoundingMode.HALF_UP);
            growthRate = growth.toString() + "%";
            if (growth.compareTo(BigDecimal.ZERO) > 0) {
                increased = Boolean.TRUE;
            }
        } else if (currentValue > 0) {
            growthRate = "100.00%";
            increased = Boolean.TRUE;
        }
        responses.get(index).setValue(currentValue);
        responses.get(index).setGrowthRate(growthRate);
        responses.get(index).setIncreased(increased);
    }

    /**
     * 获取上一个时间段的数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 上一个时间段的数据封装对象 DateRequest 类型
     */
    private DateRequest getLastDateRequest(String startTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE);
        LocalDate start = LocalDate.parse(startTime, formatter);
        LocalDate end = LocalDate.parse(endTime, formatter);

        long periodDays = ChronoUnit.DAYS.between(start, end) + 1;
        LocalDate previousStart = start.minusDays(periodDays);
        LocalDate previousEnd = end.minusDays(periodDays);

        String lastStart = previousStart.format(formatter);
        String lastEnd = previousEnd.format(formatter);
        // 封装上一个时间段对象
        DateRequest dateRequest = new DateRequest();
        dateRequest.setStartTime(lastStart);
        dateRequest.setEndTime(lastEnd);
        return dateRequest;
    }

    /**
     * 递归收集分类ID（包括所有祖先）
     */
    private void collectAllCategoryIds(Integer categoryId, Set<Integer> categoryIds) {
        if (categoryId == null || categoryId <= 0) {
            return;
        }
        if (categoryIds.contains(categoryId)) {
            return; // 已收集过
        }
        categoryIds.add(categoryId);
        // 递归收集父分类ID
        ProductCategory category = productCategoryService.getById(categoryId);
        if (category != null && category.getPid() != null && category.getPid() > 0) {
            collectAllCategoryIds(category.getPid(), categoryIds);
        }
    }

    /**
     * 批量加载分类信息
     */
    private Map<Integer, ProductCategory> loadCategoryMap(Set<Integer> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return new HashMap<>();
        }
        List<ProductCategory> categories = productCategoryService.listByIds(categoryIds);
        return categories.stream()
                .collect(Collectors.toMap(ProductCategory::getId, c -> c));
    }

    /**
     * 在内存中查找根分类名称
     */
    private String findRootCategoryName(Integer categoryId, Map<Integer, ProductCategory> categoryMap) {
        if (categoryId == null || categoryId <= 0) {
            return "";
        }
        ProductCategory current = categoryMap.get(categoryId);
        if (current == null) {
            return "";
        }
        // 向上查找直到根节点
        while (current != null && current.getPid() != null && current.getPid() > 0) {
            current = categoryMap.get(current.getPid());
        }
        return current != null ? current.getName() : "";
    }
}
