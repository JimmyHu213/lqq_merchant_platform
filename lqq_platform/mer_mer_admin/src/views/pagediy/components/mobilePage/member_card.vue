<template>
  <div v-if="configObj" class="member-card-container" :style="boxStyle">
    <!-- 顶部会员信息栏 -->
    <div class="member-header acea-row" :style="bgImgStyle" >
      <div class="member-brand">
        <img
          v-if="configObj.logoConfig.url && logoShow"
          :src="configObj.logoConfig.url"
          alt="品牌logo"
          class="brand-image"
        />
        <span class="brand-name" :style="merNameColor">{{ configObj.titleConfig.val }}</span>
        <span v-show="levelShow" class="member-level" :style="memNameColor">黄金会员</span>
      </div>
      <div v-show="ruleShow" class="member-rule" :style="rulesNameColor">会员规则</div>
      <!-- 购物余额信息 -->
      <div class="balance-section">
        <div>
          <div v-show="priceShow" class="balance-info" :style="moneyColor">
            <span class="balance-label">购物余额：</span>
            <span class="balance-amount line-heightOne">¥980.00</span>
            <i class="el-icon-arrow-right"></i>
          </div>
        </div>
        <div class="recharge-btn-box">
          <div v-show="btnShow" class="recharge-btn" :style="btnColorStyle">充值</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex';
import { filterThemeRgba } from '@/filters';

