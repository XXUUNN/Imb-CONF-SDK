package com.imb.sdk.addressbook;

import android.text.TextUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 已经替换为http方式 不用sftp
 */
@Deprecated
public class SftpUtils {
    protected String host;
    protected int port;
    protected String username;
    protected String password;

    protected ChannelSftp sftp;

    private boolean isConnected = false;

    private OnConnectCallbackListener listener;

    public boolean isConnected() {
        if (sftp != null) {
            if (!sftp.isClosed() && sftp.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public interface OnConnectCallbackListener {
        void connectCallback(boolean isConnected);
    }


    /**
     * @param host     ip
     * @param port     端口
     * @param username 账号
     * @param password 密码
     */
    public SftpUtils(String host, int port, String username, String password, OnConnectCallbackListener listener) {
        this.listener = listener;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;

        startConnect();
    }

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Future task;

    private volatile boolean isToConnect = false;

    /**
     * 获取当前的连接sftp的状态
     *
     * @return 0成功 -1未连接成功正在重连 -2未连接成功
     */
    public int getConnectState() {
        if (isConnected()) {
            return 0;
        } else {
            if (isToConnect) {
                return -1;
            } else {
                return -2;
            }
        }
    }

    public void stopConnect() {
        if (task != null) {
            isToConnect = false;
            reConnectCount = 0;
            task.cancel(true);
            task = null;
        }
    }

    public void startConnect() {
        task = executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (null == sftp || !sftp.isConnected()) {
                    isToConnect = true;
                    connect();
                    isToConnect = false;
                }
            }
        });
    }

    private Session sshSession = null;

    /**
     * 重连次数
     */
    private int reConnectCount = 0;

    /**
     * 链接linux
     */
    private void connect() {
        if (!isToConnect) {
            return;
        }
        reConnectCount++;
        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect(30 * 1000);
            Channel channel = sshSession.openChannel("sftp");
            channel.connect(10 * 1000);
            sftp = (ChannelSftp) channel;

            isConnected = true;

        } catch (Exception e) {
            e.printStackTrace();
            closeSftp();
        } finally {
            if (isConnected) {
                if (listener != null) {
                    listener.connectCallback(true);
                }
                task = null;
            } else {
                if (reConnectCount <= 3) {
                    //重连最多3次
                    connect();
                } else {
                    if (listener != null) {
                        listener.connectCallback(false);
                    }
                    reConnectCount = 0;
                    task = null;
                }
            }
        }
    }

    /**
     * linux上传文件
     */
    public boolean upload(String directory, File file) {
        boolean isUpLoadSuc = false;
        try {
            if (null != sftp) {
                sftp.cd(directory);
                sftp.put(new FileInputStream(file), file.getName());
                isUpLoadSuc = true;

            }
        } catch (Exception e) {
            e.printStackTrace();
            isUpLoadSuc = false;
        }
        return isUpLoadSuc;
    }

    /**
     * linux下载文件
     */
    public boolean get(String srcFile, String downloadFile) {
        boolean isDownLoadSuc = false;
        try {
            if (null != sftp) {
                File file = new File(srcFile);
                String parent = getParent(file);
                sftp.cd(parent);
                File desc = new File(downloadFile);
                FileOutputStream outputStream = new FileOutputStream(desc);
                sftp.get(file.getName(), outputStream);
                outputStream.close();
                isDownLoadSuc = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isDownLoadSuc = false;

        }
        return isDownLoadSuc;
    }

    /**
     * 删除服务器上的文件
     *
     * @param deleteFileW 服务端发下来
     * @param deleteFileR 客户端发上去的
     */
    public void deleteFile1(String deleteFileR, String deleteFileW) {
        try {
            String parent;
            if (!TextUtils.isEmpty(deleteFileR)) {
                File fileR = new File(deleteFileR);
                parent = fileR.getParent();
                sftp.cd(parent);
                sftp.rm(deleteFileR);
            }
            if (!TextUtils.isEmpty(deleteFileW)) {
                File fileW = new File(deleteFileW);
                parent = fileW.getParent();
                sftp.cd(parent);
                sftp.rm(deleteFileW);
            }

        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    private void closeSftp() {
        if (sftp != null) {
            sftp.disconnect();
            sftp.exit();
            sftp = null;
        }
        if (sshSession != null) {
            sshSession.disconnect();
        }
    }

    public void close() {
        stopConnect();

        isConnected = false;

        closeSftp();
    }


    protected String getParent(File desc) {
        return desc.getParent();
    }

}