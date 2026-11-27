package com.mycompany.pencatatanbahan.view;

import com.mycompany.pencatatanbahan.controller.BahanController;
import com.mycompany.pencatatanbahan.model.RingkasanDashboard;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/** Dashboard ringkasan: kondisi stok saat ini + statistik pembuangan (wastage). */
public class DashboardDialog extends JDialog {

    private static final Color HIJAU = new Color(46, 125, 50);
    private static final Color ORANYE = new Color(239, 108, 0);
    private static final Color MERAH = new Color(198, 40, 40);
    private static final Color BIRU = new Color(21, 101, 192);
    private static final Color ABU = new Color(96, 96, 96);
    private static final Color GARIS = new Color(224, 224, 224);

    private final BahanController controller;
    private final JPanel isi = new JPanel(new BorderLayout(0, 12));

    public DashboardDialog(JFrame parent, BahanController controller) {
        super(parent, "Dashboard Bahan & Pembuangan", false);
        this.controller = controller;

        isi.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
        isi.setBackground(new Color(245, 246, 248));

        JButton btnMuat = new JButton("Muat Ulang");
        JButton btnTutup = new JButton("Tutup");
        btnMuat.addActionListener(e -> muat());
        btnTutup.addActionListener(e -> dispose());
        JPanel tombol = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 6));
        tombol.add(btnMuat);
        tombol.add(btnTutup);

        setLayout(new BorderLayout());
        add(isi, BorderLayout.CENTER);
        add(tombol, BorderLayout.SOUTH);
        setSize(860, 620);
        setLocationRelativeTo(parent);
        muat();
    }

    public void muat() {
        RingkasanDashboard r;
        try {
            r = controller.ringkasan();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat dashboard: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        isi.removeAll();
        isi.add(buatKartu(r), BorderLayout.NORTH);

        JPanel tengah = new JPanel(new GridLayout(1, 2, 12, 0));
        tengah.setOpaque(false);
        tengah.add(bungkus("Pembuangan per Bulan (6 bulan terakhir)",
                new BatangVertikal(r.perBulan, BIRU)));
        tengah.add(bungkus("Alasan Pembuangan",
                new BatangHorizontal(r.perAlasan, ORANYE, "Belum ada data pembuangan")));
        isi.add(tengah, BorderLayout.CENTER);

        isi.add(bungkus("Bahan Paling Sering Dibuang (Top 5)",
                new BatangHorizontal(r.topBahan, MERAH, "Belum ada data pembuangan"), 150),
                BorderLayout.SOUTH);
        isi.revalidate();
        isi.repaint();
    }

    // ---------- Kartu angka ----------
    private JPanel buatKartu(RingkasanDashboard r) {
        JPanel baris = new JPanel(new GridLayout(1, 6, 10, 0));
        baris.setOpaque(false);
        baris.add(kartu("Bahan Tercatat", r.totalBahan, ABU));
        baris.add(kartu("Aman", r.aman, HIJAU));
        baris.add(kartu("Segera (≤3 hari)", r.segera, ORANYE));
        baris.add(kartu("Kedaluwarsa", r.kedaluwarsa, MERAH));
        baris.add(kartu("Dibuang Bulan Ini", r.dibuangBulanIni, BIRU));
        baris.add(kartu("Total Dibuang", r.totalDibuang, BIRU));
        return baris;
    }

    private JPanel kartu(String judul, int nilai, Color warna) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 0, 0, 0, warna),
                BorderFactory.createEmptyBorder(8, 10, 10, 10)));
        JLabel lblNilai = new JLabel(String.valueOf(nilai));
        lblNilai.setFont(new Font("SansSerif", Font.BOLD, 30));
        lblNilai.setForeground(warna);
        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblJudul.setForeground(ABU);
        p.add(lblNilai, BorderLayout.CENTER);
        p.add(lblJudul, BorderLayout.SOUTH);
        return p;
    }

    private JPanel bungkus(String judul, JPanel konten) { return bungkus(judul, konten, 0); }

    private JPanel bungkus(String judul, JPanel konten, int tinggi) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GARIS),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        JLabel l = new JLabel(judul);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        p.add(l, BorderLayout.NORTH);
        p.add(konten, BorderLayout.CENTER);
        if (tinggi > 0) p.setPreferredSize(new Dimension(0, tinggi));
        return p;
    }

    // ---------- Grafik batang vertikal ----------
    private static class BatangVertikal extends JPanel {
        private final Map<String, Integer> data;
        private final Color warna;

        BatangVertikal(Map<String, Integer> data, Color warna) {
            this.data = data;
            this.warna = warna;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int kiri = 8, bawah = 22, atas = 18;
            int max = Math.max(1, data.values().stream().mapToInt(Integer::intValue).max().orElse(1));
            int n = Math.max(1, data.size());
            int tinggiPlot = h - bawah - atas;
            int lebarSlot = (w - kiri * 2) / n;
            int lebarBatang = Math.min(46, lebarSlot - 14);

            g.setColor(GARIS);
            g.setStroke(new BasicStroke(1f));
            g.drawLine(kiri, h - bawah, w - kiri, h - bawah);

            FontMetrics fm = g.getFontMetrics();
            int i = 0;
            for (Map.Entry<String, Integer> e : data.entrySet()) {
                int x = kiri + i * lebarSlot + (lebarSlot - lebarBatang) / 2;
                int tinggi = (int) Math.round(tinggiPlot * (e.getValue() / (double) max));
                if (e.getValue() > 0) {
                    g.setColor(warna);
                    g.fillRoundRect(x, h - bawah - tinggi, lebarBatang, tinggi, 4, 4);
                }
                g.setColor(Color.DARK_GRAY);
                String nilai = String.valueOf(e.getValue());
                g.drawString(nilai, x + (lebarBatang - fm.stringWidth(nilai)) / 2,
                        h - bawah - tinggi - 4);
                g.setColor(ABU);
                g.drawString(e.getKey(), x + (lebarBatang - fm.stringWidth(e.getKey())) / 2, h - 6);
                i++;
            }
            g.dispose();
        }
    }

    // ---------- Grafik batang horizontal ----------
    private static class BatangHorizontal extends JPanel {
        private final Map<String, Integer> data;
        private final Color warna;
        private final String kosong;

        BatangHorizontal(Map<String, Integer> data, Color warna, String kosong) {
            this.data = data;
            this.warna = warna;
            this.kosong = kosong;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            FontMetrics fm = g.getFontMetrics();
            if (data.isEmpty()) {
                g.setColor(ABU);
                g.drawString(kosong, 4, 20);
                g.dispose();
                return;
            }
            int labelW = 0;
            for (String k : data.keySet()) labelW = Math.max(labelW, fm.stringWidth(k));
            labelW = Math.min(labelW, getWidth() / 3) + 10;
            int max = Math.max(1, data.values().stream().mapToInt(Integer::intValue).max().orElse(1));
            int barisH = 26;
            int plotW = getWidth() - labelW - 40;
            int y = 4;
            for (Map.Entry<String, Integer> e : data.entrySet()) {
                if (y + barisH > getHeight() + 2) break;
                g.setColor(Color.DARK_GRAY);
                String k = e.getKey();
                while (fm.stringWidth(k) > labelW - 10 && k.length() > 2) k = k.substring(0, k.length() - 2) + "…";
                g.drawString(k, 0, y + 16);
                int lebar = Math.max(3, (int) Math.round(plotW * (e.getValue() / (double) max)));
                g.setColor(warna);
                g.fillRoundRect(labelW, y + 3, lebar, barisH - 10, 4, 4);
                g.setColor(Color.DARK_GRAY);
                g.drawString(String.valueOf(e.getValue()), labelW + lebar + 6, y + 16);
                y += barisH;
            }
            g.dispose();
        }
    }
}
