package com.mycompany.pencatatanbahan.model;

import java.util.LinkedHashMap;
import java.util.Map;

/** Data siap tampil untuk Dashboard (hasil olahan Service). */
public class RingkasanDashboard {
    public int totalBahan;
    public int aman;
    public int segera;
    public int kedaluwarsa;
    public int totalDibuang;
    public int dibuangBulanIni;
    /** 6 bulan terakhir, label "MMM yyyy" -> jumlah (urut dari terlama). */
    public Map<String, Integer> perBulan = new LinkedHashMap<>();
    /** Alasan -> jumlah (urut terbanyak). */
    public Map<String, Integer> perAlasan = new LinkedHashMap<>();
    /** Nama bahan -> jumlah kali dibuang (maks 5, terbanyak). */
    public Map<String, Integer> topBahan = new LinkedHashMap<>();
}
