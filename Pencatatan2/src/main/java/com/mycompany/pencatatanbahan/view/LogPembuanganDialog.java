package com.mycompany.pencatatanbahan.view;

import com.mycompany.pencatatanbahan.controller.BahanController;
import com.mycompany.pencatatanbahan.model.LogPembuangan;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** Jendela Log Pembuangan (Wastage Log): riwayat bahan yang sudah dihapus. */
public class LogPembuanganDialog extends JDialog {

    private static final DateTimeFormatter WAKTU = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final BahanController controller;
    private final DefaultTableModel model;
    private final JTextField txtCari = new JTextField(20);
    private final JLabel lblJumlah = new JLabel();

    public LogPembuanganDialog(JFrame parent, BahanController controller) {
        super(parent, "Log Pembuangan (Wastage Log)", false);
        this.controller = controller;

        model = new DefaultTableModel(new String[]{
            "No", "Nama Bahan", "Tgl Kedaluwarsa", "Waktu Dibuang", "Kondisi Saat Dibuang", "Alasan"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabel = new JTable(model);
        tabel.setRowHeight(24);
        tabel.getColumnModel().getColumn(0).setMaxWidth(40);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(150);
        tabel.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String kondisi = String.valueOf(model.getValueAt(row, 4));
                    if (kondisi.startsWith("Masih Aman")) {
                        c.setBackground(new Color(255, 236, 179)); // dibuang padahal masih aman
                        c.setForeground(new Color(120, 70, 0));
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        JButton btnCari = new JButton("Cari");
        JButton btnMuat = new JButton("Muat Ulang");
        JButton btnTutup = new JButton("Tutup");
        btnCari.addActionListener(e -> muat());
        txtCari.addActionListener(e -> muat());
        btnMuat.addActionListener(e -> { txtCari.setText(""); muat(); });
        btnTutup.addActionListener(e -> dispose());

        JPanel atas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        atas.add(new JLabel("Cari nama bahan:"));
        atas.add(txtCari);
        atas.add(btnCari);
        atas.add(btnMuat);

        JPanel bawah = new JPanel(new BorderLayout());
        bawah.setBorder(BorderFactory.createEmptyBorder(6, 12, 8, 12));
        lblJumlah.setFont(lblJumlah.getFont().deriveFont(java.awt.Font.BOLD));
        bawah.add(lblJumlah, BorderLayout.WEST);
        bawah.add(btnTutup, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(atas, BorderLayout.NORTH);
        add(new JScrollPane(tabel), BorderLayout.CENTER);
        add(bawah, BorderLayout.SOUTH);
        setSize(820, 460);
        setLocationRelativeTo(parent);
        muat();
    }

    public void muat() {
        model.setRowCount(0);
        try {
            List<LogPembuangan> data = controller.ambilLog(txtCari.getText());
            int no = 1;
            for (LogPembuangan l : data) {
                model.addRow(new Object[]{
                    no++, l.getNamaBahan(),
                    l.getTanggalKedaluwarsa().format(Interface.FORMAT),
                    l.getTanggalDibuang().format(WAKTU),
                    l.getStatusSaatDibuang(), l.getAlasan()});
            }
            lblJumlah.setText("Total: " + data.size() + " catatan pembuangan");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat log: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
