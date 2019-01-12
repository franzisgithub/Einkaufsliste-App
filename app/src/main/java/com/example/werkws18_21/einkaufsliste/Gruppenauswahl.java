package com.example.werkws18_21.einkaufsliste;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Gruppenauswahl extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gruppenauswahl);

        Button gruppe1 = findViewById(R.id.button7);

        //zur Gruppe wechseln
        //TODO: wechselt noch zu einer generellen Liste
        gruppe1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String intentText ="New Activity";
               Intent toGroup =
                       new Intent(Gruppenauswahl.this, Liste.class);
               toGroup.putExtra("NEXTACTIVITY",intentText);
               startActivity(toGroup);
            }
        });
    }
}
