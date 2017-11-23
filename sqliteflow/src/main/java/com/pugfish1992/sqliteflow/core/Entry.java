package com.pugfish1992.sqliteflow.core;

import android.content.ContentValues;
import android.support.annotation.NonNull;

/**
 * Created by daichi on 11/22/17.
 */

abstract public class Entry {

    /* Intentional package-private visibility */
    static <T extends Entry> T newInstanceOf(@NonNull Class<T> entryClass) {
        try {
            return entryClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format("%s has no default constructor", entryClass.getSimpleName()));
        }
    }

    /* Intentional package-private visibility */
    // Return a primary key, this will be only used in Storage class
    abstract long getPrimaryKey();

    /* Intentional package-private visibility */
    // Set a new primary key, this will be only used in Storage class
    abstract void setPrimaryKey(long primaryKey);

    /* Intentional package-private visibility */
    // Pack all pairs of column name and its value into ContentValues
    @NonNull abstract ContentValues packColumnData();

    /* Intentional package-private visibility */
    // Apply values in the passed ContentValues to fields, each key of it must be a column name
    abstract void unpackColumnData(@NonNull ContentValues values);

    /* Intentional package-private visibility */
    @NonNull abstract Table getParentTable();

    // Return true if saving is successful, false otherwise
    abstract public boolean save();
    abstract public boolean save(AbsValidator.ValidationErrorListener listener);

    // Return true if deleting is successful, false otherwise
    abstract public boolean delete();
}
