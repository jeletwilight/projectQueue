package com.example.jelelight.servicequeuing;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class ConfirmBActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceToken,mReferenceUser,mReferenceMyCount,
            mReferenceQueue,mReferenceMyUser,mRoomQueue;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Integer token,myCount,qid;
    private boolean inRoom;
    private Boolean isMineLoaded,isTokenLoaded;
    private Button okBtn,cancelBtn;
    private TextView remainTV,qTV;

    private List<String> inRoomList = new ArrayList<>();
    private List<String> myQid = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_confirm_b);
        mDatabase = FirebaseDatabase.getInstance();
        bindView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        inRoom = false;
        if(mUser == null){
            startActivity(new Intent(ConfirmBActivity.this,MainActivity.class));
            finish();
        }
        managePage();

    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.BokBtn:
                    okClick();
                    break;
                case R.id.BcancelBtn:
                    cancelClick();
                    break;
            }
        }
    };

    private void bindView(){
        remainTV = findViewById(R.id.Bremaincount);
        qTV = findViewById(R.id.BqueueID);
        okBtn = findViewById(R.id.BokBtn);
        cancelBtn = findViewById(R.id.BcancelBtn);
        okBtn.setOnClickListener(onClickListener);
        cancelBtn.setOnClickListener(onClickListener);
    }

    private void managePage(){
        isMineLoaded = Boolean.FALSE;
        isTokenLoaded = Boolean.FALSE;

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceToken = mDatabase.getReference().child("Clinic").child("Bqueue_token");
        mReferenceUser = mDatabase.getReference().child("Users");
        mReferenceMyUser = mDatabase.getReference().child("Users").child(mUser.getUid());
        mReferenceMyCount = mReferenceMyUser.child("queueNo");
        mReferenceQueue = mDatabase.getReference().child("Clinic").child("Bqueues");
        mRoomQueue = mDatabase.getReference("Clinic").child("rooms");

        readInQueue();
        readQID();
        //readMyQID();
        readToken();
        readMyCount();
        roomQueueRead();

        /*if(isTokenLoaded && isMineLoaded) {
            remainTV.setText(String.valueOf(myCount - token));
        }*/
    }

    private void okClick(){
        onBackPressed();
        this.finish();
    }

    private void cancelClick(){
        Intent i = new Intent(getApplicationContext(),PopBActivity.class);
        startActivity(i);
    }

    private void readQID(){
        mReferenceQueue.orderByChild("user").equalTo(String.valueOf(mUser.getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myQid.clear();
                for(DataSnapshot childsnapshot : dataSnapshot.getChildren()){
                    if(childsnapshot.child("status").getValue().equals("waiting")) {
                        String key = childsnapshot.getKey();
                        qid = Integer.valueOf(key);
                        myQid.add(key);
                        qTV.setText("B" + key.toString());
                    }
                }
                Log.d("ReadQID", myQid.get(0).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMyQID(){
        mReferenceQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myQid.clear();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    if(keyNode.child("user").equals(mUser.getUid())){
                        myQid.add(keyNode.getKey());
                    }
                    Log.d("ReadQIDCompare", keyNode.child("user").getValue().toString() + "," + mUser.getUid());
                }

                Log.d("ReadQID", myQid.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readInQueue(){
        mReferenceMyUser.child("inQueue").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean iq = (Boolean) dataSnapshot.getValue(Boolean.class);
                if(iq == Boolean.FALSE) {
                    startActivity(new Intent(ConfirmBActivity.this, LobbyActivity.class));
                    finish();
                }
                //Log.d("CountReadQueue", String.valueOf(inqueue));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMyCount(){
        mReferenceMyCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myCount = dataSnapshot.getValue(Integer.class);
                isMineLoaded = Boolean.TRUE;
                //remainTV.setText(String.valueOf(myCount-token));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void readToken(){
        mReferenceToken.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                token = dataSnapshot.getValue(Integer.class);
                isTokenLoaded = Boolean.TRUE;
                //remainTV.setText(String.valueOf(myCount-token));
                if(isTokenLoaded && isMineLoaded) {
                    if(myCount - token-1 >= 0) {
                        remainTV.setText(String.valueOf(myCount - token));
                    }else if(myCount - token-1 >= -3){
                        remainTV.setText(String.valueOf("Soon"));
                        inRoom = true;
                    }else{
                        remainTV.setText(String.valueOf("Skipped"));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void roomQueueRead(){
        mRoomQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inRoomList.clear();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    if(keyNode.child("queue").exists()) {
                        inRoomList.add(keyNode.child("queue").getValue().toString());
                    }
                }
                /*if(inRoomList.contains(myQid.get(0))){
                    startActivity(new Intent(ConfirmBActivity.this,GotQueueActivity.class));
                    finish();
                }
                Log.d("RoomQueueRead", " INROOMLIST : " +inRoomList.toString() + "QID : " + myQid.toString());*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
