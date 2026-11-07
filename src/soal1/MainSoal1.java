package soal1;

/**
 * Program demo Soal 1 (LO1):
 *   1a  polymorphism lewat interface Kendaraan, pemeriksaan kontrak, dan alternatif enum
 *   1b  aggregation vs composition pada Trip dan dampaknya ke lifecycle objek
 *   1c  menambah SepedaListrik tanpa mengubah Trip
 */
public class MainSoal1 {

    private static final double[] DAFTAR_JARAK = {1, 3, 5, 8, 12, 20};

    /** Pemeriksaan kontrak memakai titik 0..30 km dengan langkah 0,01 km (3.001 titik). */
    private static final int LANGKAH_KONTRAK = 3_000;

    public static void main(String[] args) {
        bagian1aPolymorphism();
        bagian1aKontrak();
        bagian1aEnum();
        bagian1bTrip();
        bagian1cKendaraanBaru();
    }

    // ------------------------------------------------------------------
    // 1a. Polymorphism
    // ------------------------------------------------------------------
    private static void bagian1aPolymorphism() {
        judul("1a. POLYMORPHISM: satu pemanggilan hitungTarif(), perilaku berbeda");

        // Tipe variabel = interface Kendaraan; tipe objek sebenarnya berbeda-beda.
        Kendaraan[] armada = {
            new Motor("B 3456 KJT"),
            new Mobil("B 1234 GCP"),
            new MobilListrik("B 2024 EVX")
        };

        StringBuilder header = new StringBuilder(kiri("Jarak", 10));
        for (Kendaraan k : armada) {
            header.append(kanan(k.getJenis(), 16));
        }
        System.out.println(header);
        System.out.println(garis('-', 10 + 16 * armada.length));

        for (double jarak : DAFTAR_JARAK) {
            StringBuilder baris = new StringBuilder(kiri(FormatAngka.km(jarak), 10));
            for (Kendaraan k : armada) {
                // Dynamic dispatch: JVM memilih hitungTarif milik Motor/Mobil/MobilListrik saat runtime.
                baris.append(kanan(FormatAngka.rupiah(k.hitungTarif(jarak)), 16));
            }
            System.out.println(baris);
        }

        // Titik impas: dasarMobil + x * perKmMobil = dasarListrik + x * perKmListrik
        double impas = (MobilListrik.TARIF_DASAR - Mobil.TARIF_DASAR)
                / (Mobil.TARIF_PER_KM - MobilListrik.TARIF_PER_KM);
        System.out.println();
        System.out.println("Titik impas Mobil vs Mobil Listrik: " + FormatAngka.km(impas)
                + " (di bawahnya Mobil lebih murah, di atasnya Mobil Listrik lebih murah)");
    }

    // ------------------------------------------------------------------
    // 1a. Kontrak hitungTarif diperiksa pada setiap kelas
    // ------------------------------------------------------------------
    private static void bagian1aKontrak() {
        judul("1a. KONTRAK hitungTarif: diperiksa pada setiap kelas");
        System.out.println("Syarat: jarak -1, NaN, dan Infinity ditolak; pada 3.001 titik 0-30 km"
                + " (langkah 0,01 km) tarif >= 0 dan tidak turun");

        Kendaraan[] armada = {new Motor("B 1 AAA"), new Mobil("B 2 BBB"), new MobilListrik("B 3 CCC")};
        for (Kendaraan k : armada) {
            System.out.println("  " + kiri(k.getJenis(), 16) + cekKontrak(k));
        }
    }

