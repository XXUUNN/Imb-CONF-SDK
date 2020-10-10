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
    private static int maxCount;

    public static int getMaxCount() {
        return maxCount;
    }

    /**
     * 初始化
     *
     * @param remoteViewMaxCount 最大的个数
     */
    public static void init(int remoteViewMaxCount) {
        maxCount = remoteViewMaxCount;
        IdGenerator.generateIds(remoteViewMaxCount);
        IdTranslator.init(remoteViewMaxCount);
    }

    public synchronized static VideoShowManager get(int id) {
        return showManagerMap.get(id);
    }

    public synchronized static void start() {
        int id = DEFAULT_ID;
        start(id);
    }

    public synchronized static void start(int id) {
        VideoShowManager manager = showManagerMap.get(id);
        if (manager != null) {
            return;
        }
        VideoShowManager videoShowManager = new VideoShowManager(id);
        showManagerMap.put(id, videoShowManager);
        videoShowManager.start();
    }

    public synchronized static void startAll() {
        //遍历id
        List<Integer> idList = IdGenerator.getIdList();
        for (int id : idList) {
            start(id);
        }
    }

    public synchronized static void stop(int id) {
        VideoShowManager videoShowManager = showManagerMap.get(id);
        if (videoShowManager != null) {
            videoShowManager.stop();
            showManagerMap.remove(id);
        }
    }

    public synchronized static void stopAll() {
        int size = showManagerMap.size();
        for (int i = 0; i < size; i++) {
            VideoShowManager videoShowManager = showManagerMap.get(showManagerMap.keyAt(i));
            videoShowManager.stop();
        }
        showManagerMap.clear();
    }

    public synchronized static void clearAllVideo() {
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
