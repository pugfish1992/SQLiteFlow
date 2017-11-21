package com.pugfish1992.sqliteflow.utils;

/**
 * Created by daichi on 11/20/17.
 */

public class SqliteFormat {

    /* Intentional private visibility */
    private SqliteFormat() {}

    public static String toLiteral(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof CharSequence) {
            return "'" + obj.toString() + "'";
        } else if (obj instanceof Boolean) {
            return String.valueOf(toInt((Boolean) obj));
        } else {
            return obj.toString();
        }
    }

    /**
     * SQLite does not have a separate Boolean storage class. Instead,
     * Boolean values are stored as integers 0 (false) and 1 (true).
     * See more info -> http://www.sqlite.org/datatype3.html
     * -------------------------------------------------------------- */

    public static int toInt(boolean bool) {
        return bool ? 1 : 0;
    }

    public static boolean toBool(int integer) {
        return integer != 0;
    }
}
