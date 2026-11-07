package soal1;

/**
 * Jenis kendaraan BARU (Soal 1c). Cukup membuat kelas baru yang memenuhi
 * kontrak Kendaraan; kelas Trip, Driver, dan kode lain yang sudah ada TIDAK
 * diubah sama sekali. Sepeda listrik tidak berplat, jadi yang diisi adalah
 * nomor unit.
 */
public class SepedaListrik implements Kendaraan {

    static final double TARIF_DASAR = 5_000;
    static final double TARIF_PER_KM = 1_500;

    private final String platNomor;   // berisi nomor unit

    public SepedaListrik(String nomorUnit) {
        this.platNomor = ValidasiKendaraan.cekPlatNomor(nomorUnit);
    }

    @Override
    public double hitungTarif(double jarakKm) {
        ValidasiKendaraan.cekJarak(jarakKm);
        return TARIF_DASAR + jarakKm * TARIF_PER_KM;
    }

    @Override
    public String getJenis() {
        return "Sepeda Listrik";
    }

    @Override
    public String getPlatNomor() {
        return platNomor;
    }

    @Override
    public String toString() {
        return getJenis() + " [" + platNomor + "]";
    }
}
