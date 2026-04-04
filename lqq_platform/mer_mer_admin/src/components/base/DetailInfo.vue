<template>
  <div class="detail-info-container">
    <div v-for="(section, index) in list" :key="index" class="detailSection">
      <div class="title">{{ section.title }}</div>
      <ul class="list">
        <li v-for="(item, i) in section.list" :key="i" class="item" :class="item.colClass">
          <div class="lang" v-if="item.label">{{ item.label }}：</div>
          <div class="value">
            <slot v-if="item.slot" :name="item.slot" :row="item"></slot>
            <span v-else :class="item.class">{{ item.value }}</span>
          </div>
        </li>
      </ul>
      <!-- 额外的底部内容插槽 -->
      <slot v-if="section.bottomSlot" :name="section.bottomSlot"></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DetailInfo',
  props: {
    list: {
      type: Array,
      default: () => [],
    },
  },
};
</script>

<style scoped lang="scss">
.detailSection .item.width100 {
  flex: 0 0 100% !important;
  width: 100% !important;
}
</style>
