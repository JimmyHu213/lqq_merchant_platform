package com.zbkj.service.dao.circle;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.circle.Circle;
import com.zbkj.common.response.circle.CircleTreeResponse;

import java.util.List;

/**
 * <p>
 * 圈层管理表 Mapper 接口
 * </p>
 *
 * @author zzp
 * @since 2025-08-04
 */
public interface CircleDao extends BaseMapper<Circle> {


    List<CircleTreeResponse> getList();

    List<Circle> findAllDestination();

}
