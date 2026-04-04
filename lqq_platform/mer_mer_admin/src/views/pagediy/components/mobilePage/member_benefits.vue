<template>
  <div class="mobile-page" v-if="configObj">
    <div class="member-benefits" :style="boxStyle">
      <!-- 进度条部分 -->
      <div class="level-section" v-if="progressShow" :style="progressBoxBg">
        <div class="level-progress">
          <div class="level-item" v-for="(item, index) in levelList" :key="index">
            <div class="level-top">
              <div class="line-left" v-if="index > 0" :style="lineColor"></div>
              <div class="line-right" v-if="index < levelList.length - 1" :style="lineColor"></div>
              <div
                class="node"
                :style="{
                  background: index <= currentLevel ? achieveColor : notAchievedColor,
                }"
              >
                <div class="node-point" v-if="index < currentLevel" :style="{ background: achieveColor }"></div>
              </div>
              <div class="current-badge" v-if="index === currentLevel" :style="{ background: achieveColor }">当前</div>
            </div>
            <div class="level-name" :style="levelColor">{{ item.name }}</div>
          </div>
        </div>
      </div>

      <!-- 会员权益部分 -->
      <div class="benefit-section" v-if="rightsShow" :style="levelBoxBg">
        <div class="benefit-list">
          <div class="benefit-item" v-for="(item, index) in benefitList" :key="index">
            <div class="icon-box">
              <!-- 使用简单的图标模拟 -->
              <el-image :src="item.icon"></el-image>
            </div>
            <div class="benefit-name" :style="{ color: rightsColor }">{{ item.name }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex';
const benefits = [
  { name: '会员专享价', icon: require('../../images/rights.png') },
  { name: '充值赠送', icon: require('../../images/rights.png') },
  { name: '会员专享券', icon: require('../../images/rights.png') },
  { name: '入会有礼', icon: require('../../images/rights.png') },

]
export default {
  name: 'member_benefits',
  cname: '会员权益',
  icon: 't-icon-huiyuan-dengjiquanyi',
  configName: 'c_member_benefits',
  type: 2, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'memberBenefits', // 外面匹配名称
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
    progressShow() {
      // 进度条
      if (this.configObj.typeConfig.activeValue.indexOf(0) !== -1) {
        return true;
      } else {
        return false;
      }
    },
    rightsShow() {
      // 权益
      if (this.configObj.typeConfig.activeValue.indexOf(1) !== -1) {
        return true;
      } else {
        return false;
      }
    },
    //最外层盒子的样式
    boxStyle() {
      return [
        { 'border-radius': this.configObj.bgStyle.val ? this.configObj.bgStyle.val + 'px' : '0' },
        { margin: this.configObj.mbConfig.val + 'px' + ' ' + this.configObj.lrConfig.val + 'px' + ' ' + 0 },
        // { padding: this.configObj.upConfig.val + 'px' + ' ' + '10px' + ' ' + this.configObj.downConfig.val + 'px' },
      ];
    },
    levelColor() {
      return [
        {
          color: this.configObj.levelColor.color[0].item,
        },
      ];
    },
    //进去条
    progressBoxBg() {
      return [
        {
          'padding-top': this.configObj.progressUpConfig.val + 'px',
          'padding-bottom': this.configObj.progressDownConfig.val + 'px',
          background: this.configObj.levelBgColor.color[0].item,
        },
      ];
    },
    //权益
    levelBoxBg() {
      return [
        {
          'padding-top': this.configObj.rightsUpConfig.val + 'px',
          'padding-bottom': this.configObj.rightsDownConfig.val + 'px',
          background: this.configObj.rightsBgColor.color[0].item,
        },
      ];
    },
    //连接线
    lineColor() {
      return {background :this.themeStyle ? this.configObj.lineColor.color[0].item : this.themeColor}
    },
    achieveColor() {
      return this.themeStyle ? this.configObj.achieveColor.color[0].item : this.themeColor;
    },
    notAchievedColor() {
      return this.configObj.notAchievedColor.isShow ? this.configObj.notAchievedColor.color[0].item : '#eee';
    },
    rightsColor() {
      return this.configObj.rightsColor.color[0].item;
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
        this.configObj.achieveColor.isShow = this.configObj.themeStyleConfig.tabVal;
        this.configObj.lineColor.isShow = this.configObj.themeStyleConfig.tabVal;
      },
      deep: true,
    }
  },
  data() {
    return {
      currentLevel: 2, // 模拟当前等级：黄金会员
      levelList: [
        { name: '青铜会员', id: 1 },
        { name: '白银会员', id: 2 },
        { name: '黄金会员', id: 3 },
        { name: '铂金会员', id: 4 },
        { name: '钻石会员', id: 5 },
      ],
      benefitList: [
        { name: '会员专享价', icon: require('../../images/rights.png') },
        { name: '充值赠送', icon: require('../../images/rights.png') },
        { name: '会员专享券', icon: require('../../images/rights.png') },
        { name: '入会有礼', icon: require('../../images/rights.png') },

      ],
      defaultConfig: {
        name: 'memberBenefits',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
          cname: '等级权益',
        },
        typeConfig: {
          title: '展示信息',
          tabTitle: '显示内容',
          info: '会员等级请在 会员设置>会员等级 菜单中进行设置',
          name: 'rowsNum',
          activeValue: [0, 1],
          list: [
            {
              val: '进度条',
            },
            {
              val: '会员权益',
            },
          ],
        },
        // 背景颜色
        levelColor: {
          tabTitle: '进度条',
          title: '等级文字',
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
        levelBgColor: {
          title: '等级背景',
          isShow: 1,
          color: [
            {
              item: '#FFF4EC',
            },
          ],
          default: [
            {
              item: '#FFF4EC',
            },
          ],
        },
        notAchievedColor: {
          isShow: 1,
          title: '未达成颜色',
          color: [
            {
              item: '#eee',
            },
          ],
          default: [
            {
              item: '#eee',
            },
          ],
        },
        //优惠券背景
        lineColor: {
          isShow: 0,
          title: '连接线颜色',
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
        achieveColor: {
          title: '达成成就色',
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
        rightsBgColor: {
          tabTitle: '权益项目',
          title: '权益背景',
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
        rightsColor: {
          title: '权益文字',
          isShow: 1,
          color: [
            {
              item: '#666666',
            },
          ],
          default: [
            {
              item: '#666666',
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
        // 上间距
        progressUpConfig: {
          tabTitle: '边距设置',
          title: '上边距',
          val: 16,
          min: 0,
          max: 100,
        },
        // 下间距
        progressDownConfig: {
          title: '下边距',
          val: 16,
          min: 0,
          max: 100,
        },
        rightsUpConfig: {
          tabTitle: '边距设置',
          title: '上边距',
          val: 12,
          min: 0,
          max: 50,
        },
        // 下间距
        rightsDownConfig: {
          title: '下边距',
          val: 12,
          min: 0,
          max: 50,
        },
        // 左右间距
        lrConfig: {
          tabTitle: '卡片设置',
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
      },
      configObj: null,
      themeStyle: 0,
      themeColor: '',
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
      }
    },
  },
};
</script>

<style scoped lang="scss">
.member-benefits {
  position: relative;
  overflow: hidden;

  .level-section {
    .level-progress {
      display: flex;
      justify-content: space-between;
      position: relative;

      .level-item {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        position: relative;

        .level-top {
          position: relative;
          width: 100%;
          display: flex;
          justify-content: center;
          align-items: center;
          height: 30px;

          .line-left,
          .line-right {
            position: absolute;
            top: 50%;
            height: 1px;
            width: 50%;
            transform: translateY(-50%);
          }

          .line-left {
            left: 0;
          }

          .line-right {
            right: 0;
          }

          .node {
            width: 6px;
            height: 6px;
            border-radius: 50%;
            position: relative;
            z-index: 2;
            display: flex;
            align-items: center;
            justify-content: center;

            .node-point {
              width: 6px;
              height: 6px;
              border-radius: 50%;
            }
          }

          .current-badge {
            position: absolute;
            top: 6px;
            left: 50%;
            transform: translateX(-50%);
            padding: 0 6px;
            line-height: 17px;
            height: 16px;
            border-radius: 99px;
            color: #fff;
            font-size: 11px;
            white-space: nowrap;
            z-index: 3;
          }
        }

        .level-name {
          font-size: 12px;
          color: #333;
        }
      }
    }
  }

  .benefit-section {
    .benefit-list {
      display: flex;
      flex-wrap: wrap;

      .benefit-item {
        width: 25%;
        display: flex;
        flex-direction: column;
        align-items: center;

        .icon-box {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 8px;

          .iconfont {
            font-size: 24px;
          }
        }

        .benefit-name {
          font-size: 12px;
          color: #333;
        }
      }
    }
  }
}
</style>
