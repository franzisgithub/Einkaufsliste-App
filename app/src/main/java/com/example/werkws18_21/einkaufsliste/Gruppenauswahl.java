package com.example.werkws18_21.einkaufsliste;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.mbms.StreamingServiceInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Gruppenauswahl extends AppCompatActivity {

    private static final String LISTEN_NAME = "ListenName";
    private static final String LISTEN_COLLECTION = "Listen";
    private static final String USER_ID = "User-Id";
    private static final String MITGLIEDER = "Mitglieder";
    private static final String LISTEN_REFERENZ = "Listen-Referenz";
    private static final String USER_EMAIL = "User-Email";
    EditText eTNeueListe;
    ProgressBar progressBar;
    TextView tvEmail;
    private FirebaseAuth mAuth;
    //db als Instanz für die Datenbank im firestore
    FirebaseFirestore db;
    private String UserId;
    private String UserEmail;
    private ArrayList<String> mListIds = new ArrayList<>();
    private ArrayList<String> mListNames = new ArrayList<>();
    ArrayList<String> groupList1;
    String neueListeRefString;
    private Boolean ListeExistiertBereits;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gruppenauswahl);

        Toolbar toolbar = findViewById(R.id.my_toolbar1);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressbar);
        ListView groupList = findViewById(R.id.groupList);

        progressBar.setVisibility(View.VISIBLE);
        tvEmail = findViewById(R.id.tvEmail);
        eTNeueListe = findViewById(R.id.eTNeueListe);
        //db als Instanz für die Datenbank firestore
        db = FirebaseFirestore.getInstance();
        //Instanz der Firebase Authentifikation
        mAuth = FirebaseAuth.getInstance();
        UserId = mAuth.getUid();
        UserEmail = mAuth.getCurrentUser().getEmail();
        // Gruppen / Listen
        groupList1 = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, groupList1) {
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
        groupList.setAdapter(adapter);
        getEmail();
        getListen();


        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final int position1 = position;
                final CollectionReference listen = db.collection(LISTEN_COLLECTION);
                Query listenQuery = listen.whereEqualTo(LISTEN_NAME, groupList1.get(position1));
                listenQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String ListeRefString = doc.getId().toString();
                                toListe(ListeRefString);
                            }
                        }
                    }
                });

                /*
                itemList.add(beispielList.get(position));
                beispielList.remove(positionToRemove);
                adapter1.notifyDataSetChanged();
                adapter.notifyDataSetChanged();*/
                //TODO
            }
        });

    }

    private void getEmail() {
        String Email =  mAuth.getCurrentUser().getEmail();
        tvEmail.setText(Email);
    }

    //bis jetzt: schreibt alle Listen-IDs in mListIds
    private void getListen() {
        mListIds.clear();
        mListNames.clear();
        groupList1.clear();
        final CollectionReference listen = db.collection(LISTEN_COLLECTION);
        Query listQuery = listen;
        listQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot doc : task.getResult()) {
                        CollectionReference colRef = listen.document(doc.getId()).collection(MITGLIEDER);
                        Query MitgliederQuery = colRef.whereEqualTo(USER_EMAIL, mAuth.getCurrentUser().getEmail());
                        MitgliederQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot doc1 : task.getResult()) {
                                        String liste = doc.getId();
                                        mListIds.add(liste);
                                        DocumentReference ref = db.collection(LISTEN_COLLECTION).document(liste);
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
                        });
                    }
                }
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    public void NeueListeButton(View view) throws InterruptedException {
        progressBar.setVisibility(View.VISIBLE);
        addList();
    }

    private void addList() throws InterruptedException {
        String sListenName = eTNeueListe.getText().toString();
        if (sListenName.isEmpty()) {
            Toast.makeText(Gruppenauswahl.this, "Geben Sie einen Listennamen ein!", Toast.LENGTH_LONG).show();
            eTNeueListe.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }
        final Map<String, Object> ListenNameMap = new HashMap<>();
        ListenNameMap.put(LISTEN_NAME, sListenName);
        ListeExistiertBereits = false;
        //Abfrage, ob es schon eine so benannte Liste gibt:
        final CollectionReference listen = db.collection(LISTEN_COLLECTION);
        Query listenQuery = listen.whereEqualTo(LISTEN_NAME, sListenName);
        listenQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ListeExistiertBereits = true;
                    }
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (ListeExistiertBereits) {
                    Toast.makeText(Gruppenauswahl.this, "Die Liste existiert bereits!", Toast.LENGTH_SHORT).show();
                    eTNeueListe.getText().clear();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                final DocumentReference neueListeRef = db.collection(LISTEN_COLLECTION).document();
                neueListeRefString = neueListeRef.getId();
                neueListeRef.set(ListenNameMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Map<String, Object> UserMap = new HashMap<>();
                        UserMap.put(USER_ID, UserId);
                        UserMap.put(USER_EMAIL, UserEmail);
                        neueListeRef.collection(MITGLIEDER).document().set(UserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Gruppenauswahl.this, "Liste hinzugefügt", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                toGruppenManager(neueListeRefString);
                            }
                        });

                    }
                });
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item3:
                AlertDialog.Builder adb = new AlertDialog.Builder(Gruppenauswahl.this);
                adb.setTitle("Logout");
                adb.setMessage("Möchten Sie sich wirklich ausloggen?");
                adb.setNegativeButton("Zurück", null);
                adb.setPositiveButton("Logout", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();

                        toMainActiviy();
                    }
                });
                adb.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void toGruppenManager(String ListeRefString) {
        String intentText = "New Activity";
        Intent toGroup =
                new Intent(Gruppenauswahl.this, GruppenManager.class);
        toGroup.putExtra(LISTEN_REFERENZ, ListeRefString);
        startActivity(toGroup);
    }

    private void toListe(String ListeRefString) {
        String intentText = "New Activity";
        Intent toGroup =
                new Intent(Gruppenauswahl.this, Liste.class);
        toGroup.putExtra(LISTEN_REFERENZ, ListeRefString);
        startActivity(toGroup);
    }

    private void toMainActiviy() {
        String intentText = "New Activity";
        Intent toGroupSet =
                new Intent(Gruppenauswahl.this, MainActivity.class);
        toGroupSet.putExtra("NEXTACTIVITY", intentText);
        startActivity(toGroupSet);
    }

}
