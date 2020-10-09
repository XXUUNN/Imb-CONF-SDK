package com.microsys.poc.jni.base;

import com.microsys.poc.jni.entity.GroupEditResult;
import com.microsys.poc.jni.entity.GroupOperatingResult;
import com.microsys.poc.jni.entity.type.PocTemporaryGroupType;
import com.microsys.poc.jni.listener.GroupEditResultListener;


public abstract class BaseGroupEditResultListener implements GroupEditResultListener {

    @Override
    public void onResultCallback(GroupEditResult group) {
        boolean isOk = false;
        if (group.getRet() == 0) {
            //成功
            isOk = true;
        }
        onGroupOperatingResult(new GroupOperatingResult(group.getGroupTel(), PocTemporaryGroupType.of(group.getType()), isOk));
    }

    /**
     * 编辑组结果
     *
     * @param result 结果
     */
    protected abstract void onGroupOperatingResult(GroupOperatingResult result);

}
