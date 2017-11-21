package com.pugfish1992.sqliteflow.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pugfish1992.sqliteflow.component.Condition;

/**
 * Created by daichi on 11/20/17.
 */

public class Column {

    @NonNull public final String simpleName;
    @NonNull public final String name;
    @NonNull public final AffinityType type;
    public final boolean isPrimaryKey;

    /* Intentional package-private visibility */
    Column(@NonNull String simpleName, @NonNull String belongingTableName,
           @NonNull AffinityType type, boolean isPrimaryKey) {

        this.simpleName = simpleName;
        this.name = belongingTableName + "." + simpleName;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Column column = (Column) o;

        if (isPrimaryKey != column.isPrimaryKey) return false;
        if (!name.equals(column.name)) return false;
        return type == column.type;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (isPrimaryKey ? 1 : 0);
        return result;
    }

    public Condition equalsTo(@Nullable Object value) {
        return new Condition(this.name, Condition.EQUALS, value);
    }

    public Condition notEqualsTo(@Nullable Object value) {
        return new Condition(this.name, Condition.NOT_EQUALS, value);
    }

    public Condition lessThan(@Nullable Object value) {
        return new Condition(this.name, Condition.LESS_THAN, value);
    }

    public Condition lessThanOrEqualsTo(@Nullable Object value) {
        return new Condition(this.name, Condition.LESS_THAN_OR_EQUALS, value);
    }

    public Condition greater(@Nullable Object value) {
        return new Condition(this.name, Condition.GREATER_THAN, value);
    }

    public Condition greaterThanOrEqualsTo(@Nullable Object value) {
        return new Condition(this.name, Condition.GREATER_THAN_OR_EQUALS, value);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
