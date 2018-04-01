package com.kewlala.imagetaggger;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.kewlala.imagetaggger.data.AppDatabase;
import com.kewlala.imagetaggger.data.ImageEntity;

import java.util.List;

/**
 * Created by jhancock2010 on 3/25/18.
 */

public class ImageProcessor implements   LoaderManager.LoaderCallbacks<ImageEntity>{

    private static final String LOG_TAG= ImageProcessor.class.getSimpleName();
    public static final int IMAGE_PROCESSOR_ID=2;

    private Activity mActivity;
    private Uri mImageUri;
    private  List<ImageEntity> mImageList;

    /**
     * constructor
     * @param activity - hook back to calling activity
     */
    public ImageProcessor(Activity activity){
        mActivity = activity;
    }

    /**
     * sends the image uri points to, to the Clarifai image categorization service
     * @param uri - a uri pointing to some image on the device
     */
    public void process(Uri uri, List<ImageEntity> imageList){

        Log.d(LOG_TAG, "process::start");
        Log.d(LOG_TAG, "Uri: " + uri);

        mImageUri = uri;
        mImageList = imageList;

        mActivity.getLoaderManager().initLoader(
                IMAGE_PROCESSOR_ID, null, this).forceLoad();

        Log.d(LOG_TAG, "process::end");
    }

    @Override
    public Loader<ImageEntity> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader::start");
        return new ImageService(mActivity.getBaseContext(), mImageUri);
    }

    @Override
    public void onLoadFinished(Loader<ImageEntity> loader, ImageEntity data) {
        Log.d(LOG_TAG, "onLoaderFinished::start");
        Log.d(LOG_TAG, "sha256 = " + data.getSha256());
        mImageList.add(data);
        //tell the view to redraw with new item
        ((ImageListActivity) mActivity).getmViewAdapter().notifyDataSetChanged();
        Log.d(LOG_TAG, "onLoaderFinished::end");

    }

    @Override
    public void onLoaderReset(Loader<ImageEntity> loader) {
        Log.d(LOG_TAG, "onLoaderReset::start");
    }
}
