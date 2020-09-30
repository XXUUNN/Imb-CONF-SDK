package com.imb.sdk.center;

import android.text.TextUtils;
import android.util.Base64;

import com.imb.sdk.data.entity.CenterLoginResult;
import com.imb.sdk.data.response.LoginAccountInfo;
import com.imb.sdk.data.response.LoginResponse;
import com.imb.sdk.data.response.LogoutResponse;
import com.microsys.poc.jni.utils.LogUtil;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author - gongxun;
 * created on 2020/9/27-14:24;
 * description - 智能中心的登录
 */
public class CenterLoginUtils {

    /**
     * 登录智能中心
     *
     * @param name     用户名
     * @param pwd      用户密码
     * @param callback 异步回调
     */
    public static void loginCenter(String name, String pwd, final LoginCenterCallback callback) {
        ImbHttpClient.login(name, pwd, new ImbHttpClient.Callback<LoginResponse>(LoginResponse.class) {
            @Override
            public void onSuccess(LoginResponse result) {
                LoginResponse.DataBean data = result.getData();
                String token = data.getToken();
                ImbHttpClient.setToken(token);
                //获取账号信息
                getAccountInfo(token, callback);
            }

            @Override
            public void onFailure(int code, String message) {
                ImbHttpClient.setToken(null);
                callback.onFailure(code, message);
            }
        });

    }

    private static void getAccountInfo(final String token, final LoginCenterCallback callback) {
        ImbHttpClient.getAccountInfo(new ImbHttpClient.Callback<LoginAccountInfo>(LoginAccountInfo.class) {
            @Override
            public void onFailure(int code, String message) {
                ImbHttpClient.setToken(null);
                callback.onFailure(code, message);
            }

            @Override
            public void onSuccess(LoginAccountInfo result) {
                LoginAccountInfo.DataBean data = result.getData();
                List<LoginAccountInfo.DataBean.InstanceDetailVOSBean> instanceDetailVOS = data.getInstanceDetailVOS();
                if (instanceDetailVOS == null || instanceDetailVOS.isEmpty()) {
                    ImbHttpClient.setToken(null);
                    callback.onFailure(-1, "尚未开通功能");
                    return;
                }
                LoginAccountInfo.DataBean.InstanceDetailVOSBean instance = null;
                int size = instanceDetailVOS.size();
                for (int i = 0; i < size; i++) {
                    LoginAccountInfo.DataBean.InstanceDetailVOSBean instanceDetailVOSBean = instanceDetailVOS.get(i);
                    if (instanceDetailVOSBean != null) {
                        String instType = instanceDetailVOSBean.getInstType();
                        if (ImbServerResponseTranslate.isCallInstanceType(instType)) {
                            //是需要的实例
                            instance = instanceDetailVOSBean;
                            break;
                        }
                    }
                }
                if (instance == null) {
                    ImbHttpClient.setToken(null);
                    callback.onFailure(-1, "尚未开通功能");
                    return;
                }
                CenterLoginResult centerLoginResult = new CenterLoginResult(token);
                int parseResult = parseLoginInfo(instance, data, centerLoginResult);
                if (parseResult == -1) {
                    ImbHttpClient.setToken(null);
                    callback.onFailure(-2, "登录异常");
                    return;
                } else if (parseResult == -2) {
                    ImbHttpClient.setToken(null);
                    callback.onFailure(-3, "账户已过期");
                    return;
                }
                callback.onSuccess(centerLoginResult);
            }
        }, true);
    }

