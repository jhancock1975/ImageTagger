package com.kewlala.imagetaggger;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.kewlala.imagetaggger.data.AppDatabase;
import com.kewlala.imagetaggger.data.ImageEntity;

import java.util.List;

/**
 * Created by jhancock2010 on 3/17/18.
 */

public class ImageLoader extends AsyncTaskLoader<List<ImageEntity>> {
    public static final String LOG_TAG = ImageLoader.class.getSimpleName();

    public ImageLoader(Context context) {
        super(context);
    }

    private AppDatabase db;
    @Override
    public List<ImageEntity> loadInBackground() {
        Log.d(LOG_TAG, "loadInBackground :: start");

        db = Room.databaseBuilder(getContext(),
                AppDatabase.class, "ImageTagger").addMigrations(MIGRATION_1_2 ).build();
        List<ImageEntity> imgList = null;
        if (db != null) {
            Log.d(LOG_TAG, db.toString());


            imgList = db.imageEntityDao().getAll();
            Log.d(LOG_TAG, "imgList = " + imgList);
            if (imgList != null) {
                for (ImageEntity i : imgList) {
                    Log.d(LOG_TAG, i.toString());
                }
            }

        }
        Log.d(LOG_TAG, "loadInBackground :: end");
        return imgList;
    }
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("create table Image2 (imageId INTEGER not null primary key, filePath TEXT,"
                    + " sha256 text, created INTEGER)");
            database.execSQL("insert into Image2 (imageId, filePath, sha256, created) "
                    + "select imageId, filePath, sha256,created from ImageEntity");
            database.execSQL("drop table ImageEntity");
            database.execSQL("alter table Image2 rename to ImageEntity");
        }
    };

    public void createNewImage(){
        ImageEntity imgEntity = new ImageEntity();
        imgEntity.setCreated(new java.sql.Date(System.currentTimeMillis()));
        imgEntity.setFilePath("test");
        imgEntity.setSha256("test");
        db.imageEntityDao().insertAll(imgEntity);
    }
}