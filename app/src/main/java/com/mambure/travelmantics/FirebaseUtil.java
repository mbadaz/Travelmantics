package com.mambure.travelmantics;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseUtil {

    static FirebaseDatabase mFirebaseDatabase;
    static DatabaseReference mDatabaseReference;
    static FirebaseUtil mFirebaseUtil;
    static ArrayList<TravelDeal> travelDeals;


    private FirebaseUtil() {
    }

    static void openFbReference(String ref) {
        if (mFirebaseUtil == null) {
            mFirebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            travelDeals = new ArrayList<>();
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }
}
