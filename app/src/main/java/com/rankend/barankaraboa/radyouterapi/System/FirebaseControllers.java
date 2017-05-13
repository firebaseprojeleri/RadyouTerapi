package com.rankend.barankaraboa.radyouterapi.System;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.rankend.barankaraboa.radyouterapi.MainActivity;
import com.rankend.barankaraboa.radyouterapi.Models.BanClass;
import com.rankend.barankaraboa.radyouterapi.Models.IstekClass;
import com.rankend.barankaraboa.radyouterapi.Models.KullaniciBilgileriClass;
import com.rankend.barankaraboa.radyouterapi.Models.KullanicilarClass;
import com.rankend.barankaraboa.radyouterapi.Models.SikayetClass;
import com.rankend.barankaraboa.radyouterapi.Models.YorumClass;
import com.rankend.barankaraboa.radyouterapi.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by SE on 19.04.2017.
 */

public class FirebaseControllers {
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private Activity activity;
    private boolean show = false;
    private boolean firstJoin = false;

    public FirebaseControllers(Activity activity) {
        this.activity = activity;
    }

    //listeners
    private ChildEventListener yorumListener;
    private ChildEventListener banListener;
    private ValueEventListener istekListener;

    public void yorumDinle() {
        if(yorumListener!=null)
            return;
        DatabaseReference yorumRef = mDatabase.child("yorumlar");
        yorumListener = yorumRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                YorumClass data = dataSnapshot.getValue(YorumClass.class);
                data.key = dataSnapshot.getKey();
                if (data.getAddedTime() > MainActivity.LoginTime && MainActivity.banListesi.indexOf(data.getUserId()) == -1 && MainActivity.gizlenenKullanicilar.indexOf(data.getUserId()) == -1 ) {
                    MainActivity.yorumListesi.add(data);
                    ((BaseAdapter) ((MainActivity) activity).yorumlarListesi.getAdapter()).notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for ( int i = 0; i < MainActivity.yorumListesi.size(); i++)
                    if (Objects.equals(MainActivity.yorumListesi.get(i).key, dataSnapshot.getKey()))
                        MainActivity.yorumListesi.remove(i);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void banDinle(){
        if(banListener!=null)
            return;
        DatabaseReference banRef = mDatabase.child("banlistesi");
        banListener = banRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MainActivity.banListesi.add(dataSnapshot.getKey());
                BanClass banClass = dataSnapshot.getValue(BanClass.class);
                if ( ((MainActivity) activity).currentUser != null && ((MainActivity) activity).currentUser.getUid().equals(dataSnapshot.getKey()) ) {
                    MainActivity.bannedUser = banClass.banStatus;
                    if(banClass.banStatus){
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        String alertMessage = activity.getResources().getString(R.string.banned_message);
                        if(banClass.banReason != null && !banClass.banReason.isEmpty())
                            alertMessage += "\n" +activity.getResources().getString(R.string.ban_reason)+": "+banClass.banReason;
                        builder.setMessage(alertMessage)
                                .setPositiveButton(activity.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .create().show();
                        final TextView message = (TextView) activity.findViewById(R.id.textView);
                        message.setText(activity.getResources().getString(R.string.banned_message));
                    }
                    ((MainActivity) activity).initUI();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                MainActivity.banListesi.set(MainActivity.banListesi.indexOf(dataSnapshot.getKey()), dataSnapshot.getKey());
                BanClass banClass = dataSnapshot.getValue(BanClass.class);
                if ( ((MainActivity) activity).currentUser != null && ((MainActivity) activity).currentUser.getUid().equals(dataSnapshot.getKey()) ) {
                    MainActivity.bannedUser = banClass.banStatus;
                    if(banClass.banStatus){
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        String alertMessage = activity.getResources().getString(R.string.banned_message);
                        if(banClass.banReason != null && !banClass.banReason.isEmpty())
                            alertMessage += "\n" +activity.getResources().getString(R.string.ban_reason)+": "+banClass.banReason;
                        builder.setMessage(alertMessage)
                                .setPositiveButton(activity.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .create().show();
                        final TextView message = (TextView) activity.findViewById(R.id.textView);
                        message.setText(activity.getResources().getString(R.string.banned_message));
                    }
                    ((MainActivity) activity).initUI();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                MainActivity.banListesi.remove(MainActivity.banListesi.indexOf(dataSnapshot.getKey()));
                MainActivity.bannedUser = false;
                ((MainActivity) activity).initUI();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void SetUserNickname(String nickname){
        MainActivity.setUserNickname(nickname);
        if (!firstJoin) {
            YorumClass joinedMessage = new YorumClass();
            joinedMessage.setYorum(activity.getResources().getString(R.string.user_joined_chat, nickname));
            joinedMessage.joined = true;
            mDatabase.child("yorumlar").push().setValue(joinedMessage);
            firstJoin = true;
        }
        ((MainActivity) activity).initUI();
    }

    private void getUserNickname(String userId){
       mDatabase.child("kullanıcılar/"+userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("FirebaseControllers", dataSnapshot.toString());
                KullanicilarClass kullanici = dataSnapshot.getValue(KullanicilarClass.class);
                SetUserNickname(kullanici.nickname);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void istekleriGetir(){
        if(istekListener!=null)
            return;
        istekListener = mDatabase.child("istekler").orderByChild("oySayisi").addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot istekSnapshot) {
                MainActivity.istekler.clear();
                for (DataSnapshot dataSnapshot: istekSnapshot.getChildren()) {
                    IstekClass istek = dataSnapshot.getValue(IstekClass.class);
                    if (istek.ratedTime > MainActivity.LoginReqTime) {
                        istek.key = dataSnapshot.getKey();
                        MainActivity.istekler.add(0,istek);
                        ((BaseAdapter) ((MainActivity) activity).isteklerListesi.getAdapter()).notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void istekYap(final FirebaseUser user, String istekSarki, String istekNot){
        if ( user == null )
            return;
        IstekClass yeniIstek = new IstekClass(user.getUid(), MainActivity.userNickname, user.getPhotoUrl().toString(), istekSarki, istekNot, 1, System.currentTimeMillis(), System.currentTimeMillis());
        yeniIstek.oylar = new HashMap<>();
        yeniIstek.oylar.put(user.getUid(),true);
        mDatabase.child("istekler").push().setValue(yeniIstek).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if ( task.isSuccessful() )
                new AlertDialog.Builder(activity)
                    .setPositiveButton(activity.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setMessage(activity.getResources().getString(R.string.request_send_okey))
                    .create()
                    .show();
                else
                    new AlertDialog.Builder(activity).setPositiveButton(activity.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setMessage(activity.getResources().getString(R.string.request_send_fail)).create().show();

            }
        });

    }

    public void kullaniciBanla(final String userId){
        BanClass banClass = new BanClass();
        banClass.banStatus = true;
        mDatabase.child("banlistesi").child(userId).setValue(banClass);

        LayoutInflater factory = LayoutInflater.from(activity);
        final View banSebepForm = factory.inflate(R.layout.ban_sebep_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.ban_reason))
                .setPositiveButton(activity.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText banSebepText = (EditText) banSebepForm.findViewById(R.id.banSebepText);
                        TimePicker unBanTimePicker = (TimePicker) banSebepForm.findViewById(R.id.unBanTimePicker);
                        CheckBox infinityCheck = (CheckBox) banSebepForm.findViewById(R.id.infinityCheck);
                        BanClass banClass = new BanClass();
                        banClass.banReason = banSebepText.getText().toString();
                        banClass.banStatus = true;
                        banClass.banTime = System.currentTimeMillis();
                        Calendar now = Calendar.getInstance();
                        now.set(Calendar.HOUR, unBanTimePicker.getCurrentHour());
                        now.set(Calendar.MINUTE, unBanTimePicker.getCurrentMinute());
                        banClass.unBanTime = now.getTimeInMillis();
                        if(infinityCheck.isChecked())
                            banClass.unBanTime = 0;
                        mDatabase.child("banlistesi").child(userId).setValue(banClass);
                    }
                })
                .setNegativeButton(activity.getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.setView(banSebepForm);
        builder.create();
        builder.show();
    }

    public void yorumSil(YorumClass yorum){
        mDatabase.child("yorumlar/" + yorum.key).removeValue();
    }

    public void istekOyla(final FirebaseUser user, String istekId){
        DatabaseReference istekRef = mDatabase.child("istekler/"+istekId);
        istekRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                IstekClass p = mutableData.getValue(IstekClass.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.oylar.containsKey(user.getUid())) {
                    p.oySayisi = p.oySayisi - 1;
                    p.oylar.remove(user.getUid());
                } else {
                    p.oySayisi = p.oySayisi + 1;
                    p.oylar.put(user.getUid(), true);
                    p.ratedTime = System.currentTimeMillis();
                }
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                if ( b )
                new AlertDialog.Builder(activity).setPositiveButton(activity.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setMessage(activity.getResources().getString(R.string.request_vote_okey)).create().show();
                else
                    new AlertDialog.Builder(activity).setPositiveButton(activity.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setMessage(activity.getResources().getString(R.string.request_vote_fail)).create().show();

            }
        });
    }

    public void KullaniciKontrolEt(final FirebaseUser user){
        final DatabaseReference kullanicilar = mDatabase.child("kullanıcılar");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user.getUid())) {
                    if ( dataSnapshot.child(user.getUid()).hasChild("admin") ) {
                        KullanicilarClass kullanicilar = dataSnapshot.child(user.getUid()).getValue(KullanicilarClass.class);
                        MainActivity.isAdmin = kullanicilar.admin;
                    }else
                        MainActivity.isAdmin = false;
                    if ( !dataSnapshot.child(user.getUid()).hasChild("nickname") && !show){
                            final EditText input = new EditText(activity);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            Log.d("FirebaseControllers", "Kullanıcı tekrar Göster");
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage(activity.getResources().getString(R.string.user_join_welcome_message))
                                    .setPositiveButton(activity.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            KullanicilarClass kullanici = dataSnapshot.child(user.getUid()).getValue(KullanicilarClass.class);
                                            kullanici.nickname = input.getText().toString();
                                            kullanicilar.child(user.getUid()).setValue(kullanici);
                                            ((TextView) activity.findViewById(R.id.userName)).setText("@" + input.getText());
                                            MainActivity.userNickname = input.getText().toString();

                                        }
                                    })
                                    .setNegativeButton(activity.getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            FirebaseAuth.getInstance().signOut();
                                        }
                                    });
                            builder.setView(input);
                            builder.create();
                            builder.show();
                            show = true;
                    }else
                        getUserNickname(user.getUid());
                }else
                    KullaniciOlustur(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        kullanicilar.addListenerForSingleValueEvent(listener);
        kullanicilar.removeEventListener(listener);
    }

    private void KullaniciOlustur(final FirebaseUser user){
        Log.d("FirebaseControllers", "Olustur");
        final EditText input = new EditText(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        final KullanicilarClass kullanicilar = new KullanicilarClass();
        kullanicilar.profile_picture = user.getPhotoUrl().toString();
        final KullaniciBilgileriClass kullaniciBilgileri = new KullaniciBilgileriClass();
        kullaniciBilgileri.email = user.getEmail();
        kullaniciBilgileri.profile_picture = user.getPhotoUrl().toString();
        kullaniciBilgileri.username = user.getDisplayName();
        Log.d("FirebaseControllers", "Kullanıcı Oluştur Göster");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        show = true;
        builder.setMessage(activity.getResources().getString(R.string.user_first_join_welcome_message))
                .setPositiveButton(activity.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        kullaniciBilgileri.nickname = input.getText().toString();
                        mDatabase.child("kullanıcılar/"+user.getUid()+"/nickname").setValue(input.getText().toString());
                        mDatabase.child("kullanıcılar/"+user.getUid()+"/profile_picture").setValue(user.getPhotoUrl().toString());
                        mDatabase.child("kullanıcı_bilgileri").child(user.getUid()).setValue(kullaniciBilgileri);
                        MainActivity.userNickname = input.getText().toString();
                        getUserNickname(user.getUid());
                    }
                })
                .setNegativeButton(activity.getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseAuth.getInstance().signOut();
                    }
                });
        builder.setView(input);
        builder.create();
        builder.show();
    }

