package com.mycompany.pencatatanbahan.dao;

import com.mycompany.pencatatanbahan.model.BahanMakanan;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * DAO: satu-satunya kelas yang berbicara langsung dengan database (Hibernate).
 * (Di project Anda file ini bernama pencarian.java -> rename jadi BahanDAO.java)
 */
public class BahanDAO {

    private SessionFactory sessionFactory;

    // IMPLEMENTASI DI: Setter Injection (SessionFactory disuntikkan Spring)
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Session dikelola Spring (transparent session management)
    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    // Semua data, diurutkan dari yang paling cepat kedaluwarsa
    public List<BahanMakanan> cariSemua() {
        return getSession()
                .createQuery("FROM BahanMakanan b ORDER BY b.tanggalKedaluwarsa ASC", BahanMakanan.class)
                .list();
    }

    // FITUR PENCARIAN: nama mengandung kata kunci (tidak peka huruf besar/kecil)
    public List<BahanMakanan> cariByNama(String kataKunci) {
        return getSession()
                .createQuery("FROM BahanMakanan b WHERE LOWER(b.nama) LIKE :kata "
                        + "ORDER BY b.tanggalKedaluwarsa ASC", BahanMakanan.class)
                .setParameter("kata", "%" + kataKunci.toLowerCase() + "%")
                .list();
    }

    // Mencari satu bahan berdasarkan id, null jika tidak ada
    public BahanMakanan cariById(int id) {
        return getSession().get(BahanMakanan.class, id);
    }

    public void simpan(BahanMakanan bahan) {
        getSession().save(bahan);
    }

    public void ubah(BahanMakanan bahan) {
        getSession().update(bahan);
    }

    public void hapus(BahanMakanan bahan) {
        getSession().delete(bahan);
    }
}
