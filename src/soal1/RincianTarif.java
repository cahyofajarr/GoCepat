package soal1;

/**
 * Bagian (PART) yang dimiliki penuh oleh satu Trip: COMPOSITION.
 *  - Konstruktor package-private: hanya dibuat oleh Trip, tidak diterima dari luar.
 *  - Immutable: tidak bisa diubah setelah dibuat (tarif "dibekukan" saat order).
 *  - Tidak bermakna tanpa Trip-nya; ikut tidak terjangkau (lalu di-GC) bersama Trip.
 */
public final class RincianTarif {

    private final double jarakKm;
    private final double tarifPerjalanan;
    private final double biayaLayanan;

    RincianTarif(double jarakKm, double tarifPerjalanan, double biayaLayanan) {
        this.jarakKm = jarakKm;
        this.tarifPerjalanan = tarifPerjalanan;
        this.biayaLayanan = biayaLayanan;
    }

    public double getJarakKm() {
        return jarakKm;
    }

    public double getTarifPerjalanan() {
        return tarifPerjalanan;
    }

    public double getBiayaLayanan() {
        return biayaLayanan;
    }

    public double getTotal() {
        return tarifPerjalanan + biayaLayanan;
    }

    @Override
    public String toString() {
        return FormatAngka.km(jarakKm) + ": " + FormatAngka.rupiah(tarifPerjalanan)
                + " + layanan " + FormatAngka.rupiah(biayaLayanan)
                + " = " + FormatAngka.rupiah(getTotal());
    }
}
