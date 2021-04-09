package com.example.ocenastylujazdy;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ocenastylujazdy.BluetoothELM327.MainActivityELM327;
import com.example.ocenastylujazdy.DataBase.MyDatabase;
import com.example.ocenastylujazdy.DataView.DataView1Activiy;
import com.example.ocenastylujazdy.DataView.DataView2Activiy;
import com.example.ocenastylujazdy.DataView.DataView3Activiy;

public class MainActivity extends Activity implements View.OnClickListener {
    ImageButton buttonHelp, buttonSettings;
    Button buttonSensor, buttonELM, buttonSpeedMeter, buttonOcena, buttonClearDatabase, buttonstartMesure, buttonstopMesure;

    TextView textRating, textDistance, DBtext, DBtext2,DBtext3;
    ImageView imageViewAxis;
    MyDatabase database;

    SensorsActivity sensorsActivity = new SensorsActivity();
    GpsSpeedActivity gpsSpeedActivity = new GpsSpeedActivity();
    MainActivityELM327 mainActivityELM327= new MainActivityELM327();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//urzymuje aktywny ekran, nie wygasza


        database = new MyDatabase(this, 1);

        buttonSensor = findViewById(R.id.buttonSensor);
        buttonELM = findViewById(R.id.buttonELM);
        buttonHelp = findViewById(R.id.buttonHelp);
        buttonSettings = findViewById(R.id.buttonSettings);
        buttonSpeedMeter = findViewById(R.id.buttonSpeedMeter);
        imageViewAxis = findViewById(R.id.imageViewAxis);
        buttonOcena = findViewById(R.id.buttonOcena);
        buttonClearDatabase = findViewById(R.id.buttonClearDatabase);
        buttonstartMesure = findViewById(R.id.buttonStartMesure);
        buttonstopMesure = findViewById(R.id.buttonStopMesure);
        DBtext = findViewById(R.id.DatabaseReadTextView1);
        DBtext2 = findViewById(R.id.DatabaseReadTextView2);
        DBtext3 = findViewById(R.id.DatabaseReadTextView3);
        textRating = findViewById(R.id.textRating);

        // nasłuchiwanie
        buttonHelp.setOnClickListener(this);
        buttonSettings.setOnClickListener(this);
        buttonELM.setOnClickListener(this);
        buttonSensor.setOnClickListener(this);
        buttonSpeedMeter.setOnClickListener(this);
        buttonOcena.setOnClickListener(this);
        buttonClearDatabase.setOnClickListener(this);
        buttonstopMesure.setOnClickListener(this);
        buttonstartMesure.setOnClickListener(this);
        DBtext.setOnClickListener(this);
        DBtext2.setOnClickListener(this);
        DBtext3.setOnClickListener(this);



//pozwolenie na dostęp do usługi lokalizacji

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Intent intent = new Intent(this, GpsSpeedActivity.class);
            startActivity(intent);
        }
//        Intent intent = new Intent(this, SensorsActivity.class);
//        startActivityForResult(intent,0);

