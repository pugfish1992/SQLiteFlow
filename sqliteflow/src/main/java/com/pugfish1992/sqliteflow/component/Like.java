package com.pugfish1992.sqliteflow.component;

import android.support.annotation.NonNull;

import com.pugfish1992.sqliteflow.core.Column;

/**
 * Created by daichi on 11/23/17.
 */

public class Like implements Where {

    private final String mStatement;

    public Like(@NonNull Column column, @NonNull String pattern) {
        mStatement = String.format("%s LIKE '%s'", column.toString(), pattern);
    }

    @Override
    public String toString() {
        return mStatement;
    }
}
