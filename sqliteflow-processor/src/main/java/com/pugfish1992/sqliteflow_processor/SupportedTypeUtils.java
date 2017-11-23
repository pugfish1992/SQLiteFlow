package com.pugfish1992.sqliteflow_processor;

import com.pugfish1992.sqliteflow.core.AffinityType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * Created by daichi on 11/23/17.
 */

class SupportedTypeUtils {

    private static final ClassName CLASS_STRING = ClassName.get(String.class);

    static boolean isSupportedJavaType(TypeName typeName) {
        return isBooleanType(typeName) ||
                isShortType(typeName) ||
                isIntType(typeName) ||
                isLongType(typeName) ||
                isFloatType(typeName) ||
                isDoubleType(typeName) ||
                isStringType(typeName);
    }

    static AffinityType toAffinityTypeFromSupportedJavaType(TypeName typeName) {
        if (isShortType(typeName) ||
                isIntType(typeName)||
                isLongType(typeName) ||
                isBooleanType(typeName)) {
            return AffinityType.INTEGER;

        } else if (isFloatType(typeName) ||
                isDoubleType(typeName)) {
            return AffinityType.REAL;

        } else if (typeName.equals(CLASS_STRING)) {
            return AffinityType.TEXT;

        } else {
            return AffinityType.BLOB;
        }
    }

    static boolean isBooleanType(TypeName typeName) {
        return TypeName.BOOLEAN.equals(typeName);
    }

    static boolean isShortType(TypeName typeName) {
        return TypeName.SHORT.equals(typeName);
    }

    static boolean isIntType(TypeName typeName) {
        return TypeName.INT.equals(typeName);
    }

    static boolean isLongType(TypeName typeName) {
        return TypeName.LONG.equals(typeName);
    }

    static boolean isStringType(TypeName typeName) {
        return CLASS_STRING.equals(typeName);
    }

    static boolean isFloatType(TypeName typeName) {
        return TypeName.FLOAT.equals(typeName);
    }

    static boolean isDoubleType(TypeName typeName) {
        return TypeName.DOUBLE.equals(typeName);
    }
}
