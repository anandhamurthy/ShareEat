package com.shareeat;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText Register_Email_Address;
    private EditText Register_Password;
    private EditText Register_Confirm_Password;
    private EditText Register_Name, Register_Address, Register_Phone_Number;
    private FloatingActionButton Register_Button;
    private TextView Register_Login_Button, Login_Forgot_Password;


    private String Soure="",locationAddress;
    private FirebaseAuth mAuth;

    private DatabaseReference mUsersDatabase;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final String user_key = getIntent().getStringExtra("user_key");

        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        Register_Email_Address = findViewById(R.id.register_email_address);
        Register_Password = findViewById(R.id.register_password);
        Register_Confirm_Password = findViewById(R.id.register_confirm_pass);
        Register_Name = findViewById(R.id.register_name);
        Register_Address = findViewById(R.id.register_address);
        Register_Phone_Number = findViewById(R.id.register_phone_no);

        Register_Button = findViewById(R.id.register);
        Register_Login_Button = findViewById(R.id.login_register_text);
        Login_Forgot_Password = findViewById(R.id.login_forgot_password_text);

        Login_Forgot_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resetIntent = new Intent(RegisterActivity.this, ResetPasswordActivity.class);
                resetIntent.putExtra("user_key",user_key);
                startActivity(resetIntent);
            }
        });
        Register_Address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT= 2;
                if (!Register_Address.getText().toString().isEmpty()){
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= (Register_Address.getRight() - Register_Address.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            final GeocodingLocation locationAddress = new GeocodingLocation();
                            locationAddress.getAddressFromLocation(Register_Address.getText().toString(),
                                    getApplicationContext(), new GeocoderHandler());

                            return true;
                        }
                    }
                }
                return false;
            }
        });


        if (user_key.equals("getters")){
            Soure="getters";

        }else if(user_key.equals("putters")){
            Soure="putters";
        }

        Register_Login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();

            }
        });


        Register_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = Register_Email_Address.getText().toString();
                final String pass = Register_Password.getText().toString();
                final String phone = Register_Phone_Number.getText().toString();
                final String name = Register_Name.getText().toString();
                final String address = Register_Address.getText().toString();
                final String confirm_pass = Register_Confirm_Password.getText().toString();

                if (isEmpty(email, name,address,phone, pass, confirm_pass)) {

                    if (pass.length() > 5 && confirm_pass.length() > 5) {

                        if (pass.equals(confirm_pass)) {

                            mProgressDialog.setTitle("Registering");
                            mProgressDialog.setMessage("Creating User..");
                            mProgressDialog.show();
                            mProgressDialog.setCanceledOnTouchOutside(false);

                            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {


                                        String device_token = FirebaseInstanceId.getInstance().getToken();

                                        HashMap userMap = new HashMap<>();
                                        userMap.put("email_id", email);
                                        userMap.put("name", name);
                                        userMap.put("source", Soure);
                                        String laglat[] =locationAddress.split(" ");
                                        userMap.put("lon",Double.parseDouble(laglat[0]));
                                        userMap.put("lat",Double.parseDouble(laglat[1]));
                                        userMap.put("address", address);
                                        userMap.put("phone_number", phone);
                                        userMap.put("user_id", mAuth.getCurrentUser().getUid());
                                        userMap.put("device_token", device_token);

                                        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                        String uid = current_user.getUid();

                                        mUsersDatabase.child(uid).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    mProgressDialog.dismiss();

                                                    Toast.makeText(RegisterActivity.this, "Account Created & Logging in Sucessfully", Toast.LENGTH_LONG).show();
                                                    Intent setupIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(setupIntent);
                                                    finish();


                                                }

                                            }
                                        });

                                    } else {

                                        mProgressDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(RegisterActivity.this, "Error : " + error, Toast.LENGTH_LONG).show();


                                    }

                                }
                            });

                        } else {

                            Toast.makeText(RegisterActivity.this, "Confirm Password and Password Field doesn't match.", Toast.LENGTH_LONG).show();

                        }

                    } else {

                        Register_Password.setError("Atleast 6 Characters");
                        Register_Confirm_Password.setError("Atleast 6 Characters");
                        Toast.makeText(RegisterActivity.this, "Password must contain atleast 6 characters.", Toast.LENGTH_LONG).show();

                    }
                }


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            Intent mainIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(mainIntent);
            finish();

        }

    }

    private boolean isEmpty(String email, String name, String address, String phoneno, String password, String confirm_pass) {
        if (email.isEmpty() || name.isEmpty() || address.isEmpty() || phoneno.isEmpty()  || password.isEmpty() || confirm_pass.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Complete All the Details", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            Toast.makeText(RegisterActivity.this, locationAddress, Toast.LENGTH_SHORT).show();
        }
    }


}
