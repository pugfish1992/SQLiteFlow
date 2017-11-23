package com.pugfish1992.sqliteflow.component;

import android.support.annotation.NonNull;

import com.pugfish1992.sqliteflow.core.Table;

/**
 * Created by daichi on 11/23/17.
 */

abstract public class Joinable {

    public final Join innerJoin(@NonNull Table table) {
        return new Join(this, Join.Type.INNER_JOIN, table);
    }

    public final Join leftOuterJoin(@NonNull Table table) {
        return new Join(this, Join.Type.LEFT_OUTER_JOIN, table);
    }

    public final Join rightOuterJoin(@NonNull Table table) {
        return new Join(this, Join.Type.RIGHT_OUTER_JOIN, table);
    }

    @Override
    abstract public String toString();
}
