package com.pugfish1992.sample;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pugfish1992.sqliteflow.annotation.*;
import com.pugfish1992.sqliteflow.core.AbsValidator;

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

        @Override
        public Set<Integer> onValidate(@NonNull ContentValues values) {
            return null;
        }
    }
}
