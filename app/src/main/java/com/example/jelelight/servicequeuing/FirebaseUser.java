package com.example.jelelight.servicequeuing;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUser {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceQueue;
    private List<User> users = new ArrayList<>();

    public FirebaseUser(){
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceQueue = mDatabase.getReference("Users");
    }

    public interface UserStatus{
        void UserIsLoaded(List<User> users, List<String> keys);
        void UserIsInserted();
        void UserIsUpdated();
        void UserIsDeleted();
    }


    public void readUsers(final FirebaseUser.UserStatus userStatus){
        mReferenceQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    User user = keyNode.getValue(User.class);
                    users.add(user);
                }
                userStatus.UserIsLoaded(users,keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
