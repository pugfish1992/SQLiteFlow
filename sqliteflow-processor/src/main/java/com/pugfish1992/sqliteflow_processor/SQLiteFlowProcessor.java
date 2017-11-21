package com.pugfish1992.sqliteflow_processor;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pugfish1992.sqliteflow.annotation.Column;
import com.pugfish1992.sqliteflow.annotation.DefaultBool;
import com.pugfish1992.sqliteflow.annotation.DefaultDouble;
import com.pugfish1992.sqliteflow.annotation.DefaultInt;
import com.pugfish1992.sqliteflow.annotation.DefaultFloat;
import com.pugfish1992.sqliteflow.annotation.DefaultLong;
import com.pugfish1992.sqliteflow.annotation.DefaultShort;
import com.pugfish1992.sqliteflow.annotation.DefaultString;
import com.pugfish1992.sqliteflow.annotation.PrimaryKey;
import com.pugfish1992.sqliteflow.annotation.Table;
import com.pugfish1992.sqliteflow.annotation.Validator;
import com.pugfish1992.sqliteflow.core.AbsValidator;
import com.pugfish1992.sqliteflow.core.AffinityType;
import com.pugfish1992.sqliteflow.core.Entry;
import com.pugfish1992.sqliteflow.utils.SqliteFormat;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;

public class SQLiteFlowProcessor extends AbstractProcessor {

    private static final String PACKAGE_NAME_TO_GENERATE = com.pugfish1992.sqliteflow.core.Table.class.getPackage().getName();
    private static final String SUFFIX_OF_TABLE_CLASS_NAME = "Table";

    private static final ClassName CLASS_STRING = ClassName.get(String.class);
    private static final ClassName CLASS_COLUMN = ClassName.get(com.pugfish1992.sqliteflow.core.Column.class);
    private static final ClassName CLASS_AFFINITY_TYPE = ClassName.get(AffinityType.class);
    private static final ClassName CLASS_SET = ClassName.get(Set.class);
    private static final ClassName CLASS_ARRAYS = ClassName.get(Arrays.class);
    private static final ClassName CLASS_HASH_SET = ClassName.get(HashSet.class);
    private static final ClassName CLASS_TABLE = ClassName.get(com.pugfish1992.sqliteflow.core.Table.class);
    private static final ClassName CLASS_CONTENT_VALUES = ClassName.get(ContentValues.class);
    private static final ClassName CLASS_ABS_VALIDATOR = ClassName.get(AbsValidator.class);
    private static final ClassName CLASS_ENTRY = ClassName.get(Entry.class);
    private static final ClassName CLASS_SQLITE_FORMAT = ClassName.get(SqliteFormat.class);

