package com.anurag.android.treasurehunt;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ANURAG NAIK on 2/2/2017.
 */

public class ProgressListAdapter extends ArrayAdapter<Arena> {
    private Arena[] items;
    private Context context;
    private int layoutResourceId;
    SharedPreferences sharedPreferences;
    int currentPosition;
    int finalPostion;
    int startPosition;
    int count;
    int val;

    public ProgressListAdapter(Context context, int resource, Arena[] objects) {
        super(context, resource, objects);
        this.items = objects;
        this.context = context;
        this.layoutResourceId = resource;
        sharedPreferences = context.getSharedPreferences("shared_preference", Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public Arena getItem(int position) {
        return items[position];
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public int getPosition(Arena item) {
        return super.getPosition(item);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder = new Holder();
        if(row == null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder.arenaNameView = (TextView) row.findViewById(R.id.arena_number);
            holder.tickImageView = (ImageView) row.findViewById(R.id.tick);
            row.setTag(holder);
        } else{
           holder = (Holder) row.getTag();
        }
        Arena arena = items[position];
        holder.arenaNameView.setText(arena.getName());
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.blink);

        startPosition = sharedPreferences.getInt("start_position", 0);
        currentPosition = sharedPreferences.getInt("current_position", 0);
        count = sharedPreferences.getInt("count",0);

        if(position == count ){
            row.setBackgroundResource(R.drawable.active_arena);
            row.startAnimation(myFadeInAnimation);
            holder.tickImageView.setImageResource(R.drawable.key);
        }
        else if(position > count){//red

            row.setBackgroundResource(R.color.reddish);
            holder.tickImageView.setImageResource(R.drawable.closed_treasure);
            row.clearAnimation();
        }
        else{//green with tick
            row.clearAnimation();
            holder.tickImageView.setImageResource(R.drawable.open_treasure);
            row.setBackgroundResource(R.color.green);
        }


        return row;
    }

 static class Holder{
        TextView arenaNameView;
        ImageView tickImageView;
    }

}
