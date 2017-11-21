package com.pugfish1992.sqliteflow.component;

import android.support.annotation.NonNull;

/**
 * Created by daichi on 11/22/17.
 */

public interface Where {
    @NonNull public String toStatement(@NonNull String tableName);
}
