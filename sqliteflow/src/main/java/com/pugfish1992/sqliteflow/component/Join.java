package com.pugfish1992.sqliteflow.component;

import android.support.annotation.NonNull;

/**
 * Created by daichi on 11/22/17.
 */

public class Join {

    public enum Type {

        INNER_JOIN("INNER JOIN"),
        LEFT_OUTER_JOIN("LEFT OUTER JOIN"),
        RIGHT_OUTER_JOIN("RIGHT OUTER JOIN");

        private final String mStatement;

        Type(String statement) {
            mStatement = statement;
        }

        @Override
        public String toString() {
            return mStatement;
        }
    }

    @NonNull private final String mLeftTable;
    @NonNull private final String mRightTable;
    @NonNull private final Type mType;
    private Where mJoinCondition;

    public Join(@NonNull String leftTable, @NonNull Type type, @NonNull String rightTable) {
        mLeftTable = leftTable;
        mRightTable = rightTable;
        mType = type;
        mJoinCondition = null;
    }

    public Join on(@NonNull Where joinCondition) {
        mJoinCondition = joinCondition;
        return this;
    }

    @Override
    public String toString() {
        if (mJoinCondition == null) {
            throw new IllegalStateException("specify join condition");
        }

        return mLeftTable + " " + mType.toString() + " " + mRightTable
                + " ON (" + mJoinCondition.toString() + ")";
    }
}
