package soal3;

/**
 * EKSPERIMEN PENDUKUNG Soal 2b dan 2c (bukan bagian wajib Soal 3b).
 *
 * Peta sintetis: grid 1000 x 1000 = 1.000.000 persimpangan. Setiap persimpangan
 * terhubung ke tetangga kanan dan bawah dengan peluang 0,875, sehingga
 * rata-rata derajat sekitar 3,5 (sparse, sesuai asumsi Soal 2b).
 *
 * Yang diukur:
 *  1. Memori adjacency list (GraphPetaKota) vs estimasi adjacency matrix.
 *  2. Waktu bfsCariTerdekat (versi Soal 3b) pada skala 1 juta simpul.
 *  3. Efek dua strategi optimasi Soal 2c: radius pencarian dibatasi dan
 *     array bantu dipakai ulang (tanpa alokasi O(V) per permintaan).
 *
 * Ini micro-benchmark sederhana dengan System.nanoTime (bukan JMH). Angka
 * absolut bergantung pada mesin; yang penting adalah perbandingan relatifnya.
 */
public class EksperimenSkalaBesar {

    private static final int SISI = 1000;
    private static final int V = SISI * SISI;
    private static final double PELUANG_JALAN = 0.875;
    private static final int JUMLAH_QUERY = 200;
    private static final int RADIUS_MAKS_HOP = 60;
    private static final int[] DAFTAR_JUMLAH_DRIVER = {100, 1_000, 10_000};

    private static long benih = 20261104L;   // seed tetap supaya hasil acak bisa diulang

    public static void main(String[] args) {
        System.out.println("JVM: " + System.getProperty("java.vm.name") + " " + System.getProperty("java.version")
                + ", prosesor logis = " + Runtime.getRuntime().availableProcessors()
                + ", heap maks = " + ukuran(Runtime.getRuntime().maxMemory()));

        judul("1. MEMBANGUN PETA 1.000.000 PERSIMPANGAN");
        long memoriSebelum = memoriTerpakai();
        long t0 = System.nanoTime();
        GraphPetaKota peta = bangunGrid();
        long t1 = System.nanoTime();
        long memoriSesudah = memoriTerpakai();
        long e = peta.getJumlahJalan();
        System.out.println("V = " + ribuan(V) + " simpul, E = " + ribuan(e) + " ruas jalan, rata-rata derajat = "
                + desimal(2.0 * e / V, 2));
        System.out.println("Waktu membangun graph = " + milidetik(t1 - t0));

        judul("2. MEMORI: ADJACENCY LIST vs ADJACENCY MATRIX (1 MB = 10^6 byte)");
        double sel = (double) V * V;
        System.out.println("Adjacency list, terukur di heap JVM         : " + ukuran(memoriSesudah - memoriSebelum));
        System.out.println("Adjacency list, estimasi layout JVM         : " + ukuran(estimasiMemoriList(peta))
                + " (header array 16 byte, referensi 4 byte, kelipatan 8)");
        System.out.println("Isi murni 2E x 4 byte (tanpa overhead)      : " + ukuran(2.0 * e * 4));
        System.out.println("Adjacency matrix, 1 bit per sel             : " + ukuran(sel / 8));
        System.out.println("Adjacency matrix, boolean[][] (1 byte/sel)  : " + ukuran(sel));
        System.out.println("Adjacency matrix, int[][] (4 byte/sel)      : " + ukuran(sel * 4));
        System.out.println("Kepadatan matrix (2E / V^2)                 : 1 dari " + ribuan(Math.round(sel / (2.0 * e)))
                + " sel berisi 1");

        judul("3. WAKTU bfsCariTerdekat (VERSI SOAL 3b) PADA 1 JUTA SIMPUL");
        int[] penumpang = new int[JUMLAH_QUERY];
        for (int i = 0; i < JUMLAH_QUERY; i++) {
            penumpang[i] = acakInt(V);
        }
        boolean[] tanpaDriver = new boolean[V];
        for (int i = 0; i < 3; i++) {
            peta.bfsCariTerdekatDetail(penumpang[i], tanpaDriver);   // pemanasan JIT
        }
        long total = 0;
        int diproses = 0;
        int ulang = 10;
        for (int i = 0; i < ulang; i++) {
            long mulai = System.nanoTime();
            HasilPencarian h = peta.bfsCariTerdekatDetail(penumpang[i], tanpaDriver);
            total += System.nanoTime() - mulai;
            diproses = h.getJumlahSimpulDiproses();
        }
        System.out.println("Kasus terburuk (tidak ada driver, BFS menjelajah seluruh komponen):");
        System.out.println("  rata-rata " + milidetik(total / ulang) + " per permintaan, " + ribuan(diproses)
                + " simpul diproses -> maks. ~" + ribuan(Math.round(1e9 / (total / (double) ulang)))
                + " permintaan/detik per inti CPU");

        judul("4. BFS STANDAR vs BFS TEROPTIMASI (radius <= " + RADIUS_MAKS_HOP
                + " hop + array dipakai ulang), " + JUMLAH_QUERY + " permintaan");
        System.out.println(kiri("Driver", 9) + kiri("| Metode", 22) + kanan("waktu/req", 11)
                + kanan("simpul/req", 12) + kanan("ditemukan", 11) + kanan("req/detik/inti", 16));
        System.out.println(garis('-', 81));
        BfsTerbatas cepat = new BfsTerbatas(peta);
        for (int jumlahDriver : DAFTAR_JUMLAH_DRIVER) {
            boolean[] adalahDriver = sebarDriver(jumlahDriver);

            for (int i = 0; i < 20; i++) {           // pemanasan JIT
                peta.bfsCariTerdekatDetail(penumpang[i], adalahDriver);
                cepat.cari(penumpang[i], adalahDriver, RADIUS_MAKS_HOP);
            }

            long waktuStandar = 0;
            long simpulStandar = 0;
            int ketemuStandar = 0;
            for (int q = 0; q < JUMLAH_QUERY; q++) {
                long mulai = System.nanoTime();
                HasilPencarian h = peta.bfsCariTerdekatDetail(penumpang[q], adalahDriver);
                waktuStandar += System.nanoTime() - mulai;
                simpulStandar += h.getJumlahSimpulDiproses();
                if (h.isDitemukan()) {
                    ketemuStandar++;
                }
            }

            long waktuCepat = 0;
            long simpulCepat = 0;
            int ketemuCepat = 0;
            for (int q = 0; q < JUMLAH_QUERY; q++) {
                long mulai = System.nanoTime();
                int d = cepat.cari(penumpang[q], adalahDriver, RADIUS_MAKS_HOP);
                waktuCepat += System.nanoTime() - mulai;
                simpulCepat += cepat.getSimpulDiprosesTerakhir();
                if (d != -1) {
                    ketemuCepat++;
                }
            }

            cetakBaris(ribuan(jumlahDriver), "| standar (Soal 3b)", waktuStandar, simpulStandar, ketemuStandar);
            cetakBaris("", "| radius + pakai ulang", waktuCepat, simpulCepat, ketemuCepat);
        }
        System.out.println();
        System.out.println("Catatan: 'ditemukan' < 100% pada versi teroptimasi berarti tidak ada driver dalam radius;");
        System.out.println("di sistem nyata permintaan itu diulang dengan radius lebih besar (fallback).");
    }

