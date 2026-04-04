<template>
	<view :data-theme="theme">
    <view class="PromoterRank">
      <!-- #ifdef MP || APP -->
      <view
          class="accountTitle fixed"
          :style="{ 'background-image': 'url(' + urlDomain + 'crmebimage/presets/brokerage_rank_bg1.png)', height: getHeight.barTop + getHeight.barHeight + 'px' }"
      >
        <nav-bar :isBackgroundColor="false" navTitle='佣金排行' ref="navBarRef" iconColor='#fff' :isShowMenu="false">
        </nav-bar>
      </view>
      <view :style="{ height: getHeight.barTop + getHeight.barHeight + 'px' }"></view>
      <!-- #endif -->
      <view class="header-commission" :style="'background-image: url(' + urlDomain + 'crmebimage/presets/brokerage_rank_bg2.png);'">
      </view>
      <ranking-list :rankList="rankList" @getChangeTab="getChangeTab" :loading="loading"></ranking-list>
      <view style="height: 130rpx;" v-if="ranking>0"></view>
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
		getBrokerageRank
	} from '@/api/user.js';
	import {toLogin} from '@/libs/login.js';
	import {mapGetters} from "vuex";
	import {setThemeColor} from '@/utils/setTheme.js'
  import rankingList from '../components/rankingList';
  import navBar from '@/components/navBar';
	const app = getApp();
	export default {
		components: {
      rankingList,
      navBar
		},
		data() {
			return {
        getHeight: this.$util.getWXStatusHeight(),
				urlDomain: this.$Cache.get("imgHost"),
				navList: ["周排行", "月排行"],
				active: 0,
				rankList: [],
				loadend: false,
				loading: false,
				loadTitle: '加载更多',
				type: 'week',
				ranking: 0,
				isShow: false,
				theme:app.globalData.theme,
				bgColor:'#e93323'
			};
		},
		computed: mapGetters(['isLogin', 'uid']),
		watch:{
			isLogin:{
				handler:function(newV,oldV){
					if(newV){
						this.getBrokerageRankList();
					}
				},
				deep:true
			}
		},
		onLoad() {
			if (this.isLogin) {
				this.getBrokerageRankList();
			} else {
				toLogin();
			}
			let that = this;
			that.bgColor = setThemeColor();
			uni.setNavigationBarColor({
				frontColor: '#ffffff',
				backgroundColor:that.bgColor,
			});
		},
		methods: {
      // tab切换回调
      getChangeTab: function(index) {
        this.type = index ? 'month' : 'week';
        this.loadend = false;
        this.$set(this, 'rankList', []);
        this.getBrokerageRankList();
      },
			getBrokerageRankList: function() {
				if (this.loadend) return;
				if (this.loading) return;
				this.loading = true;
				this.loadTitle = '';
				this.loading = true;
				this.isShow = false;
				getBrokerageRank({
					type: this.type
				}).then(res => {
					let list = res.data || [];
					this.$set(this, 'rankList', list);
					if(list.length>0) this.ranking = list.findIndex((itm) => { return itm.id=== this.uid }) + 1;
					this.loading = false;
					this.isShow = true;
				}).catch(err => {
					this.loading = false;
					this.loadTitle = '加载更多';
				})
			}
		},
		onReachBottom: function() {
			this.getBrokerageRankList();
		}
	}
</script>

<style scoped lang="scss">
.accountTitle {
  background-repeat: no-repeat;
  background-size: 100% 100%;
  left: 0;
  top: 0;
  width: 100%;
  z-index: 99;
  .sysTitle {
    width: 100%;
    position: relative;
    font-weight: 500;
    color: #fff;
    font-size: 30rpx;
    .iconfont {
      position: absolute;
      font-size: 36rpx;
      left: 11rpx;
      width: 60rpx;
    }
  }
}
.header-commission {
  height: 434rpx;
  padding-top: 26rpx;
  background-repeat: no-repeat;
  background-size: 100% 100%;
  width: 100%;
  .left {
    .tips {
      font-size: 28rpx;
      font-weight: 400;
      color: #ffcb99;
      margin: 164rpx 0 0 46rpx;

      .num {
        font-family: 'SemiBold';
        font-size: 40rpx;
        margin: 0 8rpx;
      }
    }
  }
}
 /deep/.wrapper {
  margin: -154rpx 24rpx 0 24rpx !important;
}
@import '../static/css/ranking.scss';
</style>
