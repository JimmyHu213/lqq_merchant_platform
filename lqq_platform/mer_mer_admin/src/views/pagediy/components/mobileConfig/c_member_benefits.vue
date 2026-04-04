<template>
  <div class="mobile-config pro">
    <div v-for="(item, key) in rCom" :key="key">
      <component
          :is="item.components.name"
          :configObj="configObj"
          ref="childData"
          :configNme="item.configNme"
          :key="key"
          @getConfig="getConfig"
          :index="activeIndex"
          :number="num"
          :num="item.num"
      ></component>
    </div>
    <rightBtn :activeIndex="activeIndex" :configObj="configObj"></rightBtn>
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
import toolCom from "@/components/PageDiy/mobileConfigRight";
import rightBtn from "@/components/PageDiy/rightBtn";
export default {
  name: "c_member_benefits",
  componentsName: 'member_benefits',
  components: {
    ...toolCom,
    rightBtn,
  },
  props: {
    activeIndex: {
      type: null,
    },
    num: {
      type: null,
    },
    index: {
      type: null,
    },
  },
  data(){
    return {
      configObj: {},
      rCom: [
        {
          components: toolCom.c_checked_tab,
          configNme: 'setUp',
        },
      ],
      automatic: [
        {
          components: toolCom.c_title,
          configNme: 'typeConfig',
        },
        {
          components: toolCom.c_checkbox_group,
          configNme: 'typeConfig',
        }
      ],
      type: 0,
    };
  },
  watch: {
    num(nVal) {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[nVal]));
      this.configObj = value;
    },
    configObj: {
      handler(nVal, oVal) {
        this.$store.commit('mobildConfig/UPDATEARR', { num: this.num, val: nVal });
      },
      deep: true,
    },
    'configObj.setUp.tabVal': {
      handler(nVal, oVal) {
        var arr = [this.rCom[0]];
        if (nVal == 0) {
          this.rCom = arr.concat(this.automatic);
        } else {
          let tempArr = [
            {
              components: toolCom.c_title,
              configNme: 'levelColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'levelColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'levelBgColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'notAchievedColor',
            },
            {
              components: toolCom.c_radio,
              configNme: 'themeStyleConfig',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'lineColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'achieveColor',
            },
            {
              components: toolCom.c_slider,
              configNme: 'progressUpConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'progressDownConfig',
            },
            {
              components: toolCom.c_title,
              configNme: 'rightsBgColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'rightsBgColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'rightsColor',
            },
            {
              components: toolCom.c_slider,
              configNme: 'rightsUpConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'rightsDownConfig',
            },
            {
              components: toolCom.c_title,
              configNme: 'lrConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'lrConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'mbConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'bgStyle',
            },
          ];
          this.rCom = arr.concat(tempArr);
        }
      },
      deep: true,
    },
  },
  mounted() {
    this.$nextTick(() => {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[this.num]));
      this.configObj = value;
    });
  },
  methods: {
    getConfig(data) {}
  }
}
</script>

<style scoped>

</style>