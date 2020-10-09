package com.imb.sdk;

import android.text.TextUtils;

import com.imb.sdk.data.PocConstant;
import com.microsys.poc.jni.JniUtils;
import com.microsys.poc.jni.entity.type.PocTemporaryGroupType;
import com.microsys.poc.jni.entity.type.TempOraryGroupMode;

import java.util.List;

/**
 * @author - gongxun;
 * created on 2019/7/4-17:10;
 * description - 组的操作
 */
public class GroupOperationHelper {
    public static void createDynamicGroup(String groupName, List<String> members, String myNum) {
        //获取组成员
        StringBuilder membersSb = new StringBuilder();
        for (String number : members) {
            if (number != null) {
                membersSb.append(number).append("/");
            }
        }
        //添加上自己
        membersSb.append(myNum);
        String membersStr = membersSb.toString();
        //创建为动态组
        JniUtils.getInstance().PocTemporaryGroup(
                PocTemporaryGroupType.getTypeof(PocTemporaryGroupType.CREATE),
                groupName, membersStr,
                TempOraryGroupMode.getTypeof(TempOraryGroupMode.DYNAMIC));
    }

    public static void createDynamicGroup(String groupName, String[] members, String myNum) {
        //获取组成员
        StringBuilder membersSb = new StringBuilder();
        for (String num : members) {
            if (!TextUtils.isEmpty(num)) {
                membersSb.append(num).append("/");
            }
        }
        //添加上自己
        membersSb.append(myNum);
        String membersStr = membersSb.toString();
        //创建为动态组
        JniUtils.getInstance().PocTemporaryGroup(
                PocTemporaryGroupType.getTypeof(PocTemporaryGroupType.CREATE),
                groupName, membersStr,
                TempOraryGroupMode.getTypeof(TempOraryGroupMode.DYNAMIC));
    }

    /**
     * 创建临时组
     */
    public static void createTempGroup(String groupName, String[] members, String myNum) {
        //获取组成员
        StringBuilder membersSb = new StringBuilder();
        for (String num : members) {
            if (!TextUtils.isEmpty(num)) {
                membersSb.append(num).append("/");
            }
        }
        //添加上自己
        membersSb.append(myNum);
        String membersStr = membersSb.toString();
        //创建为动态组
        JniUtils.getInstance().PocTemporaryGroup(
                PocTemporaryGroupType.getTypeof(PocTemporaryGroupType.CREATE),
                groupName, membersStr,
                TempOraryGroupMode.getTypeof(TempOraryGroupMode.TEMP));
    }

    public static void updateGroupName(String num, String newName) {
        // 修改群名字
        JniUtils.getInstance()
                .PocTemporaryGroup(
                        PocTemporaryGroupType
                                .getTypeof(PocTemporaryGroupType.NAMEMOD),
                        num,
                        newName,
                        TempOraryGroupMode
                                .getTypeof(TempOraryGroupMode.TEMP));

    }

    public static boolean deleteGroup(String num) {
        int result = JniUtils.getInstance().PocTemporaryGroup(
                PocTemporaryGroupType
                        .getTypeof(PocTemporaryGroupType.CLOSE),
                num, "",
                0);
        return result == 0 ? true : false;
    }

    public static void deleteMember(String groupNum, String userName) {
        if (TextUtils.isEmpty(groupNum) || TextUtils.isEmpty(userName)) {
            return;
        }
        // 删除用户
        JniUtils.getInstance().PocTemporaryGroup(
                PocTemporaryGroupType
                        .getTypeof(PocTemporaryGroupType.USERDEL),
                groupNum, userName,
                0);
    }

    public static void addMembers(String groupNum, int type, String[] members, String myNum) {
        //获取组成员
        StringBuilder membersSb = new StringBuilder();
        for (String num : members) {
            if (!TextUtils.isEmpty(num)) {
                membersSb.append(num).append("/");
            }
        }
        //添加上自己
        membersSb.append(myNum);
        String membersStr = membersSb.toString();
        if (type == PocConstant.GroupType.TYPE_DYNAMIC) {
            JniUtils.getInstance().PocTemporaryGroup(PocTemporaryGroupType.getTypeof(PocTemporaryGroupType.USERADD),
                    groupNum, membersStr, TempOraryGroupMode.getTypeof(TempOraryGroupMode.DYNAMIC));
        } else if (type == PocConstant.GroupType.TYPE_TEMP) {
            JniUtils.getInstance().PocTemporaryGroup(PocTemporaryGroupType.getTypeof(PocTemporaryGroupType.USERADD),
                    groupNum, membersStr, TempOraryGroupMode.getTypeof(TempOraryGroupMode.TEMP));
        }
    }
}
