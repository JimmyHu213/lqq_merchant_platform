package com.zbkj.admin.task.card;

import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.CardTimesTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 次卡商品到期下架定时任务
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
@Component("CardTimesProductValidityTask")
public class CardTimesProductValidityTask {

    private static final Logger logger = LoggerFactory.getLogger(CardTimesProductValidityTask.class);

    @Autowired
    private CardTimesTaskService cardTimesTaskService;


    public void cardTimesProductValidity() {
        logger.info("---CardTimesProductValidityTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            cardTimesTaskService.cardTimesProductValidity();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("CardTimesProductValidityTask.task" + " | msg : " + e.getMessage());

        }

    }

}