    /**
     * Memeriksa kontrak Kendaraan.hitungTarif pada satu objek:
     *  (1) jarak -1, NaN, dan tak hingga harus ditolak dengan IllegalArgumentException;
     *  (2) pada titik 0..30 km (langkah 0,01 km) tarif harus >= 0 dan tidak turun.
     * Mengembalikan "LOLOS" atau pelanggaran pertama yang ditemukan.
     * Ini pemeriksaan sampel, bukan bukti untuk semua bilangan real.
     */
    private static String cekKontrak(Kendaraan k) {
        double[] jarakTidakValid = {-1, Double.NaN, Double.POSITIVE_INFINITY};
        for (double jarak : jarakTidakValid) {
            try {
                k.hitungTarif(jarak);
                return "GAGAL: jarak " + jarak + " diterima";
            } catch (IllegalArgumentException e) {
                // sesuai kontrak: jarak tidak valid ditolak
            }
        }

        double tarifSebelumnya = 0;
        for (int i = 0; i <= LANGKAH_KONTRAK; i++) {
            double jarak = i / 100.0;
            double tarif = k.hitungTarif(jarak);
            if (tarif < 0) {
                return "GAGAL: tarif negatif pada " + jarak + " km";
            }
            if (i > 0 && tarif < tarifSebelumnya) {
                return "GAGAL: tarif turun pada " + jarak + " km";
            }
            tarifSebelumnya = tarif;
        }
        return "LOLOS";
    }

    // ------------------------------------------------------------------
    // 1a(i). Alternatif enum
    // ------------------------------------------------------------------
    private static void bagian1aEnum() {
        judul("1a(i). ALTERNATIF: enum JenisKendaraan dengan constant-specific method");

        Kendaraan[] versiKelas = {new Motor("B 1 AAA"), new Mobil("B 2 BBB"), new MobilListrik("B 3 CCC")};
        JenisKendaraan[] versiEnum = JenisKendaraan.values();   // dibuat otomatis oleh compiler

        StringBuilder header = new StringBuilder(kiri("Konstanta", 15));
        for (double jarak : DAFTAR_JARAK) {
            header.append(kanan(FormatAngka.km(jarak), 10));
        }
        System.out.println(header);
        System.out.println(garis('-', 15 + 10 * DAFTAR_JARAK.length));

        int identik = 0;
        int total = 0;
        for (int i = 0; i < versiEnum.length; i++) {
            StringBuilder baris = new StringBuilder(kiri(versiEnum[i].name(), 15));
            for (double jarak : DAFTAR_JARAK) {
                double tarifEnum = versiEnum[i].hitungTarif(jarak);
                double tarifKelas = versiKelas[i].hitungTarif(jarak);
                if (Math.abs(tarifEnum - tarifKelas) < 1e-9) {
                    identik++;
                }
                total++;
                baris.append(kanan(FormatAngka.rupiah(tarifEnum), 10));
            }
            System.out.println(baris);
        }
        System.out.println();
        System.out.println("Cek konsistensi enum vs hierarki class: " + identik + "/" + total + " tarif identik");

        // Kelebihan enum: mengubah teks menjadi konstanta (valueOf) dan perbandingan dengan ==
        JenisKendaraan dariTeks = JenisKendaraan.valueOf("MOBIL_LISTRIK");
        System.out.println("valueOf(\"MOBIL_LISTRIK\") == MOBIL_LISTRIK: "
                + (dariTeks == JenisKendaraan.MOBIL_LISTRIK) + " (singleton, cukup dibandingkan dengan ==)");

        // Keterbatasan enum: himpunannya tertutup
        try {
            JenisKendaraan.valueOf("SEPEDA_LISTRIK");
        } catch (IllegalArgumentException e) {
            System.out.println("valueOf(\"SEPEDA_LISTRIK\") gagal -> jenis baru WAJIB mengubah source enum"
                    + " lalu kompilasi ulang");
        }
    }

