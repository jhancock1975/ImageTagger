package com.kewlala.imagetaggger;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.kewlala.imagetaggger.data.ImageEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Created by jhancock2010 on 3/25/18.
 */

public class ImageProcessor implements   LoaderManager.LoaderCallbacks<ImageEntity>{

    public static final int IMAGE_PROCESSOR_ID=2;
    private Activity mActivity;
    private Uri mImageUri;
    private static final String LOG_TAG= ImageProcessor.class.getSimpleName();
    public ImageProcessor(Activity activity){
        mActivity = activity;
    }
    public void process(Uri uri){
        Log.d(LOG_TAG, "process::start");
        Log.d(LOG_TAG, "Uri: " + uri);
        mImageUri = uri;
        mActivity.getLoaderManager().initLoader(
                IMAGE_PROCESSOR_ID, null, this).forceLoad();
        Log.d(LOG_TAG, "process::end");
    }




    @Override
    public Loader<ImageEntity> onCreateLoader(int id, Bundle args) {
        return new ImageService(mActivity.getBaseContext(), mImageUri);
    }

    @Override
    public void onLoadFinished(Loader<ImageEntity> loader, ImageEntity data) {

    }

    @Override
    public void onLoaderReset(Loader<ImageEntity> loader) {

    }
}
