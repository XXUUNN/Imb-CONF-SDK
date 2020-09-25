package com.microsys.poc.jni.entity;

import com.microsys.poc.jni.entity.type.PocTemporaryGroupType;

/**
 * @author Administrator
 */
public class GroupOperatingResult {
    public boolean isOk;
    public PocTemporaryGroupType type;
    /**
     * 组的号码
     */
    public String num;

    public GroupOperatingResult(String num, PocTemporaryGroupType type, boolean isOk) {
        this.num = num;
        this.isOk = isOk;
        this.type = type;
    }
}