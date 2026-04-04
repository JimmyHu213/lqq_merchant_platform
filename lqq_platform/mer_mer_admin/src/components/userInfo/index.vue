<template>
  <div class="acea-row row-middle">
    <div class="demo-image__preview line-heightOne mr5">
      <el-image :src="row.avatar" class="el-image" :preview-src-list="[row.avatar]" fit="cover"/>
    </div>
    <span :class="row.isLogoff ? 'red' : ''" class="mr5">{{ row.nickname || '--' }}</span>
    <span class="red"> | {{ row.uid }} | </span>
    <img :src="getSexImage(row.sex)" :title="sexFilter(row.sex)" class="sexImg"/>
    <span v-if="row.isLogoff" class="red"> | (已注销)</span>
  </div>
</template>

<script>
export default {
  name: "index",
  props: {
    row: {
      type: Object,
      default: () => {},
    },
  },
  filters: {
    sexFilter(status) {
      const statusMap = {
        0: '未知',
        1: '男',
        2: '女',
        3: '保密',
      };
      return statusMap[status];
    },
  },
  methods: {
    //性别
    getSexImage(sex) {
      const iconMap = {
        0: 'unknown.png',
        1: 'man.png',
        2: 'woman.png',
        3: 'unknown.png',
      };
      const imageName = iconMap[sex] || 'unknown.png';
      return require(`@/assets/imgs/${imageName}`);
    },
    sexFilter(status) {
      const statusMap = {
        0: '未知',
        1: '男',
        2: '女',
        3: '保密',
      };
      return statusMap[status];
    },
  }
}
</script>

<style scoped lang="scss">
.sexImg{
  width: 16px !important;
  height: 16px !important;
  margin-left: 2px;
}
</style>