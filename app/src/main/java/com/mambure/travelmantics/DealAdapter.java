package com.mambure.travelmantics;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder>
        implements ChildEventListener{


    private ArrayList<TravelDeal> deals = new ArrayList<>() ;

    public DealAdapter(ListActivity activity) {
        FirebaseUtil.openFbReference("travelDeals", activity);
        FirebaseUtil.mDatabaseReference.addChildEventListener(this);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.deal_list_item, parent, false);

        return new DealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        holder.bind(deals.get(position));
    }


    @Override
    public int getItemCount() {
        return deals.size();
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        TravelDeal deal = dataSnapshot.getValue(TravelDeal.class);
        deal.setId(dataSnapshot.getKey());
        deals.add(deal);
        notifyItemInserted(deals.size() - 1);

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        TravelDeal deal = dataSnapshot.getValue(TravelDeal.class);
        deal.setId(dataSnapshot.getKey());
        int position = deals.indexOf(deal);
        deals.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvPrice;
        private ImageView imageView;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTItle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imageView = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
        }

        public void bind(TravelDeal deal) {
            tvTitle.setText(deal.getTitle());
            tvDescription.setText(deal.getDescription());
            tvPrice.setText(deal.getPrice());
            showImage(deal.getImageUrl());
        }

        private void showImage(String url) {

            if (url != null && !url.isEmpty()) {
                GlideApp.with(imageView).
                        load(url).
                        centerCrop().override(80, 80).
                        diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).
                        into(imageView);

            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d("Click: ", String.valueOf(position));
            Intent intent = new Intent(v.getContext(), DealActivity.class);
            intent.putExtra("travelDeal", deals.get(position));
            v.getContext().startActivity(intent);
        }
    }


}
