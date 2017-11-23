package com.pugfish1992.sqliteflow.component;

import android.support.annotation.NonNull;

import com.pugfish1992.sqliteflow.core.Column;
import com.pugfish1992.sqliteflow.utils.SqliteFormat;

/**
 * Created by daichi on 11/23/17.
 */

public class Between implements Where {

    private final String mStatement;

    public Between(@NonNull Column column, @NonNull Object min, @NonNull Object max) {
        mStatement = String.format("%s BETWEEN %s AND %s",
                column.toString(), SqliteFormat.toLiteral(min), SqliteFormat.toLiteral(max));
    }

    @Override
    public String toString() {
        return mStatement;
    }
}
