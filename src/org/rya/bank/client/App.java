package org.rya.bank.client;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.firebase.database.core.view.QuerySpec;
import com.google.firebase.database.snapshot.Index;
import com.google.gson.Gson;
import com.sun.tools.hat.internal.parser.Reader;
import com.sun.tools.javac.util.Convert;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;


public class App implements Runnable {

    public static void main(String[] args) {
        App app = new App();
        Thread thread = new Thread(app);
        thread.setDaemon(false);
        thread.start();
    }

    public static void printJsonObject(JSONObject jsonObj) {
        for (Object key : jsonObj.keySet()) {
            //based on you key types
            String keyStr = (String)key;
            Object keyvalue = jsonObj.get(keyStr);

            //Print key and value
            System.out.println("key: "+ keyStr + " value: " + keyvalue);

            //for nested objects iteration if required
            if (keyvalue instanceof JSONObject)
                printJsonObject((JSONObject)keyvalue);
        }
    }

    private static String getUrlParams(Map<String, String> params) {
        if (params == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String key : params.keySet()) {
            try {
                sb.append(key).append("=").append(URLEncoder.encode(params.get(key), "utf8")).append("&");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }
        String rc = sb.toString();
        if (rc.endsWith("&")) {
            rc = rc.substring(0, rc.length() - 1);
        }
        return rc;
    }

    private JSONObject getJsonResponse(Map<String, String> params) throws IOException {
        JSONObject response;
        HttpURLConnection connection = null;
        //String host = "http://testnet-server6.ryacoin.io";
        String host = "localhost";
        String protocol = "http";

        int port = 8876;
        String urlParams = getUrlParams(params);
        System.out.println("our req" + urlParams);

        URL url;
        url = new URL(protocol, host, port, "/nxt?" + urlParams);

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            System.out.println("got HTTP OK");
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        }
//            try (Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
//                response = (JSONObject) JSONValue.parse(reader);
//            }
//        } else {
//            response = null;
//        }
//
//        if (response == null) {
//            throw new IllegalStateException(String.format("Request %s response error", url));
//        }
//        if (response.get("errorCode") != null) {
//            throw new IllegalStateException(String.format("Request %s produced error response code %s message \"%s\"",
//                    url, response.get("errorCode"), response.get("errorDescription")));
//        }
//        if (response.get("error") != null) {
//
//            throw new IllegalStateException(String.format("Request %s produced error %s",
//                    url, response.get("error")));
//        }
        return null;

    }
    private JSONObject send_loan(String durationBlocks, String amountNQT, String intrest_rate, String recipient) {//aaaa
//        params:@{@"requestType": @"loanMoney",
//            @"recipient": @"RYA-ABEK-N8VW-5ME8-CGF53",
//            @"amountNQT": @"10000000000",
//            @"secretPhrase": @"afraid very gotten disappear stun desperate fault express reason knowledge different glory",
//            @"feeNQT": @"200000000",
//            @"loanDuration": @"50",
//            @"loanInterestRateNQT": @"1000000000",
//            @"deadline": @"10000"
            Map<String, String> params = new HashMap<>();
            params.put("requestType", "loanMoney");
            Long amount = Long.parseLong(amountNQT);
            params.put("amountNQT", String.valueOf(amount* 100000000));
            params.put("secretPhrase", "afraid very gotten disappear stun desperate fault express reason knowledge different glory");
            params.put("feeNQT",String.valueOf(amount* 20000000));
            params.put("loanDuration", durationBlocks);
            params.put("loanInterestRateNQT", String.valueOf(amount * 200000));
            params.put("deadline", "10000");
            params.put("recipient", recipient);


        try {
            return getJsonResponse(params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

//                Object res = dataSnapshot.getValue();
//                //System.out.println("onDataChange:" + res);
//                Gson gson = new Gson();
//                String resJson = gson.toJson(res);
//                System.out.println("onDataChange resJson:" + resJson);

                Iterable<DataSnapshot> iter = dataSnapshot.getChildren();
                while (iter.iterator().hasNext()) {
                    DataSnapshot singleDataSnapshot = iter.iterator().next();
                    System.out.println("jambo:" + singleDataSnapshot.toString());
                    System.out.println("class:" + singleDataSnapshot.getValue().getClass().toString());
                    HashMap loanMap = (HashMap)singleDataSnapshot.getValue();
                    System.out.println("amount is :" + loanMap.get("amount"));
                    System.out.println("amount is :" + loanMap.get("amount"));
                    System.out.println("duration is :" + loanMap.get("durationBlocks"));
                    System.out.println("loanToAddress is :" + loanMap.get("loanToAddress"));


                    send_loan(loanMap.get("durationBlocks").toString(), loanMap.get("amount").toString(),
                            "1000", "RYA-ABEK-N8VW-5ME8-CGF53");
                    database.child("loans").child(singleDataSnapshot.getKey()).child("status").setValueAsync(2);

                }
                //System.out.println("koko:" + Integer.parseInt(jo.getJSONObject("loan5").toString()));

                //Integer amount =  Integer.parseInt(jo.getJSONObject("loan5").getString("amount"));

                //System.out.println("amount:" + amount.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("onCancelled:" + databaseError);
            }
        });
        child.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot singleDataSnapshot, String prevChildKey) {
;
                    System.out.println("jambo:" + singleDataSnapshot.toString());
                    System.out.println("class:" + singleDataSnapshot.getValue().getClass().toString());
                    HashMap loanMap = (HashMap)singleDataSnapshot.getValue();
                    System.out.println("amount is :" + loanMap.get("amount"));
                    System.out.println("amount is :" + loanMap.get("amount"));
                    System.out.println("duration is :" + loanMap.get("durationBlocks"));
                    System.out.println("loanToAddress is :" + loanMap.get("loanToAddress"));


                    send_loan(loanMap.get("durationBlocks").toString(), loanMap.get("amount").toString(),
                            "1000", "RYA-ABEK-N8VW-5ME8-CGF53");
                    database.child("loans").child(singleDataSnapshot.getKey()).child("status").setValueAsync(2);

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
