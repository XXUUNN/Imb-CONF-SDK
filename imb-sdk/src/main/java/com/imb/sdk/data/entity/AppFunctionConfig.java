package com.imb.sdk.data.entity;


import com.imb.sdk.data.Constant;

import java.io.Serializable;

/**
 * @author - gongxun;
 * created on 2019/4/22-15:54;
 * description - app的功能配置
 */
public class AppFunctionConfig implements Serializable {

    /**
     * 视频配置
     */
    public VideoConfig videoConfig;

    /**
     * 声音配置
     */
    public VoiceConfig voiceConfig;

    /**
     * log文件的配置
     */
    public LogConfig logConfig;

    /**
     * 登录的配置
     */
    public LoginConfig loginConfig;

    public AppFunctionConfig() {
        videoConfig = new VideoConfig();
        voiceConfig = new VoiceConfig();
        logConfig = new LogConfig();
        loginConfig = new LoginConfig();
    }

    @Override
    public String toString() {
        return "AppFunctionConfig{" +
                "videoConfig=" + videoConfig +
                ", voiceConfig=" + voiceConfig +
                ", logConfig=" + logConfig +
                ", loginConfig=" + loginConfig +
                '}';
    }

    /**
     * 视频配置
     */
    public static class VideoConfig implements Serializable {
        public int width;
        public int height;

        /**
         * 是否硬编码视频
         */
        public boolean isHardVideoEnc;
        /**
         * 是否硬解码视频
         */
        public boolean isHardVideoDec;
        /**
         * 发出的视频的码率
         */
        public int bitRate;
        /**
         * 视频关键帧间隔s
         */
        public int iFrameTime;

        public int frameRate;

        public int maxBitRate;
        public int sendCount;
        public int loopTime;

        public int rate;
        public int useuepprot;
        public int maxFrames;
        public int maskType;

        public int multipleThreadId;
        public int frameSkipEnable;
        public int sliceMode;

        public int mtu;

        public VideoConfig() {
            defaultConfig();
        }

        private void defaultConfig() {
            this.width = Constant.DEFAULT_WIDTH;
            this.height = Constant.DEFAULT_HEIGHT;

            frameRate = 15;
            bitRate = 1024;
            isHardVideoEnc = true;
            isHardVideoDec = false;
            iFrameTime = 2;

            maxBitRate = 1024;
            sendCount = 1;
            loopTime = 10;

            rate = 64;
            useuepprot = 0;
            maxFrames = 1;
            maskType = 0;

            multipleThreadId = 3;
            frameSkipEnable = 0;
            sliceMode = 4;

            mtu = 1400;
        }

        @Override
        public String toString() {
            return "VideoConfig{" +
                    "width=" + width +
                    ", height=" + height +
                    ", isHardVideoEnc=" + isHardVideoEnc +
                    ", isHardVideoDec=" + isHardVideoDec +
                    ", bitRate=" + bitRate +
                    ", iFrameTime=" + iFrameTime +
                    ", frameRate=" + frameRate +
                    ", maxBitRate=" + maxBitRate +
                    ", sendCount=" + sendCount +
                    ", loopTime=" + loopTime +
                    ", rate=" + rate +
                    ", useuepprot=" + useuepprot +
                    ", maxFrames=" + maxFrames +
                    ", maskType=" + maskType +
                    ", multipleThreadId=" + multipleThreadId +
                    ", frameSkipEnable=" + frameSkipEnable +
                    ", sliceMode=" + sliceMode +
                    ", mtu=" + mtu +
                    '}';
        }
    }

    /**
     * 声音的相关的配置
     */
    public static class VoiceConfig implements Serializable {
        public int aecmType;
        public int aecmEnable;
        public int aecmMode;
        public int nsEnable;
        public int nsMode;
        public int agcMode;
        public int agcEnable;
        public int levelDB;
        public int gainDB;


        public int streamDelay;
        public int streamAnalogLevel;

        public int samplePerChn;
        public int sampleRateHz;
        public int channelNum;
        public int volumeSent;

        public VoiceConfig() {
            defaultConfig();
        }

        private void defaultConfig() {
            aecmType = 0;
            aecmEnable = 1;
            aecmMode = 4;

            nsEnable = 1;
            nsMode = 3;
            agcEnable = 1;
            agcMode = 1;

            levelDB = 5;
            gainDB = 3;

            streamDelay = 170;
            streamAnalogLevel = 100;

            samplePerChn = 160;
            sampleRateHz = 16000;
            channelNum = 1;
            volumeSent = 1;
        }

