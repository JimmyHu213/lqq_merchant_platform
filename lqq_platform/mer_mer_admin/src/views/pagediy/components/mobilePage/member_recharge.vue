<template>
  <div class="mobile-page" v-if="configObj">
    <div class="member-recharge" :style="boxStyle">
      <!-- 头部信息 -->
      <div class="header acea-row row-between-wrapper mb10">
        <div class="left acea-row row-middle">
          <div class="title" :style="{ color: configObj.titleColor.color[0].item }">
            {{ configObj.titleConfig.val }}
          </div>
          <div class="info" :style="{ color: configObj.infoColor.color[0].item }">
            {{ configObj.infoConfig.val }}
          </div>
        </div>
        <div class="more" :style="{ color: configObj.moreColor.color[0].item }">
          更多<i class="el-icon-arrow-right"></i>
        </div>
      </div>

      <!-- 套餐列表 -->
      <div class="recharge-list" :class="'list-' + listStyle">
        <div
          class="recharge-item"
          v-for="(item, index) in rechargeList"
          :key="index"
          :style="[
            itemStyle,
            { marginRight: (index + 1) % colNum === 0 ? '0' : configObj.contentConfig.val + 'px' },
            { marginBottom: configObj.contentConfig.val + 'px' },
            index === 0
              ? {
                  borderColor: themeStyle ? amountColor : themeColor,
                  backgroundColor: themeStyle ? rechargeBgColor : themeRgba,
                }
              : {},
          ]"
        >
          <div
            class="recharge-price"
            :style="{
              color: index === 0 ? (themeStyle ? amountColor : themeColor) : themeStyle ? amountColor : themeColor,
            }"
          >
            <span class="symbol">充</span>
            <span class="num">{{ item.rechargeAmount }}</span>
          </div>
          <div class="give-price" :style="{ color: giveAwayColor }">送{{ item.giftAmount }}</div>
          <!-- 选中角标 -->
          <div
            class="active-icon"
            v-if="index === 0"
            :style="{ borderTopColor: themeStyle ? amountColor : themeColor }"
          >
            <i class="el-icon-check"></i>
          </div>
        </div>
      </div>

      <!-- 充值按钮 -->
      <div class="recharge-btn" :style="btnColorStyle">
        <div class="btn-text">立即充值</div>
        <div class="btn-info" :style="btnInfoColor">{{ configObj.btnInfoConfig.val }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapGetters } from 'vuex';
