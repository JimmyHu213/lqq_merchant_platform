<template>
  <view class="mobile-page" v-if="configObj && rechargeList.length">
    <view class="member-recharge overflow-hidden" :style="[boxStyle]">
      <!-- 头部信息 -->
      <view class="header acea-row row-between-wrapper mb20">
        <view class="left acea-row row-middle">
          <view class="f-s-30 f-w-500 mr-12" :style="[titleColor]">
            {{ configObj.titleConfig.val }}
          </view>
          <view class="f-s24" :style="[infoColor]">
            {{ configObj.infoConfig.val }}
          </view>
        </view>
        <view v-show="isLogin && merMemberInfo.isMerchantMember" @click="handleMore" class="f-s-24 flex-y-center" :style="[moreColor]">
          更多<i class="iconfont icon-ic_rightarrow f-s-24"></i>
        </view>
      </view>

      <!-- 套餐列表 -->
      <view class="recharge-list flex flex-wrap" :class="'list-' + listStyle">
        <view
            class="recharge-item flex-center flex-column relative overflow-hidden"
            v-for="(item, index) in rechargeList"
            :key="index"
            @click="clickRecharge(item, index)"
            :style="[
            itemStyle,
            { marginRight: (index + 1) % colNum === 0 ? '0' : configObj.contentConfig.val + 'px' },
            { marginBottom: configObj.contentConfig.val + 'px' },
            index === checkedIndex
              ? {
                  borderColor: themeStyle ? amountColor : themeColor,
                  backgroundColor: themeStyle ? rechargeBgColor : themeRgba,
                }
              : {},
          ]"
        >
          <view
              class="f-w-600 mb-6 recharge-price"
              :style="{
              color: index === checkedIndex ? (themeStyle ? amountColor : themeColor) : themeStyle ? amountColor : themeColor,
            }"
          >
            <span class="mr-4 symbol">充</span>
            <span class="num semiBold">{{ item.rechargeAmount }}</span>
          </view>
          <view class="f-s-26 regular" :style="{ color: giveAwayColor }">送{{ item.giftAmount }}</view>
          <!-- 选中角标 -->
          <view
              class="active-icon absolute"
              v-if="index === checkedIndex"
              :style="{ borderTopColor: themeStyle ? amountColor : themeColor }"
          >
            <i class="iconfont icon-ic_complete absolute f-s-24"></i>
          </view>
        </view>
      </view>

      <!-- 充值按钮 -->
      <view class="recharge-btn flex-center flex-column rd-50rpx mt20 w-100-p111-" @click="handleRecharge" :style="[btnColorStyle]">
        <view class="f-s-30 fw-500 mb-6">{{isLogin&&merMemberInfo.isMerchantMember ? '立即充值' : '入会并充值'}}</view>
        <view class="f-s-22" :style="[btnInfoColor]">{{ configObj.btnInfoConfig.val }}</view>
      </view>
    </view>
  </view>
</template>

