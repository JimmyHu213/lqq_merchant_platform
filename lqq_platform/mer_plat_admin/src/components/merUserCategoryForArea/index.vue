<template>
  <div>
    <el-cascader
      v-model="merIds"
      class="selWidth"
      :show-all-levels="false"
      :options="merSelect"
      :props="merProps"
      filterable
      clearable
      placeholder="请选择商户"
      @change="onChangeMerId"
    />
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
import * as merchant from '@/api/merchant';
import { areaMerchantApi } from '@/api/area';
import product from '@/mixins/product';
export default {
  name: 'merUserCategoryForArea',
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
    merIdChecked: {
      type: [Array, String, Number],
      default: () => null,
    },
    areaId: {
      type: [String, Number],
      default: 0,
    },
  },
  watch: {
    merIdChecked(n) {
      this.merIds = n;
    },
    areaId(n) {
      if (n !== undefined) {
        this.getAreaMerchantList();
      }
    },
  },
  data() {
    return {
      merProps: {
        value: 'id',
        label: 'name',
        children: 'merchantList',
        expandTrigger: 'hover',
        emitPath: false,
        multiple: this.multiple,
      },
      merSelect: [],
      merIds: null,
      areaMerchantIds: [], // 圈层关联的商户ID列表
    };
  },
  mounted() {
    this.merIds = this.merIdChecked;
    this.getMerList();
    this.getAreaMerchantList();
  },
  methods: {
    // 获取商户列表
    getMerList() {
      merchant.merCategoryListApi().then((res) => {
        this.merSelect = res;
        // 获取圈层商户数据后，更新禁用状态
        this.updateMerchantDisabledStatus();
      });
    },
    // 获取圈层关联的商户列表
    getAreaMerchantList() {
      // 传递0获取所有可选的商户列表，用于计算disabled状态
      areaMerchantApi(0)
        .then((res) => {
          // areaMerchantApi返回的直接是商户数组，格式：[{id, name, realName, phone, ...}]
          this.areaMerchantIds = Array.isArray(res) ? res.map((item) => item.id) : [];
          // 更新商户禁用状态
          this.updateMerchantDisabledStatus();
        })
        .catch((err) => {
          this.areaMerchantIds = [];
          this.updateMerchantDisabledStatus();
        });
    },
    // 更新商户禁用状态
    updateMerchantDisabledStatus() {
      if (!this.merSelect.length) return;

      // 递归处理商户列表，设置disabled状态
      const processMerchantList = (merchantList) => {
        return merchantList.map((category) => {
          if (category.merchantList && category.merchantList.length > 0) {
            // 处理分类下的商户
            category.merchantList = category.merchantList.map((merchant) => {
              return {
                ...merchant,
                disabled: !this.areaMerchantIds.includes(merchant.id),
              };
            });
          }
          return category;
        });
      };

      this.merSelect = processMerchantList(this.merSelect);
    },
    onChangeMerId() {
      // 根据merIds 获取所有 this.merSelect 下 id 对应的第二级商户的对象
      const merArr = [];
      const allMerchants = this.merSelect.flatMap((cat) => cat.merchantList);
      
      this.merIds.forEach((id) => {
        const merchant = allMerchants.find((mer) => mer.id === id);
        if (merchant) {
          merArr.push(merchant);
        }
      });
      
      this.$emit('getMerId', this.merIds, merArr);
    },
    // 刷新圈层商户数据（供外部调用）
    refreshAreaMerchantData() {
      this.getAreaMerchantList();
    },
  },
};
</script>
<style lang="scss">
::v-deep.el-cascader__search-input {
  color: white;
  opacity: 0 !important;
}
</style>
