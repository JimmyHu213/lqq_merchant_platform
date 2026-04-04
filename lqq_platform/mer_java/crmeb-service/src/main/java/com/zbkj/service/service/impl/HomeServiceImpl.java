package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.record.ProductDayRecord;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.ProductRankingRequest;
import com.zbkj.common.response.*;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户表 服务实现类
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
public class HomeServiceImpl implements HomeService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserVisitRecordService userVisitRecordService;
    @Autowired
    private ProductService productService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private UserMerchantCollectService userMerchantCollectService;
    @Autowired
    private ProductDayRecordService productDayRecordService;
    @Autowired
    private MerchantDayRecordService merchantDayRecordService;
    @Autowired
    private CircleService circleService;
    @Autowired
    private CircleMerchantService circleMerchantService;

    /**
     * 首页数据
     * @return HomeRateResponse
     */
    @Override
    public HomeRateResponse indexMerchantDate(SystemAdmin systemAdmin) {
        String today = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
        String yesterday = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);
        HomeRateResponse response = new HomeRateResponse();
        response.setSales(orderService.getPayOrderAmountByDate(systemAdmin.getMerId(), today));
        response.setYesterdaySales(orderService.getPayOrderAmountByDate(systemAdmin.getMerId(), yesterday));
        response.setOrderNum(orderService.getOrderNumByDate(systemAdmin.getMerId(), today));
        response.setYesterdayOrderNum(orderService.getOrderNumByDate(systemAdmin.getMerId(), yesterday));
        response.setFollowNum(userMerchantCollectService.getCountByMerId(systemAdmin.getMerId()));
        response.setVisitorsNum(merchantDayRecordService.getVisitorsByDate(systemAdmin.getMerId(), today));
        response.setYesterdayVisitorsNum(merchantDayRecordService.getVisitorsByDate(systemAdmin.getMerId(), yesterday));
        return response;
    }

    /**
     * 经营数据：
     * @return HomeOperatingMerDataResponse
     */
    @Override
    public HomeOperatingMerDataResponse operatingMerchantData(SystemAdmin systemAdmin) {
        HomeOperatingMerDataResponse response = new HomeOperatingMerDataResponse();
        response.setNotShippingOrderNum(orderService.getNotShippingNum(systemAdmin.getMerId()));
        response.setAwaitVerificationOrderNum(orderService.getAwaitVerificationNum(systemAdmin.getMerId()));
        response.setRefundingOrderNum(refundOrderService.getAwaitAuditNum(systemAdmin.getMerId()));
        response.setOnSaleProductNum(productService.getOnSaleNum(systemAdmin.getMerId()));
        response.setAwaitAuditProductNum(productService.getAwaitAuditNum(systemAdmin.getMerId()));
        return response;
    }

    /**
     * 平台端首页数据
     * @return PlatformHomeRateResponse
     */
    @Override
    public PlatformHomeRateResponse indexPlatformDate() {
        String today = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
        String yesterday = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);
        PlatformHomeRateResponse response = new PlatformHomeRateResponse();
        response.setTodayNewUserNum(userService.getRegisterNumByDate(today));
        response.setYesterdayNewUserNum(userService.getRegisterNumByDate(yesterday));
        response.setPageviews(userVisitRecordService.getPageviewsByDate(today));
        response.setYesterdayPageviews(userVisitRecordService.getPageviewsByDate(yesterday));
        response.setTodayNewMerchantNum(merchantService.getNewNumByDate(today));
        response.setYesterdayNewMerchantNum(merchantService.getNewNumByDate(yesterday));
        response.setOrderNum(orderService.getOrderNumByDate(0, today));
        response.setYesterdayOrderNum(orderService.getOrderNumByDate(0, yesterday));
        response.setSales(orderService.getPayOrderAmountByDate(0, today));
        response.setYesterdaySales(orderService.getPayOrderAmountByDate(0, yesterday));
        response.setUserNum(userService.getTotalNum());
        response.setMerchantNum(merchantService.getAllCount());
        return response;
    }

    /**
     * 平台端首页经营数据
     * @return HomeOperatingDataResponse
     */
    @Override
    public HomeOperatingDataResponse operatingPlatformData() {
        HomeOperatingDataResponse response = new HomeOperatingDataResponse();
        response.setNotShippingOrderNum(orderService.getNotShippingNum(0));
        response.setAwaitVerificationOrderNum(orderService.getAwaitVerificationNum(0));
        response.setRefundingOrderNum(refundOrderService.getAwaitAuditNum(0));
        response.setOnSaleProductNum(productService.getOnSaleNum(0));
        response.setAwaitAuditProductNum(productService.getAwaitAuditNum(0));
        return response;
    }

    /**
     * 平台端首页获取用户渠道数据
     */
    @Override
    public List<UserChannelDataResponse> getUserChannelData() {
        // 封装参数
        List<UserChannelDataResponse> response = Arrays.asList(
                new UserChannelDataResponse("wechat", 0),
                new UserChannelDataResponse("routine", 0),
                new UserChannelDataResponse("h5", 0),
                new UserChannelDataResponse("iosWx", 0),
                new UserChannelDataResponse("androidWx", 0),
                new UserChannelDataResponse("ios", 0)
        );

        List<User> userList = userService.getChannelData();
        //return userList.stream().map(e -> {
        //    UserChannelDataResponse response = new UserChannelDataResponse();
        //    response.setRegisterType(e.getRegisterType());
        //    response.setNum(e.getPayCount());
        //    return response;
        //}).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(userList)) {
            Map<String, Integer> channelCountMap = userList.stream()
                    .collect(Collectors.groupingBy(
                            User::getRegisterType,
                            Collectors.summingInt(User::getPayCount)
                    ));
            // 更新响应中的数值
            for (UserChannelDataResponse item : response) {
                String channel = item.getRegisterType();
                if (channelCountMap.containsKey(channel)) {
                    item.setNum(channelCountMap.get(channel));
                }
                // 如果没有对应的渠道数据，保持默认值0
            }
        }
        return response;
    }

    /**
     * 商户端商品支付排行榜
     */
    @Override
    public List<ProductRankingResponse> merchantProductPayRanking(SystemAdmin systemAdmin) {
        Integer merId = systemAdmin.getMerId();
        ProductRankingRequest request = new ProductRankingRequest();
        request.setMerId(merId);
        request.setDateLimit(DateConstants.SEARCH_DATE_LATELY_7);
        request.setSortKey("salesAmount");
        PageInfo<ProductDayRecord> pageInfo = productDayRecordService.getRanking(request);
        List<ProductDayRecord> recordList = pageInfo.getList();
        List<ProductRankingResponse> list = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(recordList)) {
            for (ProductDayRecord record : recordList) {
                Product product = productService.getById(record.getProductId());
                ProductRankingResponse response = new ProductRankingResponse();
                BeanUtils.copyProperties(record, response);
                response.setSalesAmount(record.getOrderSuccessProductFee());
                response.setProductId(product.getId());
                response.setProName(product.getName());
                response.setImage(product.getImage());
                list.add(response);
            }
        }
        return list;
    }

    /**
     * 商品浏览量排行榜
     */
    @Override
    public List<ProductRankingResponse> merchantProductPageviewRanking(SystemAdmin systemAdmin) {
        Integer merId = systemAdmin.getMerId();
        ProductRankingRequest request = new ProductRankingRequest();
        request.setMerId(merId);
        request.setDateLimit(DateConstants.SEARCH_DATE_LATELY_7);
        request.setSortKey("pageviews");
        PageInfo<ProductDayRecord> pageInfo = productDayRecordService.getRanking(request);
        List<ProductDayRecord> recordList = pageInfo.getList();
        List<ProductRankingResponse> list = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(recordList)) {
            for (ProductDayRecord record : recordList) {
                Product product = productService.getById(record.getProductId());
                ProductRankingResponse response = new ProductRankingResponse();
                BeanUtils.copyProperties(record, response);
                response.setPageView(record.getPageView());
                response.setProductId(product.getId());
                response.setProName(product.getName());
                response.setImage(product.getImage());
                list.add(response);
            }
        }
        return list;
    }

    /**
     * 圈层端 首页数据
     */
    @Override
    public PlatformHomeRateResponse indexCircleData(List<Integer> merIds) {

        String today = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
        String yesterday = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);

        if (CollUtil.isEmpty(merIds)) {
            return new PlatformHomeRateResponse();
        }

        PlatformHomeRateResponse response = new PlatformHomeRateResponse();
        // 销售额
        response.setSales(orderService.getPayOrderAmountByMerIdsAnDate(merIds, today));
        response.setYesterdaySales(orderService.getPayOrderAmountByMerIdsAnDate(merIds, yesterday));
        // 访客数
        response.setPageviews(merchantDayRecordService.getVisitorsByMerIdsAndDate(merIds, today));
        response.setYesterdayPageviews(merchantDayRecordService.getVisitorsByMerIdsAndDate(merIds, yesterday));
        // 订单数
        response.setOrderNum(orderService.getOrderNumByMerIdsAndDate(merIds, today));
        response.setYesterdayOrderNum(orderService.getOrderNumByMerIdsAndDate(merIds, yesterday));
        // 关注量
        response.setUserNum(userMerchantCollectService.getCollectNumByMerIds(merIds));
        response.setTodayNewUserNum(userMerchantCollectService.getCollectNumByMerIdsAndDate(merIds, today));
        response.setYesterdayNewUserNum(userMerchantCollectService.getCollectNumByMerIdsAndDate(merIds, yesterday));
        // 商户数
        response.setMerchantNum(merIds.size());
        response.setTodayNewMerchantNum(circleMerchantService.getMerchantNumByMerIdsAndDate(merIds, today));
        response.setYesterdayNewMerchantNum(circleMerchantService.getMerchantNumByMerIdsAndDate(merIds, yesterday));
        return response;
    }

    /**
     * 圈层端 经营数据
     */
    @Override
    public HomeOperatingDataResponse operatingCircleData(List<Integer> merIds ) {
        if (CollUtil.isEmpty(merIds)) {
            return new HomeOperatingDataResponse();
        }
        HomeOperatingDataResponse response = new HomeOperatingDataResponse();
        response.setNotShippingOrderNum(orderService.getNotShippingNumByMerIds(merIds));
        response.setAwaitVerificationOrderNum(orderService.getAwaitVerificationNumByMerIds(merIds));
        response.setRefundingOrderNum(refundOrderService.getAwaitAuditNumByMerIds(merIds));
        response.setOnSaleProductNum(productService.getOnSaleNumByMerIds(merIds));
        response.setAwaitAuditProductNum(productService.getAwaitAuditNumByMerIds(merIds));
        return response;
    }

    /**
     * 圈层端 用户渠道比例
     */
    @Override
    public List<UserChannelDataResponse> getCircleUserChannelData(List<Integer> merIds ) {
        // 封装参数
        List<UserChannelDataResponse> response = Arrays.asList(
                new UserChannelDataResponse("wechat", 0),
                new UserChannelDataResponse("routine", 0),
                new UserChannelDataResponse("h5", 0),
                new UserChannelDataResponse("iosWx", 0),
                new UserChannelDataResponse("androidWx", 0),
                new UserChannelDataResponse("ios", 0)
        );


        if (CollUtil.isNotEmpty(merIds)) {
            List<Integer> userIds = userMerchantCollectService.getUserIdsByMerIds(merIds);
            if (CollUtil.isNotEmpty(userIds)) {
                List<User> userList = userService.getChannelDataByIds(userIds);
                if (CollUtil.isNotEmpty(userList)) {

                    Map<String, Integer> channelCountMap = userList.stream()
                            .collect(Collectors.groupingBy(
                                    User::getRegisterType,
                                    Collectors.summingInt(User::getPayCount)
                            ));

                    // 更新响应中的数值
                    for (UserChannelDataResponse item : response) {
                        String channel = item.getRegisterType();
                        if (channelCountMap.containsKey(channel)) {
                            item.setNum(channelCountMap.get(channel));
                        }
                        // 如果没有对应的渠道数据，保持默认值0
                    }

                }
            }
        }
        return response;
    }
}