//        Intent i = new Intent(this, SensorsActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(i);
//        finish();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Intent intent = new Intent(this, GpsActivity.class);
//            startActivity(intent);
//        }
//    }

    //przypisanie funkcji do przycisków
    @Override
    public void onClick(View v) {
        if (v.getId() == buttonHelp.getId()) {
            Toast.makeText(MainActivity.this, "Informacje", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainHelp.class);
            startActivity(intent);
        } else if (v.getId() == buttonELM.getId()) {
            Intent intent = new Intent(this, MainActivityELM327.class);
            startActivity(intent);
            database.deleteAllData();
        } else if (v.getId() == buttonSensor.getId()) {
            Intent intent = new Intent(this, SensorsActivity.class);
            startActivity(intent);
        } else if (v.getId() == buttonSpeedMeter.getId()) {
            Intent intent = new Intent(this, GpsSpeedActivity.class);
            startActivity(intent);
        }

        //wywołanie metody zapisującej odczyty do bazy danych
        else if (v.getId() == buttonstartMesure.getId()) {
//            Intent intent = getIntent();
//            Bundle bd = intent.getExtras();
//            if (bd != null) {
//                Float getName = (Float) bd.get("z");
//                database.writeData(getName);
//            }
        } else if (v.getId() == buttonOcena.getId()) {
//przyspieszenia
            Cursor cursor = database.getAllData();
            Cursor cursor2 = database.getAllDataZX();
            float parm1 = cursor2.getCount();
            float parm2 = cursor.getCount();
            float ocena = parm1 / parm2;

            if (ocena <= 0.2) {
                DBtext.setText("Ocena za hamowanie i ruszanie: 5/5");
            } else if (ocena > 0.2 && ocena <= 0.4) {
                DBtext.setText("Ocena za hamowanie i ruszanie: 4/5");
            } else if (ocena > 0.4 && ocena <= 0.6) {
                DBtext.setText("Ocena za hamowanie i ruszanie: 3/5");
            } else if (ocena > 0.6 && ocena <= 0.8) {
                DBtext.setText("Ocena za hamowanie i ruszanie: 2/5");
            } else if (ocena > 0.8) {
                DBtext.setText("Ocena za hamowanie i ruszanie: 1/5");
            } else {
                DBtext.setText("Rozpocznij pomiar przyspieszeń!!!");
            }
            //prędkość
            Cursor cursor3 = database.getAllDataSpeed();
            Cursor cursor4 = database.getAllDataSpeedLimit();
            float parm3 = cursor4.getCount();
            float parm4 = cursor3.getCount();
            float ocena2 = parm3 / parm4;

            if (ocena2 <= 0.2) {
                DBtext2.setText("Ocena za prędkość: 5/5");
            } else if (ocena2 > 0.2 && ocena2 <= 0.4) {
                DBtext2.setText("Ocena za prędkość: 4/5");
            } else if (ocena2 > 0.4 && ocena2 <= 0.6) {
                DBtext2.setText("Ocena za prędkość: 3/5");
            } else if (ocena2 > 0.6 && ocena2 <= 0.8) {
                DBtext2.setText("Ocena za prędkość: 2/5");
            } else if (ocena2 > 0.8) {
                DBtext2.setText("Ocena za prędkość: 1/5");
            } else {
                DBtext2.setText("Rozpocznij pomiar prędkości!!!");

            }
            //ELM327
            Cursor cursor5 = database.getAllDataThrottle();
            Cursor cursor6 = database.getAllDataThrottleX();
            float parm6 = cursor5.getCount();
            float parm5 = cursor6.getCount();
            float ocena3 = parm5 / parm6;

            if (ocena3 <= 0.2) {
                DBtext3.setText("Ocena za sterowanie pedałem: 5/5");
            } else if (ocena3 > 0.2 && ocena3 <= 0.4) {
                DBtext3.setText("Ocena za sterowanie pedałem: 4/5");
            } else if (ocena3 > 0.4 && ocena3 <= 0.6) {
                DBtext3.setText("Ocena za sterowanie pedałem: 3/5");
            } else if (ocena3 > 0.6 && ocena3 <= 0.8) {
                DBtext3.setText("Ocena za sterowanie pedałem: 2/5");
            } else if (ocena3 > 0.8) {
                DBtext3.setText("Ocena za sterowanie pedałem: 1/5");
            } else {
                DBtext3.setText("Rozpocznij pomiar ELM327!!!");

            }

            //Ocena ogólna
            float ocena4=(ocena+ocena2+ocena3)/3;
            if (ocena4 <= 0.2) {
                textRating.setText("Ocena ogólna: 5/5");
            } else if (ocena4 > 0.2 && ocena4 <= 0.4) {
                textRating.setText("Ocena ogólna: 4/5");
            } else if (ocena4 > 0.4 && ocena4 <= 0.6) {
                textRating.setText("Ocena ogólna: 3/5");
            } else if (ocena4 > 0.6 && ocena4 <= 0.8) {
                textRating.setText("Ocena ogólna: 2/5");
            } else if (ocena4 > 0.8) {
                textRating.setText("Ocena ogólna: 1/5");
            } else {
                textRating.setText("Portrzebne dane ze wszystkich czujników!!!");
                textRating.setTextSize(15);
            }
        }
        //zatrzymanie odczytu z sensorów i wyświetlenie oceny
        //usuwanie danych z bazy
        else if (v.getId() == buttonClearDatabase.getId()) {
            database.deleteAllData();
            DBtext.setText("Ocena za hamowanie i ruszanie:");
            DBtext2.setText("Ocena za prędkość:");
            DBtext3.setText("Ocena za sterowanie pedałem:");
            textRating.setText("Ocena ogólna:");
            textRating.setTextSize(25);
            database.close();

        } else if (v.getId() == buttonstopMesure.getId()) {
            database.close();
            sensorsActivity.finishAffinity();
            gpsSpeedActivity.finishAffinity();
            mainActivityELM327.finishAffinity();
            sensorsActivity.finish();
            gpsSpeedActivity.finish();
            mainActivityELM327.finish();
        }
        else if (v.getId() == DBtext.getId()) {
            Intent intent= new Intent(this, DataView1Activiy.class);
            startActivity(intent);
        }
        else if (v.getId() == DBtext2.getId()) {
            Intent intent= new Intent(this, DataView2Activiy.class);
            startActivity(intent);
        }
        else if (v.getId() == DBtext3.getId()) {
            Intent intent= new Intent(this, DataView3Activiy.class);
            startActivity(intent);
        }
    }
}

