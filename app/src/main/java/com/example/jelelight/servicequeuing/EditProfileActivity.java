package com.example.jelelight.servicequeuing;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUser;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private EditText nameEdit,heightEdit,weightEdit,birthEdit,cautionEdit,phoneEdit;
    private RadioButton gendM,gendF,bloodA,bloodB,bloodO,bloodAB;

    private Button submitBtn;

    private String userID;
    private boolean hasKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        bind();
        userHasKey();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUser = mDatabase.getReference("User");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userID = mUser.getUid();
    }

    private void bind(){
        nameEdit = findViewById(R.id.name_edit);
        birthEdit = findViewById(R.id.birth_edit);
        heightEdit = findViewById(R.id.height_edit);
        weightEdit = findViewById(R.id.weight_edit);
        cautionEdit = findViewById(R.id.caution_edit);
        gendM = findViewById(R.id.gender_m);
        gendF = findViewById(R.id.gender_f);
        bloodA = findViewById(R.id.blood_a);
        bloodB = findViewById(R.id.blood_b);
        bloodO = findViewById(R.id.blood_o);
        bloodAB = findViewById(R.id.blood_ab);

        submitBtn = findViewById(R.id.editSubmit_Btn);

        submitBtn.setOnClickListener(onClickListener);
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.editSubmit_Btn:
                    String nameStr;
                    if(editToString(nameEdit) == ""){
                        nameStr = mUser.getDisplayName();
                    }else{
                        nameStr = editToString(nameEdit);
                    }
                    User user = new User(nameStr,null,null,null,null,null,null,null);

                    if(hasKey){
                        mReferenceUser.child(userID).setValue(user);
                    }else{
                        mReferenceUser.setValue(userID);
                        mReferenceUser.child(userID).setValue(user);
                    }

                    startActivity(new Intent(EditProfileActivity.this,LobbyActivity.class));
                    break;
            }
        }
    };

    private void userHasKey(){
        mReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userID)){
                    hasKey = true;
                }else{
                    hasKey = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String editToString(EditText et){
        String str = et.getText().toString();
        return str;
    }

}
