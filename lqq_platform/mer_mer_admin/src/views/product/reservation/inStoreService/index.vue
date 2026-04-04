<template>
  <div class="divBox">
    <el-card class="box-card dataBox">
      <el-table :data="tableData" class="mt10 width100">
        <el-table-column prop="name" label="节点名称" width="180"> </el-table-column>
        <el-table-column prop="status" label="状态" width="180">
          <template slot-scope="scope">
            <div v-if="checkPermi(['admin:merchant:reservation:to:store:edit'])">
              <el-switch
                  v-if="scope.row.status"
                  v-model="scope.row.status"
                  disabled
                  :active-value="true"
                  :inactive-value="false"
                  active-text="开启"
                  inactive-text="关闭"
              />
              <el-switch
                  v-else
                  v-model="form[scope.row.key]"
                  :active-value="true"
                  :inactive-value="false"
                  active-text="开启"
                  inactive-text="关闭"
                  @change="statusChange(scope.row)"
              />
            </div>
            <div v-else>{{scope.row.status ? '开启' : '关闭'}}</div>
          </template>
        </el-table-column>
        <el-table-column prop="info" label="说明">
          <template slot-scope="scope">
            <div v-html="scope.row.info"></div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
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
import { checkPermi } from '@/utils/permission';
import {
  reservationStoreEditApi,
  reservationStoreInfoApi
} from '@/api/reservation';
import { useProduct } from '@/hooks/use-product';
const { handlerCreatFromUse } = useProduct();
export default {
  name: 'LabelList',
  data() {
    return {
      activeName: '1',
      form: {
        serviceWorkOrderSwitch: false,
      },
      tableData: [
        {
          name: '用户预约',
          status: true,
          disable: true,
          info: '  用户预约为必要节点，不可关闭',
        },
        {
          name: '工单',
          key: 'serviceWorkOrderSwitch',
          info: '工单开启核销后会生成服务工单，关闭不生成工单，订单状态直接流转已收货、已服务 ',
        },
        {
          name: '结束服务',
          status: true,
          disable: true,
          info: '服务人员结束服务',
        },
      ],
    };
  },
  mounted() {
    if (checkPermi(['admin:merchant:reservation:to:store:info'])) this.getInfo();
  },
  methods: {
    checkPermi,
    //详情
    getInfo() {
      reservationStoreInfoApi().then((res) => {
        this.form = {
          ...res
        };
      });
    },
    // 调取接口
    statusChange(row) {
      this.saveDate()
    },
    // 保存数据
    saveDate(){
      reservationStoreEditApi(this.form).then((res) => {
        this.$message.success('保存成功');
      });
    },
  },
};
</script>