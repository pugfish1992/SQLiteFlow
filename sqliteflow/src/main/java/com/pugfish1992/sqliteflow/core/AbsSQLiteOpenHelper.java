package com.pugfish1992.sqliteflow.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.pugfish1992.sqliteflow.core.Column;
import com.pugfish1992.sqliteflow.core.Table;

import java.util.Set;

/**
 * Created by daichi on 11/21/17.
 */

abstract public class AbsSQLiteOpenHelper extends SQLiteOpenHelper {

    public AbsSQLiteOpenHelper(Context context, String dbName, int dbVersion) {
        super(context, dbName, null, dbVersion);
    }

    public AbsSQLiteOpenHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int dbVersion) {
        super(context, dbName, factory, dbVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public final void createTable(Class<? extends Table> tableClass, SQLiteDatabase db, boolean cancelIfExist) {
        Table table = Table.newInstanceOf(tableClass);
        Set<Column> columnSet = table.getColumnSet();
        String[] subStatements = new String[columnSet.size()];
        int i = 0;
        for (Column column : columnSet) {
            String subStatement = column.name + " " + column.type.toStatement();
            if (column.isPrimaryKey) {
                subStatement += " primary key";
            }
            subStatements[i] = subStatement;
            ++i;
        }
        String statement = (cancelIfExist) ? "create table if not exists" : "create table";
        statement += " " + table.getName() + " (" + TextUtils.join(",", subStatements) + ");";
        db.execSQL(statement);
    }

    public void deleteTableIfExists(Class<? extends Table> tableClass, SQLiteDatabase db, boolean cancelIfNotExist) {
        String table = Table.newInstanceOf(tableClass).getName();
        if (cancelIfNotExist) {
            db.execSQL("drop table if exists " + table);
        } else {
            db.execSQL("drop table " + table);
        }
    }
}
