package com.imb.imbdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

/**
 * @author Administrator
 */
public final class PermissionUtils {

    public static final String[] CALENDAR;
    public static final String[] CAMERA;
    public static final String[] CONTACTS;
    public static final String[] LOCATION;
    public static final String[] MICROPHONE;
    public static final String[] PHONE;
    public static final String[] SENSORS;
    public static final String[] SMS;
    public static final String[] STORAGE;

    public static final List<String[]> groupList = new ArrayList<>();

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            CALENDAR = new String[]{};
            CAMERA = new String[]{};
            CONTACTS = new String[]{};
            LOCATION = new String[]{};
            MICROPHONE = new String[]{};
            PHONE = new String[]{};
            SENSORS = new String[]{};
            SMS = new String[]{};
            STORAGE = new String[]{};
        } else {
            CALENDAR = new String[]{
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR};

            CAMERA = new String[]{
                    Manifest.permission.CAMERA};

            CONTACTS = new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.GET_ACCOUNTS};

            LOCATION = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};

            MICROPHONE = new String[]{
                    Manifest.permission.RECORD_AUDIO};

//            PHONE = new String[]{
//                    Manifest.permission.READ_PHONE_STATE,
//                    Manifest.permission.CALL_PHONE,
//                    Manifest.permission.READ_CALL_LOG,
//                    Manifest.permission.WRITE_CALL_LOG,
//                    Manifest.permission.USE_SIP,
//                    Manifest.permission.PROCESS_OUTGOING_CALLS};

            PHONE = new String[]{
                    Manifest.permission.READ_PHONE_STATE};

            SENSORS = new String[]{
                    Manifest.permission.BODY_SENSORS};

            SMS = new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_WAP_PUSH,
                    Manifest.permission.RECEIVE_MMS};

            STORAGE = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};

            groupList.add(CALENDAR);
            groupList.add(CAMERA);
            groupList.add(CONTACTS);
            groupList.add(LOCATION);
            groupList.add(MICROPHONE);
            groupList.add(PHONE);
            groupList.add(SENSORS);
            groupList.add(SMS);
            groupList.add(STORAGE);
        }


    }

    private static List<PermissionUtils> permissionUtilsList = new ArrayList<>();
    private static SparseIntArray tagRelation = new SparseIntArray();

    /**
     * 拒绝的权限
     */
    private List<String> deniedPermissionList = new ArrayList<>();

    private List<String> deniedForeverPermissionList = new ArrayList<>();

    /**
     * 权限申请回调
     */
    private PermissionRequestCallback callback;

    private Activity activity;

    private int tag;

    private onTagListener onTagListener;

    private PermissionUtils() {
    }

    private static void addPermissionUtil(PermissionUtils permissionUtils) {
        //可能如果相同tag 会导致  PermissionUtils.permissionUtilsList 有重复数据
        //todo、 未作 可以替换相同tag的permissionUtils

        int index = tagRelation.get(permissionUtils.tag, -1);
        if (index == -1) {
            int newIndex = PermissionUtils.permissionUtilsList.size();
            PermissionUtils.permissionUtilsList.add(newIndex, permissionUtils);
            tagRelation.put(permissionUtils.tag, newIndex);
        } else {
            //tag自动加1000
            if (permissionUtils.onTagListener != null) {
                permissionUtils.tag += new Random(permissionUtils.tag).nextInt(1000) + 1;
                permissionUtils.onTagListener.onTagNonUnique(permissionUtils.tag);
            } else {
                throw new IllegalArgumentException("tag must be the unique in your code!");
            }
            tagRelation.put(permissionUtils.tag, index);
            if (PermissionUtils.permissionUtilsList.size() > index) {
                PermissionUtils.permissionUtilsList.set(index, permissionUtils);
            }
        }
    }

    private static void removePermissionUtils(PermissionUtils permissionUtils) {
        if (permissionUtils != null) {
            int index = tagRelation.get(permissionUtils.tag, -1);
            if (index != -1) {
                if (tagRelation.size() > index) {
                    tagRelation.removeAt(index);
                }
                if (permissionUtilsList.size() > index) {
                    permissionUtilsList.remove(index);
                }

            }
            permissionUtils.activity = null;
            permissionUtils = null;
        }
    }

    private static void removeAll() {
        tagRelation.clear();
        for (PermissionUtils permissionUtils : permissionUtilsList) {
            removePermissionUtils(permissionUtils);
        }
        permissionUtilsList.clear();
    }

    /**
     * 如果在android O 以上 根据每个权限 查找他的所属 权限组
     *
     * @param permission 权限
     * @return 权限组  如果null 表示 不存在
     */
    private static String[] getGroupByPermission(String permission) {
        for (String[] group : groupList) {
            boolean isIn = false;
            for (String permissionInGroup : group) {
                if (TextUtils.equals(permissionInGroup, permission)) {
                    isIn = true;
                    break;
                }
            }
            if (isIn) {
                return group;
            }
        }
        return null;
    }

    public interface onTagListener {
        void onTagNonUnique(int newTag);
    }


    synchronized public static PermissionUtils create(Activity activity, int tag, onTagListener onTagListener) {
        PermissionUtils permissionUtils = new PermissionUtils();
        permissionUtils.activity = activity;
        permissionUtils.tag = tag;
        permissionUtils.onTagListener = onTagListener;
        PermissionUtils.addPermissionUtil(permissionUtils);
        return permissionUtils;
    }

    synchronized public static PermissionUtils create(Activity activity, int tag) {
        PermissionUtils permissionUtils = new PermissionUtils();
        permissionUtils.activity = activity;
        permissionUtils.tag = tag;
        PermissionUtils.addPermissionUtil(permissionUtils);
        return permissionUtils;
    }

    /**
     * 检测权限是否允许
     *
     * @param permissions 权限 ;
     */
    public PermissionUtils checkPermission(Context context, @NonNull PermissionRequestCallback callback, String... permissions) {
        this.callback = callback;
        //未授权的权限加在一个集合里
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isDeniedForever = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                ArrayList<String[]> permissionGroupList = new ArrayList<>();

                for (String permission : permissions) {
                    String[] groupByPermission = getGroupByPermission(permission);
                    if (groupByPermission != null && !permissionGroupList.contains(groupByPermission)) {
                        permissionGroupList.add(groupByPermission);
                    }
                }

                for (String[] groupByPermission : permissionGroupList) {
                    for (String permission : groupByPermission) {
                        if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                            deniedPermissionList.add(permission);

                            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                                isDeniedForever = true;
                                deniedForeverPermissionList.add(permission);
                            }
                        }
                    }
                }

            } else {
                for (String permission : permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                        deniedPermissionList.add(permission);

                        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                            isDeniedForever = true;
                            deniedForeverPermissionList.add(permission);
                        }
                    }
                }
            }

