package soal3;

/**
 * Hasil pencarian driver terdekat dengan BFS (objek immutable).
 * Selain jawaban utama (simpul driver dan jumlah hop), objek ini menyimpan
 * rute penjemputan dan urutan kunjungan BFS untuk keperluan penjelasan.
 */
public class HasilPencarian {

    private final int asal;
    private final int simpulDriver;     // -1 jika tidak ada driver yang terjangkau
    private final int jumlahHop;        // -1 jika tidak ada driver yang terjangkau
    private final int[] ruteJemput;     // [driver, ..., asal]; kosong jika tidak ditemukan
    private final int[] urutanKunjungan;
    private final int[] hopKunjungan;

    HasilPencarian(int asal, int simpulDriver, int jumlahHop, int[] ruteJemput,
                   int[] urutan, int[] jarakHop, int jumlahDikunjungi) {
        this.asal = asal;
        this.simpulDriver = simpulDriver;
        this.jumlahHop = jumlahHop;
        this.ruteJemput = ruteJemput;
        this.urutanKunjungan = new int[jumlahDikunjungi];
        this.hopKunjungan = new int[jumlahDikunjungi];
        for (int i = 0; i < jumlahDikunjungi; i++) {
            urutanKunjungan[i] = urutan[i];
            hopKunjungan[i] = jarakHop[urutan[i]];
        }
    }

    public int getAsal() {
        return asal;
    }

    public int getSimpulDriver() {
        return simpulDriver;
    }

    public int getJumlahHop() {
        return jumlahHop;
    }

    public boolean isDitemukan() {
        return simpulDriver != -1;
    }

    /** Banyaknya simpul yang sudah dikeluarkan dari antrian sebelum BFS berhenti. */
    public int getJumlahSimpulDiproses() {
        return urutanKunjungan.length;
    }

    public int[] getRuteJemput() {
        return salin(ruteJemput);
    }

    public int[] getUrutanKunjungan() {
        return salin(urutanKunjungan);
    }

    public int[] getHopKunjungan() {
        return salin(hopKunjungan);
    }

    private static int[] salin(int[] sumber) {
        int[] hasil = new int[sumber.length];
        for (int i = 0; i < sumber.length; i++) {
            hasil[i] = sumber[i];
        }
        return hasil;
    }
}
