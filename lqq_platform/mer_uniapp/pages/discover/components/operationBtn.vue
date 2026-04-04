<template>
	<view>
		<view class="userInfo">
			<!-- 1.头像 -->
			<navigator v-show="isShowCent" hover-class="none"
				:url="'/pages/discover/discover_user/index?id='+list.authorId" class="pictrue">
				<image class="userAvatar" :src="list.authorAvatar || urlDomain+'crmebimage/presets/morenT.png'"
					mode="aspectFill">
				</image>
				<view v-if="!list.isConcerned && list.authorId!==uid" class="guanzhu" @click.stop="followAuthor(list)">
					<text class="iconfont icon-ic_increase"></text></view>
				<view v-else class="yiguanzhu"><text class="iconfont"></text></view>
			</navigator>
			<!-- 其他按钮 -->
			<view v-for="(item, index) in operationButton(list, isShowCent)" :key="index"
				@click.stop="handleButton(item);" class="flex-column" :style="{ marginTop : item.top}">
        <!-- 提到的商品 -->
        <view v-if="item.type==='collection'" @click="openMore(list)" class="mention"
              style="opacity: 0.9; margin-top: 18px;">
          <image src="../static/images/collection.png"></image>
          <text class="count">{{list.productList.length}}</text>
        </view>
				<view :class="item.style">
					<text class="iconfont" :class="( item.type === 'like' && list.userIsLike ? 'likeNumActive' : '') + ' ' + item.icon"></text>
				</view>
				<text class="info-text">{{item.num && item.num > 0 ? item.num : item.text}}</text>
			</view>
      <view class="manage" v-show="showManage">
        <view v-for="(item, index) in myOperationButton(list)" :key="index" class="items" @click.stop="handleMyOperationButton(item)">
          <text class="iconfont" :class="item.icon"></text>
          <text>{{item.text}}</text>
        </view>
      </view>
		</view>

		<!-- 评论 -->
		<tui-bottom-popup :zIndex="98" :maskZIndex="97" :show="showComment" @close="closeComment">
			<view
				:style="'width: 100%; background-color: #fff; border-top-left-radius: 40rpx; border-top-right-radius: 40rpx;'">
				<discoverComment v-if="showComment" fromTo="popupView" :noteId="noteId" :noteDetails="noteDetail"
					@close="closeComment">
				</discoverComment>
			</view>
		</tui-bottom-popup>
		<!-- 他提到的宝贝弹窗 -->
		<uni-popup type="bottom" ref="pinglunMentioned">
			<view
				:style="'width: '+ windowWidth +'px;background-color: #F5F5F5;max-height:1200rpx; border-top-left-radius: 40rpx; border-top-right-radius: 40rpx;overflow-y: scroll;'">
				<mentioned ref="mentioned" :list="proList" @close="popup" fromType="video" :goodsStyle="goodsStyle">
				</mentioned>
			</view>
		</uni-popup>

		<!-- 分享按钮 -->
		<image v-if="H5ShareBox" class="shareImg" :src="urlDomain+'crmebimage/presets/share-info.png'"
			@click="H5ShareBox = false"></image>
		<!-- 分享按钮 -->
		<view class="generate-posters acea-row row-middle" :class="posters ? 'on' : ''">
			<!-- #ifndef MP -->
			<button class="item" :class="weixinStatus ? 'item3' : ''" hover-class='none' v-if="weixinStatus === true"
				@click="H5ShareBox = true">
				<view class="iconfont icon-icon_WeChat_1"></view>
				<view class="">发送给朋友</view>
			</button>
			<!-- #endif -->
			<!-- #ifdef MP -->
			<button class="item" :class="weixinStatus ? 'item3' : ''" open-type="share" hover-class='none'
				@click="goFriend">
				<view class="iconfont icon-icon_WeChat_1"></view>
				<view class="">发送给朋友</view>
			</button>
			<!-- #endif -->
			<button class="item" :class="weixinStatus ? 'item3' : ''" hover-class='none' @click="goPoster">
				<view class="iconfont icon-a-ic_picture1"></view>
				<view class="">生成海报</view>
			</button>
		</view>
		<view class="mask" v-if="posters" @click="listenerActionClose"></view>
		<!-- 海报展示 -->
		<view class='mask' v-if="posterImageStatus"></view>
		<view class='poster-pop' v-if="posterImageStatus">
			<image src='../static/images/poster-close.png' class='close' @click="posterImageClose"></image>
			<image :src='posterImage' class="image"></image>
			<!-- #ifndef H5 -->
			<view class="keep" @click="handleSavePosterPath">保存到相册</view>
			<!-- #endif -->
		</view>
		<view class="canvas" v-if="canvasStatus">
			<canvas style="width:750px;height:1190px;" canvas-id="myCanvas"></canvas>

		</view>

		<canvas v-if="qrcodecanvasStatus" class="qrcodecanvasStatus" canvas-id="qrcode"
			:style="{width: `${qrcodeSize}px`, height: `${qrcodeSize}px`}" />
	</view>
