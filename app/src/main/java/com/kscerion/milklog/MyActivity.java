package com.kscerion.milklog;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.kscerion.milklog.data.DBContract;

public class MyActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_MESSAGE = "com.kscerion.milklog.MESSAGE";
    SimpleCursorAdapter mAdapter;
    static final String[] PROJECTION = {DBContract.TestTab.COLUMN_NAME_NAME};
    private String mSelection="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ListView view = (ListView) findViewById(R.id.list);

        Intent intent = this.getIntent();
        if(intent != null) {
            String id = intent.getStringExtra(EXTRA_MESSAGE);
            if(id != null && !"".equals(id)) {
                mSelection = "(_id="+id+")";
            }
        }

        String[] fromColumns = {DBContract.Users._ID};
        int[] toViews = {R.id.name_view};

        mAdapter = new SimpleCursorAdapter(this, R.layout.list_item, null, fromColumns, toViews);
        view.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(parent.getContext(), DetailList.class);
                String message = ((Cursor)parent.getItemAtPosition(position)).getString(0);
                intent.putExtra(DetailList.ANOTHER_MESSAGE, message);
                startActivity(intent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), CreateActivity.class));
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[] {DBContract.Users._ID};
//        String selection = "(1=1)";
        return new CursorLoader(this, DBContract.Users.CONTENT_URI, projection, mSelection, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        System.out.println(data.getCount());
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


}
