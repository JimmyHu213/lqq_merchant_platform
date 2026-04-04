<template>
  <div class="mobile-page" v-if="configObj">
    <div class="box" v-html="richText"></div>
  </div>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import { mapState, mapMutations } from 'vuex';
export default {
  name: 'home_ueditor',
  cname: '富文本',
  configName: 'c_home_ueditor',
  icon: 'icon-biaodanzujian-tupian',
  type: 0, // 0 基础组件 1 营销组件 2会员 3工具组件
  defaultName: 'richTextEditor', // 外面匹配名称
  props: {
    index: {
      type: null,
      default: -1,
    },
    num: {
      type: null,
    },
  },
  computed: {
    ...mapState('mobildConfig', ['defaultArray']),
  },
  watch: {
    pageData: {
      handler(nVal, oVal) {
        this.setConfig(nVal);
      },
      deep: true,
    },
    num: {
      handler(nVal, oVal) {
        let data = this.$store.state.mobildConfig.defaultArray[nVal];
        this.setConfig(data);
      },
      deep: true,
    },
    defaultArray: {
      handler(nVal, oVal) {
        let data = this.$store.state.mobildConfig.defaultArray[this.num];
        this.setConfig(data);
      },
      deep: true,
    },
  },
  data() {
    return {
      // 默认初始化数据禁止修改
      defaultConfig: {
        name: 'richTextEditor',
        timestamp: this.num,
        titleConfig: {
          title: '',
          val: 'richTextEditor',
        },
        richText: {
          tabTitle: '富文本内容',
          val: '',
        },
      },
      pageData: {},
      richText: '',
      configObj: null,
    };
  },
  mounted() {
    this.$nextTick(() => {
      if (this.num) {
        this.pageData = this.$store.state.mobildConfig.defaultArray[this.num];
        this.setConfig(this.pageData);
      }
    });
  },
  methods: {
    setConfig(data) {
      if (!data) return;
      if (data) {
        this.configObj = data;
        this.richText = data.richText.val;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.mobile-page ::v-deep video {
  width: 100% !important;
}
.box {
  min-height: 100px;
  padding: 10px;
  ::v-deep img {
    max-width: 100%;
    height: auto;
  }
}
</style>
