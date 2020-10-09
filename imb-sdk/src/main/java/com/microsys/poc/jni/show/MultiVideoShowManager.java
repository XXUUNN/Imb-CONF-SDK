package com.microsys.poc.jni.show;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * @author - gongxun;
 * created on 2020/4/21-11:29;
 * description -
 */
public class MultiVideoShowManager {
    public static final int DEFAULT_ID = IdGenerator.DEFAULT_ID;
    private static SparseArray<VideoShowManager> showManagerMap = new SparseArray<>();

    /**
     * 初始化
     *
     * @param remoteViewMaxCount 最大的个数
     */
    public static void init(int remoteViewMaxCount) {
        IdGenerator.generateIds(remoteViewMaxCount);
        IdTranslator.init(remoteViewMaxCount);
    }

    public static VideoShowManager get(int id) {
        return showManagerMap.get(id);
    }

    public static void start() {
        int id = DEFAULT_ID;
        start(id);
    }

    public static void start(int id) {
        VideoShowManager manager = showManagerMap.get(id);
        if (manager != null) {
            return;
        }
        VideoShowManager videoShowManager = new VideoShowManager(id);
        showManagerMap.put(id, videoShowManager);
        videoShowManager.start();
    }

    public static void startAll() {
        //遍历id
        List<Integer> idList = IdGenerator.getIdList();
        for (int id : idList) {
            start(id);
        }
    }

    public static void stop(int id) {
        VideoShowManager videoShowManager = showManagerMap.get(id);
        if (videoShowManager != null) {
            videoShowManager.stop();
            showManagerMap.remove(id);
        }
    }

    public static void stopAll() {
        int size = showManagerMap.size();
        for (int i = 0; i < size; i++) {
            VideoShowManager videoShowManager = showManagerMap.get(showManagerMap.keyAt(i));
            videoShowManager.stop();
        }
        showManagerMap.clear();
    }

    public static void clearAllVideo() {
        int size = showManagerMap.size();
        for (int i = 0; i < size; i++) {
            VideoShowManager videoShowManager = showManagerMap.get(showManagerMap.keyAt(i));
            videoShowManager.clearVideoData();
        }
    }

    public static int getId(int index) {
        return IdGenerator.getId(index);
    }

    public static void updateRelationship(ArrayList<Integer> relationship) {
        IdTranslator.updateRelationship(relationship);
    }

    public static int ssrcToId(int ssrc) {
        return IdTranslator.toId(ssrc);
    }

    public static boolean checkIdValid(int id) {
        return IdTranslator.checkValid(id);
    }

}
