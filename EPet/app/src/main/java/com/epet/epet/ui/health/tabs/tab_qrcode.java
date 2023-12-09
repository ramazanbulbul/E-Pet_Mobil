package com.epet.epet.ui.health.tabs;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.epet.epet.AddPetActivity;
import com.epet.epet.R;
import com.epet.epet.ui.health.HealthFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class tab_qrcode extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root =  inflater.inflate(R.layout.tab_qrcode, container, false);
        ImageView imgQrcode = root.findViewById(R.id.imgQrcode);
        Button btnSaveQrCode = root.findViewById(R.id.button_saveqr);

        QRCodeWriter writer = new QRCodeWriter();
        if (HealthFragment.allPets != null && HealthFragment.allPets.size() > 0){
            try {

                BitMatrix bitMatrix = writer.encode(String.valueOf(HealthFragment.allPets.get(HealthFragment.lastPet).getId()), BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                if (bmp != null){
                    imgQrcode.setImageBitmap(bmp);
                    final Bitmap bitmap = bmp;
                    btnSaveQrCode.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View view) {
                            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                                int permissionCheck = getActivity().getApplicationContext().checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
                                permissionCheck += getActivity().getApplicationContext().checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
                                if (permissionCheck != 0) {
                                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
                                }
                            }
                            String fileName = "qrCode_" +HealthFragment.allPets.get(HealthFragment.lastPet).getName() + ".jpg";

                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

                            File filePath = Environment.getExternalStorageDirectory();
                            File dir = new File(filePath.getAbsolutePath() + "/ePet/");
                            dir.mkdir();

                            File file = new File(dir, fileName);
                            FileOutputStream fileOutputStream = null;
                            try {
                                fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(bytes.toByteArray());
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                            Toast.makeText(getContext().getApplicationContext(), "Qr Code kaydedildi!", Toast.LENGTH_SHORT).show();

                            try {
                                fileOutputStream.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                fileOutputStream.close();
                            }catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
        return root;
    }
}
