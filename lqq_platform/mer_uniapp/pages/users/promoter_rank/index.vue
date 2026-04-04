<template>
	<view :data-theme="theme">
		<view class="PromoterRank">
      <!-- #ifdef MP || APP -->
      <view class="relative">
        <nav-bar :isBackgroundColor="false" ref="navBarRef" navTitle='推广人排行'  backgroundColor="linear-gradient(180deg, #FFC48D, #FCCEA4)" :isShowMenu="false">
        </nav-bar>
      </view>
      <!-- #endif -->
      <view class="header">
        <view class="acea-row row-middle row-right">
          <view class="left">
            <view class="pictrue">
              <image src="../static/images/ranking.png"></image>
            </view>
          </view>
          <view class="trophy">
            <image src="../static/images/trophy.png"></image>
          </view>
        </view>
      </view>
      <ranking-list :rankList="rankList" @getChangeTab="getChangeTab" :loading="loading" fromType="promoter"></ranking-list>
      <view style="height: 130rpx;" v-if="ranking>0"></view>
      <view class="footer acea-row row-between-wrapper" v-if="ranking>0">
        <view class="acea-row row-middle">
          <view class="me">我</view>
          <view class="pictrue">
            <image :src="rankInfo.avatar"></image>
          </view>
          <view class="name line1">{{rankInfo.nickname}}</view>
          <view class="ranking">第{{rankInfo.position}}名</view>
        </view>
<!--        <view class="num">￥{{rankInfo.brokerage_price}}</view>-->
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
		getRankList
	} from '@/api/user.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import {
		setThemeColor
	} from '@/utils/setTheme.js'
  import navBar from '@/components/navBar';

  import rankingList from '../components/rankingList';
	const app = getApp();
	export default {
    components: {
      navBar,
      rankingList
    },
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),

				type: 'week',
				loading: false,
				loadend: false,
				rankList: [],
				Two: {},
				One: {},
				Three: {},
				theme: app.globalData.theme,
				bgColor: '#e93323',
				isShow: false,
				list: [],
        ranking: 0
			};
		},
		computed: mapGetters(['isLogin']),
		watch: {
			isLogin: {
				handler: function(newV, oldV) {
					if (newV) {
						this.getRanklist();
					}
				},
				deep: true
			}
		},
		onLoad() {
			if (this.isLogin) {
				this.getRanklist();
			} else {
				toLogin();
			}
			let that = this;
			that.bgColor = setThemeColor();
			uni.setNavigationBarColor({
				frontColor: '#ffffff',
				backgroundColor: that.bgColor,
			});
		},

		methods: {
			getRanklist: function() {
				if (this.loadend) return;
				if (this.loading) return;
        this.loading = true;
				getRankList({
					type: this.type
				}).then(res => {
					let data = res.data;
          this.$set(this, 'rankList', data);
          this.loading = false;
          this.isShow = true;
				}).catch(err => {
          this.loading = false;
				})
			},
      // tab切换回调
      getChangeTab: function(index) {
				this.type = index ? 'month' : 'week';
				this.$set(this, 'rankList', []);
				this.getRanklist();
			},
		},
		onReachBottom: function() {
			this.getRanklist();
		}
	}
</script>

<style scoped lang="scss">
@import '../static/css/ranking.scss';
</style>