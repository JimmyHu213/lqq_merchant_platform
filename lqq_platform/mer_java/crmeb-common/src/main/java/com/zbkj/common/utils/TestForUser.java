package com.zbkj.common.utils;

public class TestForUser {
    public static void main(String[] args) {
        //System.out.println(CrmebUtil.decryptPassowrd("NuY4yNzlTH5M3OIIQ8/gQg==","18292417675"));
        //System.out.println(CrmebUtil.encryptPassword("crmeb.com","18292417675"));
        //System.out.println(CrmebUtil.getNowTimeForFT());
        CrmebUtil.setBusinessDataForFT(10, 60,110);
        CrmebUtil.getNowTimeForFT();
    }
}
