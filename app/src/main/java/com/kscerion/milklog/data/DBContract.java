package com.kscerion.milklog.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ksceriath on 15-05-2016.
 */
public class DBContract {

    private DBContract() {}

    public static final String CONTENT_AUTHORITY = "com.kscerion.milklog";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_TESTTAB = "TestTab";
    public static final String PATH_USERS = "Users";
    public static final String PATH_MONTHLOGS = "MonthLogs";
    public static final String PATH_LOGS = "Logs";
    public static final String PATH_MONTHS_LOGS_JOIN = "MonthLogs";

    public static class Users implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();          // Lists all the customers
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_USERS;
        public static final String ITEM_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_USERS;
        public static final String TABLE_NAME = "Users";
        public static final String C_NAME = "Name";
        public static final String C_ADDRESS = "Address";
        public static final String C_NICKNAME = "NickName";
    }

    public static class MonthLogs implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MONTHLOGS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+ PATH_MONTHLOGS;
        public static final String ITEM_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+ PATH_MONTHLOGS;
        public static final String TABLE_NAME = "MonthLogs";
        public static final String C_MONTH = "Month";
        public static final String C_USER_ID = "UserID";
        public static final String C_DATE = "Date";
        public static final String C_MNG_QTY = "MorningQuantity";
        public static final String C_EVE_QTY = "EveningQuantity";
    }
}
