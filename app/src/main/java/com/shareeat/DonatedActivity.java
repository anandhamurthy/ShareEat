package com.shareeat;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shareeat.Adapters.DonatedAdapter;
import com.shareeat.Models.Donated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DonatedActivity extends AppCompatActivity {

    private RecyclerView Donated_List;
    private DonatedAdapter donatedAdapter;
    private List<Donated> donatedList;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private String mCurrentUserId;
    private DatabaseReference mDonatedDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        final String user_key = getIntent().getStringExtra("user_key");

        Donated_List =findViewById(R.id.list);
        Donated_List.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(DonatedActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        Donated_List.setLayoutManager(mLayoutManager);
        donatedList = new ArrayList<>();
        donatedAdapter = new DonatedAdapter(DonatedActivity.this, donatedList, user_key);
        Donated_List.setAdapter(donatedAdapter);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mCurrentUserId = mFirebaseUser.getUid();

        mDonatedDatabase = FirebaseDatabase.getInstance().getReference().child("Donated").child(mCurrentUserId);
        mDonatedDatabase.keepSynced(true);

       readNotifications();


    }

    private void readNotifications() {

        mDonatedDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Donated_List.setVisibility(View.VISIBLE);
                    donatedList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Donated donated = snapshot.getValue(Donated.class);
                        donatedList.add(donated);
                    }

                    Collections.reverse(donatedList);
                    donatedAdapter.notifyDataSetChanged();
                } else {
                    Donated_List.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

