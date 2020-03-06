package com.shareeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shareeat.Models.Users;

import java.util.ArrayList;
import java.util.HashMap;

public class EditActivity extends AppCompatActivity {

    private EditText Edit_Name, Edit_Address, Edit_Phone_Number;
    private FloatingActionButton Save_Button;

    private String locationAddress, mCurrentUserId;
    private FirebaseAuth mAuth;

    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser current_user = mAuth.getCurrentUser();
        mCurrentUserId = current_user.getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserId);
        mUsersDatabase.keepSynced(true);

        Edit_Name = findViewById(R.id.edit_name);
        Edit_Address = findViewById(R.id.edit_address);
        Edit_Phone_Number = findViewById(R.id.edit_phone_no);

        Save_Button = findViewById(R.id.save);

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                Edit_Name.setText(user.getName());
                Edit_Address.setText(user.getAddress());
                Edit_Phone_Number.setText(user.getPhone_number());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Edit_Address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT= 2;
                if (!Edit_Address.getText().toString().isEmpty()){
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= (Edit_Address.getRight() - Edit_Address.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            final GeocodingLocation locationAddress = new GeocodingLocation();
                            locationAddress.getAddressFromLocation(Edit_Address.getText().toString(),
                                    getApplicationContext(), new GeocoderHandler());

                            return true;
                        }
                    }
                }
                return false;
            }
        });

        Save_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                
                final String phone = Edit_Phone_Number.getText().toString();
                final String name = Edit_Name.getText().toString();
                final String address = Edit_Address.getText().toString();
                

                HashMap userMap = new HashMap<>();
                userMap.put("name", name);
                String laglat[] =locationAddress.split(" ");
                userMap.put("lon",Double.parseDouble(laglat[0]));
                userMap.put("lat",Double.parseDouble(laglat[1]));
                userMap.put("address", address);
                userMap.put("phone_number", phone);

                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = current_user.getUid();

                mUsersDatabase.updateChildren(userMap);

            }
        });

       

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
            Toast.makeText(EditActivity.this, locationAddress, Toast.LENGTH_SHORT).show();
        }
    }

}
