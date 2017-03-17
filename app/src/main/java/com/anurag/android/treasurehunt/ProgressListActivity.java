package com.anurag.android.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ANURAG NAIK on 2/2/2017.
 */

public class ProgressListActivity extends Activity {
    ListView listView;
    Arena[] data;
    ProgressListAdapter adapter;
    SharedPreferences preferences;
    int currentPosition;
    int finalPostion;
    int startPosition;
    Boolean flag;
    int count;
    Button finalArena;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_list);
        listView = (ListView) findViewById(R.id.list);
        preferences = getSharedPreferences("shared_preference", MODE_PRIVATE);
        editor = preferences.edit();
        flag = preferences.getBoolean("isTeamAssigned", false);
        if(!flag){
            startActivity(new Intent(ProgressListActivity.this, HomeActivity.class));
        }

        currentPosition = preferences.getInt("current_position",0);
        startPosition = preferences.getInt("start_position",0);
        finalPostion = preferences.getInt("final_position",0);
        count = preferences.getInt("count",0);

        iniatialize_data();
        adapter = new ProgressListAdapter(ProgressListActivity.this, R.layout.list_row_item, data);
        listView.setAdapter(adapter);

        if(preferences.getInt("admin_approve",0)==finalPostion){
            listView.setClickable(false);
        }

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               if(position == count){
                   Intent intent = new Intent(ProgressListActivity.this, QrScannerActivity.class);
                   startActivity(intent);
               }
           }
       });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Not Allowed!", Toast.LENGTH_SHORT).show();
    }

    public void iniatialize_data(){
        data = new Arena[]{
                new Arena("Arena A", false),
                new Arena("Arena B", false),
                new Arena("Arena C", false),
                new Arena("Arena D", false),
                new Arena("Arena E", false),
                new Arena("Final Arena", false)
        };
    }
}
