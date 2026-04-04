<template>
  <div class="divBox relative">
    <el-card class="box-card mt14" :body-style="{ padding: '20px' }" shadow="never" :bordered="false">
      <div class="acea-row row-middle">
        <el-button size="small" type="primary" @click="handleAdd()" v-hasPermi="['platform:circle:save']"
          >新增圈层</el-button
        >
        <el-button
          size="small"
          :disabled="!checkedIds.length"
          @click.native="handleAssociation()"
          v-if="checkPermi(['platform:circle:conjunctive:diy'])"
          >批量关联微页面</el-button
        >
      </div>

      <!-- 圈层列表表格 -->
      <el-table
        v-loading="listLoading"
        :data="tableData"
        style="width: 100%"
        size="small"
        class="mt20"
        row-key="id"
        @selection-change="handleSelectionChange"
        :tree-props="{ children: 'childList', hasChildren: 'hasChildren' }"
      >
        <el-table-column key="2" type="selection" width="55" />
        <el-table-column type="index" label="ID" width="80" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="圈层名称" min-width="200" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span>{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="管理员" min-width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.realName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系方式" min-width="150">
          <template slot-scope="scope">
            <span>{{ scope.row.phone }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="range" label="商户范围" min-width="200" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span>{{ scope.row.range || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="diyName" label="关联微页面" min-width="150" />
        <el-table-column label="圈层状态" min-width="100">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="true"
              active-text="启用"
              inactive-text="禁用"
              :inactive-value="false"
              @change="onchangeIsShow(scope.row, 'status')"
              size="mini"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="是否显示" min-width="100">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.isShow"
              :active-value="true"
              active-text="显示"
              inactive-text="隐藏"
              :inactive-value="false"
              @change="onchangeIsShow(scope.row, 'show')"
              size="mini"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <a
              v-if="scope.row.level < 3"
              @click="handleAdd(scope.row.id)"
              v-hasPermi="['platform:circle:save']"
              :disabled="scope.row.level == scope.row.chainLength"
              >新增下级</a
            >
            <el-divider v-if="scope.row.level < 3" direction="vertical"></el-divider>
            <a @click="handleView(scope.row)" v-hasPermi="['platform:circle:update']">详情</a>
            <el-divider direction="vertical"></el-divider>

            <el-dropdown>
              <span class="el-dropdown-link">更多<i class="el-icon-arrow-down el-icon--right"></i></span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  @click.native="handleEdit(scope.row)"
                  v-if="checkPermi(['platform:circle:update'])"
                  :disabled="scope.row.status"
                  >编辑</el-dropdown-item
                >
                <el-dropdown-item
                  @click.native="handleResetPassword(scope.row)"
                  v-if="checkPermi(['platform:circle:update'])"
                  >重置密码</el-dropdown-item
                >
                <el-dropdown-item
                  @click.native="handleBindMerchant(scope.row)"
                  v-if="checkPermi(['platform:circle:bind'])"
                  >归属商户</el-dropdown-item
                >
                <el-dropdown-item
                  @click.native="handleAssociation(scope.row)"
                  v-if="checkPermi(['platform:circle:conjunctive:diy'])"
                  >关联微页面</el-dropdown-item
                >
                <el-dropdown-item
                  @click.native="handleDelete(scope.row.id)"
                  v-if="checkPermi(['platform:circle:delete'])"
                  >删除</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <template-dialog
      :AssociationDia="AssociationDia"
      @handleClose="handleClose"
      @changeList="getList"
    ></template-dialog>
  </div>
</template>

<script>
import { areaListTreeApi, areaDeleteApi, areaResetPasswordApi, areaStatusApi, areaIsShowApi } from '@/api/area';
import '@riophae/vue-treeselect/dist/vue-treeselect.css';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import TemplateDialog from '@/views/area/areamenu/templateDialog';
import { selectionCheckedIds } from '@/utils/ZBKJIutil';

export default {
  name: 'AreaIndex',
  components: { TemplateDialog },
  data() {
    return {
      collapse: false,
      listLoading: true,
      tableData: [],
      AssociationDia: {
        visible: false,
        ids: null,
      },
      checkedIds: [],
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    checkPermi,

    handleSelectionChange(val) {
      this.checkedIds = selectionCheckedIds(val);
    },
    handleClose() {
      this.AssociationDia.visible = false;
    },
    // 关联模板
    handleAssociation(row) {
      if (!row && !this.checkedIds.length) this.$message.warning('请先选择商圈');
      this.AssociationDia = {
        ids: row ? row.id : this.checkedIds.join(','),
        visible: true,
      }
    },
    // 获取树形列表
    getList() {
      this.listLoading = true;
      areaListTreeApi()
        .then((res) => {
          // 根据实际返回的数据结构进行处理
          // 如果返回的是分页数据结构
          this.tableData = res;
          this.listLoading = false;
        })
        .catch((error) => {
          console.error('获取圈层列表失败:', error);
          this.listLoading = false;
        });
    },

    // 重置查询
    resetQuery() {
      this.queryParams = {
        name: '',
        realName: '',
        status: '',
        parentId: '',
        page: 1,
        limit: 20,
      };
      this.getList();
    },
    // 新增圈层

    // 新增圈层
    handleAdd(parentId = 0) {
      if (parentId === 0) {
        // 从顶部按钮进入，新增顶级圈层
        this.$router.push('/area/create');
      } else {
        // 从某一行进入，新增子圈层
        this.$router.push(`/area/create?parentId=${parentId}`);
      }
    },
    // 编辑圈层
    handleEdit(row) {
      this.$router.push(`/area/create/${row.id}`);
    },
    // 查看圈层详情
    handleView(row) {
      this.$router.push(`/area/create/${row.id}?mode=view`);
    },
    // 删除圈层
    handleDelete(id) {
      this.$confirm('确定删除该圈层吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        areaDeleteApi(id).then(() => {
          this.$message.success('删除成功');
          this.getList();
        });
      });
    },
    // 状态切换
    onchangeIsShow(row, type) {
      if (!row.status && type === 'show') {
        this.$message.error('该圈层已被禁用，无法显示');
        row.isShow = !row.isShow;
        return;
      }
      let api = type === 'show' ? areaIsShowApi : areaStatusApi;
      api(row.id)
        .then(() => {
          if (type === 'status' && !row.status) {
            row.isShow = false;
          }
          this.$message.success(`${type === 'show' ? '显示' : '状态'}更新成功`);
          this.getList();
        })
        .catch(() => {
          if (type === 'show') {
            row.isShow = !row.isShow;
          } else {
            row.status = !row.status;
          }
        });
    },
    // 重置密码
    handleResetPassword(row) {
      this.$confirm(`确定重置该管理员密码为 000000 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          areaResetPasswordApi(row.id)
            .then((res) => {
              this.$message.success('密码重置成功');
              // 可以在这里显示新密码或其他提示信息
              if (res.data && res.data.password) {
                this.$alert(`新密码为：${res.data.password}`, '密码重置成功', {
                  confirmButtonText: '确定',
                  type: 'success',
                });
              }
            })
            .catch((error) => {
              console.error('重置密码失败:', error);
              this.$message.error('重置密码失败');
            });
        })
        .catch(() => {
          this.$message.info('已取消重置密码');
        });
    },
    // 绑定商户 - 跳转到编辑页面的归属商户tab
    handleBindMerchant(row) {
      this.$router.push(`/area/create/${row.id}?tab=2`);
    },
    // 导出
    handleExport() {
      this.$message.info('导出功能待实现');
    },
    // Treeselect normalizer
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children,
      };
    },
  },
};
</script>

<style scoped lang="scss">
::v-deep .el-table__header{
  .el-checkbox__input{
    display: none;
  }
}
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.node-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.node-label {
  font-weight: 500;
}

.node-code {
  color: #909399;
  font-size: 12px;
}

.node-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.node-actions .el-button {
  padding: 0;
  margin: 0;
}
</style>
