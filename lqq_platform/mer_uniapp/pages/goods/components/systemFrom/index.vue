<template>
	<!--商品关联系统表单-->
	<view v-if="value.length>0">
		<view class='bg--w111-fff borRadius14 mt20 virtual_form'
			:class="{on:(list.name=='radios' || list.name=='checkboxs'),on2:list.name == 'dateranges',on3:list.name == 'citys',pd0:list.name == 'uploadPicture'}"
			v-for="(list,indexs) in orderNewForm" :key="indexs">
			<view v-for="(item,index) in list" :key="index">
        <!-- 富文本-->
        <view v-if="item.name=='richTextEditor'">
          <text-editor :richTextVal="item.richText.val"></text-editor>
        </view>
				<view class='item acea-row row-between-wrapper w-100-p111-'>
					<view v-if="item.name!=='richTextEditor'" class="name">
						<text class="item-require" v-if="item.titleShow && item.titleShow.val">*</text>
						{{ item.titleConfig.val }}
					</view>
					<!-- radio -->
					<view v-if="item.name=='radios'" class="discount">
						<radio-group @change="radioChange($event, indexs, index, item)"
							class="acea-row row-middle row-right">
							<label class="radio" v-for="(j,jindex) in item.wordsConfig.list" :key="jindex">
								<view class="acea-row row-middle">
									<!-- #ifndef MP -->
									<radio :value="jindex.toString()" :checked='j.show' />
									<!-- #endif -->
									<!-- #ifdef MP -->
									<radio :value="jindex" :checked='j.show' />
									<!-- #endif -->
									<view>{{j.val}}</view>
								</view>
							</label>
						</radio-group>
					</view>
					<!-- checkbox -->
					<view v-if="item.name=='checkboxs'" class="discount acea-row">
						<checkbox-group @change="checkboxChange($event, indexs, index, item)"
							class="acea-row row-middle row-right">
							<label class="radio" v-for="(j,jindex) in item.wordsConfig.list" :key="jindex">
								<view class="acea-row row-middle">
									<checkbox :value="jindex.toString()" :checked="j.show"
										style="transform:scale(0.9)" />
									<view class="ml-8">{{j.val}}</view>
								</view>
							</label>
						</checkbox-group>
					</view>
					<!-- text -->
					<view v-if="item.name=='texts' && item.valConfig.tabVal == 0" class="discount">
						<input type="text" :placeholder="item.tipConfig.val" class="text-28rpx"
							placeholder-class="placeholder" v-model="item.value" />
					</view>
					<!-- number -->
					<view v-if="item.name=='texts' && item.valConfig.tabVal == 4" class="discount">
						<input type="number" :placeholder="item.tipConfig.val" class="text-28rpx"
							placeholder-class="placeholder" v-model="item.value" />
					</view>
					<!-- email -->
					<view v-if="item.name=='texts' && item.valConfig.tabVal == 3" class="discount">
						<input type="text" :placeholder="item.tipConfig.val" class="text-28rpx"
							placeholder-class="placeholder" v-model="item.value" />
					</view>
					<!-- data -->
					<view v-if="item.name=='dates'" class="discount">
						<picker mode="date" :value="item.value" @change="bindDateChange($event,indexs,index)">
							<view class="acea-row row-between-wrapper text-28rpx">
								<view v-if="item.value == ''" class="text--w111-ccc">{{item.tipConfig.val}}</view>
								<view v-else>{{item.value}}</view>
								<text class='iconfont icon-ic_rightarrow'></text>
							</view>
						</picker>
					</view>
					<!-- dateranges -->
					<view v-if="item.name=='dateranges'" class="discount">
						<uni-datetime-picker v-model="item.value" type="daterange" class="text-28rpx flex"
							@maskClick="maskClick">
							<view v-if="item.value == ''" class="text--w111-ccc">请选择</view>
							<view v-else>{{item.value.length?item.value[0]+' - '+item.value[1]:item.tipConfig.val}}
							</view>
							<text class='iconfont icon-ic_rightarrow'></text>
						</uni-datetime-picker>
					</view>
					<!-- time -->
					<view v-if="item.name=='times'" class="discount">
						<picker mode="time" :value="item.value" @change="bindTimeChange($event, indexs, index)"
							:placeholder="item.tipConfig.value">
							<view class="acea-row row-between-wrapper text-28rpx">
								<view v-if="item.value == ''" class="text--w111-ccc">{{item.tipConfig.val}}</view>
								<view v-else>{{item.value}}</view>
								<text class='iconfont icon-ic_rightarrow'></text>
							</view>
						</picker>
					</view>
					<!-- timeranges -->
					<view v-if="item.name=='timeranges'" class="discount acea-row row-between-wrapper"
						@click="getTimeranges(indexs, index)">
						<view v-if="item.value" class="text-28rpx">{{item.value}}</view>
						<view v-else class="text--w111-ccc text-28rpx">{{item.tipConfig.val}}</view>
						<text class='iconfont icon-ic_rightarrow'></text>
					</view>
					<!-- select -->
					<view v-if="item.name=='selects'" class="discount">
						<picker :value="item.value" :range="item.wordsConfig.list"
							@change="bindSelectChange($event, indexs, index,item)" range-key="val">
							<view class="acea-row row-between-wrapper">
								<view v-if="item.value" class="text-28rpx">{{item.value}}</view>
								<view v-else class="text--w111-ccc text-28rpx">请选择</view>
								<text class='iconfont icon-ic_rightarrow'></text>
							</view>
						</picker>
					</view>
					<!-- city -->
					<view v-if="item.name=='citys'" class="discount acea-row" @click="changeRegion(indexs, index)">
						<view class="acea-row row-middle row-right">
							<view class="city text--w111-ccc" v-if="item.value == ''">{{item.tipConfig.val}}</view>
							<view class="city text-28rpx" v-else>{{item.value}}</view>
							<text class='iconfont icon-ic_rightarrow'></text>
						</view>
					</view>
					<!-- id -->
					<view v-if="item.name=='texts' && item.valConfig.tabVal == 2" class="discount">
						<input type="idcard" :placeholder="item.tipConfig.val" class="text-28rpx"
							placeholder-class="placeholder" v-model="item.value" />
					</view>
					<!-- phone -->
					<view v-if="item.name=='texts' && item.valConfig.tabVal == 1" class="discount">
						<input type="number" :placeholder="item.tipConfig.val" class="text-28rpx"
							placeholder-class="placeholder" v-model="item.value" />
					</view>
					<!-- img -->
					<view v-if="item.name=='uploadPicture'" class="confirmImg" style="padding-bottom: 0;">
						<view class='upload acea-row row-middle justify-end'>
							<view class='pictrue' v-for="(items,idx) in item.value" :key="idx">
								<image :src='items' mode="aspectFill"></image>
								<view class="close acea-row row-center-wrapper" @tap='DelPic(indexs,index,idx)'>
									<view class="iconfont icon-ic_close1"></view>
								</view>
							</view>
							<view class='pictrue acea-row row-center-wrapper row-column' @tap='uploadpic(indexs,index)'
								style="margin-right: 0" v-if="item.value.length < item.numConfig.val">
								<view>上传图片</view>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 时间选择 -->
		<timerangesFrom :isShow='isShow' :time='timeranges' @confrim="confrim" @cancel="cancels"></timerangesFrom>

		<areaWindow ref="areaWindow" :display="display" :address='addressInfoArea' :cityShow='cityShow'
			@submit="OnChangeAddress" @changeClose="changeAddressClose"></areaWindow>
	</view>
