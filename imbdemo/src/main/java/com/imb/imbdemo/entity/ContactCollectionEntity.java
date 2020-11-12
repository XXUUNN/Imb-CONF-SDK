package com.imb.imbdemo.entity;


/**
 * @author- XUN;
 * @create- 2019/4/1;
 * @desc - 收藏的联系人
 */
public class ContactCollectionEntity {
    private Long id;

    private ContactEntity contact;

    public ContactCollectionEntity(ContactEntity contact) {
        this.contact = contact;
    }

    public ContactCollectionEntity(Long id, ContactEntity contact) {
        this.id = id;
        this.contact = contact;
    }

    public ContactCollectionEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContactEntity getContact() {
        return this.contact;
    }

    public void setContact(ContactEntity contact) {
        this.contact = contact;
    }


    
}
