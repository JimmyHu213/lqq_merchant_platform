<template>
  <div class="base-table-container">
    <el-card
      class="box-card"
      :body-style="{ padding: '0 20px 20px', position: 'relative' }"
      shadow="never"
      :bordered="false"
    >
      <div class="clearfix" ref="headerBox">
        <!-- 标签页 -->
        <el-tabs v-if="tabs && tabs.length > 0" v-model="activeTabNew" @tab-click="handleTabClick" class="list-tabs mb5">
          <el-tab-pane v-for="(tab, index) in tabs" :key="index" :label="tab.label" :name="tab.name">
            <slot :name="'tab-' + tab.name" :tab="tab"></slot>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 自定义插槽：位于标签页和表格之间 -->
      <slot name="table-header"></slot>

      <el-table
        ref="table"
        v-loading="loading"
        :data="tableData.data"
        style="width: 100%"
        :size="size"
        class="mt20"
        :height="height"
        :row-key="rowKey"
        :highlight-current-row="highlightCurrentRow"
        @selection-change="handleSelectionChange"
        @row-click="handleRowClick"
        @row-dblclick="handleRowDblClick"
        v-bind="tableProps"
      >
        <!-- 多选列 -->
        <el-table-column v-if="showSelection" type="selection" :width="selectionWidth" :selectable="selectable" />

        <!-- 展开列 -->
        <el-table-column v-if="expandRow" type="expand" :width="expandWidth">
          <template slot-scope="props">
            <slot name="expand" :row="props.row"></slot>
          </template>
        </el-table-column>

        <!-- 自定义列 -->
        <template v-for="(column, index) in columns">
          <template v-if="column.type === 'expand'">
            <el-table-column :type="column.type" :width="column.width" :fixed="column.fixed">
              <template slot-scope="props">
                <slot :name="column.slotName" :row="props.row"></slot>
              </template>
            </el-table-column>
          </template>
          <template v-else-if="column.type === 'selection'">
            <el-table-column
              :type="column.type"
              :width="column.width"
              :fixed="column.fixed"
              :selectable="column.selectable"
            />
          </template>
          <template v-else-if="column.type === 'image'">
            <el-table-column :prop="column.prop" width="60">
              <template slot-scope="scope">
                <div class="demo-image__preview line-heightOne">
                  <el-image :src="scope.row[column.prop]" :preview-src-list="[scope.row[column.prop]]" />
                </div>
              </template>
            </el-table-column>
          </template>
          <template v-else-if="column.type === 'switch'">
            <el-table-column
              :prop="column.prop"
              :label="column.label"
              :width="column.width"
              :min-width="column.minWidth"
              :fixed="column.fixed"
            >
              <template slot-scope="scope"
                >
                <el-switch
                  v-if="checkPermi([column.permission])"
                  v-model="scope.row[column.prop]"
                  :active-value="column.activeValue || true"
                  :inactive-value="column.inactiveValue || false"
                  :disabled="column.disabled || false"
                  :active-text="column.activeText || '显示'"
                  :inactive-text="column.inactiveText || '隐藏'"
                  @change="(value) => handleSwitchChange(scope.row, value, column)"
                />
                <div v-else>
                  {{ scope.row[column.prop] ? column.activeText || '显示' : column.inactiveText || '隐藏' }}
                </div>
              </template>
            </el-table-column>
          </template>
          <template v-else>
            <el-table-column
              :prop="column.prop"
              :label="column.label"
              :width="column.width"
              :min-width="column.minWidth"
              :fixed="column.fixed"
              :align="column.align"
              :header-align="column.headerAlign"
              :sortable="column.sortable"
              :resizable="column.resizable"
              :show-overflow-tooltip="column.showOverflowTooltip"
              :formatter="column.formatter"
            >
              <template slot-scope="props">
                <slot
                  v-if="column.slotName"
                  :name="column.slotName"
                  :row="props.row"
                  :index="props.$index"
                  :column="column"
                ></slot>
                <template v-else>
                  {{
                    column.formatter
                      ? column.formatter(props.row, props.column, props.row[column.prop])
                      : props.row[column.prop]
                  }}
                </template>
              </template>
            </el-table-column>
          </template>
        </template>
      </el-table>

      <!-- 分页器 -->
      <div class="pagination-container" v-if="showPagination">
        <el-pagination
          background
          :page-sizes="pageSizes"
          :page-size="pagination.pageSize"
          :current-page="pagination.pageNum"
          :layout="paginationLayout"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import * as $constants from '@/utils/constants';
