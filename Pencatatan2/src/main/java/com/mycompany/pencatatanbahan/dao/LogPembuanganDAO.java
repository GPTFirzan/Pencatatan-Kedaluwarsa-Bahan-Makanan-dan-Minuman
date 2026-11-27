package com.mycompany.pencatatanbahan.dao;

import com.mycompany.pencatatanbahan.model.LogPembuangan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class LogPembuanganDAO {
    @PersistenceContext
    private EntityManager em;

    public void simpan(LogPembuangan log) {
        em.persist(log);
    }

    public List<LogPembuangan> cariSemua() {
        return em.createQuery(
                "FROM LogPembuangan l ORDER BY l.tanggalDibuang DESC, l.id DESC",
                LogPembuangan.class).getResultList();
    }

    public List<LogPembuangan> cariByNama(String kataKunci) {
        return em.createQuery(
                "FROM LogPembuangan l WHERE LOWER(l.namaBahan) LIKE :kata "
                + "ORDER BY l.tanggalDibuang DESC, l.id DESC", LogPembuangan.class)
                .setParameter("kata", "%" + kataKunci.toLowerCase() + "%")
                .getResultList();
    }
}
