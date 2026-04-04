<template>
  <div class="divBox relative">
    <el-card
      v-hasPermi="['merchant:user:page:list']"
      :bordered="false"
      shadow="never"
      class="ivu-mt"
      :body-style="{ padding: 0 }"
    >
      <div class="padding-add" ref="tableheader">
        <el-form
          inline
          size="small"
          :model="userFrom"
          ref="userFrom"
          label-width="66px"
          :label-position="labelPosition"
        >
          <div class="acea-row search-form" v-if="!collapse">
            <div class="search-form-box">
              <el-form-item label="用户搜索：" label-for="nickname">
                <UserSearchInput v-model="userFrom" />
              </el-form-item>
              <el-form-item label="入会时间：">
                <el-date-picker
                  v-model="timeVal"
                  align="right"
                  unlink-panels
                  value-format="yyyy-MM-dd"
                  format="yyyy-MM-dd"
                  size="small"
                  type="daterange"
                  placement="bottom-end"
                  range-separator="-"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  class="form_content_width"
                  :picker-options="pickerOptions"
                  @change="onchangeTime"
                />
              </el-form-item>
            </div>
            <el-form-item class="search-form-sub">
              <el-button type="primary" size="small" @click="handleSearchList">搜索</el-button>
              <el-button size="small" @click="handleReset('userFrom')" class="ResetSearch">重置</el-button>
              <a class="ivu-ml-8 font12 ml10" @click="collapse = !collapse">
                <template v-if="!collapse"> 展开 <i class="el-icon-arrow-down" /> </template>
                <template v-else> 收起 <i class="el-icon-arrow-up" /> </template>
              </a>
            </el-form-item>
          </div>
          <div v-if="collapse" class="acea-row search-form">
            <div class="search-form-box">
              <el-form-item label="用户搜索：" label-for="nickname">
                <UserSearchInput v-model="userFrom" />
              </el-form-item>
              <el-form-item label="性别：">
                <el-select v-model="userFrom.sex" placeholder="请选择" size="small" class="selWidth" clearable>
                  <el-option value="" label="全部"></el-option>
                  <el-option value="0" label="未知"></el-option>
                  <el-option value="1" label="男"></el-option>
                  <el-option value="2" label="女"></el-option>
                  <el-option value="3" label="保密"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="用户身份：">
                <el-select
                  v-model="userFrom.userIdentity"
                  placeholder="请选择"
                  @change="getList(1)"
                  clearable
                  class="form_content_width"
                >
                  <el-option v-for="item in userIdentityList" :key="item.value" :label="item.label" :value="item.value">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="会员等级：">
                <el-select
                  v-model="userFrom.memberLevel"
                  placeholder="请选择"
                  @change="getList(1)"
                  clearable
                  class="form_content_width"
                >
                  <el-option v-for="item in memberLevelList" :key="item.level" :label="item.name" :value="item.level">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="冻结状态：">
                <el-select
                  v-model="userFrom.financialStatus"
                  placeholder="请选择"
                  @change="getList(1)"
                  clearable
                  class="form_content_width"
                >
                  <el-option
                    v-for="item in financialStatusList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="入会时间：">
                <el-date-picker
                  v-model="timeVal"
                  align="right"
                  unlink-panels
                  value-format="yyyy-MM-dd"
                  format="yyyy-MM-dd"
                  size="small"
                  type="daterange"
                  placement="bottom-end"
                  range-separator="-"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  class="form_content_width"
                  :picker-options="pickerOptions"
                  @change="onchangeTime"
                />
              </el-form-item>
              <!--              <el-form-item label="注册类型：">-->
              <!--                <el-select-->
              <!--                    v-model="userFrom.registerType"-->
              <!--                    placeholder="请选择"-->
              <!--                    @change="getList(1)"-->
              <!--                    clearable-->
              <!--                    class="form_content_width"-->
              <!--                >-->
              <!--                  <el-option v-for="item in registerTypeList" :key="item.value" :label="item.label" :value="item.value">-->
              <!--                  </el-option>-->
              <!--                </el-select>-->
              <!--              </el-form-item>-->
            </div>
            <el-form-item class="search-form-sub">
              <el-button type="primary" size="small" @click="handleSearchList">搜索</el-button>
              <el-button size="small" @click="handleReset('userFrom')" class="ResetSearch">重置</el-button>
              <a class="ivu-ml-8 font12 ml10" @click="collapse = !collapse">
                <template v-if="!collapse"> 展开 <i class="el-icon-arrow-down" /> </template>
                <template v-else> 收起 <i class="el-icon-arrow-up" /> </template>
              </a>
            </el-form-item>
          </div>
        </el-form>
      </div>
    </el-card>
