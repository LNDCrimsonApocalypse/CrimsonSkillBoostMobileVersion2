package com.example.crimsonskillboostmobilev2;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.ai.client.generativeai.GenerativeModel;

public class CMOBoost extends Application {

    private static CMOBoost instance;
    private GenerativeModel generativeModel;

    private static final String GEMINI_API_KEY = "AIzaSyBQcpZt5UaWGR4UpLvSh_kQeH21wzKeMOA"; // (Careful: don't expose in prod apps!)

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize Gemini GenerativeModel
        generativeModel = new GenerativeModel(
                "models/gemini-1.5-flash", // <- model name as String now
                GEMINI_API_KEY
        );
    }

    public static CMOBoost getInstance() {
        return instance;
    }

    public GenerativeModel getGenerativeModel() {
        return generativeModel;
    }
}
