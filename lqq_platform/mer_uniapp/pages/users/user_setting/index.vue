<template>
	<view :data-theme="theme">
		<view class='personal-data borderPad'>
			<view class='list borRadius14 bg--w111-fff mt20'>
				<view class='item acea-row row-between-wrapper'>
					<view class="acea-row row-between-wrapper">
						<view class="pictrue mr20">
							<image :src='avatarUrl'></image>
						</view>
						<view class="name">
							{{userInfo && userInfo.nickname ? userInfo.nickname : ''}}
						</view>
					</view>
					<view class="input" @click="goEdit()">
						个人信息<text class="iconfont icon-ic_rightarrow"></text>
					</view>
				</view>
			</view>
			<view class='list borRadius14 mt20 bg--w111-fff'>
				<view class='item acea-row row-between-wrapper'>
					<view>ID号</view>
					<view class='input acea-row row-between-wrapper'>
						<input type='text' :value='uid' disabled='true' class='id fontColor'></input>
						<text class='iconfont icon-suozi'></text>
					</view>
				</view>
				<view class='item acea-row row-between-wrapper'>
					<view>手机号码</view>
					<navigator url="/pages/users/user_phone/index" hover-class="none" class="input">
						<view class='input acea-row row-between-wrapper'>
							<span type='number' disabled='true' name='phone'
								class='id fontColor'>{{userInfo.phone}}</span>
							<text class='iconfont icon-ic_rightarrow'></text>
						</view>
					</navigator>
				</view>
				<!-- #ifdef H5 -->
				<view class="item acea-row row-between-wrapper" v-if="userInfo.phone && wechat">
					<view>密码</view>
					<navigator url="/pages/users/user_pwd_edit/index" hover-class="none" class="input">
						点击修改密码<text class="iconfont icon-ic_rightarrow"></text>
					</navigator>
				</view>
				<!-- #endif -->
				<!-- #ifdef APP-PLUS -->
				<view class="item acea-row row-between-wrapper" v-if="userInfo.phone">
					<view>密码</view>
					<navigator url="/pages/users/user_pwd_edit/index" hover-class="none" class="input">
						点击修改密码<text class="iconfont icon-ic_rightarrow"></text>
					</navigator>
				</view>
				<!-- #endif -->
				<view class='item acea-row row-between-wrapper'>
					<view>地址管理</view>
					<navigator url="/pages/address/user_address_list/index" hover-class="none" class="input">
						立即设置<text class="iconfont icon-ic_rightarrow"></text>
					</navigator>
				</view>
				<!-- #ifdef APP-PLUS -->
				<view class='item acea-row row-between-wrapper'>
					<view>检查更新</view>
					<view class="input" @click="appUpdate">
						立即设置<text class="iconfont icon-ic_rightarrow"></text>
					</view>
				</view>
				<!-- #endif -->
				<!-- #ifdef MP -->
				<view class='item acea-row row-between-wrapper'>
					<view>权限设置</view>
					<view class="input" @click="Setting">
						点击管理<text class="iconfont icon-ic_rightarrow"></text>
					</view>
				</view>
				<!-- #endif -->
				<view class="item acea-row row-between-wrapper">
					<view>资质证明</view>
					<navigator url="/pages/goods/agreement_info/index?from=intelligentinfo" hover-class="none"
						class="input">
						点击查看<text class="iconfont icon-ic_rightarrow"></text>
					</navigator>
				</view>
				<view class="item acea-row row-between-wrapper">
					<view>协议规则</view>
					<navigator url="/pages/goods/agreement_rules/index" hover-class="none" class="input">
						点击查看<text class="iconfont icon-ic_rightarrow"></text>
					</navigator>
				</view>
				<view class="item acea-row row-between-wrapper">
					<view>账号注销</view>
					<view class="input" @click="changeCancel">
						注销后无法恢复<text class="iconfont icon-ic_rightarrow"></text>
					</view>
				</view>
				<!-- #ifdef H5 -->
				<view class="logOut cart-color acea-row row-center-wrapper" @click="outLogin"
					v-if="!this.$wechat.isWeixin() || (this.$wechat.isWeixin() && publicLoginType ==2)">退出登录</view>
				<!-- #endif -->
				<!-- #ifdef APP-PLUS -->
				<view class="logOut cart-color acea-row row-center-wrapper" @click="outLogin">退出登录</view>
				<!-- #endif -->
			</view>
		</view>
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
	import animationType from '@/utils/animationType.js'
	import {
		mapGetters
	} from "vuex";
  import {goToAgreement} from "@/libs/order";
  import {toLogin} from "../../../libs/login";
	let app = getApp();
	export default {
		data() {
			return {
				theme: app.globalData.theme,
				wechat: false,
				loginType: 'h5',
				avatarUrl: '',
				publicLoginType: app.globalData.publicLoginType //公众号登录方式(单选),1微信授权，2手机号登录
			}
		},
		computed: {
			...mapGetters(['isLogin', 'uid', 'userInfo'])
		},
		onLoad() {
			if (!this.isLogin) {
				toLogin();
			}
			this.avatarUrl = this.userInfo.avatar ? this.userInfo.avatar : '../static/images/f.png';
			// #ifdef H5
			let ua = navigator.userAgent.toLowerCase();
			if (ua.match(/MicroMessenger/i) == "micromessenger") {
				this.$set(this, 'wechat', false);
			} else {
				this.$set(this, 'wechat', true);
			}
			// #endif
		},
		methods: {
			/**
			 * 注销账号
			 *
			 */
			changeCancel() {
        goToAgreement('useraccountcancelnoticeinfo')
			},
			/**
			 * 编辑页面
			 *
			 */
			goEdit() {
				if (this.isLogin == false) {
					Cache.set(BACK_URL, '')
					toLogin();
				} else {
					uni.navigateTo({
						animationType: animationType.type,
						animationDuration: animationType.duration,
						url: '/pages/users/user_info/index'
					})
				}
			},
			/**
			 * APP检查更新
			 *
			 */
			appUpdate() {
				uni.navigateTo({
					url: '/pages/users/app_update/app_update',
					animationType: animationType.type,
					animationDuration: animationType.duration,
				})
			},
			/**
			 * 退出登录
			 *
			 */
			outLogin: function() {
				let that = this;
				if (that.loginType == 'h5') {
					uni.showModal({
						title: '提示',
						content: '确认退出登录?',
            cancelColor: '#f55850',
						success: function(res) {
							if (res.confirm) {
								that.$store.dispatch('GETLOGOUT');
							} else if (res.cancel) {
								console.log('用户点击取消');
							}
						}
					});
				}
			},
			/**
			 * 小程序设置
			 */
			Setting: function() {
				uni.openSetting({
					success: function(res) {
						console.log(res.authSetting)
					}
				});
			},
		}
	}
