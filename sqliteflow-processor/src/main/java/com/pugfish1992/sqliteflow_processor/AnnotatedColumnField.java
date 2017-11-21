package com.pugfish1992.sqliteflow_processor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Modifier;

/**
 * Created by daichi on 11/20/17.
 */

class AnnotatedColumnField {

    @NonNull final String variableName;
    @NonNull final TypeName variableType;
    @NonNull final String columnName;
    // this would be one of PUBLIC, PROTECTED,
    // PRIVATE, or null(package private)
    @Nullable final Modifier modifier;
    @Nullable final Object initialValue;
    final boolean isPrimaryKey;

    AnnotatedColumnField(@NonNull String variableName,
                         @NonNull TypeName variableType,
                         @NonNull String columnName,
                         @Nullable Modifier modifier,
                         @Nullable Object initialValue,
                         boolean isPrimaryKey) {

        this.variableName = variableName;
        this.variableType = variableType;
        this.modifier = modifier;
        this.columnName = columnName;
        this.initialValue = initialValue;
        this.isPrimaryKey = isPrimaryKey;
    }
}
