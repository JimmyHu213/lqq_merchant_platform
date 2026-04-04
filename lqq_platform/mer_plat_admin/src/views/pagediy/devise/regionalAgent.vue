<template>
  <div class="rightModel">
    <div slot="header" class="clearfix mb20">
      <div class="container" v-hasPermi="['platform:pagediy:list']">
        <el-form size="small" inline @submit.native.prevent>
          <el-form-item label="模板名称：">
            <el-input
              v-model.trim="name"
              placeholder="请输入模板名称"
              class="selWidth"
              clearable
              @keyup.enter.native="getList(1)"
            >
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="getList(1)">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="flex">
        <el-button
          type="primary"
          v-if="isShowAction && checkPermi(['platform:pagediy:save'])"
          @click="handlerEditDiyPage(0, 'add')"
          size="small"
          style="font-size: 12px"
          ><i class="el-icon-plus" style="margin-right: 4px" />添加</el-button
        >
        <el-button v-hasPermi="['platform:pagediy:list']" @click="getList(1)" size="small" style="font-size: 12px"
          >刷新</el-button
        >
        <div class="preview-text-box ml-14">
          <el-alert class="width-450 flex" title="若预览页面未加载出，请前往系统配置页面填写网站域名" type="info">
            <a class="to-page-btn ml-10 color-909399" @click="navigatorTo()">点击前往</a>
          </el-alert>
        </div>
      </div>
    </div>
    <el-table
      v-loading="listLoading"
      :data="tableData.data"
      size="small"
      class="table"
      @row-dblclick="handleName"
      @row-click="singleElection"
      row-key="id"
      highlight-current-row
      :cell-class-name="tableCellClassName"
    >
      <el-table-column v-if="!isShowAction" label="" width="30">
        <template slot-scope="scope">
          <el-radio class="radio" v-model="radioData" :label="scope.$index">&nbsp;</el-radio>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="ID" width="50" />
      <el-table-column label="模板名称" prop="name" min-width="180" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <div class="acea-row row-middle">
            <el-tag v-show="scope.row.isDefault === 1" effect="plain" class="mr5">首页</el-tag>
            <!--v-if去判断双击的是不是当前单元格-->
            <el-input
              @blur="hideInput(scope.row)"
              size="mini"
              maxlength="15"
              :ref="scope.row.index + ',' + scope.column.index"
              v-model="scope.row.name"
              v-if="
                scope.row.index + ',' + scope.column.index == currentCell && checkPermi(['platform:pagediy:updatename'])
              "
            >
            </el-input>
            <div v-else style="cursor: pointer" class="line1">{{ scope.row.name }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column v-if="isShowAction" prop="circleNames" label="圈层名称" min-width="230" />
      <el-table-column prop="addTime" label="添加时间" min-width="180" />
      <el-table-column prop="updateTime" label="更新时间" min-width="180" />
      <el-table-column v-if="isShowAction" label="操作" width="190" fixed="right">
        <template slot-scope="scope">
          <a
            v-hasPermi="['platform:pagediy:update', 'platform:pagediy:info']"
            @click="handlerEditDiyPage(scope.row.id, 'edit')"
            >设计</a
          >
          <el-divider direction="vertical"></el-divider>
          <template v-if="scope.row.isDefault !== 1 && checkPermi(['platform:pagediy:setdefault'])">
            <a @click="setHomepage(scope.row.id)">设为首页</a>
            <el-divider direction="vertical"></el-divider>
          </template>
          <el-dropdown trigger="click">
            <span class="el-dropdown-link"> 更多<i class="el-icon-arrow-down el-icon--right" /> </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                v-hasPermi="['platform:pagediy:save', 'platform:pagediy:info']"
                @click.native="handlerEditDiyPage(scope.row.id, 'copy')"
                >复制</el-dropdown-item
              >
              <el-dropdown-item
                v-if="scope.row.isDefault !== 1 && !scope.row.isMall && checkPermi(['platform:pagediy:delete'])"
                @click.native="handleDelete(scope.row.id)"
                >删除</el-dropdown-item
              >
              <el-dropdown-item @click.native="handlePreviewProtol(scope.row.id)">预览</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>
    <div class="block">
      <el-pagination
        background
        :page-sizes="[10, 20, 30]"
        :page-size="tableForm.limit"
        :current-page="tableForm.page"
        layout="total, sizes, prev, pager, next, jumper"
        :total="tableData.total"
        @size-change="handleSizeChange"
        @current-change="pageChange"
      />
    </div>
    <preview-dialog :perViewDia="perViewDia"></preview-dialog>
  </div>
</template>

<script>
import { checkPermi } from '@/utils/permission';
import PreviewDialog from '@/views/pagediy/devise/previewDialog';
import { pagediyDeleteApi, pagediyListApi, pagediySetdefaultApi, pagediyUpdatenameApi } from '@/api/devise';
import { handleDeleteTable } from '@/libs/public';
import useDeviseDiy from '@/views/pagediy/devise/config';
const { previewProtol, onEditDiyPage } = useDeviseDiy();
export default {
  name: 'regionalAgent',
  components: { PreviewDialog },
  props: {
    //是否展示操作列表
    isShowAction: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      templateId: 0,
      radioData: '',
      listLoading: false,
      tableData: {
        data: [],
        total: 0,
      },
      perViewDia: {
        // 列表二维码预览
        visible: false,
        perViewUrl: '',
      },
      tableForm: {
        page: 1,
        limit: 20,
        name: '',
      },
      name: '',
      // 用一个字符串来保存当前双击的是哪一个单元格
      currentCell: null,
    };
  },
  mounted() {
    this.frontDomain = `${this.$selfUtil.getFrontDomainUrl()}/pages/index/index`;
    if (checkPermi(['platform:pagediy:list'])) this.getList();
  },
  methods: {
    checkPermi,
    singleElection(row) {
      this.templateId = row.id;
      this.$emit('getTemplateId', this.templateId);
    },
    // 使用模板
    setHomepage(id) {
      this.$modalSure(`把该模板设为首页吗？`).then(async () => {
        await pagediySetdefaultApi(id);
        this.$message.success('操作成功');
        await this.getList();
      });
    },
    // 给单元格绑定横向和竖向的index，这样就能确定是哪一个单元格
    tableCellClassName({ row, column, rowIndex, columnIndex }) {
      row.index = rowIndex;
      column.index = columnIndex;
    },
    // 获得当前双击的单元格的横竖index，然后拼接成一个唯一字符串用于判断，并赋给currentCell
    // 拼接后类似这样："1,0","1,1",
    handleName(row, column) {
      this.currentCell = row.index + ',' + column.index;
      // 这里必须要setTimeout，因为在点击的时候，input才刚被v-if显示出来，不然拿不到dom
      setTimeout(() => {
        // 双击后自动获得焦点
        this.$refs[row.index + ',' + column.index].focus();
      });
    },
    // 当input失去焦点的时候，隐藏input
    hideInput(item) {
      if (!item.name) {
        return this.$message.warning('模板名称不能为空');
      } else {
        this.currentCell = null;
      }
      pagediyUpdatenameApi({
        id: item.id,
        name: item.name,
      }).then((res) => {
        this.$message.success('编辑成功');
      });
    },
    // 添加
    handlerEditDiyPage(id, type) {
      onEditDiyPage(id, type);
    },
    async getList(num) {
      this.listLoading = true;
      this.tableForm.page = num ? num : this.tableForm.page;
      this.tableForm.name = encodeURIComponent(this.name);
      try {
        const res = await pagediyListApi(this.tableForm);
        this.listLoading = false;
        this.tableData.data = res.list;
        this.tableData.total = res.total;
      } catch (e) {
        this.listLoading = false;
      }
    },
    // 删除
    handleDelete(id) {
      this.$modalSure('删除模板吗').then(async () => {
        await pagediyDeleteApi({ id: id });
        this.$message.success('删除成功');
        await handleDeleteTable(this.tableData.data.length, this.tableForm);
        await this.getList();
      });
    },
    handleSizeChange(val) {
      this.tableForm.limit = val;
      this.getList();
    },
    pageChange(val) {
      this.tableForm.page = val;
      this.getList();
    },
    //预览
    handlePreviewProtol(id) {
      this.perViewDia = previewProtol(id);
    },
    // 跳转域名配置
    navigatorTo() {
      this.$router.push('/operation/setting');
    },
  },
};
</script>

<style scoped lang="scss">
.preview-text-box {
  ::v-deep .el-alert {
    .el-alert__content {
      display: flex;
      align-items: baseline;
      .to-page-btn {
        font-weight: bold;
        font-size: 13px;
      }
    }
  }
}
.ml-14 {
  margin-left: 14px;
}
.ml-10 {
  margin-left: 10px;
}

.width-450 {
  width: 450px;
}

.color-909399 {
  color: #909399;
}
</style>
