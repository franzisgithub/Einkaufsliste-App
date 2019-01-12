package com.example.werkws18_21.einkaufsliste;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myTag";
    //Instanz der Firebase Authentifikation
    private FirebaseAuth mAuth;

    EditText eT_Email;
    EditText eT_Passwort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText name= findViewById(R.id.eT_Email);
        EditText passwort= findViewById(R.id.eT_Passwort);
        Button buttonAcc = findViewById(R.id.button);
        Button buttonP= findViewById(R.id.button2);
        Button login = findViewById(R.id.button4);

        //Instanz der Firebase Authentifikation
        mAuth =FirebaseAuth.getInstance();


        // Wechseln über erfolgreichen login zur Gruppen/Listenauswahl
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String intentText = "New Activity";
                Intent toGroupSet =
                        new Intent(MainActivity.this, Gruppenauswahl.class);
                toGroupSet.putExtra("NEXTACTIVITY", intentText);
                startActivity(toGroupSet);
            }
        });

        //TODO: irgendwie ist beim App öffnen immer als erstes das Passwort eingabefeld aktiv

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
    //wird bei klicken des "Account erstellen"-Buttons ausgeführt, erstellt Account
    private void createUser(View view){//TODO:zum butoon hinzufügen und Testen, in Doku weiterarbeiten
        //edit Text finden
        eT_Email = findViewById(R.id.eT_Email);
        eT_Passwort =findViewById(R.id.eT_Passwort);

        //Edit texts auslesen
        String s_Email = eT_Email.getText().toString();
        String s_Passwort = eT_Passwort.getText().toString();

        //Email und Passwort auf Inhalt prüfen
        if(s_Email.isEmpty() || s_Passwort.isEmpty()){
            Toast.makeText(MainActivity.this,"Email oder Passwort leer! \nBitte ausfüllen!",Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(s_Email,s_Passwort).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"User created");
                    FirebaseUser user = mAuth.getCurrentUser();
                }else{
                  Log.w(TAG,"Creating user failed!");
                  Toast.makeText(MainActivity.this, "Registrierung fehlgeschlagen!",Toast.LENGTH_SHORT).show();
                 }
            }
        });
    }
}
