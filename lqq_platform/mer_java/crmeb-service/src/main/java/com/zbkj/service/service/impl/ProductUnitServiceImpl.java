package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.product.ProductUnit;
import com.zbkj.common.request.ProductUnitAddRequest;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.ProductUnitDao;
import com.zbkj.service.service.ProductUnitService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author zzp
* @description ProductUnitServiceImpl 接口实现
* @date 2025-11-26
*/
@Service
public class ProductUnitServiceImpl extends ServiceImpl<ProductUnitDao, ProductUnit> implements ProductUnitService {

    @Resource
    private ProductUnitDao dao;


    /**
    * 列表
    * @author zzp
    * @since 2025-11-26
    * @return List<ProductUnit>
    */
    @Override
    public List<ProductUnit> getList() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        LambdaQueryWrapper<ProductUnit> query = new LambdaQueryWrapper<>();
        query.eq(ProductUnit::getMerId, systemAdmin.getId());
        query.orderByDesc(ProductUnit::getSort).orderByDesc(ProductUnit::getId);
        return dao.selectList(query);
    }

    /**
    * 保存单位
    * @author zzp
    * @since 2025-11-26
    * @param request ProductUnitAddRequest
    * @return Boolean
    */
    @Override
    public Boolean saveUnit(ProductUnitAddRequest request) {
        if(findUnitByName(request.getName())) {
            throw new RuntimeException("该商户下该单位名称已存在");
        }
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        ProductUnit productUnit = new ProductUnit();
        BeanUtils.copyProperties(request, productUnit);
        productUnit.setMerId(systemAdmin.getId());
        return save(productUnit);
    }

    /**
    * 更新单位
    * @author zzp
    * @since 2025-11-26
    * @param request ProductUnitAddRequest
    * @return Boolean
    */
    @Override
    public Boolean updateUnit(ProductUnitAddRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();

        LambdaQueryWrapper<ProductUnit> query = new LambdaQueryWrapper<>();
        query.eq(ProductUnit::getName, request.getName());
        query.eq(ProductUnit::getMerId, systemAdmin.getId());
        query.ne(ProductUnit::getId, request.getId());
        if (dao.selectCount(query) > 0 ) {
            throw new RuntimeException("该商户下该单位名称已存在");
        }
        LambdaUpdateWrapper<ProductUnit> update = new LambdaUpdateWrapper<>();
        update.eq(ProductUnit::getId, request.getId());
        update.set(ProductUnit::getName, request.getName());
        update.set(ProductUnit::getSort, request.getSort());
        return update(update);
    }

    /**
    * 根据单位名称查询是否存在
    * @author zzp
    * @since 2025-11-26
    * @param name String
    * @return Boolean
    */
    @Override
    public Boolean findUnitByName(String name) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        LambdaQueryWrapper<ProductUnit> query = new LambdaQueryWrapper<>();
        query.eq(ProductUnit::getName, name);
        query.eq(ProductUnit::getMerId, systemAdmin.getId());
        return dao.selectCount(query) > 0;
    }

}

