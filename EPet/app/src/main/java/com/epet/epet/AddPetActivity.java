package com.epet.epet;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.epet.epet.backend.business.PetBusiness;
import com.epet.epet.backend.business.UserBusiness;
import com.epet.epet.backend.mysql.MySingleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddPetActivity extends AppCompatActivity {

    EditText et_petage;
    Context context = this;
    ImageView selecetpetimage;

    private static final String TAG = AddPetActivity.class.getSimpleName();
    private int PICK_IMAGE_REQUEST = 1;

    Bitmap bitmap;
    Uri uri;

    boolean check = true;

    Button btn_petadd;

    ImageView petImage;

    EditText petName;

    ProgressDialog progressDialog ;

    int[] spinnerSelect = new int[2];
    String[] types = {"Kedi", "Köpek"};
    HashMap<String, String[]> allBreed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpet);
        selecetpetimage = findViewById(R.id.pet_imagee);

        selecetpetimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        et_petage = (EditText) findViewById(R.id.pet_agee);
        et_petage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar takvim = Calendar.getInstance();
                int yil = takvim.get(Calendar.YEAR);
                int ay = takvim.get(Calendar.MONTH);
                int gun = takvim.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month += 1;
                                et_petage.setText( month+ "-" + dayOfMonth + "-" + year);
                            }
                        }, yil, ay, gun);
                dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Seç", dpd);
                dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", dpd);
                dpd.show();
            }
        });

        allBreed = new HashMap<String, String[]>();
        allBreed.put(types[0], new String[] {"Van Kedisi", "Tekir","Fars Kedisi","Siyam Kedisi","Sfenks Kedisi","Scottish Fold","Ankara Kedisi","British Longhair"});
        allBreed.put(types[1], new String[] {"Golden", "Kangal"});

        Spinner typeSpinner = findViewById(R.id.spinnertur);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, types);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(aa);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long l) {
                Spinner breedSpinner = findViewById(R.id.spinnercins);
                String[] breeds = allBreed.get(types[position]);
                ArrayAdapter breedAdaptor = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, breeds);
                breedAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                breedSpinner.setAdapter(breedAdaptor);
                breedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        spinnerSelect[0] = position;
                        spinnerSelect[1] = i;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





        petName = findViewById(R.id.pet_namee);
        btn_petadd = findViewById(R.id.btn_petadd);
        btn_petadd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                PetBusiness petBusiness = new PetBusiness(getApplicationContext());
                final String petNameString = petName.getText().toString();
                final String petBirth = et_petage.getText().toString();
                String msg = petBusiness.addPetCheck(petNameString, petBirth, uri);
                if (msg != null){
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }else {
                    final String URL ="http://epetiste-001-site1.etempurl.com/Mobile/AddPet" ;
                    progressDialog = new ProgressDialog(AddPetActivity.this);
                    progressDialog.setMessage("Uploading, please wait...");
                    progressDialog.show();

                    checkFilePermissions();
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                    final String ref = "uploads/pets/" + UUID.randomUUID() + ".jpg";
                    final StorageReference storageReference = mStorageRef.child(ref);
                    storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final Map<String, String> parameters = new HashMap<String, String>();
                            UserBusiness business = new UserBusiness(getApplicationContext());
                            String name = petNameString;
                            String type = types[spinnerSelect[0]];
                            String breed = allBreed.get(types[spinnerSelect[0]])[spinnerSelect[1]];
                            String date = petBirth;
                            String ownerId = business.getLoginUser().getUid();
                            parameters.put("Name", name);
                            parameters.put("Birthday", date);
                            parameters.put("Type", type);
                            parameters.put("Breed", breed);
                            parameters.put("OwnerId", ownerId);
                            parameters.put("ImageUrl", ref);


                            StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){
                                @Override
                                public void onResponse(String s) {
                                    progressDialog.dismiss();
                                    Intent gecisYap = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(gecisYap);
                                    System.out.println("Success:" + s);
                                }
                            },new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    finish();
                                    progressDialog.dismiss();
                                    Toast.makeText(AddPetActivity.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
                                    System.out.println(volleyError.getMessage());
                                }
                            }) {
                                //adding parameters to send
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    return parameters;
                                }
                            };
                            MySingleton.getInstance(getApplicationContext()).addToRequest(request);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toastMessage("Upload Failed");
                            progressDialog.dismiss();
                        }
                    });
                }

            }
        });

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
            int permissionCheck = AddPetActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += AddPetActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
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
