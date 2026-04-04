<template>
	<!-- 拆单订单 -->
	<view v-if="splitGoods.length">
		<view class="all" v-if="select_all">
			<checkbox-group @change="checkboxAllChange">
				<checkbox value="all" :checked="isAllSelect" />
				<text class='checkAll'>全选</text>
			</checkbox-group>
		</view>
		<checkbox-group @change="checkboxChange">
			<block v-for="(item,index) in splitGoods" :key="index">
        <view class="splitOrder borRadius14">
          <view class='items acea-row row-between-wrapper' v-if="item.surplus_num>0">
            <checkbox v-show="isShowIpt(item)"  :value="item.id.toString()" :checked="item.checked" :disabled="(type!=1&&item.surplus_num<1)?true:false" />
            <view class='picTxt acea-row row-between-wrapper'>
              <view class='pictrue'>
                <image :src='item.image'></image>
              </view>
              <view class='text'>
                <view class="acea-row row-between-wrapper">
                  <view class='name line1'>{{item.productName}}</view>
                </view>
                <view class='infor line1'>
                  {{item.sku || '默认'}}</view>
                <view class="acea-row row-middle money-section">
                  <text class="infor-num mr-8">购买 x{{item.payNum}}</text>
                  <text class="infor-text" v-if="type==1">{{`(${item.refundNum}件已退 ${item.payNum-item.refundNum}件可退）`}}</text>
                  <text class="infor-text" v-if="type==0">{{ `(${item.deliveryNum}件已发 ${item.refundNum}件已退）`}}</text>
                </view>
              </view>
              <view v-show="isShowIpt(item)" class='carnum acea-row row-center-wrapper'>
                <view class="reduce iconfont icon-ic_Reduce mr-8" :class="item.surplus_num == 1 ? 'on' : ''" @click.stop='subCart(item)'></view>
                <view class='num rd-12rpx'>{{item.surplus_num}}</view>
                <view class="plus iconfont icon-ic_increase ml-8" :class="item.surplus_num == item.numShow ? 'on' : ''" @click.stop='addCart(item)'></view>
              </view>
            </view>
          </view>
        </view>
        <view v-if="item.workOrderList && item.workOrderList.length">
          <view v-for="(itm, idx) in getSplitOrderTableList(item.workOrderList[0])" :key="itm.id">
            <OrderTable :list="itm.list"></OrderTable>
          </view>
          <view class="bg--w111-fff borRadius14" v-for="(items,indexs) in item.workOrderList" :key="items.id">
            <view class='item flex-y-center mt20'>
              <checkbox :value="(items.id).toString()" :checked="items.checked"/>
              <view class="ml-12">工单号：{{ items.workOrderNo }}</view>
            </view>
          </view>
        </view>
			</block>
		</checkbox-group>
	</view>
</template>

<script>
	import OrderTable from "../OrderDetail/OrderTable";
  import {getSplitOrderTableList} from "../../order";
  import {ProductTypeEnum} from "@/enums/productEnums";
  export default {
		props: {
			splitGoods: {
				type: Array,
				default: () => []
			},
			select_all: {
				type: Boolean,
				default: true
			},
			//0-分单发货 1-分单付款
			type:{
				default:'0'
			},
      // 订单二级分类
      secondType: {
        type: Number,
        default: () => 0
      },
      // 父组件的全选状态
      parentAllSelected: {
        type: Boolean,
        default: false
      }
		},
    components: {
      OrderTable,
    },
    watch: {
      // 监听父组件的全选状态变化
      parentAllSelected: {
 
        handler(newVal) {
          if (newVal) {
            this.selectAllWorkOrders();
          } else {
            // 父组件 isAll 变为 false 可能是单个勾选变化导致，不在此处做全不选操作
            // 如需全不选，请由父组件显式调用 unselectAllWorkOrders()
          }
        },
        immediate: true
      }
    },
		data() {
			return {
				isAllSelect: false,
        ProductTypeEnum
			};
		},
		methods: {
      getSplitOrderTableList,
      // 针对工单与普通订单的判断
      isShowIpt(item){
        return !item.workOrderList
      },
      // 全选所有工单
      selectAllWorkOrders() {
        this.splitGoods.forEach((item) => {
          if (item.workOrderList && item.workOrderList.length) {
            item.workOrderList.forEach((workOrder) => {
              this.$set(workOrder,'checked',true)
            });
          }
          else {
            item.checked = true;
          }
        });
        this.isAllSelect = true;
        // 通知父组件更新
        this.emitSelectedWorkOrders();
      },
      // 取消全选所有工单
      unselectAllWorkOrders() {
        this.splitGoods.forEach((item) => {
          if (item.workOrderList && item.workOrderList.length) {
            item.workOrderList.forEach((workOrder) => {
              this.$set(workOrder,'checked',false)
            });
          }
          else {
            item.checked = false;
          }
        });
        this.isAllSelect = false;
        // 通知父组件更新
        this.emitSelectedWorkOrders();
      },
      // 发送选中的工单ID列表给父组件
      emitSelectedWorkOrders() {
        if ( this.splitGoods[0].workOrderList) {
          const selectedWorkOrderIds = [];
          this.splitGoods.forEach((item) => {
            if (item.workOrderList && item.workOrderList.length) {
              item.workOrderList.forEach((workOrder) => {
                if (workOrder.checked) {
                  selectedWorkOrderIds.push(workOrder.id.toString());
                }
              });
            }
          });
          this.$emit('getList', selectedWorkOrderIds);
        } else {
          this.$emit('getList', this.splitGoods);
        }
      },
			subCart(item) {
				item.surplus_num = item.surplus_num - 1;
				if (item.surplus_num <= 1) {
					item.surplus_num = 1 
				}
				this.$emit('getList', this.splitGoods);
			},
			addCart(item) {
				item.surplus_num = item.surplus_num + 1;
				if (item.surplus_num >= item.numShow) {
					item.surplus_num = item.numShow
				}
				this.$emit('getList', this.splitGoods);
			},
			inArray: function(search, array) {
				for (let i in array) {
					if (array[i] == search) {
						return true;
					}
				}
				return false;
			},
			checkboxChange(event) {
				let idList = event.detail.value;
				this.splitGoods.forEach((item) => {
          if(item.workOrderList && item.workOrderList.length){
            item.workOrderList.forEach((itm) => {
              if (this.inArray(itm.id, idList)) {
                this.$set(itm,'checked',true)
              } else {
                this.$set(itm,'checked',false)
              }
            })
            this.getAllSelect(idList, item.workOrderList)
          }else{
            if (this.inArray(item.id, idList)) {
              this.$set(item,'checked',true)
            } else {
              this.$set(item,'checked',false)
            }
            this.getAllSelect(idList, this.splitGoods)
          }
				})
        if(this.splitGoods[0].workOrderList && this.splitGoods[0].workOrderList.length){
         this.$emit('getList', idList);
        }else{
          this.$emit('getList', this.splitGoods);
        }
			},
      // 全选
      getAllSelect(idList, list){
        if (idList.length === list.length) {
          this.isAllSelect = true;
        } else {
          this.isAllSelect = false;
        }
      },
			forGoods(val) {
				let that = this;
				if (!that.splitGoods.length) return
				that.splitGoods.forEach((item) => {
					if (val) {
						item.checked = true;
					} else {
						item.checked = false;
					}
				})
				that.$emit('getList', that.splitGoods);
			},
			checkboxAllChange(event) {
				let value = event.detail.value;
				if (value.length) {
					this.forGoods(1)
				} else {
					this.forGoods(0)
				}
			}
		}
	}
