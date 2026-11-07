package soal1;

/**
 * Driver adalah entitas MANDIRI: terdaftar di sistem sebelum ada Trip, tetap
 * ada setelah Trip selesai, dan melayani BANYAK Trip sepanjang waktu.
 * Karena itu relasi Trip -> Driver adalah AGGREGATION (Driver "dipinjam"
 * oleh Trip), bukan composition.
 */
public class Driver {

    private final String idDriver;
    private final String nama;
    private Kendaraan kendaraan;        // driver boleh berganti kendaraan
    private boolean tersedia = true;    // false selama sedang melayani Trip
    private int jumlahTripSelesai = 0;

    public Driver(String idDriver, String nama, Kendaraan kendaraan) {
        if (idDriver == null || nama == null || kendaraan == null) {
            throw new IllegalArgumentException("Data driver tidak lengkap");
        }
        this.idDriver = idDriver;
        this.nama = nama;
        this.kendaraan = kendaraan;
    }

    public String getIdDriver() {
        return idDriver;
    }

    public String getNama() {
        return nama;
    }

    public Kendaraan getKendaraan() {
        return kendaraan;
    }

    public boolean isTersedia() {
        return tersedia;
    }

    public int getJumlahTripSelesai() {
        return jumlahTripSelesai;
    }

    /** Ganti kendaraan; Trip lama tetap mencatat kendaraan yang dipakai saat itu. */
    public void gantiKendaraan(Kendaraan kendaraanBaru) {
        if (kendaraanBaru == null) {
            throw new IllegalArgumentException("Kendaraan wajib diisi");
        }
        if (!tersedia) {
            throw new IllegalStateException(nama + " tidak bisa ganti kendaraan saat bertugas");
        }
        this.kendaraan = kendaraanBaru;
    }

    // Dua method berikut package-private: hanya dipanggil oleh Trip.
    // Trip "meminjam" driver saat dibuat dan "mengembalikannya" saat selesai.
    void mulaiBertugas() {
        if (!tersedia) {
            throw new IllegalStateException(nama + " sedang melayani trip lain");
        }
        tersedia = false;
    }

    void selesaiBertugas() {
        tersedia = true;
        jumlahTripSelesai++;
    }

    @Override
    public String toString() {
        return nama + " (" + idDriver + ", " + kendaraan + ")";
    }
}
