package com.kscerion.milklog;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
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
import com.kscerion.milklog.data.DBHelper;
import com.kscerion.milklog.util.FilterCursorWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MyActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_MESSAGE = "com.kscerion.milklog.MESSAGE";
    SimpleCursorAdapter mAdapter;
    private String mSelection="";
    public View mSelectedItem;
    private boolean mImportStatus = false;
    private Cursor mFilterCursor;

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
                    ((FloatingActionButton) findViewById(R.id.create)).setVisibility(View.INVISIBLE);
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.delete);
                    fab.setVisibility(View.VISIBLE);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("Delete Customer.", "Are you sure?", null, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getContentResolver().delete(DBContract.Users.CONTENT_URI, DBContract.Users._ID + "=" + id, null);
                                    getContentResolver().delete(DBContract.MonthLogs.CONTENT_URI, DBContract.MonthLogs.C_USER_ID + "=" + id, null);
                                    ((FloatingActionButton) findViewById(R.id.create)).setVisibility(View.VISIBLE);
                                    ((FloatingActionButton) findViewById(R.id.delete)).setVisibility(View.INVISIBLE);
                                    ((FloatingActionButton) findViewById(R.id.edit)).setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), "Customer Deleted.", Toast.LENGTH_SHORT).show();
                                }
                            });
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
                        }
                    });
                } else {
                    mSelectedItem = null;
                }
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

    public void showDialog(String title, String message, DialogInterface.OnClickListener noClick, DialogInterface.OnClickListener yesClick) {
        new AlertDialog.Builder(MyActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(android.R.string.no, noClick)
                .setPositiveButton(android.R.string.yes, yesClick)
                .create()
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my, menu);
        SearchView searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.swapCursor(new FilterCursorWrapper(((CursorWrapper)mFilterCursor).getWrappedCursor(),query));
                mAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.swapCursor(new FilterCursorWrapper(((CursorWrapper)mFilterCursor).getWrappedCursor(),newText));
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });
        searchView.setQueryHint("Search here");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_import) {
            showDialog("Import File.", "All existing records will be overwritten. Continue?", null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    importDBFile();
                    if(mImportStatus) {
                        getApplicationContext().getContentResolver().notifyChange(DBContract.Users.CONTENT_URI,null);
                        Toast.makeText(getApplicationContext(), "File Imported.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Import Failed!!ll", Toast.LENGTH_SHORT).show();
                    }
                    mImportStatus = false;
                }
            });
            return true;
        } else if (id == R.id.action_export) {
            if(exportDBFile()) {
                Toast.makeText(getApplicationContext(), "File Exported.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Export Failed!!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void importDBFile() {
        File dir = new File(Environment.getExternalStorageDirectory(), "MilkLog");
        File importFrom = new File(dir, DBHelper.DATABASE_NAME);
        if(!importFrom.exists()) {
            mImportStatus =  false;
        } else {
            mImportStatus = fileTransfer(importFrom,DBHelper.DATABASE_FILE);
        }
    }

    public static boolean exportDBFile() {
        File dir = new File(Environment.getExternalStorageDirectory(), "MilkLog");
        if(!dir.exists()) {
            dir.mkdirs();
        }
        File exportTo = new File(dir,DBHelper.DATABASE_NAME);
        try {
            exportTo.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return fileTransfer(DBHelper.DATABASE_FILE,exportTo);
    }

    public static boolean fileTransfer(File from, File to) {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = new FileInputStream(from).getChannel();
            toChannel = new FileOutputStream(to).getChannel();
            fromChannel.transferTo(0,fromChannel.size(),toChannel);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fromChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                toChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[] {DBContract.Users._ID,DBContract.Users.C_NAME, DBContract.Users.C_ADDRESS};
        return new CursorLoader(this, DBContract.Users.CONTENT_URI, projection, mSelection, null, DBContract.Users.C_NAME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        System.out.println(data.getCount());
        mFilterCursor = new FilterCursorWrapper(data,"");
        mAdapter.swapCursor(mFilterCursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
