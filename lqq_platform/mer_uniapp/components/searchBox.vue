<template>
  <!-- 灰色背景，用在最顶部 -->
 <view>
   <view class='header acea-row row-center-wrapper fixed z-999'>
     <!-- #ifdef MP -->
     <view class="acea-row search-contain w-100-p111- " :style="{'margin-top':searchTop+'px'}">
       <text class='iconfont icon-ic_left' @click="toBack" v-if="toBackShow"></text>
       <view class='search-box acea-row row-between-wrapper w-100-p111-' :style="[searchBoxStyle]">
         <text class='iconfont icon-ic_search'></text>
         <input :value="searchVal" @confirm="inputSearch" type='text' confirm-type='search' name="search" placeholder='点击搜索商品' placeholder-class='placeholder' maxlength="20" />
         <text class="iconfont icon-ic_close1" @click="handleClear"></text>
       </view>
     </view>
     <!-- #endif -->
     <!-- #ifndef MP -->
     <view class='acea-row row-between-wrapper search-input'>
       <text class='iconfont icon-ic_search'></text>
       <input type='text' placeholder='点击搜索商品' @confirm="inputSearch" confirm-type='search' name="search"
              placeholder-class='placeholder' maxlength="20" :value="searchVal"/>
       <text class="iconfont icon-ic_close1" @click="handleClear"></text>
     </view>
     <!-- #endif -->
   </view>
   <view class="w-100-p111-" :style="{ 'height' : `${placeholderHeight}rpx`}"></view>
 </view>
</template>

<script>
	export default {
		props: {
			//是否展示返回按钮
			toBackShow: {
				type: Boolean,
				default: true
			},
			searchValue:{
				type: String,
				default: ''
			}
		},
		data(){
			return{
				searchVal: this.searchValue,
				searchTop:0,
				searchRight:0,
				searchHeight:0,
				statusWidth:0,
			}
		},
		watch:{
			searchValue(val){
				this.searchVal=val
			}
		},
		computed:{
      placeholderHeight(){
        // #ifdef MP
        return this.searchTop*2 + 66
        // #endif
        // #ifndef MP
        return 96
        // #endif
      },
      cssVarStyle() {
        return this.$getCssVarStyle();
      },
			searchBoxStyle(){
				return {
					height:this.searchHeight + 'px',
					flex:1,
					marginRight:this.statusWidth + this.searchRight+'px',
				}
			}
		},
		created() {
      // #ifdef MP
      this.getCalculate();
      // #endif
		},
		methods:{
      handleClear(){
         this.searchVal = ''
      },
      getCalculate(){
        const res = uni.getMenuButtonBoundingClientRect()
        this.searchTop=uni.getMenuButtonBoundingClientRect().top
        const statusRight = res.right //胶囊右边界坐标
        const jnHeight = res.height //胶囊高度
        this.statusWidth= res.width
        this.searchHeight=jnHeight
        //搜索框宽度计算
        uni.getSystemInfo({
          success:res=>{
            this.searchRight=res.windowWidth-statusRight
          }
        })
      },
			inputSearch(e){
        this.searchVal = e.detail.value
				this.$emit('searchChange',e)
			},
			toBack(){
				uni.navigateBack()
			}
		}
	}
</script>

<style scoped lang="scss">
.header {
  width: 100%;
  /* #ifndef MP */
  height: 96rpx;
  /* #endif */
  background-color: #fff;
}
  .search-input{
    width: 700rpx;
    height: 60rpx;
    background-color: #f5f5f5;
    border-radius: 50rpx;
    box-sizing: border-box;
    padding: 0 30rpx;
    .iconfont {
      font-size: 28rpx;
      color: #555;
    }
    .placeholder {
      color: #999;
    }
    input {
      font-size: 28rpx;
      height: 100%;
      width: 564rpx;
    }
  }
	.search-contain{
		padding: 0 20rpx 10rpx 0;
	}
	.search-box {
		margin-left: 16rpx;
		background-color: #f7f7f7;
		border-radius: 33rpx;
		padding: 0 35rpx;
		box-sizing: border-box;
		height: 66rpx;
	}
	.icon-ic_left{
		line-height: 66rpx;
	}
	.search-box input {
		width: 85%;
		font-size: 26rpx;
	}

	.search-box .placeholder {
		color: #999999;
	}

	.search-box .iconfont {
		color: #999999;
		font-size: 24rpx;
	}
</style>