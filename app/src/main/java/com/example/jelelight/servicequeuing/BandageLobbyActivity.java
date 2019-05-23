package com.example.jelelight.servicequeuing;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.List;

public class BandageLobbyActivity extends AppCompatActivity {

    private Calendar cld;

    private Menu menu;
    private Integer count,keyCount,qCount;
    private boolean open,reservable;
    private Boolean inQ;
    private Button reserveBtn,infoBtn;
    private TextView queueCount,clinicStat;
    private EditText symField;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceStatus,mReferenceCount,
            mReferenceUser,mReferenceQueue,
            mReferenceQCount;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private List<ExUser> eusers = new ArrayList<>();
    private List<String> queueID = new ArrayList<>();
    private List<String> waitingQID = new ArrayList<>();
    private List<String> allQID = new ArrayList<>();
    private List<String> nextPageQueue = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandage_lobby);
        cld = Calendar.getInstance();
        reservable = false;
        bindView();
        pageManage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        inQ = false;
        reservable = false;
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser == null){
            startActivity(new Intent(BandageLobbyActivity.this,MainActivity.class));
            finish();
        }
        pageManage();
        readUser();
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
                startActivity(new Intent(BandageLobbyActivity.this,ProfileActivity.class));
                return true;
            case R.id.action_profile2:
                if(inQ) {
                    toCurrentQueue();
                }else{
                    Toast.makeText(this, "You are not in Queue now!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_profile3:
                mAuth.getInstance().signOut();
                Toast.makeText(BandageLobbyActivity.this, "Logged Out.",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(BandageLobbyActivity.this,MainActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    void bindView(){
        reserveBtn = findViewById(R.id.BreserveBtn);
        infoBtn = findViewById(R.id.BinfoBtn);
        queueCount = findViewById(R.id.BqueueCount_txt);
        clinicStat = findViewById(R.id.BclinicStat);

        reserveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toConfirm();
            }
        });
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toInfo();
            }
        });
        queueCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAllQueue();
            }
        });
    }

    void pageManage(){
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceQCount = mDatabase.getReference().child("Clinic").child("Bqueue_count");
        mReferenceCount = mDatabase.getReference().child("Clinic").child("Bqueue_token");
        mReferenceStatus = mDatabase.getReference().child("Clinic").child("status");
        mReferenceQueue = mDatabase.getReference().child("Clinic").child("Bqueues");
        mReferenceUser = mDatabase.getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        readCount();
        count = allQID.size();
        readQCount();
        open = readStatus();
        readUser();
        getAllQueueID();
        reserveBtn.setEnabled(open);
    }

    void toAllQueue(){
        Intent intent = new Intent(BandageLobbyActivity.this,AllBQueueActivity.class);
        startActivity(intent);
    }

    void toCurrentQueue(){
        if(inQ) {
            if(nextPageQueue.get(0).toString() == "normal") {
                startActivity(new Intent(getApplicationContext(), ConfirmActivity.class));
            }else if(nextPageQueue.get(0).toString() == "bandage"){
                startActivity(new Intent(getApplicationContext(), ConfirmBActivity.class));
            }else{
                Toast.makeText(this, "Cannot go to my QueuePage", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "You are not in Queue now!", Toast.LENGTH_SHORT).show();
        }
    }

    void toConfirm(){
        if(reservable == true) {
            mReferenceQCount.setValue(qCount + 1);
            //mReferenceCount.setValue(count + 1);
            mReferenceUser.child(mUser.getUid()).child("inQueue").setValue(Boolean.TRUE);
            mReferenceUser.child(mUser.getUid()).child("queueNo").setValue(qCount + 1);
            mReferenceUser.child(mUser.getUid()).child("queueType").setValue("bandage");
            mReferenceUser.child(mUser.getUid()).child("name").setValue(mUser.getDisplayName());
            mReferenceQueue.child(String.valueOf(qCount + 1)).child("user").setValue(mUser.getUid());
            mReferenceQueue.child(String.valueOf(qCount + 1)).child("case").setValue("bandage");
            mReferenceQueue.child(String.valueOf(qCount + 1)).child("status").setValue("waiting");

            String t = String.valueOf(cld.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(cld.get(Calendar.MINUTE))
                    + " " + String.valueOf(cld.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(cld.get(Calendar.MONTH))
                    + "/" + String.valueOf(cld.get(Calendar.YEAR));

            mReferenceQueue.child(String.valueOf(qCount + 1)).child("time").setValue(t);

            if (mUser.getDisplayName() != "") {
                mReferenceQueue.child(String.valueOf(qCount + 1)).child("name").setValue(mUser.getDisplayName());
            } else {
                mReferenceQueue.child(String.valueOf(qCount + 1)).child("name").setValue("Anonymous");
            }

            reservable = false;
        }
        startActivity(new Intent(BandageLobbyActivity.this,ConfirmBActivity.class));
        finish();
        //finish();

    }

    void toInfo(){
        Intent confirmIntent = new Intent(BandageLobbyActivity.this,ClinicInfoActivity.class);
        startActivity(confirmIntent);
    }


    private void readCount(){
        final Integer[] counter = new Integer[1];
        mReferenceQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allQID.clear();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()){
                    allQID.add(keyNode.getKey());
                }
                //queueCount.setText(count.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readQCount(){
        final Integer[] counter = new Integer[1];
        mReferenceQCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                counter[0] = dataSnapshot.getValue(Integer.class);
                qCount = counter[0];
                //queueCount.setText(qCount.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean readStatus(){
        final boolean[] cStatus = new boolean[1];
        mReferenceStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cStatus[0] = dataSnapshot.getValue(boolean.class);
                if(cStatus[0] == true && reservable == true){
                    clinicStat.setText("Open");
                    clinicStat.setTextColor(Color.parseColor("#449944"));
                    reserveBtn.setEnabled(true);
                    reserveBtn.setBackground(getResources().getDrawable(R.drawable.dark_v_round_button));
                }else if(cStatus[0] == true && reservable == false) {
                    clinicStat.setText("Open (In Queue)");
                    clinicStat.setTextColor(Color.parseColor("#449944"));
                    reserveBtn.setEnabled(false);
                    reserveBtn.setBackground(getResources().getDrawable(R.drawable.gray_round_button));
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
        mReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> keys = new ArrayList<>();
                nextPageQueue.clear();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    if(!keyNode.child("inQueue").exists()){
                        mReferenceUser.child(mUser.getUid()).child("inQueue").setValue(Boolean.FALSE);
                    }
                    if(keyNode.getKey().equals(mUser.getUid())) {
                        if (keyNode.child("inQueue").getValue(Boolean.class) == Boolean.TRUE) {
                            inQ = true;
                            reservable = false;
                            if(keyNode.child("queueType").exists()){
                                if(keyNode.child("queueType").getValue().toString().equals("normal")){
                                    nextPageQueue.add("normal");
                                }else if (keyNode.child("queueType").getValue().toString().equals("bandage")){
                                    nextPageQueue.add("bandage");
                                }else{
                                    nextPageQueue.add("nothing");
                                }
                            }else{
                                nextPageQueue.add("nothing");
                            }
                        } else {
                            inQ = false;
                            nextPageQueue.add("nothing");
                            reservable = true;
                        }
                    }
                }

                if(!keys.contains(mUser.getUid())){
                    mReferenceUser.child(mUser.getUid()).child("name").setValue(String.valueOf(mUser.getDisplayName()));
                    mReferenceUser.child(mUser.getUid()).child("inQueue").setValue(Boolean.FALSE);
                    mReferenceUser.child(mUser.getUid()).child("points").setValue(0);
                    reservable = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getAllQueueID(){
        mReferenceQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                queueID.clear();
                waitingQID.clear();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    queueID.add(keyNode.getKey());
                    if(keyNode.child("status").exists()) {
                        if (keyNode.child("status").getValue(String.class).equals("waiting")) {
                            waitingQID.add(keyNode.getKey());
                        }
                    }
                }
                queueCount.setText(String.valueOf(waitingQID.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
