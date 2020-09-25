package com.imb.sdk.manager;

import android.content.Context;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.StringDef;

/**
 * @author - gongxun;
 * created on 2020/9/25-9:57;
 * description -
 */
public final class ManagerService {
    private static final Map<String, InterfaceManager> MANAGERS_MAP = new ConcurrentHashMap<>();

    public static final String LOGIN_SERVICE = "login";
    public static final String MSG_SERVICE = "msg";
    public static final String CALL_SERVICE = "call";


    private static void registerManager() {
        MANAGERS_MAP.put(LOGIN_SERVICE, new LoginManager());
        MANAGERS_MAP.put(MSG_SERVICE, new MsgManager());
        MANAGERS_MAP.put(CALL_SERVICE, new CallManager());
    }

    public static void init(Context context) {
        registerManager();
        for (InterfaceManager value : MANAGERS_MAP.values()) {
            value.init(context);
        }
    }

    @StringDef(value = {LOGIN_SERVICE, MSG_SERVICE, CALL_SERVICE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ServiceName {
    }

    public static InterfaceManager getManager(@ServiceName String managerName) {
        InterfaceManager interfaceManager = MANAGERS_MAP.get(managerName);
        return interfaceManager;
    }

}