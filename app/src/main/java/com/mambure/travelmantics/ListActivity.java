package com.mambure.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DealAdapter mDealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        FirebaseUtil.openFbReference("travelDeals", this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_activity_menu, menu);
        return  true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.menu_addNewDeal).setVisible(true);
        } else {
            menu.findItem(R.id.menu_addNewDeal).setVisible(false);
        }
        showMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_addNewDeal:
                Intent intent = new Intent(this, DealActivity.class);
                intent.setAction(DealActivity.class.getSimpleName());
                startActivity(intent);
                break;
            case R.id.menu_logout:
                logout();
            default:
                super.onOptionsItemSelected(item);
                break;

        }

        return true;
    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Login", "");
                    }
                });
        FirebaseUtil.attacheListener();
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.removeListener();
    }


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.attacheListener();
        mRecyclerView = findViewById(R.id.rvDeals);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mDealAdapter = new DealAdapter(this);
        mRecyclerView.setAdapter(mDealAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FirebaseUtil.RC_SIGN_IN && data != null) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                handleLoginScenario(response);
            } else {
                Log.e(ListActivity.class.getSimpleName(), "Firebase error", response.getError());
            }
        }
    }

    private void handleLoginScenario(IdpResponse response) {
        if (response != null && response.isNewUser()) {
            Toast.makeText(this, "Welcome to Travelmantics!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
        }
    }

    public void showMenu() {
        invalidateOptionsMenu();
    }
}
