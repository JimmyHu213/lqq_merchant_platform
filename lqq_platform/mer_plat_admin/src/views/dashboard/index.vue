<template>
  <div class="divBox">
    <!--头部-->
    <template v-if="isPlatform">
      <!--小方块-->
      <grid-menu v-if="(info && info.type === 6) || (info && info.type === 3)" class="mb14" />
    </template>
    <base-info
      ref="baseInfo"
      v-if="
        checkPermi([
          'platform:statistics:home:index',
          'circle:statistics:home:index',
        ]) && info
      "
    />
    <!-- 经营数据、用户渠道 -->
    <user-overview></user-overview>
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
import baseInfo from './components/baseInfo';
import gridMenu from './components/gridMenu';
import userOverview from './components/userOverview';
import areaDataOverview from './components/areaDataOverview';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { isPlatform } from '@/utils/settingMer';
import { mapGetters } from 'vuex';
export default {
  name: 'Dashboard',
  components: { baseInfo, gridMenu, userOverview, areaDataOverview },
  computed: {
    ...mapGetters(['userInfo', 'circleUserInfo']),
  },
  data() {
    return {
      isPlatform,
      info: undefined,
    };
  },
  mounted() {
    if (isPlatform) {
      this.info = this.userInfo;
    } else {
      this.info = this.circleUserInfo;
    }
  },
  methods: {
    checkPermi,
  },
};
</script>
