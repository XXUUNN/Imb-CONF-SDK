package com.microsys.poc.jni.show;

import android.util.Log;

import java.util.concurrent.LinkedBlockingDeque;


public class MemPool {


    private LinkedBlockingDeque<byte[]> cacheList = new LinkedBlockingDeque<>();

    public int count = 0;

    /**
     * 取出空间对象
     */
    public byte[] allocMemory(int size) {
        synchronized (cacheList) {
            for (byte[] data : cacheList) {
                if (data != null && data.length == size) {
                    //找到了
                    cacheList.remove(data);
                    return data;
                }
            }
            //没找到 就生成一个
            if (count < 5) {
                //直接生成一个
                byte[] outData = new byte[size];
                Log.i("MemPool", "allocMemory: ");
                count++;
                return outData;
            } else {
                //取出一个
                if (cacheList.size() == 0) {
                    //都正在使用中 不能去除 生成失败
                    return null;
                } else {
                    //去除 第一个
                    cacheList.peekFirst();
                    //生成一个
                    byte[] outData = new byte[size];
                    Log.i("MemPool", "remove and allocMemory");
                    return outData;
                }
            }
//            //获取并移除队列的头
//            byte[] outData = cacheList.poll();
//            while (outData != null) {
//                if (outData != null && outData.length == size) {
//                    return outData;
//                }
//                outData = cacheList.poll();
//            }
//            //限制只生成3个以下空间
//            try {
//                if (count < 5) {
//                    outData = new byte[size];
//                    Log.i("uiiiiiiiiiiii", "allocMemory: ");
//                    count++;
//                } else {
//                    outData = null;
//                }
//            } catch (Exception e) {
//                outData = null;
//                e.printStackTrace();
//            }
//
//            return outData;
        }

    }

    /**
     * 塞回队列
     */
    public void releaseMemery(byte[] data) {
        if (!cacheList.offerLast(data)) {
            cacheList.offerLast(data);
        }
    }

    /**
     * 重置，线程池回归初始状态，不过不销毁
     */
    public void resetMemery() {
        synchronized (cacheList) {
            count = 0;
            cacheList.clear();
        }
    }

}
