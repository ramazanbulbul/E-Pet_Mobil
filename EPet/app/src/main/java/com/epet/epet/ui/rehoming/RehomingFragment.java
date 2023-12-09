package com.epet.epet.ui.rehoming;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.epet.epet.AddPetActivity;
import com.epet.epet.AddRehomingActivity;
import com.epet.epet.MainActivity;
import com.epet.epet.R;
import com.epet.epet.backend.Rehoming;
import com.epet.epet.backend.Treatment;
import com.epet.epet.backend.adapter.RehomingAdapter;
import com.epet.epet.backend.adapter.TreatmentAdapter;
import com.epet.epet.backend.mysql.MySingleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import me.ibrahimsn.lib.SmoothBottomBar;

public class RehomingFragment extends Fragment {

    private RehomingViewModel rehomingViewModel;

    private int _page = 0;

    FloatingActionButton btnPetAdd;

    ArrayList<Rehoming> allRehomings;

    public static RehomingFragment newInstance() {
        return new RehomingFragment();
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SmoothBottomBar bottomBar = getActivity().findViewById(R.id.bottomBar);
        bottomBar.setActiveItem(_page);

        if (ActivityCompat.checkSelfPermission(getContext().getApplicationContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 100);
        }
        MainActivity.lastPage = _page;

        rehomingViewModel =
                ViewModelProviders.of(this).get(RehomingViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_rehoming, container, false);




        allRehomings = new ArrayList<Rehoming>();

        String url = "http://epetiste-001-site1.etempurl.com/Mobile/GetRehomingPet";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int i = 0;
                    while (!jsonArray.isNull(i)) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int Id = jsonObject.getInt("Id");
                        String Username = jsonObject.getString("OwnerName");
                        String Description = jsonObject.getString("Description");
                        String ImageUrl = jsonObject.getString("ImageUrl");
                        String PhoneNumber = jsonObject.getString("PhoneNumber");
                        String Adress = jsonObject.getString("Adress");
                        Rehoming rehoming = new Rehoming(Id,Username,Description, ImageUrl, PhoneNumber,Adress);
                        allRehomings.add(rehoming);
                        i++;

                    }
                    RehomingAdapter adapter_items = new RehomingAdapter(allRehomings);

                    RecyclerView recycler_view = root.findViewById(R.id.rehoming_post);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    layoutManager.scrollToPosition(0);

                    recycler_view.setLayoutManager(layoutManager);
                    recycler_view.setHasFixedSize(true);
                    recycler_view.setNestedScrollingEnabled(false);
                    recycler_view.setAdapter(adapter_items);
                    recycler_view.setItemAnimator(new DefaultItemAnimator());
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                Toast.makeText(getContext().getApplicationContext(), error.getMessage() ,Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(getContext().getApplicationContext()).addToRequest(request);


        btnPetAdd = root.findViewById(R.id.pet_add);

        btnPetAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gecisYap = new Intent(getActivity().getApplicationContext(), AddRehomingActivity.class);
                startActivity(gecisYap);
            }
        });

        rehomingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }
}