<template>
  <div class="divBox">
    <div class="container_box">
      <pages-header
        ref="pageHeader"
        :title="isViewMode ? '查看圈层' : isEdit ? '编辑圈层' : '添加圈层'"
        back-url="/area/index"
      />
      <el-card class="mt14" :body-style="{ padding: '0 20px 20px' }" shadow="never" :bordered="false">
        <el-tabs v-if="tabList.length > 0" v-model="currentTab" class="list-tabs mb5">
          <el-tab-pane v-for="(item, index) in tabList" :key="index" :name="item.value" :label="item.title" />
        </el-tabs>
        <el-form
          ref="formValidate"
          v-loading="loading"
          class="formValidate"
          :rules="ruleValidate"
          :model="formValidate"
          label-width="120px"
          autocomplete="off"
          @submit.native.prevent
        >
          <!-- 圈层信息 Tab -->
          <div v-show="currentTab === '1'">
            <el-form-item label="父级：" prop="pid">
              <el-select
                v-model="formValidate.pid"
                placeholder="请选择父级"
                style="width: 420px"
                :disabled="isViewMode || isEdit"
              >
                <el-option label="顶级" :value="0" />
                <el-option v-for="item in parentAreas" :key="item.id" :label="item.name" :value="item.id" :disabled="item.disabled"/>
              </el-select>
            </el-form-item>

            <el-form-item label="圈层名称：" prop="name">
              <el-input
                v-model="formValidate.name"
                placeholder="请填写圈层名称"
                style="width: 420px; margin-right: 32px"
                :disabled="isViewMode"
              />
            </el-form-item>

            <el-form-item label="商户范围：" prop="range">
              <el-input
                v-model="formValidate.range"
                placeholder="请填写商户范围"
                type="textarea"
                :rows="3"
                style="width: 420px"
                :disabled="isViewMode"
              />
            </el-form-item>

            <el-form-item label="管理员名称：" prop="realName">
              <el-input
                v-model="formValidate.realName"
                placeholder="请填写管理员名称"
                style="width: 420px"
                :disabled="isViewMode"
              />
            </el-form-item>

            <!-- 隐藏的假字段，用于干扰浏览器自动填充 -->
            <input type="text" style="display: none" autocomplete="username" />
            <input type="password" style="display: none" autocomplete="current-password" />

            <el-form-item label="管理员账号：" prop="account">
              <el-input
                v-model="formValidate.account"
                placeholder="请填写管理员账号"
                style="width: 420px"
                autocomplete="off"
                :readonly="false"
                :disabled="isViewMode || !!this.$route.params.id"
              />
            </el-form-item>

            <el-form-item v-if="!isEdit" label="管理员密码：" prop="pwd">
              <el-input
                v-model="formValidate.pwd"
                placeholder="请填写管理员密码"
                type="password"
                show-password
                style="width: 420px"
                autocomplete="new-password"
                :readonly="false"
                :disabled="isViewMode"
              />
            </el-form-item>

            <el-form-item v-if="!isEdit" label="确认密码：" prop="confirmPassword">
              <el-input
                v-model="formValidate.confirmPassword"
                placeholder="请填写确认密码"
                type="password"
                show-password
                style="width: 420px"
                autocomplete="new-password"
                :readonly="false"
                :disabled="isViewMode"
              />
            </el-form-item>

            <el-form-item label="管理员手机号：" prop="phone">
              <el-input
                v-model="formValidate.phone"
                placeholder="请填写管理员手机号"
                style="width: 420px"
                :disabled="isViewMode"
              />
            </el-form-item>
            <el-form-item label="超管角色：" prop="role">
              <el-select
                v-model="formValidate.role"
                placeholder="请选择超管角色"
                :disabled="isViewMode"
                style="width: 420px"
              >
                <el-option v-for="item in roleList" :key="item.id" :label="item.roleName" :value="item.id" />
              </el-select>
            </el-form-item>
            <!-- 地址开关 -->
            <el-form-item label="地址开关：" prop="addressSwitch">
              <el-switch
                v-model="formValidate.addressSwitch"
                :disabled="isViewMode"
                :active-value="1"
                :inactive-value="2"
                active-text="开启"
                inactive-text="关闭"
              />
            </el-form-item>
            <el-form-item v-if="formValidate.addressSwitch === 1" label="圈层代理中心：" prop="address">
              <div class="location-picker">
                <el-input
                  v-model="formValidate.address"
                  placeholder="请在下方地图中选择位置"
                  style="width: 420px; margin-bottom: 10px"
                  disabled
                />

                <!-- 经纬度字段：完全隐藏，但保留在表单数据中用于提交 -->
                <!-- 隐藏的经纬度字段，仅用于数据传递 -->
                <input v-model="formValidate.latitude" type="hidden" />
                <input v-model="formValidate.longitude" type="hidden" />

                <!-- 腾讯地点选择器 iframe -->
                <iframe
                  v-if="!isViewMode && currentTab === '1'"
                  id="mapPage"
                  :key="mapPickerKey"
                  width="100%"
                  height="800px"
                  frameborder="0"
                  allow="geolocation *"
                  :src="mapPickerUrl"
                />
              </div>
            </el-form-item>
          </div>

          <!-- 归属商户 Tab -->
          <div v-show="currentTab === '2'">
            <div class="merchant-section">
              <!-- 商户管理头部 -->
              <div class="merchant-header">
                <el-button v-if="!isViewMode" v-debounceClick="openMerchantDialog" type="primary">选择商户</el-button>
                <span v-if="formValidate.merchantList && formValidate.merchantList.length > 0" class="merchant-count">
                  共 {{ formValidate.merchantList.length }} 个商户
                </span>
              </div>
              <!-- 商户表格 -->
              <div class="merchant-table-container">
                <el-table
                  :data="getTableData()"
                  border
                  style="width: 100%"
                  :empty-text="''"
                  :row-key="getMerchantUniqueId"
                >
                  <el-table-column prop="id" label="ID" width="80" />
                  <el-table-column prop="name" label="商户名称" min-width="150" />
                  <el-table-column prop="realName" label="联系人" width="120" />
                  <el-table-column prop="phone" label="联系方式" width="140" />
                  <el-table-column v-if="!isViewMode" label="操作" width="100">
                    <template slot-scope="scope">
                      <el-button @click="handleRemoveMerchant(scope.row, scope.$index)" type="text" size="small">
                        删除
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </div>
        </el-form>

        <!-- 操作按钮 -->
        <div v-if="!isViewMode" class="form-footer">
          <!-- 圈层维护Tab的按钮 -->
          <el-button v-show="currentTab === '1'" @click="currentTab = '2'" size="small" class="priamry_border"
            >下一步</el-button
          >

          <!-- 归属商户Tab的按钮 -->
          <el-button v-show="currentTab === '2'" @click="currentTab = '1'" size="small" class="priamry_border"
            >上一步</el-button
          >

          <el-button
            v-show="currentTab === '1'"
            v-debounceClick="saveArea"
            type="primary"
            size="small"
            :loading="loading"
          >{{ isEdit ? '更新' : '确认' }}</el-button>

          <el-button
            v-show="currentTab === '2'"
            v-debounceClick="saveMerchants"
            type="primary"
            size="small"
            :loading="loading"
            >保存</el-button
          >
        </div>
      </el-card>
    </div>

    <!-- 商户选择弹窗 -->
    <el-dialog
      title="选择商户"
      :visible.sync="merchantDialogVisible"
      width="500px"
      :close-on-click-modal="false"
      append-to-body
    >
      <div class="merchant-dialog-content">
        <el-form :model="merchantForm" label-width="80px">
          <el-form-item label="选择商户">
            <mer-user-category-for-area
              ref="merchantSelector"
              :multiple="true"
              :mer-id-checked="merchantForm.selectedMerchantId"
              :area-id="formValidate.id || 0"
              @getMerId="getMerId"
            />
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeMerchantDialog">取 消</el-button>
        <el-button v-debounceClick="confirmMerchantSelection" type="primary" :loading="bindingMerchant"
          >确 定</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  areaSaveApi,
  areaUpdateApi,
  areaInfoApi,
  areaBindMerchantApi,
  areaListTreeApi,
  areaRemoveMerchantApi,
  platformCircleRoleListApi,
  areaMerchantApi,
} from '@/api/area';
import { merchantDetailApi } from '@/api/merchant';
import merUserCategoryForArea from '@/components/merUserCategoryForArea';
// 移除对 map.js 配置的依赖，直接使用 API 获取的缓存 key

