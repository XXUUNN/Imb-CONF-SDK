package com.imb.sdk.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.imb.sdk.manager.CallManager;
import com.imb.sdk.manager.ManagerService;

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
}
