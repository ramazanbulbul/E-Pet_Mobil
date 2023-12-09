package com.epet.epet.backend.business;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.epet.epet.AuthActivity;
import com.epet.epet.MainActivity;
import com.epet.epet.R;
import com.epet.epet.backend.mysql.MySingleton;
import com.epet.epet.backend.mysql.MysqlUtility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserBusiness {

    FirebaseAuth _auth;
    Context _context;
    public UserBusiness(Context context){
        _auth = FirebaseAuth.getInstance();
        _context = context;
    }

    public FirebaseUser getLoginUser() {
        return _auth.getCurrentUser();
    }

    public String CheckUserLogin(String email, String password) {
        if (email.isEmpty() || password.isEmpty()){
            return _context.getString(R.string.auth_data_empty);
        }
        if (!email.contains("@") || !email.contains(".") || email.indexOf("@") > email.lastIndexOf(".")){
            return _context.getString(R.string.auth_email_error);
        }
        return null;
    }

    public void Login(String email, String password){

        _auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Intent intent = new Intent(_context.getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(_context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String CheckUserRegister(String username, String email, String password, String passwordCheck) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordCheck.isEmpty()){
            return _context.getString(R.string.auth_data_empty);
        }
        if (!email.contains("@") || !email.contains(".") || email.indexOf("@") > email.lastIndexOf(".")){
            return  _context.getString(R.string.auth_email_error);
        }
        if (!password.equals(passwordCheck)){
            return _context.getString(R.string.register_password_validate_error);
        }
        return null;
    }
    public void Register(final String username, final String email, final String password){
        final ProgressDialog progressDialog = new ProgressDialog(_context);
        progressDialog.setMessage(_context.getString(R.string.loading_data));
        progressDialog.show();
        _auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //Mysql kayıt
                String url = "http://epetiste-001-site1.etempurl.com/Mobile/AddPetOwner/";
                HashMap<String, String> values = new HashMap<String, String>();
                values.put("Id", authResult.getUser().getUid());
                values.put("Username", username);
                values.put("Email", email);
                MysqlUtility.SaveToMysql(url, _context,values);
                progressDialog.dismiss();
                //Email doğrulama gelebilir
                Toast.makeText(_context.getApplicationContext(), _context.getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(_context, MainActivity.class);
                _context.startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(_context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public String CheckResetPassword(String email) {
        if (email.isEmpty()){
            return _context.getString(R.string.auth_data_empty);
        }
        if (!email.contains("@") || !email.contains(".") || email.indexOf("@") > email.lastIndexOf(".")){
            return  _context.getString(R.string.auth_email_error);
        }
        return null;
    }

    public void ResetPassword(String email) {
        _auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(_context, _context.getString(R.string.reset_password_message), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(_context.getApplicationContext(), AuthActivity.class);
                _context.startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(_context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setPhone(String PhoneNumber) {
        String url = "http://epetiste-001-site1.etempurl.com/Mobile/setPhoneNumber";
        Map<String,String> values = new HashMap<String, String>();
        values.put("Id", getLoginUser().getUid());
        values.put("PhoneNumber", PhoneNumber);
        MysqlUtility.SaveToMysql(url, _context, values);
    }

    public void setUsername(String Username) {
        String url = "http://epetiste-001-site1.etempurl.com/Mobile/setUsername";
        Map<String,String> values = new HashMap<String, String>();
        values.put("Id", getLoginUser().getUid());
        values.put("Username", Username);
        MysqlUtility.SaveToMysql(url, _context, values);
    }

    public void setAdress(String Adress) {
        String url = "http://epetiste-001-site1.etempurl.com/Mobile/setAdress";
        Map<String,String> values = new HashMap<String, String>();
        values.put("Id", getLoginUser().getUid());
        values.put("Adress", Adress);
        MysqlUtility.SaveToMysql(url, _context, values);
    }

    public void deleteAccount() {
        String url = "http://epetiste-001-site1.etempurl.com/Mobile/deleteAccount";
        Map<String,String> values = new HashMap<String, String>();
        values.put("Id", getLoginUser().getUid());
        MysqlUtility.SaveToMysql(url, _context, values);
        _auth.getCurrentUser().delete();
        _auth = null;
    }
}