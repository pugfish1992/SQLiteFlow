package com.pugfish1992.sqliteflow.component;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daichi on 11/22/17.
 */

public class Expressions implements Where {

    private static final String SPACE = " ";
    private static final String AND = "AND";
    private static final String OR = "OR";

    private final List<Where> mExprs;
    private final List<String> mOperators;

    public Expressions(@NonNull Where firstExpression) {
        mExprs = new ArrayList<>();
        mOperators = new ArrayList<>();
        mExprs.add(firstExpression);
    }

    public Expressions and(@NonNull Where expression) {
        mExprs.add(expression);
        mOperators.add(AND);
        return this;
    }

    public Expressions or(@NonNull Where expression) {
        mExprs.add(expression);
        mOperators.add(OR);
        return this;
    }

    private String addParenthesesAround(String text) {
        return "(" + text + ")";
    }

    private String expressionToStatement(Where expression, String tableName) {
        if (expression instanceof Expressions) {
            return addParenthesesAround(expression.toStatement(tableName));
        } else {
            return expression.toStatement(tableName);
        }
    }

    @NonNull
    @Override
    public String toStatement(@NonNull String tableName) {
        StringBuilder statement = new StringBuilder(expressionToStatement(mExprs.get(0), tableName));
        for (int i = 0; i < mOperators.size(); ++i) {
            statement.append(SPACE)
                    .append(mOperators.get(i))
                    .append(SPACE)
                    .append(expressionToStatement(mExprs.get(i + 1), tableName));
        }
        return statement.toString();
    }
}
