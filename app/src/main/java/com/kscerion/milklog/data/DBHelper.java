package com.kscerion.milklog.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by ksceriath on 15-05-2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = "MilkLog.db";
    public static File DATABASE_FILE;

    public DBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        DATABASE_FILE = context.getDatabasePath(DATABASE_NAME);
        System.out.println(context.getDatabasePath(DATABASE_NAME));
    }

    public void  onCreate(SQLiteDatabase db) {
        for(String sqlCreateEntry : SQL_CREATE_ENTRIES) {
            db.execSQL(sqlCreateEntry);
            System.out.println("EXECUTED : "+sqlCreateEntry);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVewsion) {
        for(String sqlDeleteEntry : SQL_DELETE_ENTRIES) {
            db.execSQL(sqlDeleteEntry);
        }
        onCreate(db);
    }

    private final String TEXT = " TEXT ";
    private final String INTEGER = " INTEGER ";
    private final String[] SQL_CREATE_ENTRIES = {
            "CREATE TABLE " +
                    DBContract.Users.TABLE_NAME + "(" +
                    DBContract.Users._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DBContract.Users.C_NAME + TEXT + " NOT NULL," +
                    DBContract.Users.C_ADDRESS + TEXT +
                    ") ",
            "CREATE TABLE " +
                    DBContract.MonthLogs.TABLE_NAME + "(" +
                    DBContract.MonthLogs._ID + INTEGER + "PRIMARY KEY AUTOINCREMENT," +
                    DBContract.MonthLogs.C_MONTH + INTEGER + "," +
                    DBContract.MonthLogs.C_USER_ID + INTEGER + "," +
                    DBContract.MonthLogs.C_DATE + INTEGER + "," +
                    DBContract.MonthLogs.C_MNG_QTY + INTEGER + "," +
                    DBContract.MonthLogs.C_EVE_QTY + INTEGER + "," +
                    " UNIQUE (" + DBContract.MonthLogs.C_MONTH + "," + DBContract.MonthLogs.C_USER_ID + "," +DBContract.MonthLogs.C_DATE + ")" +
                    ") "

    };
    private final String[] SQL_DELETE_ENTRIES = {
            "DROP TABLE IF EXISTS " + DBContract.Users.TABLE_NAME,
            "DROP TABLE IF EXISTS " + DBContract.MonthLogs.TABLE_NAME,
            "DROP TABLE IF EXISTS TEStTAB",
            "DROP TABLE IF EXISTS LOG"
    };

}
