package com.mycompany.pencatatanbahan;

import com.mycompany.pencatatanbahan.view.Interface;
import javax.swing.SwingUtilities;

/**
 * Main class: hanya menjalankan tampilan. Tidak ada logika di sini.
 */
public class PencatatanBahan {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Interface().setVisible(true));
    }
}
