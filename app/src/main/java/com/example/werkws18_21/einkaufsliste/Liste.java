package com.example.werkws18_21.einkaufsliste;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Liste extends AppCompatActivity {

    private static final String TAG = "MyTag";
    //db als Instanz für die Datenbank im firestore
    FirebaseFirestore db;

    //Strings, die Online-Verzeichnisse benennen
    private static final String LIST_COLLECTION = "Listen";
    private static final String ITEMS_COLLECTION = "Items";
    private static final String LIST_NAME = "Listen-Name";

    //String-Arrays, die Ids und Namen aller Listen des Nutzers enthalten TODO: noch ohne Funktion
    private ArrayList<String> mListIds = new ArrayList<>();
    private ArrayList<String> mListNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        //db als Instanz für die Datenbank im firestore
        FirebaseFirestore.getInstance();
    }

    //aufruf soll ausgewähltes oder eingegebenes Item zur ausgewählten Liste hinzufügen
    public void addItem(View view) {
        //TODO
    }

    //aufrufen, um Items in ausgewählter Liste herunterzuladen
    public void getItems(View view) {
        //TODO
    }

    //aufrufen, um alle Listen des angemeldeten Users herunterzuladen
    public void getLists(View view) {
        //TODO

        CollectionReference listen = db.collection(LIST_COLLECTION);
        Query listQuery = listen; //TODO: whereequalto ->userId

        listQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                mListIds.clear();
                mListNames.clear();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String liste = document.getId();
                        mListIds.add(liste);
                    }
                } else {
                    Log.e("fail", "fail");
                }
            }
        });
        for(String x : mListIds){
            DocumentReference ref = db.collection(LIST_COLLECTION).document(x);
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot doc = task.getResult();
                    mListNames.add(doc.get(LIST_NAME).toString());
                    //TODO: auf Funktion prüfen
                }
            });
        }

    }
    }
