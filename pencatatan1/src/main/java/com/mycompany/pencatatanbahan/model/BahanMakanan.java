package com.mycompany.pencatatanbahan.model;

import jakarta.persistence.*;
import java.time.LocalDate;

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

    public BahanMakanan() {}

    public BahanMakanan(String nama, LocalDate tanggalKedaluwarsa) {
        this.nama = nama;
        this.tanggalKedaluwarsa = tanggalKedaluwarsa;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public LocalDate getTanggalKedaluwarsa() { return tanggalKedaluwarsa; }
    public void setTanggalKedaluwarsa(LocalDate tanggalKedaluwarsa) {
        this.tanggalKedaluwarsa = tanggalKedaluwarsa;
    }
}