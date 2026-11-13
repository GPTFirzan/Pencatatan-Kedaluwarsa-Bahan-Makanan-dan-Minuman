package com.mycompany.pencatatanbahan.controller;

import com.mycompany.pencatatanbahan.model.BahanMakanan;
import com.mycompany.pencatatanbahan.service.BahanService;
import com.mycompany.pencatatanbahan.util.SpringUtil;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller: penghubung View (Interface) dengan Service.
 * View tidak tahu apa-apa soal Spring / Hibernate / database.
 */
public class BahanController {

    private final BahanService service = SpringUtil.getBean(BahanService.class);

    public List<BahanMakanan> ambilSemua() {
        return service.ambilSemua();
    }

    public List<BahanMakanan> cari(String kataKunci) {
        return service.cari(kataKunci);
    }

    public void tambah(String nama, LocalDate tanggal) {
        service.tambah(nama, tanggal);
    }

    public void ubah(int id, String nama, LocalDate tanggal) {
        service.ubah(id, nama, tanggal);
    }

    public void hapus(int id) {
        service.hapus(id);
    }
}
