package com.imb.sdk.data.entity;

/**
 * @author - gongxun;
 * created on 2020/11/6-11:57;
 * description - 同步通讯录 结果
 */
public class PocSyncAddressBookResult {
    /**
     * 是否成功
     */
    public boolean isOk;

    public String content;

    public PocSyncAddressBookResult(boolean isOk, String content) {
        this.isOk = isOk;
        this.content = content;
    }
}
