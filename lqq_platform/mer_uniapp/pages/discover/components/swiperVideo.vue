<template>
  <view class="video-swiper-container">
    <swiper
        class="video-swiper"
        :style="'width: '+ windowWidth +'px; height: '+ windowHeight +'px; background-color: #000000;'"
        :vertical="true"
        :current="currentIndex"
        :indicator-dots="false"
        @change="onSwiperChange"
    >
      <swiper-item v-for="(video, index) in innerVideoList" :key="video.id">
        <view class="video-item">
          <video
              :id="`video-${index}`"
              :src="video.video"
              :autoplay="index === currentIndex"
              :muted="false"
              :controls="false"
              objectFit="contain"
              :loop="true"
              :style="{ width: windowWidth + 'px', height: windowHeight + 'px' }"
              @play="handleVideoPlay(index)"
              @pause="handleVideoPause(index)"
              @loadeddata="handleVideoLoaded(index)"
          ></video>

          <!-- 点击暂停/播放 -->
          <view class="video-cover" @click="togglePlay(index)" v-if=" !playingMap[index]">
            <image class="play-icon" src="../static/images/play.png" mode="aspectFit"></image>
          </view>

          <view class="loading-indicator" v-if="isLoading">
            <text class="loading-text">加载中...</text>
          </view>

          <view class="video-controls" @click="togglePlay(index)"></view>
          <!-- 视频上展示内容-->
          <video-content :list="video"></video-content>
          <!-- 审核信息-->
          <view v-if="video.auditStatus !== 1">
            <review-status :auditStatus="video.auditStatus" :refusal="video.refusal"></review-status>
          </view>
          <!-- 右侧操作按钮-->
          <operation-btn :list="video" :noteId="noteId"></operation-btn>
        </view>
      </swiper-item>
    </swiper>

  </view>
</template>

