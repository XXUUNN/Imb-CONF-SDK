package com.imb.imbdemo.entity;

/**
 * @author - gongxun;
 * created on 2019/3/22-15:01;
 * description - 联系人
 */
public class ContactEntity {
    private Long id;

    private long did;

    /**
     * 联系人的id  服务端id
     */
    private long contactIdOnServer;

    /**
     * 是否在登录状态
     */
    private String online;

    /**
     * 头像
     */
    private String headshot;

    private String name;

    private String number;

    /**
     * 使用时自己初始化
     */
    private DepartmentEntity dept;

    /**
     * 角色 有权重值
     */
    private String role;

    public ContactEntity(long did, long contactIdOnServer, String online, String headshot, String name, String number, DepartmentEntity dept, String role) {
        this.did = did;
        this.contactIdOnServer = contactIdOnServer;
        this.online = online;
        this.headshot = headshot;
        this.name = name;
        this.number = number;
        this.dept = dept;
        this.role = role;
    }

    public ContactEntity(long contactIdOnServer) {
        this.contactIdOnServer = contactIdOnServer;
    }


    public ContactEntity(String number) {
        this.number = number;
    }


    public ContactEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getDid() {
        return this.did;
    }

    public void setDid(long did) {
        this.did = did;
    }

    public long getContactIdOnServer() {
        return this.contactIdOnServer;
    }

    public void setContactIdOnServer(long contactIdOnServer) {
        this.contactIdOnServer = contactIdOnServer;
    }

    public String getOnline() {
        return this.online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getHeadshot() {
        return this.headshot;
    }

    public void setHeadshot(String headshot) {
        this.headshot = headshot;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public DepartmentEntity getDept() {
        return dept;
    }

    public void setDept(DepartmentEntity dept) {
        this.dept = dept;
    }
}

