package com.example.ocenastylujazdy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ocenastylujazdy.DataBase.MyDatabase;

public class SensorsActivity extends Activity implements SensorEventListener, View.OnClickListener {

    //private static final String TAG = "SensorsActivity";
    SensorManager sensorManager;
    Sensor accelerometer;
    ImageButton nextIntent;
    TextView textX, textY, textZ;
    float g = 9.81f;//przyspieszenie ziemskie
    float gp = g * 0.4f;


    public MyDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        textX = findViewById(R.id.textX);
        textY = findViewById(R.id.textY);
        textZ = findViewById(R.id.textZ);

        nextIntent = findViewById(R.id.buttonNextIntent);
        nextIntent.setOnClickListener(this);

        //Log.d(TAG,"onCreate: Initializing Sensor Services" );
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        sensorManager.registerListener(SensorsActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        //Log.d(TAG,"onCreate: Register accelerometer listener" );
        database = new MyDatabase(this, 1);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {

        //zaokrąglenie zmiennych do drugiego miejsca po przecinku
        float x = event.values[0];
        x *= 100;
        x = Math.round(x);
        x /= 100;
        float y = event.values[1];
        y *= 100;
        y = Math.round(y);
        y /= 100;
        float z = event.values[2];
        z *= 100;
        z = Math.round(z);
        z /= 100;

        if (Math.abs(z) > 0.5) {
            database.writeData(z);//wszystkie przyspieszenia powyzej 0.5
        }
        if (Math.abs(z) > gp) {//jeśli wartość bezwzględna z z jest większa od gp
            textZ.setTextColor(Color.RED);
            database.writeDataZX(z);//przyspiesznia większe od 0.4g
        }

//        if (z != Float.parseFloat(null)) {

//            Intent intent = new Intent();
//            intent.putExtra("MESSAGE", z);//passing data to calling activity
//            setResult(RESULT_OK, intent);

//        } else {
//            Intent intent = new Intent();
//            setResult(RESULT_CANCELED, intent);
//        }
        //finish();
        //Log.d(TAG, "onSensorChanged: X:" + event.values[0] + "Y: " + event.values[1] + "Z: " + event.values[2]);
        if (Math.abs(x) > gp) {
            textX.setTextColor(Color.RED);
//                Intent intent2 = new Intent();
//                intent2.putExtra("MESSAGE2", z); //passing data to calling activity
//                setResult(RESULT_OK, intent2);
        } else {
            textX.setText("X:" + x + " m/s^2");
            textX.setTextColor(Color.BLACK);
            textY.setText("Y:" + y + " m/s^2");
            textY.setTextColor(Color.BLACK);
            textZ.setText("Z:" + z + " m/s^2");
            textZ.setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}