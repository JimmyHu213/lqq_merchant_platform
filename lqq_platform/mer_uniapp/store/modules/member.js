// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import {memberLevelListApi, memberUserInfoApi} from "../../pages/member/memberApi";
import Vue from "vue";
import {getMerchantNavigationApi} from "../../api/merchant";
export default {
    state(){
        return {
            merLevelMap: {}, // 店铺会员等级对象，存储各店铺的等级
            merMemberInfo: {}, // 商户会员信息
            merNavigation: {} // 商户底部菜单
          //  memberLevelList: []
        }
    },
    mutations:{
        // 商户会员等级列表
        SET_MEMBER_LEVEL(state, {merId, data}){
            Vue.set(state.merLevelMap, merId, data)
        },
        MEMBER_INFO(state, data){
            state.merMemberInfo = data
         //   Vue.set(state.merMemberInfo, data)
        },
        // 商户底部菜单数据
        SET_MER_NAVIGATION(state, {merId, data}){
            Vue.set(state.merNavigation, merId, data)
        }
    },
    actions: {
        //店铺会员信息
        async getMerMemberInfo({ state,commit }, merId = 0){
            try {
                let { data } = await memberUserInfoApi(merId)
                commit('MEMBER_INFO', data);
                return data;
            }catch (error) {
                throw error;
            }
        },
        async getMemberLevelList({ state, dispatch }, merId = 0) { // 会员等级
            console.log('state.merLevelMap[merId]',state.merLevelMap[merId])
            if (state.merLevelMap[merId]) return state.merLevelMap[merId];
            return dispatch("fetchMemberLevel", merId);
        },
        async fetchMemberLevel({ commit }, merId = 0) {
            try {
                const { data } = await memberLevelListApi(merId);
                commit("SET_MEMBER_LEVEL", {merId, data});
                return data;
            }catch (error) {
                throw error;
            }
        },
        // 店铺底部导航
        async getMerNavigation({ state, dispatch }, merId = 0) {
            if (state.merNavigation[merId]) return state.merNavigation[merId];
            return dispatch("fetchMerNavigation", merId);
        },
        async fetchMerNavigation({ commit }, merId = 0) {
            try {
                const { data } = await getMerchantNavigationApi(merId);
                commit("SET_MER_NAVIGATION", {merId, data});
                return data;
            }catch (error) {
                throw error;
            }
        }
    }
}