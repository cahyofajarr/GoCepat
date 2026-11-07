package soal1;

/** Skema tarif Mobil: tarif dasar (buka pintu) + tarif per km sejak km pertama. */
public class Mobil implements Kendaraan {

    static final double TARIF_DASAR = 12_000;
    static final double TARIF_PER_KM = 4_000;

    private final String platNomor;

    public Mobil(String platNomor) {
        this.platNomor = ValidasiKendaraan.cekPlatNomor(platNomor);
    }

    @Override
    public double hitungTarif(double jarakKm) {
        ValidasiKendaraan.cekJarak(jarakKm);
        return TARIF_DASAR + jarakKm * TARIF_PER_KM;
    }

    @Override
    public String getJenis() {
        return "Mobil";
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
