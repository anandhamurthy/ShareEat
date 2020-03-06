package com.shareeat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shareeat.Models.Donated;
import com.shareeat.Models.Users;
import com.shareeat.Notification.APIService;
import com.shareeat.Notification.Client;
import com.shareeat.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DonatedAdapter extends RecyclerView.Adapter<DonatedAdapter.ImageViewHolder> {

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private String mCurrentUserId, user_key="";
    APIService apiService;

    private DatabaseReference mDonatedDatabase,mUsersDatabase;

    private Context mContext;
    private List<Donated> mDonated;

    public DonatedAdapter(Context context, List<Donated> donateds, String user_key) {
        mContext = context;
        mDonated = donateds;
        this.user_key=user_key;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_layout_donated, parent, false);
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
        mDonatedDatabase = FirebaseDatabase.getInstance().getReference().child("Donated");
        mDonatedDatabase.keepSynced(true);

        final Donated donated = mDonated.get(position);
        
        holder.Donated_Quantity.setText("Quantity : " +donated.getQuantity() +" kg / nos");
        holder.Donated_Food_Name.setText("Food Name : " +donated.getFood_name());
        holder.Donated_Best_Before.setText("Best Before : " +donated.getBest_before()+" hrs");
        if (donated.getFood_type().equals("Veg")){
            holder.Donated_Food_Type.setImageResource(R.drawable.veg);
        }else{
            holder.Donated_Food_Type.setImageResource(R.drawable.non_veg);
        }


        if (user_key.equals("putters")){
            UserInformation(holder.Donated_Name, holder.Donated_Phone, holder.Donated_Address, donated.getSender_user_id());
        }else{
            UserInformation(holder.Donated_Name, holder.Donated_Phone, holder.Donated_Address, donated.getUser_id());
        }



    }
    private void UserInformation(final TextView name, final TextView phone, final TextView address, String event_user_id) {

        mUsersDatabase.child(event_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                name.setText(user.getName());
                phone.setText(user.getPhone_number());
                address.setText(user.getAddress());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return mDonated.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView Donated_Name,Donated_Phone, Donated_Food_Name, Donated_Address, Donated_Quantity, Donated_Best_Before;
        public ImageView Donated_Food_Type;

        public ImageViewHolder(View itemView) {
            super(itemView);
            Donated_Name = itemView.findViewById(R.id.donated_name);
            Donated_Phone = itemView.findViewById(R.id.donated_phone);
            Donated_Quantity = itemView.findViewById(R.id.donated_quantity);
            Donated_Food_Name = itemView.findViewById(R.id.donated_food_name);
            Donated_Food_Type = itemView.findViewById(R.id.donated_food_type);
            Donated_Best_Before = itemView.findViewById(R.id.donated_best_before);
            Donated_Address = itemView.findViewById(R.id.donated_address);

        }
    }
}