<script>
import {noteDetailApi, noteRecommendApi} from "../../../api/discover";
import reviewStatus from "./reviewStatus";
import operationBtn from "./operationBtn";
import videoContent from "./videoContent";
export default {
  name: 'swiperVideo',
  props: {
    noteId: {
      type: [String, Number],
      default: ''
    },
    fromTo: {
      type: String,
      default: ''
    }
  },
  components: {
    reviewStatus,
    operationBtn,
    videoContent
  },
  computed: {
    windowHeight() {
      return this.getHeight.windowHeight - this.getHeight.barTop
    },
  },
  data() {
    return {
      getHeight: this.$util.getWXStatusHeight(),
      innerVideoList: [],
      currentIndex: 0,
      windowWidth: 0,
      isLoading: false,
      videoContexts: {},
      touchStartTime: 0,
      touchEndTime: 0,
      playingMap: {},
      // 分页相关
      where: {
        page: 1,
        limit: 5,
        noteId: ''
      },
      loadend: false,
      loading: false,
      // 播放控制相关
      isPlaying: false,
      playTimer: null,
      pauseTimer: null,
      // 用户手动暂停状态记录
      userPausedMap: {},
      // 手机浏览器自动播放相关
      hasUserInteracted: false,
      canAutoplay: false,
      autoplayAttempted: false
    }
  },
  watch: {
    noteId: {
      immediate: true,
      handler(newNoteId) {
        if (newNoteId) {
          this.windowWidth = uni.getSystemInfoSync().windowWidth
          this.where.noteId = newNoteId;
          this.loadVideoData();
        }
      }
    }
  },
  onLoad() {
    // 初始化窗口尺寸
    this.windowWidth = uni.getSystemInfoSync().windowWidth;
    // 初始化时设置canAutoplay为true，允许默认播放
    this.canAutoplay = true;

    // 如果有noteId，加载视频数据
    if (this.noteId) {
      this.where.noteId = this.noteId;
      this.loadVideoData();
    }
  },

  // 组件挂载后尝试自动播放第一个视频
  mounted() {
    this.$nextTick(() => {
      if (this.innerVideoList.length > 0 && this.currentIndex === 0) {
        this.playVideo(0);
      }
    });
  },
  onShow() {
  },
  methods: {
    // 加载视频数据
    async loadVideoData() {
      try {
        //	this.isLoading = true;

        // 先获取单个视频详情
        await this.getNoteDetail();

        // 然后获取推荐视频列表
        await this.getRecommendVideos();

      } catch (error) {
        this.isLoading = false;
      } finally {
        this.isLoading = false;
      }
    },

    // 获取单个视频详情
    async getNoteDetail() {
      if (!this.noteId) return;

      try {
        this.isLoading = true;
        const res = await noteDetailApi(this.noteId);
        const videoData = res.data;

        // 处理视频数据格式
        const processedVideo = this.processVideoData(videoData);

        // 如果是第一个视频，直接添加到列表开头
        if (this.innerVideoList.length === 0) {
          this.innerVideoList.push(processedVideo);
          this.$nextTick(() => {
            this.initVideoContexts();
            this.playVideo(0);
          });
        } else {
          // 检查是否已存在，避免重复
          const exists = this.innerVideoList.some(video => video.id === processedVideo.id);
          if (!exists) {
            this.innerVideoList.unshift(processedVideo);
            this.$nextTick(() => {
              this.initVideoContexts();
            });
          }
        }
        this.isLoading = false;
      } catch (error) {
        this.isLoading = false;
        console.error('获取视频详情失败:', error);
      }
    },

    // 获取推荐视频列表
    async getRecommendVideos() {
      if (this.loadend) return;

      try {
        this.loading = true;
        const res = await noteRecommendApi(this.where);

        const list = res.data.list || [];

        // 避免重复添加相同的视频数据
        const filteredList = list.filter(video =>
            !this.innerVideoList.some(existingVideo => existingVideo.id === video.id)
        );

        if (filteredList.length > 0) {
          // 处理视频数据格式
          const processedList = filteredList.map(video => this.processVideoData(video));

          // 添加到视频列表
          this.innerVideoList.push(...processedList);

          // 更新分页信息
          this.where.page = this.where.page + 1;
          this.loadend = this.where.page > res.data.totalPage;

          this.$nextTick(() => {
            this.initVideoContexts();
          });
        }
        this.isLoading = false;
      } catch (error) {
        console.error('获取推荐视频失败:', error);
      } finally {
        this.loading = false;
      }
    },

    // 清理视频URL,针对小程序端报错信息
    cleanVideoUrl(url) {
      if (!url) return '';

      // 移除开发工具添加的参数
      if (url.includes('#devtools_no_referrer')) {
        url = url.split('#devtools_no_referrer')[0];
      }

      // 移除其他可能的开发工具参数
      if (url.includes('#devtools')) {
        url = url.split('#devtools')[0];
      }

      // 确保URL格式正确
      if (url.startsWith('//')) {
        url = 'https:' + url;
      }

      return url;
    },

    // 处理视频数据格式
    processVideoData(video) {
      // 清理视频URL
      const videoUrl = this.cleanVideoUrl(video.video);

      return {
        ...video,
        video: videoUrl,
        platReplySwitch: video.platReplySwitch !== false,
        // 播放状态
        playIng: false,
        state: 'pause',
        isplay: false,
        loading: false
      };
    },

    initVideoContexts() {
      // 确保 videoContexts 和 playingMap 被正确初始化
      if (!this.videoContexts) {
        this.videoContexts = {};
      }
      if (!this.playingMap) {
        this.playingMap = {};
      }
      if (!this.userPausedMap) {
        this.userPausedMap = {};
      }

      this.innerVideoList.forEach((_, index) => {
        try {
          this.videoContexts[index] = uni.createVideoContext(`video-${index}`, this);
        } catch (error) {
        }
      });
    },

    // 处理视频播放事件
    handleVideoPlay(index) {
      if (this.playingMap && typeof this.playingMap === 'object') {
        this.$set(this.playingMap, index, true);
        this.videoPlaying(index);
      }
    },

    // 处理视频暂停事件
    handleVideoPause(index) {
      if (this.playingMap && typeof this.playingMap === 'object') {
        this.$set(this.playingMap, index, false);
        this.videoPaused(index);
      }
    },

    // 处理视频加载完成事件
    handleVideoLoaded(index) {
      if (index === this.currentIndex) {
        this.playVideo(index);
      }
    },

    // 轮播切换时的处理
    onSwiperChange(e) {
      const newIndex = e.detail.current;

      // 确保 playingMap 和 userPausedMap 已初始化
      if (!this.playingMap) {
        this.playingMap = {};
      }
      if (!this.userPausedMap) {
        this.userPausedMap = {};
      }

      // 暂停之前的视频
      if (this.currentIndex !== newIndex) {
        // 暂停旧视频
        if (this.videoContexts && this.videoContexts[this.currentIndex]) {
          try {
            this.videoContexts[this.currentIndex].pause();
            if (this.playingMap) {
              this.playingMap[this.currentIndex] = false;
            }
            this.videoPaused(this.currentIndex);
          } catch (error) {
            console.log('暂停旧视频失败:', error);
          }
        }

        // 更新当前索引
        this.currentIndex = newIndex;

        // 重置用户暂停状态，确保新视频能自动播放
        if (this.userPausedMap) {
          this.userPausedMap[newIndex] = false;
        }

        // 标记用户已交互，提高自动播放成功率
        this.hasUserInteracted = true;

        // 播放新视频
        this.$nextTick(() => {
          setTimeout(() => {
            this.playVideo(newIndex);
          }, 100); // 小延迟确保DOM已更新
        });
      }
    },

    // 播放视频
    playVideo(index) {
      // 确保 videoContexts 和 playingMap 已初始化
      if (!this.videoContexts || !this.videoContexts[index] || !this.playingMap) {
        console.log('播放视频失败：视频上下文或播放状态映射未初始化');
        return;
      }

      try {
        this.videoContexts[index].play();
        this.$set(this.playingMap, index, true);
        this.canAutoplay = true;
      } catch (error) {
        // 标记需要用户交互
        this.hasUserInteracted = false;
      }
    },

    // 暂停视频
    pauseVideo(index) {
      // 确保 videoContexts 和 playingMap 已初始化
      if (!this.videoContexts || !this.videoContexts[index] || !this.playingMap) {
        console.log('暂停视频失败：视频上下文或播放状态映射未初始化');
        return;
      }

      try {
        this.videoContexts[index].pause();
        this.$set(this.playingMap, index, false);
      } catch (error) {
      }
    },

    // 视频开始播放时更新状态
    videoPlaying(index) {
      // 确保 playingMap 已初始化
      if (!this.playingMap || typeof this.playingMap !== 'object') {
        this.playingMap = {};
      }
      this.$set(this.playingMap, index, true);
      if (this.innerVideoList && this.innerVideoList[index]) {
        this.innerVideoList[index].playIng = true;
        this.innerVideoList[index].state = 'playing';
        this.innerVideoList[index].isplay = true;
      }
    },

    // 视频暂停时更新状态
    videoPaused(index) {
      // 确保 playingMap 已初始化
      if (!this.playingMap || typeof this.playingMap !== 'object') {
        this.playingMap = {};
      }
      this.$set(this.playingMap, index, false);
      if (this.innerVideoList && this.innerVideoList[index]) {
        this.innerVideoList[index].playIng = false;
        this.innerVideoList[index].state = 'pause';
        this.innerVideoList[index].isplay = false;
      }
    },

    // 切换播放状态
    togglePlay(index) {
      // 确保 playingMap 和 userPausedMap 已初始化
      if (!this.playingMap || typeof this.playingMap !== 'object') {
        this.playingMap = {};
      }
      if (!this.userPausedMap || typeof this.userPausedMap !== 'object') {
        this.userPausedMap = {};
      }

      // 标记用户已交互
      this.hasUserInteracted = true;

      if (this.playingMap[index]) {
        // 暂停视频
        this.pauseVideo(index);
        this.userPausedMap[index] = true;
      } else {
        // 播放视频
        this.playVideo(index);
        this.userPausedMap[index] = false;
        // 确保视频元素已准备好
        setTimeout(() => {
          if (this.playingMap && this.videoContexts && !this.playingMap[index] && this.videoContexts[index]) {
            try {
              this.videoContexts[index].play();
            } catch (error) {
              console.log('二次尝试播放失败:', error);
            }
          }
        }, 100);
      }
    }
  }
}
</script>

