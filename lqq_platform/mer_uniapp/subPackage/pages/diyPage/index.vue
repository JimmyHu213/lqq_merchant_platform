<template>
  <view :data-theme="theme">
    <!-- 首页组合组件 -->
    <homeComb :bgInfo="bgInfo" v-if="showHomeComb && !merId" :dataConfig="homeCombData" @changeTab="changeTab"
              :isScrolled="isScrolled" :navIndex="navIndex" :isSmallPage="isSmallPage"></homeComb>
    <!-- 店铺组合组件 -->
    <merHomeComb :isSmallPage="isSmallPage" :bgInfo="bgInfo" v-if="showHomeComb && merId" :dataConfig="homeCombData"
                 :isScrolled="isScrolled" :navIndex="navIndex" :merId="merId">
    </merHomeComb>
    <!-- 顶部搜索框 -->
    <headerSearch :isScrolled="isScrolled" :merId="merId" :isSmallPage="isSmallPage" v-if="showHeaderSerch" :dataConfig="headerSerchCombData">
    </headerSearch>
    <!-- 分类 -->
    <cateNav v-if="showCateNav" :dataConfig="cateNavData" @changeTab="changeTab"></cateNav>
    <view class="page_content skeleton">
      <view v-if="navIndex === 0">
        <view v-for="(item, index) in styleConfig" :key="index">
          <merHome v-if="item.name == 'homeStore'&&!item.isHide" :dataConfig="item" class="tui-skeleton-rect"
                   :merchantInfo="merchantAPPInfo" :merId="merId" :isHome="true"></merHome>
          <!-- 新闻简报 -->
          <news v-if="item.name == 'news'&&!item.isHide" :dataConfig="item"></news>
          <!-- 导航组 -->
          <menus v-if="item.name == 'menus'&&!item.isHide" :dataConfig="item" :merId="merId"></menus>
          <!-- 文章列表 -->
          <articleList v-if="item.name == 'homeArticle'&&!item.isHide" :dataConfig="item">
          </articleList>
          <!-- 秒杀 -->
          <seckill-data v-if="item.name == 'seckill'&&!item.isHide" :dataConfig="item" 	:merId="merId"></seckill-data>
          <!-- 小程序直播 -->
          <!-- #ifdef MP -->
          <liveBroadcast v-if="item.name == 'liveBroadcast'&&!item.isHide" :dataConfig="item">
          </liveBroadcast>
          <!-- #endif -->
          <!-- 优惠券 -->
          <coupon v-if="item.name == 'homeCoupon'&&!item.isHide" :dataConfig="item" :merId="merId"></coupon>
          <!-- 图片魔方 -->
          <pictureCube v-if="item.name == 'pictureCube'&&!item.isHide" :dataConfig="item" :merId="merId">
          </pictureCube>
          <!-- 热区 -->
          <hotSpot v-if="item.name == 'homeHotspot'&&!item.isHide" :dataConfig="item" :merId="merId"></hotSpot>
          <!-- 轮播图 -->
          <swiperBg v-if="item.name == 'swiperBg'&&!item.isHide" :dataConfig="item" :merId="merId"></swiperBg>
          <!-- 视频 -->
          <shortVideo v-if="item.name == 'video'&&!item.isHide" :dataConfig="item"></shortVideo>
          <!-- 店铺街 -->
          <merchant-list v-if="item.name == 'homeMerchant'&&!item.isHide"
                         :dataConfig="item" :regionIds="regionIds"></merchant-list>
          <!-- 拼团 -->
          <group v-if="item.name == 'group'&&!item.isHide" :dataConfig="item" :merId="merId"></group>
          <!-- 积分 -->
          <intergralShop v-if="item.name == 'intergral'&&!item.isHide" :dataConfig="item">
          </intergralShop>
          <!-- 辅助线 -->
          <guide v-if="item.name == 'guide'&&!item.isHide" :dataConfig="item"></guide>
          <!-- 富文本-->
          <rich-text-editor v-if="item.name == 'richTextEditor'&&!item.isHide" :richTextVal="item.richText.val"
                            :dataConfig="item"></rich-text-editor>
          <!-- 辅助空白-->
          <blank-page v-if="item.name == 'blankPage'&&!item.isHide" :dataConfig="item"></blank-page>
          <!-- 标题 -->
          <home-title v-if="item.name == 'titles'&&!item.isHide" :dataConfig="item" :merId="merId"></home-title>
          <!-- 逛逛 -->
          <discover v-if="item.name == 'homeDiscover'&&!item.isHide" :dataConfig="item"
                    :isLogin="isLogin">
          </discover>
          <!-- 商品列表 圈层参数有特殊处理-->
          <goodList v-if="item.name == 'goodList'&&!item.isHide" :dataConfig="item" :merId="merId"
                    :regionIds="regionIds" @detail="goDetail"></goodList>
          <!-- 选项卡商品列表-->
          <homeTab v-if="item.name == 'homeTab'&&!item.isHide" :dataConfig="item" @detail="goDetail" :merId="merId" :regionIds="regionIds">
          </homeTab>
          <member-card :merMemberInfo="merMemberInfo" v-if="item.name == 'memberCard'&&!item.isHide" @activate-membership="activateMembership" :merchantInfo="merchantAPPInfo"  :configObj="item" :merId="merId">
          </member-card>
          <member-coupon v-if="item.name == 'memberCoupon'&&!item.isHide" @activate-membership="activateMembership" :merMemberInfo="merMemberInfo" :configObj="item" :merId="merId"></member-coupon>
          <member-benefits v-if="item.name == 'memberBenefits'&&!item.isHide" :merMemberInfo="merMemberInfo" :configObj="item" :merId="merId"></member-benefits>
          <member-recharge v-if="item.name == 'memberRecharge'&&!item.isHide" @activate-membership="activateMembership" :merMemberInfo="merMemberInfo" :configObj="item" :merId="merId"></member-recharge>
          <!-- 底部导航-->
          <page-footer v-if="item.name == 'footer'&&!item.isHide" :dataConfig="item" :isSmallPage="isSmallPage"
                       :merId="merId" :fromTo="fromTo"></page-footer>
        </view>
      </view>

      <!-- 分类页-->
      <view class="productList" v-if="navIndex === 1 && sortList.length>0">
        <view class="sort acea-row" :class="sortList.length ? '' : 'no_pad'"
              :style="{ marginTop: sortMarTop + 'px' }">
          <navigator hover-class="none"
                     :url="'/pages/goods/goods_list/index?cid=' + item.id + '&title=' + item.name"
                     class="item" v-for="(item, index) in sortList" :key="index">
            <view class="pictrue">
              <image :src="item.icon" class='slide-image tui-skeleton-rect'></image>
            </view>
            <view class="text">{{ item.name }}</view>
          </navigator>
          <view class="item" @click="bindMore()" v-if="sortList.length >= 9">
            <view class="pictrues acea-row row-center-wrapper">
              <text class="iconfont icon-ic_more"></text>
            </view>
            <view class="text">更多</view>
          </view>
        </view>
      </view>
      <!-- 推荐商品，分类商品列表-->
      <recommend v-if="categoryId>0 && isLoading" :key="categoryId" ref="recommendIndex"
                 :categoryId='categoryId' :isShowTitle="isShowTitle" @getRecommendLength="getRecommendLength">
      </recommend>
      <view class='noCommodity' v-if="isNoCommodity&& navIndex > 0">
        <view class='pictrue'>
          <image :src="urlDomain+'crmebimage/presets/noShopper.png'"></image>
        </view>
        <text class="text-ccc">暂无商品</text>
      </view>
    </view>
    <!-- 底部导航距离，做兼容处理的-->
    <view class="footerBottom"></view>
    <!-- #ifdef MP -->
    <aTip :isCustom="true" :text="wxText" :borderR="5"></aTip>
    <!-- #endif -->

    <!--加入会员-->
    <uni-popup ref="membershipRef" type="bottom" borderRadius="20px 20px 0 0">
      <join-membership @close="closeMembership" @handleSuccessful="handleSuccessful" :avatar="merchantAPPInfo.avatar" :name="merchantAPPInfo.name" :merId="merId"></join-membership>
    </uni-popup>
    <!--优惠券弹窗-->
    <uni-popup ref="couponRef" type="center" borderRadius="20px 20px 0 0">
      <coupon-pop @close="closeCoupon" :couponList="couponList"></coupon-pop>
    </uni-popup>
  </view>
