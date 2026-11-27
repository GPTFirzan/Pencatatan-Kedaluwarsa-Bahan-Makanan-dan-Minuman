package com.mycompany.pencatatanbahan.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Log Pembuangan (Wastage Log): satu baris dicatat setiap kali
 * sebuah bahan dihapus dari daftar bahan.
 */
@Entity
@Table(name = "wastage_log")
public class LogPembuangan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nama_bahan", nullable = false)
    private String namaBahan;

    @Column(name = "tanggal_kedaluwarsa", nullable = false)
    private LocalDate tanggalKedaluwarsa;

    @Column(name = "tanggal_dibuang", nullable = false)
    private LocalDateTime tanggalDibuang;

    /** Kondisi bahan saat dihapus: Sudah Kedaluwarsa / Hari Ini Kedaluwarsa / Segera Kedaluwarsa / Masih Aman */
    @Column(name = "status_saat_dibuang", nullable = false)
    private String statusSaatDibuang;

    @Column(name = "alasan", nullable = false)
    private String alasan;

    public LogPembuangan() {}

    public LogPembuangan(String namaBahan, LocalDate tanggalKedaluwarsa,
            LocalDateTime tanggalDibuang, String statusSaatDibuang, String alasan) {
        this.namaBahan = namaBahan;
        this.tanggalKedaluwarsa = tanggalKedaluwarsa;
        this.tanggalDibuang = tanggalDibuang;
        this.statusSaatDibuang = statusSaatDibuang;
        this.alasan = alasan;
    }

    public int getId() { return id; }
    public String getNamaBahan() { return namaBahan; }
    public LocalDate getTanggalKedaluwarsa() { return tanggalKedaluwarsa; }
    public LocalDateTime getTanggalDibuang() { return tanggalDibuang; }
    public String getStatusSaatDibuang() { return statusSaatDibuang; }
    public String getAlasan() { return alasan; }
}
