package com.example.android.theroom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class OnboardingActivity extends AppCompatActivity {

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

    public static final String SKIP_ONBOARDING = "skipOnboarding";

    private Map<String, CheckBox> mCheckBoxList;
    private Button mContinueButton;
    private EditText mEditText;
    private int mNumInterestsSelected;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    /**
     * Static method that returns intent used to start Onboarding Activity
     * @param context
     * @return
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, OnboardingActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // make sure the keyboard doesn't pop up automatically
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // get references to database and auth object
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // hide continue button until an interest is selected
        mContinueButton = (Button) findViewById(R.id.continue_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String displayName = mEditText.getText().toString();
                if (!displayName.trim().isEmpty()) {
                    // update user's display name
                    mDatabase.child("users/" + mAuth.getUid() + "/displayName/").setValue(displayName);

                    // use SharedPreferences to indicate onboarding should be skipped next time
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(SKIP_ONBOARDING, true);
                    editor.commit();

                    // start MainActivity and finish this one
                    startActivity(MainActivity.newIntent(getApplicationContext()));
                    finish();
                }
            }
        });
        mContinueButton.setVisibility(View.INVISIBLE);
        mNumInterestsSelected = 0;

        mEditText = (EditText) findViewById(R.id.display_name_input);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateContinueButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // get references to all CheckBoxes
        mCheckBoxList = new HashMap<String, CheckBox>();
        mCheckBoxList.put(interests[0], (CheckBox)findViewById(R.id.onboarding_interest1_checkbox));
        mCheckBoxList.put(interests[1], (CheckBox)findViewById(R.id.onboarding_interest2_checkbox));
        mCheckBoxList.put(interests[2], (CheckBox)findViewById(R.id.onboarding_interest3_checkbox));
        mCheckBoxList.put(interests[3], (CheckBox)findViewById(R.id.onboarding_interest4_checkbox));
        mCheckBoxList.put(interests[4], (CheckBox)findViewById(R.id.onboarding_interest5_checkbox));
        mCheckBoxList.put(interests[5], (CheckBox)findViewById(R.id.onboarding_interest6_checkbox));
        mCheckBoxList.put(interests[6], (CheckBox)findViewById(R.id.onboarding_interest7_checkbox));
        mCheckBoxList.put(interests[7], (CheckBox)findViewById(R.id.onboarding_interest8_checkbox));
        mCheckBoxList.put(interests[8], (CheckBox)findViewById(R.id.onboarding_interest9_checkbox));
        mCheckBoxList.put(interests[9], (CheckBox)findViewById(R.id.onboarding_interest10_checkbox));

        // check user's interests in firebase
        mDatabase.child("users/" + mAuth.getUid() + "/interests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // set checkboxes for all user's interests to checked
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        mCheckBoxList.get(d.getKey()).setChecked(true);
                        mNumInterestsSelected++;
                        updateContinueButton();
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
                    mNumInterestsSelected++;
                    updateContinueButton();

                } else {
                    mDatabase.child("users/" + mAuth.getUid() + "/interests/" + interestName).setValue(null);
                    mNumInterestsSelected--;
                    updateContinueButton();
                }
            }
        });

    }

    /**
     * Set button to visible or invisible depending on number of interests selected
     */
    private void updateContinueButton() {
        if (mNumInterestsSelected >= 1 && !mEditText.getText().toString().trim().equals("")) {
            mContinueButton.setVisibility(View.VISIBLE);
        } else {
            mContinueButton.setVisibility(View.GONE);
        }
    }
}
