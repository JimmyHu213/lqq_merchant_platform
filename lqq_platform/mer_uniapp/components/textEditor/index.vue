<template>
  <!-- 富文本 -->
  <view v-if="description">
    <view class="rich_text">
      <!-- #ifdef MP || APP-PLUS -->
      <mp-html :content="description" :tag-style="tagStyle" selectable="true"  show-img-menu="true"/>
      <!-- #endif -->
      <!-- #ifdef H5 -->
      <view v-html="description"></view>
      <!-- #endif -->
    </view>
  </view>
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
import mpHtml from "@/uni_modules/mp-html/components/mp-html/mp-html.vue";
export default {
  name: 'richText',
  props: {
    richTextVal: {
      type: String,
      default: () => ''
    }
  },
  data(){
    return{
      tagStyle: {
        image: 'width:100%;display:block;',
        img: 'width:100%;display:block;',
        table: 'width:100%',
        video: 'width:100%'
      },
    }
  },
  components:{
    mpHtml
  },
  computed: {
    //富文本内容
    description() {
      return this.richTextVal.replace(/\<img/gi, '<img style="max-width:100%;height:auto" ')
          .replace(/style="text-wrap: wrap;"/gi, '')
    }
  }
}
</script>

<style lang="scss" scoped>
.rich_text {
  width: 100%;
  overflow: hidden;
}
</style>