package com.microsys.poc.jni.utils;

import android.util.Log;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;

public class LogUtil {
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;

    private static final int LEVEL = VERBOSE;// control the output info level

    public static LogUtil logUtil;

    public static LogUtil getInstance() {
        if (logUtil == null) {
            logUtil = new LogUtil();
        }
        return logUtil;
    }

    public void start(String path) {
        SystemLog.getInstance().init(path);
        SystemLog.getInstance().start();
    }

    public void destroy() {
        SystemLog.getInstance().destroy();
    }

    public void v(String tag, String msg) {
        if (LEVEL <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public void d(String tag, String msg) {
        if (LEVEL <= DEBUG) {
            Log.d(tag, msg);
        }
    }

    public void i(String tag, String msg) {
        if (LEVEL <= INFO) {
            Log.i(tag, msg);
        }
    }

    public void w(String tag, String msg) {
        if (LEVEL <= WARN) {
            Log.w(tag, msg);
        }
    }

    public void e(String tag, String msg) {
        if (LEVEL <= ERROR) {
            Log.e(tag, msg);
        }
    }

    public void logWithMethod(Exception e, String author) {
        StackTraceElement[] trace = e.getStackTrace();
        if (trace == null || trace.length == 0) {
            i("error", "log: get trace info failed");
        }
        String className = getSimpleClassName(trace[0].getClassName());
        String methodName = trace[0].getMethodName();
        int lineNumber = trace[0].getLineNumber();
//		i(className, logContentFilter + ": " + methodName + ":" + lineNumber);

        SystemLog.getInstance().out(className, methodName, lineNumber, author);
    }

    public void logWithMethod(Exception e, String msg, String author) {
        StackTraceElement[] trace = e.getStackTrace();
        if (trace == null || trace.length == 0) {
            i("error", "log: get trace info failed");
        }
        String className = getSimpleClassName(trace[0].getClassName());
        String methodName = trace[0].getMethodName();
        int lineNumber = trace[0].getLineNumber();
//		i(className, logContentFilter + ": " + methodName + ":" + lineNumber
//				+ ": " + msg);
        SystemLog.getInstance().out(className, methodName, lineNumber, msg, author);
    }

    public String getSimpleClassName(String fullClassName) {
        String split = ".";
        String class_name = "";
        StringTokenizer token = new StringTokenizer(fullClassName, split);
        while (token.hasMoreTokens()) {
            class_name = token.nextToken();
        }
        return class_name;
    }

    private static class SystemLog {

        private static SystemLog systemLog;

        private File file;

        private boolean isDestroyed = true;
        private LogThread logThread;

        public static SystemLog getInstance() {
            if (null == systemLog) {
                systemLog = new SystemLog();
            }
            return systemLog;
        }

        public void init(String path) {
            if (null == file) {
                file = new File(path);
            }
        }

        public void destroy() {
            if (!isDestroyed) {
                if (logThread != null) {
                    logThread.stopThread();
                    logThread = null;
                }
                isDestroyed = true;
            }
        }

        public void start() {
            if (isDestroyed) {
                if (logThread == null) {
                    logThread = new LogThread(file);
                }
                if (!logThread.isRunning) {
                    logThread.start();
                }
                isDestroyed = false;
            }
        }

        public void out(String className, String methodName, int lineNumber,
                        String author) {
            String content = TimeUtils.currTime() + "  " + className + "."
                    + methodName + "()[" + lineNumber + "]   " + "(by " + author
                    + ")";

            if (!isDestroyed) {
                logToFileAsync(content);
                System.out.println(content);
            } else {
                System.out.println(content);
            }
        }

        public void out(String className, String methodName, int lineNumber,
                        String msg, String author) {

            String content = TimeUtils.currTime() + "  " + className + "."
                    + methodName + "()[" + lineNumber + "]   " + msg + "(by "
                    + author + ")";
            if (!isDestroyed) {
                logToFileAsync(content);
                System.out.println(content);
            } else {
                System.out.println(content);
            }
        }

        private void logToFileAsync(String content) {
            logThread.log(content);
        }
    }

    private static class LogThread extends Thread {
        private static final String TAG = LogThread.class.getSimpleName();
        private volatile boolean isRunning = false;
        private File file;

        private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(100);

        public LogThread(File file) {
            super(TAG);
            this.file = file;
        }

        @Override
        public synchronized void start() {
            isRunning = true;
            super.start();
        }

        public void stopThread() {
            isRunning = false;
            //打断阻塞
            interrupt();
        }

        @Override
        public void run() {
            Log.i(TAG, "start");
            while (isRunning) {
                try {
                    final String content = queue.take();
                    logToFile(content);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isRunning = false;
            Log.i(TAG, "end");
        }

        public void log(String content) {
            if (isRunning) {
                queue.offer(content);
            }
        }

        private void logToFile(String content) {
            FileWriter fw = null;
            BufferedWriter out = null;
            try {
                if (null != file) {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    fw = new FileWriter(file, true);
                    out = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(file, true), "utf-8"));
                    out.newLine();
                    out.write(content, 0, content.length());
                    out.flush();
                    fw.close();
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    if (fw != null) {
                        fw.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
