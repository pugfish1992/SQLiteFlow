package com.pugfish1992.sqliteflow.core;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Set;

/**
 * Created by daichi on 11/20/17.
 */

abstract public class Table {

    /*

    Sub-class of this class would has some fields such as below:

    public static final Column id;
    public static final Column name;
    public static final Column age;
    static {
        id = Column.Builder
                .name("id")
                .type(AffinityType.INTEGER)
                .primaryKey(true)
                .build();

        name = Column.Builder
                .name("name")
                .type(AffinityType.TEXT)
                .build();

        age = Column.Builder
                .name("age")
                .build();
    }

    */

    public static <T extends Table> T newInstanceOf(@NonNull Class<T> tableClass) {
        try {
            return tableClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format("%s has no default constructor", tableClass.getSimpleName()));
        }
    }

    // required public empty constructor
    Table() {}

    // Return the table name (this must be constant string)
    @NonNull abstract public String getName();

    // Return all columns in the table, Should return it as unmodifiable list
    @NonNull abstract public Set<Column> getColumnSet();

    /* Intentional package-private visibility */
    @Nullable abstract AbsValidator getValidator();

    // Return the column which is declared as PrimaryKey
    @NonNull abstract public Column getPrimaryKeyColumn();

    /* Intentional package-private visibility */
    // Return true if saving is successful, false otherwise
    final boolean save(Entry entry) {
        AbsValidator validator = getValidator();
        ContentValues values = entry.packColumnData();
        if (validator != null) {
            Set<Integer> errors = validator.onValidate(values);
            if (errors != null && errors.size() != 0) return false;
        }
        return Storage.api().saveItem(entry, values);
    }

    /* Intentional package-private visibility */
    // Return true if deleting is successful, false otherwise
    final boolean delete(Entry entry) {
        return Storage.api().deleteItem(entry);
    }
}
