package soal1;

/**
 * Skema tarif Motor: "biaya minimum + tarif per km".
 * Biaya minimum sudah mencakup 5 km pertama; kelebihannya dihitung per km.
 * Angkanya ilustratif.
 */
public class Motor implements Kendaraan {

    static final double BIAYA_MINIMUM = 13_000;     // mencakup 5 km pertama
    static final double KM_TERCAKUP_MINIMUM = 5.0;
    static final double TARIF_PER_KM = 2_600;       // untuk km setelah 5 km pertama

    private final String platNomor;

    public Motor(String platNomor) {
        this.platNomor = ValidasiKendaraan.cekPlatNomor(platNomor);
    }

    @Override
    public double hitungTarif(double jarakKm) {
        ValidasiKendaraan.cekJarak(jarakKm);
        if (jarakKm <= KM_TERCAKUP_MINIMUM) {
            return BIAYA_MINIMUM;
        }
        return BIAYA_MINIMUM + (jarakKm - KM_TERCAKUP_MINIMUM) * TARIF_PER_KM;
    }

    @Override
    public String getJenis() {
        return "Motor";
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
