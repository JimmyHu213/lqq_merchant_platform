<template>
	<view class="member-rules" :data-theme="theme">
		<scroll-view scroll-y="true" class="rules-container" :scroll-into-view="scrollIntoView" scroll-with-animation>
			<view class="rule-item" v-for="(item, index) in rulesList" :key="index" :id="'level-'+item.level">
				<!-- Timeline -->
				<view class="timeline">
					<view class="flex-center" :class="{ 'dot-box': item.level === level }"><view class="dot" :class="{ active: item.level === level }"></view></view>
					<view class="line" v-if="index !== rulesList.length - 1"></view>
				</view>

				<!-- Content -->
				<view class="content">
					<view class="level-badge px-32 text-999" :class="{ active: item.level === level }">
						{{ item.name }}
					</view>

					<view class="condition text-333 f-s-26">
						条件：{{ item.level === 1 ? '完成开卡即可入会' : `在本店消费金额累计达到${item.thresholdAmount}元` }}
					</view>

					<view class="text-333 f-s-26 mb20">特权：</view>

					<view class="privileges-grid">
						<view class="privilege-item" v-for="(priv, pIndex) in item.benefitsList" :key="pIndex">
							<view class="icon-box">
								<image v-show="priv.selectedIcon" :src="item.level < level || item.level === level ? priv.selectedIcon : priv.unselectedIcon"
									class="w-100-p111- h-100-p111-" />
              </view>
							<text class="priv-text text-666">{{ priv.name }}</text>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>

		<view class="footer-spacer"></view>
		<view class="footer">
			<button class="cancel-btn" @click="handleCancel">注销会员</button>
		</view>
	</view>
</template>

<script>
  import {memberLogoffApi} from "../memberApi";

  let app = getApp();
	export default {
		name: "index",
		components: {
			//uniIcons
		},
		data() {
			return {
        theme: app.globalData.theme,
				rulesList: [], //规则数组
				merId: 0,
        level: 1, // 会员等级
        scrollIntoView: ''
			}
		},
		onLoad(options) {
			this.merId = Number(options.merId)
      this.level = Number(options.level)
			this.getLevelData();
		},
		methods: {
			async getLevelData() {
				let data = await this.$store.dispatch("getMemberLevelList", this.merId);
				this.rulesList = data
        this.$nextTick(() => {
          this.scrollIntoView = 'level-' + this.level
        })
			},
			// 返回上一页
			goBack() {
				uni.navigateBack({
					delta: 1
				});
			},
      async handleCancel() {
        const result = await uni.showModal({
          content: "确定要注销会员吗？",
        });
        if (result[0] || result[1].cancel) return false;
        try {
         await memberLogoffApi(this.merId)
          uni.showToast({
            title: '注销成功',
            icon: "none"
          });
          setTimeout(()=>{
            this.$util.goToMerHome(this.merId)
          },100)
        }catch (e) {
          uni.showToast({
            title: e,
            icon: 'none'
          })
          uni.hideLoading();
        }
      }
		}
	}
</script>

<style lang="scss" scoped>
	.member-rules {
		padding: 30rpx;
		background-color: #fff;
		min-height: 100vh;
		box-sizing: border-box;
	}

	.rules-container {
		padding-left: 10rpx;
    height: calc(100vh - 150rpx); // 给 scroll-view 一个固定的高度，减去 footer 的高度
	}

	.rule-item {
		position: relative;
		padding-left: 50rpx;
		padding-bottom: 20rpx;

		&:last-child {
			padding-bottom: 0;

			.timeline .line {
				display: none;
			}
		}
	}

	.timeline {
		position: absolute;
		left: 0;
		top: 18rpx;
		bottom: 0;
		display: flex;
		flex-direction: column;
		align-items: center;
		width: 30rpx;
     .dot-box{
       width: 32rpx;
       height: 32rpx;
       border-radius: 50%;
       @include coupons_light_color(theme);
     }
		.dot {
			width: 16rpx;
			height: 16rpx;
			border-radius: 50%;
			background-color: #e5e5e5;
			z-index: 1;
			flex-shrink: 0;

			&.active {
        @include main_bg_color(theme);
			//	box-shadow: 0 0 0 6rpx rgba(255, 59, 48, 0.2);
			}
		}

		.line {
			flex: 1;
			width: 0;
			border-left: 2rpx dashed #e5e5e5;
			margin-top: 10rpx;
		}
	}

	.content {
		.level-badge {
			display: inline-block;
			line-height: 56rpx;
			height: 56rpx;
			background-color: #f5f5f5;
			border-radius: 30rpx;
			font-size: 24rpx;
			margin-bottom: 20rpx;

			&.active {
        @include main_bg_color(theme);
				color: #fff;
			}
		}

		.privileges-grid {
			display: flex;
			flex-wrap: wrap;
		}

		.privilege-item {
			display: flex;
			flex-direction: column;
			align-items: center;
			width: 25%;
			margin-bottom: 20rpx;
			padding-right: 10rpx;
			box-sizing: border-box;

			.icon-box {
				width: 80rpx;
				height: 80rpx;
				background-color: #fff5f0;
				border-radius: 50%;
				display: flex;
				align-items: center;
				justify-content: center;
				margin-bottom: 12rpx;

				.iconfont {
					color: #dcb689;
					font-size: 40rpx;
				}
			}

			.priv-text {
				font-size: 22rpx;
				text-align: center;
				white-space: nowrap;
				overflow: hidden;
				text-overflow: ellipsis;
				width: 100%;
			}
		}
	}

	.footer-spacer {
		height: 120rpx;
	}

	.footer {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 20rpx 30rpx;
		padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
		z-index: 100;

		.cancel-btn {
			background-color: #fff;
      @include main_color(theme);
      @include border_color(theme);
			border-radius: 44rpx;
			font-size: 32rpx;
			height: 80rpx;
			line-height: 76rpx;

			&::after {
				border: none;
			}
		}
	}
</style>