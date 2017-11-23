package com.pugfish1992.sqliteflow.core;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pugfish1992.sqliteflow.utils.SqliteFormat;

import java.util.Set;

/**
 * Created by daichi on 11/21/17.
 */

public abstract class AbsValidator {

    public interface ValidationErrorListener {
        void onValidationError(int validatorTag, Set<Integer> errors);
    }

    private ContentValues mContentValues = null;

    // required public empty constructor
    public AbsValidator() {}

    /**
     * @return An integer that you can use to identify validators.
     */
    public int getTag() { return 0; }

    /* Intentional package-private visibility */
    void setTarget(@NonNull Entry target) {
        mContentValues = target.packColumnData();
    }

    /**
     * Be sure to set a values to be validated using
     * {@link AbsValidator#setTarget(Entry)} before call this method.
     * @return A set of error codes if any error exist, otherwise return null or empty Set.
     */
    abstract protected Set<Integer> onValidate();

    @NonNull
    ContentValues getValidatedValues() {
        nullCheck();
        ContentValues validated = mContentValues;
        mContentValues = null;
        return validated;
    }

    public short getShortOf(@NonNull String column) {
        nullCheck();
        return mContentValues.getAsShort(column);
    }

    public int getIntOf(@NonNull String column) {
        nullCheck();
        return mContentValues.getAsInteger(column);
    }

    public long getLongOf(@NonNull String column) {
        nullCheck();
        return mContentValues.getAsLong(column);
    }

    public boolean getBooleanOf(@NonNull String column) {
        nullCheck();
        int boolAsInt = mContentValues.getAsInteger(column);
        return SqliteFormat.toBool(boolAsInt);
    }

    public float getFloatOf(@NonNull String column) {
        nullCheck();
        return mContentValues.getAsFloat(column);
    }

    public double getDoubleOf(@NonNull String column) {
        nullCheck();
        return mContentValues.getAsDouble(column);
    }

    public String getStringOf(@NonNull String column) {
        nullCheck();
        return mContentValues.getAsString(column);
    }

    private void nullCheck() {
        if (mContentValues == null) {
            throw new IllegalStateException("set target values " +
                    "using AbsValidator#setTarget(ContentValues)");
        }
    }
}
