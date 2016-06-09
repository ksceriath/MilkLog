package com.kscerion.milklog;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import com.kscerion.milklog.data.DBContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DetailList extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter mAdapter;

    public static final String ANOTHER_MESSAGE = "com.kscerion.milklog.ANOTHER_MESSAGE";

    static final String[] PROJECTION = {DBContract.TestTab.COLUMN_NAME_NAME};

    private String mSelection="";
    private String[] mSelectionArgs = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_list);

        ListView view = (ListView) findViewById(R.id.list);
        ProgressBar progressBar = new ProgressBar(this);

        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        view.setEmptyView(progressBar);
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        Intent intent = this.getIntent();
        if(intent != null) {
            String userId = intent.getStringExtra(DetailList.ANOTHER_MESSAGE);
            if(userId != null && !"".equals(userId)) {
                String month = new SimpleDateFormat("yyyyMM").format(Calendar.getInstance().getTime());
                mSelectionArgs[0] = month;
                mSelectionArgs[1] = userId;
                mSelection = DBContract.MonthLogs.C_MONTH + " = ? AND " + DBContract.MonthLogs.C_USER_ID + " = ?";
            }
        }

        String[] fromColumns = {DBContract.MonthLogs.C_DATE, DBContract.MonthLogs.C_MNG_QTY, DBContract.MonthLogs.C_EVE_QTY};
        int[] toViews = {R.id.date_view, R.id.morning_view, R.id.evening_view};

        mAdapter = new SimpleCursorAdapter(this, R.layout.daily_item, null, fromColumns, toViews){
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup v) {
                View x = super.newView(context,cursor,v);
                EditText editText = (EditText)x.findViewById(R.id.morning_view);
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus) {
                            System.out.println("WEEEEHEEEEE....!!!");
                        }
                    }
                });
                editText = (EditText)x.findViewById(R.id.evening_view);
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus) {
                            String qty = ((EditText)v).getText().toString();
                            System.out.println("WEEEEHEEEEE....!!!");
                        }
                    }
                });
                return x;
            }
        };
        view.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                EditText mng = ((EditText)view.findViewById(R.id.morning_view));
//                EditText eng = ((EditText)view.findViewById(R.id.evening_view));
//                mng.setInputType(InputType.TYPE_CLASS_NUMBER);
//                eng.setInputType(InputType.TYPE_CLASS_NUMBER);
//                mng.setClickable(true);
//                mng.setFocusable(true);
//                eng.setClickable(true);
//                eng.setFocusable(true);
//                eng.setText("0");
//                mng.setText("0");
//                Intent intent = new Intent(parent.getContext(), DetailList.class);
//                String message = String.valueOf(id);
//                intent.putExtra(ANOTHER_MESSAGE, message);
//                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[] {DBContract.MonthLogs._ID,DBContract.MonthLogs.C_DATE, DBContract.MonthLogs.C_MNG_QTY, DBContract.MonthLogs.C_EVE_QTY};
        mSelection = "(" + mSelection + ")";
        return new CursorLoader(this, DBContract.MonthLogs.CONTENT_URI, projection, mSelection, mSelectionArgs, null);
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
