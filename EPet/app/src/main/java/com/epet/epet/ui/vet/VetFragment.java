package com.epet.epet.ui.vet;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.epet.epet.MainActivity;
import com.epet.epet.R;
import com.epet.epet.backend.mysql.MySingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.ibrahimsn.lib.SmoothBottomBar;

public class VetFragment extends Fragment  {

    private final static int REQUEST_lOCATION=90;
    private GoogleMap mMap;
    private VetViewModel vetViewModel;
    private int _page = 2;
    private int gps_check;
    protected LocationManager locationManager;
    Location loc;
    ArrayList<String[]> clinicList = new ArrayList<String[]>();
    public static VetFragment newInstance() {
        return new VetFragment();
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        gps_check = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (gps_check != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }else {

        }


        SmoothBottomBar bottomBar = getActivity().findViewById(R.id.bottomBar);
        bottomBar.setActiveItem(_page);
        MainActivity.lastPage = _page;

        vetViewModel =
                ViewModelProviders.of(this).get(VetViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_vets, container, false);
        vetViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {


            }
        });

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                loc = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.vets_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mMap = googleMap;
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                final Geocoder geocoder = new Geocoder(root.getContext());


                String url = "http://epetiste-001-site1.etempurl.com/Mobile/getClinicLoc";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            int i = 0;
                            while (!jsonArray.isNull(i)){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String ClinicName = jsonObject.getString("ClinicName");
                                String Adress = jsonObject.getString("Adress");
                                String[] clinic = {ClinicName, Adress};
                                clinicList.add(clinic);
                                i++;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        for (String[] clinic : clinicList){
                            List<Address> adressList = new ArrayList<Address>();
                            try {
                                adressList = geocoder.getFromLocationName(clinic[1],1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (adressList.size() > 0){
                                Address address = adressList.get(0);
                                double k1 = address.getLatitude();
                                double k2 = address.getLongitude();
                                LatLng konum2 = new LatLng(k1, k2);
                                mMap.addMarker(new MarkerOptions().position(konum2).title("Üye(" + clinic[0] +")").icon(BitmapDescriptorFactory.fromResource(R.drawable.memberlocate)));
                            }

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                        Toast.makeText(root.getContext(), error.getMessage() ,Toast.LENGTH_SHORT).show();
                    }
                });
                MySingleton.getInstance(root.getContext().getApplicationContext()).addToRequest(request);
                LatLng camera = new LatLng(38.387183, 27.167849);
                if (loc != null)
                    camera = new LatLng(loc.getLatitude(), loc.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(camera));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 16),1,null);
            }
        });


        return root;
    }

}