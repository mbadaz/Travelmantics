package com.mambure.travelmantics;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
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
    private ProgressBar mProgressBar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        FirebaseUtil.openFbReference("travelDeals", this);
        FirebaseUtil.checkAdmin();
        IdlingResourceUtil.get().increment();
        Log.d("IdlingResource:", "onCreate increment");
        mProgressBar = findViewById(R.id.progressBar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_activity_menu, menu);
        menu.findItem(R.id.menu_addNewDeal).setVisible(false);
        return  true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.menu_addNewDeal).setVisible(true);
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
        showMenu();
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
        IdlingResourceUtil.get().increment();
        Log.d("IdlingResource:", "onPause increment");
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
        mDealAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                disableProgessSpinner();
                IdlingResourceUtil.get().decrement();
                Log.d("IdlingResource:", "Adapter decrement");
                mDealAdapter.unregisterAdapterDataObserver(this);
            }
        });
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

    public void disableProgessSpinner() {
        mProgressBar.setVisibility(View.GONE);
    }

    @VisibleForTesting
    public DealAdapter getDealAdapter() {
        return mDealAdapter;
    }

}