</template>

<script>
import { mapGetters } from "vuex";
import aTip from "./addTips";
import homeComb from "./homeComb";
import tuiSkeleton from "@/components/base/tui-skeleton";
import recommend from "@/components/base/recommend";
import pageFooter from "@/components/pageFooter";
import liveBroadcast from "./liveBroadcast";
import couponDialog from "./couponDialog";
import pictureCube from "./pictureCube";
import coupon from "./coupon";
import menus from "./menus";
import headerSearch from "./headerSearch";
import cateNav from "./cateNav";
import goodList from "./goodList";
import news from "./news";
import articleList from "./articleList";
import swiperBg from "./swiperBg";
import richTextEditor from "./richTextEditor";
import homeTab from "./homeTab";
import intergralShop from "./intergralShop";
import group from "./group";
import hotSpot from "./hotSpot";
import blankPage from "./blankPage";
import discover from "./discover";
import seckillData from "./seckill.vue";
import shortVideo from './video';
import homeTitle from './title';
import merHomeComb from "./merHomeComb";
import merHome from "@/components/merHome";
import merchantList from "./merchant";
import guide from "./guide";
import {getCategoryThird} from "@/api/api";
import {goProductDetail} from "@/libs/order";
import memberCard from "./memberCard";
import memberCoupon from "./memberCoupon";
import memberBenefits from "./memberBenefits";
import memberRecharge from "./memberRecharge";
import JoinMembership from "./joinMembership";
import CouponPop from "./couponPop";
let app = getApp();
const EVENT = {
  SmallPage: 0,
  Classify: 1,
  HomePage: 2
}
export default {
  name: "diyPage",
  computed: mapGetters(['isLogin','bottomNavigationIsCustom', 'merchantAPPInfo']),
  components: {
    CouponPop,
    merHome,
    merHomeComb,
    aTip,
    merchantList,
    homeComb,
    recommend,
    tuiSkeleton,
    seckillData,
    pageFooter,
    liveBroadcast,
    couponDialog,
    coupon,
    menus,
    pictureCube,
    news,
    goodList,
    articleList,
    swiperBg,
    headerSearch,
    cateNav,
    richTextEditor,
    shortVideo,
    homeTab,
    discover,
    blankPage,
    homeTitle,
    hotSpot,
    group,
    intergralShop,
    guide,
    memberCard,
    memberCoupon,
    memberBenefits,
    memberRecharge,
    JoinMembership,
  },
  props: {
    merMemberInfo: { // 商户会员信息
      type: Object,
      default: () => {}
    },
    componentData: {
      type: Object,
      default: () => {}
    },
    merId: { // 商户id
      type: Number,
      default: 0
    },
    // 圈层ID集合，逗号分隔
    regionIds: {
      type: String,
      default: ''
    },
    isScrolled: { //是否开始滚动
      type: Boolean,
      default: false
    },
    isSmallPage: {  //是否是微页面，true是，false不是
      type: Boolean,
      default: false
    },
    diyId: { //diyid
      type: Number,
      default: 0
    },
    fromTo: { //页面来源，为了区分店铺中底部导航使用的微页面，展示店铺配置的底部导航，
      type: String,
      default: ''
    }
  },
  watch: {
    componentData: {
      handler(val) {
        this.getDiyInfo()
      },
    }
  },
  data(){
    return {
      theme: app.globalData.theme,
      wxText: "点击添加到我的小程序，微信首页下拉即可访问商城。",
      urlDomain: this.$Cache.get("imgHost"),
      sortMarTop: 0,
      cateNavActive: 0,
      sortList: [],
      categoryId: 0,
      isLoading: false,
      isShowTitle: false, //是否显示头部标题，同时也判断是否展示分类下的商品列表
      styleConfig: [], //DIY数据
      showCateNav: false, //是否显示分类导航组件
      cateNavData: {}, //分类导航组件数据
      showHomeComb: false, //是否显示组合
      showHeaderSerch: false, //是否显示搜索框
      bgInfo: {
        colorPicker: '#f5f5f5',
        isBgColor: 1,
      },
      homeCombData: {}, //组合组件数据
      headerSerchCombData: {}, //搜索框对象
      navIndex: 0, //判断首页显示内容，1显示分类页和商品，0首页
      isNoCommodity: false,
      couponList: []
    }
  },
  mounted(){
    this.styleConfig = []
    this.getDiyInfo();
  },
  methods: {
    // 开通会员
    activateMembership(){
      this.$refs.membershipRef.open()
    },
    closeMembership(){
      this.$emit('updateMemberInfo')
      this.$refs.membershipRef.close()
    },
    closeCoupon(){
      this.$refs.couponRef.close()
    },
    // 成为会员回调
    handleSuccessful(couponList){
      if(couponList.length){
        this.couponList = couponList
        setTimeout(() => {
           this.$refs.couponRef.open();
        }, 100)
      }else{
        this.$util.Tips({
          title: '开通会员成功'
        });
      }
      this.closeMembership()
    },
    goDetail(item) {
      goProductDetail(item.id, 0, '')
    },
    // diy数据
    getDiyInfo(){
      let data = this.componentData;
      this.styleConfig = this.$util.objToArr(data.value);
      this.bgInfo.isBgColor = data.isBgColor
      this.bgInfo.colorPicker = data.colorPicker
      this.styleConfig.forEach((item) => {
        if (item.name === 'tabNav' && !item.isHide) {
          this.showCateNav = true;
          this.cateNavData = item;
        }
        if (item.name === 'homeComb' && !item.isHide) {
          this.showHomeComb = true
          this.homeCombData = item;
        }
        if (item.name === 'headerSerch' && !item.isHide) {
          this.showHeaderSerch = true
          this.headerSerchCombData = item;
        }
      });
    },
    // 分类切换点击
    changeTab(index, item) {
      //type=0微页面，1分类，2首页
      this.cateNavActive = index;
      if (item.type == EVENT.Classify) {
        this.navIndex = 1;
        if (!item.val) {
          this.sortList = [];
          this.categoryId = 0;
          this.$util.Tips({
            title: "请在平台端选择商品分类！确保加载商品分类数据。"
          });
          return;
        } else {
          this.isLoading = false; // 是否加载
          getCategoryThird(item.val).then(res => {
            this.sortList = res.data;
            // #ifdef H5
            self.sortMarTop = 10;
            // #endif
            this.isLoading = true;
          });
          this.categoryId = item.val;
          this.isShowTitle = false;
        }
      } else if (item.type == EVENT.SmallPage) {
        this.navIndex = 0;
        this.isShowTitle = true;
        this.categoryId = 0;
        if (!item.val) {
          return this.$util.Tips({
            title: "请在平台端选择微页面链接！确保加载微页面数据。"
          });
        } else {
          this.changeReset();
          this.$emit('getDiyData', item.val, true)
         // this.diyData(item.val, true);
        }
      } else {
        this.categoryId = 0;
        this.navIndex = 0;
        this.changeReset();
        this.$emit('getDiyData', this.isSmallPage ? this.diyId : item.val, false)
      }
    },
    changeReset(){
      if(this.isSmallPage){
        this.styleConfig = [];
        this.showCateNav = false;
        this.showHeaderSerch = false;
      }else{
        this.styleConfig = [];
      }
     // this.$emit('getDiyData')
    },
    bindMore() {
      uni.setStorageSync('cateNavActive', this.cateNavActive);
      uni.setStorageSync('categoryId', this.categoryId);
      uni.switchTab({
        url: `/pages/goods_cate/index`
      })
    },
    getRecommendLength(e) {
      this.isNoCommodity = e == 0 ? true : false;
    },
    getRecommend(){
      if(this.categoryId>0 && this.isLoading) this.$refs.recommendIndex.get_host_product();
    }
  }
}
</script>

