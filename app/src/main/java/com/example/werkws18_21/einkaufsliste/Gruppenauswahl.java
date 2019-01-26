package com.example.werkws18_21.einkaufsliste;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.mbms.StreamingServiceInfo;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Gruppenauswahl extends AppCompatActivity {

    private static final String LISTEN_NAME = "ListenName";
    private static final String LISTEN_COLLECTION = "Listen";
    private static final String USER_ID = "User-Id";
    private static final String MITGLIEDER = "Mitglieder";
    private static final String LISTEN_REFERENZ = "Listen-Referenz";
    private static final String USER_EMAIL = "User-Email";
    EditText eTNeueListe;
    private FirebaseAuth mAuth;
    //db als Instanz für die Datenbank im firestore
    FirebaseFirestore db;
    private String UserId;
    private String UserEmail;
    ArrayAdapter adapter;

    private ArrayList<String> mListIds = new ArrayList<>();
    private ArrayList<String> mListNames = new ArrayList<>();
    ArrayList<String> groupList1;
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
        UserEmail = mAuth.getCurrentUser().getEmail();

        final ArrayAdapter<String> adapter;

        // Gruppen / Listen
        groupList1 = new ArrayList<>();

        adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, groupList1);
        groupList.setAdapter(adapter);
        getListen();
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
        getListen();
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
                Map<String, Object> UserMap = new HashMap<>();
                UserMap.put(USER_ID, UserId);
                UserMap.put(USER_EMAIL,UserEmail);
                neueListeRef.collection(MITGLIEDER).document().set(UserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Gruppenauswahl.this, "Liste hinzugefügt", Toast.LENGTH_SHORT).show();
                        toGruppenManager(neueListeRefString);
                    }
                });

            }
        });
    }

    private void toGruppenManager(String ListeRefString) {
        String intentText = "New Activity";
        Intent toGroup =
                new Intent(Gruppenauswahl.this, GruppenManager.class);
        toGroup.putExtra(LISTEN_REFERENZ, ListeRefString);
        startActivity(toGroup);
    }


    //bis jetzt: schreibt alle Listen-IDs in mListIds
    private void getListen() {
        mListIds.clear();
        mListNames.clear();

        final CollectionReference listen = db.collection(LISTEN_COLLECTION);
        Query listQuery = listen;
        listQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(final QueryDocumentSnapshot doc:task.getResult()){
                        CollectionReference colRef = listen.document(doc.getId()).collection(MITGLIEDER);
                        Query MitgliederQuery = colRef.whereEqualTo(USER_EMAIL,mAuth.getCurrentUser().getEmail());
                        MitgliederQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot doc1 : task.getResult()){
                                        String liste = doc.getId();
                                        mListIds.add(liste);
                                    }
                                }
                            }
                        });
                        for(String x : mListIds){
                            DocumentReference ref = db.collection(LISTEN_COLLECTION).document(x);
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot doc = task.getResult();
                                    mListNames.add(doc.get(LISTEN_NAME).toString());
                                    groupList1.add(doc.get(LISTEN_NAME).toString());
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}
