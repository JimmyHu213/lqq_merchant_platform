<template>
  <div class="divBox">
    <el-card class="box-card" :bordered="false" shadow="never">
      <div v-loading="loading" class="ueditor">
        <div class="text-center mb20 f-s-18">会员开卡协议</div>
        <Tinymce v-model="agreement"></Tinymce>
      </div>
      <el-card  v-hasPermi="['merchant:agreement:member:open:card:save']" dis-hover class="fixed-card" shadow="never" :bordered="false">
        <div class="acea-row row-center-wrapper">
          <el-button class="mt20" type="primary" v-debounceClick="handleSubmit">保存</el-button>
        </div>
      </el-card>

    </el-card>
  </div>
</template>

<script>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import Tinymce from '@/components/Tinymce/index';
import { checkPermi } from '@/utils/permission';
import {agreementInfoApi, merchantAgreementInfoApi, merchantAgreementSaveApi} from '@/api/member';

export default {
  name: 'index',
  components: { Tinymce },
  data() {
    return {
      loading: false,
      agreement: '',
      defaultAgreement:
        '入会规则:申请成为本店会员，累计在本店铺消费金额和消费次数达到商家设置的值享受相应会员权益，具体等级规则可进会员页查看。会员权益包含:会员专享全店商品折扣、单品会员专享价、会员优惠券、会员积分玩法，具体权益以本店设置为准。',
    };
  },
  mounted() {
    if (checkPermi(['merchant:agreement:member:open:card:info'])) this.getInfo();
  },
  methods: {
    checkPermi,
    async handleSubmit(){
      try {
        if(!this.agreement) return this.$message.warning('请填写协议')
        await merchantAgreementSaveApi({agreement: this.agreement});
        this.$message.success('保存成功')
      } catch (e) {
      }
    },
    async getInfo(data) {
      this.loading = true;
      try {
        let res = await merchantAgreementInfoApi();
        this.agreement = res ? JSON.parse(res).agreement : this.defaultAgreement;
        this.loading = false;
      } catch (e) {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped></style>
