package com.imb.imbdemo.entity;


import java.util.List;


/**
 * @author - gongxun;
 * created on 2019/4/3-11:36;
 * description - 部门
 */
public class DepartmentEntity {
    /**
     * 如果最上层的部门id为此值 就说明他已经是最顶部门组织
     */
    public static final long TOP_DEPT_ID = -1L;

    /**
     * 未赋值上一级别的部门
     * 初始值
     */
    public static final DepartmentEntity SUP_DEPT_NOT_SET = null;

    private Long id;

    private long did;

    private long deptIdOnServer;

    private String name;

    private String headshot;

    /**
     * 上一级的部门 id
     */
    private DepartmentEntity supDept = SUP_DEPT_NOT_SET;

    /**
     * 所包含的部门的id   服务端id
     */
    private List<DepartmentEntity> deptList;

    /**
     * 所包含的联系人的id   服务端id
     */
    private List<ContactEntity> contactList;

    public DepartmentEntity(long did, long deptId, String name, String headshot, DepartmentEntity supDept,  List<DepartmentEntity>  deptList, List<ContactEntity>  contactList) {
        this.did = did;
        this.deptIdOnServer = deptId;
        this.name = name;
        this.headshot = headshot;
        this.supDept = supDept;
        this.deptList = deptList;
        this.contactList = contactList;
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


    public long getDeptIdOnServer() {
        return this.deptIdOnServer;
    }


    public void setDeptIdOnServer(long deptIdOnServer) {
        this.deptIdOnServer = deptIdOnServer;
    }


    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getHeadshot() {
        return this.headshot;
    }


    public void setHeadshot(String headshot) {
        this.headshot = headshot;
    }

    public DepartmentEntity getSupDept() {
        return supDept;
    }

    public void setSupDept(DepartmentEntity supDept) {
        this.supDept = supDept;
    }

    public List<DepartmentEntity> getDeptList() {
        return deptList;
    }

    public void setDeptList(List<DepartmentEntity> deptList) {
        this.deptList = deptList;
    }

    public List<ContactEntity> getContactList() {
        return contactList;
    }

    public void setContactList(List<ContactEntity> contactList) {
        this.contactList = contactList;
    }
}
