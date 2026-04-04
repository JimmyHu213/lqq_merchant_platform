<template>
  <div>
    <el-dialog
      :title="editDialogConfig.isCreate === 0 ? `添加员工` : `编辑员工`"
      :visible.sync="editDialogConfig.visible"
      destroy-on-close
      :close-on-click-modal="false"
      width="540px"
      class="dialog-bottom"
    >
      <el-form ref="editPram" :rules="rules" :model="editPram" label-width="90px">
        <el-form-item label="关联用户：" prop="avatar">
          <div class="upLoadPicBox" @click="handlerChangeUser">
            <div v-if="editPram.avatar" class="pictrue">
              <img :src="editPram.avatar" />
            </div>
            <div v-else class="upLoad">
              <i class="el-icon-camera cameraIconfont" />
            </div>
            <div class="nick">{{ editPram.nickName }}</div>
            <div class="from-tips">一个用户只能在整个平台成为一家商户的服务员工，编辑不可修改关联用户</div>
          </div>
        </el-form-item>
        <el-form-item label="证件照：" prop="idPhoto">
          <div class="upLoadPicBox" @click="modalPicTap(false)">
            <div v-if="editPram.idPhoto" class="pictrue">
              <img :src="editPram.idPhoto" />
            </div>
            <div v-else class="upLoad">
              <i class="el-icon-camera cameraIconfont" />
            </div>
            <div class="from-tips">用于移动端工单管理工作台展示，建议：80*80PX，大小不超过5KB。</div>
          </div>
        </el-form-item>
        <el-form-item label="员工姓名：" prop="name">
          <el-input v-model="editPram.name" placeholder="请输入员工姓名" />
        </el-form-item>
        <el-form-item label="联系电话：" prop="phone">
          <el-input v-model="editPram.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="排序：">
          <el-input-number v-model.trim="editPram.sort" step-strictly :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态：" prop="status">
          <el-switch
            v-model="editPram.status"
            :active-value="1"
            :inactive-value="0"
            active-text="开启"
            inactive-text="关闭"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="handlerClose()">取消</el-button>
        <el-button type="primary" v-debounceClick="handlerSubmit">确定</el-button>
      </div>
    </el-dialog>

    <!-- 关联用户弹窗 -->
    <el-dialog
      title="关联用户列表"
      :visible.sync="userVisible"
      width="900px"
      :append-to-body="true"
      :close-on-click-modal="false"
    >
      <userList @getRow="getRow" v-if="userVisible" userType="all"></userList>
    </el-dialog>
  </div>
</template>

<script>
import userList from '../user/list';
import { serviceStaffAddApi, serviceStaffUpdateApi } from '@/api/staff';
import { validatePhone } from '@/utils/toolsValidate';
const editPram = {
  idPhoto: null,
  name: null,
  userId: null,
  phone: null,
  status: 1,
  sort: 0,
  id: null,
  nickName: null,
  avatar: null,
}
export default {
  components: { userList },
  props: {
    editDialogConfig: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      selectedRow: null,
      userVisible: false,
      editPram: Object.assign({},editPram),
      rules: {
        idPhoto: [{ required: true, message: '请设置头像', trigger: 'change' }],
        avatar: [{ required: true, message: '请关联用户', trigger: 'change' }],
        name: [{ required: true, message: '请输入姓名', trigger: 'change' }],
        phone: [{ required: true, validator: validatePhone, trigger: 'blur' }],
      },
    };
  },
  watch: {
    editDialogConfig: {
      handler(nVal, oVal) {
        this.init();
      },
      deep: true,
    },
  },
  created() {},
  methods: {
    // 选择用户
    handlerChangeUser() {
      if (this.editDialogConfig.isCreate === 0) this.userVisible = true;
    },
    init() {
      if (this.editDialogConfig.isCreate == 1) {
        for (const key in this.editDialogConfig.data) {
          this.editPram[key] = this.editDialogConfig.data[key];
        }
      }else{
        this.editPram = Object.assign({},editPram)
      }
    },
    close() {
      this.userVisible = false;
    },
    getUser() {
      this.editPram.avatar = this.selectedRow.avatar;
      this.editPram.userId = this.selectedRow.uid;
      this.editPram.nickName = this.selectedRow.nickname;
      this.userVisible = false;
    },
    getRow(row) {
      this.selectedRow = row;
      this.getUser();
    },
    //取消
    handlerClose() {
      this.$emit('hideEditDialog', 0);
    },
    //表单确认
    handlerSubmit() {
      this.$refs.editPram.validate((valid) => {
        if (!valid) return;
        if (this.editDialogConfig.isCreate == 0) {
          serviceStaffAddApi(this.editPram).then((res) => {
            this.$emit('hideEditDialog', 1, 0);
          });
        } else {
          serviceStaffUpdateApi(this.editPram).then((res) => {
            this.$emit('hideEditDialog', 1, 1);
          });
        }
      });
    },
    //点击头像
    modalPicTap(tit, num, i) {
      const _this = this;
      this.$modalUpload(
        function (img) {
          if (!img) return;
          if (!tit && !num) {
            _this.editPram.idPhoto = img[0].sattDir;
          }
        },
        tit,
        'store',
      );
    },
  },
};
</script>

<style scoped lang="scss">
.upLoadPicBox {
  position: relative;
  .nick {
    width: 340px;
    position: absolute;
    left: 70px;
    top: 15px;
  }
}
::v-deep .el-dialog__body {
  padding-top: 20px !important;
}
::v-deep .el-card__body {
  padding: 0 0 20px !important;
}
</style>