export default {
  name: 'AreaCreate',
  components: {
    merUserCategoryForArea,
  },
  data() {
    return {
      currentTab: '1',
      tabList: [],
      roleList: [],
      loading: false,
      isViewMode: false, // 是否为查看模式
      lastRouteId: null, // 记录上次的路由ID，用于activated中判断是否需要重新初始化
      parentAreas: [],
      mapLocation: {},
      locationInfo: null,
      shouldRenderMap: false, // 控制地图是否渲染
      hasVisitedMapTab: false, // 记录是否访问过地图tab
      isMapInitialized: false, // 控制地图是否已初始化，避免重复初始化
      // 腾讯地点选择器基础地址（不含坐标）
      mapPickerBaseUrl: `https://apis.map.qq.com/tools/locpicker?type=1&key=${
        localStorage.getItem('tencentMapKey') || ''
      }&referer=myapp`,
      hasShownInvalidCoordMsg: false,
      // 商户选择相关
      merchantDialogVisible: false,
      selectedMerchants: [],
      bindingMerchant: false, // 绑定商户加载状态
      merchantForm: {
        selectedMerchantId: '',
      },
      formValidate: {
        id: null, // 圈层id，新建时为null，创建成功后设置
        merIds: [],
        pid: 0,
        name: '',
        range: '',
        realName: '',
        account: '',
        pwd: '',
        confirmPassword: '',
        phone: '',
        role: undefined,
        addressSwitch: 2, // 地址开关，1为开启，2为关闭
        address: '',
        latitude: '',
        longitude: '',
        merchantList: [], // 圈层关联的商户列表
      },
      originalMerchantIds: [], // 记录页面初始化时的原始商户ID列表
      ruleValidate: {
        pid: [{ required: true, message: '请选择父级', trigger: 'change' }],
        name: [{ required: true, message: '请填写圈层代理名称', trigger: 'blur' }],
        range: [{ required: false, message: '请填写圈层范围', trigger: 'blur' }],
        realName: [{ required: true, message: '请填写管理员姓名', trigger: 'blur' }],
        account: [{ required: true, message: '请填写管理员账号', trigger: 'blur' }],
        pwd: [{ required: true, message: '请填写管理员密码', trigger: 'blur' }],
        confirmPassword: [
          { required: true, message: '请填写确认密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value !== this.formValidate.pwd) {
                callback(new Error('两次输入的密码不一致'));
              } else {
                callback();
              }
            },
            trigger: 'blur',
          },
        ],
        phone: [
          { required: true, message: '请填写管理员手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
        ],
        role: [{ required: true, message: '请选择超管角色', trigger: 'change' }],
        address: [
          {
            validator: (rule, value, callback) => {
              // 当地址开关关闭时(值为2)，圈层代理中心为非必填项
              if (this.formValidate.addressSwitch === 2) {
                callback();
              } else {
                // 当地址开关开启时(值为1)，圈层代理中心为必填项
                if (!value || value.trim() === '') {
                  callback(new Error('请填写圈层代理地址'));
                } else {
                  callback();
                }
              }
            },
            trigger: 'blur',
          },
        ],
        latitude: [{ required: false, message: '请填写纬度', trigger: 'blur' }],
        longitude: [{ required: false, message: '请填写经度', trigger: 'blur' }],
      },
      merList: [],
    };
  },
  computed: {
    isEdit() {
      return !!this.$route.params.id;
    },
    // 经纬度合法性校验，用于顶部提示，但不阻断地图渲染
    invalidCoord() {
      // 检查是否为空值或未定义，正确处理0值情况
      if (
        this.formValidate.latitude === '' ||
        this.formValidate.latitude === null ||
        this.formValidate.longitude === '' ||
        this.formValidate.longitude === null ||
        this.formValidate.latitude === undefined ||
        this.formValidate.longitude === undefined
      ) {
        return true;
      }
      const lat = parseFloat(this.formValidate.latitude);
      const lng = parseFloat(this.formValidate.longitude);
      const latOk = !isNaN(lat) && lat >= -90 && lat <= 90;
      const lngOk = !isNaN(lng) && lng >= -180 && lng <= 180;
      return !(latOk && lngOk);
    },
    // 通过 key 触发 iframe 重新挂载，避免旧的错误坐标影响交互
    mapPickerKey() {
      // 确保经纬度为字符串格式，避免精度问题
      const lat = this.formValidate.latitude ? String(this.formValidate.latitude) : '0';
      const lng = this.formValidate.longitude ? String(this.formValidate.longitude) : '0';
      return this.invalidCoord ? `locpicker-invalid` : `locpicker-${lat}-${lng}-${this.currentTab}`;
    },
    // 根据已有经纬度拼接 locpicker 的 coord 参数，编辑时让地图居中到已保存位置
    mapPickerUrl() {
      const base = this.mapPickerBaseUrl;
      // 经纬度不合法：不带 coord，让 locpicker 自动定位到当前
      if (this.invalidCoord) return base;
      // 只有在经纬度合法时才添加coord参数，正确处理0值情况
      if (
        !this.invalidCoord &&
        this.formValidate.latitude !== '' &&
        this.formValidate.latitude !== null &&
        this.formValidate.latitude !== undefined &&
        this.formValidate.longitude !== '' &&
        this.formValidate.longitude !== null &&
        this.formValidate.longitude !== undefined
      ) {
        const latStr = String(this.formValidate.latitude).trim();
        const lngStr = String(this.formValidate.longitude).trim();
        return `${base}&coord=${latStr},${lngStr}`;
      }
      return base;
    },
  },
  watch: {
    // 监听地址开关变化，重新验证address字段
    'formValidate.addressSwitch'(newVal, oldVal) {
      if (newVal !== oldVal && this.$refs.formValidate) {
        // 当地址开关状态改变时，重新验证address字段
        this.$nextTick(() => {
          this.$refs.formValidate.validateField('address');
        });
      }
    },
    // 监听tab切换，当切换到第一个tab时初始化地图
    currentTab(newTab, oldTab) {
      // 新增圈层时，如果圈层ID为null或0，不允许切换到第二个tab
      // 但是如果是查看模式或编辑模式（有路由参数ID），则允许切换
      if (newTab === '2' && (!this.formValidate.id || this.formValidate.id === 0) && !this.$route.params.id) {
        // 只在创建模式时提示，详情模式不提示
        if (!this.isViewMode && !this.isEdit) {
          this.$message.warning('请先保存圈层');
        }
        this.$nextTick(() => {
          this.currentTab = oldTab || '1';
        });
        return;
      }

      if (newTab === '1' && !this.isMapInitialized && !this.isViewMode) {
        // 只有在地图未初始化且不是查看模式时才初始化
        this.initializeMap();
      }

      // 主动防御：切换到商户Tab时强制刷新数据，确保数据一致性
      if (newTab === '2') {
        this.$nextTick(() => {
          this.proactiveRefreshMerchantData();
        });
      }
    },
  },
  mounted() {
    this.initPage();
    this.getRoleList();
    this.getMerList();
    // 监听腾讯地点选择器回传
    window.addEventListener('message', this.onMapPickerMessage, false);
    // 如果是编辑模式且经纬度不合法，延迟检查以确保数据加载完成
    this.$nextTick(() => {
      setTimeout(() => {
        try {
          if (this.isEdit && !this.isViewMode && this.invalidCoord && !this.hasShownInvalidCoordMsg) {
            this.hasShownInvalidCoordMsg = true;
            // this.$message.error('地图经纬度不合法，已为你定位到当前位置');
          }
        } catch (e) {
          // 忽略JSON解析错误
        }
      }, 500);
    });
  },
  activated() {
    // 每次激活页面时检查路由参数变化
    const currentRouteId = this.$route.params.id;
    const currentTab = this.$route.query.tab;

    // 只有当路由参数发生变化时才重新初始化
    if (this.lastRouteId !== currentRouteId) {
      this.lastRouteId = currentRouteId;
      this.initPage();
    } else if (currentTab && this.currentTab !== currentTab) {
      // 如果只是tab参数变化，只更新currentTab
      this.currentTab = currentTab;
    }
  },
  beforeRouteLeave(to, from, next) {
    // 离开页面时重置表单数据
    this.resetForm();
    next();
  },
  beforeDestroy() {
    window.removeEventListener('message', this.onMapPickerMessage, false);
  },
  methods: {
    getMerList() {
      areaMerchantApi(0).then((res) => {
        this.merList = res;
      });
    },
    // 获取当前地图API Key
    getCurrentMapKey() {
      return localStorage.getItem('tencentMapKey') || '未配置';
    },
    // 获取角色列表
    getRoleList() {
      let listParam = {
        page: 1,
        limit: 1000,
        status: true,
      };
      platformCircleRoleListApi(listParam).then((res) => {
        this.roleList = res.list;
      });
    },
    // 初始化页面
    initPage() {
      // 检查是否为查看模式
      this.isViewMode = this.$route.query.mode === 'view';

      // 设置tab标题
      this.tabList = [
        { value: '1', title: this.isViewMode ? '查看圈层' : '圈层维护' },
        { value: '2', title: '归属商户' },
      ];

      // 先检查路由查询参数中的tab参数
      const tabParam = this.$route.query.tab;

      this.resetForm();

      // 在resetForm之后重新设置tab参数，避免被重置
      if (tabParam) {
        this.currentTab = tabParam;
      }

      // 先获取父级圈层数据，然后再获取圈层信息
      this.getParentAreas()
        .then(() => {
          // 如果是编辑模式，获取圈层信息
          if (this.$route.params.id) {
            this.getAreaInfo(this.$route.params.id);
          } else {
            // 新增模式，检查是否有父级ID参数
            const parentId = this.$route.query.parentId;
            if (parentId) {
              this.formValidate.pid = parseInt(parentId);
            } else {
              this.formValidate.pid = 0; // 顶级圈层
            }
          }
        })
        .catch((error) => {
          console.error('获取父级圈层失败:', error);
          // 即使获取父级圈层失败，也要继续处理编辑模式
          if (this.$route.params.id) {
            this.getAreaInfo(this.$route.params.id);
          }
        });

      // 如果当前在第一个tab或者没有指定tab，且不是查看模式，自动初始化地图
      if ((!tabParam || tabParam === '1') && !this.isViewMode && !this.isMapInitialized) {
        // 延迟初始化地图，确保DOM已渲染
        this.$nextTick(() => {
          setTimeout(() => {
            this.initializeMap();
          }, 100);
        });
      }
    },

    // 重置表单数据
    resetForm() {
      this.formValidate = {
        id: null, // 圈层id，新建时为null，创建成功后设置
        merIds: [],
        pid: 0,
        name: '',
        range: '',
        realName: '',
        account: '',
        pwd: '',
        confirmPassword: '',
        phone: '',
        address: '',
        latitude: '',
        longitude: '',
        merchantList: [],
        addressSwitch: 2,
        role: undefined,
      };
      this.originalMerchantIds = []; // 重置原始商户ID列表
      this.currentTab = '1';
      this.loading = false;
      // 重置商户选择相关数据
      this.merchantDialogVisible = false;
      this.selectedMerchants = [];
      this.merchantForm.selectedMerchantId = '';
      this.bindingMerchant = false;
      // 重置地图渲染状态
      this.shouldRenderMap = false;
      this.hasVisitedMapTab = false;
      this.isMapInitialized = false;
      this.locationInfo = null;
      // 清除表单验证
      if (this.$refs.formValidate) {
        this.$refs.formValidate.clearValidate();
      }
    },

    // 腾讯地点选择器回传处理
    onMapPickerMessage(event) {
      if (!event || !event.data || event.data.module !== 'locationPicker') return;
      const { latlng, poiaddress, poiname } = event.data;
      if (latlng && typeof latlng.lat !== 'undefined' && typeof latlng.lng !== 'undefined') {
        // 确保经纬度保持字符串格式
        this.formValidate.latitude = String(latlng.lat);
        this.formValidate.longitude = String(latlng.lng);
      }
      const address = poiname ? `${poiname} ${poiaddress || ''}`.trim() : poiaddress || '';
      if (address) {
        this.formValidate.address = address;
      }
      // 日志便于排查：收到回传后可确认表单字段
      try {
        console.log('[locpicker] selected:', {
          lat: this.formValidate.latitude,
          lng: this.formValidate.longitude,
          address: this.formValidate.address,
        });
      } catch (e) {
        // 忽略postMessage错误
      }
    },
    // 打开商户选择弹窗
    openMerchantDialog() {
      this.merchantDialogVisible = true;
      this.merchantForm.selectedMerchantId = null;
      // 每次打开弹窗时刷新商户选择组件的数据，获取最新的可选商户列表
      this.$nextTick(() => {
        if (this.$refs.merchantSelector) {
          this.$refs.merchantSelector.refreshAreaMerchantData();
        }
      });
    },

    // 关闭商户选择弹窗
    closeMerchantDialog() {
      this.merchantDialogVisible = false;
      this.merchantForm.selectedMerchantId = null;
      this.bindingMerchant = false;
    },

    // 获取商户ID
    getMerId(merId) {
      // 确保ID类型一致，统一转为数字类型
      if (Array.isArray(merId)) {
        this.merchantForm.selectedMerchantId = merId.map((id) => Number(id));
      } else if (merId !== null && merId !== undefined) {
        this.merchantForm.selectedMerchantId = Number(merId);
      } else {
        this.merchantForm.selectedMerchantId = null;
      }
    },
    // 确认选择商户
    async confirmMerchantSelection() {
      if (!this.merchantForm.selectedMerchantId) {
        this.$message.warning('请选择商户');
        return;
      }

      try {
        // 等待异步获取商户详情数据
        const selectedMerchants = await this.getSelectedMerchantsData(this.merchantForm.selectedMerchantId);

        // 只进行本地添加，不调用任何API
        this.addMerchantsWithData(selectedMerchants);
        // 移除成功提示，只在最终保存时提示
        this.closeMerchantDialog();
      } catch (error) {
        console.error('添加商户失败:', error);
        this.$message.error('添加商户失败，请重试');
      }
    },

    // 根据商户ID获取商户详情数据
    async getSelectedMerchantsData(selectedIds) {
      const ids = Array.isArray(selectedIds) ? selectedIds : [selectedIds];
      const merchants = [];

      try {
        // 并发调用商户详情API
        const promises = ids.map((id) => {
          return this.merList.find((mer) => mer.id === id);
        });
        // const responses = await Promise.all(promises);

        promises.forEach((response, index) => {
          // 检查API响应结构

          // 通常API返回格式可能是 { data: {...} } 或直接是 {...}
          const merchantDetail = response;

          if (merchantDetail && merchantDetail.id) {
            merchants.push({
              id: merchantDetail.id,
              name: merchantDetail.name || '未知商户',
              realName: merchantDetail.realName || '',
              phone: merchantDetail.phone || '',
              merId: merchantDetail.id,
            });
          } else {
            // API调用失败时的备用数据
            merchants.push({
              id: ids[index],
              name: '商户' + ids[index],
              realName: '',
              phone: '',
              merId: ids[index],
            });
          }
        });
      } catch (error) {
        console.error('获取商户详情失败:', error);
        // 出错时使用备用数据
        ids.forEach((id) => {
          merchants.push({
            id: id,
            name: '商户' + id,
            realName: '',
            phone: '',
            merId: id,
          });
        });
      }

      return merchants;
    },

    // 添加商户完整数据到本地列表
    addMerchantsWithData(merchantsData) {
      this.initMerchantData();

      const merchantList = this.formValidate.merchantList || [];

      merchantsData.forEach((merchant) => {
        // 检查是否已存在
        const exists = merchantList.some((m) => this.getMerchantUniqueId(m) === merchant.id);

        if (!exists) {
          this.formValidate.merchantList.push(merchant);
        }
      });

      this.syncMerchantData();
    },

    // 本地添加商户
    addMerchantsLocally(selectedMerchantIds) {
      this.initMerchantData();

      const idsToAdd = Array.isArray(selectedMerchantIds) ? selectedMerchantIds : [selectedMerchantIds];
      const merchantList = this.formValidate.merchantList || [];

      idsToAdd.forEach((merId) => {
        const numMerId = Number(merId);
        // 检查是否已存在
        const exists = merchantList.some((m) => this.getMerchantUniqueId(m) === numMerId);

        if (!exists) {
          this.formValidate.merchantList.push({
            id: numMerId,
            name: '商户' + numMerId, // 临时名称，实际应通过API获取
            merId: numMerId,
          });
        }
      });

      this.syncMerchantData();
    },

    // 绑定商户到圈层
    async bindMerchantsToArea(areaId, selectedMerchantIds) {
      try {
        this.bindingMerchant = true;

        const response = await areaBindMerchantApi({
          id: areaId,
          merchantIds: selectedMerchantIds,
        });

        if (response) {
          // 移除中间提示，只在最终保存时显示成功提示
          // 商户绑定成功后主动刷新数据，确保表格显示最新状态
          this.proactiveRefreshMerchantData();
        }
      } catch (error) {
        console.error('绑定商户失败:', error);
        this.$message.error('绑定商户失败: ' + (error.message || '未知错误'));
      } finally {
        this.bindingMerchant = false;
      }
    },

    // 获取商户的唯一标识ID
    getMerchantUniqueId(merchant) {
      return Number(merchant.id || merchant.merId || merchant.merchantId);
    },

    // 获取表格数据 - 主动防御，确保数据准确性
    getTableData() {
      // 每次获取表格数据时都进行检查和同步
      const merchantList = this.formValidate.merchantList || [];

      // 静默检查数据一致性（只在开发模式下打印）
      if (process.env.NODE_ENV === 'development' && merchantList.length > 0) {
        const uniqueIds = new Set();
        const duplicates = [];

        merchantList.forEach((merchant) => {
          const id = this.getMerchantUniqueId(merchant);
          if (uniqueIds.has(id)) {
            duplicates.push(id);
          } else {
            uniqueIds.add(id);
          }
        });

        if (duplicates.length > 0) {
          console.warn('发现重复的商户ID:', duplicates);
        }
      }

      return merchantList;
    },

    // 初始化商户数据结构
    initMerchantData() {
      if (!this.formValidate.merchantList) {
        this.formValidate.merchantList = [];
      }
      if (!this.formValidate.merIds) {
        this.formValidate.merIds = [];
      }
    },

    // 同步商户ID数组与商户列表
    syncMerchantData() {
      this.initMerchantData();
      const merchantList = this.formValidate.merchantList || [];
      this.formValidate.merIds = merchantList.map((m) => this.getMerchantUniqueId(m));
    },

    // 处理删除商户操作 - 增强版本，确保获取正确的ID
    handleRemoveMerchant(row, index) {
      this.formValidate.merchantList.splice(index, 1);
    },

    // 移除商户
    removeMerchant(merchantId) {
      const areaId = this.$route.params.id || this.formValidate.id;
      const numMerchantId = Number(merchantId);

      // 判断该商户是否为后台数据还是前端临时添加
      const isFromBackend = this.isMerchantFromBackend(numMerchantId);

      this.$confirm('确定要移除该商户吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        if (isFromBackend && this.isEdit && areaId) {
          // 来自后台的数据，调用解绑API（不显示提示，等最终保存时统一提示）
          this.removeMerchantFromBackendSilent(areaId, numMerchantId);
        } else {
          // 前端临时添加的数据，只从本地删除（不显示提示）
          this.removeMerchantLocallySilent(numMerchantId);
        }
      });
    },

    // 判断商户是否来自后台数据
    isMerchantFromBackend(merchantId) {
      // 需要一个标识来区分商户来源
      // 方案1: 检查商户是否在页面初始化时就存在（即来自getAreaInfo接口）
      // 方案2: 给临时添加的商户添加特殊标记

      // 如果不是编辑模式，所有商户都是前端添加的
      if (!this.isEdit) {
        return false;
      }

      // 在编辑模式下，需要检查商户是否在初始数据中
      // 这里我们可以通过检查商户对象是否有完整的后台字段来判断
      const merchant = this.formValidate.merchantList?.find((m) => this.getMerchantUniqueId(m) === merchantId);

      if (!merchant) {
        return false;
      }

      // 如果商户有完整的后台字段（如createTime等），说明来自后台
      // 或者我们可以检查商户名是否是临时格式"商户123"
      const isTemporaryName = merchant.name && merchant.name.startsWith('商户') && /^商户\d+$/.test(merchant.name);

      // 如果是临时名称格式，说明是前端添加的；否则来自后台
      return !isTemporaryName;
    },

    // 从后台删除商户（调用解绑API）
    async removeMerchantFromBackend(areaId, merchantId) {
      try {
        const data = {
          id: areaId,
          merchantIds: [merchantId],
        };

        await areaRemoveMerchantApi(data);
        this.$message.success('商户解绑成功');

        // API调用成功后，从本地列表中移除
        this.removeMerchantLocally(merchantId);

        // 重新获取最新数据确保同步
        this.refreshCurrentData();
      } catch (error) {
        this.$message.error('商户解绑失败');
        console.error('商户解绑失败:', error);
      }
    },

    // 静默删除后台商户（不显示提示）
    async removeMerchantFromBackendSilent(areaId, merchantId) {
      try {
        const data = {
          id: areaId,
          merchantIds: [merchantId],
        };

        await areaRemoveMerchantApi(data);
        // 不显示成功提示，API调用成功后从本地列表中移除
        this.removeMerchantLocallySilent(merchantId);

        // 重新获取最新数据确保同步
        this.refreshCurrentData();
      } catch (error) {
        this.$message.error('商户解绑失败');
        console.error('商户解绑失败:', error);
      }
    },

    // 静默删除本地商户（不显示提示）
    removeMerchantLocallySilent(merchantId) {
      this.initMerchantData();

      // 确保merchantList存在
      const merchantList = this.formValidate.merchantList || [];
      const beforeLength = merchantList.length;

      // 删除商户列表中的项目
      this.formValidate.merchantList = merchantList.filter((merchant) => {
        return this.getMerchantUniqueId(merchant) !== merchantId;
      });

      // 同步更新merIds
      this.syncMerchantData();

      const afterLength = (this.formValidate.merchantList || []).length;

      if (beforeLength === afterLength) {
        console.warn('未找到要删除的商户，ID:', merchantId);
      }

      // 统一使用数据刷新机制，确保数据一致性
      this.refreshCurrentData();
    },

    // 本地删除商户
    removeMerchantLocally(merchantId) {
      this.initMerchantData();

      // 确保merchantList存在
      const merchantList = this.formValidate.merchantList || [];
      const beforeLength = merchantList.length;

      // 删除商户列表中的项目
      this.formValidate.merchantList = merchantList.filter((merchant) => {
        return this.getMerchantUniqueId(merchant) !== merchantId;
      });

      // 同步更新merIds
      this.syncMerchantData();

      const afterLength = (this.formValidate.merchantList || []).length;
      if (beforeLength === afterLength) {
        this.$message.warning('未找到要删除的商户');
      } else {
        this.$message.success('商户删除成功');
      }

      // 统一使用数据刷新机制，确保数据一致性
      this.refreshCurrentData();
    },

    // 刷新商户选择组件
    refreshMerchantSelector() {
      this.$nextTick(() => {
        if (this.$refs.merchantSelector) {
          // 确保merIds存在且不为空
          const merIds = this.formValidate.merIds || [];
          this.$refs.merchantSelector.merIds = merIds.length > 0 ? merIds : null;
          this.$refs.merchantSelector.refreshAreaMerchantData();
        }
      });
    },

    // 强制刷新表格数据
    forceRefreshTable() {
      this.$nextTick(() => {
        // 通过重新设置数组引用来触发表格刷新
        if (this.formValidate.merchantList) {
          this.formValidate.merchantList = [...this.formValidate.merchantList];
        }
      });
    },

    // 刷新当前页面数据 - 统一的数据刷新入口
    refreshCurrentData() {
      const areaId = this.$route.params.id || this.formValidate.id;

      if (this.isEdit && areaId) {
        // 编辑模式：重新获取服务器数据
        this.getMerchantList(areaId)
          .then(() => {
            this.refreshMerchantSelector();
            this.forceRefreshTable();
          })
          .catch((error) => {
            // 即使刷新失败，也要尝试刷新UI组件
            this.refreshMerchantSelector();
            this.forceRefreshTable();
          });
      } else {
        // 新增模式：同步本地数据并刷新UI
        this.syncMerchantData();
        this.refreshMerchantSelector();
        this.forceRefreshTable();
      }
    },

    // 主动刷新商户数据 - 防患于未然
    proactiveRefreshMerchantData() {
      const areaId = this.$route.params.id || this.formValidate.id;

      if (!areaId) {
        console.log('新增模式，无需从服务器获取数据');
        return;
      }

      if (this.isEdit) {
        // 静默刷新，不显示loading，用户无感知
        this.getMerchantList(areaId)
          .then(() => {
            this.forceRefreshTable();
          })
          .catch((error) => {
            console.warn('主动刷新商户数据失败，继续使用当前数据:', error);
          });
      } else {
        // 新增模式下确保数据同步
        this.syncMerchantData();
        this.forceRefreshTable();
      }
    },

    // 保存商户关联
    async saveMerchants() {
      const areaId = this.$route.params.id || this.formValidate.id;

      if (!areaId) {
        this.$message.error('请先保存圈层信息');
        return;
      }

      // 获取当前商户ID列表
      try {
        this.loading = true;
        let ids = this.formValidate.merchantList.map((item) => item.id);
        await this.bindMerchantsToArea(areaId, ids);

        this.$message.success('商户关联更新成功');
      } catch (error) {
        console.error('保存商户关联失败:', error);
        // this.$message.error('保存失败: ' + (error.message || '未知错误'))
      } finally {
        this.loading = false;
      }
    },

    // 下一步/确认
    saveArea(tab) {
      // 防止重复提交
      if (this.loading) {
        return;
      }

      // 验证第一个Tab的表单
      const fieldsToValidate = this.isEdit
        ? ['name', 'range', 'realName', 'account', 'phone', 'role', 'addressSwitch'] // 编辑时不验证密码
        : ['name', 'range', 'realName', 'account', 'pwd', 'confirmPassword', 'role', 'addressSwitch', 'phone'];
      // 如果开关开启 验证 address
      if (this.formValidate.addressSwitch === 1) {
        fieldsToValidate.push('address');
      }

      // 使用validate方法验证指定字段
      const validationPromises = fieldsToValidate.map((field) => {
        return new Promise((resolve) => {
          this.$refs.formValidate.validateField(field, (errorMessage) => {
            resolve(!errorMessage); // 如果没有错误消息，则验证通过
          });
        });
      });

      Promise.all(validationPromises)
        .then((results) => {
          const allValid = results.every((result) => result);
          if (allValid) {
            // 表单验证通过，调用相应的API保存圈层信息
            this.loading = true;

            // 构建提交数据，移除confirmPassword字段
            const submitData = {
              account: this.formValidate.account,
              address: this.formValidate.address,
              latitude: this.formValidate.latitude,
              longitude: this.formValidate.longitude,
              name: this.formValidate.name,
              phone: this.formValidate.phone,
              range: this.formValidate.range,
              realName: this.formValidate.realName,
              role: this.formValidate.role,
              addressSwitch: this.formValidate.addressSwitch,
            };

            // 如果是新增模式，添加密码字段
            if (!this.isEdit) {
              submitData.pwd = this.formValidate.pwd;
            }

            // 如果有父级ID，则添加到提交数据中
            if (this.formValidate.pid) {
              submitData.pid = this.formValidate.pid;
            }

            // 如果是编辑模式，添加ID字段
            if (this.isEdit) {
              submitData.id = this.$route.params.id;
            }

            // 根据模式选择API
            const api = this.isEdit ? areaUpdateApi : areaSaveApi;
            const successMessage = this.isEdit ? '圈层更新成功' : '圈层创建成功';

            api(submitData)
              .then((res) => {
                this.loading = false;
                this.$message.success(successMessage);

                // 无论新增还是编辑，都刷新当前页面数据
                if (this.$route.params.id) {
                  this.getAreaInfo(this.$route.params.id);
                } else {
                  // 新增模式下，获取刚创建的圈层信息并更新路由
                  const newAreaId = res;
                  if (newAreaId) {
                    // 设置当前创建的圈层id到表单数据中，供绑定商户时使用
                    this.formValidate.id = newAreaId;
                    // 更新路由参数，设置当前创建的圈层id
                    this.$router.replace(`/area/create/${newAreaId}`);
                    this.getAreaInfo(newAreaId);
                  }
                }

                // 成功保存后自动跳转到第二个tab（归属商户）
                this.currentTab = '2';
              })
              .catch((error) => {
                this.loading = false;
                this.$message.error('保存失败，请重试');
                console.error('保存圈层信息失败:', error);
              });
          } else {
            this.$message.error('请完善圈层信息');
          }
        })
        .catch((error) => {
          console.error('表单验证出错:', error);
          this.$message.error('表单验证失败');
        });
    },

    // 保存表单
    saveForm() {
      // 防止重复提交
      if (this.loading) {
        return Promise.reject(new Error('正在提交中，请勿重复操作'));
      }

      return new Promise((resolve, reject) => {
        this.$refs.formValidate.validate((valid) => {
          if (valid) {
            this.loading = true;

            // 构建提交数据，移除confirmPassword字段
            const submitData = {
              account: this.formValidate.account,
              address: this.formValidate.address,
              latitude: this.formValidate.latitude,
              longitude: this.formValidate.longitude,
              name: this.formValidate.name,
              phone: this.formValidate.phone,
              pwd: this.formValidate.pwd,
              range: this.formValidate.range,
              realName: this.formValidate.realName,
            };
            // 如果有父级ID，则添加到提交数据中
            if (this.formValidate.pid) {
              submitData.pid = this.formValidate.pid;
            }

            const api = this.isEdit ? areaUpdateApi : areaSaveApi;
            api(submitData)
              .then((res) => {
                this.loading = false;
                this.$message.success('操作成功');
                // 如果是新增，需要更新路由参数以获取新的圈层ID
                const newAreaId = (res.data && res.data.id) || res.id;
                if (!this.isEdit && newAreaId) {
                  // 设置当前创建的圈层id到表单数据中，供绑定商户时使用
                  this.formValidate.id = newAreaId;
                  this.$router.replace(`/area/create/${newAreaId}`);
                  // 获取新创建圈层的详情信息
                  this.getAreaInfo(newAreaId);
                }
                resolve(res);
              })
              .catch((error) => {
                this.loading = false;
                this.$message.error('操作失败');
                reject(error);
              });
          } else {
            this.$message.error('请完善表单信息');
            reject(new Error('表单验证失败'));
          }
        });
      });
    },

    // 扁平化树形数据
    flattenTreeData(treeData, level = 0) {
      let result = [];
      if (Array.isArray(treeData)) {
        treeData.forEach((item) => {
          // 根据层级添加不同的前缀来区分不同层级
          let prefix = '';
          let isDisabled = false;
          if (level === 1) {
            // 二级：添加短横线
            prefix = '├─ ';
          } else if (level === 2) {
            // 三级：添加双短横线
            prefix = '├── ';
            isDisabled = true
          } else if (level > 2) {
            // 四级及以上：添加更多横线
            prefix = '├' + '─'.repeat(level) + ' ';
            isDisabled = true
          }

          result.push({
            id: item.id,
            name: level > 0 ? prefix + item.name : item.name,
            pid: item.pid,
            disabled: isDisabled
          });
          // 如果有子节点，递归处理
          if (item.childList && item.childList.length > 0) {
            result = result.concat(this.flattenTreeData(item.childList, level + 1));
          }
        });
      }
      return result;
    },

    // 获取父级圈层列表
    getParentAreas() {
      // 添加分页参数
      const params = {
        page: 1,
        limit: 999, // 设置一个较大的值以获取更多数据
      };
      return areaListTreeApi(params)
        .then((res) => {
          // 从API响应中提取list数据
          const areaList = res.data && res.data.list ? res.data.list : res.list || res || [];
          // 将树形数据扁平化处理
          this.parentAreas = this.flattenTreeData(areaList);
          return res;
        })
        .catch((error) => {
          console.error('获取父级圈层失败:', error);
          this.parentAreas = [];
          throw error;
        });
    },

    // 获取圈层信息（编辑时使用）
    getAreaInfo(id) {
      // 如果已经在加载中，直接返回
      if (this.loading) {
        return Promise.resolve();
      }

      this.loading = true;
      return new Promise((resolve, reject) => {
        areaInfoApi(id)
          .then((res) => {
            // 在这个项目中，res就直接是data，添加数据验证
            if (!res) {
              this.loading = false;
              reject(new Error('获取圈层信息失败：数据为空'));
              return;
            }

            this.formValidate = {
              id: res.id || id, // 设置圈层id
              pid: res.pid !== undefined ? res.pid : '',
              name: res.name || '',
              range: res.range || '',
              realName: res.realName || '',
              account: res.account || '',
              pwd: '',
              confirmPassword: '',
              phone: res.phone || '',
              address: res.address || '',
              latitude: res.latitude ? String(res.latitude) : '',
              longitude: res.longitude ? String(res.longitude) : '',
              merchantList: res.merchantList || [],
              addressSwitch: res.addressSwitch || 2,
              role: res.role || null,
            };

            // 记录原始商户ID列表，用于区分新增商户
            this.originalMerchantIds = (res.merchantList || []).map((m) => this.getMerchantUniqueId(m));
            // 如果有地址信息，显示位置信息并初始化地图
            if (res.latitude && res.longitude && res.address) {
              this.locationInfo = {
                lat: res.latitude,
                lng: res.longitude,
                addr: res.address,
                accuracy: 0,
              };

              // 设置地图位置数据，v-if确保只有在第一个tab时才会渲染地图组件
              this.mapLocation = {
                lat: res.latitude ? String(res.latitude) : '',
                lng: res.longitude ? String(res.longitude) : '',
                address: res.address,
              };
            } else {
              // 确保即使没有地址信息，mapLocation也有默认值
              this.locationInfo = null;
              this.mapLocation = {};
            }

            // 商户列表数据已通过areaInfoApi获取并赋值到formValidate.merchantList

            this.loading = false;
            resolve(res);
          })
          .catch((err) => {
            this.loading = false;
            this.$message.error('获取圈层信息失败');
            reject(err);
          });
      });
    },

    // 获取圈层关联的商户列表
    getMerchantList(areaId) {
      if (!areaId) {
        return Promise.resolve();
      }

      // 重新获取圈层信息，包括商户列表
      return this.getAreaInfo(areaId).catch((error) => {
        console.error('重新获取圈层信息失败:', error);
        // 确保即使失败也有默认的空数组
        if (!this.formValidate.merchantList) {
          this.formValidate.merchantList = [];
        }
        // 即使失败也要返回resolved状态，避免阻塞后续操作
        return Promise.resolve();
      });
    },

    // 地图位置变化事件处理
    onLocationChange(locationData) {
      // 更新表单字段
      this.formValidate.latitude = String(locationData.lat);
      this.formValidate.longitude = String(locationData.lng);
      this.formValidate.address = locationData.address;

      // 更新地图位置数据
      this.mapLocation = locationData.locationInfo;
    },

    // 地图准备就绪事件处理
    onMapReady(map) {
      console.log('地图准备就绪:', map);
    },

    // 地图错误事件处理
    onMapError(error) {
      console.error('地图错误:', error);

      // 检查是否是"far <= 0"错误
      if (error.message && error.message.includes('far')) {
        console.warn('检测到地图距离计算错误，尝试重新初始化地图');
        this.$message.warning('地图初始化异常，正在重新加载...');

        // 延迟重新初始化地图
        setTimeout(() => {
          if (this.$refs.tencentMap && this.$refs.tencentMap.reinitializeMap) {
            this.$refs.tencentMap.reinitializeMap();
          }
        }, 1000);
      } else {
        const errorMessage = error && error.message ? error.message : error || '未知错误';
        this.$message.error('地图加载失败：' + errorMessage);
      }
    },

    // 显示坐标信息
    showCoordinates() {
      const lat = this.formValidate.latitude;
      const lng = this.formValidate.longitude;
      const address = this.formValidate.addressDetail || '地址信息获取中...';

      this.$alert(
        `<div style="text-align: left;">
          <p><strong>纬度：</strong>${lat}</p>
          <p><strong>经度：</strong>${lng}</p>
          <p><strong>地址：</strong>${address}</p>
        </div>`,
        '位置信息',
        {
          dangerouslyUseHTMLString: true,
          confirmButtonText: '确定',
        },
      );
    },

    // 初始化地图
    initializeMap() {
      // 防止重复初始化
      if (this.isMapInitialized) {
        return;
      }

      this.shouldRenderMap = true;
      this.hasVisitedMapTab = true;
      this.isMapInitialized = true;

      // 等待地图组件渲染后设置位置
      this.$nextTick(() => {
        setTimeout(() => {
          if (this.locationInfo && this.locationInfo.lat && this.locationInfo.lng) {
            this.mapLocation = {
              lat: parseFloat(this.locationInfo.lat),
              lng: parseFloat(this.locationInfo.lng),
              address: this.locationInfo.addr || this.formValidate.address,
            };
          }
        }, 300);
      });
    },
  },
};
</script>

