package com.kewlala.imagetaggger;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kewlala.imagetaggger.data.ClassificationEntity;
import com.kewlala.imagetaggger.data.ImageEntity;

import java.util.List;

/**
 * A fragment representing a single Image detail screen.
 * This fragment is either contained in a {@link ImageListActivity}
 * in two-pane mode (on tablets) or a {@link ImageDetailActivity}
 * on handsets.
 */
public class ImageDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ClassificationEntity>>{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_PATH = "item_path";
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_OBJ = "item_object";
    public static final String LOG_TAG = ImageDetailFragment.class.getSimpleName();

    /**
     * The dummy content this fragment is presenting.
     */
    private ImageEntity mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ImageDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate::start");
        Object item = getArguments().get(ARG_ITEM_OBJ);
        Log.d(LOG_TAG, "item = " + item);
        
        if (getArguments().containsKey(ARG_ITEM_PATH)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = new ImageEntity();

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("Placeholder Title");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.image_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.image_detail)).setText("Placeholder image item detail");
        }

        return rootView;
    }

    @Override
    public Loader<List<ClassificationEntity>> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<ClassificationEntity>> loader, List<ClassificationEntity> data) {

    }

    @Override
    public void onLoaderReset(Loader<List<ClassificationEntity>> loader) {

    }
}
