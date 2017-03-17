package com.anurag.android.treasurehunt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ANURAG NAIK on 2/2/2017.
 */

public class HomeActivity extends Activity {

    EditText teamNumberText;
    Button submitButton;
    ImageView goImage;
    TextView firstDestText;
    SharedPreferences preferences;
    int currentPosition;
    int startPosition;
    int finalPosition;


@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);

    preferences = getSharedPreferences("shared_preference", MODE_PRIVATE);
    final SharedPreferences.Editor editor = preferences.edit();

    teamNumberText = (EditText) findViewById(R.id.team_id);
    submitButton = (Button) findViewById(R.id.button_submit);
    goImage = (ImageView) findViewById(R.id.go_image);

    submitButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            if(!teamNumberText.getText().toString().equals("") && Integer.valueOf(teamNumberText.getText().toString())<=4 && Integer.valueOf(teamNumberText.getText().toString())>=0){
                int startPosition = Integer.valueOf(teamNumberText.getText().toString());
                currentPosition = startPosition;
                editor.putInt("start_position", startPosition);
                editor.putInt("current_position", startPosition);
                editor.putInt("final_position", (startPosition + 6 - 1)%6);
                editor.putInt("admin_approve",-1);
                editor.putBoolean("isTeamAssigned", true);
                editor.apply();
                finalPosition = (startPosition - 1 + 6)%6;
                goImage.setVisibility(View.VISIBLE);
                teamNumberText.setVisibility(View.GONE);
                submitButton.setVisibility(View.GONE);
            }else{
                Toast.makeText(HomeActivity.this, "Please enter team number [0-4]", Toast.LENGTH_SHORT).show();
            }
        }
    });

        goImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, ProgressListActivity.class);
                startActivity(intent);
            }
        });
    }




}
