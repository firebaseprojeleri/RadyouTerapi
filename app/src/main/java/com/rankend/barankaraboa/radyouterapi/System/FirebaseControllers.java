package com.rankend.barankaraboa.radyouterapi.System;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.rankend.barankaraboa.radyouterapi.Models.IstekClass;
import com.rankend.barankaraboa.radyouterapi.Models.KullaniciBilgileriClass;
import com.rankend.barankaraboa.radyouterapi.Models.KullanicilarClass;
import com.rankend.barankaraboa.radyouterapi.Models.YorumClass;
import com.rankend.barankaraboa.radyouterapi.R;

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
    private ChildEventListener istekListener;

    public void yorumDinle() {
        DatabaseReference yorumRef = mDatabase.child("yorumlar");
        yorumListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                YorumClass data = dataSnapshot.getValue(YorumClass.class);
                data.key = dataSnapshot.getKey();
                if (data.getAddedTime() > MainActivity.LoginTime) {
                    MainActivity.yorumListesi.add(data);
                    ((BaseAdapter) MainActivity.yorumlarListesi.getAdapter()).notifyDataSetChanged();
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
        };
        yorumRef.addChildEventListener(yorumListener);
    }

    public void banDinle(){
        DatabaseReference banRef = mDatabase.child("banlistesi");
        banListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MainActivity.banListesi.add(dataSnapshot.getKey());
                if ( ((MainActivity) activity).currentUser != null && ((MainActivity) activity).currentUser.getUid().equals(dataSnapshot.getKey()) ) {
                    MainActivity.bannedUser = true;
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("Yorum yapmanız yasaklandı.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .create().show();
                    final TextView message = (TextView) activity.findViewById(R.id.textView);
                    ((MainActivity) activity).initUI();
                    message.setText("Yorum yazmanız yasaklandı.");
                }else{
                    MainActivity.bannedUser = false;
                    ((MainActivity) activity).initUI();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                MainActivity.banListesi.set(MainActivity.banListesi.indexOf(dataSnapshot.getKey()), dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                MainActivity.banListesi.remove(MainActivity.banListesi.indexOf(dataSnapshot.getKey()));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        banRef.addChildEventListener(banListener);
    }
    private void SetUserNickname(String nickname){
        MainActivity.setUserNickname(nickname);
        if (!firstJoin) {
            YorumClass joinedMessage = new YorumClass();
            joinedMessage.setYorum(nickname + " yayına katıldı.");
            joinedMessage.joined = true;
            MainActivity.yorumListesi.add(joinedMessage);
            ((BaseAdapter) MainActivity.yorumlarListesi.getAdapter()).notifyDataSetChanged();
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
        istekListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                IstekClass istek = dataSnapshot.getValue(IstekClass.class);
                if (istek.ratedTime > MainActivity.LoginTime) {
                    istek.key = dataSnapshot.getKey();
                    MainActivity.istekler.add(0,istek);
                    ((BaseAdapter) MainActivity.isteklerListesi.getAdapter()).notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for ( int i = 0; i < MainActivity.istekler.size(); i++)
                    if (Objects.equals(MainActivity.istekler.get(i).key, dataSnapshot.getKey()))
                        MainActivity.istekler.remove(i);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDatabase.child("istekler").orderByChild("oySayisi").addChildEventListener(istekListener);
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
                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setMessage("İsteğiniz gönderildi!")
                    .create()
                    .show();
                else
                    new AlertDialog.Builder(activity).setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setMessage("Bir hata oluştu! Lütfen tekrar deneyin.").create().show();

            }
        });

    }

    public void kullaniciBanla(String userId){
        mDatabase.child("banlistesi").child(userId).setValue(true);
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
                new AlertDialog.Builder(activity).setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setMessage("İsteği başarıyla oyladınız!").create().show();
                else
                    new AlertDialog.Builder(activity).setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setMessage("Bir hata oluştu! Lütfen tekrar deneyin.").create().show();

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
                        if (kullanicilar.admin)
                            MainActivity.isAdmin = true;
                    }
                    if ( !dataSnapshot.child(user.getUid()).hasChild("nickname") && !show){
                            final EditText input = new EditText(activity);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            Log.d("FirebaseControllers", "Kullanıcı tekrar Göster");
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage("Hoşgeldin! Aşağıya rumuzunu yazabilirsin.")
                                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            KullanicilarClass kullanici = dataSnapshot.child(user.getUid()).getValue(KullanicilarClass.class);
                                            kullanici.nickname = input.getText().toString();
                                            kullanicilar.child(user.getUid()).setValue(kullanici);
                                            ((TextView) activity.findViewById(R.id.userName)).setText("@" + input.getText());
                                            MainActivity.userNickname = input.getText().toString();

                                        }
                                    })
                                    .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
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
        builder.setMessage("Aramıza Hoşgeldin! Aşağıya rumuzunu yazabilirsin.")
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        kullaniciBilgileri.nickname = input.getText().toString();
                        mDatabase.child("kullanıcılar/"+user.getUid()+"/nickname").setValue(input.getText().toString());
                        mDatabase.child("kullanıcılar/"+user.getUid()+"/profile_picture").setValue(user.getPhotoUrl().toString());
                        mDatabase.child("kullanıcı_bilgileri").child(user.getUid()).setValue(kullaniciBilgileri);
                        MainActivity.userNickname = input.getText().toString();
                        getUserNickname(user.getUid());
                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseAuth.getInstance().signOut();
                    }
                });
        builder.setView(input);
        builder.create();
        builder.show();
        show = true;
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
        mDatabase.removeEventListener(yorumListener);
        mDatabase.removeEventListener(banListener);
        mDatabase.removeEventListener(istekListener);
    }
}
