<template>
  <div class="divBox relative">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: '20px 20px 20px' }">
      <div class="member-benefit">
        <item-selector
          :isShowTitle="false"
          v-if="configData.menuList.list.length"
          :configObj="configData"
          configNme="menuList"
          :isDiy="false"
          @getSaveData="getSaveData"
          isFrom="noDiy"
        ></item-selector>
      </div>
    </el-card>
    <el-card dis-hover class="fixed-card" shadow="never" :bordered="false">
      <div class="acea-row row-center-wrapper">
        <el-button
          v-hasPermi="['merchant:member:benefits:save']"
          type="primary"
          v-debounceClick="saveBenefits"
          size="small"
          >保存修改</el-button
        >
      </div>
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
import ItemSelector from '@/components/PageDiy/mobileConfigRight/c_foot.vue';
import { memberBenefitsListApi, memberBenefitsSaveApi } from '@/api/member';
import {checkPermi} from "@/utils/permission";

export default {
  name: 'index',
  components: {
    ItemSelector,
  },
  data() {
    return {
      configData: {
        menuList: {
          list: [],
        },
      },
      benefits: [
        { name: '会员专享折扣', status: 1 },
        { name: '生日礼包', status: 1 },
        { name: '积分加速', status: 0 },
      ],
    };
  },
  mounted() {
    if (checkPermi(['merchant:member:benefits:list'])) this.getBenefits();
  },
  methods: {
    //获取提交的数据
    getSaveData(list) {
      this.configData.menuList.list = list.list;
    },
    async getBenefits() {
      let data = await memberBenefitsListApi();
      this.configData.menuList.list = data.map((item, index) => {
        return {
          ...item,
          checked: item.selectedIcon,
          unchecked: item.unselectedIcon,
          sort: index + 1,
          type: 'noDiy',
          canDel: item.canDel
        };
      });
    },
    async saveBenefits() {
      let data = this.configData.menuList.list.map((item, index) => {
        return {
          ...item,
          selectedIcon: item.checked,
          unselectedIcon: item.unchecked,
          sort: index + 1,
          link: item.linkUrl
        };
      });
      await memberBenefitsSaveApi({
        benefitsList: data,
      });
      this.$message.success('权益保存成功');
    },
  },
};
</script>

<style scoped lang="scss">
.member-benefit {
  width: 500px;
  .card-header {
    font-size: 18px;
    font-weight: bold;
  }
}
</style>
