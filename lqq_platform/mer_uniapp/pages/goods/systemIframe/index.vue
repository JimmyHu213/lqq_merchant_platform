<template>
	<view class="systemForm-box">
		<view class="herader">系统表单</view>
		<!--商品关联系统表单-->
		<view class='wrapper borRadius14' v-if="systemFormValue">
			<systemFrom :value="systemFormValue" ref="systemFromRef"></systemFrom>
		</view>
	</view>
</template>

<script>
	import systemFrom from '../components/systemFrom/index.vue';
	import {systemFromDetail} from '@/api/api'
	export default {
		components: {
			systemFrom
		},
		data() {
			return {
				systemFormValue: ''
			}
		},
		onLoad(options) {
			this.getInfo(options.id)
		},
		methods:{
			getInfo(id){
				systemFromDetail(id).then(res=>{
					this.systemFormValue= res.data.formValue ? this.$util.objToArr(JSON.parse(res.data.formValue)) :[]
          this.$nextTick(() => {
            if(this.systemFormValue.length){
              this.$refs.systemFromRef.getFormList(1)
            }
          })
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	.systemForm-box{
		.herader{
			height: 100rpx;
			line-height: 100rpx;
			background: #fff;
			text-align: center;
			border: 2rpx solid #d5d5d5;
		}
	}
</style>