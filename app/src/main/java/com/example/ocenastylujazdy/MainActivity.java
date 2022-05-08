package com.example.ocenastylujazdy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ocenastylujazdy.BluetoothELM327.MainActivityELM327;
import com.example.ocenastylujazdy.DataBase.MyDatabase;
import com.example.ocenastylujazdy.DataView.DataView1Activiy;
import com.example.ocenastylujazdy.DataView.DataView2Activiy;
import com.example.ocenastylujazdy.DataView.DataView3Activiy;
import com.example.ocenastylujazdy.Language.LocaleHelper;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends Activity implements View.OnClickListener {
    ImageButton buttonHelp, buttonSettings;
    Button btnEN, btnPL, buttonSensor, buttonELM, buttonSpeedMeter, buttonOcena, buttonClearDatabase, buttonstartMesure, buttonstopMesure;
    TextView textRating;
    TextView DBtext;
    TextView DBtext2;
    TextView DBtext3;
    TextView progress;
    String brake_acc, speed_text, throttle_acc, summary_rate;
    ImageView imageViewAxis;
    MyDatabase database;
    ProgressBar prgBar;
    private int prg = 0;
    Handler handler = new Handler();

    SensorsActivity sensorsActivity = new SensorsActivity();
    GpsSpeedActivity gpsSpeedActivity = new GpsSpeedActivity();
    MainActivityELM327 mainActivityELM327 = new MainActivityELM327();

    //język
    Context context;
    Resources resources;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//urzymuje aktywny ekran, nie wygasza

        btnEN = findViewById(R.id.buttonLanguageEnglish);
        btnPL = findViewById(R.id.buttonLanguagePolish);

        //zmiana języka
        btnPL.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Polski", Toast.LENGTH_SHORT).show();
            updateViews("pl");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        btnEN.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "English", Toast.LENGTH_SHORT).show();
            updateViews("en");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

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
            progress = findViewById(R.id.textViewProgress);
            //Oceny
            //brake_acc=getResources().getString(R.string.brake_acceleration);;
            brake_acc = getString(R.string.brake_acceleration);
            speed_text = getString(R.string.speed_text);
            throttle_acc = getString(R.string.throttle_acc);
            summary_rate = getString(R.string.ratingSummary);

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

            //progress bar
            prgBar = findViewById(R.id.progressBar);
            prgBar.setVisibility(View.INVISIBLE);

            //pozwolenie na dostęp do usługi lokalizacji
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }

        //do zmiany języka
    @Override
    protected void attachBaseContext (Context base){
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
    private void updateViews (String languageCode){
        context = LocaleHelper.setLocale(this, languageCode);
        resources = context.getResources();
    }

    //przypisanie funkcji do przycisków
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (v.getId() == buttonHelp.getId()) {
            Toast.makeText(MainActivity.this, getString(R.string.info), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        } else if (v.getId() == buttonELM.getId()) {
            Intent intent = new Intent(this, MainActivityELM327.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (v.getId() == buttonSensor.getId()) {
            Intent intent = new Intent(this, SensorsActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (v.getId() == buttonSpeedMeter.getId()) {
            Intent intent = new Intent(this, GpsSpeedActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        //zapis do pliku csv wyników z bazy danych
        else if (v.getId() == buttonstartMesure.getId()) {
            String FILENAME = "log.csv";
            String FILENAME1 = "log1.csv";
            String FILENAME2 = "log2.csv";
            String FILENAME3 = "log3.csv";
            String FILENAME4 = "log4.csv";
            String FILENAME5 = "log5.csv";
            File directoryDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File logDir = new File(directoryDownload, FILENAME);
            File logDir1 = new File(directoryDownload, FILENAME1);
            File logDir2 = new File(directoryDownload, FILENAME2);
            File logDir3 = new File(directoryDownload, FILENAME3);
            File logDir4 = new File(directoryDownload, FILENAME4);
            File logDir5 = new File(directoryDownload, FILENAME5);
            try {
                logDir.createNewFile();
                logDir1.createNewFile();
                logDir2.createNewFile();
                logDir3.createNewFile();
                logDir4.createNewFile();
                logDir5.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(logDir));
                CSVWriter csvWrite1 = new CSVWriter(new FileWriter(logDir1));
                CSVWriter csvWrite2 = new CSVWriter(new FileWriter(logDir2));
                CSVWriter csvWrite3 = new CSVWriter(new FileWriter(logDir3));
                CSVWriter csvWrite4 = new CSVWriter(new FileWriter(logDir4));
                CSVWriter csvWrite5 = new CSVWriter(new FileWriter(logDir5));
                Cursor curCSV = database.getReadableDatabase().rawQuery("SELECT * FROM  sensZ", null);
                Cursor curCSV1 = database.getReadableDatabase().rawQuery("SELECT * FROM  sensZX", null);
                Cursor curCSV2 = database.getReadableDatabase().rawQuery("SELECT * FROM  Gps", null);
                Cursor curCSV3 = database.getReadableDatabase().rawQuery("SELECT * FROM  GpsL", null);
                Cursor curCSV4 = database.getReadableDatabase().rawQuery("SELECT * FROM  Throttle", null);
                Cursor curCSV5 = database.getReadableDatabase().rawQuery("SELECT * FROM  ThrottleX", null);
                csvWrite.writeNext(curCSV.getColumnNames());
                csvWrite1.writeNext(curCSV1.getColumnNames());
                csvWrite2.writeNext(curCSV2.getColumnNames());
                csvWrite3.writeNext(curCSV3.getColumnNames());
                csvWrite4.writeNext(curCSV4.getColumnNames());
                csvWrite5.writeNext(curCSV5.getColumnNames());
                while (curCSV.moveToNext()) {
                    //Which column you want to exprort;
                    String[] arrStr = {curCSV.getString(0) + "#" + curCSV.getString(1)};
                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();
                while (curCSV1.moveToNext()) {
                    String[] arrStr1 = {curCSV1.getString(0) + "#" + curCSV1.getString(1)};
                    csvWrite1.writeNext(arrStr1);
                }
                csvWrite1.close();
                curCSV1.close();
                while (curCSV2.moveToNext()) {
                    String[] arrStr2 = {curCSV2.getString(0) + "#" + curCSV2.getString(1)};
                    csvWrite2.writeNext(arrStr2);
                }
                csvWrite2.close();
                curCSV2.close();
                while (curCSV3.moveToNext()) {
                    String[] arrStr3 = {curCSV3.getString(0) + "#" + curCSV3.getString(1)};
                    csvWrite3.writeNext(arrStr3);
                }
                csvWrite3.close();
                curCSV3.close();
                while (curCSV4.moveToNext()) {
                    String[] arrStr4 = {curCSV4.getString(0) + "#" + curCSV4.getString(1)};
                    csvWrite4.writeNext(arrStr4);
                }
                csvWrite4.close();
                curCSV4.close();
                while (curCSV5.moveToNext()) {
                    String[] arrStr5 = {curCSV5.getString(0) + "#" + curCSV5.getString(1)};
                    csvWrite5.writeNext(arrStr5);
                }
                csvWrite5.close();
                curCSV5.close();

                Toast.makeText(MainActivity.this, getString(R.string.save_data_to_csv), Toast.LENGTH_SHORT).show();
            } catch (Exception sqlEx) {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
                Toast.makeText(MainActivity.this, sqlEx.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else if (v.getId() == buttonOcena.getId()) {
            //przyspieszenia
            Cursor cursor = database.getAllData();
            Cursor cursor2 = database.getAllDataZX();
            float parm1 = cursor2.getCount();
            float parm2 = cursor.getCount();
            float ocena = parm1 / parm2;

            if (ocena <= 0.2) {
                DBtext.setText(brake_acc + " 5/5");
            } else if (ocena > 0.2 && ocena <= 0.4) {
                DBtext.setText(brake_acc + " 4/5");
            } else if (ocena > 0.4 && ocena <= 0.6) {
                DBtext.setText(brake_acc + " 3/5");
            } else if (ocena > 0.6 && ocena <= 0.8) {
                DBtext.setText(brake_acc + " 2/5");
            } else if (ocena > 0.8) {
                DBtext.setText(brake_acc + " 1/5");
            } else {
                DBtext.setText(getString(R.string.test_acc));
            }
            //prędkość
            Cursor cursor3 = database.getAllDataSpeed();
            Cursor cursor4 = database.getAllDataSpeedLimit();
            float parm3 = cursor4.getCount();
            float parm4 = cursor3.getCount();
            float ocena2 = parm3 / parm4;


            if (ocena2 <= 0.2) {
                DBtext2.setText(speed_text + " 5/5");
            } else if (ocena2 > 0.2 && ocena2 <= 0.4) {
                DBtext2.setText(speed_text + " 4/5");
            } else if (ocena2 > 0.4 && ocena2 <= 0.6) {
                DBtext2.setText(speed_text + " 3/5");
            } else if (ocena2 > 0.6 && ocena2 <= 0.8) {
                DBtext2.setText(speed_text + " 2/5");
            } else if (ocena2 > 0.8) {
                DBtext2.setText(speed_text + " 1/5");
            } else {
                DBtext2.setText(getString(R.string.test_speed));

            }

            //ELM327
            Cursor cursor5 = database.getAllDataThrottle();
            Cursor cursor6 = database.getAllDataThrottleX();
            float parm6 = cursor5.getCount();
            float parm5 = cursor6.getCount();
            float ocena3 = parm5 / parm6;

            if (ocena3 <= 0.2) {
                DBtext3.setText(throttle_acc + " 5/5");
            } else if (ocena3 > 0.2 && ocena3 <= 0.4) {
                DBtext3.setText(throttle_acc + " 4/5");
            } else if (ocena3 > 0.4 && ocena3 <= 0.6) {
                DBtext3.setText(throttle_acc + " 3/5");
            } else if (ocena3 > 0.6 && ocena3 <= 0.8) {
                DBtext3.setText(throttle_acc + " 2/5");
            } else if (ocena3 > 0.8) {
                DBtext3.setText(throttle_acc + " 1/5");
            } else {
                DBtext3.setText(getString(R.string.test_ELM));
            }

            //Ocena ogólna
            float ocena4 = (ocena + ocena2 + ocena3) / 3;
            if (ocena4 <= 0.2) {
                textRating.setText(summary_rate + " 5/5");
                textRating.setTextSize(25);
            } else if (ocena4 > 0.2 && ocena4 <= 0.4) {
                textRating.setText(summary_rate + " 4/5");
                textRating.setTextSize(25);
            } else if (ocena4 > 0.4 && ocena4 <= 0.6) {
                textRating.setText(summary_rate + " 3/5");
                textRating.setTextSize(25);
            } else if (ocena4 > 0.6 && ocena4 <= 0.8) {
                textRating.setText(summary_rate + " 2/5");
                textRating.setTextSize(25);
            } else if (ocena4 > 0.8) {
                textRating.setText(summary_rate + " 1/5");
                textRating.setTextSize(25);
            } else {
                textRating.setText(getString(R.string.all_sensor));
                textRating.setTextSize(15);
            }
            Toast.makeText(MainActivity.this, getString(R.string.count_rate), Toast.LENGTH_SHORT).show();
        }
        //zatrzymanie odczytu z sensorów i wyświetlenie oceny
        //usuwanie danych z bazy
        else if (v.getId() == buttonClearDatabase.getId()) {
            database.deleteAllData();
            DBtext.setText(getString(R.string.rating1));
            DBtext2.setText(getString(R.string.rating2));
            DBtext3.setText(R.string.rating3);
            textRating.setText(summary_rate);
            textRating.setTextSize(25);
            database.close();
            Toast.makeText(MainActivity.this, getString(R.string.erase_dtb), Toast.LENGTH_SHORT).show();

        } else if (v.getId() == buttonstopMesure.getId()) {
            database.close();
            sensorsActivity.onPause();
            gpsSpeedActivity.onPause();
            mainActivityELM327.onPause();
            Toast.makeText(MainActivity.this, getString(R.string.close_all_activity), Toast.LENGTH_LONG).show();


        } else if (v.getId() == DBtext.getId()) {
            prgBar.setVisibility(View.VISIBLE);
            prg = prgBar.getProgress();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (prg < 100) {
                        prg++;
                        // pokazuje akualna wartość progresu
                        handler.post(new Runnable() {
                            public void run() {
                                prgBar.setProgress(prg);
                                progress.setText(prg + "/" + prgBar.getMax());
                            }
                        });
                        try {
                            // Pokazuje progressBar po 100milisekundach.
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            Intent intent = new Intent(this, DataView1Activiy.class);
            startActivity(intent);


        } else if (v.getId() == DBtext2.getId()) {

            prgBar.setVisibility(View.VISIBLE);
            prg = prgBar.getProgress();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (prg < 100) {
                        prg += 1;
                        // pokazuje akualna wartość progresu
                        handler.post(new Runnable() {
                            public void run() {
                                prgBar.setProgress(prg);
                                progress.setText(prg + "/" + prgBar.getMax());
                            }
                        });
                        try {
                            // Pokazuje progressBar po 100milisekundach.
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            Intent intent = new Intent(this, DataView2Activiy.class);
            startActivity(intent);
        } else if (v.getId() == DBtext3.getId()) {

            prgBar.setVisibility(View.VISIBLE);
            prg = prgBar.getProgress();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (prg < 100) {
                        prg += 1;
                        // pokazuje akualna wartość progresu
                        handler.post(new Runnable() {
                            public void run() {
                                prgBar.setProgress(prg);
                                progress.setText(prg + "/" + prgBar.getMax());
                            }
                        });
                        try {
                            // Pokazuje progressBar po 100milisekundach.
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            Intent intent = new Intent(this, DataView3Activiy.class);
            startActivity(intent);
        }
    }
}

