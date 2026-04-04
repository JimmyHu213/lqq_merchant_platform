<template>
	<view v-if="formsValue && formValue.length>0">
		<view v-for="(list,idx) in formValue" :key="idx" class="wrapper virtual_form bg-f boder-24">
			<!-- <view class="title">{{formsValue.name}}</view> -->
			<block v-for="(item,index) in list" :key="index">
				<view class='item acea-row row-between-wrapper'>
					<view class="name">
						<text class="item-require" v-if="item.titleShow.val">*</text>
						{{ item.titleConfig.value }}
					</view>
					<!-- radio -->
					<view v-if="item.name=='radios'" class="discount">
						<radio-group @change="radioChange($event, idx, index, item)" class="acea-row row-middle row-right">
							<label class="radio" v-for="(j,jindex) in item.wordsConfig.list" :key="jindex">
								<view class="acea-row row-middle">
									<!-- #ifndef MP -->
									<radio :value="jindex.toString()" :checked='j.show'/>
									<!-- #endif -->
									<!-- #ifdef MP -->
									<radio :value="jindex" :checked='j.show'/>
									<!-- #endif -->
									<view>{{j.val}}</view>
								</view>
							</label>
						</radio-group>
					</view>
					<!-- checkbox -->
					<view v-if="item.name=='checkboxs'" class="discount acea-row">
						<checkbox-group @change="checkboxChange($event, idx, index, item)" class="acea-row row-middle row-right">
							<label class="radio" v-for="(j,jindex) in item.wordsConfig.list" :key="jindex">
								<view class="acea-row row-middle">
									<!-- #ifndef MP -->
									<checkbox :value="jindex.toString()" :checked="j.show" style="transform:scale(0.9)" />
									<!-- #endif -->
									<!-- #ifdef MP -->
									<checkbox :value="jindex" :checked="j.show" style="transform:scale(0.9)" />
									<!-- #endif -->
									<view>{{j.val}}</view>
								</view>
							</label>
						</checkbox-group>
					</view>
					<!-- text -->
					<view v-if="item.name=='texts' && item.valConfig.tabVal == 0" class="discount">
						<input type="text" :placeholder="item.tipConfig.value" placeholder-class="placeholderc" v-model="item.value" />
					</view>
					<!-- number -->
					<view v-if="item.name=='texts' && item.valConfig.tabVal == 4" class="discount">
						<input type="number" :placeholder="item.tipConfig.value" placeholder-class="placeholderc" v-model="item.value" />
					</view>
					<!-- email -->
					<view v-if="item.name=='texts' && item.valConfig.tabVal == 3" class="discount">
						<input type="text" :placeholder="item.tipConfig.value" placeholder-class="placeholderc" v-model="item.value" />
					</view>
					<!-- data -->
					<view v-if="item.name=='dates'" class="discount">
						<picker mode="date" :value="item.value" @change="bindDateChange($event,idx,index)">
							<view class="acea-row row-between-wrapper">
								<view v-if="item.value == ''">{{item.tipConfig.value}}</view>
								<view v-else>{{item.value}}</view>
								<text class='iconfont icon-ic_rightarrow'></text>
							</view>
						</picker>
					</view>
					<!-- dateranges -->
					<view v-if="item.name=='dateranges'" class="discount">
						<uni-datetime-picker v-model="item.value" type="daterange" @maskClick="maskClick">
							{{item.value.length?item.value[0]+' - '+item.value[1]:item.tipConfig.value}}
						<text class='iconfont icon-ic_rightarrow'></text>
						</uni-datetime-picker>
					</view>
					<!-- time -->
					<view v-if="item.name=='times'" class="discount">
						<picker mode="time" :value="item.value" @change="bindTimeChange($event, idx, index)"
							:placeholder="item.tipConfig.value">
							<view class="acea-row row-between-wrapper">
								<view v-if="item.value == ''">{{item.tipConfig.value}}</view>
								<view v-else>{{item.value}}</view>
								<text class='iconfont icon-ic_rightarrow'></text>
							</view>
						</picker>
					</view>
					<!-- timeranges -->
					<view v-if="item.name=='timeranges'" class="discount acea-row row-between-wrapper" @click="getTimeranges(idx,index)">
						<view v-if="item.value">{{item.value}}</view>
						<view v-else>{{item.tipConfig.value}}</view>
						<text class='iconfont icon-ic_rightarrow'></text>
					</view>
					<!-- select -->
					<view v-if="item.name=='selects'" class="discount">
						<picker :value="item.value" :range="item.wordsConfig.list" @change="bindSelectChange($event,idx,index,item)" range-key="val">
							<view class="acea-row row-between-wrapper">
								<view v-if="item.value">{{item.value}}</view>
								<view v-else>请选择</view>
								<text class='iconfont icon-ic_rightarrow'></text>
							</view>
						</picker>
					</view>
					<!-- city -->
					<view v-if="item.name=='citys'" class="discount" @click="changeRegion(idx,index)">
						<view class="acea-row row-middle row-right">
							<view class="city" v-if="item.value == ''">{{item.tipConfig.value}}</view>
							<view class="city" v-else>{{item.value}}</view>
							<text class='iconfont icon-ic_rightarrow'></text>
						</view>
					</view>
					<!-- id -->
					<view v-if="item.name=='texts' && item.valConfig.tabVal == 2" class="discount">
						<input type="idcard" :placeholder="item.tipConfig.value" placeholder-class="placeholderc" v-model="item.value" />
					</view>
					<!-- phone -->
					<view v-if="item.name=='texts' && item.valConfig.tabVal == 1" class="discount">
						<input type="number" :placeholder="item.tipConfig.value" placeholder-class="placeholderc" v-model="item.value" />
					</view>
					<!-- img -->
					<scroll-view scroll-x="true" show-scrollbar="false" class="item-scroll" :style="'width:'+item.scrollWidth" v-if="item.name=='uploadPicture'">
						<view class='upload' :style="'width:'+item.scrollWidth">
							<view class='pictrue acea-row row-center-wrapper row-column' @tap='uploadpic(idx,index)'
								v-if="item.value.length < item.numConfig.val">
									<image class="camera-icon" :src="`${domain}/static/images/ic_camera.png`"></image>
								<view>上传图片</view>
							</view>
							<view class='pictrue' v-for="(items,indexs) in item.value" :key="indexs">
								<image :src='items' mode="aspectFill"></image>
								<view class="close acea-row row-center-wrapper" @tap='DelPic(idx,index,indexs)'>
									<view class="iconfont icon-ic_close"></view>
								</view>
							</view>
						</view>
					</scroll-view>
				</view>
			</block>
			<timeranges :isShow='isShow' :time='timeranges' @confrim="confrim" @cancel="cancels"></timeranges>
			<areaWindow ref="areaWindow" :display="display" :address='addressInfoArea' :cityShow='cityShow' @submit="OnAreaAddress" @changeClose="changeAddressClose"></areaWindow>
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
	const CACHE_CITY = {};
	import areaWindow from '@/components/areaWindow';
	import timeranges from '@/components/timeranges';
	import dayjs from "@/plugin/dayjs/dayjs.min.js";
	import { HTTP_REQUEST_URL } from '@/config/app';
	export default {
		components: {
			areaWindow,
			timeranges
		},
		props: {
			formsValue: {
				type: Object,
				default: function() {
					return {};
				}
			},
			
		},
		data() {
			return {
				formValue: [],
				isShow: false,
				display: false,
				cityShow: 2,
				addressInfoArea: [],
				timeranges: [],
				timerangesIndex: 0,
				formIndex: 0,
				newImg: [],
				domain: HTTP_REQUEST_URL,
			};
		},
		mounted(){

		},
		methods: {
			getFormList(form,count) {
				let arr = []
				let data = this.getFormData(form)
				for(var i=0; i<count; i++) {
					arr.push(JSON.parse(JSON.stringify(data)))
				}
				this.formValue = arr
			},
			getFormData(form) {
				let that = this;
				let formData = that.objToArr(form.value)
				formData.forEach((item, index, arr)=>{
					that.$set(item, 'value', "");
					CACHE_CITY[index] = ''; //清空省市区
					if(item.name == 'texts'){
						if(item.defaultValConfig.value){
							item.value = item.defaultValConfig.value
						}else{
							item.value = ''
						}
					}else if(item.name == 'radios'){
						item.value = item.wordsConfig.list[0].val
					}else if(item.name == 'uploadPicture'){
						item.value = [];
						item.scrollWidth = '106rpx'
					}else if(item.name == 'dateranges'){
						if(item.valConfig.tabVal==0){
							if(item.valConfig.tabData==0){
							 let obj = dayjs(new Date(Number(new Date().getTime()))).format('YYYY-MM-DD');
							 item.value = [obj,obj]
							}else{
								let data1 = dayjs(new Date(Number(new Date(item.valConfig.specifyDate[0]).getTime()))).format('YYYY-MM-DD');
								let data2 = dayjs(new Date(Number(new Date(item.valConfig.specifyDate[1]).getTime()))).format('YYYY-MM-DD');
								item.value = [data1,data2];
							}
						}else{
							item.value = [];  
						}
					}else{
						if(['times','dates','timeranges'].indexOf(item.name) != -1){
							if(item.valConfig.tabVal==0){ //显示默认值
								if(item.valConfig.tabData==0){
									if(item.name == 'times'){
										item.value = dayjs(new Date(Number(new Date().getTime()))).format('HH:mm');
									}else if(item.name == 'dates'){
										item.value = dayjs(new Date(Number(new Date().getTime()))).format('YYYY-MM-DD');
									}else{
										let current = dayjs(new Date(Number(new Date().getTime()))).format('HH:mm');
										item.value = current+' - '+current;
									}
								}else{
									if(item.name == 'times' || item.name == 'dates'){
										item.value = item.valConfig.specifyDate;
									}else{
										item.value = item.valConfig.specifyDate[0]+' - '+item.valConfig.specifyDate[1];
									}
								}
							}else{
								item.value = '';  
							}
						}else{
							item.value = '';
						}
					}
				})
				function sortNumber(a, b) {
					return a.timestamp - b.timestamp;
				}
				formData.sort(sortNumber);
				// that.$set(that, 'formValue', formData);	
				return formData
			},
			// 对象转数组
			objToArr(data) {
				let obj = Object.keys(data);
				let m = obj.map(key => data[key]);
				return m;
			},
			// 单选
			radioChange(e, i, index, item){
				this.formValue[i][index].value = item.wordsConfig.list[e.detail.value].val
			},
			// 多选
			checkboxChange(e, i,  index, item){
				let obj = e.detail.value;
				let val = '';
				item.wordsConfig.list.forEach((j,jindex)=>{
				  obj.forEach(x=>{
					  if(jindex == x){
					  	val = val +(val?',':'') + j.val;	  
					  }
				  })
				})
				this.formValue[i][index].value = val
			},
			bindDateChange(e, idx, index) {
				this.formValue[idx][index].value = e.target.value
				this.$forceUpdate()
			},
			bindTimeChange(e, i, index) {
				this.formValue[i][index].value = e.target.value
			},
			getTimeranges(i,index){
				this.isShow = true
				this.formIndex = i;
				this.timerangesIndex = index
			},
			bindSelectChange(e, i, index, item) {
				this.$set(this.formValue[i][index], 'value', item.wordsConfig.list[e.detail.value].val);
			},
			changeRegion(i,index){
				if(!this.formValue[i][index].value){
					this.addressInfoArea = [];
				}
				this.timerangesIndex = index;
				this.cityShow = Number(this.formValue[i][index].valConfig.tabVal) + 1;
				this.formIndex = i;
				this.display = true;
				if(CACHE_CITY[i]){
					this.addressInfoArea = CACHE_CITY[i];  
				}
			},
			/**上传文件*/
			uploadpic: function(idx,index) {
				let that = this;
				this.$util.uploadImageOne('upload/image', function(res) {
					let arr = that.formValue[idx][index]['value'];
					arr.push(res.data.path);
				  that.$set(that.formValue[idx][index], 'value', arr);
					that.$set(that.formValue[idx][index], 'scrollWidth', (arr.length+1)*118+'rpx');
				});
			},
			/** 删除图片*/
			DelPic: function(idx,index, indexs) {
				let that = this,
				pic = this.formValue[idx][index].value;
				that.formValue[idx][index].value.splice(indexs, 1);
				that.$set(that.formValue[idx][index], 'value', that.formValue[index].value);
			},
			OnAreaAddress(address){
			  let addr = '';
				addr = address.map(v=>v.name).join('/');
			  this.formValue[this.formIndex][this.timerangesIndex].value = addr;
			  CACHE_CITY[this.timerangesIndex] = address;
			},
			// 关闭地址弹窗；
			changeAddressClose: function() {
				this.display = false;
			},
			confrim(e){
			  this.isShow = false;
				this.$set(this.formValue[this.formIndex][this.timerangesIndex], 'value', e.time);
				let arrayNew = [];
				e.val.forEach(item=>{
					arrayNew.push(Number(item))
				})
				this.timeranges = arrayNew;
			},
			cancels(){
				this.isShow = false;
			},
			getFormValue() {
				return this.formValue
			}
		}
	}
</script>

<style scoped lang="scss">
	.discount {
		text-align: right;
	}
</style>
