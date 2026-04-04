package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.OrderConstants;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.constants.TaskConstants;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

/**
 * CardTimesTaskServiceImpl 接口实现
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
public class CardTimesTaskServiceImpl implements CardTimesTaskService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(CardTimesTaskServiceImpl.class);

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderFlowRecordService orderFlowRecordService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductRelationService productRelationService;

    @Override
    public void cardTimesProductValidity() {
        String redisKey = TaskConstants.CARD_TIMES_PRODUCT_VALIDITY;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.autoCancel | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPopUnique(redisKey, 10L);
            if (ObjectUtil.isNull(data)) {
                continue;
            }
            try {
                boolean result = productValidity(Integer.valueOf(data.toString()));
                if (!result) {
                    redisUtil.lPushUnique(redisKey, data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                redisUtil.lPushUnique(redisKey, data);
            }
        }
    }

    private boolean productValidity(Integer productId) {
        Product product = productService.getById(productId);
        if (ObjectUtil.isNull(product) || !product.getType().equals(ProductConstants.PRODUCT_TYPE_CARDTIMES)) {
            logger.error("次卡商品有效期校验，商品不存在，商品ID为:{}", productId);
            return Boolean.TRUE;
        }
        if ((product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT)
                || product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_FAIL))
                && !product.getIsShow()) {
            logger.error("次卡商品有效期校验，商品未处于出售中暂不处理，商品ID为:{}", productId);
            return Boolean.FALSE;
        }
        String verifyEndDate = product.getVerifyTimeLimitEndDate();
        String today = DateUtil.format(new Date(), DateConstants.DATE_FORMAT_DATE);
        if (product.getVerifyTimeLimitType().equals(ProductConstants.CARD_TIMES_VALIDITY_LIMIT_DATE)
                && today.compareTo(verifyEndDate) > 0) {
            // 过期了，商品下架
            product.setIsShow(false);
            product.setUpdateTime(DateUtil.date());
            return Boolean.TRUE.equals(transactionTemplate.execute(e -> {
                productService.updateById(product);
                cartService.productStatusNotEnable(productId);
                // 商品下架时，清除用户收藏
                productRelationService.deleteByProId(productId);
                return true;
            }));
        }
        return Boolean.FALSE;
    }


    @Override
    public void cardTimesOrderValidity() {
        String redisKey = TaskConstants.CARD_TIMES_ORDER_VALIDITY;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.autoCancel | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(data)) {
                continue;
            }
            try {
                boolean result = orderValidity(String.valueOf(data));
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                redisUtil.lPush(redisKey, data);
            }
        }

    }

    private boolean orderValidity(String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (ObjectUtil.isNull(order) || !order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CARDTIMES)) {
            logger.error("次卡订单有效期校验，订单不存在，订单号为:{}", orderNo);
            return Boolean.TRUE;
        }
        if (order.getStatus().equals(OrderConstants.ORDER_STATUS_COMPLETE)) {
            return Boolean.TRUE;
        }
        OrderDetail orderDetail = orderDetailService.getByOrderNo(orderNo).get(0);
        String verifyEndDate = orderDetail.getVerifyEndDate();
        if (StrUtil.isBlank(verifyEndDate)) {
            // 没有有效期，不做处理
            return Boolean.TRUE;
        }
        String today = DateUtil.format(new Date(), DateConstants.DATE_FORMAT_DATE);
        if (order.getStatus().equals(OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION)
                && today.compareTo(verifyEndDate) > 0) {
            // 过期了，订单流转
            order.setStatus(OrderConstants.ORDER_STATUS_COMPLETE);
            order.setReceivingTime(DateUtil.date());
            order.setUpdateTime(DateUtil.date());

            Boolean execute = transactionTemplate.execute(e -> {
                orderService.updateById(order);
                orderDetailService.takeDelivery(orderNo);
                orderFlowRecordService.orderComplete(orderNo);
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error("次卡订单过期自动完成，更新数据库失败，orderNo = {}", orderNo);
            }
            asyncService.cardOrderCompleteAfterFreezingOperation(orderNo);
            return Boolean.TRUE;

        }
        return Boolean.FALSE;
    }
}
