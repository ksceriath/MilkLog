package com.kscerion.milklog.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.Map;

/**
 * Created by ksceriath on 19-05-2016.
 */
public class MilkProvider extends ContentProvider {

    private DBHelper mDbHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int USERS = 300;
    static final int USERSrow = 400;
    static final int MONTHLOGS = 700;
    static final int MONTHLOGSrow = 800;


    static UriMatcher buildUriMatcher() {
        UriMatcher um = new UriMatcher(UriMatcher.NO_MATCH);
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
        switch(sUriMatcher.match(uri)) {
            case USERS:
                return DBContract.Users.CONTENT_TYPE;
            case USERSrow:
                return DBContract.Users.ITEM_CONTENT_TYPE;
            case MONTHLOGS:
                return DBContract.MonthLogs.CONTENT_TYPE;
            case MONTHLOGSrow:
                return DBContract.MonthLogs.ITEM_CONTENT_TYPE;
            default:
                return null;
        }
    }

    private String getTableFromUri(Uri uri) {
        switch(sUriMatcher.match(uri)) {
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
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int i = db.update(getTableFromUri(uri), contentValues, selection, selectionArgs);
        db.close();
        getContext().getContentResolver().notifyChange(uri,null);
        return i;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        for(Map.Entry x : values.valueSet()) {
            if(((String)x.getValue()).equals("")) {
                x.setValue(null);
            }
        }
        long id = db.insert(getTableFromUri(uri),null,values);
        if(id==-1) {
            db.close();
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return uri.buildUpon().appendPath(String.valueOf(id)).build();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int i = db.delete(getTableFromUri(uri), selection, selectionArgs);
        db.close();
        getContext().getContentResolver().notifyChange(uri,null);
        return i;
    }
}
