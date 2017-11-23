package com.pugfish1992.sqliteflow.utils;

import java.util.Set;

/**
 * Created by daichi on 11/22/17.
 */

public interface ValidationErrorListener {
    void onValidationError(int validatorTag, Set<Integer> errors);
}
