package laundry.controller;

import laundry.model.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * [POLA ARSITEKTUR MVC - CONTROLLER]
 * Penghubung antara lapisan View (GUI Swing) dengan lapisan Model dan FileStorage.
 * Mengelola seluruh data secara dinamis menggunakan Collection & Generics (List dan Map).
 */
public class LaundryController {
    // [COLLECTION & GENERICS] Penyimpanan objek secara dinamis di memori
    private final List<Pelanggan> daftarPelanggan = new ArrayList<>();
    private final Map<String, Pelanggan> petaPelanggan = new LinkedHashMap<>();
    private final List<Transaksi> daftarTransaksi = new ArrayList<>();

    // [CONSTRUCTOR] Memuat data permanen dari file saat controller dibuat
    public LaundryController() {
        muatSemuaData();
    }

    /**
     * [AUTENTIKASI] Memeriksa kredensial login petugas.
     */
    public boolean validasiLogin(String username, String password) {
        return username.equals("admin") && password.equals("12345");
    }

    /**
     * [SINKRONISASI DATA] Memuat data pelanggan dan transaksi dari file .txt.
     */
    public void muatSemuaData() {
        daftarPelanggan.clear();
        petaPelanggan.clear();
        daftarTransaksi.clear();

        try {
            List<Pelanggan> hasilPlg = FileStorage.muatSemuaPelanggan();
            for (Pelanggan p : hasilPlg) {
                daftarPelanggan.add(p);
                petaPelanggan.put(p.getIdPelanggan(), p);
            }

            List<Transaksi> hasilTrx = FileStorage.muatSemuaTransaksi(petaPelanggan);
            daftarTransaksi.addAll(hasilTrx);
        } catch (IOException e) {
            System.err.println("Gagal memuat data awal: " + e.getMessage());
        }
    }

    // CRUD ENTITAS 1: PELANGGAN

    public List<Pelanggan> getDaftarPelanggan() {
        return daftarPelanggan;
    }

