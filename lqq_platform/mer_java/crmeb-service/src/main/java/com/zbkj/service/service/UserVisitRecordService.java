package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.record.UserVisitRecord;
import com.zbkj.common.request.DateRequest;

/**
 * UserVisitRecordService 接口
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
public interface UserVisitRecordService extends IService<UserVisitRecord> {

    /**
     * 通过日期获取浏览量
     * @param date 日期
     * @return Integer
     */
    Integer getPageviewsByDate(String date);

    /**
     * 获取活跃用户数
     * @param dateRequest 日期请求对象
     * @return Integer
     */
    Integer getAliveUserNum(DateRequest dateRequest);

    /**
     * 获取活跃用户数量
     * @param date 日期
     */
    Integer getUserAliveNum(String date, DateRequest dateRequest);
}