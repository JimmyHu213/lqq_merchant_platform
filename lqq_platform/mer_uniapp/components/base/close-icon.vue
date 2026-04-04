<template>
  <view class="bottom_close" :style="topStyle" @click="close"><view class="iconfont icon-ic_close f-s-24"></view></view>
</template>

<script>
export default {
  name: "close-icon",
  props:{
    topStyle:{
      type: String,
      default: ''
    }
  },
  methods: {
    close(){
      this.$emit('handle-close')
    }
  }
}
</script>

<style scoped>

</style>