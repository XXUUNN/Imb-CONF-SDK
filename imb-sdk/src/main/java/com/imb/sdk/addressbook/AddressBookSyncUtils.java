package com.imb.sdk.addressbook;

import android.text.TextUtils;
import android.util.Log;

import com.imb.sdk.Poc;
import com.imb.sdk.listener.PocSyncAddressBookListener;
import com.imb.sdk.login.FileUtils;
import com.microsys.poc.jni.JniUtils;
import com.microsys.poc.jni.entity.SyncAddressBookResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.Semaphore;

/**
 * @author - gongxun;
 * created on 2019/4/24-11:42;
 * description - 同步通讯录 会上传下载
 * Deprecated. be instead of {@link AddressBookSyncByHttp}
 */
@Deprecated
public class AddressBookSyncUtils {
    private static final String TAG = AddressBookSyncUtils.class.getSimpleName();

    /**
     * 保证上传到服务器的文件名字不一样 避免请求间隔短的话 会删除服务端下一次生成的文件
     */
    private static int tag = 0;

    /**
     * 同步通讯录  先上传通讯录
     * 会生成一个临时文件在上传到服务器
     * 上传完后会删除这个临时文件
     *
     * @param tempFileParentPath 临时文件存放的文件夹
     * @param parentPathOnServer 传到服务器的指定的文件夹 绝对路径-/home/temp
     * @param userNum            自己的号码
     * @return 返回了上传到服务端的文件的名字 如果服务器返回了结果 就需要自己去删除这个文件
     */
    public static String syncAddressBookRequest(SftpUtils sftp, String userNum, String parentPathOnServer, String tempFileParentPath) {

        String addressBookStr = getLocalAddressBook(userNum);
        String tempFileName = userNum + "Tmp" + tag++ + ".txt";

        String filePath = FileUtils.writeToFile(tempFileName, addressBookStr, tempFileParentPath);
        File tmpFile = new File(filePath);
        boolean isOk = sftp.upload(parentPathOnServer, tmpFile);

        String pathOnServer = parentPathOnServer
                + File.separator + tempFileName;
        JniUtils.getInstance().PocSendLocalAddrListUrl(pathOnServer);
        if (isOk) {
            //删除临时文件
            tmpFile.delete();
        }
        return pathOnServer;
    }

