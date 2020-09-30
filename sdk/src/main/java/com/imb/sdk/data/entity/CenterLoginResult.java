package com.imb.sdk.data.entity;

/**
 * @author - gongxun;
 * created on 2020/9/27-14:36;
 * description - 智能中心登录的返回 包含poc的账户密码
 */
public class CenterLoginResult {
    /**
     * 请求的校验token
     */
    public String token;
    /**
     * 头像
     */
    public String headshot;
    /**
     * 手机号
     */
    public String mobileNum;
    /**
     * 账户名字
     */
    public String accountName;
    /**
     * poc服务器地址
     */
    public String pocServerHost;
    /**
     * poc端口号
     */
    public int pocServerPort;
    /**
     * poc的号码
     */
    public String pocNum;
    /**
     * poc的密码base64加密
     */
    public String pocPassword;
    /**
     * 容量 一个会议最多有几个人参与
     */
    public int capacity;
    /**
     * 0：账户未失效过期
     * 1：账户已过期
     */
    public int expireType;
    /**
     * 账户的过期时间
     */
    public String expirationTime;
    /**
     * 账户的类型
     * 0：参会者。不能创建会议
     * 1：所有功能允许
     */
    public int userType;

    public CenterLoginResult() {
    }

    public CenterLoginResult(String token, String headshot, String mobileNum, String accountName,
                             String serverHost, int serverPort, String username, String password,
                             int capacity, int expireType, String expirationTime, int userType) {
        this.token = token;
        this.headshot = headshot;
        this.mobileNum = mobileNum;
        this.accountName = accountName;
        this.pocServerHost = serverHost;
        this.pocServerPort = serverPort;
        this.pocNum = username;
        this.pocPassword = password;
        this.capacity = capacity;
        this.expireType = expireType;
        this.expirationTime = expirationTime;
        this.userType = userType;
    }

    public CenterLoginResult(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "CenterLoginResult{" +
                "token='" + token + '\'' +
                ", headshot='" + headshot + '\'' +
                ", mobileNum='" + mobileNum + '\'' +
                ", accountName='" + accountName + '\'' +
                ", pocServerHost='" + pocServerHost + '\'' +
                ", pocServerPort=" + pocServerPort +
                ", pocNum='" + pocNum + '\'' +
                ", pocPassword='" + pocPassword + '\'' +
                ", capacity=" + capacity +
                ", expireType=" + expireType +
                ", expirationTime='" + expirationTime + '\'' +
                ", userType=" + userType +
                '}';
    }
}
