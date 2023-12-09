package com.epet.epet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.epet.epet.backend.business.UserBusiness;

public class ForgotActivity extends AppCompatActivity {
    EditText txtEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);

        txtEmail = findViewById(R.id.forgot_email);

        UserBusiness userBusiness = new UserBusiness(this);
        String message = userBusiness.CheckResetPassword(txtEmail.getText().toString());
        if (message == null){
            userBusiness.ResetPassword(txtEmail.getText().toString());
        }else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
