package soal3;

/**
 * Graph tak berarah (undirected) untuk peta kota GoCepat.
 *   simpul (vertex) = persimpangan jalan, diberi ID 0..jumlahSimpul-1
 *   sisi (edge)     = ruas jalan dua arah yang menghubungkan dua persimpangan
 *
 * Representasi: ADJACENCY LIST berbasis array biasa (tanpa java.util).
 *   tetangga[v] : array int berisi ID tetangga v. Panjang array = KAPASITAS,
 *                 yang belum tentu terisi penuh.
 *   derajat[v]  : jumlah tetangga yang benar-benar terisi di tetangga[v].
 *
 * Setiap tetangga[v] berperilaku sebagai dynamic array buatan sendiri:
 * bila penuh, kapasitasnya digandakan (4 -> 8 -> 16 ...).
 */
public class GraphPetaKota {

    /** Rata-rata persimpangan terhubung ke 3-4 jalan (Soal 2b), jadi kapasitas awal 4. */
    public static final int KAPASITAS_AWAL = 4;

    private final int jumlahSimpul;
    private final int[][] tetangga;
    private final int[] derajat;
    private int jumlahJalan;

    public GraphPetaKota(int jumlahSimpul) {
        if (jumlahSimpul <= 0) {
            throw new IllegalArgumentException("Jumlah simpul harus > 0");
        }
        this.jumlahSimpul = jumlahSimpul;
        this.tetangga = new int[jumlahSimpul][];
        this.derajat = new int[jumlahSimpul];
        for (int v = 0; v < jumlahSimpul; v++) {
            tetangga[v] = new int[KAPASITAS_AWAL];
        }
    }

    // ------------------------------------------------------------------
    // Pembentukan graph
    // ------------------------------------------------------------------

    /**
     * Menambah ruas jalan dua arah u <-> v.
     * Kompleksitas: O(derajat(u)) untuk cek duplikat, penyisipan O(1) amortized.
     *
     * @return true jika jalan baru ditambahkan, false jika jalan sudah ada.
     */
    public boolean tambahJalan(int u, int v) {
        validasiSimpul(u);
        validasiSimpul(v);
        if (u == v) {
            throw new IllegalArgumentException("Jalan dari simpul ke dirinya sendiri tidak diizinkan: " + u);
        }
        if (adaJalan(u, v)) {
            return false;
        }
        sisipkanTetangga(u, v);
        sisipkanTetangga(v, u);
        jumlahJalan++;
        return true;
    }

    /** Apakah u dan v terhubung langsung? O(derajat(u)). */
    public boolean adaJalan(int u, int v) {
        validasiSimpul(u);
        validasiSimpul(v);
        for (int i = 0; i < derajat[u]; i++) {
            if (tetangga[u][i] == v) {
                return true;
            }
        }
        return false;
    }

    private void sisipkanTetangga(int dari, int ke) {
        if (derajat[dari] == tetangga[dari].length) {
            perbesarKapasitas(dari);
        }
        tetangga[dari][derajat[dari]] = ke;
        derajat[dari]++;
    }

    /** Dynamic array: buat array 2x lebih besar lalu salin isi lama. O(derajat), jarang terjadi. */
    private void perbesarKapasitas(int v) {
        int[] lama = tetangga[v];
        int[] baru = new int[lama.length * 2];
        for (int i = 0; i < derajat[v]; i++) {
            baru[i] = lama[i];
        }
        tetangga[v] = baru;
    }

    // ------------------------------------------------------------------
    // BFS: driver terdekat dalam satuan hop (jumlah ruas jalan)
    // ------------------------------------------------------------------

    /**
     * Mengembalikan simpul driver terdekat (jumlah hop paling sedikit) dari asal,
     * atau -1 jika tidak ada driver yang terjangkau.
     */
    public int bfsCariTerdekat(int asal, boolean[] adalahDriver) {
        return bfsCariTerdekatDetail(asal, adalahDriver).getSimpulDriver();
    }

