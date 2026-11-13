package com.mycompany.pencatatanbahan.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * Entity Hibernate untuk tabel bahan_makanan.
 */
@Entity
@Table(name = "bahan_makanan")
public class BahanMakanan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nama", nullable = false)
    private String nama;

    @Column(name = "tanggal_kedaluwarsa", nullable = false)
    private LocalDate tanggalKedaluwarsa;

    // Hibernate wajib punya constructor kosong
    public BahanMakanan() {
    }

    public BahanMakanan(String nama, LocalDate tanggalKedaluwarsa) {
        this.nama = nama;
        this.tanggalKedaluwarsa = tanggalKedaluwarsa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public LocalDate getTanggalKedaluwarsa() {
        return tanggalKedaluwarsa;
    }

    public void setTanggalKedaluwarsa(LocalDate tanggalKedaluwarsa) {
        this.tanggalKedaluwarsa = tanggalKedaluwarsa;
    }

    /** Logika status ada di model karena murni berkaitan dengan data. */
    public boolean isKedaluwarsa() {
        return tanggalKedaluwarsa.isBefore(LocalDate.now());
    }
}
