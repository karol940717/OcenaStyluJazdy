package com.example.ocenastylujazdy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.ocenastylujazdy.DataBase.MyDatabase;

public class SensorsActivity extends Activity implements SensorEventListener {

    //private static final String TAG = "SensorsActivity";
    private SensorManager sensorManager;
    Sensor accelerometer;

    TextView textX, textY, textZ;
    float g = 9.81f;//przyspieszenie ziemskie
    float gp = g * 0.4f;

    public float Zaxe;

    public MyDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        textX = findViewById(R.id.textX);
        textY = findViewById(R.id.textY);
        textZ = findViewById(R.id.textZ);


        //Log.d(TAG,"onCreate: Initializing Sensor Services" );
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(SensorsActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        //Log.d(TAG,"onCreate: Register accelerometer listener" );
        database = new MyDatabase(this, 1);


    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        // W trakcie wstrzymania aplikacji zatrzymuje pobieranie aktualizowanych danych w celu
//// zaoszczędzenia energii
//        sensorManager.unregisterListener(this);
//        Intent intent=  new Intent(this,MainActivity.class);
//        startActivity(intent);
//    }



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


//        Intent intent= new Intent(this,MainActivity.class);
//        intent.putExtra("z",z);

        database.writeData(z);//wszystkie przyspieszenia

        //Log.d(TAG, "onSensorChanged: X:" + event.values[0] + "Y: " + event.values[1] + "Z: " + event.values[2]);
        if (Math.abs(x) > gp) {
            textX.setTextColor(Color.RED);
            //jeśli wartość bezwzględna z z jest większa od gp
        } else if (Math.abs(z) > gp) {
            textZ.setTextColor(Color.RED);
            database.writeDataZX(z);//przyspiesznia większe od 0.4g
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


}