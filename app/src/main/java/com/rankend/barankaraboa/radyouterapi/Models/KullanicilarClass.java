package com.rankend.barankaraboa.radyouterapi.Models;

/**
 * Created by SE on 19.04.2017.
 */

public class KullanicilarClass {
    public String nickname;
    public String profile_picture;
    public boolean admin;

    public KullanicilarClass(){

    }

    public KullanicilarClass(String nickname, String profile_picture, boolean admin) {
        this.nickname = nickname;
        this.profile_picture = profile_picture;
    }
}
