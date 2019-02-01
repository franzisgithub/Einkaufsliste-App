package com.example.werkws18_21.einkaufsliste;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myTag";
    //Instanz der Firebase Authentifikation
    private FirebaseAuth mAuth;

    EditText eT_Email;
    EditText eT_Password;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText name = findViewById(R.id.eT_Email);
        EditText passwort = findViewById(R.id.eT_Passwort);
        Button buttonAcc = findViewById(R.id.button);
        Button buttonP = findViewById(R.id.button2);
        Button login = findViewById(R.id.button4);
        progressBar = findViewById(R.id.progressbar);

        //Instanz der Firebase Authentifikation
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            toGruppenauswahl();
            this.finish();
        }

    }

    //loggt user ein, weiter in gruppenauswahl
    public void loginUser() {
        //edit Text finden
        eT_Email = findViewById(R.id.eT_Email);
        eT_Password = findViewById(R.id.eT_Passwort);

        //Edit texts auslesen
        String s_Email = eT_Email.getText().toString().trim();
        String s_Passwort = eT_Password.getText().toString().trim();

        //Email und Passwort auf Inhalt prüfen
        if (s_Email.isEmpty() || s_Passwort.isEmpty()) {
            Toast.makeText(MainActivity.this, "Email oder Passwort leer! \nBitte ausfüllen!", Toast.LENGTH_LONG).show();
            eT_Email.requestFocus();
            eT_Password.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(s_Email).matches()) {
            Toast.makeText(MainActivity.this, "Keine gültige E-Mail-Adresse", Toast.LENGTH_LONG).show();
            eT_Email.requestFocus();
            return;
        }

        if (s_Passwort.length() < 6) {
            Toast.makeText(MainActivity.this, "Passwort muss mmindestens 6 Zeichen umfassen", Toast.LENGTH_LONG).show();
            eT_Password.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(s_Email, s_Passwort).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    toGruppenauswahl();
                } else {
                    Toast.makeText(MainActivity.this, "Login fehlgeschlagen", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }


    //erstellt User, führt anchließend loginUer aus
    public void createUser() {

        //edit Text finden
        eT_Email = findViewById(R.id.eT_Email);
        eT_Password = findViewById(R.id.eT_Passwort);

        //Edit texts auslesen
        String s_Email = eT_Email.getText().toString().trim();
        String s_Passwort = eT_Password.getText().toString().trim();

        //Email und Passwort auf Inhalt prüfen
        if (s_Email.isEmpty() || s_Passwort.isEmpty()) {
            Toast.makeText(MainActivity.this, "Email oder Passwort leer! \nBitte ausfüllen!", Toast.LENGTH_LONG).show();
            eT_Email.requestFocus();
            eT_Password.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(s_Email).matches()) {
            Toast.makeText(MainActivity.this, "Keine gültige E-Mail-Adresse", Toast.LENGTH_LONG).show();
            eT_Email.requestFocus();
            return;
        }

        if (s_Passwort.length() < 6) {
            Toast.makeText(MainActivity.this, "Passwort muss mmindestens 6 Zeichen umfassen", Toast.LENGTH_LONG).show();
            eT_Password.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(s_Email, s_Passwort).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Registrierung erfolgreich!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "User created");
                    FirebaseUser user = mAuth.getCurrentUser();
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(MainActivity.this, "Email ist berreits registriert!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "Creating user failed!");
                        Toast.makeText(MainActivity.this, "Registrierung fehlgeschlagen!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        toGruppenauswahl();
    }

    //beim klick auf login
    public void loginUserButton(View view) {
        loginUser();
    }

    //bei klickauf Registrieren
    public void createUserButton(View view) {
        createUser();
    }

    private void toGruppenauswahl() {
        progressBar.setVisibility(View.GONE);
        String intentText = "New Activity";
        Intent toGroupSet =
                new Intent(MainActivity.this, Gruppenauswahl.class);
        toGroupSet.putExtra("NEXTACTIVITY", intentText);
        startActivity(toGroupSet);
    }

}
