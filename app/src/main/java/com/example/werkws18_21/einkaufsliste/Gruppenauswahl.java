package com.example.werkws18_21.einkaufsliste;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Gruppenauswahl extends AppCompatActivity {

    private static final String LISTEN_NAME = "ListenName";
    private static final String LISTEN_COLLECTION = "Listen";
    private static final String USER_ID = "User-Id";
    private static final String MITGLIEDER = "Mitglieder";
    private static final String LISTEN_REFERENZ = "Listen-Referenz";
    EditText eTNeueListe;
    private FirebaseAuth mAuth;
    //db als Instanz für die Datenbank im firestore
    FirebaseFirestore db;
    private String UserId;

    String neueListeRefString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gruppenauswahl);

        Button gruppe1 = findViewById(R.id.button7);
        ListView groupList = findViewById(R.id.groupList);

        eTNeueListe = findViewById(R.id.eTNeueListe);
        //db als Instanz für die Datenbank firestore
        db = FirebaseFirestore.getInstance();
        //Instanz der Firebase Authentifikation
        mAuth = FirebaseAuth.getInstance();
        UserId = mAuth.getUid();

        // Gruppen / Listen
        final ArrayList<String> groupList1 = new ArrayList<>();


        //zur Gruppe wechseln
        //TODO: wechselt noch zu einer generellen Liste
        gruppe1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String intentText = "New Activity";
                Intent toGroup =
                        new Intent(Gruppenauswahl.this, Liste.class);
                toGroup.putExtra("NEXTACTIVITY", intentText);
                startActivity(toGroup);
            }
        });
    }

    public void NeueListe(View view) {
        addList();
    }

    private void addList() {
        String sListenName = eTNeueListe.getText().toString();
        if (sListenName.isEmpty()) {
            Toast.makeText(Gruppenauswahl.this, "Geben Sie einen Listennamen ein!", Toast.LENGTH_LONG).show();
            eTNeueListe.requestFocus();
            return;
        }
        Map<String, Object> ListenNameMap = new HashMap<>();
        ListenNameMap.put(LISTEN_NAME, sListenName);

        final DocumentReference neueListeRef = db.collection(LISTEN_COLLECTION).document();
        neueListeRefString = neueListeRef.getId();
        neueListeRef.set(ListenNameMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Map<String, Object> UserIdMap = new HashMap<>();
                UserIdMap.put(USER_ID, UserId);
                neueListeRef.collection(MITGLIEDER).document().set(UserIdMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Gruppenauswahl.this, "Liste hinzugefügt", Toast.LENGTH_SHORT).show();
                        toGruppenManager(neueListeRefString);
                    }
                });

            }
        });
    }

    private void toGruppenManager(String ListeRef) {
        String intentText = "New Activity";
        Intent toGroup =
                new Intent(Gruppenauswahl.this, GruppenManager.class);
        toGroup.putExtra(LISTEN_REFERENZ, ListeRef);
        startActivity(toGroup);
    }

}
