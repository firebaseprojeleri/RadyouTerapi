package com.rankend.barankaraboa.radyouterapi.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.rankend.barankaraboa.radyouterapi.MainActivity;
import com.rankend.barankaraboa.radyouterapi.Models.IstekClass;
import com.rankend.barankaraboa.radyouterapi.R;
import com.rankend.barankaraboa.radyouterapi.System.CustomImageView;
import com.rankend.barankaraboa.radyouterapi.System.FirebaseControllers;

import java.util.ArrayList;

/**
 * Created by SE on 18.04.2017.
 */

public class IstekAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<IstekClass> istekListesi;
    private Activity activity;
    public IstekAdapter(Activity activity, ArrayList<IstekClass> istekListesi) {
        this.activity = activity;
        mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        this.istekListesi = istekListesi;
    }

    @Override
    public int getCount() {
        return istekListesi.size();
    }

    @Override
    public Object getItem(int position) {
        return istekListesi.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final IstekClass istek = istekListesi.get(position);
        final View satirView = mInflater.inflate(R.layout.yorum_layout, null);
        final LinearLayout rootLayout = (LinearLayout) satirView.findViewById(R.id.rootLayout);
        TextView userName = (TextView) satirView.findViewById(R.id.userName);
        TextView yorumText = (TextView) satirView.findViewById(R.id.yorum);
        CustomImageView userImage = (CustomImageView) satirView.findViewById(R.id.userImage);
        userName.setText("@"+istek.userNickName);

        yorumText.setText(activity.getResources().getString(R.string.request_message, istek.istekSarki));
        Glide
                .with(satirView.getContext())
                .load(istek.userImage)
                .centerCrop()
                .placeholder(R.drawable.pp_default)
                .crossFade()
                .into(userImage);

        final AlertDialog.Builder builder = new AlertDialog.Builder(satirView.getContext());
        builder.setTitle("@"+istek.userNickName);

        String resName = activity.getResources().getString(R.string.request_give_vote);
        if ( istek.oylar.containsKey(((MainActivity) activity).currentUser.getUid()) )
        {
            resName = activity.getResources().getString(R.string.request_withdraw_vote);
        }

        builder.setItems(new CharSequence[] {resName} ,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            FirebaseControllers Controller = new FirebaseControllers(activity);
                            Controller.istekOyla(FirebaseAuth.getInstance().getCurrentUser(), istek.key);
                        }
                    }
                });
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });
        return satirView;
    }
}
