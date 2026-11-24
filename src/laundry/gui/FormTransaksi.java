package laundry.gui;

import laundry.controller.LaundryController;
import laundry.model.CucianInvalidException;
import laundry.model.Pelanggan;
import laundry.model.Transaksi;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

/**
 * [MVC - VIEW / HALAMAN 4]
 * Halaman CRUD lengkap dan Pencarian untuk Entitas 2 (Transaksi Laundry).
 * Menerapkan variasi komponen Swing lengkap: JComboBox, JRadioButton, JCheckBox, JSpinner,
 * JTextField, JTable, JMenuBar, serta perhitungan estimasi biaya secara real-time.
 */
public class FormTransaksi extends JFrame {

    // [ATRIBUT KOMPONEN INPUT BERVARIASI]
    private final LaundryController controller;
    private JTextField txtIdTransaksi, txtCari;
    private JComboBox<Pelanggan> cbPelanggan;
    private JComboBox<String> cbTipe;
    private JRadioButton rbReguler, rbExpress;
    private ButtonGroup grupPaket;
    private JSpinner spnKuantitas, spnDiskon;
    private JCheckBox chkAntarJemput;
    private JLabel lblEstimasiBiaya;

    // Tombol & Tabel
    private JButton btnTambah, btnUbah, btnHapus, btnBersihkan, btnKembali;
    private JTable tabelTransaksi;
    private DefaultTableModel modelTabel;

    // Menu Bar
    private JMenuItem itemMenuUtama, itemPelanggan, itemLogOut, itemKeluar, itemTentang;

    public FormTransaksi(LaundryController controller) {
        this.controller = controller;
        inisialisasiGUI();
        pasangEvent();
        muatComboPelanggan();
        muatTabel(controller.getDaftarTransaksi());
        perbaruiEstimasi();
    }

    private void inisialisasiGUI() {
        setTitle("Kelola Transaksi Laundry - CleanWashApp");
        setSize(920, 620);
        setMinimumSize(new Dimension(820, 540));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- [JMenuBar KONSISTEN ANTARHALAMAN] ---
        JMenuBar menuBar = new JMenuBar();
        JMenu menuNavigasi = new JMenu("Menu");
        itemMenuUtama = new JMenuItem("Menu Utama");
        itemPelanggan = new JMenuItem("Kelola Data Pelanggan");
        itemLogOut = new JMenuItem("Log Out");
        itemKeluar = new JMenuItem("Keluar Aplikasi");

        menuNavigasi.add(itemMenuUtama);
        menuNavigasi.add(itemPelanggan);
        menuNavigasi.addSeparator();
        menuNavigasi.add(itemLogOut);
        menuNavigasi.add(itemKeluar);

        JMenu menuBantuan = new JMenu("Bantuan");
        itemTentang = new JMenuItem("Tentang CleanWashApp");
        menuBantuan.add(itemTentang);

        menuBar.add(menuNavigasi);
        menuBar.add(menuBantuan);
        setJMenuBar(menuBar);

        // --- PANEL UTARA (Form 2 Kolom GridBagLayout + Tombol) ---

        JPanel panelUtara = new JPanel(new BorderLayout());

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Form Input Transaksi Laundry"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // --- KOLOM KIRI FORM ---
        // Baris 0: ID Transaksi
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("ID Transaksi:"), gbc);
        gbc.gridx = 1;
        txtIdTransaksi = new JTextField("(Otomatis)", 15);
        txtIdTransaksi.setEditable(false);
        panelForm.add(txtIdTransaksi, gbc);

        // Baris 1: Pelanggan (JComboBox<Pelanggan> - Relasi Objek)
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Pelanggan:"), gbc);
        gbc.gridx = 1;
        cbPelanggan = new JComboBox<>();
        cbPelanggan.setPreferredSize(new Dimension(210, 25));
        panelForm.add(cbPelanggan, gbc);

        // Baris 2: Tipe Layanan (JComboBox<String>)
        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Tipe Layanan:"), gbc);
        gbc.gridx = 1;
        cbTipe = new JComboBox<>(new String[]{"Kiloan", "Satuan"});
        cbTipe.setPreferredSize(new Dimension(150, 25));
        panelForm.add(cbTipe, gbc);