    private Filer mFiler;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Arrays.asList(
                Column.class.getCanonicalName(),
                DefaultBool.class.getCanonicalName(),
                DefaultDouble.class.getCanonicalName(),
                DefaultFloat.class.getCanonicalName(),
                DefaultInt.class.getCanonicalName(),
                DefaultLong.class.getCanonicalName(),
                DefaultShort.class.getCanonicalName(),
                DefaultString.class.getCanonicalName(),
                PrimaryKey.class.getCanonicalName(),
                Table.class.getCanonicalName(),
                Validator.class.getCanonicalName()
        ));
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element typeElement : roundEnvironment.getElementsAnnotatedWith(Table.class)) {
            if (typeElement.getKind() != ElementKind.CLASS) {
                error(typeElement, "%s can be applied only to class", Table.class);
                continue;
            }

            Table tableAnno = typeElement.getAnnotation(Table.class);
            final String tableName = tableAnno.value();
            final String entryClassName = toPascalCase(tableName);
            final String tableClassName = entryClassName + SUFFIX_OF_TABLE_CLASS_NAME;

            if (tableName.length() == 0) {
                error(typeElement, "specified table name is " + "invalid because it is empty");
                continue;
            }
            if (entryClassName.length() == 0) {
                error(typeElement, "can't generate the name of a entry class from the specified table name '%s'", tableName);
                continue;
            }

            // See -> https://qiita.com/izumin5210/items/6675c68ae4229e9697bf
            Validator validatorAnno = typeElement.getAnnotation(Validator.class);
            TypeName validatorClass = null;
            if (validatorAnno != null) {
                try {
                    validatorAnno.value();
                } catch (MirroredTypeException e) {
                    validatorClass = ClassName.get(e.getTypeMirror());
                }
            }

            List<AnnotatedColumnField> annotatedFields = new ArrayList<>();
            List<AnnotatedColumnField> primaryKeyAnnotatedFields = new ArrayList<>();
            for (Element element : typeElement.getEnclosedElements()) {
                AnnotatedColumnField field = extractAnnotatedColumnFieldData(element);
                if (field != null) {
                    annotatedFields.add(field);
                    if (field.isPrimaryKey) primaryKeyAnnotatedFields.add(field);
                }
            }

            if (primaryKeyAnnotatedFields.size() == 0) {
                error("there is no primary-key column in the table '%s'", tableName);
                continue;
            } else if (1 < primaryKeyAnnotatedFields.size()) {
                error("there are multiple primary-key column in '%s'", tableName);
                continue;
            }

            try {
                ClassName tableClass = ClassName.get(PACKAGE_NAME_TO_GENERATE, tableClassName);
                ClassName entryClass = ClassName.get(PACKAGE_NAME_TO_GENERATE, entryClassName);
                generateTableClass(PACKAGE_NAME_TO_GENERATE, tableClass, tableName, annotatedFields, primaryKeyAnnotatedFields.get(0), validatorClass);
                generateEntryClass(PACKAGE_NAME_TO_GENERATE, entryClass, tableClass, annotatedFields, primaryKeyAnnotatedFields.get(0));

            } catch (IOException e) {
                e.printStackTrace();
                error("error creating file '%s.java'", tableClassName);
            }

        }

        return false;
    }

    @Nullable
    private AnnotatedColumnField extractAnnotatedColumnFieldData(Element element) {
        if (element.getKind() != ElementKind.FIELD) return null;

        String variableName = element.getSimpleName().toString();

        Column columnAnno = element.getAnnotation(Column.class);
        if (columnAnno == null) return null;
        String columnName = columnAnno.value();
        if (columnName.length() == 0) {
            error(element, "column name can't be empty");
            return null;
        }

        TypeName variableType = TypeName.get(element.asType());
        if (!isSupportedJavaType(variableType)) {
            error(element, "the variable '%s' is declared as '%s', but this type is not supported",
                    variableName, variableType.toString());
            return null;
        }

        // 'element' is a field, so it would have one of public, protected,
        // private modifier, or nothing (this means package-private).
        Modifier modifier = null;
        for (Modifier m : element.getModifiers()) {
            if (m == Modifier.PUBLIC ||
                    m == Modifier.PROTECTED ||
                    m == Modifier.PRIVATE) {
                modifier = m;
                break;
            }
        }

        boolean isPrimaryKey = false;
        if (element.getAnnotation(PrimaryKey.class) != null) {
            if (!isLongType(variableType)) {
                error("@%s can be applied only to long field", PrimaryKey.class.getSimpleName());
                return null;
            }
            isPrimaryKey = true;
        }

        Object defaultValue = null;
        DefaultBool defBoolAnno = element.getAnnotation(DefaultBool.class);
        if (defBoolAnno != null) {
            if (!isBooleanType(variableType)) {
                error("@%s can be applied only to boolean field", DefaultBool.class.getSimpleName());
            } else {
                defaultValue = defBoolAnno.value();
            }
        }
        DefaultDouble defDoubleAnno = element.getAnnotation(DefaultDouble.class);
        if (defDoubleAnno != null) {
            if (!isDoubleType(variableType)) {
                error("@%s can be applied only to double field", DefaultDouble.class.getSimpleName());
            } else {
                defaultValue = defDoubleAnno.value();
            }
        }
        DefaultFloat defFloatAnno = element.getAnnotation(DefaultFloat.class);
        if (defFloatAnno != null) {
            if (!isFloatType(variableType)) {
                error("@%s can be applied only to float field", DefaultFloat.class.getSimpleName());
            } else {
                defaultValue = defFloatAnno.value();
            }
        }
        DefaultInt defIntAnno = element.getAnnotation(DefaultInt.class);
        if (defIntAnno != null) {
            if (!isIntType(variableType)) {
                error("@%s can be applied only to int field", DefaultInt.class.getSimpleName());
            } else {
                defaultValue = defIntAnno.value();
            }
        }
        DefaultLong defLongAnno = element.getAnnotation(DefaultLong.class);
        if (defLongAnno != null) {
            if (!isLongType(variableType)) {
                error("@%s can be applied only to long field", DefaultLong.class.getSimpleName());
            } else {
                defaultValue = defLongAnno.value();
            }
        }
        DefaultShort defShortAnno = element.getAnnotation(DefaultShort.class);
        if (defShortAnno != null) {
            if (!isShortType(variableType)) {
                error("@%s can be applied only to short field", DefaultShort.class.getSimpleName());
            } else {
                defaultValue = defShortAnno.value();
            }
        }
        DefaultString defStrAnno = element.getAnnotation(DefaultString.class);
        if (defStrAnno != null) {
            if (!isStringType(variableType)) {
                error("@%s can be applied only to String field", DefaultString.class.getSimpleName());
            } else {
                defaultValue = defStrAnno.value();
            }
        }

        return new AnnotatedColumnField(
                variableName, variableType, columnName, modifier, defaultValue, isPrimaryKey);
    }

    private void generateTableClass(String packageName, ClassName tableClass, String tableName,
                                    List<AnnotatedColumnField> annotatedColumnFields, AnnotatedColumnField primaryKeyField,
                                    @Nullable TypeName validatorClass) throws IOException {

        TypeSpec.Builder tableClassSpec = TypeSpec
                .classBuilder(tableClass)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(CLASS_TABLE);

        // a static constant instance of AbsValidator class
        final String varName_VALIDATOR = "VALIDATOR";
        FieldSpec.Builder fieldSpec = FieldSpec
                .builder(CLASS_ABS_VALIDATOR, varName_VALIDATOR)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .addAnnotation(Nullable.class);
        if (validatorClass != null) {
            fieldSpec.initializer("new $T()", validatorClass);
        } else {
            fieldSpec.initializer("null");
        }
        tableClassSpec.addField(fieldSpec.build());

        // static constant Column variables
        for (AnnotatedColumnField field : annotatedColumnFields) {
            AffinityType affinityType = toAffinityTypeFromSupportedJavaType(field.variableType);
            FieldSpec.Builder constColumnSpec = FieldSpec
                    .builder(CLASS_COLUMN, field.variableName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new $T($S, $S, $T.$L, $L)", CLASS_COLUMN, field.columnName, tableName,
                            CLASS_AFFINITY_TYPE, affinityType.name(), String.valueOf(field.isPrimaryKey));
            tableClassSpec.addField(constColumnSpec.build());
        }

        // override method; getName()
        tableClassSpec.addMethod(MethodSpec
                .methodBuilder("getName")
                .addAnnotation(Override.class)
                .addAnnotation(NonNull.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(CLASS_STRING)
                .addStatement("return $S", tableName)
                .build());

        // override method; getColumnSet()
        List<String> varNames = new ArrayList<>(annotatedColumnFields.size());
        for (AnnotatedColumnField field : annotatedColumnFields) {
            varNames.add(field.variableName);
        }
        tableClassSpec.addMethod(MethodSpec
                .methodBuilder("getColumnSet")
                .addAnnotation(Override.class)
                .addAnnotation(NonNull.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(CLASS_SET, CLASS_COLUMN))
                .addStatement("return new $T<>($T.asList($L))", CLASS_HASH_SET, CLASS_ARRAYS, joinTokens(",", varNames))
                .build());

        // override method; getValidator()
        tableClassSpec.addMethod(MethodSpec
                .methodBuilder("getValidator")
                .addAnnotation(Override.class)
                .addAnnotation(Nullable.class)
                .returns(CLASS_ABS_VALIDATOR)
                .addStatement("return $L", varName_VALIDATOR)
                .build());

        // override method; getPrimaryKeyColumn()
        tableClassSpec.addMethod(MethodSpec
        .methodBuilder("getPrimaryKeyColumn")
        .addAnnotation(Override.class)
        .addAnnotation(NonNull.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(CLASS_COLUMN)
        .addStatement("return $L", primaryKeyField.variableName)
        .build());

        // required public empty constructor
        tableClassSpec.addMethod(MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build());

        JavaFile.builder(packageName, tableClassSpec.build()).build().writeTo(mFiler);
    }

    private void generateEntryClass(String packageName, ClassName entryClass, ClassName parentTableCass,
                                    List<AnnotatedColumnField> annotatedColumnFields,
                                    AnnotatedColumnField primaryKeyField) throws IOException {

        TypeSpec.Builder entryClassSpec = TypeSpec
                .classBuilder(entryClass)
                .addModifiers(Modifier.PUBLIC)
                .superclass(CLASS_ENTRY);

        // member variables
        for (AnnotatedColumnField field : annotatedColumnFields) {
            FieldSpec.Builder memberSpec = FieldSpec.builder(field.variableType, field.variableName);
            if (field.modifier != null) {
                memberSpec.addModifiers(field.modifier);
            }
            if (field.initialValue != null) {
                if (field.initialValue instanceof String) {
                    memberSpec.initializer("$S", field.initialValue);
                } else {
                    memberSpec.initializer("$L", field.initialValue);
                }
            }
            entryClassSpec.addField(memberSpec.build());
        }

        // a static constant instance of the parent Table class
        final String varName_PARENT_TABLE = "PARENT_TABLE";
        entryClassSpec.addField(FieldSpec
                .builder(CLASS_TABLE, varName_PARENT_TABLE)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T()", parentTableCass)
                .build());

        // override method; getParentTable()
        entryClassSpec.addMethod(MethodSpec
                .methodBuilder("getParentTable")
                .addAnnotation(Override.class)
                .addAnnotation(NonNull.class)
                .returns(CLASS_TABLE)
                .addStatement("return $L", varName_PARENT_TABLE)
                .build());

        // override method; getPrimaryKey()
        entryClassSpec.addMethod(MethodSpec
                .methodBuilder("getPrimaryKey")
                .addAnnotation(Override.class)
                .returns(TypeName.LONG)
                .addStatement("return $L", primaryKeyField.variableName)
                .build());

        // override method; setPrimaryKey()
        entryClassSpec.addMethod(MethodSpec
                .methodBuilder("setPrimaryKey")
                .addAnnotation(Override.class)
                .addParameter(TypeName.LONG, "primaryKey")
                .addStatement("$L = primaryKey", primaryKeyField.variableName)
                .build());

        // override method; packColumnData()
        MethodSpec.Builder method = MethodSpec
                .methodBuilder("packColumnData")
                .addAnnotation(Override.class)
                .addAnnotation(NonNull.class)
                .returns(CLASS_CONTENT_VALUES)
                .addStatement("$T v = new $T()", CLASS_CONTENT_VALUES, CLASS_CONTENT_VALUES);
        for (AnnotatedColumnField field : annotatedColumnFields) {
            if (isBooleanType(field.variableType)) {
                method.addStatement("v.put($S, $T.toInt($L))", field.columnName, CLASS_SQLITE_FORMAT, field.variableName);
            } else {
                method.addStatement("v.put($S, $L)", field.columnName, field.variableName);
            }
        }
        method.addStatement("return v");
        entryClassSpec.addMethod(method.build());

        // override method; unpackColumnData()
        method = MethodSpec
                .methodBuilder("unpackColumnData")
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(CLASS_CONTENT_VALUES, "v").addAnnotation(NonNull.class).build());
        for (AnnotatedColumnField field : annotatedColumnFields) {
            String methodNameToGetVal = null;
            if (isBooleanType(field.variableType)) {
                method.addStatement("$L = $T.toBool(v.getAsInteger($S))", field.variableName, CLASS_SQLITE_FORMAT, field.columnName);
            } else {
                if (isShortType(field.variableType)) methodNameToGetVal = "getAsShort";
                else if (isIntType(field.variableType)) methodNameToGetVal = "getAsInteger";
                else if (isLongType(field.variableType)) methodNameToGetVal = "getAsLong";
                else if (isFloatType(field.variableType)) methodNameToGetVal = "getAsFloat";
                else if (isDoubleType(field.variableType)) methodNameToGetVal = "getAsDouble";
                else if (isStringType(field.variableType)) methodNameToGetVal = "getAsString";

                if (methodNameToGetVal != null) {
                    method.addStatement("$L = v.$L($S)", field.variableName, methodNameToGetVal, field.columnName);
                }
            }
        }
        entryClassSpec.addMethod(method.build());

        // override method; save()
        entryClassSpec.addMethod(MethodSpec
        .methodBuilder("save")
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeName.BOOLEAN)
        .addStatement("return $L.save(this)", varName_PARENT_TABLE)
        .build());

        // override method; delete()
        entryClassSpec.addMethod(MethodSpec
        .methodBuilder("delete")
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeName.BOOLEAN)
        .addStatement("return $L.delete(this)", varName_PARENT_TABLE)
        .build());

        // required public empty constructor
        entryClassSpec.addMethod(MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build());

        JavaFile.builder(packageName, entryClassSpec.build()).build().writeTo(mFiler);
    }

    /**
     * Convert a snake-case or a camel-case to a pascal-case(upper-camel-case).
     * @param name Expect a snake-case or a camel-case.
     */
    @NonNull private String toPascalCase(@NonNull String name) {
        StringBuilder pascal = new StringBuilder();
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (Character.isLetter(c)) {
                pascal.append(c);
            } else {
                if (Character.isDigit(c)) {
                    pascal.append(c);
                }
                if (i + 1 < name.length() && Character.isLetter(name.charAt(i + 1))) {
                    pascal.append(Character.toUpperCase(name.charAt(i + 1)));
                    ++i;
                }
            }
        }

        if (0 < pascal.length()) {
            char c = Character.toUpperCase(pascal.charAt(0));
            pascal.replace(0, 1, Character.toString(c));
        }

        return pascal.toString();
    }

    private void error(Element e, String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private boolean isSupportedJavaType(TypeName typeName) {
        return isBooleanType(typeName) ||
                isShortType(typeName) ||
                isIntType(typeName) ||
                isLongType(typeName) ||
                isFloatType(typeName) ||
                isDoubleType(typeName) ||
                isStringType(typeName);
    }

    private AffinityType toAffinityTypeFromSupportedJavaType(TypeName typeName) {
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

    private boolean isBooleanType(TypeName typeName) {
        return TypeName.BOOLEAN.equals(typeName);
    }

    private boolean isShortType(TypeName typeName) {
        return TypeName.SHORT.equals(typeName);
    }

    private boolean isIntType(TypeName typeName) {
        return TypeName.INT.equals(typeName);
    }

    private boolean isLongType(TypeName typeName) {
        return TypeName.LONG.equals(typeName);
    }

    private boolean isStringType(TypeName typeName) {
        return CLASS_STRING.equals(typeName);
    }

    private boolean isFloatType(TypeName typeName) {
        return TypeName.FLOAT.equals(typeName);
    }

    private boolean isDoubleType(TypeName typeName) {
        return TypeName.DOUBLE.equals(typeName);
    }

    private String joinTokens(CharSequence delimiter, Iterable tokens) {
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = tokens.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(delimiter);
                sb.append(it.next());
            }
        }
        return sb.toString();
    }
}
