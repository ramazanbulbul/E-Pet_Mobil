package com.epet.epet;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.epet.epet.backend.Pet;
import com.epet.epet.backend.business.UserBusiness;
import com.epet.epet.backend.mysql.MySingleton;
import com.epet.epet.ui.health.HealthFragment;
import com.epet.epet.ui.rehoming.RehomingFragment;
import com.epet.epet.ui.vet.VetFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {

    public static int lastPage = 1;
    private ImageView qrcode,settings;
    private int camera_check;
    TabLayout tabLayout;
    ViewPager viewPager;
    FirebaseAuth _auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _auth = FirebaseAuth.getInstance();


        final Activity activity = this;
        camera_check = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        qrcode = findViewById(R.id.qr_code);

        qrcode.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (camera_check != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},100);
                }else { }
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
                integrator.setOrientationLocked(true);
            }
        });



        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gecisYap = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(gecisYap);
            }
        });



        SmoothBottomBar bottomBar = findViewById(R.id.bottomBar);
        for (int j = 0; j< getSupportFragmentManager().getFragments().size();j++){
            Fragment frag = getSupportFragmentManager().getFragments().get(j);
            getSupportFragmentManager().beginTransaction().remove(frag).commit();
        }

        switch (lastPage){
            case 0:
                getSupportFragmentManager().beginTransaction().add(R.id.fragment, RehomingFragment.newInstance()).commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().add(R.id.fragment, HealthFragment.newInstance()).commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().add(R.id.fragment, VetFragment.newInstance()).commit();
                break;
        }
        bottomBar.setActiveItem(lastPage);
        System.out.println(getSupportFragmentManager().getFragments().size());
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelect(int i) {
                if (getSupportFragmentManager().getFragments().get(0) != null){
                    switch (i){
                        case 0:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, RehomingFragment.newInstance()).commit();
                            break;
                        case 1:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, HealthFragment.newInstance()).commit();
                            break;
                        case 2:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, VetFragment.newInstance()).commit();
                            break;
                    }
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        for (Fragment frag : getSupportFragmentManager().getFragments()){
            getSupportFragmentManager().beginTransaction().remove(frag).commit();
        }
        if (lastPage-1 == -1) getSupportFragmentManager().beginTransaction().replace(R.id.fragment, HealthFragment.newInstance()).commit();
        else if (lastPage-1==1) getSupportFragmentManager().beginTransaction().replace(R.id.fragment, HealthFragment.newInstance()).commit();
        else  if (lastPage-1 == 0){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    this);
            alertDialog.setTitle(R.string.quit_master);
            alertDialog.setMessage(R.string.quit);
            alertDialog.setIcon(R.mipmap.ic_appicon_round);
            alertDialog.setPositiveButton(R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                            System.exit(0);
                        }
                    });
            alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, HealthFragment.newInstance()).commit();
                }
            });
            alertDialog.show();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, HealthFragment.newInstance()).commit();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String url = "http://epetiste-001-site1.etempurl.com/Mobile/GetQrCodeDetail?id="+ result.getContents();
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.isNull(0)){
                                Toast.makeText(getApplicationContext(), "Aradığınız hayvan sistemde kayıtlı değildir!", Toast.LENGTH_SHORT).show();
                            }else {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String Username = jsonObject.getString("Username");
                                String Adress = jsonObject.getString("Adress");
                                String Email = jsonObject.getString("Email");
                                final String PhoneNumber = jsonObject.getString("PhoneNumber");

                                String petOwnerDetail = "Kullanıcı Adı: " + Username + "\n";
                                petOwnerDetail += "E-Mail: " + Email + "\n";
                                if (Adress != "null" && !Adress.isEmpty() )
                                    petOwnerDetail += "Adres: " + Adress + "\n";
                                if (PhoneNumber != "null" && !Adress.isEmpty())
                                    petOwnerDetail += "Phone Number: " + PhoneNumber;
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Sahip Bilgisi");
                                builder.setMessage(petOwnerDetail);
                                if (PhoneNumber != "null" && !Adress.isEmpty())
                                    builder.setPositiveButton("Ara", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                            callIntent.setData(Uri.parse("tel:" + PhoneNumber + ""));

                                            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE},100);
                                                Toast.makeText(getApplicationContext(), "Arama yapabilmek için lütfen arama iznini açınız!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            MainActivity.this.startActivity(callIntent);
                                    }
                                });

                                builder.setNegativeButton("Tamam", null);
                                builder.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage() ,Toast.LENGTH_SHORT).show();
                    }
                });

                MySingleton.getInstance(getApplicationContext()).addToRequest(request);

            } else {

            }
        }
    }


}