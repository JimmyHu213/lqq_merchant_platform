import {
	getCoupons,
	setCouponReceive
} from '@/api/api.js';
import {
	toLogin
} from '@/libs/login.js';
import {
	mapGetters
} from "vuex";
export default {
	computed: {
		...mapGetters(['isLogin']),
	},
	data() {
		return {
			couponsList: [],
			loadingcoupon: false,
			loadendcoupon: false,
			page: 1,
			limit: 10,
			merid: 0,
			loadTitle: '显示更多',
			isShow: false,
			identityType: 0
		}
	},
	methods: {
		// 领取优惠券
		async fetchReceiveCoupon(item){
			if(!this.isLogin) return toLogin();
			try {
				uni.showLoading({
					title: '加载中...'
				});
				await setCouponReceive(item.id)
				uni.showToast({
					title: '领取成功',
					icon: 'none'
				})
				uni.hideLoading();
				item.isUse = !item.isUse
			}catch (e) {
				uni.showToast({
					title: e,
					icon: 'none'
				})
				uni.hideLoading();
			}
		},
		// 获取商铺优惠券
		getCouponList: function() {
			let that = this
			if (that.loadendcoupon) return false;
			if (that.loadingcoupon) return false;
			that.loadingcoupon = true;
			getCoupons({
				page: that.page,
				limit: that.limit,
				category: this.type || 0, // 默认为1，如果组件中没有type
				merId: this.merid,
				identityType: this.identityType
			}).then(res => {
				let list = res.data.list,
					loadend = list.length < that.limit;
				let couponsList = that.$util.SplitArray(list, that.couponsList);
				that.$set(that, 'couponsList', couponsList);
				that.loadendcoupon = loadend;
				that.loadTitle = loadend ? '到底了~~' : '显示更多';
				that.page = that.page + 1;
				that.loadingcoupon = false;
				that.isShow = true;
			}).catch(err => {
				that.loadingcoupon = false;
				that.loadTitle = '显示更多';
			});
		},
		//去使用的商品列表
		goUseCouponPro(item, type, isUserReceive){
			this.$util.navigateTo(`/pages/goods/coupon_goods_list/index?type=${type}&userCouponId=${item.id}&money=${item.money}&minPrice=${item.minPrice}&isUserReceive=${isUserReceive}`)
		}
	}
}