</script>

<style lang="scss">
	/deep/checkbox .uni-checkbox-input.uni-checkbox-input-checked {
		border: 1px solid #007aff !important;
		background-color: #007aff !important;
		color: #fff !important;
	}
	.splitOrder {
		background-color: #fff;
		margin: 20rpx auto 0 auto;
		padding: 32rpx 24rpx;
	}

	.splitOrder .all {
		padding: 20rpx 30rpx;
	}

	.splitOrder .all .checkAll {
		margin-left: 20rpx;
	}
  .item{
    padding: 32rpx 24rpx;
  }
	.splitOrder .items~.items {
		margin-top: 48rpx;
	}

	.splitOrder .items .picTxt {
		width: 604rpx;
		position: relative;
	}

	.splitOrder .items .picTxt .name {
		width: 444rpx;
	}

	.splitOrder .items .picTxt .pictrue {
		width: 136rpx;
		height: 136rpx;
	}

	.splitOrder .items .picTxt .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 16rpx;
	}

	.splitOrder .items .picTxt .text {
		width: 450rpx;
		font-size: 28rpx;
		color: #333;
		font-weight: 400;
	}

	.splitOrder .items .picTxt .text .reColor {
		color: #999;
	}

	.splitOrder .items .picTxt .text .reElection {
		margin-top: 20rpx;
	}

	.splitOrder .items .picTxt .text .reElection .title {
		font-size: 24rpx;
	}

	.splitOrder .items .picTxt .text .reElection .reBnt {
		width: 120rpx;
		height: 46rpx;
		border-radius: 23rpx;
		font-size: 26rpx;
	}

	.splitOrder .items .picTxt .text .infor {
		font-size: 22rpx;
		color: #999;
		margin-top: 12rpx;
		width: 284rpx;
	}

	.splitOrder .items .picTxt .text .money-section {
		margin-top: 18rpx;
		font-size: 22rpx;
		color: #999999;
	}

	.splitOrder .items .picTxt .text .money {
		font-size: 36rpx;
		color: #333;
		font-family: 'Regular';
		color: #FF7D00;
	}

	.splitOrder .items .picTxt .carnum {
		height: 36rpx;
		position: absolute;
		bottom: 0;
		right: -16rpx;
	}

	.splitOrder .items .picTxt .carnum view {
		text-align: center;
		height: 100%;
		line-height: 36rpx;
		font-size: 24rpx;
		color: #333;
	}

	.splitOrder .items .picTxt .carnum .reduce {
		border-right: 0;
		border-radius: 3rpx 0 0 3rpx;
	}

	.splitOrder .items .picTxt .carnum .reduce.on {
		border-color: #e3e3e3;
		color: #dedede;
	}

	.splitOrder .items .picTxt .carnum .plus {
		border-left: 0;
		border-radius: 0 3rpx 3rpx 0;
		font-size: 26rpx;
	}

	.splitOrder .items .picTxt .carnum .plus.on {
		border-color: #e3e3e3;
		color: #dedede;
	}

	.splitOrder .items .picTxt .carnum .num {
		color: #282828;
		background: #F5F5F5;
		width: 72rpx;
	}
	.infor-num{
		font-size: 22rpx;
		color: #333333;
	}
	.infor-text{
		font-size: 22rpx;
		color: #2A7EFB;
	}
</style>