package com.zbkj.common.vo;

import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 圈层树
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/9/19
 */
public class CircleTree {

    private List<CirCleCheckVo> circleList = new ArrayList<CirCleCheckVo>();

    public CircleTree(List<CirCleCheckVo> circleList) {
        this.circleList = circleList;
    }

    //建立树形结构
    public List<CirCleCheckVo> buildTree(){
        List<CirCleCheckVo> circleMenus = new ArrayList<>();
        for(CirCleCheckVo cleCheckMenu : getRootNode()) {
            cleCheckMenu = buildChildTree(cleCheckMenu);
            circleMenus.add(cleCheckMenu);
        }
        return sortList(circleMenus);
//        return treeMenus;
    }

    // 排序
    private List<CirCleCheckVo> sortList(List<CirCleCheckVo> circleMenus) {
        circleMenus = circleMenus.stream().sorted(Comparator.comparing(CirCleCheckVo::getId).reversed()).collect(Collectors.toList());
        circleMenus.forEach(e -> {
            if (CollUtil.isNotEmpty(e.getChildList())) {
                e.setChildList(sortList(e.getChildList()));
            }
        });
        return circleMenus;
    }

    //递归，建立子树形结构
    private CirCleCheckVo buildChildTree(CirCleCheckVo pNode){
        List<CirCleCheckVo> childMenus = new ArrayList<>();
        for(CirCleCheckVo cleCheckMenu : circleList) {
            if(cleCheckMenu.getPid().equals(pNode.getId())) {
                childMenus.add(buildChildTree(cleCheckMenu));
            }
        }
        pNode.setChildList(childMenus);
        return pNode;
    }

    //获取根节点
    private List<CirCleCheckVo> getRootNode() {
        List<CirCleCheckVo> cirCleCheckVoList = new ArrayList<>();
        for(CirCleCheckVo cleCheckVo : circleList) {
            if(cleCheckVo.getPid().equals(0)) {
                cirCleCheckVoList.add(cleCheckVo);
            }
        }
        return cirCleCheckVoList;
    }

}