import { filterLinearGradient } from '@/filters';
const recharge = [
  { rechargeAmount: '4500', giftAmount: '200' },
  { rechargeAmount: '4500', giftAmount: '200' },
  { rechargeAmount: '4500', giftAmount: '200' },
  { rechargeAmount: '4500', giftAmount: '200' },
];
export default {
  name: 'member_recharge',
  cname: '购物金充值',
  icon: 't-icon-huiyuan-gouwujin',
  configName: 'c_member_recharge',
  type: 2, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'memberRecharge', // 外面匹配名称
  props: {
    index: {
      type: null,
    },
    num: {
      type: null,
    },
  },
  computed: {
    ...mapState('mobildConfig', ['defaultArray']),
    ...mapGetters(['mediaDomain', 'mobileTheme']),
    //最外层盒子的样式
    boxStyle() {
      return [
        { 'border-radius': this.configObj.bgStyle.val ? this.configObj.bgStyle.val + 'px' : '0' },
        {
          background: `linear-gradient(to right,${this.configObj.bgColor.color[0].item}, ${this.configObj.bgColor.color[1].item})`,
        },
        { margin: this.configObj.mbConfig.val + 'px' + ' ' + this.configObj.lrConfig.val + 'px' + ' ' + 0 },
        { padding: this.configObj.upConfig.val + 'px' + ' ' + '10px' + ' ' + this.configObj.downConfig.val + 'px' },
      ];
    },
    //内容边距
    contentConfig() {
      return this.configObj.contentConfig.val ? this.configObj.contentConfig.val + 'px' : '0';
    },
    scopeColor() {
      return [{ color: this.configObj.scopeColor.color[0].item }];
    },
    rechargeBgColor() {
      return this.configObj.rechargeBgColor.color[0].item;
    },
    //优惠金额颜色
    giveAwayColor() {
      return this.configObj.giveAwayColor.color[0].item;
    },
    amountColor() {
      return this.configObj.amountColor.color[0].item;
    },
    btnInfoColor() {
      return [{ color: this.themeStyle ? this.configObj.btnInfoColor.color[0].item : '#fff' }];
    },
    // 优惠券
    couponListStyle() {
      return [
        { marginBottom: this.configObj.contentConfig.val + 'px' },
        { backgroundColor: this.themeStyle ? this.configObj.couponBgColor.color[0].item : this.themeRgba }, // 默认淡橙色背景
        { borderRadius: this.configObj.contentStyle.val + 'px' },
      ];
    },
    //领取按钮
    btnColorStyle() {
      return [
        {
          background: this.themeStyle
            ? `linear-gradient(90deg,${this.configObj.btnColor.color[0].item}, ${this.configObj.btnColor.color[1].item})`
            : `linear-gradient(${this.linearGradient})`,
          color: this.themeStyle ? this.configObj.btnBgColor.color[0].item : '#fff',
        },
      ];
    },
    // 单个卡片样式
    itemStyle() {
      const margin = this.configObj.contentConfig.val;
      const col = this.colNum;
      // 计算宽度: (100% - (列数-1) * 间距) / 列数
      // 注意：这里需要返回字符串形式的 calc
      return {
        borderRadius: this.configObj.contentStyle.val + 'px',
        width: `calc((100% - ${(col - 1) * margin}px) / ${col})`,
      };
    },
    // 列数
    colNum() {
      if (this.listStyle == 0) return 2;
      if (this.listStyle == 1) return 3;
      if (this.listStyle == 2) return 4;
      return 2;
    },
    // 卡片宽度百分比
    itemWidth() {
      // (100 - (列数-1) * 间距) / 列数
      // 这里简化处理，直接用 calc 在 style 中动态计算可能更精确，或者用 flex
      // 简单起见，这里返回近似值，实际宽度在 CSS 中控制更好
      // 但因为有 marginRight，需要精确计算
      // 100% / colNum - margin * (colNum - 1) / colNum
      // 为了简单，我们用 flex 布局控制
      return 100; // 这里的返回值会在 style 中被覆盖，或者我们可以直接不返回宽度，用 flex
    },
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
    themeStyle: {
      handler(nVal, oVal) {
        this.configObj.rechargeBgColor.isShow = this.configObj.themeStyleConfig.tabVal;
        this.configObj.amountColor.isShow = this.configObj.themeStyleConfig.tabVal;
        this.configObj.btnInfoColor.isShow = this.configObj.themeStyleConfig.tabVal;
        this.configObj.btnBgColor.isShow = this.configObj.themeStyleConfig.tabVal;
        this.configObj.btnColor.isShow = this.configObj.themeStyleConfig.tabVal;
      },
      deep: true,
    },
    rechargeData(nVal, oVal) {
      if (nVal.length) {
        return (this.rechargeList = nVal);
      } else {
        return (this.rechargeList = [...recharge]);
      }
    },
  },
  data() {
    return {
      rechargeList: [...recharge],
      defaultConfig: {
        name: 'memberRecharge',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '购物金',
        },
        tabConfig: {
          title: '单行数量',
          tabTitle: '布局设置',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '2个',
            },
            {
              val: '3个',
            },
            {
              val: '4个',
            },
          ],
        },
        titleConfig: {
          title: '标题',
          tabTitle: '购物金设置',
          place: '请输入标题',
          isShow: 1,
          max: 10,
          val: '购物金',
        },
        infoConfig: {
          title: '副标题',
          place: '请输入副标题',
          isShow: 1,
          max: 30,
          val: '5万+用户已充值，人均省16.89元',
        },
        btnInfoConfig: {
          title: '按钮副标题',
          place: '请输入按钮副标题',
          isShow: 1,
          max: 30,
          val: '购物金可随时退',
        },
        rechargeConfig: {
          title: '选择套餐',
          btnTitle: '添加套餐',
          list: [],
          ids: '',
          type: 'recharge',
        },
        titleColor: {
          isShow: 1,
          title: '标题颜色',
          tabTitle: '头部设置',
          color: [
            {
              item: '#333',
            },
          ],
          default: [
            {
              item: '#333',
            },
          ],
        },
        infoColor: {
          isShow: 1,
          title: '副标题颜色',
          color: [
            {
              item: '#333',
            },
          ],
          default: [
            {
              item: '#333',
            },
          ],
        },
        moreColor: {
          isShow: 1,
          title: '更多颜色',
          color: [
            {
              item: '#999',
            },
          ],
          default: [
            {
              item: '#999',
            },
          ],
        },
        giveAwayColor: {
          title: '赠送金额',
          tabTitle: '购物金设置',
          isShow: 1,
          color: [
            {
              item: '#333333',
            },
          ],
          default: [
            {
              item: '#333333',
            },
          ],
        },
        //色调
        themeStyleConfig: {
          title: '色调',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '跟随主题风格',
            },
            {
              val: '自定义',
            },
          ],
        },
        rechargeBgColor: {
          isShow: 0,
          title: '充值背景',
          color: [
            {
              item: 'rgba(233, 51, 35, 0.05)',
            },
          ],
          default: [
            {
              item: 'rgba(233, 51, 35, 0.05)',
            },
          ],
        },
        amountColor: {
          isShow: 0,
          title: '充值金额',
          color: [
            {
              item: '#E93323',
            },
          ],
          default: [
            {
              item: '#E93323',
            },
          ],
        },
        btnColor: {
          isShow: 0,
          title: '按钮背景',
          color: [
            {
              item: '#FF7931',
            },
            {
              item: '#E93323',
            },
          ],
          default: [
            {
              item: '#FF7931',
            },
            {
              item: '#E93323',
            },
          ],
        },
        btnBgColor: {
          title: '按钮文字',
          isShow: 0,
          color: [
            {
              item: '#FFFFFF',
            },
          ],
          default: [
            {
              item: '#FFFFFF',
            },
          ],
        },
        btnInfoColor: {
          title: '按钮提示',
          isShow: 0,
          color: [
            {
              item: '#FFFFFF',
            },
          ],
          default: [
            {
              item: '#FFFFFF',
            },
          ],
        },
        // 背景颜色
        bgColor: {
          tabTitle: '卡片设置',
          title: '组件背景',
          isShow: 1,
          color: [
            {
              item: '#FFFFFF',
            },
            {
              item: '#FFFFFF',
            },
          ],
          default: [
            {
              item: '#FFFFFF',
            },
            {
              item: '#FFFFFF',
            },
          ],
        },
        // 上间距
        upConfig: {
          tabTitle: '边距设置',
          title: '上边距',
          val: 10,
          min: 0,
          max: 100,
        },
        // 下间距
        downConfig: {
          title: '下边距',
          val: 10,
          min: 0,
        },
        // 左右间距
        lrConfig: {
          title: '左右边距',
          val: 12,
          min: 0,
          max: 25,
        },
        contentConfig: {
          title: '内容间距',
          val: 10,
          min: 0,
          max: 30,
        },
        mbConfig: {
          title: '页面间距',
          val: 10,
          min: 0,
        },
        bgStyle: {
          tabTitle: '圆角设置',
          title: '背景圆角',
          name: 'bgStyle',
          val: 10,
          min: 0,
          max: 30,
        },
        contentStyle: {
          title: '内容圆角',
          name: 'contentStyle',
          val: 7,
          min: 0,
          max: 10,
        },
      },
      configObj: null,
      listStyle: 0,
      themeStyle: 0,
      themeColor: '',
      linearGradient: '',
      themeRgba: '',
      rechargeData: [],
    };
  },
  mounted() {
    this.$nextTick(() => {
      let pageData = this.$store.state.mobildConfig.defaultArray[this.num];
      this.setConfig(pageData);
    });
  },
  methods: {
    setConfig(data) {
      if (!data) return;
      if (data) {
        this.configObj = data;
        this.listStyle = this.configObj.tabConfig.tabVal;
        this.themeStyle = data.themeStyleConfig.tabVal;
        this.themeColor = this.$options.filters.filterTheme(this.mobileTheme - 1); //主题色
        this.linearGradient = this.$options.filters.filterLinearGradient(this.mobileTheme - 1); //渐变色
        this.themeRgba = this.$options.filters.filterThemeRgba(this.mobileTheme - 1); //主题色淡色
        this.rechargeData = this.configObj.rechargeConfig.list;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.member-recharge {
  overflow: hidden;

  .header {
    .left {
      .title {
        font-size: 15px;
        font-weight: 500;
        margin-right: 6px;
      }
      .info {
        font-size: 12px;
      }
    }

    .more {
      font-size: 12px;
      display: flex;
      align-items: center;
    }
  }

  .recharge-list {
    display: flex;
    flex-wrap: wrap;

    .recharge-item {
      background: #fff;
      border: 1px solid #eee;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 14px 0;
      position: relative;
      box-sizing: border-box;
      overflow: hidden;

      .recharge-price {
        font-size: 26px;
        font-weight: 600;
        margin-bottom: 3px;

        .symbol {
          font-size: 15px;
          margin-right: 2px;
        }
      }

      .give-price {
        font-size: 13px;
      }

      .active-icon {
        position: absolute;
        right: 0;
        top: 0;
        width: 0;
        height: 0;
        border-top: 24px solid;
        border-left: 24px solid transparent;
        color: #fff;

        .el-icon-check {
          position: absolute;
          top: -22px;
          right: 1px;
          font-size: 12px;
          transform: scale(0.8);
        }
      }
    }

    // 2列
    &.list-0 {
      .recharge-item {
        // width 由内联样式控制
      }
    }
    // 3列
    &.list-1 {
      .recharge-item {
        // width 由内联样式控制
      }
    }
    // 4列
    &.list-2 {
      .recharge-item {
        // width 由内联样式控制
        padding: 10px 0;

        .recharge-price {
          font-size: 14px;
        }
        .num{
          font-size: 20px;
        }
      }
    }
  }

  .recharge-btn {
    width: 100%;
    height: 46px;
    border-radius: 25px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin-top: 10px;

    .btn-text {
      font-size: 15px;
      font-weight: 500;
      margin-bottom: 3px;
    }

    .btn-info {
      font-size: 11px;
    }
  }
}
</style>
