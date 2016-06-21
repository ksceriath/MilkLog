package com.kscerion.milklog;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.kscerion.milklog.data.DBContract;

import java.util.Arrays;
import java.util.Calendar;

public class DetailList extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter mAdapter;

    public static final String ANOTHER_MESSAGE = "com.kscerion.milklog.ANOTHER_MESSAGE";

    public static String[] sMonths = { "Jan","Feb","Mar","Apr",
                                        "May","Jun","Jul","Aug",
                                        "Sep","Oct","Nov","Dec"};

    private int mYear;
    private int mMonth;
    private TextView mTotalQty;

    static final String[] PROJECTION = {DBContract.TestTab.COLUMN_NAME_NAME};

    private String mSelection="";
    private String[] mSelectionArgs = new String[2];
    private String mUserId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_list);

        mTotalQty = (TextView) findViewById(R.id.total_milk);

        ListView dailyList = (ListView) findViewById(R.id.list);
        ProgressBar progressBar = new ProgressBar(this);

        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        dailyList.setEmptyView(progressBar);
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        Intent intent = this.getIntent();
        if(intent != null) {
            mUserId = intent.getStringExtra(DetailList.ANOTHER_MESSAGE);
            if(mUserId != null && !"".equals(mUserId)) {
                Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mSelection = DBContract.MonthLogs.C_MONTH + " = ? AND " + DBContract.MonthLogs.C_USER_ID + " = ?";
            }
        }

        String[] fromColumns = {DBContract.MonthLogs.C_DATE, DBContract.MonthLogs.C_MNG_QTY, DBContract.MonthLogs.C_EVE_QTY};
        int[] toViews = {R.id.date_view, R.id.morning_view, R.id.evening_view};

        mAdapter = new SimpleCursorAdapter(this, R.layout.daily_item, null, fromColumns, toViews);
        dailyList.setAdapter(mAdapter);


        getLoaderManager().initLoader(0, null, this);

        dailyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, final long id) {
                final ViewGroup viewGroup = (ViewGroup)getLayoutInflater().inflate(R.layout.edit_date, null);
                EditText mQty = (EditText)viewGroup.findViewById(R.id.mngQty);
                EditText eQty = (EditText)viewGroup.findViewById(R.id.engQty);
                mQty.setText(((TextView)view.findViewById(R.id.morning_view)).getText());
                mQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus) {
                            String qty = ((EditText)v).getText().toString();
                            ((TextView)view.findViewById(R.id.morning_view)).setText(qty);
                            TextView tv1 = (TextView)viewGroup.findViewById(R.id.engQty);
                            qty = tv1.getText().toString();
                            ((TextView)view.findViewById(R.id.evening_view)).setText(qty);
                        }
                    }
                });

                eQty.setText(((TextView)view.findViewById(R.id.evening_view)).getText());
                eQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus) {
                            String qty = ((EditText)v).getText().toString();
                            ((TextView)view.findViewById(R.id.evening_view)).setText(qty);
                            TextView tv1 = (TextView)viewGroup.findViewById(R.id.mngQty);
                            qty = tv1.getText().toString();
                            ((TextView)view.findViewById(R.id.morning_view)).setText(qty);
                        }
                    }
                });

                int width = (int)(parent.getWidth()*0.75);
                PopupWindow popupWindow = new PopupWindow(viewGroup,width,400,true);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        String date = ((TextView)view.findViewById(R.id.date_view)).getText().toString();
                        String mQty = ((TextView)view.findViewById(R.id.morning_view)).getText().toString();
                        String eQty = ((TextView)view.findViewById(R.id.evening_view)).getText().toString();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBContract.MonthLogs.C_MNG_QTY,mQty);
                        contentValues.put(DBContract.MonthLogs.C_EVE_QTY,eQty);
                        contentValues.put(DBContract.MonthLogs.C_USER_ID,mSelectionArgs[1]);
                        contentValues.put(DBContract.MonthLogs.C_DATE,date);
                        contentValues.put(DBContract.MonthLogs.C_MONTH,mSelectionArgs[0]);
                        new AsyncUpdater().execute(contentValues);
                    }
                });
                popupWindow.showAtLocation(findViewById(R.id.zoomba),Gravity.NO_GRAVITY,parent.getWidth()/2-width/2,parent.getHeight()/2);
            }
        });
        Spinner monthSpinner = (Spinner) findViewById(R.id.month);
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this, R.array.months, android.R.layout.simple_spinner_item);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setSelection(mMonth-1);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(view != null) {
                    mMonth=position+1;
                    getLoaderManager().restartLoader(0,null,(DetailList)parent.getContext());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner yearSpinner = (Spinner) findViewById(R.id.year);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
        yearAdapter.addAll(Arrays.asList(new String[] {"2015","2016","2017"}));
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setSelection(1);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(view != null) {
                    mYear = Integer.parseInt(((TextView)view).getText().toString());
                    getLoaderManager().restartLoader(0,null,(DetailList)parent.getContext());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id==0) {
            String[] projection = new String[] {DBContract.MonthLogs._ID,DBContract.MonthLogs.C_DATE, DBContract.MonthLogs.C_MNG_QTY, DBContract.MonthLogs.C_EVE_QTY};
            mSelection = "(" + mSelection + ")";
            mSelectionArgs[0] = mYear+(mMonth<10?"0":"")+mMonth;
            mSelectionArgs[1] = mUserId;
            return new CursorLoader(this, DBContract.MonthLogs.CONTENT_URI, projection, mSelection, mSelectionArgs, null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        System.out.println(data.getCount());
        int total = 0;
        while(data.moveToNext()) {
            total = total + data.getInt(2)+data.getInt(3);
        }
        mTotalQty.setText(""+total);
        System.out.println(total);
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private class AsyncUpdater extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues[] contentValues1) {
            ContentValues content = contentValues1[0];
            String selection = DBContract.MonthLogs.C_MONTH+"=? and "
                    +DBContract.MonthLogs.C_USER_ID+"=? and "
                    +DBContract.MonthLogs.C_DATE+"=?";
            String[] selectionArgs = {(String)content.get(DBContract.MonthLogs.C_MONTH),
                    (String)content.get(DBContract.MonthLogs.C_USER_ID), (String)content.get(DBContract.MonthLogs.C_DATE)};
            getContentResolver().update(DBContract.MonthLogs.CONTENT_URI,content,selection,selectionArgs);
            return null;
        }
    }
}
