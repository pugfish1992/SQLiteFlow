package com.pugfish1992.sample.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pugfish1992.sqliteflow.core.DinosaurBase;
import com.pugfish1992.sqliteflow.core.DinosaurTable;
import com.pugfish1992.sqliteflow.core.Select;

import java.util.List;

/**
 * Created by daichi on 11/23/17.
 */

public class Dinosaur extends DinosaurBase {

    @Nullable
    public static Dinosaur findById(long id) {
        List<Dinosaur> result = Select
                .target(Dinosaur.class)
                .from(DinosaurTable.class)
                .where(DinosaurTable.id.equalsTo(id))
                .start();

        return (result.size() == 1) ? result.get(0) : null;
    }

    public static List<Dinosaur> getAll() {
        return Select
                .target(Dinosaur.class)
                .from(DinosaurTable.class)
                .start();
    }

    private Period mPeriod; // cache

    public Dinosaur() {
        mPeriod = Period.from(super.period);
    }

    // Id is read-only
    public long getId() {
        return super.id;
    }

    public void setName(@NonNull String name) {
        super.name = name;
    }

    public String getName() {
        return super.name;
    }

    public void setPeriod(@NonNull Period period) {
        mPeriod = period;
        super.period = period.getId();
    }

    public Period getPeriod() {
        return mPeriod;
    }

    public void setIsLiked(boolean isLiked) {
        super.isLiked = isLiked;
    }

    public boolean isLiked() {
        return super.isLiked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Dinosaur dinosaur = (Dinosaur) o;

        return mPeriod == dinosaur.mPeriod;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mPeriod != null ? mPeriod.hashCode() : 0);
        return result;
    }
}