<script>
import {setLinearThemeColor, setThemeColor, themeRgba} from "../../../utils/setTheme";
import { mapGetters } from "vuex";
import {toLogin} from "../../../libs/login";
import {getMemberCreditsApi} from "../../../pages/member/memberApi";
import {handleGoRecharge} from "../../../pages/member/member";
const recharge = [
  { rechargeAmount: '4500', giftAmount: '200',id: 1 },
  { rechargeAmount: '4500', giftAmount: '200',id: 2 },
  { rechargeAmount: '4500', giftAmount: '200',id: 3 },
  { rechargeAmount: '4500', giftAmount: '200',id: 4 },
];
export default {
  name: "memberRecharge",
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
  computed: {
    ...mapGetters(['isLogin']),
    themeStyle(){
      return this.configObj.themeStyleConfig.tabVal
    },
    listStyle(){
      return this.configObj.tabConfig.tabVal;
    },
    //最外层盒子的样式
    boxStyle() {
      return  { 'border-radius': this.configObj.bgStyle.val ? 2 * this.configObj.bgStyle.val + 'rpx' : '0',
        background: `linear-gradient(to right,${this.configObj.bgColor.color[0].item}, ${this.configObj.bgColor.color[1].item})`,
        margin: 2 * this.configObj.mbConfig.val + 'rpx' + ' ' + 2 * this.configObj.lrConfig.val + 'rpx' + ' ' + 0,
        padding:2 * this.configObj.upConfig.val + 'rpx' + ' ' + '20rpx' + ' ' + 2 * this.configObj.downConfig.val + 'rpx'}
    },
    //内容边距
    contentConfig() {
      return this.configObj.contentConfig.val ? 2 * this.configObj.contentConfig.val + 'rpx' : '0';
    },
    titleColor(){
      return { color: this.configObj.titleColor.color[0].item };
    },
    infoColor(){
      return { color: this.configObj.infoColor.color[0].item };
    },
    moreColor(){
      return { color: this.configObj.moreColor.color[0].item };
    },
    scopeColor() {
      return { color: this.configObj.scopeColor? this.configObj.scopeColor.color[0].item:'' };
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
      return { color: this.themeStyle ? this.configObj.btnInfoColor.color[0].item : '#fff' };
    },
    //领取按钮
    btnColorStyle() {
      return  {
        background: this.themeStyle
            ? `linear-gradient(90deg,${this.configObj.btnColor.color[0].item}, ${this.configObj.btnColor.color[1].item})`
            : setLinearThemeColor(),
        color: this.themeStyle ? this.configObj.btnBgColor.color[0].item : '#fff',
      }
    },
    // 单个卡片样式
    itemStyle() {
      const margin = this.configObj.contentConfig.val;
      const col = this.colNum;
      // 计算宽度: (100% - (列数-1) * 间距) / 列数
      // 注意：这里需要返回字符串形式的 calc
      return {
        borderRadius: 2 * this.configObj.contentStyle.val + 'rpx',
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
  data(){
    return{
      checkedIndex: 0,
      themeColor: setThemeColor(),
      themeRgba: themeRgba(),
      rechargeList: [],
      recharge: {
        id: '',
        price: ''
      }
    }
  },
  mounted(){
    this.getMemberRecharge()
  },
  methods: {
    clickRecharge(item, index){
      this.checkedIndex = index
      this.recharge = {
        id: item.id,
        price: item.rechargeAmount
      }
    },
    async getMemberRecharge(){
      if(this.configObj.rechargeConfig.ids){
        let { data } = await getMemberCreditsApi(this.configObj.rechargeConfig.ids)
        this.rechargeList  = [...data]
        let item = this.rechargeList[this.checkedIndex]
        this.recharge = {
          id: item.id,
          price: item.rechargeAmount
        }
      }else{
        this.rechargeList = []
      }
    },
    handleMore(){ //去更多
      this.$util.navigateTo(`/pages/member/recharge_amount/index?merId=${this.merId}`)
    },
    handleRecharge(){
      if(this.isLogin){
        if(this.merMemberInfo.isMerchantMember){
          this.handlerOpen()
        }else{
          this.$emit('activate-membership')
        }
      }else{
        toLogin();
      }
    },
    handlerOpen() { //充值
      handleGoRecharge(this.recharge, this.merId)
    }
  }
}
</script>

<style scoped lang="scss">
.member-recharge {
  .recharge-list {
    display: flex;
    flex-wrap: wrap;

    .recharge-item {
      background: #fff;
      border: 2rpx solid #eee;
      padding: 28rpx 0;
      box-sizing: border-box;
      .recharge-price {
        font-size: 26px;

        .symbol {
          font-size: 15px;
        }
      }
      .active-icon {
        right: 0;
        top: 0;
        width: 0;
        height: 0;
        border-top: 48rpx solid;
        border-left: 48rpx solid transparent;
        color: #fff;

        .icon-ic_complete {
          position: absolute;
          top: -44rpx;
          right: 2rpx;
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
        padding: 20rpx 0;
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
    height: 46px;
  }
}
</style>