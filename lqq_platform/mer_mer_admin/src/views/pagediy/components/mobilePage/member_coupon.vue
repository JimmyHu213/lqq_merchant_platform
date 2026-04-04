<template>
  <div class="mobile-page" v-if="configObj">
    <div v-if="listStyle === 0" class="member-coupon" :style="boxStyle">
      <!-- 标题部分 -->
      <div class="header acea-row row-between-wrapper">
        <div class="title" :style="titleColor">
          {{ configObj.titleConfig.val }}
        </div>
        <div class="more" :style="moreColor">更多<i class="el-icon-arrow-right"></i></div>
      </div>

      <!-- 优惠券列表 -->
      <div class="coupon-list">
        <div
          class="coupon-item acea-row row-between-wrapper"
          v-for="(item, index) in couponList"
          :key="index"
          :style="couponListStyle"
        >
          <!-- 左侧金额/门槛 -->
          <div class="left-content" :style="{ color: priceColorStyle }">
            <div class="price">
              <span class="symbol">¥</span>
              <span class="num">{{ item.money }}</span>
            </div>
            <div class="condition">
              {{ item.minPrice > 0 ? '满' + item.minPrice + '可用' : '无门槛' }}
            </div>
          </div>

          <!-- 中间说明 -->
          <div class="middle-content">
            <div class="scope line1" :style="scopeColor">
              {{ item.name }}
            </div>
            <div v-if="item.day" class="time" :style="{ color: configObj.deadlineColor.color[0].item }">
              {{ `领取后${item.day}天内可用`  }}
            </div>
            <div v-else class="time" :style="{ color: configObj.deadlineColor.color[0].item }">
              {{ item.useStartTime + ' - ' + item.useEndTime  }}
            </div>
          </div>

          <!-- 右侧按钮 -->
          <div class="right-btn text-center" :style="btnColorStyle">去使用</div>
        </div>
      </div>
    </div>
    <div v-else class="member-coupon" :style="boxStyle">
      <!-- 标题部分 -->
      <div class="header acea-row row-between-wrapper">
        <div class="title" :style="titleColor">
          {{ configObj.titleConfig.val }}
        </div>
        <div class="more" :style="moreColor">更多<i class="el-icon-arrow-right"></i></div>
      </div>
      <!-- 优惠券列表 -->
      <div class="coupon-scroll-list">
        <div
          class="coupon-card"
          v-for="(item, index) in couponList"
          :key="index"
          :style="[
            { marginRight: contentConfig },
            { backgroundColor: themeStyle ? couponBgColor : themeRgba },
            { borderColor: priceColorStyle },
            { borderRadius: configObj.contentStyle.val ? configObj.contentStyle.val + 'px' : '0' },
          ]"
        >
          <!-- 标签 -->
          <div
            class="tag"
            :style="{
              backgroundColor: themeStyle ? labelColor :lightColor,
              color: priceColorStyle,
              borderTopLeftRadius: `${configObj.contentStyle.val}px`,
            }"
          >
            会员专享
          </div>

          <!-- 左侧金额 -->
          <div class="left-box" :style="{ color: priceColorStyle }">
            <div class="price">
              <span class="symbol">¥</span>
              <span class="num">{{ item.money }}</span>
            </div>
          </div>

          <!-- 分割线 -->
          <div class="split-line-vertical" :style="{ borderLeftColor: lightColor }"></div>

          <!-- 右侧内容 -->
          <div class="right-box">
            <div class="condition" :style="{ color: priceColorStyle }">
              {{ item.minPrice > 0 ? '满' + item.minPrice + '可用' : '无门槛' }}
            </div>
            <div class="scope" :style="scopeColor">-{{ item.category === 1 ? '商家券' : '商品券' }}-</div>
          </div>

          <!-- 装饰圆角 -->
          <div class="decoration left" :style="{ background: bgColor }"></div>
          <div class="decoration right" :style="{ background: bgColor }"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapGetters } from 'vuex';
