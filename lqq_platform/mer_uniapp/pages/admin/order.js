// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

const getServiceList = (workOrderNoInfo) => {
    return [
        {
            label: "服务人员",
            value: (workOrderNoInfo.serviceStaffName || workOrderNoInfo.serviceStaff.name) || ''
        },
        {
            label: "联系电话",
            value: (workOrderNoInfo.serviceStaffPhone || workOrderNoInfo.serviceStaff.phone)  || "",
            isPhone: true
        },
        {
            label: "开始服务时间",
            value: workOrderNoInfo?.serviceStartTime || "-",
            isDate: true
        },
        {
            label: "结束服务时间",
            value: workOrderNoInfo?.serviceEndTime || "-",
            isDate: true
        },
        {
            label: "服务时长",
            value:  workOrderNoInfo?.serviceDuration || "-",
        }
    ];
};

// 打卡信息
export const getSignList = (workOrderNoInfo) => {
    return [
        {
            label: "打卡时间",
            value: workOrderNoInfo?.serviceStartTime || "-"
        },
        {
            label: "打卡备注",
            value: workOrderNoInfo?.clockInRemark || "-"
        },
        {
            label: "打卡照片",
            isImage: true,
            value: workOrderNoInfo?.clockInPhoto.split(',')  || []
        }
    ];
};

// 一字马开工单信息
export const getTableList = (workOrderNoInfo) => {
    const reservationConfig = uni.getStorageSync('reservationConfig'); // 商户预约设置
    if (!workOrderNoInfo) {
        return [];
    }

    const result = [];
    if(workOrderNoInfo.serviceStatus > 1){
        result.push({
            title: "服务信息",
            list: getServiceList(workOrderNoInfo)
        });
    }


    // 已经打过卡 serviceType	服务类型:1-上门服务，2-到店服务 clockInType	打卡类型：1-无需打卡，2-人工打卡
    if (workOrderNoInfo.serviceStatus > 2 && workOrderNoInfo.serviceType ===1) {
        result.push({
            title: "打卡信息",
            list: getSignList(workOrderNoInfo)
        });
    }
    
    return result;
}

export function getSplitOrderTableList (workOrderNoInfo) {
    const result = [];
    result.push({
        list:  [
            {
                label: "预约时间",
                value: workOrderNoInfo?.reservationDate + ' , ' + workOrderNoInfo?.reservationTimeSlot || "-"
            },
            {
                label: "预约信息",
                value: workOrderNoInfo?.userName + workOrderNoInfo?.userPhone  || "-"
            },
            {
                label: "预约地址",
                value: workOrderNoInfo?.userAddress  || "-",
                isShow: workOrderNoInfo.serviceType === 2 ? true : false
            }
        ]
    });
    return result;
}

