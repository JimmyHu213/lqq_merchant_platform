package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.VisitRecordConstants;
import com.zbkj.common.model.record.UserVisitRecord;
import com.zbkj.common.request.DateRequest;
import com.zbkj.service.dao.UserVisitRecordDao;
import com.zbkj.service.service.UserVisitRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * UserVisitRecordServiceImpl 接口实现
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
public class UserVisitRecordServiceImpl extends ServiceImpl<UserVisitRecordDao, UserVisitRecord> implements UserVisitRecordService {

    @Resource
    private UserVisitRecordDao dao;

    /**
     * 通过日期获取浏览量
     * @param date 日期
     * @return Integer
     */
    @Override
    public Integer getPageviewsByDate(String date) {
        QueryWrapper<UserVisitRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("date", date);
        return dao.selectCount(wrapper);
    }

    /**
     * 获取活跃用户数
     * @param dateRequest 日期请求对象
     * @return Integer
     */
    @Override
    public Integer getAliveUserNum(DateRequest dateRequest) {
        QueryWrapper<UserVisitRecord> wrapper = new QueryWrapper<>();
        wrapper.select("COUNT(DISTINCT uid) as userCount");
        wrapper.eq("visit_type", VisitRecordConstants.VISIT_TYPE_DETAIL);
        wrapper.between("date", dateRequest.getStartTime(), dateRequest.getEndTime());

        List<Map<String, Object>> result = dao.selectMaps(wrapper);
        if (result != null && !result.isEmpty()) {
            Map<String, Object> map = result.get(0);
            return ((Number) map.get("userCount")).intValue();
        }
        return 0;
    }

    /**
     * 获取新增用户数量
     * @param date 日期
     */
    @Override
    public Integer getUserAliveNum(String date, DateRequest dateRequest) {
        QueryWrapper<UserVisitRecord> wrapper = new QueryWrapper<>();
        wrapper.select("COUNT(DISTINCT uid) as userCount");
        wrapper.eq("visit_type", VisitRecordConstants.VISIT_TYPE_DETAIL);
        wrapper.like("date", date);
        if (date.matches("^\\d{4}-\\d{2}$")) {
            if (date.equals(dateRequest.getStartTime().substring(0, 7))) {
                wrapper.ge("date", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 7))) {
                wrapper.le("date", dateRequest.getEndTime());
            }
        } else if (date.matches("^\\d{4}$")) {
            if (date.equals(dateRequest.getStartTime().substring(0, 4))) {
                wrapper.ge("date", dateRequest.getStartTime());
            } else if (date.equals(dateRequest.getEndTime().substring(0, 4))) {
                wrapper.le("date", dateRequest.getEndTime());
            }
        }
        List<Map<String, Object>> result = dao.selectMaps(wrapper);
        if (result != null && !result.isEmpty()) {
            Map<String, Object> map = result.get(0);
            return ((Number) map.get("userCount")).intValue();
        }
        return 0;
    }


}