//            if (isDeniedForever) {
//                callback.deniedForever(deniedForeverPermissionList);
//            }
        }
//        else {
//            callback.granted();
//        }
        return this;
    }

    /**
     * 开始申请权限
     */
    public PermissionUtils request() {
        if (deniedPermissionList.size() == 0) {
            if (deniedForeverPermissionList.size() == 0) {
                callback.granted(false);
            }
            removePermissionUtils(this);
        } else {
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                //去重复
                deniedPermissionList.removeAll(deniedForeverPermissionList);
                deniedPermissionList.addAll(deniedForeverPermissionList);
                ActivityCompat.requestPermissions(activity, deniedPermissionList.toArray(new String[]{}), tag);
            } else {
                removePermissionUtils(this);
            }
        }
        return this;
    }

    /**
     * @param tag 当前activity PermissionUtils.create时传入的Tag
     */
    public static void onPermissionCallback(int tag, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int index = tagRelation.get(tag, -1);
        if (index == -1) {
            removeAll();
            throw new IllegalArgumentException("this tag must be the same with the tag what be put in current activity's“PermissionUtils.create()”");
        }
        int size = permissionUtilsList.size();
        if (size > index) {
            PermissionUtils permissionUtils = permissionUtilsList.get(index);
            if (permissionUtils != null && permissionUtils.callback != null) {
                if (requestCode == permissionUtils.tag) {
                    if (grantResults.length == 0) {
                        return;
                    }
                    //更新被拒绝的权限列表
                    permissionUtils.deniedPermissionList.clear();
                    boolean isAllGranted = true;

                    boolean isDeniedForever = false;

                    for (int i = 0, len = grantResults.length; i < len; i++) {
                        int result = grantResults[i];
                        if (result == PackageManager.PERMISSION_DENIED) {
                            isAllGranted = false;
                            permissionUtils.deniedPermissionList.add(permissions[i]);

                            if (!ActivityCompat.shouldShowRequestPermissionRationale(permissionUtils.activity, permissions[i])) {
                                isDeniedForever = true;
                                permissionUtils.deniedForeverPermissionList.add(permissions[i]);
                            }

                        }
                    }
                    if (isAllGranted) {
                        permissionUtils.callback.granted(true);
                    } else {

                        if (isDeniedForever) {
                            permissionUtils.callback.deniedForever(permissionUtils.deniedForeverPermissionList);
                        } else {
                            permissionUtils.callback.denied(permissionUtils.deniedPermissionList);
                        }
                    }

                }
            }
            removePermissionUtils(permissionUtils);
        }
    }

    /**
     * 权限申请的回调
     */
    public interface PermissionRequestCallback {
        /**
         * 权限同意
         * @param isCalledInActivityResult 是否是在activity的
         * {@link Activity#onActivityResult(int, int, Intent)}中回调。
         *  主要解决-在如果在这个方法中使用了FragmentManager，并且此方法是在
         *  onActivityResult中回调执行可能导致commit报错。
         *  true标识是在onActivityResult中回调执行
         */
        void granted(boolean isCalledInActivityResult);

        /**
         * 权限拒绝
         *
         * @param deniedPermissionList 被拒绝的权限list
         */
        void denied(List<String> deniedPermissionList);

        /**
         * 存在永久拒绝的权限，展示相关说明UI
         *
         * @param deniedForeverPermissionList 永久拒绝的权限list
         */
        void deniedForever(List<String> deniedForeverPermissionList);
    }

}
