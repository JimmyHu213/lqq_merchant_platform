<template>
	<view id="home" :data-theme="theme" :style="[pageStyle]">
    <tui-skeleton v-if="showSkeleton"></tui-skeleton>
    <!-- 有网内容 -->
    <view class="page-index tui-skeleton page_count" :class="{'bgf':navIndex >0}"
          :style="{visibility: showSkeleton ? 'hidden' : 'visible'}">
      <diy-page ref="diyComponentRef" :merId="merId" :merMemberInfo="merMemberInfo" @updateMemberInfo="updateMemberInfo"
                :isScrolled="isScrolled" :componentData="componentData" v-if="componentData && componentData.value"></diy-page>
      <!-- 底部导航距离，做兼容处理的-->
      <view class="footerBottom"></view>
    </view>
		<coupon-and-classify v-if="tabActive >0 && merId" :tabActive="tabActive" :merId="merId"></coupon-and-classify>

    <page-footer :merId="merId" v-if="merId" fromTo="merHome"></page-footer>
<!--    <view class="footer">-->
<!--			<view v-for="(item, index) in tabs" :key="index" :class="{ active: tabActive === index }" class="item"-->
<!--				@click="handleChangetab(index)">-->
<!--				<view :class="['iconfont', item.icon]"></view>-->
<!--				<view>{{ item.name }}</view>-->
<!--			</view>-->
<!--			<view class="item" @click="handleCustomer">-->
<!--				<view class="iconfont icon-ic_customerservice"></view>-->
<!--				<view>联系客服</view>-->
<!--			</view>-->
<!--      <view class="item" @click="handleMembership">-->
<!--        <view class="iconfont icon-ic_customerservice"></view>-->
<!--        <view>加入会员</view>-->
<!--      </view>-->
<!--      <view class="item" @click="handleShoppingCredit">-->
<!--        <view class="iconfont icon-ic_customerservice"></view>-->
<!--        <view>购物金</view>-->
<!--      </view>-->
<!--      <view class="item" @click="handleRules">-->
<!--        <view class="iconfont icon-ic_customerservice"></view>-->
<!--        <view>会员规则</view>-->
<!--      </view>-->
<!--		</view>-->
	</view>
</template>