    /**
     * 获取只能中心传过来的登录数据
     */
    private static int parseLoginInfo(LoginAccountInfo.DataBean.InstanceDetailVOSBean result, LoginAccountInfo.DataBean data, CenterLoginResult centerLoginResult) {
        if (TextUtils.isEmpty(result.getAccountName()) || TextUtils.isEmpty(result.getPassword())
                || TextUtils.isEmpty(result.getServerHost()) || 0 == result.getServerPort()) {
            return -1;
        }


        centerLoginResult.accountName = result.getAccountName();
        centerLoginResult.pocNum = result.getUsername();
        centerLoginResult.mobileNum = data.getMobile();

        centerLoginResult.headshot = data.getFileName();

        //已经base64加密了
        String pwd = null;
        try {
            byte[] decode = Base64.decode(result.getPassword(), Base64.DEFAULT);
            if (decode != null) {
                pwd = new String(decode);
            }
        } catch (IllegalArgumentException e) {
            LogUtil.getInstance().logWithMethod(e, "密码解析错误", "x");
            e.printStackTrace();
        }
        centerLoginResult.pocPassword = pwd;

        centerLoginResult.pocServerHost = result.getServerHost();
        centerLoginResult.pocServerPort = result.getServerPort();


        //账户类型和容量
        centerLoginResult.capacity = result.getCapacity();

        centerLoginResult.expirationTime = result.getExpirationTime();
        //账户类型
        if (result.getExpireType() != 0) {
            //过期
            return -2;
        }

        return 0;
    }

    /**
     * token登录
     *
     * @param token    缓存的token
     * @param callback 回调
     */
    public static void quickLogin(final String token, final LoginCenterCallback callback) {
        ImbHttpClient.getAccountInfo(new ImbHttpClient.Callback<LoginAccountInfo>(LoginAccountInfo.class) {
            @Override
            public void onFailure(int code, String message) {
                ImbHttpClient.setToken(null);
                callback.onFailure(code, message);
            }

            @Override
            public void onSuccess(LoginAccountInfo result) {
                LoginAccountInfo.DataBean data = result.getData();
                List<LoginAccountInfo.DataBean.InstanceDetailVOSBean> instanceDetailVOS = data.getInstanceDetailVOS();
                if (instanceDetailVOS == null || instanceDetailVOS.isEmpty()) {
                    ImbHttpClient.setToken(null);
                    callback.onFailure(-1, "尚未开通功能");
                    return;
                }
                //查找还有没有对应实例
                LoginAccountInfo.DataBean.InstanceDetailVOSBean instance = null;
                int size = instanceDetailVOS.size();
                for (int i = 0; i < size; i++) {
                    LoginAccountInfo.DataBean.InstanceDetailVOSBean instanceDetailVOSBean = instanceDetailVOS.get(0);
                    if (instanceDetailVOSBean != null) {
                        String instType = instanceDetailVOSBean.getInstType();
                        if (ImbServerResponseTranslate.isCallInstanceType(instType)) {
                            //是需要的实例
                            instance = instanceDetailVOSBean;
                            break;
                        }
                    }
                }

                if (instance == null) {
                    ImbHttpClient.setToken(null);
                    callback.onFailure(-1, "尚未开通功能");
                    return;
                }
                CenterLoginResult centerLoginResult = new CenterLoginResult(token);
                int parseResult = parseLoginInfo(instance, data, centerLoginResult);
                if (parseResult == -1) {
                    ImbHttpClient.setToken(null);
                    callback.onFailure(-2, "登录异常");
                    return;
                } else if (parseResult == -2) {
                    ImbHttpClient.setToken(null);
                    callback.onFailure(-3, "账户已过期");
                    return;
                }
                callback.onSuccess(centerLoginResult);
            }
        }, true);
    }

    /**
     * 退出智能中心
     *
     * @param callback null忽略回调结果
     */
    public static void logout(@Nullable ImbHttpClient.Callback<LogoutResponse> callback) {
        ImbHttpClient.logout(callback);
        ImbHttpClient.setToken(null);
    }

    public interface LoginCenterCallback {
        /**
         * 登录成功
         *
         * @param result 数据
         */
        void onSuccess(CenterLoginResult result);

        /**
         * 失败
         *
         * @param code    失败码
         * @param message 失败信息
         */
        void onFailure(int code, String message);
    }

}