    // ------------------------------------------------------------------
    // 1b. Aggregation vs composition dan lifecycle objek
    // ------------------------------------------------------------------
    private static void bagian1bTrip() {
        judul("1b. TRIP: aggregation (Driver, Penumpang, Kendaraan) vs composition (RincianTarif)");

        // Driver dan penumpang sudah terdaftar SEBELUM ada trip.
        Driver budi = new Driver("DRV-001", "Budi", new Motor("B 3456 KJT"));
        Penumpang ani = new Penumpang("PNP-101", "Ani");
        Penumpang citra = new Penumpang("PNP-102", "Citra");
        System.out.println("[1] Sebelum ada trip: " + budi + ", tersedia=" + budi.isTersedia());

        Trip trip1 = new Trip("TRP-0001", budi, ani, 6.5);
        System.out.println("[2] Trip dibuat:");
        System.out.println("    " + trip1);
        System.out.println("    Budi sedang dipinjam trip1 -> tersedia=" + budi.isTersedia());

        try {
            new Trip("TRP-0002", budi, citra, 3.0);
        } catch (IllegalStateException e) {
            System.out.println("[3] Order kedua untuk Budi ditolak: " + e.getMessage());
        }

        trip1.selesaikan();
        System.out.println("[4] trip1 " + trip1.getStatus() + " -> Budi dikembalikan, tersedia="
                + budi.isTersedia() + ", trip selesai=" + budi.getJumlahTripSelesai());

        trip1 = null; // trip1 tidak dirujuk lagi: ia dan RincianTarif-nya boleh di-GC,
                      // tetapi Driver tetap hidup karena masih dirujuk dari luar (variabel budi).
        System.out.println("[5] trip1 dibuang, Driver tetap ada: " + budi);

        Trip trip2 = new Trip("TRP-0003", budi, citra, 3.0);
        System.out.println("[6] Driver yang SAMA melayani trip baru:");
        System.out.println("    " + trip2);
        trip2.selesaikan();

        budi.gantiKendaraan(new MobilListrik("B 2024 EVX"));
        System.out.println("[7] Budi ganti kendaraan -> " + budi.getKendaraan());
        System.out.println("    Riwayat trip2 tetap mencatat kendaraan saat itu: " + trip2.getKendaraan()
                + ", total " + FormatAngka.rupiah(trip2.getRincianTarif().getTotal()));
        System.out.println("    Total trip selesai Budi: " + budi.getJumlahTripSelesai());
    }

    // ------------------------------------------------------------------
    // 1c. Jenis kendaraan baru tanpa mengubah Trip
    // ------------------------------------------------------------------
    private static void bagian1cKendaraanBaru() {
        judul("1c. EKSTENSIBILITAS: SepedaListrik ditambahkan TANPA mengubah Trip");

        Driver eko = new Driver("DRV-004", "Eko", new SepedaListrik("SL-0007"));
        Trip trip = new Trip("TRP-0004", eko, new Penumpang("PNP-103", "Dina"), 2.5);
        System.out.println(trip);
        trip.selesaikan();

        Kendaraan[] semuaJenis = {
            new Motor("B 1 AAA"), new Mobil("B 2 BBB"),
            new MobilListrik("B 3 CCC"), new SepedaListrik("SL-0001")
        };
        System.out.println();
        System.out.println("Kode pemanggil yang SAMA untuk semua jenis (tarif 4 km):");
        for (Kendaraan k : semuaJenis) {
            System.out.println("  " + kiri(k.getJenis(), 16) + FormatAngka.rupiah(k.hitungTarif(4)));
        }

        System.out.println();
        System.out.println("Kontrak seragam (Liskov): pemeriksaan yang sama dengan bagian 1a, untuk SEMUA jenis");
        for (Kendaraan k : semuaJenis) {
            System.out.println("  " + kiri(k.getJenis(), 16) + cekKontrak(k));
        }
    }

    // ------------------------------------------------------------------
    // Utilitas tampilan
    // ------------------------------------------------------------------
    private static void judul(String teks) {
        System.out.println();
        System.out.println(garis('=', 88));
        System.out.println(teks);
        System.out.println(garis('=', 88));
    }

    private static String garis(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    private static String kiri(String s, int lebar) {
        return String.format("%-" + lebar + "s", s);
    }

    private static String kanan(String s, int lebar) {
        return String.format("%" + lebar + "s", s);
    }
}
