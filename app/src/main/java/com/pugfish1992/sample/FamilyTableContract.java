package com.pugfish1992.sample;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pugfish1992.sqliteflow.annotation.Column;
import com.pugfish1992.sqliteflow.annotation.DefaultBool;
import com.pugfish1992.sqliteflow.annotation.DefaultLong;
import com.pugfish1992.sqliteflow.annotation.DefaultString;
import com.pugfish1992.sqliteflow.annotation.PrimaryKey;
import com.pugfish1992.sqliteflow.annotation.Table;
import com.pugfish1992.sqliteflow.annotation.Validator;
import com.pugfish1992.sqliteflow.core.AbsValidator;

import java.util.Set;

/**
 * Created by daichi on 11/22/17.
 */

public class FamilyTableContract {

    @Table("family")
    public static class Schema {

        @PrimaryKey
        @DefaultLong(-1)
        @Column("id")
        public long id;
    }
}
