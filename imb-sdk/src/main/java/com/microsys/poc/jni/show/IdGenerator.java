package com.microsys.poc.jni.show;

import java.util.ArrayList;
import java.util.List;

/**
 * @author - gongxun;
 * created on 2020/7/23-11:49;
 * description - 生成id 0-7
 */
public class IdGenerator {
    /**
     * 默认的id为0
     */
    public static final int DEFAULT_ID = 0;

    private static ArrayList<Integer> idsPool;

    private static void checkState() {
        if (idsPool == null) {
            throw new IllegalStateException("not initial, should call generateIds() first");
        }
    }

    public static void generateIds(int maxCount) {
        idsPool = new ArrayList<>(maxCount);
        for (int i = 0; i < maxCount; i++) {
            idsPool.add(DEFAULT_ID + i);
        }
    }

    /**
     * 获取第几个id
     *
     * @param index 从0 开始
     * @return id
     */
    public static int getId(int index) {
        checkState();
        return idsPool.get(index);
    }

    public static List<Integer> getIdList() {
        checkState();
        return new ArrayList<>(idsPool);
    }

}
