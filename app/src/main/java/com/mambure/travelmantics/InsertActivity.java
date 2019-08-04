package com.mambure.travelmantics;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("travelDeals");

        titleEditText = findViewById(R.id.txtTitle);
        descriptionEditText = findViewById(R.id.txtDescription);
        priceEditText = findViewById(R.id.txtPrice);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemSave:
                saveDeal();
                Toast.makeText(this, R.string.saved_toast, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void saveDeal() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String price = priceEditText.getText().toString();
        TravelDeal travelDeal = new TravelDeal(title, description, price, "");
        mDatabaseReference.push().setValue(travelDeal);
        clean();
    }

    private void clean() {
        titleEditText.setText("");
        descriptionEditText.setText("");
        priceEditText.setText("");
    }
}
