<template>
  <div class="divBox relative">
    <el-card class="box-card" :body-style="{ padding: '20px' }" shadow="never" :bordered="false">
      <div class="mb20 acea-row row-between-wrapper">
        <span class="table-title">分账记录</span>
        <el-date-picker
          v-model="tableFrom.date"
          type="date"
          placeholder="按日期筛选"
          value-format="yyyy-MM-dd"
          size="small"
          style="width: 200px"
          @change="getList"
        />
      </div>
      <el-table v-loading="listLoading" :data="tableData" style="width: 100%" size="small">
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column prop="userId" label="消费者ID" min-width="90" />
        <el-table-column label="接收方" min-width="120">
          <template slot-scope="scope">
            <span>{{ receiverTypeText(scope.row.receiverType) }}</span>
            <span v-if="scope.row.receiverName"> ({{ scope.row.receiverName }})</span>
          </template>
        </el-table-column>
        <el-table-column prop="baseAmount" label="分账基数(元)" min-width="110" />
        <el-table-column prop="rate" label="比例(%)" min-width="80" />
        <el-table-column prop="amount" label="分账金额(元)" min-width="110">
          <template slot-scope="scope">
            <span class="amount-text">{{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="100">
          <template slot-scope="scope">
            <el-tag :type="statusType(scope.row.status)" size="small">
              {{ statusText(scope.row.status) }}
            </el-tag>
            <el-tag v-if="scope.row.isUnfrozen === 1" type="success" size="small" style="margin-left:4px">已解冻</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" min-width="160" />
        <el-table-column label="操作" min-width="80" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleDetail(scope.row.orderNo)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog title="分账详情" :visible.sync="detailVisible" width="750px">
      <el-descriptions :column="2" border size="small" v-if="detailList.length > 0">
        <el-descriptions-item label="订单号">{{ detailList[0].orderNo }}</el-descriptions-item>
        <el-descriptions-item label="消费者ID">{{ detailList[0].userId }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detailList" style="width: 100%; margin-top: 16px" size="small">
        <el-table-column label="接收方" min-width="120">
          <template slot-scope="scope">
            {{ receiverTypeText(scope.row.receiverType) }}
          </template>
        </el-table-column>
        <el-table-column prop="receiverName" label="接收方名称" min-width="100" />
        <el-table-column prop="baseAmount" label="分账基数" min-width="100" />
        <el-table-column prop="rate" label="比例(%)" min-width="80" />
        <el-table-column prop="amount" label="金额(元)" min-width="100">
          <template slot-scope="scope">
            <span class="amount-text">{{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="120">
          <template slot-scope="scope">
            <el-tag :type="statusType(scope.row.status)" size="small">
              {{ statusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="150" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { profitSharingListApi, profitSharingDetailApi } from '@/api/lqq';

export default {
  name: 'ProfitSharingList',
  data() {
    return {
      listLoading: false,
      tableFrom: {
        date: '',
      },
      tableData: [],
      detailVisible: false,
      detailList: [],
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    getList() {
      this.listLoading = true;
      profitSharingListApi(this.tableFrom)
        .then((res) => {
          this.tableData = res.data || [];
          this.listLoading = false;
        })
        .catch(() => {
          this.listLoading = false;
        });
    },
    handleDetail(orderNo) {
      profitSharingDetailApi(orderNo).then((res) => {
        this.detailList = res.data || [];
        this.detailVisible = true;
      });
    },
    receiverTypeText(type) {
      const map = {
        'LOCKED_MERCHANT': '锁客商户',
        'REFERRER': '推荐人(裂变)',
        'REFERRER_PARENT': '推荐人上级',
        'PROMOTER': '代理推广员',
        'PLATFORM': '平台',
      };
      return map[type] || type;
    },
    statusText(status) {
      const map = { 0: '待分账', 1: '分账成功', 2: '分账失败', 3: '已解冻' };
      return map[status] || '未知';
    },
    statusType(status) {
      const map = { 0: 'info', 1: 'success', 2: 'danger', 3: '' };
      return map[status] || 'info';
    },
  },
};
</script>

<style scoped>
.table-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.amount-text {
  color: #e93323;
  font-weight: 600;
}
</style>
