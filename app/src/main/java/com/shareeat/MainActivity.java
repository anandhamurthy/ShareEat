package com.shareeat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shareeat.Notification.Token;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton Notification;
    private FloatingActionButton Next, Donated;
    private EditText Quantity, Best_Before;
    private EditText Total_Members, KG;
    private Switch Need_Food;
    private RadioGroup Delivery, Getters_Food_Type, Putters_Food_Type;
    private Button Update;
    private AutoCompleteTextView Food_Name;
    private RadioButton Veg,Non_Veg,Self,Drop;
    private DatabaseReference mUsersDatabase, mDetailsDatabase;
    private String getters_food_type="",delivery="",food_need="false",putter_food_type="";
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private String mCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mCurrentUserId = mFirebaseUser.getUid();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserId);
        mUsersDatabase.keepSynced(true);

        mDetailsDatabase = FirebaseDatabase.getInstance().getReference().child("Details").child(mCurrentUserId);
        mDetailsDatabase.keepSynced(true);

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("source").getValue().equals("getters")){
                    setContentView(R.layout.activity_main_getters);
                    Total_Members= findViewById(R.id.total_people);
                    KG= findViewById(R.id.kg);
                    Notification= findViewById(R.id.notification);
                    Donated= findViewById(R.id.donated);
                    Need_Food= findViewById(R.id.need_food);
                    Update= findViewById(R.id.update);
                    Getters_Food_Type = findViewById(R.id.food_type);
                    Delivery = findViewById(R.id.delivery);
                    Veg=findViewById(R.id.veg);
                    Non_Veg=findViewById(R.id.non_veg);
                    Self=findViewById(R.id.self);
                    Drop=findViewById(R.id.drop);

                    Getters_Food_Type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            RadioButton rb=(RadioButton)findViewById(checkedId);

                            getters_food_type=rb.getText().toString();
                        }
                    });

                    Delivery.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            RadioButton rb=(RadioButton)findViewById(checkedId);

                            delivery=rb.getText().toString();
                        }
                    });

                    mDetailsDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {

                                Need_Food.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (Need_Food.isChecked()){
                                            HashMap userMap = new HashMap<>();
                                            userMap.put("online","true");
                                            userMap.put("user_id", mCurrentUserId);
                                            mDetailsDatabase.updateChildren(userMap);
                                        }else{
                                            HashMap userMap = new HashMap<>();
                                            userMap.put("online","false");
                                            userMap.put("user_id", mCurrentUserId);
                                            mDetailsDatabase.updateChildren(userMap);
                                        }
                                    }
                                });
                                food_need = dataSnapshot.child("online").getValue().toString();
                                final String total_members = dataSnapshot.child("tp").getValue().toString();
                                final String food_needed_kg = dataSnapshot.child("kg").getValue().toString();
                                final String food_type = dataSnapshot.child("ft").getValue().toString();
                                final String delivery = dataSnapshot.child("de").getValue().toString();

                                if (food_need.equals("true")) {
                                    Need_Food.setChecked(true);
                                } else {
                                    Need_Food.setChecked(false);
                                }
                                if (food_type.equals("Veg")) {
                                    Veg.setChecked(true);
                                    Non_Veg.setChecked(false);
                                } else {
                                    Veg.setChecked(false);
                                    Non_Veg.setChecked(true);
                                }
                                if (delivery.equals("Self Pick")) {
                                    Self.setChecked(true);
                                    Drop.setChecked(false);
                                } else {
                                    Self.setChecked(false);
                                    Drop.setChecked(true);
                                }
                                Total_Members.setText(total_members);
                                KG.setText(food_needed_kg);



                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String tm = Total_Members.getText().toString();
                            String kg = KG.getText().toString();
                            HashMap userMap = new HashMap<>();
                            userMap.put("user_id", mCurrentUserId);
                            userMap.put("tp", tm);
                            userMap.put("kg", kg);
                            userMap.put("ft", getters_food_type);
                            userMap.put("de", delivery);
                            userMap.put("online",food_need);
                            userMap.put("lon",dataSnapshot.child("lon").getValue());
                            userMap.put("lat",dataSnapshot.child("lat").getValue());

                            mDetailsDatabase.setValue(userMap);


                        }
                    });

                    Notification.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                            startActivity(intent);
                        }
                    });

                    Donated.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, DonatedActivity.class);
                            intent.putExtra("user_key",dataSnapshot.child("source").getValue().toString());
                            startActivity(intent);
                        }
                    });



                }else{
                    setContentView(R.layout.activity_main_putters);
                    Next=findViewById(R.id.next);
                    Quantity=findViewById(R.id.quantity);
                    Best_Before=findViewById(R.id.best_before);
                    Donated= findViewById(R.id.donated);
                    Notification= findViewById(R.id.notification);
                    Putters_Food_Type = findViewById(R.id.food_type);
                    Food_Name = findViewById(R.id.food_name);
                    Veg=findViewById(R.id.veg);
                    Non_Veg=findViewById(R.id.non_veg);
                    Notification.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                            startActivity(intent);
                        }
                    });

                    Putters_Food_Type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            RadioButton rb=(RadioButton)findViewById(checkedId);

                            putter_food_type=rb.getText().toString();
                            if (putter_food_type.equals("Veg")){
                                setSuggestions("Veg");
                            }else{
                                setSuggestions("Non_Veg");
                            }
                        }
                    });

                    Donated.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, DonatedActivity.class);
                            intent.putExtra("user_key",dataSnapshot.child("source").getValue().toString());
                            startActivity(intent);
                        }
                    });
                    Next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String quantity = Quantity.getText().toString();
                            String best_before = Best_Before.getText().toString();
                            String food_name = Food_Name.getText().toString();


                            if (!quantity.isEmpty() && !best_before.isEmpty() && !food_name.isEmpty()){
                                Intent intent = new Intent(MainActivity.this, ShowGettersActivity.class);
                                intent.putExtra("quantity", quantity);
                                intent.putExtra("lon",dataSnapshot.child("lon").getValue().toString());
                                intent.putExtra("lat",dataSnapshot.child("lat").getValue().toString());
                                intent.putExtra("best_before", best_before);
                                intent.putExtra("food_name", food_name);
                                intent.putExtra("address", dataSnapshot.child("address").getValue().toString());
                                intent.putExtra("food_type", putter_food_type);
                                startActivity(intent);
                            }else{
                                Toast.makeText(MainActivity.this, "Enter the Details", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void updateToken(String refreshToken) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Tokens");
        Token token = new Token(refreshToken);
        reference.child(firebaseUser.getUid()).setValue(token);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item1:
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);

                return true;

            case R.id.item2:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void setSuggestions(String type) {
        String items[];
        if (type.equals("Veg")){
            items = getResources().getStringArray(R.array.Veg);
        }else {
            items = getResources().getStringArray(R.array.Non_Veg);
        }


        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < items.length; i++) {
            list.add(items[i]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                MainActivity.this, R.layout.single_layout_source, list);

        Food_Name.setAdapter(adapter);
        Food_Name.setThreshold(1);

    }
}