<style scoped>
.video-swiper-container {
  width: 100%;
  height: 100vh;
  background-color: #000;
}
.video-swiper {
  /*width: 100%;*/
  /*height: 100%;*/
}
.video-item {
  position: relative;
  width: 100%;
  height: 100%;
}
.video-cover {
  position: absolute;
  top: 50%;
  left: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
  animation: pulse 2s infinite;
}

.pause-cover {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120rpx;
  height: 120rpx;
  background-color: rgba(0, 0, 0, 0.3);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
}

.pause-icon {
  width: 60rpx;
  height: 60rpx;
  opacity: 0.8;
}

@keyframes pulse {
  0% {
    opacity: 0.8;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(1.1);
  }
  100% {
    opacity: 0.8;
    transform: translate(-50%, -50%) scale(1);
  }
}
.play-icon {
  width: 120rpx;
  height: 120rpx;
  opacity: 0.8;
}
.play-hint {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.7);
  padding: 20rpx 40rpx;
  border-radius: 40rpx;
}
.hint-text {
  color: #fff;
  font-size: 28rpx;
}
.loading-indicator {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(0, 0, 0, 0.5);
  padding: 20rpx 40rpx;
  border-radius: 40rpx;
  z-index: 20;
}
.loading-text {
  color: #fff;
  font-size: 28rpx;
}
.video-controls {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  /*z-index: 20;*/
}
.video-info {
  position: absolute;
  bottom: 180rpx;
  left: 40rpx;
  right: 140rpx;
  color: #fff;
  z-index: 30;
}
.video-title {
  font-size: 36rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 20rpx;
}
.video-description {
  font-size: 28rpx;
  line-height: 40rpx;
  opacity: 0.9;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}
</style>