<script>
	// +----------------------------------------------------------------------
	// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
	// +----------------------------------------------------------------------
	// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------
	import {
		chatConfig
	} from '@/utils/consumerType.js'
	import tuiSkeleton from '@/components/base/tui-skeleton.vue';
	import couponAndClassify from '../components/couponAndClassify/index.vue';
	import {
		getMerIndexInfoApi,
	} from '@/api/merchant.js';
	import {
		mapGetters
	} from "vuex";
	import {
		silenceBindingSpread,
	} from '@/utils/index.js';
  import {getMerDiyInfoApi} from "../../../api/merchant";
  import diyPage from "@/subPackage/pages/diyPage/index.vue";
  import {memberUserInfoApi} from "../../member/memberApi";
  import pageFooter from "../../../components/pageFooter";
	const arrTemp = ["beforePay", "afterPay", "createBargain", "pink"];
	var statusBarHeight = uni.getSystemInfoSync().statusBarHeight + 'px';
	let app = getApp();
	export default {
		computed: mapGetters(['isLogin', 'uid', 'globalData', 'bottomNavigationIsCustom']),
		components: {
      pageFooter,
			tuiSkeleton,
      diyPage,
			couponAndClassify
		},
		data() {
			return {
        merMemberInfo: {}, //会员信息
        componentData: {},
				isScrolled: false, //是否开始滚动
				showSkeleton: true, //骨架屏显示隐藏
				isNodes: 0, //控制什么时候开始抓取元素节点,只要数值改变就重新抓取
				statusBarHeight: statusBarHeight,
				navIndex: 0, //判断首页显示内容，1显示分类页和商品，0首页
				site_name: '', //首页title
				configApi: {}, //分享类容配置
				theme: app.globalData.theme,
				// #ifdef APP-PLUS || MP
				isFixed: true,
				// #endif
				// #ifdef H5
				isFixed: false,
				// #endif
				domOffsetTop: 50,
				diyId: 0, //DIYID
				bgPic: '', //背景图片
				bgTabVal: '', //背景图片样式
				pageStyle: {},

				merchantInfo: {},
				merId: 0,
				// 底部菜单
				tabs: [{
						icon: 'icon-ic_mall',
						name: '首页',
					},
					{
						icon: 'icon-a-ic_Picturearrangement',
						name: '分类'
					},
					{
						icon: 'icon-ic_coupon',
						name: '领券'
					}
				],
				tabActive: 0, // 底部切换
			}
		},
		onLoad(options) {
      // 仅仅小程序扫码进入获取商品id，商品类型
      if (options.scene) {
        let value = this.$util.getUrlParams(decodeURIComponent(options.scene));
        this.diyId = value.id ? value.id : 0;
        this.merId = value.merId ? value.merId : 0;
        if (value.sd) this.$store.commit('Change_Spread', value.sd);
      }else{
        this.merId = options.merId ? parseInt(options.merId) : 0;
        this.diyId = options.id ? parseInt(options.id) : 0;
        if (options.sd) this.$store.commit('Change_Spread', options.sd); //分享id
      }
      this.getMerIndexInfo()
      this.diyData();
		},
		// #ifdef MP
		/**
		 * 用户点击右上角分享到朋友圈
		 */
		onShareTimeline() {
			return {
				title: this.merchantInfo.name,
				imageUrl: this.merchantInfo.avatar,
        query: `merId=${this.merId}&sd=${this.uid}`
			};
		},
		/**
		 * 用户点击右上角分享
		 */
		onShareAppMessage() {
			return {
				title: this.merchantInfo.name,
				imageUrl: this.merchantInfo.avatar,
				path: `/pages/merchant/home/index?merId=${this.merId}&sd=${this.uid}`
			};
		},
		// #endif
		// 滚动监听
		onPageScroll(e) {
			// 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
			uni.$emit('scroll');
			if (e.scrollTop > this.domOffsetTop) {
				this.isScrolled = true;
			}
			if (e.scrollTop < this.domOffsetTop) {
				this.$nextTick(() => {
					this.isScrolled = false;
				});
			}
		},
		methods: {
      // 加入会员后的调用
      updateMemberInfo(){
        this.getMemberInfo()
      },
      //店铺会员信息
      getMemberInfo(){
        this.$store.dispatch("getMerMemberInfo", this.merId).then(data => {
          this.merMemberInfo = data
        });
      },
      //购物金
      handleShoppingCredit(){
        this.$util.navigateTo(`/pages/member/recharge_amount/index?merId=${this.merId}`)
      },
      // 会员规则
      handleRules(){
        this.$util.navigateTo(`/pages/member/membership_rules/index?merId=${this.merId}`)
      },
      handleMembership(){
        this.$refs.membershipRef.open()
      },
      /**
       * 获取DIY
       * @param {number} id
       * @param {boolean} type 区分是否是微页面
       */
      diyData() {
        let that = this;
        uni.showLoading({
          title: '加载中...'
        });
        getMerDiyInfoApi(this.merId, this.diyId).then(res => {
          uni.setNavigationBarTitle({
            title: res.data.title
          });
          let data = res.data;
          this.componentData = data
          this.reloadData();
          that.styleConfig = that.$util.objToArr(res.data.value);
          that.pageStyle = {
            'background-color': data.isBgColor === 1 ? res.data.colorPicker : '',
            'background-image': data.isBgPic === 1 ? `url(${res.data.bgPic})` : '',
            'background-repeat': res.data.bgTabVal === 1 ? 'repeat-y' : 'no-repeat',
            'background-size': res.data.bgTabVal === 2 ? 'cover' : 'contain'
          };
          uni.hideLoading();
          setTimeout(() => {
            that.isNodes++;
          }, 100);
        }).catch(err => {
          return that.$util.Tips({
            title: err
          });
          uni.hideLoading();
        });
      },
			//客服跳转
			handleCustomer: function() {
				chatConfig(this.merchantInfo);
			},
			//商户信息
			getMerIndexInfo() {
				let that = this;
				getMerIndexInfoApi(this.merId).then(res => {
					this.merchantInfo = res.data
					uni.setNavigationBarTitle({
						title: res.data.name
					})
					this.$store.commit('MERCHANTJINFO', res.data);
					this.skeletonShow = false;
				}).catch(err => {
					that.loading = false;
					that.loadTitle = '加载更多';
					this.skeletonShow = false;
				});
			},
			//底部切换
			handleChangetab(param) {
				this.tabActive = param;
			},
			//清除骨架屏
			reloadData() {
				this.showSkeleton = false;
			},
			// 关闭优惠券弹窗
			closeCouponPop() {
				this.$refs.couponRef.close();
			},
		},
		onShow() {
      if(this.merId && this.isLogin)this.getMemberInfo()
			//分销绑定
			silenceBindingSpread(this.isLogin, this.globalData.spread);
		},
		mounted() {
		},
		onReachBottom() {
			// this.$refs.recommendIndex.get_host_product();
		}
	}
</script>
<style>
	page {
		height: auto;
		display: flex;
		flex-direction: column;
		height: 100%;
		/* #ifdef H5 */
		//background-color: #fff;
		/* #endif */
	}
</style>
<style lang="scss" scoped>
	.footer {
		position: fixed;
		bottom: 0;
		left: 0;
		z-index: 1;
		display: flex;
		width: 100%;
		padding-top: 12rpx;
		padding-bottom: constant(safe-area-inset-bottom); ///兼容 IOS<11.2/
		padding-bottom: env(safe-area-inset-bottom); ///兼容 IOS>11.2/
		background-color: #FFFFFF;
		opacity: 0.96;

		.item {
			flex: 1;
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;
			font-weight: 500;
			font-size: 20rpx;
			color: #282828;

			.iconfont {
				font-size: 43rpx !important;
			}
		}

		.active {
			@include main_color(theme);
		}
	}
</style>