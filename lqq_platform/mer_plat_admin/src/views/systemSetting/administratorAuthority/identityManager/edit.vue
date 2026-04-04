<template>
  <div>
    <el-form ref="pram" :model="pram" label-width="75px" @submit.native.prevent>
      <el-form-item
        label="角色名称："
        prop="roleName"
        :rules="[{ required: true, message: '请填写角色名称', trigger: ['blur', 'change'] }]"
      >
        <el-input v-model.trim="pram.roleName" placeholder="身份名称" />
      </el-form-item>
      <el-form-item label="状态：">
        <el-switch
          v-model="pram.status"
          active-text="开启"
          inactive-text="关闭"
          :active-value="true"
          :inactive-value="false"
        />
      </el-form-item>
      <el-form-item label="菜单权限：">
        <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event, 'menu')">展开/折叠</el-checkbox>
        <!-- <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event, 'menu')">全选/全不选</el-checkbox> -->
        <el-checkbox v-model="menuCheckStrictly" @change="handleCheckedTreeConnect($event, 'menu')"
          >父子联动</el-checkbox
        >
        <el-tree
          class="tree-border"
          :data="menuOptions"
          show-checkbox
          ref="menu"
          node-key="id"
          :check-strictly="!menuCheckStrictly"
          empty-text="加载中，请稍候"
          :props="defaultProps"
        ></el-tree>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer-inner">
      <el-button @click="close">取消</el-button>
      <el-button
        type="primary"
        @click="handlerSubmit('pram')"
        v-hasPermi="[
          'platform:admin:role:update',
          'platform:admin:role:save',
          'circle:role:save',
          'circle:role:update',
        ]"
      >
        {{ isCreate === 0 ? '确定' : '更新' }}</el-button
      >
    </div>
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
import * as roleApi from '@/api/role.js';
import * as areaApi from '@/api/area.js';
import { Debounce } from '@/utils/validate';
import { isPlatform } from '@/utils/settingMer';
import Cookies from 'js-cookie';
export default {
  name: 'roleEdit',
  props: {
    isCreate: {
      type: Number,
      required: true,
    },
    editData: {
      type: Object,
      default: null,
    },
  },
  data() {
    return {
      pram: {
        roleName: null,
        rules: '',
        status: null,
        id: null,
      },
      menuExpand: false,
      menuNodeAll: false,
      menuOptions: [],
      menuCheckStrictly: true,
      currentNodeId: [],
      defaultProps: {
        children: 'childList',
        label: 'name',
      },
      menuIds: [],
    };
  },
  mounted() {
    this.initEditData();
    this.getCacheMenu();
  },
  methods: {
    close() {
      this.$emit('hideEditDialog');
    },
    initEditData() {
      if (this.isCreate !== 1) return;
      const { roleName, status, id } = this.editData;
      this.pram.roleName = roleName;
      this.pram.status = status;
      this.pram.id = id;
      const loading = this.$loading({
        lock: true,
        text: 'Loading',
      });
      const api = isPlatform ? roleApi.getInfo : areaApi.areasRoleInfoApi;
      api(id).then((res) => {
        this.menuOptions = res.menuList;
        this.checkDisabled(this.menuOptions);
        loading.close();
        this.getTreeId(res.menuList);
        this.$nextTick(() => {
          this.menuIds.forEach((i, n) => {
            var node = this.$refs.menu.getNode(i);
            if (node.isLeaf) {
              this.$refs.menu.setChecked(node, true);
            }
          });
        });
      });
    },
    handlerSubmit: Debounce(function (form) {
      this.$refs[form].validate((valid) => {
        if (!valid) return;
        let roles = this.getMenuAllCheckedKeys().toString();
        this.pram.rules = roles;
        if (this.isCreate === 0) {
          this.handlerSave();
        } else {
          this.handlerEdit();
        }
      });
    }),
    handlerSave() {
      const api = isPlatform ? roleApi.addRole : areaApi.areasRoleSaveApi;
      api(this.pram).then((data) => {
        this.$message.success('创建身份成功');
        this.$emit('hideEditDialog');
      });
    },
    handlerEdit() {
      const api = isPlatform ? roleApi.updateRole : areaApi.areasRoleUpdateApi;
      api(this.pram).then((data) => {
        this.$message.success('更新身份成功');
        this.$emit('hideEditDialog');
      });
    },
    rulesSelect(selectKeys) {
      this.pram.rules = selectKeys;
    },
    // 树权限（展开/折叠）
    handleCheckedTreeExpand(value, type) {
      if (type == 'menu') {
        let treeList = this.menuOptions;
        for (let i = 0; i < treeList.length; i++) {
          this.$refs.menu.store.nodesMap[treeList[i].id].expanded = value;
        }
      }
    },
    // 树权限（全选/全不选）
    handleCheckedTreeNodeAll(value, type) {
      if (type == 'menu') {
        this.$refs.menu.setCheckedNodes(value ? this.menuOptions : []);
      }
    },
    // 树权限（父子联动）
    handleCheckedTreeConnect(value, type) {
      if (type == 'menu') {
        this.menuCheckStrictly = value ? true : false;
      }
    },
    // 所有菜单节点数据
    getMenuAllCheckedKeys() {
      // 目前被选中的菜单节点
      let checkedKeys = this.$refs.menu.getCheckedKeys();
      // 半选中的菜单节点
      let halfCheckedKeys = this.$refs.menu.getHalfCheckedKeys();
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
      return checkedKeys;
    },
    async getCacheMenu() {
      if (this.isCreate !== 0) return;
      const loading = this.$loading({
        lock: true,
        text: 'Loading',
      });

      try {
        // 从Vuex store中获取用户信息
        let userInfo = isPlatform ? this.$store.state.user.userInfo : this.$store.state.user.circleUserInfo;

        // 如果store中没有用户信息，则重新获取
        if (!userInfo) {
          userInfo = await this.$store.dispatch('user/getInfo');
        }

        // 根据用户类型动态选择菜单API
        let menuApi;
        if (isPlatform) {
          // 平台用户使用原有API
          menuApi = roleApi.menuCacheList;
        } else {
          // 圈层用户根据type选择不同的API
          if (userInfo.type === 6) {
            menuApi = areaApi.areasMenuParentCacheTreeApi;
          } else if (userInfo.type === 8) {
            menuApi = areaApi.areasMenuNodeCacheTreeApi;
          } else {
            // 默认使用原有API
            menuApi = roleApi.menuCacheList;
          }
        }

        // 调用选定的菜单API
        const res = await menuApi();
        this.menuOptions = res;
        this.checkDisabled(this.menuOptions);
        loading.close();
      } catch (error) {
        console.error('获取菜单数据失败:', error);
        loading.close();
      }
    },
    getTreeId(datas) {
      for (var i in datas) {
        if (datas[i].checked) this.menuIds.push(datas[i].id);
        if (datas[i].childList) {
          this.getTreeId(datas[i].childList);
        }
      }
    },
    checkDisabled(data) {
      //设置公共权限默认勾选且不可操作
      let arr = isPlatform ? [9, 8, 10, 1, 1075, 1176, 1183, 1074] : [1217, 1220];
      data.forEach((item) => {
        // 圈层用户禁用部分公共权限 前面的小id 都是平台的，1176 是圈层非末梢层级的 1074 圈层首页 1075 圈层非末梢首页
        if (arr.includes(item.id)) {
          item.disabled = true;
          item.childList.forEach((item1) => {
            item1.disabled = true;
            this.$nextTick(() => {
              var node = this.$refs.menu.getNode(item1.id);
              if (node.isLeaf) {
                this.$refs.menu.setChecked(node, true);
              }
            });
          });
        }
      });
    },
  },
};
</script>

<style scoped></style>
