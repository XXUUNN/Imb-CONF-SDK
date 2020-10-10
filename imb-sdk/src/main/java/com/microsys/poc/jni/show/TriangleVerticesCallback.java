package com.microsys.poc.jni.show;

import java.nio.FloatBuffer;

/**
 * @author - gongxun;
 * created on 2020/10/10-14:45;
 * description - 矩阵回调
 */
public interface TriangleVerticesCallback {
    /**
     * 每一帧绘画 的矩阵的回调 控制视频的绘制
     *
     * @param mTriangleVertices 矩阵
     * @param screenDir         本机的方向
     * @param direction         远程视频的方向
     */
    void callback(FloatBuffer mTriangleVertices, int screenDir, int direction);
}
