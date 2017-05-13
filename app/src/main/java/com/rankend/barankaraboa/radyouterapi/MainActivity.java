package com.rankend.barankaraboa.radyouterapi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.rankend.barankaraboa.radyouterapi.Adapters.IstekAdapter;
import com.rankend.barankaraboa.radyouterapi.Adapters.YorumAdapter;
import com.rankend.barankaraboa.radyouterapi.Models.IstekClass;
import com.rankend.barankaraboa.radyouterapi.Models.YorumClass;
import com.rankend.barankaraboa.radyouterapi.System.CustomImageView;
import com.rankend.barankaraboa.radyouterapi.System.FirebaseControllers;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";

    public static ArrayList<String> banListesi = new ArrayList<>();
    public static ArrayList<YorumClass> yorumListesi = new ArrayList<>();
    public static ArrayList<IstekClass> istekler = new ArrayList<>();
    public static boolean isAdmin = false;

    public static long LoginTime = System.currentTimeMillis();
    public static long LoginReqTime = System.currentTimeMillis();
    public static boolean bannedUser = false;
    public ListView yorumlarListesi;
    public ListView isteklerListesi;
    public AVLoadingIndicatorView avi;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    public FirebaseUser currentUser;
    private FirebaseControllers Controller = new FirebaseControllers(this);
    public static String userNickname;
    public static ArrayList gizlenenKullanicilar = new ArrayList<>();

    public VideoView videoView;

    private static final int RC_SIGN_IN = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        avi= (AVLoadingIndicatorView) findViewById(R.id.avi);

        videoView = (VideoView) findViewById(R.id.video_view);
        initVideo();
        LinearLayout unAuthLayout = (LinearLayout) findViewById(R.id.unAuthLayout) ;
        unAuthLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bannedUser)
                    signIn();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                    currentUser = user;
                initUI();
            }
        };
        yorumlarListesi = (ListView) findViewById(R.id.yorumlarListesi);
        YorumAdapter yorumAdapter = new YorumAdapter(this, yorumListesi);
        yorumlarListesi.setAdapter(yorumAdapter);

        isteklerListesi = (ListView) findViewById(R.id.isteklerListesi);
        IstekAdapter istekAdapter = new IstekAdapter(this, istekler);
        isteklerListesi.setAdapter(istekAdapter);
    }
    public void initVideo(){
        final String url = "rtsp://95.173.179.23/live/Mystream";
        videoView.setVideoURI(Uri.parse(url));
        videoView.start();
        avi.show();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.setVideoURI(Uri.parse(url));
                videoView.start();
                avi.show();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d("video", "setOnErrorListener ");
                videoView.setVideoURI(Uri.parse(url));
                videoView.start();
                avi.show();
                return true;
            }
        });
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if(what==MediaPlayer.MEDIA_INFO_BUFFERING_START){
                    avi.show();
                    return true;
                }else if(what==MediaPlayer.MEDIA_INFO_UNKNOWN){
                    avi.show();
                    return true;
                }else if(what==MediaPlayer.MEDIA_ERROR_UNSUPPORTED){
                    avi.show();
                    return true;
                }else if(what==MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                    avi.hide();
                    return true;
                }
                return false;
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        Log.d(TAG, "OnStart");
        LoginTime = System.currentTimeMillis();
        Controller.yorumDinle();
        Controller.banDinle();
        Controller.istekleriGetir();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        Controller.RemoveListeners();
        Log.d(TAG, "OnStop");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        CustomImageView  avatar = (CustomImageView) findViewById(R.id.pPhoto);
        Glide.clear(avatar);
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        final LinearLayout istekLayout = (LinearLayout) findViewById(R.id.istekLayout);
        if(istekLayout.getVisibility() == View.VISIBLE){
            final LinearLayout yorumLayout = (LinearLayout) findViewById(R.id.yorumLayout);
            istekLayout.setVisibility(View.GONE);
            yorumLayout.setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, getResources().getString(R.string.play_connection_error), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        initUI();
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.unable_login), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void setUserNickname(String nickname){
        userNickname = nickname;
    }

    public void initUI(){
        initKontrol();
        touchUI();
        final LinearLayout authLayout = (LinearLayout) findViewById(R.id.authLayout);
        final LinearLayout kontrolLayout = (LinearLayout) findViewById(R.id.kontrolLayout);
        final CustomImageView avatar = (CustomImageView) findViewById(R.id.pPhoto);
        final LinearLayout unAuthLayout = (LinearLayout) findViewById(R.id.unAuthLayout);
        final TextView userName = (TextView) findViewById(R.id.userName);
        if ( currentUser != null && !bannedUser) {

            Controller.KullaniciKontrolEt(currentUser);
            try{
                Glide
                        .with(this)
                        .load(currentUser.getPhotoUrl())
                        .centerCrop()
                        .placeholder(R.drawable.pp_default)
                        .crossFade()
                        .into(avatar);
            }catch (IllegalArgumentException | NullPointerException ex){
                Log.e(TAG, ex.getMessage());
            }
            userName.setText("@"+userNickname);
            unAuthLayout.setVisibility(View.GONE);
            authLayout.setVisibility(View.VISIBLE);
            kontrolLayout.setVisibility(View.VISIBLE);
        }else{
            authLayout.setVisibility(View.GONE);
            unAuthLayout.setVisibility(View.VISIBLE);
            kontrolLayout.setVisibility(View.GONE);
        }
        final EditText messageText = (EditText) findViewById(R.id.messageText);
        messageText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    authLayout.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.shape_active));
                }else{
                    messageText.setText("");
                    authLayout.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.shape_main));
                }
            }
        });
        messageText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                        Controller.MesajGonder(currentUser, messageText.getText().toString());
                        return true;
                    }
                }
                return false;
            }
        });
    }
    private void initKontrol(){
        final LinearLayout yorumLayout = (LinearLayout) findViewById(R.id.yorumLayout);
        final LinearLayout istekLayout = (LinearLayout) findViewById(R.id.istekLayout);

        ImageButton closeButton = (ImageButton) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yorumLayout.setVisibility(View.VISIBLE);
                istekLayout.setVisibility(View.GONE);
            }
        });
        ImageButton oyVerButton = (ImageButton) findViewById(R.id.oyVerButton);
        oyVerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(yorumLayout.getVisibility() == View.VISIBLE){
                    yorumLayout.setVisibility(View.GONE);
                    istekLayout.setVisibility(View.VISIBLE);
                }else{
                    yorumLayout.setVisibility(View.VISIBLE);
                    istekLayout.setVisibility(View.GONE);
                }
            }
        });
        ImageButton istekYapButton = (ImageButton) findViewById(R.id.istekYapButton);
        istekYapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                final View istekForm = factory.inflate(R.layout.istek_layout, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getResources().getString(R.string.make_request))
                        .setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText istekParcaText = (EditText) istekForm.findViewById(R.id.istekParcaText);
                                EditText notText = (EditText) istekForm.findViewById(R.id.notText);
                                Controller.istekYap(currentUser,istekParcaText.getText().toString(),notText.getText().toString());
                            }
                        })
                        .setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                builder.setView(istekForm);
                builder.create();
                builder.show();
            }
        });
    }
    private void touchUI(){
        RelativeLayout touchInterceptor = (RelativeLayout) findViewById(R.id.activity_main);
        final EditText mEditText = (EditText) findViewById(R.id.messageText);
        final ListView YorumListesi = (ListView) findViewById(R.id.yorumlarListesi);
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (mEditText.isFocused()) {
                        Rect outRect = new Rect();
                        mEditText.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                            mEditText.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                }
                return false;
            }
        };
        YorumListesi.setOnTouchListener(listener);
        touchInterceptor.setOnTouchListener(listener);
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}