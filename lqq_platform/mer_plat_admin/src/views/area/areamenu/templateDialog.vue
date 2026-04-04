<template>
  <el-dialog
    :visible.sync="AssociationDia.visible"
    title=""
    width="800px"
    top="7vh"
    destroy-on-close
    :close-on-click-modal="false"
  >
    <regional-agent v-if="AssociationDia.visible" :isShowAction="false" class="mb20" @getTemplateId="getTemplateId"></regional-agent>
    <div slot="footer">
      <el-button @click="handleClose"> 取消 </el-button>
      <el-button type="primary" v-debounceClick="handleConfirm"> 确定 </el-button>
    </div>
  </el-dialog>
</template>

<script>
import RegionalAgent from '@/views/pagediy/devise/regionalAgent';
import { conjunctiveDiyApi } from '@/api/area';

export default {
  name: 'templateDialog',
  components: { RegionalAgent },
  props: {
    AssociationDia: {
      type: Object,
      default: {},
    },
  },
  data() {
    return {
      templateId: 0,
    };
  },
  methods: {
    handleClose() {
      this.$emit('handleClose');
    },
    // 模板id
    getTemplateId(id) {
      this.templateId = id;
    },
    async handleConfirm() {
      await conjunctiveDiyApi({ diyId: this.templateId, ids: this.AssociationDia.ids });
      this.$emit('changeList')
      this.handleClose();
    },
  },
};
</script>

<style scoped></style>