<style scoped lang="scss">
.noCommodity {
  margin-top: 30%;
}
.productList {
  background-color: #F5F5F5;
  margin-top: 20rpx;

  // min-height: 70vh;
  .sort {
    width: 710rpx;
    max-height: 380rpx;
    background: rgba(255, 255, 255, 1);
    border-radius: 16rpx;
    padding: 0rpx 0rpx 20rpx 0rpx !important;
    flex-wrap: wrap;
    margin: 25rpx auto 0 auto;

    &.no_pad {
      padding: 0;
    }

    .item {
      width: 20%;
      margin-top: 20rpx;
      text-align: center;

      .pictrues {
        width: 90rpx;
        height: 90rpx;
        background: #F5F5F5;
        border-radius: 50%;
        margin: 0 auto;
      }

      .pictrue {
        width: 90rpx;
        height: 90rpx;
        background: #F5F5F5;
        border-radius: 50%;
        margin: 0 auto;
      }

      .slide-image {
        width: 90rpx;
        height: 90rpx;
        border-radius: 50%;
        overflow: hidden;
      }

      /deep/ .easy-loadimage,
      uni-image,
      .easy-loadimage {
        width: 90rpx;
        height: 90rpx;
        display: inline-block;
      }

      .text {
        color: #272727;
        font-size: 24rpx;
        margin-top: 10rpx;
        // overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
      }
    }
  }
}

.productList .list {
  padding: 0 20rpx;
}

.productList .list.on {
  background-color: #fff;
  border-top: 1px solid #f6f6f6;
}

.productList .list .item {
  width: 345rpx;
  margin-top: 20rpx;
  background-color: #fff;
  border-radius: 10rpx;

  .name {
    display: flex;
    align-items: center;

    .name_text {
      display: inline-block;
      max-width: 200rpx;
    }
  }
}

.page_content {
  overflow-x: hidden;

  // background-color: #f5f5f5;
  .swiper {
    position: relative;
    width: 100%;
    height: 246rpx;
    margin: 0 auto;
    border-radius: 10rpx;
    overflow: hidden;
    margin-bottom: 25rpx;
    /* #ifdef MP */
    margin-top: 20rpx;

    /* #endif */
    swiper,
    swiper-item,
    .slide-image,
    image {
      width: 100%;
      height: 246rpx;
      border-radius: 10rpx;
    }
  }
}
</style>