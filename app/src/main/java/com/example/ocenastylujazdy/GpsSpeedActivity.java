package com.example.ocenastylujazdy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.ocenastylujazdy.DataBase.MyDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GpsSpeedActivity extends Activity implements LocationListener, View.OnClickListener {

    // Date startTime;
    final private static DecimalFormat df2 = new DecimalFormat("#.######");
    TextView textSpeedLimit;
    public float currentSpeed = 0;
    public MyDatabase database;
    ImageButton nextIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_speed);
        nextIntent = findViewById(R.id.buttonNextIntent3);
        nextIntent.setOnClickListener(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //startTime = new Date();
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        //this.onLocationChanged(null);
        textSpeedLimit = findViewById(R.id.textViewSpeedLimit);
        database = new MyDatabase(this, 1);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(@NonNull Location location) {
        //TextView time = this.findViewById(R.id.textViewTimeGps);
        TextView textSpeed = this.findViewById(R.id.textViewCurrentSpeed);
        TextView latLong = this.findViewById(R.id.textViewLatLong);

        double temp1 = location.getLatitude();
        double temp2 = location.getLongitude();
        String test1 = df2.format(temp1);
        String test2 = df2.format(temp2);
        String t1 = test1.replace(",", ".");
        String t2 = test2.replace(",", ".");
        jsonParse(t1, t2);
//        clat = Double.parseDouble(t1);
//        clong = Double.parseDouble(t2);
        //lokalizacja
        latLong.setText("Współrzędne:\n" + t1 + " , " + t2);
        //prędkość
        float nCurrentSpeed = location.getSpeed() * 3.6f;
        nCurrentSpeed *= 100;
        nCurrentSpeed = Math.round(nCurrentSpeed);
        nCurrentSpeed /= 100;
        textSpeed.setText("Prędkość bieżąca: \n" + nCurrentSpeed + " km/h");
        if (nCurrentSpeed >= 5) {
            database.writeDataSpeed(nCurrentSpeed);
        }
        currentSpeed = nCurrentSpeed;
//            long dateMs = new Date().getTime() - startTime.getTime();
//            long dateSec = TimeUnit.MILLISECONDS.toSeconds(dateMs);//czas w sekundach od momentu pobrania lokalizacji i wyswietlenia prędkości
//            time.setText("Czas: " + (int) dateSec + " s");
    }

    //limity prędkości
    @SuppressLint("SetTextI18n")
    private void jsonParse(String t1, String t2) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "SEQNR,\tLATITUDE,\tLONGITUDE\r\n1,\t" + t1 + ",\t" + t2);
        Request request = new Request.Builder()
                .url("https://fleet.api.here.com/2/calculateroute.json?routeMatch=1&mode=fastest;car;traffic:disabled&attributes=SPEED_LIMITS_FCn(FROM_REF_SPEED_LIMIT,TO_REF_SPEED_LIMIT)&app_id=vudtoepqc0e56blvzmZM&app_code=qB1FJk5HJzh71HY0EKQrsQ")
                .method("POST", body)
                .addHeader("Content-Type", "text/plain")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody).getJSONObject("response").getJSONArray("route").getJSONObject(0).getJSONArray("leg").getJSONObject(0).getJSONArray("link").getJSONObject(0).getJSONObject("attributes").getJSONArray("SPEED_LIMITS_FCN").getJSONObject(0);

            String speed = jsonResponse.getString("FROM_REF_SPEED_LIMIT");
            String speed2 = jsonResponse.getString("TO_REF_SPEED_LIMIT");

            if (Integer.parseInt(speed) > Integer.parseInt(speed2)) {
                textSpeedLimit.setText("Limit prędkości: \n" + speed + " km/h");
                if (currentSpeed > Float.parseFloat(speed)) {
                    database.writeDataSpeedLimit(currentSpeed);
                }
            } else {
                textSpeedLimit.setText("Limit prędkości: \n" + speed2 + " km/h");
                if (currentSpeed > Float.parseFloat(speed2)) {
                    database.writeDataSpeedLimit(currentSpeed);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }

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