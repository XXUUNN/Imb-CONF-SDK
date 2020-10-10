package com.imb.sdk.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.imb.sdk.data.Constant;
import com.imb.sdk.manager.CallManager;
import com.imb.sdk.manager.ManagerService;
import com.microsys.poc.jni.show.MultiVideoShowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author - gongxun;
 * created on 2020/9/24-9:38;
 * description - 初始化
 */
public class PocInitializer extends ContentProvider {
    @Override
    public boolean onCreate() {
        final Context context = getContext().getApplicationContext();
        ManagerService.init(context);
        CallManager callManager = (CallManager) ManagerService.getManager(ManagerService.CALL_SERVICE);
        callManager.init(context);

        String remoteViewMaxCountStr = getMetaDataValue(context, Constant.META_REMOTE_VIEW_MAX_COUNT);
        int count = 0;
        if (remoteViewMaxCountStr != null) {
            try {
                count = Integer.parseInt(remoteViewMaxCountStr);
            } catch (NumberFormatException e) {
                Log.i("PoC_init", "meta 'imb_remote_view_max_count' config error");
                e.printStackTrace();
            }
        } else {
            Log.i("PoC_init", "meta 'imb_remote_view_max_count' not config. use default " + Constant.REMOTE_VIEW_MAX_COUNT);
        }
        if (count == 0) {
            count = Constant.REMOTE_VIEW_MAX_COUNT;
        }
        Log.i("PoC_init", "meta 'imb_remote_view_max_count' must be the same with server's configuration");
        MultiVideoShowManager.init(count);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    private String getMetaDataValue(Context context, String metaName) {
        String value = null;
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                Object object = applicationInfo.metaData.get(metaName);
                if (object != null) {
                    value = object.toString();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }
}
