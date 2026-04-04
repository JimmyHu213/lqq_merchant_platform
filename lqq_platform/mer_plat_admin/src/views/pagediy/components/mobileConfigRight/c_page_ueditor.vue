<template>
  <!--富文本编辑器-->
  <div class="box borderPadding" v-if="configData">
    <Tinymce v-model="newVal" style="width: 100%; height: 60%" :key="keyIndex" @input="(info) => tinymceInput(info)" @change="changeTinymce"></Tinymce>
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
import Tinymce from '@/components/Tinymce/index';
import { getToken } from '@/utils/auth';

export default {
  name: 'c_page_ueditor',
  props: {
    configObj: {
      type: Object,
    },
    configNme: {
      type: String,
    },
  },
  components: { Tinymce },
  data() {
    // const url = SettingMer.https + '/upload/image/0/file?ueditor=1&token=' + getToken()
    return {
      myConfig: {
        autoHeightEnabled: false, // 编辑器不自动被内容撑高
        initialFrameHeight: 350, // 初始容器高度
        initialFrameWidth: '100%', // 初始容器宽度
        UEDITOR_HOME_URL: '/UEditor/',
        imageFieldName: 'file',
        imageUrlPrefix: '',
        imageActionName: 'upfile',
        imageMaxSize: 2048000,
        imageAllowFiles: ['.png', '.jpg', '.jpeg', '.gif', '.bmp'],
      },
      description: '',
      defaults: {},
      configData: {},
      newVal: '',
      keyIndex: 0,
    };
  },
  created() {
    this.defaults = this.configObj;
    this.configData = this.configObj[this.configNme];
    this.newVal = this.configData.val;
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
      //  this.keyIndex += 1;
        this.defaults = nVal;
        this.configData = nVal[this.configNme];
        this.newVal = nVal[this.configNme].val || '';
      },
      immediate: true,
      deep: true,
    },
  },
  methods: {
    changeTinymce(){
      this.keyIndex += 1;
    },
    tinymceInput (info) {

   //   this.newVal = info

      this.$emit('getConfig', info);
      this.configData.val = info
    },
    // 获取组件参数
    getConfig(data) {
    },
  }
};
</script>

<style scoped lang="scss">
::v-deep.mce-last {
  display: flex;
  flex-wrap: wrap;
}
</style>
