<template>
	<view :data-theme="theme">
		<view class="ChangePassword mt20 borderPad">
			<view class="list bg--w111-fff borRadius14 w-100-p111- borderPad">
				<block v-if="isNew">
					<view class="item">
						<input disabled='true' :value="userInfo.phone"></input>
					</view>
					<view class="item acea-row row-between-wrapper">
						<input type='number' placeholder='填写验证码' placeholder-class='placeholder' class="codeIput"
							v-model="captcha" maxlength="6"></input>
						<button class="code" :class="disabled === true ? 'on' : ''" :disabled='disabled' @click="code">
							{{ text }}
						</button>
					</view>
				</block>
				<block v-else>
					<view class="item">
						<input type='number' placeholder='新手机号' placeholder-class='placeholder' v-model="phone"
							maxlength="11"></input>
					</view>
					<view class="item acea-row row-between-wrapper">
						<input type='number' placeholder='填写验证码' placeholder-class='placeholder' class="codeIput"
							v-model="bindingCaptcha" maxlength="6"></input>
						<button class="code" :class="disabled === true ? 'on' : ''" :disabled='disabled' @click="code">
							{{ text }}
						</button>
					</view>
				</block>
			</view>
			<button form-type="submit" v-if="isNew" class="confirmBnt bg-color" @click="next">下一步</button>
			<button form-type="submit" v-if="!isNew" class="confirmBnt bg-color" @click="editPwd">保存</button>
		</view>
		<Verify @success="handlerOnVerSuccess" :captchaType="'clickWord'" :imgSize="{ width: '330px', height: '155px' }"
			ref="verify"></Verify>
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
	import sendVerifyCode from "@/mixins/SendVerifyCode";
	import {
		registerVerify,
		bindingPhone,
		verifyCode,
		bindingPhoneCode
	} from '@/api/api.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import {
		setThemeColor
	} from '@/utils/setTheme.js'
	import {
		Debounce
	} from '@/utils/validate.js'
	import Verify from '../components/verifition/verify.vue';

	const PHONE_REGEX = /^1(3|4|5|7|8|9|6)\d{9}$/i;
	const MESSAGES = {
		PHONE_REQUIRED: '请填写手机号码！',
		PHONE_INVALID: '请输入正确的手机号码！',
		CAPTCHA_REQUIRED: '请填写验证码',
		BINDING_CONFIRM: '是否更换绑定账号',
		BINDING_CONFIRM_H5: '是否更换绑定账号,更换之后需重新登录',
		BINDING_CANCEL: '您已取消更换绑定！'
	};

	const app = getApp();
	export default {
		components: {
			Verify
		},
		mixins: [sendVerifyCode],
		data() {
			return {
				phone: '',
				captcha: '',
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false, //是否隐藏授权
				key: '',
				isNew: true,
				timer: '',
				text: '获取验证码',
				nums: 60,
				theme: app.globalData.theme,
				bgColor: '',
				bindingCaptcha: '',
				isLoading: false
			};
		},
		mounted() {
			// this.timer = setInterval(this.getTimes, 1000);
		},
		computed: {
			...mapGetters(['isLogin', 'userInfo']),
			isPhoneValid() {
				return PHONE_REGEX.test(this.phone);
			},
			canSendCode() {
				return this.isNew || (this.phone && this.isPhoneValid);
			}
		},
		onLoad() {
			let that = this;
			that.bgColor = setThemeColor();
			uni.setNavigationBarColor({
				frontColor: '#ffffff',
				backgroundColor: that.bgColor,
			});
			if (this.isLogin) {
			} else {
				toLogin();
			}
		},
		methods: {
			getTimes() {
				this.nums = this.nums - 1;
				this.text = "剩余 " + this.nums + "s";
				if (this.nums < 0) {
					clearInterval(this.timer);
				}
				this.text = "剩余 " + this.nums + "s";
				if (this.text < "剩余 " + 0 + "s") {
					this.disabled = false;
					this.text = "重新获取";
				}
			},
			next() {
				if (!this.captcha) {
					return this.$util.Tips({
						title: MESSAGES.CAPTCHA_REQUIRED
					});
				}
				this.isNew = false;
				clearInterval(this.timer);
				this.disabled = false;
				this.text = "获取验证码";
				// 自动聚焦到新手机号输入框
				this.$nextTick(() => {
					const input = uni.createSelectorQuery().select('.item input');
					input.focus();
				});
			},
			editPwd: Debounce(function(e) {
				if (!this.phone) {
					return this.$util.Tips({
						title: MESSAGES.PHONE_REQUIRED
					});
				}
				if (!this.isPhoneValid) {
					return this.$util.Tips({
						title: MESSAGES.PHONE_INVALID
					});
				}
				if (!this.bindingCaptcha) {
					return this.$util.Tips({
						title: MESSAGES.CAPTCHA_REQUIRED
					});
				}

				this.isLoading = true;
				uni.showModal({
					title: uni.getSystemInfoSync().platform === 'h5' ? MESSAGES.BINDING_CONFIRM_H5 : MESSAGES.BINDING_CONFIRM,
					confirmText: '绑定',
          confirmColor: '#f55850',
					success: async (res) => {
						if (res.confirm) {
							try {
								uni.showLoading({
									title: '加载中',
									mask: true
								});
								await bindingPhone({
									phone: this.phone,
									captcha: this.bindingCaptcha
								});
								this.$store.dispatch('GETLOGOUT');
							} catch (err) {
								this.$util.Tips({
									title: err.toString()
								});
							} finally {
								uni.hideLoading();
								this.isLoading = false;
							}
						} else {
							this.$util.Tips({
								title: MESSAGES.BINDING_CANCEL
							}, {
								tab: 5,
								url: '/pages/users/user_setting/index'
							});
							this.isLoading = false;
						}
					}
				});
			}),
			//滑块验证成功
			handlerOnVerSuccess(data) {
				this.$refs.verify.hide();
				this.codeSend();
			},
			codeSend() {
				let that = this;
				bindingPhoneCode({
					phone: that.phone,
					captcha: that.captcha
				}).then(res => {
					that.$util.Tips({
						title: res.message
					});

					that.timer = setInterval(that.getTimes, 1000);
					that.disabled = true;
					uni.hideLoading();
				}).catch(err => {
					return that.$util.Tips({
						title: err.toString()
					}, {
						tab: 3,
						url: 1
					});
					uni.hideLoading();
				});
			},
			/**
			 * 发送验证码
			 *
			 */
			code: Debounce(function() {
				if (this.isLoading) return;
				
				this.nums = 60;
				this.isLoading = true;

				if (this.isNew) {
					this.sendOldPhoneCode();
				} else {
					if (!this.phone) {
						return this.$util.Tips({
							title: MESSAGES.PHONE_REQUIRED
						});
					}
					if (!this.isPhoneValid) {
						return this.$util.Tips({
							title: MESSAGES.PHONE_INVALID
						});
					}
					this.$refs.verify.show();
				}
			}),
			async sendOldPhoneCode() {
				try {
					const res = await registerVerify();
					this.$util.Tips({
						title: res.message
					});
					this.timer = setInterval(this.getTimes, 1000);
					this.disabled = true;
				} catch (err) {
					this.$util.Tips({
						title: err
					});
				} finally {
					uni.hideLoading();
					this.isLoading = false;
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
.ChangePassword {
	.mt20 {
		margin-top: 20rpx;
	}

	.borderPad {
		padding: 0 30rpx;
	}

	.list {
		background-color: #fff;
		border-radius: 14rpx;
		width: 100%;

		.item {
			width: 100%;
			height: 110rpx;
			border-bottom: 2rpx solid #f0f0f0;

			input {
				width: 100%;
				height: 100%;
				font-size: 32rpx;

				&.placeholder {
					color: #b9b9bc;
				}

				&.codeIput {
					width: 340rpx;
				}
			}

			.code {
				font-size: 32rpx;
				@include main_color(theme);
				transition: all 0.3s;

				&:disabled {
					opacity: 0.6;
					cursor: not-allowed;
				}

				&.on {
					color: #b9b9bc !important;
					opacity: 0.6;
					cursor: not-allowed;
				}
			}
		}
	}

	.confirmBnt {
		font-size: 32rpx;
		height: 90rpx;
		border-radius: 45rpx;
		color: #fff;
		margin: 70rpx auto 0;
		text-align: center;
		line-height: 90rpx;
		transition: all 0.3s;

		&:disabled {
			opacity: 0.6;
			cursor: not-allowed;
		}
	}
}

</style>
