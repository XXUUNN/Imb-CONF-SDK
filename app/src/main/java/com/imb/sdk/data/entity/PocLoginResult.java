package com.imb.sdk.data.entity;

/**
 * @author - gongxun;
 * created on 2020/9/25-16:31;
 * description - 登录结果
 */
public class PocLoginResult {
    /**
     * 0 成功
     * 其他 失败
     * @see com.imb.sdk.data.PocConstant.RegisterResult
     */
    public int code;
    /**
     * 附带的信息
     * 失败，那么就是失败信息
     * 成功，如果同步了通讯录，那就是通讯录的内容 否则无意义
     */
    public String msg;

    public PocLoginResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