    // ------------------------------------------------------------------
    // Strategi optimasi Soal 2c
    // ------------------------------------------------------------------

    /**
     * BFS dengan dua optimasi:
     *  (1) array bantu dialokasikan SEKALI lalu dipakai ulang dengan teknik
     *      "stempel": stempel[v] == idPermintaan berarti v sudah dikunjungi pada
     *      permintaan ini, sehingga tidak perlu mengosongkan array O(V);
     *  (2) pencarian berhenti pada radius maksimum (jumlah hop).
     * Biaya per permintaan menjadi sebanding dengan jumlah simpul di dalam radius.
     */
    static final class BfsTerbatas {
        private final GraphPetaKota peta;
        private final int[] stempel;
        private final int[] hop;
        private final int[] antrian;     // tiap simpul masuk maksimal sekali per permintaan
        private int idPermintaan = 0;
        private int simpulDiprosesTerakhir = 0;

        BfsTerbatas(GraphPetaKota peta) {
            this.peta = peta;
            int n = peta.getJumlahSimpul();
            this.stempel = new int[n];
            this.hop = new int[n];
            this.antrian = new int[n];
        }

        int cari(int asal, boolean[] adalahDriver, int radiusMaksHop) {
            if (idPermintaan == Integer.MAX_VALUE) {   // cegah overflow: reset stempel sesekali
                for (int i = 0; i < stempel.length; i++) {
                    stempel[i] = 0;
                }
                idPermintaan = 0;
            }
            idPermintaan++;
            int kepala = 0;
            int ekor = 0;
            stempel[asal] = idPermintaan;
            hop[asal] = 0;
            antrian[ekor++] = asal;
            while (kepala < ekor) {
                int u = antrian[kepala++];
                if (adalahDriver[u]) {
                    simpulDiprosesTerakhir = kepala;
                    return u;
                }
                if (hop[u] == radiusMaksHop) {
                    continue;                          // jangan ekspansi melewati radius
                }
                int derajat = peta.getDerajat(u);
                for (int i = 0; i < derajat; i++) {
                    int w = peta.getTetangga(u, i);
                    if (stempel[w] != idPermintaan) {
                        stempel[w] = idPermintaan;
                        hop[w] = hop[u] + 1;
                        antrian[ekor++] = w;
                    }
                }
            }
            simpulDiprosesTerakhir = kepala;
            return -1;
        }

