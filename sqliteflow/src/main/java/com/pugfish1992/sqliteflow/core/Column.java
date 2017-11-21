package com.pugfish1992.sqliteflow.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pugfish1992.sqliteflow.component.Condition;

/**
 * Created by daichi on 11/20/17.
 */

public class Column {

    @NonNull public final String name;
    @NonNull public final AffinityType type;
    public final boolean isPrimaryKey;

    /* Intentional package-private visibility */
    Column(@NonNull String name, @NonNull AffinityType type, boolean isPrimaryKey) {
        this.name = name;
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

    /**
     * BUILDER CLASS
     * ---------- */

    public static class Builder {

        @NonNull private String mName;
        @NonNull private AffinityType mType;
        private boolean mIsPrimaryKey;

        /* Intentional private visibility */
        private Builder(@NonNull String name) {
            mName = name;
            mType = AffinityType.BLOB;
            mIsPrimaryKey = false;
        }

        public static Builder name(@NonNull String name) {
            return new Builder(name);
        }

        public Builder type(@NonNull AffinityType type) {
            mType = type;
            return this;
        }

        public Builder primaryKey(boolean isPrimaryKey) {
            mIsPrimaryKey = isPrimaryKey;
            return this;
        }

        public Column build() {
            return new Column(mName, mType, mIsPrimaryKey);
        }
    }
}
