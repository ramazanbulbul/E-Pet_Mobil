package com.epet.epet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.epet.epet.backend.Pet;
import com.epet.epet.backend.Treatment;
import com.epet.epet.backend.adapter.TreatmentAdapter;
import com.epet.epet.backend.business.PetBusiness;
import com.epet.epet.backend.business.UserBusiness;
import com.epet.epet.backend.mysql.MySingleton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddRehomingActivity extends AppCompatActivity {

    EditText description;
    Spinner spinner;
    Button btnAdd;
    ArrayList<Pet> allPets;

    Context context = this;
    ImageView selecetpetimage;

    private static final String TAG = AddRehomingActivity.class.getSimpleName();
    private int PICK_IMAGE_REQUEST = 1;

    Bitmap bitmap;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addrehoming);

        selecetpetimage = findViewById(R.id.addrehoming_image);
        selecetpetimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        UserBusiness userBusiness = new UserBusiness(getApplicationContext());
        String url = "http://epetiste-001-site1.etempurl.com/Mobile/GetOwnerPets?uid="+userBusiness.getLoginUser().getUid();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                allPets = new ArrayList<Pet>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int i = 0;

                    while (!jsonArray.isNull(i)){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int Id = jsonObject.getInt("Id");
                        String Name = jsonObject.getString("Name");
                        String Birthdate = jsonObject.getString("Birthday");
                        String Type = jsonObject.getString("Type");
                        String Breed = jsonObject.getString("Breed");
                        String ImageUrl = jsonObject.getString("ImageUrl");
                        Pet pet = new Pet(Id, Name,  Birthdate,Type,Breed, ImageUrl);
                        allPets.add(pet);
                        i++;
                    }

                    String[] pets = new String[allPets.size()];

                    System.out.println("size:" + allPets.size());
                    for (int j = 0; j < allPets.size(); j++) {
                        pets[j] = allPets.get(j).getName();
                    }


                    spinner = findViewById(R.id.spinnerPet);
                    ArrayAdapter aa = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item, pets);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //Setting the ArrayAdapter data on the Spinner
                    spinner.setAdapter(aa);

                    description = findViewById(R.id.rehoming_aciklama);
                    btnAdd = findViewById(R.id.btn_petadd);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long l) {

                            btnAdd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PetBusiness petBusiness = new PetBusiness(getApplicationContext());
                                    String msg = petBusiness.addRehomingPetCheck(description.getText().toString());
                                    if (msg != null){
                                        Toast.makeText(AddRehomingActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }else {
                                        String url = "http://epetiste-001-site1.etempurl.com/Mobile/isRehomingPet";
                                        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                if (response.indexOf("False") != -1){
                                                    checkFilePermissions();
                                                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                                                    final String ref = "uploads/pets/" + UUID.randomUUID() + ".jpg";
                                                    final StorageReference storageReference = mStorageRef.child(ref);
                                                    storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            final Map<String, String> values = new HashMap<String, String>();
                                                            values.put("PetId",  allPets.get(position).getId() + "");
                                                            values.put("Desc",  description.getText().toString());
                                                            values.put("ImageUrl", ref);
                                                            String url = "http://epetiste-001-site1.etempurl.com/Mobile/addRehomingPet";
                                                            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    finish();
                                                                    System.exit(0);
                                                                }
                                                            }, new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    System.out.println(error.getMessage());
                                                                    Toast.makeText(getApplicationContext(), error.getMessage() ,Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                }
                                                            }){
                                                                @Override
                                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                                    return values;
                                                                }
                                                            };
                                                            MySingleton.getInstance(getApplicationContext()).addToRequest(request);
                                                        }
                                                    });
                                                }else{
                                                    Toast.makeText(AddRehomingActivity.this, "Bu hayvaniniz zaten sahiplendirme bolumunde bulunuyor", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                finish();
                                                System.out.println(error.getMessage());
                                                Toast.makeText(getApplicationContext(), error.getMessage() ,Toast.LENGTH_SHORT).show();
                                            }
                                        }){
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String, String> values = new HashMap<String, String>();
                                                values.put("PetId", allPets.get(position).getId() + "");
                                                return values;
                                            }
                                        };
                                        MySingleton.getInstance(getApplicationContext()).addToRequest(request);
                                    }
                                }
                            });
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Toast.makeText(getApplicationContext(), error.getMessage() ,Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(getApplicationContext()).addToRequest(request);

    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                selecetpetimage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkFilePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = AddRehomingActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += AddRehomingActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
