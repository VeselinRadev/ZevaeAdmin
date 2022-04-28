package com.veselin.zevaeadmin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    List<User> users;
    private RecyclerView recyclerViewChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        users = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        login("admin@gmail.com", "admin1234");
    }

    private void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        initChatRecyclerView();
                        displayUsers();
                    } else {
                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void initChatRecyclerView() {
        recyclerViewChat = findViewById(R.id.recyclerView);
        recyclerViewChat.setHasFixedSize(true);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        updateChatAdapter();
    }

    private void updateChatAdapter(){
        ChatsAdapter adapter = new ChatsAdapter(this, users);
        recyclerViewChat.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void displayUsers(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    FirebaseDatabase.getInstance().getReference("Users").child(snap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshotUser) {
                            User user = snapshotUser.getValue(User.class);
                            if(snapshotUser.child("IsBanned").getValue() != null)
                            user.setIsBanned(snapshotUser.child("IsBanned").getValue().toString());
                            users.add(user);
                            updateChatAdapter();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
