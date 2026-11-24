package laundry.gui;

import laundry.controller.LaundryController;
import javax.swing.*;
import java.awt.*;

/**
 * [MVC - VIEW / HALAMAN 1]
 * Halaman autentikasi petugas sebelum masuk ke Menu Utama CleanWashApp.
 * Menggunakan kombinasi BorderLayout, GridBagLayout, dan FlowLayout (tanpa Absolute Layout).
 */
public class HalamanLogin extends JFrame {

    // [ATRIBUT KOMPONEN GUI & CONTROLLER]
    private final LaundryController controller;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnReset;

    // [CONSTRUCTOR]
    public HalamanLogin() {
        this.controller = new LaundryController();
        inisialisasiGUI();
        pasangEvent();
    }

    /**
     * [PENYUSUNAN ANTARMUKA & LAYOUT MANAGER]
     */
    private void inisialisasiGUI() {
        setTitle("Login - CleanWashApp");
        setSize(420, 270);
        setMinimumSize(new Dimension(380, 250));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- HEADER (NORTH) ---
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        JLabel lblJudul = new JLabel("LOGIN CLEANWASHAPP");
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 16));
        panelHeader.add(lblJudul);
        add(panelHeader, BorderLayout.NORTH);

        // --- FORM INPUT (CENTER dengan GridBagLayout agar tetap ringkas saat di-resize) ---
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createEmptyBorder(5, 25, 5, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        // Baris 1: Username
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        txtUsername = new JTextField(16);
        panelForm.add(txtUsername, gbc);

        // Baris 2: Password
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtPassword = new JPasswordField(16);
        panelForm.add(txtPassword, gbc);

        // Baris 3: Informasi akun default untuk demo
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel lblInfo = new JLabel("Akun default: admin / 12345");
        lblInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblInfo.setForeground(Color.GRAY);
        panelForm.add(lblInfo, gbc);

        add(panelForm, BorderLayout.CENTER);

        // --- TOMBOL AKSI (SOUTH dengan FlowLayout) ---
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        btnLogin = new JButton("Masuk");
        btnReset = new JButton("Bersihkan");
        panelTombol.add(btnLogin);
        panelTombol.add(btnReset);
        add(panelTombol, BorderLayout.SOUTH);
    }

    /**
     * [EVENT HANDLING TERPISAH]
     * Setiap listener hanya memanggil method aksi khusus.
     */
    private void pasangEvent() {
        btnLogin.addActionListener(e -> prosesLogin());
        txtPassword.addActionListener(e -> prosesLogin()); // Bisa tekan Enter langsung
        btnReset.addActionListener(e -> bersihkanForm());
    }

    // [METHOD AKSI - AUTENTIKASI VIA CONTROLLER]
    private void prosesLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username dan Password tidak boleh kosong!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (controller.validasiLogin(username, password)) {
            JOptionPane.showMessageDialog(this,
                "Login berhasil! Selamat datang di CleanWashApp.",
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
            new MenuUtama(controller).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Username atau Password salah!",
                "Login Gagal", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    private void bersihkanForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.requestFocus();
    }

    /**
     * [MAIN METHOD] Titik awal eksekusi aplikasi CleanWashApp.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HalamanLogin().setVisible(true));
    }
}