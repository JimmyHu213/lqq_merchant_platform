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
  name: "c_member_recharge",
  componentsName: 'member_recharge',
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
          configNme: 'tabConfig',
        },
        {
          components: toolCom.c_radio,
          configNme: 'tabConfig',
        },
        {
          components: toolCom.c_title,
          configNme: 'titleConfig',
        },
        {
          components: toolCom.c_input_item,
          configNme: 'titleConfig',
        },
        {
          components: toolCom.c_input_item,
          configNme: 'infoConfig',
        },
        {
          components: toolCom.c_input_item,
          configNme: 'btnInfoConfig',
        },
        {
          components: toolCom.c_coupon_select,
          configNme: 'rechargeConfig',
        },
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
              configNme: 'titleColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'titleColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'infoColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'moreColor',
            },
            {
              components: toolCom.c_title,
              configNme: 'giveAwayColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'giveAwayColor',
            },
            {
              components: toolCom.c_radio,
              configNme: 'themeStyleConfig',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'rechargeBgColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'amountColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'btnColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'btnBgColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'btnInfoColor',
            },
            {
              components: toolCom.c_title,
              configNme: 'bgColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'bgColor',
            },
            {
              components: toolCom.c_slider,
              configNme: 'upConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'downConfig',
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
              configNme: 'contentConfig',
            },
            {
              components: toolCom.c_title,
              configNme: 'bgStyle',
            },
            {
              components: toolCom.c_slider,
              configNme: 'bgStyle',
            },
            {
              components: toolCom.c_slider,
              configNme: 'contentStyle',
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