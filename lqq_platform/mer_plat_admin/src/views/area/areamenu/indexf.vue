<template>
  <div class="divBox">
    <!-- 使用封装的区域菜单组件，传递systemType为parent -->
    <AreaMenuComponent system-type="parent" />
  </div>
</template>

<script>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import AreaMenuComponent from './AreaMenuComponent.vue';

export default {
  name: 'AreaMenu',
  components: {
    AreaMenuComponent
  }
};
</script>
<style lang="scss" scoped>
/* 样式已移至组件内部 */
</style>
