package com.example.co_fitness.Utils;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SignalSingleton.init(this);
    }
}
