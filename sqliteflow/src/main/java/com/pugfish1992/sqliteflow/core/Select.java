package com.pugfish1992.sqliteflow.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pugfish1992.sqliteflow.component.Joinable;
import com.pugfish1992.sqliteflow.component.Where;

import java.util.List;

/**
 * Created by daichi on 11/22/17.
 */

public class Select<T extends Entry> {

    @NonNull private Class<T> mTarget;
    private boolean mDistinct;
    @Nullable private Joinable mFrom;
    @Nullable private Where mWhere;

    public static <T extends Entry> Select<T> target(@NonNull Class<T> target) {
        return new Select<>(target);
    }

    /* Intentional private visibility */
    private Select(@NonNull Class<T> target) {
        mTarget = target;
        mDistinct = false;
        mFrom = null;
        mWhere = null;
    }

    public Select<T> removeDuplicates(boolean removeDuplicates) {
        mDistinct = removeDuplicates;
        return this;
    }

    public Select<T> from(@NonNull Joinable from) {
        mFrom = from;
        return this;
    }

    public Select<T> from(@NonNull Class<? extends Table> from) {
        mFrom = Table.newInstanceOf(from);
        return this;
    }

    public Select<T> where(@NonNull Where where) {
        mWhere = where;
        return this;
    }

    @NonNull
    public List<T> start() {
        if (mFrom == null) {
            throw new IllegalStateException("specify a target table");
        }
        return Storage.api().selectItems(
                mTarget,
                mDistinct,
                mFrom.toString(),
                mWhere != null ? mWhere.toString() : null,
                null,
                null,
                null,
                null);
    }
}
