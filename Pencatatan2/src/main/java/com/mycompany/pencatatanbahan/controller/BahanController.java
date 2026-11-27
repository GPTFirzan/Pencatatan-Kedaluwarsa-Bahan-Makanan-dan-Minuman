package com.mycompany.pencatatanbahan.controller;

import com.mycompany.pencatatanbahan.model.BahanMakanan;
import com.mycompany.pencatatanbahan.model.LogPembuangan;
import com.mycompany.pencatatanbahan.model.RingkasanDashboard;
import com.mycompany.pencatatanbahan.service.BahanService;
import com.mycompany.pencatatanbahan.util.SpringUtil;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller: penghubung View (Swing) dengan Service (bean Spring).
 * Service diambil secara lazy agar kegagalan koneksi database tidak
 * membuat jendela gagal dibuat; error tampil sebagai dialog di View.
 */
public class BahanController {

    private BahanService service;

    private BahanService service() {
        if (service == null) {
            service = SpringUtil.getBean(BahanService.class);
        }
        return service;
    }

    public List<BahanMakanan> ambilSemua() {
        return service().ambilSemua();
    }

    public List<BahanMakanan> cari(String kataKunci) {
        return service().cari(kataKunci);
    }

    public void tambah(String nama, LocalDate tanggal) {
        service().tambah(nama, tanggal);
    }

    public void ubah(int id, String nama, LocalDate tanggal) {
        service().ubah(id, nama, tanggal);
    }

    public void hapus(int id, String alasan) {
        service().hapus(id, alasan);
    }

    public List<LogPembuangan> ambilLog(String kataKunci) {
        return service().ambilLog(kataKunci);
    }

    public RingkasanDashboard ringkasan() {
        return service().ringkasan();
    }
}
