package com.example.werkws18_21.einkaufsliste;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

public class Liste extends AppCompatActivity {

    private static final String LISTEN_REFERENZ = "Listen-Referenz";
    private static final String LISTEN_NAME = "ListenName";
    private static final String LISTEN_COLLECTION = "Listen";
    private static final String USER_EMAIL = "User-Email";
    private static final String MITGLIEDER = "Mitglieder";

    //Strings, die Online-Verzeichnisse benennen-alt
    private static final String LIST_COLLECTION = "Listen";
    private static final String ITEMS_COLLECTION = "Items";
    private static final String LIST_NAME = "Listen-Name";
    private static final String ITEM_NAME = "Item-Name";
    private static final String BEISPIELE_COLLECTION = "Beispiele";
    // Listen, Buttons und ArrayList
    private ListView listView;
    private ListView listView2;
    private EditText eingabe;
    private Button adden;
    private TextView tvListenName;
    DocumentReference ListeRef;
    String ListenName;
    String ListeRefString;
    ArrayList<String> itemList;
    ArrayList<String> beispielList;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter1;


    private static final String TAG = "MyTag";
    //db als Instanz für die Datenbank im firestore
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.my_toolbar1);
        setSupportActionBar(toolbar);

        //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);

        /*public boolean onOptionsItemSelected(myToolbar item) {
            switch (myToolbar.getItemId()) {
                case R.id.action_settings:
                    // User chose the "Settings" item, show the app settings UI...
                    return true;

                case R.id.Account_settings:
                    // User chose the "Favorite" action, mark the current item
                    // as a favorite...
                    return true;

                default:
                    // If we got here, the user's action was not recognized.
                    // Invoke the superclass to handle it.
                    return super.onOptionsItemSelected(item);

            }
        }*/

        //db als Instanz für die Datenbank im firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Intent toGroup = getIntent();
        if (toGroup.getExtras() != null) {
            ListeRefString = toGroup.getExtras().get(LISTEN_REFERENZ).toString();
            ListeRef = db.collection(LISTEN_COLLECTION).document(ListeRefString);
        }
        getListenName();
        //ListView der zu kaufenden Sachen
        listView = (ListView) findViewById(R.id.ListView);
        listView2 = (ListView) findViewById(R.id.ListView2);
        tvListenName = findViewById(R.id.tvListenName);

        itemList = new ArrayList<>();
        beispielList = new ArrayList<>();
        eingabe = (EditText) findViewById(R.id.Eingabe);
        adden = (Button) findViewById(R.id.button10);
        //final ArrayAdapter<String> adapter;
        //final ArrayAdapter<String> adapter1;

        /*adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, itemList){
          public View getView(int position, View convertView, ViewGroup parent);
              itemList.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
        };*/
        // adapter mit den drei Parametern
        //adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, itemList);
        //adapter1 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, beispielList);
        // data setzen in der ListView
        //listView.setAdapter(adapter);
        //listView2.setAdapter(adapter1);

        // Adapter mit Textgröße und Feldgröße für die MainListe
        adapter = new ArrayAdapter<String>(Liste.this, android.R.layout.simple_list_item_1, itemList) {
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
        listView.setAdapter(adapter);

        // Adapter mit Textgröße und Feldgröße für Beispiele
        adapter1 = new ArrayAdapter<String>(Liste.this, android.R.layout.simple_list_item_1, beispielList) {
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
        listView2.setAdapter(adapter1);

        getBeispiele();
        getItems();


        //aufruf soll ausgewähltes oder eingegebenes Item zur ausgewählten Liste hinzufügen
        // war vorher addItem Funktion
        adden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Überprüfen, ob etwas in die TextView geschrieben wurde
                if (eingabe.getText().toString().isEmpty()) {
                    Toast.makeText(Liste.this, "Geben Sie einen Text ein!", Toast.LENGTH_LONG).show();
                    eingabe.requestFocus();
                    //progressBar.setVisibility(View.GONE);
                    return;
                }

                itemList.add(eingabe.getText().toString());
                adapter.notifyDataSetChanged();
                eingabe.getText().clear();
                uploadItems();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(Liste.this);
                adb.setTitle("Entfernen?");
                adb.setMessage("Möchten Sie das Produkt entfernen?");
                final int positionToRemove = position;
                adb.setNegativeButton("Zurück", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        itemList.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        uploadItems();
                    }
                });
                adb.show();
                return true;
            }
        });

        listView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(Liste.this);
                adb.setTitle("Entfernen?");
                adb.setMessage("Möchten Sie das Produkt entfernen?");
                final int positionToRemove = position;
                adb.setNegativeButton("Zurück", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        beispielList.remove(positionToRemove);
                        adapter1.notifyDataSetChanged();
                        uploadBeispiele();
                    }
                });
                adb.show();
                return true;
            }
        });
        //Funktion zum entfernen von Items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final int positionToRemove = position;
                beispielList.add(0, itemList.get(position));
                Collections.sort(beispielList,String.CASE_INSENSITIVE_ORDER);
                adapter1.notifyDataSetChanged();
                itemList.remove(positionToRemove);
                adapter.notifyDataSetChanged();
                uploadBeispiele();
                uploadItems();
            }
        });
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final int positionToRemove = position;
                itemList.add(beispielList.get(position));
                Collections.sort(itemList,String.CASE_INSENSITIVE_ORDER);
                beispielList.remove(positionToRemove);
                adapter1.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                uploadBeispiele();
                uploadItems();
            }
        });
    }//onCreate-Ende

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.item1:
                String intentText = "New Activity";
                Intent toGroupSet =
                        new Intent(Liste.this, Gruppenauswahl.class);
                toGroupSet.putExtra("NEXTACTIVITY", intentText);
                startActivity(toGroupSet);
                return true;
            case R.id.item2 :
                String intentText1 = "New Activity";
                Intent toGroup =
                        new Intent(Liste.this, GruppenManager.class);
                toGroup.putExtra(LISTEN_REFERENZ, ListeRefString);
                startActivity(toGroup);
                return true;
            case R.id.item3:
                AlertDialog.Builder adb = new AlertDialog.Builder(Liste.this);
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
            default:  return super.onOptionsItemSelected(item);
        }

    }

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

    private void uploadItems() {
        final CollectionReference items = ListeRef.collection(ITEMS_COLLECTION);

        items.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String x = doc.getId();
                        items.document(x).delete();
                    }
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (String x : itemList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(ITEM_NAME, x);
                    items.document().set(map);
                }
            }
        });

    }

    private void uploadBeispiele() {
        final CollectionReference beispiele = ListeRef.collection(BEISPIELE_COLLECTION);

        beispiele.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String x = doc.getId();
                        beispiele.document(x).delete();
                    }
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (String x : beispielList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(ITEM_NAME, x);
                    beispiele.document().set(map);
                }
            }
        });

    }

    private void getItems() {
        CollectionReference items = ListeRef.collection(ITEMS_COLLECTION);
        items.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    itemList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        itemList.add(doc.get(ITEM_NAME).toString());
                        adapter.notifyDataSetChanged();
                    }
                    Collections.sort(itemList,String.CASE_INSENSITIVE_ORDER);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void getBeispiele() {
        Query query = ListeRef.collection(BEISPIELE_COLLECTION);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    beispielList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        beispielList.add(doc.get(ITEM_NAME).toString());
                    }
                    Collections.sort(beispielList,String.CASE_INSENSITIVE_ORDER);
                    adapter1.notifyDataSetChanged();
                }
            }
        });
    }



    public void toGruppenauswahl(View view) {
        String intentText = "New Activity";
        Intent toGroupSet =
                new Intent(Liste.this, Gruppenauswahl.class);
        toGroupSet.putExtra("NEXTACTIVITY", intentText);
        startActivity(toGroupSet);
    }

    public void toGruppenManager(View view) {
        String intentText = "New Activity";
        Intent toGroup =
                new Intent(Liste.this, GruppenManager.class);
        toGroup.putExtra(LISTEN_REFERENZ, ListeRefString);
        startActivity(toGroup);
    }
    private void toMainActiviy() {
        String intentText = "New Activity";
        Intent toGroupSet =
                new Intent(Liste.this, MainActivity.class);
        toGroupSet.putExtra("NEXTACTIVITY", intentText);
        startActivity(toGroupSet);
    }

    public void aktualisierenButton(View view){
        getItems();
        getBeispiele();
    }

}
