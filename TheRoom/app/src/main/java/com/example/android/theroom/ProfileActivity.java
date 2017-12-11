package com.example.android.theroom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String[] interests = {
            "interest1",
            "interest2",
            "interest3",
            "interest4",
            "interest5",
            "interest6",
            "interest7",
            "interest8",
            "interest9",
            "interest10"
    };

    private Map<String, CheckBox> mCheckBoxList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    /**
     * Static method that returns intent used to start ProfileActivity
     * @param context
     * @return
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, ProfileActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // get references to database and auth object
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // get references to all CheckBoxes
        mCheckBoxList = new HashMap<String, CheckBox>();
        mCheckBoxList.put(interests[0], (CheckBox)findViewById(R.id.interest1_checkbox));
        mCheckBoxList.put(interests[1], (CheckBox)findViewById(R.id.interest2_checkbox));
        mCheckBoxList.put(interests[2], (CheckBox)findViewById(R.id.interest3_checkbox));
        mCheckBoxList.put(interests[3], (CheckBox)findViewById(R.id.interest4_checkbox));
        mCheckBoxList.put(interests[4], (CheckBox)findViewById(R.id.interest5_checkbox));
        mCheckBoxList.put(interests[5], (CheckBox)findViewById(R.id.interest6_checkbox));
        mCheckBoxList.put(interests[6], (CheckBox)findViewById(R.id.interest7_checkbox));
        mCheckBoxList.put(interests[7], (CheckBox)findViewById(R.id.interest8_checkbox));
        mCheckBoxList.put(interests[8], (CheckBox)findViewById(R.id.interest9_checkbox));
        mCheckBoxList.put(interests[9], (CheckBox)findViewById(R.id.interest10_checkbox));

        // check user's interests in firebase
        mDatabase.child("users/" + mAuth.getUid() + "/interests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // set checkboxes for all user's interests to checked
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        mCheckBoxList.get(d.getKey()).setChecked(true);
                    }
                }

                // add click listeners to each checkbox
                for (int i = 0; i < mCheckBoxList.size(); i++) {
                    addCheckBoxClickListener(mCheckBoxList.get(interests[i]), interests[i]);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Add OnCheckChangedListener to CheckBox c that updates the user's interests in Firebase
     * @param c
     * @param interestName
     */
    private void addCheckBoxClickListener(CheckBox c, final String interestName) {
        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mDatabase.child("users/" + mAuth.getUid() + "/interests/" + interestName).setValue(true);
                } else {
                    mDatabase.child("users/" + mAuth.getUid() + "/interests/" + interestName).setValue(null);
                }
            }
        });

    }
}
