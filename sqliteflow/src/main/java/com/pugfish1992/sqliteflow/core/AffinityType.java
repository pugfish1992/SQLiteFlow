package com.pugfish1992.sqliteflow.core;

/**
 * This class is based on the chapter 3 of this page -> https://sqlite.org/datatype3.html
 */
public enum AffinityType {
    TEXT,
    NUMERIC,
    INTEGER,
    REAL,
    BLOB;

    public String toStatement() {
        return this.name();
    }
}