    /**
     * 获取本地所有的contact和group
     * 并转换为服务端需要的json
     * {"tel":"","did_list":"34/33"}
     * did_list为所有数据的did 服务端只会下发 不包含这些did的数据
     *
     * @param userNum 自己的号码
     */
    private static String getLocalAddressBook(String userNum) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tel", userNum);
            // TODO: 2019/4/29 暂时更新所有的
            jsonObject.put("did_list", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * 数据量大可能会比较耗时 建议子线程
     * 处理同步结果
     * 下载文件 读取内容
     * 删除本地和远程临时文件
     *
     * @param filePathOnServer            服务器产生的文件的路径
     * @param localOnServer               自己上传到服务器的文件路径
     * @param tempFileParentServerOnLocal 服务器的文件下载到本地的哪个文件夹
     * @param userName                    用户名
     * @return nullable 服务端返回的通讯录内容 为空说明下载失败
     */
    public static String handleResponse(String filePathOnServer, String localOnServer, String tempFileParentServerOnLocal,
                                        SftpUtils sftp, String userName) {
        //下载
        String tmpFile = tempFileParentServerOnLocal + File.separator + userName + "TmpDiff.txt";
        boolean isOk = sftp.get(filePathOnServer, tmpFile);
        String content = null;
        if (isOk) {
            //读取文件
            content = FileUtils.readFile(tmpFile);
        }
        //删除本地
        File localFile = new File(tmpFile);
        if (localFile.exists()) {
            localFile.delete();
        }
        //删除远程文件
        sftp.deleteFile1(localOnServer, filePathOnServer);
        return content;
    }


    private static AddressBookCallback callback;
    private static Thread addressBookListenThread = null;
    private static volatile boolean isThreadRunning = false;
    private static Semaphore semaphore;

    public static void request() {
        if (isThreadRunning) {
            if (semaphore != null) {
                semaphore.release();
            }
        }
    }

    /**
     * 开启一个常在的通讯录请求回调监听
     */
    public synchronized static void startAddressBookListen(AddressBookCallback addressBookCallback,
                                                           final String myPocNum,
                                                           final String host, final int port,
                                                           final String userName, final String pwd,
                                                           final String pathOnServer, final String tempFileParentPath) {
        if (isThreadRunning || addressBookListenThread != null) {
            return;
        }
        callback = addressBookCallback;
        isThreadRunning = true;
        semaphore = new Semaphore(0);
        addressBookListenThread = new Thread(new SyncAddressBookRunnable(myPocNum,
                host, port,
                userName, pwd,
                pathOnServer, tempFileParentPath));
        addressBookListenThread.start();
    }

    /**
     * 停止通讯录更新监听
     */
    public synchronized static void stopAddressBookListen() {
        if (addressBookListenThread != null) {
            if (isThreadRunning) {
                isThreadRunning = false;
                addressBookListenThread.interrupt();
            }
            addressBookListenThread = null;
            semaphore = null;
        }
    }

    private static class SyncAddressBookRunnable implements Runnable {
        private volatile SyncAddressBookResult result;
        private String host;
        private int port;
        private String userName;
        private String pwd;
        private String myPocNum;
        private String pathOnServer;
        private String tempFileParentPath;

        public SyncAddressBookRunnable(
                String myPocNum,
                String host, int port,
                String userName, String pwd,
                String pathOnServer, String tempFileParentPath) {
            this.host = host;
            this.port = port;
            this.userName = userName;
            this.pwd = pwd;
            this.myPocNum = myPocNum;
            this.pathOnServer = pathOnServer;
            this.tempFileParentPath = tempFileParentPath;
        }

        @Override
        public void run() {
            Log.i(TAG, "AddressBookListen: start");
            SftpUtils sftpUtils = new SftpUtils(host, port, userName, pwd, null);
            int connectState = sftpUtils.getConnectState();
            while (isThreadRunning && connectState != 0) {
                //等待连接成功
                if (connectState == -2) {
                    //连接失败 需要重新连接
                    sftpUtils.startConnect();
                }
                Log.i(TAG, "AddressBookListen: wait for <sftp connected>");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connectState = sftpUtils.getConnectState();
            }
            Log.i(TAG, "AddressBookListen: sftp connected");
            while (isThreadRunning) {
                Log.i(TAG, "AddressBookListen: ready for a request. if 0 then wait");
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                checkAndTryReconnectSftp(sftpUtils);
                if (!isThreadRunning) {
                    Log.i(TAG, "AddressBookListen: isThreadRunning = false");
                    break;
                }
                Log.i(TAG, "AddressBookListen: execute a request");
                PocSyncAddressBookListener pocSyncAddressBookListener = new PocSyncAddressBookListener() {
                    @Override
                    protected void onReceivedAddressBookPath(SyncAddressBookResult ret) {
                        result = ret;
                    }
                };
                registerSyncAddressBookListener(pocSyncAddressBookListener);
                String resultPathOnServer = syncAddressBookRequest(sftpUtils, myPocNum,
                        pathOnServer, tempFileParentPath);
                Log.i(TAG, "AddressBookListen: wait...  request result");
                while (isThreadRunning && result == null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                unregisterSyncAddressBookListener(pocSyncAddressBookListener);
                Log.i(TAG, "AddressBookListen: wait...  over");
                checkAndTryReconnectSftp(sftpUtils);
                if (!isThreadRunning) {
                    Log.i(TAG, "AddressBookListen: isThreadRunning = false");
                    break;
                }
                Log.i(TAG, "AddressBookListen: wait...  handle request result");
                if (result.isSuccessful
                        && !TextUtils.isEmpty(result.diffAddressBookFilePathOnServer)) {
                    Log.i(TAG, "AddressBookListen: request ok");
                    String content = AddressBookSyncUtils.handleResponse(result.diffAddressBookFilePathOnServer,
                            resultPathOnServer, tempFileParentPath,
                            sftpUtils, myPocNum);
                    Log.i(TAG, "AddressBookListen: handleResponse ok = " + content);
                    if (callback != null) {
                        callback.onReceiveAddressBook(content);
                    }
                } else {
                    Log.i(TAG, "AddressBookListen: request error");
                }
                Log.i(TAG, "AddressBookListen: to next request");
            }
            sftpUtils.close();
            Log.i(TAG, "AddressBookListen: end");
        }

        private void unregisterSyncAddressBookListener(PocSyncAddressBookListener
                                                               pocSyncAddressBookListener) {
            Poc.unregisterListener(pocSyncAddressBookListener);
        }

        private void registerSyncAddressBookListener(PocSyncAddressBookListener pocSyncAddressBookListener) {
            if (pocSyncAddressBookListener != null) {
                Poc.registerListener(pocSyncAddressBookListener);
            }
        }

        /**
         * 避免断网在恢复时 sftp已经断开
         */
        private void checkAndTryReconnectSftp(SftpUtils sftpUtils) {
            int connectState = sftpUtils.getConnectState();
            Log.i(TAG, "AddressBookListen: check sftp " + connectState);
            while (isThreadRunning && connectState != 0) {
                //等待连接成功
                if (connectState == -2) {
                    //连接失败 需要重新连接
                    sftpUtils.startConnect();
                }
                Log.i(TAG, "AddressBookListen: sftp retry connected...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connectState = sftpUtils.getConnectState();
            }
        }
    }

    /**
     * 通讯录的回调
     */
    public interface AddressBookCallback {
        /**
         * 更新成功收到的通讯录
         *
         * @param content 通讯录内容
         */
        void onReceiveAddressBook(String content);
    }
}
