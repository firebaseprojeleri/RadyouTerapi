package com.rankend.barankaraboa.radyouterapi.Models;

import java.util.HashMap;

/**
 * Created by SE on 20.04.2017.
 */

public class IstekClass {
    public String key;
    public String userId;
    public String userNickName;
    public String userImage;
    public String istekSarki;
    public String istekNot;
    public HashMap<String,Boolean> oylar = new HashMap<>();
    public int oySayisi;
    public long addedTime;
    public long ratedTime;

    public IstekClass(String userId, String userNickName, String userImage, String istekSarki, String istekNot, int oySayisi, long addedTime, long ratedTime) {
        this.userId = userId;
        this.userNickName = userNickName;
        this.userImage = userImage;
        this.istekSarki = istekSarki;
        this.istekNot = istekNot;
        this.oySayisi = oySayisi;
        this.addedTime = addedTime;
        this.ratedTime = ratedTime;
    }

    public IstekClass() {
    }
}
