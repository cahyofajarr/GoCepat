package soal1;

/**
 * Skema tarif Mobil Listrik: tarif dasar LEBIH TINGGI dari Mobil biasa,
 * tetapi tarif per km LEBIH MURAH. Asumsi di balik arah ini: harga unitnya
 * lebih mahal, sedangkan biaya energi listrik per km lebih rendah. Akibatnya
 * ada titik impas: untuk perjalanan jauh, Mobil Listrik justru lebih murah.
 */
public class MobilListrik implements Kendaraan {

    static final double TARIF_DASAR = 20_000;
    static final double TARIF_PER_KM = 3_000;

    private final String platNomor;

    public MobilListrik(String platNomor) {
        this.platNomor = ValidasiKendaraan.cekPlatNomor(platNomor);
    }

    @Override
    public double hitungTarif(double jarakKm) {
        ValidasiKendaraan.cekJarak(jarakKm);
        return TARIF_DASAR + jarakKm * TARIF_PER_KM;
    }

    @Override
    public String getJenis() {
        return "Mobil Listrik";
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
