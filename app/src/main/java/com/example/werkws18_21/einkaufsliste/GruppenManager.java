package com.example.werkws18_21.einkaufsliste;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
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

import io.grpc.util.TransmitStatusRuntimeExceptionInterceptor;

public class GruppenManager extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView tvListenName;
    EditText eTNeueEmail;
    TextView tv;
    DocumentReference ListeRef;
    String ListenName;
    String ListeRefString;



    private static final String LISTEN_REFERENZ = "Listen-Referenz";
    private static final String LISTEN_NAME = "ListenName";
    private static final String LISTEN_COLLECTION = "Listen";
    private static final String USER_EMAIL = "User-Email";
    private static final String MITGLIEDER = "Mitglieder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gruppen_manager);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        eTNeueEmail = findViewById(R.id.eTEmail);
        tvListenName = findViewById(R.id.tvListenName);
        tv=findViewById(R.id.tv);
        Intent toGroup = getIntent();
        if (toGroup.getExtras() != null) {
            ListeRefString = toGroup.getExtras().get(LISTEN_REFERENZ).toString();
            ListeRef = db.collection(LISTEN_COLLECTION).document(ListeRefString);
        }
        getListenName();

    }

    //schreibt den Listennamen in den Titel
    private void getListenName() {
        ListeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                ListenName = doc.get(LISTEN_NAME).toString();
                tvListenName.setText("Liste: " + ListenName);
            }
        });
    }


    public void addUserButton(View view){
        addUser();
    }
    private void addUser(){
        String sEmail = eTNeueEmail.getText().toString();

        if (sEmail.isEmpty()) {
            return;
        }
        Map<String, Object> UserMap = new HashMap<>();
        UserMap.put(USER_EMAIL,sEmail);
        ListeRef.collection(MITGLIEDER).document().set(UserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(GruppenManager.this, "Mitglied hinzugefügt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void toListeButton(View view){
       toListe(ListeRefString);
    }

    private void toListe(String ListeRefString) {
        String intentText = "New Activity";
        Intent toGroup =
                new Intent(GruppenManager.this, Liste.class);
        toGroup.putExtra(LISTEN_REFERENZ, ListeRefString);
       startActivity(toGroup);
    }

}
