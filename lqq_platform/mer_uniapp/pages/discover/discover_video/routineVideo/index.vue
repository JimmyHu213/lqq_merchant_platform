<template>
  <view>
    <!-- #ifdef MP-->
    <view class="sys-head" :style="{ height: sysHeight+'px' }"></view>
    <view class='cart_nav'>
      <nav-bar ref="navBarRef" :isBackgroundColor="false" iconColor='#fff' :isHeight="false"></nav-bar>
    </view>
    <!--  #endif -->
    <swiper-video :noteId="noteId" :fromTo="fromTo"></swiper-video>
  </view>
</template>

<script>
import {
  mapGetters
} from "vuex";
import {
  getUserSpread
} from '@/utils/index.js';
import navBar from '@/components/navBar';
import mentioned from '../../components/mentioned';
import tuiBottomPopup from "../../components/tui-bottom-popup.vue";
import discoverComment from '../../components/discoverComment'
import BaseTextMore from "../../components/BaseTextMore.vue"
import swiperVideo from "../../components/swiperVideo";
let sysHeight = uni.getSystemInfoSync().statusBarHeight;
let audo = uni.createInnerAudioContext();
audo.loop = true
export default {
  components: {
    swiperVideo,
    navBar,
    tuiBottomPopup,
    mentioned,
    discoverComment,
    BaseTextMore
  },
  computed: mapGetters(['userInfo', 'isLogin', 'uid', 'globalData']),
  data() {
    return {
      sysHeight: sysHeight,
      noteId: 0,
      fromTo: '',
    }
  },
  onReady() {
    //新的标题
    uni.setNavigationBarTitle({
      title: '短视频'
    })
    //标题蓝颜色
    uni.setNavigationBarColor({
      frontColor: '#ffffff',
      backgroundColor: '#000'
    })
  },
  onLoad(options) {
    //分销码
    getUserSpread(options);

    if (options.hasOwnProperty('noteId') || options.scene) {
      if (options.scene) { // 仅仅小程序扫码进入
        let value = this.$util.getUrlParams(decodeURIComponent(options.scene));
        this.noteId = value.id ? value.id : "";
        if (value.sd) this.$store.commit('Change_Spread', value.sd);
      } else {
        this.noteId = options.noteId ? Number(options.noteId) : ''
      }
    }
    this.fromTo = options.fromTo || '';
  }
}
</script>

<style scoped lang="scss">
.sys-head {
  background-color: #000;
  color: #fff;
}
</style>