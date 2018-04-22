package com.kewlala.imagetaggger;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kewlala.imagetaggger.data.AppDatabase;
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

    /*TODO: jhancock1975@gmail.com replace progress dialog with progress bar*/
    private ProgressBar mProgressBar;
    private ProgressDialog mProgressDialog;

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

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_tagger_main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected::start");
        Log.d(LOG_TAG, "item.getTitle() " + item.getTitle());
        View v;
        if (item.getTitle().equals(getString(R.string.action_edit_list))) {
            item.setTitle(R.string.title_menu_delete);
            for (ImageEntity e : mViewAdapter.getImageList()) {
                Log.d(LOG_TAG, e.toString());
                v = mRecyclerView.findViewWithTag(e);
                if (v != null) {
                    v.findViewById(R.id.id_radio_delete_select).setVisibility(View.VISIBLE);
                }
            }
        } else {
            item.setTitle(R.string.action_edit_list);
            List<ImageEntity> deleteList = new ArrayList<ImageEntity>(3);
            for (ImageEntity e : mViewAdapter.getImageList()) {
                Log.d(LOG_TAG, e.toString());
                v = mRecyclerView.findViewWithTag(e);
                Log.d(LOG_TAG, "v = " + v);
                if (v != null) {
                    RadioButton rb = (RadioButton) v.findViewById(R.id.id_radio_delete_select);
                    Log.d(LOG_TAG, "rb = " + rb);
                    if (((RadioButton) v.findViewById(R.id.id_radio_delete_select)).isChecked()){
                        Log.d(LOG_TAG, "deleting image...");
                        deleteList.add(e);
                    }
                }
            }
            //can't delete on main thread
            //using async task example: http://www.jithin88.com/2012/05/async-task-in-android.html
            //would probably be better to use Cursor loader for this list
            //https://developer.android.com/guide/components/loaders.html
            if (deleteList.size() > 0){
                SyncIncoData task = new SyncIncoData(this);
                task.execute(((ImageEntity[]) deleteList.toArray(new ImageEntity[deleteList.size()])));

            }
            //redraw list after deleting items from db
            getLoaderManager().initLoader(IMAGE_LIST_ACTIVITY_ID, null, this).forceLoad();
            //getmViewAdapter().notifyDataSetChanged();
        }
        return true;
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
        public List<ImageEntity> getImageList(){
            return mValues;
        }
    }

    @Override
    public Loader<List<ImageEntity>> onCreateLoader(int id, Bundle args) {
            return new ImageLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<ImageEntity>> loader, List<ImageEntity> data) {
        Log.d(LOG_TAG, "onLoadFinished::start");
        mViewAdapter.setImageList(data);
        getmViewAdapter().notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<ImageEntity>> loader) {
        Log.d(LOG_TAG, "onLoaderReset::start");
        mViewAdapter.setImageList(new ArrayList<ImageEntity>());
        getmViewAdapter().notifyDataSetChanged();
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
                String uriStr = uri.toString();

                mProgressDialog = ProgressDialog.show(this, "Classifying Image",
                        "Please wait...", true);

                new ImageProcessor(this).process(uri,mViewAdapter.getImageList()) ;
            }
            Log.d(LOG_TAG, "onActivityResult:: end");
        }
    }

    public SimpleItemRecyclerViewAdapter getmViewAdapter() {
        return mViewAdapter;
    }

    public ProgressDialog getProgressDialog(){
        return mProgressDialog;
    }
}
