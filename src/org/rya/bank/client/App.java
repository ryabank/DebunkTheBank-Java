package org.rya.bank.client;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.firebase.database.core.view.QuerySpec;
import com.google.firebase.database.snapshot.Index;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class App implements Runnable {

    public static void main(String[] args) {
        App app = new App();
        Thread thread = new Thread(app);
        thread.setDaemon(false);
        thread.start();
    }

    public void run() {
        FileInputStream serviceAccount;
        try {
            serviceAccount = new FileInputStream("ryabank2018-firebase-adminsdk-20e9p-085e5f6e17.json");
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }

        FirebaseOptions options;
        try {
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://ryabank2018.firebaseio.com")
                    .build();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);

        firebaseApp.getName();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference child = database.child("loans");
        Query query = child.orderByKey();
        QuerySpec spec = query.getSpec();
        System.out.println("query" + query);
        System.out.println("spec" + spec);
        Index index = spec.getIndex();
        System.out.println("index:" + index);
        System.out.println(index.getQueryDefinition());
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object res = dataSnapshot.getValue();
                System.out.println("onDataChange:" + res);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("onCancelled:" + databaseError);
            }
        });
        child.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("onChildAdded:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println("onChildChanged:" + dataSnapshot.getKey() + "/" + dataSnapshot.getValue());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println("onChildRemoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                System.out.println("onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("onCancelled2:" + databaseError);
            }

            // ...
        });
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException ignored) {}
        }
    }
}
