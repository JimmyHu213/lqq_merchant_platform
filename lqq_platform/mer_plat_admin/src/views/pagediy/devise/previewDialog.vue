<template>
  <!--    列表预览弹窗-->
  <el-dialog
      :visible.sync="perViewDia.visible"
      title=""
      width="430px"
      top="7vh"
      destroy-on-close
      :close-on-click-modal="false"
  >
    <iframe
        v-if="perViewDia.visible"
        id="ifPerviewShop"
        :src="perViewDia.perViewUrl"
        style="width: 390px; height: 800px"
        frameborder="0"
    />
  </el-dialog>
</template>

<script>
export default {
  name: "previewDialog",
  props: {
    perViewDia: {
      type: Object,
      default: {},
    },
  },
}
</script>

<style scoped>

</style>