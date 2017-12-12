package com.example.android.theroom.models;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.example.android.theroom.R;

/**
 * Created by Tarek on 12/12/2017.
 */

public class Popup extends Activity{

    public boolean wantLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.7), (int)(height*.3));

        Button yesBtn = findViewById(R.id.yesBtn);
        Button noBtn = findViewById(R.id.noBtn);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wantLocation = true;
                Intent intent = new Intent();
                intent.putExtra("wantLocation", wantLocation); //value should be your string from the edittext
                setResult(1, intent); //The data you want to send back
                finish();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wantLocation = false;
                Intent intent = new Intent();
                intent.putExtra("wantLocation", wantLocation); //value should be your string from the edittext
                setResult(1, intent); //The data you want to send back
                finish();
            }
        });


    }
}
