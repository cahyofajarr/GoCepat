package soal1;

/**
 * Satu perjalanan GoCepat.
 *
 * Relasi yang dimiliki Trip:
 *  - Driver, Penumpang, Kendaraan : AGGREGATION. Objek sudah ada sebelum Trip,
 *    diterima dari luar, hanya DIRUJUK, dan tetap hidup setelah Trip selesai.
 *  - RincianTarif                 : COMPOSITION. DIBUAT di dalam Trip, tidak
 *    dibagi ke Trip lain, dan umurnya terikat pada Trip.
 *
 * Kelas ini hanya mengenal interface Kendaraan. Tidak ada satu pun referensi
 * ke Motor, Mobil, MobilListrik, atau SepedaListrik (Soal 1c).
 */
public class Trip {

    public enum Status { BERJALAN, SELESAI }

    static final double BIAYA_LAYANAN = 2_000;   // biaya jasa aplikasi (ilustratif)

    private final String idTrip;
    private final Driver driver;               // aggregation
    private final Penumpang penumpang;         // aggregation
    private final Kendaraan kendaraan;         // aggregation: kendaraan yang dipakai SAAT trip ini
    private final RincianTarif rincianTarif;   // composition
    private Status status;

    public Trip(String idTrip, Driver driver, Penumpang penumpang, double jarakKm) {
        if (idTrip == null || driver == null || penumpang == null) {
            throw new IllegalArgumentException("Data trip tidak lengkap");
        }
        this.idTrip = idTrip;
        this.driver = driver;
        this.penumpang = penumpang;
        this.kendaraan = driver.getKendaraan();

        // Polymorphism: method yang dijalankan ditentukan saat RUNTIME oleh objek
        // kendaraan sebenarnya (Motor/Mobil/MobilListrik/...), bukan oleh Trip.
        double tarif = kendaraan.hitungTarif(jarakKm);

        // Composition: bagian ini lahir di dalam Trip dan tidak pernah dibagi.
        this.rincianTarif = new RincianTarif(jarakKm, tarif, BIAYA_LAYANAN);

        // Status driver baru diubah SETELAH semua langkah yang bisa gagal selesai,
        // supaya driver tidak "tersangkut" sibuk bila pembuatan Trip gagal.
        driver.mulaiBertugas();
        this.status = Status.BERJALAN;
    }

    /** Menyelesaikan trip dan "mengembalikan" driver agar bisa menerima order lain. */
    public void selesaikan() {
        if (status != Status.BERJALAN) {
            throw new IllegalStateException("Trip " + idTrip + " sudah selesai");
        }
        status = Status.SELESAI;
        driver.selesaiBertugas();
    }

    public String getIdTrip() {
        return idTrip;
    }

    public Driver getDriver() {
        return driver;
    }

    public Penumpang getPenumpang() {
        return penumpang;
    }

    public Kendaraan getKendaraan() {
        return kendaraan;
    }

    /** Aman dikembalikan karena RincianTarif immutable. */
    public RincianTarif getRincianTarif() {
        return rincianTarif;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return idTrip + " | " + penumpang.getNama() + " dijemput " + driver.getNama()
                + " | " + kendaraan + " | " + rincianTarif + " | " + status;
    }
}
