package com.carsmart.driving;

import java.io.File;
import java.io.IOException;

/**
 * 文件函数工具类
 *
 * @author qulei
 */
public class FileUtils {

    /**
     * 创建目录(不存在时创建)
     */
    public static boolean createFolder(File folder) {
        return folder != null && (folder.exists() || folder.mkdirs());
    }

    /**
     * 创建文件(不存在时创建)
     */
    public static boolean createFile(File file) {
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
