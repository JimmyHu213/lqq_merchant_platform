<template>
  <div class="item-selector">
    <div class="item-list">
      <draggable class="dragArea list-group" :list="items" group="peoples" handle=".iconfont">
        <div v-for="(item, index) in items" :key="index" class="item-wrapper">
          <item-base
              v-model="items[index]"
              :item-label="itemLabel"
              :selected-text="selectedText"
              :unselected-text="unselectedText"
          ></item-base>
          <div class="item-actions">
            <el-button type="text" icon="el-icon-close" @click="removeItem(index)" class="delete-btn"></el-button>
          </div>
        </div>
      </draggable>

    </div>
    <el-button type="text" @click="addItem" class="add-item-btn">
      <i class="el-icon-plus"></i>添加{{ itemLabel }}
    </el-button>
    <div class="item-footer" v-if="items.length > 0">
      <el-button type="primary" @click="handleSave">{{ saveText }}</el-button>
    </div>
  </div>
</template>

<script>
import ItemBase from './itemBase.vue';

export default {
  name: 'ItemSelector',
  components: {
    ItemBase
  },
  props: {
    value: {
      type: Array,
      default: () => []
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
    },
    saveText: {
      type: String,
      default: '保存'
    }
  },
  data() {
    return {
      items: []
    };
  },
  watch: {
    value: {
      immediate: true,
      deep: true,
      handler(newVal) {
        this.items = newVal.map(item => ({ ...item }));
      }
    },
    items: {
      deep: true,
      handler(newVal) {
        this.$emit('input', newVal);
      }
    }
  },
  methods: {
    addItem() {
      this.items.push({
        name: '',
        status: 1
      });
    },
    removeItem(index) {
      this.items.splice(index, 1);
    },
    handleSave() {
      this.$emit('save', this.items);
    }
  }
};
</script>

<style scoped lang="scss">
.item-selector {
  padding: 20px;
  border-radius: 4px;
  
  .add-item-btn {
    color: #409eff;
    font-size: 14px;
    padding: 8px 15px;
    border: 1px dashed #dcdfe6;
    background: #fff;
    border-radius: 4px;
    transition: all 0.3s;
    
    &:hover {
      color: #66b1ff;
      border-color: #c6e2ff;
      background: #ecf5ff;
    }
  }
  
  .item-list {
    margin-top: 20px;
  }
  
  .item-wrapper {
    width: 500px;
    position: relative;
    margin-bottom: 15px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    :deep(.item-base) {
      padding-right: 30px;
    }
    
    .item-actions {
      position: absolute;
      top: 15px;
      right: 15px;
      
      .delete-btn {
        color: #909399;
        padding: 5px;
        
        &:hover {
          color: #f56c6c;
        }
      }
    }
  }
  
  .item-footer {
    margin-top: 20px;
    display: flex;
    justify-content: center;
    
    .el-button--primary {
      padding: 10px 30px;
      border-radius: 4px;
    }
  }
}
</style>