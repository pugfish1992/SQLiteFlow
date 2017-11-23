package com.pugfish1992.sample;

import android.app.Application;

import com.pugfish1992.sample.data.DbOpenHelper;
import com.pugfish1992.sqliteflow.core.SQLiteFlow;

/**
 * Created by daichi on 11/22/17.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SQLiteFlow.init(new DbOpenHelper(this));
    }
}
