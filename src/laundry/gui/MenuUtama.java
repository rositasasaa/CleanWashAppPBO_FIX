package laundry.gui;

import laundry.controller.LaundryController;
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * [MVC - VIEW / HALAMAN 2]
 * Halaman Menu Utama / Dashboard yang menampilkan ringkasan statistik data
 * serta navigasi menuju halaman Kelola Pelanggan dan Kelola Transaksi.
 */
public class MenuUtama extends JFrame {

    // [ATRIBUT]
    private final LaundryController controller;
    private JLabel lblTotalPelanggan, lblTotalTransaksi, lblTotalPendapatan;
    private JButton btnPelanggan, btnTransaksi, btnMuatUlang, btnLogOut;
    private JMenuItem itemPelanggan, itemTransaksi, itemLogOut, itemKeluar, itemTentang;

    // [CONSTRUCTOR] Menerima objek LaundryController agar data antarhalaman tersinkronisasi
    public MenuUtama(LaundryController controller) {
        this.controller = controller;
        inisialisasiGUI();
        pasangEvent();
        perbaruiRingkasan();
    }

    private void inisialisasiGUI() {
        setTitle("Menu Utama - CleanWashApp");
        setSize(700, 400);
        setMinimumSize(new Dimension(600, 350));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- [JMenuBar KONSISTEN ANTARHALAMAN] ---
        JMenuBar menuBar = new JMenuBar();

        JMenu menuNavigasi = new JMenu("Menu");
        itemPelanggan = new JMenuItem("Kelola Data Pelanggan");
        itemTransaksi = new JMenuItem("Kelola Transaksi Laundry");
        itemLogOut = new JMenuItem("Log Out");
        itemKeluar = new JMenuItem("Keluar Aplikasi");

        menuNavigasi.add(itemPelanggan);
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

        // --- HEADER (NORTH) ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        JLabel lblJudul = new JLabel("Dashboard CleanWashApp");
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 20));
        JLabel lblSub = new JLabel("Sistem Manajemen Pelanggan dan Transaksi Laundry");
        lblSub.setForeground(Color.DARK_GRAY);

        panelHeader.add(lblJudul, BorderLayout.NORTH);
        panelHeader.add(lblSub, BorderLayout.SOUTH);
        add(panelHeader, BorderLayout.NORTH);

        // --- PANEL KARTU STATISTIK (CENTER dengan GridLayout 1x3) ---
        JPanel panelStatistik = new JPanel(new GridLayout(1, 3, 15, 15));
        panelStatistik.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        lblTotalPelanggan = new JLabel("0", SwingConstants.CENTER);
        lblTotalTransaksi = new JLabel("0", SwingConstants.CENTER);
        lblTotalPendapatan = new JLabel("Rp 0", SwingConstants.CENTER);

        panelStatistik.add(buatKartu("Total Pelanggan", lblTotalPelanggan));
        panelStatistik.add(buatKartu("Total Transaksi", lblTotalTransaksi));
        panelStatistik.add(buatKartu("Total Pendapatan", lblTotalPendapatan));
        add(panelStatistik, BorderLayout.CENTER);

        // --- PANEL TOMBOL NAVIGASI (SOUTH dengan FlowLayout) ---
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 20));
        btnPelanggan = new JButton("Kelola Pelanggan");
        btnTransaksi = new JButton("Kelola Transaksi");
        btnMuatUlang = new JButton("Muat Ulang");
        btnLogOut = new JButton("Log Out");

        panelTombol.add(btnPelanggan);
        panelTombol.add(btnTransaksi);
        panelTombol.add(btnMuatUlang);
        panelTombol.add(btnLogOut);
        add(panelTombol, BorderLayout.SOUTH);
    }

    /**
     * [METHOD BANTUAN GUI] Membuat panel kartu statistik yang seragam.
     */
    private JPanel buatKartu(String judul, JLabel labelNilai) {
        JPanel kartu = new JPanel(new BorderLayout(5, 10));
        kartu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));

        JLabel lblJudulKartu = new JLabel(judul, SwingConstants.CENTER);
        lblJudulKartu.setFont(new Font("SansSerif", Font.PLAIN, 13));

        labelNilai.setFont(new Font("SansSerif", Font.BOLD, 20));

        kartu.add(lblJudulKartu, BorderLayout.NORTH);
        kartu.add(labelNilai, BorderLayout.CENTER);
        return kartu;
    }

    // [EVENT HANDLING TERPISAH]
    private void pasangEvent() {
        btnPelanggan.addActionListener(e -> bukaFormPelanggan());
        itemPelanggan.addActionListener(e -> bukaFormPelanggan());

        btnTransaksi.addActionListener(e -> bukaFormTransaksi());
        itemTransaksi.addActionListener(e -> bukaFormTransaksi());

        btnMuatUlang.addActionListener(e -> muatUlangData());

        btnLogOut.addActionListener(e -> prosesLogOut());
        itemLogOut.addActionListener(e -> prosesLogOut());

        itemKeluar.addActionListener(e -> prosesKeluar());
        itemTentang.addActionListener(e -> tampilkanTentang());
    }

    private void perbaruiRingkasan() {
        DecimalFormat formatter = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.forLanguageTag("id-ID")));
        lblTotalPelanggan.setText(String.valueOf(controller.getTotalPelanggan()));
        lblTotalTransaksi.setText(String.valueOf(controller.getTotalTransaksi()));
        lblTotalPendapatan.setText("Rp " + formatter.format(controller.getTotalPendapatan()));
    }

    private void muatUlangData() {
        controller.muatSemuaData();
        perbaruiRingkasan();
        JOptionPane.showMessageDialog(this, "Ringkasan data berhasil diperbarui!");
    }

    private void bukaFormPelanggan() {
        new FormPelanggan(controller).setVisible(true);
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
            "CleanWashApp v2.0 (Arsitektur MVC)\n" +
            "Dikembangkan oleh:\n" +
            "- Yunita Dwi Ardilasari\n" +
            "- Greycilfa\n" +
            "- Rosita Salsabila",
            "Tentang CleanWashApp", JOptionPane.INFORMATION_MESSAGE);
    }
}
