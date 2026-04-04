<template>
	<!-- 组合组件 -->
	<view class="page_count tui-skeleton" :data-theme="theme">
		<!--logo-->
		<view class="bg-img" :style="{'background-image': bgColor}">
			<img :src="bgColor" alt="">
			<view class="maskBg" :style="[maskBgStyle]"></view>
		</view>
		<!--头部-->
		<view :class="{scrolled:isScrolled, 'my-main': true}" :style="{ height: myMainHeight+'px' }">
			<!--搜索-->
			<!-- #ifdef H5 -->
			<view class="header">
				<view class="serch-wrapper acea-row">
					<view v-if="logoConfig" class="logo skeleton-rect">
            <!-- 首页模式：显示Logo -->
            <image :src="logoUrl" mode="scaleToFill"></image>
					</view>
          <!-- 圈层管理模式：显示位置选择 -->
          <LocationDisplay v-if="locationConfig"
                           :text="currentLocationText"
                           :isScrolled="isScrolled"
													 :positioningType="positioningType"
                           @click="navigateToRegionSelect" />
					<navigator v-if="hotWords.length > 0" :url="'/pages/goods/goods_search/index?searchVal='+searchVal"
						:style="[searchBoxStyle]" :class="inputWidth" hover-class="none"
						class="input">
						<view class='swiperTxt'>
							<swiper :indicator-dots="indicatorDots" :autoplay="true" :interval="interval"
								:duration="duration" vertical="true" circular="true" @change="textChange">
								<block v-for="(item,index) in hotWords" :key='index'>
									<swiper-item catchtouchmove='catchTouchMove'>
										<view class=''>
											<view class='text'>
												<view class='newsTitle line1'><text
														class="iconfont icon-ic_search" :style="[iconColorStyle]" ></text><text>{{item.val}}</text>
												</view>
											</view>
										</view>
									</swiper-item>
								</block>
							</swiper>
						</view>
					</navigator>
					<navigator v-else :style="[searchBoxStyle]" :class="inputWidth"
						url="/pages/goods/goods_search/index" class="input" hover-class="none">
						<text class="iconfont icon-sousuo8" :style="[iconColorStyle]"></text>
						<text class="line1">{{placeWords}}</text>
					</navigator>
				</view>
			</view>
			<!-- #endif -->
			<!-- #ifdef MP || APP-PLUS-->
			<view class="mp-header">
				<view class="sys-head" :style="{ height: `${isSmallPage?0:statusBarHeight}px`}"></view>
				<!-- #ifdef MP -->
				<view class="serch-box" :style="{ 'margin-top': `${searchTop}px`,'height': `${searchHeight}px`, 'max-height': `${searchHeight}px` }">
				<!-- #endif -->
				<!-- #ifdef APP-PLUS -->
				<view class="serch-box" style="margin-top: 9px;margin-right: 2px;">
				<!-- #endif -->
					<view class="serch-wrapper acea-row">
						<view v-if="logoConfig" class="logo tui-skeleton-rect">
							<!-- 首页模式：显示Logo -->
							<image :src="logoUrl" mode="scaleToFill"></image>
						</view>
            <!-- 圈层管理模式：显示位置选择 -->
            <LocationDisplay  v-if="locationConfig"
                             :text="currentLocationText"
                             :isScrolled="isScrolled"
														 :positioningType="positioningType"
                             @click="navigateToRegionSelect" />
						<navigator v-if="hotWords.length > 0"
							:url="'/pages/goods/goods_search/index?searchVal='+searchVal" :style="[searchBoxStyle]"
							hover-class="none" class="input" :class="inputWidth">
							<view class='swiperTxt' :style="[swiperTxtStyle]">
								<swiper :indicator-dots="indicatorDots" :autoplay="true" :interval="interval"
									:duration="duration" vertical="true" circular="true" @change="textChange">
									<block v-for="(item,index) in hotWords" :key='index'>
										<swiper-item catchtouchmove='catchTouchMove'>
											<view class='acea-row row-between-wrapper text-box'>
												<view class='text'>
													<view class='newsTitle line1'><text
															class="iconfont icon-ic_search"></text><text>{{item.val}}</text>
													</view>
												</view>
											</view>
										</swiper-item>
									</block>
								</swiper>
							</view>
						</navigator>
						<navigator v-else :style="[searchBoxStyle]" :class="inputWidth"
							url="/pages/goods/goods_search/index" class="input" hover-class="none">
							<view class="swiperTxt" :style="[swiperTxtStyle]">
								<text class="iconfont icon-sousuo8"></text>
								<text class="line1">{{placeWords}}</text>
							</view>
						</navigator>
					</view>
				</view>
			</view>
			<!-- #endif -->
			<view v-if="tabShowConfig" class="navTabBox tabNav tui-skeletonpictrue acea-row" :style="'top:'+isTop">
				<view class="longTab">
					<scroll-view scroll-x="true" style="white-space: nowrap; display: flex;" scroll-with-animation
						:scroll-left="tabLeft" show-scrollbar="true">
						<view class="longItem" :data-index="index" v-for="(item,index) in tabList" :key="index"
							:id="'id'+index" @click="longClick(index,item)"
							:class="tabClick === index? 'navChecked':''">
							<view class="acea-row row-middle">
								<view class="name tui-skeleton-rect">{{item.title}}</view>
								<view class="underlineBox" v-if="index===tabClick">
									<!-- <view class="underline"></view> -->
								</view>
							</view>
						</view>

					</scroll-view>
				</view>
				<view class="category" @click="isShow=!isShow">
					<text v-if="isShow" class="iconfont icon-ic_uparrow"></text>
					<text v-if="!isShow" class="iconfont icon-ic_downarrow"></text>
				</view>
			</view>
			<view v-if="isShow" class="navChangeBox" catchtouchmove="true" :style="'top:'+isTop">
				<view class="navChange">
					<block v-for="(item,index) in tabList" :key="index">
						<view class="titleBox">
							<text :class="tabClick === index ? 'checkColor' : 'textColor' " class="title line1"
								:id="'id'+index" @click="longClick(index,item)">{{item.title}}</text>
						</view>
					</block>
				</view>
				<view class="mask" @touchmove.prevent :hidden="!isShow" @click="isShow=false"></view>
			</view>
		</view>

		<!-- banner -->
		<view class="swiperBg" :style="{ marginTop: swiperTop+'px'}">
			<view class="swiper page_swiper" v-if="navIndex === 0">
				<swiper :autoplay="true" :circular="circular" :interval="intervalBanner" :duration="duration"
					:previous-margin="swiperType==0?'30rpx':''" :next-margin="swiperType==0?'30rpx':''"
					:current="swiperCur" @change="swiperChange">
					<block v-for="(item,index) in banner" :key="index">
						<swiper-item :style="[contentStyleBanner]" :class="{ active: index == swiperCur }"
							class="scalex">
							<view @click="menusTap(item.info[1].value)"
								class='slide-navigator acea-row row-between-wrapper tui-skeleton-rect'
								:class="swiperType==0?'row-between-wrapper-1':'row-between-wrapper-2'">
								<image mode="aspectFill" :style="[contentStyleBanner]" :src="item.img"
									class="slide-image aa"></image>
							</view>
						</swiper-item>
					</block>
				</swiper>
				<view v-if="docType === 0" class="dots" :style="[dotStyle]">
					<block v-for="(item,index) in banner" :key="index">
						<view class="dot-item"
							:style="{'background-color': swiperCur === index ? (dataConfig.themeStyleConfig.tabVal?dataConfig.docColor.color[0].item:themeColor) : ''}">
						</view>
					</block>
				</view>
				<view v-if="docType === 1" class="dots" :style="[dotStyle]">
					<block v-for="(item,index) in banner" :key="index">
						<view class="dot"
							:style="{'background-color': swiperCur === index ? (dataConfig.themeStyleConfig.tabVal?dataConfig.docColor.color[0].item:themeColor)  : ''}">
						</view>
					</block>
				</view>
			</view>
		</view>

	</view>
