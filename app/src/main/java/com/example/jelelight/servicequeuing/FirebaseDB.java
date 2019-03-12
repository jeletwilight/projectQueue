package com.example.jelelight.servicequeuing;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDB {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceQueue;
    private List<Queue> queues = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    public FirebaseDB(){
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceQueue = mDatabase.getReference("Clinic");
    }

    public interface DataStatus{
        void DataIsLoaded(List<Queue> queues,List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }


    public void readQueues(final DataStatus dataStatus){
        mReferenceQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                queues.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Queue queue = keyNode.getValue(Queue.class);
                    queues.add(queue);
                }
                dataStatus.DataIsLoaded(queues,keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