    /**
     * Versi lengkap bfsCariTerdekat: selain simpul driver, juga mengembalikan
     * jumlah hop, rute penjemputan, dan urutan kunjungan BFS.
     */
    public HasilPencarian bfsCariTerdekatDetail(int asal, boolean[] adalahDriver) {
        validasiSimpul(asal);
        if (adalahDriver == null || adalahDriver.length != jumlahSimpul) {
            throw new IllegalArgumentException("Panjang adalahDriver harus = jumlah simpul (" + jumlahSimpul + ")");
        }

        // Semua struktur bantu adalah array biasa berukuran V.
        boolean[] sudahDikunjungi = new boolean[jumlahSimpul];
        int[] jarakHop = new int[jumlahSimpul];
        int[] induk = new int[jumlahSimpul];
        int[] urutanKunjungan = new int[jumlahSimpul];
        int jumlahDikunjungi = 0;
        AntrianInt antrian = new AntrianInt(jumlahSimpul);   // tiap simpul masuk antrian maksimal 1x

        sudahDikunjungi[asal] = true;
        jarakHop[asal] = 0;
        induk[asal] = -1;
        antrian.enqueue(asal);

        while (!antrian.isEmpty()) {
            int u = antrian.dequeue();
            urutanKunjungan[jumlahDikunjungi++] = u;

            // BFS mengeluarkan simpul dalam urutan jarak yang tidak menurun,
            // jadi driver PERTAMA yang keluar dari antrian pasti yang terdekat.
            if (adalahDriver[u]) {
                return new HasilPencarian(asal, u, jarakHop[u], bangunRuteJemput(induk, u),
                        urutanKunjungan, jarakHop, jumlahDikunjungi);
            }

            for (int i = 0; i < derajat[u]; i++) {
                int w = tetangga[u][i];
                if (!sudahDikunjungi[w]) {
                    sudahDikunjungi[w] = true;          // tandai saat MASUK antrian
                    jarakHop[w] = jarakHop[u] + 1;
                    induk[w] = u;
                    antrian.enqueue(w);
                }
            }
        }
        return new HasilPencarian(asal, -1, -1, new int[0], urutanKunjungan, jarakHop, jumlahDikunjungi);
    }

    /**
     * Mengikuti pointer induk dari simpul driver kembali ke asal.
     * Hasilnya langsung berupa rute penjemputan: [driver, ..., asal].
     */
    private static int[] bangunRuteJemput(int[] induk, int simpulDriver) {
        int panjang = 0;
        for (int v = simpulDriver; v != -1; v = induk[v]) {
            panjang++;
        }
        int[] rute = new int[panjang];
        int i = 0;
        for (int v = simpulDriver; v != -1; v = induk[v]) {
            rute[i++] = v;
        }
        return rute;
    }

    /**
     * BFS penuh tanpa berhenti dini: jarak hop dari asal ke SEMUA simpul
     * (-1 = tidak terjangkau). Dipakai sebagai cek silang hasil bfsCariTerdekat.
     */
    public int[] hitungSemuaJarakHop(int asal) {
        validasiSimpul(asal);
        int[] jarakHop = new int[jumlahSimpul];
        for (int v = 0; v < jumlahSimpul; v++) {
            jarakHop[v] = -1;
        }
        AntrianInt antrian = new AntrianInt(jumlahSimpul);
        jarakHop[asal] = 0;
        antrian.enqueue(asal);
        while (!antrian.isEmpty()) {
            int u = antrian.dequeue();
            for (int i = 0; i < derajat[u]; i++) {
                int w = tetangga[u][i];
                if (jarakHop[w] == -1) {
                    jarakHop[w] = jarakHop[u] + 1;
                    antrian.enqueue(w);
                }
            }
        }
        return jarakHop;
    }

    // ------------------------------------------------------------------
    // Akses baca
    // ------------------------------------------------------------------

    public int getJumlahSimpul() {
        return jumlahSimpul;
    }

    public int getJumlahJalan() {
        return jumlahJalan;
    }

    public int getDerajat(int v) {
        validasiSimpul(v);
        return derajat[v];
    }

    /** Panjang array tetangga[v] saat ini (kapasitas dynamic array). */
    public int getKapasitas(int v) {
        validasiSimpul(v);
        return tetangga[v].length;
    }

    /** Tetangga ke-i dari simpul v, 0 <= i < derajat(v). O(1). */
    public int getTetangga(int v, int i) {
        validasiSimpul(v);
        if (i < 0 || i >= derajat[v]) {
            throw new IndexOutOfBoundsException("Simpul " + v + " hanya punya " + derajat[v] + " tetangga");
        }
        return tetangga[v][i];
    }

    private void validasiSimpul(int v) {
        if (v < 0 || v >= jumlahSimpul) {
            throw new IllegalArgumentException("Simpul " + v + " di luar rentang 0.." + (jumlahSimpul - 1));
        }
    }
}
