package soal3;

/**
 * Graph BERBOBOT untuk Soal 3c: setiap ruas jalan punya panjang dalam km.
 *
 * Perubahan dibanding GraphPetaKota (versi BFS):
 *   1. Ada array paralel bobotKm[v][i] = panjang jalan v -> tetangga[v][i].
 *   2. Antrian FIFO diganti MIN-HEAP (priority queue buatan sendiri).
 *   3. Status "sudah dikunjungi" diganti status "sudah final": jarak sebuah
 *      simpul baru pasti benar saat simpul itu DIKELUARKAN dari heap, bukan
 *      saat pertama kali ditemukan.
 *   4. Ada langkah relaksasi: jarak[w] diperbarui bila ditemukan jalur lebih pendek.
 */
public class GraphBerbobot {

    private static final int KAPASITAS_AWAL = 4;

    private final int jumlahSimpul;
    private final int[][] tetangga;
    private final double[][] bobotKm;
    private final int[] derajat;

    public GraphBerbobot(int jumlahSimpul) {
        if (jumlahSimpul <= 0) {
            throw new IllegalArgumentException("Jumlah simpul harus > 0");
        }
        this.jumlahSimpul = jumlahSimpul;
        this.tetangga = new int[jumlahSimpul][];
        this.bobotKm = new double[jumlahSimpul][];
        this.derajat = new int[jumlahSimpul];
        for (int v = 0; v < jumlahSimpul; v++) {
            tetangga[v] = new int[KAPASITAS_AWAL];
            bobotKm[v] = new double[KAPASITAS_AWAL];
        }
    }

    /** Ruas jalan dua arah u <-> v sepanjang km. Dijkstra mensyaratkan bobot tidak negatif. */
    public void tambahJalan(int u, int v, double km) {
        validasiSimpul(u);
        validasiSimpul(v);
        if (u == v) {
            throw new IllegalArgumentException("Self-loop tidak diizinkan: " + u);
        }
        if (Double.isNaN(km) || Double.isInfinite(km) || km <= 0) {
            throw new IllegalArgumentException("Panjang jalan harus > 0 km: " + km);
        }
        sisipkan(u, v, km);
        sisipkan(v, u, km);
    }

    private void sisipkan(int dari, int ke, double km) {
        if (derajat[dari] == tetangga[dari].length) {
            int kapasitasBaru = tetangga[dari].length * 2;
            int[] tetanggaBaru = new int[kapasitasBaru];
            double[] bobotBaru = new double[kapasitasBaru];
            for (int i = 0; i < derajat[dari]; i++) {
                tetanggaBaru[i] = tetangga[dari][i];
                bobotBaru[i] = bobotKm[dari][i];
            }
            tetangga[dari] = tetanggaBaru;
            bobotKm[dari] = bobotBaru;
        }
        tetangga[dari][derajat[dari]] = ke;
        bobotKm[dari][derajat[dari]] = km;
        derajat[dari]++;
    }

    /** Panjang ruas jalan langsung u -> v. O(derajat(u)). */
    public double panjangJalan(int u, int v) {
        validasiSimpul(u);
        validasiSimpul(v);
        for (int i = 0; i < derajat[u]; i++) {
            if (tetangga[u][i] == v) {
                return bobotKm[u][i];
            }
        }
        throw new IllegalArgumentException("Tidak ada jalan langsung " + u + " - " + v);
    }

    /**
     * Dijkstra dengan berhenti dini: driver PERTAMA yang dikeluarkan dari heap
     * adalah driver dengan jarak km terpendek (jaraknya sudah final).
     */
    public Hasil dijkstraCariTerdekat(int asal, boolean[] adalahDriver) {
        validasiSimpul(asal);
        if (adalahDriver == null || adalahDriver.length != jumlahSimpul) {
            throw new IllegalArgumentException("Panjang adalahDriver harus = jumlah simpul");
        }

        double[] jarak = new double[jumlahSimpul];
        int[] induk = new int[jumlahSimpul];
        boolean[] sudahFinal = new boolean[jumlahSimpul];
        for (int v = 0; v < jumlahSimpul; v++) {
            jarak[v] = Double.POSITIVE_INFINITY;
            induk[v] = -1;
        }
        MinHeapJarak heap = new MinHeapJarak(jumlahSimpul);
        jarak[asal] = 0;
        heap.tambah(asal, 0);
        int jumlahFinal = 0;

        while (!heap.isEmpty()) {
            int u = heap.simpulMin();
            double du = heap.jarakMin();
            heap.hapusMin();
            if (sudahFinal[u]) {
                continue;               // entri lama yang sudah basi (lazy deletion)
            }
            sudahFinal[u] = true;
            jumlahFinal++;

            if (adalahDriver[u]) {
                return new Hasil(u, du, bangunRuteJemput(induk, u), jumlahFinal);
            }

            for (int i = 0; i < derajat[u]; i++) {
                int w = tetangga[u][i];
                double kandidat = du + bobotKm[u][i];
                if (!sudahFinal[w] && kandidat < jarak[w]) {   // relaksasi
                    jarak[w] = kandidat;
                    induk[w] = u;
                    heap.tambah(w, kandidat);
                }
            }
        }
        return new Hasil(-1, Double.POSITIVE_INFINITY, new int[0], jumlahFinal);
    }

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

    private void validasiSimpul(int v) {
        if (v < 0 || v >= jumlahSimpul) {
            throw new IllegalArgumentException("Simpul " + v + " di luar rentang 0.." + (jumlahSimpul - 1));
        }
    }

    /** Hasil Dijkstra (immutable). */
    public static final class Hasil {
        private final int simpulDriver;
        private final double jarakKm;
        private final int[] ruteJemput;
        private final int jumlahSimpulFinal;

        Hasil(int simpulDriver, double jarakKm, int[] ruteJemput, int jumlahSimpulFinal) {
            this.simpulDriver = simpulDriver;
            this.jarakKm = jarakKm;
            this.ruteJemput = ruteJemput;
            this.jumlahSimpulFinal = jumlahSimpulFinal;
        }

        public int getSimpulDriver() {
            return simpulDriver;
        }

        public double getJarakKm() {
            return jarakKm;
        }

        public int getJumlahHop() {
            return ruteJemput.length - 1;
        }

        public int getJumlahSimpulFinal() {
            return jumlahSimpulFinal;
        }

        public int[] getRuteJemput() {
            int[] salinan = new int[ruteJemput.length];
            for (int i = 0; i < ruteJemput.length; i++) {
                salinan[i] = ruteJemput[i];
            }
            return salinan;
        }
    }
}
