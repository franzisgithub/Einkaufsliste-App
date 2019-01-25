package com.example.werkws18_21.einkaufsliste;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GruppenManager extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView tvListenName;
    DocumentReference ListeRef;
    String ListenName;

    String ListeRefString;


    private static final String LISTEN_REFERENZ = "Listen-Referenz";
    private static final String LISTEN_NAME = "ListenName";
    private static final String LISTEN_COLLECTION = "Listen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gruppen_manager);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tvListenName=findViewById(R.id.tvListenName);

        Intent toGroup = getIntent();
        if(toGroup.getExtras()!=null){
            ListeRefString =  toGroup.getExtras().get(LISTEN_REFERENZ).toString();
            ListeRef = db.collection(LISTEN_COLLECTION).document(ListeRefString);
        }
       getListenName();

    }

    private void getListenName() {
        ListeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
             DocumentSnapshot doc = task.getResult();
             ListenName = doc.get(LISTEN_NAME).toString();
             tvListenName.setText(ListenName);
            }
        });
    }
}
