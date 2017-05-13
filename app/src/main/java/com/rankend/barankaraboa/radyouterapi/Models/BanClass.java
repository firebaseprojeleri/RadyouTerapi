package com.rankend.barankaraboa.radyouterapi.Models;

/**
 * Created by SE on 11.05.2017.
 */

public class BanClass {
    public boolean banStatus;
    public long banTime;
    public String banReason;
    public long unBanTime;

    public BanClass() {
    }

    public BanClass(boolean banStatus, long banTime, String banReason, long unBanTime) {
        this.banStatus = banStatus;
        this.banTime = banTime;
        this.banReason = banReason;
        this.unBanTime = unBanTime;
    }
}
