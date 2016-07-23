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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class DetailList extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String USER_ID = "com.kscerion.milklog.USER_ID";
    public static final String USER_NAME = "com.kscerion.milklog.USER_NAME";

    SimpleCursorAdapter mAdapter;
    ListView mDailyListView;

    private Collection<String> mAvailableYears;

    private int mYear;
    private int mMonth;
    private int mStartYear = 2000;

    private TextView mTotalQty;

    private String mSelection="";
    private String[] mSelectionArgs = new String[2];
    private String mUserId="";
    private String mUserName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_list);

        mTotalQty = (TextView) findViewById(R.id.total_milk);

        mDailyListView = (ListView) findViewById(R.id.list);
        ProgressBar progressBar = new ProgressBar(this);

        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        mDailyListView.setEmptyView(progressBar);
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        Intent intent = this.getIntent();
        if(intent != null) {
            mUserId = intent.getStringExtra(DetailList.USER_ID);
            mUserName = intent.getStringExtra(DetailList.USER_NAME);
            if(mUserId != null && !"".equals(mUserId)) {
                Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH)+1;
                mSelection = DBContract.MonthLogs.C_MONTH + " = ? AND " + DBContract.MonthLogs.C_USER_ID + " = ?";
            }
        }

        ((TextView)root.findViewById(R.id.user_name)).setText(mUserName);

        String[] fromColumns = {DBContract.MonthLogs.C_DATE, DBContract.MonthLogs.C_MNG_QTY, DBContract.MonthLogs.C_EVE_QTY};
        int[] toViews = {R.id.date_view, R.id.morning_view, R.id.evening_view};

        mAdapter = new SimpleCursorAdapter(this, R.layout.daily_item, null, fromColumns, toViews);
        mDailyListView.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);

        mDailyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, final long id) {
                final ViewGroup viewGroup = (ViewGroup)getLayoutInflater().inflate(R.layout.edit_date, null);
                EditText mQty = (EditText)viewGroup.findViewById(R.id.mngQty);
                EditText eQty = (EditText)viewGroup.findViewById(R.id.engQty);
                mQty.setText(((TextView)view.findViewById(R.id.morning_view)).getText());
                eQty.setText(((TextView)view.findViewById(R.id.evening_view)).getText());

                ((TextView)viewGroup.findViewById(R.id.edit_date_date))
                        .setText(getResources().getStringArray(R.array.months)[mMonth-1]+" "+
                                ((TextView)view.findViewById(R.id.date_view)).getText());

                int width = (int)(parent.getWidth()*0.75);
                int height = (int)(2*view.getHeight());
                final PopupWindow popupWindow = new PopupWindow(viewGroup,width,height,true);

                viewGroup.findViewById(R.id.OK_edit_date).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String date = ((TextView)view.findViewById(R.id.date_view)).getText().toString();
                        String mQty = ((EditText)viewGroup.findViewById(R.id.mngQty)).getText().toString();
                        String eQty = ((EditText)viewGroup.findViewById(R.id.engQty)).getText().toString();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBContract.MonthLogs.C_MNG_QTY,mQty);
                        contentValues.put(DBContract.MonthLogs.C_EVE_QTY,eQty);
                        contentValues.put(DBContract.MonthLogs.C_USER_ID,mSelectionArgs[1]);
                        contentValues.put(DBContract.MonthLogs.C_DATE,date);
                        contentValues.put(DBContract.MonthLogs.C_MONTH,mSelectionArgs[0]);
                        new AsyncUpdater().execute(contentValues);
                        popupWindow.dismiss();
                    }
                });

                viewGroup.findViewById(R.id.CANCEL_edit_date).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
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
        yearAdapter.addAll(getAvailableYears());
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setSelection(Calendar.getInstance().get(Calendar.YEAR)-mStartYear);
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
        if(data.getCount()==0) {
            int month = Integer.parseInt(mSelectionArgs[0].substring(4));
            int year = Integer.parseInt(mSelectionArgs[0].substring(0,4));
            int days = 0;
            switch(month) {
                case 1:case 3:case 5:case 7:case 8:case 10:case 12:
                    days = 31;
                    break;
                case 4:case 6:case 9:case 11:
                    days = 30;
                    break;
                case 2:
                    days = (year%4 == 0) ? 29 : 28;
                    break;
            }
            ContentValues[] values = new ContentValues[days];
            for(int i=1; i<=days; i++) {
                String date = (i/10==0) ? "0" : "";
                date = date + i;
                values[i-1] = new ContentValues();
                values[i-1].put(DBContract.MonthLogs.C_MONTH, mSelectionArgs[0]);
                values[i-1].put(DBContract.MonthLogs.C_USER_ID, mSelectionArgs[1]);
                values[i-1].put(DBContract.MonthLogs.C_DATE, date);
            }
            getContentResolver().bulkInsert(DBContract.MonthLogs.CONTENT_URI, values);
        }
        double total = 0;
        while(data.moveToNext()) {
            total = total + data.getDouble(2)+data.getDouble(3);
        }
        mTotalQty.setText(""+total);
        System.out.println(total);
        mAdapter.swapCursor(data);
        mDailyListView.setSelectionFromTop(Calendar.getInstance().get(Calendar.DATE)-1,mDailyListView.getHeight()/2);
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

    private Collection<String> getAvailableYears() {
        if(mAvailableYears==null) {
            int lastYear = 5 + Calendar.getInstance().get(Calendar.YEAR);
            mAvailableYears = new ArrayList<>();
            for(int i=mStartYear; i<=lastYear; i++) {
                mAvailableYears.add(""+i);
            }
        }
        return mAvailableYears;
    }
}