</template>

<script>
	// +----------------------------------------------------------------------
	// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
	// +----------------------------------------------------------------------
	// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------
	let app = getApp();
	import locationManager from '@/utils/locationManager.js'
	import LocationDisplay from '@/components/locationDisplay/index.vue'
	export default {
		name: 'homeComb',
		components: {
			LocationDisplay
		},
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			},
			//判断首页显示内容，1显示分类页和商品，0首页
			navIndex: {
				type: Number,
				default: 0
			},
			//是否开始滚动
			isScrolled: {
				type: Boolean,
				default: false
			},
			//是否为微页面
			isSmallPage: {
				type: Boolean,
				default: false
			},
			//页面设置信息
			bgInfo: {
				type: Object,
				default: () => {}
			},
		},
		data() {
			return {
				myMainHeight: 0, //头部tab切换页和搜索按钮的高度和
				indicatorDots: false,
				circular: true,
				autoplay: false,
				duration: 500,
				searchH: 0,
				swiperTop: 0,
				statusBarHeight: app.globalData.statusBarHeight, //手机端头部显示时间位置的高度
				swiperCur: 0,
				showSkeleton: true,
				tabClick: 0, //导航栏被点击
				isLeft: 0, //导航栏下划线位置
				isWidth: 0, //每个导航栏占位
				mainWidth: app.globalData.mainWidth,
				theme: app.globalData.theme,
				tabLeft: 0,
				bgColor: '',
				isTop: 0,
				navHeight: 0,
				isShow: false,
				marTop: 0,
				searchVal: '',
				intervalBanner: 2500,
				themeColor:this.$options.filters.filterTheme(app.globalData.theme),
				searchTop:0,
				searchRight:0,
				searchHeight:0,
				statusWidth:0,
				currentLocationName: '', // 当前定位地址
				storageTimer: null, // 存储监听定时器
			};
		},
		computed: {
      // 搜索框长度判断
      inputWidth(){
        return this.dataConfig.searConfig.tabVal !== 0 && this.isSmallPage? 'uninput-new' : this.dataConfig.searConfig.tabVal === 0 && !this.isSmallPage?'uninput':'maxInput'
      },
			// 定位类型，0 定位，1 圈层
			positioningType() {
        return this.dataConfig.searchConfig ? this.dataConfig.searchConfig.tabVal==1 : false;
			},
			//分类是否展示，0展示，1不展示
			tabShowConfig() {
				return this.dataConfig.tabShowConfig.tabVal == 0;
			},
			//搜索提示语
			placeWords() {
				return this.dataConfig.placeWords.val;
			},
			//轮播切换时间
			interval() {
				return this.dataConfig.titleConfig.val * 1000
			},
			//指示器类型，0圆，1直，2无
			docType() {
				return this.dataConfig.docConfig.tabVal
			},
			//轮播图样式
			swiperType() {
				return this.dataConfig.swiperStyleConfig.tabVal
			},
			//搜索热词列表
			hotWords() {
        return this.dataConfig.hotWords.list.length ? this.dataConfig.hotWords.list : []
			},
			//分类选中颜色
			lineColor() {
				return {
					backgroundColor: this.dataConfig.checkColor.color[0].item
				}
			},
			maskBgStyle() {
				return {
					background: this.bgInfo.isBgColor=='1' ?
						`linear-gradient(180deg, rgba(245, 245, 245, 0) 0%, ${this.bgInfo.colorPicker} 100%)` :
						`linear-gradient(180deg, rgba(245, 245, 245, 0) 0%, #f5f5f5 100%)`,
				}
			},
			//判断logo图是否展示
			logoConfig() {
				return this.dataConfig.logoConfig.url && this.dataConfig.searConfig.tabVal === 1
			},
      //判断搜索框左侧是否展示定位
      locationConfig() {
        return this.dataConfig.searConfig.tabVal === 2
      },
			//logo图
			logoUrl() {
				if (this.isScrolled && this.dataConfig.logoFixConfig.url) {
					return this.dataConfig.logoFixConfig.url
				} else {
					return this.dataConfig.logoConfig.url
				}
			},
			//标签文字颜色
			textColor() {
				return this.dataConfig.fontColor.color[0].item;
			},
			//分类列表
			tabList() {
				//type=0微页面，1分类，2首页
				let tabList = this.dataConfig.listConfig.list;
				tabList.unshift({
					title: '首页',
					type: 2,
					val: 0
				})
				return tabList
			},
			//轮播列表
			banner() {
				return this.dataConfig.swiperConfig.list
			},
			//搜索框样式
			searchBoxStyle() {
				return {
					borderRadius: this.dataConfig.contentStyle.val ? this.dataConfig.contentStyle.val + 'px' : '0',
					backgroundColor: this.dataConfig.borderColor.color[0].item,
					color: this.dataConfig.textColor.color[0].item,
					textAlign: this.dataConfig.textPosition.list[this.dataConfig.textPosition.tabVal].style,
					// #ifdef MP
					height:this.searchHeight + 'px',
					flex:!this.isSmallPage?1:'',
					marginRight:!this.isSmallPage?(this.statusWidth + this.searchRight+'px'):'',
					// #endif
				}
			},
			swiperTxtStyle(){
				return {
					// #ifdef MP
					lineHeight:this.searchHeight + 'px',
					// #endif
				}
			},
			iconColorStyle(){
				return {
					color: this.dataConfig.textColor.color[0].item,
				}
			},
			//指示器样式
			dotStyle() {
				return {
					padding: '0 40rpx',
					justifyContent: this.dataConfig.txtStyle.tabVal === 1 ? 'center' : this.dataConfig.txtStyle
						.tabVal === 2 ? 'flex-end' : 'flex-start'
				}
			},
			//轮播图圆角
			contentStyleBanner() {
				return {
					'borderRadius': this.dataConfig.contentStyleBanner.val ? this.dataConfig.contentStyleBanner
						.val + 'px' : '0'
				}
			},
			// 当前位置显示文本
			currentLocationText() {
				if (this.currentLocationName) {
					// 直接显示地址，不做截取
					return this.currentLocationName;
				}
				return this.positioningType ? '选择圈层' : '选择位置';
			},
			
			// 是否有位置数据（用于控制LocationDisplay显示）
			hasLocationData() {
				// 检查是否有位置缓存或圈层缓存
				try {
					// 优先检查location_info（用户最新的主动定位选择）
					const locationInfo = uni.getStorageSync('location_info');
					if (locationInfo && (locationInfo.name || locationInfo.address)) {
						return true;
					}
					
					// 备选检查areas_info（圈层选择的位置）
					const areasInfo = uni.getStorageSync('areas_info');
					if (areasInfo && Array.isArray(areasInfo) && areasInfo.length > 0) {
						return true;
					}
				} catch (error) {
				}
				return false;
			},
		},
		watch: {
      banner: {
        handler(val) {
          this.bgColor = val[0].img;
        },
        immediate: true
      },
			// 监听导航列表变化，重新计算宽度
			tabList() {
				this.$nextTick(() => {
					setTimeout(() => {
						this.calculateNavItemWidth();
					}, 100);
				});
			}
		},
		created() {
      this.clearRegionIds();
      // 搜搜热词刚进入页面
      this.searchVal = this.hotWords.length ? this.hotWords[0].val : '';
			
			// 初始化并获取位置信息
			this.initLocationData();
			
			// 设置存储监听
			this.setupStorageListener();

			// #ifdef MP || APP-PLUS
			this.isTop = (this.isSmallPage ? 0 : this.statusBarHeight) + 48 + 'px' //分类的top值
			// this.tabShowConfig，true有分类,false无分类
			if (!this.tabShowConfig) {
				this.myMainHeight = (this.isSmallPage ? 0 : this.statusBarHeight) + 40 + 10; //头部tab切换页和搜索按钮的高度和，10是下边距
			} else {
				this.myMainHeight = (this.isSmallPage ? 0 : this.statusBarHeight) + 40 + 42; //头部tab切换页和搜索按钮的高度和
			}
			// #endif
			
			// #ifdef MP
			const res = uni.getMenuButtonBoundingClientRect()
			const statusHeight = res.top //胶囊距离顶部
			const statusRight = res.right //胶囊右边界坐标
			const jnHeight = res.height //胶囊高度
			this.statusWidth= res.width
			this.searchTop=statusHeight-this.statusBarHeight
			this.searchHeight=jnHeight
			//搜索框宽度计算
			uni.getSystemInfo({
				success:res=>{
					this.searchRight=res.windowWidth-statusRight
				}
			})
			// #endif
			
			// #ifdef H5
			this.isTop = 0
			this.myMainHeight = 'auto';

			// #endif

			// 先设置一个默认值，后面通过实际测量来更新
			this.isWidth = (this.mainWidth - 65) / 4;
			
			setTimeout((e) => {
				const query = uni.createSelectorQuery().in(this);
				query.select('.header').boundingClientRect(res => {
					if (res) this.marTop = res.height //头部的高度
				}).exec();
				
				// 动态计算导航栏每个item的宽度（延迟执行确保DOM渲染完成）
				setTimeout(() => {
					this.calculateNavItemWidth();
					
					// #ifdef MP
					// 小程序额外延迟，确保渲染稳定后再次计算
					setTimeout(() => {
						if (this.isWidth <= 0) {
							this.calculateNavItemWidth();
						}
					}, 500);
					// #endif
				}, 300);

				//展示与不展示分类的距离值判断
				if (!this.tabShowConfig) {
					// 不展示分类
					query.select('.swiperBg').boundingClientRect(res => {
						// #ifdef H5
						this.swiperTop = this.navHeight + this.marTop + this.statusBarHeight +
							4; //轮播图的top值
						//#endif
						// #ifndef H5
						if (this.isSmallPage) {
							this.swiperTop = this.statusBarHeight; //轮播图的top值
						} else {
							this.swiperTop = this.statusBarHeight + 48; //轮播图的top值
						}
						//#endif
					}).exec();
				} else {
					//展示分类
					query.select('.navTabBox').boundingClientRect(data => {
						this.navHeight = data.height //元素navHeight的高度
						// #ifdef H5
						this.swiperTop = this.navHeight + this.marTop + this.statusBarHeight +
							4; //轮播图的top值
						//#endif
						// #ifndef H5
						if (this.isSmallPage) {
							this.swiperTop = 85; //轮播图的top值
						} else {
							this.swiperTop = this.statusBarHeight + 85; //轮播图的top值
						}
						//#endif
					}).exec();
				}

			}, 200)
		},
		methods: {
      clearRegionIds(){
        if(this.dataConfig.searConfig.tabVal< 2) locationManager.clearHistoricalAreaCache()
      },
			// 从缓存获取位置数据 - 以location_info为准
			initLocationData() {
				try {
					// 优先使用location_info（用户最新的主动定位选择）
					const locationInfo = uni.getStorageSync('location_info');
					if (locationInfo && (locationInfo.name || locationInfo.address) && !this.positioningType) {
						this.currentLocationName = locationInfo.name || locationInfo.address;
						return;
					}
					
					// 备选：使用areas_info（圈层选择的位置）
					const areasInfo = uni.getStorageSync('areas_info');
					if (areasInfo && Array.isArray(areasInfo) && areasInfo.length > 0 && this.positioningType) {
						// 显示最新选择的圈层
						const latestArea = areasInfo[0];
						this.currentLocationName = latestArea.name || latestArea.address || '选择圈层';
						return;
					}
					
					// 默认显示
					this.currentLocationName = this.positioningType ? '选择圈层' : '选择位置';
				} catch (error) {
					this.currentLocationName = this.positioningType ? '选择圈层' : '选择位置';
				}
			},
			
			// 设置存储监听
			setupStorageListener() {
				// 使用定时器定期检查缓存变化
				this.storageTimer = setInterval(() => {
					this.checkLocationUpdate();
				}, 1000); // 每秒检查一次
			},
			
			// 检查位置更新 - 以location_info为准
			checkLocationUpdate() {
				try {
					let newLocationName = this.positioningType ? '选择圈层' : '选择位置';
					
					// 优先使用location_info（用户最新的主动定位选择）
					const locationInfo = uni.getStorageSync('location_info');
					if (locationInfo && (locationInfo.name || locationInfo.address)) {
						newLocationName = locationInfo.name || locationInfo.address;
					} else {
						// 备选：使用areas_info（圈层选择的位置）
						const areasInfo = uni.getStorageSync('areas_info');
						if (areasInfo && Array.isArray(areasInfo) && areasInfo.length > 0) {
							const latestArea = areasInfo[0];
							newLocationName = latestArea.name || latestArea.address || '选择圈层';
						}
					}
					
					// 如果地址发生变化，更新显示
					if (newLocationName !== this.currentLocationName) {
						// 只在真正有意义的变化时记录日志（排除初始化同步）
						this.currentLocationName = newLocationName;
					}
				} catch (error) {
					}
			},
			
			// 定位导航点击
			navigateToLocation() {
				uni.navigateTo({
					url: '/pages/circle/index'
				});
			},
			
			// 圈层管理模式：跳转到圈层选择页面
			navigateToRegionSelect() {
				if(this.positioningType){
					uni.navigateTo({
						url: '/pages/circle/specify'
					});
				}	else {
					this.checkLocationPermissionAndNavigate();
				}
			},

			// 检查位置权限并跳转到选择页面
			async checkLocationPermissionAndNavigate() {
				try {
					// 检查当前权限状态
					const hasPermission = await locationManager.hasPermission();
					
					if (hasPermission) {
						// 有权限，直接跳转到选择页面
							uni.navigateTo({
								url: '/pages/circle/select'
							});
						
					} else {
						// 无权限，尝试请求权限
						const granted = await locationManager.requestLocationPermission();
						
						if (granted) {
							// 权限获取成功，跳转到选择页面
							uni.navigateTo({
								url: '/pages/circle/select'
							});
						} else {
							// 权限获取失败，跳转到权限引导页面
							uni.navigateTo({
								url: '/pages/circle/index'
							});
						}
					}
				} catch (error) {
					// 出错时跳转到权限引导页面
					uni.navigateTo({
						url: '/pages/circle/index'
					});
				}
			},
			//轮播图跳转
			menusTap(url) {
				this.$util.navigateTo(url);
			},
			swiperChange(e) {
				let {
					current,
					source
				} = e.detail;
				if (source === 'autoplay' || source === 'touch') {
					this.swiperCur = e.detail.current;
					this.bgColor = this.banner[e.detail.current]['img']
				}
			},
			textChange(e) {
				let {
					current,
					source
				} = e.detail;
				if (source === 'autoplay' || source === 'touch') {
					this.searchVal = this.hotWords[e.detail.current]['val']
				}
			},
			// 导航栏点击
			longClick(index, item) {
				this.tabClick = index; //设置导航点击了哪一个
				this.$nextTick(() => {
					// 计算滚动位置，确保选中项在可视圈层中央
					// 如果isWidth还没有正确计算，先重新计算
					if (this.isWidth <= 0) {
						this.calculateNavItemWidth();
					}
					
					// 计算滚动位置：让选中项居中显示
					// 减去2是为了让选中项显示在可视圈层中央位置
					this.tabLeft = Math.max(0, (index - 2) * this.isWidth);
					
					
					this.$emit('changeTab', index, item);
				})
			},
			parentEmit(id, index) {
				this.$emit('changeTab', id, index);
			},
			
			// 动态计算导航栏每个item的宽度
			calculateNavItemWidth() {
				this.$nextTick(() => {
					const query = uni.createSelectorQuery().in(this);
					
					// 获取导航栏容器的宽度
					query.select('.longTab').boundingClientRect();
					// 获取前两个导航项的位置来计算实际间距
					query.select('#id0').boundingClientRect(); // 第一个导航项
					query.select('#id1').boundingClientRect(); // 第二个导航项
					
					query.exec((res) => {
						if (res[0] && res[1] && res[2] && this.tabList.length >= 2) {
							const containerWidth = res[0].width;
							const firstItemRect = res[1];
							const secondItemRect = res[2];
							
							
							// 通过两个item的left位置差计算实际的滚动单位
							this.isWidth = Math.abs(secondItemRect.left - firstItemRect.left);
							
							
							// 小程序额外验证：如果计算结果过小或过大，使用备用方案
							// #ifdef MP
							if (this.isWidth < 50 || this.isWidth > 200) {
								const itemWidth = firstItemRect.width;
								const marginRightPx = 42 * (uni.getSystemInfoSync().windowWidth / 750);
								this.isWidth = itemWidth + marginRightPx;
							}
							// #endif
							
							// 如果当前有选中项，重新计算滚动位置
							if (this.tabClick > 0) {
								this.tabLeft = Math.max(0, (this.tabClick - 2) * this.isWidth);
							}
						} else {
							// 回退方案：使用第一个item的宽度 + 边距
							if (res[1]) {
								const itemWidth = res[1].width;
								const marginRightPx = 42 * (uni.getSystemInfoSync().windowWidth / 750);
								this.isWidth = itemWidth + marginRightPx;
							} else {
								// 针对小程序做特殊处理
								// #ifdef MP
								// 小程序中考虑胶囊占用的空间
								const availableWidth = this.mainWidth - this.statusWidth - 100; // 预留更多空间
								this.isWidth = availableWidth / 4;
								// #endif
								
								// #ifndef MP
								this.isWidth = (this.mainWidth - 65) / 4;
								// #endif
							}
						}
					});
				});
			}
		},
		beforeDestroy() {
			// 清理定时器
			if (this.storageTimer) {
				clearInterval(this.storageTimer);
				this.storageTimer = null;
			}
		},
		onShow() {
			// 页面显示时不自动触发位置检查，避免覆盖用户选择
		},
		onHide() {
			// 页面隐藏时暂停检查
			if (this.storageTimer) {
				clearInterval(this.storageTimer);
				this.storageTimer = null;
			}
		},
		onUnload() {
			// 页面卸载时清理定时器
			if (this.storageTimer) {
				clearInterval(this.storageTimer);
				this.storageTimer = null;
			}
		}
	}
