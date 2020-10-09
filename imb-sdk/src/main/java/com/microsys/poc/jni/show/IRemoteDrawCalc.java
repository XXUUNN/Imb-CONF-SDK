package com.microsys.poc.jni.show;

/**
 * @author - gongxun;
 * created on 2020/4/24-16:18;
 * description - 计算画yuv的位置
 */
public interface IRemoteDrawCalc {
    class Params {
        /**
         * 画布的宽
         */
        public int surfaceW;
        /**
         * 画布的高
         */
        public int surfaceH;

        /**
         * 相对画布的左下角的的视频左下角的点的横坐标
         */
        public int x;
        /**
         * 相对画布的左下角的的视频左下角的点的纵坐标
         */
        public int y;
        /**
         * 视频展现的宽
         */
        public int w;
        /**
         * 视频展现的高
         */
        public int h;

        /**
         * 视频数据的宽
         */
        int videoW;
        /**
         * 视频数据的高
         */
        int videoH;

        /**
         * 视频的方向
         */
        int videoDir;

        /**
         * 当前设备屏幕的方向
         */
        int screenDir;

        public int getVideoW() {
            return videoW;
        }

        public int getVideoH() {
            return videoH;
        }

        public int getVideoDir() {
            return videoDir;
        }

        public int getScreenDir() {
            return screenDir;
        }

        public Params(int surfaceW, int surfaceH, int x, int y, int w, int h, int videoW, int videoH,
                      int videoDir, int screenDir) {
            this.surfaceW = surfaceW;
            this.surfaceH = surfaceH;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.videoW = videoW;
            this.videoH = videoH;
            this.videoDir = videoDir;
            this.screenDir = screenDir;
        }

        public Params() {
        }
    }

    /**
     * 计算显示位置
     * 修改参数就生效
     * @param params 计算显示位置所需的参数 动态修改生效
     */
    void calc(Params params);
}
