package com.imb.sdk.center;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by zlg on 2018/5/12.
 */

public class OKHttpUtil {
    /**
     * 8s
     */
    private static volatile OkHttpClient singleton;
    /**
     * 25s
     */
    private static volatile OkHttpClient singleton25;

    //非常有必要，要不此类还是可以被new
    private OKHttpUtil() {
    }

    public static OkHttpClient getInstance() {
        if (singleton == null) {
            synchronized (OKHttpUtil.class) {
                if (singleton == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            .connectTimeout(8, TimeUnit.SECONDS)
                            .readTimeout(8, TimeUnit.SECONDS);
                    builder.hostnameVerifier(SSLSocketClient.hostnameVerifier);
                    builder.sslSocketFactory(SSLSocketClient.sslSocketFactory, SSLSocketClient.imbTrust);
                    singleton = builder.build();
                }
            }
        }
        return singleton;
    }

    public static OkHttpClient getInstance25() {
        if (singleton25 == null) {
            synchronized (OKHttpUtil.class) {
                if (singleton25 == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            .connectTimeout(25, TimeUnit.SECONDS)
                            .readTimeout(25, TimeUnit.SECONDS);
                    builder.hostnameVerifier(SSLSocketClient.hostnameVerifier);
                    builder.sslSocketFactory(SSLSocketClient.sslSocketFactory, SSLSocketClient.imbTrust);
                    singleton25 = builder.build();
                }
            }
        }
        return singleton25;
    }
}
