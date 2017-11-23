package com.pugfish1992.sample.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.pugfish1992.sqliteflow.core.AbsSQLiteOpenHelper;
import com.pugfish1992.sqliteflow.core.DinosaurTable;

/**
 * Created by daichi on 11/22/17.
 */

public class DbOpenHelper extends AbsSQLiteOpenHelper {

    private static final String DATABASE_NAME = "SQLiteFlowSample.db";
    private static final int DATABASE_VERSION = 1;

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.createTable(DinosaurTable.class, db);
    }
}
