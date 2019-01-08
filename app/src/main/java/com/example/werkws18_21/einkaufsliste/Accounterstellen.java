package com.example.werkws18_21.einkaufsliste;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Accounterstellen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounterstellen);
        Intent aufruf = getIntent();
        String intentText = "";
        if (aufruf.getExtras() != null) {
            intentText =
                    aufruf.getExtras().get("NEXTACTIVITY").toString() ;
        }
    }
}
