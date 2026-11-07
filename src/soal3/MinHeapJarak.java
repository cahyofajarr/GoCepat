package soal3;

/**
 * Min-heap biner berbasis array (dibuat sendiri, tanpa java.util.PriorityQueue).
 * Menyimpan pasangan (simpul, jarak); elemen di akar selalu berjarak terkecil.
 *
 * Heap disimpan sebagai pohon biner lengkap di dalam array:
 *   anak kiri dari indeks i  = 2i + 1
 *   anak kanan dari indeks i = 2i + 2
 *   induk dari indeks i      = (i - 1) / 2
 *
 * tambah() dan hapusMin() O(log n); simpulMin() dan jarakMin() O(1).
 * Kapasitas tumbuh otomatis (dynamic array), karena Dijkstra versi "lazy"
 * boleh memasukkan simpul yang sama lebih dari sekali.
 */
public class MinHeapJarak {

    private int[] simpul;
    private double[] jarak;
    private int ukuran = 0;

    public MinHeapJarak(int kapasitasAwal) {
        int kapasitas = Math.max(1, kapasitasAwal);
        simpul = new int[kapasitas];
        jarak = new double[kapasitas];
    }

    public boolean isEmpty() {
        return ukuran == 0;
    }

    public int size() {
        return ukuran;
    }

    /** Masukkan (v, d) lalu naikkan ke posisi yang benar. O(log n). */
    public void tambah(int v, double d) {
        if (ukuran == simpul.length) {
            perbesar();
        }
        simpul[ukuran] = v;
        jarak[ukuran] = d;
        naikkan(ukuran);
        ukuran++;
    }

    public int simpulMin() {
        cekTidakKosong();
        return simpul[0];
    }

    public double jarakMin() {
        cekTidakKosong();
        return jarak[0];
    }

    /** Buang akar: pindahkan elemen terakhir ke akar lalu turunkan. O(log n). */
    public void hapusMin() {
        cekTidakKosong();
        ukuran--;
        if (ukuran > 0) {
            simpul[0] = simpul[ukuran];
            jarak[0] = jarak[ukuran];
            turunkan(0);
        }
    }

    private void naikkan(int i) {
        while (i > 0) {
            int induk = (i - 1) / 2;
            if (jarak[i] >= jarak[induk]) {
                return;
            }
            tukar(i, induk);
            i = induk;
        }
    }

    private void turunkan(int i) {
        while (true) {
            int kiri = 2 * i + 1;
            int kanan = kiri + 1;
            int terkecil = i;
            if (kiri < ukuran && jarak[kiri] < jarak[terkecil]) {
                terkecil = kiri;
            }
            if (kanan < ukuran && jarak[kanan] < jarak[terkecil]) {
                terkecil = kanan;
            }
            if (terkecil == i) {
                return;
            }
            tukar(i, terkecil);
            i = terkecil;
        }
    }

    private void tukar(int a, int b) {
        int s = simpul[a];
        simpul[a] = simpul[b];
        simpul[b] = s;
        double d = jarak[a];
        jarak[a] = jarak[b];
        jarak[b] = d;
    }

    private void perbesar() {
        int[] simpulBaru = new int[simpul.length * 2];
        double[] jarakBaru = new double[jarak.length * 2];
        for (int i = 0; i < ukuran; i++) {
            simpulBaru[i] = simpul[i];
            jarakBaru[i] = jarak[i];
        }
        simpul = simpulBaru;
        jarak = jarakBaru;
    }

    private void cekTidakKosong() {
        if (ukuran == 0) {
            throw new IllegalStateException("Heap kosong");
        }
    }
}
