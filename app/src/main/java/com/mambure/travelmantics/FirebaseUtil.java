package com.mambure.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static int RC_SIGN_IN = 123;
    static FirebaseDatabase mFirebaseDatabase;
    static DatabaseReference mDatabaseReference;
    static FirebaseUtil mFirebaseUtil;
    static FirebaseAuth mFirebaseAuth;
    static FirebaseAuth.AuthStateListener mAuthStateListener;
    static ArrayList<TravelDeal> travelDeals;
    static ListActivity caller;
    static boolean isAdmin;



    private FirebaseUtil() {
    }

    static void openFbReference(String ref, final ListActivity callerActivity) {
        if (mFirebaseUtil == null) {
            mFirebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (mFirebaseAuth.getCurrentUser() == null) {
                        FirebaseUtil.signIn();
                    } else {
                        checkAdmin(mFirebaseAuth.getCurrentUser().getUid());
                    }

                }
            };
        }
        travelDeals = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    private static void checkAdmin(String uid) {
        FirebaseUtil.isAdmin = false;
        mFirebaseDatabase.getReference().child("administrators").
                child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin = true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUtil.isAdmin = false;
                caller.showMenu();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    static void attacheListener() {
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    static void removeListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    private static void signIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        caller.startActivityForResult(
                AuthUI.getInstance().
                        createSignInIntentBuilder().
                        setAvailableProviders(providers).
                        build(), RC_SIGN_IN
        );
    }
}
