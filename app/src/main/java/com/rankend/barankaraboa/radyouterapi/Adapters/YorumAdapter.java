package com.rankend.barankaraboa.radyouterapi.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rankend.barankaraboa.radyouterapi.MainActivity;
import com.rankend.barankaraboa.radyouterapi.Models.YorumClass;
import com.rankend.barankaraboa.radyouterapi.R;
import com.rankend.barankaraboa.radyouterapi.System.CustomImageView;
import com.rankend.barankaraboa.radyouterapi.System.FirebaseControllers;

import java.util.ArrayList;

/**
 * Created by SE on 18.04.2017.
 */

public class YorumAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<YorumClass> yorumListesi;
    private Activity activity;
    public YorumAdapter(Activity activity, ArrayList<YorumClass> yorumListesi) {
        this.activity = activity;
        mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        this.yorumListesi = yorumListesi;
    }

    @Override
    public int getCount() {
        return yorumListesi.size();
    }

    @Override
    public Object getItem(int position) {
        return yorumListesi.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final YorumClass yorum = yorumListesi.get(position);
        final View satirView;
        if(yorum.joined){
            satirView = mInflater.inflate(R.layout.joined_layout, null);
            TextView yorumText = (TextView) satirView.findViewById(R.id.yorum);
            yorumText.setText(yorum.getYorum());
        }else{
            satirView = mInflater.inflate(R.layout.yorum_layout, null);
            final LinearLayout rootLayout = (LinearLayout) satirView.findViewById(R.id.rootLayout);
            TextView userName = (TextView) satirView.findViewById(R.id.userName);
            TextView yorumText = (TextView) satirView.findViewById(R.id.yorum);
            CustomImageView  userImage = (CustomImageView) satirView.findViewById(R.id.userImage);
            userName.setText("@"+yorum.getUserNickName());
            if ( MainActivity.isAdmin ){
                userImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!MainActivity.isAdmin)
                            return;
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
                        builder.setTitle(activity.getResources().getString(R.string.admin_ban_alert))
                                .setPositiveButton(activity.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        FirebaseControllers Controller = new FirebaseControllers(activity);
                                        Controller.kullaniciBanla(yorum.getUserId());
                                    }
                                })
                                .setNegativeButton(activity.getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                        builder.create();
                        builder.show();
                    }
                });
                final AlertDialog.Builder builder = new AlertDialog.Builder(satirView.getContext());

                builder.setTitle("@"+yorum.getUserNickName());

                builder.setItems(new CharSequence[] {activity.getResources().getString(R.string.delete), activity.getResources().getString(R.string.reply)} ,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 1){
                                    EditText messageText = (EditText) activity.findViewById(R.id.messageText);
                                    messageText.setText("@"+yorum.getUserNickName()+" ");
                                    messageText.setFocusableInTouchMode(true);
                                    messageText.requestFocus();
                                    int position = messageText.getText().toString().length();
                                    messageText.setSelection(position);
                                }else if(which == 0){
                                    FirebaseControllers controllers = new FirebaseControllers(activity);
                                    controllers.yorumSil(yorum);
                                }
                            }
                        });
                rootLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.show();
                    }
                });
            }else{
                final AlertDialog.Builder builder = new AlertDialog.Builder(satirView.getContext());

                builder.setTitle("@"+yorum.getUserNickName());

                builder.setItems(new CharSequence[] {activity.getResources().getString(R.string.report), activity.getResources().getString(R.string.reply )} ,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 1){
                                    EditText messageText = (EditText) activity.findViewById(R.id.messageText);
                                    messageText.setText("@"+yorum.getUserNickName()+" ");
                                    messageText.setFocusableInTouchMode(true);
                                    messageText.requestFocus();
                                    int position = messageText.getText().toString().length();
                                    messageText.setSelection(position);
                                }else if(which == 0){
                                    FirebaseControllers controllers = new FirebaseControllers(activity);
                                    controllers.sikayetEt(yorum);
                                }
                            }
                        });
                rootLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.show();
                    }
                });
            }
            yorumText.setText(yorum.getYorum());
            Glide
                    .with(satirView.getContext())
                    .load(yorum.getUserImage())
                    .centerCrop()
                    .placeholder(R.drawable.pp_default)
                    .crossFade()
                    .into(userImage);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    if(!yorumListesi.get(position).joined){
                        if(yorumListesi.get(position).key.equals(yorum.key)){
                            yorumListesi.remove(position);
                            notifyDataSetChanged();
                        }
                    }else{
                        yorumListesi.remove(position);
                        notifyDataSetChanged();
                    }

                }catch (IndexOutOfBoundsException ex){
                    ex.getStackTrace();
                }
            }
        }, 10000);
        return satirView;
    }
}
