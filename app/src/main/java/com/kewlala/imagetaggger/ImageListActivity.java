package com.kewlala.imagetaggger;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kewlala.imagetaggger.data.ImageEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Images. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ImageDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ImageListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<ImageEntity>> {
    public static final int IMAGE_LIST_ACTIVITY_ID=1;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private static String LOG_TAG = "ImageListActivity";
    private static final int READ_REQUEST_CODE = 42;


    private View mRecyclerView;
    private SimpleItemRecyclerViewAdapter mViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreate::start");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "onClick :: start - onclick of FAB");
                // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
                // browser.
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                // Filter to only show results that can be "opened", such as a
                // file (as opposed to a list of contacts or timezones)
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Filter to show only images, using the image MIME data type.
                // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
                // To search for all documents available via installed storage providers,
                // it would be "*/*".
                intent.setType("image/*");

                startActivityForResult(intent, READ_REQUEST_CODE);

                Log.d(LOG_TAG, "onClick :: end - onclick of FAB");
            }
        });

        if (findViewById(R.id.image_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        getLoaderManager().initLoader(IMAGE_LIST_ACTIVITY_ID, null, this).forceLoad();

        mRecyclerView = findViewById(R.id.image_list);
        assert mRecyclerView != null;
        setupRecyclerView((RecyclerView) mRecyclerView);

        Log.d(LOG_TAG, "onCreate::end");
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mViewAdapter  = new SimpleItemRecyclerViewAdapter(this,
                new ArrayList<ImageEntity>(), mTwoPane);
        recyclerView.setAdapter(mViewAdapter);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {


        private List<ImageEntity> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageEntity item  = (ImageEntity) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ImageDetailFragment.ARG_ITEM_ID, item.getFilePath());
                    ImageDetailFragment fragment = new ImageDetailFragment();
                    fragment.setArguments(arguments);

                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ImageDetailActivity.class);
                    intent.putExtra(ImageDetailFragment.ARG_ITEM_ID, item.getFilePath());

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(ImageListActivity parent,
                                      List<ImageEntity> items,
                                      boolean twoPane) {
            mValues = items;
            mTwoPane = twoPane;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).getImageId()+"");
            holder.mContentView.setText(mValues.get(position).getFilePath());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
        public void setImageList(List<ImageEntity> imageList){
            mValues = imageList;
        }
    }

    @Override
    public Loader<List<ImageEntity>> onCreateLoader(int id, Bundle args) {
            return new ImageLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<ImageEntity>> loader, List<ImageEntity> data) {
        mViewAdapter.setImageList(data);
    }

    @Override
    public void onLoaderReset(Loader<List<ImageEntity>> loader) {
        mViewAdapter.setImageList(new ArrayList<ImageEntity>());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Log.d(LOG_TAG, "onActivityResult:: Received an \"Activity Result\"");
        // BEGIN_INCLUDE (parse_open_document_response)
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.d(LOG_TAG, "Uri: " + uri.toString());
                new ImageProcessor(this).process(uri);
            }
            Log.d(LOG_TAG, "onActivityResult:: end");
        }
    }
}