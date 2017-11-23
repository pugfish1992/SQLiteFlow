package com.pugfish1992.sqliteflow.component;

import android.support.annotation.NonNull;

import com.pugfish1992.sqliteflow.core.Table;

/**
 * Created by daichi on 11/22/17.
 */

public class Join extends Joinable {

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

    @NonNull private final Joinable mLeftJoinable;
    @NonNull private final Table mRightTable;
    @NonNull private final Type mType;
    private Where mJoinCondition;

    public Join(@NonNull Joinable leftJoinable, @NonNull Type type, @NonNull Table rightTable) {
        mLeftJoinable = leftJoinable;
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

        String subStatement = (mLeftJoinable instanceof Join)
                ? String.format("(%s)", mLeftJoinable.toString())
                : mLeftJoinable.toString();

        return subStatement + " " + mType.toString() + " " + mRightTable.toString()
                + " ON (" + mJoinCondition.toString() + ")";
    }
}
