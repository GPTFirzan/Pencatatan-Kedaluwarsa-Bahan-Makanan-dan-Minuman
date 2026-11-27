package com.mycompany.pencatatanbahan.service;

import com.mycompany.pencatatanbahan.dao.BahanDAO;
import com.mycompany.pencatatanbahan.dao.LogPembuanganDAO;
import com.mycompany.pencatatanbahan.model.BahanMakanan;
import com.mycompany.pencatatanbahan.model.LogPembuangan;
import com.mycompany.pencatatanbahan.model.RingkasanDashboard;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service: logika bisnis + validasi + transaksi.
 * (Di project Anda file ini bernama service.java -> rename jadi BahanService.java)
 */
public class BahanService {

    public static final int BATAS_PERINGATAN = 3; // H-3 dianggap "segera"
    public static final String[] DAFTAR_ALASAN =
            {"Kedaluwarsa", "Rusak / Busuk", "Terkontaminasi", "Lainnya"};

    private BahanDAO bahanDAO;
    private LogPembuanganDAO logDAO;

    // IMPLEMENTASI DI: Setter Injection
    public void setBahanDAO(BahanDAO bahanDAO) {
        this.bahanDAO = bahanDAO;
    }

    public void setLogPembuanganDAO(LogPembuanganDAO logDAO) {
        this.logDAO = logDAO;
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

    /**
     * Hapus bahan SEKALIGUS mencatatnya ke Wastage Log.
     * Keduanya berada dalam satu transaksi: jika salah satu gagal, keduanya dibatalkan.
     */
    @Transactional
    public void hapus(int id, String alasan) {
        BahanMakanan bahan = bahanDAO.cariById(id);
        if (bahan == null) {
            throw new IllegalArgumentException("Data dengan id " + id + " tidak ditemukan.");
        }
        String alasanFinal = (alasan == null || alasan.trim().isEmpty()) ? "Lainnya" : alasan.trim();
        logDAO.simpan(new LogPembuangan(
                bahan.getNama(),
                bahan.getTanggalKedaluwarsa(),
                LocalDateTime.now(),
                statusKondisi(bahan.getTanggalKedaluwarsa(), LocalDate.now()),
                alasanFinal));
        bahanDAO.hapus(bahan);
    }

    @Transactional(readOnly = true)
    public List<LogPembuangan> ambilLog(String kataKunci) {
        if (kataKunci == null || kataKunci.trim().isEmpty()) {
            return logDAO.cariSemua();
        }
        return logDAO.cariByNama(kataKunci.trim());
    }

    @Transactional(readOnly = true)
    public RingkasanDashboard ringkasan() {
        RingkasanDashboard r = new RingkasanDashboard();
        LocalDate hariIni = LocalDate.now();

        // --- Stok saat ini ---
        for (BahanMakanan b : bahanDAO.cariSemua()) {
            long sisa = ChronoUnit.DAYS.between(hariIni, b.getTanggalKedaluwarsa());
            r.totalBahan++;
            if (sisa <= 0) r.kedaluwarsa++;
            else if (sisa <= BATAS_PERINGATAN) r.segera++;
            else r.aman++;
        }

        // --- Wastage ---
        List<LogPembuangan> logs = logDAO.cariSemua();
        r.totalDibuang = logs.size();
        YearMonth sekarang = YearMonth.from(hariIni);

        Map<YearMonth, Integer> bulan = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) bulan.put(sekarang.minusMonths(i), 0);

        Map<String, Integer> alasan = new HashMap<>();
        Map<String, Integer> nama = new HashMap<>();
        for (LogPembuangan l : logs) {
            YearMonth ym = YearMonth.from(l.getTanggalDibuang());
            if (ym.equals(sekarang)) r.dibuangBulanIni++;
            bulan.computeIfPresent(ym, (k, v) -> v + 1);
            alasan.merge(l.getAlasan(), 1, Integer::sum);
            nama.merge(l.getNamaBahan().trim(), 1, Integer::sum);
        }
        Locale id = Locale.forLanguageTag("id-ID");
        bulan.forEach((ym, n) -> r.perBulan.put(
                ym.getMonth().getDisplayName(TextStyle.SHORT, id) + " " + String.valueOf(ym.getYear()).substring(2), n));
        urutkanTurun(alasan, r.perAlasan, Integer.MAX_VALUE);
        urutkanTurun(nama, r.topBahan, 5);
        return r;
    }

    private static void urutkanTurun(Map<String, Integer> sumber, Map<String, Integer> tujuan, int batas) {
        sumber.entrySet().stream()
                .sorted((a, b) -> b.getValue() != a.getValue().intValue()
                        ? Integer.compare(b.getValue(), a.getValue())
                        : a.getKey().compareToIgnoreCase(b.getKey()))
                .limit(batas)
                .forEach(e -> tujuan.put(e.getKey(), e.getValue()));
    }

    static String statusKondisi(LocalDate tanggal, LocalDate hariIni) {
        long sisa = ChronoUnit.DAYS.between(hariIni, tanggal);
        if (sisa < 0) return "Sudah Kedaluwarsa";
        if (sisa == 0) return "Hari Ini Kedaluwarsa";
        if (sisa <= BATAS_PERINGATAN) return "Segera Kedaluwarsa";
        return "Masih Aman";
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