        // Baris 3: Pilihan Paket (JRadioButton + ButtonGroup)
        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Pilihan Paket:"), gbc);
        gbc.gridx = 1;
        JPanel panelRadio = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        rbReguler = new JRadioButton("Reguler", true);
        rbExpress = new JRadioButton("Express");
        grupPaket = new ButtonGroup();
        grupPaket.add(rbReguler);
        grupPaket.add(rbExpress);
        panelRadio.add(rbReguler);
        panelRadio.add(rbExpress);
        panelForm.add(panelRadio, gbc);

        // --- KOLOM KANAN FORM ---
        // Baris 0: Berat / Jumlah (JSpinner)
        gbc.gridx = 2; gbc.gridy = 0;
        panelForm.add(new JLabel("Berat (kg) / Jumlah:"), gbc);
        gbc.gridx = 3;
        spnKuantitas = new JSpinner(new SpinnerNumberModel(1.0, 0.5, 100.0, 0.5));
        spnKuantitas.setPreferredSize(new Dimension(100, 25));
        panelForm.add(spnKuantitas, gbc);

        // Baris 1: Diskon % (JSpinner - memicu Method Overloading hitungBiaya)
        gbc.gridx = 2; gbc.gridy = 1;
        panelForm.add(new JLabel("Diskon (%):"), gbc);
        gbc.gridx = 3;
        spnDiskon = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 5.0));
        spnDiskon.setPreferredSize(new Dimension(100, 25));
        panelForm.add(spnDiskon, gbc);

        // Baris 2: Layanan Tambahan (JCheckBox)
        gbc.gridx = 2; gbc.gridy = 2;
        panelForm.add(new JLabel("Layanan Tambahan:"), gbc);
        gbc.gridx = 3;
        chkAntarJemput = new JCheckBox("Antar-Jemput (+Rp 5.000)");
        panelForm.add(chkAntarJemput, gbc);

        // Baris 3: Estimasi Biaya Real-Time
        gbc.gridx = 2; gbc.gridy = 3;
        panelForm.add(new JLabel("Estimasi Biaya:"), gbc);
        gbc.gridx = 3;
        lblEstimasiBiaya = new JLabel("Rp 6.000");
        lblEstimasiBiaya.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblEstimasiBiaya.setForeground(new Color(0, 102, 153));
        panelForm.add(lblEstimasiBiaya, gbc);

        // Spacer kanan agar layout tetap ringkas dan tidak melar saat di-resize
        gbc.gridx = 4; gbc.gridy = 0; gbc.weightx = 1.0;
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

        // --- PANEL TENGAH (Pencarian + JTable Read-Only) ---

        JPanel panelTengah = new JPanel(new BorderLayout(5, 5));
        panelTengah.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel panelCari = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelCari.add(new JLabel("Cari Transaksi (ID/Nama/Tipe/Paket):"));
        txtCari = new JTextField(24);
        panelCari.add(txtCari);
        panelTengah.add(panelCari, BorderLayout.NORTH);

        String[] kolom = {
            "ID", "Pelanggan", "Tipe", "Paket", "Berat/Jml",
            "Antar-Jemput", "Diskon", "Total Biaya", "Estimasi"
        };
        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelTransaksi = new JTable(modelTabel);
        tabelTransaksi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelTengah.add(new JScrollPane(tabelTransaksi), BorderLayout.CENTER);

        add(panelTengah, BorderLayout.CENTER);
    }

    /**
     * [EVENT HANDLING TERPISAH]
     * Seluruh listener memanggil method khusus tanpa menumpuk logika di dalam lambda.
     */
    private void pasangEvent() {
        // Pembaruan estimasi biaya real-time saat komponen berubah
        cbTipe.addActionListener(e -> perbaruiEstimasi());
        rbReguler.addActionListener(e -> perbaruiEstimasi());
        rbExpress.addActionListener(e -> perbaruiEstimasi());
        spnKuantitas.addChangeListener(e -> perbaruiEstimasi());
        spnDiskon.addChangeListener(e -> perbaruiEstimasi());
        chkAntarJemput.addActionListener(e -> perbaruiEstimasi());

        // Tombol CRUD
        btnTambah.addActionListener(e -> tambahTransaksi());
        btnUbah.addActionListener(e -> ubahTransaksi());
        btnHapus.addActionListener(e -> hapusTransaksi());
        btnBersihkan.addActionListener(e -> bersihkanForm());
        btnKembali.addActionListener(e -> bukaMenuUtama());

        // Seleksi baris tabel
        tabelTransaksi.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariTabel();
            }
        });

        // Pencarian real-time
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
        itemPelanggan.addActionListener(e -> bukaFormPelanggan());
        itemLogOut.addActionListener(e -> prosesLogOut());
        itemKeluar.addActionListener(e -> prosesKeluar());
        itemTentang.addActionListener(e -> tampilkanTentang());
    }

    private void muatComboPelanggan() {
        cbPelanggan.removeAllItems();
        for (Pelanggan p : controller.getDaftarPelanggan()) {
            cbPelanggan.addItem(p);
        }
    }

    private String getPaketTerpilih() {
        return rbExpress.isSelected() ? "Express" : "Reguler";
    }

    private String formatRupiah(double nominal) {
        DecimalFormat formatter = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.forLanguageTag("id-ID")));
        return "Rp " + formatter.format(nominal);
    }

    private void perbaruiEstimasi() {
        try {
            String tipe = cbTipe.getSelectedItem().toString();
            double kuantitas = ((Number) spnKuantitas.getValue()).doubleValue();
            double diskon = ((Number) spnDiskon.getValue()).doubleValue();
            boolean antarJemput = chkAntarJemput.isSelected();

            double estimasi = controller.hitungEstimasiBiaya(
                tipe, kuantitas, getPaketTerpilih(), antarJemput, diskon
            );
            lblEstimasiBiaya.setText(formatRupiah(estimasi));
        } catch (CucianInvalidException ex) {
            lblEstimasiBiaya.setText("Rp 0");
        }
    }

    // [METHOD CRUD - READ]
    private void muatTabel(List<Transaksi> daftar) {
        modelTabel.setRowCount(0);
        for (Transaksi t : daftar) {
            double qty = t.getLayanan().getKuantitas();
            Object tampilQty = (qty == (int) qty) ? (int) qty : qty;

            double disc = t.getDiskonPersen();
            String tampilDisc = (disc == (int) disc) ? ((int) disc + "%") : (disc + "%");

            modelTabel.addRow(new Object[]{
                t.getIdTransaksi(),
                t.getPelanggan().getNama(),
                t.getLayanan().getTipeLayanan(),
                t.getLayanan().getJenisPaket(),
                tampilQty,
                t.isAntarJemput() ? "Ya" : "Tidak",
                tampilDisc,
                formatRupiah(t.getTotalBiaya()),
                t.getEstimasiSelesai()
            });
        }
    }

    private void isiFormDariTabel() {
        int baris = tabelTransaksi.getSelectedRow();
        if (baris >= 0) {
            String idTrx = modelTabel.getValueAt(baris, 0).toString();
            Transaksi trx = controller.cariTransaksiById(idTrx);
            if (trx != null) {
                txtIdTransaksi.setText(trx.getIdTransaksi());
                pilihPelangganDiCombo(trx.getPelanggan().getIdPelanggan());
                cbTipe.setSelectedItem(trx.getLayanan().getTipeLayanan());
                if (trx.getLayanan().getJenisPaket().equals("Express")) {
                    rbExpress.setSelected(true);
                } else {
                    rbReguler.setSelected(true);
                }
                spnKuantitas.setValue(trx.getLayanan().getKuantitas());
                spnDiskon.setValue(trx.getDiskonPersen());
                chkAntarJemput.setSelected(trx.isAntarJemput());
                perbaruiEstimasi();
            }
        }
    }

    private void pilihPelangganDiCombo(String idPelanggan) {
        for (int i = 0; i < cbPelanggan.getItemCount(); i++) {
            Pelanggan p = cbPelanggan.getItemAt(i);
            if (p.getIdPelanggan().equals(idPelanggan)) {
                cbPelanggan.setSelectedIndex(i);
                return;
            }
        }
    }

    // [METHOD CRUD - CREATE]
    private void tambahTransaksi() {
        Pelanggan pelanggan = (Pelanggan) cbPelanggan.getSelectedItem();
        if (pelanggan == null) {
            JOptionPane.showMessageDialog(this,
                "Belum ada data pelanggan! Tambahkan pelanggan dulu di menu Kelola Data Pelanggan.",
                "Pelanggan Kosong", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String tipe = cbTipe.getSelectedItem().toString();
            double kuantitas = ((Number) spnKuantitas.getValue()).doubleValue();
            double diskon = ((Number) spnDiskon.getValue()).doubleValue();
            boolean antarJemput = chkAntarJemput.isSelected();

            controller.tambahTransaksi(pelanggan, tipe, kuantitas, getPaketTerpilih(), antarJemput, diskon);
            muatTabel(controller.getDaftarTransaksi());
            bersihkanForm();
            JOptionPane.showMessageDialog(this, "Transaksi berhasil ditambahkan & disimpan permanen!");
        } catch (CucianInvalidException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan file: " + ex.getMessage(), "Error File", JOptionPane.ERROR_MESSAGE);
        }
    }

    // [METHOD CRUD - UPDATE]
    private void ubahTransaksi() {
        int baris = tabelTransaksi.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris transaksi pada tabel yang ingin diubah!");
            return;
        }
        Pelanggan pelanggan = (Pelanggan) cbPelanggan.getSelectedItem();
        if (pelanggan == null) {
            JOptionPane.showMessageDialog(this, "Data pelanggan tidak tersedia!");
            return;
        }

        try {
            String id = txtIdTransaksi.getText();
            String tipe = cbTipe.getSelectedItem().toString();
            double kuantitas = ((Number) spnKuantitas.getValue()).doubleValue();
            double diskon = ((Number) spnDiskon.getValue()).doubleValue();
            boolean antarJemput = chkAntarJemput.isSelected();

            controller.ubahTransaksi(id, pelanggan, tipe, kuantitas, getPaketTerpilih(), antarJemput, diskon);
            muatTabel(controller.getDaftarTransaksi());
            bersihkanForm();
            JOptionPane.showMessageDialog(this, "Transaksi berhasil diperbarui!");
        } catch (CucianInvalidException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui file: " + ex.getMessage(), "Error File", JOptionPane.ERROR_MESSAGE);
        }
    }

    // [METHOD CRUD - DELETE dengan Konfirmasi]
    private void hapusTransaksi() {
        int baris = tabelTransaksi.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris transaksi yang ingin dihapus!");
            return;
        }
        String id = modelTabel.getValueAt(baris, 0).toString();
        String nama = modelTabel.getValueAt(baris, 1).toString();
        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus transaksi " + id + " milik " + nama + "?",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            try {
                controller.hapusTransaksi(id);
                muatTabel(controller.getDaftarTransaksi());
                bersihkanForm();
                JOptionPane.showMessageDialog(this, "Transaksi berhasil dihapus!");
            } catch (CucianInvalidException | IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Hapus", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cariData() {
        muatTabel(controller.cariTransaksi(txtCari.getText()));
    }

    private void bersihkanForm() {
        txtIdTransaksi.setText("(Otomatis)");
        if (cbPelanggan.getItemCount() > 0) {
            cbPelanggan.setSelectedIndex(0);
        }
        cbTipe.setSelectedIndex(0);
        rbReguler.setSelected(true);
        spnKuantitas.setValue(1.0);
        spnDiskon.setValue(0.0);
        chkAntarJemput.setSelected(false);
        tabelTransaksi.clearSelection();
        perbaruiEstimasi();
    }

    private void bukaMenuUtama() {
        new MenuUtama(controller).setVisible(true);
        dispose();
    }

    private void bukaFormPelanggan() {
        new FormPelanggan(controller).setVisible(true);
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