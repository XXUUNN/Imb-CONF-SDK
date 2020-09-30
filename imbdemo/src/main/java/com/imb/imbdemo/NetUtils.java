package com.imb.imbdemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;

/**
 * @author - gongxun;
 * created on 2019/4/24-10:23;
 * description - 网络相关
 */
public class NetUtils {
    public static String getLocalIp(Context context) {
        String ip;
        ConnectivityManager conMann = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ConnectivityManager aNull = Objects.requireNonNull(conMann);
        NetworkInfo mobileNetworkInfo = aNull.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetworkInfo = aNull.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if ( wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = intToIp(ipAddress);
        } else if (mobileNetworkInfo != null && mobileNetworkInfo.isConnected()) {
            ip = getLocalIpV4Address();
        } else {
            ip = "192.168.1.12";
        }
        return ip;
    }

    // 获得本地ip
    public static String getLocalIPAddress(Context context) {
        String ip = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = intToIp(ipAddress);
        } else {
            try {// 获取GPRS条件下的ip
                for (Enumeration<NetworkInterface> mEnumeration = NetworkInterface
                        .getNetworkInterfaces(); mEnumeration.hasMoreElements();) {
                    NetworkInterface intf = mEnumeration.nextElement();
                    for (Enumeration<InetAddress> enumIPAddr = intf
                            .getInetAddresses(); enumIPAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIPAddr.nextElement();
                        // 如果不是回环地址
                        if (!inetAddress.isLoopbackAddress() &&  inetAddress instanceof Inet4Address) {
                            // 直接返回本地IP地址
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
        }
        return ip;

    }


    private static String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    public static String getLocalIpV4Address() {
        try {
            String ipv4;
            ArrayList<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : nilist) {
                ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address : ialist) {
                    if (!address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
                        ipv4 = address.getHostAddress();
                        return ipv4;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("localip", ex.toString());
        }
        return null;
    }


    public static boolean isNetConnected(Context context) {

        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }


}