        @Override
        public String toString() {
            return "VoiceConfig{" +
                    "aecmType=" + aecmType +
                    ", aecmEnable=" + aecmEnable +
                    ", aecmMode=" + aecmMode +
                    ", nsEnable=" + nsEnable +
                    ", nsMode=" + nsMode +
                    ", agcMode=" + agcMode +
                    ", agcEnable=" + agcEnable +
                    ", levelDB=" + levelDB +
                    ", gainDB=" + gainDB +
                    ", streamDelay=" + streamDelay +
                    ", streamAnalogLevel=" + streamAnalogLevel +
                    ", samplePerChn=" + samplePerChn +
                    ", sampleRateHz=" + sampleRateHz +
                    ", channelNum=" + channelNum +
                    ", volumeSent=" + volumeSent +
                    '}';
        }
    }

    public static class LogConfig implements Serializable {
        /**
         * 是否打开poc日志
         */
        public boolean isFileLogEnable = false;

        public int maxSizeBytes = Constant.MAX_LOG_DIR_SIZE_BYTES;

        public String logDir;

        @Override
        public String toString() {
            return "LogConfig{" +
                    "isFileLogEnable=" + isFileLogEnable +
                    ", maxSizeBytes=" + maxSizeBytes +
                    ", logDir='" + logDir + '\'' +
                    '}';
        }
    }

    public static class LoginConfig implements Serializable {
        /**
         * 默认登录行为8秒就超时了
         */
        public long loginTimeOut = Constant.DEFAULT_LOGIN_TIME_OUT;
        /**
         * 注册过期时间s
         */
        public int registerExpireTime;

        /**
         * 本地ip
         */
        public String localIp;

        /**
         * 服务端的ip
         */
        public String serverIp;
        /**
         * 服务端的协议通讯端口
         */
        public int serverPort;

        /**
         * 是否启用同步通讯录
         * true同步
         */
        private boolean enableSyncAddressBook;

        /**
         * 服务端的通讯录同步的sftp端口
         */
        private int sftpPort;

        /**
         * sftp用户名
         */
        private String sftpUserName;
        /**
         * 默认的sftp账户密码
         */
        private String sftpUserPwd;

        /**
         * 自己上传的文件存在服务器上的哪个目录下
         */
        private String dirOnServer;

        /**
         * 同步通讯录过程中产生的临时文件放的位置
         */
        private String tempFileParentDir;

        /**
         * sip的协议依赖
         * 0 tcp
         * 1 udp
         * 默认udp
         */
        public int sipProtocolDependency;

        /**
         * 客户端版本号 如：1.1.1
         */
        public String versionName;

        public LoginConfig() {
            defaultConfig();
        }

        private void defaultConfig() {
            registerExpireTime = Constant.DEFAULT_POC_EXPIRE_TIME;

            serverPort = Constant.DEFAULT_SIP_PORT;

            enableSyncAddressBook = false;

            sipProtocolDependency = 1;
        }

        public void setSyncAddressBookConfig(int sftpPort, String sftpUserName,
                                             String sftpUserPwd, String dirOnServer, String tempFileParentDir) {
            this.sftpPort = sftpPort;
            this.sftpUserName = sftpUserName;
            this.sftpUserPwd = sftpUserPwd;
            this.dirOnServer = dirOnServer;
            this.tempFileParentDir = tempFileParentDir;

        }

        public void enableSyncAddressBook(boolean enable) {
            enableSyncAddressBook = enable;
        }

        public boolean isEnableSyncAddressBook() {
            return enableSyncAddressBook;
        }

        public int getSftpPort() {
            return sftpPort;
        }

        public String getSftpUserName() {
            return sftpUserName;
        }

        public String getSftpUserPwd() {
            return sftpUserPwd;
        }

        public String getDirOnServer() {
            return dirOnServer;
        }

        public String getTempFileParentDir() {
            return tempFileParentDir;
        }

        @Override
        public String toString() {
            return "LoginConfig{" +
                    "loginTimeOut=" + loginTimeOut +
                    ", registerExpireTime=" + registerExpireTime +
                    ", localIp='" + localIp + '\'' +
                    ", serverIp='" + serverIp + '\'' +
                    ", serverPort=" + serverPort +
                    ", enableSyncAddressBook=" + enableSyncAddressBook +
                    ", sftpPort=" + sftpPort +
                    ", sftpUserName='" + sftpUserName + '\'' +
                    ", sftpUserPwd='" + sftpUserPwd + '\'' +
                    ", dirOnServer='" + dirOnServer + '\'' +
                    ", tempFileParentDir='" + tempFileParentDir + '\'' +
                    ", sipProtocolDependency=" + sipProtocolDependency +
                    ", versionName='" + versionName + '\'' +
                    '}';
        }
    }
}
