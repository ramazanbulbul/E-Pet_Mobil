package com.epet.epet.backend.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.epet.epet.R;
import com.epet.epet.backend.Treatment;

import java.util.List;

public class TreatmentAdapter extends RecyclerView.Adapter<TreatmentAdapter.ViewHolder> {

public static class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView vetLogo;
    public TextView clinicName;
    public TextView title;
    public TextView description;
    public TextView date;
    public CardView card_view;


    public ViewHolder(View view) {
        super(view);

        card_view = (CardView)view.findViewById(R.id.treatment_card);
        vetLogo = (ImageView)view.findViewById(R.id.vet_logo);
        clinicName = (TextView)view.findViewById(R.id.vet_name);
        title = (TextView)view.findViewById(R.id.vet_operation);
        description = (TextView)view.findViewById(R.id.vet_detail);
        date = (TextView)view.findViewById(R.id.vet_date);

    }
}

    List<Treatment> list_treatment;
    public TreatmentAdapter(List<Treatment> list_treatment) {

        this.list_treatment = list_treatment;
    }
    @Override
    public TreatmentAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.saglikbilgisi_post, parent, false);
        final ViewHolder view_holder = new ViewHolder(v);
        v.setTag(view_holder.getPosition());
        return view_holder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.clinicName.setText(list_treatment.get(position).getClinicName());
        holder.title.setText(list_treatment.get(position).getTitle());
        holder.description.setText(list_treatment.get(position).getDescription());
        holder.date.setText("İşlem tarihi:" + list_treatment.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return list_treatment.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}