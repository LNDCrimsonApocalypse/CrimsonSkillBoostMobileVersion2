package com.example.crimsonskillboostmobilev2;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class CMOBoost extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this); // ✅ Initialize Firebase once
    }
}
