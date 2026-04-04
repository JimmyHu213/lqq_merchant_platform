<template>
  <div class="divBox relative">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: '20px 20px 20px' }">
<!--      <div class="current_home">-->
<!--        <el-image :src="homeImage"></el-image>-->
<!--        &lt;!&ndash;底部导航&ndash;&gt;-->
<!--        <div-->
<!--            v-hasPermi="['merchant:page:layout:bottom:navigation']"-->
<!--            class="page-foot cur_pointer"-->
<!--            @click="handleMessage('bottomNavigation')"-->
<!--        >-->
<!--          &lt;!&ndash;        :class="{ select_ctive: shows == 8 }"&ndash;&gt;-->
<!--          <div class="page-fooot">-->
<!--            <div class="foot-item" v-for="(item, index) in configData.menuList.list" :key="index">-->
<!--              <el-image :src="item.checked" alt="" class="el-image" />-->
<!--              <p v-if="index == 0" class="textE93323">{{ item.name }}</p>-->
<!--              <p v-else>{{ item.name }}</p>-->
<!--            </div>-->
<!--          </div>-->
<!--        </div>-->
<!--      </div>-->
      <div class="benefit-box">
        <item-selector
            :isShowTitle="false"
            :configObj="configData"
            configNme="menuList"
            :isDiy="true"
            @getSaveData="getSaveData"
            isFrom="merHome"
        ></item-selector>
      </div>
    </el-card>
    <el-card dis-hover class="fixed-card" shadow="never" :bordered="false">
      <div class="acea-row row-center-wrapper">
        <el-button
            v-hasPermi="['merchant:page:layout:bottom:navigation:save']"
            type="primary"
            v-debounceClick="saveNavigation"
            size="small"
        >保存修改</el-button
        >
      </div>
    </el-card>
  </div>
</template>

<script>
import ItemSelector from '@/components/PageDiy/mobileConfigRight/c_foot.vue';
import { checkPermi } from '@/utils/permission';
import {bottomNavigationGetApi, bottomNavigationSaveApi} from '@/api/devise';
import {memberBenefitsSaveApi} from "@/api/member";
export default {
  name: 'index',
  components: {
    ItemSelector,
  },
  data() {
    return {
      navigationListTab: [],
      homeImage: '',
      configData: {
        menuList: {
          list: [],
        },
      },
    };
  },
  mounted() {
    if (checkPermi(['merchant:page:layout:bottom:navigation'])) this.getNavigation();
  },
  methods: {
    async getNavigation() {
      const data = await bottomNavigationGetApi();
      this.configData.menuList.list = data.map((item, index) => {
        return {
          ...item,
          link: item.linkUrl,
          checked: item.selectedIconLinkUrl,
          unchecked: item.unselectedIconLinkUrl,
          sort: index + 1,
          type: 'noDiy',
          canDel: true
        };
      });
    },
    async saveNavigation(){
      let data = this.configData.menuList.list.map((item, index) => {
        return {
          ...item,
          selectedIconLinkUrl: item.checked,
          unselectedIconLinkUrl: item.unchecked,
          sort: index + 1,
          linkUrl: item.link,
          status: true
        };
      });
      await bottomNavigationSaveApi(data);
      this.$message.success('菜单成功');
    },
    handleMessage() {},
    getSaveData() {},
  },
};
</script>

<style scoped lang="scss">
.benefit-box{
  width: 600px;
}
</style>
