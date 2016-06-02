package com.carsmart.driving;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LogProvider {

    String appPath;
    String currentFile;

    File originalFile;
    File denoiseFile;
    File filterFile;
    File velocityFile;

    FileOutputStream fos1;
    FileOutputStream fos2;
    FileOutputStream fos3;
    FileOutputStream fos4;

    public LogProvider(Context context) {
        appPath = ((App) context.getApplicationContext()).appFilePath;
    }


    public void init() {
        String timeFile = DateUtils.formatDate(System.currentTimeMillis(), DateUtils.DATE_FORMAT_2);
        currentFile = appPath + "/" + timeFile;
        FileUtils.createFolder(new File(currentFile));

        originalFile = new File(currentFile + "/" + "original.log");
        FileUtils.createFile(originalFile);
        try {
            fos1 = new FileOutputStream(originalFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        denoiseFile = new File(currentFile + "/" + "denoise.log");
        FileUtils.createFile(denoiseFile);
        try {
            fos2 = new FileOutputStream(denoiseFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        filterFile = new File(currentFile + "/" + "filter.log");
        FileUtils.createFile(filterFile);
        try {
            fos3 = new FileOutputStream(filterFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        velocityFile = new File(currentFile + "/" + "velocity.log");
        FileUtils.createFile(velocityFile);
        try {
            fos4 = new FileOutputStream(velocityFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        if (fos1 != null) {
            try {
                fos1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (fos2 != null) {
            try {
                fos2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (fos3 != null) {
            try {
                fos3.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (fos4 != null) {
            try {
                fos4.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void logOriginal(float x, float y, float z) {
        String str = DateUtils.formatDate(System.currentTimeMillis(), DateUtils.DATE_FORMAT)
                + " original : " + "x=" + x + ",y=" + y + ",z=" + z + "\n";
        writeLog(fos1, str);

    }

    public void logDenoise(float x, float y, float z) {
        String str = DateUtils.formatDate(System.currentTimeMillis(), DateUtils.DATE_FORMAT)
                + " denoise : " + "x=" + x + ",y=" + y + ",z=" + z + "\n";
        writeLog(fos2, str);
    }

    public void logFilter(float a) {
        String str = DateUtils.formatDate(System.currentTimeMillis(), DateUtils.DATE_FORMAT)
                + " filter : " + "acceleration=" + a + "\n";
        writeLog(fos3, str);
    }


    public void logVelocity(float v) {
        String str = DateUtils.formatDate(System.currentTimeMillis(), DateUtils.DATE_FORMAT)
                + " velocity : " + "velocity=" + v + "\n";
        writeLog(fos4, str);
    }

    private void writeLog(FileOutputStream fos, String content) {
        if (fos != null) {
            try {
                fos.write(content.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