<!--    :height="tableHeight"-->
    <base-table
      class="mt14"
      ref="table"
      :loading="listLoading"
      :table-data="tableData"
      :columns="tableColumns"
      :pagination="{
        pageNum: userFrom.page,
        pageSize: userFrom.limit,
      }"
      @size-change="handleSizeChange"
      @current-change="pageChange"
    >
      <!-- 昵称插槽 -->
      <template #nickname="{ row }">
        <user-info :row="row"></user-info>
      </template>
      <template #recharge="{ row }">
        <div>本金：{{ row.rechargeAmount }}</div>
        <div>赠送：{{ row.giftAmount }}</div>
      </template>
      <template #financialStatus="{ row }">
        <span class="tag-padding" :class="row.financialStatus ? 'primaryTag' : 'notStartTag'">{{
          row.financialStatus ? '正常' : '冻结'
        }}</span>
      </template>
      <!-- 操作插槽 -->
      <template #operate="{ row }">
        <a v-if="checkPermi(['merchant:user:detail'])" @click="onDetails(row.uid)">详情</a>
        <el-divider direction="vertical"></el-divider>
        <a v-if="checkPermi(['merchant:user:shopping:credits:freeze:update'])" @click="handleShoppingCredit(row)">{{
          `${row.financialStatus}` == 0 ? '解冻购物金' : '冻结购物金'
        }}</a>
<!--        <el-divider direction="vertical"></el-divider>-->
<!--        <a v-hasPermi="['platform:coupon:can:send:list', 'platform:coupon:batch:send']" @click="sendCoupon">发送券</a>-->
      </template>
    </base-table>
    <!--用户详情-->
    <detail-user ref="userDetailFrom"></detail-user>
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

