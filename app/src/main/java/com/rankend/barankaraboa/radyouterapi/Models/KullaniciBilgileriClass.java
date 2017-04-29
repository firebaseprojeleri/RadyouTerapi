package com.rankend.barankaraboa.radyouterapi.Models;

/**
 * Created by SE on 19.04.2017.
 */

public class KullaniciBilgileriClass {
    public String email;
    public String nickname;
    public String profile_picture;

    public KullaniciBilgileriClass(String email, String nickname, String profile_picture, String username) {
        this.email = email;
        this.nickname = nickname;
        this.profile_picture = profile_picture;
        this.username = username;
    }

    public String username;

    public KullaniciBilgileriClass() {

    }

}
