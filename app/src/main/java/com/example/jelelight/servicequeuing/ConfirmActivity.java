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

public class ConfirmActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceToken,mReferenceUser,mReferenceMyCount,
                                mReferenceQueue,mReferenceMyUser;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Integer token,myCount,qid;
    private boolean inqueue;
    private Boolean isMineLoaded,isTokenLoaded;
    private Button okBtn,cancelBtn;
    private TextView remainTV,qTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_confirm);
        mDatabase = FirebaseDatabase.getInstance();
        bindView();
        managePage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser == null){
            startActivity(new Intent(ConfirmActivity.this,MainActivity.class));
            finish();
        }
        managePage();

    }

    @Override
    protected void onResume() {
        super.onResume();
        managePage();
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.okBtn:
                    okClick();
                    break;
                case R.id.cancelBtn:
                    cancelClick();
                    break;
            }
        }
    };

    void bindView(){
        remainTV = findViewById(R.id.remaincount);
        qTV = findViewById(R.id.queueID);
        okBtn = findViewById(R.id.okBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(onClickListener);
        cancelBtn.setOnClickListener(onClickListener);
    }

    void managePage(){
        isMineLoaded = Boolean.FALSE;
        isTokenLoaded = Boolean.FALSE;

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceToken = mDatabase.getReference().child("Clinic").child("queue_token");
        mReferenceUser = mDatabase.getReference().child("Users");
        mReferenceMyUser = mDatabase.getReference().child("Users").child(mUser.getUid());
        mReferenceMyCount = mReferenceMyUser.child("queueNo");
        mReferenceQueue = mDatabase.getReference().child("Clinic").child("queues");

        readInQueue();
        readQID();

        readToken();
        readMyCount();

        /*if(isTokenLoaded && isMineLoaded) {
            remainTV.setText(String.valueOf(myCount - token));
        }*/
    }

    void okClick(){
        Intent okIntent = new Intent(ConfirmActivity.this,LobbyActivity.class);
        startActivity(okIntent);
        //finish();
    }

    void cancelClick(){
        //mReferenceQueue.child(qid.toString()).child("status").setValue("canceled");
        Intent i = new Intent(getApplicationContext(),PopWarningActivity.class);
        startActivity(i);
        //Intent cancelIntent = new Intent(ConfirmActivity.this,LobbyActivity.class);
        //startActivity(cancelIntent);
        //finish();
    }

    private void readQID(){
        mReferenceQueue.orderByChild("user").equalTo(String.valueOf(mUser.getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childsnapshot : dataSnapshot.getChildren()){
                    String key = childsnapshot.getKey();
                    qid = Integer.valueOf(key);
                    qTV.setText(key);
                }
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
                    startActivity(new Intent(ConfirmActivity.this, LobbyActivity.class));
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
                    remainTV.setText(String.valueOf(myCount - token));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
