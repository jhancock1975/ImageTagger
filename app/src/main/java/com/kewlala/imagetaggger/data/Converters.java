package com.kewlala.imagetaggger.data;

import android.arch.persistence.room.TypeConverter;

import java.sql.Date;

/**
 * Created by jhancock2010 on 3/4/18.
 *
 * copied from https://developer.android.com/training/data-storage/room/referencing-data.html
 */

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}