    public void sikayetEt(final YorumClass yorum){
        LayoutInflater factory = LayoutInflater.from(activity);

        final AlertDialog.Builder hideBuilder = new AlertDialog.Builder(activity).setMessage(activity.getResources().getString(R.string.hide_user_message))
                .setPositiveButton(activity.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.gizlenenKullanicilar.add(yorum.getUserId());
                    }
                }).setNegativeButton(activity.getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setCancelable(true);

        final View sikayetSebepForm = factory.inflate(R.layout.sikayet_sebep_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.ban_reason))
                .setPositiveButton(activity.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final SikayetClass sikayet = new SikayetClass();
                        sikayet.sikayetEden = ((MainActivity) activity).currentUser.getUid();
                        sikayet.sikayetZaman = System.currentTimeMillis();
                        sikayet.sikayetEdilenMesaj = yorum.getYorum();
                        sikayet.sikayetEdilenKullanici = yorum.getUserId();
                        RadioGroup sikayetSebepGroup = (RadioGroup) sikayetSebepForm.findViewById(R.id.sikayetSebepGroup);
                        int selected = sikayetSebepGroup.getCheckedRadioButtonId();
                        switch (selected){
                            case R.id.spamRadioButton:
                                sikayet.sikayetSebep = "Spam";
                                break;
                            case R.id.uygunsuzRadioButton:
                                sikayet.sikayetSebep = "Uygunsuz içerik";
                        }

                        mDatabase.child("yorum_sikayet").push().setValue(sikayet).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        hideBuilder.show();
                    }
                })
                .setNegativeButton(activity.getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.setView(sikayetSebepForm);
        builder.create();
        builder.show();
    }
    public void MesajGonder(FirebaseUser user, String yorumText){
        if ( MainActivity.banListesi.indexOf(user.getUid()) != -1 )
            return;
        yorumText = yorumText.replaceAll("<", "&lt;").replace(">", "&gt;").replace("'", "&#39;").replace("\"", "&#34;");
        YorumClass yorum = new YorumClass();
        yorum.setAddedTime(System.currentTimeMillis());
        yorum.setUserId(user.getUid());
        yorum.setUserImage(user.getPhotoUrl().toString());
        yorum.setUserNickName(MainActivity.userNickname);
        yorum.setYorum(yorumText);
        mDatabase.child("yorumlar").push().setValue(yorum).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ((EditText) activity.findViewById(R.id.messageText)).setText("");
            }
        });
    }

    public void RemoveListeners(){
        mDatabase.child("yorumlar").removeEventListener(yorumListener);
        mDatabase.child("banlistesi").removeEventListener(banListener);
        mDatabase.child("istekler").orderByChild("oySayisi").removeEventListener(istekListener);
    }
}