</template>

<script>
	import areaWindow from '../areaWindow';
	import timerangesFrom from '../timeranges';
	// import uniDatetimePicker from '../uni-datetime-picker/components/uni-datetime-picker/uni-datetime-picker.vue';
	import dayjs from "@/plugin/dayjs/dayjs.min.js";
  import textEditor from "../../../../components/textEditor";
	const CACHE_CITY = {};
	export default {
		props: {
			value: {
				type: Array,
				default: function() {
					return []
				}
			},
		},
		components: {
			areaWindow,
			timerangesFrom,
      textEditor
			// uniDatetimePicker
		},
		data() {
			return {
				orderNewForm: [],
				isShow: false,
				timerangesIndex: 0,
				display: false,
				addressInfoArea: [],
				cityShow: 2,
				timeranges: [],
				orderExtend: {}, //提交接口表单的数据
			}
		},
		watch: {
			orderNewForm: {
				handler(newVal) {
					this.$emit('input', newVal);
				},
				deep: true,
			},
		},
		mounted() {
			this.getFromData()
		},
		methods: {
			getFormList(count) {
				let arr = []
				let data = this.getFromData()
				for (var i = 0; i < count; i++) {
					arr.push(JSON.parse(JSON.stringify(data)))
				}
				this.$set(this, 'orderNewForm', arr);
				//  this.orderNewForm = arr
			},
			getFromData(form) {
				let formData = [...this.value];
				formData.forEach((item, index, arr) => {
					this.$set(item, 'value', "");
					CACHE_CITY[index] = ''; //清空省市区
					if (item.name == 'texts') {
						if (item.defaultValConfig.val) {
							item.value = item.defaultValConfig.val
						} else {
							item.value = ''
						}
					}else if (item.name == 'richTextEditor') {
            item.value = item.richText.val
          } else if (item.name == 'radios') {
						item.value = item.wordsConfig.list[0].val
					} else if (item.name == 'uploadPicture') {
						item.value = [];
					} else if (item.name == 'dateranges') {
						if (item.valConfig.tabVal == 0) {
							if (item.valConfig.tabData == 0) {
								let obj = dayjs(new Date(Number(new Date().getTime()))).format('YYYY-MM-DD');
								item.value = [obj, obj]
							} else {
								let data1 = dayjs(new Date(Number(new Date(item.valConfig.specifyDate[0])
									.getTime()))).format('YYYY-MM-DD');
								let data2 = dayjs(new Date(Number(new Date(item.valConfig.specifyDate[1])
									.getTime()))).format('YYYY-MM-DD');
								item.value = [data1, data2];
							}
						} else {
							item.value = [];
						}
					} else {
						if (['times', 'dates', 'timeranges'].indexOf(item.name) != -1) {
							if (item.valConfig && item.valConfig.tabVal == 0) { //显示默认值
								if (item.valConfig.tabData == 0) {
									if (item.name == 'times') {
										item.value = dayjs(new Date(Number(new Date().getTime()))).format('HH:mm');
									} else if (item.name == 'dates') {
										item.value = dayjs(new Date(Number(new Date().getTime()))).format(
											'YYYY-MM-DD');
									} else {
										let current = dayjs(new Date(Number(new Date().getTime()))).format(
											'HH:mm');
										item.value = current + ' - ' + current;
									}
								} else {
									if (item.name == 'times' || item.name == 'dates') {
										item.value = item.valConfig.specifyDate;
									} else {
										item.value = item.valConfig.specifyDate[0] + ' - ' + item.valConfig
											.specifyDate[1];
									}
								}
							} else {
								item.value = '';
							}
						} else {
							item.value = '';
						}
					}
				})

				function sortNumber(a, b) {
					return a.timestamp - b.timestamp;
				}
				formData.sort(sortNumber);
				return formData
				//this.$set(this, 'orderNewForm', formData);
			},
			getSubmitFromData(num) {
				let systemFormValue = Array.from(this.orderNewForm)
				let systemFormData = [] //处理后的系统表单值
				for (var i = 0; i < systemFormValue.length; i++) {
					let reservation = []
					for (var j = 0; j < systemFormValue[i].length; j++) {
						let curdata = systemFormValue[i][j]
						if (['radios'].indexOf(curdata.name) == -1 && (curdata.titleShow && curdata.titleShow.val || (['uploadPicture',
								'dateranges'
							].indexOf(curdata.name) == -1 && curdata.value && curdata.value.trim()))) {
							if ((curdata.name === 'texts' && curdata.valConfig.tabVal == 0) || ['dates', 'times',
									'selects', 'citys', 'checkboxs','richTextEditor'
								].indexOf(curdata.name) != -1) {
								if (!curdata.value || (curdata.value && !curdata.value.trim())) {
									return this.$util.Tips({
										title: `请填写第${i+1}个表单的${curdata.titleConfig.val}`
									});
								}
							}
							if (curdata.name === 'timeranges') {
								if (!curdata.value) {
									return this.$util.Tips({
										title: `请选择${i+1}个表单的${curdata.titleConfig.val}`
									});
								}
							}
							if (curdata.name === 'dateranges') {
								if (!curdata.value.length) {
									return this.$util.Tips({
										title: `请选择${i+1}个表单的${curdata.titleConfig.val}`
									});
								}
							}
							if (curdata.name === 'texts' && curdata.valConfig.tabVal == 4) {
								if (curdata.value <= 0) {
									return this.$util.Tips({
										title: `第${i+1}个表单请填写大于0的${curdata.titleConfig.val}`
									});
								}
							}
							if (curdata.name === 'texts' && curdata.valConfig.tabVal == 3) {
								if (!/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(curdata.value)) {
									return this.$util.Tips({
										title: `第${i+1}个表单请填写正确的${curdata.titleConfig.val}`
									});
								}
							}
							if (curdata.name === 'texts' && curdata.valConfig.tabVal == 1) {
								if (!/^1(3|4|5|7|8|9|6)\d{9}$/i.test(curdata.value)) {
									return this.$util.Tips({
										title: `第${i+1}个表单请填写正确的${curdata.titleConfig.val}`
									});
								}
							}
							if (curdata.name === 'texts' && curdata.valConfig.tabVal == 2) {
								if (!
									/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/i
									.test(curdata.value)) {
									return this.$util.Tips({
										title: `第${i+1}个表单请填写正确的${curdata.titleConfig.val}`
									});
								}
							}
							if (curdata.name === 'uploadPicture') {
								if (!curdata.value.length) {
									return this.$util.Tips({
										title: `请上传第${i+1}个表单的${curdata.titleConfig.val}`
									});
								}
							}
						}
						this.orderExtend[curdata.key] = curdata.value
						reservation.push({
							title: curdata.titleConfig.val,
							value: curdata.value,
						})
					}

					if (num !== 1) {
						systemFormData.push(reservation)
					} else {
						systemFormData = reservation
					}

				}
				return systemFormData
			},
			// 单选
			radioChange(e, i, index, item) {
				this.orderNewForm[i][index].value = item.wordsConfig.list[e.detail.value].val
			},

			// 多选
			checkboxChange(e, i, index, item) {
				let obj = e.detail.value;
				let val = '';
				item.wordsConfig.list.forEach((j, jindex) => {
					obj.forEach(x => {
						if (jindex == x) {
							val = val + (val ? ',' : '') + j.val;
						}
					})
				})
				this.orderNewForm[i][index].value = val
			},

			//日期
			bindDateChange(e, i, index) {
				this.orderNewForm[i][index].value = e.target.value
				this.$forceUpdate()
			},

			//时间
			bindTimeChange(e, i, index) {
				this.orderNewForm[i][index].value = e.target.value
			},

			//时间选择
			getTimeranges(i, index) {
				this.isShow = true
				this.formIndex = i;
				this.timerangesIndex = index
			},

			//时间选择回调
			confrim(e) {
				this.isShow = false;
				this.$set(this.orderNewForm[this.formIndex][this.timerangesIndex], 'value', e.time);
				let arrayNew = [];
				e.val.forEach(item => {
					arrayNew.push(Number(item))
				})
				this.timeranges = arrayNew;
			},

			//关闭时间选择弹窗
			cancels() {
				this.isShow = false;
			},

			//sel选择
			bindSelectChange(e, i, index, item) {
				this.$set(this.orderNewForm[i][index], 'value', item.wordsConfig.list[e.detail.value].val);
			},

			//城市地址选择
			changeRegion(i, index) {
				if (!this.orderNewForm[i][index].value) {
					this.addressInfoArea = [];
				}
				this.timerangesIndex = index;
				this.formIndex = i;
				this.cityShow = Number(this.orderNewForm[i][index].valConfig.tabVal) + 1;
				this.display = true;
				if (CACHE_CITY[i]) {
					this.addressInfoArea = CACHE_CITY[i];
				}
			},

			//选择地址回调
			OnChangeAddress(address) {
				// let addr = '';
				let addr = address.map(v => v.regionName).join('/');
				this.orderNewForm[this.formIndex][this.timerangesIndex].value = addr;
				CACHE_CITY[this.timerangesIndex] = address;
			},

			// 关闭地址弹窗；
			changeAddressClose() {
				this.display = false;
			},

			/**上传文件*/
			uploadpic(idx, index) {
				let that = this;
				that.$util.uploadImageOne({
					url: 'upload/image',
					name: 'multipart',
					model: "order",
					pid: 0
				}, function(res) {
					let arr = that.orderNewForm[idx][index]['value'];
					//let pics = item.value || []
					arr.push(res);
					that.$set(that.orderNewForm[idx][index], 'value', arr);
					that.$set(that.orderNewForm[idx][index], 'scrollWidth', (arr.length + 1) * 118 + 'rpx');
					//	that.$set(item, 'value', pics);
				});
			},

			//删除图片
			DelPic(idx, index, indexs) {
				let pic = this.orderNewForm[idx][index].value;
				this.orderNewForm[idx][index].value.splice(indexs, 1);
				this.$set(this.orderNewForm[idx][index], 'value', this.orderNewForm[index].value);
			},
		}
	}
</script>

<style scoped lang="scss">
	/deep/.uni-date-editor {}

	.item {
		font-size: 28rpx;
		color: #282828;
	}

	.name {
		width: 168rpx;
		padding: 20rpx 0;
	}

	.virtual_form {
		padding: 10rpx 24rpx 32rpx 24rpx;

		.uni-input-wrapper {
			text-align: right;
		}

		.item-require {
			color: red;
			margin-right: 4rpx;
		}

		.item {
			.pd0 {
				padding-bottom: 0;
			}

			.radio {
				margin: 0 22rpx 0 22rpx;
				padding: 10rpx 0;
			}

			.discount .placeholder {
				color: #ccc;
				text-align: right;
				font-size: 28rpx;
			}

			.discount {
				max-width: 480rpx;
				font-size: 28rpx;
				text-align: right;

				&.discount_voice {
					overflow: hidden;
					text-overflow: ellipsis;
					white-space: nowrap;
					max-width: 460rpx;
					text-align: right;
				}

				.num {
					font-size: 28rpx;
					margin-right: 20rpx;
				}
			}
		}
	}
</style>