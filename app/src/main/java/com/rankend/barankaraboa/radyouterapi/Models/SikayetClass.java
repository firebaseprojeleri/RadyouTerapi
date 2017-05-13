package com.rankend.barankaraboa.radyouterapi.Models;

/**
 * Created by SE on 11.05.2017.
 */

public class SikayetClass {
    public String sikayetEden;
    public String sikayetSebep;
    public String sikayetEdilenKullanici;
    public String sikayetEdilenMesaj;
    public long sikayetZaman;

    public SikayetClass() {

    }
    public SikayetClass(String sikayetEden, String sikayetSebep, String sikayetEdilenKullanici, String sikayetEdilenMesaj, long sikayetZaman) {
        this.sikayetEden = sikayetEden;
        this.sikayetSebep = sikayetSebep;
        this.sikayetEdilenKullanici = sikayetEdilenKullanici;
        this.sikayetEdilenMesaj = sikayetEdilenMesaj;
        this.sikayetZaman = sikayetZaman;
    }
}
