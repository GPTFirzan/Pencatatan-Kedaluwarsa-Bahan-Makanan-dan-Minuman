package com.mycompany.pencatatanbahan.service;

import com.mycompany.pencatatanbahan.dao.BahanDAO;
import com.mycompany.pencatatanbahan.model.BahanMakanan;
import java.time.LocalDate;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service: logika bisnis + validasi + transaksi.
 * (Di project Anda file ini bernama service.java -> rename jadi BahanService.java)
 */
public class BahanService {

    private BahanDAO bahanDAO;

    // IMPLEMENTASI DI: Setter Injection
    public void setBahanDAO(BahanDAO bahanDAO) {
        this.bahanDAO = bahanDAO;
    }

    // Declarative Transaction: tidak ada beginTransaction/commit/rollback manual
    @Transactional(readOnly = true)
    public List<BahanMakanan> ambilSemua() {
        return bahanDAO.cariSemua();
    }

    // FITUR PENCARIAN: kata kunci kosong = tampilkan semua
    @Transactional(readOnly = true)
    public List<BahanMakanan> cari(String kataKunci) {
        if (kataKunci == null || kataKunci.trim().isEmpty()) {
            return bahanDAO.cariSemua();
        }
        return bahanDAO.cariByNama(kataKunci.trim());
    }

    @Transactional
    public void tambah(String nama, LocalDate tanggal) {
        validasi(nama, tanggal);
        bahanDAO.simpan(new BahanMakanan(nama.trim(), tanggal));
    }

    @Transactional
    public void ubah(int id, String nama, LocalDate tanggal) {
        validasi(nama, tanggal);
        BahanMakanan bahan = bahanDAO.cariById(id);
        if (bahan == null) {
            throw new IllegalArgumentException("Data dengan id " + id + " tidak ditemukan.");
        }
        bahan.setNama(nama.trim());
        bahan.setTanggalKedaluwarsa(tanggal);
        bahanDAO.ubah(bahan);
    }

    @Transactional
    public void hapus(int id) {
        BahanMakanan bahan = bahanDAO.cariById(id);
        if (bahan == null) {
            throw new IllegalArgumentException("Data dengan id " + id + " tidak ditemukan.");
        }
        bahanDAO.hapus(bahan);
    }

    private void validasi(String nama, LocalDate tanggal) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama bahan tidak boleh kosong.");
        }
        if (tanggal == null) {
            throw new IllegalArgumentException("Tanggal kedaluwarsa tidak boleh kosong.");
        }
    }
}
