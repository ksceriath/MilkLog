package com.kscerion.milklog;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.kscerion.milklog.data.DBContract;

/**
 * Created by ksceriath on 29-05-2016.
 */
public class CreateActivity extends AppCompatActivity {

    public static final String USER_ID = "com.kscerion.milklog.EditCustomer.USER_ID";
    public static final String USER_NAME = "com.kscerion.milklog.EditCustomer.USER_NAME";
    public static final String USER_ADDRESS = "com.kscerion.milklog.EditCustomer.USER_ADDRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create);

        Intent intent = this.getIntent();

        if(intent!=null && intent.getStringExtra(CreateActivity.USER_ID)==null) {
            this.setTitle("New Customer");
        } else {
            this.setTitle("Edit Customer Details");
            String name = intent.getStringExtra(CreateActivity.USER_NAME);
            String address = intent.getStringExtra(CreateActivity.USER_ADDRESS);
            ((EditText) findViewById(R.id.name)).setText(name);
            ((EditText) findViewById(R.id.address)).setText(address);
        }

    }

    public void insertUser(View view) {
        String name = ((EditText) findViewById(R.id.name)).getText().toString();
//        String nickName = ((EditText) findViewById(R.id.nickname)).getText().toString();
        String address = ((EditText) findViewById(R.id.address)).getText().toString();
        ContentValues values = new ContentValues();
        values.put(DBContract.Users.C_NAME, name);
//        values.put(DBContract.Users.C_NICKNAME, nickName);
        values.put(DBContract.Users.C_ADDRESS, address);
        Uri uri = getContentResolver().insert(DBContract.Users.CONTENT_URI,values);
        System.out.println(uri);
        finish();
    }

    public void editUser(View view) {
        String name = ((EditText) findViewById(R.id.name)).getText().toString();
        String address = ((EditText) findViewById(R.id.address)).getText().toString();
        ContentValues values = new ContentValues();
        values.put(DBContract.Users.C_NAME, name);
        values.put(DBContract.Users.C_ADDRESS, address);
        String selection = "_ID = ? ";
        String[] selectionArgs = {this.getIntent().getStringExtra(CreateActivity.USER_ID)};
        int x = getContentResolver().update(DBContract.Users.CONTENT_URI,values,selection,selectionArgs);
        System.out.println(x);
        finish();
    }

    public void submit(View view) {
        Intent intent = this.getIntent();
        if(this.getIntent()!=null && intent.getStringExtra(CreateActivity.USER_ID)==null) {
            insertUser(view);
        } else {
            editUser(view);
        }
    }
}
