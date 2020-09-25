package com.microsys.poc.jni.show;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;


public class MemPool {


    private LinkedBlockingQueue<byte[]> cacheList = new LinkedBlockingQueue<byte[]>();

    public int count = 0;

    /**
     * 取出空间对象
     */
    public byte[] allocMemory(int size) {
        synchronized (cacheList) {
            //获取并移除队列的头
            byte[] outData = cacheList.poll();
            while (outData != null) {
                if (outData != null && outData.length == size) {
                    return outData;
                }
                outData = cacheList.poll();
            }
            //限制只生成3个以下空间
            try {
                if (count < 5) {
                    outData = new byte[size];
					Log.i("uiiiiiiiiiiii", "allocMemory: ");
                    count++;
                } else {
                    outData = null;
                }
            } catch (Exception e) {
                outData = null;
                e.printStackTrace();
            }

            return outData;
        }

    }

    /**
     * 塞回队列
     *
     * @param data
     */
    public void releaseMemery(byte[] data) {
        try {
            synchronized (cacheList) {
                cacheList.put(data);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
