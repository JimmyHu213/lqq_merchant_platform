package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.dto.Location;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.circle.Circle;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.AddressUtil;
import com.zbkj.common.vo.CirCleCheckVo;
import com.zbkj.common.vo.CircleTree;
import com.zbkj.front.request.RegionAreasSearchRequest;
import com.zbkj.front.service.CircleAreasService;
import com.zbkj.service.service.CircleService;
import com.zbkj.service.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商圈服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/8/22
 */
@Slf4j
@Service
public class CircleAreasServiceImpl implements CircleAreasService {

    @Autowired
    private CircleService circleService;
    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取圈层列表-经纬度
     */
    @Override
    public List<Circle> findListByLocation(RegionAreasSearchRequest request) {
        if (StrUtil.isBlank(request.getLongitude())
                || StrUtil.isBlank(request.getLatitude())
                || ObjectUtil.isNull(request.getDistance())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "经纬度参数缺失");
        }
        if (request.getLatitude().equals(0) && request.getLongitude().equals("0")) {
            return new ArrayList<>();
        }
        if (request.getDistance() <= 0) return new ArrayList<>();
        // 获取所有的结点商圈
        List<Circle> areasList = circleService.findAllDestination();
        if (CollUtil.isEmpty(areasList)) {
            return areasList;
        }
        int distance = request.getDistance() * 1000;
        double lat = Double.parseDouble(request.getLatitude());
        double lon = Double.parseDouble(request.getLongitude());
        for (int i = 0; i < areasList.size(); ) {
            Circle circle = areasList.get(i);
            double lat1 = Double.parseDouble(circle.getLatitude());
            double lon1 = Double.parseDouble(circle.getLongitude());
            // 计算是否满足距离
            double calculateDistance = AddressUtil.calculateDistance(lat, lon, lat1, lon1);
            if ((double) distance < calculateDistance) {
                areasList.remove(i);
                continue;
            }
            i++;
        }
        return areasList;
    }

    /**
     * 获取圈层列表-地址
     */
    @Override
    public List<Circle> findListByAddress(RegionAreasSearchRequest request) {
        if (StrUtil.isBlank(request.getAddress())
                || ObjectUtil.isNull(request.getDistance())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "经纬度参数缺失");
        }
        if (request.getDistance() <= 0) return new ArrayList<>();

        String address = URLUtil.decode(request.getAddress()).trim();
        Location location;
        try {
            String txKey = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_SITE_TENG_XUN_MAP_KEY);
            location = AddressUtil.getLocationByTx(txKey, address);
        } catch (Exception e) {
            log.error("通过腾讯地图，解析地址获取经纬度失败！地址 = {}", address);
            log.error("通过腾讯地图，解析地址获取经纬度失败！", e);
            return new ArrayList<>();
        }

        // 获取所有的结点商圈
        List<Circle> areasList = circleService.findAllDestination();
        if (CollUtil.isEmpty(areasList)) {
            return areasList;
        }
        int distance = request.getDistance() * 1000;
        double lat = Double.parseDouble(location.getLatitude());
        double lon = Double.parseDouble(location.getLongitude());
        for (int i = 0; i < areasList.size(); ) {
            Circle circle = areasList.get(i);
            double lat1 = Double.parseDouble(circle.getLatitude());
            double lon1 = Double.parseDouble(circle.getLongitude());
            // 计算是否满足距离
            double calculateDistance = AddressUtil.calculateDistance(lat, lon, lat1, lon1);
            if ((double) distance < calculateDistance) {
                areasList.remove(i);
                continue;
            }
            i++;
        }
        return areasList;
    }

    /**
     * 获取商圈树
     */
    @Override
    public List<CirCleCheckVo> getCircleTree() {
        List<Circle> circleList = circleService.findOpenAll();
        if (CollUtil.isEmpty(circleList)) return new ArrayList<>();
        List<CirCleCheckVo> checkVoList = circleList.stream().map(circle -> {
            CirCleCheckVo cleCheckVo = new CirCleCheckVo();
            cleCheckVo.setId(circle.getId());
            cleCheckVo.setName(circle.getName());
            cleCheckVo.setPid(circle.getPid());
            return cleCheckVo;
        }).collect(Collectors.toList());
        return new CircleTree(checkVoList).buildTree();
    }
}
