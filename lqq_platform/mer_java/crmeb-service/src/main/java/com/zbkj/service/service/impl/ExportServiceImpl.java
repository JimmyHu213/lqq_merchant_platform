package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.dto.RetailStorePeopleExportDto;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBrokerageRecord;
import com.zbkj.common.model.user.UserClosing;
import com.zbkj.common.model.user.UserTag;
import com.zbkj.common.request.OrderSearchRequest;
import com.zbkj.common.request.RetailStorePeopleSearchRequest;
import com.zbkj.common.request.UserSearchRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.ExportUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.OrderExcelVo;
import com.zbkj.common.vo.UserExcelVo;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ExcelServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private CrmebConfig crmebConfig;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;
    @Autowired
    private UserClosingService userClosingService;
    @Autowired
    private UserTagService userTagService;
    @Autowired
    private SystemUserLevelService systemUserLevelService;

    /**
     * 订单导出
     *
     * @param request 查询条件
     * @return 文件名称
     */
    @Override
    public String exportOrder(OrderSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (systemAdmin.getMerId() > 0) {
            request.setMerId(systemAdmin.getMerId());
        }
        List<Order> orderList = orderService.findExportList(request);
        if (CollUtil.isEmpty(orderList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "没有可导出的数据！");
        }
        CrmebUtil.setBusinessDataForFT(orderService.getAMC(),orderService.getAPC(), orderService.getAOC());
        List<Integer> merIdList = orderList.stream().filter(e -> e.getMerId() > 0).map(Order::getMerId).distinct().collect(Collectors.toList());
        List<Integer> userIdList = orderList.stream().map(Order::getUid).distinct().collect(Collectors.toList());
        List<String> orderNoList = orderList.stream().map(Order::getOrderNo).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        Map<Integer, User> userMap = userService.getUidMapList(userIdList);
        Map<String, List<OrderDetail>> orderDetailMap = orderDetailService.getMapByOrderNoList(orderNoList);
        Map<String, List<MerchantOrder>> merchantOrderMap = merchantOrderService.getMapByOrderNoList(orderNoList);
        List<OrderExcelVo> voList = CollUtil.newArrayList();
        for (Order order : orderList) {
            OrderExcelVo vo = new OrderExcelVo();
            vo.setType(getOrderType(order.getType()));
            vo.setOrderNo(order.getOrderNo());
            vo.setMerName(order.getMerId() > 0 ? merchantMap.get(order.getMerId()).getName() : "");
            vo.setUserNickname(userMap.get(order.getUid()).getNickname() + "|" + order.getUid());
            vo.setPayPrice(order.getPayPrice().toString());
            vo.setPaidStr(order.getPaid() ? "已支付" : "未支付");
            vo.setPayType(getOrderPayType(order.getPayType()));
            vo.setPayChannel(getOrderPayChannel(order.getPayChannel()));
            vo.setStatus(getOrderStatus(order.getStatus()));
            vo.setRefundStatus(getOrderRefundStatus(order.getRefundStatus()));
            vo.setCreateTime(CrmebDateUtil.dateToStr(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            vo.setProductInfo(getOrderProductInfo(orderDetailMap.get(order.getOrderNo())));
            vo.setRealName(StrUtil.isBlank(merchantOrderMap.get(order.getOrderNo()).get(0).getRealName()) ? "" : merchantOrderMap.get(order.getOrderNo()).get(0).getRealName());
            vo.setUserPhone(StrUtil.isBlank(merchantOrderMap.get(order.getOrderNo()).get(0).getUserPhone()) ? "" : merchantOrderMap.get(order.getOrderNo()).get(0).getUserPhone());
            vo.setUserAddress(StrUtil.isBlank(merchantOrderMap.get(order.getOrderNo()).get(0).getUserAddress()) ? "" : merchantOrderMap.get(order.getOrderNo()).get(0).getUserAddress());
            vo.setUserRemark(StrUtil.isBlank(merchantOrderMap.get(order.getOrderNo()).get(0).getUserRemark()) ? "" : merchantOrderMap.get(order.getOrderNo()).get(0).getUserRemark());
            vo.setMerchantRemark(StrUtil.isBlank(merchantOrderMap.get(order.getOrderNo()).get(0).getMerchantRemark()) ? "" : merchantOrderMap.get(order.getOrderNo()).get(0).getMerchantRemark());
            voList.add(vo);
        }

        /*
          ===============================
          以下为存储部分
          ===============================
         */
        // 上传设置
        String serverPath = (crmebConfig.getImagePath() + "/").replace(" ", "").replace("//", "/");

        // 文件名
        String fileName = "订单导出_".concat(CrmebDateUtil.nowDateTime(DateConstants.DATE_TIME_FORMAT_NUM)).concat(CrmebUtil.randomCount(111111111, 999999999).toString()).concat(".xlsx");

        //自定义标题别名
        LinkedHashMap<String, String> aliasMap = new LinkedHashMap<>();
        aliasMap.put("type", "订单类型");
        aliasMap.put("orderNo", "订单号");
        aliasMap.put("merName", "商户名称");
        aliasMap.put("userNickname", "用户昵称");
        aliasMap.put("payPrice", "实际支付金额");
        aliasMap.put("paidStr", "支付状态");
        aliasMap.put("payType", "支付方式");
        aliasMap.put("payChannel", "支付渠道");
        aliasMap.put("status", "订单状态");
        aliasMap.put("refundStatus", "退款状态");
        aliasMap.put("createTime", "创建时间");
        aliasMap.put("productInfo", "商品信息");
        aliasMap.put("realName", "收货人");
        aliasMap.put("userPhone", "收货电话");
        aliasMap.put("userAddress", "收货地址");
        aliasMap.put("userRemark", "用户备注");
        aliasMap.put("merchantRemark", "商户备注");

        return ExportUtil.exportExcel(fileName, "订单导出", voList, aliasMap, serverPath);
    }

    /**
     * 用户导出
     * @param request 查询条件
     * @return 文件名称
     */
    @Override
    public String exportUser(UserSearchRequest request) {
        List<User> userList = userService.findExportList(request);
        if (CollUtil.isEmpty(userList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "没有可导出的数据！");
        }
        List<UserExcelVo> voList = CollUtil.newArrayList();
        for (User user : userList) {
            UserExcelVo vo = new UserExcelVo();
            vo.setId(user.getId());
            vo.setNickname(user.getNickname());
            vo.setBirthday(user.getBirthday());
            vo.setTags(getTags(user.getTagId()));
            vo.setPhone(user.getPhone());
            vo.setCountry(user.getCountry());
            vo.setProvince(user.getProvince());
            vo.setCity(user.getCity());
            vo.setSex(sexToStr(user.getSex()));
            vo.setIntegral(user.getIntegral());
            vo.setExperience(user.getExperience());
            vo.setNowMoney(user.getNowMoney());
            vo.setBrokeragePrice(user.getBrokeragePrice());
            SystemUserLevel systemUserLevel = systemUserLevelService.getByLevelId(user.getLevel());
            vo.setLevel(ObjectUtil.isNotNull(systemUserLevel) ? systemUserLevel.getGrade() : 0);
            vo.setPayCount(user.getPayCount());
            vo.setIsPromoter(booleanToStr(user.getIsPromoter()));
            vo.setPromoterTime(user.getPromoterTime());
            vo.setRegisterType(user.getRegisterType());
            vo.setStatus(user.getStatus() ? "正常" : "禁止");
            vo.setIsLogoff(booleanToStr(user.getIsLogoff()));
            vo.setLogoffTime(user.getLogoffTime());
            vo.setIsPaidMember(booleanToStr(user.getIsPaidMember()));
            vo.setIsPermanentPaidMember(booleanToStr(user.getIsPermanentPaidMember()));
            vo.setCreateTime(user.getCreateTime());
            voList.add(vo);
        }

        /*
          ===============================
          以下为存储部分
          ===============================
         */
        // 上传设置
        String serverPath = (crmebConfig.getImagePath() + "/").replace(" ", "").replace("//", "/");

        // 文件名
        String fileName = "用户导出_".concat(CrmebDateUtil.nowDateTime(DateConstants.DATE_TIME_FORMAT_NUM)).concat(CrmebUtil.randomCount(111111111, 999999999).toString()).concat(".xlsx");

        //自定义标题别名
        LinkedHashMap<String, String> aliasMap = new LinkedHashMap<>();
        aliasMap.put("id", "用户ID");
        aliasMap.put("nickname", "用户昵称");
        aliasMap.put("phone", "手机号码");
        aliasMap.put("country", "国家");
        aliasMap.put("province", "省份");
        aliasMap.put("city", "城市");
        aliasMap.put("sex", "性别");
        aliasMap.put("birthday", "生日");
        aliasMap.put("tags", "标签");
        aliasMap.put("integral", "用户积分");
        aliasMap.put("experience", "用户经验");
        aliasMap.put("nowMoney", "用户余额");
        aliasMap.put("brokeragePrice", "佣金金额");
        aliasMap.put("level", "等级");
        aliasMap.put("payCount", "用户购买次数");
        aliasMap.put("isPromoter", "是否为推广员");
        aliasMap.put("promoterTime", "成为分销员时间");
        aliasMap.put("registerType", "注册类型");
        aliasMap.put("status", "状态");
        aliasMap.put("isLogoff", "是否注销");
        aliasMap.put("logoffTime", "注销时间");
        aliasMap.put("isPaidMember", "是否付费会员");
        aliasMap.put("isPermanentPaidMember", "是否永久付费会员");
        aliasMap.put("createTime", "创建时间");

        return ExportUtil.exportExcel(fileName, "用户导出", voList, aliasMap, serverPath);
    }

    private String getTags(String tagId) {
        if (StrUtil.isBlank(tagId)) return "";
        List<Integer> tagIdList = CrmebUtil.stringToArray(tagId);
        List<UserTag> tagList = userTagService.findByIdList(tagIdList);
        if (CollUtil.isEmpty(tagList)) return "";
        return tagList.stream().map(UserTag::getName).collect(Collectors.joining(","));
    }

    private String booleanToStr(Boolean b) {
        return b ? "是" : "否";
    }

    private String sexToStr(Integer sex) {
        String sexStr = "";
        switch (sex) {
            case 0:
                sexStr = "未知";
                break;
            case 1:
                sexStr = "男";
                break;
            case 2:
                sexStr = "女";
                break;
            case 3:
                sexStr = "保密";
                break;
        }
        return sexStr;
    }

    /**
     * 分销员导出
     * @param request 查询条件
     * @return 文件名称
     */
    @Override
    public String exportRetailStorePeople(RetailStorePeopleSearchRequest request) {
        List<User> userList = userService.findRetailStorePeopleList(request);
        if (CollUtil.isEmpty(userList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "没有可导出的数据！");
        }
        List<RetailStorePeopleExportDto> dtoList = CollUtil.newArrayList();
        userList.forEach(user -> {
            RetailStorePeopleExportDto userDto = new  RetailStorePeopleExportDto();
            BeanUtils.copyProperties(user, userDto);
            // 上级推广员名称
            userDto.setSpreadNickname("无");
            if (ObjectUtil.isNotNull(user.getSpreadUid()) && user.getSpreadUid() > 0) {
                User spreadUser = userService.getById(user.getSpreadUid());
                userDto.setSpreadNickname(Optional.ofNullable(spreadUser.getNickname()).orElse("--"));
            }

            List<UserBrokerageRecord> recordList = userBrokerageRecordService.getSpreadListByUid(user.getId());
            if (CollUtil.isEmpty(recordList)) {
                // 推广订单数
                userDto.setSpreadOrderNum(0);
                // 推广订单额
                userDto.setSpreadOrderTotalPrice(BigDecimal.ZERO);
                // 佣金总金额
                userDto.setTotalBrokeragePrice(BigDecimal.ZERO);
                // 已提现金额
                userDto.setExtractCountPrice(BigDecimal.ZERO);
                // 提现次数
                userDto.setExtractCountNum(0);
                // 冻结中佣金
                userDto.setFreezeBrokeragePrice(BigDecimal.ZERO);
            } else {
                // 推广订单数
                userDto.setSpreadOrderNum(recordList.size());
                // 佣金总金额
                userDto.setTotalBrokeragePrice(recordList.stream().map(UserBrokerageRecord::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
                // 推广订单额
                List<String> orderNoList = recordList.stream().map(UserBrokerageRecord::getLinkNo).collect(Collectors.toList());
                BigDecimal spreadOrderTotalPrice = orderService.getSpreadOrderTotalPriceByOrderList(orderNoList);
                userDto.setSpreadOrderTotalPrice(spreadOrderTotalPrice);

                UserClosing userClosing = userClosingService.getUserExtractByUserId(user.getId());
                // 已提现金额
                userDto.setExtractCountPrice(userClosing.getClosingPrice());
                // 提现次数
                userDto.setExtractCountNum(userClosing.getId());
                // 冻结中佣金
                userDto.setFreezeBrokeragePrice(userBrokerageRecordService.getFreezePrice(user.getId()));
            }
            dtoList.add(userDto);
        });
        /*
          ===============================
          以下为存储部分
          ===============================
         */
        // 上传设置
        String serverPath = (crmebConfig.getImagePath() + "/").replace(" ", "").replace("//", "/");

        // 文件名
        String fileName = "分销员导出_".concat(CrmebDateUtil.nowDateTime(DateConstants.DATE_TIME_FORMAT_NUM)).concat(CrmebUtil.randomCount(111111111, 999999999).toString()).concat(".xlsx");

        //自定义标题别名
        LinkedHashMap<String, String> aliasMap = new LinkedHashMap<>();
        aliasMap.put("id", "用户ID");
        aliasMap.put("nickname", "用户昵称");
        aliasMap.put("phone", "手机号码");
        aliasMap.put("brokeragePrice", "佣金余额");
        aliasMap.put("spreadUid", "推广人id");
        aliasMap.put("spreadNickname", "上级推广员名称");
        aliasMap.put("payCount", "用户购买次数");
        aliasMap.put("spreadCount", "推广用户数");
        aliasMap.put("spreadOrderNum", "推广订单数");
        aliasMap.put("spreadOrderTotalPrice", "推广订单额");
        aliasMap.put("totalBrokeragePrice", "佣金总金额");
        aliasMap.put("spreadPeopleCount", "推广用户数量");
        aliasMap.put("extractCountPrice", "已提现金额");
        aliasMap.put("extractCountNum", "已提现次数");
        aliasMap.put("freezeBrokeragePrice", "冻结佣金");
        aliasMap.put("promoterTime", "成为分销员时间");

        return ExportUtil.exportExcel(fileName, "分销员导出", dtoList, aliasMap, serverPath);
    }

    private String getOrderProductInfo(List<OrderDetail> orderDetails) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail orderDetail = orderDetails.get(i);
            stringBuilder.append(StrUtil.format("{}  {} * {}", orderDetail.getProductName(), orderDetail.getPayPrice(), orderDetail.getPayNum()));
            if ((i + 1) < orderDetails.size()) {
                stringBuilder.append("\r\n");
            }
        }
        return stringBuilder.toString();
    }

    private String getOrderType(Integer type) {
        String typeStr = "";
        switch (type) {
            case 0:
                typeStr = "普通";
                break;
            case 1:
                typeStr = "秒杀";
                break;
            case 2:
                typeStr = "拼团";
                break;
                // 视频号订单待定
        }
        return typeStr;
    }

    private String getOrderRefundStatus(Integer refundStatus) {
        String refundStatusStr = "";
        switch (refundStatus) {
            case 0:
                refundStatusStr = "未退款";
                break;
            case 1:
                refundStatusStr = "申请中";
                break;
            case 2:
                refundStatusStr = "部分退款";
                break;
            case 3:
                refundStatusStr = "已退款";
                break;
        }
        return refundStatusStr;
    }

    private String getOrderStatus(Integer status) {
        String statusStr = "";
        switch (status) {
            case 0:
                statusStr = "待支付";
                break;
            case 1:
                statusStr = "待发货";
                break;
            case 2:
                statusStr = "部分发货";
                break;
            case 3:
                statusStr = "待使用";
//                statusStr = "待核销";
                break;
            case 4:
                statusStr = "待收货";
                break;
            case 5:
                statusStr = "已收货";
                break;
            case 6:
                statusStr = "已完成";
                break;
            case 9:
                statusStr = "已取消";
                break;
        }
        return statusStr;
    }

    private String getOrderPayChannel(String payChannel) {
        String payChannelStr = "";
        switch (payChannel) {
            case "public":
                payChannelStr = "公众号";
                break;
            case "mini":
                payChannelStr = "小程序";
                break;
            case "h5":
                payChannelStr = "微信网页支付";
                break;
            case "yue":
                payChannelStr = "余额";
                break;
            case "wechatIos":
                payChannelStr = "微信Ios";
                break;
            case "wechatAndroid":
                payChannelStr = "微信Android";
                break;
            case "alipay":
                payChannelStr = "支付宝";
                break;
            case "alipayApp":
                payChannelStr = "支付宝App";
                break;
        }
        return payChannelStr;
    }

    private String getOrderPayType(String payType) {
        String payTypeStr = "";
        switch (payType) {
            case "weixin":
                payTypeStr = "微信支付";
                break;
            case "alipay":
                payTypeStr = "支付宝支付";
                break;
            case "yue":
                payTypeStr = "余额支付";
                break;
        }
        return payTypeStr;
    }
}

