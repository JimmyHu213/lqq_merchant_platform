package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.circle.Circle;
import com.zbkj.common.request.circle.CircleAddRequest;
import com.zbkj.common.request.circle.CircleConjunctiveDiyRequest;
import com.zbkj.common.request.circle.CircleMerchantRequest;
import com.zbkj.common.request.circle.CircleUpdateRequest;
import com.zbkj.common.response.circle.CircleDetailResponse;
import com.zbkj.common.response.circle.CircleMerchantResponse;
import com.zbkj.common.response.circle.CircleTreeResponse;

import java.util.List;
import java.util.Map;

/**
* @author zzp
* @description CircleService 接口
* @date 2025-08-04
*/
public interface CircleService extends IService<Circle> {

    /**
     * 显示圈层管理列表
     */
    List<CircleTreeResponse> getListTree();

    /**
     * 新增圈层
     *
     * @param request 新增参数
     * @return 新增结果
     */
    Integer add(CircleAddRequest request);

    /**
     * 删除圈层
     *
     * @param id 圈层ID
     * @return 删除结果
     */
    Boolean removeCircleById(Integer id);

    /**
     * 修改圈层
     *
     * @param request 修改参数
     * @return 修改结果
     */
    Boolean updateCircle(CircleUpdateRequest request);


    /**
     * 查询圈层详情
     *
     * @param id 圈层ID
     * @return 圈层详情
     */
    CircleDetailResponse getCircleId(Integer id);

    /**
     * 重置圈层密码
     *
     * @param id 圈层ID
     * @return 重置结果
     */
    Boolean resetPassword(Integer id);

    /**
     * 更新圈层状态
     *
     * @param id 圈层ID
     * @return 更新结果
     */
    Boolean updateStatus(Integer id);

    /**
     * 更新圈层显示状态
     *
     * @param id 圈层ID
     * @return 更新结果
     */
    Boolean updateShow(Integer id);


    /**
     * 获取圈层下的商家列表
     *
     * @param id 圈层ID
     * @return 商家列表
     */
    List<CircleMerchantResponse> getMerchantsByCircleId(Integer id);

    /**
     * 圈层关联商户
     *
     * @param request 关联商户请求
     * @return 关联结果
     */
    Boolean bindMerchant(CircleMerchantRequest request);

    /**
     * 圈层解绑商户
     *
     * @param request  解绑商户请求
     * @return 解绑结果
     */
    Boolean removeMerchant(CircleMerchantRequest request);

    /**
     * 根据圈层id 查询下级圈层id集合
     *
     */
    List<Integer> getCircleChildById(Integer circleId);

    /**
     * 获取所有的终点商圈
     */
    List<Circle> findAllDestination();

    /**
     * 获取所有打开的圈层
     */
    List<Circle> findOpenAll();

    /**
     * 圈层关联DIY模板
     */
    Boolean conjunctiveDiy(CircleConjunctiveDiyRequest request);

    Map<Integer, String> getCircleNameMapByDiyIdList(List<Integer> diyIdList);
}