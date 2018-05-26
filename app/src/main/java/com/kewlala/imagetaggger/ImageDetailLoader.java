package com.kewlala.imagetaggger;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.kewlala.imagetaggger.data.AppDatabase;
import com.kewlala.imagetaggger.data.ClassificationEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhancock2010 on 5/12/18.
 */

class ImageDetailLoader extends AsyncTaskLoader<List<ClassificationEntity>> {
    public static final String LOG_TAG = ImageDetailLoader.class.getSimpleName();
    private Context mContext;
    private AppDatabase mDb;
    public ImageDetailLoader(Context context) {
        super(context);
        Log.d(LOG_TAG, "ImageLoader::begin constructor");
        this.mContext = mContext;
        Log.d(LOG_TAG, "ImageLoader::end constructor");
    }

    @Override
    public List<ClassificationEntity> loadInBackground() {
        Log.d(LOG_TAG, "loadInBackground::start");
        mDb = AppDatabase.getInstance(mContext);
        List<ClassificationEntity> classificationEntityList = new ArrayList<ClassificationEntity>();

        Log.d(LOG_TAG, "loadInBackground::end");
        return null;
    }
}
