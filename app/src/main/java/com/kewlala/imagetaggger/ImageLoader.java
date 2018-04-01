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
    private Context mContext;

    public ImageLoader(Context context) {
        super(context);
        Log.d(LOG_TAG, "ImageLoader::begin constructor");
        mContext = context;
        Log.d(LOG_TAG, "ImageLoader::end constructor");
    }

    private AppDatabase db;
    @Override
    public List<ImageEntity> loadInBackground() {
        Log.d(LOG_TAG, "loadInBackground :: start");

        db = AppDatabase.getInstance(mContext);

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
}