package com.epet.epet.ui.health.tabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.epet.epet.AddPetActivity;
import com.epet.epet.ForgotActivity;
import com.epet.epet.R;
import com.epet.epet.backend.business.PetBusiness;
import com.epet.epet.ui.health.HealthFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class tab_petlerim extends Fragment {

    Button btnPetAdd;

    ImageView imageView;
    TextView txtId, txtName, txtType, txtBreed, txtBirthday;
    CardView view2;
    LinearLayout petDetail, petDetail2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.tab_petlerim, container, false);

        btnPetAdd = root.findViewById(R.id.button_pet_add);
        btnPetAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gecisYap = new Intent(view.getContext().getApplicationContext(), AddPetActivity.class);
                startActivity(gecisYap);
            }
        });
        imageView = root.findViewById(R.id.pet_image);
        txtId = root.findViewById(R.id.pet_id);
        txtName = root.findViewById(R.id.pet_name);
        txtType = root.findViewById(R.id.pet_type);
        txtBreed = root.findViewById(R.id.pet_genus);
        txtBirthday = root.findViewById(R.id.pet_age);
        view2 = root.findViewById(R.id.view2);
        petDetail = root.findViewById(R.id.petDetail);
        petDetail2 = root.findViewById(R.id.petDetail2);

        if (HealthFragment.allPets != null && HealthFragment.allPets.size() > 0) {
            petDetail.setVisibility(View.VISIBLE);
            view2.setVisibility(View.VISIBLE);
            txtId.setVisibility(View.VISIBLE);
            petDetail2.setBackgroundResource(R.drawable.second_layer);


            txtId.setText(String.valueOf(HealthFragment.allPets.get(HealthFragment.lastPet).getId()));
            txtBirthday.setText(HealthFragment.allPets.get(HealthFragment.lastPet).getBirthdate());
            txtBreed.setText(HealthFragment.allPets.get(HealthFragment.lastPet).getBreed());
            txtName.setText(HealthFragment.allPets.get(HealthFragment.lastPet).getName());
            txtType.setText(HealthFragment.allPets.get(HealthFragment.lastPet).getType());
            ;
            if (!HealthFragment.allPets.get(HealthFragment.lastPet).getImageUrl().isEmpty()) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                storageReference = storageReference.child(HealthFragment.allPets.get(HealthFragment.lastPet).getImageUrl());
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).fit().centerCrop().into(imageView);
                    }
                });
            }
        } else {
            petDetail.setVisibility(View.INVISIBLE);
            view2.setVisibility(View.INVISIBLE);
            txtId.setVisibility(View.INVISIBLE);
            petDetail2.setBackgroundResource(R.drawable.first_layer);
        }

        return root;
    }
}