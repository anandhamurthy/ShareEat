package com.shareeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Location;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shareeat.Adapters.DetailsAdapter;
import com.shareeat.Models.Details;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ShowGettersActivity extends AppCompatActivity {

    private RecyclerView mDetails;
    private DetailsAdapter eventsAdapter;
    private List<Details> eventsList;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private String mCurrentUserId, lon, lat;
    private DatabaseReference mEventsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_getters);

        final String quantity = getIntent().getStringExtra("quantity");
        final String best_before = getIntent().getStringExtra("best_before");
        lon = getIntent().getStringExtra("lon");
        lat = getIntent().getStringExtra("lat");
        final String food_type = getIntent().getStringExtra("food_type");
        final String food_name = getIntent().getStringExtra("food_name");
        final String address = getIntent().getStringExtra("address");

        mDetails=findViewById(R.id.details);
        mDetails.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ShowGettersActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mDetails.setLayoutManager(mLayoutManager);
        eventsList = new ArrayList<>();
        eventsAdapter = new DetailsAdapter(this, eventsList, lon,lat, quantity, best_before, food_name, food_type, address);
        mDetails.setAdapter(eventsAdapter);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mCurrentUserId = mFirebaseUser.getUid();

        mEventsDatabase = FirebaseDatabase.getInstance().getReference().child("Details");
        mEventsDatabase.keepSynced(true);
        readDetails(food_type, quantity);
    }

    private void readDetails(final String food_type, final String quan) {
        mEventsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Details events = snapshot.getValue(Details.class);
                    double dis=MyLagLat(events.getLat(), events.getLon(),lat,lon);
                    if(events.getOnline().equals("true") && events.getFt().equals(food_type) && dis<=5 && Integer.parseInt(events.getKg())<=Integer.parseInt(quan))
                    eventsList.add(events);
                }
                eventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private double MyLagLat(double lat, double lon, String latitude, String longitude) {
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(lat);
        startPoint.setLongitude(lon);

        Location endPoint=new Location("locationB");
        endPoint.setLatitude(Double.parseDouble(latitude));
        endPoint.setLongitude(Double.parseDouble(longitude));

        float dis=startPoint.distanceTo(endPoint);
        DecimalFormat df = new DecimalFormat("###.##");
        return Double.parseDouble(df.format(dis*0.001));
       // distanc.setText(df.format(dis*0.001)+"\n KM");


    }
}
