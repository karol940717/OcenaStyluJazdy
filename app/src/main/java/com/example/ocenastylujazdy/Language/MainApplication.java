package com.example.ocenastylujazdy.Language;

import android.app.Application;
import android.content.Context;

//klasa potrzebna do ustawienia języka aplikacji
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}