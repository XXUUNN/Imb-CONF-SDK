package com.microsys.poc.jni.show;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author - gongxun;
 * created on 2020/7/16-10:00;
 * description - 转换ssrc到id
 * 做对应关系 控制流显示在哪个id里
 */
public class IdTranslator {
    private static final String TAG = IdTranslator.class.getSimpleName();

    private static Map<Integer, Integer> relationshipMap;

    public static void init(int maxStreamsCount){
        relationshipMap = new HashMap<>(maxStreamsCount);
    }

    public static void updateRelationship(ArrayList<Integer> relationship) {
        if (relationshipMap == null) {
            throw new RuntimeException("init() should be called first");
        }
        relationshipMap.clear();
        if (relationship != null && !relationship.isEmpty()) {
            //按顺序 生成对应的id
            for (int i = 0, size = relationship.size(); i < size; i++) {
                int id = generateId(i);
                relationshipMap.put(relationship.get(i), id);
            }
        }
        Log.i(TAG, "updateRelationship: "+relationshipMap);
    }

    /**
     * 生成id对应ssrc 更改此规则 可以调整画面的显示位置、
     * 要求：0-7
     *
     * @return id
     */
    private static int generateId(int index) {
        return IdGenerator.getId(index);
    }

    /**
     * 防止ssrc在关系没有存储
     *
     * @param id {@link #toId(Integer)}生成的id
     * @return true存在对应的id
     */
    public static boolean checkValid(int id) {
        return id < 0 ? false : true;
    }

    public static int toId(Integer ssrc) {
        if (relationshipMap == null) {
            throw new RuntimeException("init() should be called first");
        }

        Integer id = relationshipMap.get(ssrc);
        if (id == null) {
            return -1;
        } else {
            return id;
        }
    }
}
