package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.RedisConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.record.ProductDayRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.DateRequest;
import com.zbkj.common.request.ProductRankingRequest;
import com.zbkj.common.response.statistcs.ProductStatisticsChartResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.service.dao.ProductDayRecordDao;
import com.zbkj.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ShoppingProductDayRecordService 接口实现
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
public class ProductDayRecordServiceImpl extends ServiceImpl<ProductDayRecordDao, ProductDayRecord> implements ProductDayRecordService {

    @Resource
    private ProductDayRecordDao dao;
    @Autowired
    private ProductService productService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private RefundOrderInfoService refundOrderInfoService;
    @Autowired
    private MerchantOrderService merchantOrderService;

    /**
     * 获取商品排行榜
     * @param request 查询参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductDayRecord> getRanking(ProductRankingRequest request) {
        String startDate;
        String endDate;
        DateTime date = DateUtil.date();
        switch (request.getDateLimit()){
            case DateConstants.SEARCH_DATE_YESTERDAY:
                startDate = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);
                endDate = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);
                break;
            case DateConstants.SEARCH_DATE_LATELY_7:
                startDate = DateUtil.offsetDay(date, -7).toString(DateConstants.DATE_FORMAT_DATE);
                endDate = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);
                break;
            case DateConstants.SEARCH_DATE_LATELY_30:
                startDate = DateUtil.offsetDay(date, -30).toString(DateConstants.DATE_FORMAT_DATE);
                endDate = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);
                break;
            case DateConstants.SEARCH_DATE_WEEK:
                startDate = DateUtil.beginOfWeek(date).toString(DateConstants.DATE_FORMAT_DATE);
                endDate = DateUtil.endOfWeek(date).toString(DateConstants.DATE_FORMAT_DATE);
                break;
            case DateConstants.SEARCH_DATE_MONTH:
                startDate = DateUtil.beginOfMonth(date).toString(DateConstants.DATE_FORMAT_DATE);
                endDate = DateUtil.endOfMonth(date).toString(DateConstants.DATE_FORMAT_DATE);
                break;
            case DateConstants.SEARCH_DATE_YEAR:
                startDate = DateUtil.beginOfYear(date).toString(DateConstants.DATE_FORMAT_DATE);
                endDate = DateUtil.endOfYear(date).toString(DateConstants.DATE_FORMAT_DATE);
                break;
            default:
                String[] split = request.getDateLimit().split(",");
                if (split.length < 2) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择正确的时间范围");
                }
                startDate = DateUtil.parse(split[0]).toString(DateConstants.DATE_FORMAT_DATE);
                endDate = DateUtil.parse(split[1]).toString(DateConstants.DATE_FORMAT_DATE);
                break;
        }
        Page<Object> startPage = PageHelper.startPage(request.getPage(), request.getLimit());

        QueryWrapper<ProductDayRecord> wrapper = new QueryWrapper<>();
        wrapper.select("product_id", "sum(page_view) as page_view", "sum(collect_num) as collect_num",
                "sum(add_cart_num) as add_cart_num",
                "sum(order_product_num) as order_product_num",
                "sum(order_success_product_fee) as order_success_product_fee");
        wrapper.between("date", startDate, endDate);
        wrapper.eq("mer_id", request.getMerId());
        wrapper.groupBy("product_id");
        switch (request.getSortKey()) {
            case "pageviews":
                wrapper.orderByDesc("page_view");
                break;
            case "collectNum":
                wrapper.orderByDesc("collect_num");
                break;
            case "addCartNum":
                wrapper.orderByDesc("add_cart_num");
                break;
            case "salesNum":
                wrapper.orderByDesc("order_product_num");
                break;
            case "salesAmount":
                wrapper.orderByDesc("order_success_product_fee");
                break;
        }
        List<ProductDayRecord> recordList = dao.selectList(wrapper);
        return CommonPage.copyPageInfo(startPage, recordList);
    }

    @Override
    public ProductDayRecord getStatisticsByDateRequest(DateRequest dateRequest, Integer merId) {
        QueryWrapper<ProductDayRecord> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(sum(page_view), 0) as page_view, IFNULL(sum(collect_num), 0) as collect_num, " +
                "IFNULL(sum(add_cart_num), 0) as add_cart_num, IFNULL(sum(order_product_num), 0) as order_product_num," +
                "IFNULL(sum(order_success_product_num), 0) as order_success_product_num, IFNULL(sum(refund_product_num), 0) as refund_product_num");
        wrapper.eq("mer_id", merId);
        wrapper.between("date", dateRequest.getStartTime(), dateRequest.getEndTime());
        ProductDayRecord record = dao.selectOne(wrapper);
        if (ObjectUtil.isNull(record)) {
            record = new ProductDayRecord(0, null, 0, 0, 0, 0, 0, 0, BigDecimal.ZERO, 0, 0);
        }
        if (dateRequest.getIsToday()) {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));

            List<Integer> productIds = productService.findStatisticsIds(merId);

            int pageViewNum = 0;
            int collectNum = 0;
            int addCartNum = 0;
            int orderProductNum = 0;
            int orderSuccessProductNum = 0;
            int refundProductNum = 0;
            // 浏览量
            Set<String> pageViewNumKeys = redisUtil.keys(StrUtil.format(RedisConstants.PRO_PRO_PAGE_VIEW_KEY, today, "*"));
            for (String key : pageViewNumKeys) {
                int productId = Integer.parseInt(key.substring(key.lastIndexOf(":") + 1));
                if (productIds.contains(productId)) {
                    int num = 0;
                    Object object = redisUtil.get(key);
                    if (ObjectUtil.isNotNull(object)) {
                        num = (Integer) object;
                    }
                    pageViewNum += num;
                }
            }
            // 收藏量
            Integer collectDetailNum = productRelationService.getCountByDateAndProIds(today, productIds);
            collectNum += collectDetailNum;
            // 加购量
            Set<String> addCartNumKeys = redisUtil.keys(StrUtil.format(RedisConstants.PRO_PRO_ADD_CART_KEY, today, "*"));
            for (String key : addCartNumKeys) {
                int productId = Integer.parseInt(key.substring(key.lastIndexOf(":") + 1));
                if (productIds.contains(productId)) {
                    int num = 0;
                    Object object = redisUtil.get(key);
                    if (ObjectUtil.isNotNull(object)) {
                        num = (Integer) object;
                    }
                    addCartNum += num;
                }
            }
            // 交易成功件数
            Integer orderSuccessProductDetailNum = orderDetailService.getSalesNumByDateAndMerId(today, merId);
            orderSuccessProductNum += orderSuccessProductDetailNum;
            // 交易总件数
            Integer orderProductDetailNum = merchantOrderService.getOrderProductNumByDateAndMerId(today, merId);
            orderProductNum += orderProductDetailNum;
            // 退款件数
            Integer refundProductDetailNum = refundOrderInfoService.getRefundProductNumByDateAndMerId(today, merId);
            refundProductNum += refundProductDetailNum;

            record.setPageView(record.getPageView() + pageViewNum);
            record.setCollectNum(record.getCollectNum() + collectNum);
            record.setAddCartNum(record.getAddCartNum() + addCartNum);
            record.setOrderProductNum(record.getOrderProductNum() + orderProductNum);
            record.setOrderSuccessProductNum(record.getOrderSuccessProductNum() + orderSuccessProductNum);
            record.setRefundProductNum(record.getRefundProductNum() + refundProductNum);
        }
        return record;
    }

    @Override
    public ProductStatisticsChartResponse getStatisticsByDate(String date, DateRequest dateRequest, Integer merId) {
        LocalDate now = LocalDate.now();
        boolean isToday = Boolean.FALSE;

        QueryWrapper<ProductDayRecord> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(sum(page_view), 0) as page_view, IFNULL(sum(collect_num), 0) as collect_num, " +
                "IFNULL(sum(add_cart_num), 0) as add_cart_num, IFNULL(sum(order_product_num), 0) as order_product_num," +
                "IFNULL(sum(order_success_product_num), 0) as order_success_product_num");
        wrapper.eq("mer_id", merId);
        if (date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            wrapper.eq("date", date);
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
            isToday = today.compareTo(date) == 0 && date.compareTo(dateRequest.getEndTime()) == 0;
        } else if (date.matches("^\\d{4}-\\d{2}$")) {
            wrapper.like("date", date + "-%");
            if (date.equals(dateRequest.getStartTime().substring(0, 7))) {
                wrapper.ge("date", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 7))) {
                wrapper.le("date", dateRequest.getEndTime());
            }
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_MONTH));
            isToday = today.compareTo(date) == 0 && date.compareTo(dateRequest.getEndTime()) == 0;
        } else if (date.matches("^\\d{4}$")) {
            wrapper.like("date", date + "-%");
            if (date.equals(dateRequest.getStartTime().substring(0, 4))) {
                wrapper.ge("date", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 4))) {
                wrapper.le("date", dateRequest.getEndTime());
            }
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_YEAR));
            isToday = today.compareTo(date) == 0 && date.compareTo(dateRequest.getEndTime()) == 0;
        }
        ProductDayRecord record = dao.selectOne(wrapper);
        if (ObjectUtil.isNull(record)) {
            record = new ProductDayRecord(0, null, 0, 0, 0, 0, 0, 0, BigDecimal.ZERO, 0, 0);
        }

        int pageViewNum = 0;
        int collectNum = 0;
        int orderProductNum = 0;
        int orderSuccessProductNum = 0;
        if (isToday) {
            List<Integer> productIds = productService.findStatisticsIds(merId);
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
            // 浏览量
            Set<String> pageViewNumKeys = redisUtil.keys(StrUtil.format(RedisConstants.PRO_PRO_PAGE_VIEW_KEY, today, "*"));
            for (String key : pageViewNumKeys) {
                int productId = Integer.parseInt(key.substring(key.lastIndexOf(":") + 1));
                if (productIds.contains(productId)) {
                    int num = 0;
                    Object object = redisUtil.get(key);
                    if (ObjectUtil.isNotNull(object)) {
                        num = (Integer) object;
                    }
                    pageViewNum += num;
                }
            }
            // 收藏量
            Integer collectDetailNum = productRelationService.getCountByDateAndProIds(today, productIds);
            collectNum += collectDetailNum;
            // 交易成功件数
            Integer orderSuccessProductDetailNum = orderDetailService.getSalesNumByDateAndMerId(today, merId);
            orderSuccessProductNum += orderSuccessProductDetailNum;
            // 交易总件数
            Integer orderProductDetailNum = merchantOrderService.getOrderProductNumByDateAndMerId(today, merId);
            orderProductNum += orderProductDetailNum;
        }
        ProductStatisticsChartResponse response = new ProductStatisticsChartResponse();
        response.setDate(date);
        response.setVisitVolume(record.getPageView() + pageViewNum);
        response.setCollectionVolume(record.getCollectNum() + collectNum);
        response.setOrderVolume(record.getOrderProductNum() + orderProductNum);
        response.setPaidVolume(record.getOrderSuccessProductNum() + orderSuccessProductNum);
        return response;
    }
}