</template>

<script>
	import {
		mapGetters
	} from "vuex";
	import {
		discoverFollowAuthor,
		discoverNoteLike
	} from "../../../libs/follow";
	import {
		Debounce
	} from "../../../utils/validate";
	import {
		toLogin
	} from "../../../libs/login";
  import {
    myOperationButton,
    operationButton
  } from "./config";
  import tuiBottomPopup from "./tui-bottom-popup";
  import mentioned from "./mentioned";
  import discoverComment from "./discoverComment";
  import BaseTextMore from "./BaseTextMore";
  import {goodsStyle} from "../discover_video/default";
  import {imageBase64} from "../../../api/public";
  import uQRCode from "../../../js_sdk/Sansnn-uQRCode/uqrcode";
  import {noteDelApi, noteReplySwitchApi} from "../../../api/discover";
  import {workOrderServiceEndApi} from "../../admin/workOrder_manage/workOrder";
  import util from "../../../utils/util";
  import {mpQrcode} from "../../../api/api";
  import {base64src} from "../../../utils/base64src";
	export default {
		name: "operationBtn",
    components: {
      tuiBottomPopup,
      mentioned,
      discoverComment,
      BaseTextMore
    },
		computed: {
      ...mapGetters(['userInfo', 'isLogin', 'uid', 'globalData']),
      windowWidth() {
        const vars = this.$getCssVarStyle();
        return vars['--screen-width'] || '100%';
      },
    },
		props: {
			list: {
				type: Object,
				default: function() {
					return {};
				}
			},
      noteId: {
        type: [String, Number],
        default: ''
      }
		},
		data() {
			return {
        goodsStyle, //商品样式
				isShowCent: true, //是否展示内容
				urlDomain: this.$Cache.get("imgHost"),
				H5ShareBox: false,
				statusTop: 0, //审核状态样式
				showManage: false, //是否显示操作
				posters: false,
				weixinStatus: false, //是否是微信
				posterImageStatus: false,
				storeImage: '', //海报产品图
				PromotionCode: '', //二维码图片
				canvasStatus: false, //海报绘图标签
				posterImage: '', //海报路径
				posterbackgd: '../static/images/posterbackgd.png',
				codeImg: "",
				qrcodeSize: 600,
        proList: [], //商品列表
        popupShow: false, //商品弹窗
        showComment: false,
        noteDetail: {},
        qrcodecanvasStatus: true, //h5分享二维码
			}
		},
    mounted() {
      //#ifdef H5
      this.make(this.uid,  this.noteId);
      this.ShareInfo(this.list);
      // #endif
      // #ifdef MP
      this.imgTop = this.list.cover;
      this.getQrcode(this.noteId);
      // #endif
    },
		methods: {
      // 视频编辑
      myOperationButton,
			// 视频操作按钮
			operationButton,
      // 操作自己视频
      handleMyOperationButton: Debounce(function(item) {
        switch (item.type) {
          case 'edit':
            this.handleEdit(this.list)
            break;
          case 'delete':
            this.deleteTopic(this.list)
            break;
          case 'message':
            this.replySwitch(this.list)
            break;
        }
      }),
      // 编辑
      handleEdit(){
        this.$util.navigateTo(`/pages/discover/discover_release/index?noteId=${this.noteId}`);
      },
      //禁止评论
      replySwitch(item) {
        if (!item.platReplySwitch && item.replyStatus === 3) {
          this.$util.Tips({
            title: '平台设置不可评论'
          });
        } else {
          noteReplySwitchApi(item.id).then(res => {
            if (item.replyStatus === 1) {
              this.$set(item, 'replyStatus', 2)
              this.$util.Tips({
                title: '禁止成功'
              });
            } else {
              this.$set(item, 'replyStatus', 1)
              this.$util.Tips({
                title: '开启成功'
              });
            }
          }).catch(err => {
            uni.showToast({
              title: err,
              icon: 'none'
            })
          });
        }
      },
      // 删除内容
      async deleteTopic(item) {
        const result = await uni.showModal({
          content: "确定要删除该内容么?",
        });
        if (result[0] || result[1].cancel) return;
        try {
           await noteDelApi(item.id)
          util.Tips({
            title: '删除成功'
          });
          setTimeout(function() {
            uni.redirectTo({
              url: '/pages/discover/discover_user/index'
            })
          }, 1000);
        }catch (err) {
          uni.showToast({
            title: err,
            icon: 'none'
          })
        }
      },
      // 小程序二维码
      getQrcode(id) {
        let data = {
          scene: 'id=' + id + '&sd=' + this.uid,
          page: 'pages/discover/discover_video/routineVideo/index'
        }
        mpQrcode(data).then(res => {
          base64src(res.data.code, Date.now(), res => {
            this.PromotionCode = res;
          });

        }).catch(err => {
          this.errT = err;
        });
      },
      //微信分享
      ShareInfo(noteDetail) {
        let data = noteDetail;
        let href = location.href;
        if (this.$wechat.isWeixin()) {
          href = href.indexOf("?") === -1 ? href + "?sd=" + this.uid + '&id=' + noteDetail.id : href +
              "&sd=" + this.uid + '&id=' + this.noteId;
          let configAppMessage = {
            desc: data.content,
            title: data.title,
            link: href,
            imgUrl: data.cover
          };
          this.$wechat.wechatEvevt([
            "updateAppMessageShareData",
            "updateTimelineShareData",
            "onMenuShareAppMessage",
            "onMenuShareTimeline"
          ], configAppMessage).then(res => {}).catch(err => {
            console.log(err);
          })
        }
      },
			// 关注作者
			followAuthor: Debounce(function(item) {
				if (this.isLogin) {
					discoverFollowAuthor(item.authorId).then(() => {
						this.$set(item, 'isConcerned', !item.isConcerned);
					});
				} else {
					toLogin();
				}
			}),
			handleButton: Debounce(function(item) {
				switch (item.type) {
					case 'like':
						this.likeToggle(this.list)
						break;
					case 'comment':
						this.toComment(this.list)
						break;
					case 'share':
						this.listenerActionSheet()
						break;
					case 'more':
						this.showManage = !this.showManage
						break;
					case 'collection':
						this.openMore(this.list);
						break;
					case 'enlarge':
						this.onHideCent();
						break;
				}
			}),
			//收起内容
			onHideCent() {
				this.isShowCent = !this.isShowCent;
        this.showManage = false
			},
			// 查看提到的宝贝
			openMore(item) {
				this.proList = item.productList;
				this.$refs.pinglunMentioned.open('bottom');
			},
      //关闭提到的宝贝
      popup() {
        this.$refs.pinglunMentioned.close();
      },
			// 分享
			listenerActionSheet() {
				// #ifdef H5
				if (this.$wechat.isWeixin() === true) {
					this.weixinStatus = true;
				}
				// #endif
				this.posters = true;
			},
			//点赞
			likeToggle(item) {
				if (this.isLogin) {
					discoverNoteLike(item.id).then(() => {
						this.$set(item, 'userIsLike', !item.userIsLike);
						if (!item.userIsLike) {
							item.likeNum--;
							item.likeNum = item.likeNum == 0 ? 0 : item.likeNum
						} else {
							item.likeNum++;
						}
					});
				} else {
					toLogin();
				}
			},
			//打开评论
			toComment(item) {
				this.noteDetail = item;
				uni.showToast({
					title: '加载中...',
					icon: 'none',
					position: 'bottom',
					duration: 300
				})
				this.showComment = true
			},
      //关闭评论
      closeComment(){
        this.showComment = false
      },
			// 生成二维码；
			make(uid, id) {
				let href = location.href.split('?')[0] + '?noteId=' + id + "&sd=" + uid;
				uQRCode.make({
					canvasId: 'qrcode',
					text: href,
					size: this.qrcodeSize,
					margin: 10,
					success: res => {
						this.PromotionCode = res;
					},
					complete: () => {},
					fail: res => {
						this.$util.Tips({
							title: '海报二维码生成失败！'
						});
					}
				})
			},
			//h5 转base64
			async getImageBase64(images) {
				let res = await imageBase64({
					url: images
				})
				return res.data.code
			},
			/**
			 * 生成海报
			 */
			async goPoster() {
				let that = this;
				if (that.posterImage) {
					that.posterImageStatus = true
					that.posters = false
					return
				}
				let arr2
				uni.showLoading({
					title: '海报生成中',
					mask: true
				});
				that.posters = false;
				that.qrcodecanvasStatus = true;
				that.$set(that, 'canvasStatus', true);
				// #ifdef MP
				arr2 = [that.posterbackgd, await this.$util.downloadFilestoreImage(that.list.cover), that
					.PromotionCode, await this.$util.downloadFilestoreImage(that.list.authorAvatar)
				];
				// #endif
				// #ifdef H5
				arr2 = [that.posterbackgd, await that.getImageBase64(that.list.cover), that.PromotionCode,
					await that.getImageBase64(that.list.authorAvatar)
				];
				// #endif
				//生成推广海报
				that.$util.videoPosterCanvas(arr2, that.list.content, that.list.authorName,
					function(tempFilePath) {
						that.qrcodecanvasStatus = false
						that.$set(that, 'posterImage', tempFilePath);
						that.$set(that, 'posterImageStatus', true);
						that.$set(that, 'canvasStatus', false);
						uni.hideLoading();
					}, (err) => {
						that.qrcodecanvasStatus = false
						that.$set(that, 'canvasStatus', false);
						uni.hideLoading();
					});
			},
      //保存海报
      handleSavePosterPath(){
        this.$util.savePosterPath(this.posterImage).then(userInfo => {
          this.posterImageClose();
        });
      },
			share() {
				this.H5ShareBox = true;
			},
			// 分享关闭
			listenerActionClose: function() {
				this.posters = false;
			},
			//隐藏海报
			posterImageClose: function() {
				this.posterImageStatus = false
			},
			// 小程序关闭分享弹窗；
			goFriend: function() {
				this.posters = false;
			},
		}
	}
