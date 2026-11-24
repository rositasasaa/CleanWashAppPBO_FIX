package laundry.gui;

import laundry.controller.LaundryController;
import laundry.model.CucianInvalidException;
import laundry.model.Pelanggan;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * [MVC - VIEW / HALAMAN 3]
 * Halaman CRUD lengkap (Tambah, Lihat, Ubah, Hapus) dan Pencarian untuk Entitas 1 (Pelanggan).
 * Seluruh operasi bisnis dan penyimpanan .txt didelegasikan ke LaundryController.
 */
public class FormPelanggan extends JFrame {

    // [ATRIBUT]
    private final LaundryController controller;
    private JTextField txtId, txtNama, txtNoHp, txtAlamat, txtCari;
    private JButton btnTambah, btnUbah, btnHapus, btnBersihkan, btnKembali;
    private JTable tabelPelanggan;
    private DefaultTableModel modelTabel;
    private JMenuItem itemMenuUtama, itemTransaksi, itemLogOut, itemKeluar, itemTentang;

    public FormPelanggan(LaundryController controller) {
        this.controller = controller;
        inisialisasiGUI();
        pasangEvent();
        muatTabel(controller.getDaftarPelanggan());
    }

    private void inisialisasiGUI() {
        setTitle("Kelola Data Pelanggan - CleanWashApp");
        setSize(780, 560);
        setMinimumSize(new Dimension(680, 480));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- [JMenuBar KONSISTEN ANTARHALAMAN] ---
        JMenuBar menuBar = new JMenuBar();
        JMenu menuNavigasi = new JMenu("Menu");
        itemMenuUtama = new JMenuItem("Menu Utama");
        itemTransaksi = new JMenuItem("Kelola Transaksi Laundry");
        itemLogOut = new JMenuItem("Log Out");
        itemKeluar = new JMenuItem("Keluar Aplikasi");

        menuNavigasi.add(itemMenuUtama);
        menuNavigasi.add(itemTransaksi);
        menuNavigasi.addSeparator();
        menuNavigasi.add(itemLogOut);
        menuNavigasi.add(itemKeluar);

        JMenu menuBantuan = new JMenu("Bantuan");
        itemTentang = new JMenuItem("Tentang CleanWashApp");
        menuBantuan.add(itemTentang);

        menuBar.add(menuNavigasi);
        menuBar.add(menuBantuan);
        setJMenuBar(menuBar);

        // =========================================================
        // --- PANEL UTARA (Form GridBagLayout Ringkas + Tombol) ---
        // =========================================================
        JPanel panelUtara = new JPanel(new BorderLayout());

        // [LAYOUT MANAGER] GridBagLayout dengan spacer kanan agar kotak input tidak melebar berlebihan
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Form Data Pelanggan"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Baris 0: ID Pelanggan (Read-Only)
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("ID Pelanggan:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField("(Otomatis)", 14);
        txtId.setEditable(false);
        panelForm.add(txtId, gbc);

        // Baris 1: Nama Pelanggan
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Nama Pelanggan:"), gbc);
        gbc.gridx = 1;
        txtNama = new JTextField(22);
        panelForm.add(txtNama, gbc);

        // Baris 2: No. HP
        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("No. HP:"), gbc);
        gbc.gridx = 1;
        txtNoHp = new JTextField(16);
        panelForm.add(txtNoHp, gbc);

        // Baris 3: Alamat
        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1;
        txtAlamat = new JTextField(28);
        panelForm.add(txtAlamat, gbc);

        // Spacer kanan (agar komponen tetap rata kiri dan rapi saat jendela diperbesar)
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 1.0;
        panelForm.add(Box.createHorizontalGlue(), gbc);

        // Panel Tombol Aksi (FlowLayout)
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btnTambah = new JButton("Tambah");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        btnBersihkan = new JButton("Bersihkan");
        btnKembali = new JButton("Menu Utama");

        panelTombol.add(btnTambah);
        panelTombol.add(btnUbah);
        panelTombol.add(btnHapus);
        panelTombol.add(btnBersihkan);
        panelTombol.add(btnKembali);

        panelUtara.add(panelForm, BorderLayout.CENTER);
        panelUtara.add(panelTombol, BorderLayout.SOUTH);
        add(panelUtara, BorderLayout.NORTH);

        // =========================================================
        // --- PANEL TENGAH (Pencarian + JTable Read-Only) ---
        // =========================================================
        JPanel panelTengah = new JPanel(new BorderLayout(5, 5));
        panelTengah.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel panelCari = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelCari.add(new JLabel("Cari Pelanggan (ID/Nama/HP/Alamat):"));
        txtCari = new JTextField(22);
        panelCari.add(txtCari);
        panelTengah.add(panelCari, BorderLayout.NORTH);

        String[] kolom = {"ID Pelanggan", "Nama Pelanggan", "No. HP", "Alamat"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelPelanggan = new JTable(modelTabel);
        tabelPelanggan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelTengah.add(new JScrollPane(tabelPelanggan), BorderLayout.CENTER);

        add(panelTengah, BorderLayout.CENTER);
    }

    /**
     * [EVENT HANDLING TERPISAH]
     * Menghubungkan setiap komponen GUI ke method khusus.
     */
    private void pasangEvent() {
        btnTambah.addActionListener(e -> tambahPelanggan());
        btnUbah.addActionListener(e -> ubahPelanggan());
        btnHapus.addActionListener(e -> hapusPelanggan());
        btnBersihkan.addActionListener(e -> bersihkanForm());
        btnKembali.addActionListener(e -> bukaMenuUtama());

        tabelPelanggan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariTabel();
            }
        });

        // [EVENT LISTENER PENCARIAN REAL-TIME]
        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { cariData(); }
            @Override
            public void removeUpdate(DocumentEvent e) { cariData(); }
            @Override
            public void changedUpdate(DocumentEvent e) { cariData(); }
        });

        // Navigasi MenuBar
        itemMenuUtama.addActionListener(e -> bukaMenuUtama());
        itemTransaksi.addActionListener(e -> bukaFormTransaksi());
        itemLogOut.addActionListener(e -> prosesLogOut());
        itemKeluar.addActionListener(e -> prosesKeluar());
        itemTentang.addActionListener(e -> tampilkanTentang());
    }

    // [METHOD CRUD - READ]
    private void muatTabel(List<Pelanggan> daftar) {
        modelTabel.setRowCount(0);
        for (Pelanggan p : daftar) {
            modelTabel.addRow(new Object[]{
                p.getIdPelanggan(),
                p.getNama(),
                p.getNoHp(),
                p.getAlamat()
            });
        }
    }

    private void isiFormDariTabel() {
        int baris = tabelPelanggan.getSelectedRow();
        if (baris >= 0) {
            txtId.setText(modelTabel.getValueAt(baris, 0).toString());
            txtNama.setText(modelTabel.getValueAt(baris, 1).toString());
            txtNoHp.setText(modelTabel.getValueAt(baris, 2).toString());
            txtAlamat.setText(modelTabel.getValueAt(baris, 3).toString());
        }
    }

    // [METHOD CRUD - CREATE dengan Exception Handling]
    private void tambahPelanggan() {
        try {
            controller.tambahPelanggan(txtNama.getText(), txtNoHp.getText(), txtAlamat.getText());
            muatTabel(controller.getDaftarPelanggan());
            bersihkanForm();
            JOptionPane.showMessageDialog(this, "Data pelanggan berhasil ditambahkan!");
        } catch (CucianInvalidException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan file: " + ex.getMessage(), "Error File", JOptionPane.ERROR_MESSAGE);
        }
    }

    // [METHOD CRUD - UPDATE]
    private void ubahPelanggan() {
        int baris = tabelPelanggan.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris pelanggan pada tabel terlebih dahulu!");
            return;
        }
        try {
            String id = txtId.getText();
            controller.ubahPelanggan(id, txtNama.getText(), txtNoHp.getText(), txtAlamat.getText());
            muatTabel(controller.getDaftarPelanggan());
            bersihkanForm();
            JOptionPane.showMessageDialog(this, "Data pelanggan berhasil diperbarui!");
        } catch (CucianInvalidException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui file: " + ex.getMessage(), "Error File", JOptionPane.ERROR_MESSAGE);
        }
    }

    // [METHOD CRUD - DELETE dengan Dialog Konfirmasi]
    private void hapusPelanggan() {
        int baris = tabelPelanggan.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris pelanggan yang ingin dihapus!");
            return;
        }
        String id = modelTabel.getValueAt(baris, 0).toString();
        String nama = modelTabel.getValueAt(baris, 1).toString();
        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus pelanggan " + nama + " (" + id + ")?",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            try {
                controller.hapusPelanggan(id);
                muatTabel(controller.getDaftarPelanggan());
                bersihkanForm();
                JOptionPane.showMessageDialog(this, "Data pelanggan berhasil dihapus!");
            } catch (CucianInvalidException | IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Hapus", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cariData() {
        muatTabel(controller.cariPelanggan(txtCari.getText()));
    }

    private void bersihkanForm() {
        txtId.setText("(Otomatis)");
        txtNama.setText("");
        txtNoHp.setText("");
        txtAlamat.setText("");
        tabelPelanggan.clearSelection();
    }

    private void bukaMenuUtama() {
        new MenuUtama(controller).setVisible(true);
        dispose();
    }

    private void bukaFormTransaksi() {
        new FormTransaksi(controller).setVisible(true);
        dispose();
    }

    private void prosesLogOut() {
        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin Log Out?", "Konfirmasi Log Out", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            new HalamanLogin().setVisible(true);
            dispose();
        }
    }

    private void prosesKeluar() {
        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Yakin ingin menutup aplikasi?", "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void tampilkanTentang() {
        JOptionPane.showMessageDialog(this,
            "CleanWashApp v2.0 (Arsitektur MVC)\nDikembangkan oleh:\n- Yunita Dwi Ardilasari\n- Greycilfa\n- Rosita Salsabila",
            "Tentang CleanWashApp", JOptionPane.INFORMATION_MESSAGE);
    }
}