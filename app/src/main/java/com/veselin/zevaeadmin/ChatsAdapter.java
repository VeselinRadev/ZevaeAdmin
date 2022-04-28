package com.veselin.zevaeadmin;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.RecyclerViewHolder> {
    private List<User> users;
    private Context context;

    // RecyclerView recyclerView;
    public ChatsAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ChatsAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_list_item, parent, false);
        return new ChatsAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChatsAdapter.RecyclerViewHolder holder, int position) {
        final User mUser = users.get(position);
        holder.getName().setText(mUser.getUsername());
        Glide.with(context).load(FirebaseStorage.getInstance().getReferenceFromUrl(mUser.getImageURL())).centerCrop().into(holder.getImage());
        holder.getItem().setOnClickListener(view -> banDialog(mUser, holder, position));
    }

    void banDialog(User user, final ChatsAdapter.RecyclerViewHolder holder, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(((Activity) context));
        if(user.getIsBanned() == null) {
            builder.setMessage("Are you sure you want to ban user");

        }else {
            if (user.getIsBanned().equals("true")) {
                builder.setMessage("Are you sure you want to unban user");
            }else
            builder.setMessage("Are you sure you want to ban user");
        }
        builder
                .setPositiveButton("Yes", (dialog, id) -> {
                    if(user.getIsBanned() == null) {
                        FirebaseDatabase.getInstance().getReference("Users").child(user.getId()).child("IsBanned").setValue("true")
                                .addOnCompleteListener(task -> Toast.makeText((Activity)context, "Successfully", Toast.LENGTH_SHORT).show());
                        user.setIsBanned("true");

                    }else {
                        if (user.getIsBanned().equals("true")) {
                            FirebaseDatabase.getInstance().getReference("Users").child(user.getId()).child("IsBanned").setValue("false")
                                    .addOnCompleteListener(task -> Toast.makeText((Activity) context, "Successfully", Toast.LENGTH_SHORT).show());
                            user.setIsBanned("false");

                        } else {
                            FirebaseDatabase.getInstance().getReference("Users").child(user.getId()).child("IsBanned").setValue("true")
                                    .addOnCompleteListener(task -> Toast.makeText((Activity) context, "Successfully", Toast.LENGTH_SHORT).show());
                            user.setIsBanned("true");
                        }
                    }
                })
                .setNegativeButton("No", (dialog, id) -> {

                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }



    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private LinearLayout item;
        private CircularImageView image;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.item = itemView.findViewById(R.id.ll_parent);
            this.image = itemView.findViewById(R.id.image);
        }

        public LinearLayout getItem() {
            return item;
        }

        public TextView getName() {
            return name;
        }

        public CircularImageView getImage() {
            return image;
        }
    }
}