<style scoped>
.merchant-management {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 6px;
  margin-top: 15px;
}

.merchant-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.merchant-count {
  color: #909399;
  font-size: 14px;
}

.merchant-list {
  margin-top: 15px;
}

.merchant-table-container {
  margin-top: 20px;
}

.merchant-table-container .el-table {
  margin-bottom: 0;
}

.merchant-table-container .el-table th {
  background-color: #fafafa;
  color: #606266;
  font-weight: 500;
}

.merchant-table-container .el-table td {
  padding: 12px 0;
}

.merchant-table-container .el-table .el-button--text {
  color: #409eff;
  padding: 0;
}

.merchant-table-container .el-table .el-button--text:hover {
  color: #66b1ff;
}

.empty-table-tip {
  text-align: center;
  padding: 20px;
  background-color: #fafafa;
  border: 1px solid #ebeef5;
  border-top: none;
}

.empty-table-tip p {
  margin: 0 0 15px 0;
  color: #909399;
  font-size: 14px;
}

.merchant-dialog-content {
  min-height: 300px;
}

.dialog-footer {
  text-align: right;
}

.merchant-section h3 {
  color: #303133;
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
}

.merchant-section .desc {
  color: #909399;
  font-size: 14px;
  margin-bottom: 20px;
}
.location-success-tip {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background-color: #f0f9ff;
  border: 1px solid #b3e5fc;
  border-radius: 4px;
  margin-bottom: 10px;
}

