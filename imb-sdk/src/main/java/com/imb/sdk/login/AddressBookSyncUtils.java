package com.imb.sdk.login;

import com.microsys.poc.jni.JniUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * @author - gongxun;
 * created on 2019/4/24-11:42;
 * description - 同步通讯录 会上传下载
 */
 class AddressBookSyncUtils {
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
        String tempFileName = userNum + "Tmp.txt";

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
        return parentPathOnServer;
    }

    /**
     * 获取本地所有的contact和group
     * 并转换为服务端需要的json
     * {"tel":"","did_list":"34/33"}
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

}
