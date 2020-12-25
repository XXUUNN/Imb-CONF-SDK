package com.imb.sdk.addressbook;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author - gongxun;
 * created on 2020/11/17-14:47;
 * description - http方式更新通讯录
 */
public class AddressBookSyncByHttp {
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .readTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS).build();

    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    /**
     * userNum = "8005";
     * url = "http://10.20.50.207:8085/syncAddress";
     *  @param userNum poc 号码
     * @param url     请求地址
     */
    public static Call getAddressBook(String userNum, String url, @NonNull final Callback callback) {
        String requestStr = getLocalAddressBook(userNum);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(requestStr, JSON));
        Call call = CLIENT.newCall(builder.build());
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (TextUtils.equals("Canceled", e.getMessage())) {
                    //被取消的 不回调
                    return;
                }
                callback.callback(false, e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200 && response.body() != null) {
                    String s = response.body().string();
                    callback.callback(true,s);
                }else{
                    String msg;
                    if (response.body() != null) {
                        msg = response.body().string();
                    }else{
                        msg = null;
                    }
                    callback.callback(false,msg);
                }
            }
        });
        return call;
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

    public interface Callback {
        void callback(boolean isOk, String msg);
    }

    public static String getSyncAddressBookUrl(String pocServerIp){
        return "http://" + pocServerIp + ":8085/syncAddress";
    }

}
