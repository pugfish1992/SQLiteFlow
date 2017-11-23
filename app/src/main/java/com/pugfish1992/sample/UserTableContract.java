package com.pugfish1992.sample;


import com.pugfish1992.sqliteflow.annotation.*;
import com.pugfish1992.sqliteflow.core.AbsValidator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by daichi on 11/20/17.
 */

public class UserTableContract {

    @Validator(UserValidator.class)
    @Table("user")
    public static class Schema {

        @PrimaryKey
        @DefaultLong(-1)
        @Column("id")
        public long id;

        @Column("age")
        public int age;

        @DefaultString("def_text")
        @Column("name")
        public String name;

        @Column("has_brothers")
        @DefaultBool(true)
        public boolean hasBrothers;
    }

    public static class UserValidator extends AbsValidator {

        public static final int NAME_IS_EMPTY = 0;
        public static final int AGE_IS_LESS_THAN_10 = 1;

        @Override
        protected Set<Integer> onValidate() {
            Set<Integer> errors = new HashSet<>();

            String name = super.getStringOf("name");
            if (name == null || name.length() == 0) {
                errors.add(NAME_IS_EMPTY);
            }

            int age = super.getIntOf("age");
            if (age < 10) {
                errors.add(AGE_IS_LESS_THAN_10);
            }

            return errors;
        }
    }
}
