package com.imb.imbdemo.entity;


import java.util.List;

/**
 * @author - gongxun;
 * created on 2019/3/27-17:28;
 * description - 组
 * todo 未完成
 */
public class ContactGroupEntity {

    private Long id;

    private long did;

    /**
     * 组id
     */
    private long groupIdOnServer;

    /**
     * 对讲组还是广播组
     */
    private int type;

    /**
     * 组名
     */
    private String name;

    /**
     * 组号码
     */
    private String number;

    /**
     * 头像
     */
    private String headshot;

    /**
     * 是否被屏蔽 被屏蔽 无法接收到 电话消息
     */
    private boolean shielded;

    /**
     * 创建者的号码
     */
    private String creatorNum;

    /**
     * 成员
     */
    private List<ContactEntity> contactsList;

    public ContactGroupEntity(long did, long groupIdOnServer, String name, String number, String headshot,
                              List<ContactEntity> contactsList,
                              int type, boolean shielded, String creator) {
        this.did = did;
        this.groupIdOnServer = groupIdOnServer;
        this.name = name;
        this.number = number;
        this.headshot = headshot;
        this.contactsList = contactsList;
        this.type = type;
        this.shielded = shielded;
        this.creatorNum = creator;
    }

    public ContactGroupEntity(Long id, long did, long groupIdOnServer, int type, String name, String number,
                              String headshot, boolean shielded, String creatorNum, List<ContactEntity> contactsList) {
        this.id = id;
        this.did = did;
        this.groupIdOnServer = groupIdOnServer;
        this.type = type;
        this.name = name;
        this.number = number;
        this.headshot = headshot;
        this.shielded = shielded;
        this.creatorNum = creatorNum;
        this.contactsList = contactsList;
    }


    public ContactGroupEntity() {
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


    public long getGroupIdOnServer() {
        return this.groupIdOnServer;
    }


    public void setGroupIdOnServer(long groupIdOnServer) {
        this.groupIdOnServer = groupIdOnServer;
    }


    public int getType() {
        return this.type;
    }


    public void setType(int type) {
        this.type = type;
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


    public String getHeadshot() {
        return this.headshot;
    }


    public void setHeadshot(String headshot) {
        this.headshot = headshot;
    }


    public boolean getShielded() {
        return this.shielded;
    }


    public void setShielded(boolean shielded) {
        this.shielded = shielded;
    }


    public List<ContactEntity> getContactsList() {
        return this.contactsList;
    }


    public void setContactsList(List<ContactEntity> contactsList) {
        this.contactsList = contactsList;
    }

    public boolean isShielded() {
        return shielded;
    }

    public String getCreatorNum() {
        return this.creatorNum;
    }


    public void setCreatorNum(String creatorNum) {
        this.creatorNum = creatorNum;
    }
}
