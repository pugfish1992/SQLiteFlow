package com.pugfish1992.sqliteflow.component;

import android.support.annotation.NonNull;

/**
 * Created by daichi on 11/22/17.
 */

public class Expressions implements Where {

    private final StringBuilder mStatement;

    public Expressions(@NonNull Where firstExpression) {
        mStatement = new StringBuilder(firstExpression.toString());
    }

    public Expressions(@NonNull Expressions firstExpression) {
        mStatement = new StringBuilder(addParenthesesAround(firstExpression.toString()));
    }

    public Expressions and(@NonNull Where expression) {
        mStatement.append(" AND ").append(expression.toString());
        return this;
    }

    public Expressions and(@NonNull Expressions expression) {
        mStatement.append(" AND ").append(addParenthesesAround(expression.toString()));
        return this;
    }

    public Expressions or(@NonNull Where expression) {
        mStatement.append(" OR ").append(expression.toString());
        return this;
    }

    public Expressions or(@NonNull Expressions expression) {
        mStatement.append(" OR ").append(addParenthesesAround(expression.toString()));
        return this;
    }

    private String addParenthesesAround(String text) {
        return "(" + text + ")";
    }

    @Override
    public String toString() {
        return mStatement.toString();
    }
}
