package com.example.ocenastylujazdy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainHelp extends Activity {
    TextView textHelp, textCopyright;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_help);

        textHelp = findViewById(R.id.textHelp);
        textCopyright = findViewById(R.id.textCopyright);


        textHelp.setText("APLIKACJA DO OCENY STYLU JAZDY KIEROWCY\n" +
                "\n" +
                "Kryteria oceny stylu jazdy:\n" +
                "\n" +
                "-Ocena na podstawie odczytów przyspieszeń osi X oraz Z urządzenia i przekroczenia określonych progów tych wartości\n" +
                "\n" +
                "-Ocena na podstawie odczytów prędkości urządzenia z czujnika GPS i przekroczenia wartości w porównaniu z danymi z Google Maps\n" +
                "\n" +
                "Dodatkowe funkcje aplikacji:\n" +
                "\n" +
                "Możliwość połączenia aplikacji z intefejsem diagnostycznym ELM327 za pomocą BLUETOOTH");

        textCopyright.setText("©Copyright Karol Chotkiewicz");

    }

}