import { shoppingCreditsFreezeApi, userListApi } from '@/api/user';
import userList from '@/components/userList';
import detailUser from './userDetails.vue';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { tableColumns } from './default';
import { Debounce } from '@/utils/validate';
import { memberLevelListApi } from '@/api/member';
import userInfo from '@/components/userInfo';
export default {
  name: 'UserIndex',
  components: { detailUser, userInfo },
  filters: {
    sexFilter(status) {
      const statusMap = {
        0: '未知',
        1: '男',
        2: '女',
        3: '保密',
      };
      return statusMap[status];
    },
    typeFilter(value) {
      const statusMap = {
        facebook: 'Facebook',
        twitter: 'Twitter',
        google: 'Google',
        email: 'Email',
        phone: 'Phone',
        visitor: '游客',
      };
      return statusMap[value];
    },
  },
  data() {
    return {
      tableColumns,
      userIdentityList: [
        {
          value: 'member',
          label: '会员用户',
        },
        {
          value: 'fans',
          label: '粉丝用户',
        },
        {
          value: 'ordinary',
          label: '普通用户',
        },
      ],
      registerTypeList: [
        {
          value: 'wechat',
          label: '公众号',
        },
        {
          value: 'routine',
          label: '小程序',
        },
        {
          value: 'h5',
          label: 'H5',
        },
        {
          value: 'iosWx',
          label: '微信ios',
        },
        {
          value: 'androidWx',
          label: '微信安卓',
        },
        {
          value: 'ios',
          label: 'ios',
        },
      ],
      financialStatusList: [
        {
          value: '1',
          label: '正常',
        },
        {
          value: '0',
          label: '冻结',
        },
      ],
      tableHeight: 0,
      formExtension: {
        image: '',
        spreadUid: '',
        userId: '',
      },
      ruleInline: {},
      extensionVisible: false,
      userVisible: false,
      levelInfo: '',
      pickerOptions: this.$timeOptions,
      loadingBtn: false,
      PointValidateForm: {
        integralType: 2,
        integralValue: 0,
        moneyType: 2,
        moneyValue: 0,
        uid: '',
      },
      loadingPoint: false,
      VisiblePoint: false,
      visible: false,
      userIds: '',
      dialogVisible: false,
      levelVisible: false,
      labelPosition: 'right',
      collapse: false,
      props: {
        children: 'child',
        label: 'name',
        value: 'name',
        emitPath: false,
      },
      propsCity: {
        children: 'child',
        label: 'name',
        value: 'name',
      },
      listLoading: false,
      tableData: {
        data: [],
        total: 0,
      },
      userFrom: {
        sex: '',
        membershipTime: '',
        page: 1,
        limit: this.$constants.page.limit[0],
        searchType: 'all',
        content: '',
        userIdentity: '',
        financialStatus: '',
        memberLevel: '',
      },
      grid: {
        xl: 8,
        lg: 12,
        md: 12,
        sm: 24,
        xs: 24,
      },
      levelList: [],
      labelLists: [],
      groupList: [],
      selectedData: [],
      timeVal: [],
      dynamicValidateForm: {
        groupId: [],
      },
      loading: false,
      groupIdFrom: [],
      selectionList: [],
      batchName: '',
      uid: 0,
      Visible: false,
      keyNum: 0,
      address: [],
      multipleSelectionAll: [],
      idKey: 'uid',
      checkAll: false,
      isIndeterminate: true,
      memberLevelList: [],
    };
  },
  created() {
    // 浏览器高度
    let windowHeight = document.documentElement.clientHeight || document.body.clientHeight;

    // 此处减去100即为当前屏幕内除了表格高度以外其它内容的总高度，
    // this.tableHeight = windowHeight - 388;
  },
  activated() {
    if (checkPermi(['merchant:user:page:list'])) this.getList(1);
  },
  mounted() {
    if (checkPermi(['merchant:user:page:list'])) this.getList();
    this.$nextTick(() => {
      let tableHeader = this.$refs.tableheader.offsetHeight;
      this.tableHeight = this.$selfUtil.getTableHeight(tableHeader + 100);
    });
    this.getMemberLevel();
  },
  methods: {
    checkPermi,
    //解冻 冻结购物金
    handleShoppingCredit(row) {
      try {
        this.$modalSure(
          row.financialStatus == 0 ? `您确定要解冻该用户的购物金吗？` : `您确定要冻结该用户的购物金吗？`,
        ).then(async () => {
          await shoppingCreditsFreezeApi(row.uid);
          this.getList(1);
          this.$message.success(row.financialStatus == 0 ? '解冻成功' : '冻结成功');
        });
      } catch (e) {}
    },
    //性别
    getSexImage(sex) {
      const iconMap = {
        0: 'unknown.png',
        1: 'man.png',
        2: 'woman.png',
        3: 'unknown.png',
      };
      const imageName = iconMap[sex] || 'unknown.png';
      return require(`@/assets/imgs/${imageName}`);
    },
    sexFilter(status) {
      const statusMap = {
        0: '未知',
        1: '男',
        2: '女',
        3: '保密',
      };
      return statusMap[status];
    },
    // 会员等级
    async getMemberLevel() {
      this.memberLevelList = await memberLevelListApi();
    },
    filterRegisterType(status) {
      const statusMap = {
        wechat: '#FD5ACC',
        routine: '#A277FF',
        h5: '#E8B600',
        iosWx: '#1BBE6B',
        androidWx: '#EF9C20',
        ios: '#1890FF',
      };
      return statusMap[status];
    },
    onCollapse() {
      this.collapse = !this.collapse;
      this.$nextTick(() => {
        let tableHeader = this.$refs.tableheader.offsetHeight;
        this.tableHeight = this.$selfUtil.getTableHeight(tableHeader + 150);
      });
    },
    getTemplateRow(row) {
      this.formExtension.image = row.avatar;
      this.formExtension.spreadUid = row.uid;
    },
    handleCloseExtension() {
      this.extensionVisible = false;
    },
    modalPicTap() {
      this.userVisible = true;
    },
    resetForm() {
      this.visible = false;
    },
    handleReset() {
      const { limit } = this.userFrom;
      this.userFrom = { ...this.$options.data.call(this).userFrom, limit };
      this.timeVal = [];
      this.getList();
    },
    // 发送文章
    sendNews() {
      if (this.selectionList.length === 0) return this.$message.warning('请先选择用户');
      const _this = this;
      this.$modalArticle(function (row) {}, 'send');
    },
    // 发送优惠劵
    sendCoupon() {
    //  if (this.selectionList.length === 0) return this.$message.warning('请选择要设置的用户');
      const _this = this;
      this.$modalCoupon(
        'send',
        (this.keyNum += 5),
        [],
        function (row) {
          _this.formValidate.give_coupon_ids = [];
          _this.couponData = [];
          row.map((item) => {
            _this.formValidate.give_coupon_ids.push(item.coupon_id);
            _this.couponData.push(item.title);
          });
          _this.selectionList = [];
        },
        this.userIds,
        'user',
      );
    },
    Close() {
      this.Visible = false;
      this.levelVisible = false;
    },
    // 账户详情
    onDetails(id) {
      this.$refs.userDetailFrom.getUserDetail(id);
      if (checkPermi(['merchant:user:shopping:credits:record'])) this.$refs.userDetailFrom.getShoppingCreditsRecord(id);
      this.$refs.userDetailFrom.dialogUserDetail = true;
    },
    // 积分余额
    handlePointClose() {
      this.VisiblePoint = false;
      this.PointValidateForm = {
        integralType: 2,
        integralValue: 0,
        moneyType: 2,
        moneyValue: 0,
        uid: '',
      };
    },
    handleClose() {
      this.dialogVisible = false;
      this.$refs['dynamicValidateForm'].resetFields();
    },
    // 搜索
    handleSearchList() {
      this.userFrom.page = 1;
      this.getList();
    },
    // 具体日期
    onchangeTime(e) {
      this.timeVal = e;
      this.userFrom.membershipTime = e ? this.timeVal.join(',') : '';
    },
    // 列表
    getList(num) {
      this.listLoading = true;
      this.userFrom.page = num ? num : this.userFrom.page;
      userListApi(this.userFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.listLoading = false;
        })
        .catch(() => {
          this.listLoading = false;
        });
      this.checkedCities = this.$cache.local.has('user_stroge')
        ? this.$cache.local.getJSON('user_stroge')
        : this.checkedCities;
    },
    pageChange(page) {
      this.$selfUtil.changePageCoreRecordData(
        this.multipleSelectionAll,
        this.multipleSelection,
        this.tableData.data,
        (e) => {
          this.multipleSelectionAll = e;
        },
      );
      this.userFrom.page = page;
      this.getList();
    },
    handleSizeChange(val) {
      this.$selfUtil.changePageCoreRecordData(
        this.multipleSelectionAll,
        this.multipleSelection,
        this.tableData.data,
        (e) => {
          this.multipleSelectionAll = e;
        },
      );
      this.userFrom.limit = val;
      this.getList();
    },
    handleCheckAllChange(val) {
      this.checkedCities = val ? this.columnData : [];
      this.isIndeterminate = false;
    },
    handleCheckedCitiesChange(value) {
      let checkedCount = value.length;
      this.checkAll = checkedCount === this.columnData.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.columnData.length;
    },
  },
};
</script>

