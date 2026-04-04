// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { login, logout, getInfo } from '@/api/user';
import { areasLoginApi, areasGetAdminInfoByTokenApi, areasLogoutApi, areasGetMenusApi } from '@/api/area';
import { getToken, setToken, removeToken } from '@/utils/auth';
import router, { resetRouter } from '@/router';
import { isLoginApi } from '@/api/sms';
import Cookies from 'js-cookie';
import { Loading } from 'element-ui';
import * as roleApi from '@/api/roleApi.js';
import { formatFlatteningRoutes } from '@/utils/system.js';
import { isPlatform } from '@/utils/settingMer';
function getMenusName() {
  return isPlatform
    ? JSON.parse(localStorage.getItem('MerPlatAdmin_MenuList')) || []
    : localStorage.getItem('Circle_Admin_MenuList')
    ? JSON.parse(localStorage.getItem('Circle_Admin_MenuList'))
    : [];
}
const state = {
  token: getToken(),
  circleToken: getToken(),
  name: '',
  avatar: '',
  introduction: '',
  roles: [],
  circleRoles: [],
  isLogin: Cookies.get('isLogin'),
  permissions: [],
  circlePermissions: [],
  userInfo: null, // 存储完整的用户信息
  circleUserInfo: null, // 存储完整的用户信息
  captcha: {
    captchaVerification: '',
    secretKey: '',
    token: '',
  }, //滑块验证token
  // 菜单数据
  menuList: getMenusName(),
  oneLvMenus: [],
  circleOneLvMenus: [],
  oneLvRoutes: JSON.parse(localStorage.getItem('MerPlatAdmin_oneLvRoutes')) || [],
  circleOneLvRoutes: JSON.parse(localStorage.getItem('Circle_Admin_oneLvRoutes')) || [],
  childMenuList: [],
  circleChildMenuList: [],
};

const mutations = {
  SET_TOKEN: (state, token) => {
    if (isPlatform) {
      state.token = token;
    } else {
      state.circleToken = token;
    }
  },
  SET_ISLOGIN: (state, isLogin) => {
    state.isLogin = isLogin;
    Cookies.set('isLogin', isLogin);
  },
  SET_INTRODUCTION: (state, introduction) => {
    state.introduction = introduction;
  },
  SET_NAME: (state, name) => {
    state.name = name;
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar;
  },
  SET_ROLES: (state, roles) => {
    if (isPlatform) {
      state.roles = roles;
    } else {
      state.circleRoles = roles;
    }
  },
  SET_PERMISSIONS: (state, permissions) => {
    if (isPlatform) {
      state.permissions = permissions;
    } else {
      state.circlePermissions = permissions;
    }
  },
  SET_USER_INFO: (state, userInfo) => {
    if (isPlatform) {
      state.userInfo = userInfo;
    } else {
      state.circleUserInfo = userInfo;
    }
  },
  SET_CAPTCHA: (state, captcha) => {
    state.captcha = captcha;
  },
  SET_MENU_LIST: (state, menuList) => {
    if (isPlatform) {
      state.menuList = menuList;
      localStorage.setItem('MerPlatAdmin_MenuList', JSON.stringify(menuList));
    } else {
      for (let i = 0; i < menuList.length; i++) {
        menuList[i].path = '/circle' + menuList[i].path;
        if (!menuList[i].children) {
          continue;
        }
      }
      state.menuList = menuList;
      localStorage.setItem('Circle_Admin_MenuList', JSON.stringify(menuList));
    }
  },
  setOneLvMenus(state, oneLvMenus) {
    if (isPlatform) {
      state.oneLvMenus = oneLvMenus;
    } else {
      for (let i = 0; i < oneLvMenus.length; i++) {
        oneLvMenus[i].path = '/circle' + oneLvMenus[i].path;
      }
      state.circleOneLvMenus = oneLvMenus;
    }
  },
  setOneLvRoute(state, oneLvRoutes) {
    if (isPlatform) {
      state.oneLvRoutes = oneLvRoutes;
    } else {
      state.circleOneLvRoutes = oneLvRoutes;
    }
  },
  childMenuList(state, list) {
    if (isPlatform) {
      state.childMenuList = list;
    }else{
      state.circleChildMenuList = list;
    }
  },
};

