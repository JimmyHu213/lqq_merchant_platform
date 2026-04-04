package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.RedisConstants;
import com.zbkj.common.model.record.ShoppingProductDayRecord;
import com.zbkj.common.request.DateRequest;
import com.zbkj.common.response.statistcs.ProductStatisticsChartResponse;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.service.dao.ShoppingProductDayRecordDao;
import com.zbkj.service.service.OrderService;
import com.zbkj.service.service.ProductRelationService;
import com.zbkj.service.service.RefundOrderService;
import com.zbkj.service.service.ShoppingProductDayRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
public class ShoppingProductDayRecordServiceImpl extends ServiceImpl<ShoppingProductDayRecordDao, ShoppingProductDayRecord> implements ShoppingProductDayRecordService {

    @Resource
    private ShoppingProductDayRecordDao dao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RefundOrderService refundOrderService;

    @Override
    public ShoppingProductDayRecord getStatisticsByDateRequest(DateRequest dateRequest) {
        QueryWrapper<ShoppingProductDayRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(sum(page_view), 0) as page_view, IFNULL(sum(collect_num), 0) as collect_num, " +
                "IFNULL(sum(add_cart_num), 0) as add_cart_num, IFNULL(sum(order_product_num), 0) as order_product_num," +
                "IFNULL(sum(order_success_product_num), 0) as order_success_product_num,IFNULL(sum(refund_product_num), 0) as refund_product_num");
        queryWrapper.between("date", dateRequest.getStartTime(), dateRequest.getEndTime());
        ShoppingProductDayRecord record = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(record)) {
            record = new ShoppingProductDayRecord(0, null, 0, 0, 0, 0,0, 0,0);
        }
        if (dateRequest.getIsToday()) {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
            // 浏览量
            int pageViewNum = 0;
            Object pageViewObject = redisUtil.get(RedisConstants.PRO_PAGE_VIEW_KEY + today);
            if (ObjectUtil.isNotNull(pageViewObject)) {
                pageViewNum = (Integer) pageViewObject;
            }
            // 收藏量
            Integer collectNum = productRelationService.getCountByDate(today);
            // 加购量
            int addCartNum = 0;
            Object addCartObject = redisUtil.get(RedisConstants.PRO_ADD_CART_KEY + today);
            if (ObjectUtil.isNotNull(addCartObject)) {
                addCartNum = (Integer) addCartObject;
            }
            // 交易总件数
            Integer orderProductNum = orderService.getOrderProductNumByDate(today);
            // 交易成功件数
            Integer orderSuccessProductNum = orderService.getOrderSuccessProductNumByDate(today);
            // 退款件数
            Integer refundProductNum = refundOrderService.getRefundProductNumByDate(today);

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
    public ProductStatisticsChartResponse getStatisticsByDate(String date, DateRequest dateRequest) {
        LocalDate now = LocalDate.now();
        boolean isToday = Boolean.FALSE;

        QueryWrapper<ShoppingProductDayRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(page_view) as page_view, sum(collect_num) as collect_num," +
                " sum(order_product_num) as order_product_num, sum(order_success_product_num) as order_success_product_num");
        if (date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            queryWrapper.eq("date", date);
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
            isToday = today.compareTo(date) == 0 && date.compareTo(dateRequest.getEndTime()) == 0;
        } else if (date.matches("^\\d{4}-\\d{2}$")) {
            queryWrapper.like("date", date + "-%");
            if (date.equals(dateRequest.getStartTime().substring(0, 7))) {
                queryWrapper.ge("date", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 7))) {
                queryWrapper.le("date", dateRequest.getEndTime());
            }
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_MONTH));
            isToday = today.compareTo(date) == 0 && today.compareTo(dateRequest.getEndTime()) == 0;
        } else if (date.matches("^\\d{4}$")) {
            queryWrapper.like("date", date + "-%");
            if (date.equals(dateRequest.getStartTime().substring(0, 4))) {
                queryWrapper.ge("date", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 4))) {
                queryWrapper.le("date", dateRequest.getEndTime());
            }
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_YEAR));
            isToday = today.compareTo(date) == 0 && today.compareTo(dateRequest.getEndTime()) == 0;
        }
        ShoppingProductDayRecord record = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(record)) {
            record = new ShoppingProductDayRecord(0, null, 0, 0, 0, 0,0, 0,0);
        }
        int pageViewNum = 0;
        int collectNum = 0;
        int orderProductNum = 0;
        int orderSuccessProductNum = 0;
        if (isToday) {
            String today = now.format(DateTimeFormatter.ofPattern(DateConstants.DATE_FORMAT_DATE));
            Object pageViewObject = redisUtil.get(RedisConstants.PRO_PAGE_VIEW_KEY + today);
            if (ObjectUtil.isNotNull(pageViewObject)) {
                pageViewNum = (Integer) pageViewObject;
            }
            // 收藏量
            collectNum = productRelationService.getCountByDate(today);
            // 交易总件数
            orderProductNum = orderService.getOrderProductNumByDate(today);
            // 交易成功件数
            orderSuccessProductNum = orderService.getOrderSuccessProductNumByDate(today);
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

