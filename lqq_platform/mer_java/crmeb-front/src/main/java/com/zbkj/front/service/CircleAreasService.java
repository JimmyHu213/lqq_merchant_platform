package com.zbkj.front.service;

import com.zbkj.common.model.circle.Circle;
import com.zbkj.common.vo.CirCleCheckVo;
import com.zbkj.front.request.RegionAreasSearchRequest;

import java.util.List;

/**
 * 商圈服务类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/22
 */
public interface CircleAreasService {

    /**
     * 获取圈层列表-经纬度
     */
    List<Circle> findListByLocation(RegionAreasSearchRequest request);

    /**
     * 获取圈层列表-地址
     */
    List<Circle> findListByAddress(RegionAreasSearchRequest request);

    /**
     * 获取商圈树
     */
    List<CirCleCheckVo> getCircleTree();
}
