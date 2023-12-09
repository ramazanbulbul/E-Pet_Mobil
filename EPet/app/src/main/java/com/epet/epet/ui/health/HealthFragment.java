package com.epet.epet.ui.health;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.epet.epet.AddPetActivity;
import com.epet.epet.MainActivity;
import com.epet.epet.R;
import com.epet.epet.backend.Pet;
import com.epet.epet.backend.Treatment;
import com.epet.epet.backend.adapter.TreatmentAdapter;
import com.epet.epet.backend.adapter.ViewPagerAdapter;
import com.epet.epet.backend.business.PetBusiness;
import com.epet.epet.backend.business.UserBusiness;
import com.epet.epet.backend.mysql.MySingleton;
import com.epet.epet.ui.health.tabs.tab_petlerim;
import com.epet.epet.ui.health.tabs.tab_qrcode;
import com.epet.epet.ui.health.tabs.tab_saglikbilgileri;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.ibrahimsn.lib.SmoothBottomBar;

public class HealthFragment extends Fragment {

    private HealthViewModel healthViewModel;
    private int _page = 1;

    Spinner spinner;
    ImageView imageView, imgQrcode;
    TextView txtId, txtName, txtType, txtBreed, txtBirthday;
    CardView view2;
    LinearLayout petDetail, petDetail2;
    ProgressDialog progressDialog ;
    ArrayList<Treatment> allTreatments;
    public static ArrayList<Pet> allPets;
    public static int lastPet;

    public static HealthFragment newInstance() {
        return new HealthFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        healthViewModel =
                ViewModelProviders.of(this).get(HealthViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_health, container, false);
        TabLayout tabLayout;
        ViewPager viewPager;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        SmoothBottomBar bottomBar = getActivity().findViewById(R.id.bottomBar);
        bottomBar.setActiveItem(_page);
        MainActivity.lastPage = _page;


        viewPager = root.findViewById(R.id.pagee);

        final tab_qrcode tab_qrcode = new tab_qrcode();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.FragmentEkle(new tab_petlerim(), "Petlerim");
        adapter.FragmentEkle(new tab_saglikbilgileri(), "Sağlık Bilgisi");
        adapter.FragmentEkle(tab_qrcode, "QR Kod");
        viewPager.setAdapter(adapter);

        tabLayout = root.findViewById(R.id.tabb);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0);
        tabLayout.getTabAt(1);
        tabLayout.getTabAt(2);

        UserBusiness userBusiness = new UserBusiness(root.getContext().getApplicationContext());
        String url = "http://epetiste-001-site1.etempurl.com/Mobile/GetOwnerPets?uid="+userBusiness.getLoginUser().getUid();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                imageView = root.findViewById(R.id.pet_image);
                txtId = root.findViewById(R.id.pet_id);
                txtName = root.findViewById(R.id.pet_name);
                txtType = root.findViewById(R.id.pet_type);
                txtBreed = root.findViewById(R.id.pet_genus);
                txtBirthday = root.findViewById(R.id.pet_age);
                view2 = root.findViewById(R.id.view2);
                petDetail = root.findViewById(R.id.petDetail);
                petDetail2 = root.findViewById(R.id.petDetail2);

                progressDialog.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.isNull(0)){
                        petDetail.setVisibility(View.INVISIBLE);
                        view2.setVisibility(View.INVISIBLE);
                        txtId.setVisibility(View.INVISIBLE);
                        petDetail2.setBackgroundResource(R.drawable.first_layer);
                    }else{
                        petDetail.setVisibility(View.VISIBLE);
                        view2.setVisibility(View.VISIBLE);
                        txtId.setVisibility(View.VISIBLE);
                        petDetail2.setBackgroundResource(R.drawable.second_layer);
                        allPets = new ArrayList<Pet>();
                        int i = 0;

                        while (!jsonArray.isNull(i)){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int Id = jsonObject.getInt("Id");
                            String Name = jsonObject.getString("Name");
                            String Birthdate = jsonObject.getString("Birthday");
                            String Type = jsonObject.getString("Type");
                            String Breed = jsonObject.getString("Breed");
                            String ImageUrl = jsonObject.getString("ImageUrl");
                            Pet pet = new Pet(Id, Name,  Birthdate.substring(0,Birthdate.indexOf(" ")).replace("/" ,"-"),Type,Breed, ImageUrl);
                            allPets.add(pet);
                            i++;
                        }

                        String[] pets = new String[allPets.size()];

                        System.out.println("size:" + allPets.size());
                        for (int j = 0; j < allPets.size(); j++) {
                            pets[j] = allPets.get(j).getName();
                        }


                        spinner = root.findViewById(R.id.spinner2);
                        ArrayAdapter aa = new ArrayAdapter(root.getContext().getApplicationContext(),android.R.layout.simple_spinner_item, pets);
                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //Setting the ArrayAdapter data on the Spinner
                        spinner.setAdapter(aa);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                                lastPet = position;
                                //qrCode



                                //My Pets
                                txtId.setText(String.valueOf(allPets.get(position).getId()));
                                txtBirthday.setText(allPets.get(position).getBirthdate());
                                txtBreed.setText(allPets.get(position).getBreed());
                                txtName.setText(allPets.get(position).getName());
                                txtType.setText(allPets.get(position).getType());;
                                if (!allPets.get(position).getImageUrl().isEmpty()){
                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                    storageReference = storageReference.child(allPets.get(position).getImageUrl());
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Picasso.get().load(uri).into(imageView);
                                            System.out.println(uri.getPath());
                                        }
                                    });
                                }

                                //Treatments
                                allTreatments = new ArrayList<Treatment>();

                                String url = "http://epetiste-001-site1.etempurl.com/Mobile/GetPetTreatment?id="+ allPets.get(position).getId();
                                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            JSONArray jsonArray = new JSONArray(response);
                                            int i = 0;

                                            while (!jsonArray.isNull(i)){
                                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                String ClinicName = jsonObject.getString("ClinicName");
                                                String Title = jsonObject.getString("Title");
                                                String Description = jsonObject.getString("Description");
                                                String Date = jsonObject.getString("Date");
                                                Treatment treatment = new Treatment(ClinicName,  Date.substring(0,Date.indexOf(" ")).replace("/" ,"-"),Title,Description);
                                                allTreatments.add(treatment);
                                                i++;

                                            }
                                            TreatmentAdapter adapter_items = new TreatmentAdapter(allTreatments);

                                            RecyclerView recycler_view = root.findViewById(R.id.saglikbilgisi_post);
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

                                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                            layoutManager.scrollToPosition(0);

                                            recycler_view.setLayoutManager(layoutManager);
                                            recycler_view.setHasFixedSize(true);
                                            recycler_view.setNestedScrollingEnabled(false);
                                            recycler_view.setAdapter(adapter_items);
                                            recycler_view.setItemAnimator(new DefaultItemAnimator());


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(root.getContext().getApplicationContext(), error.getMessage() ,Toast.LENGTH_SHORT).show();
                                    }
                                });

                                MySingleton.getInstance(root.getContext().getApplicationContext()).addToRequest(request);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(root.getContext().getApplicationContext(), error.getMessage() ,Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(root.getContext().getApplicationContext()).addToRequest(request);



        healthViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }
}