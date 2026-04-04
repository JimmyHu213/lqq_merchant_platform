package com.zbkj.common.constants;

/**
 * 工单常量类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/6
 */
public class WorkOrderConstants {

    /** 服务状态—未分配 */
    public static final Integer SERVICE_STATUS_AWAIT = 1;
    /** 服务状态—待服务 */
    public static final Integer SERVICE_STATUS_AWAIT_SERVICE = 2;
    /** 服务状态—服务中 */
    public static final Integer SERVICE_STATUS_SERVICING = 3;
    /** 服务状态—服务结束 */
    public static final Integer SERVICE_STATUS_END = 4;


    /** 退款状态—未退款 */
    public static final Integer REFUND_STATUS_NO = 0;
    /** 退款状态—退款审核中 */
    public static final Integer REFUND_STATUS_AUDIT_ING = 1;
    /** 退款状态—已退款 */
    public static final Integer REFUND_STATUS_REFUND = 2;

    /** 打卡状态—未打卡 */
    public static final Integer CLOCK_IN_STATUS_NO = 0;
    /** 打卡状态—自动打卡 */
    public static final Integer CLOCK_IN_STATUS_AUTO = 1;
    /** 打卡状态—人工打卡 */
    public static final Integer CLOCK_IN_STATUS_MANUAL = 2;

    /** 服务类型—上门服务 */
    public static final Integer SERVICE_TYPE_HOME_SERVICE = 1;
    /** 服务类型—到店服务 */
    public static final Integer SERVICE_TYPE_TO_STORE = 2;

    /** 分配类型—未分配 */
    public static final Integer ALLOCATE_TYPE_NO = 0;
    /** 分配类型—派单 */
    public static final Integer ALLOCATE_TYPE_APPOINT = 1;
    /** 分配类型—抢单 */
    public static final Integer ALLOCATE_TYPE_WREST = 2;

}
