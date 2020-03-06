package com.shareeat.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
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
import com.shareeat.Models.Details;
import com.shareeat.Models.Users;
import com.shareeat.Notification.APIService;
import com.shareeat.Notification.Client;
import com.shareeat.Notification.Data;
import com.shareeat.Notification.MyResponse;
import com.shareeat.Notification.Sender;
import com.shareeat.Notification.Token;
import com.shareeat.R;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsAdapter  extends RecyclerView.Adapter<DetailsAdapter.ImageViewHolder> {

    APIService apiService;
    private Context mContext;
    private List<Details> mEvents;
    private FirebaseAuth mAuth;
    private DatabaseReference mDetailsDatabase,mUsersDatabase,mNotificationDatabase, mPuttersDetailsDatabase, mNoticationKeyDatabase, mConfirmDatabase;
    private FirebaseUser mFirebaseUser;
    long count=0;
    private String mCurrentUserId, quantity, best_before, longitude,latitude, food_name, food_type, address, distance;

    public DetailsAdapter(Context context, List<Details> events, String lon, String lat, String quan, String best, String food_name, String food_type, String address) {
        mContext = context;
        mEvents = events;
        quantity=quan;
        best_before=best;
        longitude=lon;
        latitude=lat;
        this.food_name=food_name;
        this.food_type=food_type;
        this.address=address;
    }



    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_layout_details, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mCurrentUserId = mFirebaseUser.getUid();
        mDetailsDatabase = FirebaseDatabase.getInstance().getReference().child("Details");
        mDetailsDatabase.keepSynced(true);
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mNotificationDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mPuttersDetailsDatabase = FirebaseDatabase.getInstance().getReference().child("Putters Details");
        mPuttersDetailsDatabase.keepSynced(true);

        mConfirmDatabase = FirebaseDatabase.getInstance().getReference().child("Confirms");
        mConfirmDatabase.keepSynced(true);

        mNoticationKeyDatabase = FirebaseDatabase.getInstance().getReference().child("Notification Key");
        mNoticationKeyDatabase.keepSynced(true);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        final Details events = mEvents.get(position);
        UserInformation(holder.Name,events.getUser_id());
        getConfirmed(holder.Confirm,events.getUser_id());
        MyLagLat(holder.Distance, events.getLat(), events.getLon(),latitude,longitude);
        DetailInformation(holder.Total_Members, holder.Food_Needed, holder.Food_Type, holder.Delivery, events.getTp(), events.getKg(), events.getFt(), events.getDe());

        holder.Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPuttersDetailsDatabase.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()){
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("need_food", false);

                            mPuttersDetailsDatabase.child(mCurrentUserId).setValue(hashMap);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                if (holder.Confirm.getTag().equals("confirm")) {
                    addDialog(events.getUser_id(), mCurrentUserId);
                }

            }
        });

    }

    private void getConfirmed(final ImageView confirm, final String user_id) {
        mConfirmDatabase.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user_id).exists()) {
                   confirm.setTag("confirmed");
                } else {
                    confirm.setTag("confirm");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView Name, Total_Members, Food_Needed, Distance, Delivery;
        public ImageView Food_Type, Confirm;

        public ImageViewHolder(View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.details_name);
            Total_Members = itemView.findViewById(R.id.details_total_people);
            Food_Needed = itemView.findViewById(R.id.details_food_needed);
            Distance=itemView.findViewById(R.id.details_distance);
            Food_Type=itemView.findViewById(R.id.details_food_type);
            Confirm=itemView.findViewById(R.id.details_confirm);
            Delivery=itemView.findViewById(R.id.details_delivery);
        }
    }

    private void sendNotification(final String Reciever ,final String Message, final String user_id) {

        final DatabaseReference token = FirebaseDatabase.getInstance().getReference().child("Tokens");
        Query query = token.orderByKey().equalTo(Reciever);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Token token1 = snapshot.getValue(Token.class);
                    Data data = new Data(user_id, R.drawable.icon, " ", Message, Reciever);

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

    private void UserInformation(final TextView name, String user_id) {

        mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                name.setText(user.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void addNotification(String userid, String message, String food_type, String quantity, String address, String food_name, String best_before, String distance) {


            final String key = mNotificationDatabase.push().getKey();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("user_id", userid);
            hashMap.put("sender_user_id", mCurrentUserId);
            hashMap.put("message", message);
            hashMap.put("food_type", food_type);
            hashMap.put("quantity", quantity);
            hashMap.put("address", address);
            hashMap.put("food_name", food_name);
            hashMap.put("best_before", best_before);
            hashMap.put("distance", distance);
            hashMap.put("accepted", false);
            hashMap.put("sent", false);
            hashMap.put("noti_key", key);

            mNotificationDatabase.child(key).setValue(hashMap);

            mNoticationKeyDatabase.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    count=dataSnapshot.getChildrenCount();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if (count<3){
                HashMap<String, Object> notiMap = new HashMap<>();
                notiMap.put("key_"+count+"", key);
                mNoticationKeyDatabase.child(mCurrentUserId).updateChildren(notiMap);
            }
            count++;




    }

    private void MyLagLat(final TextView distanc, double lat, double lon, String latitude, String longitude) {
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(lat);
        startPoint.setLongitude(lon);

        Location endPoint=new Location("locationB");
        endPoint.setLatitude(Double.parseDouble(latitude));
        endPoint.setLongitude(Double.parseDouble(longitude));

        float dis=startPoint.distanceTo(endPoint);
        DecimalFormat df = new DecimalFormat("###.##");
        distance=df.format(dis*0.001);
        distanc.setText(df.format(dis*0.001)+"\n KM");


    }

    private void DetailInformation(final TextView total_members, final TextView food_needed, ImageView food_Type, TextView delivery, String tp, String kg, String ft, String de) {

        total_members.setText("Total Members : " +tp);
        food_needed.setText("Quantity Needed in KG : "+kg+" kg");
        if (ft.equals("Veg")){
            food_Type.setImageResource(R.drawable.veg);
        }else{
            food_Type.setImageResource(R.drawable.non_veg);
        }
        delivery.setText(de);


    }

    private void addDialog(final String user_id, final String mCurrentUserId) {

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Confirm");
            builder.setMessage("Do you want send Notification ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                                mConfirmDatabase.child(mCurrentUserId).child(user_id).setValue(true);
                                sendNotification(user_id, "Food is available Please Confirm.", mCurrentUserId);
                                addNotification(user_id, "Food is available Please Confirm.", food_type, quantity, address, food_name, best_before, distance);

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
}