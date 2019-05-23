package com.example.jelelight.servicequeuing;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class SelectCaseActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRefType,mReferenceUser;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Menu menu;
    private Button tNormal_btn,tBand_btn;

    private boolean inQ;

    private List<String> nextPageQueue = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_case);
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
        pageManage();
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
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                return true;
            case R.id.action_profile2:
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
                return true;
            case R.id.action_profile3:
                mAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Logged Out.",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bind(){
        tNormal_btn = findViewById(R.id.to_normal_btn);
        tBand_btn = findViewById(R.id.to_bandage_btn);

        tNormal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LobbyActivity.class));
            }
        });

        tBand_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),BandageLobbyActivity.class));
            }
        });
    }

    private void pageManage(){
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUser = mDatabase.getReference("Users");

        readUser();

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
                        }
                    }
                }

                if(!keys.contains(mUser.getUid())){
                    mReferenceUser.child(mUser.getUid()).child("name").setValue(String.valueOf(mUser.getDisplayName()));
                    mReferenceUser.child(mUser.getUid()).child("inQueue").setValue(Boolean.FALSE);
                    mReferenceUser.child(mUser.getUid()).child("points").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}
