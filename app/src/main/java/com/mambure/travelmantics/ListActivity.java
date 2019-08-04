package com.mambure.travelmantics;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DealAdapter mDealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mRecyclerView = findViewById(R.id.rvDeals);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mDealAdapter = new DealAdapter();
        mRecyclerView.setAdapter(mDealAdapter);
    }
}
