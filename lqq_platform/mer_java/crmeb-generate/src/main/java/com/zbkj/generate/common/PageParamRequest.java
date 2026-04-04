package com.zbkj.generate.common;

import lombok.Data;

@Data
public class PageParamRequest {

    // 页码
    private int pageNum;

    // 每页数量
    private int pageSize;
}

