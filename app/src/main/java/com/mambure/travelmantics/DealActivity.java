package com.mambure.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class DealActivity extends AppCompatActivity {

    public static final String TAG = DealActivity.class.getSimpleName();
    public static final int REQUEST_CODE = 125;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private ImageView imageView;
    private TravelDeal mDeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        Intent intent = getIntent();
        mDeal = (TravelDeal) intent.getSerializableExtra("travelDeal");
        if (mDeal == null) {
            mDeal = new TravelDeal();
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("travelDeals");
        imageView = findViewById(R.id.image);
        titleEditText = findViewById(R.id.txtTitle);
        titleEditText.setText(mDeal.getTitle());
        descriptionEditText = findViewById(R.id.txtDescription);
        descriptionEditText.setText(mDeal.getDescription());
        priceEditText = findViewById(R.id.txtPrice);
        priceEditText.setText(mDeal.getPrice());

        showImage(mDeal.getImageUrl());

        Button btnImage = findViewById(R.id.buttonAddImage);
        if (!FirebaseUtil.isAdmin) {
            btnImage.setEnabled(false);
        }
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "Select an image"), REQUEST_CODE);
            }
        });

        IdlingResourceUtil.get().decrement();
        Log.d("IdlingResource:", "onCreate decrement");

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void enableEditTexts(boolean isEnabled) {
        titleEditText.setEnabled(isEnabled);
        priceEditText.setEnabled(isEnabled);
        descriptionEditText.setEnabled(isEnabled);
    }

    void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            GlideApp.with(imageView).
                    load(url).
                    diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).
                    override(width, width * 2 / 3).
                    into(imageView);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemSave:
                Log.d(TAG, "save menu item clicked");
                saveDeal();
                Toast.makeText(this, R.string.saved_toast, Toast.LENGTH_SHORT).show();
                getBack();
                break;
            case R.id.menuDeleteDeal:
                Log.d(TAG, "delete menu item clicked");
                deleteDeal();
                break;
        }
        return true;
    }

    private void saveDeal() {
//        IdlingResourceUtil.get().increment();
//        Log.d("IdlingResource:", "save deal increment");
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
            StorageReference ref = FirebaseUtil.mFirebaseStorage.getReference().child(mDeal.getImageName());
            ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Delete image", "Successful");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Delete image", "Failed: " + e.getMessage());
                }
            });

            Toast.makeText(this, "Deal deleted!", Toast.LENGTH_SHORT).show();
            getBack();
        }
    }

    private void getBack() {
        Intent intent = new Intent(this, ListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void clean() {
        titleEditText.setText("");
        descriptionEditText.setText("");
        priceEditText.setText("");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            final StorageReference ref = FirebaseUtil.mStorageReference.
                    child(imageUri.getLastPathSegment());

            StorageMetadata metadata = new StorageMetadata.Builder().
                    setCustomMetadata(Boolean.toString(FirebaseUtil.isAdmin), "").
                    build();
            UploadTask uploadTask = ref.putFile(imageUri, metadata);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    String url = task.getResult().toString();
                    String pictureName = task.getResult().getPath();
                    mDeal.setImageUrl(url);
                    mDeal.setImageName(pictureName);
                    showImage(url);

                }
            });

        }

    }
}
