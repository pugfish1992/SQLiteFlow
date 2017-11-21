package com.pugfish1992.sqliteflow.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pugfish1992.sqliteflow.component.Where;

import java.util.List;

/**
 * Created by daichi on 11/22/17.
 */

public final class Select<T extends Entry> {

    @NonNull private Class<T> mTarget;
    private boolean mDistinct;
    @Nullable private String mTable;
    @Nullable private Where mWhere;

    public static <T extends Entry> Select<T> target(@NonNull Class<T> target) {
        return new Select<>(target);
    }

    /* Intentional private visibility */
    private Select(@NonNull Class<T> target) {
        mTarget = target;
        mDistinct = false;
        mTable = null;
        mWhere = null;
    }

    public Select<T> removeDuplicates(boolean removeDuplicates) {
        mDistinct = removeDuplicates;
        return this;
    }

    public Select<T> from(@NonNull Class<? extends Table> table) {
        mTable = Table.newInstanceOf(table).getName();
        return this;
    }

    public Select<T> where(@NonNull Where where) {
        mWhere = where;
        return this;
    }

    @NonNull
    public List<T> start() {
        return Storage.api().selectItems(
                mTarget,
                mDistinct,
                mTable,
                mWhere != null ? mWhere.toStatement() : null,
                null,
                null,
                null,
                null);
    }
}
