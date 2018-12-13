package com.jaychan.library.app;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author JayChan
 * @date 2018/12/13  15:06
 */
public class MyApplication extends Application {

    public static Context mContext;
    public static String dbFilePath;


    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        dbFilePath = getFilesDir().getPath() + "/city.db";


        copyCityDB("city.db");
    }

    private void copyCityDB(final String dbName) {
        new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(getFilesDir(), dbName);
                    if (file.exists() && file.length() > 0) {
                        return;
                    }
                    InputStream is = getAssets().open(dbName);
                    FileOutputStream fos = openFileOutput(dbName, MODE_PRIVATE);
                    byte[] buffer = new byte[1024];
                    int len = 0;

                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }

                    is.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.run();
    }
}
