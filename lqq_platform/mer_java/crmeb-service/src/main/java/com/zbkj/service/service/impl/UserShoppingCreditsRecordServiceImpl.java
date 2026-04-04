package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.user.UserShoppingCreditsRecord;
import com.zbkj.service.dao.UserShoppingCreditsRecordDao;
import com.zbkj.service.service.UserShoppingCreditsRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author HZW
 * @description UserShoppingCreditsRecordServiceImpl 接口实现
 * @date 2026-01-06
 */
@Service
public class UserShoppingCreditsRecordServiceImpl extends ServiceImpl<UserShoppingCreditsRecordDao, UserShoppingCreditsRecord> implements UserShoppingCreditsRecordService {
    @Resource
    private UserShoppingCreditsRecordDao dao;

    /**
     * 获取移动端列表
     *
     * @param userId     用户ID
     * @param merId      商户ID
     * @param searchType 查询类型 all-全部，consume-消费，recharge-充值，refund-退款
     */
    @Override
    public List<UserShoppingCreditsRecord> findFrontList(Integer userId, Integer merId, String searchType) {
        LambdaQueryWrapper<UserShoppingCreditsRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserShoppingCreditsRecord::getUid, userId);
        lqw.eq(UserShoppingCreditsRecord::getMerId, merId);
        switch (searchType) {
            case "consume":
                lqw.eq(UserShoppingCreditsRecord::getLinkType, "order");
                break;
            case "recharge":
                lqw.eq(UserShoppingCreditsRecord::getLinkType, "recharge");
                break;
            case "refund":
                lqw.eq(UserShoppingCreditsRecord::getLinkType, "refund");
                break;
        }
        lqw.orderByDesc(UserShoppingCreditsRecord::getId);
        return dao.selectList(lqw);
    }

    @Override
    public List<UserShoppingCreditsRecord> findByMerAndUid(Integer merId, Integer userId) {
        LambdaQueryWrapper<UserShoppingCreditsRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserShoppingCreditsRecord::getUid, userId);
        lqw.eq(UserShoppingCreditsRecord::getMerId, merId);
        lqw.orderByDesc(UserShoppingCreditsRecord::getId);
        return dao.selectList(lqw);
    }

    @Override
    public Boolean deleteByLinkNo(String linkNo) {
        LambdaUpdateWrapper<UserShoppingCreditsRecord> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(UserShoppingCreditsRecord::getLinkNo, linkNo);
        return remove(wrapper);
    }

    /**
     * 获取购物金记录
     * @param linkNo 关联单号
     * @param linkType 关联类型（order,recharge,refund）
     */
    @Override
    public UserShoppingCreditsRecord getByLinkNoAndType(String linkNo, String linkType) {
        LambdaQueryWrapper<UserShoppingCreditsRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserShoppingCreditsRecord::getLinkNo, linkNo);
        lqw.eq(UserShoppingCreditsRecord::getLinkType, linkType);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }
}

