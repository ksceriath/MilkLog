package com.kscerion.milklog;

import android.content.ContentValues;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create);
    }

//    public void sendMessage(View view) {
//        Intent intent = new Intent(this, DetailList.class);
//        EditText editText = (EditText) findViewById(R.id.edit_message);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
//    }

    public void insertName(View view) {
        String name = ((EditText) findViewById(R.id.edit_message)).getText().toString();
        ContentValues values = new ContentValues();
        values.put(DBContract.Users.C_NAME, name);
        Uri uri = getContentResolver().insert(DBContract.Users.CONTENT_URI,values);
//        long newRowId = new DBHelper(getApplicationContext()).getWritableDatabase().insert(
//                DBContract.TestTab.TABLE_NAME, null, values);
        System.out.println(uri);
        finish();
//        startActivity(new Intent(this, MyActivity.class));
    }
}
