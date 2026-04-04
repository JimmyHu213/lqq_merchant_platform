package com.zbkj.service.dao.circle;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.circle.CircleMenu;

import java.util.List;

/**
 * <p>
 * 圈层菜单表 Mapper 接口
 * </p>
 *
 * @author zzp
 * @since 2025-09-16
 */
public interface CircleMenuDao extends BaseMapper<CircleMenu> {

    List<CircleMenu> findPermissionByUserId(Integer uid);

    List<CircleMenu> getMenusByUserId(Integer uid);
}
