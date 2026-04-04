package com.zbkj.common.vo;

import cn.hutool.core.collection.CollUtil;
import com.zbkj.common.response.circle.CircleTreeResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CircleManageTree {

    private List<CircleTreeResponse> circleList = new ArrayList<CircleTreeResponse>();

    public CircleManageTree(List<CircleTreeResponse> circleList) {
        this.circleList = circleList;
    }

    //建立树形结构
    public List<CircleTreeResponse> buildTree(){
        List<CircleTreeResponse> circleMenus = new ArrayList<>();
        for(CircleTreeResponse cleCheckMenu : getRootNode()) {
            cleCheckMenu = buildChildTree(cleCheckMenu);
            circleMenus.add(cleCheckMenu);
        }
        return sortList(circleMenus);
//        return treeMenus;
    }

    // 排序
    private List<CircleTreeResponse> sortList(List<CircleTreeResponse> circleMenus) {
        circleMenus = circleMenus.stream().sorted(Comparator.comparing(CircleTreeResponse::getId).reversed()).collect(Collectors.toList());
        circleMenus.forEach(e -> {
            if (CollUtil.isNotEmpty(e.getChildList())) {
                e.setChildList(sortList(e.getChildList()));
            }
        });
        return circleMenus;
    }

    //递归，建立子树形结构
    private CircleTreeResponse buildChildTree(CircleTreeResponse pNode){
        List<CircleTreeResponse> childMenus = new ArrayList<>();
        for(CircleTreeResponse cleCheckMenu : circleList) {
            if(cleCheckMenu.getPid().equals(pNode.getId())) {
                childMenus.add(buildChildTree(cleCheckMenu));
            }
        }
        pNode.setChildList(childMenus);
        return pNode;
    }

    //获取根节点
    private List<CircleTreeResponse> getRootNode() {
        List<CircleTreeResponse> CircleTreeResponseList = new ArrayList<>();
        for(CircleTreeResponse cleCheckVo : circleList) {
            if(cleCheckVo.getPid().equals(0)) {
                CircleTreeResponseList.add(cleCheckVo);
            }
        }
        return CircleTreeResponseList;
    }
}
