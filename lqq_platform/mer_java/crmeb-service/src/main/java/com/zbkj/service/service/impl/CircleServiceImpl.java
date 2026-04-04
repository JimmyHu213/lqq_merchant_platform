package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.enums.RoleEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.circle.Circle;
import com.zbkj.common.model.circle.CircleAdmin;
import com.zbkj.common.model.circle.CircleMerchant;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.request.circle.CircleAddRequest;
import com.zbkj.common.request.circle.CircleConjunctiveDiyRequest;
import com.zbkj.common.request.circle.CircleMerchantRequest;
import com.zbkj.common.request.circle.CircleUpdateRequest;
import com.zbkj.common.response.circle.CircleDetailResponse;
import com.zbkj.common.response.circle.CircleMerchantResponse;
import com.zbkj.common.response.circle.CircleTreeResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.vo.CircleManageTree;
import com.zbkj.service.dao.circle.CircleDao;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author zzp
* @description circleServiceImpl 接口实现
* @date 2025-08-04
*/
@Service
public class CircleServiceImpl extends ServiceImpl<CircleDao, Circle> implements CircleService {

    @Resource
    private CircleDao dao;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private CircleAdminService circleAdminService;

    @Autowired
    private CircleMerchantService circleMerchantService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private PageDiyService pageDiyService;
    @Autowired
    private CrmebConfig crmebConfig;


    /**
     * 圈层管理列表
     *
     */
    @Override
    public List<CircleTreeResponse> getListTree() {
        List<CircleTreeResponse> circleList = dao.getList();
        List<Integer> diyIdList = circleList.stream().map(CircleTreeResponse::getDiyId).distinct().filter(e -> e > 0).collect(Collectors.toList());
        Map<Integer, String> diyMap = pageDiyService.getNameMapByIdList(diyIdList);
        circleList.forEach(circle -> {
            if (circle.getDiyId().equals(0)) {
                circle.setDiyName("-");
            } else {
                String diyName = ObjectUtil.isNull(diyMap.get(circle.getDiyId())) ? "-" : diyMap.get(circle.getDiyId());
                circle.setDiyName(diyName);
            }
            if (crmebConfig.getPhoneMaskSwitch()) {
                circle.setPhone(CrmebUtil.maskMobile(circle.getPhone()));
            }
        });
        return new CircleManageTree(circleList).buildTree() ;
    }

    /**
     * 新增圈层管理表
     *
     * @param request 新增参数
     * @return 新增结果
     */
    @Override
    public Integer add(CircleAddRequest request) {
        VerifyAddData(request);
        // 封装圈层数据
        Circle circle = new Circle();
        BeanUtils.copyProperties(request, circle);
        circle.setStatus(Boolean.FALSE); // 默认禁用
        circle.setIsShow(Boolean.FALSE); // 默认不显示
        circle.setIsDelete(Boolean.FALSE);
        calculateLevel(circle);  // 计算层级
        // 封装圈层管理员数据
        CircleAdmin admin = new CircleAdmin();
        admin.setAccount(request.getAccount());
        admin.setPwd(CrmebUtil.encryptPassword(request.getPwd(), request.getAccount()));
        admin.setRealName(request.getRealName());
        admin.setPhone(request.getPhone());
        // 新增超管账号信息
        admin.setType(RoleEnum.CIRCLE_SUPER.getValue());
        admin.setRoles(request.getRole());
        admin.setStatus(Boolean.FALSE);
        return transactionTemplate.execute(e -> {
            save(circle);
            admin.setCircleId(circle.getId());
            circleAdminService.save(admin);
            return circle.getId();
        });
    }
    
