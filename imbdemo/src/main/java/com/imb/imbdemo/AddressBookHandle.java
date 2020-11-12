package com.imb.imbdemo;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.imb.imbdemo.entity.ContactEntity;
import com.imb.imbdemo.entity.ContactGroupEntity;
import com.imb.imbdemo.entity.DepartmentEntity;
import com.imb.sdk.data.PocConstant;
import com.imb.sdk.data.response.AddressUrlResponse;
import com.imb.sdk.login.ResponseTranslateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author - gongxun;
 * created on 2020/11/5-17:11;
 * description -
 */
public class AddressBookHandle {
    public static void handleContent(String content, List<ContactEntity> contactList,
                                     List<ContactGroupEntity> groupList,
                                     List<DepartmentEntity> deptList) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        AddressUrlResponse response = JSON.parseObject(content, AddressUrlResponse.class);
        //提取通讯录信息
        updateAddressBook(response, contactList, groupList, deptList);
    }


    private static void updateAddressBook(AddressUrlResponse response,
                                          List<ContactEntity> contacts,
                                          List<ContactGroupEntity> groups,
                                          List<DepartmentEntity> depts) {
        //用户列表
        HashMap<Long, ContactEntity> contactMap = new HashMap<>();

        ArrayList<ContactEntity> contactList = getContactList(response, response.userInfo, contactMap);
        //组 包括对讲组和广播组
        ArrayList<ContactGroupEntity> groupList = getGroupList(response.groupInfo, response.groupDdbstatus, contactMap, response.userId);
        //获取组织
        ArrayList<DepartmentEntity> deptList = getDeptList(response.organizationInfo, contactMap);
        //更新用户数据里的deptId
        updateContactDeptId(contactMap, deptList);

        if (contactList != null) {
            contacts.addAll(contactList);
        }

        if (groupList != null) {
            groups.addAll(groupList);
        }

        if (deptList != null) {
            depts.addAll(deptList);
        }
    }

    private static void updateContactDeptId(HashMap<Long, ContactEntity> map,
                                            ArrayList<DepartmentEntity> deptList) {
        //一个部门多个用户
        if (deptList != null) {
            int deptSize = deptList.size();
            for (int i = 0; i < deptSize; i++) {
                DepartmentEntity dept = deptList.get(i);
                List<ContactEntity> contactIdArr = dept.getContactList();
                if (contactIdArr != null) {
                    for (ContactEntity contact : contactIdArr) {
                        ContactEntity contactEntity = map.get(contact.getContactIdOnServer());
                        if (contactEntity != null) {
                            contactEntity.setDept(dept);
                        }
                    }
                }
            }
        }
    }

    /**
     * 用户组织
     */
    private static ArrayList<DepartmentEntity> getDeptList(
            List<AddressUrlResponse.OrganizationInfo> organizationInfo, HashMap<Long, ContactEntity> contactMap) {
        if (organizationInfo == null || organizationInfo.isEmpty()) {
            return null;
        }

        ArrayList<DepartmentEntity> list = new ArrayList<>();

        //手动添加最上层的 最顶级的他的oid是 0
        AddressUrlResponse.OrganizationInfo top = new AddressUrlResponse.OrganizationInfo();
        top.userList = null;
        top.parentOid = DepartmentEntity.TOP_DEPT_ID;
        top.did = -1;
        top.name = "组织结构";
        top.oid = 0;
        organizationInfo.add(top);

        int size = organizationInfo.size();

        //id 对应的部门
        Map<Long, DepartmentEntity> deptMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            AddressUrlResponse.OrganizationInfo info = organizationInfo.get(i);
            //先都默认空
            DepartmentEntity departmentEntity = new DepartmentEntity(info.did,
                    info.oid,
                    info.name,
                    null,
                    DepartmentEntity.SUP_DEPT_NOT_SET,
                    null, null);
            //加入子联系人
            String userList = info.userList;
            long[] uIdArr = ResponseTranslateUtils.toLongArray(userList);
            //!!!检查是否此人在通讯录!!!
            List<ContactEntity> contactList = null;
            if (uIdArr != null && uIdArr.length > 0) {
                contactList = new ArrayList<>();
                for (long uid : uIdArr) {
                    ContactEntity contactEntity = contactMap.get(uid);
                    if (contactEntity != null) {
                        contactList.add(contactEntity);
                    }
                }
                if (contactList.isEmpty()) {
                    contactList = null;
                }
            }
            departmentEntity.setContactList(contactList);
            deptMap.put(departmentEntity.getDeptIdOnServer(), departmentEntity);
            list.add(departmentEntity);
        }

        //处理得到每个部门的子部门
        for (int i = 0; i < size; i++) {
            AddressUrlResponse.OrganizationInfo info = organizationInfo.get(i);
            long parentOid = info.parentOid;
            if (parentOid == DepartmentEntity.TOP_DEPT_ID) {
                //没有上级部门
            } else {
                DepartmentEntity parentDept = deptMap.get(parentOid);
                if (parentDept != null) {
                    List<DepartmentEntity> sonDeptList = parentDept.getDeptList();
                    if (sonDeptList == null) {
                        sonDeptList = new ArrayList<DepartmentEntity>();
                        parentDept.setDeptList(sonDeptList);
                    }
                    DepartmentEntity curDept = deptMap.get(info.oid);
                    if (curDept != null) {
                        sonDeptList.add(curDept);
                    }
                }
            }
        }
        //处理 他的上级部门
        for (int i = 0; i < size; i++) {
            AddressUrlResponse.OrganizationInfo info = organizationInfo.get(i);
            DepartmentEntity departmentEntity = deptMap.get(info.oid);
            departmentEntity.setSupDept(deptMap.get(info.parentOid));
        }
        return list;
    }

    private static ArrayList<ContactGroupEntity> getGroupList(List<AddressUrlResponse.GroupInfo> groupInfo,
                                                              List<AddressUrlResponse.GroupDdbstatus> groupDdbstatus,
                                                              HashMap<Long, ContactEntity> contactMap, long userId) {

        if (groupInfo == null || groupInfo.size() == 0) {
            return null;
        }

        Comparator<AddressUrlResponse.GroupDdbstatus> comparator = new Comparator<AddressUrlResponse.GroupDdbstatus>() {
            @Override
            public int compare(AddressUrlResponse.GroupDdbstatus o1, AddressUrlResponse.GroupDdbstatus o2) {
                return (int) (o1.gid - o2.gid);
            }
        };
        if (groupDdbstatus != null && !groupDdbstatus.isEmpty()) {
            //排序 后面二分查找
            Collections.sort(groupDdbstatus, comparator);
        }

        int size = groupInfo.size();
        ArrayList<ContactGroupEntity> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            AddressUrlResponse.GroupInfo info = groupInfo.get(i);

            int type;
            String num;
            if (!TextUtils.isEmpty(info.vgcsTel)) {
                //号码不为空  那就当时普通组 这里没有包含他是不是 动态组
                if (info.groupType == 0) {
                    type = PocConstant.GroupType.TYPE_NORMAL;
                } else if (info.groupType == 1) {
                    type = PocConstant.GroupType.TYPE_DYNAMIC;
                } else if (info.groupType == 2) {
                    type = PocConstant.GroupType.TYPE_TEMP;
                } else {
                    type = PocConstant.GroupType.TYPE_NORMAL;
                }
                num = info.vgcsTel;
            } else {
                //全当是广播组
                type = PocConstant.GroupType.TYPE_BROADCAST;
                num = info.bcTel;
            }
            String userList = info.userList;
            List<ContactEntity> contactList = null;
            if (!TextUtils.isEmpty(userList)) {
                String[] uidArr = ResponseTranslateUtils.toArray(userList);
                contactList = new ArrayList<>();
                for (String uid : uidArr) {
                    long uidInt = Long.parseLong(uid);
                    //!!!检查是否此人在通讯录!!! 在通讯录添加，不在舍弃
                    if (contactMap.containsKey(uidInt)) {
                        //存储到group
                        contactList.add(contactMap.get(uidInt));
                    }
                }
                //没有自己 需要手动添加
                ContactEntity my = contactMap.get(userId);
                if (my != null) {
                    contactList.add(0,my);
                }
            }

            boolean isShielded = false;
            if (groupDdbstatus != null && !groupDdbstatus.isEmpty()) {
                AddressUrlResponse.GroupDdbstatus ddbstatus = new AddressUrlResponse.GroupDdbstatus();
                ddbstatus.gid = info.gid;
                int index = Collections.binarySearch(groupDdbstatus, ddbstatus, comparator);
                if (index > -1) {
                    //存在这个 屏蔽了
                    isShielded = true;
                    //删除下次查找快
                    groupDdbstatus.remove(index);
                }
            }
            String creatorNum = info.createUser;

            ContactGroupEntity entity = new ContactGroupEntity(info.did, info.gid, info.name, num,
                    null, contactList, type, isShielded, creatorNum);
            list.add(entity);
        }

        return list;
    }

    private static ArrayList<ContactEntity> getContactList(AddressUrlResponse response, List<AddressUrlResponse.UserInfo> userList, HashMap<Long, ContactEntity> contactMap) {
        ArrayList<ContactEntity> list = new ArrayList<>();
        ContactEntity my = new ContactEntity(-1, response.userId, "login", response.headshot, response.name, response.tel, null, null);
        //加入自己
        list.add(my);
        contactMap.put(my.getContactIdOnServer(), my);

        if (userList != null && userList.size() > 0) {
            for (AddressUrlResponse.UserInfo userInfo : userList) {
                ContactEntity entity = new ContactEntity(userInfo.did, userInfo.uid, userInfo.st,
                        userInfo.headshot, userInfo.name,
                        userInfo.tel, null, null);
                list.add(entity);
                //加入map 后续好更新部门id
                contactMap.put(userInfo.uid, entity);
            }
            return list;
        }
        return list;
    }

}
