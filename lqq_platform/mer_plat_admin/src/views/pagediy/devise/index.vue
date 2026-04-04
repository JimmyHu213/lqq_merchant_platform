<template>
  <div class="divBox">
    <el-card class="box-card" shadow="never" :bordered="false">
      <div class="acea-row">
        <div class="tab_view">
          <div
            class="cell_item"
            :class="{ tab_active: listActive === index }"
            v-for="(item, index) in tabList"
            :key="index"
            @click="ProductNavTab(index)"
          >
            {{ item }}
          </div>
        </div>
        <div v-if="listActive === 0">
          <div class="rightModel acea-row">
            <div class="leftModel">
              <div class="current_home">
                <div class="model_header">
                  <iframe id="iframe" class="iframe-box" :src="frontDomain" frameborder="0" ref="iframe"></iframe>
                </div>
                <div class="mask"></div>
              </div>
            </div>
            <div style="width: 100%">
              <el-row>
                <el-col v-bind="grid">
                  <div class="flex_between">
                    <el-button
                      v-hasPermi="['platform:pagediy:update', 'platform:pagediy:getdefault', 'platform:pagediy:info']"
                      type="primary"
                      @click="handlerDiyHomePage()"
                      size="small"
                      class="mb35"
                      style="font-size: 12px"
                      >首页装修</el-button
                    >
                    <el-card
                      body-style="background-color: #F9F9F9;"
                      class="mb20 Qrcode-card"
                      shadow="never"
                      :bordered="false"
                    >
                      <el-row>
                        <el-col v-bind="grid2">
                          <div class="acea-row row-between-wrapper Qrcode-box">
                            <div>
                              <div class="title mb20">微信小程序</div>
                              <div class="tips">扫描右侧二维码查看</div>
                            </div>
                          </div>
                        </el-col>
                        <el-col v-bind="grid2">
                          <div style="text-align: right">
                            <el-image :src="Qrcode" class="Qrcode_img"></el-image>
                          </div>
                        </el-col>
                      </el-row>
                    </el-card>
                    <el-card
                      body-style="background-color: #F9F9F9;"
                      class="Qrcode-card"
                      shadow="never"
                      :bordered="false"
                    >
                      <el-row>
                        <el-col v-bind="grid2">
                          <div class="acea-row row-between-wrapper Qrcode-box">
                            <div>
                              <div class="title mb20">微信公众号</div>
                              <div class="tips">扫描右侧二维码查看</div>
                            </div>
                          </div>
                        </el-col>
                        <el-col v-bind="grid2">
                          <div class="acea-row" style="justify-content: right">
                            <div id="diyQrcode"></div>
                          </div>
                        </el-col>
                      </el-row>
                    </el-card>
                  </div>
                </el-col>
              </el-row>
              <div class="preview-text-box ml-60 mt-10">
                <el-alert class="width-450 flex" title="若预览页面未加载出，请前往系统配置页面填写网站域名" type="info">
                  <a class="to-page-btn ml-10 color-909399" @click="navigatorTo()">点击前往</a>
                </el-alert>
              </div>
            </div>
          </div>
        </div>
        <regional-agent v-if="listActive === 1 || listActive === 2" :listActive="listActive"></regional-agent>
      </div>
    </el-card>
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
import { pagediyGetSetHome, wechatQrcodeApi } from '@/api/devise';
import QRcode from 'qrcodejs2';
import { checkPermi } from '@/utils/permission';
import RegionalAgent from '@/views/pagediy/devise/regionalAgent';
import useDeviseDiy from '@/views/pagediy/devise/config'; // 权限判断函数
const { previewProtol, onEditDiyPage } = useDeviseDiy();
export default {
  name: 'index',
  components: { RegionalAgent },
  data() {
    return {
      grid: {
        xl: 16,
        lg: 20,
        md: 20,
        sm: 24,
        xs: 24,
      },
      grid2: {
        xl: 12,
        lg: 12,
        md: 20,
        sm: 24,
        xs: 24,
      },
      listActive: 0,
      tabList: ['商城首页', '微页面'],
      listLoading: false,
      frontDomain: '',
      Qrcode: '', //小程序二维码
    };
  },
  mounted() {
    this.frontDomain = `${this.$selfUtil.getFrontDomainUrl()}/pages/index/index`;
    this.getWechatQrcode();
    this.getQRcode();
  },
  methods: {
    checkPermi,
    //微信二维码
    getQRcode() {
      document.getElementById('diyQrcode').innerHTML = '';
      new QRcode('diyQrcode', { width: 120, height: 120, text: this.frontDomain });
    },
    //小程序二维码
    getWechatQrcode() {
      // env_version	默认值为："release"，要打开的小程序版本。正式版为"release"，体验版为"trial"，开发版为"develop"，仅在微信外打开时生效。
      wechatQrcodeApi({
        scene: 'id=0',
        path: 'pages/index/index',
        env_version: 'release',
      }).then((res) => {
        this.Qrcode = res.code;
      });
    },
    //点击左侧菜单
    ProductNavTab(index) {
      this.listActive = index;
    },
    // DIY 编辑首页
    handlerDiyHomePage() {
      pagediyGetSetHome().then((res) => {
        onEditDiyPage(res.id, 'edit', res.type);
      });
    },
    // 跳转域名配置
    navigatorTo() {
      this.$router.push('/operation/setting');
    },
  },
};
</script>

<style scoped lang="scss">
.current_home {
  position: relative;
}

.mask {
  position: absolute;
  left: 0;
  width: 100%;
  top: 0;
  height: 100%;
  background-color: transparent;
}

.Qrcode-box {
  width: 100%;
  height: 120px;
}

.Qrcode_img {
  width: 120px;
}

.flex_between {
  margin-left: 60px;
  width: 100%;
  border-radius: 2px 2px 2px 2px;
  opacity: 1;
  border: 1px solid #eeeeee;
  position: relative;
  padding: 20px;

  &:before {
    content: '';
    width: 0px;
    height: 0px;
    border-right: 10px solid #eeeeee;
    border-top: 10px solid transparent;
    border-bottom: 10px solid transparent;
    position: absolute;
    top: 23px;
    left: -10px;
  }

  &:after {
    content: '';
    width: 0px;
    height: 0px;
    border-right: 9px solid #ffffff;
    border-top: 9px solid transparent;
    border-bottom: 9px solid transparent;
    position: absolute;
    top: 24px;
    left: -9px;
  }
}

.title {
  font-size: 20px;
  font-weight: 800;
  color: #303133;
}

.tips {
  font-size: 14px;
  color: #909399;
}

.tab_view {
  width: 200px;
  border-right: 1px solid #eee;
  margin-right: 40px;
  display: flex;
  flex-direction: column;

  .cell_item {
    height: 50px;
    font-size: 14px;
    line-height: 50px;
    text-align: left;
    padding-left: 30px;
    cursor: pointer;
  }
}

.tab_active {
  color: var(--prev-color-primary);
  border-right: 1px solid var(--prev-color-primary);
}

.rightModel {
  width: 100%;
  min-width: 800px;
}

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

.acea-row {
  flex-wrap: nowrap !important;
}

.model_header {
  width: 350px;
}

.iframe-box {
  width: 350px;
  height: 669px;
  border-radius: 10px;
  border: 1px solid #eee;
}
.color-red {
  color: red;
}
.ml-10 {
  margin-left: 10px;
}
.mt-10 {
  margin-top: 10px;
}

.ml-60 {
  margin-left: 60px;
}

.width-450 {
  width: 450px;
}

.color-909399 {
  color: #909399;
}
</style>
