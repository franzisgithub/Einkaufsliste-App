package com.example.werkws18_21.einkaufsliste;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText name= findViewById(R.id.editText2);
        EditText passwort= findViewById(R.id.editText);
        Button buttonAcc = findViewById(R.id.button);
        Button buttonP= findViewById(R.id.button2);

        buttonAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String intentText = "New Activity";
                Intent meinIntent =
                        new Intent(MainActivity.this, Accounterstellen.class);
                meinIntent.putExtra("NEXTACTIVITY",intentText);
                // zur Activity die im Intent benannt wurde wechseln
                startActivity(meinIntent);
            }
        });
    }
}
