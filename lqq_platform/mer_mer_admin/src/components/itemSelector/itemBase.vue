<template>
  <div class="item-base">
    <div class="item-header">
      <label class="item-label">{{ itemLabel }}名称</label>
      <el-input
        v-model="item.name"
        maxlength="10"
        show-word-limit
        class="item-name-input"
      ></el-input>
    </div>
    <div class="item-status">
      <div
        class="status-item"
        :class="{ active: item.status === 1 }"
        @click="item.status = 1"
      >
        <div class="status-icon selected-icon"></div>
        <span>{{ selectedText }}</span>
      </div>
      <div
        class="status-item"
        :class="{ active: item.status === 0 }"
        @click="item.status = 0"
      >
        <div class="status-icon unselected-icon"></div>
        <span>{{ unselectedText }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ItemBase',
  props: {
    value: {
      type: Object,
      default: () => ({
        name: '',
        status: 1
      })
    },
    itemLabel: {
      type: String,
      default: '权益'
    },
    selectedText: {
      type: String,
      default: '选中'
    },
    unselectedText: {
      type: String,
      default: '未选中'
    }
  },
  computed: {
    item: {
      get() {
        return this.value;
      },
      set(val) {
        this.$emit('input', val);
      }
    }
  }
};
</script>

<style scoped lang="scss">
.item-base {
  padding: 15px;
  background: #fff;
  border-radius: 4px;
  margin-bottom: 15px;
  background: #f9f9f9;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #ebeef5;
  
  .item-header {
    margin-bottom: 15px;
    
    .item-label {
      display: inline-block;
      margin-right: 10px;
      color: #606266;
      font-size: 14px;
    }
    
    .item-name-input {
      width: 350px;
      
      & .el-input__inner {
        border-radius: 4px;
        border-color: #dcdfe6;
      }
    }
  }
  
  .item-status {
    display: flex;
    align-items: center;
    
    .status-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      cursor: pointer;
      margin-right: 30px;
      transition: all 0.3s;
      
      &.active {
        color: #409eff;
      }
      
      .status-icon {
        width: 40px;
        height: 40px;
        margin-bottom: 5px;
        background-size: contain;
        background-position: center;
        background-repeat: no-repeat;
      }
      
      .selected-icon {
        background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 40 40'%3E%3Cpath d='M20 5C12.8 5 7 10.8 7 18c0 1.9.4 3.7 1.1 5.3l-3.7 2.1c-.3.2-.4.6-.2.9.2.3.6.4.9.2l3.7-2.1c1.6.7 3.4 1.1 5.3 1.1s3.7-.4 5.3-1.1l3.7 2.1c.3.2.7.1.9-.2.2-.3.1-.7-.2-.9l-3.7-2.1c.7-1.6 1.1-3.4 1.1-5.3 0-7.2-5.8-13-13-13zm0 25c-3.9 0-7.2-1.7-9.5-4.5l3.1-1.8c1.7.9 3.7 1.3 5.7 1.3s4-.4 5.7-1.3l3.1 1.8c-2.3 2.8-5.6 4.5-9.5 4.5z' fill='%23ff6b6b'/%3E%3Cpath d='M15 15l2.5 2.5L22 15' stroke='%23fff' stroke-width='2' fill='none' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E");
      }
      
      .unselected-icon {
        background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 40 40'%3E%3Cpath d='M20 5C12.8 5 7 10.8 7 18c0 1.9.4 3.7 1.1 5.3l-3.7 2.1c-.3.2-.4.6-.2.9.2.3.6.4.9.2l3.7-2.1c1.6.7 3.4 1.1 5.3 1.1s3.7-.4 5.3-1.1l3.7 2.1c.3.2.7.1.9-.2.2-.3.1-.7-.2-.9l-3.7-2.1c.7-1.6 1.1-3.4 1.1-5.3 0-7.2-5.8-13-13-13zm0 25c-3.9 0-7.2-1.7-9.5-4.5l3.1-1.8c1.7.9 3.7 1.3 5.7 1.3s4-.4 5.7-1.3l3.1 1.8c-2.3 2.8-5.6 4.5-9.5 4.5z' fill='%23909399'/%3E%3C/svg%3E");
      }
    }
  }
}
</style>