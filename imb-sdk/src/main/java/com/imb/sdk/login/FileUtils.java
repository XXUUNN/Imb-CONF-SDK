package com.imb.sdk.login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author - gongxun;
 * created on 2019/4/24-14:07;
 * description - 文件操作
 */
public class FileUtils {

    public static String writeToFile(String fileName, String content, String parentPath) {
        File file = new File(parentPath);
        if (!file.exists()) {
            file.mkdir();
        }
        File txtFile = new File(parentPath, fileName);
        if (!txtFile.exists()) {
            try {
                txtFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(txtFile);
            fileWriter.write(content);
            fileWriter.flush();
            return txtFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
                fileWriter = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String readFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader reader = new BufferedReader(fileReader);
                StringBuilder stringBuilder = new StringBuilder();
                String tmp;
                while ((tmp = reader.readLine()) != null) {
                    stringBuilder.append(tmp);
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据日志目录文件夹的大小来删除日志文件
     *
     * @param dirPath 要删除的目录 
     * @param maxBytes 超过这个值就删除最旧的日志直到目录大小到指定的最大的值
     */
    public static void deleteFileIfSizeMax(File dirPath,int maxBytes) {
        File[] files = dirPath.listFiles();
        if (files == null) {
            return;
        }
        if (files.length == 0) {
            return;
        }
        long allSize = 0;
        ArrayList<File> list = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                list.add(file);
                allSize += file.length();
            }
        }
        int filesCount = list.size();
        if (filesCount == 0) {
            return;
        }

        if (allSize > maxBytes) {
            Collections.sort(list, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    //不能直接return int(left-right); long型结果转的Int 不能当比较值
                    long left = o1.lastModified();
                    long right = o2.lastModified();
                    if (left > right) {
                        return 1;
                    } else if (left == right) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
            while (allSize > maxBytes) {
                if (filesCount == 0) {
                    break;
                }
                File first = list.remove(0);
                allSize = allSize - first.length();
                filesCount--;
                first.delete();
            }
        }
    }
}
