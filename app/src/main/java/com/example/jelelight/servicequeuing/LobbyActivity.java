package com.example.jelelight.servicequeuing;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends AppCompatActivity {

    private Menu menu;
    private Integer count;
    private boolean open,reservable;
    private Button reserveBtn,infoBtn;
    private TextView queueCount,clinicStat;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceStatus,mReferenceCount,mReferenceUser,mReferenceQueue,mReferenceKeys;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private List<String> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        reservable = true;
        bindView();
        pageManage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.getInstance();
        readUser();
        mUser = mAuth.getCurrentUser();
        if(mUser == null){
            startActivity(new Intent(LobbyActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.custom_menu,menu);
        this.menu = menu;
        menu.findItem(R.id.profileName).setTitle(mUser.getDisplayName());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_profile1:
                startActivity(new Intent(LobbyActivity.this,ProfileActivity.class));
                return true;
            case R.id.action_profile2:
                return true;
            case R.id.action_profile3:
                mAuth.getInstance().signOut();
                Toast.makeText(LobbyActivity.this, "Logged Out.",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LobbyActivity.this,MainActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.queueCount_txt:
                    toAllQueue();
                    break;
                case R.id.reserveBtn:
                    toConfirm();
                    break;
                case R.id.infoBtn:
                    toInfo();
                    break;
            }
        }
    };

    void bindView(){
        reserveBtn = findViewById(R.id.reserveBtn);
        infoBtn = findViewById(R.id.infoBtn);
        queueCount = findViewById(R.id.queueCount_txt);
        clinicStat = findViewById(R.id.clinicStat);
        reserveBtn.setOnClickListener(onClickListener);
        infoBtn.setOnClickListener(onClickListener);
        queueCount.setOnClickListener(onClickListener);
    }

    void pageManage(){
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceStatus = mDatabase.getReference().child("Clinic").child("status");
        mReferenceCount = mDatabase.getReference().child("Clinic").child("queue_count");
        mReferenceQueue = mDatabase.getReference().child("Clinic").child("queues");
        mReferenceKeys = mDatabase.getReference().child("Keys");
        mReferenceUser = mDatabase.getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        count = readCount();
        open = readStatus();
        readUser();
        reserveBtn.setEnabled(open);
    }

    void toAllQueue(){
        Intent intent = new Intent(LobbyActivity.this,AllQueueActivity.class);
        startActivity(intent);
    }

    void toConfirm(){
        if(mReferenceUser.child(mUser.getUid()).child("inQueue").equals(false)) {
            mReferenceCount.setValue(count + 1);
            mReferenceUser.child(mUser.getUid()).child("inQueue").setValue(true);
            mReferenceUser.child(mUser.getUid()).child("queueNo").setValue(count + 1);
            mReferenceQueue.child(String.valueOf(count + 1)).child("user").setValue(mUser.getUid());
            mReferenceQueue.child(String.valueOf(count + 1)).child("case").setValue("normal");
        }
        Intent intent = new Intent(LobbyActivity.this,ConfirmActivity.class);
        startActivity(intent);
    }

    void toInfo(){
        mReferenceStatus.setValue(false);
        open = !open;
        if (open) {
            mReferenceStatus.setValue(true);
        } else {
            mReferenceStatus.setValue(false);
        }

        //Intent confirmIntent = new Intent(LobbyActivity.this,ConfirmActivity.class);
        //startActivity(confirmIntent);
    }


    private Integer readCount(){
        final Integer[] counter = new Integer[1];
        mReferenceCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                counter[0] = dataSnapshot.getValue(Integer.class);
                count = counter[0];
                queueCount.setText(count.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return counter[0];
    }

    private boolean readStatus(){
        final boolean[] cStatus = new boolean[1];
        mReferenceStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cStatus[0] = dataSnapshot.getValue(boolean.class);
                if(cStatus[0] == true){
                    clinicStat.setText("Open");
                    clinicStat.setTextColor(Color.parseColor("#449944"));
                    reserveBtn.setEnabled(true);
                    reserveBtn.setBackground(getResources().getDrawable(R.drawable.dark_v_round_button));
                }else{
                    clinicStat.setText("Closed");
                    clinicStat.setTextColor(Color.parseColor("#BB0000"));
                    reserveBtn.setEnabled(false);
                    reserveBtn.setBackground(getResources().getDrawable(R.drawable.gray_round_button));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                cStatus[0] = false;
            }
        });
        return cStatus[0];
    }

    private void readUser(){
        mReferenceKeys.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    String user = keyNode.getValue(String.class);
                    users.add(user);
                }

                if(!users.contains(mUser.getUid())){
                    mReferenceKeys.child(String.valueOf(users.size())).setValue(mUser.getUid());
                    mReferenceUser.child(mUser.getUid()).child("name").setValue(mUser.getDisplayName());
                    mReferenceUser.child(mUser.getUid()).child("inQueue").setValue(false);
                    mReferenceUser.child(mUser.getUid()).child("points").setValue(0);
                    reservable = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    void alwaysCheckOpen(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (open) {
//                    reserveBtn.setEnabled(true);
//                    reserveBtn.setBackground(getResources().getDrawable(R.drawable.dark_v_round_button));
//                } else {
//                    reserveBtn.setEnabled(false);
//                    reserveBtn.setBackground(getResources().getDrawable(R.drawable.gray_round_button));
//                }
//            }
//        }).start();
//    }

}
