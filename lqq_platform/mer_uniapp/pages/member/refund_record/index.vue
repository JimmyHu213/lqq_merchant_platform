<template>
	<view class="recharge-record-container" :data-theme="theme">
		<!-- 充值记录列表 -->
    <view v-for="(item, index) in records" :key="index">
      <line-table :item="item">
        <template #status>
          <text>{{ item.refundStatus | memberRefundListStatusFilter }}</text>
        </template>
      </line-table>
    </view>
		<!-- 空状态提示 -->
		<template v-if="!loadOptions.loading">
			<view class="text-center order-list-empty" v-if="loadOptions.loaded && records.length">
				暂无更多～
			</view>
			<view v-else-if="records.length === 0" class="nothing">
				<emptyPage title="暂无退款记录~" mTop="25%" :imgSrc="urlDomain+'crmebimage/presets/nodingdan.png'" />
			</view>
		</template>
		<view class="list-bottom-tab-placeholder"></view>

		<!-- 申请退款 -->
		<request-refund ref="requestRefundRef" :refundAmount="refundAmount" :orderNo="orderNo"
			@handleSubmit="handleForceRefetch"></request-refund>
	</view>
</template>

<script>
	import {
		shoppingCreditsRefundOrderApi
	} from "../memberApi";
	import lineTable from "../../../subPackage/pages/lineTable";
	import requestRefund from "../components/requestRefund";
  import emptyPage from "../../../components/emptyPage";
  let app = getApp();
	export default {
		name: "index",
		components: {
			requestRefund,
			lineTable,
      emptyPage
		},
		data() {
			return {
        theme: app.globalData.theme,
				refundAmount: '',
				urlDomain: this.$Cache.get("imgHost"),
				orderNo: '',
				merId: 0,
				loadOptions: {
					page: 1,
					limit: 20,
					total: 0,
					loading: false,
					loaded: false
				},
				records: []
			};
		},
		async onPullDownRefresh() {
			await this.handleForceRefetch();
			uni.stopPullDownRefresh();
		},
		computed: {
			// 搜索条件
			queryParams() {
				const params = {
					merId: this.merId
				};
				return params;
			}
		},
		onLoad(options) {
			this.merId = options.merId
			this.getRefundRecord();
		},
		methods: {
			// 申请退款
			// requestRefund(item) {
			// 	this.orderNo = item.orderNo
			// 	this.refundAmount = item.refundAmount
			// 	this.$refs.requestRefundRef.open()
			// },
			handleConfirm() {
				this.$util.navigateTo('/pages/member/refund_record/index')
			},
			handleForceRefetch() {
				this.handleResetLoadOptions();
				this.records = [];
				return this.getRefundRecord();
			},
			handleResetLoadOptions() {
				this.loadOptions.page = 1;
				this.loadOptions.total = 0;
				this.loadOptions.loaded = false;
				this.loadOptions.loading = false;
			},
			// 列表
			async getRefundRecord() {
				const {
					loading,
					loaded,
					page,
					limit
				} = this.loadOptions;
				if (loading || loaded) return;
				this.loadOptions.loading = true;
				try {
					const res = await shoppingCreditsRefundOrderApi({
						...this.queryParams,
						page,
						limit
					})
          this.$set(this.loadOptions, 'page', this.loadOptions.page + 1);
					const list = res.data.list.map(item => {
						return {
							...item,
							details: [{
									key: 'refundOrderNo',
									label: '',
									prefix: ''
								},
								{
									key: 'refundAmount',
									label: '申请金额',
									prefix: '¥'
								},
								{
									key: 'refundReason',
									label: '退款原因',
									prefix: ''
								},
								{
									key: 'refusingRefundReason',
									label: '拒绝原因',
									prefix: ''
								},
								{
									key: 'orderNo',
									label: '充值单号',
									prefix: ''
								},
								{
									key: 'createTime',
									label: '申请时间',
									prefix: ''
								}
							]
						}
					})
					this.records.push(...list);
					this.loadOptions.total = res.data.total;
					this.loadOptions.loaded = this.records.length >= this.loadOptions.total;
				} catch (err) {
					this.$util.Tips({
						title: err,
						icon: "none"
					});
				}
				this.loadOptions.loading = false;
			}
		},
    onReachBottom() {
      this.getRefundRecord();
    }
	}
</script>

<style scoped>
	.refund-btn {
		padding: 12rpx 32rpx;
		border-radius: 30rpx;
		border: 1rpx solid #CCCCCC;
	}

	/* 页面容器 */
	.recharge-record-container {
		background-color: #f5f5f5;
		min-height: 100vh;
		padding-bottom: 30rpx;
	}
</style>