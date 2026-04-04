<template>
  <view class="mobile-page" v-if="configObj">
    <view class="member-benefits relative overflow-hidden" :style="[boxStyle]">
      <!-- 进度条部分 -->
      <view class="level-section" v-if="progressShow && isShow" :style="[progressBoxBg]">
        <scroll-view scroll-x="true" class="level-scroll" :scroll-into-view="scrollIntoView" scroll-with-animation>
          <view class="level-progress flex justify-between relative">
            <view class="level-item flex-column flex-center relative" v-for="(item, index) in levelList" :key="index" :id="'level-' + index">
              <view class="relative w-100-p111- flex-center h-60" @click="handleLevel(item)">
                <view class="line-left" v-if="index > 0" :style="[lineColor]"></view>
                <view class="line-right" v-if="index < levelList.length - 1" :style="[lineColor]"></view>
                <view class="current-badge absolute" v-if="index+1 === currentLevel" :style="{ background: achieveColor }">当前</view>
                <view v-else :style="[activeColor(item)]" class="active flex-center relative">
                  <view
                      class="node relative z-10 flex-x-center"
                      :style="{
                  background: index+1 < currentLevel || item.id === benefitId ? [achieveColor] : [notAchievedColor],
                }"
                  >
                    <view class="node-point" v-if="index+1 < currentLevel" :style="{ background: achieveColor }"></view>
                  </view>
                </view>
              </view>
              <view class="f-s-24 text-333" :style="[levelColor]">{{ item.name }}</view>
            </view>
          </view>
        </scroll-view>
      </view>
      <!-- 会员权益部分 -->
      <view class="benefit-section" v-if="rightsShow && this.benefitList.length " :style="[levelBoxBg]">
        <view v-show="isShowBenefitList" class="f-s-30 text-333 fw-500 ml-32 mb-24">入会享特权</view>
        <view class="flex flex-wrap">
          <view class="benefit-item flex-column flex-y-center" v-for="(item, index) in benefitList" :key="index">
            <view class="icon-box flex-center mb-16">
              <!-- 使用简单的图标模拟 -->
              <image :src="currentLevel > benefitLevel || currentLevel === benefitLevel ? item.selectedIcon : item.unselectedIcon" class="w-100-p111- h-100-p111-"></image>
            </view>
            <view class="f-s-24 text-333" :style="{ color: rightsColor }">{{ item.name }}</view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import {lightColor, setThemeColor, themeRgba} from "../../../utils/setTheme";
