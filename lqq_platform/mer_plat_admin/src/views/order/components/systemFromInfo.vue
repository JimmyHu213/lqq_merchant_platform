<template>
<div class="acea-row">
  <div v-if="item.title !== 'richTextEditor'" class="lang" :title="item.title">{{ item.title }}</div>
  <div v-if="item.title !== 'richTextEditor'">{{ item.title.includes(':') ? '' : '：' }}</div>
  <div v-if="item.title === 'richTextEditor'">
    <div class="contentPic" v-html="item.value"></div>
  </div>
  <div v-else-if="!Array.isArray(item.value)" class="value">{{ item.value | filterEmpty }}</div>
  <div v-else class="flex conter">
    <template v-if="item.value">
      <div v-for="(pic, idx) in item.value" :key="idx">
        <el-image v-if="pic.includes('http')" class="pictrue" :src="pic" :preview-src-list="[pic]" />
        <div v-else class="text-14px fontColor333 ml-5px acea-row row-middle mr5">
          {{ pic }}
          <div style="margin-left: 6px" v-show="idx < item.value.length - 1">-</div>
        </div>
      </div>
    </template>
    <template v-else> - </template>
  </div>
</div>
</template>

<script>
export default {
  name: "systemFromInfo",
  props: {
    item: {
      type: Object,
      default: function() {
        return {}
      }
    },
  },
}
</script>

<style scoped>

</style>