package com.example.jelelight.servicequeuing;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GotQueueActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRefRoom,mRefUid;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private TextView room_tv,qid_tv,doctor_tv,uid_tv;
    private Button ok_btn,cancel_btn;

    private List<String> myQid = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_got_queue);
        bind();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser == null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        uid_tv.setText(mUser.getUid().toString());
        pageManage();
    }

    private void bind(){
        room_tv = findViewById(R.id.room_got);
        qid_tv = findViewById(R.id.qid_got);
        uid_tv = findViewById(R.id.uid_got);
        doctor_tv = findViewById(R.id.doctor_got);
        ok_btn = findViewById(R.id.ok_got);
        cancel_btn = findViewById(R.id.cancel_got);

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LobbyActivity.class));
                finish();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PopWarningActivity.class));
            }
        });
    }

    private void pageManage(){
        mDatabase = FirebaseDatabase.getInstance();
        mRefRoom = mDatabase.getReference("Clinic").child("rooms");
        mRefUid = mDatabase.getReference("Users");

        readUid();
        readRoomGot();
    }

    private void readRoomGot(){
        mRefRoom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot keynode : dataSnapshot.getChildren()){
                    if(String.valueOf(keynode.child("queue").getValue()).equals(myQid.get(0))){
                        room_tv.setText(keynode.getKey().toString());
                        doctor_tv.setText(keynode.child("doctor").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUid(){
        mRefUid.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myQid.clear();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()){
                    if(keyNode.getKey().equals(mUser.getUid())){
                        myQid.add(keyNode.child("queueNo").getValue().toString());
                        qid_tv.setText(keyNode.child("queueNo").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
