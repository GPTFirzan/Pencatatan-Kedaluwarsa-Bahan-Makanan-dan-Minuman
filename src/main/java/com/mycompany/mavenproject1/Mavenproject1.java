package com.mycompany.mavenproject1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Mavenproject1 extends JFrame {

    static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Konfigurasi Database Laragon
    private final String DB_URL = "jdbc:mysql://localhost:3306/db_kedaluwarsa_makanan";
    private final String DB_USER = "root";
    private final String DB_PASS = ""; // Laragon default password kosong

    // Komponen GUI
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtNama;
    private JTextField txtTanggal;
    private JButton btnTambah, btnUbah, btnHapus, btnClear;

    public Mavenproject1() {
        setTitle("Aplikasi Pengingat Kedaluwarsa Bahan Makanan (Database)");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        muatDataDariDatabase(); // Load data saat aplikasi dibuka
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // --- PANEL INPUT ---
        JPanel panelInput = new JPanel(new GridBagLayout());
        panelInput.setBorder(BorderFactory.createTitledBorder("Kelola Bahan Makanan / Minuman"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panelInput.add(new JLabel("Nama Bahan:"), gbc);
        txtNama = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        panelInput.add(txtNama, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelInput.add(new JLabel("Kedaluwarsa (dd-MM-yyyy):"), gbc);
        txtTanggal = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        panelInput.add(txtTanggal, gbc);

        // --- PANEL TOMBOL ---
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnTambah = new JButton("Tambah");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        btnClear = new JButton("Bersihkan Form");

        panelTombol.add(btnTambah);
        panelTombol.add(btnUbah);
        panelTombol.add(btnHapus);
        panelTombol.add(btnClear);

        JPanel panelAtas = new JPanel(new BorderLayout());
        panelAtas.add(panelInput, BorderLayout.CENTER);
        panelAtas.add(panelTombol, BorderLayout.SOUTH);
        add(panelAtas, BorderLayout.NORTH);

        // --- TABEL DATA ---
        // Kolom ID disembunyikan atau diletakkan di indeks 0 untuk keperluan edit/hapus database
        tableModel = new DefaultTableModel(new String[]{"ID", "No.", "Nama Bahan", "Tanggal Kedaluwarsa", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        
        // Sembunyikan kolom ID (kolom ke-0) agar rapi dilihat user
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Bahan Makanan & Minuman"));
        add(scrollPane, BorderLayout.CENTER);

        // --- EVENT HANDLING ---
        btnTambah.addActionListener(e -> tambahData());
        btnUbah.addActionListener(e -> ubahData());
        btnHapus.addActionListener(e -> hapusData());
        btnClear.addActionListener(e -> kosongkanForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                txtNama.setText(tableModel.getValueAt(selectedRow, 2).toString());
                txtTanggal.setText(tableModel.getValueAt(selectedRow, 3).toString());
            }
        });
    }

    // Koneksi ke Database Laragon
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    // Menampilkan data dari database ke JTable
    private void muatDataDariDatabase() {
        tableModel.setRowCount(0);
        String query = "SELECT * FROM bahan_makanan ORDER BY tanggal_kedaluwarsa ASC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int nomor = 1;
            LocalDate hariIni = LocalDate.now();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nama = rs.getString("nama");
                Date sqlDate = rs.getDate("tanggal_kedaluwarsa");
                LocalDate tanggal = sqlDate.toLocalDate();

                String status = tanggal.isBefore(hariIni) ? "SUDAH KEDALUWARSA" : "Aman";

                tableModel.addRow(new Object[]{
                    id, 
                    nomor++, 
                    nama, 
                    tanggal.format(FORMAT), 
                    status
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat database: " + e.getMessage(), "Error Koneksi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tambahData() {
        String nama = txtNama.getText().trim();
        String tglStr = txtTanggal.getText().trim();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama bahan tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate tanggal;
        try {
            tanggal = LocalDate.parse(tglStr, FORMAT);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah! Gunakan format: dd-MM-yyyy (contoh: 31-12-2026)", "Format Salah", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO bahan_makanan (nama, tanggal_kedaluwarsa) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nama);
            pstmt.setDate(2, Date.valueOf(tanggal)); // Konversi LocalDate ke java.sql.Date
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, nama + " berhasil ditambahkan ke database!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            muatDataDariDatabase();
            kosongkanForm();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ubahData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris data pada tabel yang ingin diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0); // Ambil ID asli dari database
        String namaBaru = txtNama.getText().trim();
        String tglStr = txtTanggal.getText().trim();

        if (namaBaru.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama bahan tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate tanggalBaru;
        try {
            tanggalBaru = LocalDate.parse(tglStr, FORMAT);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah! Gunakan format: dd-MM-yyyy", "Format Salah", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "UPDATE bahan_makanan SET nama = ?, tanggal_kedaluwarsa = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, namaBaru);
            pstmt.setDate(2, Date.valueOf(tanggalBaru));
            pstmt.setInt(3, id);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data berhasil diubah di database!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            muatDataDariDatabase();
            kosongkanForm();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris data pada tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM bahan_makanan WHERE id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, id);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Data berhasil dihapus dari database!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                muatDataDariDatabase();
                kosongkanForm();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void kosongkanForm() {
        txtNama.setText("");
        txtTanggal.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Mavenproject1().setVisible(true);
        });
    }
}