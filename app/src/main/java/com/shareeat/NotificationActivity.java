package com.shareeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shareeat.Adapters.NotificationAdapter;
import com.shareeat.Models.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView Notifications_List;
    private NotificationAdapter notificationsAdapter;
    private List<Notification> notificationsList;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private String mCurrentUserId;
    private DatabaseReference mNotificationsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Notifications_List =findViewById(R.id.list);
        Notifications_List.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(NotificationActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        Notifications_List.setLayoutManager(mLayoutManager);
        notificationsList = new ArrayList<>();
        notificationsAdapter = new NotificationAdapter(NotificationActivity.this, notificationsList);
        Notifications_List.setAdapter(notificationsAdapter);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mCurrentUserId = mFirebaseUser.getUid();

        mNotificationsDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mNotificationsDatabase.keepSynced(true);

        readNotifications();


    }

    private void readNotifications() {

        mNotificationsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Notifications_List.setVisibility(View.VISIBLE);
                    notificationsList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Notification notifications = snapshot.getValue(Notification.class);
                        if (notifications.getUser_id().equals(mCurrentUserId))
                        notificationsList.add(notifications);
                    }

                    Collections.reverse(notificationsList);
                    notificationsAdapter.notifyDataSetChanged();
                } else {
                    Notifications_List.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