import { checkPermi } from '@/utils/permission';

export default {
  name: 'BaseTable',
  props: {
    // 表格数据
    tableData: {
      type: Object,
      default: () => ({
        data: [],
        total: 0,
      }),
    },
    // 表格列配置
    columns: {
      type: Array,
      default: () => [],
    },
    // 加载状态
    loading: {
      type: Boolean,
      default: false,
    },
    // 表格尺寸
    size: {
      type: String,
      default: 'mini',
      validator: (value) => ['medium', 'small', 'mini'].indexOf(value) !== -1,
    },
    // 表格高度
    height: {
      type: [String, Number],
      default: 'auto',
    },
    // 行标识
    rowKey: {
      type: String,
      default: 'id',
    },
    // 是否高亮当前行
    highlightCurrentRow: {
      type: Boolean,
      default: true,
    },
    // 是否显示多选
    showSelection: {
      type: Boolean,
      default: false,
    },
    // 多选列宽度
    selectionWidth: {
      type: [String, Number],
      default: 50,
    },
    // 是否显示展开列
    expandRow: {
      type: Boolean,
      default: false,
    },
    // 展开列宽度
    expandWidth: {
      type: [String, Number],
      default: 40,
    },
    // 是否显示分页
    showPagination: {
      type: Boolean,
      default: true,
    },
    // 分页配置
    pagination: {
      type: Object,
      default: () => ({
        pageNum: 1,
        pageSize: 10,
      }),
    },
    // 分页器布局
    paginationLayout: {
      type: String,
      default: 'total, sizes, prev, pager, next, jumper',
    },
    // 每页大小选项
    pageSizes: {
      type: Array,
      default: () => $constants.page.limit,
    },
    // 自定义表格属性
    tableProps: {
      type: Object,
      default: () => ({}),
    },
    // 多选过滤函数
    selectable: {
      type: Function,
      default: () => true,
    },
    // 标签页配置
    tabs: {
      type: Array,
      default: () => [],
    },
    // 当前激活的标签页
    activeTab: {
      type: [String, Number],
      default: '',
    },
    // 标签页类型
    tabType: {
      type: String,
      default: 'card',
      validator: (value) => ['card', 'border-card'].indexOf(value) !== -1,
    },
    // 标签页是否可关闭
    tabClosable: {
      type: Boolean,
      default: false,
    },
    // 标签页是否可添加
    tabAddable: {
      type: Boolean,
      default: false,
    },
    checkedList:{
      type: Array,
      default: () => [],
    }
  },
  watch: {
    activeTab: {
      handler(newVal) {
        this.activeTabNew = newVal ;
      },
      deep: true,
    },
    'tableData.data': {
      handler(val) {
        this.$nextTick(() => {
          this.setSelectRow();
        //  this.setSelection();
        });
      },
      deep: true,
    },
  },
  data(){
    return{
      activeTabNew: this.activeTab,
      multipleSelection: [],
      multipleSelectionAll: [],
      idKey: 'id',
    }
  },
  emits: [
    'selection-change',
    'row-click',
    'row-dblclick',
    'size-change',
    'current-change',
    'tab-click',
    'tab-remove',
    'tab-add',
    'switch-change',
  ],
  mounted() {
    if (!this.checkedList) return;
    this.checkedList.forEach((row) => {
      this.$refs.table.toggleRowSelection(row);
    });
  },
  methods: {
    checkPermi,
    // 处理选择变更
    handleSelectionChange(selection) {
      this.multipleSelection = selection;
      setTimeout(() => {
        this.$selfUtil.changePageCoreRecordData(
            this.multipleSelectionAll,
            this.multipleSelection,
            this.tableData.data,
            (e) => {
              this.multipleSelectionAll = e;
              this.$emit('selection-change', this.multipleSelectionAll);
            },
        );
      }, 50);

    },
    // 设置选中的方法
    setSelectRow() {
      if (!this.multipleSelectionAll || this.multipleSelectionAll.length <= 0) {
        return;
      }
      // 标识当前行的唯一键的名称
      const idKey = this.idKey;
      const selectAllIds = [];
      this.multipleSelectionAll.forEach((row) => {
        selectAllIds.push(row[idKey]);
      });
      this.$refs.table.clearSelection();
      for (var i = 0; i < this.tableData.data.length; i++) {
        if (selectAllIds.indexOf(this.tableData.data[i][idKey]) >= 0) {
          // 设置选中，记住table组件需要使用ref="table"
          this.$refs.table.toggleRowSelection(this.tableData.data[i], true);
        }
      }
    },
    // 处理行点击
    handleRowClick(row, column, event) {
      this.$emit('row-click', row, column, event);
    },
    // 处理行双击
    handleRowDblClick(row, column, event) {
      this.$emit('row-dblclick', row, column, event);
    },
    // 处理每页大小变更
    handleSizeChange(size) {
      this.$selfUtil.changePageCoreRecordData(
          this.multipleSelectionAll,
          this.multipleSelection,
          this.tableData.data,
          (e) => {
            this.multipleSelectionAll = e;
          },
      );
      this.$emit('size-change', size);
    },
    // 处理页码变更
    handleCurrentChange(current) {
      this.$selfUtil.changePageCoreRecordData(
          this.multipleSelectionAll,
          this.multipleSelection,
          this.tableData.data,
          (e) => {
            this.multipleSelectionAll = e;
          },
      );
      this.$emit('current-change', current);
    },
    // 清空选择
    clearSelection() {
      this.$refs.table.clearSelection();
    },
    // 切换行选中状态
    toggleRowSelection(row, selected) {
      this.$refs.table.toggleRowSelection(row, selected);
    },
    // 切换全选状态
    toggleAllSelection() {
      this.$refs.table.toggleAllSelection();
    },
    // 设置当前行
    setCurrentRow(row) {
      this.$refs.table.setCurrentRow(row);
    },
    // 获取表格引用
    getTableRef() {
      return this.$refs.table;
    },

    // 处理标签页点击
    handleTabClick(tab, event) {
      this.$emit('tab-click', this.activeTabNew);
    },

    // 处理标签页移除
    handleTabRemove(tabName) {
      this.$emit('tab-remove', tabName);
    },

    // 处理标签页添加
    handleTabAdd() {
      this.$emit('tab-add');
    },
    // 处理开关状态变化
    handleSwitchChange(row, value, column) {
       this.$emit('switch-change', row);
    },
    // 回显选中状态
    setSelection() {
      if (!this.checkedList || this.checkedList.length === 0) return;
      if (!this.tableData.data || this.tableData.data.length === 0) return;

      this.$nextTick(() => {
        this.tableData.data.forEach((row) => {
          const key = this.rowKey || 'id';
          const rowId = row[key];
          const isChecked = this.multipleSelection.some((checkedItem) => {
            if (typeof checkedItem === 'object' && checkedItem !== null) {
              return checkedItem[key] === rowId;
            } else {
              return checkedItem === rowId;
            }
          });
          if (isChecked) {
            this.$refs.table.toggleRowSelection(row, true);
          }
        });
      });
    },
  },
};
</script>

<style scoped lang="scss">
::v-deep .el-table__body-wrapper {
  height: auto !important;
}
.base-table-container {
  width: 100%;
  .pagination-container {
    margin-top: 15px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
