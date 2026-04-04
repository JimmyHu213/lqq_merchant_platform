<template>
  <div>
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form :inline="inline" :size="size" :label-position="labelPosition" @submit.native.prevent>
          <el-form-item v-for="(item, index) in formItems" :key="index" :label="item.label" :class="item.className">
            <!-- 文本输入框 -->
            <el-input
                v-if="item.type === 'input'"
                v-model.trim="validateProps[item.prop]"
                :placeholder="item.placeholder || `请输入${item.label}`"
                :class="item.class || ''"
                :size="size"
                :clearable="item.clearable !== false"
                @keyup.enter.native="handleSearch"
                v-bind="item.attrs || {}"
            ></el-input>

            <!-- 下拉选择框 -->
            <el-select
                v-else-if="item.type === 'select'"
                v-model="validateProps[item.prop]"
                :placeholder="item.placeholder || `请选择${item.label}`"
                :class="item.class || ''"
                :size="size"
                :clearable="item.clearable !== false"
                @change="item.change ? item.change(validateProps[item.prop]) : handleSearch"
                v-bind="item.attrs || {}"
            >
              <el-option
                  v-for="option in item.options"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
              ></el-option>
            </el-select>

            <!-- 级联选择器 -->
            <el-cascader
                v-else-if="item.type === 'cascader'"
                v-model="validateProps[item.prop]"
                :options="item.options"
                :props="item.props"
                :placeholder="item.placeholder || `请选择${item.label}`"
                :class="item.class || ''"
                :size="size"
                :clearable="item.clearable !== false"
                @change="item.change ? item.change(validateProps[item.prop]) : handleSearch"
                v-bind="item.attrs || {}"
            ></el-cascader>

            <!-- 日期选择器 -->
            <el-date-picker
                v-else-if="item.type === 'date'"
                v-model="validateProps[item.prop]"
                value-format="yyyy-MM-dd"
                format="yyyy-MM-dd"
                :type="item.dateType || 'date'"
                :placeholder="item.placeholder || `请选择${item.label}`"
                :class="item.class || ''"
                :size="size"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                :clearable="item.clearable !== false"
                @change="item.change ? item.change(validateProps[item.prop]) : handleSearch"
                v-bind="item.attrs || {}"
            ></el-date-picker>

            <!-- 用户搜索 -->
            <UserSearchInput v-else-if="item.type === 'user'" v-model="validateProps" />

            <!-- 自定义内容 -->
            <template v-else-if="item.type === 'slot'">
              <slot :name="item.prop"></slot>
            </template>
          </el-form-item>

          <!-- 操作按钮 -->
          <el-form-item v-if="showButtons !== false">
            <el-button type="primary" :size="size" @click="handleSearch">查询</el-button>
            <el-button :size="size" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'BaseSearchForm',
  props: {
    // 表单字段配置
    formItems: {
      type: Array,
      required: true,
      default: () => [],
    },
    // 表单数据
    formData: {
      type: Object,
      required: true,
      default: () => ({}),
    },
    // 是否内联表单
    inline: {
      type: Boolean,
      default: true,
    },
    // 表单大小
    size: {
      type: String,
      default: 'small',
      validator: (value) => ['large', 'medium', 'small', 'mini'].includes(value),
    },
    // 标签位置
    labelPosition: {
      type: String,
      default: 'right',
      validator: (value) => ['left', 'right', 'top'].includes(value),
    },
    // 是否显示按钮
    showButtons: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      validateProps: this.formData,
    };
  },
  watch: {
    formData: {
      handler(val) {
        this.validateProps = val;
      },
      immediate: true,
      deep: true,
    },
  },
  computed: {
    // 获取所有可重置的字段
    resettableFields() {
      return this.formItems.map((item) => item.prop);
    },
  },
  methods: {
    // 搜索方法
    handleSearch() {
      // 处理表单数据，特别是日期类型
      const processedData = JSON.parse(JSON.stringify(this.validateProps));
      
      // 遍历所有字段，处理日期类型
      this.formItems.forEach(item => {
        if (item.type === 'date' && processedData[item.prop] !== null && processedData[item.prop] !== undefined) {
          // 如果是日期范围数组，转换为逗号分隔的字符串
          if (Array.isArray(processedData[item.prop])) {
            processedData.dateLimit = processedData.dateLimitAttr.join(',');
          }
        }
      });
      this.$emit('search', processedData);
    },

    // 重置方法
    handleReset() {
      // 重置所有字段
      this.resettableFields.forEach((prop) => {
        const item = this.formItems.find((i) => i.prop === prop);
        if (item) {
          // 根据字段类型设置默认值
          if (item.type === 'select' || item.type === 'cascader') {
            this.$set(this.validateProps, prop, item.defaultValue || '');
          } else if (item.type === 'date') {
            this.$set(this.validateProps, 'dateLimit', '');
            this.$set(this.validateProps, prop, item.defaultValue || []);
          }else if (item.type === 'user') { //用户搜索
            this.$set(this.validateProps, 'searchType', 'all');
            this.$set(this.validateProps, 'content', '');
          }
          else {
            this.$set(this.validateProps, prop, item.defaultValue || '');
          }
        }

      });
      // 触发重置事件
      this.$emit('reset', { ...this.validateProps });

      // 重置后自动搜索
     // this.handleSearch();
    },
  },
};
</script>
