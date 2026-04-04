<template>
  <div class="divBox relative">
    <el-card class="box-card dataBox" :body-style="{ padding: '0 20px 20px' }">
      <el-tabs v-model="activeName" class="list-tabs">
        <el-tab-pane label="上门服务流程" name="1" />
        <el-tab-pane label="服务区域" name="2" />
      </el-tabs>
      <el-table :data="tableData" v-if="activeName == 1" class="mt10 width100">
        <el-table-column prop="name" label="节点名称" width="180"> </el-table-column>
        <el-table-column prop="status" label="状态" width="200">
          <template slot-scope="scope">
            <div v-if="checkPermi(['admin:merchant:reservation:to:home:edit'])">
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
        <el-table-column prop="address" label="操作" width="100">
          <template slot-scope="scope">
            <a v-if="scope.row.action && checkPermi(['admin:merchant:reservation:to:home:edit'])" @click="open(scope.row)">{{ scope.row.action }}</a>
          </template>
        </el-table-column>
      </el-table>
      <div class="width100" v-if="activeName == 2">
        <draw-geometry-map
          @getServiceAreaRange="getServiceAreaRange"
          v-model="form"
        ></draw-geometry-map>
      </div>
    </el-card>
    <!-- 打卡范围 -->
    <el-dialog title="管理表单设置" :visible.sync="clockVisible" width="600px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <template v-if="type === 1">
          <el-form-item label="打卡限制：">
            <el-radio-group v-model="form.clockInAddressSwitch" @change="typeChange">
              <el-radio :label="true">服务范围内打卡</el-radio>
              <el-radio :label="false">不限制打卡地址</el-radio>
            </el-radio-group>
            <div class="from-tips">未配置地图时，请选择不限制打卡地址</div>
          </el-form-item>
          <el-form-item label="打卡范围：" v-if="form.clockInAddressSwitch" prop="clockInDistance">
            <el-input-number
                v-model.number="form.clockInDistance"
                size="small"
                type="number"
                :min="1"
                style="width: 48%"
            >
            </el-input-number>
            <span class="from-tips ml10">米</span>
          </el-form-item>
          <el-form-item label="打卡要求：">
            <el-radio-group v-model="form.clockInPhotoSwitch">
              <el-radio :label="true">需要拍照备注</el-radio>
              <el-radio :label="false">跳过拍照备注环节</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>
        <template v-if="type === 2 && clockVisible">
          <el-form-item label="关联表单：" prop="serviceEvidenceFormId">
            <el-select
              v-model="form.serviceEvidenceFormId"
              placeholder="请选择关联表单"
              clearable
              filterable
              style="width: 57%"
            >
              <el-option
                :value="item.id"
                v-for="(item, index) in systemFormList"
                :key="index.id"
                :label="item.formName"
              ></el-option>
            </el-select>
            <el-button
              size="small"
              class="ml15"
              v-if="checkPermi(['merchant:system:form:add'])"
              @click="handlerCreatFrom(0, 'add')"
              >添加表单</el-button
            >
            <el-button size="small" class="ml15" v-if="checkPermi(['merchant:system:form:add'])" @click="getFormList"
              >刷新</el-button
            >
            <div class="from-tips mb5">服务人员结束服务之前，必须提交此表单内容。</div>
            <div class="item" v-if="form.serviceEvidenceFormId">
              <div class="acea-row row-middle">
                <div>
                  <iframe
                    :src="`${$selfUtil.getFrontDomainUrl()}/pages/goods/systemIframe/index?id=${
                      form.serviceEvidenceFormId
                    }`"
                    style="width: 350px; height: 500px"
                    frameborder="0"
                  />
                </div>
              </div>
            </div>
          </el-form-item>
        </template>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleSubmit" size="small">确定</el-button>
      </div>
    </el-dialog>
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
import { systemFormPageApi } from '@/api/systemForm';
import { reservationHomeEditApi, reservationHomeInfoApi} from '@/api/reservation';
import { useProduct } from '@/hooks/use-product';
const { handlerCreatFromUse } = useProduct();
import DrawGeometryMap from './DrawGeometryMap';
export default {
  name: 'LabelList',
  data() {
    return {
      activeName: '1',
      type: 1,
      clockVisible: false,
      form: {
        serviceEvidenceFormId: null,
        clockInDistance: 1,
        vieSwitch: true,
        clockInPhotoSwitch: true,
        clockInAddressSwitch: false,
        serviceEvidenceSwitch: false,
        clockInType: 1,
        clockInTypeBtn: false,
        serviceAreaSwitch: false,
        serviceAreaRange: ''
      },
      rules: {
        clockInDistance: [{ required: true, message: '请输入打卡范围', trigger: 'change' }],
        serviceEvidenceFormId: [{ required: true, message: '请选择关联表单', trigger: 'change' }],
      },
      formUrl: '',
      systemFormList: [],
      tableData: [
        {
          name: '用户预约',
          status: true,
          disable: true,
          info: '  用户预约为必要节点，不可关闭',
        },
        {
          name: '抢单',
          key: 'vieSwitch',
          info: `抢单开启，服务人员可以抢单`,
        },
        {
          name: '上门打卡',
          key: 'clockInTypeBtn',
          info: '上门打卡关闭之后，则无需开始服务直接进入下一环节',
          type: 1,
          action: '上门打卡范围',
        },
        {
          name: '服务过程留凭',
          key: 'serviceEvidenceSwitch',
          info: '服务过程留凭根据关联系统表单填写提交，服务过程选填可跳过，关闭则直接进入下一环节',
          action: '关联系统表单',
          type: 2,
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
  components: { DrawGeometryMap },
  mounted() {
    if (checkPermi(['merchant:system:form:page'])) this.getFormList();
    if (checkPermi(['admin:merchant:reservation:to:home:info'])) this.getInfo();
  },
  methods: {
    checkPermi,
    // 回调地图绘制后得到坐标数组
    getServiceAreaRange(val) {
      this.form.serviceAreaRange = JSON.stringify(val);
      this.saveDate();
    },
    //创建、编辑表单
    handlerCreatFrom(id, type) {
      handlerCreatFromUse(id, type);
    },
    typeChange(val) {
      if (this.form.clockInAddressSwitch) {
        this.form.clockInDistance = 1;
      } else {
        this.form.clockInDistance = 500;
      }
    },

    // 表格操作
    open(row) {
      this.type = row.type;
      if (row.type == 1) {
        this.clockVisible = true;
      } else {
        this.clockVisible = true;
      }
    },
    //详情
    getInfo() {
      reservationHomeInfoApi().then((res) => {
        this.form = {
          ...res,
          clockInTypeBtn: res.clockInType === 2 ? true : false,
          serviceEvidenceFormId: res.serviceEvidenceFormId || null,
        };
      });
    },
    // 保存
    handleSave() {
      this.saveDate();
    },
    //  提交数据调接口
    handleSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.saveDate();
          this.clockVisible = false;
        }
      });
    },
    // 调取接口
    statusChange(row) {
      if(row.key === 'serviceEvidenceSwitch' && this.form.serviceEvidenceSwitch &&  !this.form.serviceEvidenceFormId){
        this.form.serviceEvidenceSwitch = !this.form.serviceEvidenceSwitch
         return this.$message.warning('开启服务过程留凭请先关联系统表单！');
      }
      this.saveDate()
    },
    // 保存数据
    saveDate(){
      this.form.clockInType = this.form.clockInTypeBtn ? 2 : 1;
      reservationHomeEditApi(this.form).then((res) => {
        this.$message.success('保存成功');
      });
    },
    // 系统表单下拉数据
    async getFormList() {
      const { list } = await systemFormPageApi({ page: 1, limit: 999 });
      this.systemFormList = list;
    },
  },
};
</script>
<style scoped lang="scss">
.iframe-box {
  min-height: 300px;
}
</style>
