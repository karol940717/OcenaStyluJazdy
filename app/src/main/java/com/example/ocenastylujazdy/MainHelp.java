package com.example.ocenastylujazdy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainHelp extends Activity {
    TextView textHelp, textCopyright;


    //@SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_help);

        textHelp = findViewById(R.id.textHelp);
        textCopyright = findViewById(R.id.textCopyright);


        textHelp.setText(getString(R.string.hlp) + "\n" +
                "\n" +
                        getString(R.string.hlp1) +"\n" +
                "\n" +
                        getString(R.string.hlp2) +"\n" +
                "\n" +
                        getString(R.string.hlp3) +"\n" +
                "\n" +
                        getString(R.string.hlp4));

        textCopyright.setText(getString(R.string.hlp5));

    }

}
