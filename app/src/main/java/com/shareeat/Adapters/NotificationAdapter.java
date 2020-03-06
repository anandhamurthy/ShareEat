package com.shareeat.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shareeat.Models.Notification;
import com.shareeat.Models.Users;
import com.shareeat.Notification.APIService;
import com.shareeat.Notification.Client;
import com.shareeat.Notification.Data;
import com.shareeat.Notification.MyResponse;
import com.shareeat.Notification.Sender;
import com.shareeat.Notification.Token;
import com.shareeat.R;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ImageViewHolder> {

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private String mCurrentUserId;
    APIService apiService;

    private DatabaseReference mNotificationDatabase,mUsersDatabase, mConfirmDatabase, mDetailsDatabase, mPuttersDetailsDatabase,mDonatedDatabase, mNoticationKeyDatabase;

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        mContext = context;
        mNotification = notifications;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_layout_notifications, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mCurrentUserId = mFirebaseUser.getUid();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mNotificationDatabase.keepSynced(true);
        mDetailsDatabase = FirebaseDatabase.getInstance().getReference().child("Details");
        mDetailsDatabase.keepSynced(true);
        mPuttersDetailsDatabase = FirebaseDatabase.getInstance().getReference().child("Putters Details");
        mPuttersDetailsDatabase.keepSynced(true);

        mNoticationKeyDatabase = FirebaseDatabase.getInstance().getReference().child("Notification Key");
        mNoticationKeyDatabase.keepSynced(true);

        mDonatedDatabase = FirebaseDatabase.getInstance().getReference().child("Donated");
        mDonatedDatabase.keepSynced(true);

        mConfirmDatabase = FirebaseDatabase.getInstance().getReference().child("Confirms");
        mConfirmDatabase.keepSynced(true);

        final Notification notification = mNotification.get(position);

        holder.Notification_Message.setText(notification.getMessage());
        holder.Notification_Distance.setText("Distance : " +notification.getDistance()+" kms");
        holder.Notification_Address.setText(notification.getAddress());
        holder.Notification_Quantity.setText("Quantity : " +notification.getQuantity() +" kg / nos");
        holder.Notification_Food_Name.setText("Food Name : " +notification.getFood_name());
        holder.Notification_Best_Before.setText("Best Before : " +notification.getBest_before()+" hrs");
        if (notification.getFood_type().equals("Veg")){
            holder.Notification_Food_Type.setImageResource(R.drawable.veg);
        }else{
            holder.Notification_Food_Type.setImageResource(R.drawable.non_veg);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!notification.isAccepted()){
                    if (!notification.isSent()){
                        mPuttersDetailsDatabase.child(notification.getSender_user_id()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.child("need_food").getValue().equals(false)) {
                                        addDialog(notification.getSender_user_id(), mCurrentUserId, notification);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else{
//                        mPuttersDetailsDatabase.child(notification.getUser_id()).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.child("need_food").getValue().equals(true)){
////                                    HashMap<String, Object> confirmMap = new HashMap<>();
////                                    confirmMap.put("user_id", "");
////                                    confirmMap.put("need_food", false);
//
//                                    mPuttersDetailsDatabase.child(mCurrentUserId).removeValue();
//
//
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });

                        sendNotification(notification.getSender_user_id()," ","Ok. We Confirmed.", mCurrentUserId);
                    }


                }

            }
        });

        UserInformation(holder.Notification_Name, holder.Notification_Phone, notification.getUser_id());

    }

    private void sendNotification(final String Reciever, final String User_Name, final String Message, final String user_id) {

        final DatabaseReference token = FirebaseDatabase.getInstance().getReference().child("Tokens");
        Query query = token.orderByKey().equalTo(Reciever);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Token token1 = snapshot.getValue(Token.class);
                    Data data = new Data(user_id, R.drawable.icon, User_Name, Message, Reciever);

                    Sender sender = new Sender(data, token1.getToken());
                    apiService.sendNotification(sender).enqueue(
                            new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    if (response.code() == 200) {
                                        if (response.body().sucess == 1) {
                                            Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            }
                    );
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void UserInformation(final TextView name, final TextView phone, String event_user_id) {

        mUsersDatabase.child(event_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                name.setText(user.getName());
                phone.setText(user.getPhone_number());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void addDialog(final String user_id, final String mCurrentUserId, final Notification notification) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Confirm");
        builder.setMessage("Do you need Food ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String key = mNotificationDatabase.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("sender_user_id", mCurrentUserId);
                        hashMap.put("user_id", user_id);
                        hashMap.put("message", "Ok. Send us Food.");
                        hashMap.put("food_type", notification.getFood_type());
                        hashMap.put("quantity", notification.getQuantity());
                        hashMap.put("address", notification.getAddress());
                        hashMap.put("food_name", notification.getFood_name());
                        hashMap.put("best_before", notification.getBest_before());
                        hashMap.put("distance", notification.getDistance());
                        hashMap.put("accepted", false);
                        hashMap.put("sent", true);
                        hashMap.put("noti_key", key);
                        mNotificationDatabase.child(key).setValue(hashMap);

                        HashMap<String, Object> donatedMap = new HashMap<>();
                        donatedMap.put("sender_user_id", mCurrentUserId);
                        donatedMap.put("user_id", user_id);
                        donatedMap.put("food_type", notification.getFood_type());
                        donatedMap.put("quantity", notification.getQuantity());
                        donatedMap.put("food_name", notification.getFood_name());
                        donatedMap.put("best_before", notification.getBest_before());

                        mDonatedDatabase.child(mCurrentUserId).child(key).setValue(donatedMap);
                        mDonatedDatabase.child(user_id).child(key).setValue(donatedMap);

                        HashMap<String, Object> hashMap1 = new HashMap<>();
                        hashMap1.put("online", "false");

                        mDetailsDatabase.child(mCurrentUserId).updateChildren(hashMap1);


                        HashMap<String, Object> confirmMap = new HashMap<>();
                        confirmMap.put("user_id", mCurrentUserId);
                        confirmMap.put("need_food", true);

                        mPuttersDetailsDatabase.child(user_id).updateChildren(confirmMap);

                        mNoticationKeyDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long count=0;
                                for(;count<dataSnapshot.getChildrenCount();count++){
                                    HashMap<String, Object> upMap = new HashMap<>();
                                    upMap.put("accepted", true);
                                    mNotificationDatabase.child(dataSnapshot.child("key_"+count+"").getValue().toString()).updateChildren(upMap);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        mNoticationKeyDatabase.child(user_id).removeValue();

                        mConfirmDatabase.child(user_id).removeValue();

                        sendNotification(user_id," ","Ok. Send us Food.", mCurrentUserId);



                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public int getItemCount() {
        return mNotification.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView Notification_Message, Notification_Name,Notification_Phone, Notification_Food_Name, Notification_Distance, Notification_Address, Notification_Quantity, Notification_Best_Before;
        public ImageView Notification_Food_Type;

        public ImageViewHolder(View itemView) {
            super(itemView);
            Notification_Message = itemView.findViewById(R.id.notification_message);
            Notification_Name = itemView.findViewById(R.id.notification_name);
            Notification_Phone = itemView.findViewById(R.id.notification_phone);
            Notification_Distance = itemView.findViewById(R.id.notification_distance);
            Notification_Address = itemView.findViewById(R.id.notification_address);
            Notification_Quantity = itemView.findViewById(R.id.notification_quantity);
            Notification_Food_Name = itemView.findViewById(R.id.notification_food_name);
            Notification_Food_Type = itemView.findViewById(R.id.notification_food_type);
            Notification_Best_Before = itemView.findViewById(R.id.notification_best_before);

        }
    }
}