<style scoped lang="scss">
::v-deep .el-table__cell:nth-child(2) .cell {
  padding-left: 14px;
  padding-right: 14px;
}
.search-form {
  display: flex;
  justify-content: space-between;
  .search-form-box {
    display: flex;
    flex-wrap: wrap;
    flex: 1;
  }
}
.search-form-sub {
  display: flex;
}
.red {
  color: #ed4014;
}
.color4073FA {
  color: #4073fa;
}
.el-icon-arrow-down {
  font-size: 12px;
}
.text-right {
  text-align: right;
}
.demo-table-expand {
  font-size: 0;
}
.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 33.33%;
}
.seachTiele {
  line-height: 30px;
}
.container {
  min-width: 821px;
  ::v-deep .el-form-item {
    width: 100%;
  }
  ::v-deep .el-form-item__content {
    width: 72%;
  }
}
.btn_bt {
  border-top: 1px dashed #ccc;
  padding-top: 20px;
}
.relative {
  position: relative;
}
.card_abs {
  position: absolute;
  padding-bottom: 15px;
  right: 40px;
  width: 200px;
  background: #fff;
  z-index: 99999;
  box-shadow: 0px 0px 14px 0px rgba(0, 0, 0, 0.1);
}
.cell_ht {
  height: 50px;
  padding: 15px 20px;
  box-sizing: border-box;
  border-bottom: 1px solid #eeeeee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.check_cell {
  width: 100%;
  padding: 15px 20px 0;
}
::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
  color: #606266;
}
.userbtn {
  position: absolute;
  right: 0;
}
::v-deep.el-tag {
  color: #fff !important;
}
</style>
