<template>
	<view :data-theme="theme">
		<form @submit="formSubmit" report-submit='true'>
			<view class='personal-data borderPad'>
				<view class='list borRadius14 bg--w111-fff mt20'>
					<view class="item acea-row row-between-wrapper">
						<view>头像</view>
						<!-- #ifndef MP -->
						<view class="pictrue" @click.stop='uploadpic'>
							<image :src='avatarUrl'></image>
							<image :src='editPng' class="alter"></image>
						</view>
						<!-- #endif -->
						<!-- #ifdef MP -->
						<view class="pictrue">
							<button class="avatar-wrapper" open-type="chooseAvatar" @chooseavatar="onChooseAvatar">
								<image class="avatar" :src="avatarUrl"></image>
							</button>
							<image :src='editPng' class="alter"></image>
						</view>
						<!-- #endif -->
					</view>
					<view class='item acea-row row-between-wrapper'>
						<view>昵称</view>
						<view class='input fontColor'>
							<!-- #ifndef MP -->
							<input type='text' name='nickname' :value='nickname' maxlength="20"></input>
							<!-- #endif -->
							<!-- #ifdef MP -->
							<input type="nickname" name='nickname' :value='nickname' class="weui-input"
								placeholder="请输入昵称" maxlength="20"/>
							<!-- #endif -->
						</view>
					</view>
					<view class='item acea-row row-between-wrapper'>
						<view>性别</view>
						<view class='input'>
							<picker @change="bindSexChange" :value="sexindex" :range="sexList" range-key="name">
								<view class="uni-input">{{sexList[sexindex].name}}</view>
							</picker>
						</view>
					</view>
					<view class='item acea-row row-between-wrapper'>
						<view>出生日期</view>
						<view class='input'>
							<picker mode="date" :value="date"
								@change="bindDateChange">
								<view class="uni-input">{{date}}</view>
							</picker>
						</view>
					</view>
					<view class='item acea-row row-between-wrapper'>
						<view>地区</view>
						<view class='input'>
							<picker mode="multiSelector" :value="cityIndex" :range="cityData" @change="bindCityChange"
								@columnchange="selMonitor">
								<view class="uni-input">{{addressNode.province&&addressNode.city ? addressNode.province + ' , ' + addressNode.city : '-'}}
								</view>
							</picker>
						</view>
					</view>
				</view>
				<button class='modifyBnt bg-color' formType="submit">保存修改</button>
			</view>
		</form>
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
		userEdit
	} from '@/api/user.js';
	import {
		getCity
	} from '@/api/api.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import {
		Debounce
	} from '@/utils/validate.js'
	import city from "../static/js/city.js";
	let app = getApp();
	const CACHE_ADDRESS = {};
	export default {
		data() {
			return {
				memberInfo: {
					avatar: '',
				},
				userIndex: 0,
				wechat: false,
				theme: app.globalData.theme,
				editPng: '../../../static/images/alert1.png',
				sexList: [{
						val: 0,
						name: '未知'
					},
					{
						val: 1,
						name: '男'
					},
					{
						val: 2,
						name: '女'
					}
				],
				sexindex: 0,
				date: '2000-01-01',
				sex: 0,
				cityData: city,
				cityIndex: 0,
				addressNode: {
					province: "请选择城市",
					city: ""
				},
				provinceList: [],
				cityAllList: [],
				avatarUrl: "",
				nickname: '',
				userInfo: null
			};
		},
		computed: {
			...mapGetters(['isLogin']),
			startDate() {
				return this.getDate('start');
			},
			endDate() {
				return this.getDate('end');
			}

		},
		onLoad() {
			//this.loadAddress(1, 1);
			if (!this.isLogin) {
				toLogin();
			} else {
				this.getUserInfo();
			}
			this.getAddressData();
			// #ifdef H5
			let ua = navigator.userAgent.toLowerCase();
			if (ua.match(/MicroMessenger/i) == "micromessenger") {
				this.$set(this, 'wechat', false);
			} else {
				this.$set(this, 'wechat', true);
			}
			// #endif
			switch (this.theme) {
				case 'theme2':
					this.editPng = '../static/images/alert2.png'
					break;
				case 'theme3':
					this.editPng = '../static/images/alert3.png'
					break;
				case 'theme4':
					this.editPng = '../static/images/alert4.png'
					break;
				case 'theme5':
					this.editPng = '../static/images/alert5.png'
					break;
				default:
					this.editPng = '../static/images/alert1.png'
					break;
			}
		},
		methods: {
			getUserInfo(data) {
				this.$store.dispatch('USERINFO').then(res => {
					this.userInfo = res;
					this.avatarUrl = this.userInfo.avatar ? this.userInfo.avatar : '../static/images/f.png';
					this.nickname = this.userInfo.nickname ? this.userInfo.nickname : '-';
					this.date = this.userInfo.birthday || '2000-01-01';
					this.sexindex = this.userInfo.sex;
					this.sex = this.sexList[this.sexindex].val;
					this.addressNode = {
						province: this.userInfo.province,
						city: this.userInfo.city
					}
				});
			},
			/**
			 * 小程序端上传头像
			 *
			 */
			onChooseAvatar(e) {
				const {
					avatarUrl
				} = e.detail
				uni.showLoading({
					title: '加载中...'
				});
				this.$util.uploadImgs( avatarUrl, {
					url: 'upload/image',
					name: 'multipart',
					model: "maintain",
					pid: 0
				},(res) => {
					this.avatarUrl = res.data.url;
					uni.hideLoading();
				}, (err) => {
					uni.hideLoading();
				})
			},
			/**
			 * 监听省市区滚动
			 *
			 */
			selMonitor(e) {
				const that = this
				let column = e.detail.column
				if (column == 0) {
					let index = e.detail.value
					let length = that.cityData[1].length
					// 改变市
					that.cityData[1].splice(0, length, ...that.cityAllList[index])
				}
			},
			/**
			 * 将省市区的数据转换为picker可用的多维数组
			 *
			 */
			getAddressData() {
				// 所有城市列表,二维数组
				let cityAllList = [];
				// 省列表
				let provinceList = [];
				// cityData为省市区的json数据
				for (let key in this.cityData) {
					let newDataList = [];
					if (this.cityData[key].child) {
						for (let key2 in this.cityData[key].child) {
							newDataList.push(this.cityData[key].child[key2].city);
						}
					}
					provinceList.push(this.cityData[key].province);
					cityAllList.push(newDataList);
				}
				this.provinceList = provinceList;
				this.cityAllList = cityAllList;
				this.cityData = [provinceList, cityAllList[0]];
			},
			bindCityColumnChange(e) {
				let column = e.detail.column
				if (column == 0) {
					let index = e.detail.value
					this.cityData[1] = this.cityAllList[index]
				}
			},
			bindCityChange(e) {
				let val = e.target.value
				this.addressNode = {
					province: this.cityData[0][val[0]],
					city: this.cityData[1][val[1]]
				}
			},
			bindSexChange(e) {
				this.sexindex = e.detail.value;
				this.sex = this.sexList[this.sexindex].val;
			},
			bindDateChange: function(e) {
				this.date = e.detail.value
			},
			getDate(type) {
				const date = new Date();
				let year = date.getFullYear();
				let month = date.getMonth() + 1;
				let day = date.getDate();

				if (type === 'start') {
					year = year - 60;
				} else if (type === 'end') {
					year = year + 2;
				}
				month = month > 9 ? month : '0' + month;
				day = day > 9 ? day : '0' + day;
				return `${year}-${month}-${day}`;
			},
			/**
			 * 上传文件
			 *
			 */
			uploadpic: function() {
				let that = this;
				that.$util.uploadImageOne({
					url: 'upload/image',
					name: 'multipart',
					model: "maintain",
					pid: 0,
					count: 1
				}, function(res) {
					that.avatarUrl = res;
				});
			},

			/**
			 * 提交修改
			 */
			formSubmit: Debounce(function(e) {
				let that = this,
					value = e.detail.value
				if (!value.nickname) return that.$util.Tips({
					title: '用户姓名不能为空'
				});
				value.avatar = that.avatarUrl ? that.avatarUrl : that.userInfo.avatar;
				uni.showLoading({
					title: '加载中...'
				});

				userEdit({
					avatar: value.avatar,
					birthday: that.date,
					city: this.addressNode.city,
					province: this.addressNode.province,
					nickname: value.nickname,
					sex: that.sex
				}).then(res => {
					uni.hideLoading();
					that.$util.Tips({
						title: '保存成功',
						icon: 'success'
					});
					setTimeout(function() {
						uni.reLaunch({
							url: '/pages/user/index',
						})
					}, 2000);

				}).catch(msg => {
					uni.hideLoading();
					return that.$util.Tips({
						title: msg || '保存失败，您并没有修改'
					});
				});
			})
		}
	}
</script>

<style scoped lang="scss">
	.fontColor {
		color: #868686;
	}

	.personal-data .wrapper {
		margin: 10rpx 0;
		background-color: #fff;
		padding: 36rpx 30rpx 13rpx 30rpx;
	}

	.personal-data .wrapper .title {
		margin-bottom: 30rpx;
		font-size: 32rpx;
		color: #282828;
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
    width: 80rpx;
    height: 80rpx;
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
		width: 415rpx;
		text-align: right;
		color: #666666;
	}

	.personal-data .list .item .input .id {
		width: 365rpx;
	}

	.personal-data .list .item .input .iconfont {
		font-size: 35rpx;
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
		margin: 30rpx auto 0 auto;
	}
</style>