        int getSimpulDiprosesTerakhir() {
            return simpulDiprosesTerakhir;
        }
    }

    // ------------------------------------------------------------------
    // Pembentukan data uji
    // ------------------------------------------------------------------

    private static GraphPetaKota bangunGrid() {
        GraphPetaKota peta = new GraphPetaKota(V);
        for (int baris = 0; baris < SISI; baris++) {
            for (int kolom = 0; kolom < SISI; kolom++) {
                int id = baris * SISI + kolom;
                if (kolom + 1 < SISI && acak() < PELUANG_JALAN) {
                    peta.tambahJalan(id, id + 1);
                }
                if (baris + 1 < SISI && acak() < PELUANG_JALAN) {
                    peta.tambahJalan(id, id + SISI);
                }
            }
        }
        return peta;
    }

    private static boolean[] sebarDriver(int jumlah) {
        boolean[] adalahDriver = new boolean[V];
        int terpasang = 0;
        while (terpasang < jumlah) {
            int v = acakInt(V);
            if (!adalahDriver[v]) {
                adalahDriver[v] = true;
                terpasang++;
            }
        }
        return adalahDriver;
    }

    /** xorshift64: generator acak sederhana buatan sendiri (tanpa java.util.Random). */
    private static double acak() {
        benih ^= benih << 13;
        benih ^= benih >>> 7;
        benih ^= benih << 17;
        return (benih >>> 11) / (double) (1L << 53);
    }

    private static int acakInt(int batas) {
        return (int) (acak() * batas);
    }

    // ------------------------------------------------------------------
    // Pengukuran
    // ------------------------------------------------------------------

    /**
     * Estimasi memori GraphPetaKota pada HotSpot 64-bit dengan compressed oops:
     * header array 16 byte (12 header + 4 panjang), referensi 4 byte, dibulatkan ke kelipatan 8.
     */
    private static long estimasiMemoriList(GraphPetaKota peta) {
        long total = bulat8(16 + 4L * V);   // int[][] tetangga (array referensi)
        total += bulat8(16 + 4L * V);       // int[] derajat
        for (int v = 0; v < V; v++) {
            total += bulat8(16 + 4L * peta.getKapasitas(v));   // int[] tetangga[v]
        }
        return total;
    }

    private static long bulat8(long byteMentah) {
        return (byteMentah + 7) / 8 * 8;
    }

    private static long memoriTerpakai() {
        Runtime rt = Runtime.getRuntime();
        for (int i = 0; i < 3; i++) {
            System.gc();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        return rt.totalMemory() - rt.freeMemory();
    }

    // ------------------------------------------------------------------
    // Format tampilan (tanpa bergantung pada Locale)
    // ------------------------------------------------------------------

    private static void cetakBaris(String driver, String metode, long totalNano, long totalSimpul, int ketemu) {
        double nanoPerReq = totalNano / (double) JUMLAH_QUERY;
        String waktu = nanoPerReq >= 1e6 ? desimal(nanoPerReq / 1e6, 2) + " ms" : desimal(nanoPerReq / 1e3, 1) + " us";
        System.out.println(kiri(driver, 9) + kiri(metode, 22) + kanan(waktu, 11)
                + kanan(ribuan(totalSimpul / JUMLAH_QUERY), 12)
                + kanan(ketemu * 100 / JUMLAH_QUERY + "%", 11)
                + kanan(ribuan(Math.round(1e9 / nanoPerReq)), 16));
    }

    private static String milidetik(long nano) {
        return desimal(nano / 1e6, 2) + " ms";
    }

    private static String ukuran(double nByte) {
        if (nByte >= 1e12) {
            return desimal(nByte / 1e12, 1) + " TB";
        }
        if (nByte >= 1e9) {
            return desimal(nByte / 1e9, 1) + " GB";
        }
        if (nByte >= 1e6) {
            return desimal(nByte / 1e6, 1) + " MB";
        }
        return desimal(nByte / 1e3, 1) + " KB";
    }

    private static String desimal(double nilai, int digit) {
        long skala = 1;
        for (int i = 0; i < digit; i++) {
            skala *= 10;
        }
        long dibulatkan = Math.round(nilai * skala);
        String pecahan = Long.toString(Math.abs(dibulatkan % skala));
        while (pecahan.length() < digit) {
            pecahan = "0" + pecahan;
        }
        return ribuan(dibulatkan / skala) + "," + pecahan;
    }

    private static String ribuan(long nilai) {
        String digit = Long.toString(Math.abs(nilai));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digit.length(); i++) {
            if (i > 0 && (digit.length() - i) % 3 == 0) {
                sb.append('.');
            }
            sb.append(digit.charAt(i));
        }
        return (nilai < 0 ? "-" : "") + sb;
    }

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
