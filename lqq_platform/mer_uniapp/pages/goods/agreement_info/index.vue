<template>
	<view class="user_about" :data-theme="theme">
		<view>
			<view class="text cancelTxt" :class="{cancelTxt: type == 'useraccountcancelnoticeinfo'}">
				<mp-html :content="agreementData?agreementData.replace(/<br\/>/ig, ''):''" />
			</view>
			<view v-if="type === 'useraccountcancelnoticeinfo'" class="btn">
				<button @click="cancelBtn" class="sure-btn">申请注销</button>
			</view>
		</view>
		<view class="cancel" v-if="type == 'useraccountcancelnoticeinfo' && loaded">
			<view class="acea-row row-center">
				<view class="checkbox acea-row row-middle" @click="setCheck">
					<text v-if="!check" class="iconfont icon-ic_unselect"></text>
					<text v-else class="iconfont icon-a-ic_CompleteSelect font-color"></text>
				</view>
				<view class="ml10">已阅读并同意<text class="font font-color" @click="toCancel">《重要提醒》</text></view>
			</view>
		</view>
		<view class="outMoal" v-if="moal">
			<view class="box">
				<view class="title">该账号将永久注销</view>
				<view class="moalBtn">
					<view class="ok" @click="ok">确定</view>
					<view class="no" @click="cancelMoal">取消</view>
				</view>
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
	import {
		agreementInfo
	} from '@/api/api.js'
	import {
		userOut,
	} from '@/api/user.js'
	import {
		mapGetters
	} from "vuex";
	import parser from "../components/jyf-parser/jyf-parser";
  import mpHtml from "@/uni_modules/mp-html/components/mp-html/mp-html.vue";
	import {
		goToAgreement
	} from "@/libs/order";
	let app = getApp();
	export default {
		name: 'user_about',
		components: {
			"jyf-parser": parser,
      mpHtml
		},
		data() {
			return {
				theme: app.globalData.theme,
				type: '',
				agreementData: '',
				check: false,
				moal: false,
				loaded: false,
				tagStyle: {
					img: 'width:100%;display:block;'
				},
			}
		},
		onLoad: function(options) {
			this.type = options.from;
			this.setTitle(this.type);
			this.getCacheinfo();
		},
		methods: {
			navigateBack() {
				uni.navigateBack();
			},
			toCancel() {
				goToAgreement('useraccountcancelinfo')
			},
			setCheck() {
				this.check = !this.check
			},
			ok() {
				uni.showLoading({
					title: '注销中',
					mask: true
				});
				this.moal = false;
				userOut().then(res => {
          this.$store.dispatch('GETLOGOUT');
          this.$store.dispatch("GetTokenIsExist");
          this.$store.dispatch('GetGlobalConfig');
          this.$store.dispatch('GetLoginConfig');
          this.$store.dispatch('GetThemeConfig');
					uni.hideLoading();
					setTimeout(() => {
            this.$util.navigateTo('/pages/index/index');
					}, 500);
				}).then(err => {
					uni.hideLoading()
					this.$util.Tips({
						title: err
					});
				});
			},
			cancelBtn() {
				if (!this.check) {
					return uni.showToast({
						title: "请勾选已阅读",
						icon: 'none',
						duration: 2000,
					})
				} else {
					this.moal = true
				}
			},
			cancelMoal() {
				this.moal = false
				this.check = false
			},
			getCacheinfo() {
				this.loaded = false;
				agreementInfo(this.type).then(res => {
					this.agreementData = res.data ? JSON.parse(res.data).agreement : ''
					this.loaded = true;
				})
			},
			setTitle(e) {
				const titleMap = {
					'aboutusinfo': '关于我们协议',
					'intelligentinfo': '平台资质证明',
					'merincomminginfo': '商户入驻协议',
					'useraccountcancelinfo': '用户注销协议',
					'useraccountcancelnoticeinfo': '用户注销声明',
					'userinfo': '用户注册协议',
					'platfromruleinfo': '平台规则',
					'coupon/agreement/info': '优惠券协议',
					'paid/member/agreement/info': '会员服务协议',
					'userprivacyinfo': '用户隐私协议',
					'shop/member/card/registration/authorization/info': '会员授权协议',
          'consumer/rights/protection/info': '消费者权益保障说明'
				};
				uni.setNavigationBarTitle({
					title: titleMap[e] || '会员开卡协议'
				});
			}
		}
	}
</script>

<style lang="scss">
  page{
    background: #fff !important;
  }
	.user_about {
		.text {
			font-size: 28rpx;
			font-weight: 400;
			padding: 20rpx 24rpx;
			color: #282828;
		}

		.cancelTxt {
			overflow: hidden;
			overflow-y: auto;

			image {
				max-width: 100%;
			}
		}

		.cancel {
			position: fixed;
			bottom: 160rpx;
			left: 0;
			z-index: 1;
			width: 100%;
			bottom: 120rpx;
			padding: 10rpx 0;
			background: #fff;
			.checkbox {
				text-align: center;
				font-size: 24rpx;
				font-weight: 400;

				span {
					margin-left: 5rpx;
				}

				.font {
					@include main_color(theme);
					font-style: normal;
				}

				.iconfont {
					font-size: 36rpx;
				}
			}

			.btn {
				width: 690rpx;
				height: 90rpx;
				@include linear-gradient(theme);
				border-radius: 45rpx;
				margin: 0 auto;
				margin-top: 30rpx;
				text-align: center;
				line-height: 90rpx;
				font-size: 32rpx;
				font-weight: 400;
				color: #FFFFFF;
			}
		}
	}

	.outMoal {
		width: 100%;
		height: 100%;
		background: rgba(0, 0, 0, 0.5);
		position: fixed;
		top: 0;
		left: 0;
		z-index: 2;
		display: flex;
		align-items: center;
		justify-content: center;

		.box {
			position: fixed;
			width: 590rpx;
			height: 258rpx;
			background: #FFFFFF;
			opacity: 1;
			border-radius: 32rpx;
			text-align: center;
			padding: 50rpx;

			.title {
				font-size: 30rpx;
				font-weight: 600;
				color: #282828;
			}

			.moalBtn {
				margin-top: 43rpx;
				display: flex;
				justify-content: space-between;

				.ok {
					width: 234rpx;
					height: 66rpx;
					@include coupons_border_color(theme);
					border-radius: 33rpx;
					font-size: 26rpx;
					line-height: 66rpx;
					@include main_color(theme);
				}

				.no {
					width: 234rpx;
					height: 66rpx;
					@include linear-gradient(theme);
					border-radius: 33rpx;
					font-size: 26rpx;
					color: #FFFFFF;
					line-height: 66rpx;
				}
			}
		}
	}

	.btn {
		width: 100%;
		height: 120rpx;
		background-color: #FFFFFF;
		position: fixed;
		bottom: 0;
		line-height: 120rpx;
		padding: 17rpx 0;

		.sure-btn {
			width: 690rpx;
			height: 88rpx;
			margin: 0 auto;
			line-height: 88rpx;
			color: #FFFFFF;
			border-radius: 43rpx;
			@include main_bg_color(theme);
			font-size: 28rpx;
		}
	}
</style>