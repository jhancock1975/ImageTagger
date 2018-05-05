package com.kewlala.imagetaggger.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.util.Log;

/**
 * Created by jhancock2010 on 3/4/18.
 */

@Database(entities = {ImageEntity.class, ClassificationEntity.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static String LOG_TAG = AppDatabase.class.getSimpleName();

    private static AppDatabase INSTANCE;

    public abstract ImageEntityDao imageEntityDao();
    public abstract ClassificationEntityDao classificationEntityDao();

    public static AppDatabase getInstance(Context context) {
        Log.d(LOG_TAG, "getInstance::start");
        Log.d(LOG_TAG, "context = " + context.toString());

        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                            "ImageTagger")
                            .addMigrations(MIGRATION_1_2)
                            .addMigrations(MIGRATION_2_3)
                            .build();
        }
        Log.d(LOG_TAG, "instance = " + INSTANCE);
        Log.d(LOG_TAG, "getInstance::end");
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    static void rebuildImageEntity(SupportSQLiteDatabase database){
        database.execSQL("create table Image2 (imageId INTEGER not null primary key, filePath TEXT,"
                + " sha256 text, created INTEGER)");
        database.execSQL("insert into Image2 (imageId, filePath, sha256, created) "
                + "select imageId, filePath, sha256,created from ImageEntity");
        database.execSQL("drop table ImageEntity");
        database.execSQL("alter table Image2 rename to ImageEntity");
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            rebuildImageEntity(database);
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            rebuildImageEntity(database);
            database.execSQL("create table Classifications " +
                    "(classificationId INTEGER not null primary key, imageId INTEGER not null,  " +
                    "category TEXT, probability REAL not null, created INTEGER, " +
                     "constraint imageId foreign key(imageId) references ImageEntity(imageId) on delete cascade on update cascade)");
        }
    };
}