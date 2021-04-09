package com.example.ocenastylujazdy.DataView;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ocenastylujazdy.DataBase.MyDatabase;
import com.example.ocenastylujazdy.R;

public class DataView2Activiy extends Activity {
    MyDatabase database;
    TextView data1, data2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view2_activiy);

        database = new MyDatabase(this, 1);

        data1=findViewById(R.id.DataViewLayout3);
        data2=findViewById(R.id.DataViewLayout4);

        StringBuilder builder = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();
        Cursor cursor = database.getAllDataSpeed();
        Cursor cursor1 = database.getAllDataSpeedLimit();

        while (cursor.moveToNext()) {
            builder.append("\nid: " + cursor.getInt(0));
            builder.append("\nPredkości: " + cursor.getFloat(1));
        }
        while (cursor1.moveToNext()) {
            builder2.append("\nid: " + cursor1.getInt(0));
            builder2.append("\nPrzekroczonych: " + cursor1.getFloat(1));
        }
        data1.setText(builder.toString());
        data2.setText(builder2.toString());
    }
}