</script>

<style lang="scss" scoped>
.onHide {
  width: 66rpx;
  height: 66rpx;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, .4);
  color: #fff;
  text-align: center;
  line-height: 66rpx;
  margin: auto;
}
.icon-ic_enlarge2, .icon-ic_reduce{
  font-size: 40rpx;
}
.icon-ic_love_2, .icon-ic_love_2{
  font-size: 68rpx;
}
.icon-icon_comment{
  font-size: 60rpx;
}
.icon-icon_transmit{
  font-size: 64rpx;
}
.generate-posters {
  width: 100%;
  height: 170rpx;
  background-color: #fff;
  position: fixed;
  left: 0;
  bottom: 0;
  z-index: 388;
  transform: translate3d(0, 100%, 0);
  transition: all 0.3s cubic-bezier(0.25, 0.5, 0.5, 0.9);
  border-top: 1px solid #eee;
  align-content: center;
  align-items: center;
  flex-direction: row;
  .item {
    /* #ifdef H5 */
    flex: 100%;
    /* #endif */
    /* #ifndef H5 */
    flex: 33.33%;
    /* #endif */
    text-align: center;
    font-size: 30rpx;

    &.item3 {
      flex: 33.33%;
    }
    .iconfont {
      font-size: 80rpx;
      color: #5eae72;
      .icon-a-ic_picture1 {
        color: #5391f1;
      }
    }
  }
}
.mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  z-index: 9;
}
.generate-posters.on {
  transform: translate3d(0, 0, 0);
}
	.flex-column {
		display: flex;
		flex-direction: column;
	}

	.userInfo {
		position: absolute;
		bottom: 100rpx;
		right: 24rpx;
		flex-direction: column;
		text-align: center;
		display: flex;
    width: 92rpx;

		.pictrue {
			flex-direction: column;
			justify-content: center;
			align-items: center;
			position: relative;

			// margin-bottom: 18rpx;
			image {
				width: 92rpx;
				height: 92rpx;
			}

			.guanzhu {
				width: 42rpx;
				height: 42rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				color: #fff;
				background: #E93323;
				border-radius: 100%;
				position: relative;
				top: -20rpx;
				left: 29rpx;

				.iconfont {
					font-size: 20rpx;
				}
			}

			.yiguanzhu {
				width: 42rpx;
				height: 42rpx;
			}
		}

		.mention {
			width: 92rpx;
			height: 92rpx;
			position: relative;

			image {
				width: 58rpx;
				height: 58rpx;
				position: absolute;
				right: 16rpx;
			}

			.count {
				width: 30rpx;
				height: 30rpx;
				line-height: 30rpx;
				background: #fff;
				border-radius: 100%;
				display: flex;
				align-items: center;
				justify-content: center;
				color: #E93323;
				position: absolute;
				right: 8rpx;
				top: -12rpx;
				font-size: 20rpx;
			}
		}

		.iconfont {
			color: #ffffff;

			&.likeNumActive {
				color: #E93323;
			}
		}
	}

	.info-text {
		margin-top: 10rpx;
		color: #ffffff;
		font-size: 22rpx;
	}

	.userAvatar {
		border-radius: 500%;
		border-style: solid;
		border-width: 2px;
		border-color: #ffffff;
		width: 80rpx;
		height: 80rpx;
		display: block;
	}

	.video-dian {
		position: relative;
		width: 66rpx;
		height: 66rpx;
		align-items: center;
		justify-content: center;
		flex-direction: row;
		left: 12rpx;

		.dian {
			display: inline-block;
			width: 13rpx;
			height: 13rpx;
			background-color: #fff;
			border-radius: 100%;
			margin-right: 10rpx;

			&:last-child {
				margin-right: 0;
			}
		}
	}

	.product {
		display: block;
		margin-top: 27rpx;

		.product-item {
			display: inline-block;
			width: 444rpx;
			height: 136rpx;
			background: rgba(0, 0, 0, .55);
			border-radius: 16rpx;
			padding: 16rpx 15rpx;
			margin-right: 30rpx;
		}

		/deep/uni-swiper,
		/deep/swiper {
			display: block;
			width: 500rpx !important;
			height: 136rpx !important;

			.swiper-count {
				display: block;
				width: 444rpx !important;
				height: 136rpx !important;
				background: rgba(0, 0, 0, .55);
				border-radius: 12rpx;
				padding: 16rpx 15rpx;
			}

			.swiper-item {
				display: block;
			}
		}

		.item-count {
			width: 414rpx;
			flex-direction: row;
			justify-content: space-between;
			border-radius: 12rpx;

			.picture {
				width: 104rpx;
				height: 104rpx;
				border-radius: 16rpx;

				image {
					width: 104rpx;
					height: 104rpx;
					border-radius: 10rpx;
				}
			}

			.product-text {
				width: 296rpx;
				justify-content: space-between;

				.buy {
					padding: 4rpx 7rpx;
					background: #5D5747;
					border-radius: 3px 3px 3px 3px;
					font-size: 16rpx;
					display: inline-block;
				}

				.name {
					width: 266rpx;
					font-size: 26rpx;
				}

				.product-price {
					display: flex;
					flex-direction: row;
					justify-content: space-between;
					align-items: center;

					.price {
						display: flex;
						flex-direction: row;
						align-items: flex-end;
						font-size: 24rpx;

						text {
							font-size: 26rpx;
						}
					}

					.buy-btn {
						width: 89rpx;
						height: 36rpx;
						display: flex;
						align-items: center;
						justify-content: center;
						color: #fff;
						border-radius: 26rpx;
						background: #E93323;
						font-size: 20rpx;
					}
				}
			}
		}
	}

	.content {
		width: 590rpx;
		position: absolute;
		bottom: 80rpx;
		/* justify-content: center; */
		padding: 15rpx 0;
		flex-direction: column;
		justify-content: flex-start;
		color: #ffffff;
		left: 24rpx;

		.time {
			font-size: 24rpx;
			color: rgba(255, 255, 255, 0.5);
			margin-left: 12rpx;
		}

		.cart {
			height: 48rpx;
			flex-direction: row;

			.cartName {
				font-size: 28rpx;
				color: #fff;
			}
		}
	}

	.manage {
		width: 210rpx;
		background: #ffffff;
		box-shadow: 0 2rpx 15rpx rgba(0, 0, 0, 0.1);
		padding: 0 15rpx;
		position: absolute;
		bottom: -65rpx;
		right: 113rpx;
		z-index: 10;
		border-radius: 16rpx;

		.iconfont {
			color: #333 !important;
		}

		&::before {
			content: "";
			display: inline-block;
			width: 26rpx;
			height: 26rpx;
			background: #ffffff;
			transform: rotate(137deg);
			position: absolute;
			top: 102rpx;
			right: -8rpx;
			box-shadow: -1rpx -1rpx 1rpx rgba(0, 0, 0, 0.05);
		}

		.items {
			border-bottom: 1px solid #EEEEEE;
			color: #282828;
			font-size: 26rpx;
			padding: 20rpx 0;
			display: flex;
			align-items: center;

			&:last-child {
				border-bottom: none;
			}

			.iconfont {
				margin: 0 16rpx 0;
			}
		}
	}
</style>