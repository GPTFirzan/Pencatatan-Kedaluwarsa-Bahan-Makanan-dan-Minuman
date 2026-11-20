package com.mycompany.pencatatanbahan.dao;

import com.mycompany.pencatatanbahan.model.BahanMakanan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class BahanDAO {
    @PersistenceContext
    private EntityManager em;
    public List<BahanMakanan> cariSemua() {
        return em.createQuery(
                "FROM BahanMakanan b ORDER BY b.tanggalKedaluwarsa ASC", BahanMakanan.class)
                .getResultList();
    }

    public List<BahanMakanan> cariByNama(String kataKunci) {
        return em.createQuery(
                "FROM BahanMakanan b WHERE LOWER(b.nama) LIKE :kata "
                + "ORDER BY b.tanggalKedaluwarsa ASC", BahanMakanan.class)
                .setParameter("kata", "%" + kataKunci.toLowerCase() + "%")
                .getResultList();
    }

    public BahanMakanan cariById(int id) {
        return em.find(BahanMakanan.class, id);
    }

    public void simpan(BahanMakanan bahan) {
        em.persist(bahan);
    }

    public void ubah(BahanMakanan bahan) {
        em.merge(bahan);
    }

    public void hapus(BahanMakanan bahan) {
        em.remove(bahan);
    }
}
