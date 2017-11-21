package com.pugfish1992.sqliteflow.annotation;

import com.pugfish1992.sqliteflow.core.AbsValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by daichi on 11/21/17.
 */

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Validator {
    Class<? extends AbsValidator> value();
}
