package com.kewlala.imagetaggger;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.kewlala.imagetaggger.data.AppDatabase;
import com.kewlala.imagetaggger.data.ImageEntity;

import java.util.List;

/**
 * Created by jhancock2010 on 4/21/18.
 */

public class SyncIncoData extends AsyncTask<ImageEntity, ImageEntity, ImageEntity> {

    SyncIncoData(Context context){
        this.mContext = context;
    }
    private AppDatabase db;
    private Context mContext;


    @Override
    protected ImageEntity doInBackground(ImageEntity... imageEntities) {
        db = AppDatabase.getInstance(mContext);
        for (ImageEntity e: imageEntities){
            db.imageEntityDao().delete(e);
        }
        return null;
    }
}