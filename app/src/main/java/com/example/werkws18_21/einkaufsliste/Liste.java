package com.example.werkws18_21.einkaufsliste;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

    private static final String LISTEN_REFERENZ = "Listen-Referenz";
    private static final String LISTEN_NAME = "ListenName";
    private static final String LISTEN_COLLECTION = "Listen";
    private static final String USER_EMAIL = "User-Email";
    private static final String MITGLIEDER = "Mitglieder";

    //Strings, die Online-Verzeichnisse benennen-alt
    private static final String LIST_COLLECTION = "Listen";
    private static final String ITEMS_COLLECTION = "Items";
    private static final String LIST_NAME = "Listen-Name";
    // Listen, Buttons und ArrayList
    private ListView listView;
    private ListView listView2;
    private EditText eingabe;
    private Button adden;
    private TextView tvListenName;
    DocumentReference ListeRef;
    String ListenName;
    String ListeRefString;


    private static final String TAG = "MyTag";
    //db als Instanz für die Datenbank im firestore
    FirebaseFirestore db;

    //String-Arrays, die Ids und Namen aller Listen des Nutzers enthalten TODO: noch ohne Funktion
    private ArrayList<String> mListIds = new ArrayList<>();
    private ArrayList<String> mListNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

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

        Intent toGroup = getIntent();
        if (toGroup.getExtras() != null) {
            ListeRefString = toGroup.getExtras().get(LISTEN_REFERENZ).toString();
            ListeRef = db.collection(LISTEN_COLLECTION).document(ListeRefString);
        }
        getListenName();
        //ListView der zu kaufenden Sachen
        listView = (ListView) findViewById(R.id.ListView);
        listView2 = (ListView) findViewById(R.id.ListView2);
        tvListenName=findViewById(R.id.tvListenName);

        final ArrayList<String> itemList = new ArrayList<>();
        final ArrayList<String> beispielList = new ArrayList<>();
        eingabe = (EditText) findViewById(R.id.Eingabe);
        adden = (Button) findViewById(R.id.button10);
        final ArrayAdapter<String> adapter;
        final ArrayAdapter<String> adapter1;

        /*adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, itemList){
          public View getView(int position, View convertView, ViewGroup parent);
              itemList.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
        };*/
        // adapter mit den drei Parametern
        adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, itemList);
        adapter1 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, beispielList);
        // data setzen in der ListView
        listView.setAdapter(adapter);
        listView2.setAdapter(adapter1);
        //aufruf soll ausgewähltes oder eingegebenes Item zur ausgewählten Liste hinzufügen
        // war vorher addItem Funktion
        adden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.add(eingabe.getText().toString());
                adapter.notifyDataSetChanged();
                eingabe.getText().clear();
            }
        });


        listView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(Liste.this);
                adb.setTitle("Delete?");
                adb.setMessage("Möchten sie das Produkt entfernen?");
                final int positionToRemove = position;
                adb.setNegativeButton("Zurück", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        beispielList.remove(positionToRemove);
                        adapter1.notifyDataSetChanged();
                    }
                });
                adb.show();
                return true;
            }
        });
        //Funktion zum entfernen von Items
        //TODO: MyDataObject ist die Datenbank musst du dann noch umbenennen und einfügen
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final int positionToRemove = position;
                beispielList.add(0, itemList.get(position));
                adapter1.notifyDataSetChanged();
                itemList.remove(positionToRemove);
                adapter.notifyDataSetChanged();
            }
        });
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final int positionToRemove = position;
                itemList.add(beispielList.get(position));
                beispielList.remove(positionToRemove);
                adapter1.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
            }
        });
    }//onCreate-Ende

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
        for (String x : mListIds) {
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
