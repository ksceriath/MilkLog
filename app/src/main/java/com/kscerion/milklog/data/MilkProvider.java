package com.kscerion.milklog.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by ksceriath on 19-05-2016.
 */
public class MilkProvider extends ContentProvider {

    private DBHelper mDbHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int TESTTAB = 100;
    static final int TESTTABrow = 200;

    static final int USERS = 300;
    static final int USERSrow = 400;
    static final int MONTHLOGS = 700;
    static final int MONTHLOGSrow = 800;


    static UriMatcher buildUriMatcher() {
        UriMatcher um = new UriMatcher(UriMatcher.NO_MATCH);
        um.addURI(DBContract.CONTENT_AUTHORITY,DBContract.PATH_TESTTAB,TESTTAB);   // whole bunch of rows
        um.addURI(DBContract.CONTENT_AUTHORITY,DBContract.PATH_TESTTAB+"/#",TESTTABrow);    // just one row
        um.addURI(DBContract.CONTENT_AUTHORITY,DBContract.PATH_USERS, USERS);
        um.addURI(DBContract.CONTENT_AUTHORITY,DBContract.PATH_USERS+"/#", USERSrow);
        um.addURI(DBContract.CONTENT_AUTHORITY,DBContract.PATH_MONTHLOGS, MONTHLOGS);
        um.addURI(DBContract.CONTENT_AUTHORITY,DBContract.PATH_MONTHLOGS+"/#", MONTHLOGSrow);
        return um;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return DBContract.TestTab.CONTENT_TYPE;
    }

    private String getTableFromUri(Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case TESTTAB:
            case TESTTABrow:
                return DBContract.TestTab.TABLE_NAME;
            case USERS:
            case USERSrow:
                return DBContract.Users.TABLE_NAME;
            case MONTHLOGS:
            case MONTHLOGSrow:
                return DBContract.MonthLogs.TABLE_NAME;
            default:
                return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.query(false,getTableFromUri(uri),projection,selection,selectionArgs,null,null,sortOrder,null);
        if(sUriMatcher.match(uri)==MONTHLOGS && c.getCount()==0) {
            String insertMonth = "INSERT INTO MONTHLOGS (Month,UserId,Date) VALUES ("+selectionArgs[0]+","+selectionArgs[1]+",";
            int month = Integer.parseInt(selectionArgs[0].substring(4));
            int year = Integer.parseInt(selectionArgs[0].substring(0,4));
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
            for(int i=1; i<=days; i++) {
                String date = (i/10==0) ? "0" : "";
                date = date + i;
                db.execSQL(insertMonth+date+")");
            }
            c = db.query(false,getTableFromUri(uri),projection,selection,selectionArgs,null,null,sortOrder,null);
            System.out.print("CURSORY OUTPUT FOR "+uri+" "+c.getCount());
        }
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int i = db.update(getTableFromUri(uri), contentValues, selection, selectionArgs);
        db.close();
        return i;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(getTableFromUri(uri),null,values);
        db.close();
        return DBContract.TestTab.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int i = db.delete(getTableFromUri(uri), selection, selectionArgs);
        db.close();
        return i;
    }

}