</script>

<style lang="scss" scoped>
	.uninput {
		/* #ifdef MP */
		width: 75% !important;
		/* #endif */
		/* #ifndef MP */
		width: 100% !important;
		/* #endif */
	}

	.icon-sousuo8 {
		font-size: 15px;

	}

	.bgwhite {
		background-color: #fff !important;
	}

	.mask {
		z-index: 999;
		top: 260rpx;
	}

	.navChangeBox {
		position: relative;
	}

	.navChange {
		background-color: #fff;
		position: absolute;
		z-index: 999999;
		width: 100%;
		border-radius: 0px 0px 16rpx 16rpx;
		padding: 24rpx 20rpx;
		display: grid;
		grid-template-rows: auto;
		grid-template-columns: repeat(5, 1fr);
		grid-column-gap: 16rpx;
		grid-row-gap: 16rpx;

		.nobg {
			background-color: #fff !important;
		}

		.titleBox {
			height: 58rpx;
			background: #F2F2F2;
			border-radius: 8rpx;
			text-align: center;
		}

		.title {
			margin: 0 auto;
			display: inline-block;
			width: 118rpx;
			height: 58rpx;
			line-height: 58rpx;
			text-align: center;

			opacity: 1;
			color: #333333;
			font-size: 24rpx;
		}

		.titleBox:nth-child(5n) {
			margin-right: 0;
		}

		.titleBox:last-child {
			margin-bottom: 0;
		}

		.changed {
			border-radius: 8rpx;
			@include cate-two-btn(theme);
			@include coupons_border_color(theme);

			.title {
				@include main_color(theme);
			}
		}
	}

	.row-middle {
		flex-direction: column;
	}

	.navTabBox {
		width: 100%;
		height: 66rpx;
		color: rgba(255, 255, 255, 1);
		position: relative;
		padding: 0 24rpx 0 24rpx;
		display: flex;
		justify-content: space-between;
		z-index: 9;

		&.isFixed {
			z-index: 10;
			position: fixed;
			left: 0;
			width: 100%;
			/* #ifdef H5 */
			top: 0;
			/* #endif */
		}

		.click {
			color: white;
		}

		.longTab {
			width: 94%;

			.longItem {
				//height: 72rpx;
				display: inline-block;
				// line-height: 52rpx;
				text-align: center;
				font-size: 28rpx;
				color: #fff;
				white-space: nowrap;
				text-overflow: ellipsis;
				margin-right: 42rpx;

				&.click {
					font-weight: bold;
					font-size: 30rpx;
					color: #fff;
					font-weight: bold;
				}

				.name {
					height: 48rpx;
				}
			}

			.underlineBox {
				margin-top: 8rpx;
				height: 3px;
				transition: .5s;

				.underline {
					width: 33rpx;
					height: 4rpx;
				}
			}
		}

		.category {
			height: 66rpx;
			line-height: 46rpx;
			z-index: 3;

			// padding: 0 15rpx 0 25rpx;
			.iconfont {
				font-size: 24rpx;
			}
		}
	}


	.swiperBg {
		z-index: 1;
		margin-top: 10rpx;

		.colorBg {
			position: absolute;
			left: 0;
			top: 0;
			height: 130rpx;
			width: 100%;
		}

		.page_swiper {
			position: relative;
			width: 100%;
			height: auto;
			margin: 0 auto;
			border-radius: 10rpx;
			overflow: hidden;
			z-index: 8;
			padding: 0 0rpx 20rpx 0rpx;

			image {
				width: 100%;
				height: 310rpx;
				margin: 0 auto;
				border-radius: 10rpx;
			}

			.acea-row.row-between-wrapper {
				height: 310rpx;
				margin: 0 auto;
				border-radius: 10rpx;
			}

			.acea-row.row-between-wrapper-1 {
				width: 100%;
			}

			.acea-row.row-between-wrapper-2 {
				width: 97%;
			}

			swiper {
				width: 100%;
				display: block;
				height: 310rpx;

				&.scalex {
					/deep/.uni-swiper-slide-frame {
						transform: translate(0, 0) !important;
					}
				}
			}

			image {
				transform: scale(0.96);
				transition: all 0.6s ease;
			}

			/deep/ swiper-item.active {
				image {
					transform: scale(1);
				}
			}

			/*用来包裹所有的小圆点  */
			.dots {
				display: flex;
				flex-direction: row;
				position: absolute;
				bottom: 40rpx;
				align-items: center;
				width: 100%;
			}

			.dot-item {
				width: 10rpx;
				height: 10rpx;
				background-color: rgba(255, 255, 255, .4);
				border-radius: 50%;
				margin: 0 6rpx;
			}

			/*未选中时的小圆点样式 */
			.dot {
				width: 16rpx;
				height: 6rpx;
				border-radius: 6rpx;
				margin-right: 6rpx;
				background-color: rgba(255, 255, 255, .4);
			}
		}
	}

	.scrolled {
		z-index: 5000;
		/* #ifndef H5 */
		position: fixed;
		/* #endif */
		/* #ifdef H5 */
		position: relative;
		/* #endif */
		left: 0;
		top: 0;
		width: 100%;
		background-color: #fff !important;
		color: #000 !important;
		transition: background-color .5s ease;

		/* #ifdef H5 */
		.header {
			background-color: #fff !important;
			
			.serch-wrapper {
				.logo {
					// 确保位置显示在滚动时保持正确样式
				}
				
				.input {
					background-color: #f5f5f5 !important;
					color: #000 !important;
				}
			}
		}
		/* #endif */

		.longItem,
		.click,
		.category text {
			color: #000 !important;
		}

		.btn .iconfont {
			color: #333 !important;
		}

		.iconnum {
			background: #333 !important;
		}

		.underline {
			background: #000 !important;
		}

		.click {
			&::after {
				background-color: #fff !important;
			}
		}

		.input,
		.uninput,
		.maxInput {
			background-color: #f5f5f5 !important;
		}
	}

	.page_count {
		position: relative;
		overflow: hidden;
		background-repeat: no-repeat;


		.bg-img {
			position: absolute;
			width: 100%;
			height: 100%;
			top: 0;
			/* #ifdef MP || APP-PLUS */
			z-index: -1;
			/* #endif */
			/* #ifdef H5 */
			z-index: 0;
			/* #endif */
			z-index: 0;
			filter: blur(0);
			overflow: hidden;

			img {
				width: 100%;
				height: 100%;
				filter: blur(30rpx);
				transform: scale(1.5);
			}

			.maskBg {
				position: absolute;
				bottom: 0;
				width: 100%;
				height: 136px;
				background: linear-gradient(180deg, rgba(245, 245, 245, 0) 0%, #f5f5f5 100%);
			}
		}
	}

	.my-main {
		left: 0;
		position: fixed;
		top: 0;
		width: 100%;
		z-index: 30;
		transition: background-color .5s ease;
	}

	.page_count {

		/* #ifdef H5 */
		.header {
			width: 100%;
			padding: 24rpx;
			position: relative;
			z-index: 999;

			.serch-wrapper {
				align-items: center;
				height: 55rpx;

				.logo {
					width: 118rpx;
					margin-right: 24rpx;
					flex-shrink: 0;
					height: 42rpx;
					display: flex;
					align-items: center;
				}

				image {
					width: 118rpx;
					height: 42rpx;
				}

				.input {
					flex: 1;
					height: 55rpx;
					line-height: 55rpx;
					padding-left: 30rpx;
					font-size: 26rpx;
					padding-right: 4rpx;
					box-sizing: border-box;
					display: flex;
					align-items: center;

					.iconfont {
						margin-right: 4rpx;
						color: #666666;
					}
				}
				
				// 修复不同宽度模式的布局
				.input:not(.maxInput) {
					width: 540rpx;
					flex: none;
				}
			}

			.tabNav {
				padding-top: 24rpx;
			}
		}
		/* #endif */

		/* #ifdef MP || APP-PLUS */
		.mp-header {
			z-index: 999;
			position: fixed;
			left: 0;
			top: 0;
			width: 100%;
			/* #ifdef H5 */
			padding-bottom: 20rpx;
			/* #endif */
		}
		
		.serch-box {
			/* #ifdef MP */
			box-sizing: border-box;
			/* #endif */

			.serch-wrapper {
				height: 100%;
				align-items: center;
				padding: 0 24rpx 0 24rpx;

				image {
					width: 118rpx;
					height: 42rpx;
					margin-right: 30rpx;
				}

				.input {
					display: flex;
					align-items: center;
					/* #ifndef MP */
					width: 540rpx;
					/* #endif */
					/* #ifdef APP-PLUS */
					flex: 1;
					/* #endif */
					height: 50rpx;
					padding-left: 20rpx;
					font-size: 28rpx;
					box-sizing: border-box;

					.iconfont {
						margin-right: 20rpx;
					}
				}
			}
		}

		/* #endif */
		.swiperTxt {
			width: 100%;
			height: 100%;
			overflow: hidden;
		}

		.swiperTxt .text {
			width: 100%;
			/* #ifdef APP */
			margin-bottom: 4rpx;
			/* #endif */

		}

		.swiperTxt .text .label {
			font-size: 20rpx;
			color: #ff4c48;
			width: 64rpx;
			height: 30rpx;
			border-radius: 40rpx;
			text-align: center;
			line-height: 28rpx;
			border: 2rpx solid #ff4947;
		}

		.swiperTxt .text .newsTitle {
			font-size: 24rpx;
		}

		.swiperTxt swiper {
			height: 100%;
		}
	}

	.navChecked {
		font-size: 32rpx !important;
		font-weight: 500;
	}

	.checkColor {
		@include main_color(theme);
	}
	.maxInput{
		width: 100% !important;
	}
	.uninput-new{
		width: 75% !important;
	}
	.text-box{
		height: 100%;
	}
</style>