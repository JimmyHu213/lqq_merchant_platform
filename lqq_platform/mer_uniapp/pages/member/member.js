export function handleGoRecharge(recharge, merId) {
    uni.showLoading({
        title: '正在提交中'
    });
    setTimeout(() => {
        uni.navigateTo({
            url: `/pages/goods/order_payment/index?orderNo=other&payPrice=${recharge.price}&fromType=shoppingCredits&merId=${merId}&packageId=${recharge.id}`
        });
        uni.hideLoading();
    }, 1000)
}