package com.pugfish1992.sample.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by daichi on 11/23/17.
 */

public enum  Period {
    
    INVALID_PERIOD(-1),
    // 145.5 million years ago — 66.0 million years ago.
    CRETACEOUS(0),
    // 201.3 million years ago — 145.0 million years ago.
    JURASSIC(1),
    // 252.17 million years ago — 201.3 million years ago.
    TRIASSIC(2);

    private final int mId;

    Period(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public static Period from(int id) {
        Period[] values = values();
        for (Period value : values) {
            if (value.getId() == id) return value;
        }
        return INVALID_PERIOD;
    }
}
