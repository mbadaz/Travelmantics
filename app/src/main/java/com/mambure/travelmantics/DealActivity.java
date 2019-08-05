package com.mambure.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DealActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private TravelDeal mDeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        Intent intent = getIntent();
        mDeal = (TravelDeal) intent.getSerializableExtra("travelDeal");
        if (mDeal == null) {
            mDeal = new TravelDeal();
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("travelDeals");

        titleEditText = findViewById(R.id.txtTitle);
        titleEditText.setText(mDeal.getTitle());
        descriptionEditText = findViewById(R.id.txtDescription);
        descriptionEditText.setText(mDeal.getDescription());
        priceEditText = findViewById(R.id.txtPrice);
        priceEditText.setText(mDeal.getPrice());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deal_activity_menu, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.menuItemSave).setVisible(true);
            menu.findItem(R.id.menuDeleteDeal).setVisible(true);
            enableEditTexts(true);
        } else {
            menu.findItem(R.id.menuItemSave).setVisible(false);
            menu.findItem(R.id.menuDeleteDeal).setVisible(false);
            enableEditTexts(false);
        }
        return true;
    }

    private void enableEditTexts(boolean isEnabled) {
        titleEditText.setEnabled(isEnabled);
        priceEditText.setEnabled(isEnabled);
        descriptionEditText.setEnabled(isEnabled);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemSave:
                saveDeal();
                Toast.makeText(this, R.string.saved_toast, Toast.LENGTH_SHORT).show();
                getBack();
                break;
            case R.id.menuDeleteDeal:
                deleteDeal();
                break;
        }
        return true;
    }

    private void saveDeal() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String price = priceEditText.getText().toString();
        mDeal.setTitle(title);
        mDeal.setDescription(description);
        mDeal.setPrice(price);
        if (mDeal.getId() == null) {
            mDatabaseReference.push().setValue(mDeal);
        } else {
            mDatabaseReference.child(mDeal.getId()).setValue(mDeal);
        }
        clean();
    }

    private void deleteDeal() {
        if (mDeal == null) {
            Toast.makeText(this, "Create and save deal first!", Toast.LENGTH_SHORT).show();
        } else {
            mDatabaseReference.child(mDeal.getId()).removeValue();
            Toast.makeText(this, "Deal deleted!", Toast.LENGTH_SHORT).show();
            getBack();
        }
    }

    private void getBack() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void clean() {
        titleEditText.setText("");
        descriptionEditText.setText("");
        priceEditText.setText("");
    }
}
