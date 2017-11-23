package com.pugfish1992.sqliteflow_processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Objects;

import javax.lang.model.element.Modifier;

import static com.pugfish1992.sqliteflow_processor.SupportedTypeUtils.*;

/**
 * Created by daichi on 11/23/17.
 */

class MethodSpecUtils {

    private static final ClassName CLASS_OBJECT = ClassName.get(Object.class);
    private static final ClassName CLASS_OBJECTS = ClassName.get(Objects.class);

    static MethodSpec equalsMethod(ClassName className, List<AnnotatedColumnField> fields) {
        if (fields.isEmpty()) return null;
        MethodSpec.Builder method = MethodSpec
                .methodBuilder("equals")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addParameter(CLASS_OBJECT, "o")
                .addStatement("if (this == o) return true")
                .addStatement("if (o == null || getClass() != o.getClass()) return false")
                .addStatement("$T that = ($T) o", className, className);

        for (int i = 0; i < fields.size(); ++i) {
            TypeName type = fields.get(i).variableType;
            String name = fields.get(i).variableName;
            boolean isLast = (i + 1 == fields.size());

            if (isShortType(type) || isIntType(type) ||
                    isLongType(type) || isBooleanType(type)) {
                if (isLast) method.addStatement("return $L == that.$L", name, name);
                else method.addStatement("if ($L != that.$L) return false", name, name);

            } else if (isFloatType(type)) {
                if (isLast) method.addStatement("return Float.compare(that.$L, $L) == 0", name, name);
                else method.addStatement("if (Float.compare(that.$L, $L) != 0) return false", name, name);

            } else if (isDoubleType(type)) {
                if (isLast) method.addStatement("return Double.compare(that.$L, $L) == 0", name, name);
                else method.addStatement("if (Double.compare(that.$L, $L) != 0) return false", name, name);

            } else if (isStringType(type)) {
                if (isLast) method.addStatement("return $L != null ? $L.equals(that.$L) : that.$L == null", name, name, name, name);
                else method.addStatement("if ($L != null ? !$L.equals(that.$L) : that.$L != null) return false", name, name, name, name);

            } else {
                // should not be reached
                throw new IllegalArgumentException(type.toString() + " is not supported");
            }
        }

        return method.build();
    }

    static MethodSpec hashCodeMethod(List<AnnotatedColumnField> fields) {
        MethodSpec.Builder method = MethodSpec
                .methodBuilder("hashCode")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.INT);

        if (fields.isEmpty()) {
            method.addStatement("return super.hashCode()");
        } else {
            method.addCode("return $L.hash(\n", CLASS_OBJECTS);
            for (int i = 0; i < fields.size(); ++i) {
                method.addCode((i + 1 == fields.size()) ? "$L);\n" : "$L,\n", fields.get(i).variableName);
            }
        }

        return method.build();
    }
}
