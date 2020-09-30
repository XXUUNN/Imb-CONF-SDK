package com.imb.imbdemo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.imb.sdk.manager.CallManager;
import com.imb.sdk.manager.ManagerService;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class FunctionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);

        setTitle("功能");

        PermissionUtils.create(this,2).checkPermission(this, new PermissionUtils.PermissionRequestCallback() {
            @Override
            public void granted(boolean isCalledInActivityResult) {

            }

            @Override
            public void denied(List<String> deniedPermissionList) {

            }

            @Override
            public void deniedForever(List<String> deniedForeverPermissionList) {

            }
        }, Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA).request();

        CallManager manager = (CallManager) ManagerService.getManager(ManagerService.CALL_SERVICE);
        manager.subscribePoc();

    }

    public void onCallClick(View view) {
        startActivity(new Intent(this,DialActivity.class));
    }

    public void onMsgClick(View view) {
        startActivity(new Intent(this, MessageActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onPermissionCallback(2,requestCode,permissions,grantResults);
    }
}
