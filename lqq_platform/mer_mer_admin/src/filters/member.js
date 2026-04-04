
export function memRefundStatusFilter(status) {
    const statusMap = {
        0: '待审核',
        1: '商家拒绝',
        2: '退款中',
        3: '退款成功',
        4: '用户撤销',
    };
    return statusMap[status];
}