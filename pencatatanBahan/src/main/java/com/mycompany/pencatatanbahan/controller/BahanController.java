package com.mycompany.pencatatanbahan.controller;

import com.mycompany.pencatatanbahan.model.BahanMakanan;
import com.mycompany.pencatatanbahan.util.HibernateUtil;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BahanController {

    public List<BahanMakanan> ambilSemua() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from BahanMakanan order by tanggalKedaluwarsa asc",
                    BahanMakanan.class).list();
        }
    }

    public void tambah(String nama, LocalDate tanggal) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(new BahanMakanan(nama, tanggal));
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void ubah(int id, String nama, LocalDate tanggal) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            BahanMakanan b = session.find(BahanMakanan.class, id);
            if (b != null) {
                b.setNama(nama);
                b.setTanggalKedaluwarsa(tanggal);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void hapus(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            BahanMakanan b = session.find(BahanMakanan.class, id);
            if (b != null) session.remove(b);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }
}