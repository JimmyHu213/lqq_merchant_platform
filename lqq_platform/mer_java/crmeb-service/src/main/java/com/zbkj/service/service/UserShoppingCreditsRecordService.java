package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.user.UserShoppingCreditsRecord;

import java.util.List;

/**
 * @author HZW
 * @description UserShoppingCreditsRecordService 接口
 * @date 2026-01-06
 */
public interface UserShoppingCreditsRecordService extends IService<UserShoppingCreditsRecord> {

    /**
     * 获取移动端列表
     *
     * @param userId     用户ID
     * @param merId      商户ID
     * @param searchType 查询类型 all-全部，consume-消费，recharge-充值，refund-退款
     */
    List<UserShoppingCreditsRecord> findFrontList(Integer userId, Integer merId, String searchType);

    List<UserShoppingCreditsRecord> findByMerAndUid(Integer merId, Integer userId);

    /**
     * 删除购物金记录
     */
    Boolean deleteByLinkNo(String linkNo);

    /**
     * 获取购物金记录
     * @param linkNo 关联单号
     * @param linkType 关联类型（order,recharge,refund）
     */
    UserShoppingCreditsRecord getByLinkNoAndType(String linkNo, String linkType);
}