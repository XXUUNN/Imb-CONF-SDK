package com.imb.sdk.center;


import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.imb.sdk.data.request.RecordCallRequest;
import com.imb.sdk.data.response.BaseResponse;
import com.imb.sdk.data.response.ChangePwdResponse;
import com.imb.sdk.data.response.CheckVersionResponse;
import com.imb.sdk.data.response.DeleteMeetingResponse;
import com.imb.sdk.data.response.LoginAccountInfo;
import com.imb.sdk.data.response.LoginResponse;
import com.imb.sdk.data.response.LogoutResponse;
import com.imb.sdk.data.response.MeetingRemainingTimeResponse;
import com.imb.sdk.data.response.MeetingResponse;
import com.imb.sdk.data.response.RecordCallResponse;
import com.imb.sdk.data.response.ResetPwdResponse;
import com.imb.sdk.data.response.SendAuthCodeResponse;
import com.imb.sdk.data.response.StartMeetingResponse;
import com.imb.sdk.data.response.UpdateHeadshotResponse;
import com.imb.sdk.data.response.UserMobileResponse;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author microsys
 * @date 2018/3/29
 */

public class ImbHttpClient {


    /**
     * 服务端返回成功code为200
     */
    private static final int RESPONSE_CODE_BUSINESS_OK = 200;

    /**
     * token失效
     */
    public static final int RESPONSE_CODE_BUSINESS_TOKEN_ERROR = -10086;

    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private static String token;


    public static void setToken(String token) {
        ImbHttpClient.token = token;
    }

    public static String getToken() {
        return token;
    }

    /**
     * 加密方法
     */
    public static String signature(String httpType, String date, String pwd) throws Exception {

        String StringToSign =
                httpType + "\n" + date + "\n" + "/user/login";
        byte[] hmac = EncryptionUtil.HmacSHA1Encrypt(EncryptionUtil.md5(pwd), StringToSign);
        StringBuilder builder = new StringBuilder(hmac.length);
        for (byte b : hmac) {
            int i = b & 0xff;
            if (i <= 0xf) {
                builder.append("0");
            }
            builder.append(Integer.toHexString(i));
        }
        String macStr = builder.toString();
        String Signature = EncryptionUtil.stringToBase64(macStr);
        return Signature;
    }

