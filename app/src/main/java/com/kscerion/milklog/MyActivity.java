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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.kscerion.milklog.data.DBContract;

public class MyActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_MESSAGE = "com.kscerion.milklog.MESSAGE";
    SimpleCursorAdapter mAdapter;
    private String mSelection="";
    public View mSelectedItem;

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

        LinearLayout spaceTaker = (LinearLayout) findViewById(R.id.space_taker);
        spaceTaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedItem!=null) {
                    mSelectedItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    mSelectedItem = null;
                }
                ((FloatingActionButton) findViewById(R.id.create)).setVisibility(View.VISIBLE);
                ((FloatingActionButton) findViewById(R.id.delete)).setVisibility(View.INVISIBLE);
                ((FloatingActionButton) findViewById(R.id.edit)).setVisibility(View.INVISIBLE);
            }
        });

        String[] fromColumns = {DBContract.Users.C_NAME, DBContract.Users.C_ADDRESS};
        int[] toViews = {R.id.name_view,R.id.addr_view};

        mAdapter = new SimpleCursorAdapter(this, R.layout.list_item, null, fromColumns, toViews);
        view.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mSelectedItem!=null) {
                    mSelectedItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    ((FloatingActionButton) findViewById(R.id.create)).setVisibility(View.VISIBLE);
                    ((FloatingActionButton) findViewById(R.id.delete)).setVisibility(View.INVISIBLE);
                    ((FloatingActionButton) findViewById(R.id.edit)).setVisibility(View.INVISIBLE);
                    mSelectedItem = null;
                }
                Intent intent = new Intent(parent.getContext(), DetailList.class);
                String userId = ((Cursor)parent.getItemAtPosition(position)).getString(0);
                String userName = ((Cursor)parent.getItemAtPosition(position)).getString(1);
                intent.putExtra(DetailList.USER_ID, userId);
                intent.putExtra(DetailList.USER_NAME, userName);
                startActivity(intent);
            }
        });


        view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, final long id) {
                if(mSelectedItem!=null) {
                    mSelectedItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    ((FloatingActionButton) findViewById(R.id.create)).setVisibility(View.VISIBLE);
                    ((FloatingActionButton) findViewById(R.id.delete)).setVisibility(View.INVISIBLE);
                    ((FloatingActionButton) findViewById(R.id.edit)).setVisibility(View.INVISIBLE);
                }
                if(mSelectedItem !=  view) {
                    view.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    mSelectedItem = view;
//                view.setSelected(true);
                    ((FloatingActionButton) findViewById(R.id.create)).setVisibility(View.INVISIBLE);
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.delete);
                    fab.setVisibility(View.VISIBLE);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getContentResolver().delete(DBContract.Users.CONTENT_URI, DBContract.Users._ID+"="+id,null);
                            getContentResolver().delete(DBContract.MonthLogs.CONTENT_URI, DBContract.MonthLogs.C_USER_ID+"="+id,null);
                            ((FloatingActionButton) findViewById(R.id.create)).setVisibility(View.VISIBLE);
                            ((FloatingActionButton) findViewById(R.id.delete)).setVisibility(View.INVISIBLE);
                            ((FloatingActionButton) findViewById(R.id.edit)).setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(),"Customer Deleted.",Toast.LENGTH_SHORT).show();
                        }
                    });
                    fab = (FloatingActionButton) findViewById(R.id.edit);
                    fab.setVisibility(View.VISIBLE);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelectedItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                            ((FloatingActionButton) findViewById(R.id.create)).setVisibility(View.VISIBLE);
                            ((FloatingActionButton) findViewById(R.id.delete)).setVisibility(View.INVISIBLE);
                            ((FloatingActionButton) findViewById(R.id.edit)).setVisibility(View.INVISIBLE);
                            mSelectedItem = null;
                            Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
                            String userAddr = ((Cursor)parent.getItemAtPosition(position)).getString(2);
                            String userName = ((Cursor)parent.getItemAtPosition(position)).getString(1);
                            intent.putExtra(CreateActivity.USER_ID,""+id);
                            intent.putExtra(CreateActivity.USER_NAME,userName);
                            intent.putExtra(CreateActivity.USER_ADDRESS,userAddr);
                            startActivity(intent);
//                            Toast.makeText(getApplicationContext(),"Edit functionality not yet available.",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mSelectedItem = null;
                }
//                TextView delete = new TextView(parent.getContext());
//                delete.setText("DELETE");
//                delete.setBackgroundColor(Color.WHITE);
//                delete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                });
//                ViewGroup deletePop = new RelativeLayout(parent.getContext());
//                deletePop.addView(delete);
//                PopupWindow popupWindow = new PopupWindow(deletePop,400,400);
//                int[] location = new int[2];
//                view.getLocationOnScreen(location);
//                popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY,parent.getWidth()-deletePop.getWidth(),location[1]);
                return true;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.create);
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
        String[] projection = new String[] {DBContract.Users._ID,DBContract.Users.C_NAME, DBContract.Users.C_ADDRESS};
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
