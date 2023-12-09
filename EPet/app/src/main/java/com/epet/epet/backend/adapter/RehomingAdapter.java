package com.epet.epet.backend.adapter;

import android.Manifest;
import android.appwidget.AppWidgetHost;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.epet.epet.AddRehomingActivity;
import com.epet.epet.R;
import com.epet.epet.backend.Rehoming;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RehomingAdapter extends RecyclerView.Adapter<RehomingAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public ImageView phone;
        public TextView username;
        public TextView description;
        public CardView card_view;


        public ViewHolder(View view) {
            super(view);

            card_view = (CardView)view.findViewById(R.id.rehoming_card);
            imageView = (ImageView)view.findViewById(R.id.rehoming_image);
            username = (TextView)view.findViewById(R.id.rehoming_username);
            description = (TextView)view.findViewById(R.id.rehonming_comment);
            phone = (ImageView)view.findViewById(R.id.phone);
        }
    }

    List<Rehoming> list_rehoming;
    public RehomingAdapter(List<Rehoming> list_rehoming) {

        this.list_rehoming = list_rehoming;
    }
    @Override
    public RehomingAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rehoming_post, parent, false);
        final RehomingAdapter.ViewHolder view_holder = new RehomingAdapter.ViewHolder(v);
        v.setTag(view_holder.getPosition());
        return view_holder;
    }


    @Override
    public void onBindViewHolder(final RehomingAdapter.ViewHolder holder, final int position) {
        holder.username.setText(list_rehoming.get(position).getUsername());
        holder.description.setText(list_rehoming.get(position).getDescription());
        if (!list_rehoming.get(position).getImageUrl().isEmpty()){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference = storageReference.child(list_rehoming.get(position).getImageUrl());
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().centerCrop().into(holder.imageView);
                }
            });
        }
        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + list_rehoming.get(position).getPhoneNumber() + ""));

                if (ActivityCompat.checkSelfPermission(holder.phone.getContext().getApplicationContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(view.getContext(), "Arama yapabilmek için lütfen arama iznini açınız!", Toast.LENGTH_SHORT).show();
                    return;
                }
                holder.phone.getContext().startActivity(callIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list_rehoming.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}