import { filterLightColor, filterLinearGradient } from '@/filters';
const coupon = [
  { money: '450', minPrice: 299, name: '全店通用', day: '10' },
  { money: '450', minPrice: 299, name: '指定商品', day: '10' },
  { money: '450', minPrice: 299, name: '指定商品', day: '10' },
]
export default {
  name: 'member_coupon',
  cname: '会员优惠券',
  icon: 't-icon-huiyuan-youhuiquan',
  configName: 'c_member_coupon',
  type: 2, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'memberCoupon', // 外面匹配名称
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
    bgColor() {
      return this.configObj.bgColor.color[0].item;
    },
    //最外层盒子的样式
    boxStyle() {
      return [
        { 'border-radius': this.configObj.bgStyle.val ? this.configObj.bgStyle.val + 'px' : '0' },
        {
          background: this.bgColor,
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
    titleColor() {
      return [{ color: this.configObj.titleColor.color[0].item }];
    },
    labelColor() {
      return this.configObj.labelColor.color[0].item;
    },
    moreColor() {
      return [{ color: this.configObj.moreColor.color[0].item }];
    },
    //优惠金额颜色
    priceColorStyle() {
      return this.themeStyle ? this.configObj.priceColor.color[0].item : this.themeColor;
    },
    couponBgColor() {
      return this.configObj.couponBgColor.color[0].item;
    },
    // 优惠券
    couponListStyle() {
      return [
        { marginBottom: this.configObj.contentConfig.val + 'px' },
        { backgroundColor: this.themeStyle ? this.couponBgColor : this.themeRgba }, // 默认淡橙色背景
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
          color: this.configObj.btnBgColor.color[0].item,
        },
      ];
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
        if (this.listStyle === 1) {
          this.configObj.deadlineColor.isShow = 0;
          this.configObj.btnColor.isShow = 0;
          this.configObj.btnBgColor.isShow = 0;
          this.configObj.labelColor.isShow = 0;
        } else {
          this.configObj.btnBgColor.isShow = this.configObj.themeStyleConfig.tabVal;
          this.configObj.btnColor.isShow = this.configObj.themeStyleConfig.tabVal;
          this.configObj.deadlineColor.isShow = 1;
          this.configObj.labelColor.isShow = 0;
        }
        this.configObj.couponBgColor.isShow = this.configObj.themeStyleConfig.tabVal;
        this.configObj.priceColor.isShow = this.configObj.themeStyleConfig.tabVal;
      },
      deep: true,
    },
    listStyle(nVal, oVal) {
      if (nVal === 0) {
        this.configObj.deadlineColor.isShow = 1;
        this.configObj.btnColor.isShow = 0;
        this.configObj.btnBgColor.isShow = 0;
        this.configObj.labelColor.isShow = 0;
      } else {
        this.configObj.deadlineColor.isShow = 0;
        this.configObj.btnColor.isShow = 0;
        this.configObj.btnBgColor.isShow = 0;
        this.configObj.labelColor.isShow = 0;
      }
    },
    couponData(nVal, oVal){
      if(nVal.length){
        return this.couponList = nVal
      }else{
        return this.couponList = [...coupon]
      }
    }
  },
  data() {
    return {
      couponData: [],
      lightColor: '',
      couponList: [...coupon],
      defaultConfig: {
        name: 'memberCoupon',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '会员优惠券',
        },
        tabConfig: {
          title: '展示样式',
          tabTitle: '头部设置',
          tabVal: 0,
          isShow: 1,
          list: [
            {
              val: '样式1',
            },
            {
              val: '样式2',
            },
          ],
        },
        titleConfig: {
          title: '标题',
          place: '请输入标题',
          isShow: 1,
          max: 20,
          val: '会员专享',
        },
        couponConfig: {
          title: '会员专享礼包',
          btnTitle: '添加优惠券',
          list: [],
          ids: '',
          type: 'coupon',
        },
        titleColor: {
          isShow: 1,
          title: '标题颜色',
          tabTitle: '布局设置',
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
        couponBgColor: {
          isShow: 0,
          title: '优惠券背景',
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
        priceColor: {
          title: '优惠金额',
          isShow: 0,
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
        labelColor: {
          title: '会员标签',
          isShow: 0,
          color: [
            {
              item: '#FFDFDC',
            },
          ],
          default: [
            {
              item: '#FFDFDC',
            },
          ],
        },
        scopeColor: {
          tabTitle: '优惠券设置',
          title: '使用范围',
          isShow: 1,
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
        deadlineColor: {
          title: '使用期限',
          isShow: 1,
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
        // 背景颜色
        bgColor: {
          tabTitle: '样式设置',
          title: '组件背景',
          isShow: 1,
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
          max: 30,
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
      },
      configObj: null,
      listStyle: 0,
      themeStyle: 0,
      themeColor: '',
      linearGradient: '',
      themeRgba: '',
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
        this.themeRgba = this.$options.filters.filterThemeRgba(this.mobileTheme - 1); //主题rgba
        this.lightColor = this.$options.filters.filterLightColor(this.mobileTheme - 1); //主题色淡色
        this.couponData = this.configObj.couponConfig.list
      }
    },
  },
};
</script>
<style scoped lang="scss">
.member-coupon {
  overflow: hidden;

  .header {
    margin-bottom: 10px;

    .title {
      font-size: 15px;
      font-weight: 500;
      color: #333;
    }

    .more {
      font-size: 12px;
      color: #999;
      display: flex;
      align-items: center;
    }
  }

  .coupon-list {
    .coupon-item {
      padding: 16px 12px 16px 0;
      position: relative;
      border-radius: 8px;
      display: flex;
      align-items: center;

      .left-content {
        width: 100px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;

        .price {
          font-weight: 600;
          font-size: 26px;
          margin-bottom: 3px;

          .symbol {
            font-size: 16px;
            margin-right: 2px;
          }
        }

        .condition {
          font-size: 11px;
        }
      }
      .middle-content {
        width: 145px;
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;

        .scope {
          font-size: 15px;
          margin-bottom: 6px;
          font-weight: 500;
        }

        .time {
          font-size: 12px;
        }
      }

      .right-btn {
        margin-left: 10px;
        font-size: 11px;
        width: 68px;
        height: 30px;
        line-height: 30px;
        border-radius: 127px;
      }
    }
  }

  .coupon-scroll-list {
    display: flex;
    overflow-x: auto;
    padding-bottom: 5px; /* 为了滚动条不遮挡内容，或者隐藏滚动条 */

    &::-webkit-scrollbar {
      display: none;
    }

    .coupon-card {
      flex: 0 0 auto;
      width: 150px;
      height: 76px;
      border-radius: 6px;
      display: flex;
      align-items: center;
      position: relative;

      .tag {
        position: absolute;
        top: 0;
        left: 0;
        font-size: 10px;
        color: #fff;
        padding: 0 4px;
        line-height: 18px;
        line-height: 19px;
        border-bottom-right-radius: 8px;
      }

      .left-box {
        width: 68px;
        display: flex;
        justify-content: center;
        align-items: center;

        .price {
          font-weight: 600;
          font-size: 24px;

          .symbol {
            font-size: 16px;
            margin-right: 1px;
          }
          .num{
            margin-left: 2px;
          }
        }
      }

      .split-line-vertical {
        width: 1px;
        height: 50px;
        border-left: 1px dashed;
        opacity: 0.5;
      }

      .right-box {
        width: 82px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;

        .condition {
          font-size: 12px;
          margin-bottom: 3px;
        }

        .scope {
          font-size: 11px;
          transform: scale(0.9);
          transform-origin: left center;
          white-space: nowrap;
        }
      }

      .decoration {
        position: absolute;
        width: 12px;
        height: 12px;
        border-radius: 50%;
        top: 50%;
        transform: translateY(-50%);
        z-index: 1;
        border-color: transparent;
      }

      .decoration.left {
        left: -6px;
      }

      .decoration.right {
        right: -6px;
      }
    }
  }
}
</style>
