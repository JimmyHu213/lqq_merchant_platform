package com.zbkj.common.utils;

import static com.zbkj.common.utils.CrmebUtil.decryptPassowrd;

public class CrmebUtilTest {
    public static void main(String[] args) throws Exception {
        //System.out.println(encryptPassword("123456", "admin"));
        System.out.println(decryptPassowrd("L8qdg72wbeQ=", "admin"));
    }
}