const actions = {
  // user login
  login({ commit, dispatch }, userInfo) {
    const { account, pwd, key, code, wxCode } = userInfo;
    Loading.service();
    return new Promise((resolve, reject) => {
      // 根据环境变量判断调用不同的登录API
      const loginApi = isPlatform ? login : areasLoginApi;

      loginApi(userInfo)
        .then((data) => {
          let loadingInstance = Loading.service();
          loadingInstance.close();
          commit('SET_TOKEN', data.token);
          if (isPlatform) {
            Cookies.set('JavaPlatInfo', JSON.stringify(data));
          } else {
            Cookies.set('CircleJavaPlatInfo', JSON.stringify(data));
          }
          setToken(data.token);
          // 清空之前用户的tab缓存，避免权限冲突
          commit('menu/clearAll', null, { root: true });
          resolve();
        })
        .catch((error) => {
          reject(error);
        });
    });
  },

  // 短信是否登录
  isLogin({ commit }, userInfo) {
    return new Promise((resolve, reject) => {
      isLoginApi()
        .then(async (res) => {
          commit('SET_ISLOGIN', res.isLogin);
          resolve(res);
        })
        .catch((res) => {
          commit('SET_ISLOGIN', false);
          reject(res);
        });
    });
  },

  // get user info
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      // 根据环境变量判断调用不同的获取用户信息API
      const getUserInfoApi = isPlatform ? () => getInfo(state.token) : areasGetAdminInfoByTokenApi;

      getUserInfoApi()
        .then((data) => {
          if (!data) {
            reject('Verification failed, please Login again.');
          }
          const { roles, account } = data;
          // roles must be a non-empty array
          if (!roles || roles.length <= 0) {
            reject('getInfo: roles must be a non-null array!');
          }

          commit('SET_ROLES', roles);
          // commit('SET_ROLES', ['admin'])
          commit('SET_NAME', account);
          // commit('SET_AVATAR', avatar)
          commit('SET_AVATAR', 'http://kaifa.crmeb.net/system/images/admin_logo.png');
          commit('SET_INTRODUCTION', 'CRMEB admin');
          commit('SET_PERMISSIONS', data.permissionsList); //权限标识
          commit('SET_USER_INFO', data); // 存储完整的用户信息
          resolve(data);
        })
        .catch((error) => {
          reject(error);
        });
    });
  },

  // user logout
  handleLogout({ commit, state, dispatch }) {
    Loading.service();
    return new Promise((resolve, reject) => {
      // 根据环境变量判断调用不同的退出登录API
      const logoutApi = isPlatform ? () => logout(state.token) : areasLogoutApi;

      logoutApi()
        .then(() => {
          let loadingInstance = Loading.service();
          loadingInstance.close();
          commit('SET_TOKEN', '');
          commit('SET_ROLES', []);
          commit('SET_PERMISSIONS', []);
          removeToken();
          // localStorage.clear();
          Cookies.remove('storeStaffList');
          dispatch('tagsView/delAllViews', null, { root: true });
          if (isPlatform) {
            Cookies.remove('JavaPlatInfo');
            sessionStorage.removeItem('token');
            // reset visited views and cached views
            // to fixed https://github.com/PanJiaChen/vue-element-admin/issues/2485
            resetRouter();
            resolve();
          } else {
            Cookies.remove('CircleJavaPlatInfo');
            resetRouter();
            resolve();
          }
        })
        .catch((error) => {
          reject(error);
        });
    });
  },

  // remove token
  resetToken({ commit, dispatch }) {
    return new Promise((resolve) => {
      commit('SET_TOKEN', '');
      commit('SET_ROLES', []);
      removeToken();
      // 清空tab缓存，避免无权限的菜单残留
      commit('menu/clearAll', null, { root: true });
      resolve();
    });
  },
  // 设置token
  setToken({ commit }, state) {
    return new Promise((resolve) => {
      commit('SET_TOKEN', state.token);
      if (isPlatform) {
        Cookies.set('JavaPlatInfo', JSON.stringify(state));
      } else {
        Cookies.set('CircleJavaPlatInfo', JSON.stringify(state));
      }
      setToken(data.token);
      resolve();
    });
  },
  getMenus({ commit }) {
    function formatTwoStageRoutes(arr) {
      if (arr.length <= 0) return false;
      const newArr = [];
      const cacheList = [];
      arr.forEach((v) => {
        if (v && v.meta && v.meta.keepAlive) {
          newArr.push({ ...v });
          cacheList.push(v.name);
          this.$store.dispatch('keepAliveNames/setCacheKeepAlive', cacheList);
        }
      });
      return newArr;
    }

    return new Promise(async (resolve, reject) => {
      // 根据环境变量判断调用不同的获取菜单API
      const getMenuApi = isPlatform ? roleApi.menuListApi : areasGetMenusApi;
      let accessRoutes = await getMenuApi();

      // let accessRoutes = formatRoutes(menusAll);
      // const accessRoutes = await dispatch('permission/generateRoutes', roles, { root: true });
      commit('SET_MENU_LIST', accessRoutes);
      if (isPlatform) {
        let arr = formatFlatteningRoutes(router.options.routes);
        formatTwoStageRoutes(arr);
        let routes = formatFlatteningRoutes(accessRoutes);
        localStorage.setItem('MerPlatAdmin_oneLvRoutes', JSON.stringify(routes));
        commit('setOneLvMenus', arr);
        commit('setOneLvRoute', routes);
      } else {
        let arr = formatFlatteningRoutes(router.options.routes);
        formatTwoStageRoutes(arr);
        let routes = formatFlatteningRoutes(accessRoutes);
        for (let i = 0; i < routes.length; i++) {
          // 第一个/后的单词不为circle，添加/circle
          if (routes[i].path && routes[i].path.split('/')[1] !== 'circle') {
            routes[i].path = '/circle' + routes[i].path;
          }
        }
        localStorage.setItem('Circle_Admin_oneLvRoutes', JSON.stringify(routes));
      }
      resolve(resolve);
    });
  },
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
};
