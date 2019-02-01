package com.example.werkws18_21.einkaufsliste;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListeningExecutorService;
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
import java.util.Collections;
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
    ArrayList<String> Mitglieder;
    ListView lvMitglieder;
    private boolean owner = false;

    private static final String LISTEN_REFERENZ = "Listen-Referenz";
    private static final String LISTEN_NAME = "ListenName";
    private static final String LISTEN_COLLECTION = "Listen";
    private static final String USER_EMAIL = "User-Email";
    private static final String MITGLIEDER = "Mitglieder";
    private static final String USER_ID = "User-Id";

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gruppen_manager);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        eTNeueEmail = findViewById(R.id.eTEmail);
        tvListenName = findViewById(R.id.tvListenName);

        lvMitglieder = findViewById(R.id.lvMitglieder);
        Mitglieder = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, Mitglieder) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView textView5 = (TextView) view.findViewById(android.R.id.text1);
                textView5.setTextSize(20);
                ViewGroup.LayoutParams layoutparams = view.getLayoutParams();

                //Define your height here.
                layoutparams.height = 140;

                view.setLayoutParams(layoutparams);

                return view;
            }
        };
        ;
        lvMitglieder.setAdapter(adapter);

        tv = findViewById(R.id.tv);
        Intent toGroup = getIntent();
        if (toGroup.getExtras() != null) {
            ListeRefString = toGroup.getExtras().get(LISTEN_REFERENZ).toString();
            ListeRef = db.collection(LISTEN_COLLECTION).document(ListeRefString);
        }
        getListenName();
        getMitglieder();

        lvMitglieder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                deleteMitglied(position);
                return true;
            }
        });


    }//onCreate-Ende

    private void deleteMitglied(final int position) {
        final CollectionReference mitglieder = ListeRef.collection(MITGLIEDER);
        mitglieder.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        if (mAuth.getCurrentUser().getEmail().equals(Mitglieder.get(position))) {
                            AlertDialog.Builder adb = new AlertDialog.Builder(GruppenManager.this);
                            adb.setTitle("Entfernen");
                            adb.setMessage("Sie sind der Adminsrator.\nSie können sich nicht selbst entfernen.\nLöschen Sie ggf. die Gruppe.");
                            adb.setNegativeButton("Zurück", null);
                            adb.show();
                        }

                        if (doc.get(USER_ID).toString().equals(mAuth.getUid().toString()) && !mAuth.getCurrentUser().getEmail().equals(Mitglieder.get(position))) {
                            AlertDialog.Builder adb = new AlertDialog.Builder(GruppenManager.this);
                            adb.setTitle("Entfernen?");
                            adb.setMessage("Möchten Sie das Mitglied wirklich entfernen?");
                            adb.setNegativeButton("Zurück", null);
                            adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Query query = mitglieder.whereEqualTo(USER_EMAIL, Mitglieder.get(position));
                                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                                    mitglieder.document(doc.getId()).delete();
                                                }
                                            }
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            getMitglieder();
                                        }
                                    });
                                }
                            });
                            adb.show();
                        }
                    }
                }
            }
        });
    }

    private void getMitglieder() {
        Mitglieder.clear();
        ListeRef.collection(MITGLIEDER).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Mitglieder.add(doc.get(USER_EMAIL).toString());
                    }
                    Collections.sort(Mitglieder, String.CASE_INSENSITIVE_ORDER);
                    adapter.notifyDataSetChanged();
                }
            }
        });
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


    public void addUserButton(View view) {
        addUser();
        getMitglieder();
    }

    private void addUser() {
        String sEmail = eTNeueEmail.getText().toString();

        if (sEmail.isEmpty()) {
            return;
        }
        Map<String, Object> UserMap = new HashMap<>();
        UserMap.put(USER_ID, "");
        UserMap.put(USER_EMAIL, sEmail);
        ListeRef.collection(MITGLIEDER).document().set(UserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(GruppenManager.this, "Mitglied hinzugefügt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void toListeButton(View view) {
        toListe(ListeRefString);
    }

    public void deleteListe(View view) {
        final CollectionReference mitglieder = ListeRef.collection(MITGLIEDER);

        AlertDialog.Builder adb = new AlertDialog.Builder(GruppenManager.this);
        adb.setTitle("Entfernen?");
        adb.setMessage("Möchten Sie die Liste wirklich löschen?");
        adb.setNegativeButton("Zurück", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mitglieder.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                if (mAuth.getUid().equals(doc.get(USER_ID).toString())) {
                                    owner = true;
                                    ListeRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            toGruppenauswahl();
                                        }
                                    });
                                }
                            }
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (owner == false) {
                            Query query = mitglieder.whereEqualTo(USER_EMAIL, mAuth.getCurrentUser().getEmail());
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            mitglieder.document(doc.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    toGruppenauswahl();
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        adb.show();


    }

    private void toGruppenauswahl() {
        String intentText = "New Activity";
        Intent toGroupSet =
                new Intent(GruppenManager.this, Gruppenauswahl.class);
        toGroupSet.putExtra("NEXTACTIVITY", intentText);
        startActivity(toGroupSet);
    }

    private void toListe(String ListeRefString) {
        String intentText = "New Activity";
        Intent toGroup =
                new Intent(GruppenManager.this, Liste.class);
        toGroup.putExtra(LISTEN_REFERENZ, ListeRefString);
        startActivity(toGroup);
    }

}