    private String buatIdPelangganBaru() {
        int max = 0;
        for (Pelanggan p : daftarPelanggan) {
            try {
                int angka = Integer.parseInt(p.getIdPelanggan().replace("PLG-", ""));
                if (angka > max) max = angka;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("PLG-%03d", max + 1);
    }

    public Pelanggan tambahPelanggan(String nama, String noHp, String alamat)
            throws CucianInvalidException, IOException {
        Pelanggan baru = new Pelanggan(buatIdPelangganBaru(), nama, noHp, alamat);
        daftarPelanggan.add(baru);
        petaPelanggan.put(baru.getIdPelanggan(), baru);
        FileStorage.simpanSemuaPelanggan(daftarPelanggan);
        return baru;
    }

    public void ubahPelanggan(String idPelanggan, String nama, String noHp, String alamat)
            throws CucianInvalidException, IOException {
        Pelanggan target = petaPelanggan.get(idPelanggan);
        if (target == null) {
            throw new CucianInvalidException("Data pelanggan tidak ditemukan!");
        }
        target.setNama(nama);
        target.setNoHp(noHp);
        target.setAlamat(alamat);
        FileStorage.simpanSemuaPelanggan(daftarPelanggan);
        // Perbarui juga file transaksi agar perubahan nama pelanggan tersinkronisasi
        FileStorage.simpanSemuaTransaksi(daftarTransaksi);
    }

    public void hapusPelanggan(String idPelanggan) throws CucianInvalidException, IOException {
        Pelanggan target = petaPelanggan.remove(idPelanggan);
        if (target == null) {
            throw new CucianInvalidException("Data pelanggan tidak ditemukan!");
        }
        daftarPelanggan.remove(target);
        FileStorage.simpanSemuaPelanggan(daftarPelanggan);
    }

    /**
     * [FITUR PENCARIAN] Menyaring daftar pelanggan berdasarkan ID, nama, HP, atau alamat.
     */
    public List<Pelanggan> cariPelanggan(String kataKunci) {
        if (kataKunci == null || kataKunci.trim().isEmpty()) {
            return new ArrayList<>(daftarPelanggan);
        }
        String key = kataKunci.trim().toLowerCase();
        List<Pelanggan> hasil = new ArrayList<>();
        for (Pelanggan p : daftarPelanggan) {
            if (p.getIdPelanggan().toLowerCase().contains(key) ||
                p.getNama().toLowerCase().contains(key) ||
                p.getNoHp().toLowerCase().contains(key) ||
                p.getAlamat().toLowerCase().contains(key)) {
                hasil.add(p);
            }
        }
        return hasil;
    }

    // CRUD ENTITAS 2: TRANSAKSI LAUNDRY

    public List<Transaksi> getDaftarTransaksi() {
        return daftarTransaksi;
    }

    private String buatIdTransaksiBaru() {
        int max = 0;
        for (Transaksi t : daftarTransaksi) {
            try {
                int angka = Integer.parseInt(t.getIdTransaksi().replace("TRX-", ""));
                if (angka > max) max = angka;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("TRX-%03d", max + 1);
    }

    private Layanan buatObjekLayanan(String tipeLayanan, double kuantitas, String jenisPaket)
            throws CucianInvalidException {
        if (tipeLayanan.equals("Kiloan")) {
            return new LayananKiloan(kuantitas, jenisPaket);
        } else {
            return new LayananSatuan(kuantitas, jenisPaket);
        }
    }

    public double hitungEstimasiBiaya(String tipeLayanan, double kuantitas, String jenisPaket,
                                      boolean antarJemput, double diskonPersen) throws CucianInvalidException {
        Layanan layanan = buatObjekLayanan(tipeLayanan, kuantitas, jenisPaket);
        return layanan.hitungBiaya(diskonPersen, antarJemput);
    }

    public Transaksi tambahTransaksi(Pelanggan pelanggan, String tipeLayanan, double kuantitas,
                                     String jenisPaket, boolean antarJemput, double diskonPersen)
            throws CucianInvalidException, IOException {
        Layanan layanan = buatObjekLayanan(tipeLayanan, kuantitas, jenisPaket);
        Transaksi baru = new Transaksi(buatIdTransaksiBaru(), pelanggan, layanan, antarJemput, diskonPersen);
        daftarTransaksi.add(baru);
        FileStorage.simpanSemuaTransaksi(daftarTransaksi);
        return baru;
    }

    public void ubahTransaksi(String idTransaksi, Pelanggan pelanggan, String tipeLayanan,
                              double kuantitas, String jenisPaket, boolean antarJemput, double diskonPersen)
            throws CucianInvalidException, IOException {
        Transaksi target = cariTransaksiById(idTransaksi);
        if (target == null) {
            throw new CucianInvalidException("Data transaksi tidak ditemukan!");
        }
        Layanan layananBaru = buatObjekLayanan(tipeLayanan, kuantitas, jenisPaket);
        target.setPelanggan(pelanggan);
        target.setLayanan(layananBaru);
        target.setAntarJemput(antarJemput);
        target.setDiskonPersen(diskonPersen);
        target.hitungUlang();
        FileStorage.simpanSemuaTransaksi(daftarTransaksi);
    }

    public void hapusTransaksi(String idTransaksi) throws CucianInvalidException, IOException {
        Transaksi target = cariTransaksiById(idTransaksi);
        if (target == null) {
            throw new CucianInvalidException("Data transaksi tidak ditemukan!");
        }
        daftarTransaksi.remove(target);
        FileStorage.simpanSemuaTransaksi(daftarTransaksi);
    }

    public Transaksi cariTransaksiById(String idTransaksi) {
        for (Transaksi t : daftarTransaksi) {
            if (t.getIdTransaksi().equals(idTransaksi)) {
                return t;
            }
        }
        return null;
    }

    /**
     * [FITUR PENCARIAN] Menyaring daftar transaksi berdasarkan ID, nama pelanggan, atau tipe layanan.
     */
    public List<Transaksi> cariTransaksi(String kataKunci) {
        if (kataKunci == null || kataKunci.trim().isEmpty()) {
            return new ArrayList<>(daftarTransaksi);
        }
        String key = kataKunci.trim().toLowerCase();
        List<Transaksi> hasil = new ArrayList<>();
        for (Transaksi t : daftarTransaksi) {
            if (t.getIdTransaksi().toLowerCase().contains(key) ||
                t.getPelanggan().getNama().toLowerCase().contains(key) ||
                t.getLayanan().getTipeLayanan().toLowerCase().contains(key) ||
                t.getLayanan().getJenisPaket().toLowerCase().contains(key)) {
                hasil.add(t);
            }
        }
        return hasil;
    }

    // STATISTIK RINGKAS UNTUK MENU UTAMA

    public int getTotalPelanggan() {
        return daftarPelanggan.size();
    }

    public int getTotalTransaksi() {
        return daftarTransaksi.size();
    }

    public double getTotalPendapatan() {
        double total = 0;
        for (Transaksi t : daftarTransaksi) {
            total += t.getTotalBiaya();
        }
        return total;
    }
}