</script>

<style scoped lang="scss">
	.fontColor {
		color: #666666;
	}
  .list{
    padding: 0 30rpx;
  }
	.personal-data .list .item {
		border-bottom: 1rpx solid #f2f2f2;
    padding: 36rpx 0;
		font-size: 32rpx;
		color: #333333;
	}

	.personal-data .list .item .phone {
		width: 160rpx;
		height: 56rpx;
		font-size: 24rpx;
		color: #fff;
		line-height: 56rpx;
		border-radius: 32rpx
	}

	.personal-data .list .item .pictrue {
		width: 80rpx;
		height: 80rpx;
		position: relative;
	}

	.personal-data .list .item .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 50%;
	}

	.personal-data .list .item .pictrue .alter {
		width: 30rpx;
		height: 30rpx;
		border-radius: 50%;
		position: absolute;
		bottom: 0;
		right: 0;
	}

	.personal-data .list .item .input {
		text-align: right;
		color: #BBBBBB;
	}

	.personal-data .list .item .input .id {
		width: 365rpx;
	}

	.personal-data .list .item .input .iconfont {
		font-size: 32rpx;
		margin-left: 20rpx;
	}

	.personal-data .modifyBnt {
		font-size: 32rpx;
		color: #fff;
		width: 690rpx;
		height: 90rpx;
		border-radius: 50rpx;
		text-align: center;
		line-height: 90rpx;
		margin: 76rpx auto 0 auto;
	}

	.personal-data .logOut {
		font-size: 32rpx;
		text-align: center;
		width: 690rpx;
		height: 90rpx;
		border-radius: 45rpx;
		margin: 0 auto;
	}
</style>
