package com.pugfish1992.sqliteflow.component;

import android.support.annotation.NonNull;

/**
 * Created by daichi on 11/22/17.
 */

public final class Expression implements Where {

    private StringBuilder mStatement;

    public static Expression first(@NonNull Where firstExpression) {
        return new Expression(firstExpression);
    }

    public static Expression first(@NonNull Expression firstExpression) {
        return new Expression(firstExpression);
    }

    /* Intentional private visibility */
    private Expression(@NonNull Where firstExpression) {
        mStatement = new StringBuilder(firstExpression.toStatement());
    }

    /* Intentional private visibility */
    private Expression(@NonNull Expression firstExpression) {
        mStatement = new StringBuilder(addParenthesesAround(firstExpression.toStatement()));
    }

    public Expression and(@NonNull Where expression) {
        mStatement.append(" AND ").append(expression.toStatement());
        return this;
    }

    public Expression and(@NonNull Expression expression) {
        mStatement.append(" AND ").append(addParenthesesAround(expression.toStatement()));
        return this;
    }

    public Expression or(@NonNull Where expression) {
        mStatement.append(" OR ").append(expression.toStatement());
        return this;
    }

    public Expression or(@NonNull Expression expression) {
        mStatement.append(" OR ").append(addParenthesesAround(expression.toStatement()));
        return this;
    }

    private String addParenthesesAround(String text) {
        return "(" + text + ")";
    }

    @NonNull
    @Override
    public String toStatement() {
        return mStatement.toString();
    }
}
