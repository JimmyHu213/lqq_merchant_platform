<template>
  <div class="detailHead">
    <div class="acea-row row-between headerBox">
      <div class="full">
        <div class="order_icon"><span class="iconfont" :class="icon"></span></div>
        <div class="text">
          <div class="title">{{ title }}</div>
          <div v-if="titleLable">
            <span class="mr20" :class="colClass?colClass:''">{{ titleLable }}</span>
          </div>
        </div>
      </div>
      <div class="acea-row justify-content">
        <slot name="operation"></slot>
      </div>
    </div>

    <ul v-if="list.length" class="list">
      <li class="item" v-for="(item, index) in list" :key="index">
        <div class="title">{{ item.label }}</div>
        <div :class="item.color">{{ item.value }}</div>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'DetailHeader',
  props: {
    // 标题
    title: {
      type: String,
      default: '',
    },
    // 图标 class
    icon: {
      type: String,
      default: '',
    },
    // 单号
    orderNo: {
      type: [String, Number],
      default: '',
    },
    // 单号标签
    titleLable: {
      type: String,
      default: '',
    },
    colClass: { //样式
      type: String,
      default: '',
    },
    // 底部列表 [{label: '', value: '', color: ''}]
    list: {
      type: Array,
      default: () => [],
    },
  },
};
</script>

<style scoped></style>