    /**
     * 删除圈层
     *
     * @param id 圈层ID
     * @return 删除结果
     */
    @Override
    public Boolean removeCircleById(Integer id) {
        Circle circle = getById(id);
        if (ObjectUtil.isNull(circle) || circle.getIsDelete()) {
            throw new CrmebException("该圈层不存在或已删除");
        }
        if (circle.getStatus()) {
            throw new CrmebException("该圈层处于启用状态，无法删除");
        }
        // 判断是否有子节点
        LambdaQueryWrapper<Circle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Circle::getPid, id);
        queryWrapper.eq(Circle::getIsDelete, Boolean.FALSE);
        int count = dao.selectCount(queryWrapper);
        if (count > 0) {
            throw new CrmebException("存在下级圈层，无法删除本级");
        }
        // 判断是否有商家绑定
        List<Integer> merIds = circleMerchantService.getMerIdsByCircleId(id);
        if (merIds.size() > 0) {
            throw new CrmebException("该圈层存在商家绑定，无法删除");
        }
        // 删除圈层数据
        LambdaUpdateWrapper<Circle> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Circle::getId, id);
        wrapper.set(Circle::getIsDelete, Boolean.TRUE);
        return transactionTemplate.execute(e -> {
            update(wrapper);
            // 删除管理员账号
            circleAdminService.deleteByCircleId(id);
            return Boolean.TRUE;
        });
    }

    /**
     * 修改圈层
     * @param request 修改参数
     * @return 修改结果
     */
    @Override
    public Boolean updateCircle(CircleUpdateRequest request) {
        Circle circle = getById(request.getId());
        if (ObjectUtil.isNull(circle) || circle.getIsDelete()) {
            throw new CrmebException("该圈层不存在或已删除");
        }
        if (circle.getStatus()) {
            throw new CrmebException("该圈层处于启用状态，无法修改");
        }
        // 校验圈层修改数据
        VerifyUpdateData(request);
        BeanUtils.copyProperties(request, circle);
        calculateLevel(circle);
        circle.setUpdateTime(new Date());

        return transactionTemplate.execute(e -> {
            updateById(circle);
            circleAdminService.updateCircleAdminData(request.getRealName(),request.getPhone(), request.getId(), request.getRole());
            return Boolean.TRUE;
        });
    }

    /**
     * 查询圈层详情
     *
     * @param id 圈层ID
     * @return 圈层详情
     */
    @Override
    public CircleDetailResponse getCircleId(Integer id) {
        CircleDetailResponse detailResponse = new CircleDetailResponse();
        LambdaQueryWrapper<Circle> query = Wrappers.lambdaQuery();
        query.eq(Circle::getId, id);
        query.eq(Circle::getIsDelete, Boolean.FALSE);
        Circle circle = getOne(query);
        if (ObjectUtil.isNull(circle) || circle.getIsDelete()) {
            throw new CrmebException("该圈层不存在或已删除");
        }
        BeanUtils.copyProperties(circle, detailResponse);
        // 查询管理员信息
        CircleAdmin admin = circleAdminService.getByCircleId(id);
        detailResponse.setAccount(admin.getAccount());
        detailResponse.setRealName(admin.getRealName());
        detailResponse.setPhone(admin.getPhone());
        detailResponse.setRole(Integer.valueOf(admin.getRoles()));

        // 查询商户信息
        detailResponse.setMerchantList(getMerchantsByCircleId(id));
        // 手机号掩码处理
        if (crmebConfig.getPhoneMaskSwitch()) {
            detailResponse.setPhone(CrmebUtil.maskMobile(detailResponse.getPhone()));
        }
        return detailResponse;
    }

    /**
     * 重置圈层密码
     *
     * @param id 圈层ID
     * @return 重置结果
     */
    @Override
    public Boolean resetPassword(Integer id) {
        Circle circle = getById(id);
        if (ObjectUtil.isNull(circle) || circle.getIsDelete()) {
            throw new CrmebException("该圈层不存在或已删除");
        }
        CircleAdmin admin = circleAdminService.getByCircleId(id);
        String newPwd = CrmebUtil.encryptPassword("000000", admin.getAccount());
        admin.setPwd(newPwd);
        admin.setUpdateTime(new Date());
        circleAdminService.updateById(admin);
        return Boolean.TRUE;
    }


    /**
     * 更新圈层状态
     *
     * @param id 圈层ID
     * @return 更新结果
     */
    @Override
    public Boolean updateStatus(Integer id) {
        Circle circle = getById(id);
        if (ObjectUtil.isNull(circle) || circle.getIsDelete()) {
            throw new CrmebException("该圈层不存在或已删除");
        }
        CircleAdmin admin = circleAdminService.getByCircleId(id);
        Boolean status = !circle.getStatus();

        List<Integer> allCircleIds = getCircleChildById(id);

        if (status ){
            if (circle.getLevel() > 1) {
                if (circle.getLevel() == 3){
                    Circle parent = getById(circle.getPid());
                    allCircleIds.add(parent.getPid());
                }
                allCircleIds.add(circle.getPid());
            }
        }

        LambdaUpdateWrapper<Circle> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Circle::getId, allCircleIds);
        wrapper.set(Circle::getStatus, status);
        if (!status) {
            wrapper.set(Circle::getIsShow, Boolean.FALSE);
        }

        LambdaUpdateWrapper<CircleAdmin> adminWrapper = new LambdaUpdateWrapper<>();
        adminWrapper.in(CircleAdmin::getCircleId, allCircleIds);
        adminWrapper.set(CircleAdmin::getStatus, status);

        return transactionTemplate.execute(e -> {
            update(wrapper);
            circleAdminService.update(adminWrapper);
            return Boolean.TRUE;
        });
    }

    /**
     * 更新圈层显示状态
     *
     * @param id 圈层ID
     * @return 更新结果
     */
    @Override
    public Boolean updateShow(Integer id) {
        Circle circle = getById(id);
        if (ObjectUtil.isNull(circle) || circle.getIsDelete()) {
            throw new CrmebException("该圈层不存在或已删除");
        }
        if (!circle.getStatus()) {
            throw new CrmebException("该圈层处于禁用状态，无法修改显示状态");
        }
        Boolean isShow = !circle.getIsShow();
        List<Integer> allCircleIds = getCircleChildById(id);
        if (isShow ){
            if (circle.getLevel() > 1) {
                if (circle.getLevel() == 3){
                    Circle parent = getById(circle.getPid());
                    allCircleIds.add(parent.getPid());
                }
                allCircleIds.add(circle.getPid());
            }
        }

        LambdaUpdateWrapper<Circle> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Circle::getId, allCircleIds);
        wrapper.set(Circle::getIsShow, isShow);
        update(wrapper);
        return Boolean.TRUE;
    }

    /**
     * 获取圈层下的商家列表
     *
     * @param id 圈层ID
     * @return 商家列表
     */
    @Override
    public List<CircleMerchantResponse> getMerchantsByCircleId(Integer id) {
        List<CircleMerchantResponse> list = new ArrayList<>();
        if (id > 0) { // 查询已关联圈层商户信息
            List<Integer> merIdList = circleMerchantService.getMerIdsByCircleId(id);
            if (CollectionUtil.isNotEmpty(merIdList)) {
                merchantService.getListByIdList(merIdList).forEach(merchant -> {
                    CircleMerchantResponse response = new CircleMerchantResponse();
                    BeanUtils.copyProperties(merchant, response);
                    if (crmebConfig.getPhoneMaskSwitch()) {
                        response.setPhone(CrmebUtil.maskMobile(response.getPhone()));
                    }
                    list.add(response);
                });
            }
            return list;
        }
        // 查询未关联圈层商户信息
        return merchantService.getCircleMerchantsUnBind(id);
    }

    /**
     * 圈层关联商户
     *
     * @param request 关联商户请求
     * @return 关联结果
     */
    //@Override
    //public Boolean bindMerchant(CircleMerchantRequest request) {
    //    Integer circleId = request.getId();
    //    List<Integer> merchantIds = request.getMerchantIds();
    //    Circle circle = getById(circleId);
    //    if (ObjectUtil.isNull(circle) || circle.getIsDelete()) {
    //        throw new CrmebException("该圈层不存在或已删除");
    //    }
    //    // 查询商户信息
    //    List<Merchant> merchantList = merchantService.getListByIdList(merchantIds);
    //    if (CollectionUtil.isEmpty(merchantList) || merchantIds.size() != merchantList.size()) {
    //        throw new CrmebException("选择的部分商户不存在或已被删除");
    //    }
    //    ArrayList<CircleMerchant> circleMerchants = new ArrayList<>();
    //    if (circle.getLevel() == 1 ) {
    //        // 需判断商户列表是否绑定过本圈层：若绑定过，跳过；若未绑定过，则直接绑定；
    //        List<Integer> bindMerIds = circleMerchantService.getMerIdsByCircleId(circleId);
    //        merchantIds.forEach(merId -> {
    //            if (!bindMerIds.contains(merId)) {
    //                CircleMerchant circleMerchant = new CircleMerchant();
    //                circleMerchant.setMerId(merId);
    //                circleMerchant.setCircleId(circleId);
    //                circleMerchants.add(circleMerchant);
    //            }
    //        });
    //
    //    } else if (circle.getLevel() == 2) {
    //        // 需判断商户列表是否绑定过本圈层：若绑定过，则不允许再次绑定；若未绑定过，则直接绑定；
    //        // 需判断商户列表是否绑定过上级圈层：若绑定过，则直接绑定；若未绑定过，则两级都需要绑定
    //        List<Integer> bindMerIds = circleMerchantService.getMerIdsByCircleId(circleId);
    //        merchantIds.forEach(merId -> {
    //            if (!bindMerIds.contains(merId)) {
    //                CircleMerchant circleMerchant = new CircleMerchant();
    //                circleMerchant.setMerId(merId);
    //                circleMerchant.setCircleId(circleId);
    //                circleMerchants.add(circleMerchant);
    //            }
    //        });
    //        List<Integer> bindMerIds1 = circleMerchantService.getMerIdsByCircleId(circle.getPid());
    //        merchantIds.forEach(merId -> {
    //            if (!bindMerIds1.contains(merId)) {
    //                CircleMerchant circleMerchant = new CircleMerchant();
    //                circleMerchant.setMerId(merId);
    //                circleMerchant.setCircleId(circle.getPid());
    //                circleMerchants.add(circleMerchant);
    //            }
    //        });
    //    } else if (circle.getLevel() == 3) {
    //        // 需判断商户列表是否绑定过本圈层：若绑定过，则不允许再次绑定；若未绑定过，则直接绑定；
    //        // 需判断商户列表是否绑定过上级圈层；若绑定过，则直接绑定；若未绑定过，则两级都需要绑定
    //        // 需判断商户列表是否绑定过上级的上级圈层：若绑定过，则直接绑定；若未绑定过，则三级都需要绑定
    //        List<Integer> bindMerIds = circleMerchantService.getMerIdsByCircleId(circleId);
    //        merchantIds.forEach(merId -> {
    //            if (!bindMerIds.contains(merId)) {
    //                CircleMerchant circleMerchant = new CircleMerchant();
    //                circleMerchant.setMerId(merId);
    //                circleMerchant.setCircleId(circleId);
    //                circleMerchants.add(circleMerchant);
    //            }
    //        });
    //        List<Integer> bindMerIds1 = circleMerchantService.getMerIdsByCircleId(circle.getPid());
    //        merchantIds.forEach(merId -> {
    //            if (!bindMerIds1.contains(merId)) {
    //                CircleMerchant circleMerchant = new CircleMerchant();
    //                circleMerchant.setMerId(merId);
    //                circleMerchant.setCircleId(circle.getPid());
    //                circleMerchants.add(circleMerchant);
    //            }
    //        });
    //        Circle parent = getById(circle.getPid());
    //        List<Integer> bindMerIds2 = circleMerchantService.getMerIdsByCircleId(parent.getPid());
    //        merchantIds.forEach(merId -> {
    //            if (!bindMerIds2.contains(merId)) {
    //                CircleMerchant circleMerchant = new CircleMerchant();
    //                circleMerchant.setMerId(merId);
    //                circleMerchant.setCircleId(parent.getPid());
    //                circleMerchants.add(circleMerchant);
    //            }
    //        });
    //    }
    //    // 只有当有需要绑定的记录时才执行保存
    //    if (CollectionUtil.isNotEmpty(circleMerchants)) {
    //        return circleMerchantService.saveBatch(circleMerchants);
    //    }
    //    return Boolean.TRUE;
    //}
    /**
     * 圈层商户关系全量更新
     *
     * @param request 包含圈层ID和商户ID集合的请求
     * @return 操作结果
     */
    @Override
    @Transactional
    public Boolean bindMerchant(CircleMerchantRequest request) {
        Integer circleId = request.getId();
        List<Integer> newMerchantIds = request.getMerchantIds();
        Circle circle = getById(circleId);

        // 验证圈层有效性
        if (ObjectUtil.isNull(circle) || circle.getIsDelete()) {
            throw new CrmebException("该圈层不存在或已删除");
        }

        // 使用Set提高查找效率
        Set<Integer> newMerchantSet = new HashSet<>(CollectionUtil.isNotEmpty(newMerchantIds) ? newMerchantIds : Collections.emptyList());

        // 获取当前圈层已绑定的商户ID
        List<Integer> existingMerchantIds = circleMerchantService.getMerIdsByCircleId(circleId);
        Set<Integer> existingSet = new HashSet<>(existingMerchantIds);

        // 计算需要解绑的商户（旧有但不在新集合中的）
        Set<Integer> toUnbind = new HashSet<>(existingSet);
        toUnbind.removeAll(newMerchantSet);

        // 计算需要绑定的商户（新集合中但未绑定的）
        Set<Integer> toBind = new HashSet<>(newMerchantSet);
        toBind.removeAll(existingSet);

        // 查找本级或下级所有节点
        List<Integer> allCircleIds = getCircleChildById(circleId);

        // 执行绑定操作
        List<CircleMerchant> circleMerchants = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(toBind)) {
            // 批量验证商户有效性
            Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(new ArrayList<>(toBind));
            if (merchantMap.size() != toBind.size()) {
                throw new CrmebException("部分商户不存在或已被删除");
            }

            // 预加载上级圈层绑定关系
            Map<Integer, Set<Integer>> bindCache = new HashMap<>();
            if (circle.getLevel() > 1) {
                loadParentBindings(circle, bindCache);
            }

            // 根据圈层等级处理绑定
            switch (circle.getLevel()) {
                case 1:
                    for (Integer merId : toBind) {
                        circleMerchants.add(createRelation(circleId, merId));
                    }
                    break;

                case 2:
                    Set<Integer> parentBindSet = bindCache.getOrDefault(circle.getPid(), Collections.emptySet());
                    for (Integer merId : toBind) {
                        circleMerchants.add(createRelation(circleId, merId));
                        if (!parentBindSet.contains(merId)) {
                            circleMerchants.add(createRelation(circle.getPid(), merId));
                        }
                    }
                    break;

                case 3:
                    Circle parent = getById(circle.getPid());
                    Set<Integer> level1BindSet = bindCache.getOrDefault(parent.getPid(), Collections.emptySet());
                    Set<Integer> level2BindSet = bindCache.getOrDefault(circle.getPid(), Collections.emptySet());

                    for (Integer merId : toBind) {
                        circleMerchants.add(createRelation(circleId, merId));
                        if (!level2BindSet.contains(merId)) {
                            circleMerchants.add(createRelation(circle.getPid(), merId));
                        }
                        if (!level1BindSet.contains(merId)) {
                            circleMerchants.add(createRelation(parent.getPid(), merId));
                        }
                    }
                    break;
            }
        }

        return transactionTemplate.execute(e -> {
            // 执行解绑操作
            if (CollectionUtil.isNotEmpty(toUnbind)) {
                circleMerchantService.removeMer(allCircleIds, new ArrayList<>(toUnbind));
            }

            // 执行绑定操作
            if (CollectionUtil.isNotEmpty(circleMerchants)) {
                circleMerchantService.saveBatch(circleMerchants);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 预加载上级圈层绑定关系
     */
    private void loadParentBindings(Circle circle, Map<Integer, Set<Integer>> bindCache) {
        List<Integer> circleIdsToLoad = new ArrayList<>();

        if (circle.getLevel() >= 2) {
            circleIdsToLoad.add(circle.getPid());
        }

        if (circle.getLevel() == 3) {
            Circle parent = getById(circle.getPid());
            if (parent != null) {
                circleIdsToLoad.add(parent.getPid());
            }
        }

        if (!circleIdsToLoad.isEmpty()) {
            // 批量获取所有需要的绑定关系
            List<CircleMerchant> bindings = circleMerchantService.getByCircleIds(circleIdsToLoad);

            // 转换为Map<圈层ID, Set<商户ID>>
            for (CircleMerchant binding : bindings) {
                bindCache.computeIfAbsent(binding.getCircleId(), k -> new HashSet<>())
                        .add(binding.getMerId());
            }
        }
    }

    /**
     * 创建圈层商户关系对象
     */
    private CircleMerchant createRelation(Integer circleId, Integer merId) {
        CircleMerchant relation = new CircleMerchant();
        relation.setCircleId(circleId);
        relation.setMerId(merId);
        return relation;
    }


    /**
     * 圈层解绑商户
     *
     * @param request  解绑商户请求
     * @return 解绑结果
     */
    @Override
    public Boolean removeMerchant(CircleMerchantRequest request) {
        Integer circleId = request.getId();
        List<Integer> merchantIds = request.getMerchantIds();
        Circle circle = getById(circleId);
        if (ObjectUtil.isNull(circle) || circle.getIsDelete()) {
            throw new CrmebException("该圈层不存在或已删除");
        }
        // 查询自己及下级所有节点-直接全解绑
        List<Integer> allCircleIds = getCircleChildById(circleId);
        circleMerchantService.removeMer(allCircleIds, merchantIds);
        return Boolean.TRUE;
    }



    /**
     * 根据圈层id 查询下级圈层id集合 + 本级id
     *
     */
    @Override
    public List<Integer> getCircleChildById(Integer circleId) {
        // 预先加载所有圈层数据（避免多次查询数据库）
        List<Circle> allCircle = list(
                new LambdaQueryWrapper<Circle>()
                        .eq(Circle::getIsDelete, Boolean.FALSE)
        );

        // 使用内存操作递归查询子节点
        Map<Integer, List<Circle>> areaMap = allCircle.stream()
                .collect(Collectors.groupingBy(Circle::getPid));

        List<Integer> circleIds = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(circleId);

        while (!queue.isEmpty()) {
            Integer currentId = queue.poll();
            List<Circle> children = areaMap.get(currentId);
            if (CollectionUtil.isNotEmpty(children)) {
                children.forEach(child -> {
                    circleIds.add(child.getId());
                    queue.add(child.getId());  // 加入队列继续查找下级
                });
            }
        }
        circleIds.add(circleId);
        return circleIds;
    }
    //
    /**
     * 获取所有的终点商圈
     */
    @Override
    public List<Circle> findAllDestination() {
        return dao.findAllDestination();
    }

    /**
     * 获取所有打开的圈层
     */
    @Override
    public List<Circle> findOpenAll() {
        LambdaQueryWrapper<Circle> lqw = Wrappers.lambdaQuery();
        lqw.eq(Circle::getIsDelete, 0);
        lqw.eq(Circle::getStatus, 1);
        lqw.eq(Circle::getIsShow, 1);
        return dao.selectList(lqw);
    }

    /**
     * 圈层关联DIY模板
     */
    @Override
    public Boolean conjunctiveDiy(CircleConjunctiveDiyRequest request) {
        List<Integer> idList = Arrays.stream(request.getIds().split(",")).map(Integer::parseInt).collect(Collectors.toList());
        LambdaUpdateWrapper<Circle> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Circle::getDiyId, request.getDiyId());
        wrapper.in(Circle::getId, idList);
        return update(wrapper);
    }

    @Override
    public Map<Integer, String> getCircleNameMapByDiyIdList(List<Integer> diyIdList) {
        LambdaQueryWrapper<Circle> lqw = Wrappers.lambdaQuery();
        lqw.select(Circle::getId, Circle::getName, Circle::getDiyId);
        lqw.in(Circle::getDiyId, diyIdList);
        lqw.eq(Circle::getIsDelete, 0);
        List<Circle> circleList = dao.selectList(lqw);
        Map<Integer, String> map = new HashMap<>();
        if (CollUtil.isEmpty(circleList)) {
            return map;
        }
        Map<Integer, List<Circle>> groupMap = circleList.stream().collect(Collectors.groupingBy(Circle::getDiyId));
        for (Map.Entry<Integer, List<Circle>> entry : groupMap.entrySet()) {
            Integer key = entry.getKey();
            List<Circle> valueList = entry.getValue();
            String value = valueList.stream().map(Circle::getName).collect(Collectors.joining(","));
            map.put(key, value);
        }
        return map;
    }

    /**
     * 辅助方法： 校验计算层级
     *
     * @param circle 圈层对象
     */
    private void calculateLevel(Circle circle) {
        // 顶级节点
        if (circle.getPid() == 0) {
            circle.setLevel(1);
            return;
        }
        // 查询父节点
        Circle parent = dao.selectById(circle.getPid());
        // 层级 = 父节点层级 + 1,链条长度 = 父节点链条长度
        circle.setLevel(parent.getLevel() + 1);
        // 验证层级限制
        if (circle.getLevel() > 3) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "圈层层级不能超过三级");
        }
    }


    /**
     * 辅助方法：校验新增的圈层数据
     *
     * @param request 圈层数据
     */
    private void VerifyAddData(CircleAddRequest request) {
        // 地址校验
        if (request.getAddressSwitch().equals(1)) {
            if (StringUtils.isBlank(request.getAddress()) ||
                    StringUtils.isBlank(request.getLongitude()) || StringUtils.isBlank(request.getLatitude())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "圈层地址打开时，圈层地址和经纬度不能为空");
            }
        }
        // 圈层名称校验
        LambdaQueryWrapper<Circle> lqw = Wrappers.lambdaQuery();
        lqw.select(Circle::getId);
        lqw.eq(Circle::getName, request.getName());
        lqw.eq(Circle::getIsDelete, Boolean.FALSE);
        if (dao.selectCount(lqw) > 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "圈层名称已存在");
        }
        // 圈层管理员账号校验
        if (circleAdminService.checkAdminAccount(request.getAccount()) > 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "圈层管理员账号已存在");
        }
    }

    /**
     * 辅助方法：校验修改的圈层数据
     *
     * @param request 圈层数据
     */
    private void VerifyUpdateData(CircleUpdateRequest request) {
        // 地址校验
        if (request.getAddressSwitch().equals(1)) {
            if (StringUtils.isBlank(request.getAddress()) ||
                    StringUtils.isBlank(request.getLongitude()) || StringUtils.isBlank(request.getLatitude())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "圈层地址打开时，圈层地址和经纬度不能为空");
            }
        }
        // 圈层名称校验
        LambdaQueryWrapper<Circle> lqw = Wrappers.lambdaQuery();
        lqw.select(Circle::getId);
        lqw.ne(Circle::getId, request.getId());
        lqw.eq(Circle::getName, request.getName());
        lqw.eq(Circle::getIsDelete, Boolean.FALSE);
        if (dao.selectCount(lqw) > 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "圈层名称已存在");
        }
    }



}