    /**
     * 登入
     */
    public static void login(String account, String pwd, Callback<LoginResponse> callback) {

        String url = UrlManager.getLoginPath();

        String date = TimeUtil.formatMillisToGMT();

        url = url + "/" + account;
        //密码加密
        String sign = null;
        try {
            sign = signature("POST", date, pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpClient okHttpClient = OKHttpUtil.getInstance();

        Request request = createRequestBuilder(url, false)
                .post(RequestBody.create("", JSON))
                .header("Date", date)
                .header("Authorization", sign)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 获取本系统内所账号信息
     */
    public static Call getAccountInfo(Callback<LoginAccountInfo> callback, boolean responseNow) {
        String url = UrlManager.getUserInfoPath();


        OkHttpClient okHttpClient;
        Request request;
        if (responseNow) {
            okHttpClient = OKHttpUtil.getInstance();
            // 02.请求体
            request = createRequestBuilder(url, true)
                    .get()
                    .header("Poll-Connection", "close")
                    .build();
        } else {
            okHttpClient = OKHttpUtil.getInstance25();
            // 02.请求体
            request = createRequestBuilder(url, true)
                    .get()
                    .header("Poll-Connection", "keep-alive")
                    .build();
        }

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
        return call;
    }

    /**
     * 登出
     */
    public static void logout(@Nullable Callback<LogoutResponse> callback) {
        String url = UrlManager.getLogoutPath();
        OkHttpClient okHttpClient = OKHttpUtil.getInstance();

        // 02.请求体
        Request request = createRequestBuilder(url, true)
                .post(RequestBody.create("", JSON))
                .header("Poll-Connection", "close")
                .build();
        if (callback == null) {
            callback = new Callback<LogoutResponse>(LogoutResponse.class) {
                @Override
                public void onFailure(int code, String message) {

                }

                @Override
                public void onSuccess(LogoutResponse result) {

                }
            };
        }
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 检测版本
     */
    public static void checkVersion(String version,Callback<CheckVersionResponse> callback) {
        String appName = "meeting";
        String url = UrlManager.getCheckVersionPath() + "?appName=" + appName + "&type=android&version=" + version;
        OkHttpClient okHttpClient = OKHttpUtil.getInstance();
        // 02.请求体
        Request request = createRequestBuilder(url, false)
                .get()
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }


    /**
     * 修改密码
     */
    public static void updatePassword(String newPwd, String mobileNum, String authCode, Callback<ChangePwdResponse> callback) {
        String url = UrlManager.getUpdatePwdPath();

        OkHttpClient okHttpClient = OKHttpUtil.getInstance();
        MultipartBody body = new MultipartBody.Builder()
                .addFormDataPart("password", newPwd)
                .addFormDataPart("mobile", mobileNum)
                .addFormDataPart("identityCode", authCode)
                .setType(MultipartBody.FORM)
                .build();

        // 02.请求体
        Request request = createRequestBuilder(url, true)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 发送验证码
     */
    public static void sendAuthCode(String mobileNum, Callback<SendAuthCodeResponse> callback) {
        String url = UrlManager.getSendAuthCode();

        OkHttpClient okHttpClient = OKHttpUtil.getInstance();
        MultipartBody body = new MultipartBody.Builder()
                .addFormDataPart("mobile", mobileNum)
                .addFormDataPart("type", "2")
                .setType(MultipartBody.FORM)
                .build();

        // 02.请求体
        Request request = createRequestBuilder(url, false)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 重设密码
     */
    public static void resetPwd(String userName, String pwd, String mobileNum, String authCode,
                                Callback<ResetPwdResponse> callback) {
        String url = UrlManager.getResetPwd();

        OkHttpClient okHttpClient = OKHttpUtil.getInstance();
        MultipartBody body = new MultipartBody.Builder()
                .addFormDataPart("userName", userName)
                .addFormDataPart("password", pwd)
                .addFormDataPart("mobile", mobileNum)
                .addFormDataPart("identityCode", authCode)
                .setType(MultipartBody.FORM).build();

        // 02.请求体
        Request request = createRequestBuilder(url, false)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 通话记录
     */
    public static void recordCallToServer(RecordCallRequest requestEntity) {
        String url = UrlManager.getRecordCall();

        OkHttpClient okHttpClient = OKHttpUtil.getInstance();

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .addFormDataPart("callerId", String.valueOf(requestEntity.callerId))
                .addFormDataPart("callerName", requestEntity.callerName)
                .addFormDataPart("callerTel", requestEntity.callerTel)
                .addFormDataPart("calledId", requestEntity.calledId)
                .addFormDataPart("calledName", requestEntity.calledName)
                .addFormDataPart("calledTel", requestEntity.calledTel)
                .addFormDataPart("length", requestEntity.length + "")
                .addFormDataPart("callType", requestEntity.callType + "");

        if (!TextUtils.isEmpty(requestEntity.callTime)) {
            builder.addFormDataPart("callTime", requestEntity.callTime);
        }
        if (!TextUtils.isEmpty(requestEntity.answerTime)) {
            builder.addFormDataPart("answerTime", requestEntity.answerTime);
        }
        if (!TextUtils.isEmpty(requestEntity.endTime)) {
            builder.addFormDataPart("endTime", requestEntity.endTime);
        }

        MultipartBody body = builder.setType(MultipartBody.FORM).build();

        Request request = createRequestBuilder(url, true)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback<RecordCallResponse>(RecordCallResponse.class) {
            @Override
            public void onFailure(int code, String message) {
            }

            @Override
            public void onSuccess(RecordCallResponse result) {
            }
        });
    }

    /**
     * 上传头像
     *
     * @param imgFile 文件
     */
    public static void uploadHeadshot(File imgFile, Callback<UpdateHeadshotResponse> callback) {
        if (imgFile == null) {
            return;
        }
        OkHttpClient okHttpClient = OKHttpUtil.getInstance();
        String url = UrlManager.getUploadHeadshotPath();
        RequestBody requestBody = FormBody.create(imgFile, MediaType.parse("image/*"));
        MultipartBody build = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", imgFile.getName(), requestBody).build();
        Request request = createRequestBuilder(url, true)
                .post(build)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void getUserMobileByUserName(String userName, Callback<UserMobileResponse> callback) {
        String url = UrlManager.getGetMobile() + "/" + userName;

        OkHttpClient okHttpClient = OKHttpUtil.getInstance();
        // 02.请求体
        Request request = createRequestBuilder(url, false)
                .get()
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 获取会议剩余时间
     *
     * @param meetingNum 会议号码（组号码）
     */
    public static Call getMeetingRemainingTime(String meetingNum, Callback<MeetingRemainingTimeResponse> callback) {
        String url = UrlManager.getGetMeetingRemainingTime() + "/" + meetingNum;
        OkHttpClient okHttpClient = OKHttpUtil.getInstance();
        // 02.请求体
        Request request = createRequestBuilder(url, true)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
        return call;
    }

    /**
     * 获取会议
     */
    public static void getMeeting(Callback<MeetingResponse> callback) {
        String url = UrlManager.getGetMeetings();
        OkHttpClient okHttpClient = OKHttpUtil.getInstance();
        // 02.请求体
        Request request = createRequestBuilder(url, true)
                .get()
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void updateMeetingAlreadyRead() {
        String url = UrlManager.getDeleteMeetingNotify();
        OkHttpClient okHttpClient = OKHttpUtil.getInstance();

        // 02.请求体
        Request request = createRequestBuilder(url, true)
                .post(RequestBody.create("", JSON))
                .build();
        Callback<DeleteMeetingResponse> callback = new Callback<DeleteMeetingResponse>(DeleteMeetingResponse.class) {
            @Override
            public void onFailure(int code, String message) {

            }

            @Override
            public void onSuccess(DeleteMeetingResponse result) {

            }
        };
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void startMeetingNow(String meetingNum, Callback<StartMeetingResponse> callback) {
        String url = UrlManager.getStartMeetingNow();
        OkHttpClient okHttpClient = OKHttpUtil.getInstance();
        MultipartBody builder = new MultipartBody.Builder()
                //是组的号码
                .addFormDataPart("groupId", meetingNum)
                .setType(MultipartBody.FORM).build();

        Request request = createRequestBuilder(url, true)
                .post(builder)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void startMeetingNowById(String meetingId, Callback<StartMeetingResponse> callback) {
        String url = UrlManager.getStartMeetingNow();
        OkHttpClient okHttpClient = OKHttpUtil.getInstance();
        MultipartBody builder = new MultipartBody.Builder()
                //是组的号码
                .addFormDataPart("meetingId", meetingId)
                .setType(MultipartBody.FORM).build();

        Request request = createRequestBuilder(url, true)
                .post(builder)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    private static Request.Builder createRequestBuilder(String url, boolean withToken) {

        Request.Builder builder = new Request.Builder()
                .url(url)
                .header("Date", TimeUtil.formatMillisToGMT())
                .header("Content-Type", "application/json");
        if (withToken) {
            if (token != null) {
                builder.header("token", token);
            }
        }
        return builder;
    }

    /**
     * 取消所有普通的请求（除了25s超时的轮询的接口）
     */
    public static void cancelAllCommonRequest(){
        OkHttpClient instance = OKHttpUtil.getInstance();
        instance.dispatcher().cancelAll();
    }

    public abstract static class Callback<T extends BaseResponse> implements okhttp3.Callback {

        private Class<T> clazz;

        private Handler handler;

        public Callback(Class<T> responseClazz) {
            this.clazz = responseClazz;
            handler = new Handler();
        }

        public Callback(Class<T> responseClazz, Looper looper) {
            this.clazz = responseClazz;
            handler = new Handler(looper);
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull final IOException e) {
            if (TextUtils.equals("Canceled", e.getMessage())) {
                //被取消的 不回调 避免遇到toast之类崩溃
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onFailure(-1, e.getMessage());
                }
            });

        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) {
            try {
                String s = response.body().string();
                final T t = com.alibaba.fastjson.JSON.parseObject(s, clazz);
                if (t.code == RESPONSE_CODE_BUSINESS_OK) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Callback.this.onSuccess(t);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Callback.this.onFailure(t.code, t.msg);
                        }
                    });
                }
            } catch (final Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Callback.this.onFailure(-1, e.getMessage());
                    }
                });
            }
        }

        /**
         * 失败
         *
         * @param message 失败原因
         * @param code    失败码
         */
        public abstract void onFailure(int code, String message);

        /**
         * 业务成功
         *
         * @param result 返回结果
         */
        public abstract void onSuccess(T result);
    }
}
