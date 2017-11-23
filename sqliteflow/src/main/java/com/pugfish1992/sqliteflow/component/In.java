package com.pugfish1992.sqliteflow.component;

import android.support.annotation.NonNull;

import com.pugfish1992.sqliteflow.core.Column;
import com.pugfish1992.sqliteflow.utils.SqliteFormat;

/**
 * Created by daichi on 11/23/17.
 */

public class In implements Where {

    private final String mStatement;

    public In(@NonNull Column column, @NonNull Object... args) {
        StringBuilder statement = new StringBuilder()
                .append(column.toString())
                .append(" IN (");

        for (int i = 0; i < args.length; ++i) {
            statement.append(SqliteFormat.toLiteral(args[i]));
            if (i + 1 < args.length) statement.append(",");
        }

        mStatement = statement.append(")").toString();
    }

    @Override
    public String toString() {
        return mStatement;
    }
}
