package com.pugfish1992.sqliteflow.core;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

/**
 * Created by daichi on 11/21/17.
 */

public class SQLiteFlow {

    /* Intentional private visibility */
    private SQLiteFlow() {}

    public static void init(@NonNull SQLiteOpenHelper openHelper) {
        Storage.init(openHelper);
    }
}
