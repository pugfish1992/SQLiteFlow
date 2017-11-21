package com.pugfish1992.sqliteflow.component;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pugfish1992.sqliteflow.utils.SqliteFormat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by daichi on 11/22/17.
 */

public class Condition implements Where {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({EQUALS, NOT_EQUALS, GREATER_THAN, GREATER_THAN_OR_EQUALS, LESS_THAN, LESS_THAN_OR_EQUALS})
    public @interface Operator{}
    public static final int EQUALS = 0;
    public static final int NOT_EQUALS = 1;
    public static final int GREATER_THAN = 2;
    public static final int GREATER_THAN_OR_EQUALS = 3;
    public static final int LESS_THAN = 4;
    public static final int LESS_THAN_OR_EQUALS = 5;

    @NonNull private String mColumn;
    @Operator private int mOperator;
    @Nullable private Object mValue;

    public Condition(@NonNull String column, @Operator int operator, @Nullable Object value) {
        mColumn = column;
        mOperator = operator;
        mValue = value;
    }

    public void setColumn(@NonNull String column) {
        mColumn = column;
    }

    public void setOperator(@Operator int operator) {
        mOperator = operator;
    }

    public void setValue(@Nullable Object value) {
        mValue = value;
    }

    @NonNull
    @Override
    public String toStatement(@NonNull String tableName) {
        StringBuilder statement = new StringBuilder(tableName).append(".").append(mColumn);
        if (mValue != null) {
            switch (mOperator) {
                case EQUALS: statement.append(" = "); break;
                case NOT_EQUALS: statement.append(" != "); break;
                case GREATER_THAN: statement.append(" > "); break;
                case GREATER_THAN_OR_EQUALS: statement.append(" >= "); break;
                case LESS_THAN: statement.append(" < "); break;
                case LESS_THAN_OR_EQUALS: statement.append(" <= "); break;
                default: throw new IllegalArgumentException("invalid operator");
            }
            statement.append(SqliteFormat.toLiteral(mValue));

        } else {
            if (mOperator == EQUALS) {
                statement.append(" is null");
            } else if (mOperator == NOT_EQUALS) {
                statement.append(" is not null");
            } else {
                throw new IllegalArgumentException("invalid operator");
            }
        }

        return statement.toString();
    }
}
