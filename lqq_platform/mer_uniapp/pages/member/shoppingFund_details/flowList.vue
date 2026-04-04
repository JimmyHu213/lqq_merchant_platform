<template>
<view class="borderPad">
  <view class="borRadius14">
    <!-- Tabs -->
    <view class="bg--w111-fff borders-radius-top">
      <view class="nav acea-row row-middle borders-radius-top">
        <view class="item" :class="type === item.type ? 'on' : ''" v-for="(item,index) in navList" :key="index" @click="changeType(item.type)">
          <view :class="type === item.type ? 'font-color' : ''">{{item.name}}</view>
          <view class="line" v-if="type === item.type"></view>
        </view>
      </view>
    </view>
    <!-- List -->
    <view class="list borRadius14">
      <view v-for="(item, index) in flowData" :key="index">
        <view class="time-header f-s-26 text-999 pl-24 pt-32 pb-24" :class="index===0?'bg--w111-fff':''">
          {{item.yearMonth}}
        </view>
        <view class="bg--w111-fff borderPad" :class="index===0?'borders-radius-bottom':'borRadius14'">
          <view :style="index===0 && subIndex === 0 ?'padding-top: 0 !important;':''" class="list-item flex justify-between align-center pt-30 pb-30" v-for="(subItem, subIndex) in item.recordList" :key="subIndex">
            <view>
              <view class="f-s-28 text-333 mb-12">{{subItem.title}}</view>
              <view class="f-s-24 text-999">{{subItem.createTime}}</view>
            </view>
            <view class="text-right flex-col flex-x-center">
              <view class="f-s-22 text-999 mb-12">本金 <text class="regular f-s-36 text-333"> {{subItem.type === 1 ? '+' : '-'}} {{subItem.rechargeAmount}}</text></view>
              <view class="f-s-22 text-999">赠送 <text class="regular f-s-36 text-333"> {{subItem.type === 1 ? '+' : '-'}} {{subItem.giftAmount}}</text></view>
            </view>
          </view>
<!--          <view class="text-center order-list-empty" v-if="loadOptions.loaded && flowData.length && flowData.length === 1">-->
<!--            暂无更多～-->
<!--          </view>-->
        </view>
      </view>
      <view class="text-center order-list-empty mt20" v-if="loadOptions.loaded && flowData.length && flowData.length>1">
        暂无更多～
      </view>
      <!-- 空状态提示 -->
      <view v-if="!loadOptions.loading" class="bg--w111-fff borders-radius-bottom">

        <view v-if="flowData.length === 0" class="nothing">
          <emptyPage title="暂无记录~" mTop="20%" :imgSrc="urlDomain+'crmebimage/presets/nodingdan.png'" />
        </view>
      </view>
    </view>
  </view>
</view>
</template>

<script>
import { shoppingCreditsUserRecordApi } from "../memberApi";
import emptyPage from "../../../components/emptyPage";

export default {
  name: "flowList",
  components: {
    emptyPage
  },
  props: {
    merId: {
      type: Number,
      default: () => 0
    }
  },
  data() {
    return {
      urlDomain: this.$Cache.get("imgHost"),
      type: 'all',
      navList:[
        {
          name: '全部',
          type: 'all'
        },
        {
          name: '消费',
          type: 'consume'
        },
        {
          name: '充值',
          type: 'recharge'
        },
        {
          name: '退款',
          type: 'refund'
        }
      ],
      flowData: [],
      loadOptions: {
        page: 1,
        limit: 20,
        total: 0,
        loading: false,
        loaded: false
      },
    }
  },
  mounted(options) {
    this.getFlowList();
  },
  watch: {
    type: {
      handler: function(newV, oldV) {
        if (newV) {
          this.handleResetLoadOptions()
        }
      },
      deep: true
    }
  },
  computed: {
    // 搜索条件
    queryParams() {
      const params = {
        merId: this.merId,
        searchType: this.type
      };
      return params;
    }
  },
  methods: {
    changeType(type) {
      this.type = type;
    },
    handleResetLoadOptions() {
      this.flowData = [];
      this.loadOptions.page = 1;
      this.loadOptions.total = 0;
      this.loadOptions.loaded = false;
      this.loadOptions.loading = false;
      this.getFlowList();
    },
    async getFlowList(){
      const { loading, loaded, page, limit } = this.loadOptions;
      if (loading || loaded) return;
      this.loadOptions.loading = true;
      try {
        const res = await shoppingCreditsUserRecordApi({
          ...this.queryParams,
          page,
          limit
        })
        const list = res.data.list,
        loadend = res.data.totalPage <= page;
        for (let i = 0; i < list.length; i++) {
          let time1 = list[i].yearMonth;
          let array1 = list[i].recordList;
          let isEquals = false;
          for (let j = 0; j < this.flowData.length; j++) {
            let time2 = this.flowData[j].yearMonth;
            let array2 = this.flowData[j].recordList;
            if (time1 == time2) {
              array2.push.apply(array2, array1);
              this.flowData[j].recordList = array2;
              isEquals = true;
              break;
            }
          }
          if (!isEquals) {
            this.flowData.push({
              yearMonth: time1,
              recordList: array1
            })
          }
        }
        this.$set(this, 'flowData', this.flowData);
        this.$set(this.loadOptions, 'page', this.loadOptions.page + 1);
        this.loadOptions.total = res.data.total;
        this.loadOptions.loaded = loadend
        this.loadOptions.loading = false;
      } catch (err) {
        this.$util.Tips({
          title: err,
          icon: "none"
        });
      }
      this.loadOptions.loading = false;
    }
  },
}
</script>

<style scoped lang="scss">
.borders-radius-bottom{
  border-radius: 0 0 24rpx 24rpx;
}
.borders-radius-top{
  border-radius: 24rpx 24rpx 0 0;
}
.nav {
  width: 100%;
  padding: 36rpx 24rpx 0 24rpx;
  box-sizing: border-box;

  .item {
    font-size: 30rpx;
    color: #303133;
    margin-right: 60rpx;
    position: relative;
    height: 100%;
    display: flex;
    align-items: center;

    &.on {
      color: #333;
      font-weight: bold;
      font-size: 32rpx;

      .line {
        position: absolute;
        bottom: -14rpx;
        left: 50%;
        transform: translateX(-50%);
        width: 40rpx;
        height: 4rpx;
        @include main_bg_color(theme);
        border-radius: 2rpx;
      }
    }
  }
}

.list {
  padding-bottom: 30rpx;
  .list-item {
    border-bottom: 1px solid #EEEEEE;
    &:nth-last-child(1),
    &:last-child {
      border: none;
      margin-bottom: 0 !important;
    }
  }
}
</style>