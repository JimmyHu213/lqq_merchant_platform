package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.product.ProductUnit;
import com.zbkj.common.request.ProductUnitAddRequest;

import java.util.List;

/**
* @author zzp
* @description ProductUnitService 接口
* @date 2025-11-26
*/
public interface ProductUnitService extends IService<ProductUnit> {

    List<ProductUnit> getList();

    Boolean saveUnit(ProductUnitAddRequest request);

    Boolean updateUnit(ProductUnitAddRequest request);

    Boolean findUnitByName(String name);
}