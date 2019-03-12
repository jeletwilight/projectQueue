package com.example.jelelight.servicequeuing;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabasae;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private TextView nameView,genderView,bDayView,bMonthView,bYearView,heightView,weightView,bloodView,cautionView,uidView,phoneView;
    private Button editBtn,backBtn;
    private ImageButton copyBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        bind();
        pullFromFirebase();
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.editProfile_Btn:
                    startActivity(new Intent(ProfileActivity.this,EditProfileActivity.class));
                    break;
                case R.id.backProfile_Btn:
                    onBackPressed();
                    break;
                case R.id.copy_profile:
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("UserID", uidView.getText());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(ProfileActivity.this, "Copied to Clipboard.",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void bind(){
        nameView = findViewById(R.id.name_profile);
        genderView = findViewById(R.id.gender_profile);
        bloodView = findViewById(R.id.blood_profile);
        bDayView = findViewById(R.id.day_profile);
        bMonthView = findViewById(R.id.month_profile);
        bYearView = findViewById(R.id.year_profile);
        weightView = findViewById(R.id.weight_profile);
        heightView = findViewById(R.id.height_profile);
        cautionView = findViewById(R.id.caution_profile);
        uidView = findViewById(R.id.uid_profile);
        phoneView = findViewById(R.id.phone_profile);

        copyBtn = findViewById(R.id.copy_profile);
        editBtn = findViewById(R.id.editProfile_Btn);
        backBtn = findViewById(R.id.backProfile_Btn);

        copyBtn.setOnClickListener(onClickListener);
        editBtn.setOnClickListener(onClickListener);
        backBtn.setOnClickListener(onClickListener);

    }

    private void pullFromFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        uidView.setText(mUser.getUid());
    }
}