.merchant-section {
  text-align: center;
}

.merchant-section h3 {
  margin-bottom: 10px;
  color: #303133;
}

.merchant-section .desc {
  color: #909399;
  margin-bottom: 30px;
}

.form-footer {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  /* border-top: 1px solid #ebeef5; */
}

.primary_border {
  border: 1px solid #409eff;
  color: #409eff;
}

.location-picker {
  width: 100%;
}

.location-buttons {
  margin-bottom: 15px;
}

.location-buttons .el-button {
  margin-right: 10px;
}

.location-info {
  background-color: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 15px;
  margin-top: 10px;
}

.location-info p {
  margin: 5px 0;
  color: #606266;
  font-size: 14px;
}

.location-info strong {
  color: #303133;
}

.map-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.map-box {
  width: 100%;
  height: 300px;
}

.map-controls {
  padding: 10px;
  background-color: #f5f7fa;
  border-top: 1px solid #e4e7ed;
  text-align: center;
}

.map-controls .el-button {
  margin: 0 5px;
}

.map-tips {
  padding: 8px 10px;
  background-color: #ecf5ff;
  border-top: 1px solid #b3d8ff;
  text-align: center;
}

.map-tips p {
  margin: 0;
  color: #409eff;
  font-size: 12px;
}

.map-placeholder {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fafafa;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
}

.placeholder-content {
  text-align: center;
}
</style>
