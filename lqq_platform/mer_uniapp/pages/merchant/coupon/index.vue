<template>
  <view class="classify_coupon" :data-theme="theme">
    <coupon-and-classify :merId="merId" v-if="merId" tabActive="coupon"></coupon-and-classify>
    <page-footer :merId="merId" v-if="merId" fromTo="merHome"></page-footer>
  </view>
</template>

<script>
import couponAndClassify from '../components/couponAndClassify/index.vue';
import pageFooter from "../../../components/pageFooter";
let app = getApp();
export default {
  name: "index",
  components: {
    couponAndClassify,
    pageFooter
  },
  onLoad(options) {
    this.merId = Number(options.merId)
  },
  data() {
    return {
      theme: app.globalData.theme,
      merId: 0
    }
  },
  methods: {
  }

}
</script>

<style scoped>

</style>