export default {
  name: 'member_card',
  cname: '会员卡片',
  icon: 't-icon-huiyuankapian',
  configName: 'c_member_card',
  type: 2, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'memberCard', // 外面匹配名称
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
    //背景图片
    bgImgStyle() {
      return {
        'background-image': `url(${this.bgImgUrl})`,
        'border-radius': this.configObj.contentStyle.val ? this.configObj.contentStyle.val + 'px' : '0',
      };
    },
    //最外层盒子的样式
    boxStyle() {
      return [
        {
          background: `linear-gradient(${this.configObj.bgColor.color[0].item}, ${this.configObj.bgColor.color[1].item})`,
        },
        { 'border-radius': this.configObj.bgStyle.val ? this.configObj.bgStyle.val + 'px' : '0' },
        { margin: this.configObj.mbConfig.val + 'px' + ' ' + this.configObj.lrConfig.val + 'px' + ' ' + 0 },
        { padding: this.configObj.upConfig.val + 'px' + ' ' + '10px' + ' ' + this.configObj.downConfig.val + 'px' }
      ];
    },
    // 领取按钮
    btnColorStyle() {
      return [
        {
          background: this.themeStyle ? this.configObj.btnColor.color[0].item : this.themeRgba,
          color: this.themeStyle ? this.configObj.itemBgColor.color[0].item : this.themeColor,
        },
      ];
    },
    // 店铺名称
    merNameColor() {
      return { color: this.configObj.merNameColor.color[0].item };
    },
    // 会员文字
    memNameColor() {
      return { color: this.configObj.memNameColor  ? this.configObj.memNameColor.color[0].item : '', backgroundColor: 'rgba(0, 0, 0, 0.3)' };
    },
    // 规则文字
    rulesNameColor() {
      return { color: this.configObj.rulesNameColor.color[0].item };
    },
    // 购物金文字
    moneyColor() {
      return { color: this.configObj.moneyColor.color[0].item };
    },
    logoShow() {
      if (this.configObj.typeConfig.activeValue.indexOf(0) !== -1) {
        return true;
      } else {
        return false;
      }
    },
    levelShow() {
      //等级
      if (this.configObj.typeConfig.activeValue.indexOf(1) !== -1) {
        return true;
      } else {
        return false;
      }
    },
    ruleShow() {
      //规则
      if (this.configObj.typeConfig.activeValue.indexOf(2) !== -1) {
        return true;
      } else {
        return false;
      }
    },
    btnShow() {
      //充值按钮
      if (this.configObj.typeConfig.activeValue.indexOf(3) !== -1) {
        return true;
      } else {
        return false;
      }
    },
    priceShow() {
      //购物金
      if (this.configObj.typeConfig.activeValue.indexOf(4) !== -1) {
        return true;
      } else {
        return false;
      }
    },
    // 会员权益列表
    benefitList() {
      return [
        { name: '会员专享价', icon: '💎' },
        { name: '充值赠送', icon: '💰' },
        { name: '会员专享券', icon: '🎫' },
        { name: '入会有礼', icon: '🎁' },
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
        this.configObj.btnColor.isShow = this.configObj.themeStyleConfig.tabVal;
        this.configObj.itemBgColor.isShow = this.configObj.themeStyleConfig.tabVal;
      },
      deep: true,
    },
  },
  data() {
    return {
      defaultConfig: {
        name: 'memberCard',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '会员卡片',
        },
        //显示内容
        typeConfig: {
          title: '展示信息',
          tabTitle: '显示内容',
          name: 'rowsNum',
          activeValue: [0, 1, 2, 3, 4, 5],
          list: [
            {
              val: '店铺Logo',
            },
            {
              val: '会员等级',
            },
            {
              val: '会员规则',
            },
            {
              val: '充值按钮',
            },
            {
              val: '购物金余额',
            },
          ],
        },
        cardLogoConfig: {
          isShow: 1,
          tabTitle: '会员卡片',
          title: '图片',
          tips: '建议：710px * 250px',
          url: localStorage.getItem('mediaDomain') + '/crmebimage/presets/cardBg.png'
          // url: localStorage.getItem('mediaDomain') + '/crmebimage/perset/202412/yhq.png',
        },
        logoConfig: {
          isShow: 1,
          title: 'LOGO',
          tips: '建议：44px * 44px',
          url: require('../../images/cardLogo.png'),
          // url: localStorage.getItem('mediaDomain') + '/crmebimage/perset/202412/yhq.png',
        },
        titleConfig: {
          title: '品牌名称',
          place: '请输入品牌名称',
          isShow: 1,
          max: 10,
          val: '海澜之家',
        },
        // 背景颜色
        bgColor: {
          tabTitle: '颜色设置',
          title: '背景颜色',
          isShow: 1,
          color: [
            {
              item: 'rgba(255, 255, 255, 1)',
            },
            {
              item: 'rgba(255, 255, 255, 1)',
            },
          ],
          default: [
            {
              item: 'rgba(255, 255, 255, 1)',
            },
            {
              item: 'rgba(255, 255, 255, 1)',
            },
          ],
        },
        // 颜色设置
        merNameColor: {
          tabTitle: '颜色设置',
          title: '品牌名称',
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
        // 颜色设置
        memNameColor: {
          title: '会员文字',
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
        rulesNameColor: {
          title: '规则文字',
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
        moneyColor: {
          title: '购物金文字',
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
        btnColor: {
          isShow: 0,
          title: '按钮背景',
          color: [
            {
              item: '#FDD9BF',
            },
          ],
          default: [
            {
              item: '#FDD9BF',
            },
          ],
        },
        itemBgColor: {
          isShow: 0,
          title: '按钮文字',
          color: [
            {
              item: '#C26125',
            },
          ],
          default: [
            {
              item: '#C26125',
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
        mbConfig: {
          title: '页面间距',
          val: 10,
          min: 0,
        },
        bgStyle: {
          tabTitle: '圆角设置',
          title: '背景圆角',
          name: 'bgStyle',
          val: 0,
          min: 0,
          max: 30,
        },
        contentStyle: {
          title: '内容圆角',
          name: 'bgStyle',
          val: 0,
          min: 0,
          max: 30,
        },
      },
      configObj: null,
      themeStyle: 0,
      themeColor: '',
      themeRgba: '',
      bgImgUrl: '',
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
        this.themeStyle = data.themeStyleConfig.tabVal;
        this.themeColor = this.$options.filters.filterTheme(this.mobileTheme - 1);
        this.themeRgba = this.$options.filters.filterThemeRgba(this.mobileTheme - 1);
        this.bgImgUrl = this.configObj.cardLogoConfig.url;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.member-card-container {
  overflow: hidden;
}

.member-header {
  background-repeat: no-repeat;
  background-size: cover;
  color: #ffffff;
  padding: 14px 0 14px 14px;
  position: relative;
  height: 125px;
  overflow: hidden;
  justify-content: space-between;
}

.member-brand {
  display: flex;
  z-index: 1;
  height: 24px;
  align-items: center;
}

.brand-image {
  width: 22px;
  height: 22px;
  margin-right: 7px;
  border-radius: 4px;
}

.brand-name {
  font-size: 14px;
  font-weight: 500;
  margin-right: 9px;
}

.member-box {
  height: 17px;
  border-radius: 4px;
}

.member-level {
  display: inline-block;
  line-height: 19px;
  font-size: 10px;
  height: 17px;
  padding: 0 4px;
  border-radius: 4px;
}

.member-rule {
  font-size: 12px;
  z-index: 1;
  width: 68px;
  height: 24px;
  text-align: center;
  line-height: 24px;
  background-color: rgba(0, 0, 0, 0.3);
  border-radius: 12px 0px 0px 12px;
}

.balance-section {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(0, 0, 0, 0.3);
}

.balance-info {
  display: flex;
  align-items: center;
  height: 26px;
}

.balance-label {
  font-size: 12px;
}

.balance-amount {
  font-size: 18px;
  font-weight: 600;
}
.recharge-btn-box {
  background-color: #fff;
  border-radius: 4px;
  line-height: 27px;
  height: 26px;
  .recharge-btn {
    padding: 0 10px;
    border-radius: 4px;
    font-size: 14px;
  }
}

.level-progress {
  padding: 20px 12px;
  background-color: #ffffff;
}

.level-dots {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  padding: 0;
}

.level-dots::before {
  content: '';
  position: absolute;
  top: 10px;
  left: 0;
  right: 0;
  height: 2px;
  background-color: #e8e8e8;
  z-index: 0;
}

.level-dot {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 12px;
  color: #999;
  width: 20%;
}

.level-dot::before {
  content: '';
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #e8e8e8;
  margin-bottom: 8px;
  transition: all 0.3s ease;
  position: relative;
}

.level-dot.current::before {
  background-color: #ff4d4f;
  box-shadow: 0 0 0 3px rgba(255, 77, 79, 0.2);
  width: 16px;
  height: 16px;
  top: -2px;
}

.level-dot.current::after {
  content: '当前';
  position: absolute;
  top: -25px;
  left: 50%;
  transform: translateX(-50%);
  background-color: #ff4d4f;
  color: #ffffff;
  padding: 4px 10px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: bold;
  white-space: nowrap;
}

.level-dot.current {
  color: #ff4d4f;
  font-weight: bold;
}

.member-benefits {
  display: flex;
  justify-content: space-around;
  padding: 16px 0;
  background-color: #ffffff;
  border-top: 1px solid #f0f0f0;
}

.benefit-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  width: 25%;
}

.benefit-icon {
  width: 60px;
  height: 60px;
  background-color: #fff5f5;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.benefit-icon-content {
  font-size: 28px;
  color: #ff4d4f;
}

.benefit-name {
  font-size: 12px;
  color: #666;
  text-align: center;
  word-break: keep-all;
}

.benefit-item:hover .benefit-icon {
  transform: scale(1.1);
}
</style>
