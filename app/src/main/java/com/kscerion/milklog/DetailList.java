package com.kscerion.milklog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.kscerion.milklog.data.DBContract;

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

    static final String[] PROJECTION = {DBContract.TestTab.COLUMN_NAME_NAME};

    private String mSelection="";
    private String[] mSelectionArgs = new String[2];
    private String mUserId="";

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
            mUserId = intent.getStringExtra(DetailList.ANOTHER_MESSAGE);
            if(mUserId != null && !"".equals(mUserId)) {
                Calendar c = Calendar.getInstance();
                mYear = +c.get(Calendar.YEAR);
                mMonth =+c.get(Calendar.MONTH);
//                mDate = new SimpleDateFormat("yyyyMM").format(Calendar.getInstance().getTime());
                mSelection = DBContract.MonthLogs.C_MONTH + " = ? AND " + DBContract.MonthLogs.C_USER_ID + " = ?";
            }
        }

        ((Button)findViewById(R.id.date_pick)).setText(sMonths[mMonth]+" "+mYear);

        String[] fromColumns = {DBContract.MonthLogs.C_DATE, DBContract.MonthLogs.C_MNG_QTY, DBContract.MonthLogs.C_EVE_QTY};
        int[] toViews = {R.id.date_view, R.id.morning_view, R.id.evening_view};

        mAdapter = new SimpleCursorAdapter(this, R.layout.daily_item, null, fromColumns, toViews);//{
//            @Override
//            public View newView(Context context, Cursor cursor, ViewGroup v) {
//                View x = super.newView(context,cursor,v);
//                EditText editText = (EditText)x.findViewById(R.id.morning_view);
//                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View v, boolean hasFocus) {
//                        if(!hasFocus) {
//                            System.out.println("WEEEEHEEEEE....!!!");
//                        }
//                    }
//                });
//                editText = (EditText)x.findViewById(R.id.evening_view);
//                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View v, boolean hasFocus) {
//                        if(!hasFocus) {
//                            String qty = ((EditText)v).getText().toString();
//                            System.out.println("WEEEEHEEEEE....!!!");
//                        }
//                    }
//                });
//                return x;
//            }
//        };
        view.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        if(getContentResolver().insert(DBContract.MonthLogs.CONTENT_URI,contentValues)==null) {
                            String selection = DBContract.MonthLogs.C_MONTH+"=? and "
                                    +DBContract.MonthLogs.C_USER_ID+"=? and "
                                    +DBContract.MonthLogs.C_DATE+"=?";
                            String[] selectionArgs = {(String)contentValues.get(DBContract.MonthLogs.C_MONTH),
                                    (String)contentValues.get(DBContract.MonthLogs.C_USER_ID), (String)contentValues.get(DBContract.MonthLogs.C_DATE)};
                            getContentResolver().update(DBContract.MonthLogs.CONTENT_URI,contentValues,selection,selectionArgs);
                        }
                    }
                });
                popupWindow.showAtLocation(findViewById(R.id.zoomba),Gravity.NO_GRAVITY,parent.getWidth()/2-width/2,parent.getHeight()/2);
            }
        });
    }

    public void changeDate(View v) {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE,0);
        dialogFragment.show(getSupportFragmentManager(),"datepick");
    }

    public void setDate(int year,int month) {
        mYear = year;
        mMonth = month;
        ((Button)findViewById(R.id.date_pick)).setText(sMonths[month]+" "+year);
        getLoaderManager().restartLoader(0,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[] {DBContract.MonthLogs._ID,DBContract.MonthLogs.C_DATE, DBContract.MonthLogs.C_MNG_QTY, DBContract.MonthLogs.C_EVE_QTY};
        mSelection = "(" + mSelection + ")";
        mSelectionArgs[0] = ""+mYear+mMonth;
        mSelectionArgs[1] = mUserId;
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

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(), this, 2000,1,1);
        }

        public void onDateSet(DatePicker datePicker, int year, int month, int date) {
            ((DetailList)getActivity()).setDate(year,month);
        }
    }
}
