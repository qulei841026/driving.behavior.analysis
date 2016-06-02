package com.carsmart.driving;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import timber.log.Timber;

public class App extends Application {

    public String appFilePath = Environment.getExternalStorageDirectory() + "/driving.behavior.analysis";

    @Override
    public void onCreate() {
        super.onCreate();

        FileUtils.createFolder(new File(appFilePath));

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            FakeCrashLibrary.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    FakeCrashLibrary.logError(t);


                } else if (priority == Log.WARN) {
                    FakeCrashLibrary.logWarning(t);
                }
            }
        }
    }
}