import { mapGetters } from "vuex";
export default {
  name: "memberBenefits",
  props: {
    merMemberInfo:{ //店铺会员信息
      type: Object,
      default: () => {}
    },
    merId: { // 商户id
      type: Number,
      default: 0
    },
    configObj: {
      type: Object,
      default: () => {}
    }
  },
  mounted() {
    this.getLevelData()
  },
  computed: {
    ...mapGetters(['isLogin']),
    currentLevel: { //当前等级
      get() {
        return this.merMemberInfo?.level;
      },
      set() {
        // 空函数，什么都不做
        console.warn('Attempted to set currentLevel, but it is read-only.');
      }
    },
    isShowBenefitList(){
      return (!this.isLogin || (this.isLogin && !this.merMemberInfo.isMerchantMember)) && this.benefitList.length
    },
    isShow(){ // 是否是会员
      return this.isLogin && this.merMemberInfo.isMerchantMember
    },
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
    themeStyle() {
      return this.configObj.themeStyleConfig.tabVal
    },
    //最外层盒子的样式
    boxStyle() {
      return  { 'border-radius': this.configObj.bgStyle.val ? 2 * this.configObj.bgStyle.val + 'rpx' : '0',
        margin: 2 * this.configObj.mbConfig.val + 'rpx' + ' ' + 2 * this.configObj.lrConfig.val + 'rpx' + ' ' + 0}
    },
    achieveColor() {
      return this.themeStyle ? this.configObj.achieveColor.color[0].item : this.themeColor;
    },
    levelColor() {
      return {
        color: this.configObj.levelColor.color[0].item,
      };
    },
    //进去条
    progressBoxBg() {
      return {
        'padding-top': 2 * this.configObj.progressUpConfig.val + 'rpx',
        'padding-bottom': 2 * this.configObj.progressDownConfig.val + 'rpx',
        background: this.configObj.levelBgColor.color[0].item,
      };
    },
    //权益
    levelBoxBg() {
      return {
        'padding-top': 2 * this.configObj.rightsUpConfig.val + 'rpx',
        'padding-bottom': 2 * this.configObj.rightsDownConfig.val + 'rpx',
        background: this.configObj.rightsBgColor.color[0].item,
      };
    },
    //连接线
    lineColor() {
      return {
        background: this.themeStyle ? this.configObj.lineColor.color[0].item : this.themeColor,
      };
    },
    notAchievedColor() {
      return this.configObj.notAchievedColor.isShow ? this.configObj.notAchievedColor.color[0].item : '#eee';
    },
    rightsColor() {
      return this.configObj.rightsColor.color[0].item;
    },
  },
  data() {
    return {
      themeColor: setThemeColor(),
      themeRgba: themeRgba(),
      lightColor: lightColor(),
      levelList: [],
      benefitList: [],
      benefitId: 0,
      scrollIntoView: '',
      benefitLevel: 0
    }
  },
  methods: {
    activeColor(item){ // 点击选中的样式
      if(item.id === this.benefitId){
        return{
          border: `1px solid ${this.achieveColor}`,
          left: '12rpx',
        }
      }
    },
    async getLevelData() {
      const list = await this.$store.dispatch("getMemberLevelList", this.merId);
      this.levelList = list || [];

      if (!this.levelList.length) return;

      const isValidMember = this.isLogin && this.merMemberInfo?.isMerchantMember;
      console.log('this.merMemberInfo?.level',this.merMemberInfo?.level)
      const level = isValidMember ? (this.merMemberInfo?.level || 0) : 0;

      this.currentLevel = level;
      this.benefitLevel = level;

      const index = level > 0 ? level - 1 : 0;
      const levelData = this.levelList[index] || this.levelList[0];
      this.benefitList = levelData?.benefitsList || [];

      this.$nextTick(() => {
        this.scrollIntoView = `level-${this.currentLevel}`;
      })
    },
    handleLevel(item){
      this.benefitList = item.benefitsList
      this.benefitId = item.id
      this.benefitLevel = item.level
    }
  }
}
</script>

<style scoped lang="scss">
.active {
  width: 20rpx;
  height: 20rpx;
  border-radius: 50%;
 // box-shadow: 0 0 0 6rpx rgba(255, 59, 48, 0.2);
}
.member-benefits {

  .level-section {
    .level-scroll {
      width: 100%;
      white-space: nowrap;
    }
    .level-progress {
      display: inline-flex;
      min-width: 100%;

      .level-item {
        flex: 1;
        min-width: 140rpx; /* 确保每个项目有最小宽度，避免挤压 */
        .line-left,
        .line-right {
          position: absolute;
          top: 52%;
          height: 2rpx;
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
          width: 12rpx;
          height: 12rpx;
          border-radius: 50%;

          .node-point {
            width: 12rpx;
            height: 12rpx;
            border-radius: 50%;
          }
        }

        .current-badge {
          top: 12rpx;
          left: 50%;
          transform: translateX(-50%);
          padding: 0 12rpx;
          line-height: 34rpx;
          height: 32rpx;
          border-radius: 198rpx;
          color: #fff;
          font-size: 22rpx;
          white-space: nowrap;
          z-index: 3;
        }
      }
    }
  }

  .benefit-section {
    .benefit-item {
      width: 25%;
      margin-bottom: 24rpx;
      .icon-box {
        width: 40px;
        height: 40px;
        border-radius: 50%;
      }
    }
    .benefit-item:last-child{
      margin-bottom: 0 !important;
    }
  }
}
</style>