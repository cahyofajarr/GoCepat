package soal3;

/**
 * Queue (FIFO) bilangan int berbasis ARRAY BIASA dengan teknik circular buffer.
 * Dibuat sendiri, tanpa java.util.Queue / LinkedList / ArrayDeque.
 *
 *   kepala : indeks elemen terdepan (yang akan keluar berikutnya)
 *   ekor   : indeks slot kosong tempat elemen berikutnya masuk
 *   ukuran : jumlah elemen saat ini
 *
 * Indeks "memutar" dengan operasi modulo sehingga slot yang sudah dikosongkan
 * oleh dequeue bisa dipakai lagi. Semua operasi O(1).
 */
public class AntrianInt {

    private final int[] data;
    private int kepala = 0;
    private int ekor = 0;
    private int ukuran = 0;

    public AntrianInt(int kapasitas) {
        if (kapasitas <= 0) {
            throw new IllegalArgumentException("Kapasitas antrian harus > 0");
        }
        this.data = new int[kapasitas];
    }

    /** Masukkan nilai di belakang antrian. O(1). */
    public void enqueue(int nilai) {
        if (ukuran == data.length) {
            throw new IllegalStateException("Antrian penuh (kapasitas " + data.length + ")");
        }
        data[ekor] = nilai;
        ekor = (ekor + 1) % data.length;
        ukuran++;
    }

    /** Keluarkan nilai terdepan antrian. O(1). */
    public int dequeue() {
        if (ukuran == 0) {
            throw new IllegalStateException("Antrian kosong");
        }
        int nilai = data[kepala];
        kepala = (kepala + 1) % data.length;
        ukuran--;
        return nilai;
    }

    public boolean isEmpty() {
        return ukuran == 0;
    }

    public int size() {
        return ukuran;
    }
}
