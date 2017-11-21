package com.pugfish1992.sqliteflow.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by daichi on 11/20/17.
 */

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface DefaultString {
    String value();
}
