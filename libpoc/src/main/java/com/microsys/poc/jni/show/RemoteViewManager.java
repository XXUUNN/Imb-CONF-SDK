package com.microsys.poc.jni.show;

import android.util.SparseArray;

/**
 * @author - gongxun;
 * created on 2020/4/20-15:29;
 * description - 创建和
 */
public class RemoteViewManager {
    private static SparseArray<RemoteVideoView> remoteVideoViewMap = new SparseArray<>();

    public static RemoteVideoView createInstance(int uniqueId, int videoWidth, int videoHeight, int screenOrientation) {
        RemoteVideoView remoteVideoView = new RemoteVideoViewGL(uniqueId, videoWidth, videoHeight, screenOrientation);
        //检查缓存
        RemoteVideoView temp = remoteVideoViewMap.get(uniqueId);
        if (temp != null) {
            //已经添加同样key的
            throw new IllegalArgumentException("不合法的参数 uniqueId,必须不同的");
//            Log.e("RemoteViewManager", "不合法的参数 uniqueId,必须不同的" );
        }
        //添加到缓存
        remoteVideoViewMap.put(uniqueId, remoteVideoView);
        return remoteVideoView;
    }

    /**
     * 如果只有一个的话就返回那个，无论参数多少
     *
     * @param unique remoteView对应的唯一值
     * @return removeView
     */
    public static RemoteVideoView adaptiveGet(int unique) {
        int size = remoteVideoViewMap.size();
        if (size == 0) {
            return null;
        } else if (size == 1) {
            RemoteVideoView remoteVideoView_3 = remoteVideoViewMap.valueAt(0);
            return remoteVideoView_3;
        } else {
            RemoteVideoView remoteVideoView_3 = remoteVideoViewMap.get(unique);
//            if (remoteVideoView_3 == null) {
//                throw new RuntimeException("unique 对应的RemoteVideoView为null");
//            }
            return remoteVideoView_3;
        }
    }

    public static void destroyInstance(int unique) {
        remoteVideoViewMap.remove(unique);
    }

    public static void destroyAll() {
        //清空
        remoteVideoViewMap.clear();
    }
}
