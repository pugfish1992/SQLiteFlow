package com.pugfish1992.sample.data;

import com.pugfish1992.sqliteflow.annotation.*;
import com.pugfish1992.sqliteflow.core.AbsValidator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by daichi on 11/23/17.
 */

public class DinosaurTableContract {

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PERIOD = "period";
    private static final String COLUMN_IS_LIKED = "is_liked";

    public static final int ERROR_NAME_CANNOT_BE_EMPTY = 0;
    public static final int ERROR_INVALID_PERIOD = 1;

    @ClassNames(entryClass = "DinosaurBase", tableClass = "DinosaurTable")
    @Validator(DinosaurValidator.class)
    @Table("dinosaur")
    public static class Schema {

        @PrimaryKey
        @Column(COLUMN_ID)
        protected long id;

        @Column(COLUMN_NAME)
        protected String name;

        @Column(COLUMN_PERIOD)
        protected int period;

        @DefaultBool(false)
        @Column(COLUMN_IS_LIKED)
        protected boolean isLiked;
    }

    public static class DinosaurValidator extends AbsValidator {

        @Override
        protected Set<Integer> onValidate() {
            Set<Integer> errors = new HashSet<>();

            String name = super.getStringOf(COLUMN_NAME);
            if (name == null || name.length() == 0) {
                errors.add(ERROR_NAME_CANNOT_BE_EMPTY);
            }

            int periodAsInt = super.getIntOf(COLUMN_PERIOD);
            if (Period.from(periodAsInt) == Period.INVALID_PERIOD) {
                errors.add(ERROR_INVALID_PERIOD);
            }

            return errors;
        }
    }
}
