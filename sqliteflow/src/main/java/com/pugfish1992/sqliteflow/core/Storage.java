package com.pugfish1992.sqliteflow.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by daichi on 11/21/17.
 */

/* Intentional package-private visibility */
class Storage {

    private static Storage INSTANCE = null;
    private final SQLiteOpenHelper mOpenHelper;

    static void init(SQLiteOpenHelper openHelper) {
        INSTANCE = new Storage(openHelper);
    }

    static Storage api() {
        if (INSTANCE == null) {
            throw new RuntimeException("initialize Storage class");
        }
        return INSTANCE;
    }

    /* Intentional private visibility */
    private Storage(SQLiteOpenHelper openHelper) {
        mOpenHelper = openHelper;
    }

    @NonNull
    <T extends Entry> List<T> selectItems(Class<T> target, boolean distinct, String from, String where,
                                          String groupBy, String having, String orderBy, String limit) {

        Set<Column> columnSet = Entry.newInstanceOf(target).getParentTable().getColumnSet();
        String[] columns = new String[columnSet.size()];
        int i = 0;
        for (Column column : columnSet) {
            columns[i] = column.name;
            ++i;
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(distinct, from, columns, where, null, groupBy, having, orderBy, limit);
        boolean hasNext = cursor.moveToFirst();
        List<T> items = new ArrayList<>(cursor.getCount());
        ContentValues values = new ContentValues();
        while (hasNext) {
            values.clear();
            T item = Entry.newInstanceOf(target);
            DatabaseUtils.cursorRowToContentValues(cursor, values);
            item.unpackColumnData(values);
            items.add(item);
            hasNext = cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return items;
    }

    boolean saveItem(Entry item, ContentValues validatedValues) {
        Table table = item.getParentTable();
        String where = table.getPrimaryKeyColumn().name + " = " + String.valueOf(item.getPrimaryKey());
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int affectedRowCount;

        // 1. attempt to update
        db.beginTransaction();
        try {
            affectedRowCount = db.update(table.getName(), validatedValues, where, null);
            if (affectedRowCount == 1) db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (affectedRowCount == 1) {
            db.close();
            return true;
        } else if (1 < affectedRowCount) {
            // TODO; when the id is not unique...
            db.close();
            return false;
        }

        // 2. attempt to insert
        validatedValues.remove(table.getPrimaryKeyColumn().simpleName);
        long newId = db.insert(table.getName(), null, validatedValues);
        db.close();
        if (newId == -1) {
            // TODO; when fail to insert a new row...
            return false;
        } else {
            item.setPrimaryKey(newId);
            return true;
        }
    }

    boolean deleteItem(Entry item) {
        Table table = item.getParentTable();
        String where = table.getPrimaryKeyColumn().name + " = " + String.valueOf(item.getPrimaryKey());
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int affectedRowCount;

        db.beginTransaction();
        try {
            affectedRowCount = db.delete(table.getName(), where, null);
            if (affectedRowCount == 1) db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }

        if (affectedRowCount == 1) {
            return true;
        } else {
            // TODO; when fail to delete a row...
            return false;
        }
    }
}
