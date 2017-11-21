package com.pugfish1992.sqliteflow.core;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import java.util.Set;

/**
 * Created by daichi on 11/21/17.
 */

public abstract class AbsValidator {

    // required public empty constructor
    public AbsValidator() {}

    /**
     * Return a set of error codes if any error exist, otherwise return null or empty Set.
     */
    abstract public Set<Integer> onValidate(@NonNull ContentValues values);
}
